package packet.transfer.write;

import constants.GameConstants;
import java.awt.Point;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import org.apache.log4j.LogManager;
import packet.opcode.OutHeader;
import tools.HexTool;

/**
 * @author WL
 * Created on 10/02/2019.
 */
public class OutPacket {
    private int opcode;
    private static final org.apache.log4j.Logger log = LogManager.getRootLogger();
    private final ByteArrayOutputStream baos;

    public OutPacket(int size) {
        this.baos = new ByteArrayOutputStream(size);
    }

    public OutPacket() {
        this(32);
    }

    public OutPacket(OutHeader outHeader) {
        this(outHeader, 32);
    }

    public OutPacket(OutHeader outHeader, int capacity) {
        this(capacity);
        this.opcode = outHeader.getValue();
        write(opcode);
    }

    public OutPacket(byte[] in) {
        this(in.length);
        writeBytes(in);
    }

    public int getHeader() {
        return opcode;
    }

    public final byte[] getData() {
        return baos.toByteArray();
    }

    public OutPacket clone() {
        return new OutPacket(getData());
    }

    @Override
    public String toString() {
        return String.format("%s, %s/0x%s\t| %s", OutHeader.getOutHeaderByOp(opcode), opcode, Integer.toHexString(opcode).toUpperCase()
                , HexTool.readableByteArray(Arrays.copyOfRange(getData(), 0, getData().length)));
    }

    public final void writeBytes(byte[] in) {
        writeBytes(in, 0, in.length);
    }
    
    public final void writeZeroBytes(int len) {
        writeBytes(new byte[len]);
    }
    
    public final void writeBytes(byte[] in, int off, int len) {
        for (int i = off; i < len; i++) {
            baos.write(in[i]);
        }
    }

    public final void writeBytes(String arr) {
        writeBytes(HexTool.getByteArrayByString(arr));
    }

    public final void write(int b) {
        write((byte) b);
    }

    public final void write(byte b) {
        baos.write(b);
    }

    public final void writeChar(char c) {
        baos.write(c);
    }

    public final void writeBool(boolean b) {
        baos.write(b ? 1 : 0);
    }

    public final void writeShort(short s) {
        baos.write((byte) (s & 0xFF));
        baos.write((byte) ((s >>> 8) & 0xFF));
    }

    public final void writeShort(int value) {
        writeShort((short) value);
    }

    public final void writeInt(int i) {
        baos.write((byte) (i & 0xFF));
        baos.write((byte) ((i >>> 8) & 0xFF));
        baos.write((byte) ((i >>> 16) & 0xFF));
        baos.write((byte) ((i >>> 24) & 0xFF));
    }

    public final void writeLong(long l) {
        baos.write((byte) (l & 0xFF));
        baos.write((byte) ((l >>> 8) & 0xFF));
        baos.write((byte) ((l >>> 16) & 0xFF));
        baos.write((byte) ((l >>> 24) & 0xFF));
        baos.write((byte) ((l >>> 32) & 0xFF));
        baos.write((byte) ((l >>> 40) & 0xFF));
        baos.write((byte) ((l >>> 48) & 0xFF));
        baos.write((byte) ((l >>> 56) & 0xFF));
    }

    public final void writeDouble(double d) {
        writeLong(Double.doubleToLongBits(d));
    }

    public final void writeMapleAsciiString(String s) {
        byte[] data = s != null ? s.getBytes(GameConstants.ENCODING) : new byte[]{};
        if (data.length > Short.MAX_VALUE) {
            log.error("尝试写入过大的字符串.");
            return;
        }
        writeShort(data.length);
        writeBytes(data);
    }

    public final void writeAsciiString(String s, short length) {
        if (s == null) {
            s = "";
        }
        byte[] data = s.getBytes(GameConstants.ENCODING);
        if (data.length > 0) {
            writeBytes(data);
        }
        for (int i = data.length; i < length; i++) {
            write((byte) 0);
        }
    }

    public final void writeAsciiString(String name, int length) {
        writeAsciiString(name, (short) length);
    }
    
    public final void writePos(Point pos) {
        writeShort((short) pos.getX());
        writeShort((short) pos.getY());
    }
}
