package packet.creators;

import cashshop.CashItem;
import cashshop.CashItemFactory;
import client.Client;
import client.player.Player;
import client.player.inventory.Item;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import packet.opcode.OutHeader;
import packet.transfer.write.OutPacket;
import tools.HexTool;
import tools.Pair;
import tools.StringUtil;

public class CashShopPackets {

    private final static byte[] CHAR_INFO_MAGIC = new byte[]{(byte) 0xff, (byte) 0xc9, (byte) 0x9a, 0x3b};
    private static final byte[] ADDITIONAL_CS_BYTES = HexTool.getByteArrayFromHexString("00 08 00 00 00 37 00 31 00 38 00 31 00 00 00 00 00 18 00 0E 00 0F 00 0C 06 38 02 14 00 08 80 B6 03 67 00 69 00 6E 00 49 00 70 00 00 00 00 00 00 00 06 00 04 00 13 00 0E 06 A8 01 14 00 D8 9F CD 03 33 00 2E 00 33 00 31 00 2E 00 32 00 33 00 35 00 2E 00 32 00 32 00 34 00 00 00 00 00 00 00 00 00 04 00 0A 00 15 01 0C 06 0E 00 00 00 62 00 65 00 67 00 69 00");

    private static final byte INVENTORY = 0x2F,
            GIFTS = 0x31,
            DISPLAY_WISH_LIST = 0x33,
            UPDATE_WISH_LIST = 0x39,
            WISH_LIST_ERROR = 0x3A,
            INSERT_TO_STAGING = 0x3B,
            BUY_ERROR = 0x24,
            REDEEM_COUPON = 0x3D,
            COUPON_ERROR = 0x40,
            UPDATE_INVENTORY_SLOTS = 0x44,
            INVENTORY_SLOTS_ERROR = 0x45,
            UPDATE_STORAGE_SLOTS = 0x46,
            STORAGE_SLOTS_ERROR = 0x47,
            UPDATE_CHARACTER_SLOTS = 0x48,
            CHARACTER_SLOTS_ERROR = 0x49,
            MOVE_FROM_STAGING = 0x4A,
            MOVE_FROM_STAGING_ERROR = 0x4B,
            MOVE_TO_STAGING = 0x4C,
            MOVE_TO_STAGING_ERROR = 0x4D,
            EXPIRE_ITEM = 0x4E,
            SEND_GIFT = 0x6B,
            GIFT_ERROR = 0x6C,
            BUY_MESO_ITEM = 0x6D;

    public static byte ERROR_UNKNOWN = 0x00,
            ERROR_UNKNOWN_THEN_EXIT_TO_CHANNEL_1 = (byte) 0x7F,
            ERROR_REQUEST_TIMED_OUT = (byte) 0x80,
            ERROR_UNKNOWN_WARP_TO_CHANNEL_2 = (byte) 0x81,
            ERROR_INSUFFICIENT_CASH = (byte) 0x82,
            ERROR_AGE_LIMIT = (byte) 0x83,
            ERROR_EXCEEDED_ALLOTTED_LIMIT_OF_PRICE = (byte) 0x84,
            ERROR_TOO_MANY_CASH_ITEMS = (byte) 0x85,
            ERROR_GIFT_ITEM_RECEIVER_GENDER = (byte) 0x86,
            ERROR_COUPON_NUMBER = (byte) 0x87,
            ERROR_COUPON_EXPIRED = (byte) 0x88,
            ERROR_COUPON_USED = (byte) 0x89,
            ERROR_NEXON_CAFE_ONLY_COUPON = (byte) 0x8A,
            ERROR_USED_NEXON_CAFE_ONLY_COUPON = (byte) 0x8B,
            ERROR_EXPIRED_NEXON_CAFE_ONLY_COUPON = (byte) 0x8C,
            ERROR_IS_COUPON_NUMBER = (byte) 0x8D,
            ERROR_GENDER_RESTRICTIONS = (byte) 0x8E,
            ERROR_REGULAR_ITEM_ONLY_COUPON = (byte) 0x8F,
            ERROR_MAPLESTORY_ONLY_COUPON_NO_GIFTS = (byte) 0x90,
            ERROR_INVENTORY_FULL = (byte) 0x91,
            ERROR_PREMIUM_SERVICE = (byte) 0x92,
            ERROR_INVALID_RECIPIENT = (byte) 0x93,
            ERROR_RECIPIENT_NAME = (byte) 0x94,
            ERROR_OUT_OF_STOCK = (byte) 0x96,
            ERROR_INSUFFICIENT_MESOS = (byte) 0x98,
            ERROR_CASH_SHOP_IN_BETA = (byte) 0x99,
            ERROR_BIRTHDAY = (byte) 0x9A,
            ERROR_ONLY_AVAILABLE_FOR_GIFTS = (byte) 0x9D,
            ERROR_ALREADY_APPLIED = (byte) 0x9E,
            ERROR_DAILY_PURCHASE_LIMIT = (byte) 0xA3,
            ERROR_MAXIMUM_USAGE = (byte) 0xA6,
            ERROR_COUPON_SYSTEM_COMING_SOON = (byte) 0xA7,
            ERROR_ITEM_ONLY_USABLE_15_DAYS_AFTER_REGGING = (byte) 0xA8,
            ERROR_INSUFFICIENT_GIFT_TOKENS = (byte) 0xA9,
            ERROR_SEND_TECHNICAL_DIFFICULTIES = (byte) 0xAA,
            ERROR_SEND_LESS_THAN_2_WEEKS_SINCE_FIRST_CHARGE = (byte) 0xAB,
            ERROR_CANNOT_GIVE_WITH_BAN_HISTORY = (byte) 0xAC,
            ERROR_GIFT_LIMITATION = (byte) 0xAD,
            ERROR_TOO_LATE_TO_GIVE = (byte) 0xAE,
            ERROR_GIFT_TECHNICAL_DIFFICULTIES = (byte) 0xAF,
            ERROR_TRANSFER_TO_WORLD_UNDER_20 = (byte) 0xB0,
            ERROR_TRANSFER_TO_SAME_WORLD = (byte) 0xB1,
            ERROR_TRANSFER_TO_YOUNG_WORLD = (byte) 0xB2,
            ERROR_WORLD_CHARACTER_SLOTS_FULL = (byte) 0xB3,
            ERROR_EVENT_NOT_AVAILABLE_OR_EXPIRED = (byte) 0xCE;

    private static void AddExpirationTime(final OutPacket wp, long time) {
        wp.writeLong(PacketCreator.GetTime(time));
    }

    public static OutPacket GiftError(byte message) {
        return ErrorSimple(GIFT_ERROR, message);
    }

    public static OutPacket BuyError(byte message) {
        return ErrorSimple(BUY_ERROR, message);
    }

    public static OutPacket WishListError(byte message) {
        return ErrorSimple(WISH_LIST_ERROR, message);
    }

    public static OutPacket TakeError(byte message) {
        return ErrorSimple(MOVE_FROM_STAGING_ERROR, message);
    }

    public static OutPacket PlaceError(byte message) {
        return ErrorSimple(MOVE_TO_STAGING_ERROR, message);
    }

    public static OutPacket BuyInventorySlotsError(byte message) {
        return ErrorSimple(INVENTORY_SLOTS_ERROR, message);
    }

    public static OutPacket BuyStorageSlotsError(byte message) {
        return ErrorSimple(STORAGE_SLOTS_ERROR, message);
    }

    public static OutPacket BuyCharacterSlotsError(byte message) {
        return ErrorSimple(CHARACTER_SLOTS_ERROR, message);
    }

    public static OutPacket CouponError(byte message) {
        return ErrorSimple(COUPON_ERROR, message);
    }

    public static void AddCashItemInformation(final OutPacket wp, Item item, int accountId) {
        AddCashItemInformation(wp, item, accountId, null);
    }

    public static OutPacket TakeFromCashInventory(Item item, short destPos) {
        OutPacket lew = new OutPacket();
        lew.write(OutHeader.CASH_SHOP.getValue());
        lew.write(48);
        lew.writeShort(destPos);
        lew.write(item.getItemType());
        PacketCreator.AddItemInfo(lew, item, true, true);
        lew.writeLong(0);
        return lew;
    }

    public static OutPacket PutIntoCashInventory(Item item, int accountId) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.CASH_SHOP.getValue());
        wp.write(50);
        AddCashItemInformation(wp, item, accountId);
        return wp;
    }

    public static OutPacket UpdateCharacterSlots(short newCapacity) {
        OutPacket wp = new OutPacket(5);
        wp.write(OutHeader.CASH_SHOP.getValue());
        wp.write(UPDATE_CHARACTER_SLOTS);
        wp.writeShort(newCapacity);
        return wp;
    }

    private static OutPacket ErrorSimple(byte header, byte message) {
        OutPacket wp = new OutPacket(message != ERROR_OUT_OF_STOCK ? 4 : 8);
        wp.write(OutHeader.CASH_SHOP.getValue());
        wp.write(header);
        wp.write(message);
        if (message == ERROR_OUT_OF_STOCK) {
            wp.writeInt(0);
        }
        return wp;
    }

    public static OutPacket UpdateInventorySlots(byte invType, short newCapacity) {
        OutPacket wp = new OutPacket(6);
        wp.write(OutHeader.CASH_SHOP.getValue());
        wp.write(44);
        wp.write(invType);
        wp.writeShort(newCapacity);
        return wp;
    }

    public static OutPacket UpdateStorageSlots(short newCapacity) {
        OutPacket wp = new OutPacket(5);
        wp.write(OutHeader.CASH_SHOP.getValue());
        wp.write(46);
        wp.writeShort(newCapacity);
        return wp;
    }

    public static OutPacket GiftSent(String recipient, int itemId, int price, int quantity, boolean packages) {
        OutPacket wp = new OutPacket(17 + recipient.length());
        wp.write(OutHeader.CASH_SHOP.getValue());
        wp.write(42);
        wp.writeMapleAsciiString(recipient);
        wp.writeInt(itemId);
        wp.writeShort(quantity);
        wp.writeShort(0);
        wp.writeInt(price);
        return wp;
    }

    public static OutPacket ItemExpired(long uniqueId) {
        OutPacket wp = new OutPacket(11);
        wp.write(OutHeader.CASH_SHOP.getValue());
        wp.write(EXPIRE_ITEM);
        wp.writeLong(uniqueId);
        return wp;
    }

    public static OutPacket ShowBoughtCashItem(Item item, int accountId) {
        OutPacket mplew = new OutPacket();
        mplew.write(OutHeader.CASH_SHOP.getValue());
        mplew.write(35);
        AddCashItemInformation(mplew, item, accountId);
        return mplew;
    }

    public static void AddCashItemInformation(final OutPacket wp, Item item, int accountId, String giftMessage) {
        boolean isGift = giftMessage != null;
        wp.writeLong(item.getUniqueId() > 0 ? item.getUniqueId() : 0);
        if (!isGift) {
            wp.writeInt(accountId);
            wp.writeInt(0);//charid
        }
        wp.writeInt(item.getItemId());
        if (!isGift) {
            wp.writeInt(item.getSN());
            wp.writeShort(item.getQuantity());
        }
        wp.writeAsciiString(item.getGiftFrom(), 19);
        if (isGift) {
            wp.writeAsciiString(giftMessage, 73);
            return;
        }
        AddExpirationTime(wp, item.getExpiration());
    }

    public static OutPacket ShowCash(Player p) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.CS_UPDATE.getValue());
        wp.writeInt(p.getCashShop().getCash(1));
        wp.writeInt(p.getCashShop().getCash(2));
        return wp;
    }

    public static OutPacket ShowCashInventory(Client c, List<Pair<Item, String>> gifts) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.CASH_SHOP.getValue());
        wp.write(29);
        wp.writeShort(c.getPlayer().getCashShop().getInventory().size());
        c.getPlayer().getCashShop().getInventory().stream().forEach((item) -> {
            AddCashItemInformation(wp, item, c.getAccountID());
        });
        wp.writeShort(gifts.size());
        gifts.stream().forEach((gift) -> {
            AddCashItemInformation(wp, gift.getLeft(), 0, gift.getRight());
        });
        wp.writeShort(c.getPlayer().getStorage().getSlots());
        return wp;
    }

    public static OutPacket CouponRewards(final int accid, final int MaplePoints, final Map<Integer, Item> items1, final List<Pair<Integer, Integer>> items2, final int mesos) {
        OutPacket mplew = new OutPacket();
        mplew.write(OutHeader.CASH_SHOP.getValue());
        mplew.write(REDEEM_COUPON);
        mplew.write(items1.size());
        items1.entrySet().stream().forEach((sn) -> {
            AddCashItemInformation(mplew, sn.getValue(), accid, null);
        });
        mplew.writeInt(MaplePoints);
        mplew.writeInt(items2.size());
        items2.stream().map((item) -> {
            mplew.writeInt(item.getRight());
            return item;
        }).forEach((item) -> {
            mplew.writeInt(item.getLeft());
        });
        mplew.writeInt(mesos);

        return mplew;
    }

    public static OutPacket ShowCouponRedeemedItem(int itemID) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.CASH_SHOP.getValue());
        wp.writeShort(WISH_LIST_ERROR);
        wp.writeInt(0);
        wp.writeInt(1);
        wp.writeShort(1);
        wp.writeShort(0x1A);
        wp.writeInt(itemID);
        wp.writeInt(0);
        return wp;
    }

    public static OutPacket WrongCouponCode() {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.CASH_SHOP.getValue());
        wp.write(COUPON_ERROR);
        wp.write(ERROR_COUPON_NUMBER);
        return wp;
    }

    public static OutPacket TransferToCashShop(Client c) {
        OutPacket wp = new OutPacket(OutHeader.CS_OPEN);
        PacketCreator.AddCharacterData(wp, c.getPlayer());
        wp.writeMapleAsciiString(c.getAccountName());

        wp.writeShort(0); //blockedSerials.size()

        Collection<CashItem> items = CashItemFactory.getItems().values().stream().filter((ci) -> !(ci.getSN() >= 80000000)).collect(Collectors.toList());

        wp.writeShort(items.size()); //(short) moddedCommodities.size()
        for (CashItem ci : items) {
            wp.writeInt(ci.getSN());
            wp.writeInt(ci.getItemId());
            wp.writeInt(ci.getCount());
            wp.writeInt(ci.getPrice());
            wp.writeInt(ci.getPrice());
            wp.writeInt(ci.getPeriod());
            wp.writeInt(0);
            wp.writeInt(8);//Priority
            wp.write(0);
            wp.writeInt(ci.getGender());//Gender
            wp.writeBool(ci.isOnSale());
            wp.writeInt(0);//termEnd
            wp.write(0);
            wp.writeInt(0);
            wp.writeInt(0);
        }

        //best itens
        for (byte i = 1; i <= 8; i++) {
            for (byte j = 0; j <= 1; j++) {
                wp.writeInt(10000281);
                wp.writeInt(i);
                wp.writeInt(j);

                wp.writeInt(10000282);
                wp.writeInt(i);
                wp.writeInt(j);

                wp.writeInt(10000283);
                wp.writeInt(i);
                wp.writeInt(j);

                wp.writeInt(10000284);
                wp.writeInt(i);
                wp.writeInt(j);

                wp.writeInt(10000285);
                wp.writeInt(i);
                wp.writeInt(j);
            }
        }

        wp.writeShort(5); // Stock 
        wp.writeInt(-1); // 1 = Sold Out, 2 = Not Sold      
        wp.writeInt(20900028);
        wp.writeInt(0); // 1 = Sold Out, 2 = Not Sold
        wp.writeInt(20900027);
        wp.writeInt(2); // 1 = Sold Out, 2 = Not Sold
        wp.writeInt(20900026);
        wp.writeInt(4); // 1 = Sold Out, 2 = Not Sold
        wp.writeInt(20900026);
        wp.writeInt(5); // 1 = Sold Out, 2 = Not Sold
        wp.writeInt(20900026);

        return wp;
    }
}
