package handling.login.handler;

import cashshop.CashShop;
import client.player.Player;
import client.player.PlayerStringUtil;
import client.Client;
import client.ClientLoginState;
import constants.ItemConstants;
import handling.channel.ChannelServer;
import handling.login.LoginTools;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import handling.login.LoginWorker;
import static handling.login.handler.CharLoginHeaders.*;
import packet.transfer.read.InPacket;
import java.util.HashMap;
import java.util.Map;
import packet.creators.LoginPackets;
import client.player.PlayerSkin;
import client.player.inventory.Inventory;
import client.player.inventory.types.InventoryType;
import client.player.inventory.Item;
import static configure.Gamemxd.弹窗标题;
import static console.MsgServer.QQMsgServer.判断QQ是否绑定手机;
import static console.MsgServer.QQMsgServer.群活跃;
import constants.ServerProperties;
import static gui.MySQL.取绑定手机;
import static gui.MySQL.取账号是否可以登录;
import static gui.MySQL.取账号绑定的QQ;
import static gui.MySQL.账号状态;
import static gui.MySQL.账号状态2;
import handling.world.World;
import java.nio.charset.Charset;
import launch.Start;
import static launch.Start.ConfigValuesMap;
import static launch.Start.服务端维护;
import static launch.Start.服务端通行;
import packet.creators.PacketCreator;
import server.itens.ItemInformationProvider;
import tools.KoreanDateUtil;
import tools.TimerTools;

public class CharLoginHandler {

    /**
     * <服务器列表>
     */
    public static final void ServerListRequest(final Client c) {
        c.write(PacketCreator.ServerNotice(1, "" + 弹窗标题 + "\r\n\r\n游戏账号：" + c.getAccountName() + "\r\n账号ID:" + c.getAccountID() + "\r\n绑定QQ:" + c.getqq() + "\r\n\r\n欢迎来到冒险岛世界，在这里你将和曾经的小伙伴一起结伴冒险岛。"));
        Start.getInstance().getWorlds().forEach((world) -> {
            c.announce(LoginPackets.getServerList(world));
        });
        c.announce(LoginPackets.getEndOfServerList());
    }

    /**
     * <频道热度显示>
     */
    public static final void ServerStatusRequest(final InPacket packet, final Client c) {
        World world = Start.getInstance().getWorldById(packet.readShort());
        if (world != null) {
            final int userLimit = world.getChannels().size() * 100;
            int onlinePlayers = 0;
            for (ChannelServer cs : world.getChannels()) {
                onlinePlayers += cs.getPlayerStorage().getConnectedClients();
            }
            if (onlinePlayers >= userLimit) {
                c.write(LoginPackets.GetServerStatus(2));
            } else if (onlinePlayers * 2 >= userLimit) {
                c.write(LoginPackets.GetServerStatus(1));
            } else {
                c.write(LoginPackets.GetServerStatus(0));
            }
        }
    }

    public static final void AfterLogin(final InPacket packet, final Client c) {
        byte opOne = packet.readByte();
        byte opTwo = 5;
        if (packet.available() > 0) {
            opTwo = packet.readByte();
        }
        if (opOne == 1 && opTwo == 1) {
            if (c.getPin() == null) {
                c.announce(LoginPackets.RequestPinStatus(CharLoginHeaders.PIN_REGISTER));
            } else {
                c.announce(LoginPackets.RequestPinStatus(CharLoginHeaders.PIN_REQUEST));
            }
        } else if (opOne == 1 && opTwo == 0) {
            //packet.seek(8);
            String pin = packet.readMapleAsciiString();
            if (c.checkPin(pin)) {
                c.announce(LoginPackets.RequestPinStatus(CharLoginHeaders.PIN_ACCEPTED));
            } else {
                c.announce(LoginPackets.RequestPinStatus(CharLoginHeaders.PIN_REJECTED));
            }
        } else if (opOne == 2 && opTwo == 0) {
            //packet.seek(8);
            String pin = packet.readMapleAsciiString();
            if (c.checkPin(pin)) {
                c.announce(LoginPackets.RequestPinStatus(CharLoginHeaders.PIN_REGISTER));
            } else {
                c.announce(LoginPackets.RequestPinStatus(CharLoginHeaders.PIN_REJECTED));
            }
        } else if (opOne == 0 && opTwo == 5) {
            c.updateLoginState(ClientLoginState.LOGIN_NOTLOGGEDIN, c.getSessionIPAddress());
        }
    }

    /**
     * <设置性别>
     */
    public static final void SetGender(final InPacket packet, final Client c) {
        final byte type = packet.readByte();
        if ((type == 0x01) && (c.getGender() == 10)) {
            c.setGender(packet.readByte());
            c.write(LoginPackets.GetAuthSuccess(c));
            final Client client = c;
            c.setIdleTask(TimerTools.ItemTimer.getInstance().schedule(() -> {
                client.close();
            }, 600000));
        }
    }

    /**
     * <注册密码>
     */
    public static final void RegisterPin(final InPacket packet, final Client c) {
        byte operation = packet.readByte();
        if (operation == 0) {
            c.updateLoginState(ClientLoginState.LOGIN_NOTLOGGEDIN, c.getSessionIPAddress());
        } else {
            String pin = packet.readMapleAsciiString();
            if (pin != null) {
                c.setPin(pin);
                c.announce(LoginPackets.PinRegistered());
                c.updateLoginState(ClientLoginState.LOGIN_NOTLOGGEDIN, c.getSessionIPAddress());
            }
        }
    }

    /**
     * <点击频道>
     */
    public static final void CharlistRequest(final InPacket packet, final Client c) {
        if (!c.isLoggedIn()) {
            c.close();
            return;
        }
        final byte worldId = packet.readByte();
        final byte channel = (byte) (packet.readByte() + 1);
        World world = Start.getInstance().getWorldById(worldId);
        List<Player> chars = c.loadCharacters(worldId);
        if (chars != null && world.getChannelById(channel) != null) {
            c.setWorld(worldId);
            c.setChannel(channel);
            c.sendCharList(worldId);
        } else {
            c.close();
        }
    }

    public static final void CharacterSelect(InPacket packet, Client c) {
        final int charId = packet.readInt();
        if (c.hasBannedMac() || c.hasBannedHWID()) {
            c.close();
            return;
        }
        if (c.getIdleTask() != null) {
            c.getIdleTask().cancel(true);
            c.setIdleTask(null);
        }
        c.updateLoginState(ClientLoginState.LOGIN_SERVER_TRANSITION, c.getSessionIPAddress());
        ChannelServer cs = Start.getInstance().getWorldById(c.getWorld()).getChannelById(c.getChannel());
        if (cs != null) {
            cs.addClientInTransfer(c.getChannel(), charId, c);
            c.write(LoginPackets.GetServerIP(cs.getPort(), charId));
        }
    }

    public static final void AcceptToS(InPacket packet, Client c) {
        if (packet.available() == 0 || packet.readByte() != 1 || c.acceptToS()) {
            c.disconnect(false, false);
            return;
        }
        if (c.finishLogin() == 0) {
            c.write(LoginPackets.GetAuthSuccess(c));
        } else {
            c.announce(LoginPackets.GetLoginStatus(LOGIN_ERROR_));
        }
    }

    /**
     * <登陆账号> @param packet
     *
     * @param c
     */
    public static final void Login(InPacket packet, Client c) {
        final String loginID = packet.readMapleAsciiString();
        final String pwd = packet.readMapleAsciiString();

        c.setAccountName(loginID);

        final boolean ipBan = c.hasBannedIP();
        final boolean macBan = c.hasBannedMac();
        final boolean hwidBan = c.hasBannedHWID();
        int loginSucess = c.clientLogin(loginID, pwd);

        //用于解卡角色？
        //c.断开账号(loginID);
        /*if (pwd.equals("fixme") || pwd.equals("jieka")) {
            if (c.getPlayer() != null) {
                CashShop cs = c.getPlayer().getCashShop();
                ChannelServer channel = c.getChannelServer();
                if (cs != null && cs.isOpened() && channel != null) {
                    c.write(PacketCreator.ServerNotice(1, "你的账号已解卡， 请尝试重新登陆。"));
                    channel.removePlayer(c.getPlayer());
                    channel.removeClientFromTransfer(c.getPlayer().getId());
                    c.getPlayer().getClient().close();
                } else {
                    final ClientLoginState state = c.getLoginState();
                    if (state == ClientLoginState.LOGIN_SERVER_TRANSITION || state == ClientLoginState.CHANGE_CHANNEL || state == ClientLoginState.LOGIN_NOTLOGGEDIN) {
                        c.write(PacketCreator.ServerNotice(1, "你的账号已解卡， 请尝试重新登陆。"));
                        c.getPlayer().getClient().close();
                    }
                }
                return;
            }
            for (World w : Start.getInstance().getWorlds()) {
                for (ChannelServer cs : w.getChannels()) {
                    for (Player p : cs.getPlayerStorage().getAllCharacters()) {
                        if (p != null && p.equals(c.getPlayer()) && c.getAccountName().equalsIgnoreCase(loginID)) {
                            c.write(PacketCreator.ServerNotice(1, "你的账号已解卡， 请尝试重新登陆。"));
                            cs.removePlayer(p);
                            cs.removeClientFromTransfer(p.getId());
                            c.close();
                            break;
                        }
                    }
                }
            }
        }*/
        Calendar tempbannedTill = c.getTempBanCalendar();
        if (loginSucess == CharLoginHeaders.LOGIN_OK && (ipBan || macBan) && !c.isGm()) {
            loginSucess = CharLoginHeaders.LOGIN_BLOCKED;
            if (macBan) {
                Player.ban(c.getIP(), "账户封禁 " + loginID, false);
            }
        }
        if (loginSucess == CharLoginHeaders.LOGIN_BLOCKED) {
            c.write(LoginPackets.GetPermBan((byte) 1));
            return;
        }
        if (服务端维护) {
            c.write(LoginPackets.GetLoginStatus(CharLoginHeaders.系统错误));
            c.write(PacketCreator.ServerNotice(1, "" + 弹窗标题 + "\r\n\r\n服务器正在进行维护,无法登陆游戏，详情请关注群内信息。"));
            return;
        }
        //判断QQ绑定下的账号是否封禁
        if (账号状态(取账号绑定的QQ(loginID)) > 0) {
            c.write(LoginPackets.GetLoginStatus(CharLoginHeaders.系统错误));
            c.write(PacketCreator.ServerNotice(1, "" + 弹窗标题 + "\r\n\r\n你的账号由于违反了玩家守则，已经被封禁 - 1。"));
            return;
        }
        //判断手机绑定下的账号是否有封禁
        if (账号状态2(取绑定手机(取账号绑定的QQ(loginID))) > 0) {
            c.write(LoginPackets.GetLoginStatus(CharLoginHeaders.系统错误));
            c.write(PacketCreator.ServerNotice(1, "" + 弹窗标题 + "\r\n\r\n你的账号由于违反了玩家守则，已经被封禁 - 2。"));
            return;
        }

        if (ConfigValuesMap.get("手机登陆") == 0) {
            if (判断QQ是否绑定手机(取账号绑定的QQ(loginID)) == 0) {
                c.write(LoginPackets.GetLoginStatus(CharLoginHeaders.系统错误));
                c.write(PacketCreator.ServerNotice(1, "" + 弹窗标题 + "\r\n\r\n你的游戏账号尚未绑定手机，请私聊机器人进行绑定后，即可正常登陆游戏。"));
                return;
            }
        }
        //判断QQ是否绑定手机
        if (ConfigValuesMap.get("发言登陆") == 0) {
            int dzx = 0;
            for (int i = 0; i < ServerProperties.World.COUNT; i++) {
                if (群活跃(i, 取账号绑定的QQ(loginID)) != null) {
                    dzx++;
                }
            }
            if (dzx == 0) {
                c.write(LoginPackets.GetLoginStatus(CharLoginHeaders.系统错误));
                c.write(PacketCreator.ServerNotice(1, "<自由冒险岛Ver.027>\r\n\r\n你未在官方群里发言y签到或者y任意文字，再尝试登陆，谢谢。"));
                return;
            }
        }
        boolean xz = true;
        if (取账号是否可以登录(loginID) == 0) {
            if (服务端通行) {
                c.write(LoginPackets.GetLoginStatus(CharLoginHeaders.系统错误));
                c.write(PacketCreator.ServerNotice(1, "" + 弹窗标题 + "\r\n\r\n服务器正在进行维护,无法登陆游戏，详情请关注群内信息。"));
                return;
            }
            if (ConfigValuesMap.get("0级账号通行") == 0) {
                xz = false;
            }
        }
        if (取账号是否可以登录(loginID) == 1) {
            if (ConfigValuesMap.get("1级账号通行") == 0) {
                xz = false;
            }
        }
        if (取账号是否可以登录(loginID) == 2) {
            if (ConfigValuesMap.get("2级账号通行") == 0) {
                xz = false;
            }
        }
        if (xz) {
            c.write(LoginPackets.GetLoginStatus(CharLoginHeaders.系统错误));
            c.write(PacketCreator.ServerNotice(1, "" + 弹窗标题 + "\r\n\r\n你的账号目前没有权限登陆游戏。"));
            return;
        }
        if (loginSucess != CharLoginHeaders.LOGIN_OK) {
            c.write(LoginPackets.GetLoginStatus(loginSucess));
        } else if (tempbannedTill.getTimeInMillis() != 0) {
            c.write(LoginPackets.GetTempBan((long) KoreanDateUtil.getTempBanTimestamp(tempbannedTill.getTimeInMillis()), (byte) c.getBanReason()));
        } else {
            LoginWorker.registerClient(c);
        }
    }

    public static final void CheckCharName(String name, Client c) {
        c.write(LoginPackets.CharNameResponse(name, !PlayerStringUtil.canCreateChar(name, c.getWorld())));
    }

    public static final void CreateChar(InPacket packet, Client c) {
        final String name = packet.readMapleAsciiString();
        final int face = packet.readInt();
        final int hair = packet.readInt();
        final int top = packet.readInt();
        final int bottom = packet.readInt();
        final int shoes = packet.readInt();
        final int weapon = packet.readInt();
        final int str = packet.readByte();
        final int dex = packet.readByte();
        final int _int = packet.readByte();
        final int luk = packet.readByte();

        Player newchar = Player.getDefault(c);
        newchar.setWorld(c.getWorld());
        newchar.setWorld2(c.getWorld());
        newchar.setFace(face);
        newchar.setHair(hair + 0);
        newchar.getStat().setStr(str);
        newchar.getStat().setDex(dex);
        newchar.getStat().setInt(_int);
        newchar.getStat().setLuk(luk);
        newchar.getStat().setRemainingAp(9);
        newchar.setName(name, false);
        newchar.setSkinColor(PlayerSkin.getById(0));
        if (c.isGm()) {
            newchar.setGMLevel(c.getGMLevel());
        }

        Inventory equip = newchar.getInventory(InventoryType.EQUIPPED);

        Item equipTop = ItemInformationProvider.getInstance().getEquipById(top);
        equipTop.setPosition(ItemConstants.TOP);
        equip.addFromDB(equipTop);

        Item equipBottom = ItemInformationProvider.getInstance().getEquipById(bottom);
        equipBottom.setPosition(ItemConstants.BOTTOM);
        equip.addFromDB(equipBottom);

        Item equipShoes = ItemInformationProvider.getInstance().getEquipById(shoes);
        equipShoes.setPosition(ItemConstants.SHOES);
        equip.addFromDB(equipShoes);

        Item equipWeapon = ItemInformationProvider.getInstance().getEquipById(weapon);
        equipWeapon.setPosition(ItemConstants.WEAPON);
        equip.addFromDB(equipWeapon);

        //newchar.getInventory(InventoryType.ETC).addItem(new Item(4161001, (byte) 0, (short) 1));
        //newchar.getInventory(InventoryType.ETC).addItem(new Item(4031180, (byte) 0, (short) 1));
        boolean createChar = true;
        /*int checking = 0;
         int[] typeName = {face, hair, hairColor, skinColor, top, bottom, shoes, weapon};
         for (int verfifyName : typeName) {
         if (!LoginTools.checkCharEquip(gender, checking, verfifyName)) {
         createChar = false;
         }
         checking++;
         }*/
        if (PlayerStringUtil.hasSymbols(name) || name.getBytes(Charset.forName("GBK")).length < 4 || name.getBytes(Charset.forName("GBK")).length > 19) {
            createChar = false;
        }
        if (createChar && PlayerStringUtil.canCreateChar(name, c.getWorld()) && !LoginTools.isForbiddenName(name)) {
            newchar.saveNewCharDB(newchar);
            c.write(LoginPackets.AddNewCharEntry(newchar, createChar));
        } else {
            System.out.println(Client.getLogMessage(c, "Trying to create a character with a name: " + name));
        }
    }

    public static void DeleteChar(InPacket packet, Client c) {
        int iDate = packet.readInt();
        int cID = packet.readInt();
        if (!c.isLoggedIn()) {
            return;
        }
        c.announce(LoginPackets.DeleteCharResponse(cID, c.deleteCharacter(cID, iDate)));
    }

    public static void ViewChar(InPacket packet, Client c) {
        Map<Integer, ArrayList<Player>> worlds = new HashMap<>();
        List<Player> chars = c.loadCharacters(0);
        c.announce(LoginPackets.ShowAllCharacter(chars.size()));
        chars.stream().filter((chr) -> (chr != null)).forEach((chr) -> {
            ArrayList<Player> chrr;
            if (!worlds.containsKey(chr.getWorld())) {
                chrr = new ArrayList<>();
                worlds.put(c.getWorld(), chrr);
            } else {
                chrr = worlds.get(chr.getWorld());
            }
            chrr.add(chr);
        });
        worlds.entrySet().forEach((w) -> {
            c.announce(LoginPackets.ShowAllCharacterInfo(w.getKey(), w.getValue()));
        });
    }

    public static void PickCharHandler(InPacket packet, Client c) {
        int charId = packet.readInt();
        int world = packet.readInt();
        c.setWorld(world);
        try {
            c.setChannel(new Random().nextInt(Start.getInstance().getWorldById(world).getChannels().size()));
        } catch (Exception e) {
            c.setChannel(1);
        }
        if (c.getIdleTask() != null) {
            c.getIdleTask().cancel(true);
        }
        c.updateLoginState(ClientLoginState.LOGIN_SERVER_TRANSITION, c.getSessionIPAddress());
        c.write(LoginPackets.GetServerIP(Integer.parseInt(Start.getInstance().getWorldById(c.getWorld()).getChannelById((byte) c.getChannel()).getIP().split(":")[1]), charId));
    }
}
