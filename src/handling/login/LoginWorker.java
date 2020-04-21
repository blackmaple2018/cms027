package handling.login;

import java.util.Map;
import java.util.Map.Entry;

import client.Client;
import handling.channel.ChannelServer;
import handling.login.handler.CharLoginHandler;
import handling.login.handler.CharLoginHeaders;
import handling.world.World;
import java.util.List;
import launch.Start;
import packet.creators.LoginPackets;
import tools.TimerTools.PingTimer;

public class LoginWorker {

    private static long lastUpdate = 0;

    public static void registerClient(final Client c) {
        if (System.currentTimeMillis() - lastUpdate > 600000) {
            lastUpdate = System.currentTimeMillis();
            List<World> worlds = Start.getInstance().getWorlds();
            if (worlds == null || worlds.size() <= 0) {
                lastUpdate = 0;
                c.write(LoginPackets.GetLoginStatus(CharLoginHeaders.LOGIN_ALREADY));
                return;
            }
            lastUpdate = System.currentTimeMillis();
        }

        if (c.finishLogin() == 0) {
            c.write(LoginPackets.GetAuthSuccess(c));
            CharLoginHandler.ServerListRequest(c);
            c.setIdleTask(PingTimer.getInstance().schedule(c::close, 10 * 60 * 10000));
        } else {
            c.write(LoginPackets.GetLoginStatus(CharLoginHeaders.LOGIN_ALREADY));
        }
    }
}
