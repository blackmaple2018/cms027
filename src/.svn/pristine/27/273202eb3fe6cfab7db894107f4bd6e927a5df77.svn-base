/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zevms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import launch.Start;
import server.itens.ItemInformationProvider;

/**
 *
 * @author Administrator
 */
public class warehouse {

    public static String getItemName(int id) {//物品名字
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        return ii.getName(id);
    }

    public static String 仓库物品(int b, int a) {
        StringBuilder name = new StringBuilder();
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM warehouse WHERE accountid = ? && world = ?");
            ps.setInt(1, a);
            ps.setInt(2, b);
            ResultSet rs = ps.executeQuery();
            int x = 0;
            while (rs.next()) {
                int 序号 = rs.getInt("id");
                int 物品 = rs.getInt("itemid");
                int 数量 = rs.getInt("cout");
                long 保管费用 = ((System.currentTimeMillis() - rs.getLong("time")) / 1000 / 60) * 2;
                if (保管费用 > 1000) {
                    保管费用 = 1000;
                }
                x++;
                String 物品名字 = getItemName(物品);
                String 物品数量 = "" + 数量 + "";
                //name.append("#L").append(序号).append("# ").append(x).append(")  #v").append(物品).append("##b#t").append(物品).append("##k x ").append(数量).append("  #b保管费#k:").append(保管费用).append("\r\n");
                name.append("#L").append(序号).append("# ").append(x).append(") #b").append(物品名字).append("#k");
                //for (int j = 30 - 物品名字.getBytes().length; j >= 0; j--) {
                name.append("      ");
                // }
                name.append("x").append(数量).append("");
                //for (int j = 5 - 物品数量.getBytes().length; j >= 0; j--) {
                name.append("       ");
                //}
                name.append("#b保管费#k:").append(保管费用).append("#l\r\n");
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("显示背包物品、出错" + ex.getMessage());
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

    public static int 取物品数量(int id) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT cout as DATA FROM warehouse WHERE id = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("取物品数量、出错");
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

    public static int 取物品代码(int id) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT itemid as DATA FROM warehouse WHERE id = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("取物品代码、出错");
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

    public static long 取物品存入时间(int id) {
        long data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT time as DATA FROM warehouse WHERE id = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getLong("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("取物品代码、出错");
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

    public static void 修改仓库物品数量(int a, int b) {
        if (取物品数量(b) > 0) {
            Connection con = null;
            try {
                con = Start.getInstance().getConnection();
                try (PreparedStatement ps = con.prepareStatement("UPDATE warehouse SET cout = cout + ? WHERE id = ?")) {
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
            清空仓库不存在物品();
        }
    }

    public static void 清空仓库不存在物品() {
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            ps1 = con.prepareStatement("SELECT * FROM warehouse WHERE cout = 0");
            rs = ps1.executeQuery();
            if (rs.next()) {
                String sqlstr = " Delete from warehouse where cout = 0";
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

    public static void 添加物品到仓库(int a, int b, int d, int c) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO warehouse (cout,itemid,accountid,time,world) VALUES (?,?,?,?,?)");
            ps.setInt(1, a);
            ps.setInt(2, b);
            ps.setInt(3, c);
            ps.setLong(4, System.currentTimeMillis());
            ps.setInt(5, d);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            System.out.println("添加物品到仓库" + ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public static int 所有保管费(int b, int a) {
        int data = 0;
        try (Connection con = Start.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM warehouse WHERE accountid = ? && world = ?")) {
                ps.setInt(1, a);
                ps.setInt(2, b);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    data += (int) ((System.currentTimeMillis() - rs.getLong("time")) / 60000 * 2);
                }
                ps.close();
                rs.close();
            }
        } catch (SQLException ex) {
        }
        return data;
    }

    public static int 所有保管物品(int b, int a) {
        int data = 0;
        try (Connection con = Start.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM warehouse WHERE accountid = ? && world = ?")) {
                ps.setInt(1, a);
                ps.setInt(2, b);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    data++;
                }
                ps.close();
                rs.close();
            }
        } catch (SQLException ex) {
        }
        return data;
    }

}
