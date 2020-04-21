/*
 QQ机器人
 */
package console.MsgServer;

import client.player.Player;
import static configure.Gamemxd.合伙群;
import static configure.Gamemxd.游戏账号注册上限;
import static configure.worldworld.数字大区;
import static configure.worldworld.文字大区;
import static configure.worldworld.说话同步到游戏里;
import static configure.worldworld.输出信息到所有群;
import static console.MsgServer.QQMsgServer_a.查看世界爆物;
import static console.MsgServer.QQMsgServer_b.写入群等级;
import static console.MsgServer.QQMsgServer_b.群头衔等级;
import static gui.MySQL.取账号绑定的QQ;
import static gui.MySQL.查询QQ下是否有封禁账号;
import static gui.MySQL.角色ID取角色名;
import static gui.MySQL.角色ID取账号ID;
import static gui.MySQL.账号ID取绑定QQ;
import static gui.MySQL.账号ID取账号现金券;
import static gui.MySQL.账号取账号ID;
import static gui.MySQL.账号注册数量;
import static gui.SendMsg_webchinese.发送验证信息;
import static gui.logo.group.群;
import handling.channel.ChannelServer;
import handling.login.handler.AutoRegister;
import handling.world.service.BroadcastService;
import handling.world.service.FindService;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import launch.Start;
import static launch.Start.ConfigValuesMap;
import static launch.Start.加载机器人管理;
import static launch.Start.机器人管理员;
import static launch.Start.地图通行;
import static launch.Start.大区群号1;
import static launch.Start.大区群号2;
import packet.creators.PacketCreator;
import static launch.Start.泡点倍率;
import static launch.Start.经验倍率;
import server.life.MapleMonsterInformationProvider;
import static zevms.Todayitinerary.判断行程;
import tools.FileLogger;
import static zevms.extension.修改推广推广员;
import static zevms.extension.重置自己的推广余额1;
import static zevms.extension.重置自己的推广余额2;
import static zevms.stock.判断股票数值;

public class QQMsgServer implements Runnable {

    private static final int INPORT = 9001;
    private static final int PeerPort = 9000;
    private static int 冷却时间 = 3 * 1000;
    private static Map<String, Long> 工作冷却 = new LinkedHashMap<>();
    private static Map<String, Integer> 回复编号 = new LinkedHashMap<>();
    private static Map<Integer, String> 编号回复 = new LinkedHashMap<>();
    private static int 编号 = 0;
    private byte[] buf = new byte[4096];
    private DatagramPacket dp = new DatagramPacket(buf, buf.length);
    private static DatagramSocket socket;
    private static final QQMsgServer instance = new QQMsgServer();

    public static String 群活跃(int a, String qq) {
        if (群头衔等级.get(qq) != null) {
            String[] A = 群头衔等级.get(qq);
            return A[a];
        }
        return null;
    }

    public static void main(String args[]) {
        System.err.println("机器人启动成功。");
        new Thread(QQMsgServer.getInstance()).start();
    }

    public static QQMsgServer getInstance() {
        return instance;
    }

    private static void 写入冷却(String qq) {
        工作冷却.put(qq, System.currentTimeMillis());
    }

    private static boolean 判断冷却(String qq) {
        long a = 0;
        if (工作冷却.containsKey(qq)) {
            a = 工作冷却.get(qq);
        }
        if (System.currentTimeMillis() - a < 冷却时间) {
            sendMsgToQQ("机器人繁忙，请稍后再试，请你稍后再试。", qq);
            return true;
        }
        return false;
    }

    /**
     * * <输出信息到指定人>
     *
     */
    public static void sendMsgToQQ(final String msg, final String qq) {
        //sendMsgToQQGroup("回复QQ " + qq + ",编号 " + 编号(qq) + "\r\n" + msg, 合伙群);
        try {
            String data = String.format("P`%s`%s", qq, msg.replace("`", "'"));
            byte[] buf = data.getBytes(Charset.forName("GB18030"));
            DatagramPacket echo = new DatagramPacket(buf, buf.length, InetAddress.getLoopbackAddress(), PeerPort);
            socket.send(echo);
            FileLogger.printError(FileLogger.NPC + "/" + qq + "/私聊信息.txt", msg);
        } catch (IOException e) {
            System.err.println("sendMsgToQQToQQ error");
            e.printStackTrace();
            FileLogger.printError(FileLogger.NPC + "sendMsgToQQ" + msg + ".txt", e);
        }
    }

    /**
     * * <输出信息到指定群>
     *
     */
    public static void sendMsgToQQGroup(final String msg, final String qqGroup) {
        try {
            String data = String.format("G`%s`%s", qqGroup, msg.replace("`", "'"));
            byte[] buf = data.getBytes(Charset.forName("GB18030"));
            DatagramPacket echo = new DatagramPacket(buf, buf.length, InetAddress.getLoopbackAddress(), PeerPort);
            socket.send(echo);
        } catch (IOException e) {
            System.err.println("sendMsgToQQGroup error");
            e.printStackTrace();
        }
    }

    public static Map<Integer, Integer[]> 群聊信息 = new HashMap<>();

    public static int 编号(String qq) {
        if (回复编号.containsKey(qq)) {
            return 回复编号.get(qq);
        }
        return 0;
    }

    public static String 回复编号(int a) {
        if (编号回复.containsKey(a)) {
            return 编号回复.get(a);
        }
        return null;
    }

    private static void 公告(final String a, final String token) {
        if (机器人管理(token) > 0) {
            String aa = "[系统公告]:" + a;
            输出信息到所有群(aa);
            BroadcastService.broadcastMessage(Start.getInstance().getWorldById(0).getWorldId(), PacketCreator.ServerNotice(1, aa));
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                socket.receive(dp);
                // 接收到来自QQ机器人的消息
                String rcvd = new String(dp.getData(), 0, dp.getLength(), "UTF-8");
                //System.out.println("QQ: " + rcvd);

                String msgArr[] = rcvd.split("`");
                String msgType = msgArr[0];
                if (msgType.equals("P")) { // 私人
                    int index = msgType.length() + 1;
                    String fromQQ = msgArr[1];
                    index += fromQQ.length() + 1;
                    String token = msgArr[2];
                    index += token.length() + 1;
                    String msg[] = rcvd.substring(index).trim().split("\\s+");
                    FileLogger.printError(FileLogger.NPC + "/" + fromQQ + "/接收信息.txt", rcvd);
                    /*if (编号(fromQQ) == 0) {
                        回复编号.put(fromQQ, 编号 + 1);
                        编号回复.put(编号 + 1, fromQQ);
                        编号 += 1;
                    }
                    String txt = "Q Q：" + 回复编号(编号(fromQQ)) + "\r\n";
                    txt += "编号：" + 编号(fromQQ) + "\r\n";
                    txt += "信息：" + msgArr[3] + "\r\n";
                    sendMsgToQQGroup(txt, 合伙群);*/
                    switch (msg[0]) {
                        case "*g":
                        case "*公告":
                            if (msg.length > 1) {
                                公告(msg[1], token);
                            }
                            break;
                        case "@帮助":
                        case "*帮助":
                            指令大全(token);
                            break;
                        case "*查询":
                        case "*查询账号":
                        case "*查询帐号":
                        case "*我的账号":
                        case "*我的帐号":
                            查询账号(token);
                            break;
                        case "*注册":
                        case "*注册帐号":
                        case "*注册账号":
                        case "*帐号注册":
                        case "*账号注册":
                            if (msg.length > 3) {
                                注册账号(msg[1], msg[2], msg[3], token);
                            }
                            break;
                        case "*绑定手机":
                        case "*绑定手机号":
                        case "*绑定手机号码":
                            if (msg.length > 1) {
                                绑定手机号(fromQQ, msg[1]);
                            }
                            break;
                        case "*推广":
                        case "*推广员":
                        case "*绑定推广员":
                            if (msg.length > 1) {
                                绑定推广员(fromQQ, msg[1]);
                            }
                            break;
                        case "*验证码":
                            if (msg.length > 1) {
                                验证码(fromQQ, msg[1]);
                            }
                            break;

                        case "*修改密码":
                        case "*密码修改":
                        case "*改密":
                            if (msg.length > 2) {
                                修改密码(msg[1], msg[2], token);
                            } else {
                                sendMsgToQQ("指令格式不正确。", token);
                            }
                            break;
                        case "*重载":
                            机器人重载(token);
                            break;
                        case "*经验活动":
                            if (msg.length > 3) {
                                经验活动(msg[1], msg[2], msg[3], token);
                            }
                            break;
                        case "*地图通行":
                            if (msg.length > 2) {
                                地图通行(msg[1], msg[2], token);
                            }
                            break;
                        case "*延迟经验活动":
                            if (msg.length > 4) {
                                延迟经验活动(msg[1], msg[2], msg[3], msg[4], token);
                            }
                            break;
                        case "*泡点活动":
                            if (msg.length > 3) {
                                泡点活动(msg[1], msg[2], msg[3], token);
                            }
                            break;
                        case "*延迟泡点活动":
                            if (msg.length > 4) {
                                延迟泡点活动(msg[1], msg[2], msg[3], msg[4], token);
                            }
                            break;
                        case "*解封":
                            if (msg.length > 1) {
                                解封(msg[1], token);
                            }
                            break;
                        case "*游戏股票":
                            游戏股票(token);
                            break;

                        case "*转区":
                            if (msg.length > 2) {
                                转区(msg[1], msg[2], token);
                            }
                            break;
                        case "*添加怪物爆物":
                        case "*gwbw":
                            if (机器人管理(token) > 0) {
                                if (msg.length > 4) {
                                    QQMsgServer_a.添加怪物爆物(msg[1], msg[2], msg[3], msg[4], token);
                                }
                            }
                            break;
                        case "*充值现金券":
                            if (msg.length > 2) {
                                充值现金券(msg[1], msg[2], token);
                            }
                            break;
                        case "*添加世界爆物":
                        case "*sjbw":
                            if (机器人管理(token) > 0) {
                                if (msg.length > 3) {
                                    QQMsgServer_a.添加世界爆物(msg[1], msg[2], msg[3], token);
                                }
                            }
                            break;
                        case "*查看怪物爆物":
                            if (机器人管理(token) > 0) {
                                if (msg.length > 1) {
                                    QQMsgServer_a.查看怪物爆物(msg[1], token);
                                }
                            }
                            break;
                        case "*查看世界爆物":
                            if (机器人管理(token) > 0) {
                                查看世界爆物(token);
                            }
                            break;
                        case "*删除世界爆物":
                            if (机器人管理(token) > 0) {
                                if (msg.length > 1) {
                                    QQMsgServer_a.删除世界爆物(msg[1], token);
                                }
                            }
                            break;
                        case "*删除怪物爆物":
                            if (机器人管理(token) > 0) {
                                if (msg.length > 2) {
                                    QQMsgServer_a.删除怪物爆物(msg[1], msg[2], token);
                                }
                            }
                            break;
                        default:
                            //交流(msgArr[3], token);
                            break;
                    }
                } else if (msgType.equals("G")) { // 群组
                    int index = msgType.length() + 1;
                    //群
                    String fromGroup = msgArr[1];
                    index += fromGroup.length() + 1;
                    String fromQQ = msgArr[2];
                    index += fromQQ.length() + 1;
                    String qqNickname = "";
                    String qqTitle = "";
                    if (msgArr.length == 6) {
                        qqNickname = msgArr[3];
                        index += qqNickname.length() + 1;
                        qqTitle = msgArr[4];
                        index += qqTitle.length() + 1;
                    }
                    String msg = rcvd.substring(index).trim();
                    String a = msg.substring(0, 1);
                    if ("y".equals(a) || "Y".equals(a)) {
                        说话同步到游戏里(qqNickname, fromQQ, fromGroup, msg);
                        写入群等级(fromQQ, 大区群号2(fromGroup), qqTitle);
                    }
                }
            }
        } catch (SocketException e) {
            System.err.println("Can't open socket");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("UdpHost init error");
            e.printStackTrace();
        }
    }

    public static int 机器人管理(String qq) {
        if (机器人管理员.containsKey(qq)) {
            return 机器人管理员.get(qq);
        }
        return 0;
    }

    private static void 机器人重载(final String token) {
        if (是否通行(token)) {
            return;
        }
        if (机器人管理(token) > 0) {
            加载机器人管理();
            MapleMonsterInformationProvider.getInstance().clearDrops();
            MapleMonsterInformationProvider.getInstance();
            sendMsgToQQ("重载成功。", token);
        }
    }

    private static void 解封(final String account, final String token) {
        if (机器人管理(token) > 0) {
            try (Connection con = Start.getInstance().getConnection()) {
                PreparedStatement ps;
                ps = con.prepareStatement("UPDATE accounts SET banned = 0 WHERE qq = ?");
                ps.setString(1, account);
                ps.executeUpdate();
                ps.close();
            } catch (Exception ex) {
            }

            try (Connection con = Start.getInstance().getConnection()) {
                PreparedStatement ps;
                ps = con.prepareStatement("UPDATE mxmxd_qq_sj SET banned = 0 WHERE qq = ?");
                ps.setString(1, account);
                ps.executeUpdate();
                ps.close();
            } catch (Exception ex) {
            }
            try (Connection con = Start.getInstance().getConnection()) {
                PreparedStatement ps;
                ps = con.prepareStatement("UPDATE characters SET banned = 0 WHERE accountid = ?");
                ps.setString(1, account);
                ps.executeUpdate();
                ps.close();
            } catch (Exception ex) {
            }

            sendMsgToQQ("绑定QQ下的账号解封完成。", token);
        }
    }

    private static void 游戏股票(final String token) {
        if (机器人管理(token) > 0) {
            String txt = "游戏股票价值预览\r\n"
                    + "股票:绿蜗牛 " + 判断股票数值(1) + " 金币/股\r\n"
                    + "股票:蓝蜗牛 " + 判断股票数值(2) + " 金币/股\r\n"
                    + "股票:红蜗牛 " + 判断股票数值(3) + " 金币/股\r\n";
            sendMsgToQQ(txt, token);
        }
    }

    private static void 转区(final String a, final String b, final String token) {
        if (机器人管理(token) > 0) {
            if (!a.matches("^[0-9]{1,10}")) {
                return;
            }

            if (!b.matches("^[0-9]{1,10}")) {
                return;
            }

            if (Integer.parseInt(a) < 0 || Integer.parseInt(a) > 19) {
                return;
            }

            Connection con = null;
            try {
                con = Start.getInstance().getConnection();
                PreparedStatement pss = con.prepareStatement("UPDATE characters SET world = ? WHERE id = ?");
                try {
                    pss.setInt(1, Integer.parseInt(b));
                    pss.setInt(2, Integer.parseInt(a));
                    int res = pss.executeUpdate();
                    if (res > 0) {
                        sendMsgToQQ("恭喜你，角色转区成功！", token);
                    } else {
                        sendMsgToQQ("角色转区失败！", token);
                    }
                } finally {
                    pss.close();
                }
            } catch (SQLException e) {
                System.err.println("转区。" + e);
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

    private static void 地图通行(final String a, final String b, final String token) {
        if (机器人管理(token) > 0) {
            if (文字大区(a) >= 0) {
                int 大区 = 文字大区(a);
                if ("开".equals(b)) {
                    地图通行.put(大区, 0);
                    sendMsgToQQ("" + a + " 大区地图通行 开启。", token);
                } else if ("关".equals(b)) {
                    地图通行.put(大区, 1);
                    sendMsgToQQ("" + a + " 大区地图通行 关闭。", token);
                }

            }
        }
    }

    private static void 经验活动(final String a, final String b, final String c, final String token) {
        if (机器人管理(token) > 0) {
            if (文字大区(a) >= 0) {
                int 大区 = 文字大区(a);
                if (经验倍率(大区) == 0) {
                    经验倍率.put(大区, Integer.parseInt(c));
                    String txt = "[系统公告]:开启 " + b + " 分钟，" + c + " 倍打怪经验活动。";
                    sendMsgToQQ(txt, token);
                    BroadcastService.broadcastMessage(Start.getInstance().getWorldById(0).getWorldId(), PacketCreator.ServerNotice(2, txt));
                    群(txt, 大区群号1(大区));
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000 * 60 * Integer.parseInt(b));
                                String txt = "[系统公告]:经验活动结束。";
                                BroadcastService.broadcastMessage(Start.getInstance().getWorldById(0).getWorldId(), PacketCreator.ServerNotice(2, txt));
                                群(txt, 大区群号1(大区));
                                经验倍率.put(大区, 0);
                            } catch (InterruptedException e) {
                            }
                        }
                    }.start();
                } else {
                    sendMsgToQQ("当前活动开启中。", token);
                }
            }
        }
    }

    private static void 延迟经验活动(final String a, final String b, final String c, final String d, final String token) {
        if (机器人管理(token) > 0) {
            if (文字大区(a) >= 0) {
                int 大区 = 文字大区(a);
                if (经验倍率(大区) == 0) {
                    sendMsgToQQ("" + d + " 分钟后 " + 数字大区(大区) + " 执行 " + b + " 分钟，" + c + " 倍打怪经验活动。", token);
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000 * 60 * Integer.parseInt(d));
                                经验倍率.put(大区, Integer.parseInt(c));
                                String txt = "[系统公告]:开启 " + b + " 分钟，" + c + " 倍打怪经验活动。";
                                sendMsgToQQ(txt, token);
                                BroadcastService.broadcastMessage(Start.getInstance().getWorldById(0).getWorldId(), PacketCreator.ServerNotice(2, txt));
                                群(txt, 大区群号1(大区));
                                new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(1000 * 60 * Integer.parseInt(b));
                                            String txt = "[系统公告]:经验活动结束。";
                                            BroadcastService.broadcastMessage(大区, PacketCreator.ServerNotice(2, txt));
                                            群(txt, 大区群号1(大区));
                                            经验倍率.put(大区, 0);
                                        } catch (InterruptedException e) {
                                        }
                                    }
                                }.start();
                            } catch (InterruptedException e) {
                            }
                        }
                    }.start();
                } else {
                    sendMsgToQQ("当前活动开启中。", token);
                }
            }
        }
    }

    private static void 充值现金券(final String zh, final String b, final String token) {
        if (机器人管理(token) > 0) {
            //判断输出的账号
            if (!zh.matches("^[0-9A-Za-z]{6,11}$")) {
                sendMsgToQQ("账号格式不对。", token);
                return;
            }
            //判断游戏账号是否存在
            if (!AutoRegister.getAccountExists(zh)) {
                sendMsgToQQ("充值失败，游戏账号不存在。", token);
                return;
            }
            int 账号ID = 账号取账号ID(zh);
            int 现金券 = 账号ID取账号现金券(账号ID);
            修改现金券(账号ID, Integer.parseInt(b));
            int 现金券2 = 账号ID取账号现金券(账号ID);
            //通知信息
            sendMsgToQQ("自由冒险岛充值信息\r\n"
                    + "恭喜你，收到来自管理员的充值信息\r\n"
                    + "到账金额：" + b + " 现金券\r\n"
                    + "账户余额：" + 现金券2 + "\r\n", 账号ID取绑定QQ(账号ID));
            sendMsgToQQ("现金券充值成功，游戏账号 " + zh + " 充值 " + b + " 现金券，（现金券金额变动 " + 现金券 + " → " + 现金券2 + "）", token);
            sendMsgToQQGroup("现金券充值成功，游戏账号 " + zh + " 充值 " + b + " 现金券，（现金券金额变动 " + 现金券 + " → " + 现金券2 + "）,操作账号 " + token, 合伙群);
        }
    }

    public static void 修改现金券(int a, int b) {
        if (判断现金券(a) == 0) {
            初始化现金券(a);
        }
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts_xj SET xianjin = xianjin + " + b + " WHERE id =" + a + "")) {
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

    public static void 初始化现金券(int a) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO accounts_xj (id) values (?);");
            ps.setInt(1, a);
            ps.executeUpdate();
            ps.close();
        } catch (Exception Ex) {
            System.err.println("初始化现金券、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public static int 判断现金券(int a) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts_xj WHERE id = ?");
            ps.setInt(1, a);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data += 1;
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("判断现金券、出错");
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

    private static void 泡点活动(final String a, final String b, final String c, final String token) {
        if (机器人管理(token) > 0) {
            if (文字大区(a) >= 0) {
                int 大区 = 文字大区(a);
                if (泡点倍率(大区) == 1) {
                    泡点倍率.put(大区, Integer.parseInt(c));
                    String txt = "[系统公告]:开启 " + b + " 分钟，" + c + " 倍泡点收益活动。";
                    sendMsgToQQ(txt, token);
                    BroadcastService.broadcastMessage(Start.getInstance().getWorldById(0).getWorldId(), PacketCreator.ServerNotice(2, txt));
                    群(txt, 大区群号1(大区));
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000 * 60 * Integer.parseInt(b));
                                String txt = "[系统公告]:泡点活动结束。";
                                BroadcastService.broadcastMessage(大区, PacketCreator.ServerNotice(2, txt));
                                群(txt, 大区群号1(大区));
                                泡点倍率.put(大区, 1);
                            } catch (InterruptedException e) {
                            }
                        }
                    }.start();
                } else {
                    sendMsgToQQ("当前活动开启中。", token);
                }
            }
        }
    }

    private static void 延迟泡点活动(final String a, final String b, final String c, final String d, final String token) {
        if (机器人管理(token) > 0) {
            if (文字大区(a) >= 0) {
                int 大区 = 文字大区(a);
                if (泡点倍率(大区) == 1) {
                    sendMsgToQQ("" + d + " 分钟后 " + 数字大区(大区) + " 执行 " + b + " 分钟，" + c + " 倍泡点收益活动。", token);
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000 * 60 * Integer.parseInt(b));
                                泡点倍率.put(大区, Integer.parseInt(c));
                                String txt = "[系统公告]:开启 " + b + " 分钟，" + c + " 倍泡点收益活动。";
                                sendMsgToQQ(txt, token);
                                BroadcastService.broadcastMessage(大区, PacketCreator.ServerNotice(2, txt));
                                群(txt, 大区群号1(大区));
                                new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(1000 * 60 * Integer.parseInt(b));
                                            String txt = "[系统公告]:泡点活动结束。";
                                            BroadcastService.broadcastMessage(大区, PacketCreator.ServerNotice(2, txt));
                                            群(txt, 大区群号1(大区));
                                            泡点倍率.put(大区, 1);
                                        } catch (InterruptedException e) {
                                        }
                                    }
                                }.start();
                            } catch (InterruptedException e) {
                            }
                        }
                    }.start();
                } else {
                    sendMsgToQQ("当前活动开启中。", token);
                }
            }
        }
    }

    public static void 全服喇叭(int world, int a, String b) {
        for (ChannelServer cserv : Start.getInstance().getWorldById(world).getChannels()) {
            for (Player chr : cserv.getPlayerStorage().getAllCharacters()) {
                if (chr == null) {
                    continue;
                }
                chr.dropMessage(a, b);
            }
        }
    }

    private static void 指令大全(final String token) {
        if (是否通行(token)) {
            return;
        }
        sendMsgToQQ(""
                + "<你可以使用的自助指令如下>\r\n"
                + "[*注册 账号 密码 (男/女)] 这是注册男或者女性账号。\r\n"
                + "[*改密 账号 新密码] 修改密码的时候使用。\r\n"
                + "[*查询] 查看QQ号注册的所有账号。\r\n"
                + "[*推广员 QQ号码] 推广人享受被推广人的充值福利。\r\n"
                + "[*绑定手机 手机号码] 绑定该QQ的游戏账号。\r\n"
                + "[*验证码 验证码] 输入手机收到的验证码。\r\n"
                //+ "[*举报 QQ] 举报有嫌疑的QQ号。\r\n"
                //+ "[*转区 角色ID/昵称 大区] 将角色转到其他区域。\r\n"
                // + "[*附魔] 查看游戏内所有附魔类型。\r\n"
                // + "[*称号] 查看游戏内所有称号效果。\r\n"
                //+ "[*大区说明] 每个大区的详细。\r\n"
                // + "商店地址：https://www.vifaka.com/list/7830ea84edd2f958\r\n"
                // + "论坛地址：https://www.fengyewuyu.com/forum.php?mod=forumdisplay&fid=67"
                + "", token);

        if (机器人管理(token) > 0) {
            sendMsgToQQ(""
                    + "<你可以使用的自助指令如下>\r\n"
                    + "*游戏股票\r\n"
                    + "*游戏数据\r\n"
                    + "*游戏数据修改[序号 修改]\r\n"
                    + "*经验活动[大区 分钟 倍率]\r\n"
                    + "*泡点活动[大区 分钟 倍率]\r\n"
                    + "*解封[QQ] 解封该QQ封禁状态\r\n"
                    + "*充值现金券[游戏账号 数量] 给指定游戏账号充值现金券\r\n"
                    /*+ "*重载 重载爆物\r\n"
                    + "*添加怪物爆物[怪物id 物品id 爆率 0]\r\n"
                    + "*添加世界爆物[物品id 爆率]\r\n"
                    + "*删除世界爆物[物品id]\r\n"
                    + "*删除怪物爆物[怪物id 物品id]\r\n"
                    + "*查看怪物爆物[怪物id]\r\n"
                    + "*查看世界爆物\r\n"*/
                    + "", token);
        }
    }

    public static void sendImgToQQGroup(final String imgFileName, final String qqGroup) {
        try {
            String data = String.format("G`%s`[CQ:image,file=%s]", qqGroup, imgFileName.replace("`", "'"));
            byte[] buf = data.getBytes();
            //System.out.println("[->qq group] : " + new String(buf));
            DatagramPacket echo = new DatagramPacket(buf, buf.length, InetAddress.getLoopbackAddress(), PeerPort);
            socket.send(echo);
        } catch (IOException e) {
            System.err.println("sendImgToQQGroup error");
            //e.printStackTrace();
        }

    }


    private static void 注册账号(final String zh, final String mm, final String xb, final String token) {
        if (ConfigValuesMap.get("账号注册") != 0) {
            sendMsgToQQ("管理员未开放账号注册。", token);
            return;
        }
        if (是否通行(token)) {
            return;
        }

        //判断输出的账号
        if (!zh.matches("^[0-9A-Za-z]{6,11}$")) {
            sendMsgToQQ("账号格式不对，必须由6-11位数字或字母组成。", token);
            return;
        }
        //判断密码长度
        if (mm.length() > 10) {
            sendMsgToQQ("注册失败，密码长度过长。", token);
            return;
        }
        //判断密码格式
        if (!mm.matches("^[0-9A-Za-z]{6,10}$")) {
            sendMsgToQQ("密码不合格，必须由6-10位数字或字母组成。", token);
            return;
        }
        //判断游戏账号是否存在
        if (AutoRegister.getAccountExists(zh)) {
            sendMsgToQQ("注册失败，游戏账号已经存在。", token);
            return;
        }
        //判断性别
        if (!"0".equals(xb) && !"1".equals(xb) && !"男".equals(xb) && !"女".equals(xb)) {
            sendMsgToQQ("账号性别设置只能0(男)或者1(女)。", token);
            return;
        }
        //账号注册数量限制
        if (账号注册数量(token) >= 游戏账号注册上限) {
            sendMsgToQQ("每个QQ号只能注册 " + 游戏账号注册上限 + " 个游戏账号。", token);
            return;
        }
        //查询是否已经被封禁
        if (查询QQ下是否有封禁账号(token) > 0) {
            sendMsgToQQ("你的QQ关联的游戏账号已经违反了冒险岛绿色游戏守则，已经被永久封禁。", token);
            return;
        }

        String account = zh;
        String password = mm;
        String QQ = token;
        int xingbie = 0;
        String 性别 = "";
        if ("0".equals(xb) || "男".equals(xb)) {
            xingbie = 0;
            性别 = "男";
        } else {
            xingbie = 1;
            性别 = "女";
        }
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
        } catch (Exception ex) {
            sendMsgToQQ("注册失败，账号注册出错。" + ex, token);
            return;
        }
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO accounts (name, password,qq,gender) VALUES (?,?,?,?)");
            ps.setString(1, account);
            ps.setString(2, password);
            ps.setString(3, QQ);
            ps.setInt(4, xingbie);
            ps.executeUpdate();
            ps.close();
            sendMsgToQQ("恭喜你注册成功，账号: " + zh + " 密码: " + mm + " 账号性别为: " + 性别 + "，祝你游戏愉快。", token);
        } catch (SQLException ex) {
            sendMsgToQQ("注册失败，账号注册出错。" + ex, token);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }

    }

    private static void 修改密码(final String zh, final String mm, final String token) {
        if (ConfigValuesMap.get("密码修改") != 0) {
            sendMsgToQQ("管理员未开放账号密码修改功能。", token);
            return;
        }
        if (是否通行(token)) {
            return;
        }
        if (!mm.matches("^[0-9A-Za-z]{6,10}$")) {
            sendMsgToQQ("新密码不合格，必须由6-10位数字或字母组成。", token);
            return;
        }
        Boolean isExists = AutoRegister.getAccountExists(zh);
        if (!isExists) {
            sendMsgToQQ("该账号不存在。", token);
            return;
        }
        if (取账号绑定的QQ(zh) == null ? token != null : !取账号绑定的QQ(zh).equals(token)) {
            sendMsgToQQ("这个账号不属于你。", token);
            return;
        }
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement pss = con.prepareStatement("UPDATE accounts SET password = ? WHERE name = ?");
            try {
                pss.setString(1, mm);
                pss.setString(2, zh);
                int res = pss.executeUpdate();
                if (res > 0) {
                    sendMsgToQQ("恭喜你，密码修改成功！", token);
                } else {
                    sendMsgToQQ("没有找到你的QQ对应的账号，密码修改失败！", token);
                }
            } finally {
                pss.close();
            }
        } catch (SQLException e) {
            System.err.println("修改密码出错。" + e);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
    }

    private static void 查询账号(final String token) {

        if (ConfigValuesMap.get("账号查询") != 0) {
            sendMsgToQQ("管理员未开放账号查询功能。", token);
            return;
        }
        if (是否通行(token)) {
            return;
        }
        final StringBuilder sb = new StringBuilder();
        int a = 0;
        int b = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = null;

            ResultSet rs = null;
            ps = con.prepareStatement("SELECT * FROM accounts WHERE qq = " + token + "");
            rs = ps.executeQuery();
            while (rs.next()) {
                String txt = "";
                int 现金券 = 账号ID取账号现金券(账号取账号ID(rs.getString("name")));
                txt += "游戏账号 : " + rs.getString("name") + "\r\n"
                        + "现金券 : " + 现金券 + "\r\n";
                a += 1;
                b += 现金券;
                sb.append(String.format(txt));
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {

        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        sendMsgToQQ("所有账号如下：\r\n"
                + "账号数量 : " + a + "\r\n"
                + "现金券 : " + b + "\r\n"
                + "===========\r\n" + sb.toString(), token);
    }

    public static boolean 是否通行(String token) {
        if (查询QQ下是否有封禁账号(token) > 0) {
            sendMsgToQQ("你的QQ关联的游戏账号已经违反了冒险岛绿色游戏守则，已经被永久封禁。", token);
            return true;
        }
        if (判断冷却(token)) {
            return true;
        } else {
            写入冷却(token);
        }

        return false;
    }

    private QQMsgServer() {
        try {
            socket = new DatagramSocket(INPORT);
        } catch (SocketException e) {
            System.err.println("无法开启，端口被占用");
            System.exit(1);
        }
    }

    public static String 显示所有附魔() {
        StringBuilder name = new StringBuilder();
        Connection con = null;
        int 总 = 概率();
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM mxmxd_fumo_info order by id asc ")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String a1 = rs.getString("fumoName");
                    String a2 = rs.getString("fumoInfo");
                    double a3 = rs.getInt("gailv");
                    if (a2.contains("%s%%")) {
                        name.append(a1).append(a2.replaceAll("%s%%", "N%")).append(" ");
                    } else if (a2.contains("%s")) {
                        name.append(a1).append(a2.replaceAll("%s", "N")).append(" ");
                    }
                    // 创建一个数值格式化对象   

                    NumberFormat numberFormat = NumberFormat.getInstance();

                    // 设置精确到小数点后2位   
                    numberFormat.setMaximumFractionDigits(2);

                    String result = numberFormat.format((float) a3 / (float) 总 * 100);

                    name.append(" - (").append(result).append("%)\r\n");

                }
                rs.close();
            }
        } catch (SQLException ex) {
            System.err.println("显示所有附魔类型、出错" + ex.getMessage());
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

    public static String 显示所有称号() {
        StringBuilder name = new StringBuilder();
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM mxmxd_chenghao")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String a1 = rs.getString("Name");
                    String a2 = rs.getString("xiaoguo");
                    String a3 = rs.getString("huoqu");
                    name.append("称号:" + a1 + "\r\n");
                    name.append("效果:" + a2 + "\r\n");
                    name.append("获取:" + a3 + "\r\n\r\n");
                }
                rs.close();
            }
        } catch (SQLException ex) {
            System.err.println("显示所有称号、出错" + ex.getMessage());
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

    public static int 概率() {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM mxmxd_fumo_info");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data += rs.getInt("gailv");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("角色取会员等级、出错");
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

    public static Map<String, String> 流程 = new HashMap<>();
    public static Map<String, String[]> 注册账号 = new HashMap<>();

    private static void 交流(final String a, final String qq) {
        if (a.contains("注册") || "注册账号".equals(判断流程(qq)) || "询问账号".equals(判断流程(qq)) || "注册密码".equals(判断流程(qq))
                || "询问密码".equals(判断流程(qq)) || "选择性别".equals(判断流程(qq)) || "完成选性".equals(判断流程(qq))) {
            注册游戏账号(a, qq);
        } else {
            sendMsgToQQ("请问我有什么可以帮助到你的吗？注册游戏账号，还是修改密码？输入帮助可看快捷指令哦。", qq);
        }
    }

    private static void 注册游戏账号(final String a, final String qq) {
        String 游戏账号 = 判断(0, qq);
        String 游戏密码 = 判断(1, qq);
        String 账号性别 = 判断(3, qq);

        //询问游戏账号
        if (判断流程(qq) == null) {
            写入流程("注册账号", qq);
            sendMsgToQQ("你好，请直接告诉我，你想注册的游戏账号。", qq);
            return;
        }
        //注册账号
        if ("注册账号".equals(判断流程(qq))) {
            if (!a.matches("^[0-9A-Za-z]{6,11}$")) {
                sendMsgToQQ("账号格式不对，必须由6-11位数字或字母组成。", qq);
                return;
            }
            //判断游戏账号是否存在
            if (AutoRegister.getAccountExists(a)) {
                sendMsgToQQ("注册失败，游戏账号已经存在。", qq);
                return;
            }
            写入流程("询问账号", qq);
            sendMsgToQQ("请问 " + a + " 是你想要注册的游戏账号吗？", qq);
            写入(a, "", qq, "");
            return;
        }

        //询问并且确定注册的账号
        if ("询问账号".equals(判断流程(qq))) {
            switch (a) {
                case "嗯":
                case "恩":
                case "是的":
                case "是":
                case "对":
                case "对的":
                    写入流程("注册密码", qq);
                    sendMsgToQQ("请为 " + 游戏账号 + " 设置一个密码吧？", qq);
                    break;
                default:
                    sendMsgToQQ("请问 " + 游戏账号 + " 是你想要注册的游戏账号吗？请回复我是，或者不是。", qq);
                    break;
            }
            return;
        }
        //询问注册密码
        if ("注册密码".equals(判断流程(qq))) {
            //判断密码长度
            if (a.length() > 10) {
                sendMsgToQQ("注册失败，密码长度过长。", qq);
                return;
            }
            //判断密码格式
            if (!a.matches("^[0-9A-Za-z]{6,10}$")) {
                sendMsgToQQ("密码不合格，必须由6-10位数字或字母组成。", qq);
                return;
            }
            写入流程("询问密码", qq);
            写入(游戏账号, a, qq, "");
            sendMsgToQQ("请问 " + a + " 是你想要设置的密码？", qq);
            return;
        }

        //询问并且确定注册账号密码
        if ("询问密码".equals(判断流程(qq))) {
            switch (a) {
                case "嗯":
                case "恩":
                case "是的":
                case "是":
                case "对":
                case "对的":
                    写入(游戏账号, 游戏密码, qq, "");
                    写入流程("选择性别", qq);
                    sendMsgToQQ("请为你的账号选择一个性别，男还是女？", qq);
                    break;
                default:
                    sendMsgToQQ("请问 " + 游戏密码 + " 是你想要设置的账号密码？请回复我是，或者不是。", qq);
                    break;
            }
            return;
        }
        //选择性别
        if ("选择性别".equals(判断流程(qq))) {

            switch (a) {
                case "男":
                case "男的":
                case "男性":
                    写入(游戏账号, 游戏密码, qq, "男");
                    写入流程("完成选性", qq);
                    sendMsgToQQ("你是要注册一个男性角色的账号对吧？", qq);
                    break;
                case "女":
                case "女的":
                case "女性":
                    写入(游戏账号, 游戏密码, qq, "女");
                    写入流程("完成选性", qq);
                    sendMsgToQQ("你是要注册一个女性角色的账号对吧？", qq);
                    break;
                default:
                    sendMsgToQQ("请告诉我你选择男还是女？", qq);
                    break;
            }
            return;
        }
        //询问并且确定注册账号密码
        if ("完成选性".equals(判断流程(qq))) {
            switch (a) {
                case "嗯":
                case "恩":
                case "是的":
                case "是":
                case "对":
                case "对的":
                case "确认":
                    //账号注册数量限制
                    if (账号注册数量(qq) >= 游戏账号注册上限) {
                        sendMsgToQQ("每个QQ号只能注册 " + 游戏账号注册上限 + " 个游戏账号。", qq);
                        return;
                    }
                    //查询是否已经被封禁
                    if (查询QQ下是否有封禁账号(qq) > 0) {
                        sendMsgToQQ("你的QQ关联的游戏账号已经违反了冒险岛绿色游戏守则，已经被永久封禁。", qq);
                        return;
                    }

                    String 性别 = "";
                    int x = 0;
                    if ("男".equals(账号性别)) {
                        性别 = "男";
                        x = 0;
                    } else {
                        性别 = "女";
                        x = 1;
                    }
                    Connection con = null;
                    try {
                        con = Start.getInstance().getConnection();
                    } catch (Exception ex) {
                        sendMsgToQQ("注册失败，账号注册出错。" + ex, qq);
                        return;
                    }
                    try {
                        PreparedStatement ps = con.prepareStatement("INSERT INTO accounts (name, password,qq,gender) VALUES (?,?,?,?)");
                        ps.setString(1, 游戏账号);
                        ps.setString(2, 游戏密码);
                        ps.setString(3, qq);
                        ps.setInt(4, x);
                        ps.executeUpdate();
                        ps.close();
                        sendMsgToQQ("恭喜你注册成功，账号: " + 游戏账号 + " 密码: " + 游戏密码 + " 账号性别为: " + 性别 + "，祝你游戏愉快。", qq);
                    } catch (SQLException ex) {
                        sendMsgToQQ("注册失败，账号注册出错。" + ex, qq);
                    } finally {
                        try {
                            if (con != null && !con.isClosed()) {
                                con.close();
                            }

                        } catch (SQLException e) {
                        }
                    }
                    流程.remove(qq);
                    break;
                default:
                    sendMsgToQQ("请确认一下你要注册的游戏账号。\r\n"
                            + "游戏账号: " + 游戏账号 + " \r\n"
                            + "账号密码: " + 游戏密码 + " \r\n"
                            + "角色性别: " + 账号性别 + " \r\n"
                            + "回复确认即可注册成功。", qq);
                    break;
            }
            return;
        }

    }

    private static void 写入流程(final String a, final String qq) {
        流程.put(qq, a);
    }

    public static String 判断流程(String qq) {
        if (流程.containsKey(qq)) {
            return 流程.get(qq);
        }
        return null;
    }

    public static String 判断(int a, String qq) {
        String 值 = null;
        if (注册账号.get(qq) != null) {
            String[] A = 注册账号.get(qq);
            return A[a];
        }
        return null;
    }

    private static void 绑定推广员(String token, String a) throws IOException {

        if (判断是否有操作(token) > 0) {
            return;
        }

        boolean result = a.matches("[0-9]+");

        if (!result) {
            sendMsgToQQ("请输入对方QQ号码。", token);
            return;
        }

        if (账号注册数量(a) == 0) {
            sendMsgToQQ("不存在的推广员。", token);
            return;
        }

        if (a == null ? token == null : a.equals(token)) {
            sendMsgToQQ("自己不能绑定自己。", token);
            return;
        }

        修改推广推广员(a, token);
        重置自己的推广余额1(token);
        重置自己的推广余额2(token);
        sendMsgToQQ("恭喜你成功绑定推广员，QQ账号为 " + a + " ，你后续的现金券充值将会以10%比例反馈给对方。如果你想进行更换推广员可以再次绑定，但是会清空反馈的余额。", token);
        sendMsgToQQ("恭喜你，QQ账号为 " + token + " ，成功绑定推广员为你，你将享受对方充值的反馈奖励。", a);
    }

    private static void 写入(final String 账号, final String 密码, final String qq, final String c) {
        注册账号.put(qq, new String[]{账号, 密码, qq, c});
    }
    public static Map<String, String> 绑定手机号缓存 = new HashMap<>();
    public static Map<String, String> 绑定手机号缓存2 = new HashMap<>();
    public static Map<String, Integer> 绑定手机冷却 = new HashMap<>();
    public static Map<String, Integer> QQ账号操作 = new HashMap<>();

    private static void 绑定手机号(String token, String a) throws IOException {
        if (判断绑定手机冷却(a) > 0) {
            sendMsgToQQ("请等待一会后再绑定其他账号。", token);
            return;
        }
        if (判断是否有操作(token) > 0) {
            return;
        }
        if (账号注册数量(token) == 0) {
            sendMsgToQQ("你尚未注册游戏账号。", token);
            return;
        }
        if (判断QQ是否绑定手机(token) > 0) {
            sendMsgToQQ("你已经绑定了手机，无需继续绑定。", token);
            return;
        }
        if (判断手机是否绑定QQ(a) > 0) {
            sendMsgToQQ("该手机已经绑定了其他QQ，无法继续绑定。", token);
            return;
        }
        sendMsgToQQ("你要绑定的手机号码为 " + a + " ，请在 3 分钟内输入收到的验证码，本服不会泄露玩家信息。", token);
        int 验证码 = (int) (Math.random() * 899999) + 100000;
        绑定手机号缓存.put(a, "" + 验证码 + "");
        绑定手机号缓存2.put("" + 验证码 + "", a);
        QQ账号操作.put(token, 1);
        绑定手机冷却.put(a, 1);
        发送验证信息(a, "你本次操作的验证码是 : " + 验证码 + "，请在3分钟内，发送指令 *验证码 验证码 给机器人进行最后的绑定。");
        final String A = a;
        final int B = 验证码;
        final String C = token;

        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000 * 60 * 3);
                    绑定手机号缓存.remove(A);
                    绑定手机号缓存2.remove("" + B + "");
                    QQ账号操作.remove(C);
                    绑定手机冷却.remove(A);

                } catch (InterruptedException e) {
                }
            }
        }.start();
    }

    private static void 验证码(final String token, final String a) throws IOException {

        if (判断是否有操作(token) == 0) {
            return;
        }
        if (判断绑定手机号缓存(判断绑定手机号缓存2(a)) == null ? a != null : !判断绑定手机号缓存(判断绑定手机号缓存2(a)).equals(a)) {
            sendMsgToQQ("验证码错误，不正确的操作。", token);
            return;
        }
        try {
            Connection con = Start.getInstance().getConnection();
            PreparedStatement pss = con.prepareStatement("REPLACE INTO mxmxd_qq_sj(qq, sj) VALUES(?, ?)");
            try {
                pss.setString(1, token);
                pss.setString(2, 判断绑定手机号缓存2(a));
                pss.executeUpdate();
            } finally {
                pss.close();
            }
        } catch (SQLException ex) {
            System.err.println("保存绑定手机号出错：" + ex.getMessage());
        }
        sendMsgToQQ("恭喜你的QQ账号 " + token + "，绑定手机号码 " + 判断绑定手机号缓存2(a) + " 成功。", token);
        绑定手机号缓存.remove(token);
        绑定手机号缓存2.remove(token);
        QQ账号操作.remove(token);
    }

    public static String 判断绑定手机号缓存(String a) {
        if (绑定手机号缓存.containsKey(a)) {
            return 绑定手机号缓存.get(a);
        }
        return "";
    }

    public static String 判断绑定手机号缓存2(String a) {
        if (绑定手机号缓存2.containsKey(a)) {
            return 绑定手机号缓存2.get(a);
        }
        return "";
    }

    public static int 判断是否有操作(String a) {
        if (QQ账号操作.containsKey(a)) {
            return QQ账号操作.get(a);
        }
        return 0;
    }

    public static int 判断绑定手机冷却(String a) {
        if (绑定手机冷却.containsKey(a)) {
            return 绑定手机冷却.get(a);
        }
        return 0;
    }

    public static int 判断QQ是否绑定手机(String a) {
        int data = 0;
        try (Connection con = Start.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM mxmxd_qq_sj WHERE qq = '" + a + "'")) {
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

    public static int 判断手机是否绑定QQ(String a) {
        int data = 0;
        try (Connection con = Start.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM mxmxd_qq_sj WHERE sj = '" + a + "'")) {
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

    private static void 角色(final String account, final String token) {

        boolean result = account.matches("[0-9]+");
        if (!result) {
            sendMsgToQQ("请输入数字ID。", token);
            return;
        }
        Boolean isExists = AutoRegister.判断角色ID是否存在(Integer.parseInt(account));
        if (!isExists) {
            sendMsgToQQ("你输入的角色ID不存在。", token);
            return;
        }

        if (!账号ID取绑定QQ(角色ID取账号ID(Integer.parseInt(account))).equals(token)) {
            sendMsgToQQ("你要删除的角色不属于你。", token);
            return;
        }

        for (int i = 0; i <= 19; i++) {
            if (FindService.findChannel(角色ID取角色名(Integer.parseInt(account)), i) > 0) {
                sendMsgToQQ("你的角色目前在线，请让角色离线后在使用。", token);
                return;
            }
        }

        sendMsgToQQ("申请删除角色 " + 角色ID取角色名(Integer.parseInt(account)) + " 成功，角色将在 1 小时候被删除。", token);

        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000 * 60 * 60 * 1);

                } catch (InterruptedException e) {
                }
            }
        }.start();
    }

    private static void 删除角色(final String account, final String token) {

        boolean result = account.matches("[0-9]+");
        if (!result) {
            sendMsgToQQ("请输入数字ID。", token);
            return;
        }
        Boolean isExists = AutoRegister.判断角色ID是否存在(Integer.parseInt(account));
        if (!isExists) {
            sendMsgToQQ("你输入的角色ID不存在。", token);
            return;
        }

        if (!账号ID取绑定QQ(角色ID取账号ID(Integer.parseInt(account))).equals(token)) {
            sendMsgToQQ("你要删除的角色不属于你。", token);
            return;
        }

        for (int i = 0; i <= 19; i++) {
            if (FindService.findChannel(角色ID取角色名(Integer.parseInt(account)), i) > 0) {
                sendMsgToQQ("你的角色目前在线，请让角色离线后在使用。", token);
                return;
            }
        }

        sendMsgToQQ("申请删除角色 " + 角色ID取角色名(Integer.parseInt(account)) + " 成功，角色将在 1 小时候被删除。", token);

        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000 * 60 * 60 * 1);

                } catch (InterruptedException e) {
                }
            }
        }.start();
    }

    private static void 查询角色今日行程(final String a, final String token) {
        if (是否通行(token)) {
            return;
        }

        boolean result = a.matches("[0-9]+");
        if (!result) {
            sendMsgToQQ("请输入角色数字ID。", token);
            return;
        }

        if (取账号绑定的QQ(角色ID取账号ID(Integer.parseInt(a))) == null ? token != null : !取账号绑定的QQ(角色ID取账号ID(Integer.parseInt(a))).equals(token)) {
            sendMsgToQQ("这个账号不属于你。", token);
            return;
        }

        int id = Integer.parseInt(a);
        sendMsgToQQ("<角色今日行程>\r\n"
                + "今日在线:" + 判断行程(id, 5) + "\r\n"
                + "泡点时间:" + 判断行程(id, 6) + "\r\n"
                + "泡点收益/金币:" + 判断行程(id, 7) + "\r\n"
                + "泡点收益/经验:" + 判断行程(id, 8) + "\r\n"
                + "泡点收益/点券:" + 判断行程(id, 9) + "\r\n"
                + "杀怪数量:" + 判断行程(id, 10) + "\r\n"
                + "获取经验:" + 判断行程(id, 11) + "\r\n",
                token);
    }
}