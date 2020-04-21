package packet.creators;

import community.MapleParty;
import community.MaplePartyCharacter;
import community.MaplePartyOperation;
import constants.MapConstants;
import handling.channel.handler.ChannelHeaders.PartyHeaders;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import packet.opcode.OutHeader;
import packet.transfer.write.OutPacket;
import server.maps.MapleDoor;
import server.maps.object.FieldDoorObject;

public class PartyPackets {

    public static OutPacket UpdateParty(int forChannel, MapleParty party, MaplePartyOperation op, MaplePartyCharacter target) {
        OutPacket wp = new OutPacket(OutHeader.PARTY_OPERATION);
        switch (op) {
            case DISBAND:
            case EXPEL:
            case LEAVE:
                wp.write(PartyHeaders.LEFT_PARTY);
                wp.writeInt(party.getId());
                wp.writeInt(target.getId());
                if (op == MaplePartyOperation.DISBAND) {
                    wp.write(0);
                    wp.writeInt(party.getId());
                } else {
                    wp.write(1);
                    if (op == MaplePartyOperation.EXPEL) {
                        wp.writeBool(true);
                    } else {
                        wp.writeBool(false);
                    }
                    wp.writeMapleAsciiString(target.getName());
                    AddPartyStatus(forChannel, party, wp, false);
                }
                break;
            case JOIN:
                wp.write(PartyHeaders.JOINED_PARTY);
                wp.writeInt(party.getId());
                wp.writeMapleAsciiString(target.getName());
                AddPartyStatus(forChannel, party, wp, false);
                break;
            case SILENT_UPDATE:
            case LOG_ONOFF:
                wp.write(PartyHeaders.PARTY_SILENT_LIST_UPDATE);
                wp.writeInt(party == null ? 0 : party.getId());
                AddPartyStatus(forChannel, party, wp, false);
                break;
            case CHANGE_LEADER:
                wp.write(0x1A);
                wp.writeInt(target.getId());
                wp.write(1);
                break;
        }
        return wp;
    }

    private static void AddPartyStatus(int forChannel, MapleParty party, OutPacket wp, boolean leaving) {
        List<MaplePartyCharacter> partymembers = new ArrayList<>(party.getMembers());
        while (partymembers.size() < 6) {
            partymembers.add(new MaplePartyCharacter());
        }
        partymembers.stream().forEach((partychar) -> {
            wp.writeInt(partychar.getId());// 24
        });
        partymembers.stream().forEach((partychar) -> {
            wp.writeAsciiString(partychar.getName(), 19); //78
        });
        partymembers.stream().forEach((partychar) -> {
            // 24
            if (partychar.isOnline()) {
                wp.writeInt(partychar.getMapId());
            } else {
                wp.writeInt(-1);
            }
        });
        wp.writeInt(party.getLeader().getId());//4
        partymembers.stream().forEach((partychar) -> {
            // 24
            if (partychar.isOnline()) {
                wp.writeInt(partychar.getChannel() - 1);
            } else {
                wp.writeInt(-2);
            }
        });
        partymembers.stream().forEach((partychar) -> {
            if (partychar.getChannel() == forChannel && !leaving) {
                boolean display = false;
                for (MapleDoor door : partychar.getDoors()) {
                    FieldDoorObject mdo = door.getTownDoor();
                    wp.writeInt(mdo.getTown().getId());
                    wp.writeInt(mdo.getArea().getId());
                    wp.writeShort(mdo.getPosition().x);
                    wp.writeShort(mdo.getPosition().y);
                    display = true;
                    //System.out.println("town: " + mdo.getTown().getId() + " area: " + mdo.getArea().getId() + " position: " + mdo.getPosition().toString());
                }
                if (!display) {
                    wp.writeInt(MapConstants.NULL_MAP);
                    wp.writeInt(MapConstants.NULL_MAP);
                    wp.writeShort(0);
                    wp.writeShort(0);
                }
            } else {
                wp.writeInt(MapConstants.NULL_MAP);
                wp.writeInt(MapConstants.NULL_MAP);
                wp.writeShort(0);
                wp.writeShort(0);
            }
        });
    }

    public static OutPacket PartyCreated(MaplePartyCharacter partychar) {
        OutPacket wp = new OutPacket(OutHeader.PARTY_OPERATION);
        wp.write(PartyHeaders.PARTY_CREATED);
        wp.writeInt(partychar.getId());
        if (partychar.getDoors().size() > 0) {
            boolean deployedPortal = false;

            for (MapleDoor door : partychar.getDoors()) {
                if (door.getOwnerId() == partychar.getId()) {
                    FieldDoorObject mdo = door.getAreaDoor();
                    wp.writeInt(mdo.getTo().getId());
                    wp.writeInt(mdo.getFrom().getId());
                    wp.writeShort(mdo.getPosition().x);
                    wp.writeShort(mdo.getPosition().y);
                    deployedPortal = true;
                }
            }

            if (!deployedPortal) {
                wp.writeInt(999999999);
                wp.writeInt(999999999);
                wp.writeShort(0);
                wp.writeShort(0);
            }
        } else {
            wp.writeInt(999999999);
            wp.writeInt(999999999);
            wp.writeShort(0);
            wp.writeShort(0);
        }
        return wp;
    }

    public static OutPacket PartyStatusMessage(byte opCode) {
        OutPacket wp = new OutPacket(OutHeader.PARTY_OPERATION);
        wp.write(opCode);
        return wp;
    }

    public static OutPacket PartyInvite(int partyID, String name) {
        OutPacket wp = new OutPacket(OutHeader.PARTY_OPERATION);
        wp.write(PartyHeaders.PARTY_INVITE);
        wp.writeInt(partyID);
        wp.writeMapleAsciiString(name);
        wp.write(0);
        return wp;
    }

    public static OutPacket PartyInviteRejected(String name) {
        OutPacket wp = new OutPacket(OutHeader.PARTY_OPERATION);
        wp.write(PartyHeaders.PARTY_INVITE_DENIED);
        wp.writeMapleAsciiString(name);
        return wp;
    }

    public static OutPacket PartyPortal(int linkedPortalId, int townID, int targetID, Point position) {
        OutPacket wp = new OutPacket(OutHeader.PARTY_OPERATION);
        wp.write(26);
        wp.write(linkedPortalId - 0x80);//0-5
        wp.writeInt(townID);
        wp.writeInt(targetID);
        wp.writePos(position);
        return wp;
    }

    public static OutPacket UpdatePartyMemberHP(int cID, int curHP, int maxHP) {
        OutPacket wp = new OutPacket(OutHeader.UPDATE_PARTYMEMBER_HP);
        wp.writeInt(cID);
        wp.writeInt(curHP);
        wp.writeInt(maxHP);
        return wp;
    }
}
