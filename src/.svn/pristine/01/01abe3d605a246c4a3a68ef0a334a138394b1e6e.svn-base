package zevms;

import static gui.logo.group.群;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import launch.Start;

public class stock {

    public static int 群说话 = 0;
    public static int 游戏说话 = 0;

    public static void 股票浮动() {
        /**
         * <绿蜗牛股票>
         */
        int 绿蜗牛股票值 = 1000;
        int 绿蜗牛增涨幅度 = 20;
        int 绿蜗牛跌落幅度 = 10;
        if (判断股票数值(1) <= 绿蜗牛股票值) {
            绿蜗牛增涨幅度 = 30;
            绿蜗牛跌落幅度 = 10;
        } else if (判断股票数值(1) >= 绿蜗牛股票值 * 3) {
            绿蜗牛增涨幅度 = 50;
            绿蜗牛跌落幅度 = 45;
        } else if (判断股票数值(1) >= 绿蜗牛股票值 * 2) {
            绿蜗牛增涨幅度 = 30;
            绿蜗牛跌落幅度 = 25;
        } else if (判断股票数值(1) >= 绿蜗牛股票值 * 1.5) {
            绿蜗牛增涨幅度 = 30;
            绿蜗牛跌落幅度 = 20;
        }

        if (群说话 >= 200) {
            绿蜗牛增涨幅度 += 5;
        } else if (群说话 >= 100) {
            绿蜗牛增涨幅度 += 3;
        }

        if (群说话 >= 200) {
            绿蜗牛增涨幅度 += 20;
        } else if (群说话 >= 100) {
            绿蜗牛增涨幅度 += 3;
        }
        随机重置股票数据(1, (int) Math.ceil(Math.random() * 绿蜗牛增涨幅度) - 绿蜗牛跌落幅度);

        /**
         * <蓝蜗牛股票>
         */
        int 蓝蜗牛股票值 = 2000;
        int 蓝蜗牛增涨幅度 = 100;
        int 蓝蜗牛跌落幅度 = 50;
        if (判断股票数值(2) <= 蓝蜗牛股票值) {
            蓝蜗牛增涨幅度 = 100;
            蓝蜗牛跌落幅度 = 30;
        } else if (判断股票数值(2) >= 蓝蜗牛股票值 * 3) {
            蓝蜗牛增涨幅度 = 100;
            蓝蜗牛跌落幅度 = 80;
        } else if (判断股票数值(2) >= 蓝蜗牛股票值 * 2) {
            蓝蜗牛增涨幅度 = 100;
            蓝蜗牛跌落幅度 = 70;
        } else if (判断股票数值(2) >= 蓝蜗牛股票值 * 1.5) {
            蓝蜗牛增涨幅度 = 100;
            蓝蜗牛跌落幅度 = 60;
        }

        if (群说话 >= 200) {
            蓝蜗牛增涨幅度 += 20;
        } else if (群说话 >= 100) {
            蓝蜗牛增涨幅度 += 10;
        }

        if (群说话 >= 200) {
            蓝蜗牛增涨幅度 += 20;
        } else if (群说话 >= 100) {
            蓝蜗牛增涨幅度 += 10;
        }

        随机重置股票数据(2, (int) Math.ceil(Math.random() * 蓝蜗牛增涨幅度) - 蓝蜗牛跌落幅度);

        /**
         * <红蜗牛股票>
         */
        int 红蜗牛股票值 = 2000;
        int 红蜗牛增涨幅度 = 200;
        int 红蜗牛跌落幅度 = 100;
        if (判断股票数值(3) <= 红蜗牛股票值) {
            红蜗牛增涨幅度 = 200;
            红蜗牛跌落幅度 = 50;
        } else if (判断股票数值(3) >= 红蜗牛股票值 * 3) {
            红蜗牛增涨幅度 = 200;
            红蜗牛跌落幅度 = 160;
        } else if (判断股票数值(3) >= 红蜗牛股票值 * 2) {
            红蜗牛增涨幅度 = 200;
            红蜗牛跌落幅度 = 140;
        } else if (判断股票数值(3) >= 红蜗牛股票值 * 1.5) {
            红蜗牛增涨幅度 = 200;
            红蜗牛跌落幅度 = 120;
        }

        if (群说话 >= 200) {
            红蜗牛增涨幅度 += 15;
        } else if (群说话 >= 100) {
            红蜗牛增涨幅度 += 5;
        }

        if (群说话 >= 200) {
            红蜗牛增涨幅度 += 15;
        } else if (群说话 >= 100) {
            红蜗牛增涨幅度 += 5;
        }
        随机重置股票数据(3, (int) Math.ceil(Math.random() * 红蜗牛增涨幅度) - 红蜗牛跌落幅度);

        群说话 = 0;
        游戏说话 = 0;
    }

    public static void 随机重置股票数据(int a, int b) {
        if (股票记录累计() > 100 * 3) {
            清空股票记录();
        }
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE stock SET c = c + ? WHERE id = ?")) {
                ps.setInt(1, b);
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

        if (判断股票数值(a) < 0) {
            清空股票数值(a);
            String txet = "[股票公告]:股票（";
            switch (a) {
                case 1:
                    txet += "绿蜗牛";
                    break;
                case 2:
                    txet += "蓝蜗牛";
                    break;
                case 3:
                    txet += "红蜗牛";
                    break;
                default:
                    break;
            }
            txet += "）已经跌停，重新开盘。";
            if (a == 1) {
                重置股票数据(a, 1000);
            } else if (a == 2 || a == 3) {
                重置股票数据(a, 2000);
            }

            //群(txet, 蓝蜗牛群);
            //群(txet, 蘑菇仔群);
        } else {
            String txet = "[股票公告]:股票（";
            switch (a) {
                case 1:
                    txet += "绿蜗牛";
                    break;
                case 2:
                    txet += "蓝蜗牛";
                    break;
                case 3:
                    txet += "红蜗牛";
                    break;
                default:
                    break;
            }
            txet += "）数值波动变更为 " + 判断股票数值(a) + " 每股。";
            记录股票波动(a, 判断股票数值(a));
            //群(txet, 蓝蜗牛群);
            //群(txet, 蘑菇仔群);
        }
    }

    public static String 查看股票波动记录(int a) {
        StringBuilder name = new StringBuilder();
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM stocktxet WHERE a = ? order by shijian desc");
            ps.setInt(1, a);
            ResultSet rs = ps.executeQuery();
            int x = 0;
            while (rs.next()) {
                x++;
                name.append("").append(x).append(").").append(rs.getString("shijian")).append("     #b").append(rs.getInt("b")).append("#k金币/股\r\n");
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("查看股票波动记录、出错" + ex.getMessage());
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

    public static void 重置股票数据(int a, int b) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE stock SET c = c + ? WHERE id = ?")) {
                ps.setInt(1, b);
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

    public static int 判断股票数值(int id) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT c as DATA FROM stock WHERE id = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("判断股票数值、出错");
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

    public static int 判断股票剩余数量(int id) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT d as DATA FROM stock WHERE id = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("判断股票剩余数量、出错");
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

    public static void 清空股票数值(int a) {
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            ps1 = con.prepareStatement("SELECT * FROM stocklog");
            rs = ps1.executeQuery();
            if (rs.next()) {
                String sqlstr = " Delete from stocklog where id = ?";
                ps1.setInt(1, a);
                ps1.executeUpdate(sqlstr);
            }
            rs.close();
            ps1.close();
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

    public static void 记录股票波动(int a, int b) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO stocktxet (a,b) VALUES (?,?)");
            ps.setInt(1, a);
            ps.setInt(2, b);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            System.out.println("记录股票波动" + ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public static int 获取股票数量(int a, int b) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT shuliang as DATA FROM stocklog WHERE accountid = ? && id = ?");
            ps.setInt(1, a);
            ps.setInt(2, b);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取股票数量、出错");
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

    public static void 修改股票剩余数量(int a, int b) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE stock SET d = d + ? WHERE id = ?")) {
                ps.setInt(1, b);
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

    public static int 股票记录累计() {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM stocklog");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data++;
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("股票记录累计、出错");
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

    public static void 清空股票记录() {
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            ps1 = con.prepareStatement("SELECT * FROM stocklog ");
            rs = ps1.executeQuery();
            if (rs.next()) {
                String sqlstr = " Delete from stocklog";
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

}
