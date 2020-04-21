package handling.world;

import client.player.Player;
import constants.ServerProperties;
import handling.channel.ChannelServer;
import java.util.ArrayList;
import java.util.List;
import packet.transfer.write.OutPacket;

public class World {

    private final int worldId;
    private final String name;
    private final List<ChannelServer> channels;

    public World(int worldId, String name) {
        this.worldId = worldId;
        this.name = name;
        List<ChannelServer> channelList = new ArrayList<>();
        for (int i = 0; i < ServerProperties.Channel.COUNT; i++) {
            channelList.add(new ChannelServer(this, i));
        }
        this.channels = channelList;
        //航班
        this.channels.forEach((cs) -> {
            cs.loadTransitions();
        });
    }

    public int getWorldId() {
        return worldId;
    }

    public String getName() {
        return name;
    }

    public ChannelServer getChannelById(int id) {
        return getChannels().stream().filter(c -> (c.getChannel() + 1) == id).findFirst().orElse(null);
    }

    public List<ChannelServer> getChannels() {
        return channels;
    }

    public void broadcastPacket(OutPacket outPacket) {
        getChannels().forEach((channel) -> {
            channel.broadcastPacket(outPacket);
        });
    }

    public Player getCharByName(String name) {
        for (ChannelServer c : getChannels()) {
            Player chr = c.getPlayerStorage().getCharacterByName(name);
            if (chr != null) {
                return chr;
            }
        }
        return null;
    }
    
    public Player getCharByID(int id) {
        for (ChannelServer c : getChannels()) {
            Player chr = c.getPlayerStorage().getCharacterById(id);
            if (chr != null) {
                return chr;
            }
        }
        return null;
    }
    
    public boolean isCharacterListConnected(List<String> charName) {
        for (ChannelServer cs : getChannels()) {
            for (final String c : charName) {
                if (cs.getPlayerStorage().getCharacterByName(c) != null) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isFull() {
        boolean full = true;
        for (ChannelServer cs : getChannels()) {
            if (cs.getPlayerStorage().getCheatersSize() < 50) {
                full = false;
                break;
            }
        }
        return full;
    }
}
