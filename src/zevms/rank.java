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
public class rank {

   public static int 获取天梯线排名2(int a) {
        int DATA = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT rank FROM (SELECT @rownum := @rownum + 1 AS rank, `name`, integral*100000+totalOnlineTimett, `id` FROM characters, (SELECT @rownum := 0) r WHERE gm = 0  && banned = 0  ORDER BY integral*100000+totalOnlineTimett DESC) AS T1 WHERE `id` = ?");
            ps.setInt(1, a);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DATA = rs.getInt("rank");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取天梯线排名 - 数据库查询失败：" + Ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return DATA;
    }

    public static String 天梯积分排行2(int a) {
        int 名次 = 1;
        StringBuilder name = new StringBuilder();
        Connection con = null;
        Connection con2 = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters  WHERE gm = 0  && job !=500 && id = " + a + "");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String 玩家名字2 = rs.getString("name");
                int 在线2 = rs.getInt("integral");
                int x2 = rs.getInt("world");
                String 排名显示2 = String.valueOf(在线2);
                name.append("\t  -------------------------------------------\r\n");
                name.append("\t  #b第 #r"+获取天梯线排名2(a)+"#b 名#k  ");
                name.append("  积分").append(排名显示2);
                for (int j = 8 - 排名显示2.getBytes().length; j > 0; j--) {
                    name.append(" ");
                }
                name.append("[").append(大区(x2)).append("] ").append(玩家名字2).append("\r\n");
                name.append("\t  -------------------------------------------\r\n");
            }
            rs.close();
            ps.close();

            con2 = Start.getInstance().getConnection();
            PreparedStatement ps2 = con2.prepareStatement("SELECT * FROM characters  WHERE gm = 0  && job !=500 && integral >= 100  && banned = 0 order by (integral*100000+totalOnlineTimett) desc  LIMIT 99");
            ResultSet rs2 = ps2.executeQuery();
            while (rs2.next()) {
                String 玩家名字 = rs2.getString("name");
                int 在线 = rs2.getInt("integral");
                int x = rs2.getInt("world");
                String 排名显示 = String.valueOf(在线);
                if (名次 <= 9) {
                    name.append("\t  #b第 #r0").append(名次).append("#b 名#k  ");
                    name.append("  积分").append(排名显示);
                    for (int j = 8 - 排名显示.getBytes().length; j > 0; j--) {
                        name.append(" ");
                    }
                    name.append("[").append(大区(x)).append("] ").append(玩家名字).append("\r\n");
                } else {
                    name.append("\t  #b第 #r").append(名次).append("#b 名#k  ");
                    name.append("  积分").append(排名显示);
                    for (int j = 8 - 排名显示.getBytes().length; j > 0; j--) {
                        name.append(" ");
                    }
                    name.append("[").append(大区(x)).append("] ").append(玩家名字).append("\r\n");
                }
                名次++;
            }

            rs2.close();
            ps2.close();
        } catch (SQLException ex) {
            System.err.println("天梯积分排行、出错" + ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
                if (con2 != null && !con2.isClosed()) {
                    con2.close();
                }
            } catch (SQLException e) {
            }
        }
        name.append("\r\n\r\n");
        return name.toString();
    }

    public static String 锻造工艺排行榜2(int a) {
        int 名次 = 1;
        StringBuilder name = new StringBuilder();
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters  WHERE gm = 0 && world = " + a + "  && job !=500 && forgingExp > 0  && banned = 0 order by forgingExp desc  LIMIT 99");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String 玩家名字 = rs.getString("name");
                int 在线 = rs.getInt("forgingExp");
                int id = rs.getInt("id");
                String 排名显示 = "gy." + 在线 + "";
                if (名次 <= 9) {
                    name.append("\t\t#L").append(id).append("##b第 #r0").append(名次).append("#b 名#k     ");
                    name.append(排名显示);
                    for (int j = 13 - 排名显示.getBytes().length; j > 0; j--) {
                        name.append(" ");
                    }
                    name.append(" ").append(玩家名字).append("#l\r\n");

                } else {
                    name.append("\t\t#L").append(id).append("##b第 #r").append(名次).append("#b 名#k     ");
                    name.append(排名显示);
                    for (int j = 13 - 排名显示.getBytes().length; j > 0; j--) {
                        name.append(" ");
                    }
                    name.append(" ").append(玩家名字).append("#l\r\n");
                }
                名次++;
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("锻造工艺排行榜、出错" + ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        name.append("\r\n\r\n");
        return name.toString();
    }

    public static String 等级排行榜2(int a) {
        int 名次 = 1;
        StringBuilder name = new StringBuilder();
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters  WHERE gm = 0 && world = " + a + "  && banned = 0 order by (level*10000000000+exp+totalOnlineTime) desc LIMIT 99");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String 玩家名字 = rs.getString("name");
                int 等级 = rs.getInt("level");
                int id = rs.getInt("id");
                String 排名显示 = "Lv." + 等级 + "";
                if (名次 <= 9) {
                    name.append("\t\t#L").append(id).append("##b第 #r0").append(名次).append("#b 名#k     ");
                    name.append(排名显示);
                    for (int j = 13 - 排名显示.getBytes().length; j > 0; j--) {
                        name.append(" ");
                    }
                    name.append(" ").append(玩家名字).append("#l\r\n");

                } else {
                    name.append("\t\t#L").append(id).append("##b第 #r").append(名次).append("#b 名#k     ");
                    name.append(排名显示);
                    for (int j = 13 - 排名显示.getBytes().length; j > 0; j--) {
                        name.append(" ");
                    }
                    name.append(" ").append(玩家名字).append("#l\r\n");
                }
                名次++;
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("等级排行榜、出错" + ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        name.append("\r\n\r\n");
        return name.toString();
    }

    public static String 在线排行榜2(int a) {
        int 名次 = 1;
        StringBuilder name = new StringBuilder();
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters  WHERE gm = 0 && world = " + a + "  && job !=500 && totalOnlineTime > 0  && banned = 0 order by totalOnlineTime desc  LIMIT 99");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String 玩家名字 = rs.getString("name");
                int 在线 = rs.getInt("totalOnlineTime");
                int id = rs.getInt("id");
                String 排名显示 = "Ta." + 在线 + "";
                if (名次 <= 9) {
                    name.append("\t\t#L").append(id).append("##b第 #r0").append(名次).append("#b 名#k     ");
                    name.append(排名显示);
                    for (int j = 13 - 排名显示.getBytes().length; j > 0; j--) {
                        name.append(" ");
                    }
                    name.append(" ").append(玩家名字).append("#l\r\n");

                } else {
                    name.append("\t\t#L").append(id).append("##b第 #r").append(名次).append("#b 名#k     ");
                    name.append(排名显示);
                    for (int j = 13 - 排名显示.getBytes().length; j > 0; j--) {
                        name.append(" ");
                    }
                    name.append(" ").append(玩家名字).append("#l\r\n");
                }
                名次++;
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("在线排行榜、出错" + ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        name.append("\r\n\r\n");
        return name.toString();
    }

    public static String 点赞排行榜2(int a) {
        int 名次 = 1;
        StringBuilder name = new StringBuilder();
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters  WHERE gm = 0 && world = " + a + "  && job !=500 && dianzhan > 0  && banned = 0 order by dianzhan desc  LIMIT 99");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String 玩家名字 = rs.getString("name");
                int id = rs.getInt("id");
                int DZ = rs.getInt("dianzhan");
                String 排名显示 = "Dz." + DZ + "";
                if (名次 <= 9) {
                    name.append("\t\t#L").append(id).append("##b第 #r0").append(名次).append("#b 名#k     ");
                    name.append(排名显示);
                    for (int j = 13 - 排名显示.getBytes().length; j > 0; j--) {
                        name.append(" ");
                    }
                    name.append(" ").append(玩家名字).append("#l\r\n");

                } else {
                    name.append("\t\t#L").append(id).append("##b第 #r").append(名次).append("#b 名#k     ");
                    name.append(排名显示);
                    for (int j = 13 - 排名显示.getBytes().length; j > 0; j--) {
                        name.append(" ");
                    }
                    name.append(" ").append(玩家名字).append("#l\r\n");
                }
                名次++;
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("点赞排行榜、出错" + ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        name.append("\r\n\r\n");
        return name.toString();
    }

    public static String 击杀怪物排行榜2(int a) {
        int 名次 = 1;
        StringBuilder name = new StringBuilder();
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters  WHERE gm = 0 && world = " + a + "  && job !=500 && KillMonster > 0  && banned = 0 order by KillMonster desc  LIMIT 99");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String 玩家名字 = rs.getString("name");
                int 击杀怪物 = rs.getInt("KillMonster");
                int id = rs.getInt("id");
                String 排名显示 = "ki." + 击杀怪物 + "";
                if (名次 <= 9) {
                    name.append("\t\t#L").append(id).append("##b第 #r0").append(名次).append("#b 名#k     ");
                    name.append(排名显示);
                    for (int j = 13 - 排名显示.getBytes().length; j > 0; j--) {
                        name.append(" ");
                    }
                    name.append(" ").append(玩家名字).append("#l\r\n");

                } else {
                    name.append("\t\t#L").append(id).append("##b第 #r").append(名次).append("#b 名#k     ");
                    name.append(排名显示);
                    for (int j = 13 - 排名显示.getBytes().length; j > 0; j--) {
                        name.append(" ");
                    }
                    name.append(" ").append(玩家名字).append("#l\r\n");
                }
                名次++;
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("在线排行榜、出错" + ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        name.append("\r\n\r\n");
        return name.toString();
    }

    public static String 人气排行榜2(int a) {
        int 名次 = 1;
        StringBuilder name = new StringBuilder();
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters  WHERE gm = 0 && world = " + a + "  && job !=500 && fame > 0  && banned = 0 order by fame desc  LIMIT 99");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String 玩家名字 = rs.getString("name");
                int 人气 = rs.getInt("fame");
                int id = rs.getInt("id");
                String 排名显示 = "Fa." + 人气 + "";
                if (名次 <= 9) {
                    name.append("\t\t#L").append(id).append("##b第 #r0").append(名次).append("#b 名#k     ");
                    name.append(排名显示);
                    for (int j = 13 - 排名显示.getBytes().length; j > 0; j--) {
                        name.append(" ");
                    }
                    name.append(" ").append(玩家名字).append("#l\r\n");

                } else {
                    name.append("\t\t#L").append(id).append("##b第 #r").append(名次).append("#b 名#k     ");
                    name.append(排名显示);
                    for (int j = 13 - 排名显示.getBytes().length; j > 0; j--) {
                        name.append(" ");
                    }
                    name.append(" ").append(玩家名字).append("#l\r\n");
                }
                名次++;
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("在线排行榜、出错" + ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        name.append("\r\n\r\n");
        return name.toString();
    }

    public static String 泡点排行榜2(int a) {
        int 名次 = 1;
        StringBuilder name = new StringBuilder();
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters  WHERE gm = 0 && world = " + a + "  && job !=500 && pdexp > 0  && banned = 0 order by pdexp desc  LIMIT 99");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String 玩家名字 = rs.getString("name");
                int 泡点 = rs.getInt("pdexp");
                int id = rs.getInt("id");
                String 排名显示 = "Pd." + 泡点 + "";
                if (名次 <= 9) {
                    name.append("\t\t#L").append(id).append("##b第 #r0").append(名次).append("#b 名#k     ");
                    name.append(排名显示);
                    for (int j = 13 - 排名显示.getBytes().length; j > 0; j--) {
                        name.append(" ");
                    }
                    name.append(" ").append(玩家名字).append("#l\r\n");

                } else {
                    name.append("\t\t#L").append(id).append("##b第 #r").append(名次).append("#b 名#k     ");
                    name.append(排名显示);
                    for (int j = 13 - 排名显示.getBytes().length; j > 0; j--) {
                        name.append(" ");
                    }
                    name.append(" ").append(玩家名字).append("#l\r\n");
                }
                名次++;
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("在线排行榜、出错" + ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        name.append("\r\n\r\n");
        return name.toString();
    }

    public static String 大区(int a) {
        switch (a) {
            case 0:
                return "蓝蜗牛";
            case 1:
                return "蘑菇仔";
            case 2:
                return "绿水灵";
            case 3:
                return "漂漂猪";
            case 4:
                return "小青蛇";
            case 5:
                return "红螃蟹";
            case 6:
                return "大海龟";
            case 7:
                return "章鱼怪";
            case 8:
                return "顽皮猴";
            case 9:
                return "星精灵";
            case 10:
                return "胖企鹅";
            case 11:
                return "白雪人";
            case 12:
                return "石头人";
            case 13:
                return "紫色猫";
            case 14:
                return "大灰狼";
            case 15:
                return "小白兔";
            case 16:
                return "喷火龙";
            case 17:
                return "火野猪";
            case 18:
                return "青鳄鱼";
            case 19:
                return "花蘑菇";
            default:
                return "未知";
        }
    }
}
