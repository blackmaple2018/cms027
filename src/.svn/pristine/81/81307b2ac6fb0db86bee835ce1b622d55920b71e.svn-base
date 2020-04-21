package launch;

import handling.channel.ChannelServer;
import handling.world.World;
import java.awt.Point;
import java.util.LinkedHashMap;
import java.util.Map;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.maps.Field;
import tools.TimerTools.WorldTimer;

public class FieldBoss {

    public static Map<Integer, Integer> 红蜗牛长老 = new LinkedHashMap<>();

    public static void 世界BOSS线程() {
        WorldTimer.getInstance().register(new Runnable() {
            @Override
            public void run() {
                召唤怪物();
            }
        }, 1000 * 60 * 1);
    }

    public static void 召唤怪物() {
        for (World world : Start.getInstance().getWorlds()) {
            for (ChannelServer cs : world.getChannels()) {
                int mob = 2220000;
                int hp = 100 * 10000;
                int exp = 100 * 10000;
                int level = 100;
                Field mapleMap = cs.getMapFactory().getMap(104000400);
                if (mapleMap.monsterCountById(mob) == 0) {
                    MapleMonster mapleMonster = MapleLifeFactory.getMonster(mob);
                    mapleMonster.setPosition(new Point(301, -85));
                    mapleMonster.getStats().setLevel(level);
                    mapleMonster.getStats().setHp(hp);
                    mapleMonster.getStats().setExp(exp);
                    mapleMap.spawnMonsterOnGroundBelow(mapleMonster, mapleMonster.getPosition());
                }
            }
        }
    }
}
