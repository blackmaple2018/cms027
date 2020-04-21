package server.shops;

import client.Client;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import client.player.inventory.types.InventoryType;
import client.player.inventory.Item;
import static configure.Gamemxd.物品操作间隔;
import security.violation.AutobanManager;
import constants.ItemConstants;
import database.Database;
import launch.Start;
import packet.creators.PacketCreator;
import server.itens.InventoryManipulator;
import server.itens.ItemInformationProvider;

/** <游戏商店>
 */
public class Shop {

    private static final Set<Integer> rechargeableItems = new LinkedHashSet<>();
    private final int id;
    private final int npcId;
    private final List<ShopItem> items;

    /**
     * <飞镖>
     */
    static {
        for (int itemId = 2070000; itemId <= 2070013; itemId++) {
            rechargeableItems.add(itemId);
        }
    }

    private Shop(int id, int npcId) {
        this.id = id;
        this.npcId = npcId;
        items = new LinkedList<>();
    }

    public void addItem(ShopItem item) {
        items.add(item);
    }

    public void sendShop(Client c) {
        c.getPlayer().setShop(this);
        c.write(PacketCreator.GetNPCShop(c, getNpcId(), items));
    }

    public void buy(Client c, int itemId, short quantity) {
        if (quantity < 0) {
            AutobanManager.getInstance().autoban(c, "尝试从NPC商店购买负数量");
            return;
        }
        ShopItem item = findById(itemId);
        if (item == null || item.getItemId() != itemId) {
            AutobanManager.getInstance().autoban(c, "试图从NPC商店购买不存在的物品");
            return;
        }

        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        if (item.getPrice() > 0 && c.getPlayer().getMeso() >= item.getPrice() * quantity) {
            if (InventoryManipulator.checkSpace(c, itemId, quantity, "")) {
                if (ii.isRechargable(itemId)) {
                    short rechquantity = ii.getSlotMax(c, item.getItemId());
                    InventoryManipulator.addById(c, itemId, rechquantity, "购买可再收费物品。", null, null);
                } else {
                    InventoryManipulator.addById(c, itemId, quantity, c.getPlayer().getName() + " bought " + quantity + " for " + item.getPrice() * quantity + " from shop " + id, null, null);
                }
                c.getPlayer().gainMeso(-(item.getPrice() * quantity), false);
                c.write(PacketCreator.ConfirmShopTransaction((byte) 0));
            } else {
                c.write(PacketCreator.ConfirmShopTransaction((byte) 3));
            }
        } else {
            c.write(PacketCreator.ConfirmShopTransaction((byte) 2));
        }
    }

    /**
     * <卖出道具>
     */
    public void sell(Client c, InventoryType type, short slot, short quantity) {
        if (quantity == 0xFFFF || quantity == 0) {
            quantity = 1;
        }
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        Item item = c.getPlayer().getInventory(type).getItem(slot);

        if (System.currentTimeMillis() - c.getPlayer().getItemCooldown() < 物品操作间隔) {
            c.getPlayer().dropMessage(1, "操作过快，请你慢点。");
            c.write(PacketCreator.EnableActions());
            return;
        }

        if (item == null || type == InventoryType.CASH) {
            AutobanManager.getInstance().autoban(c, "试图把不存在的东西卖给NPC商店");
            return;
        }
        if (ii.isThrowingStar(item.getItemId()) || ii.isBullet(item.getItemId())) {
            quantity = item.getQuantity();
        }
        if (quantity < 0) {
            return;
        }
        short iQuant = item.getQuantity();
        if (iQuant == 0xFFFF) {
            iQuant = 1;
        }
        if (quantity <= iQuant && iQuant > 0) {
            InventoryManipulator.removeFromSlot(c, type, slot, quantity, false);
            double price;
            if (ii.isThrowingStar(item.getItemId()) || ii.isBullet(item.getItemId())) {
                price = ii.getWholePrice(item.getItemId()) / (double) ii.getSlotMax(c, item.getItemId());
            } else {
                price = ii.getPrice(item.getItemId());
            }
            int recvMesos = (int) Math.max(Math.ceil(price * quantity), 0);
            if (price != -1 && recvMesos > 0) {
                c.getPlayer().gainMeso(recvMesos, false);

                String x = "每日任务_卖道具到游戏商店";
                if (c.getPlayer().getbosslog(x) <= 0) {
                    c.getPlayer().setbosslog(x);
                }
            }
            c.write(PacketCreator.ConfirmShopTransaction((byte) 0x8));
        }
        c.getPlayer().setItemCooldown(System.currentTimeMillis());
    }

    /**
     * <购买道具>
     */
    public void recharge(Client c, byte slot) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        Item item = c.getPlayer().getInventory(InventoryType.USE).getItem(slot);
        if (item == null || (!ItemConstants.isThrowingStar(item.getItemId()) && !ItemConstants.isBullet(item.getItemId()))) {
            AutobanManager.getInstance().autoban(c, "试图从NPC商店购买不存在的商品");
            return;
        }
        short slotMax = ii.getSlotMax(c, item.getItemId());

        if (item.getQuantity() < 0) {
            return;
        }
        if (System.currentTimeMillis() - c.getPlayer().getItemCooldown() < 物品操作间隔) {
            c.getPlayer().dropMessage(1, "操作过快，请你慢点。");
            c.write(PacketCreator.EnableActions());
            return;
        }
        if (item.getQuantity() < slotMax) {
            int price = (int) Math.round(ii.getPrice(item.getItemId()) * (slotMax - item.getQuantity()));
            if (c.getPlayer().getMeso() >= price) {
                item.setQuantity(slotMax);
                c.write(PacketCreator.UpdateInventorySlot(InventoryType.USE, (Item) item));
                c.getPlayer().gainMeso(-price, false, true, false);
                c.write(PacketCreator.ConfirmShopTransaction((byte) 0x0));
            } else {
                c.write(PacketCreator.ConfirmShopTransaction((byte) 0x2));
            }
        }
        c.getPlayer().setItemCooldown(System.currentTimeMillis());
    }

    protected ShopItem findById(int itemId) {
        for (ShopItem item : items) {
            if (item.getItemId() == itemId) {
                return item;
            }
        }
        return null;
    }

    /**
     * <打开商店>
     */
    public static Shop createFromDB(int id, boolean isShopId) {
        Shop ret = null;
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        int shopId;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(isShopId ? "SELECT * FROM shops WHERE shopid = ?" : "SELECT * FROM shops WHERE npcid = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                shopId = rs.getInt("shopid");
                ret = new Shop(shopId, rs.getInt("npcid"));
                rs.close();
                ps.close();
            } else {
                rs.close();
                ps.close();
                return null;
            }
            ps = con.prepareStatement("SELECT * FROM shopitems WHERE shopid = ? ORDER BY position ASC ");
            ps.setInt(1, shopId);
            rs = ps.executeQuery();
            List<Integer> recharges = new ArrayList<>(rechargeableItems);
            while (rs.next()) {
                if (!ii.itemExists(rs.getInt("itemid"))) {
                    continue;
                }
                if (ii.isThrowingStar(rs.getInt("itemid")) || ii.isBullet(rs.getInt("itemid"))) {
                    ShopItem starItem = new ShopItem((short) 1, rs.getInt("itemid"), rs.getInt("price"));
                    ret.addItem(starItem);
                    if (rechargeableItems.contains(starItem.getItemId())) {
                        recharges.remove(Integer.valueOf(starItem.getItemId()));
                    }
                } else {
                    ret.addItem(new ShopItem((short) 1000, rs.getInt("itemid"), rs.getInt("price")));

                }
            }
            for (Integer recharge : recharges) {
                ret.addItem(new ShopItem((short) 1000, recharge, 0));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {

            }
        }
        return ret;
    }

    public int getNpcId() {
        return npcId;
    }

    public int getId() {
        return id;
    }
}
