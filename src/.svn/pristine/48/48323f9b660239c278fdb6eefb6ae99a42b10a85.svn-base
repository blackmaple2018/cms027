package handling.channel.handler;

import client.Client;
import client.ClientLoginState;
import client.player.Player;
import client.player.PlayerQuest;
import client.player.inventory.Item;
import client.player.inventory.types.InventoryType;
import community.*;
import static console.MsgServer.QQMsgServer.群活跃;
import constants.ServerProperties;
import static gui.MySQL.取账号绑定的QQ;
import static gui.MySQL.角色取会员时间;
import static gui.MySQL.角色取会员经验;
import static gui.MySQL.账号ID取绑定QQ;
import static gui.MySQL.账号ID取绑定SJ;
import handling.channel.ChannelServer;
import handling.channel.handler.ChannelHeaders.BuddyListHeaders;
import handling.login.handler.CharLoginHeaders;
import packet.transfer.read.InPacket;
import handling.world.CharacterIdChannelPair;
import handling.world.PlayerBuffStorage;
import handling.world.messenger.MapleMessenger;
import handling.world.messenger.MapleMessengerCharacter;
import handling.world.service.*;
import packet.creators.CashShopPackets;
import packet.creators.PacketCreator;
import server.maps.FieldLimit;
import tools.FileLogger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Calendar;
import launch.Start;
import static launch.Start.ConfigValuesMap;
import org.apache.log4j.LogManager;
import packet.creators.LoginPackets;
import scripting.npc.NPCScriptManager;
import server.itens.ItemInformationProvider;
import server.quest.MapleQuestInfoFactory;
import tools.Pair;

public class InterServerHandler {

    /**
     * <切换游戏频道>*
     */
    private static final org.apache.log4j.Logger log = LogManager.getRootLogger();

    public static void ChangeChannel(InPacket packet, Client c) {
        Player p = c.getPlayer();
        if (c == null || p == null) {
            return;
        }
        //10 01 31 EA B2 40 99 1D
        int channel = packet.readByte() + 1;
        if (p.getEventInstance() != null || c.getChannel() == channel || FieldLimit.CHANGECHANNEL.check(p.getMap().getFieldLimit())) {
            c.write(PacketCreator.ServerMigrateFailed((byte) 1));
            return;
        }
        p.changeChannel(channel);
    }

    /**
     * <角色进入游戏>* @param packet
     *
     * @param packet
     * @param c
     */
    public static final void Loggedin(InPacket packet, final Client c) {
        final int cid = packet.readInt();
        Pair<Integer, Client> info = Start.getInstance().getChannelFromTransfer(cid);
        if (info != null) {
            try {

                int channel = info.getLeft();
                Client oldClient = info.getRight();
                int world = oldClient.getWorld();

                ChannelServer cs = Start.getInstance().getWorldById(world).getChannelById(channel);
                cs.removeClientFromTransfer(cid);
                c.setChannel(channel);
                c.setWorld(world);

                Player p = oldClient.getPlayer();
                if (p == null || p.getId() != cid) {
                    try {
                        p = Player.loadinCharacterDatabase(cid, c, true);
                    } catch (SQLException e) {
                        FileLogger.printError(FileLogger.DATABASE_EXCEPTION, "Error player logged-in: " + e);
                    }
                }
                if (p == null) {
                    c.close();
                    return;
                }

                p.setClient(c);
                c.setPlayer(p);
                c.setAccountID(p.getAccountID());
                c.updateLoginState(ClientLoginState.LOGIN_LOGGEDIN, c.getSessionIPAddress());
                c.getChannelServer().addPlayer(p);
                c.write(PacketCreator.GetCharInfo(p));
                p.getWarpMap(p.getMapId()).addPlayer(p);
                if (!p.isHidden()) {
                    p.toggleVisibility(true);
                }

                try {
                    p.silentGiveBuffs(PlayerBuffStorage.getBuffsFromStorage(p.getId()));
                    p.giveSilentDebuff(PlayerBuffStorage.getDiseaseFromStorage(p.getId()));

                    int buddyIds[] = p.getBuddylist().getBuddyIds();
                    BuddyService.loggedOn(p.getWorldId(), p.getName(), p.getId(), c.getChannel(), buddyIds, p.getAdministrativeLevel(), p.isHidden());
                    final CharacterIdChannelPair[] onlineBuddies = FindService.multiBuddyFind(p.getWorldId(), p.getId(), buddyIds);
                    for (CharacterIdChannelPair onlineBuddy : onlineBuddies) {
                        final MapleBuddyListEntry ble = p.getBuddylist().get(onlineBuddy.getCharacterId());
                        ble.setChannel(onlineBuddy.getChannel());
                        p.getBuddylist().put(ble);
                    }

                    c.write(PacketCreator.UpdateBuddylist(BuddyListHeaders.FIRST, p.getBuddylist().getBuddies()));

                    if (p.getParty() != null) {
                        MaplePartyCharacter partyChar = p.getMPC();
                        partyChar.setChannel(c.getChannel());
                        partyChar.setMapId(p.getMapId());
                        partyChar.setOnline(true);
                        p.receivePartyMemberHP();
                        p.updatePartyMemberHP();
                        final MapleParty party = p.getParty();
                        if (party != null) {
                            PartyService.updateParty(p, party.getId(), MaplePartyOperation.LOG_ONOFF, new MaplePartyCharacter(p));
                        }
                    }
                    //召唤宠物
                    p.spawnSavedPets(false, true);
                    //显示小纸条
                    //PlayerNote.showNote(p);
                    p.expirationTask();
                    final MapleMessenger messenger = p.getMessenger();
                    if (messenger != null) {
                        MessengerService.silentJoinMessenger(messenger.getId(), new MapleMessengerCharacter(c.getPlayer()));
                        MessengerService.updateMessenger(c.getPlayer(), messenger.getId(), c.getPlayer().getName(), c.getChannel());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                p.刷新身上装备附魔汇总数据(false);
                p.setQQ(账号ID取绑定QQ(p.getAccountID()));
                p.setSJ(账号ID取绑定SJ(p.getAccountID()));
                p.setTX(群活跃(p.getWorldId(), p.getQQ()));
                if (角色取会员时间(p.getId()) > 0) {
                    p.setvip(角色取会员经验(p.getId()));
                }
                //家族
                p.setjiazu(p.判断家族());
                if (p.getjiazu() > 0) {
                    for (ChannelServer cserv : p.getWorld().getChannels()) {
                        for (Player chr : cserv.getPlayerStorage().getAllCharacters()) {
                            if (chr == null) {
                                continue;
                            }
                            if (chr.getjiazu() == p.getjiazu() && (chr.getName() == null ? p.getName() != null : !chr.getName().equals(p.getName()))) {
                                chr.dropMessage(5, "[提示]:家族成员 " + p.getName() + " 上线了。");
                            }
                        }
                    }
                }
                //加载角色任务信息
                PlayerQuest pq = p.getQuest();
                for (MapleQuestInfoFactory mqif : MapleQuestInfoFactory.getEligibleQuests(p.getLevel(), (short) p.getJob().getId())) {
                    if (pq != null) {
                        pq.addNewQuest(mqif.getQuest(), mqif.getStartData());
                    }
                }

                p.封号状态解除();
                ItemInformationProvider ii = ItemInformationProvider.getInstance();
                int 星期 = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                int 时 = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                p.经验倍率 = 0;
                for (Item item : p.getInventory(InventoryType.ETC)) {
                    switch (item.getItemId()) {
                        //双倍经验卡(1天)
                        case 4100000:
                        //双倍经验卡(7天)
                        case 4100001:
                            //周六, 周日 : 00:00 - 24:00
                            if (星期 == 1 || 星期 == 7) {
                                if (时 >= 0 && 时 <= 24) {
                                    p.经验倍率 = 2;
                                    p.dropMessage(5, "[提示]:" + ii.getName(item.getItemId()) + " 生效");
                                }
                                //周一至周五 : 10:00 - 22:00
                            } else if (时 >= 10 && 时 <= 22) {
                                p.经验倍率 = 2;
                                p.dropMessage(5, "[提示]:" + ii.getName(item.getItemId()) + " 生效");
                            }
                            break;
                        //双倍经验卡(1天白天)
                        case 4100002:
                        //双倍经验卡(7天白天)
                        case 4100003:
                            //周六, 周日 : 00:00 - 24:00
                            if (星期 == 1 || 星期 == 7) {
                                if (时 >= 0 && 时 <= 24) {
                                    p.经验倍率 = 2;
                                    p.dropMessage(5, "[提示]:" + ii.getName(item.getItemId()) + " 生效");
                                }
                                //周一至周五 : 06:00 - 18:00
                            } else if (时 >= 6 && 时 <= 18) {
                                p.经验倍率 = 2;
                                p.dropMessage(5, "[提示]:" + ii.getName(item.getItemId()) + " 生效");
                            }
                            break;
                        //双倍经验卡(1天黑夜)
                        case 4100004:
                        //双倍经验卡(7天黑夜)
                        case 4100005:
                            //周六, 周日 : 00:00 - 24:00
                            if (星期 == 1 || 星期 == 7) {
                                if (时 >= 0 && 时 <= 24) {
                                    p.经验倍率 = 2;
                                    p.dropMessage(5, "[提示]:" + ii.getName(item.getItemId()) + " 生效");
                                }
                                //周一至周五 : 当天18:00 - 次日06:00
                            } else if (时 >= 18 || 时 <= 6) {
                                p.经验倍率 = 2;
                                p.dropMessage(5, "[提示]:" + ii.getName(item.getItemId()) + " 生效");
                            }
                            break;
                    }
                }

                p.记录本次上线在线时间();
                p.同步锻造信息("forgingExp", p.获取锻造信息("forgingExp"));
                if (p.主播() > 0) {
                    if (p.switch_zhubosx() == 0) {
                        BroadcastService.broadcastMessage(Start.getInstance().getWorldById(p.getWorldId()).getWorldId(), PacketCreator.ServerNotice(2, "[上线提醒] : " + p.getName() + " 进入游戏，他/她目前在 " + p.getClient().getChannel() + " 频道 " + c.getPlayer().getMap().getMapName() + " 里面。"));
                    }
                }
                //判断QQ是否绑定手机
                if (!"71447155".equals(p.getQQ())) {
                    if (ConfigValuesMap.get("发言登陆") == 0) {
                        if (群活跃(p.getWorldId(), 账号ID取绑定QQ(p.getAccountID())) == null) {
                            c.write(PacketCreator.ServerNotice(1, "游戏断开链接\r\n请在正确的游戏群发言签到。"));
                            p.getClient().close();
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            c.close();
        }
    }

    /**
     * <商城按钮>* @param packet
     *
     * @param c
     */
    public static void EnterCS(Client c) {
        if (System.currentTimeMillis() - c.getPlayer().本次上线在线时间() > 10 * 1000) {
            c.getPlayer().getClient().write(PacketCreator.EnableActions());
            NPCScriptManager.getInstance().dispose(c.getPlayer().getClient());
            if (c.getPlayer().主界面() == 0) {
                NPCScriptManager.getInstance().start(c.getPlayer().getClient(), 9900000, 0);
            } else {
                NPCScriptManager.getInstance().start(c.getPlayer().getClient(), 9900000, 9900000);
            }
        } else {
            c.getPlayer().dropMessage(1, "上线 10 秒后才可以使用主页面。");
            c.getPlayer().getClient().write(PacketCreator.EnableActions());
            NPCScriptManager.getInstance().dispose(c.getPlayer().getClient());
        }
    }

    /**
     * <进入商城>
     */
    public static void TransferToCashShop(Client c) {
        try {
            Player p = c.getPlayer();
            //断开账号(p.getAccountName());
            if (!ServerProperties.Misc.CASHSHOP_AVAILABLE || p.getCashShop().isOpened()) {
                c.announce(PacketCreator.ServerMigrateFailed((byte) 2));
                return;
            }
            p.closePlayerInteractions();
            p.cancelAllBuffs(false);
            p.cancelAllDebuffs();
            p.cancelExpirationTask();

            c.announce(CashShopPackets.TransferToCashShop(c));
            c.announce(CashShopPackets.ShowCashInventory(c, p.getCashShop().loadGifts(c)));
            c.announce(CashShopPackets.ShowCash(p));

            c.getChannelServer().removePlayer(c.getPlayer());
            p.getMap().removePlayer(p);
            p.getCashShop().openedCashShop(true);
            p.saveDatabase();
        } catch (Exception ex) {
            FileLogger.printError("EnterCS.txt", ex);
            System.out.println("[-] EnterCS Exception");
        }
    }

    public static void LeaveCS(InPacket packet, Client c) {
        String[] socket = c.getChannelServer().getIP().split(":");
        //断开账号(c.getPlayer().getAccountName());
        if (c.getPlayer().getCashShop().isOpened()) {
            c.getPlayer().getCashShop().openedCashShop(false);
            c.getPlayer().saveDatabase();
        } else {
            c.write(PacketCreator.EnableActions());
            return;
        }
        c.getChannelServer().removePlayer(c.getPlayer());
        c.getChannelServer().addClientInTransfer(c.getChannel(), c.getPlayer().getId(), c);
        c.updateLoginState(ClientLoginState.LOGIN_SERVER_TRANSITION, c.getSessionIPAddress());
        try {
            c.announce(PacketCreator.GetChannelChange(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1])));
        } catch (UnknownHostException ex) {
            FileLogger.printError("LeaveCS.txt", ex);
            System.out.println("[-] LeaveCS Exception");
        }
    }

    public static void TouchingCS(InPacket packet, Client c) {
        c.write(CashShopPackets.ShowCash(c.getPlayer()));
    }
}
