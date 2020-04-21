package handling.world.service;

import java.util.List;
import packet.transfer.write.OutPacket;
import client.player.Player;
import launch.Start;

public class BroadcastService {

    public static void broadcastSmega(int world, OutPacket message) {
        Start.getInstance().getWorldById(world).getChannels().forEach((cs) -> {
            cs.broadcastSmega(message);
        });
    }

    public static void broadcastGMMessage(int world, OutPacket message) {
        Start.getInstance().getWorldById(world).getChannels().forEach((cs) -> {
            cs.broadcastGMMessage(message);
        });
    }

    public static void broadcastMessage(int world, OutPacket message) {
        Start.getInstance().getWorldById(world).getChannels().forEach((cs) -> {
            cs.broadcastMessage(message);
        });
    }

    public static void broadcastMessagekg(int world, OutPacket message) {
        Start.getInstance().getWorldById(world).getChannels().forEach((cs) -> {
            cs.broadcastMessagekg(message);
        });
    }

    public static void sendPacket(int world, List<Integer> targetIds, OutPacket packet, int exception) {
        Player c;
        for (int i : targetIds) {
            if (i == exception) {
                continue;
            }
            int ch = FindService.findChannel(i, world);
            if (ch < 0) {
                continue;
            }
            c = Start.getInstance().getWorldById(world).getChannelById(ch).getPlayerStorage().getCharacterById(i);
            if (c != null) {
                c.getClient().write(packet);
            }
        }
    }
}
