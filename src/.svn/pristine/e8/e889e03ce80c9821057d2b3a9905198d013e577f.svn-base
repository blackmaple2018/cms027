package server.movement;

import packet.transfer.write.OutPacket;

public class BaseActionMovement extends AbsoluteLifeMovement {

    public BaseActionMovement(int type, int duration, int newstate) {
        super(type, null, duration, newstate);
    }

    @Override
    public void serialize(OutPacket wp) {
        wp.write(getType());
        wp.write(getNewstate());
        wp.writeShort(getDuration());
    }
}
