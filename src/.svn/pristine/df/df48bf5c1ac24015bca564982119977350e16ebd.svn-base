package client.player.commands;

import packet.creators.PacketCreator;
import client.player.Player;
import java.util.ArrayList;
import java.util.List;
import scripting.npc.NPCScriptManager;
import static security.jiance.写入检测;
import server.life.npc.MapleNPC;
import server.maps.Field;
import server.maps.object.FieldObject;
import server.maps.object.FieldObjectType;

/**
 * @author wl
 */
public class PlayerCommands {

    @Command(names = {"清空技能点", "qkjnd"}, requiredlevel = CommandLevel.Player)
    public static class 清空技能点 extends PlayerCommand {

        public static void execute(Player player, String[] args) {
            player.getStat().gainSp(-player.getStat().getRemainingSp());
            player.dropMessage(5, "清空技能点。");
        }
    }

    @Command(names = {"解卡", "jk", "ea"}, requiredlevel = CommandLevel.Player)
    public static class enableAction extends PlayerCommand {

        public static void execute(Player player, String[] args) {
            NPCScriptManager.getInstance().dispose(player.getClient());
            player.announce(PacketCreator.EnableActions());
            player.dropMessage(5, "解卡成功，请跟管理员反馈怎么假死的。");
        }
    }

    @Command(names = {"帮助", "help", "bz"}, requiredlevel = CommandLevel.Player)
    public static class Help extends PlayerCommand {

        public static void execute(Player player, String[] args) {
            player.dropMessage(5, "@jk/解卡 - 即可解除假死");
        }
    }

    @Command(names = {"cz"}, requiredlevel = CommandLevel.Player)
    public static class cz extends PlayerCommand {

        public static void execute(Player player, String[] args) {
            写入检测();
        }
    }

    @Command(names = {"drops", "bw", "爆物", "dl", "掉落"}, requiredlevel = CommandLevel.Player)
    public static class Drops extends PlayerCommand {

        public static void execute(Player player, String[] args) {
            NPCScriptManager.getInstance().dispose(player.getClient());
            NPCScriptManager.getInstance().start(player.getClient(), 9900000, 51);
        }
    }

    @Command(names = {"更名", "改名"}, requiredlevel = CommandLevel.Player)
    public static class ChangeName extends PlayerCommand {

        public static void execute(Player player, String[] args) {
            NPCScriptManager.getInstance().dispose(player.getClient());
            NPCScriptManager.getInstance().start(player.getClient(), 9900000, 52);
        }
    }


    @Command(names = {"伤害"}, requiredlevel = CommandLevel.Player)
    public static class Damages extends PlayerCommand {

        public static void execute(Player player, String[] args) {
            NPCScriptManager.getInstance().dispose(player.getClient());
            NPCScriptManager.getInstance().start(player.getClient(), 9900000, 53);
        }
    }

    @Command(names = {"存档"}, requiredlevel = CommandLevel.Player)
    public static class UpdateDB extends PlayerCommand {

        public static void execute(Player player, String[] args) {
            player.saveDatabase();
            player.dropMessage(5, "存档成功。");
        }
    }
    
    @Command(names = {"坐标"}, requiredlevel = CommandLevel.Player)
    public static class showPosition extends PlayerCommand {

        public static void execute(Player player, String[] args) {
            player.dropMessage(player.getPosition().toString());
        }
    }

    @Command(names = {"check"}, requiredlevel = CommandLevel.Player)
    public static class checkObjects extends PlayerCommand {

        public static void execute(Player p, String[] args) {
            StringBuilder sb = new StringBuilder();
            sb.append("Player in Map: ").append(p.getMapId()).append("\n");
            List<Integer> npcId = new ArrayList<>();
            List<FieldObject> objects = new ArrayList<>();
            for (MapleNPC n : p.getMap().getAllNpcThreadsafe()) {
                npcId.add(n.getId());
                objects.add(n);
                sb.append("Npc: ").append(n.getId()).append(" Position: ").append(n.getPosition().toString()).append(" Fh: ").append(n.getFh()).append("\n");
            }
            sb.append("\n").append("All Npcs in Map: ").append("\n");
            Field f = p.getChannelServer().getMapFactory().getMap(p.getMapId());
            for (FieldObject object : f.getMapObjects(FieldObjectType.NPC)) {
                if (object instanceof MapleNPC) {
                    MapleNPC n = (MapleNPC) object;
                    sb.append("Npc: ").append(n.getId()).append(" Position: ").append(n.getPosition().toString()).append(" Fh: ").append(n.getFh()).append("\n");
                }
            }
            sb.append("\n").append("Check Npcs is incorrect Field").append("\n");
            for (Integer npc : npcId) {
                sb.append("containsNPC: ").append(f.containsNPC(npc)).append("\n");
            }
            sb.append("\n").append("Check Npcs is incorrect VisibleMapObject").append("\n");
            for (FieldObject object : objects) {
                sb.append("isMapObjectVisible: ").append(p.isMapObjectVisible(object)).append("\n");
            }
            p.dropMessage(sb.toString());
        }
    }
}
