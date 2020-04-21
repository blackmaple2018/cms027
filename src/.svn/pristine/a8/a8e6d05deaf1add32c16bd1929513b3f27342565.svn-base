package security;

import client.Client;
import client.player.Player;
import static configure.Gamemxd.合伙群;
import static console.MsgServer.QQMsgServer.sendMsgToQQGroup;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import launch.Start;
import static launch.Start.大区;

/**
 *
 * @author Administrator
 */
public class jiance {

    public static Map<Integer, Integer[]> 检测坐标 = new HashMap<>();

    public static void 写入检测() {
        检测坐标.clear();
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM jiance")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int 地图 = rs.getInt("map");
                        int x1 = rs.getInt("x1");
                        int y1 = rs.getInt("y1");
                        int x2 = rs.getInt("x2");
                        int y2 = rs.getInt("y2");
                        int x3 = rs.getInt("x3");
                        int y3 = rs.getInt("y3");
                        检测坐标.put(地图, new Integer[]{x1, y1, x2, y2, x3, y3});
                    }
                    rs.close();
                }
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

    }

    public static void 检测(Client c, int map, int x, int y) {
        Player p = c.getPlayer();
        int x1 = 0;
        int y1 = 0;

        if (检测坐标.get(map) != null) {
            Integer[] A = 检测坐标.get(map);
            x1 = A[0];
            y1 = A[1];
            if (((x - x1 > 10 || x - x1 < -10) && (y - y1 > 10 || y - y1 < -10))) {
                检测坐标.remove(map);
                p.getMap().killMonster(9400711);
                p.dropMessage(1, "数据异常，网络中断。");
                //p.ban();
                p.getClient().close();
                sendMsgToQQGroup(""
                        + "QQ：" + p.getQQ() + "\r\n"
                                 + "大区：" + 大区(p.getWorldId()) + "\r\n"
                        + "玩家：" + p.getName() + "\r\n"
                        + "地图：" + p.getMapName(p.getMapId()) + "\r\n"
                        + "原因：吸怪检测2\r\n",
                        合伙群);
            }
        }
    }

}
