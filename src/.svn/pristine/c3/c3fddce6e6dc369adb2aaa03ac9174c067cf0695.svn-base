package configure;

import client.player.Player;
import static configure.Gamemxd.蓝蜗牛伤害排行;
import static configure.Gamemxd.蘑菇仔伤害排行;
import static console.MsgServer.QQMsgServer.sendImgToQQGroup;
import static gui.MySQL.角色名字;
import static gui.logo.group.群;
import handling.world.service.BroadcastService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import launch.Start;
import static launch.Start.大区群号1;
import static launch.Start.大区群号2;
import packet.creators.PacketCreator;
import static zevms.stock.游戏说话;
import static zevms.stock.群说话;

public class worldworld {

    public static void 输出信息到所有群(String s) {
        for (int i = 0; i <= 大区群号1.size(); i++) {
            群(s, 大区群号1(i));
        }
    }

    public static void 输出天梯排行榜到所有群() {
        for (int i = 0; i <= 大区群号1.size(); i++) {
            sendImgToQQGroup("tiantipaihangbang//0.jpg", 大区群号1(i));
        }
    }

    /**
     * <说话同步到群里，从游戏通过机器人同步到群里>*
     */
    public static void 说话同步到群里(Player p, String s) {
        String a = s.substring(0, 1);
        if ("q".equals(a) || "Q".equals(a)) {
            String txt1 = s;
            String txt2 = txt1.substring(1, txt1.length());
            群("[" + p.getName() + "(" + p.getId() + ")]：" + txt2, 大区群号1(p.getWorldId()));
            游戏说话++;
        }
    }

    /**
     * <说话同步到游戏里，从QQ插件执行到这里>*
     */
    public static void 说话同步到游戏里(String a, String qq, String fromGroup, String msg) {
        String txt1 = msg;
        String txt2 = txt1.substring(1, txt1.length());
        if (msg.contains("image") || msg.contains("face,id")) {
            BroadcastService.broadcastMessagekg(Start.getInstance().getWorldById(大区群号2(fromGroup)).getWorldId(), PacketCreator.ServerNotice(2, "[" + a + "(" + qq + ")]:发了个图"));
        } else {
            BroadcastService.broadcastMessagekg(Start.getInstance().getWorldById(大区群号2(fromGroup)).getWorldId(), PacketCreator.ServerNotice(2, "[" + a + "(" + qq + ")]:" + txt2));
            群说话++;
        }
    }

    /**
     * <每个大区的伤害排行>*
     */
    public static void 全服伤害排行(int p, int a) {
        switch (p) {
            case 0:
                蓝蜗牛伤害排行.put(p, a);
                break;
            case 1:
                蘑菇仔伤害排行.put(p, a);
                break;
            default:
                break;
        }
    }

    /**
     * <返回大区名字>*
     */
    public static String 数字大区(int a) {
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

    /**
     * <返回大区名字>*
     */
    public static int 文字大区(String a) {
        switch (a) {
            case "蓝蜗牛":
                return 0;
            case "蘑菇仔":
                return 1;
            case "绿水灵":
                return 2;
            case "漂漂猪":
                return 3;
            case "小青蛇":
                return 4;
            case "红螃蟹":
                return 5;

            default:
                return -1;
        }
    }

    /**
     * <每个大区的伤害排行>*
     */
    public static String 伤害排行(int aa) {
        StringBuilder sb = new StringBuilder();
        switch (aa) {
            case 0:
                List<Map.Entry<Integer, Integer>> cityInfoList0 = new ArrayList<>(蘑菇仔伤害排行.entrySet());
                Collections.sort(cityInfoList0, new Comparator<Map.Entry<Integer, Integer>>() {
                    public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                        return o2.getValue().compareTo(o1.getValue());
                    }
                });
                int a = 0;
                for (Map.Entry<Integer, Integer> entry : cityInfoList0) {
                    Integer key = entry.getKey();
                    Integer value = entry.getValue();
                    String x = "" + value + "";
                    a++;
                    if (a <= 9) {
                        sb.append("\t00").append(a).append(")  造成伤害 : #r").append(value).append("#k ");
                        for (int j = 12 - x.getBytes().length; j > 0; j--) {
                            sb.append(" ");
                        }
                        sb.append(" #b").append(角色名字(key)).append("#k \r\n");
                    } else if (a > 9 && a <= 99) {
                        sb.append("\t0").append(a).append(")  造成伤害 : #r").append(value).append("#k ");
                        for (int j = 12 - x.getBytes().length; j > 0; j--) {
                            sb.append(" ");
                        }
                        sb.append(" #b").append(角色名字(key)).append("#k \r\n");
                    } else {
                        sb.append("\t").append(a).append(")  造成伤害 : #r").append(value).append("#k ");
                        for (int j = 12 - x.getBytes().length; j > 0; j--) {
                            sb.append(" ");
                        }
                        sb.append(" #b").append(角色名字(key)).append("#k \r\n");
                    }
                }
                break;
            case 1:
                List<Map.Entry<Integer, Integer>> cityInfoList1 = new ArrayList<>(蘑菇仔伤害排行.entrySet());
                Collections.sort(cityInfoList1, new Comparator<Map.Entry<Integer, Integer>>() {
                    public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                        return o2.getValue().compareTo(o1.getValue());
                    }
                });
                int a1 = 0;
                for (Map.Entry<Integer, Integer> entry : cityInfoList1) {
                    Integer key = entry.getKey();
                    Integer value = entry.getValue();
                    String x = "" + value + "";
                    a1++;
                    if (a1 <= 9) {
                        sb.append("\t00").append(a1).append(")  造成伤害 : #r").append(value).append("#k ");
                        for (int j = 12 - x.getBytes().length; j > 0; j--) {
                            sb.append(" ");
                        }
                        sb.append(" #b").append(角色名字(key)).append("#k \r\n");
                    } else if (a1 > 9 && a1 <= 99) {
                        sb.append("\t0").append(a1).append(")  造成伤害 : #r").append(value).append("#k ");
                        for (int j = 12 - x.getBytes().length; j > 0; j--) {
                            sb.append(" ");
                        }
                        sb.append(" #b").append(角色名字(key)).append("#k \r\n");
                    } else {
                        sb.append("\t").append(a1).append(")  造成伤害 : #r").append(value).append("#k ");
                        for (int j = 12 - x.getBytes().length; j > 0; j--) {
                            sb.append(" ");
                        }
                        sb.append(" #b").append(角色名字(key)).append("#k \r\n");
                    }
                }
                break;
            default:
                break;
        }
        return sb.toString();
    }
}
