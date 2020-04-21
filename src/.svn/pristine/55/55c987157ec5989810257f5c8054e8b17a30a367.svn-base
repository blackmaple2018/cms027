package gui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import launch.Start;

/**
 *
 * @author Administrator
 */
public class MySQL {

    public static int 角色ID取大区机器人管理员区域(String id) {
        int data = 0;
        try (Connection con = Start.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT id as DATA FROM mxmxd_qq_qun_gm WHERE qq = ?")) {
                ps.setString(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        data = rs.getInt("DATA");
                    }
                    rs.close();
                }
                ps.close();
            }
        } catch (SQLException Ex) {
            System.err.println("角色ID取角色名、出错");
        }
        return data;
    }

    public static int 大区机器人管理员(int b, String a) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM mxmxd_qq_qun_gm WHERE qq = ? && id = ?");
            ps.setString(1, a);
            ps.setInt(1, b);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                data += 1;
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
        return data;
    }

    public static String 取绑定手机(String a) {
        String data = "";
        try (Connection con = Start.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM mxmxd_qq_sj WHERE qq = '" + a + "'")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    data = rs.getString("sj");
                }
                ps.close();
                rs.close();
            }
        } catch (SQLException ex) {
        }
        return data;
    }

    public static String 角色ID取角色名(int id) {
        String data = "";
        try (Connection con = Start.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT name as DATA FROM characters WHERE id = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        data = rs.getString("DATA");
                    }
                    rs.close();
                }
                ps.close();
            }
        } catch (SQLException Ex) {
            System.err.println("角色ID取角色名、出错");
        }
        return data;
    }

    public static String 账号ID取账号(int id) {
        String data = "";
        try (Connection con = Start.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT name as DATA FROM accounts WHERE id = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        data = rs.getString("DATA");
                    }
                    rs.close();
                }
                ps.close();
            }
        } catch (SQLException Ex) {
            System.err.println("账号ID取账号、出错");
        }
        return data;
    }

    public static int 角色ID取账号ID(int id) {
        int data = 0;
        try (Connection con = Start.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT accountid as DATA FROM characters WHERE id = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        data = rs.getInt("DATA");
                    }
                    rs.close();
                }
                ps.close();
            }
        } catch (SQLException Ex) {
            System.err.println("角色名字取账号ID、出错");
        }
        return data;
    }

    public static int 已经使用技能点(int id) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM skills WHERE characterid = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data += rs.getInt("skilllevel");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("已经使用技能点、出错");
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

    public static String 角色名字(int id) {
        String data = "";
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT name as DATA FROM characters WHERE id = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getString("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("角色名字、出错");
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

    public static int 角色取会员经验(int id) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT vip as DATA FROM characters WHERE id = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("角色取会员等级、出错");
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

    public static int 角色取会员时间(int id) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT viptime as DATA FROM characters WHERE id = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("角色取会员时间、出错");
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

    public static int 角色取区域(int id) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT world as DATA FROM characters WHERE id = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("角色取区域、出错");
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

    public static String 账号ID取绑定QQ(int id) {
        String data = "";
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT qq as DATA FROM accounts WHERE id = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getString("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("账号ID取绑定QQ、出错");
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

    public static int 账号取账号ID(String id) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT id as DATA FROM accounts WHERE name = ?");
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("账号ID取绑定QQ、出错");
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

    public static int 账号ID取账号现金券(int id) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT xianjin as DATA FROM accounts_xj WHERE id = ? ");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("账号ID取账号现金券、出错");
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

    public static String 账号ID取绑定SJ(int id) {
        String data = "";
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT sj as DATA FROM accounts WHERE id = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getString("DATA");
                }
                rs.close();
            }
            ps.close();

        } catch (SQLException Ex) {
            System.err.println("账号ID取绑定SJ、出错");
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

    public static int 账号注册数量(String a) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE qq = " + a + "");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("qq") == null ? a == null : rs.getString("qq").equals(a)) {
                    data += 1;
                }
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
        return data;
    }

    public static int 角色数量(int a, int b) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE accountid = " + a + " && world = " + b + " ");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                data += 1;

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
        return data;
    }

    public static int 查询QQ下是否有封禁账号(String a) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE qq = " + a + " && banned > 0")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    if (rs.getString("qq") == null ? a == null : rs.getString("qq").equals(a)) {
                        data += 1;
                    }
                }
                rs.close();
                ps.close();
            }
        } catch (SQLException ex) {
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

    public static String 取账号绑定的QQ(String id) {
        String data = "";
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT qq as DATA FROM accounts WHERE name = ?")) {
                ps.setString(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        data = rs.getString("DATA");
                    }
                    rs.close();
                }
                ps.close();
            }
        } catch (SQLException Ex) {
            System.err.println("取账号绑定的QQ、出错");
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

    public static String 取账号绑定的QQ(int id) {
        String data = "";
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT qq as DATA FROM accounts WHERE id = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        data = rs.getString("DATA");
                    }
                    rs.close();
                }
                ps.close();
            }
        } catch (SQLException Ex) {
            System.err.println("取账号绑定的QQ、出错");
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

    public static String 取账号绑定的SJ(int id) {
        String data = "";
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT sj as DATA FROM mxmxd_qq_sj WHERE qq = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        data = rs.getString("DATA");
                    }
                    rs.close();
                }
                ps.close();
            }
        } catch (SQLException Ex) {
            System.err.println("取账号绑定的SJ、出错");
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

    public static int 取账号是否可以登录(String id) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT lv as DATA FROM accounts WHERE name = ?")) {
                ps.setString(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        data = rs.getInt("DATA");
                    }
                    rs.close();
                }
                ps.close();
            }
        } catch (SQLException Ex) {
            System.err.println("取账号是否可以登录、出错");
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return data;
    }

    public static int 账号状态(String id) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE qq = ? AND banned > 0")) {
                ps.setString(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        data++;
                    }
                    rs.close();
                }
                ps.close();
            }
        } catch (SQLException Ex) {
            Ex.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return data;
    }

    public static int 账号状态2(String id) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM mxmxd_qq_sj WHERE sj = ? AND banned > 0")) {
                ps.setString(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        data++;
                    }
                    rs.close();
                }
                ps.close();
            }
        } catch (SQLException Ex) {
            Ex.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return data;
    }

    public static int 取账号是否可以登录(int id) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT dl as DATA FROM accounts WHERE id = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        data = rs.getInt("DATA");
                    }
                    rs.close();
                }
                ps.close();
            }

        } catch (SQLException Ex) {
            System.err.println("取账号是否可以登录、出错");
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

    public static int 角色今日在线(int id) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT todayOnlineTime as DATA FROM characters WHERE id = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("角色今日在线、出错");
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

    public static int 角色总在线(int id) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT totalOnlineTime as DATA FROM characters WHERE id = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("角色总在线、出错");
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
