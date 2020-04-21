package zevms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import launch.Start;

/**
 *
 * @author Administrator
 */
public class redenvelopes {

    public static String 显示所有红包() {
        清空抢完的红包();
        StringBuilder name = new StringBuilder();
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM redenvelopes WHERE a > 0;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                int 红包类型1 = rs.getInt("leixing1");
                String 显示类型1 = "";
                switch (红包类型1) {
                    case 0:
                        显示类型1 = "#r[现金券]#k";
                        break;
                    case 1:
                        显示类型1 = "#r[点  券]#k";
                        break;
                    case 2:
                        显示类型1 = "#r[金  币]#k";
                        break;
                    default:
                        break;
                }

                int 红包类型2 = rs.getInt("leixing2");
                String 显示类型2 = "";
                if (红包类型2 == 0) {
                    显示类型2 = "#r[手气]#k";
                } else if (红包类型2 == 1) {
                    显示类型2 = "#r[平分]#k";
                }

                int 数量 = rs.getInt("a");

                name.append("\t\t#L").append(rs.getInt("id")).append("#").append(显示类型1).append("").append(显示类型2).append("#r[").append(数量).append("]  #b").append(rs.getString("txet")).append("#k#l\r\n");

            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("显示红包、出错" + ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        name.append("\r\n");
        return name.toString();
    }

    public static void 清空抢完的红包() {
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            ps1 = con.prepareStatement("SELECT * FROM redenvelopes WHERE a = 0");
            rs = ps1.executeQuery();
            if (rs.next()) {
                String sqlstr = " Delete from redenvelopes where a = 0";
                ps1.executeUpdate(sqlstr);
            }
            rs.close();
            ps1.close();
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

    public static int 取红包数据(int id, String a) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT " + a + " as DATA FROM redenvelopes WHERE id = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("取红包数据、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return data;
    }
}
