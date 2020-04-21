package tools.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import launch.Start;

public class Sql {
    public static void main(String[] args) {
        updateSocket("inventoryequipment", "inventoryitemid");
        updateSocket("auctionitems", "id");
    }
    
    public static void updateSocket(String table, String uid) {
        Connection c = Start.getInstance().getConnection();
        try {
            Map<Long, String> options = new HashMap<>();
            
            PreparedStatement ps = c.prepareStatement(String.format("SELECT %s, `options` FROM %s", uid, table));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String option = rs.getString("options");
                long id = rs.getLong(uid);
                if (option != null && !option.isEmpty()) {
                    options.put(id, option);
                }
            }
            rs.close();
            ps.close();
            
            System.out.println("size: " + options.size());
            //inventoryequipment
            ps = c.prepareStatement(String.format("UPDATE %s SET socket = ? WHERE %s = ?", table, uid));
            for (Map.Entry<Long, String> kvp : options.entrySet()) {
                String[] values = kvp.getValue().split(",");
                if (values.length > 0) {
                    ps.setByte(1, (byte) values.length);
                    ps.setLong(2, kvp.getKey());
                    ps.addBatch();
                }
            }
            ps.executeBatch();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        } finally {
            database.Database.cleanUP(null, null, c);
        }
    }
}