package packet.creators;

import client.player.Player;
import handling.channel.handler.ChannelHeaders.PlayerInteractionHeaders;
import java.util.List;
import packet.opcode.OutHeader;
import packet.transfer.write.OutPacket;
import server.minirooms.PlayerShop;
import server.minirooms.PlayerShopItem;
import server.minirooms.components.SoldItem;

public class PersonalShopPackets {

    public static void AddAnnounceBox(OutPacket wp, PlayerShop shop, int availability) {
        wp.write(4);
        wp.writeInt(shop.getObjectId());
        wp.writeMapleAsciiString(shop.getDescription());
        wp.write(0);
        wp.write(0);
        wp.write(1);
        wp.write(availability);
        wp.writeBool(false);
    }

    public static OutPacket AddCharBox(Player c, int type) {
        //44 03 00 00 00 04 65 00 00 00 05 00 31 31 31 31 31 00 00 01 04 00
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.UPDATE_CHAR_BOX.getValue());
        wp.writeInt(c.getId());
        AddAnnounceBox(wp, c.getPlayerShop(), type);
        return wp;
    }

    public static OutPacket RemoveCharBox(Player p) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.UPDATE_CHAR_BOX.getValue());
        wp.writeInt(p.getId());
        wp.write(0);
        return wp;
    }

    public static OutPacket GetPlayerShopRemoveVisitor(int slot) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.PLAYER_INTERACTION.getValue());
        wp.write(PlayerInteractionHeaders.ACT_EXIT);
        wp.writeShort(slot);
        return wp;
    }

    public static OutPacket ShopErrorMessage(int error, int slot) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.PLAYER_INTERACTION.getValue());
        wp.write(PlayerInteractionHeaders.ACT_EXIT);
        wp.write(slot);
        wp.write(error);
        return wp;
    }
    
    public static OutPacket ShopCannotBuyMessage(byte message) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.PLAYER_INTERACTION.getValue());
        wp.write(PlayerInteractionHeaders.ACT_CANNOT_BUY);
        wp.write(message);
        return wp;
    }

    public static OutPacket ShopChat(String message, int slot) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.PLAYER_INTERACTION.getValue());
        wp.write(PlayerInteractionHeaders.ACT_CHAT);
        wp.write(PlayerInteractionHeaders.ACT_CHAT_THING);
        wp.write(slot);
        wp.writeMapleAsciiString(message);
        return wp;
    }

    public static OutPacket GetPlayerShopNewVisitor(Player c, int slot) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.PLAYER_INTERACTION.getValue());
        wp.write(PlayerInteractionHeaders.ACT_VISIT);
        wp.write(slot);
        InteractionPackets.AddCharLook(wp, c);
        wp.writeMapleAsciiString(c.getName());
        return wp;
    }

    public static OutPacket GetPlayerShop(PlayerShop shop, boolean owner) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.PLAYER_INTERACTION.getValue());
        wp.write(PlayerInteractionHeaders.ACT_JOIN);
        wp.write(4);
        wp.write(4);
        wp.write(owner ? 0 : 1);

        wp.write(0);
        InteractionPackets.AddCharLook(wp, shop.getOwner());
        wp.writeMapleAsciiString(shop.getOwner().getName());
        if (!owner) {
            Player visitors[] = shop.getVisitors();
            for (int i = 0; i < 3; i++) {
                if (visitors[i] != null) {
                    wp.write(i + 1);
                    InteractionPackets.AddCharLook(wp, visitors[i]);
                    wp.writeMapleAsciiString(visitors[i].getName());
                }
            }
        }

        wp.write(0xFF);
        wp.writeMapleAsciiString(shop.getDescription());
        List<PlayerShopItem> items = shop.getItems();
        wp.write(0x10);
        wp.write(items.size());
        for (PlayerShopItem item : items) {
            wp.writeShort(item.getBundles());
            wp.writeShort(item.getItem().getQuantity());
            wp.writeInt(item.getPrice());
            wp.write(item.getItem().getItemType());
            PacketCreator.AddItemInfo(wp, item.getItem(), true, true);
        }
        return wp;
    }

    public static OutPacket ShopItemUpdate(PlayerShop shop) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.PLAYER_INTERACTION.getValue());
        wp.write(PlayerInteractionHeaders.ACT_SHOP_ITEM_UPDATE);
        wp.write(shop.getItems().size());
        for (PlayerShopItem item : shop.getItems()) {
            wp.writeShort(item.getBundles());
            wp.writeShort(item.getItem().getQuantity());
            wp.writeInt(item.getPrice());
            wp.write(item.getItem().getItemType());
            PacketCreator.AddItemInfo(wp, item.getItem(), true, true);
        }
        return wp;
    }

    public static OutPacket GetPlayerShopOwnerUpdate(SoldItem item, int position) {
        OutPacket mplew = new OutPacket();
        mplew.write(OutHeader.PLAYER_INTERACTION.getValue());
        mplew.write(PlayerInteractionHeaders.ACT_SHOP_SOLD_ITEM);
        mplew.write(position);
        mplew.writeShort(item.getQuantity());
        mplew.writeMapleAsciiString(item.getBuyer());
        return mplew;
    }
}
