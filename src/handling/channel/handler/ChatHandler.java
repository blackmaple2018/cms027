package handling.channel.handler;

import client.player.Player;
import client.Client;
import client.player.commands.AdminCommand;
import client.player.commands.AdminCommands;
import client.player.commands.Command;
import client.player.commands.GMCommand;
import client.player.commands.ICommand;
import client.player.commands.PlayerCommand;
import client.player.commands.PlayerCommands;
import static configure.worldworld.说话同步到群里;
import constants.CommandConstants;
import security.violation.CheatingOffense;
import static handling.channel.handler.ChannelHeaders.ChatHeaders.*;
import packet.transfer.read.InPacket;
import handling.world.messenger.MapleMessenger;
import handling.world.messenger.MapleMessengerCharacter;
import handling.world.service.BuddyService;
import handling.world.service.FindService;
import handling.world.service.MessengerService;
import handling.world.service.PartyService;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import launch.Start;
import packet.creators.PacketCreator;
import tools.FileLogger;

/**
 * @author GabrielSin
 */
public class ChatHandler {

    /**
     * <玩家说话> @param packet
     *
     * @param c
     */
    public static void GeneralChat(InPacket packet, Client c) {
        String s = packet.readMapleAsciiString();
        Player p = c.getPlayer();

        String command = s.split(" ")[0].replace(String.valueOf(s.charAt(0)), "");
        Class[] commandClasses = null;

        if (s.startsWith(String.valueOf(CommandConstants.PLAYER_PREFIX))) {
            commandClasses = PlayerCommands.class.getClasses();
        } else if (s.startsWith(String.valueOf(CommandConstants.GM_PREFIX))) {
            commandClasses = GMCommand.class.getClasses();
        } else if (s.startsWith(String.valueOf(CommandConstants.ADMIN_PREFIX))) {
            commandClasses = AdminCommands.class.getClasses();
        }
        if (commandClasses == null) {
            if (s.getBytes(Charset.forName("GBK")).length > Byte.MAX_VALUE && !p.isGameMaster()) {
                CheatingOffense.PACKET_EDIT.cheatingSuspicious(p, p.getName() + " 聊天框编辑.");
                return;
            }
            if (p.getMap().getProperties().getProperty("mute").equals(Boolean.TRUE) && !p.isGameMaster()) {
                p.dropMessage("当前地图不允许聊天。");
            } else {
                if (!p.isHidden()) {
                    p.getMap().broadcastMessage(PacketCreator.GetChatText(p.getId(), s, p.isGameMaster()));
                    if (p.检测() > 0) {
                        if (s.equals(String.valueOf(p.检测()))) {
                            p.检测(0);
                            p.dropMessage("[提示]:恭喜你验证成功，解除限制。");
                        } else {
                            p.dropMessage("[提示]:验证失败，请直接输入 " + p.检测() + " 来解除检测。");

                        }
                    }
                    if (System.currentTimeMillis() - p.群说话冷却() > 10 * 1000) {
                        说话同步到群里(p, s);
                        p.记录群说话冷却();
                    }
                } else {
                    p.getMap().broadcastGMMessage(PacketCreator.GetChatText(p.getId(), s, p.isGameMaster()));
                }
                /*if (p.getMapId() == 105090900 && "我要发".equals(s) && 巨型蝙蝠怪 == 0 && p.haveItem(4006006) && p.getMap().monsterCount() == 0) {
                    if (Randomizer.nextInt(100) < 10) {
                        巨型蝙蝠怪++;
                        p.getMap().broadcastMessage(EffectPackets.ShowEffect("AbyssShadow"));
                        p.getMap().dropMessage(5, "邪恶的魔法石引导出黑暗气息，巨型蝙蝠怪出现。");
                        MapleMonster mob1 = MapleLifeFactory.getMonster(8830000);
                        MapleMonster mob2 = MapleLifeFactory.getMonster(8830001);
                        MapleMonster mob3 = MapleLifeFactory.getMonster(8830002);
                        mob1.setHp(10000000);
                        mob2.setHp(5000000);
                        mob3.setHp(5000000);
                        p.getMap().spawnMonsterOnGroundBelow(mob1, new Point(650, 130));
                        p.getMap().spawnMonsterOnGroundBelow(mob2, new Point(650, 130));
                        p.getMap().spawnMonsterOnGroundBelow(mob3, new Point(650, 130));
                    } else {
                        p.dropMessage(5, "邪恶的魔法石已被黑暗吞噬。");
                    }
                    p.gainItem(4006006, (short) -1, false);
                }*/
            }
        } else {
            for (Class commandClass : commandClasses) {
                Command cmd = (Command) commandClass.getAnnotation(Command.class);
                for (String name : cmd.names()) {
                    if (!name.equalsIgnoreCase(command)) {
                        continue;
                    }
                    if (p.getGM() < cmd.requiredlevel().getLevel()) {
                        continue;
                    }
                    try {
                        ICommand iCommand = null;
                        switch (s.charAt(0)) {
                            case CommandConstants.PLAYER_PREFIX:
                                iCommand = (PlayerCommand) commandClass.getConstructor().newInstance();
                                break;
                            case CommandConstants.GM_PREFIX:
                                iCommand = (GMCommand) commandClass.getConstructor().newInstance();
                                break;
                            case CommandConstants.ADMIN_PREFIX:
                                iCommand = (AdminCommand) commandClass.getConstructor().newInstance();
                                break;
                        }
                        commandClass.getDeclaredMethod("execute", Player.class, String[].class)
                                .invoke(iCommand, p, s.split(" "));

                        saveCommandsDatabase(p, s);
                        //群("GM " + p.getName() + " 指令 " + s + "", "815717096");

                    } catch (IllegalAccessException | IllegalArgumentException
                            | InstantiationException | NoSuchMethodException
                            | SecurityException | InvocationTargetException e) {
                        p.dropMessage("Exception: " + e.getCause().toString());
                    }
                    return;
                }
            }
            p.dropMessage(6, "未知指令 \"" + command + "\"");
        }
    }

    private static void saveCommandsDatabase(Player p, String command) {
        Connection con = Start.getInstance().getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO gmlog (cid, command, mapid) VALUES (?, ?, ?)");
            ps.setInt(1, p.getId());
            ps.setString(2, command);
            ps.setInt(3, p.getMap().getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            FileLogger.printError(FileLogger.DATABASE_EXCEPTION, ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    /**
     * <私聊密语>
     */
    public static void Whisper_Find(InPacket packet, Client c) {
        switch (packet.readByte()) {
            case COMMAND_FIND: {
                String toFind = packet.readMapleAsciiString();
                Player victim = c.getChannelServer().getPlayerStorage().getCharacterByName(toFind);
                if (victim != null) {
                    if (!victim.isGameMaster() || (c.getPlayer().isGameMaster() && victim.isGameMaster())) {
                        if (victim.getCashShop().isOpened()) {
                            c.write(PacketCreator.GetFindReplyWithCS(victim.getName()));
                        } else if (c.getChannel() == victim.getClient().getChannel()) {
                            c.write(PacketCreator.GetFindReplyWithMap(victim.getName(), victim.getMapId()));
                        } else {
                            c.write(PacketCreator.GetFindReply(victim.getName(), (byte) victim.getClient().getChannel()));
                        }
                    } else {
                        c.write(PacketCreator.GetWhisperReply(toFind, (byte) 0));
                    }
                } else {
                    c.write(PacketCreator.GetWhisperReply(toFind, (byte) 0));
                }
                break;
            }
            case COMMAND_WHISPER: {
                String recipient = packet.readMapleAsciiString();
                String message = packet.readMapleAsciiString();
                final int ch = FindService.findChannel(recipient, c.getWorld());
                if (message.length() > 100) {
                    break;
                }
                if (ch > 0) {
                    Player p = c.getPlayer().getWorld().getChannelById(ch).getPlayerStorage().getCharacterByName(recipient);
                    if (p == null) {
                        break;
                    }
                    p.getClient().write(PacketCreator.GetWhisper(c.getPlayer().getName(), c.getChannel(), message));
                    if (!c.getPlayer().isGameMaster() && p.isGameMaster()) {
                        c.write(PacketCreator.GetWhisperReply(recipient, (byte) 0));
                    } else {
                        c.write(PacketCreator.GetWhisperReply(recipient, (byte) 1));
                    }
                } else {
                    c.write(PacketCreator.GetWhisperReply(recipient, (byte) 0));
                }
                break;
            }
        }
    }

    /**
     * <私聊>
     */
    public static void PrivateChat(InPacket packet, Client c) {
        int type = packet.readByte();
        int numRecipients = packet.readByte();
        int recipients[] = new int[numRecipients];

        for (int i = 0; i < numRecipients; i++) {
            recipients[i] = packet.readInt();
        }

        String chatText = packet.readMapleAsciiString();
        Player p = c.getPlayer();
        if (chatText.length() > 100 || p == null) {
            return;
        }
        switch (type) {
            case PRIVATE_CHAT_TYPE_BUDDY:
                BuddyService.buddyChat(p, recipients, p.getId(), p.getName(), chatText);
                break;
            case PRIVATE_CHAT_TYPE_PARTY:
                PartyService.partyChat(p, p.getParty().getId(), chatText, p.getName());
                break;
        }
    }

    public static void Messenger(InPacket packet, Client c) {
        String input;
        if (c.getPlayer() == null) {
            return;
        }
        MapleMessenger messenger = c.getPlayer().getMessenger();

        switch (packet.readByte()) {
            case MESSENGER_OPEN:
                if (messenger == null) {
                    int messengerid = packet.readInt();
                    if (messengerid == 0) {
                        c.getPlayer().setMessenger(MessengerService.createMessenger(new MapleMessengerCharacter(c.getPlayer())));
                    } else {
                        messenger = MessengerService.getMessenger(messengerid);
                        if (messenger != null) {
                            final int position = messenger.getLowestPosition();
                            if (position > -1 && position < 4) {
                                c.getPlayer().setMessenger(messenger);
                                MessengerService.joinMessenger(c.getPlayer(), messenger.getId(), new MapleMessengerCharacter(c.getPlayer()), c.getPlayer().getName(), c.getChannel());
                            }
                        }
                    }
                }
                break;
            case MESSENGER_EXIT:
                if (messenger != null) {
                    final MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(c.getPlayer());
                    MessengerService.leaveMessenger(c.getPlayer(), messenger.getId(), messengerplayer);
                    c.getPlayer().setMessenger(null);
                }
                break;
            case MESSENGER_INVITE:
                if (messenger != null) {
                    final int position = messenger.getLowestPosition();
                    if (position <= -1 || position >= 4) {
                        return;
                    }
                    input = packet.readMapleAsciiString();
                    final Player target = c.getChannelServer().getPlayerStorage().getCharacterByName(input);

                    if (target != null) {
                        if (target.getMessenger() == null) {
                            if (!target.isGameMaster() || c.getPlayer().isGameMaster()) {
                                c.write(PacketCreator.MessengerNote(input, 4, 1));
                                target.getClient().write(PacketCreator.MessengerInvite(c.getPlayer().getName(), messenger.getId()));
                            } else {
                                c.write(PacketCreator.MessengerNote(input, 4, 0));
                            }
                        } else {
                            c.write(PacketCreator.MessengerChat(c.getPlayer().getName() + " : " + target.getName() + " is already using Maple Messenger."));
                        }
                    } else {
                        if (FindService.findChannel(input, c.getWorld()) > 0) {
                            MessengerService.messengerInvite(c.getPlayer(), c.getPlayer().getName(), messenger.getId(), input, c.getChannel(), c.getPlayer().isGameMaster());
                        } else {
                            c.write(PacketCreator.MessengerNote(input, 4, 0));
                        }
                    }
                }
                break;
            case MESSENGER_DECLINE:
                final String targeted = packet.readMapleAsciiString();
                final Player target = c.getChannelServer().getPlayerStorage().getCharacterByName(targeted);
                if (target != null) {
                    if (target.getMessenger() != null) {
                        target.getClient().write(PacketCreator.MessengerNote(c.getPlayer().getName(), 5, 0));
                    }
                } else {
                    if (!c.getPlayer().isGameMaster()) {
                        MessengerService.declineChat(c.getPlayer(), targeted, c.getPlayer().getName());
                    }
                }
                break;
            case MESSENGER_CHAT:
                if (messenger != null) {
                    MessengerService.messengerChat(c.getPlayer(), messenger.getId(), packet.readMapleAsciiString(), c.getPlayer().getName());
                }
                break;
        }
    }

    public static void Spouse_Chat(InPacket packet, Client c) {
        if (c.getPlayer() == null || c.getPlayer().getMap() == null) {
            return;
        }
        String recipient = packet.readMapleAsciiString();
        String message = packet.readMapleAsciiString();
        final int channel = FindService.findChannel(recipient, c.getWorld());
        if (c.getPlayer().getPartnerId() == 0 || !c.getPlayer().getPartner().equalsIgnoreCase(recipient)) {
            c.getPlayer().dropMessage(5, "你没有结婚或者你的配偶离线。");
            c.announce(PacketCreator.EnableActions());
            return;
        }
        if (channel > 0) {
            Player spouseChar = c.getPlayer().getWorld().getChannelById(channel).getPlayerStorage().getCharacterByName(recipient);
            if (spouseChar == null) {
                c.getPlayer().dropMessage(5, "You are not married or your spouse is offline.");
                c.announce(PacketCreator.EnableActions());
                return;
            }
            // TODO: code spouse-chat watch system: if (c.getPlayer().getWatcher() != null) { return; }
            spouseChar.getClient().write(PacketCreator.OnCoupleMessage(c.getPlayer().getName(), message, true));
            c.write(PacketCreator.OnCoupleMessage(c.getPlayer().getName(), message, true));
        } else {
            c.getPlayer().dropMessage(5, "You are not married or your spouse is offline.");
        }
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
