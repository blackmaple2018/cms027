package client.player.commands;

import client.player.Player;
import client.player.PlayerJob;
import client.player.PlayerStat;
import handling.world.service.BroadcastService;
import packet.creators.PacketCreator;
import server.maps.Field;
import server.maps.portal.Portal;
import tools.TimerTools.WorldTimer;

/**
 * @author wl
 */
public class GMCommands {

    @Command(names = {"等级", "level"}, requiredlevel = CommandLevel.GameMaster)
    public static class Level extends GMCommand {

        public static void execute(Player player, String[] args) {
            player.setLevel(Short.parseShort(args[1]));
            player.levelUp(false);
            player.setExp(0);
            player.getStat().updateSingleStat(PlayerStat.EXP, 0);
        }
    }

    @Command(names = {"升级", "levelup", "sj"}, requiredlevel = CommandLevel.GameMaster)
    public static class LevelUp extends GMCommand {

        public static void execute(Player player, String[] args) {
            player.levelUp(true);
            player.setExp(0);
            player.getStat().updateSingleStat(PlayerStat.EXP, 0);
        }
    }

    @Command(names = {"转职", "zz", "job"}, requiredlevel = CommandLevel.GameMaster)
    public static class Job extends GMCommand {

        public static void execute(Player player, String[] args) {
            int jobId = Integer.parseInt(args[1]);
            if (PlayerJob.getById(jobId) != null) {
                player.changeJob(PlayerJob.getById(jobId));
            } else {
                player.dropMessage("不存在的职业.");
            }
        }
    }

    @Command(names = {"传送", "cs", "warp"}, requiredlevel = CommandLevel.GameMaster)
    public static class Warp extends GMCommand {

        public static void execute(Player player, String[] args) {
            try {
                Field target = player.getChannelServer().getMapFactory().getMap(Integer.parseInt(args[1]));
                if (target == null) {
                    player.dropMessage(1, "地图不存在。");
                    return;
                }
                if (player.getMapId() >= 910000000 && player.getMapId() <= 910000022) {
                    player.dropMessage(1, "市场不允许传送到其他地图。");
                    return;
                }
                if (player.getMapId() < 910000000 || player.getMapId() > 910000022) {
                    if (Integer.parseInt(args[1]) >= 910000000 && Integer.parseInt(args[1]) <= 910000022) {
                        player.dropMessage(1, "不允许传送到自由市场地图。");
                        return;
                    }
                }
                Portal targetPortal = null;
                if (args.length > 2) {
                    try {
                        targetPortal = target.getPortal(Integer.parseInt(args[2]));
                    } catch (IndexOutOfBoundsException e) {
                        player.dropMessage(5, "传送点错误.");
                    } catch (NumberFormatException a) {
                    }
                }
                if (targetPortal == null) {
                    targetPortal = target.getPortal(0);
                }
                player.changeMap(target, targetPortal);
            } catch (Exception e) {
                player.dropMessage(5, "Error: " + e.getMessage());
            }
        }
    }

    @Command(names = {"封号", "ban"}, requiredlevel = CommandLevel.GameMaster)
    public static class Ban extends GMCommand {

        public static void execute(Player player, String[] args) {
            if (args.length < 2) {
                player.dropMessage("参数: !封号 <角色名称> <封号原因>");
                return;
            }
            String name = args[1];
            String reason = args[2];

            Player target = player.getWorld().getCharByName(name);
            if (target != null) {
                target.bans(reason);
                target.dropMessage(1, String.format("你因违反游戏规定：%s，被管理员：%s执行封号处理，如有疑问请立即联系在线管理。", reason, player.getName()));
                BroadcastService.broadcastMessage(player.getWorldId(), PacketCreator.ServerMessage(6, "[封号]: 玩家 " + target.getName() + " 因 " + reason + " 违反游戏规定，被封号处理。"));
                WorldTimer.getInstance().schedule(() -> {
                    target.getClient().disconnect(false, false);
                }, 5000); 
            }
        }
    }
}
