package packet.transfer.read;

import org.apache.log4j.Logger;

/**
 * @author WL
 * Created on 10/02/2019.
 */
public class Packet {
    private int offset = 0;
    private byte[] data;
    private static final Logger log = Logger.getLogger(Packet.class);

    public Packet(byte[] data) {
        this.data = new byte[data.length];
        System.arraycopy(data, 0, this.data, 0, data.length);
    }

    public int getLength() {
        if (data != null) {
            return data.length;
        }
        return 0;
    }

    public int getHeader() {
        if (data.length < 1) {
            return 0xFF;
        }
        return (data[0]);
    }

    public int decode() {
        try {
            return ((int) data[offset++]) & 0xFF;
        } catch (ArrayIndexOutOfBoundsException e) {
            //log.error("decode 溢出：" + Util.getExceptionText(e));
            return -1;
        }
    }

    public void setData(byte[] nD) {
        data = nD;
    }

    public byte[] getData() {
        return data;
    }

    public int getUnreadAmount() {
        return data.length - offset;
    }
}
