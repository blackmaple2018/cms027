package packet.creators;

import client.Client;
import client.player.Player;
import client.player.inventory.Inventory;
import client.player.inventory.types.InventoryType;
import client.player.inventory.Item;
import client.player.inventory.ItemFactory;
import constants.ItemConstants;
import handling.channel.handler.ChannelHeaders;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import packet.opcode.OutHeader;
import packet.transfer.write.OutPacket;
import server.itens.Trade;
import server.maps.object.AbstractMapleFieldObject;
import server.minirooms.Minigame;
import server.minirooms.PlayerShop;
import server.minirooms.PlayerShopItem;
import tools.HexTool;
import tools.Pair;

public class InteractionPackets {

    public static OutPacket GetHiredMerchant(Client c, Minigame miniGame, String description) {
        final OutPacket wp = new OutPacket();
        wp.write(OutHeader.PLAYER_INTERACTION.getValue());
        wp.writeBytes(HexTool.getByteArrayFromHexString("05 05 04 00 00 71 C0 4C 00"));
        wp.writeMapleAsciiString(description);
        wp.write(0xFF);
        wp.write(0);
        wp.write(0);
        wp.writeMapleAsciiString(c.getPlayer().getName());
        wp.writeBytes(HexTool.getByteArrayFromHexString("1F 7E 00 00 00 00 00 00 00 00 03 00 31 32 33 10 00 00 00 00 01 01 00 01 00 7B 00 00 00 02 52 8C 1E 00 00 00 80 05 BB 46 E6 17 02 01 00 00 00 00 00"));
        return wp;
    }

    public static OutPacket DestroyHiredMerchant(int id) {
        final OutPacket wp = new OutPacket();
        wp.write(OutHeader.DESTROY_HIRED_MERCHANT.getValue());
        wp.writeInt(id);
        return wp;
    }

    public static OutPacket ShopVisitorAdd(Player p, int slot) {
        final OutPacket wp = new OutPacket();
        wp.write(OutHeader.PLAYER_INTERACTION.getValue());
        wp.write(0x04);
        wp.write(slot);
        PacketCreator.AddCharLook(wp, p, false);
        wp.writeMapleAsciiString(p.getName());
        return wp;
    }

    public static OutPacket ShopVisitorLeave(int slot) {
        final OutPacket wp = new OutPacket();
        wp.write(OutHeader.PLAYER_INTERACTION.getValue());
        wp.write(0x0A);
        wp.write(slot);
        return wp;
    }

    public static OutPacket ShopErrorMessage(int error, int type) {
        final OutPacket wp = new OutPacket();
        wp.write(OutHeader.PLAYER_INTERACTION.getValue());
        wp.write(0x0A);
        wp.write(type);
        wp.write(error);
        return wp;
    }

    public static OutPacket ShopChat(String message, int slot) {
        final OutPacket wp = new OutPacket();
        wp.write(OutHeader.PLAYER_INTERACTION.getValue());
        wp.write(0x06);
        wp.write(8);
        wp.write(slot);
        wp.writeMapleAsciiString(message);
        return wp;
    }

    public static OutPacket GetMiniBoxFull() {
        final OutPacket wp = new OutPacket();
        wp.write(OutHeader.PLAYER_INTERACTION.getValue());
        wp.writeShort(5);
        wp.write(2);
        return wp;
    }

    public static OutPacket GetTradeInvite(Player p) {
        final OutPacket wp = new OutPacket();
        wp.write(OutHeader.PLAYER_INTERACTION.getValue());
        wp.writeBytes(HexTool.getByteArrayFromHexString("02 03"));
        wp.writeMapleAsciiString(p.getName());
        wp.writeBytes(HexTool.getByteArrayFromHexString("B7 50 00 00"));//objectid
        return wp;
    }

    public static OutPacket GetTradeMesoSet(byte number, int meso) {
        final OutPacket wp = new OutPacket();
        wp.write(OutHeader.PLAYER_INTERACTION.getValue());
        wp.write(0xE);
        wp.write(number);
        wp.writeInt(meso);
        return wp;
    }

    public static OutPacket GetTradeItemAdd(byte number, Item item) {
        final OutPacket wp = new OutPacket();
        wp.write(OutHeader.PLAYER_INTERACTION.getValue());
        wp.write(0xD);
        wp.write(number);
        wp.write(item.getPosition());
        wp.write(item.getItemType());
        PacketCreator.AddItemInfo(wp, item, true, true);
        return wp;
    }

    public static OutPacket GetTradeStart(Client c, Trade trade, byte number) {
        final OutPacket wp = new OutPacket();
        wp.write(OutHeader.PLAYER_INTERACTION.getValue());
        wp.write(5);//enter room
        wp.write(3);//room base
        wp.write(2);//room slots
        wp.write(number);
        if (number == 1) {
            wp.write(0);
            AddCharLook(wp, trade.getPartner().getChr());
            wp.writeMapleAsciiString(trade.getPartner().getChr().getName());
        }
        wp.write(number);
        AddCharLook(wp, c.getPlayer());
        wp.writeMapleAsciiString(c.getPlayer().getName());
        wp.write(0xFF);
        return wp;
    }

    public static void AddCharLook(OutPacket wp, Player p) {
        wp.write(p.getGender());
        wp.write(p.getSkinColor().getId());
        wp.writeInt(p.getFace());
        wp.writeBool(false);
        wp.writeInt(p.getHair());
        
        Inventory equip = p.getInventory(InventoryType.EQUIPPED);
        Map<Short, Integer> myEquip = new LinkedHashMap<>();
        Map<Short, Integer> maskedEquip = new LinkedHashMap<>();

        equip.list().stream().forEach((item) -> {
            short pos = (short) Math.abs(item.getPosition());
            if (pos != 111) {
                if (pos > 100) {
                    pos -= 100;
                    myEquip.put(pos, item.getItemId());
                } else {
                    if (myEquip.get(pos) != null) {
                        maskedEquip.put(pos, item.getItemId());
                    } else {
                        myEquip.put(pos, item.getItemId());
                    }
                }
            }
        });

        myEquip.entrySet().stream().map((entry) -> {
            wp.write(entry.getKey());
            return entry;
        }).forEach((entry) -> {
            wp.writeInt(entry.getValue());
        });

        wp.write(11);
        Item cWeapon = equip.getItem((short) -111);
        wp.writeInt(cWeapon != null ? cWeapon.getItemId() : 0);
        wp.write(-1);
        
        wp.writeInt(0);
    }

    public static OutPacket GetTradeConfirmation() {
        OutPacket mplew = new OutPacket();
        mplew.write(OutHeader.PLAYER_INTERACTION.getValue());
        mplew.write(0x0F);
        return mplew;
    }

    public static OutPacket GetTradeCompletion(byte number) {
        final OutPacket wp = new OutPacket();
        wp.write(OutHeader.PLAYER_INTERACTION.getValue());
        wp.write(0xA);
        wp.write(number);
        wp.write(5);
        return wp;
    }

    public static OutPacket GetTradeCancel(byte number) {
        final OutPacket wp = new OutPacket();
        wp.write(OutHeader.PLAYER_INTERACTION.getValue());
        wp.write(0xA);
        wp.write(number);
        wp.write(2);
        return wp;
    }

    public static OutPacket GetPlayerShopChat(Player p, String chat, boolean owner) {
        final OutPacket wp = new OutPacket();
        wp.write(OutHeader.PLAYER_INTERACTION.getValue());
        wp.writeBytes(HexTool.getByteArrayFromHexString("06 08"));
        wp.write(owner ? 0 : 1);
        wp.writeMapleAsciiString(p.getName() + " : " + chat);
        return wp;
    }

    public static OutPacket GetPlayerShopChat(Player p, String chat, byte slot) {
        final OutPacket wp = new OutPacket();
        wp.write(OutHeader.PLAYER_INTERACTION.getValue());
        wp.writeBytes(HexTool.getByteArrayFromHexString("06 08"));
        wp.write(slot);
        wp.writeMapleAsciiString(p.getName() + " : " + chat);
        return wp;
    }

    public static OutPacket GetTradePartnerAdd(Player p) {
        final OutPacket wp = new OutPacket();
        wp.write(OutHeader.PLAYER_INTERACTION.getValue());
        wp.writeBytes(HexTool.getByteArrayFromHexString("04 01"));
        AddCharLook(wp, p);
        wp.writeMapleAsciiString(p.getName());
        return wp;
    }

    public static OutPacket SendShopLinkResult(int msg) {
        OutPacket mplew = new OutPacket(3);
        mplew.write(OutHeader.SHOP_LINK_RESULT.getValue());
        mplew.write(msg);
        return mplew;
    }

    public static OutPacket ShopScannerResult(Client c, boolean displayTopTen, int itemid, List<Pair<PlayerShopItem, AbstractMapleFieldObject>> hmsAvailable, ConcurrentLinkedQueue<Integer> mostSearched) {
        byte itemType = ItemConstants.getInventoryType(itemid).getType();
        final OutPacket wp = new OutPacket();
        wp.write(OutHeader.SHOP_SCANNER_RESULT.getValue());
        wp.write(displayTopTen ? 7 : 6);
        if (!displayTopTen) {
            wp.writeInt(itemid);
            wp.writeInt(hmsAvailable.size());
            for (Pair<PlayerShopItem, AbstractMapleFieldObject> hme : hmsAvailable) {
                PlayerShopItem item = hme.getLeft();
                AbstractMapleFieldObject mo = hme.getRight();

                if (mo instanceof PlayerShop) {
                    PlayerShop ps = (PlayerShop) mo;
                    Player owner = ps.getOwner();

                    wp.writeMapleAsciiString(owner.getName());
                    wp.writeInt(owner.getMapId());
                    wp.writeMapleAsciiString(ps.getDescription());
                    wp.writeInt(item.getBundles());
                    wp.writeInt(item.getItem().getQuantity());
                    wp.writeInt(item.getPrice());
                    wp.writeInt(owner.getId());
                    wp.write(owner.getClient().getChannel() - 1);
                } 
                wp.write(itemType);
                if (itemType == InventoryType.EQUIP.getType()) {
                    PacketCreator.AddItemInfo(wp, item.getItem(), true, false);
                }
            }
        } else {
            wp.write(mostSearched.size());
            for (Integer i : mostSearched) {
                wp.writeInt(i);
            }
        }
        return wp;
    }

    /**
     * 1 = Room already closed * 2 = Can't enter due full cappacity 3 = Other
     * requests at this minute 4 = Can't do while dead 5 = Can't do while middle
     * event 6 = This character unable to do it 7, 20 = Not allowed to trade
     * anymore 9 = Can only trade on same map 10 = May not open store near
     * portal 11, 14 = Can't start game here 12 = Can't open store at this
     * channel 13 = Can't estabilish miniroom 15 = Stores only an the free
     * market 16 = Lists the rooms at FM (?) 17 = You may not enter this store
     * 18 = Owner undergoing store maintenance 19 = Unable to enter tournament
     * room 21 = Not enough mesos to enter 22 = Incorrect password
     *
     * @param status
     * @return
     */
    public static OutPacket GetMiniroomMessage(int status) {
        final OutPacket mplew = new OutPacket(5);
        mplew.write(OutHeader.PLAYER_INTERACTION.getValue());
        mplew.write(ChannelHeaders.PlayerInteractionHeaders.ACT_JOIN);
        mplew.write(0);
        mplew.write(status);
        return mplew;
    }
}
