package packet.creators;

import java.util.List;
import packet.transfer.write.OutPacket;
import server.movement.LifeMovementFragment;
import tools.HexTool;
import tools.KoreanDateUtil;

public class HelpPackets {
    
    public static void SerializeMovementList(OutPacket wp, List<LifeMovementFragment> moves) {
        wp.write(moves.size());
        moves.stream().forEach((move) -> {
            move.serialize(wp);
        });
    }
    
    public static void AddExpirationTime(OutPacket wp, long time, boolean showexpirationtime) {
        if (time != 0) {
            wp.writeInt(KoreanDateUtil.getItemTimestamp(time));
        } else {
            wp.writeInt(400967355);
        }
        wp.write(showexpirationtime ? 1 : 2);
    }
    
    public static OutPacket GetPacketFromHexString(String hex) {
        return new OutPacket(HexTool.getByteArrayFromHexString(hex));
    }
}
