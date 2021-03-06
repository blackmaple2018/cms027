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
package server;

import java.sql.SQLException;

import handling.channel.ChannelServer;
import handling.world.World;
import handling.world.service.BroadcastService;
import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import launch.Start;
import packet.creators.PacketCreator;
import tools.TimerTools.AntiCheatTimer;
import tools.TimerTools.CharacterTimer;
import tools.TimerTools.CheatTrackerTimer;
import tools.TimerTools.ClientTimer;
import tools.TimerTools.EventTimer;
import tools.TimerTools.ItemTimer;
import tools.TimerTools.MapTimer;
import tools.TimerTools.MiscTimer;
import tools.TimerTools.MonsterTimer;
import tools.TimerTools.MountTimer;
import tools.TimerTools.NPCTimer;
import tools.TimerTools.SkillTimer;
import tools.TimerTools.WorldTimer;

/**
 * @author Frz
 */
public class ShutdownServer implements ShutdownServerMBean {

    public static ShutdownServer instance;
    public int mode = 0;

    public static void registerMBean() {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            instance = new ShutdownServer();
            mBeanServer.registerMBean(instance, new ObjectName("server:type=ShutdownServer"));
        } catch (InstanceAlreadyExistsException | MBeanRegistrationException | MalformedObjectNameException | NotCompliantMBeanException e) {
            System.out.println("Error registering Shutdown MBean");
            e.printStackTrace();
        }
    }

    public static ShutdownServer getInstance() {
        return instance;
    }

    @Override
    public void run() {
        if (this.mode == 0) {
            int ret = 0;
            for (World world : Start.getInstance().getWorlds()) {
                BroadcastService.broadcastMessage(world.getWorldId(), PacketCreator.ServerNotice(0, "服務器即將關閉，請大家安全下綫."));
                for (ChannelServer cs : world.getChannels()) {
                    cs.setShutdown();
                    cs.setServerMessage("服務器即將關閉，請大家安全下綫..");
                }
            }

            this.mode += 1;
        } else if (this.mode == 1) {
            this.mode += 1;
            System.out.println("Shutdown 2 commencing...");
            try {
                for (World world : Start.getInstance().getWorlds()) {
                    BroadcastService.broadcastMessage(world.getWorldId(), PacketCreator.ServerNotice(0, "服務器即將關閉，請大家安全下綫."));
                    Integer[] chs = world.getChannels().toArray(new Integer[0]);
                    for (int i : chs) {
                        try {
                            ChannelServer cs = world.getChannelById(i);
                            synchronized (this) {
                                cs.shutdown();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                WorldTimer.getInstance().stop();
                MiscTimer.getInstance().stop();
                ClientTimer.getInstance().stop();
                MountTimer.getInstance().stop();
                MonsterTimer.getInstance().stop();
                ItemTimer.getInstance().stop();
                MapTimer.getInstance().stop();
                EventTimer.getInstance().stop();
                AntiCheatTimer.getInstance().stop();
                NPCTimer.getInstance().stop();
                CharacterTimer.getInstance().stop();
                SkillTimer.getInstance().stop();
                CheatTrackerTimer.getInstance().stop();
            } catch (Exception e) {
                System.out.println("Failed to shutdown..." + e);
            }

            System.out.println("Shutdown 2 has finished.");
            try {
                Start.getInstance().getConnection().close();
            } catch (SQLException ex) {
                Logger.getLogger(ShutdownServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.mode = 0;
            System.out.println("Done.");
        }
    }

    @Override
    public void shutdown() {
        run();
    }
}
