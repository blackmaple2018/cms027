package handling.world.service;

import handling.world.CharacterIdChannelPair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import launch.Start;

public class FindService {

    private static final List<FindObserve> finder = new ArrayList<>();
    private static final ReentrantReadWriteLock findLock = new ReentrantReadWriteLock();

    public static void register(int id, String name, int world, int channel) {
        findLock.writeLock().lock();
        try {
            finder.add(new FindObserve(world, channel + 1, id, name.toLowerCase()));
        } finally {
            findLock.writeLock().unlock();
        }
    }

    public static void forceDeregister(String name, int world) {
        findLock.writeLock().lock();
        try {
            FindObserve observe = getObserveByName(name, world);
            if (observe != null) {
                finder.remove(observe);
            }
        } finally {
            findLock.writeLock().unlock();
        }
    }

    public static void forceDeregister(int id, int world) {
        findLock.writeLock().lock();
        try {
            FindObserve observe = getObserveById(id, world);
            if (observe != null) {
                finder.remove(observe);
            }
        } finally {
            findLock.writeLock().unlock();
        }
    }

    private static FindObserve getObserveByName(String name, int world) {
        return finder.stream().filter(f -> name.toLowerCase().equals(f.getName()) && f.getWorld() == world).findAny().orElse(null);
    }

    private static FindObserve getObserveById(int id, int world) {
        return finder.stream().filter(f -> f.getCharId() == id && f.getWorld() == world).findAny().orElse(null);
    }

    public static int findChannel(int id, int world) {
        Integer ret = null;
        findLock.readLock().lock();
        try {
            FindObserve observe = getObserveById(id, world);
            if (observe != null) {
                ret = observe.getChannel();
            }
        } finally {
            findLock.readLock().unlock();
        }
        if (ret != null) {
            if (ret != -10 && ret != -20 && Start.getInstance().getWorldById(world).getChannelById(ret) == null) {
                forceDeregister(id, world);
                return -1;
            }
            return ret;
        }
        return -1;
    }

    public static int findChannel(String st, int world) {
        Integer ret = null;
        findLock.readLock().lock();
        try {
            FindObserve observe = getObserveByName(st, world);
            if (observe != null) {
                ret = observe.getChannel();
            }
        } finally {
            findLock.readLock().unlock();
        }
        if (ret != null) {
            if (ret != -10 && ret != -20 && Start.getInstance().getWorldById(world).getChannelById(ret) == null) {
                forceDeregister(st, world);
                return -1;
            }
            return ret;
        }
        return -1;
    }

    public static CharacterIdChannelPair[] multiBuddyFind(int world, int charIdFrom, int[] characterIds) {
        List<CharacterIdChannelPair> foundsChars = new ArrayList<>(characterIds.length);
        for (int i : characterIds) {
            int channel = findChannel(i, world);
            if (channel > 0) {
                foundsChars.add(new CharacterIdChannelPair(i, channel));
            }
        }
        Collections.sort(foundsChars);
        return foundsChars.toArray(new CharacterIdChannelPair[foundsChars.size()]);
    }
}
