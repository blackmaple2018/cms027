package client.player.inventory;

import client.player.inventory.types.InventoryType;
import constants.ItemConstants;
import database.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import launch.Start;
import server.itens.ItemInformationProvider;
import tools.Pair;

public enum ItemFactory {

    INVENTORY(1, false),
    STORAGE(2, true),
    CASHSHOP(3, true),
    MERCHANT(4, false);

    private final int value;
    private final boolean account;

    private ItemFactory(int value, boolean account) {
        this.value = value;
        this.account = account;
    }

    public int getValue() {
        return value;
    }

    public List<Pair<Item, InventoryType>> loadItems(int id, boolean login) throws SQLException {
        List<Pair<Item, InventoryType>> items = new ArrayList<>();
        Connection con = Start.getInstance().getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(String.format("SELECT * FROM `inventoryitems` WHERE `type` = ? AND `%s` = ?", account ? "accountid" : "characterid"));
            ps.setInt(1, value);
            ps.setInt(2, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                final ItemInformationProvider ii = ItemInformationProvider.getInstance();
                if (!ii.itemExists(rs.getInt("itemid"))) {
                    continue;
                }
                InventoryType mit = InventoryType.getByType(rs.getByte("inventorytype"));
                if (mit.getType() > 1) {
                    Item item = new Item(rs.getInt("itemid"), (short) rs.getInt("position"), (short) rs.getInt("quantity"), rs.getInt("uniqueid"));
                    item.setOwner(rs.getString("owner"));
                    item.setGiftFrom(rs.getString("giftFrom"));
                    item.setExpiration(rs.getLong("expiration"));
                    if (ItemConstants.isPet(item.getItemId())) {
                        if (item.getUniqueId() > -1) {
                            ItemPet pet = ItemPet.loadDatabase(item.getItemId(), item.getUniqueId(), item.getPosition());
                            if (pet != null) {
                                item.setPet(pet);
                                item.getPet().setExpiration(item.getExpiration());
                            }
                        } else {
                            final int new_unique = InventoryIdentifier.getInstance();
                            item.setUniqueId(new_unique);
                            item.setPet(ItemPet.createPet(item.getItemId(), new_unique));
                        }
                    }
                    items.add(new Pair<>(item.copy(), mit));
                }
            }
            rs.close();
            ps.close();

            ps = con.prepareStatement(String.format("SELECT * FROM `inventoryequipment` WHERE `type` = ? AND `%s` = ?", account ? "accountid" : "characterid"));
            ps.setInt(1, value);
            ps.setInt(2, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                final ItemInformationProvider ii = ItemInformationProvider.getInstance();
                final InventoryType mit = InventoryType.getByType(rs.getByte("inventorytype"));
                if (!ii.itemExists(rs.getInt("itemid")) || (!mit.equals(InventoryType.EQUIP) && !mit.equals(InventoryType.EQUIPPED))) {
                    continue;
                }
                Equip equip = new Equip(rs.getInt("itemid"), (short) rs.getInt("position"), rs.getInt("uniqueid"));
                equip.setOwner(rs.getString("owner"));
                equip.setQuantity((short) 1);
                equip.setAcc(rs.getShort("acc"));
                equip.setAvoid(rs.getShort("avoid"));
                equip.setDex(rs.getShort("dex"));
                equip.setHands(rs.getShort("hands"));
                equip.setHp(rs.getShort("hp"));
                equip.setInt(rs.getShort("inte"));
                equip.setJump(rs.getShort("jump"));
                equip.setLuk(rs.getShort("luk"));
                equip.setMatk(rs.getShort("matk"));
                equip.setMdef(rs.getShort("mdef"));
                equip.setMp(rs.getShort("mp"));
                equip.setSpeed(rs.getShort("speed"));
                equip.setStr(rs.getShort("str"));
                equip.setWatk(rs.getShort("watk"));
                equip.setWdef(rs.getShort("wdef"));
                equip.setUpgradeSlots(rs.getByte("upgradeslots"));
                equip.setLocked(rs.getByte("locked"));
                equip.setLevel(rs.getByte("level"));
                equip.setDzlevel(rs.getInt("dzlevel"));
                equip.setGiftFrom(rs.getString("giftFrom"));
                equip.setExpiration(rs.getLong("expiration"));
                equip.setOptionValues(rs.getString("options"));
                equip.setOption(new EquipOption(equip));
                equip.setSocket(rs.getByte("socket"));
                equip.getEquipOption().rebuildEquipOptions();

                if (equip.getUniqueId() > -1) {
                    if (ItemConstants.isEffectRing(equip.getItemId())) {
                        ItemRing ring = ItemRing.loadingRing(equip.getUniqueId());
                        if (ring != null) {
                            equip.setRing(ring);
                        }
                    }
                }
                items.add(new Pair<>(equip.copy(), mit));
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        } finally {
            Database.cleanUP(rs, ps, con);
        }
        return items;
    }

    public void saveItems(List<Pair<Item, InventoryType>> items, int id) throws SQLException {
        Connection con = Start.getInstance().getConnection();
        PreparedStatement ps = null;
        PreparedStatement eq_ps = null;
        try {
            ps = con.prepareStatement(String.format("DELETE FROM `inventoryitems` WHERE `type` = ? AND `%s` = ?", account ? "accountid" : "characterid"));
            ps.setInt(1, value);
            ps.setInt(2, id);
            ps.executeUpdate();
            ps.close();

            ps = con.prepareStatement(String.format("DELETE FROM `inventoryequipment` WHERE `type` = ? AND `%s` = ?", account ? "accountid" : "characterid"));
            ps.setInt(1, value);
            ps.setInt(2, id);
            ps.executeUpdate();
            ps.close();

            ps = con.prepareStatement("INSERT INTO `inventoryitems` (`type`, `characterid`, `accountid`, `itemid`, `inventorytype`, `position`, `quantity`, `owner`, `uniqueid`, `giftfrom`, `expiration`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            eq_ps = con.prepareStatement("INSERT INTO `inventoryequipment` "
                    + "(`type`, `characterid`, `accountid`, `itemid`, `position`, `owner`, `uniqueid`, `giftfrom`, `expiration`, `inventorytype`, `upgradeslots`, "
                    + "`level`, `str`, `dex`, `inte`, `luk`, `hp`, `mp`, `watk`, `matk`, `wdef`, `mdef`, `acc`, `avoid`, `hands`, `speed`, `jump`, "
                    + "`locked`, `options`, `socket`, `dzlevel`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            for (Pair<Item, InventoryType> pair : items) {
                Item item = pair.getLeft();
                if (item.disappearsAtLogout()) {
                    continue;
                }
                InventoryType mit = pair.getRight();
                if (mit.getType() > 1) {
                    ps.setInt(1, value);
                    ps.setString(2, account ? null : String.valueOf(id));
                    ps.setString(3, account ? String.valueOf(id) : null);
                    ps.setInt(4, item.getItemId());
                    ps.setInt(5, mit.getType());
                    ps.setInt(6, item.getPosition());
                    ps.setInt(7, item.getQuantity());
                    ps.setString(8, item.getOwner());
                    ps.setInt(9, item.getUniqueId());
                    ps.setString(10, item.getGiftFrom());
                    ps.setLong(11, item.getExpiration());
                    ps.addBatch();
                } else {
                    int i = 0;
                    Equip equip = (Equip) item;
                    eq_ps.setByte(++i, (byte) value);
                    eq_ps.setObject(++i, account ? null : String.valueOf(id));
                    eq_ps.setObject(++i, account ? String.valueOf(id) : null);
                    eq_ps.setInt(++i, equip.getItemId());
                    eq_ps.setShort(++i, equip.getPosition());
                    eq_ps.setString(++i, equip.getOwner());
                    eq_ps.setInt(++i, equip.getUniqueId());
                    eq_ps.setString(++i, equip.getGiftFrom());
                    eq_ps.setLong(++i, equip.getExpiration());
                    eq_ps.setByte(++i, mit.getType());
                    eq_ps.setByte(++i, equip.getUpgradeSlots());
                    eq_ps.setByte(++i, equip.getLevel());
                    eq_ps.setShort(++i, equip.getStr());
                    eq_ps.setShort(++i, equip.getDex());
                    eq_ps.setShort(++i, equip.getInt());
                    eq_ps.setShort(++i, equip.getLuk());
                    eq_ps.setShort(++i, equip.getHp());
                    eq_ps.setShort(++i, equip.getMp());
                    eq_ps.setShort(++i, equip.getWatk());
                    eq_ps.setShort(++i, equip.getMatk());
                    eq_ps.setShort(++i, equip.getWdef());
                    eq_ps.setShort(++i, equip.getMdef());
                    eq_ps.setShort(++i, equip.getAcc());
                    eq_ps.setShort(++i, equip.getAvoid());
                    eq_ps.setShort(++i, equip.getHands());
                    eq_ps.setShort(++i, equip.getSpeed());
                    eq_ps.setShort(++i, equip.getJump());
                    eq_ps.setByte(++i, equip.getLocked());
                    eq_ps.setString(++i, equip.getOptionValues());
                    eq_ps.setByte(++i, equip.getSocket());
                    eq_ps.setInt(++i, equip.getDzlevel());
                    eq_ps.addBatch();
                }
            }
            ps.executeBatch();
            ps.close();
            eq_ps.executeBatch();
            eq_ps.close();
        } catch (SQLException | RuntimeException ex) {
            ex.printStackTrace(System.err);
        } finally {
            Database.cleanUP(null, ps, con);
            Database.cleanUP(null, eq_ps, null);
        }
    }
}
