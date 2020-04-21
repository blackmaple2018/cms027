package handling.channel.handler;

import client.player.Player;
import client.Client;
import community.MapleBuddyInvitedEntry;
import community.MapleBuddyList;
import community.MapleBuddyList.BuddyAddResult;
import community.MapleBuddyListEntry;
import handling.channel.ChannelServer;
import static handling.channel.handler.ChannelHeaders.BuddyListHeaders.*;
import packet.transfer.read.InPacket;
import handling.world.service.BuddyService;
import handling.world.service.FindService;
import java.nio.charset.Charset;
import packet.creators.PacketCreator;
import tools.Pair;

/**
 *
 * @author GabrielSin
 */
public class BuddyListHandler {

    /**
     * <好友>
     */
    public static void BuddyOperation(final InPacket packet, final Client c) {
        final MapleBuddyList buddylist = c.getPlayer().getBuddylist();
        switch (packet.readByte()) {
            case BUDDY_INVITE_MODIFY:
                //47 01 04 00 C8 FD C7 EF
                final String addName = packet.readMapleAsciiString();
                if (addName.getBytes(Charset.forName("GBK")).length > 19) {
                    return;
                }
                if (buddylist.isFull()) {
                    c.write(PacketCreator.BuddylistMessage(YOUR_LIST_FULL));
                    return;
                }
                final int channel = FindService.findChannel(addName, c.getWorld());
                if (channel <= 0) {
                    c.write(PacketCreator.BuddylistMessage(NONEXISTENT));
                    c.write(PacketCreator.EnableActions());
                    return;
                }
                final Player otherChar = c.getChannelServer().getPlayerStorage().getCharacterByName(addName);
                if (otherChar == null) {
                    c.write(PacketCreator.BuddylistMessage(NONEXISTENT));
                    c.write(PacketCreator.EnableActions());
                    return;
                }
                if (otherChar.getBuddylist().isFull()) {
                    c.write(PacketCreator.BuddylistMessage(THEIR_LIST_FULL));
                    return;
                }
                final BuddyAddResult buddyAddResult = BuddyService.requestBuddyAdd(addName, c.getPlayer());
                if (buddyAddResult == BuddyAddResult.BUDDYLIST_FULL) {
                    c.write(PacketCreator.BuddylistMessage(THEIR_LIST_FULL));
                    return;
                }
                if (buddyAddResult == BuddyAddResult.ALREADY_ON_LIST) {
                    c.write(PacketCreator.BuddylistMessage(DENY_ERROR));
                    c.write(PacketCreator.EnableActions());
                    return;
                }
                if (buddyAddResult == BuddyAddResult.NOT_FOUND) {
                    c.write(PacketCreator.BuddylistMessage(NONEXISTENT));
                    c.write(PacketCreator.EnableActions());
                    return;
                }
                c.getPlayer().dropMessage(1, "向 '" + addName + "' 发送了好友请求.");
                c.write(PacketCreator.EnableActions());
                break;
            case BUDDY_ACCEPT:
                final int otherCid = packet.readInt();
                /*boolean isOnPending_ = BuddyService.isBuddyPending(new MapleBuddyInvitedEntry(c.getPlayer().getName(), otherCid));
                if (!isOnPending_) {
                    c.write(PacketCreator.BuddylistMessage(DENY_ERROR));
                    return;
                }*/
                final Pair<BuddyAddResult, String> bal = BuddyService.acceptToInvite(c.getPlayer(), otherCid);
                if (bal.getLeft() == BuddyAddResult.NOT_FOUND) {
                    c.write(PacketCreator.BuddylistMessage(DENY_ERROR));
                    return;
                }
                if (bal.getLeft() == BuddyAddResult.BUDDYLIST_FULL) {
                    c.write(PacketCreator.BuddylistMessage(YOUR_LIST_FULL));
                    return;
                }
                c.getPlayer().dropMessage(1, "已添加 '" + bal.getRight() + "'为好友.");
                c.write(PacketCreator.EnableActions());
                break;
            case BUDDY_DELETE_DENY:
                final int otherCID = packet.readInt();
                /*boolean isInvited = BuddyService.isBuddyPending(new MapleBuddyInvitedEntry(c.getPlayer().getName(), otherCID));
                if (isInvited) {
                    c.getPlayer().dropMessage(5, BuddyService.denyToInvite(c.getPlayer(), otherCID));
                    c.write(PacketCreator.UpdateBuddylist(REMOVE, buddylist.getBuddies()));
                    c.write(PacketCreator.EnableActions());
                    return;
                }*/
                final MapleBuddyListEntry blz = buddylist.get(otherCID);
                if (blz == null) {
                    c.write(PacketCreator.BuddylistMessage(DENY_ERROR));
                    c.write(PacketCreator.EnableActions());
                    return;
                }
                final MapleBuddyList.BuddyDelResult bdr = BuddyService.DeleteBuddy(c.getPlayer(), otherCID);
                if (bdr == MapleBuddyList.BuddyDelResult.NOT_ON_LIST) {
                    c.write(PacketCreator.BuddylistMessage(DENY_ERROR));
                    c.write(PacketCreator.EnableActions());
                    return;
                }
                if (bdr == MapleBuddyList.BuddyDelResult.IN_CASH_SHOP) {
                    c.getPlayer().dropMessage(5, "当前角色处于商城，无法进行操作.");
                    c.write(PacketCreator.EnableActions());
                    return;
                }
                if (bdr == MapleBuddyList.BuddyDelResult.ERROR) {
                    c.write(PacketCreator.BuddylistMessage(DENY_ERROR));
                    c.write(PacketCreator.EnableActions());
                    return;
                }
                c.getPlayer().dropMessage(5, "和 '" + blz.getName() + "'解除好友关系.");
                c.write(PacketCreator.EnableActions());
                break;
            default:
                System.out.println("Unknown buddylist action: " + packet.toString());
                break;
        }
    }
}
