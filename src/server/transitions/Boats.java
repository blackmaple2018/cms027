package server.transitions;

import java.awt.Point;
import handling.channel.ChannelServer;
import packet.creators.EffectPackets;
import packet.creators.PacketCreator;
import server.PropertiesTable;
import server.life.MapleLifeFactory;
import server.maps.Field;
import tools.TimerTools.MapTimer;

/**
 * @author GabrielSin
 */
public class Boats {

    public long 航行倒计时时间 = 0;
    //关闭
    public long 等待时间 = 1000 * 60 * 3;
    //开始
    public long 开船时间 = 1000 * 60 * 3;
    //航行时间
    public long 航行时间 = 1000 * 60 * 10;
    //蝙蝠魔入侵
    public long 蝙蝠魔入侵 = 1000 * 60 * 60;
    public static PropertiesTable prop = new PropertiesTable();
    public Field 开往魔法密林候船室, 开往天空之城, 开往天空之城船仓, 天空之城码头, 开往魔法密林, 开往天空之城候船室, 开往魔法密林船仓, 魔法密林码头, 天空之城售票处;
    private final ChannelServer channel;

    public Boats(ChannelServer channel) {
        this.channel = channel;
    }

    public void Start() {
        //候船室&lt;开往魔法密林
        开往魔法密林候船室 = channel.getMapFactory().getMap(200000112);
        //候船室&lt;开往天空之城
        开往天空之城候船室 = channel.getMapFactory().getMap(101000301);
        //开往天空之城
        开往天空之城 = channel.getMapFactory().getMap(200090010);
        //开往魔法密林
        开往魔法密林 = channel.getMapFactory().getMap(200090000);
        //船仓&lt;开往天空之城
        开往天空之城船仓 = channel.getMapFactory().getMap(200090011);
        //船仓&lt;开往魔法密林
        开往魔法密林船仓 = channel.getMapFactory().getMap(200090001);
        //魔法密林码头
        魔法密林码头 = channel.getMapFactory().getMap(101000300);
        //天空之城售票处
        天空之城售票处 = channel.getMapFactory().getMap(200000100);
        //码头&lt;开往魔法密林
        天空之城码头 = channel.getMapFactory().getMap(200000111);
        setPortalOrbis();
        setPortalEllinia();
        scheduleNew();
    }

    /**
     * <计划新建>
     */
    public final void scheduleNew() {
        魔法密林码头.setDocked(true);
        天空之城码头.setDocked(true);
        魔法密林码头.broadcastMessage(PacketCreator.ShipEffect(true));
        天空之城码头.broadcastMessage(PacketCreator.ShipEffect(true));
        prop.setProperty("docked", Boolean.TRUE);
        prop.setProperty("entry", Boolean.TRUE);
        prop.setProperty("haveBalrog", Boolean.FALSE);
        MapTimer.getInstance().schedule(() -> {
            stopEntry();
        }, 等待时间);
        MapTimer.getInstance().schedule(() -> {
            takeOff();
        }, 开船时间);
    }

    public void stopEntry() {
        prop.setProperty("entry", Boolean.FALSE);
        开往天空之城船仓.resetReactors();
        开往魔法密林船仓.resetReactors();
    }

    public void takeOff() {
        prop.setProperty("docked", Boolean.FALSE);
        开往魔法密林候船室.warpEveryone(开往魔法密林.getId());
        开往天空之城候船室.warpEveryone(开往天空之城.getId());
        魔法密林码头.setDocked(false);
        天空之城码头.setDocked(false);
        魔法密林码头.broadcastMessage(PacketCreator.ShipEffect(false));
        天空之城码头.broadcastMessage(PacketCreator.ShipEffect(false));
        航行倒计时时间 = 10;
        MapTimer.getInstance().schedule(() -> {
            invasionBalrog();
        }, 蝙蝠魔入侵);
        MapTimer.getInstance().schedule(() -> {
            arrived();
        }, 航行时间);
        MapTimer.getInstance().schedule(() -> {
            提醒();
        }, 航行时间 / (1000 * 60 * 1));
    }

    public void 提醒() {
        开往天空之城.dropMessage(5, "距离抵达天空之城还剩下 " + 航行倒计时时间 + " 分钟。");
        开往天空之城船仓.dropMessage(5, "距离抵达天空之城还剩下 " + 航行倒计时时间 + " 分钟。");
        开往魔法密林.dropMessage(5, "距离抵达魔法密林还剩下 " + 航行倒计时时间 + " 分钟。");
        开往魔法密林船仓.dropMessage(5, "距离抵达魔法密林还剩下 " + 航行倒计时时间 + " 分钟。");
        航行倒计时时间 -= 1;
    }

    public void arrived() {
        String x = "每日任务_坐密林和天空的航班";
        开往天空之城.setmapbosslog(x);
        开往天空之城船仓.setmapbosslog(x);
        开往魔法密林.setmapbosslog(x);
        开往魔法密林船仓.setmapbosslog(x);
        开往天空之城.warpEveryone2(天空之城码头.getId());
        开往天空之城船仓.warpEveryone2(天空之城码头.getId());
        //p.changeMap(to, to.getPortal(0));
        开往魔法密林.warpEveryone2(魔法密林码头.getId());
        开往魔法密林船仓.warpEveryone2(魔法密林码头.getId());
        开往天空之城.killAllMonsters();
        开往魔法密林.killAllMonsters();
        scheduleNew();
    }

    /**
     * <蝙蝠魔>
     */
    public void invasionBalrog() {
        int numberSpawns = 10;
        if (numberSpawns > 0) {
            for (int i = 0; i < numberSpawns; i++) {
                //蝙蝠魔
                开往天空之城.spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(8150000), new Point(485, -221));
                开往魔法密林.spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(8150000), new Point(-590, -221));
            }
            开往天空之城.broadcastMessage(EffectPackets.ShowEffect("AbyssShadow"));
            开往魔法密林.broadcastMessage(EffectPackets.ShowEffect("AbyssShadow"));
            开往天空之城.setmapbosslog("每日任务_航行遭遇蝙蝠魔袭击");
            开往魔法密林.setmapbosslog("每日任务_航行遭遇蝙蝠魔袭击");
            开往天空之城船仓.setmapbosslog("每日任务_航行遭遇蝙蝠魔袭击");
            开往魔法密林船仓.setmapbosslog("每日任务_航行遭遇蝙蝠魔袭击");
            开往天空之城.setDocked(true);
            开往魔法密林.setDocked(true);
            开往天空之城.broadcastMessage(PacketCreator.MonsterBoat(true));
            开往魔法密林.broadcastMessage(PacketCreator.MonsterBoat(true));
            开往天空之城.broadcastMessage(EffectPackets.MusicChange("Bgm04/ArabPirate"));
            开往魔法密林.broadcastMessage(EffectPackets.MusicChange("Bgm04/ArabPirate"));
            开往天空之城.dropMessage(5, "蝙蝠魔偷袭了飞船。");
            开往天空之城船仓.dropMessage(5, "蝙蝠魔偷袭了飞船。");
            开往魔法密林.dropMessage(5, "蝙蝠魔偷袭了飞船。");
            开往魔法密林船仓.dropMessage(5, "蝙蝠魔偷袭了飞船。");
            prop.setProperty("haveBalrog", Boolean.TRUE);

        }
    }

    /**
     * <船仓&lt;开往天空之城>
     */
    public final void setPortalOrbis() {

        for (ChannelServer cserv : channel.getWorld().getChannels()) {
            cserv.getMapFactory().getMap(200090011).getPortal("out00").setScriptName("OBoat1");
            cserv.getMapFactory().getMap(200090011).getPortal("out01").setScriptName("OBoat2");
        }
    }

    /**
     * <船仓&lt;开往魔法密林>
     */
    public final void setPortalEllinia() {
        for (ChannelServer cserv : channel.getWorld().getChannels()) {
            cserv.getMapFactory().getMap(200090001).getPortal("out00").setScriptName("EBoat1");
            cserv.getMapFactory().getMap(200090001).getPortal("out01").setScriptName("EBoat2");
        }
    }

    public static PropertiesTable getProperties() {
        return prop;
    }

    public static boolean boatOpen() {
        return getProperties().getProperty("entry").equals(Boolean.TRUE);
    }

    public static boolean hasBalrog() {
        return getProperties().getProperty("haveBalrog").equals(Boolean.TRUE);
    }
}
