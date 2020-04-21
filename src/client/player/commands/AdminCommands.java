package client.player.commands;

import client.ClientLoginState;
import handling.channel.ChannelServer;
import handling.world.service.BroadcastService;
import handling.world.service.FindService;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import packet.creators.PacketCreator;
import client.player.Player;
import client.player.inventory.Equip;
import client.player.inventory.Item;
import client.player.inventory.types.InventoryType;
import constants.ItemConstants;
import launch.Start;
import scripting.portal.PortalScriptManager;
import server.itens.ItemInformationProvider;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleMonsterInformationProvider;
import server.life.npc.MapleNPC;
import server.maps.Field;
import tools.TimerTools;

/**
 * @author wl
 */
public class AdminCommands {

    @Command(names = {"召唤扎昆"}, requiredlevel = CommandLevel.Admin)
    public static class SpawnZakum extends AdminCommand {

       public static void execute(Player player, String[] args) {
            player.getMap().spawnZakum(player.getPosition());
        }
    }
    
    @Command(names = {"召唤怪物", "zhgw"}, requiredlevel = CommandLevel.Admin)
    public static class Spawn extends AdminCommand {

        public static void execute(Player player, String[] args) {
            if (args.length < 2) {
                player.yellowMessage("*召唤怪物 <mobid>");
                return;
            }
            MapleMonster monster = MapleLifeFactory.getMonster(Integer.parseInt(args[1]));
            if (monster == null) {
                player.yellowMessage("怪物不存在。");
                return;
            }
            if (args.length > 2) {
                for (int i = 0; i < Integer.parseInt(args[2]); i++) {
                    player.getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(Integer.parseInt(args[1])), player.getPosition());
                }
            } else {
                player.getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(Integer.parseInt(args[1])), player.getPosition());
            }
        }
    }

    @Command(names = {"丢", "d", "drop"}, requiredlevel = CommandLevel.Admin)
    public static class Drop extends AdminCommand {

        public static void execute(Player player, String[] args) {
            final int itemId = Integer.parseInt(args[1]);
            final short quantity = Short.parseShort(args[2]);
            ItemInformationProvider ii = ItemInformationProvider.getInstance();
            if (ItemConstants.isPet(itemId)) {
                player.dropMessage(5, "请从现金商店购买宠物。");
            } else if (!ii.itemExists(itemId)) {
                player.dropMessage(5, itemId + " 物品不存在。");
            } else {
                Item toDrop;
                if (ItemConstants.getInventoryType(itemId) == InventoryType.EQUIP) {
                    toDrop = ii.randomizeStats((Equip) ii.getEquipById(itemId));
                } else {
                    toDrop = new Item(itemId, (byte) 0, (short) quantity, (byte) 0);
                }
                player.getMap().spawnItemDrop(player, player, toDrop, player.getPosition(), true, true);
            }
        }
    }

    @Command(names = {"关闭服务器", "shutdown", "维护"}, requiredlevel = CommandLevel.Admin)
    public static class ShutdownServer extends AdminCommand {

        public static void execute(Player player, String[] args) {
            if (args.length < 1) {
                player.dropMessage("参数: *维护 <时间: 分钟> (例：!维护 30)");
                return;
            }
            int minutes = Integer.parseInt(args[1]);
            BroadcastService.broadcastMessage(player.getWorldId(), PacketCreator.ServerNotice(0, "服务器将在 " + minutes + " 分钟后关闭，请各位玩家安全下线"));
            TimerTools.WorldTimer.getInstance().schedule(() -> {
                System.exit(0);
            }, minutes * 60 * 1000);
        }
    }

    @Command(names = {"坐标", "pos"}, requiredlevel = CommandLevel.Admin)
    public static class Position extends AdminCommand {

        public static void execute(Player player, String[] args) {
            float xpos = player.getPosition().x;
            float ypos = player.getPosition().y;
            float fh = player.getMap().getFootholds().findBelow(player.getPosition()).getId();
            player.dropMessage(6, "Position: (" + xpos + ", " + ypos + ")");
            player.dropMessage(6, "Foothold ID: " + fh);
        }
    }

    @Command(names = {"重载掉落", "reloadDrops"}, requiredlevel = CommandLevel.Admin)
    public static class ReloadDrops extends AdminCommand {

        public static void execute(Player player, String[] args) {
            MapleMonsterInformationProvider.getInstance().clearDrops();
            player.dropMessage(5, "重载完成.");
        }
    }

    @Command(names = {"重载传送口", "reloadPortals"}, requiredlevel = CommandLevel.Admin)
    public static class ReloadPortals extends AdminCommand {

        public static void execute(Player player, String[] args) {
            PortalScriptManager.getInstance().clearScripts();
            player.dropMessage(5, "重载完成.");
        }
    }

    @Command(names = {"重载地图", "reloadMaps"}, requiredlevel = CommandLevel.Admin)
    public static class ReloadMaps extends AdminCommand {

        public static void execute(Player player, String[] args) {
            Field oldMap = player.getMap();
            Field newMap = player.getChannelServer().getMapFactory().resetMap(player.getMapId());
            int callerid = player.getId();

            for (Player p : oldMap.getCharactersThreadsafe()) {
                p.changeMap(newMap);
                if (p.getId() != callerid) {
                    p.dropMessage("您因地图重载已被重新传入地图， 给您带来的不便敬请谅解.");
                }
            }
            newMap.Respawn(false);
        }
    }

    @Command(names = {"设置顶部公告", "servermessage"}, requiredlevel = CommandLevel.Admin)
    public static class ServerMessage extends AdminCommand {

        public static void execute(Player player, String[] args) {
            if (args.length < 1) {
                player.dropMessage("参数: *设置顶部公告 <内容>");
                return;
            }
            for (ChannelServer cserv : player.getWorld().getChannels()) {
                cserv.setServerMessage(args[1]);
            }
        }
    }

    @Command(names = {"玩家存档", "saveall"}, requiredlevel = CommandLevel.Admin)
    public static class SaveAll extends AdminCommand {

        public static void execute(Player player, String[] args) {
            try {
                for (ChannelServer ch : player.getWorld().getChannels()) {
                    for (Player chr : ch.getPlayerStorage().getAllCharacters()) {
                        chr.saveDatabase();
                    }
                }
                player.dropMessage("[注意] 存档完毕!");
            } catch (Exception e) {
                player.dropMessage("[注意] 存档出错!");
            }
        }
    }

    @Command(names = {"传送大区玩家到身边", "warpallhere"}, requiredlevel = CommandLevel.Admin)
    public static class WarpAllHere extends AdminCommand {

        public static void execute(Player player, String[] args) {
            List<Player> people = new LinkedList<>();
            for (ChannelServer cs : player.getWorld().getChannels()) {
                for (Player mch : cs.getPlayerStorage().getAllCharacters()) {
                    if (mch.getMapId() != player.getMapId() || mch.getClient().getChannel() != player.getClient().getChannel()) {
                        people.add(mch);
                    }
                }
            }
            String ip = player.getChannelServer().getIP();
            String[] socket = ip.split(":");

            for (Player p : people) {
                if (p.getClient().getChannel() != player.getClient().getChannel()) {
                    try {
                        p.getMap().removePlayer(p);
                        p.getChannelServer().removePlayer(p);
                        p.getClient().updateLoginState(ClientLoginState.LOGIN_SERVER_TRANSITION, player.getClient().getSessionIPAddress());
                        p.getClient().write(PacketCreator.GetChannelChange(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1])));
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(AdminCommands.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                p.changeMap(player.getMap(), player.getMap().getPortal("sp"));
            }
        }
    }

    @Command(names = {"召唤NPC", "npc"}, requiredlevel = CommandLevel.Admin)
    public static class NPC extends AdminCommand {

        public static void execute(Player player, String[] args) {
            int npcId = Integer.parseInt(args[1]);
            MapleNPC npc = MapleLifeFactory.getNPC(npcId);
            if (npc != null && !npc.getName().equals("MISSINGNO")) {
                npc.setPosition(player.getPosition());
                npc.setCy(player.getPosition().y);
                npc.setRx0(player.getPosition().x);
                npc.setRx1(player.getPosition().x);
                npc.setFh(player.getMap().getFootholds().findBelow(player.getPosition()).getId());
                npc.getStats().setCustom(true);
                player.getMap().addMapObject(npc);
                player.getMap().broadcastMessage(PacketCreator.SpawnNPC(npc, true));
            } else {
                player.dropMessage(6, "您输入了一个无效的NPC-ID");
            }
        }
    }

    @Command(names = {"传送", "warp"}, requiredlevel = CommandLevel.Admin)
    public static class Warp extends AdminCommand {

        public static void execute(Player player, String[] args) {
            ChannelServer cserv = player.getChannelServer();
            Player victim = cserv.getPlayerStorage().getCharacterByName(args[1]);
            if (victim != null) {
                if (args.length == 2) {
                    player.changeMap(victim.getMap(), victim.getMap().findClosestPlayerSpawnpoint(victim.getPosition()));
                } else {
                    Field target = cserv.getMapFactory().getMap(Integer.parseInt(args[2]));
                    victim.changeMap(target, target.getPortal(0));
                }
            } else {
                try {
                    victim = player;
                    int ch = FindService.findChannel(args[1], victim.getWorldId());
                    if (ch < 0) {
                        Field target = player.getChannelServer().getMapFactory().getMap(Integer.parseInt(args[1]));
                        player.changeMap(target, target.getPortal(0));
                    } else {
                        victim = cserv.getPlayerStorage().getCharacterByName(args[1]);
                        if (victim.getMapId() != player.getMapId()) {
                            final Field mapp = player.getChannelServer().getMapFactory().getMap(victim.getMapId());
                            player.changeMap(mapp, mapp.getPortal(0));
                        }
                        player.changeChannel(ch);
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    @Command(names = {"传送玩家到指定地图", "warpmapto"}, requiredlevel = CommandLevel.Admin)
    public static class WarpMapTo extends AdminCommand {

        public static void execute(Player player, String[] args) {
            try {
                final Field target = player.getChannelServer().getMapFactory().getMap(Integer.parseInt(args[1]));
                final Field from = player.getMap();
                for (Player p : from.getCharactersThreadsafe()) {
                    p.changeMap(target, target.getPortal(0));
                }
            } catch (NumberFormatException e) {
                player.dropMessage(5, "Error: " + e.getMessage());
            }
        }
    }

    @Command(names = {"传送玩家到身边", "warphere"}, requiredlevel = CommandLevel.Admin)
    public static class WarpHere extends AdminCommand {

        public static void execute(Player player, String[] args) {
            Player victim = player.getChannelServer().getPlayerStorage().getCharacterByName(args[1]);
            if (victim != null) {
                victim.changeMap(player.getMap(), player.getMap().findClosestPlayerSpawnpoint(player.getPosition()));
            } else {
                int ch = FindService.findChannel(args[1], player.getWorldId());
                if (ch < 0) {
                    player.dropMessage(5, "找不到玩家.");
                    return;
                }
                victim = player.getChannelServer().getPlayerStorage().getCharacterByName(args[1]);
                if (victim.getMapId() != player.getMapId()) {
                    final Field map = victim.getClient().getChannelServer().getMapFactory().getMap(player.getMapId());
                    victim.changeMap(map, map.getPortal(0));
                }
                victim.changeChannel(player.getClient().getChannel());
            }
        }
    }

    @Command(names = {"召唤永久NPC", "pnpc"}, requiredlevel = CommandLevel.Admin)
    public static class PNPC extends AdminCommand {

        public static void execute(Player player, String[] args) {
            if (args.length < 1) {
                player.dropMessage(6, "*pnpc <npcid>");
                return;
            }

            int npcId = Integer.parseInt(args[1]);
            MapleNPC npc = MapleLifeFactory.getNPC(npcId);
            if (npc != null && !npc.getName().equals("MISSINGNO")) {
                Connection con = null;
                try {
                    con = Start.getInstance().getConnection();
                    npc.setPosition(player.getPosition());
                    npc.setCy(player.getPosition().y);
                    npc.setRx0(player.getPosition().x + 50);
                    npc.setRx1(player.getPosition().x - 50);
                    npc.setFh(player.getMap().getFootholds().findBelow(player.getPosition()).getId());
                    npc.getStats().setCustom(false);

                    try (PreparedStatement ps = con.prepareStatement("INSERT INTO spawns ( idd, f, fh, cy, rx0, rx1, type, x, y, mid ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )")) {
                        ps.setInt(1, npcId);
                        ps.setInt(2, 0);
                        ps.setInt(3, player.getMap().getFootholds().findBelow(player.getPosition()).getId());
                        ps.setInt(4, player.getPosition().y);
                        ps.setInt(5, player.getPosition().x + 50);
                        ps.setInt(6, player.getPosition().x - 50);
                        ps.setString(7, "n");
                        ps.setInt(8, player.getPosition().x);
                        ps.setInt(9, player.getPosition().y);
                        ps.setInt(10, player.getMapId());
                        ps.executeUpdate();
                        ps.close();
                    }
                    player.getMap().addMapObject(npc);
                    player.getMap().broadcastMessage(PacketCreator.SpawnNPC(npc));
                } catch (SQLException ex) {
                    player.dropMessage(6, "Failed to save NPC to the database");
                } finally {
                    try {
                        if (con != null && !con.isClosed()) {
                            con.close();
                        }
                    } catch (SQLException e) {
                    }
                }
            } else {
                player.dropMessage("You have entered an invalid Npc-Id");
            }
        }
    }

    @Command(names = {"召唤永久怪物", "pmob"}, requiredlevel = CommandLevel.Admin)
    public static class PMOB extends AdminCommand {

        public static void execute(Player player, String[] args) {
            if (args.length < 2) {
                player.dropMessage(6, "*pmob <怪物ID> <复活时间>");
                return;
            }

            int npcId = Integer.parseInt(args[1]);
            int mobTime = Integer.parseInt(args[2]);
            final MapleMonster mob = MapleLifeFactory.getMonster(npcId);
            if (mob != null && !mob.getName().equals("MISSINGNO")) {
                Connection con = null;
                try {
                    con = Start.getInstance().getConnection();
                    mob.setPosition(player.getPosition());
                    mob.setCy(player.getPosition().y);
                    mob.setRx0(player.getPosition().x + 50);
                    mob.setRx1(player.getPosition().x - 50);
                    mob.setFh(player.getMap().getFootholds().findBelow(player.getPosition()).getId());

                    try (PreparedStatement ps = con.prepareStatement("INSERT INTO spawns ( idd, f, fh, cy, rx0, rx1, type, x, y, mid, mobtime ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )")) {
                        ps.setInt(1, npcId);
                        ps.setInt(2, 0);
                        ps.setInt(3, player.getMap().getFootholds().findBelow(player.getPosition()).getId());
                        ps.setInt(4, player.getPosition().y);
                        ps.setInt(5, player.getPosition().x + 50);
                        ps.setInt(6, player.getPosition().x - 50);
                        ps.setString(7, "m");
                        ps.setInt(8, player.getPosition().x);
                        ps.setInt(9, player.getPosition().y);
                        ps.setInt(10, player.getMapId());
                        ps.setInt(11, mobTime);
                        ps.executeUpdate();
                        ps.close();
                    }
                    player.getMap().addMonsterSpawn(mob, mobTime, -1);
                } catch (SQLException ex) {
                    player.dropMessage(6, "无法将NPC保存到数据库");
                } finally {
                    try {
                        if (con != null && !con.isClosed()) {
                            con.close();
                        }
                    } catch (SQLException e) {
                    }
                }
            } else {
                player.dropMessage("您输入了无效的Mob-Id");
            }
        }
    }
}
