package handling.channel.handler;

import client.player.Player;
import client.Client;
import client.player.PlayerEffects;
import client.player.inventory.Inventory;
import client.player.inventory.types.InventoryType;
import client.player.inventory.Item;
import static configure.Gamemxd.点NPC间隔;
import security.violation.AutobanManager;
import security.violation.CheatingOffense;
import constants.GameConstants;
import constants.ItemConstants;
import constants.MapConstants;
import static handling.channel.handler.ChannelHeaders.NPCHeaders.*;
import packet.transfer.read.InPacket;
import packet.creators.PacketCreator;
import packet.opcode.OutHeader;
import scripting.npc.NPCScriptManager;
import scripting.quest.QuestScriptManager;
import constants.NPCConstants;
import java.awt.Point;
import packet.creators.EffectPackets;
import packet.transfer.write.OutPacket;
import scripting.npc.NPCConversationManager;
import server.itens.InventoryManipulator;
import server.itens.ItemInformationProvider;
import server.shops.Shop;
import server.itens.StorageKeeper;
import server.life.MapleLifeFactory;
import server.life.npc.MapleNPC;
import server.quest.MapleQuest;

/**
 *
 * @author GabrielSin
 */
public class NPCHandler {

    public static final void NPCTalk(final InPacket packet, final Client c) {
        int objectNPC = packet.readInt();
        final MapleNPC npc = c.getPlayer().getMap().getNPCByOid(objectNPC);
        if (!c.getPlayer().isAlive() || npc == null) {
            c.announce(PacketCreator.EnableActions());
            return;
        }
        if (System.currentTimeMillis() - c.getPlayer().getNpcCooldown() < 点NPC间隔) {
            c.getPlayer().dropMessage(5, "操作过快，请你慢点。");
            c.announce(PacketCreator.EnableActions());
            return;
        }
        /*if (c.getPlayer().getGM() > 0) {
            c.getPlayer().dropMessage(5, "与NPC " + npc.getId() + " 进行连接。");
        }*/

        for (final int i : NPCConstants.DISABLE_NPCS) {
            if (npc.getId() == i) {
                c.getPlayer().getClient().write(PacketCreator.GetNPCTalk(i, (byte) 0, String.format(NPCConstants.DISABLE_NPCS_MESSAGE, c.getPlayer().getName()), "00 00"));
                return;
            }
        }
        if (c.getCM() != null) {
            NPCScriptManager.getInstance().dispose(c);
        } else if (c.getQM() != null) {
            QuestScriptManager.getInstance().dispose(c);
        } else if (c.getPlayer().getShop() != null) {
            c.getPlayer().setShop(null);
            c.write(PacketCreator.ConfirmShopTransaction((byte) 20));
        }
        if (npc.hasShop()) {
            npc.sendShop(c);
        } else {
            NPCScriptManager.getInstance().start(c, npc.getId());
        }
        c.getPlayer().setNpcCooldown(System.currentTimeMillis());
    }

    public static void NPCMoreTalk(InPacket packet, Client c) {
        byte lastMsg = packet.readByte();
        byte action = packet.readByte();
        final NPCConversationManager cm = NPCScriptManager.getInstance().getCM(c);
        if (cm == null) {
            c.write(PacketCreator.EnableActions());
            return;
        }
        cm.setLastMsg((byte) -1);
        if (lastMsg == 2) {
            if (action != 0) {
                String returnText = packet.readMapleAsciiString();
                if (c.getQM() != null) {
                    c.getQM().setGetText(returnText);
                    if (c.getQM().isStart()) {
                        QuestScriptManager.getInstance().start(c, action, lastMsg, -1);
                    } else {
                        QuestScriptManager.getInstance().end(c, action, lastMsg, -1);
                    }
                } else {
                    c.getCM().setGetText(returnText);
                    NPCScriptManager.getInstance().action(c, action, lastMsg, -1);
                }
            } else if (c.getQM() != null) {
                c.getQM().dispose();
            } else {
                c.getCM().dispose();
            }
        } else if (lastMsg == 6) { // Speed Quiz 
            if (c.getPlayer().getSpeedQuiz() == null) {
                cm.dispose();
                return;
            }
            c.getPlayer().getSpeedQuiz().nextRound(c, packet.readMapleAsciiString());
        } else {
            int selection = -1;
            if (packet.available() >= 4) {
                selection = packet.readInt();
            } else if (packet.available() > 0) {
                selection = packet.readByte();
            }
            if (c.getQM() != null) {
                if (c.getQM().isStart()) {
                    QuestScriptManager.getInstance().start(c, action, lastMsg, selection);
                } else {
                    QuestScriptManager.getInstance().end(c, action, lastMsg, selection);
                }
            } else if (c.getCM() != null) {
                NPCScriptManager.getInstance().action(c, action, lastMsg, selection);
            }
        }
    }

    /*
     * Special thanks Darter (YungMoozi) for reporting unchecked player position
     */
    private static boolean isNpcNearby(InPacket packet, Player p, MapleQuest quest, int npcId) {
        Point playerPosition;
        Point pos = p.getPosition();

        if (packet.available() >= 4) {
            playerPosition = new Point(packet.readShort(), packet.readShort());
            if (playerPosition.distance(pos) > 1000) {
                playerPosition = pos;
            }
        } else {
            playerPosition = pos;
        }

        if (!quest.isAutoStart() && !quest.isAutoComplete()) {
            MapleNPC npc = p.getMap().getNPCById(npcId);
            if (npc == null) {
                return false;
            }

            Point npcPosition = npc.getPosition();
            if (Math.abs(npcPosition.getX() - playerPosition.getX()) > 1200 || Math.abs(npcPosition.getY() - playerPosition.getY()) > 800) {
                p.dropMessage(5, "Approach the NPC to fulfill this quest operation.");
                return false;
            }
        }

        return true;
    }

    public static final void QuestAction(final InPacket packet, final Client c) {
        byte action = packet.readByte();
        short questId = packet.readShort();
        Player p = c.getPlayer();
        MapleQuest quest = MapleQuest.getInstance(questId);
        if (p == null || quest == null) {
            return;
        }
        switch (action) {
            case START_QUEST: {
                int npc = packet.readInt();
                if (!isNpcNearby(packet, p, quest, npc)) {
                    return;
                }
                if (quest.canStart(p, npc)) {
                    quest.start(p, npc);
                }
                break;
            }
            case COMPLETE_QUEST: {
                int npc = packet.readInt();
                if (!isNpcNearby(packet, p, quest, npc)) {
                    return;
                }
                if (!quest.isAutoComplete()) {
                    quest.complete(p, npc, packet.readInt());
                } else {
                    quest.complete(p, npc);
                }
                p.getClient().announce(EffectPackets.ShowSelfQuestComplete());
                p.getMap().broadcastMessage(p, PacketCreator.ShowThirdPersonEffect(p.getId(), PlayerEffects.QUEST_COMPLETE.getEffect()), false);
                break;
            }
            case FORFEIT_QUEST: {
                quest.forfeit(p);
                break;
            }
            case SCRIPT_START_QUEST: {
                int npc = packet.readInt();
                if (!isNpcNearby(packet, p, quest, npc)) {
                    return;
                }
                if (quest.canStart(p, npc)) {
                    QuestScriptManager.getInstance().start(c, questId, npc);
                }
                break;
            }
            case SCRIPT_END_QUEST: {
                int npc = packet.readInt();
                if (!isNpcNearby(packet, p, quest, npc)) {
                    return;
                }
                if (quest.canComplete(p, npc)) {
                    QuestScriptManager.getInstance().end(c, questId, npc);
                }
                p.getClient().announce(EffectPackets.ShowSelfQuestComplete());
                p.getMap().broadcastMessage(p, PacketCreator.ShowThirdPersonEffect(p.getId(), PlayerEffects.QUEST_COMPLETE.getEffect()), false);
                break;
            }
        }
    }

    public static void NPCShop(InPacket packet, Client c, Player p) {
        if (p == null || p.getMap() == null) {
            return;
        }
        //25 [01] [01 00] [F0 DD 13 00] [01 00]
        switch (packet.readByte()) {
            case BUY_SHOP: {
                //25 00 00 00 80 84 1E 00 01 00
                final Shop shop = p.getShop();
                if (shop == null) {
                    return;
                }
                packet.readShort();
                int itemId = packet.readInt();
                short quantity = packet.readShort();
                p.getShop().buy(c, itemId, quantity);
                break;
            }
            case SELL_SHOP: {
                final Shop shop = p.getShop();
                if (shop == null) {
                    return;
                }
                short slot = packet.readShort();
                int itemId = packet.readInt();
                InventoryType type = ItemInformationProvider.getInstance().getInventoryType(itemId);
                short quantity = packet.readShort();
                if (quantity < 0) {
                    p.getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT, "Tried to sell negative quantity to the NPC store.");
                    return;
                }
                p.getShop().sell(c, type, slot, quantity);
                break;
            }
            case RECHARGE_SHOP: {
                final Shop shop = p.getShop();
                if (shop == null) {
                    return;
                }
                byte slot = (byte) packet.readShort();
                p.getShop().recharge(c, slot);
                break;
            }
            case EXIT_SHOP: {
                p.setShop(null);
            }
        }
    }

    public static void Storage(InPacket packet, Client c) {
        Player p = c.getPlayer();
        p.dropMessage(1, "仓库关闭,无法使用,请使用主界面的仓库。");
//        final StorageKeeper storage = p.getStorage();
//        final MapleNPC npc = MapleLifeFactory.getNPC(MapConstants.isStorageKeeperMap(c.getPlayer().getMapId()));
//        switch (packet.readByte()) {
//            //取物品
//            case TAKE_OUT_STORAGE: {
//                byte type = packet.readByte();
//                byte slot = storage.getSlot(InventoryType.getByType(type), packet.readByte());
//                int i = 1;
//                for (i = 1; i <= 4; i++) {
//                    if (c.getPlayer().getInventory(InventoryType.getByType((byte) i)).isFull(1)) {
//                        p.dropMessage(1, "背包满了，请空出背包后再尝试。");
//                        return;
//                    }
//                }
//                final Item item = storage.takeOut(slot);
//                if (c.getPlayer().getMeso() < npc.getStats().getWithdrawCost()) {
//                    p.announce(PacketCreator.GetStorageInsufficientFunds());
//                }
//
//                if (slot < 0 || slot > storage.getSlots()) {
//                    c.disconnect(true, false);
//                    return;
//                }
//
//                if (item != null) {
//                    if (ItemInformationProvider.getInstance().isPickupRestricted(item.getItemId()) && p.getItemQuantity(item.getItemId(), true) > 0) {
//                        p.announce(PacketCreator.GetStorageFull());
//                        return;
//                    }
//                    if (!InventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
//                        storage.store(item);
//                        p.announce(PacketCreator.StorageWithdrawInventoryFull());
//                    } else {
//                        InventoryManipulator.addFromDrop(c, item, "从仓库中取出 " + p.getName(), false);
//                    }
//                    p.gainMeso(-npc.getStats().getWithdrawCost(), npc.getStats().getWithdrawCost() > 0);
//                    storage.sendTakenOut(c, ItemConstants.getInventoryType(item.getItemId()));
//                } else {
//                    AutobanManager.getInstance().autoban(c, p.getName() + " 试图从不存在的存储项中获取项。");
//                }
//                break;
//            }
//            //存物品
//            case SEND_STORAGE: {
//                final byte slot = (byte) packet.readShort();
//                final int itemId = packet.readInt();
//                short quantity = packet.readShort();
//                final ItemInformationProvider ii = ItemInformationProvider.getInstance();
//                InventoryType slotType = ItemConstants.getInventoryType(itemId);
//                Inventory Inv = p.getInventory(slotType);
//                if (p.getWorldId() != storage.getWorld()) {
//                    p.dropMessage(1, "该大区无法使用仓库。");
//                    return;
//                }
//
//                if (itemId >= 2070000 && itemId < 2080000) {
//                    p.dropMessage(1, "该类型物品无法操作。");
//                    return;
//                }
//                
//                if (p.getMeso() < npc.getStats().getDepositCost()) {
//                    p.announce(PacketCreator.GetStorageInsufficientFunds());
//                    return;
//                }
//                if (slot < 1 || slot > Inv.getSlotLimit()) {
//                    p.getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT, "试图存储负的或多余的存款金额。");
//                    return;
//                }
//                if (slotType == InventoryType.CASH) {
//                    p.getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT, "试图在存款中存储现金项目。");
//                    return;
//                }
//                if (storage.isFull()) {
//                    p.announce(PacketCreator.GetStorageFull());
//                    return;
//                }
//
//                InventoryType type = ii.getInventoryType(itemId);
//                Item item = c.getPlayer().getInventory(type).getItem(slot).copy();
//                if (item == null) {
//                    p.getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT, "您试图将不存在的物品存入押金中。");
//                    return;
//                }
//
//                if (item.getItemId() == itemId && (item.getQuantity() >= quantity || ItemConstants.isThrowingStar(itemId) || ItemConstants.isBullet(itemId))) {
//                    if (ItemConstants.isThrowingStar(itemId) || ItemConstants.isBullet(itemId)) {
//                        quantity = item.getQuantity();
//                    }
//                    p.gainMeso(-npc.getStats().getDepositCost(), false, true, false);
//                    InventoryManipulator.removeFromSlot(c, type, slot, quantity, false);
//                    item.setQuantity(quantity);
//                    storage.store(item);
//                }
//                storage.sendStored(c, ItemConstants.getInventoryType(itemId));
//                break;
//            }
//            //存取钱
//            case SET_MESO_STORAGE: {
//                //角色要减少的钱
//                int meso = packet.readInt();
//                //仓库的钱
//                final int storageMesos = storage.getMeso();
//                //角色身上的钱
//                final int playerMesos = c.getPlayer().getMeso();
//
//                //取出
//                if (meso > 0) {
//                    if (playerMesos + meso >= 2147483647 || playerMesos + meso < 0) {
//                        p.dropMessage(1, "取出失败，背包金币已经达到最大限制，无法取出。");
//                        return;
//                    }
//                }
//
//                if (meso < 0) {
//                    if (p.getWorldId() != storage.getWorld()) {
//                        p.dropMessage(1, "该大区无法使用仓库。");
//                        return;
//                    }
//                }
//
//                //存入
//                if (meso < 0) {
//                    if (storageMesos == 2147483647) {
//                        p.dropMessage(1, "存入失败，仓库金币已经达到最大限制，无法存入。");
//                        return;
//                    }
//                }
//
//                if ((meso > 0 && storageMesos >= meso) || (meso < 0 && playerMesos >= -meso)) {
//                    if (meso < 0 && (storageMesos - meso) < 0) {
//                        meso = -(Integer.MAX_VALUE - storageMesos);
//                        if ((-meso) > playerMesos) {
//                            return;
//                        }
//                    } else if (meso > 0 && (playerMesos + meso) < 0) {
//                        meso = (Integer.MAX_VALUE - playerMesos);
//                        if ((meso) > storageMesos) {
//                            return;
//                        }
//                    }
//                    storage.setMeso(storageMesos - meso);
//                    p.gainMeso(meso, true, true, true);
//                } else {
//                    return;
//                }
//                storage.sendMeso(c);
//                break;
//            }
//            case CLOSE_STORAGE: {
//                storage.close();
//                break;
//            }
//        }
    }

    public static void NPCAnimation(InPacket r, Client c) {
        OutPacket wp = new OutPacket(OutHeader.NPC_ACTION);
        wp.writeBytes(r.readBytes(r.available()));
        c.write(wp);
    }
}
