package console.MsgServer;

import static console.MsgServer.QQMsgServer.sendMsgToQQ;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import launch.Start;
import server.itens.ItemInformationProvider;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;

public class QQMsgServer_a {

    public static void 添加怪物爆物(String 怪物id, String 物品id, String 物品爆率, String 任务id, String token) {

        if (!怪物id.matches("[0-9]{1,7}") || !物品id.matches("[0-9]{1,7}") || !物品爆率.matches("[0-9]{1,7}") || !任务id.matches("[0-9]{1,7}")) {
            return;
        }

        MapleMonster mob = MapleLifeFactory.getMonster(Integer.parseInt(怪物id));
        ItemInformationProvider ii = ItemInformationProvider.getInstance();

        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
        } catch (Exception ex) {

            return;
        }
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO drop_data (dropperid,itemid,chance,questid) VALUES (?,?,?,?)");
            ps.setInt(1, Integer.parseInt(怪物id));
            ps.setInt(2, Integer.parseInt(物品id));
            ps.setInt(3, Integer.parseInt(物品爆率));
            ps.setInt(4, Integer.parseInt(任务id));
            ps.executeUpdate();
            sendMsgToQQ("添加 " + mob.getName() + " 以 万分之" + 物品爆率 + " 的概率掉落 " + ii.getName(Integer.parseInt(物品id)) + " 成功。", token);
            ps.close();
        } catch (SQLException ex) {

        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public static void 添加世界爆物(String 物品id, String 物品爆率, String 任务id, String token) {

        if (!物品id.matches("[0-9]{6,7}") || !物品爆率.matches("[0-9]") || !任务id.matches("[0-9]")) {
            return;
        }

        ItemInformationProvider ii = ItemInformationProvider.getInstance();

        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
        } catch (Exception ex) {

            return;
        }
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO drop_data_global (itemid,chance,questid) VALUES (?,?,?)");
            ps.setInt(1, Integer.parseInt(物品id));
            ps.setInt(2, Integer.parseInt(物品爆率));
            ps.setInt(3, Integer.parseInt(任务id));
            ps.executeUpdate();
            sendMsgToQQ("添加 全服怪物 以 万分之" + 物品爆率 + " 的概率掉落 " + ii.getName(Integer.parseInt(物品id)) + " 成功。", token);
            ps.close();
        } catch (SQLException ex) {

        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public static void 查看怪物爆物(String 怪物id, String token) {
        boolean result = 怪物id.matches("[0-9]+");
        if (!result) {
            sendMsgToQQ("请输入数字。", token);
            return;
        }

        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        MapleMonster mob = MapleLifeFactory.getMonster(Integer.parseInt(怪物id));
        StringBuilder name = new StringBuilder();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Start.getInstance().getConnection();
        } catch (Exception ex) {

        }
        try {
            ps = con.prepareStatement("SELECT * FROM drop_data WHERE dropperid = ? && itemid !=0");
            ps.setInt(1, Integer.parseInt(怪物id));
            rs = ps.executeQuery();
            name.append("怪物 ").append(mob.getStats().getName()).append(" 掉落详细\r\n");
            while (rs.next()) {
                int 物品 = rs.getInt("itemid");
                int 爆率 = rs.getInt("chance");
                name.append("").append(ii.getName(物品)).append("(").append(物品).append(") - ").append(爆率).append("\r\n");
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {

        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        sendMsgToQQ(name.toString(), token);
    }

    public static void 查看世界爆物(String token) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        StringBuilder name = new StringBuilder();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Start.getInstance().getConnection();
        } catch (Exception ex) {

        }
        try {
            ps = con.prepareStatement("SELECT * FROM drop_data_global");
            rs = ps.executeQuery();
            name.append("世界掉落详细\r\n");
            while (rs.next()) {
                int 物品 = rs.getInt("itemid");
                int 爆率 = rs.getInt("chance");
                name.append("").append(ii.getName(物品)).append("(").append(物品).append(") - ").append(爆率).append("\r\n");
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {

        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        sendMsgToQQ(name.toString(), token);
    }

    public static void 删除世界爆物(String 物品id, String token) {

        boolean result = 物品id.matches("[0-9]+");
        if (!result) {
            sendMsgToQQ("请输入数字。", token);
            return;
        }
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            ps = con.prepareStatement("delete from drop_data_global where itemid = ?");
            ps.setInt(1, Integer.parseInt(物品id));
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }

        sendMsgToQQ("删除世界爆物 " + ii.getName(Integer.parseInt(物品id)) + " 成功。", token);
    }

    public static void 删除怪物爆物(String 怪物id, String 物品id, String token) {

        boolean result = 怪物id.matches("[0-9]+");
        boolean result2 = 物品id.matches("[0-9]+");
        if (!result || !result2) {
            sendMsgToQQ("请输入数字。", token);
            return;
        }
        MapleMonster mob = MapleLifeFactory.getMonster(Integer.parseInt(怪物id));
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            ps = con.prepareStatement("delete from dropperid where dropperid =? && itemid = ?");
            ps.setInt(1, Integer.parseInt(怪物id));
            ps.setInt(2, Integer.parseInt(物品id));
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }

        sendMsgToQQ("删除 " + mob.getStats().getName() + " 爆物 " + ii.getName(Integer.parseInt(物品id)) + " 成功。", token);
    }
}
