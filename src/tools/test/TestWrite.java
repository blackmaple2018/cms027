/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.test;

import constants.ServerProperties;
import database.Database;
import packet.transfer.read.InPacket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import launch.Start;
import tools.TimerTools;
import tools.TimerTools.MapTimer;

/**
 *
 * @author Administrator
 */
public class TestWrite {

    public static void main(String[] args) {
        MapTimer.getInstance().schedule(() -> System.out.print("1"), 3000);
    }

    private static void cleanEquipment() {
        Connection con = Start.getInstance().getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Set<Long> items = new HashSet<>();
        Set<Long> equips = new HashSet<>();
        try {
            ps = con.prepareStatement("SELECT * FROM inventoryitems WHERE inventorytype = -1 OR inventorytype = 1");
            rs = ps.executeQuery();
            while (rs.next()) {
                items.add(rs.getLong("inventoryitemid"));
            }
            rs.close();
            ps.close();

            ps = con.prepareStatement("SELECT * FROM inventoryequipment");
            rs = ps.executeQuery();
            while (rs.next()) {
                equips.add(rs.getLong("inventoryitemid"));
            }
            rs.close();
            ps.close();

            equips.removeAll(items);
            System.out.println(String.format("开始清理%d个错误的装备数据.", equips.size()));
            ps = con.prepareStatement("DELETE FROM inventoryequipment WHERE inventoryitemid = ?");
            for (Long id : equips) {
                ps.setLong(1, id);
                ps.execute();
                System.out.println("清理：" + id);
            }
            //ps.executeBatch();
            ps.close();

            System.out.println(String.format("清理了%d个错误的装备数据.", equips.size()));
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            Database.cleanUP(rs, ps, con);
        }
    }
}
