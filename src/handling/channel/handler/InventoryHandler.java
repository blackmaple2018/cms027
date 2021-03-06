package handling.channel.handler;

import client.player.Player;
import client.Client;
import client.player.buffs.BuffStat;
import community.MaplePartyCharacter;
import constants.ExperienceConstants;
import constants.ItemConstants;
import handling.channel.ChannelServer;
import packet.transfer.read.InPacket;
import handling.world.service.BroadcastService;
import java.util.List;
import java.util.Map;
import java.util.Random;
import packet.creators.PacketCreator;
import client.player.buffs.Disease;
import client.player.inventory.Equip;
import client.player.inventory.EquipScrollResult;
import client.player.inventory.Inventory;
import client.player.inventory.types.InventoryType;
import client.player.inventory.Item;
import client.player.inventory.ItemPet;
import client.player.inventory.TamingMob;
import client.player.skills.PlayerSkill;
import client.player.skills.PlayerSkillFactory;
import static client.player.skills.PlayerSkillFactory.技能名字;
import static configure.Gamemxd.一转技能附魔;
import static configure.Gamemxd.二转技能附魔20;
import static configure.Gamemxd.二转技能附魔30;
import static configure.Gamemxd.合伙群;
import static configure.Gamemxd.物品操作间隔;
import security.violation.CheatingOffense;
import constants.GameConstants;
import static constants.ServerProperties.World.开服名字;
import static console.MsgServer.QQMsgServer.sendMsgToQQGroup;
import handling.channel.handler.operation.UseCashItemOperation;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import static launch.Start.FuMoInfoMap;
import packet.creators.EffectPackets;
import server.MapleStatEffect;
import server.itens.InventoryManipulator;
import server.itens.ItemInformationProvider;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.maps.Field;
import server.maps.FieldItem;
import server.maps.FieldLimit;
import server.maps.object.FieldObject;
import server.maps.object.FieldObjectType;
import tools.FileLogger;
import tools.Randomizer;

/**
 *
 * @author GabrielSin
 */
public class InventoryHandler {

    /**
     * <使用现金物品>
     */
    public static void UseCashItem(InPacket packet, Client c) {
        Player p = c.getPlayer();
        if (System.currentTimeMillis() - p.getLastUsedCashItem() < 3000) {
            return;
        }
        p.setLastUsedCashItem(System.currentTimeMillis());

        //2B 02 00 D0 C4 1F 00 07 00 31 31 31 31 31 31 31 01
        //2B 18 00 E8 C0 1F 00 04 00 31 31 31 31
        short slot = packet.readShort();
        int itemId = packet.readInt();
        int itemType = itemId / 10000;
        Item item = p.getInventory(InventoryType.USE).getItem(slot);
        if (item == null || item.getItemId() != itemId || item.getQuantity() < 1 || !c.getPlayer().haveItem(itemId)) {
            p.announce(PacketCreator.EnableActions());
            return;
        }
        switch (itemType) {
            case 217:
                UseCashItemOperation.UseVipTeleport(packet, p, itemId, slot);
                break;
            case 218:
                UseCashItemOperation.UseAPandSP(packet, p, itemId, slot);
                break;
            case 506:
                UseCashItemOperation.UseUndefined(packet, p, itemId, slot);
                break;
            case 208:
                UseCashItemOperation.UseMegaphoneItem(packet, p, itemId, slot, item);
                break;
            case 509:
                UseCashItemOperation.UseSendNote(packet, p, slot);
                break;
            case 510:
                UseCashItemOperation.UseJukeBox(p, slot);
                break;
            case 209:
                UseCashItemOperation.UseMapEffectItem(packet, p, itemId, slot);
                break;
            case 213://love
                UseCashItemOperation.UseLoveItem(packet, p, itemId, slot);
                break;
            case 211:
                UseCashItemOperation.UsePetNameChange(packet, p, slot);
                break;
            case 520:
                UseCashItemOperation.UseBagMeso(p, itemId, slot);
                break;
            case 524:
                UseCashItemOperation.UsePetFood(p, itemId, slot);
                break;
            case 523:
                UseCashItemOperation.UseShopScanner(packet, p, slot);
                break;
            case 528:
                UseCashItemOperation.UsePassedGas(p, itemId, slot);
                break;
            case 530:
                UseCashItemOperation.UseItemEffect(p, itemId, slot);
                break;
            case 537:
                UseCashItemOperation.UseChalkBoard(packet, p);
                break;
            case 539:
                UseCashItemOperation.UseMegaAvatar(packet, p, itemId, slot);
                break;
            case 545:
                UseCashItemOperation.UseMyoMyo(p);
                break;
            default:
                System.out.println("Packet unhandled (type: " + itemType + ") : " + packet.toString());
                FileLogger.print("useCashHandler.txt", packet.toString());
                break;
        }
        p.getClient().write(PacketCreator.EnableActions());
    }

    /**
     * <物品收集>
     */
    public static void ItemGather(InPacket packet, Client c) {
        Player p = c.getPlayer();
        packet.readInt();
        InventoryType inventoryType = InventoryType.getByType(packet.readByte());

        if (!GameConstants.USE_ITEM_SORT) {
            c.announce(PacketCreator.EnableActions());
            return;
        }

        Inventory inventory = c.getPlayer().getInventory(inventoryType);
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        Item srcItem, dstItem;

        for (short dst = 1; dst <= inventory.getSlotLimit(); dst++) {
            dstItem = inventory.getItem(dst);
            if (dstItem == null) {
                continue;
            }

            for (short src = (short) (dst + 1); src <= inventory.getSlotLimit(); src++) {
                srcItem = inventory.getItem(src);
                if (srcItem == null) {
                    continue;
                }

                if (dstItem.getItemId() != srcItem.getItemId()) {
                    continue;
                }
                if (dstItem.getQuantity() == ii.getSlotMax(c, inventory.getItem(dst).getItemId())) {
                    break;
                }

                InventoryManipulator.move(c, inventoryType, src, dst);
            }
        }

        inventory = p.getInventory(inventoryType);
        boolean sorted = false;

        while (!sorted) {
            short freeSlot = inventory.getNextFreeSlot();

            if (freeSlot != -1) {
                short itemSlot = -1;
                for (short i = (short) (freeSlot + 1); i <= inventory.getSlotLimit(); i = (short) (i + 1)) {
                    if (inventory.getItem(i) != null) {
                        itemSlot = i;
                        break;
                    }
                }
                if (itemSlot > 0) {
                    InventoryManipulator.move(c, inventoryType, itemSlot, freeSlot);
                } else {
                    sorted = true;
                }
            } else {
                sorted = true;
            }
        }
        c.announce(PacketCreator.FinishedGather(inventoryType.getType()));
    }

    /**
     * <整理背包>
     */
    public static void ItemSort(InPacket packet, Client c) {
        Player p = c.getPlayer();
        packet.readInt();
        byte inv = packet.readByte();
        if (inv < 0 || inv > 5) {
            return;
        }
        Inventory Inv = p.getInventory(InventoryType.getByType(inv));
        ArrayList<Item> itemarray = new ArrayList<>();
        for (Item i : Inv) {
            itemarray.add(i.copy());
        }
        Collections.sort(itemarray);
        for (Item item : itemarray) {
            InventoryManipulator.removeById(c, InventoryType.getByType(inv), item.getItemId(), item.getQuantity(), false, false);
        }
        for (Item i : itemarray) {
            InventoryManipulator.addFromDrop(c, i, "", false);
        }
        c.announce(PacketCreator.FinishedSort(inv));
    }

    private static void printEquipOptionsInfo(Equip equip, Player player) {
        if (equip.getOptionValues() == null || equip.getOptionValues().isEmpty()) {
            return;
        }
        for (Map.Entry<String, String> kvp : equip.getEquipOption().getOptions().entrySet()) {
            String kongInfo = "●";
            int type = Integer.parseInt(kvp.getKey());
            int val = Integer.parseInt(kvp.getValue());
            String[] infoArr = FuMoInfoMap.get(type);
            if (infoArr != null) {
                String fumoName = infoArr[0];
                String fumoInfo = infoArr[1];
                kongInfo += fumoName + " " + String.format(fumoInfo, val);
            } else if (type >= 100000) {
                kongInfo += "[能手册] 当(" + 技能名字2(type) + ")达到满级时增加 " + val + " 级技能等级";
            } else {
                kongInfo += "[未附魔]";
            }
            player.dropMessage(2, "\t\t\t附魔 : " + kongInfo);
        }
    }

    public static void 输出装备打孔附魔信息(final String mxmxdDaKongFuMo, final Player player) {
        if (mxmxdDaKongFuMo != null && mxmxdDaKongFuMo.length() == 0) {
            return;
        }
        String arr1[] = mxmxdDaKongFuMo.split(",");
        for (int i = 0; i < arr1.length; i++) {
            String pair = arr1[i];
            if (pair.contains(":")) {
                String kongInfo = "●";
                String arr2[] = pair.split(":");
                int fumoType = Integer.parseInt(arr2[0]);
                int fumoVal = Integer.parseInt(arr2[1]);
                if (fumoType > 0 && FuMoInfoMap.containsKey(fumoType)) {
                    String infoArr[] = FuMoInfoMap.get(fumoType);
                    String fumoName = infoArr[0];
                    String fumoInfo = infoArr[1];
                    kongInfo += fumoName + " " + String.format(fumoInfo, fumoVal);
                } else if (一转技能附魔(fumoType) || 二转技能附魔20(fumoType) || 二转技能附魔30(fumoType)) {
                    kongInfo += "[能手册] 当(" + 技能名字2(fumoType) + ")达到满级时增加 " + fumoVal + " 级技能等级";
                } else {
                    kongInfo += "[未附魔]";
                }
                player.dropMessage(2, "\t\t\t附魔 : " + kongInfo);
            }
        }
    }

    public static String 技能名字2(int a) {
        if (技能名字.containsKey(a)) {
            return 技能名字.get(a);
        }
        return "攻击技能";
    }

    /**
     * <角色移动穿戴取下丢弃物品>*
     */
    public static void ItemMove(InPacket packet, Client c) {
        InventoryType type = InventoryType.getByType(packet.readByte());
        short src = packet.readShort();
        short dst = packet.readShort();
        short quantity = packet.readShort();

        if (System.currentTimeMillis() - c.getPlayer().getItemCooldown() < 物品操作间隔) {
            c.getPlayer().dropMessage(1, "操作过快，请你慢点。");
            c.write(PacketCreator.EnableActions());
            return;
        }
        if (src < 0 && dst > 0) {
            //取下装备
            InventoryManipulator.unequip(c, src, dst);
            c.getPlayer().刷新身上装备附魔汇总数据(true);
        } else if (dst < 0) {
            //穿戴装备
            InventoryManipulator.equip(c, src, dst);
            c.getPlayer().刷新身上装备附魔汇总数据(true);
        } else if (dst == 0) {
            //丢弃物品
            InventoryManipulator.drop(c, type, src, quantity);
        } else {
            ItemInformationProvider ii = ItemInformationProvider.getInstance();
            Item item = c.getPlayer().getInventory(type).getItem(src);
            int itemided = item.getItemId();
            //移动装备
            InventoryManipulator.move(c, type, src, dst);
            if (c.getPlayer().getGM() > 0) {
                c.getPlayer().dropMessage(5, "物品代码 : " + itemided);
            }
            if (itemided < 2000000 && !ii.isCash(itemided)) {
                int x = 2;
                Equip nEquip = (Equip) item;
                c.getPlayer().dropMessage(x, " : ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
                c.getPlayer().dropMessage(x, "\t\t\t装备名称 : " + ii.getName(itemided));
                c.getPlayer().dropMessage(x, "\t\t\t穿戴等级 : " + ii.getReqLevel(itemided));
                c.getPlayer().dropMessage(x, "\t\t\t锻造等级 : " + nEquip.getDzlevel());
                c.getPlayer().dropMessage(x, "\t\t\t可砸卷 : " + nEquip.getUpgradeSlots());
                c.getPlayer().dropMessage(x, "\t\t\t已砸卷 : " + nEquip.getLevel());
                c.getPlayer().dropMessage(x, "\t\t\t已打孔 : " + nEquip.getSocket());
                c.getPlayer().dropMessage(x, "\t\t\t已附魔 : " + nEquip.getEquipOption().getOptions().size());
                //c.getPlayer().dropMessage(x, "\t\t\t剩余 : " + nEquip.getEquipOption().getRemainingSockets());
                if (nEquip.getStr() > 0) {
                    c.getPlayer().dropMessage(x, "\t\t\t力量 : " + nEquip.getStr());
                }
                if (nEquip.getDex() > 0) {
                    c.getPlayer().dropMessage(x, "\t\t\t敏捷 : " + nEquip.getDex());
                }
                if (nEquip.getInt() > 0) {
                    c.getPlayer().dropMessage(x, "\t\t\t智力 : " + nEquip.getInt());
                }
                if (nEquip.getLuk() > 0) {
                    c.getPlayer().dropMessage(x, "\t\t\t运气 : " + nEquip.getLuk());
                }
                if (nEquip.getHp() > 0) {
                    c.getPlayer().dropMessage(x, "\t\t\tMaxHP : " + nEquip.getHp());
                }
                if (nEquip.getMp() > 0) {
                    c.getPlayer().dropMessage(x, "\t\t\tMaxMP : " + nEquip.getMp());
                }
                if (nEquip.getWatk() > 0) {
                    c.getPlayer().dropMessage(x, "\t\t\t攻击力 : " + nEquip.getWatk());
                }
                if (nEquip.getMatk() > 0) {
                    c.getPlayer().dropMessage(x, "\t\t\t魔法力 : " + nEquip.getMatk());
                }
                if (nEquip.getWdef() > 0) {
                    c.getPlayer().dropMessage(x, "\t\t\t物理防御力 : " + nEquip.getWdef());
                }
                if (nEquip.getMdef() > 0) {
                    c.getPlayer().dropMessage(x, "\t\t\t魔法防御力 : " + nEquip.getMdef());
                }
                if (nEquip.getAcc() > 0) {
                    c.getPlayer().dropMessage(x, "\t\t\t命中率 : " + nEquip.getAcc());
                }
                if (nEquip.getAvoid() > 0) {
                    c.getPlayer().dropMessage(x, "\t\t\t回避率 : " + nEquip.getAvoid());
                }
                if (nEquip.getHands() > 0) {
                    c.getPlayer().dropMessage(x, "\t\t\t手技 : " + nEquip.getHands());
                }
                if (nEquip.getSpeed() > 0) {
                    c.getPlayer().dropMessage(x, "\t\t\t移动速度 : " + nEquip.getSpeed());
                }
                if (nEquip.getJump() > 0) {
                    c.getPlayer().dropMessage(x, "\t\t\t跳跃力 : " + nEquip.getJump());
                }
                /*String mxmxdDaKongFuMo = nEquip.getOptionValues();
                if (mxmxdDaKongFuMo != null && mxmxdDaKongFuMo.length() > 0) {
                    输出装备打孔附魔信息(mxmxdDaKongFuMo, c.getPlayer());
                }*/
                printEquipOptionsInfo(nEquip, c.getPlayer());
                c.getPlayer().dropMessage(x, "\t\t\t评分 : - ");
                c.getPlayer().dropMessage(x, "\t\t\t区域 : " + 开服名字 + "-" + 大区(c.getPlayer().getWorldId()));

                c.getPlayer().dropMessage(x, " : ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
            }
        }
        //c.getPlayer().saveDatabase();
        c.getPlayer().setItemCooldown(System.currentTimeMillis());
    }

    /**
     * <角色使用物品>*
     */
    public static void UseItem(InPacket packet, Client c) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        Player p = c.getPlayer();

        byte slot = (byte) packet.readShort();
        int itemId = packet.readInt();
        final Item toUse = p.getInventory(InventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId) {
            c.write(PacketCreator.EnableActions());
            return;
        }
        if (p.switch_skill() == 0) {
            MapleStatEffect statEffect = ii.getItemEffect(itemId);
            if (statEffect != null) {
                p.announce(EffectPackets.ShowEffect("s/1"));
            }
        }
        switch (itemId) {
            case 2050000:
                p.dispelDebuff(Disease.POISON);
                break;
            case 2050001:
                p.dispelDebuff(Disease.DARKNESS);
                break;
            case 2050002:
                p.dispelDebuff(Disease.WEAKEN);
                break;
            case 2050003:
                p.dispelDebuff(Disease.SEAL);
                p.dispelDebuff(Disease.CURSE);
                break;
            case 2050004:
                p.dispelDebuffs();
                break;
            default:
                break;
        }
        if (!FieldLimit.CANNOTUSEPOTION.check(p.getMap().getFieldLimit())) {
            if (ItemConstants.isTownScroll(itemId)) {
                if (ii.getItemEffect(toUse.getItemId()).applyTo(p)) {
                    InventoryManipulator.removeFromSlot(c, InventoryType.USE, slot, (short) 1, false);
                } else {
                    c.write(PacketCreator.EnableActions());
                }
                return;
            }
            InventoryManipulator.removeFromSlot(c, InventoryType.USE, slot, (short) 1, false);
            ii.getItemEffect(toUse.getItemId()).applyTo(p);
            p.checkBerserk(p.isHidden());
        } else {
            c.write(PacketCreator.EnableActions());
        }
    }

    /**
     * <使用卷轴>
     */
    public static void UseUpgradeScroll(InPacket packet, Client c) {
        Player p = c.getPlayer();
        //物品的位置
        byte slot = (byte) packet.readShort();
        //装备栏-1
        byte dst = (byte) packet.readShort();
        boolean legendarySpirit = false;
        final ItemInformationProvider ii = ItemInformationProvider.getInstance();

        /*if ((ws & 2) == 2) {
         whiteScroll = true;
         }*/
        Equip toScroll;
        if (dst < 0) {
            toScroll = (Equip) p.getInventory(InventoryType.EQUIPPED).getItem(dst);
        } else {
            legendarySpirit = true;
            toScroll = (Equip) p.getInventory(InventoryType.EQUIP).getItem(dst);
        }
        if (toScroll == null) {
            p.getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT, "不存在装备");
            return;
        }
        //装备已经砸了的次数
        byte oldLevel = toScroll.getLevel();
        //装备剩余砸卷次数
        byte oldSlots = toScroll.getUpgradeSlots();

        if (((Equip) toScroll).getUpgradeSlots() < 1) {
            c.getPlayer().dropMessage(1, "道具已经无法使用卷轴了。");
            c.write(PacketCreator.GetInventoryFull());
            return;
        }
        Inventory useInventory = p.getInventory(InventoryType.USE);
        //使用的物品信息
        Item scroll = useInventory.getItem(slot);
        //Item wscroll = null;

        if (scroll == null || scroll.getQuantity() < 1) {
            p.getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT, "Tried to scroll equip with nonexistent scroll");
            return;
        }

        List<Integer> scrollReqs = ii.getScrollReqs(scroll.getItemId());
        if (scrollReqs.size() > 0 && !scrollReqs.contains(toScroll.getItemId())) {
            c.write(PacketCreator.GetInventoryFull());
            return;
        }

        /*if (whiteScroll) {
         wscroll = p.getInventory(InventoryType.USE).findById(2340000);
         if (wscroll == null || wscroll.getItemId() != 2340000) {
         whiteScroll = false;
         }
         }*/
        if (scroll.getItemId() != 2049100 && !ii.isCleanSlate(scroll.getItemId())) {
            if (!ii.canScroll(scroll.getItemId(), toScroll.getItemId())) {
                return;
            }
        }
        //Equip scrolled = (Equip) ii.scrollEquipWithId(toScroll, scroll.getItemId(), whiteScroll, p.isGameMaster());
        //装备属性变更
        Equip scrolled = (Equip) ii.scrollEquipWithId(toScroll, scroll.getItemId(), false, p.isGameMaster());
        EquipScrollResult scrollSuccess = EquipScrollResult.FAIL;
        if (scrolled == null) {
            scrollSuccess = EquipScrollResult.CURSE;
        } else if (scrolled.getLevel() > oldLevel || (ii.isCleanSlate(scroll.getItemId()) && scrolled.getUpgradeSlots() == oldSlots + 1)) {
            scrollSuccess = EquipScrollResult.SUCCESS;
        }
        //消耗掉物品
        useInventory.removeItem(scroll.getPosition(), (short) 1, false);
        /*if (whiteScroll) {
         if (wscroll != null) {
         useInventory.removeItem(wscroll.getPosition(), (short) 1, false);
         if (wscroll.getQuantity() < 1) {
         c.write(PacketCreator.ClearInventoryItem(InventoryType.USE, wscroll.getPosition(), false));
         } else {
         c.write(PacketCreator.UpdateInventorySlot(InventoryType.USE, (Item) wscroll));
         }
         }
         }*/
        if (scrollSuccess == EquipScrollResult.CURSE) {
            c.write(PacketCreator.ScrolledItem(scroll, toScroll, true));
            if (dst < 0) {
                p.getInventory(InventoryType.EQUIPPED).removeItem(toScroll.getPosition());
            } else {
                p.getInventory(InventoryType.EQUIP).removeItem(toScroll.getPosition());
            }
        } else {
            c.write(PacketCreator.ScrolledItem(scroll, scrolled, false));
        }

        c.write(PacketCreator.GetScrollEffect(p.getId(), scrollSuccess, legendarySpirit));

        PlayerSkill LS = PlayerSkillFactory.getSkill(1003);
        int LSLevel = p.getSkillLevel(LS);
        if (legendarySpirit && LSLevel <= 0) {
            return;
        }
        if (dst < 0 && (scrollSuccess == EquipScrollResult.SUCCESS || scrollSuccess == EquipScrollResult.CURSE)) {
            p.equipChanged();
        }

        switch (toScroll.getItemId()) {
            case 1122000:
                Field map = c.getChannelServer().getMapFactory().getMap(240000000);
                map.broadcastMessage(PacketCreator.ServerNotice(5, "A mysterious power arose as I heard the powerful cry of Nine Spirit Baby Dragon."));
                map.buffField(2022109);
                break;
        }
        if (ItemConstants.isGmScroll(scroll.getItemId()) && !(toScroll.getItemId() == 1122000)) {
            ItemInformationProvider mii = ItemInformationProvider.getInstance();
            MapleStatEffect statEffect = mii.getItemEffect(2022118);
            c.getChannelServer().getPlayerStorage().getAllCharacters().forEach((mc) -> {
                statEffect.applyTo(mc);
            });
            c.getChannelServer().broadcastPacket(PacketCreator.ServerNotice(5, "A Mysterious power arose as I heard the power of the super scroll."));
            BroadcastService.broadcastGMMessage(c.getWorld(), PacketCreator.ServerNotice(5, p.getName() + " is using a GM scroll, itemID: " + scroll.getItemId()));
            FileLogger.print("useGMScroll.txt", p.getName() + " is using a GM scroll, itemID: " + scroll.getItemId());
        }
    }

    /**
     * <使用召唤包>
     */
    public static void UseSummonBag(InPacket packet, Client c) {
        Player p = c.getPlayer();
        final byte slot = (byte) packet.readShort();
        final int itemId = packet.readInt();
        final Item toUse = p.getInventory(InventoryType.USE).getItem(slot);

        if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == itemId) {

            InventoryManipulator.removeFromSlot(c, InventoryType.USE, slot, (short) 1, false);
            if (p.isGameMaster() || !FieldLimit.SUMMON.check(p.getMap().getFieldLimit())) {
                final int[][] toSpawn = ItemInformationProvider.getInstance().getSummonMobs(itemId);

                for (int[] toSpawnChild : toSpawn) {
                    if (Randomizer.nextInt(101) <= toSpawnChild[1]) {
                        p.getMap().spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(toSpawnChild[0]), p.getPosition());
                    }
                }
            }
        }
    }

    /**
     * <捡物品>
     */
    public static void PickupPlayer(InPacket packet, Client c) {
        //63 2D 04 8B 01 A9 00 00 00
        packet.readInt();
        Player p = c.getPlayer();
        final FieldObject ob = p.getMap().getMapObject(packet.readInt(), FieldObjectType.ITEM);
        if (ob == null) {
            c.write(PacketCreator.EnableActions());
            return;
        }

        if (p.getBuffedValue(BuffStat.DarkSight) != null && !p.isGameMaster()) {
            c.write(PacketCreator.EnableActions());
            return;
        }

        final FieldItem fieldItem = (FieldItem) ob;
        final ItemInformationProvider ii = ItemInformationProvider.getInstance();
        if (fieldItem.isPickedUp()) {
            c.write(PacketCreator.EnableActions());
            return;
        }
        if (fieldItem.getOwnerId() != p.getId() && ((!fieldItem.isPlayerDrop() && fieldItem.getDropType() == 0) || (fieldItem.isPlayerDrop() && p.getMap().getEverlast()))) {
            c.write(PacketCreator.EnableActions());
            return;
        }
        if (!fieldItem.isPlayerDrop() && fieldItem.getDropType() == 1 && fieldItem.getOwnerId() != p.getId() && (p.getParty() == null || p.getParty() != null && p.getParty().getMemberById(fieldItem.getOwnerId()) == null)) {
            c.write(PacketCreator.EnableActions());
            return;
        }
        //捡金币
        if (fieldItem.getMeso() > 0) {
            if (p.getParty() != null) {
                ChannelServer cserv = c.getChannelServer();
                int mesosAmm = fieldItem.getMeso();
                int partyNum = 1;
                partyNum = p.getParty().getMembers().stream().filter((partymem) -> (partymem.isOnline() && partymem.getMapId() == p.getMap().getId() && partymem.getChannel() == c.getChannel() && partymem.getPlayer() != null && !partymem.getPlayer().getCashShop().isOpened())).map((_item) -> 1).reduce(partyNum, Integer::sum);
                int mesosGain = Math.max(1, mesosAmm / partyNum);
                p.getParty().getMembers().stream().filter((partyMem) -> (partyMem.isOnline() && partyMem.getMapId() == p.getMap().getId() && partyMem.getChannel() == c.getChannel() && partyMem.getPlayer() != null && !partyMem.getPlayer().getCashShop().isOpened())).map((partyMem) -> cserv.getPlayerStorage().getCharacterById(partyMem.getId())).filter((somecharacter) -> (somecharacter != null)).forEach((somecharacter) -> {
                    somecharacter.gainMeso(mesosGain, true, fieldItem.getOwnerId() != somecharacter.getId());
                });
            } else {
                p.gainMeso(fieldItem.getMeso(), true, true);
            }
            p.getMap().broadcastMessage(PacketCreator.RemoveItemFromMap(fieldItem.getObjectId(), 2, p.getId()));
            p.getCheatTracker().pickupComplete();
            p.getMap().removeMapObject(ob);
            fieldItem.setPickedUp(true);
        } else {
            final int itemId = fieldItem.getItem().getItemId();
            if (ii.isConsumeOnPickup(itemId)) {
                ii.getItemEffect(itemId).applyTo(c.getPlayer());
                c.announce(PacketCreator.GetShowItemGain(itemId, fieldItem.getItem().getQuantity()));
                p.getMap().broadcastMessage(PacketCreator.RemoveItemFromMap(fieldItem.getObjectId(), 2, p.getId()));
                p.getCheatTracker().pickupComplete();
                p.getMap().removeMapObject(ob);
                fieldItem.setPickedUp(true);
            } else if (InventoryManipulator.addFromDrop(c, fieldItem.getItem(), "Picked up by " + p.getId(), true)) {
                p.getMap().broadcastMessage(PacketCreator.RemoveItemFromMap(fieldItem.getObjectId(), 2, p.getId()));
                p.getCheatTracker().pickupComplete();
                p.getMap().removeMapObject(ob);
                if (fieldItem.getItem().getItemId() == ItemConstants.ARIANT_JEWEL) {
                    p.updateAriantScore();
                }
                fieldItem.setPickedUp(true);
            } else {
                p.getCheatTracker().pickupComplete();
            }
        }
        p.getClient().write(PacketCreator.EnableActions());
    }

    public static void PetMapItemPickUp(InPacket packet, Client c) {
        //4F C5 FF D7 00 8D 00 00 00
        Player p = c.getPlayer();
        final ItemPet pet = p.getPet(0);
        packet.readInt();
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        final FieldObject ob = p.getMap().getMapObject(packet.readInt(), FieldObjectType.ITEM);
        if (ob == null || pet == null) {
            return;
        }
        final FieldItem fieldItem = (FieldItem) ob;
        synchronized (fieldItem) {
            if (fieldItem.isPickedUp()) {
                p.announce(PacketCreator.GetInventoryFull());
                return;
            }
            final double Distance = pet.getPosition().distanceSq(fieldItem.getPosition());
            p.getCheatTracker().checkPickupAgain();

            if (Distance > 2500) {
                p.getCheatTracker().registerOffense(CheatingOffense.ITEMVAC);
            } else if (Distance > 640000.0) {
                p.getCheatTracker().registerOffense(CheatingOffense.SHORT_ITEMVAC);
            }
            int mesos = fieldItem.getMeso();
            if (mesos > 0) {
                if (p.getParty() != null) {
                    final ChannelServer cserv = p.getClient().getChannelServer();
                    int partynum = 0;
                    partynum = p.getParty().getMembers().stream().filter((partymem) -> (partymem.isOnline() && partymem.getChannel() == p.getClient().getChannel() && partymem.getMapId() == p.getMap().getId() && partymem.getPlayer() != null && !partymem.getPlayer().getCashShop().isOpened())).map((_item) -> 1).reduce(partynum, Integer::sum);
                    if (partynum == 0) {
                        partynum = 1;
                    }
                    for (MaplePartyCharacter partymem : p.getParty().getMembers()) {
                        if (partymem.isOnline() && partymem.getChannel() == p.getClient().getChannel() && partymem.getMapId() == p.getMap().getId() && partymem.getPlayer() != null && !partymem.getPlayer().getCashShop().isOpened()) {
                            Player somecharacter = cserv.getPlayerStorage().getCharacterById(partymem.getId());
                            if (somecharacter != null) {
                                somecharacter.gainMeso(mesos / partynum, true, false, false);
                                p.getMap().broadcastMessage(PacketCreator.RemoveItemFromMap(fieldItem.getObjectId(), 5, p.getId(), true, 0));
                                p.getCheatTracker().pickupComplete();
                                p.getMap().removeMapObject(ob);
                                fieldItem.setPickedUp(true);
                            }
                        }
                    }
                } else {
                    p.gainMeso(mesos, true, false, false);
                    p.getMap().broadcastMessage(PacketCreator.RemoveItemFromMap(fieldItem.getObjectId(), 5, p.getId(), true, 0));
                    p.getCheatTracker().pickupComplete();
                    p.getMap().removeMapObject(ob);
                    fieldItem.setPickedUp(true);
                }
            } else if (fieldItem.getItem() != null) {
                int itemId = fieldItem.getItem().getItemId();
                if (ii.isConsumeOnPickup(itemId)) {
                    ItemInformationProvider.getInstance().getItemEffect(itemId).applyTo(p);
                    p.announce(PacketCreator.GetShowItemGain(itemId, fieldItem.getItem().getQuantity()));
                    p.getMap().broadcastMessage(PacketCreator.RemoveItemFromMap(fieldItem.getObjectId(), 5, p.getId(), true, 0));
                    p.getCheatTracker().pickupComplete();
                    p.getMap().removeMapObject(ob);
                    fieldItem.setPickedUp(true);
                } else {
                    if (InventoryManipulator.addFromDrop(p.getClient(), fieldItem.getItem(), "Picked up by " + p.getName(), true)) {
                        p.getMap().broadcastMessage(PacketCreator.RemoveItemFromMap(fieldItem.getObjectId(), 5, p.getId(), true, 0));
                        p.getCheatTracker().pickupComplete();
                        p.getMap().removeMapObject(ob);
                        fieldItem.setPickedUp(true);
                    } else {
                        p.getCheatTracker().pickupComplete();
                    }
                }
            }
        }
    }

    public static void UseSkillBook(InPacket packet, Client c) {
        Player p = c.getPlayer();
        packet.readInt();
        short slot = (short) packet.readShort();
        int itemId = packet.readInt();
        final Item toUse = c.getPlayer().getInventory(ItemConstants.getInventoryType(itemId)).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId) {
            return;
        }

        final Map<String, Integer> skilldata = ItemInformationProvider.getInstance().getSkillStats(toUse.getItemId(), c.getPlayer().getJob().getId());
        if (skilldata == null) {
            return;
        }

        boolean canuse = false;
        boolean success = false;
        int skill = 0;
        int maxlevel = 0;

        final int SuccessRate = skilldata.get("success");
        final int ReqSkillLevel = skilldata.get("reqSkillLevel");
        final int MasterLevel = skilldata.get("masterLevel");
        byte i = 0;
        Integer CurrentLoopedSkillId;
        while (true) {
            CurrentLoopedSkillId = skilldata.get("skillid" + i);
            i++;
            if (CurrentLoopedSkillId == null) {
                break;
            }
            final PlayerSkill CurrSkillData = PlayerSkillFactory.getSkill(CurrentLoopedSkillId);
            if (CurrSkillData != null && CurrSkillData.canBeLearnedBy(p.getJob()) && p.getSkillLevel(CurrSkillData) >= ReqSkillLevel && p.getMasterLevel(CurrSkillData) < MasterLevel) {
                canuse = true;
                if (Randomizer.nextInt(100) <= SuccessRate && SuccessRate != 0) {
                    success = true;
                    p.changeSkillLevel(CurrSkillData, p.getSkillLevel(CurrSkillData), (byte) MasterLevel);
                } else {
                    success = false;
                }
                InventoryManipulator.removeFromSlot(c, ItemConstants.getInventoryType(itemId), slot, (short) 1, false);
                break;
            }
        }
        c.getPlayer().getMap().broadcastMessage(PacketCreator.useSkillBook(p, skill, maxlevel, canuse, success));
    }

    public static void UseCatchItem(InPacket packet, Client c) {
        if (System.currentTimeMillis() - c.getPlayer().getLastCatch() < 2000) {
            c.write(PacketCreator.ServerNotice(5, "你现在不能用石头了！"));
            c.write(PacketCreator.EnableActions());
            return;
        }
        packet.readInt();
        final byte slot = (byte) packet.readShort();
        final int itemid = packet.readInt();
        final int oid = packet.readInt();
        final MapleMonster mob = c.getPlayer().getMap().getMonsterByOid(oid);
        final Item toUse = c.getPlayer().getInventory(InventoryType.USE).getItem(slot);

        if (toUse != null && toUse.getQuantity() > 0 && toUse.getItemId() == itemid && mob != null) {
            switch (itemid) {
                case 2270002: { // Characteristic Stone
                    final Field map = c.getPlayer().getMap();
                    if (map.getId() != 980010101 && map.getId() != 980010201 && map.getId() != 980010301 && !c.getPlayer().isGameMaster()) {
                        c.getPlayer().dropMessage(1, "此项不可用于AriantPQ之外的其他用途。");
                        c.write(PacketCreator.EnableActions());
                        return;
                    }
                    if (mob.getHp() <= mob.getMaxHp() / 2) {
                        c.getPlayer().getMap().broadcastMessage(PacketCreator.CatchMonster(oid, itemid, (byte) 1));
                        mob.getMap().killMonster(mob, c.getPlayer(), false, 0);
                        InventoryManipulator.removeById(c, InventoryType.USE, itemid, 1, false, false);
                        InventoryManipulator.addById(c, ItemConstants.ARIANT_JEWEL, (short) 1, null, "", null);
                        c.write(PacketCreator.ServerNotice(5, "You gained a jewel!"));
                        c.getPlayer().setLastCatch(System.currentTimeMillis());
                        c.getPlayer().updateAriantScore();
                    } else {
                        c.getPlayer().getMap().broadcastMessage(PacketCreator.CatchMonster(oid, itemid, (byte) 0));
                        c.getPlayer().dropMessage(5, "The monster has a lot of physical strength, so you can not catch it.");
                    }
                    break;
                }
                case 2270000: { // Pheromone Perfume
                    if (mob.getId() != 9300101) {
                        break;
                    }
                    c.getPlayer().getMap().broadcastMessage(PacketCreator.CatchMonster(oid, itemid, (byte) 1));
                    c.getPlayer().getMap().killMonster(mob, c.getPlayer(), true, (byte) 1);
                    InventoryManipulator.addById(c, 1902000, (short) 1, null, "", null);
                    InventoryManipulator.removeById(c, InventoryType.USE, itemid, 1, false, false);
                    break;
                }
                case 2270003: { // Cliff's Magic Cane
                    if (mob.getId() != 9500320) {
                        break;
                    }
                    if (mob.getHp() < ((mob.getMaxHp() / 10) * 4)) {
                        c.getPlayer().getMap().broadcastMessage(PacketCreator.CatchMonster(oid, itemid, (byte) 1));
                        c.getPlayer().getMap().killMonster(mob, c.getPlayer(), true, (byte) 1);
                        InventoryManipulator.removeById(c, InventoryType.USE, itemid, 1, false, false);
                        InventoryManipulator.addById(c, 4031887, (short) 1, null, "", null);
                    } else {
                        c.getPlayer().getMap().broadcastMessage(PacketCreator.CatchMonster(oid, itemid, (byte) 0));
                        c.getPlayer().dropMessage(5, "The monster has a lot of physical strength so you can not catch it.");
                    }
                    break;
                }
                case 2270001: {
                    if (mob.getId() != 9500197) {
                        break;
                    }
                    if (mob.getHp() < ((mob.getMaxHp() / 10) * 4)) {
                        c.getPlayer().getMap().broadcastMessage(PacketCreator.CatchMonster(oid, itemid, (byte) 1));
                        c.getPlayer().getMap().killMonster(mob, c.getPlayer(), true, (byte) 1);
                        InventoryManipulator.addById(c, 4031830, (short) 1, null, "", null);
                        InventoryManipulator.removeById(c, InventoryType.USE, itemid, 1, false, false);
                    } else {
                        c.getPlayer().getMap().broadcastMessage(PacketCreator.CatchMonster(oid, itemid, (byte) 0));
                    }
                    break;
                }

                case 2270005: {
                    if (mob.getId() != 9300187) {
                        break;
                    }
                    if (mob.getHp() < ((mob.getMaxHp() / 10) * 3)) {
                        c.getPlayer().getMap().broadcastMessage(PacketCreator.CatchMonster(oid, itemid, (byte) 1));
                        c.getPlayer().getMap().killMonster(mob, c.getPlayer(), true, (byte) 1);
                        InventoryManipulator.removeById(c, InventoryType.USE, itemid, 1, false, false);
                        InventoryManipulator.addById(c, 2109001, (short) 1, null, "", null);
                    } else {
                        c.getPlayer().getMap().broadcastMessage(PacketCreator.CatchMonster(oid, itemid, (byte) 0));
                    }
                }
                c.write(PacketCreator.EnableActions());
                break;

                case 2270006: {
                    if (mob.getId() == 9300189) {
                        final Field map = c.getPlayer().getMap();
                        if (mob.getHp() < ((mob.getMaxHp() / 10) * 3)) {
                            map.broadcastMessage(PacketCreator.CatchMonster(mob.getId(), itemid, (byte) 1));
                            map.killMonster(mob, c.getPlayer(), true, (byte) 0);
                            InventoryManipulator.removeById(c, InventoryType.USE, itemid, 1, false, false);
                            InventoryManipulator.addById(c, 2109002, (short) 1, "");
                        }

                    }
                    c.write(PacketCreator.EnableActions());
                    break;
                }
                case 2270007: {
                    if (mob.getId() == 9300191) {
                        final Field map = c.getPlayer().getMap();
                        if (mob.getHp() < ((mob.getMaxHp() / 10) * 3)) {
                            map.broadcastMessage(PacketCreator.CatchMonster(mob.getId(), itemid, (byte) 1));
                            map.killMonster(mob, c.getPlayer(), true, (byte) 0);
                            InventoryManipulator.removeById(c, InventoryType.USE, itemid, 1, false, false);
                            InventoryManipulator.addById(c, 2109003, (short) 1, "");
                        }
                    }
                    c.write(PacketCreator.EnableActions());
                    break;
                }
                case 2270004: {
                    if (mob.getId() == 9300175) {
                        final Field map = c.getPlayer().getMap();
                        if (mob.getHp() < ((mob.getMaxHp() / 10) * 4)) {
                            map.broadcastMessage(PacketCreator.CatchMonster(mob.getId(), itemid, (byte) 1));
                            map.killMonster(mob, c.getPlayer(), true, (byte) 0);
                            InventoryManipulator.removeById(c, InventoryType.USE, itemid, 1, false, false);
                            InventoryManipulator.addById(c, 4001169, (short) 1, "");
                        }
                    }
                    c.write(PacketCreator.EnableActions());
                    break;
                }
            }
        }
    }

    public static void UseMountFood(InPacket packet, Client c) {
        packet.readInt();
        final byte slot = (byte) packet.readShort();
        final int itemid = packet.readInt();
        final Item toUse = c.getPlayer().getInventory(InventoryType.USE).getItem(slot);
        final TamingMob mount = c.getPlayer().getMount();

        if ((itemid / 10000 == 226) && toUse != null && toUse.getQuantity() > 0 && toUse.getItemId() == itemid && mount != null) {
            boolean levelUp;
            final int fatigue;

            fatigue = mount.getTiredness();
            levelUp = false;
            mount.setTiredness(-30);
            if (fatigue > 0) {
                mount.increaseExp();
                final int level = mount.getLevel();
                if (mount.getExp() >= ExperienceConstants.getMountExpNeededForLevel(level + 1) && level < 31) {
                    mount.setLevel((level + 1));
                    levelUp = true;
                }
            }
            c.getPlayer().getMap().broadcastMessage(PacketCreator.UpdateMount(c.getPlayer(), levelUp));
            InventoryManipulator.removeById(c, InventoryType.USE, itemid, 1, true, false);
        }
    }

    public static void UseSilverBox(InPacket packet, Client c) {
        short slot = packet.readShort();
        final int itemId = packet.readInt();

        Item item = c.getPlayer().getInventory(InventoryType.CASH).getItem(slot);
        if (item == null || item.getItemId() != itemId || item.getQuantity() < 1 || !c.getPlayer().haveItem(itemId)) {
            c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT, "Tried open silverbox nonexistent item");
            return;
        }
        if (!c.getPlayer().isAlive()) {
            c.write(PacketCreator.EnableActions());
            return;
        }
        if (!c.getPlayer().haveItem(ItemConstants.SILVER_BOX_KEY)) {
            c.getPlayer().dropMessage(5, "You do not have the box key to open this box.");
            c.write(PacketCreator.EnableActions());
            return;
        }
        for (InventoryType type : InventoryType.values()) {
            if (c.getPlayer().getInventory(type).isFull()) {
                c.getPlayer().dropMessage(5, "You do not have enough inventory space to get the item. Please clear some space from your inventory.");
                c.write(PacketCreator.EnableActions());
                return;
            }
        }
        int prizeid = 0;
        double chance = Math.random();
        if (itemId == ItemConstants.RewardSilverBox.SILVER_BOX_ITEM) {
            if (chance < 0.05) {
                prizeid = ItemConstants.RewardSilverBox.RARE[new Random().nextInt(ItemConstants.RewardSilverBox.RARE.length)];
            } else if (chance >= 0.06 && chance < 0.35) {
                prizeid = ItemConstants.RewardSilverBox.UNCOMMON[new Random().nextInt(ItemConstants.RewardSilverBox.UNCOMMON.length)];
            } else {
                prizeid = ItemConstants.RewardSilverBox.COMMON[new Random().nextInt(ItemConstants.RewardSilverBox.COMMON.length)];
            }
        } else if (itemId == ItemConstants.RewardGoldBox.GOLD_BOX_ITEM) {
            if (chance < 0.1) {
                prizeid = ItemConstants.RewardGoldBox.RARE[new Random().nextInt(ItemConstants.RewardGoldBox.RARE.length)];
            } else if (chance >= 0.11 && chance < 0.35) {
                prizeid = ItemConstants.RewardGoldBox.UNCOMMON[new Random().nextInt(ItemConstants.RewardGoldBox.UNCOMMON.length)];
            } else {
                prizeid = ItemConstants.RewardGoldBox.COMMON[new Random().nextInt(ItemConstants.RewardGoldBox.COMMON.length)];
            }
        }
        if (prizeid != 0) {
            c.getPlayer().gainItem(ItemConstants.SILVER_BOX_KEY, (short) -1, true);
            c.getPlayer().gainItem(itemId, (short) -1, true);
            c.getPlayer().gainItem(prizeid, (short) 1, true);
            c.write(PacketCreator.SilverBoxOpened(itemId));
        }
    }

    public static boolean UseIncubator(Client c, int itemid) {
        String[] types = {"Normal", "Medium", "Rare"};
        HashMap<String, String> IncubatedItem = new HashMap<>();
        try {
            double chance = Math.random();
            BufferedReader br;
            try (FileReader fl = new FileReader(System.getProperty("user.dir") + "/Script/Reward/Incubator/" + ItemConstants.getNameCityIncubator(itemid) + "/" + types[(int) (chance * types.length)] + ".properties")) {
                br = new BufferedReader(fl);
                String[] readSplit = new String[2];
                String readLine = null;
                while ((readLine = br.readLine()) != null) {
                    readSplit = readLine.split(" - ");
                    IncubatedItem.put(readSplit[0], readSplit[1]);
                }
            }
            br.close();
        } catch (IOException e) {
            System.out.println("There was a error with Incubator.");
            FileLogger.printError("Error_Incubator.txt", "Nome - " + ItemConstants.getNameCityIncubator(itemid) + "\r\n" + e);
            return false;
        }

        int randomItem = (int) (Math.random() * IncubatedItem.entrySet().size());
        int hmany = 0;
        int itemCode = 0;
        int quantity = 0;
        for (Map.Entry<String, String> entry : IncubatedItem.entrySet()) {
            hmany++;
            if (hmany == randomItem) {
                try {
                    itemCode = Integer.parseInt(entry.getKey());
                    quantity = Integer.parseInt(entry.getValue());
                    break;
                } catch (NumberFormatException e) {
                    System.out.print(e);
                    return false;
                }
            }
        }
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        if (itemCode == 0 || quantity <= 0) {
            return false;
        }
        if (!InventoryManipulator.checkSpace(c, itemCode, quantity, "")) {
            c.write(PacketCreator.GetInventoryFull());
            c.write(PacketCreator.GetShowInventoryFull());
            c.write(PacketCreator.EnableActions());
            return false;
        }
        if (ItemConstants.getInventoryType(itemCode) == InventoryType.EQUIP) {
            InventoryManipulator.addFromDrop(c, ii.randomizeStatsIncuba((Equip) ii.getEquipById(itemCode)), "Obtained through the Incubator", true);
            c.write(PacketCreator.GetShowItemGain(itemCode, (short) quantity, true));
        } else {
            InventoryManipulator.addById(c, itemCode, (short) quantity, "Obtained through the Incubator", "");
            c.write(PacketCreator.GetShowItemGain(itemCode, (short) quantity, true));
        }
        return true;
    }

    public static String 大区(int a) {
        switch (a) {
            case 0:
                return "蓝蜗牛";
            case 1:
                return "蘑菇仔";
            case 2:
                return "绿水灵";
            case 3:
                return "漂漂猪";
            case 4:
                return "小青蛇";
            case 5:
                return "红螃蟹";
            case 6:
                return "大海龟";
            case 7:
                return "章鱼怪";
            case 8:
                return "顽皮猴";
            case 9:
                return "星精灵";
            case 10:
                return "胖企鹅";
            case 11:
                return "白雪人";
            case 12:
                return "石头人";
            case 13:
                return "紫色猫";
            case 14:
                return "大灰狼";
            case 15:
                return "小白兔";
            case 16:
                return "喷火龙";
            case 17:
                return "火野猪";
            case 18:
                return "青鳄鱼";
            case 19:
                return "花蘑菇";
            default:
                return "未知";
        }
    }
}
