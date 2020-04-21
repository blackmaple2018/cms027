package packet.creators;

import client.Client;
import client.player.Player;
import constants.ServerProperties;
import handling.channel.ChannelServer;
import static handling.login.handler.CharLoginHeaders.*;
import handling.world.World;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import packet.opcode.OutHeader;
import packet.transfer.write.OutPacket;
import tools.HexTool;

/**
 *
 * @author GabrielSin
 */
public class LoginPackets {

    /**
     * Sends a hello packet.
     *
     * @param mapleVersion The maple client version.
     * @param sendIv the IV used by the server for sending
     * @param recvIv the IV used by the server for receiving
     * @param testServer
     * @return
     */
    public static OutPacket GetHello(short mapleVersion, int sendIv, int recvIv) {
        OutPacket mplew = new OutPacket(16);
        mplew.writeShort(0x0E);
        mplew.writeShort(mapleVersion);
        mplew.writeMapleAsciiString("1");
        mplew.writeInt(sendIv);
        mplew.writeInt(recvIv);
        mplew.write(4);
        return mplew;
    }

    /**
     * Sends a ping packet.
     *
     * @return The packet.
     */
    public static OutPacket PingMessage() {
        return new OutPacket(OutHeader.PING);
    }

    /**
     * Gets a login failed packet.
     *
     * Possible values for <code>reason</code>:<br>
     * 3: ID deleted or blocked<br>
     * 4: Incorrect password<br>
     * 5: Not a registered id<br>
     * 6: System error<br>
     * 7: Already logged in<br>
     * 8: System error<br>
     * 9: System error<br>
     * 10: Cannot process so many connections<br>
     * 11: Only users older than 20 can use this channel<br>
     * 13: Unable to log on as master at this ip<br>
     * 14: Wrong gateway or personal info and weird korean button<br>
     * 15: Processing request with that korean button!<br>
     * 16: Please verify your account through email...<br>
     * 17: Wrong gateway or personal info<br>
     * 21: Please verify your account through email...<br>
     * 23: License agreement<br>
     * 25: Maple Europe notice =[<br>
     * 27: Some weird full client notice, probably for trial versions<br>
     *
     * @param reason The reason logging in failed.
     * @return The login failed packet.
     */
    public static OutPacket GetLoginStatus(int reason) {
        OutPacket wp = new OutPacket(16);
        wp.write(OutHeader.LOGIN_STATUS.getValue());
        wp.writeInt(reason);
        wp.writeShort(0);
        return wp;
    }

    public static OutPacket GetPermBan(byte reason) {
        OutPacket wp = new OutPacket(16);
        wp.write(OutHeader.LOGIN_STATUS.getValue());
        wp.write(2);
        wp.write(reason);
        wp.writeBytes(HexTool.getByteArrayFromHexString("00 80 05 BB 46 E6 17 02"));
        return wp;
    }

    public static OutPacket GetTempBan(long timestampTill, byte reason) {
        OutPacket wp = new OutPacket(17);
        wp.write(OutHeader.LOGIN_STATUS.getValue());
        wp.write(2);
        wp.write(reason);
        wp.writeBytes(HexTool.getByteArrayFromHexString("00 80 05 BB 46 E6 17 02"));
        return wp;
    }

    /**
     * Gets a successful authentication and PIN Request packet.
     *
     * @param c
     * @return The PIN request packet.
     */
    public static OutPacket GetAuthSuccess(Client c) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.LOGIN_STATUS.getValue());
        wp.write(0);
        wp.writeInt(c.getAccountID());
        wp.write(c.getGender());
        wp.write(c.isGm() ? 1 : 0);
        wp.writeMapleAsciiString(c.getAccountName());
        wp.writeInt(c.getAccountID());
        wp.write(0);
        return wp;
    }

    /**
     *
     * @param cid
     * @param state
     * @return
     */
    public static OutPacket DeleteCharResponse(int cid, int state) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.DELETE_CHAR_RESPONSE.getValue());
        wp.writeInt(cid);
        wp.write(state);
        return wp;
    }

    public static OutPacket AddNewCharEntry(Player chr) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.ADD_NEW_CHAR_ENTRY.getValue());
        wp.write(0);
        AddCharEntry(wp, chr);
        return wp;
    }

    public static OutPacket AddNewCharEntry(Player p, boolean worked) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.ADD_NEW_CHAR_ENTRY.getValue());
        wp.write(worked ? 0 : 1);
        AddCharEntry(wp, p);
        return wp;
    }

    /**
     * Adds an entry for a character to an existing OutPacket.
     *
     * @param mplew The MaplePacketLittleEndianWrite instance to write the stats
     * to.
     * @param chr The character to add.
     */
    private static void AddCharEntry(OutPacket wp, Player chr) {
        PacketCreator.addCharStats(wp, chr);
        PacketCreator.AddCharLook(wp, chr, false, true);
    }

    /**
     * Gets a packet detailing a PIN operation. Possible values for
     * <code>mode</code>:<br>
     * 0 - PIN was accepted<br>
     * 1 - Register a new PIN<br>
     * 2 - Invalid pin / Reenter<br>
     * 3 - Connection failed due to system error<br>
     * 4 - Enter the pin
     *
     * @param mode The mode.
     * @return
     */
    public static OutPacket PinOperation(byte mode) {
        OutPacket wp = new OutPacket(3);
        wp.write(OutHeader.PIN_OPERATION.getValue());
        wp.write(mode);
        return wp;
    }

    public static OutPacket PinRegistered() {
        OutPacket mplew = new OutPacket();
        mplew.write(OutHeader.PIN_ASSIGNED.getValue());
        mplew.write(PIN_ACCEPTED);
        return mplew;
    }

    /**
     * Gets a packet requesting the client enter a PIN.
     *
     * @param status
     * @return The request PIN packet.
     */
    public static OutPacket RequestPinStatus(byte status) {
        switch (status) {
            case 0:
                return PinOperation(PIN_ACCEPTED);
            case 1:
                return PinOperation(PIN_REGISTER);
            case 2:
                return PinOperation(PIN_REJECTED);
            case 4:
                return PinOperation(PIN_REQUEST);

        }
        return null;
    }

    /**
     * Gets a packet detailing a server and its channels.
     *
     * @param worldId The index of the server to create information about.
     * @param serverName The name of the server.
     * @param channelLoad Load of the channel - 1200 seems to be max.
     * @return The server info packet.
     */
    public static OutPacket getServerList(World world) {
        OutPacket wp = new OutPacket(OutHeader.SERVERLIST);
        wp.write(world.getWorldId());
        wp.writeMapleAsciiString(world.getName());
        wp.write(world.getChannels().size());
        for (ChannelServer cs : world.getChannels()) {
            wp.writeMapleAsciiString(world.getName() + " - " + (cs.getChannel() + 1));
            //wp.writeInt(Math.min(800, cs.getPlayerStorage().getConnectedClients() * 80));
            wp.writeInt(cs.getPlayerStorage().getConnectedClients() * 100);
            wp.write(cs.getWorldId());
            wp.write(cs.getChannel());
            wp.write(0);//isAdultChannel
        }
        wp.writeShort(0);
        return wp;
    }

    public static OutPacket getEndOfServerList() {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.SERVERLIST.getValue());
        wp.write(0xFF);
        return wp;
    }

    /**
     * Gets a packet detailing a server status message. Possible values for
     * <code>status</code>:<br>
     * 0 - Normal<br>
     * 1 - Highly populated<br>
     * 2 - Full
     *
     * @param status The server status.
     * @return The server status packet.
     */
    public static OutPacket GetServerStatus(int status) {
        OutPacket wp = new OutPacket(OutHeader.SERVERSTATUS);
        wp.write(status);
        if (status == 1) {
            wp.writeInt(0);
            wp.write(0);
        }
        return wp;
    }

    /**
     * Gets a packet telling the client the IP of the channel server.
     *
     * @param port The port the channel is on.
     * @param clientId The ID of the client.
     * @return The server IP packet.
     */
    public static OutPacket GetServerIP(int port, int clientId) {
        OutPacket wp = new OutPacket();

        wp.write(OutHeader.SERVER_IP.getValue());
        wp.writeShort(0);
        try {
            wp.writeBytes(InetAddress.getByName(ServerProperties.World.HOST).getAddress());
        } catch (UnknownHostException e) {
            wp.writeBytes(ServerProperties.World.HOST_BYTE);
        }
        wp.writeShort(port);
        wp.writeInt(clientId);
        wp.write(0);
        return wp;
    }

    public static OutPacket CharNameResponse(String charname, boolean nameUsed) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.CHAR_NAME_RESPONSE.getValue());
        wp.writeMapleAsciiString(charname);
        wp.writeBool(nameUsed);
        return wp;
    }

    /**
     * Gets the response to a relog request.
     *
     * @return The relog response packet.
     */
    public static OutPacket GetRelogResponse() {
        OutPacket wp = new OutPacket(3);
        wp.write(OutHeader.RELOG_RESPONSE.getValue());
        wp.write(1);
        return wp;
    }

    public static OutPacket ShowAllCharacter(int chars) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.ALL_CHARLIST.getValue());
        wp.write(1);
        wp.writeInt(chars);
        wp.writeInt(chars + (3 - chars % 3));
        return wp;
    }

    public static OutPacket ShowAllCharacterInfo(int worldID, List<Player> chars) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.ALL_CHARLIST.getValue());
        wp.write(0);
        wp.write(worldID);
        wp.write(chars.size());
        chars.stream().forEach((chr) -> {
            AddCharEntry(wp, chr);
        });
        return wp;
    }

    /**
     * Gets a packet with a list of characters.
     *
     * @param c The MapleClient to load characters of.
     * @param serverID The ID of the server requested.
     * @return The character list packet.
     */
    public static OutPacket GetCharList(Client c, int serverID) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.CHARLIST.getValue());
        wp.write(0);
        wp.writeInt(c.getCharacterSlots());
        List<Player> chars = c.loadCharacters(serverID);
        wp.write((byte) chars.size());
        chars.stream().forEach((chr) -> {
            AddCharEntry(wp, chr);
        });
        return wp;
    }
}
