package cashshop;

import client.player.inventory.types.InventoryType;
import client.player.inventory.ItemFactory;
import client.Client;
import client.player.inventory.Item;
import database.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import launch.Start;
import packet.creators.CashShopPackets;
import server.itens.ItemInformationProvider;
import tools.FileLogger;
import tools.Pair;

public class CashShop {

    private int paypalNX;
    private int mPoints;
    private int cardNX;
    private final int accountId;
    private final int characterId;
    private boolean opened;
    private final ItemFactory factory = ItemFactory.CASHSHOP;
    private final List<Item> inventory = new ArrayList<>();
    private final List<Integer> wishList = new ArrayList<>();
    private final List<Integer> isPackage = new ArrayList<>();
    private final List<Integer> uniqueids = new ArrayList<>();
    private final ReentrantLock cashLock = new ReentrantLock();

    public CashShop(int accountId, int characterId) throws SQLException {
        this.accountId = accountId;
        this.characterId = characterId;

        Connection con = Start.getInstance().getConnection();
        try {
            try (PreparedStatement ps = con.prepareStatement("SELECT `paypalNX`, `mPoints`, `cardNX` FROM `accounts` WHERE `id` = ?")) {
                ps.setInt(1, accountId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        this.paypalNX = rs.getInt("paypalNX");
                        this.mPoints = rs.getInt("mPoints");
                        this.cardNX = rs.getInt("cardNX");
                    }
                }
            }

            for (Pair<Item, InventoryType> item : factory.loadItems(accountId, false)) {
                inventory.add(item.getLeft());
            }
        } finally {
            Database.cleanUP(null, null, con);
        }
    }

    public void gift(int recipient, String from, String message, int sn) {
        gift(recipient, from, message, sn, -1);
    }

    public void gift(int recipient, String from, String message, int sn, int uniqueid) {
        PreparedStatement ps = null;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            ps = con.prepareStatement("INSERT INTO `gifts` VALUES (DEFAULT, ?, ?, ?, ?, ?)");
            ps.setInt(1, recipient);
            ps.setString(2, from);
            ps.setString(3, message);
            ps.setInt(4, sn);
            ps.setInt(5, uniqueid);
            ps.executeUpdate();
        } catch (SQLException sqle) {
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public List<Pair<Item, String>> loadGifts(Client c) {
        List<Pair<Item, String>> gifts = new ArrayList<>();
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM `gifts` WHERE `to` = ?");
            ps.setInt(1, characterId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                CashItem cItem = CashItemFactory.getItem(rs.getInt("sn"));
                Item item = cItem.toItem(cItem, rs.getInt("uniqueid"), cItem.getCount(), rs.getString("from"));
                item.setGiftFrom(rs.getString("from"));
                uniqueids.add(item.getUniqueId());
                gifts.add(new Pair<>(item, rs.getString("message")));
                if (CashItemFactory.isPackage(cItem.getItemId())) {
                    for (Item packageItem : CashItemFactory.getPackage(cItem.getItemId())) {
                        if (packageItem != null) {
                            packageItem.setGiftFrom(rs.getString("from"));
                            addToInventory(packageItem);
                        } else {
                            FileLogger.printError("loadGifts", "CashPackage {" + cItem.getItemId() + "} contain null item.");
                            c.write(CashShopPackets.PlaceError(CashShopPackets.ERROR_UNKNOWN));
                        }
                    }
                    isPackage.add(item.getUniqueId());
                } else {
                    addToInventory(item);
                }
            }

            rs.close();
            ps.close();
            ps = con.prepareStatement("DELETE FROM `gifts` WHERE `to` = ?");
            ps.setInt(1, characterId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException sqle) {
            FileLogger.printError(FileLogger.DATABASE_EXCEPTION, sqle);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return gifts;
    }

    public boolean canSendNote(int uniqueid) {
        return uniqueids.contains(uniqueid);
    }

    public void sendedNote(int uniqueid) {
        for (int i = 0; i < uniqueids.size(); i++) {
            if (uniqueids.get(i) == uniqueid) {
                uniqueids.remove(i);
            }
        }
    }

    public boolean isPackage(int uniqueid) {
        return isPackage.contains(uniqueid);
    }

    public void removePackage(int uniqueid) {
        for (int i = 0; i < isPackage.size(); i++) {
            if (isPackage.get(i) == uniqueid) {
                isPackage.remove(i);
            }
        }
    }

    public int getCash(int type) {
        switch (type) {
            case 1:
                return paypalNX;
            case 2:
                return mPoints;
            case 4:
                return cardNX;
        }

        return 0;
    }

    public void gainCash(int type, int cash) {
        switch (type) {
            case 1:
                paypalNX += cash;
                break;
            case 2:
                mPoints += cash;
                break;
            case 4:
                cardNX += cash;
                break;
        }
    }

    public int getItemsSize() {
        return inventory.size();
    }

    public boolean isOpened() {
        return opened;
    }

    public void openedCashShop(boolean b) {
        opened = b;
    }

    public List<Item> getInventory() {
        cashLock.lock();
        try {
            return Collections.unmodifiableList(inventory);
        } finally {
            cashLock.unlock();
        }
    }

    public Item findByUniqueId(int cashId) {
        for (Item item : inventory) {
            if (item.getUniqueId() == cashId) {
                return item;
            }
        }
        return null;
    }

    public boolean isFull() {
        return inventory.size() >= 100;
    }

    public boolean canFit(int add) {
        return inventory.size() + add <= 100;
    }

    public void addToInventory(Item item) {
        cashLock.lock();
        try {
            inventory.add(item);
        } finally {
            cashLock.unlock();
        }
    }

    public void removeFromInventory(Item item) {
        cashLock.lock();
        try {
            inventory.remove(item);
        } finally {
            cashLock.unlock();
        }
    }

    public List<Integer> getWishList() {
        return wishList;
    }

    public void clearWishList() {
        wishList.clear();
    }

    public void addToWishList(int sn) {
        wishList.add(sn);
    }

    public void saveToDB() {
        PreparedStatement ps = null;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            ps = con.prepareStatement("UPDATE `accounts` SET `paypalNX` = ?, `mPoints` = ?, `cardNX` = ? WHERE `id` = ?");
            ps.setInt(1, paypalNX);
            ps.setInt(2, mPoints);
            ps.setInt(3, cardNX);
            ps.setInt(4, accountId);
            ps.executeUpdate();
            ps.close();
            List<Pair<Item, InventoryType>> itemsWithType = new ArrayList<>();
            cashLock.lock();
            try {
                inventory.forEach((item) -> {
                    itemsWithType.add(new Pair<>(item, ItemInformationProvider.getInstance().getInventoryType(item.getItemId())));
                });
            } finally {
                cashLock.unlock();
            }
            factory.saveItems(itemsWithType, accountId);
        } catch (SQLException ee) {
            System.out.println("[DB-CASH] Teve Rolling Back com a DB: " + ee);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }
}
