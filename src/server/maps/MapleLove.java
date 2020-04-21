/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.maps;

import client.Client;
import client.player.Player;
import packet.creators.PacketCreator;
import server.maps.object.AbstractMapleFieldObject;
import server.maps.object.FieldObjectType;

/**
 *
 * @author Administrator
 */
public class MapleLove extends AbstractMapleFieldObject {

    public int itemID;
    public String message;
    public Player player;
    
    public MapleLove(int itemID, String message, Player p) {
        this.itemID = itemID;
        this.message = message;
        this.player = p;
    }
    
    @Override
    public FieldObjectType getType() {
        return FieldObjectType.LOVE;
    }

    @Override
    public void sendSpawnData(Client client) {
        player.getClient().write(PacketCreator.spawnLove(this));
    }

    @Override
    public void sendDestroyData(Client client) {
        player.getClient().write(PacketCreator.removeLove(this, (byte) 0));
    }
}
