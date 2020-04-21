package cashshop;

import static cashshop.CashShopTools.getMaxInventorySlots;
import client.Client;
import packet.transfer.read.InPacket;
import java.util.List;
import packet.creators.CashShopPackets;
import packet.creators.PacketCreator;
import client.player.Player;
import client.player.PlayerNote;
import client.player.PlayerQuery;
import client.player.inventory.Inventory;
import client.player.inventory.InventoryIdentifier;
import client.player.inventory.types.InventoryType;
import client.player.inventory.Item;
import client.player.inventory.ItemRing;
import security.violation.CheatingOffense;
import constants.GameConstants;
import server.itens.InventoryManipulator;
import server.itens.ItemInformationProvider;

/**
 * <商城>
 */
public class CashShopOperation {

    public static void CashShopAction(InPacket packet, Client c) {
        Player p = c.getPlayer();
        switch (packet.readByte()) {
            //购买物品
            case 2:
                BuyCashItem(p, packet);
                break;
            //送礼物
            case 3:
                GiftItem(p, packet);
                break;
            //判断扩充背包
            case 5:
                BuyInventorySlots(p, packet);
                break;
            //扩充背包
            case 6:
                CashInventorySlots(p, packet);
                break;
            case 7:
                BuyCharacterSlots(p, packet);
                break;
            //物品从商城移动到背包
            case 10:
                TakeFromCashInventory(p, packet);
                break;
            //物品从背包移动到商城
            case 11:
                PutIntoCashInventory(p, packet);
                break;
            case 28:
                BuyPackage(p, packet);
                break;
            case 29:
                GiftPackage(p, packet);
                break;
            case 30:
                BuyQuestItem(p, packet);
                break;
            case 27:
                BuyFriendshipAndCoupleRing(p, packet, true);
                break;
            case 33:
                BuyFriendshipAndCoupleRing(p, packet, false);
                break;
            default:
                break;
        }
    }

    private static void updateInformation(Client c, Item item, boolean showBuy) {
        if (showBuy) {
            c.write(CashShopPackets.ShowBoughtCashItem(item, c.getAccountID()));
        }
        c.write(CashShopPackets.ShowCash(c.getPlayer()));
        c.write(PacketCreator.EnableActions());
    }

    private static void BuyCashItem(Player p, InPacket packet) {
        int currencyType = packet.readByte() + 1;
        int serialNumber = packet.readInt();
        CashItem cashItem = CashItemFactory.getItem(serialNumber);

        if (cashItem == null || !cashItem.isOnSale() || p.getCashShop().getCash(currencyType) < cashItem.getPrice()) {
            //p.getClient().write(CashShopPackets.BuyError(CashShopPackets.ERROR_INSUFFICIENT_CASH));    
            p.dropMessage(1, "点卷不足。");
            updateInformation(p.getClient(), null, false);
            return;
        }
        if (p.getCashShop().isFull()) {
            p.dropMessage(1, "商城背包已满。");
            updateInformation(p.getClient(), null, false);
            //p.getClient().write(CashShopPackets.BuyError(CashShopPackets.ERROR_INVENTORY_FULL));
            return;
        }
        if (!cashItem.genderEquals(p.getGender())) {
            p.dropMessage(1, "该道具与你性别不符。");
            updateInformation(p.getClient(), null, false);
            //p.getClient().write(CashShopPackets.BuyError(CashShopPackets.ERROR_GENDER_RESTRICTIONS));
            return;
        }
        Item item = cashItem.toItem(cashItem);
        p.getCashShop().addToInventory(item);
        p.getCashShop().gainCash(currencyType, -cashItem.getPrice());
        String x = "每日任务_游戏商城购买道具";
        if (p.getbosslog(x) <= 0) {
            p.setbosslog(x);
        }
        updateInformation(p.getClient(), item, true);
    }

    private static void GiftItem(Player p, InPacket packet) {
        //73 03 DB 96 98 00 04 00 C8 FD C7 F2 02 00 0A 31
        int serialNumber = packet.readInt();
        String recipient = packet.readMapleAsciiString();
        String message = packet.readMapleAsciiString();

        CashItem item = CashItemFactory.getItem(serialNumber);
        if (item == null || !item.isOnSale()) {
            p.dropMessage(1, "道具当前不在销售状态。");
            updateInformation(p.getClient(), null, false);
            //p.getClient().write(CashShopPackets.GiftError(CashShopPackets.ERROR_OUT_OF_STOCK));
            return;
        }
        if (p.getCashShop().getCash(1) < item.getPrice() || message.getBytes().length > 78 || message.getBytes().length < 1 || item.getPrice() <= 0) {
            p.dropMessage(1, "发生未知错误。");
            updateInformation(p.getClient(), null, false);
            //p.getClient().write(CashShopPackets.GiftError(CashShopPackets.ERROR_INSUFFICIENT_CASH));
            return;
        }

        int recipientAcct = PlayerQuery.getIdByName(recipient);
        if (recipientAcct == -1) {
            p.dropMessage(1, "请确认接受人的姓名是否正确。");
            updateInformation(p.getClient(), null, false);
            //p.getClient().write(CashShopPackets.GiftError(CashShopPackets.ERROR_RECIPIENT_NAME));
            return;
        }
        if (!item.genderEquals(PlayerQuery.getGenderById(recipientAcct))) {
            p.dropMessage(1, "请确认接受人的性别是否符合道具要求。");
            updateInformation(p.getClient(), null, false);
            //p.getClient().write(CashShopPackets.GiftError(CashShopPackets.ERROR_GIFT_ITEM_RECEIVER_GENDER));
            return;
        }
        p.getCashShop().gift(recipientAcct, p.getName(), message, item.getSN(), InventoryManipulator.getUniqueId(item.getItemId(), null));
        p.getCashShop().gainCash(1, -item.getPrice());
        p.getClient().write(CashShopPackets.GiftSent(recipient, item.getItemId(), item.getPrice(), item.getCount(), false));
        updateInformation(p.getClient(), null, false);
    }

    private static void BuyInventorySlots(Player p, InPacket packet) {
        //73 05 00 01
        int currencyType = packet.readByte() + 1;
        InventoryType invType = InventoryType.getByType(packet.readByte());
        int cost = 600;

        Inventory inv = p.getInventory(invType);
        byte currentSlots = inv.getSlotLimit();

        if (currentSlots + 4 > getMaxInventorySlots(p.getJob().getId(), invType)) {
            p.dropMessage("背包空间已达最大值。");
            updateInformation(p.getClient(), null, false);
            //p.getClient().write(CashShopPackets.BuyInventorySlotsError(CashShopPackets.ERROR_TOO_MANY_CASH_ITEMS));
            return;
        }

        if (p.getCashShop().getCash(currencyType) < cost) {
            p.dropMessage("点卷余额不足。");
            updateInformation(p.getClient(), null, false);
            //p.getClient().write(CashShopPackets.BuyInventorySlotsError(CashShopPackets.ERROR_INSUFFICIENT_CASH));
            return;
        }

        if (p.gainSlots(invType.getType(), 4, false)) {
            p.getCashShop().gainCash(currencyType, -600);
            p.getClient().write(CashShopPackets.UpdateInventorySlots(invType.getType(), p.getSlots(invType.getType())));
            updateInformation(p.getClient(), null, false);
        }
    }

    private static void CashInventorySlots(Player p, InPacket packet) {
        int currencyType = packet.readByte() + 1;
        Inventory inv = p.getInventory(InventoryType.CASH);
        byte currentSlots = (byte) p.getStorage().getSlots();
        if (currentSlots + 4 > 96) {
            p.dropMessage("背包空间已达最大值。");
            updateInformation(p.getClient(), null, false);
            return;
        }
        if (p.getCashShop().getCash(currencyType) < 600) {
            p.dropMessage("点卷余额不足。");
            updateInformation(p.getClient(), null, false);
            //p.getClient().write(CashShopPackets.BuyStorageSlotsError(CashShopPackets.ERROR_INSUFFICIENT_CASH));
            return;
        }
        p.getStorage().increaseSlots((byte) 4);
        p.getCashShop().gainCash(currencyType, -600);
        p.getClient().write(CashShopPackets.UpdateStorageSlots((short) p.getStorage().getSlots()));
        updateInformation(p.getClient(), null, false);
    }

    private static void BuyCharacterSlots(Player p, InPacket packet) {
        packet.readByte();
        int currencyType = packet.readInt();
        int currentSlots = p.getClient().getCharacterSlots();
        if (currentSlots + 1 > 6) {
            p.getClient().write(CashShopPackets.BuyCharacterSlotsError(CashShopPackets.ERROR_TOO_MANY_CASH_ITEMS));
            return;
        }
        if (p.getCashShop().getCash(currencyType) < 6900) {
            p.getClient().write(CashShopPackets.BuyCharacterSlotsError(CashShopPackets.ERROR_INSUFFICIENT_CASH));
            return;
        }
        if (p.getClient().gainCharacterSlot()) {
            p.getCashShop().gainCash(currencyType, -6900);
            p.getClient().write(CashShopPackets.UpdateCharacterSlots(p.getClient().getCharacterSlots()));
            updateInformation(p.getClient(), null, false);
        } else {
            p.getClient().write(CashShopPackets.BuyCharacterSlotsError(CashShopPackets.ERROR_TOO_MANY_CASH_ITEMS));
        }
    }

    private static void TakeFromCashInventory(Player p, InPacket packet) {
        //73 0A [02 00 00 00 00 00 00 00] [01] [07 00]
        long uniqueId = packet.readLong();
        packet.readByte();
        packet.readByte();
        packet.readByte();
        final Item cashItem = p.getCashShop().findByUniqueId((int) uniqueId);

        if (cashItem == null) {
            CheatingOffense.PACKET_EDIT.cheatingSuspicious(p, "尝试将不存在的现金库项目从分期转移");
            p.getClient().write(CashShopPackets.TakeError(CashShopPackets.ERROR_UNKNOWN));
            return;
        }
        if (cashItem.getQuantity() > 0 && !InventoryManipulator.checkSpace(p.getClient(), cashItem.getItemId(), cashItem.getQuantity(), cashItem.getOwner())) {
            p.getClient().write(CashShopPackets.TakeError(CashShopPackets.ERROR_UNKNOWN));
            return;
        }

        final Item item = cashItem.copy();
        short position = InventoryManipulator.addbyItem(p.getClient(), item, true);
        if (position < 0) {
            p.getClient().write(CashShopPackets.TakeError(CashShopPackets.ERROR_INVENTORY_FULL));
            return;
        }
        if (item.getPet() != null) {
            item.getPet().setInventoryPosition(position);
            item.getPet().setExpiration(item.getExpiration());
            p.addPet(item.getPet());
        }
        p.getCashShop().removeFromInventory(cashItem);
        p.getClient().write(CashShopPackets.TakeFromCashInventory(cashItem, position));
    }

    private static void PutIntoCashInventory(Player p, InPacket packet) {
        long uniqueId = packet.readLong();
        byte type = packet.readByte();
        Inventory mi = p.getInventory(InventoryType.getByType(type));
        Item item = mi.findByUniqueId((int) uniqueId);
        if (item == null) {
            p.getClient().write(CashShopPackets.PlaceError(CashShopPackets.ERROR_UNKNOWN));
            return;
        }
        if (p.getCashShop().getItemsSize() >= 100) {
            p.getClient().write(CashShopPackets.TakeError(CashShopPackets.ERROR_INVENTORY_FULL));
            return;
        }
        if (item.getUniqueId() < 0) {
            p.getClient().write(CashShopPackets.TakeError(CashShopPackets.ERROR_UNKNOWN));
            return;
        }
        if (item.getPet() != null) {
            p.removePetCS(item.getPet());
        }
        p.getCashShop().addToInventory(item);
        mi.removeSlot(item.getPosition());
        p.getClient().announce(CashShopPackets.PutIntoCashInventory(item, p.getClient().getAccountID()));
        p.getClient().announce(CashShopPackets.ShowCashInventory(p.getClient(), p.getCashShop().loadGifts(p.getClient())));
    }

    private static void BuyQuestItem(Player p, InPacket packet) {
        int serialNumber = packet.readInt();
        CashItem item = CashItemFactory.getItem(serialNumber);
        if (serialNumber / 10000000 != 8) {
            p.getClient().write(CashShopPackets.BuyError(CashShopPackets.ERROR_OUT_OF_STOCK));
            return;
        }
        if (item == null || !item.isOnSale()) {
            p.getClient().write(CashShopPackets.BuyError(CashShopPackets.ERROR_OUT_OF_STOCK));
            return;
        }
        if (p.getMeso() >= item.getPrice()) {
            if (ItemInformationProvider.getInstance().isQuestItem(item.getItemId())) {
                p.gainMeso(-item.getPrice(), false);
                InventoryManipulator.addById(p.getClient(), item.getItemId(), (short) item.getCount(), "通过现金商店获得！");
            } else {
                p.getClient().write(CashShopPackets.BuyError(CashShopPackets.ERROR_UNKNOWN));
            }
        } else {
            p.getClient().write(CashShopPackets.BuyError(CashShopPackets.ERROR_INSUFFICIENT_MESOS));
        }
    }

    private static void BuyPackage(Player p, InPacket packet) {
        packet.readByte();
        int currencyType = packet.readInt();
        int serialNumber = packet.readInt();
        CashItem cashItem = CashItemFactory.getItem(serialNumber);
        if (cashItem == null || !cashItem.isOnSale()) {
            p.getClient().write(CashShopPackets.BuyError(CashShopPackets.ERROR_OUT_OF_STOCK));
            return;
        }

        List<Item> serialNumbers = CashItemFactory.getPackage(cashItem.getItemId());
        if (serialNumbers == null) {
            p.getClient().write(CashShopPackets.BuyError(CashShopPackets.ERROR_OUT_OF_STOCK));
            return;
        }

        if (!p.getCashShop().canFit(serialNumbers.size())) {
            p.getClient().write(CashShopPackets.BuyError(CashShopPackets.ERROR_INVENTORY_FULL));
            return;
        }

        if (p.getCashShop().getCash(currencyType) < cashItem.getPrice()) {
            p.getClient().write(CashShopPackets.BuyError(CashShopPackets.ERROR_INSUFFICIENT_CASH));
            return;
        }
        Item item = cashItem.toItem(cashItem);
        List<Item> cashPackage = CashItemFactory.getPackage(cashItem.getItemId());
        cashPackage.stream().filter((c) -> !(c == null)).forEach((c) -> {
            p.getCashShop().addToInventory(c);
        });
        p.getCashShop().gainCash(currencyType, -cashItem.getPrice());
        updateInformation(p.getClient(), item, true);
        p.getClient().write(CashShopPackets.ShowCashInventory(p.getClient(), p.getCashShop().loadGifts(p.getClient())));
    }

    private static void GiftPackage(Player p, InPacket packet) {
        int enteredBirthday = packet.readInt();
        int serialNumber = packet.readInt();
        String recipient = packet.readMapleAsciiString();
        String message = packet.readMapleAsciiString();

        CashItem cashItem = CashItemFactory.getItem(serialNumber);
        if (!CashShopTools.checkBirthday(p.getClient(), enteredBirthday)) {
            p.getClient().write(CashShopPackets.GiftError(CashShopPackets.ERROR_BIRTHDAY));
            return;
        }

        if (cashItem == null || !cashItem.isOnSale()) {
            CheatingOffense.PACKET_EDIT.cheatingSuspicious(p, "试图从现钞店购买不存在的包裹.");
            p.getClient().write(CashShopPackets.BuyError(CashShopPackets.ERROR_OUT_OF_STOCK));
            return;
        }

        List<Item> serialNumbers = CashItemFactory.getPackage(cashItem.getItemId());
        if (serialNumbers == null) {
            p.getClient().write(CashShopPackets.BuyError(CashShopPackets.ERROR_OUT_OF_STOCK));
            return;
        }

        if (!p.getCashShop().canFit(serialNumbers.size())) {
            p.getClient().write(CashShopPackets.BuyError(CashShopPackets.ERROR_INVENTORY_FULL));
            return;
        }

        if (p.getCashShop().getCash(4) < cashItem.getPrice()) {
            CheatingOffense.PACKET_EDIT.cheatingSuspicious(p, "尝试从现金商店赠送礼品，现金不存在");
            p.getClient().write(CashShopPackets.BuyError(CashShopPackets.ERROR_INSUFFICIENT_CASH));
            return;
        }

        int recipientAcct = PlayerQuery.getIdByName(recipient);
        if (recipientAcct == -1) {
            p.getClient().write(CashShopPackets.GiftError(CashShopPackets.ERROR_RECIPIENT_NAME));
            return;
        }

        p.getCashShop().gift(recipientAcct, p.getName(), message, cashItem.getSN(), InventoryIdentifier.getInstance());
        p.getCashShop().gainCash(4, -cashItem.getPrice());
        p.getClient().write(CashShopPackets.GiftSent(recipient, cashItem.getItemId(), cashItem.getPrice(), cashItem.getCount(), false));
        updateInformation(p.getClient(), null, false);
    }

    private static void BuyFriendshipAndCoupleRing(Player p, InPacket r, boolean couple) {
        int enteredBirthday = r.readInt();
        int currencyType = r.readInt();
        int serialNumber = r.readInt();
        String recipient = r.readMapleAsciiString();
        String message = r.readMapleAsciiString();
        CashItem ring = CashItemFactory.getItem(serialNumber);
        if (!CashShopTools.checkBirthday(p.getClient(), enteredBirthday)) {
            p.getClient().write(CashShopPackets.GiftError(CashShopPackets.ERROR_BIRTHDAY));
            return;
        }
        if (ring == null || !ring.isOnSale()) {
            p.getClient().write(CashShopPackets.GiftError(CashShopPackets.ERROR_OUT_OF_STOCK));
            return;
        }
        if (p.getCashShop().isFull()) {
            p.getClient().write(CashShopPackets.GiftError(CashShopPackets.ERROR_INVENTORY_FULL));
            return;
        }
        if (p.getCashShop().getCash(currencyType) < ring.getPrice()) {
            CheatingOffense.PACKET_EDIT.cheatingSuspicious(p, "试图从现金商店购买友谊戒指，现金不存在。");
            p.getClient().write(CashShopPackets.GiftError(CashShopPackets.ERROR_INSUFFICIENT_CASH));
            return;
        }
        int recipientAcct = PlayerQuery.getIdByName(recipient);
        if (recipientAcct == -1) {
            p.getClient().write(CashShopPackets.GiftError(CashShopPackets.ERROR_RECIPIENT_NAME));
            return;
        }
        int gender = PlayerQuery.getGenderByName(recipient);
        if (p.getGender() == gender && GameConstants.GENDER_RESTRICT_RINGS && couple) {
            p.getClient().write(CashShopPackets.GiftError(CashShopPackets.ERROR_GENDER_RESTRICTIONS));
            return;
        }

        boolean creationRing = ItemRing.createRing(ring.getItemId(), p, recipientAcct, message, ring.getSN());
        if (!creationRing) {
            p.dropMessage(1, "你已经和这个人有戒指了。"); // TODO: GMS-LIKE ?
            updateInformation(p.getClient(), null, false);
            return;
        }
        p.getCashShop().gainCash(currencyType, -ring.getPrice());
        PlayerNote.sendNote(p, PlayerQuery.getNameById(recipientAcct), message, 0);
        Player partnerChar = p.getClient().getChannelServer().getPlayerStorage().getCharacterByName(recipient);
        if (partnerChar != null) {
            PlayerNote.showNote(partnerChar);
        }
        updateInformation(p.getClient(), null, false);
    }
}
