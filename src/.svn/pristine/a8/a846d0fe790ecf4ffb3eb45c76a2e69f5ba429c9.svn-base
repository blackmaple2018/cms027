package scripting.npc;

import java.util.List;
import client.player.Player;
import community.MaplePartyCharacter;
import server.partyquest.mcpq.MCParty;

public interface NPCScript {
    public void start();
    public void start(Player chr);
    public void start(List<MaplePartyCharacter> chrs, MCParty pty);
    public void action(byte mode, byte type, int selection);
}
