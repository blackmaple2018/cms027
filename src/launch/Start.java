package launch;

import cashshop.CashItemFactory;
import client.Client;
import client.player.skills.PlayerSkillFactory;
import static configure.Gamemxd.合伙群;
import static configure.worldworld.输出信息到所有群;
import static configure.worldworld.输出天梯排行榜到所有群;
import constants.ServerProperties;
import static constants.ServerProperties.World.开服名字;
import database.Database;
import gui.zevms;
import handling.channel.ChannelServer;
import handling.login.LoginTools;
import handling.world.World;
import handling.world.service.RespawnService.Respawn;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import packet.netty.LoginAcceptor;
import server.ShutdownServer;
import tools.TimerTools;
import server.itens.ItemInformationProvider;
import server.life.MapleMonsterInformationProvider;
import server.quest.MapleQuestInfoFactory;
import tools.Pair;
import tools.TimerTools.WorldTimer;
import zevms.stock;
import console.MsgServer.QQMsgServer;
import static console.MsgServer.QQMsgServer.sendMsgToQQGroup;
import static console.MsgServer.TextToImage.createTianTiPaiHangBang;
import java.util.logging.Level;
import java.util.logging.Logger;
import zevms.Deletemysql;

public class Start {

    public static Boolean 服务端维护 = false;
    public static Boolean 服务端通行 = false;
    public static Boolean 攻击段数检测 = true;
    public static Boolean 攻击数量检测 = true;
    public static Boolean 角色恢复检测 = true;
    public static Boolean 角色无敌检测 = true;
    public static Start instance = new Start();
    public static long startTime = System.currentTimeMillis();
    public static Map<Integer, Integer> 地图通行 = new HashMap<>();
    public static Map<Integer, String[]> FuMoInfoMap = new HashMap<>();
    public static Map<String, Integer> ConfigValuesMap = new HashMap<>();
    public static Map<Integer, String> 抽奖广播缓存 = new HashMap<>();
    public static Map<Integer, Long> 废弃副本开始时间 = new HashMap<>();
    public static Map<Integer, Integer> 废弃副本随机 = new HashMap<>();
    public static Map<Integer, Integer> 杀怪任务计数 = new HashMap<>();
    public static Map<Integer, Integer> 杀怪计数 = new HashMap<>();
    public static Map<String, Integer> 机器人管理员 = new HashMap<>();
    public static Map<Integer, String> 大区群号1 = new HashMap<>();
    public static Map<String, Integer> 大区群号2 = new HashMap<>();

    public static int 游戏物品爆率 = 100;
    public static int 游戏金币爆率 = 100;
    public static int 巨型蝙蝠怪 = 0;
    public static int 缓存序号 = 0;
    //倍率
    public static Map<Integer, Integer> 经验倍率 = new HashMap<>();
    public static Map<Integer, Integer> 泡点倍率 = new HashMap<>();

    public static zevms zevms;
    public Database db;
    public List<World> worldList = new ArrayList<>();

    public static void 加载游戏倍率() {
        //加载经验倍率
        for (int i = 0; i <= ServerProperties.World.COUNT; i++) {
            //大区，经验倍率
            经验倍率.put(i, 0);
        }
        //加载泡点倍率
        for (int i = 0; i <= ServerProperties.World.COUNT; i++) {
            //大区，泡点倍率
            泡点倍率.put(i, 1);
        }
    }

    public static int 经验倍率(int a) {
        if (经验倍率.containsKey(a)) {
            return 经验倍率.get(a);
        }
        return 0;
    }

    public static int 泡点倍率(int a) {
        if (泡点倍率.containsKey(a)) {
            return 泡点倍率.get(a);
        }
        return 1;
    }

    private void run() {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET loggedin = 0")) {
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            Database.cleanUP(null, null, con);
        }

        System.out.println("ZEVMS冒险岛(027)游戏服务端");
        System.out.println("开服 : " + 开服名字);
        System.out.println("版本 : 027");
        //World.init();
        printLoad("载入计时线程");
        TimerTools.WorldTimer.getInstance().start();
        TimerTools.MiscTimer.getInstance().start();
        TimerTools.ClientTimer.getInstance().start();
        TimerTools.MonsterTimer.getInstance().start();
        TimerTools.ItemTimer.getInstance().start();
        TimerTools.MapTimer.getInstance().start();
        TimerTools.EventTimer.getInstance().start();
        TimerTools.AntiCheatTimer.getInstance().start();
        TimerTools.NPCTimer.getInstance().start();
        TimerTools.CharacterTimer.getInstance().start();
        TimerTools.SkillTimer.getInstance().start();
        TimerTools.PingTimer.getInstance().start();
        TimerTools.CheatTrackerTimer.getInstance().start();

        printLoad("载入物品信息");
        ItemInformationProvider.getInstance().getAllItems();

        printLoad("载入爆物信息");
        MapleMonsterInformationProvider.getInstance();

        printLoad("载入技能信息");
        PlayerSkillFactory.cacheSkills();

        printLoad("载入基础信息");
        LoginTools.setUp();

        printLoad("载入任务信息");
        MapleQuestInfoFactory.loadQuestInfo();
        //
        try {
            printLoad("启动登陆服务器");
            new Thread(new LoginAcceptor()).start();
            for (int i = 0; i < ServerProperties.World.COUNT; i++) {
                getWorlds().add(new World(i, "" + 大区(i)));
                printLoad("启动大区" + 大区(i) + "");
            }

            printLoad("加载游戏频道");
            for (World world : getWorlds()) {
                for (ChannelServer channel : world.getChannels()) {
                    channel.getAcceptor().start();
                }
            }
            WorldTimer.getInstance().register(new Respawn(null, 0), 6000);//todo test
            /*printLoad("经验获取倍率 : " + EXP);
            printLoad("物品掉落倍率 : " + DROP);
            printLoad("金币获取倍率 : " + MESO);*/
            MainThread();
            GetConfigValues();
            GetFuMoInfo();
            new Thread(QQMsgServer.getInstance()).start();
            Thread consoleListener = new Thread(new ConsoleListener());
            consoleListener.setDaemon(true);
            consoleListener.start();
            CashItemFactory.loadCashItemsFromDatabase();
            Gui();
            加载机器人管理();
            加载大区游戏群();
            加载杀怪数量();
            福利泡点();
            加载游戏倍率();
            天梯积分排行();
            详细通知();
            输出信息到所有群("服务端启动完成，玩家可以进入游戏。");
            System.out.println("服务端启动完成");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Pair<Integer, Client> getChannelFromTransfer(int charId, int worldId) {
        for (ChannelServer c : getWorldById(worldId).getChannels()) {
            c.getTrantferLock().readLock().lock();
            try {
                if (c.getTransfers().containsKey(charId)) {
                    return c.getTransfers().get(charId);
                }
            } finally {
                c.getTrantferLock().readLock().unlock();
            }
        }
        return null;
    }

    public Pair<Integer, Client> getChannelFromTransfer(int charId) {
        for (World world : getWorlds()) {
            for (ChannelServer cs : world.getChannels()) {
                cs.getTrantferLock().readLock().lock();
                try {
                    if (cs.getTransfers().containsKey(charId)) {
                        return cs.getTransfers().get(charId);
                    }
                } finally {
                    cs.getTrantferLock().readLock().unlock();
                }
            }
        }
        return null;
    }

    private static Boolean isClearBossLog = false;
    private static Boolean tianti = false;
    private static Boolean stocklog = false;
    private static int Day = 0;

    /**
     * <主要运行的线程1分钟1次的那种>
     */
    public static void MainThread() {
        WorldTimer.getInstance().register(new Runnable() {
            @Override
            public void run() {

                int 时 = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                int 分 = Calendar.getInstance().get(Calendar.MINUTE);
                int 星期 = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
                //记录在线时间
                Start.getInstance().getWorlds().forEach((world) -> {
                    world.getChannels().forEach((cserv) -> {
                        cserv.getPlayerStorage().getAllCharacters().stream().filter((chr) -> !(chr == null)).forEach((chr) -> {
                            chr.记录在线时间();
                        });
                    });
                });
                //股票数据浮动开启
                if (时 >= 5) {
                    if (分 % 59 == 0 && stocklog == false) {
                        stocklog = true;
                        stock.股票浮动();
                    } else if (分 % 30 == 0) {
                        stocklog = false;
                    }
                }

                //每小时执行一次
                if (Day >= 60) {
                    if (星期 > 0) {
                        if (时 == 8 || 时 == 10 || 时 == 12 || 时 == 14 || 时 == 16 || 时 == 18 || 时 == 20 || 时 == 22 || 时 == 23) {
                            try {
                                天梯积分排行();
                            } catch (Exception ex) {
                                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } else if (星期 == 0) {
                        if (时 == 8 || 时 == 10 || 时 == 12 || 时 == 14 || 时 == 16) {
                            try {
                                天梯积分排行();
                            } catch (Exception ex) {
                                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    详细通知();
                    Day = 0;
                } else {
                    Day++;
                }
                //星期天18点通知
                if (星期 == 0 && 时 == 18 && tianti == false) {
                    try {
                        Start.getInstance().getWorlds().forEach((world) -> {
                            world.getChannels().forEach((cserv) -> {
                                cserv.getPlayerStorage().getAllCharacters().stream().filter((chr) -> !(chr == null)).forEach((chr) -> {
                                    chr.saveDatabase();
                                    chr.dropMessage(5, "本周天梯赛已经结算，请各位及时领取天梯奖励。");
                                });
                            });
                        });
                        输出信息到所有群("本周天梯赛已经结算，请各位及时领取天梯奖励。");
                        天梯积分排行();
                    } catch (Exception ex) {
                        Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    tianti = true;
                } else if (星期 == 0 && 时 == 17) {
                    tianti = false;
                }

                //每日凌晨重置
                if (时 == 0 && isClearBossLog == false) {
                    Start.getInstance().getWorlds().forEach((world) -> {
                        world.getChannels().forEach((cserv) -> {
                            cserv.getPlayerStorage().getAllCharacters().stream().filter((chr) -> !(chr == null)).forEach((chr) -> {
                                chr.重置在线时间(星期);
                                chr.dropMessage(5, "新的一天开始了，请你注意休息，不要过于爆肝。");
                            });
                        });
                    });
                    巨型蝙蝠怪 = 0;
                    Deletemysql.Deletelog();
                    isClearBossLog = true;
                } else if (时 == 23) {
                    isClearBossLog = false;
                }
            }
        }, 60 * 1000);
    }

    public static void 福利泡点() {
        WorldTimer.getInstance().register(new Runnable() {
            @Override
            public void run() {
                Start.getInstance().getWorlds().forEach((world) -> {
                    world.getChannels().forEach((cserv) -> {
                        cserv.getPlayerStorage().getAllCharacters().stream().filter((chr) -> !(chr == null)).filter((chr) -> (chr.getLevel() >= 10 && (chr.getMapId() >= 910000000 && chr.getMapId() <= 910000023))).forEach((chr) -> {
                            StringBuilder name = new StringBuilder();
                            int 经验 = chr.get泡点经验();
                            int 加成经验 = 0;
                            if (chr.getEquippedFuMoMap().get(24) != null) {
                                if (chr.getEquippedFuMoMap().get(24) > 0) {
                                    经验 += chr.getEquippedFuMoMap().get(24);
                                }
                            }
                            if (chr.getEquippedFuMoMap().get(26) != null) {
                                if (chr.getEquippedFuMoMap().get(26) > 0) {
                                    加成经验 += 经验 * (chr.getEquippedFuMoMap().get(26) / 100);
                                }
                            }
                            if (经验 > 0) {
                                经验 += 加成经验;
                                经验 = 经验 * 泡点倍率(chr.getWorldId());
                                chr.gainExp(经验);
                                name.append(" 经验 + ").append(经验);
                            }
                            int 金币 = chr.get泡点金币();
                            int 加成金币 = 0;
                            if (chr.getEquippedFuMoMap().get(23) != null) {
                                if (chr.getEquippedFuMoMap().get(23) > 0) {
                                    金币 += chr.getEquippedFuMoMap().get(23);
                                }
                            }
                            if (chr.getEquippedFuMoMap().get(26) != null) {
                                if (chr.getEquippedFuMoMap().get(26) > 0) {
                                    加成金币 += 金币 * (chr.getEquippedFuMoMap().get(26) / 100);
                                }
                            }
                            if (金币 > 0) {
                                金币 += 加成金币;
                                金币 = 金币 * 泡点倍率(chr.getWorldId());
                                chr.gainMeso(金币, true);

                                name.append(" 金币 + ").append(金币);
                            }
                            int 点券 = chr.get泡点点券();
                            int 加成点券 = 0;
                            if (chr.getEquippedFuMoMap().get(25) != null) {
                                if (chr.getEquippedFuMoMap().get(25) > 0) {
                                    点券 += chr.getEquippedFuMoMap().get(25);
                                }
                            }
                            if (chr.getEquippedFuMoMap().get(26) != null) {
                                if (chr.getEquippedFuMoMap().get(26) > 0) {
                                    加成点券 += 点券 * (chr.getEquippedFuMoMap().get(26) / 100);
                                }
                            }
                            if (点券 > 0) {
                                点券 += 加成点券;
                                点券 = 点券 * 泡点倍率(chr.getWorldId());
                                chr.getCashShop().gainCash(1, 点券);
                                name.append(" 点券 + ").append(点券);
                            }
                            if (经验 > 0 || 金币 > 0 || 点券 > 0) {
                                //泡点时间记录
                                chr.增加角色泡点经验();
                                chr.dropMessage(5, "[福利泡点] " + name.toString());
                            }
                        });
                    });
                });
            }
        }, 1000 * 60 * 6);
    }

    /**
     * <数据表>
     */
    public static void GetConfigValues() {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT name, val FROM ConfigValues")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String name = rs.getString("name");
                        int val = rs.getInt("val");
                        ConfigValuesMap.put(name, val);
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

    public static void GetFuMoInfo() {
        FuMoInfoMap.clear();
        //System.out.println("○ 开始加载附魔装备效果");
        //重载//
        Connection con = Start.getInstance().getConnection();
        try {
            try (PreparedStatement ps = con.prepareStatement("SELECT fumoType, fumoName, fumoInfo FROM mxmxd_fumo_info")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int fumoType = rs.getInt("fumoType");
                        String fumoName = rs.getString("fumoName");
                        String fumoInfo = rs.getString("fumoInfo");
                        FuMoInfoMap.put(fumoType, new String[]{fumoName, fumoInfo});
                    }
                }
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

    public static void 加载机器人管理() {
        机器人管理员.clear();
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM `qqgm`");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    机器人管理员.put(rs.getString("QQ"), 1);
                    printLoad("加载机器人管理员 QQ" + rs.getString("QQ") + "");
                }
                rs.close();
            }
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
    }

    public static void 加载大区游戏群() {
        大区群号1.clear();
        大区群号2.clear();
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM `mxmxd_qq_qun`");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    大区群号1.put(rs.getInt("id"), rs.getString("qqGun"));
                    大区群号2.put(rs.getString("qqGun"), rs.getInt("id"));
                    printLoad("加载游戏分区群号 " + 大区(rs.getInt("id")) + " " + rs.getString("qqGun") + "");
                }
                rs.close();
            }
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
    }

    public static String 大区群号1(int a) {
        if (大区群号1.containsKey(a)) {
            return 大区群号1.get(a);
        }
        return "";
    }

    public static int 大区群号2(String a) {
        if (大区群号2.containsKey(a)) {
            return 大区群号2.get(a);
        }
        return 0;
    }

    public static int 地图是否通行(int a) {
        if (地图通行.containsKey(a)) {
            return 地图通行.get(a);
        }
        return 0;
    }

    public static void 加载杀怪数量() {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM `characters_kmob`");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    杀怪任务计数.put(rs.getInt("id"), rs.getInt("mobshuliang"));
                }
                rs.close();
            }
            String sqlstr = " Delete from characters_kmob";
            ps.executeUpdate(sqlstr);
            ps.close();
        } catch (SQLException ex) {
        } finally {
            Database.cleanUP(null, null, con);
        }
    }

    public static void Gui() {
        if (zevms != null) {
            zevms.dispose();
        }
        zevms = new zevms();
        zevms.setVisible(true);
    }

    private static void printLoad(String thread) {
        System.out.println(thread);
    }

    public static Start getInstance() {
        if (instance == null) {
            instance = new Start();
        }
        return instance;
    }

    public static void main(String[] args) {
        getInstance().run();
    }

    public static class Shutdown implements Runnable {

        @Override
        public void run() {
            ShutdownServer.getInstance().run();
        }
    }

    public Connection getConnection() {
        if (db == null) {
            db = Database.createDatabase();
        }
        return db.get();
    }

    public List<World> getWorlds() {
        return worldList;
    }

    public World getWorldById(int world) {
        return getWorlds().stream().filter(w -> w.getWorldId() == world).findAny().orElse(null);
    }

    public static void 天梯积分排行() throws Exception {
        int 名次 = 1;
        StringBuilder name = new StringBuilder();
        Connection con2 = null;
        try {
            con2 = Start.getInstance().getConnection();
            try (PreparedStatement ps2 = con2.prepareStatement("SELECT * FROM characters  WHERE gm = 0  && job !=500 && integral >= 100  && banned = 0 order by (integral*100000+totalOnlineTimett) desc  LIMIT 10")) {
                ResultSet rs2 = ps2.executeQuery();
                name.append("*-- 天梯赛排行榜 ------------------------------*\r\n");
                while (rs2.next()) {
                    String 玩家名字 = rs2.getString("name");
                    int 在线 = rs2.getInt("integral");
                    int x = rs2.getInt("world");
                    String 排名显示 = String.valueOf(在线);
                    if (名次 <= 9) {
                        name.append("第 0").append(名次).append(" 名");
                        name.append("  积分").append(排名显示);
                        for (int j = 6 - 排名显示.getBytes().length; j > 0; j--) {
                            name.append(" ");
                        }
                        name.append(" [").append(大区(x)).append("] ").append(玩家名字).append("\r\n");
                    } else {
                        name.append("第 ").append(名次).append(" 名");
                        name.append("  积分").append(排名显示);
                        for (int j = 6 - 排名显示.getBytes().length; j > 0; j--) {
                            name.append(" ");
                        }
                        name.append(" [").append(大区(x)).append("] ").append(玩家名字).append("\r\n");
                    }
                    名次++;
                }
                rs2.close();
            }
        } catch (SQLException ex) {
            System.err.println("天梯积分排行、出错" + ex.getMessage());
        } finally {
            try {
                if (con2 != null && !con2.isClosed()) {
                    con2.close();
                }
            } catch (SQLException e) {
            }
        }
        createTianTiPaiHangBang(name.toString());
        输出天梯排行榜到所有群();
    }

    public static void 详细通知() {
        StringBuilder sb = new StringBuilder();
        int b = 0;
        int z = 0;
        for (int i = 0; i < ServerProperties.World.COUNT; i++) {
            int a = 0;
            int yl = 本月盈利(i);
            for (ChannelServer cserv : Start.getInstance().getWorldById(i).getChannels()) {
                a += cserv.getConnectedClients();
                b += cserv.getConnectedClients();
            }
            if (a > 0) {
                sb.append(String.format("[" + 大区(i) + "] 在线人数:" + a + "\r\n"));
            }
            if (yl > 0) {
                sb.append(String.format("[" + 大区(i) + "] 本月盈利:" + yl + "\r\n"));
                z += yl;
            }
        }
        sendMsgToQQGroup("自由冒险岛Ver.027 - " + Calendar.getInstance().get(Calendar.DAY_OF_WEEK_IN_MONTH) + "月\r\n" + sb + "\r\n总在线:" + b + "\r\n总盈利:" + z + "", 合伙群);
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

    public static int 本月盈利(int id) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM accountsrecharge WHERE World = ? && b = ?");
            ps.setInt(1, id);
            ps.setInt(2, Calendar.getInstance().get(Calendar.DAY_OF_WEEK_IN_MONTH));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data += rs.getInt("xianjin");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("本月盈利、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
        return (int) (data * 0.97);
    }

}
