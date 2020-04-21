package cashshop;

import client.player.inventory.Item;
import database.Database;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import launch.Start;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import tools.FileLogger;

public class CashItemFactory {

    private static Map<Integer, CashItem> items = new HashMap<>();
    private static Map<Integer, List<Integer>> packages = new HashMap<>();

    /*static {
     MapleDataProvider etc = MapleDataProviderFactory.getDataProvider("Etc");

     etc.getData("Commodity.img").getChildren().forEach((item) -> {
     int sn = MapleDataTool.getIntConvert("SN", item);
     int itemId = MapleDataTool.getIntConvert("ItemId", item);
     int price = MapleDataTool.getIntConvert("Price", item, 0);
     int period = MapleDataTool.getIntConvert("Period", item, 1);
     int gender = MapleDataTool.getIntConvert("Gender", item, 2);
     short count = (short) MapleDataTool.getIntConvert("Count", item, 1);
     boolean onSale = MapleDataTool.getIntConvert("OnSale", item, 0) == 1;
     items.put(sn, new CashItem(sn, itemId, price, count, onSale, period, gender));
     });

     etc.getData("CashPackage.img").getChildren().forEach((cashPackage) -> {
     List<Integer> cPackage = new ArrayList<>();

     cashPackage.getChildByPath("SN").getChildren().forEach((item) -> {
     cPackage.add(Integer.parseInt(item.getData().toString()));
     });

     packages.put(Integer.parseInt(cashPackage.getName()), cPackage);
     });
     }*/
    public static void loadCashItemsFromDatabase() {
        Connection con = null;
        items.clear();
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM cashitems order by serialnumber asc");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getInt("sj") == 0) {
                    int sn = rs.getInt("serialnumber");
                    items.put(sn, new CashItem(sn, rs.getInt("itemid"), rs.getInt("price"), rs.getShort("quantity"), rs.getBoolean("onsale"), rs.getInt("period"), rs.getInt("gender")));
                }
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        System.out.println(String.format("载入%d个商城道具", items.size()));
    }

    public static void main(String[] args) {
        MapleDataProvider etc = MapleDataProviderFactory.getDataProvider("Etc");
        StringBuilder sb = new StringBuilder();
        for (MapleData item : etc.getData("Commodity.img").getChildren()) {
            int sn = MapleDataTool.getIntConvert("SN", item);
            int itemId = MapleDataTool.getIntConvert("ItemId", item, 0);
            int price = MapleDataTool.getIntConvert("Price", item, 0);
            int period = MapleDataTool.getIntConvert("Period", item, 1);
            int gender = MapleDataTool.getIntConvert("Gender", item, 2);
            short count = (short) MapleDataTool.getIntConvert("Count", item, 1);
            int onSale = MapleDataTool.getIntConvert("OnSale", item, 0);
            sb.append(String.format("INSERT INTO `cashitems` VALUES ('%d', '%d', '%d', '%d', '%d', '%d', '%d');", sn, itemId, count, price, gender, period, onSale));
        }
        FileLogger.logToFile(FileLogger.FILE_PATH + "/sql.txt", sb.toString());
    }

    public static Map<Integer, CashItem> getItems() {
        return items;
    }

    public static CashItem getItem(int sn) {
        return items.get(sn);
    }

    public static List<Item> getPackage(int itemId) {
        List<Item> cashPackage = new ArrayList<>();

        packages.get(itemId).forEach((sn) -> {
            cashPackage.add(getItem(sn).toItem(CashItemFactory.getItem(sn), CashItemFactory.getItem(sn).getCount()));
        });

        return cashPackage;
    }

    public static boolean isPackage(int itemId) {
        return packages.containsKey(itemId);
    }
}
