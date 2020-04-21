/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation. You may not use, modify
    or distribute this program under any other version of the
    GNU Affero General Public License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package community;

import client.Client;
import client.player.Player;
import handling.channel.ChannelServer;
import handling.world.service.PartyService;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import scripting.event.EventInstanceManager;
import server.maps.Field;
import server.partyquest.mcpq.MCField;

public class MapleParty implements Serializable {

    private int id;
    private int leaderId;
    private int nextEntry = 0;
    private MaplePartyCharacter leader;
    private List<MaplePartyCharacter> pqMembers = null;
    private final List<MaplePartyCharacter> members = new LinkedList<>();
    private Map<Integer, Integer> histMembers = new HashMap<>();

    public MapleParty(int id, MaplePartyCharacter chrfor) {
        this.leader = chrfor;
        this.leaderId = chrfor.getId();
        this.members.add(chrfor);
        this.id = id;
    }

    public boolean containsMembers(MaplePartyCharacter member) {
        return members.contains(member);
    }

    public void addMember(MaplePartyCharacter member) {
        histMembers.put(member.getId(), nextEntry);
        nextEntry++;

        members.add(member);
    }

    public void removeMember(MaplePartyCharacter member) {
        histMembers.remove(member.getId());
        members.remove(member);
    }

    public void setLeader(MaplePartyCharacter victim) {
        this.leader = victim;
    }

    public void updateMember(MaplePartyCharacter member) {
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).getId() == member.getId()) {
                members.set(i, member);
            }
        }
    }

    public MaplePartyCharacter getMemberById(int id) {
        for (MaplePartyCharacter chr : members) {
            if (chr.getId() == id) {
                return chr;
            }
        }
        return null;
    }

    public Collection<MaplePartyCharacter> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public List<MaplePartyCharacter> getPartyMembers() {
        return Collections.unmodifiableList(members);
    }

    public byte getPartyDoor(int cid) {
        List<Entry<Integer, Integer>> histList = new LinkedList<>(histMembers.entrySet());

        Collections.sort(histList, (Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) -> (o1.getValue()).compareTo(o2.getValue()));

        byte slot = 0;
        for (Entry<Integer, Integer> e : histList) {
            if (e.getKey() == cid) {
                break;
            }
            slot++;
        }

        return slot;
    }

    public MaplePartyCharacter getMemberByPos(int pos) {
        int i = 0;
        for (MaplePartyCharacter chr : members) {
            if (pos == i) {
                return chr;
            }
            i++;
        }
        return null;
    }

    public void setEligibleMembers(List<MaplePartyCharacter> eliParty) {
        pqMembers = eliParty;
    }

    public Collection<MaplePartyCharacter> getEligibleMembers() {
        return Collections.unmodifiableList(pqMembers);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MaplePartyCharacter getLeader() {
        for (MaplePartyCharacter mpc : members) {
            if (mpc.getId() == leaderId) {
                return mpc;
            }
        }

        return null;
    }

    public int getLeaderId() {
        return leaderId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MapleParty other = (MapleParty) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }

    public static void leaveParty(MapleParty party, Client c) {
        ChannelServer cs = c.getChannelServer();
        Player player = c.getPlayer();
        MaplePartyCharacter partyplayer = player.getMPC();

        if (party != null && partyplayer != null) {
            if (partyplayer.getId() == party.getLeaderId()) {
                cs.removeMapPartyMembers(party.getId());

                MCField monsterCarnival = player.getMCPQField();
                if (monsterCarnival != null) {
                    monsterCarnival.onPlayerDisconnected(player);
                }

                PartyService.updateParty(c.getPlayer(), party.getId(), MaplePartyOperation.DISBAND, partyplayer);

                EventInstanceManager eim = player.getEventInstance();
                if (eim != null) {
                    eim.disbandParty();
                }
            } else {
                Field map = player.getMap();
                if (map != null) {
                    map.removePartyMember(player);
                }

                MCField monsterCarnival = player.getMCPQField();
                if (monsterCarnival != null) {
                    monsterCarnival.onPlayerDisconnected(player);
                }

                PartyService.updateParty(c.getPlayer(), party.getId(), MaplePartyOperation.LEAVE, partyplayer);

                EventInstanceManager eim = player.getEventInstance();
                if (eim != null) {
                    eim.leftParty(player);
                }
            }

            player.setParty(null);
        }
    }
}
