/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.login.handler;

import database.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import launch.Start;

/**
 *
 * @author Administrator
 */
public class AutoRegister {

    public static boolean 判断角色ID是否存在(int login) {
        boolean accountExists = false;

        try (Connection con = Start.getInstance().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT id FROM characters WHERE id = ?");
            ps.setInt(1, login);
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                accountExists = true;
            }
            rs.close();;
            ps.close();
        } catch (SQLException ex) {
            System.out.println("ZZ" + ex);
        }
        return accountExists;
    }

    public static boolean getAccountExists(String login) {
        boolean accountExists = false;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT name FROM accounts WHERE name = ?");
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                accountExists = true;
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.out.println("getAccountExists:" + ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
        return accountExists;
    }

    public static boolean getcharacters(String txt) {
        boolean accountExists = false;

        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT name FROM characters WHERE name = ?");
            ps.setString(1, txt);
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                accountExists = true;
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.out.println("getAccountExists:" + ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
        return accountExists;
    }

    public static boolean 判断家族名字(String txt) {
        boolean accountExists = false;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT jiazuname FROM jiazu WHERE jiazuname = ?");
            ps.setString(1, txt);
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                accountExists = true;
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.out.println("判断家族名字:" + ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
        return accountExists;
    }
}
