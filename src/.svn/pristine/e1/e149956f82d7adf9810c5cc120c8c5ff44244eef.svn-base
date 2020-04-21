package handling.channel.handler;

import client.player.Player;
import client.Client;
import static handling.channel.handler.ChannelHeaders.PlayersHeaders.*;
import client.player.PlayerStat;
import packet.transfer.read.InPacket;
import packet.creators.CashShopPackets;
import packet.creators.PacketCreator;
import client.player.PlayerNote;
import client.player.inventory.Item;
import security.violation.AutobanManager;
import security.violation.CheatingOffense;
import community.MapleParty;
import scripting.npc.NPCScriptManager;
import server.maps.object.FieldObject;
import server.maps.object.FieldDoorObject;
import server.maps.object.FieldObjectType;
import server.maps.reactors.Reactor;
import tools.FileLogger;

/**
 *
 * @author GabrielSin
 */
public class PlayersHandler {

    public static void GiveFame(InPacket packet, Client c) {
        final Player self = c.getPlayer();
        final Player receiver = (Player) c.getPlayer().getMap().getMapObject(packet.readInt(), FieldObjectType.PLAYER);
        int add = packet.readByte();
        int fameChange = 2 * add - 1;

        if (receiver == self) {
            self.getCheatTracker().registerOffense(CheatingOffense.FAMING_SELF, " 努力让自己出名");
            return;
        }
        if (receiver == null) {
            c.write(PacketCreator.GiveFameErrorResponse(FAME_OPERATION_RESPONSE_NOT_IN_MAP));
            return;
        }
        if (self.getLevel() < 15) {
            self.getCheatTracker().registerOffense(CheatingOffense.FAMING_UNDER_15);
            c.write(PacketCreator.GiveFameErrorResponse(FAME_OPERATION_RESPONSE_UNDER_LEVEL));
            return;
        }
        if (fameChange != 1 && fameChange != -1) {
            AutobanManager.getInstance().autoban(self.getClient(), self.getName() + " 问题编辑包。");
            return;
        }
        switch (c.getPlayer().canGiveFame(receiver)) {
            case OK:
                if (Math.abs(receiver.getFame() + fameChange) < 30001) {
                    receiver.addFame(fameChange);
                    receiver.getStat().updateSingleStat(PlayerStat.FAME, receiver.getFame());
                }
                if (!c.getPlayer().isGameMaster()) {
                    c.getPlayer().hasGivenFame(receiver);
                }
                c.write(PacketCreator.GiveFameResponse(add, receiver.getName(), receiver.getFame()));
                receiver.getClient().write(PacketCreator.ReceiveFame(add, c.getPlayer().getName()));
                String x = "每日任务_添加其他角色人气";
                if (self.getbosslog(x) <= 0) {
                    self.setbosslog(x);
                }
                String xx = "每日任务_被其他角色添加人气";
                if (receiver.getbosslog(xx) <= 0) {
                    receiver.setbosslog(xx);
                }
                break;
            case NOT_TODAY:
                c.write(PacketCreator.GiveFameErrorResponse(FAME_OPEARTION_RESPONSE_NOT_TODAY));
                break;
            case NOT_THIS_MONTH:
                c.write(PacketCreator.GiveFameErrorResponse(FAME_OPERATION_RESPONSE_NOT_THIS_MONTH));
                break;
        }
    }

    public static void UseDoor(InPacket packet, Client c) {
        int doorId = packet.readInt();
        boolean inTown = packet.readBool();

        for (FieldObject obj : c.getPlayer().getMap().getAllDoorsThreadsafe()) {
            Player p = c.getPlayer();
            Player owner = c.getChannelServer().getPlayerStorage().getCharacterById(doorId);
            MapleParty party = c.getPlayer().getParty();
            FieldDoorObject door = (FieldDoorObject) obj;
            if (door == null) {
                p.getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT, "试图使用不存在的时空门");
                return;
            }
            if (owner != p && (party == null || party != p.getParty())) {
                p.getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT, "使用不存在的时空门");
                return;
            }
            if (door.getOwnerId() == doorId) {
                door.warp(c.getPlayer(), inTown);
                return;
            }
        }
    }

    public static void HitReactor(InPacket packet, Client c) {
        final int oid = packet.readInt();
        final int charPos = packet.readInt();
        final short stance = packet.readShort();
        final Reactor reactor = c.getPlayer().getMap().getReactorByOid(oid);

        if (reactor == null || !reactor.isAlive()) {
            return;
        }
        reactor.hitReactor(true, charPos, stance, c);
    }

    public static void Note(InPacket packet, Client c) {
        int type = packet.readByte();
        switch (type) {
            case NOTE_RECEIVE:
                String name = packet.readMapleAsciiString();
                String message = packet.readMapleAsciiString();
                boolean fame = packet.readBool();
                packet.readInt();

                int uniqueid = (int) packet.readLong();
                boolean isPackage = c.getPlayer().getCashShop().isPackage(uniqueid);
                if (!isPackage) {
                    Item item = c.getPlayer().getCashShop().findByUniqueId(uniqueid);
                    if (item == null || !item.getGiftFrom().equalsIgnoreCase(name) || !c.getPlayer().getCashShop().canSendNote(item.getUniqueId())) {
                        return;
                    }
                    c.getPlayer().getCashShop().sendedNote(item.getUniqueId());
                } else {
                    c.getPlayer().getCashShop().removePackage(uniqueid);
                }
                PlayerNote.sendNote(c.getPlayer(), name, message, fame ? 1 : 0);
                c.write(CashShopPackets.ShowCashInventory(c, c.getPlayer().getCashShop().loadGifts(c)));
                break;
            case NOTE_DELETE:
                int num = packet.readByte();
                packet.skip(2);
                for (int i = 0; i < num; i++) {
                    final int id = packet.readInt();
                    PlayerNote.deleteNote(c.getPlayer(), id, packet.readBool() ? 1 : 0);
                }
                break;
        }
    }

    public static void EnableActions(InPacket packet, Client c) {
        try {
            if (c.getPlayer() != null && c.getPlayer().getMap() != null) {
                c.getPlayer().saveDatabase();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            FileLogger.printError("Erro_updatep.txt", ex);
        }
    }

    public static void RingAction(InPacket packet, Client c) {
        byte mode = packet.readByte();
        Player player = c.getPlayer();
        switch (mode) {
            case SEND_RING:
                String partnerName = packet.readMapleAsciiString();
                Player partner = c.getChannelServer().getPlayerStorage().getCharacterByName(partnerName);
                if (partnerName.equalsIgnoreCase(player.getName())) {
                    player.dropMessage(1, "你不能把自己的名字写进去。");
                } else if (partner == null) {
                    player.dropMessage(1, partnerName + " 在这个频道上找不到。如果两人都已登录，请确保处于同一频道。");
                } else if (partner.getGender() == player.getGender()) {
                    player.dropMessage(1, "你的伴侣和你是同一性别。");
                } else if (player.getPartnerId() > 0 && partner.getPartnerId() > 0) {
                    NPCScriptManager.getInstance().start(partner.getClient(), 9201002, "marriagequestion", player, 0);
                }
                break;
            case CANCEL_SEND_RING:
                player.dropMessage(1, "您已取消请求。");
                break;
            case DROP_RING:
                //    Marriage.divorceEngagement(player);
                player.dropMessage(1, "你的订婚已经解除了。");
                break;
            default:
                System.out.println("Unhandled Ring Packet : " + packet.toString());
                break;
        }
    }
}
