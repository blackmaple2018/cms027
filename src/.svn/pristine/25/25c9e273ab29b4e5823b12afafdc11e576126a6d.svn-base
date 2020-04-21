package handling.channel.handler;

import client.Client;
import packet.transfer.read.InPacket;
import handling.world.service.BroadcastService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import packet.creators.PacketCreator;
import client.player.PlayerQuery;
import launch.Start;

/**
 *
 * @author GabrielSin
 */
public class NotificationsHandler {

    public final static String[] REASONS = {
        "Hacking",
        "Botting",
        "Scamming",
        "Fake GM",
        "Harassment",
        "Advertising"
    };
        
    public static void ReportPlayer(InPacket slea, Client c) {
       	int reportedCharId = slea.readInt();
        byte reason = slea.readByte();
        String chatlog = "No chatlog";
        short clogLen = slea.readShort();
        if (clogLen > 0) {
            chatlog = slea.readAsciiString(clogLen);
        }
        
        int cid = reportedCharId;

        if (addReportEntry(c.getPlayer().getId(), reportedCharId, reason, chatlog)) {
            c.write(PacketCreator.ReportReply((byte) 0));
        } else {
            c.write(PacketCreator.ReportReply((byte) 4));
        }
        
        BroadcastService.broadcastGMMessage(c.getWorld(), PacketCreator.ServerNotice(5, c.getPlayer().getName() + " reportou " + PlayerQuery.getNameById(cid) + " por " + REASONS[reason] + "."));
    }
    
    public static boolean addReportEntry(int reporterId, int victimId, byte reason, String chatlog) {
         Connection con =null;
        try {
             con = Start.getInstance().getConnection();
            PreparedStatement ps;
            ps = con.prepareStatement("INSERT INTO reports VALUES (NULL, CURRENT_TIMESTAMP, ?, ?, ?, ?, 'UNHANDLED')");
            ps.setInt(1, reporterId);
            ps.setInt(2, victimId);
            ps.setInt(3, reason);
            ps.setString(4, chatlog);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
        return true;
    }
}
