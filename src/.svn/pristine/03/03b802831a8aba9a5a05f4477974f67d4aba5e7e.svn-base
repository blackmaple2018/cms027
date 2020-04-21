package handling.channel.handler;

import client.player.Player;
import client.Client;
import packet.transfer.read.InPacket;
import community.MapleParty;
import community.MaplePartyCharacter;
import community.MaplePartyOperation;
import static configure.Gamemxd.副本;
import static handling.channel.handler.ChannelHeaders.PartyHeaders.*;
import handling.world.service.PartyService;
import packet.creators.PacketCreator;
import packet.creators.PartyPackets;

/**
 * <组队>
 */
public class PartyHandler {

    public static void handlePartyOperation(InPacket packet, Client c) {
        Player p = c.getPlayer();
        if (p == null || p.getMap() == null) {
            return;
        }
        switch (packet.readByte()) {
            //创建队伍
            case PARTY_CREATE:
                CreateParty(packet, p);
                break;
            //离开队伍
            case PARTY_LEAVE:
                LeaveParty(packet, p);
                break;
            //更换队长
            case PARTY_CHANGE_LEADER:
                ChangePartyLeader(packet, p);
                break;
            //接受邀请
            case PARTY_ACCEPT_INVITE:
                AcceptInviteParty(packet, p);
                break;
            //发起邀请
            case PARTY_INVITE:
                InvitePartyPlayer(packet, p);
                break;
            //踢出队伍
            case PARTY_EXPEL:
                ExpelPartyPlayer(packet, p);
                break;
        }
    }

    /**
     * <创建队伍>
     */
    public static void CreateParty(InPacket packet, Player p) {
        MapleParty party = p.getParty();
        MaplePartyCharacter partyPlayer = p.getMPC();
        if (party == null) {
            party = PartyService.createParty(partyPlayer);
            if (party == null) {
                p.announce(PartyPackets.PartyStatusMessage(NOT_IN_PARTY));
                return;
            }
            p.setParty(party);
            p.setMPC(partyPlayer);
            p.announce(PartyPackets.PartyCreated(p.getMPC()));
        } else {
            p.announce(PartyPackets.PartyStatusMessage(ALREADY_IN_PARTY));
        }
    }

    /**
     * <离开队伍>
     */
    public static void LeaveParty(InPacket packet, Player p) {
        MapleParty party = p.getParty();
        MaplePartyCharacter partyPlayer = p.getMPC();
        if (party != null) {
            if (partyPlayer.equals(party.getLeader())) {
                PartyService.updateParty(p, party.getId(), MaplePartyOperation.DISBAND, partyPlayer);
                if (p.getEventInstance() != null) {
                    p.getEventInstance().disbandParty();
                }
            } else {
                PartyService.updateParty(p, party.getId(), MaplePartyOperation.LEAVE, partyPlayer);
                if (p.getEventInstance() != null) {
                    p.getEventInstance().leftParty(p);
                }
            }
            if (副本(p.getMapId())) {
                p.changeMap(103000000);
                p.removeAll(4001007);
                p.removeAll(4001008);
            }

        } else {
            p.announce(PartyPackets.PartyStatusMessage(NOT_IN_PARTY));
        }
        p.setParty(null);
    }

    /**
     * <接受邀请方>
     */
    public static void AcceptInviteParty(InPacket packet, Player p) {
        MapleParty party = p.getParty();
        int partyID = packet.readInt();
        if (party == null) {
            party = PartyService.getParty(partyID);
            if (party != null) {
                if (party.getMembers().size() < 6) {
                    MaplePartyCharacter partyPlayer = new MaplePartyCharacter(p);
                    PartyService.updateParty(p, party.getId(), MaplePartyOperation.JOIN, partyPlayer);
                    p.receivePartyMemberHP();
                    p.updatePartyMemberHP();
                } else {
                    p.announce(PartyPackets.PartyStatusMessage(PARTY_FULL));
                }
            } else {
                p.announce(PacketCreator.ServerNotice(5, "你申请加入的队伍不存在。"));
            }
        } else {
            p.announce(PartyPackets.PartyStatusMessage(ALREADY_IN_PARTY));
        }
    }

    /**
     * <邀请组队>
     */
    public static void InvitePartyPlayer(InPacket packet, Player p) {
        //String name = packet.readMapleAsciiString();
        MapleParty party = p.getParty();
        Player invited = p.getClient().getChannelServer().getPlayerStorage().getCharacterById(packet.readInt());

        if (invited == null) {
            p.announce(PartyPackets.PartyStatusMessage(PARTY_CANNOT_FIND));
            return;
        }
        if (invited.getParty() != null) {
            p.announce(PartyPackets.PartyStatusMessage(ALREADY_IN_PARTY));
            return;
        }
        if (party == null) {
            p.announce(PartyPackets.PartyStatusMessage(NOT_IN_PARTY));
            return;
        }
        if (party.getMembers().size() < 6) {
            p.announce(PacketCreator.ServerNotice(5, "向 " + invited.getName() + " 发起组队邀请。"));
            invited.getClient().write(PartyPackets.PartyInvite(p.getPartyId(), p.getName()));
        } else {
            p.announce(PartyPackets.PartyStatusMessage(PARTY_FULL));
        }
    }

    /**
     * <踢出队伍>
     */
    public static void ExpelPartyPlayer(InPacket packet, Player p) {
        MapleParty party = p.getParty();
        MaplePartyCharacter partyPlayer = p.getMPC();
        int cid = packet.readInt();

        if (party == null || partyPlayer == null) {
            p.announce(PartyPackets.PartyStatusMessage(NOT_IN_PARTY));
            return;
        }
        if (!partyPlayer.equals(party.getLeader())) {
            p.announce(PartyPackets.PartyStatusMessage(NOT_IN_PARTY));
            return;
        }

        final MaplePartyCharacter expelled = party.getMemberById(cid);
        if (expelled != null) {
            PartyService.updateParty(p, party.getId(), MaplePartyOperation.EXPEL, expelled);
            if (p.getEventInstance() != null && expelled.isOnline()) {
                p.getEventInstance().leftParty(expelled.getPlayer());
            }
        }
    }

    /**
     * <转让队长>
     */
    public static void ChangePartyLeader(InPacket packet, Player p) {
        final int newLeaderID = packet.readInt();
        MapleParty party = p.getParty();
        MaplePartyCharacter partyPlayer = p.getMPC();

        if (party == null) {
            p.announce(PartyPackets.PartyStatusMessage(NOT_IN_PARTY));
            return;
        }
        if (!partyPlayer.equals(party.getLeader())) {
            p.announce(PartyPackets.PartyStatusMessage(NOT_IN_PARTY));
            return;
        }
        if (p.getMapId() >= 103000801 && p.getMapId() <= 103000805) {
            p.announce(PartyPackets.PartyStatusMessage(NOT_IN_PARTY));
            return;
        }
        final MaplePartyCharacter newLeadr = party.getMemberById(newLeaderID);
        final Player cfrom = p.getClient().getChannelServer().getPlayerStorage().getCharacterById(newLeaderID);

        if (newLeadr != null && cfrom.getMapId() == p.getMapId()) {
            //PartyService.updateParty(party.getId(), MaplePartyOperation.CHANGE_LEADER, newLeadr);
        } else {
            p.dropMessage(5, "队长只能转让给同地图的队员。");
        }
    }

    public static void PartyResponse(InPacket packet, Client c) {
        packet.readByte();
        String from = packet.readMapleAsciiString();
        String to = packet.readMapleAsciiString();
        Player cfrom = c.getChannelServer().getPlayerStorage().getCharacterByName(from);
        if (cfrom != null) {
            cfrom.getClient().write(PartyPackets.PartyInviteRejected(to));
        }
    }
}
