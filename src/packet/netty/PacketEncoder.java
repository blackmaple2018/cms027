package packet.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.log4j.LogManager;
import packet.crypto.AESCipher;
import static packet.crypto.AESCipher.nVersion;
import packet.crypto.CIGCipher;
import packet.opcode.OutHeader;
import packet.transfer.write.OutPacket;
import tools.HexTool;

public final class PacketEncoder extends MessageToByteEncoder<OutPacket> {
    private static final org.apache.log4j.Logger log = LogManager.getRootLogger();
    private static final int uSeqBase = (short) ((((0xFFFF - nVersion) >> 8) & 0xFF) | (((0xFFFF - nVersion) << 8) & 0xFF00));

    @Override
    protected void encode(ChannelHandlerContext chc, OutPacket outPacket, ByteBuf bb) {
        byte[] data = outPacket.getData();
        boolean bEncryptData = true;
        NettyClient c = chc.channel().attr(NettyClient.CLIENT_KEY).get();
        if (c != null) {
            /*if (!OutHeader.isSpamHeader(OutHeader.getOutHeaderByOp(outPacket.getHeader()))) {
                log.info("[发送到客户端 " + c.getIP() + "]\t| " + outPacket);
            }*/
            int uSeqSend = c.getSendIV();
            short uDataLen = (short) (((data.length << 8) & 0xFF00) | (data.length >>> 8));
            short uRawSeq = (short) ((((uSeqSend >> 24) & 0xFF) | (((uSeqSend >> 16) << 8) & 0xFF00)) ^ uSeqBase);

            c.acquireEncoderState();
            try {
                if (bEncryptData) {
                    uDataLen ^= uRawSeq;
                    //AESCipher.Crypt(data, uSeqSend);
                }
                c.setSendIV(CIGCipher.InnoHash(uSeqSend, 4, 0));
            } finally {
                c.releaseEncodeState();
            }

            bb.writeShort(uRawSeq);
            bb.writeShort(uDataLen);
            bb.writeBytes(data);
            //System.out.println("uSeqSend：" + uSeqSend + " uRawSeq：" + uRawSeq + " uDataLen：" + uDataLen + " data：" + HexTool.toString(data));
        } else {
            //log.info("[PacketEncoder] | 明文发送 " + outPacket);
            bb.writeBytes(data);
        }
    }
}

