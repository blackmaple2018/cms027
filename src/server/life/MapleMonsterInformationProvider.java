package server.life;

import constants.GameConstants;
import constants.ItemConstants;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import database.Database;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import launch.Start;
import server.itens.ItemInformationProvider;
import tools.Randomizer;

public class MapleMonsterInformationProvider {

    private static final MapleMonsterInformationProvider instance = new MapleMonsterInformationProvider();

    private final Map<Integer, List<MonsterDropEntry>> drops = new HashMap<>();
    private final List<MonsterGlobalDropEntry> globaldrops = new ArrayList<>();

    private final Map<Integer, List<Integer>> dropsChancePool = new HashMap<>();
    private final Set<Integer> hasNoMultiEquipDrops = new HashSet<>();
    private final Map<Integer, List<MonsterDropEntry>> extraMultiEquipDrops = new HashMap<>();

    protected MapleMonsterInformationProvider() {
        retrieveGlobal();
    }

    public static MapleMonsterInformationProvider getInstance() {
        return instance;
    }

    public final List<MonsterGlobalDropEntry> getGlobalDrop() {
        return globaldrops;
    }

    private void retrieveGlobal() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            ps = con.prepareStatement("SELECT * FROM drop_data_global WHERE chance > 0");
            rs = ps.executeQuery();

            while (rs.next()) {
                globaldrops.add(
                        new MonsterGlobalDropEntry(
                                rs.getInt("itemid"),
                                rs.getInt("chance"),
                                rs.getInt("continent"),
                                rs.getByte("dropType"),
                                rs.getInt("minimum_quantity"),
                                rs.getInt("maximum_quantity"),
                                rs.getShort("questid")));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error retrieving drop" + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ignore) {
            }
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public List<MonsterDropEntry> retrieveEffectiveDrop(final int monsterId) {
        List<MonsterDropEntry> list = retrieveDrop(monsterId);
        if (hasNoMultiEquipDrops.contains(monsterId) || !GameConstants.USE_MULTIPLE_SAME_EQUIP_DROP) {
            return list;
        }

        List<MonsterDropEntry> multiDrops = extraMultiEquipDrops.get(monsterId), extra = new LinkedList<>();
        if (multiDrops == null) {
            multiDrops = new LinkedList<>();

            for (MonsterDropEntry mde : list) {
                if (ItemConstants.isEquip(mde.itemId) && mde.Maximum > 1) {
                    multiDrops.add(mde);

                    int rnd = Randomizer.rand(mde.Minimum, mde.Maximum);
                    for (int i = 0; i < rnd - 1; i++) {
                        extra.add(mde);
                    }
                }
            }
            if (!multiDrops.isEmpty()) {
                extraMultiEquipDrops.put(monsterId, multiDrops);
            } else {
                hasNoMultiEquipDrops.add(monsterId);
            }

        } else {
            for (MonsterDropEntry mde : multiDrops) {
                int rnd = Randomizer.rand(mde.Minimum, mde.Maximum);
                for (int i = 0; i < rnd - 1; i++) {
                    extra.add(mde);
                }
            }
        }

        List<MonsterDropEntry> ret = new LinkedList<>(list);
        ret.addAll(extra);

        return ret;
    }

    public final List<MonsterDropEntry> retrieveDrop(final int monsterId) {
        if (drops.containsKey(monsterId)) {
            return drops.get(monsterId);
        }
        final List<MonsterDropEntry> ret = new LinkedList<>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            ps = con.prepareStatement("SELECT itemid, chance, minimum_quantity, maximum_quantity, questid FROM drop_data WHERE dropperid = ?");
            ps.setInt(1, monsterId);
            rs = ps.executeQuery();

            while (rs.next()) {
                int 爆率 = rs.getInt("chance");
                int 物品 = rs.getInt("itemid");
                if (物品 >= 1000000 && 物品 < 2000000) {
                    爆率 /= 4;
                }
                if (物品 >= 2070005 && 物品 <= 2070007) {
                    爆率 /= 10;
                }
                if (物品 / 10000 == 204) {
                    爆率 /= 10;
                }
                ret.add(new MonsterDropEntry(物品, 爆率, rs.getInt("minimum_quantity"), rs.getInt("maximum_quantity"), rs.getShort("questid"), monsterId));
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return ret;
        } finally {
            try {
                if (ps != null && !ps.isClosed()) {
                    ps.close();
                }
                if (rs != null && !rs.isClosed()) {
                    rs.close();
                }
            } catch (SQLException ignore) {
                ignore.printStackTrace();
                return ret;
            }
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {

            }
        }
        drops.put(monsterId, ret);
        return ret;
    }

    public final List<Integer> retrieveDropPool(final int monsterId) {
        if (dropsChancePool.containsKey(monsterId)) {
            return dropsChancePool.get(monsterId);
        }

        ItemInformationProvider ii = ItemInformationProvider.getInstance();

        List<MonsterDropEntry> dropList = retrieveDrop(monsterId);
        List<Integer> ret = new ArrayList<>();

        int accProp = 0;
        for (MonsterDropEntry mde : dropList) {
            if (!ii.isQuestItem(mde.itemId) && !ii.isPartyQuestItem(mde.itemId)) {
                accProp += mde.chance;
            }

            ret.add(accProp);
        }

        if (accProp == 0) {
            ret.clear();
        }

        dropsChancePool.put(monsterId, ret);
        return ret;
    }

    public final void clearDrops() {
        drops.clear();
        hasNoMultiEquipDrops.clear();
        extraMultiEquipDrops.clear();
        dropsChancePool.clear();
        globaldrops.clear();
        retrieveGlobal();
    }
}
