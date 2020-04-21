package handling.world.service;

import client.player.Player;
import community.MapleBuddyInvitedEntry;
import community.MapleBuddyList;
import community.MapleBuddyListEntry;
import handling.channel.ChannelServer;
import handling.channel.handler.ChannelHeaders;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import launch.Start;
import packet.creators.PacketCreator;
import tools.FileLogger;
import tools.Pair;

public class BuddyService {

    private static final List<MapleBuddyInvitedEntry> buddyInvited = new LinkedList<>();
    private static final ReentrantReadWriteLock buddyLock = new ReentrantReadWriteLock();
    private static long lastPruneTime;

    public static boolean canPrune(long now) {
        return (lastPruneTime + (20 * 60 * 1000)) < now;
    }

    public static void prepareRemove() {
        final long now = System.currentTimeMillis();
        lastPruneTime = now;
        Iterator<MapleBuddyInvitedEntry> itr = buddyInvited.iterator();
        MapleBuddyInvitedEntry inv;
        while (itr.hasNext()) {
            inv = itr.next();
            if (now >= inv.expiration) {
                itr.remove();
            }
        }
    }

    public static boolean isBuddyPending(final MapleBuddyInvitedEntry inv) {
        buddyLock.readLock().lock();
        try {
            if (buddyInvited.contains(inv)) {
                return true;
            }
        } finally {
            buddyLock.readLock().unlock();
        }
        return false;
    }

    public static MapleBuddyList.BuddyAddResult requestBuddyAdd(String addName, Player inviter) {
        int ch = FindService.findChannel(addName, inviter.getWorldId());
        if (ch > 0) {
            final Player addChar = inviter.getWorld().getChannelById(ch).getPlayerStorage().getCharacterByName(addName);
            if (addChar != null) {
                final MapleBuddyList buddylist = addChar.getBuddylist();
                if (buddylist.isFull()) {
                    return MapleBuddyList.BuddyAddResult.BUDDYLIST_FULL;
                }
                if (buddylist.contains(inviter.getId())) {
                    return MapleBuddyList.BuddyAddResult.ALREADY_ON_LIST;
                }
                buddyLock.writeLock().lock();
                try {
                    buddyInvited.add(new MapleBuddyInvitedEntry(addChar.getName(), inviter.getId()));
                } finally {
                    buddyLock.writeLock().unlock();
                }
                addChar.getClient().write(PacketCreator.RequestBuddylistAdd(inviter.getId(), inviter.getName(), inviter.getChannelServer().getChannel()));
                return MapleBuddyList.BuddyAddResult.OK;
            }
        }
        return MapleBuddyList.BuddyAddResult.NOT_FOUND;
    }

    public static Pair<MapleBuddyList.BuddyAddResult, String> acceptToInvite(Player chr, int inviterCid) {
        Iterator<MapleBuddyInvitedEntry> itr = buddyInvited.iterator();
        while (itr.hasNext()) {
            MapleBuddyInvitedEntry inv = itr.next();
            if (inviterCid == inv.inviter && chr.getName().equalsIgnoreCase(inv.name)) {
                itr.remove();
                if (chr.getBuddylist().isFull()) {
                    return new Pair<>(MapleBuddyList.BuddyAddResult.BUDDYLIST_FULL, null);
                }
                final int ch = FindService.findChannel(inviterCid, chr.getWorldId());
                if (ch > 0) {
                    final Player addChar = chr.getWorld().getChannelById(ch).getPlayerStorage().getCharacterById(inviterCid);
                    if (addChar == null) {
                        return new Pair<>(MapleBuddyList.BuddyAddResult.NOT_FOUND, null);
                    }
                    addChar.getBuddylist().put(new MapleBuddyListEntry(chr.getName(), chr.getId(), chr.getClient().getChannel()));
                    addChar.getClient().write(PacketCreator.UpdateBuddylist(ChannelHeaders.BuddyListHeaders.ADD, addChar.getBuddylist().getBuddies()));

                    chr.getBuddylist().put(new MapleBuddyListEntry(addChar.getName(), addChar.getId(), ch));
                    chr.getClient().write(PacketCreator.UpdateBuddylist(ChannelHeaders.BuddyListHeaders.ADD, chr.getBuddylist().getBuddies()));

                    return new Pair<>(MapleBuddyList.BuddyAddResult.OK, addChar.getName());
                }
            }
        }
        return new Pair<>(MapleBuddyList.BuddyAddResult.NOT_FOUND, null);
    }

    public static String denyToInvite(Player p, int inviterCid) {
        Iterator<MapleBuddyInvitedEntry> itr = buddyInvited.iterator();
        while (itr.hasNext()) {
            MapleBuddyInvitedEntry inv = itr.next();
            if (inviterCid == inv.inviter && p.getName().equalsIgnoreCase(inv.name)) {
                itr.remove();
                final int ch = FindService.findChannel(inviterCid, p.getWorldId());
                if (ch > 0) {
                    final Player addChar = p.getWorld().getChannelById(ch).getPlayerStorage().getCharacterById(inviterCid);
                    if (addChar == null) {
                        return "你拒绝了好友请求.";
                    }
                    addChar.dropMessage(5, p.getName() + " 玩家拒绝了你的好友请求.");
                    return "你拒绝了 '" + addChar.getName() + "'玩家的好友请求";
                }
            }
        }
        return "你拒绝了好友请求.";
    }

    public static MapleBuddyList.BuddyDelResult DeleteBuddy(Player p, int deleteCid) {
        final MapleBuddyListEntry myBlz = p.getBuddylist().get(deleteCid);
        if (myBlz == null) {
            return MapleBuddyList.BuddyDelResult.NOT_ON_LIST;
        }
        final int ch = FindService.findChannel(deleteCid, p.getWorldId());
        if (ch == -20 || ch == -10) {
            return MapleBuddyList.BuddyDelResult.IN_CASH_SHOP;
        }
        if (ch > 0) {
            final Player delChar = p.getWorld().getChannelById(ch).getPlayerStorage().getCharacterById(deleteCid);
            if (delChar == null) {
                final int ch_ = FindService.findChannel(deleteCid, p.getWorldId());
                if (ch_ == -20 || ch_ == -10) {
                    return MapleBuddyList.BuddyDelResult.IN_CASH_SHOP;
                }
                if (ch_ <= 0) {
                    final byte result = deleteOfflineBuddy(deleteCid, p.getId());
                    if (result == -1) {
                        return MapleBuddyList.BuddyDelResult.ERROR;
                    }
                    p.getBuddylist().remove(deleteCid);
                    p.getClient().write(PacketCreator.UpdateBuddylist(ChannelHeaders.BuddyListHeaders.REMOVE, p.getBuddylist().getBuddies()));
                    return MapleBuddyList.BuddyDelResult.OK;
                }
            }
            if (delChar != null) {
                delChar.getBuddylist().remove(p.getId());
                delChar.getClient().write(PacketCreator.UpdateBuddylist(ChannelHeaders.BuddyListHeaders.REMOVE, delChar.getBuddylist().getBuddies()));
                delChar.dropMessage(5, "'" + p.getName() + "' 和你解除了好友关系.");

                p.getBuddylist().remove(deleteCid);
                p.getClient().write(PacketCreator.UpdateBuddylist(ChannelHeaders.BuddyListHeaders.REMOVE, p.getBuddylist().getBuddies()));
                return MapleBuddyList.BuddyDelResult.OK;
            } else {
                return MapleBuddyList.BuddyDelResult.ERROR;
            }
        } else {
            final byte result = deleteOfflineBuddy(deleteCid, p.getId());
            if (result == -1) {
                return MapleBuddyList.BuddyDelResult.ERROR;
            }
            p.getBuddylist().remove(deleteCid);
            p.getClient().write(PacketCreator.UpdateBuddylist(ChannelHeaders.BuddyListHeaders.REMOVE, p.getBuddylist().getBuddies()));
            return MapleBuddyList.BuddyDelResult.OK;
        }
    }

    public static byte deleteOfflineBuddy(final int delId, final int myId) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("DELETE from `buddyentries` WHERE `owner` = ? AND `buddyid` = ?");
            ps.setInt(1, delId);
            ps.setInt(2, myId);
            ps.executeUpdate();
            ps.close();
            ps = con.prepareStatement("DELETE from `buddyentries` WHERE `owner` = ? AND `buddyid` = ?");
            ps.setInt(1, myId);
            ps.setInt(2, delId);
            ps.executeUpdate();
            ps.close();
            return 0;
        } catch (SQLException e) {
            System.out.println("Error deleting buddy");
            FileLogger.printError("deleteOfflineBuddy.txt", e);
            return -1;
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
    }

    public static void buddyChat(Player player, int[] recipientCharacterIds, int cidFrom, String nameFrom, String chattext) {
        for (int characterId : recipientCharacterIds) {
            int ch = FindService.findChannel(characterId, player.getWorldId());
            if (ch > 0) {
                Player p = player.getWorld().getChannelById(ch).getPlayerStorage().getCharacterById(characterId);
                if (p != null) {
                    p.getClient().write(PacketCreator.PrivateChatMessage(nameFrom, chattext, 0));
                }
            }
        }
    }

    private static void updateBuddies(int world, int characterId, int channel, int[] buddies, boolean offline, int gmLevel, boolean isHidden) {
        for (int buddy : buddies) {
            int ch = FindService.findChannel(buddy, world);
            if (ch > 0) {
                Player p = Start.getInstance().getWorldById(world).getChannelById(ch).getPlayerStorage().getCharacterById(buddy);
                if (p != null) {
                    MapleBuddyListEntry ble = p.getBuddylist().get(characterId);
                    if (ble != null) {
                        int mcChannel;
                        if (offline || (isHidden && p.getAdministrativeLevel() < gmLevel)) {
                            ble.setChannel(-1);
                            mcChannel = -1;
                        } else {
                            ble.setChannel(channel);
                            mcChannel = channel - 1;
                        }
                        p.getBuddylist().put(ble);
                        p.getClient().write(PacketCreator.UpdateBuddyChannel(ble.getCharacterId(), mcChannel));
                    }
                }
            }
        }
    }

    public static void loggedOn(int world, String name, int characterId, int channel, int[] buddies, int gmLevel, boolean isHidden) {
        updateBuddies(world, characterId, channel, buddies, false, gmLevel, isHidden);
    }

    public static void loggedOff(int world, String name, int characterId, int channel, int[] buddies, int gmLevel, boolean isHidden) {
        updateBuddies(world, characterId, channel, buddies, true, gmLevel, isHidden);
    }
}
