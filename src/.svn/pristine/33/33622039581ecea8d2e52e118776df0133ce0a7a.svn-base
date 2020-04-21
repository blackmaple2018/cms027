package zevms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import launch.Start;

public class extension {

    public static String 显示推广关系2(String a) {
        StringBuilder name = new StringBuilder();
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE `promoters` = ?")) {
                ps.setString(1, a);
                ResultSet rs = ps.executeQuery();
                int x = 0;
                while (rs.next()) {
                    x++;
                    String 推广QQ = rs.getString("qq");
                    int 推广金额 = rs.getInt("Promoterschongzhi");
                    int 推广总金额 = rs.getInt("Promoterschongzhilog");
                    name.append("\t  ").append(x).append("）. #bQQ#k:").append(推广QQ);
                    for (int j = 11 - 推广QQ.getBytes().length; j > 0; j--) {
                        name.append(" ");
                    }
                    name.append("#b现金券#k:");
                    name.append(推广金额);
                    for (int j = 6 - String.valueOf(推广金额).getBytes().length; j > 0; j--) {
                        name.append(" ");
                    }
                    name.append("#b汇总#k:");
                    name.append(推广总金额);
                    name.append("\r\n");
                }
            }
        } catch (SQLException ex) {
            System.err.println("显示推广关系、出错" + ex.getMessage());
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

    public static int 显示推广收益2(String a) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE promoters = ?")) {
                ps.setString(1, a);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    data += rs.getInt("Promoterschongzhi");
                }
                rs.close();
            }
        } catch (SQLException ex) {
            System.err.println("显示推广关系、出错" + ex.getMessage());
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

    public static int 收取所有推广收益2(String a) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET Promoterschongzhi = 0 WHERE promoters = ?")) {
                ps.setString(1, a);
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            System.err.println("显示推广关系、出错" + ex.getMessage());
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

    public static String 获取推广员2(int a) {
        String data = "";
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT promoters as DATA FROM accounts WHERE id = ? ");
            ps.setInt(1, a);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data = rs.getString("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取推广员、出错");
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

    public static int 获取推广收益2(int a) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT Promoterschongzhi as DATA FROM accounts WHERE id = ? ");
            ps.setInt(1, a);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取推广收益、出错");
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

    public static void 修改推广员2(String b, int a) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET promoters = ? WHERE id = ?")) {
                ps.setString(1, b);
                ps.setInt(2, a);
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public static void 修改推广收益2(int a, int b) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET Promoterschongzhi = Promoterschongzhi + ? WHERE id = ?")) {
                ps.setInt(1, a);
                ps.setInt(2, b);
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        if (a > 0) {
            修改推广收益log(a, b);
        }
    }

    public static void 修改推广收益log(int a, int b) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET Promoterschongzhilog = Promoterschongzhilog + ? WHERE id = ?")) {
                ps.setInt(1, a);
                ps.setInt(2, b);
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public static int 获取推广收益log2(int a) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT Promoterschongzhilog as DATA FROM accounts WHERE id = ? ");
            ps.setInt(1, a);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取推广收益、出错");
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

    public static void 修改推广推广员(String a, String b) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET promoters = ? WHERE qq = ?")) {
                ps.setString(1, a);
                ps.setString(2, b);
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public static int 重置自己的推广余额1(String a) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET Promoterschongzhi = 0 WHERE qq = ?")) {
                ps.setString(1, a);
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            System.err.println("重置自己的推广余额、出错" + ex.getMessage());
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

    public static int 重置自己的推广余额2(String a) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET Promoterschongzhilog = 0 WHERE qq = ?")) {
                ps.setString(1, a);
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            System.err.println("重置自己的推广余额、出错" + ex.getMessage());
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
