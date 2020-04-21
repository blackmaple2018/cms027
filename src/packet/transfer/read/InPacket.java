package packet.transfer.read;

import constants.GameConstants;
import java.awt.Point;
import java.util.Arrays;
import tools.HexTool;

/**
 * @author WL
 * Created on 10/02/2019.
 */
public class InPacket {
    private final Packet packet;
    /**
     * Creates a new InPacket with no data.
     */
    public InPacket() {
        this.packet = new Packet(new byte[0]);
    }

    /**
     * Creates a new InPacket with given data.
     *
     * @param data The data this InPacket has to be initialized with.
     */
    public InPacket(byte[] data) {
        this.packet = new Packet(data);
    }

    public int getLength() {
        return getData().length;
    }

    public byte[] getData() {
        return packet.getData();
    }

    public InPacket clone() {
        return new InPacket(getData());
    }

    /**
     * Reads a single byte of the ByteBuf.
     *
     * @return The byte that has been read.
     */
    public final byte readByte() {
        return (byte) packet.decode();
    }

    public final short readUByte() {
        return (short) packet.decode();
    }

    /**
     * Reads an <code>amount</code> of bytes from the ByteBuf.
     *
     * @param amount The amount of bytes to read.
     * @return The bytes that have been read.
     */
    public final byte[] readBytes(int amount) {
        byte[] arr = new byte[amount];
        for (int i = 0; i < amount; i++) {
            arr[i] = readByte();
        }
        return arr;
    }
    
    public final void skip(int amount) {
        readBytes(amount);
    }
    
    public final Point readPos() {
        return new Point(readShort(), readShort());
    }

    /**
     * Reads an integer from the ByteBuf.
     *
     * @return The integer that has been read.
     */
    public final int readInt() {
        final int byte1 = packet.decode();
        final int byte2 = packet.decode();
        final int byte3 = packet.decode();
        final int byte4 = packet.decode();
        return (byte4 << 24) + (byte3 << 16) + (byte2 << 8) + byte1;
    }

    /**
     * Reads a short from the ByteBuf.
     *
     * @return The short that has been read.
     */
    public final short readShort() {
        final int byte1 = packet.decode();
        final int byte2 = packet.decode();
        return (short) ((byte2 << 8) + byte1);
    }

    public final boolean readBool() {
        return packet.decode() > 0;
    }
    /**
     * Reads a char array of a given length of this ByteBuf.
     *
     * @param amount The length of the char array
     * @return The char array as a String
     */

    public final String readAsciiString(int amount) {
        byte[] bytes = readBytes(amount);
        return new String(bytes, GameConstants.ENCODING);
    }

    /**
     * Reads a String, by first reading a short, then reading a char array of that length.
     *
     * @return The char array as a String
     */
    public final String readMapleAsciiString() {
        int amount = readShort();
        return readAsciiString(amount);
    }

    @Override
    public final String toString() {
        return HexTool.readableByteArray(Arrays.copyOfRange(getData(), getData().length - available(), getData().length)); // Substring after copy of range xd
    }

    /**
     * Reads and returns a long from this net.swordie.ms.connection.packet.
     *
     * @return The long that has been read.
     */
    public final long readLong() {
        final int byte1 = packet.decode();
        final int byte2 = packet.decode();
        final int byte3 = packet.decode();
        final long byte4 = packet.decode();
        final long byte5 = packet.decode();
        final long byte6 = packet.decode();
        final long byte7 = packet.decode();
        final long byte8 = packet.decode();
        return ((byte8 << 56) + (byte7 << 48) + (byte6 << 40) + (byte5 << 32) + (byte4 << 24) + (byte3 << 16) + (byte2 << 8) + byte1);
    }

    /**
     * Reads a position (short x, short y) and returns this.
     *
     * @return The position that has been read.
     */
    /*public final Pos decodePosition() {
        return new Position(decodeShort(), decodeShort());
    }*/

    /**
     * Returns the amount of bytes that are unread.
     *
     * @return The amount of bytes that are unread.
     */
    public final int available() {
        return packet.getUnreadAmount();
    }
}
