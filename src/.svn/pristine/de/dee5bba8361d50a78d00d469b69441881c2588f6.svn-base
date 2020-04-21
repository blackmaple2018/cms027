/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.creators;

import client.player.Player;
import packet.opcode.OutHeader;
import packet.transfer.write.OutPacket;
import server.partyquest.mcpq.MCParty;

public class CarnivalPackets {
    
    public static OutPacket StartMonsterCarnival(Player p) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.MONSTER_CARNIVAL_START.getValue());
        wp.write(p.getTeam()); 
        wp.writeShort(p.getAvailableCP()); 
        wp.writeShort(p.getTotalCP()); 
        wp.writeShort(p.getMCPQField().getRed().getAvailableCP()); 
        wp.writeShort(p.getMCPQField().getRed().getTotalCP()); 
        wp.writeShort(p.getMCPQField().getBlue().getAvailableCP()); 
        wp.writeShort(p.getMCPQField().getBlue().getAvailableCP()); 
        wp.writeShort(0);
        wp.writeLong(0); 
        return wp;
    }
    
    public static OutPacket UpdatePersonalCP(Player p) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.MONSTER_CARNIVAL_OBTAINED_CP.getValue());
        wp.writeShort(p.getAvailableCP()); 
        wp.writeShort(p.getTotalCP()); 
        return wp;
    }
    
    public static OutPacket UpdatePartyCP(MCParty pty) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.MONSTER_CARNIVAL_PARTY_CP.getValue());
        wp.write(pty.getTeam().code);  
        wp.writeShort(pty.getAvailableCP());  
        wp.writeShort(pty.getTotalCP());  
        return wp;
    }

    public static OutPacket PlayerSummoned(int tab, int num, String name) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.MONSTER_CARNIVAL_SUMMON.getValue());
        wp.write(tab);
        wp.write(num);
        wp.writeMapleAsciiString(name);
        return wp;
    }
    
    public static OutPacket PlayerDiedMessage(Player p, int loss) { 
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.MONSTER_CARNIVAL_DIED.getValue());
        wp.write(p.getTeam()); 
        wp.writeMapleAsciiString(p.getName());
        wp.write(loss);
        return wp;
    }  
    
    public static OutPacket CarnivalMessage(int message) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.MONSTER_CARNIVAL_MESSAGE.getValue());
        wp.write(message); 
        return wp;
    }
    
    public static OutPacket CarnivalLeave(int team, String name) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.MONSTER_CARNIVAL_LEAVE.getValue());
        wp.write(0); 
        wp.write(team);  
        wp.writeMapleAsciiString(name); 
        return wp;
    }
}
