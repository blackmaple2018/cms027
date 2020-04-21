package handling.channel;

import handling.world.CheaterData;
import handling.world.service.FindService;
import client.player.Player;
import java.util.*;
import client.player.PlayerStringUtil;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PlayerStorage {

    private final ReentrantReadWriteLock pStorageLock = new ReentrantReadWriteLock();
    private final Map<String, Player> nameToChar = new HashMap<>();
    private final Map<Integer, Player> idToChar = new HashMap<>();
    private final int channel;
    private final int world;

    public PlayerStorage(int world, int channel) {
        this.channel = channel;
        this.world = world;
    }

    public final ArrayList<Player> getAllCharacters() {
        pStorageLock.readLock().lock();
        try {
            return new ArrayList<>(idToChar.values());
        } finally {
            pStorageLock.readLock().unlock();
        }
    }

    public final void registerPlayer(final Player chr) {
        pStorageLock.writeLock().lock();
        try {
            nameToChar.put(chr.getName().toLowerCase(), chr);
            idToChar.put(chr.getId(), chr);
        } finally {
            pStorageLock.writeLock().unlock();
        }
        FindService.register(chr.getId(), chr.getName(), world, channel);
    }

    public final void deregisterPlayer(final Player chr) {
        pStorageLock.writeLock().lock();
        try {
            nameToChar.remove(chr.getName().toLowerCase());
            idToChar.remove(chr.getId());
        } finally {
            pStorageLock.writeLock().unlock();
        }
        FindService.forceDeregister(chr.getId(), world);
    }

    public final void deregisterPlayer(final int idz, final String namez) {
        pStorageLock.writeLock().lock();
        try {
            nameToChar.remove(namez.toLowerCase());
            idToChar.remove(idz);
        } finally {
            pStorageLock.writeLock().unlock();
        }
        FindService.forceDeregister(idz, world);
    }

    public final Player getCharacterByName(final String name) {
        pStorageLock.readLock().lock();
        try {
            return nameToChar.get(name.toLowerCase());
        } finally {
            pStorageLock.readLock().unlock();
        }
    }

    public final Player getCharacterById(final int id) {
        pStorageLock.readLock().lock();
        try {
            return idToChar.get(id);
        } finally {
            pStorageLock.readLock().unlock();
        }
    }

    public final int getConnectedClients() {
        return idToChar.size();
    }

    public final int getCheatersSize() {
        int size = 0;
        pStorageLock.readLock().lock();
        try {
            final Iterator<Player> itr = nameToChar.values().iterator();
            Player chr;
            while (itr.hasNext()) {
                chr = itr.next();

                if (chr.getCheatTracker().getPoints() > 0) {
                    size++;
                }
            }
        } finally {
            pStorageLock.readLock().unlock();
        }
        return size;
    }

    public final List<CheaterData> getCheaters() {
        final List<CheaterData> cheaters = new ArrayList<>();

        pStorageLock.readLock().lock();
        try {
            final Iterator<Player> itr = nameToChar.values().iterator();
            Player chr;
            while (itr.hasNext()) {
                chr = itr.next();

                if (chr.getCheatTracker().getPoints() > 0) {
                    cheaters.add(new CheaterData(chr.getCheatTracker().getPoints(), PlayerStringUtil.makeMapleReadable(chr.getName()) + " (" + chr.getCheatTracker().getPoints() + ") " + chr.getCheatTracker().getSummary()));
                }
            }
        } finally {
            pStorageLock.readLock().unlock();
        }
        return cheaters;
    }

    public final void disconnectAll() {
        disconnectAll(false);
    }

    public final void disconnectAll(final boolean checkGM) {
        pStorageLock.writeLock().lock();
        try {
            final Iterator<Player> itr = nameToChar.values().iterator();
            Player chr;
            while (itr.hasNext()) {
                chr = itr.next();

                if (!chr.isGameMaster() || !checkGM) {
                    chr.getClient().disconnect(false, false);
                    chr.getClient().close();
                    FindService.forceDeregister(chr.getId(), world);
                    itr.remove();
                }
            }
        } finally {
            pStorageLock.writeLock().unlock();
        }
    }

    public final void disconnectAlll() {
        pStorageLock.writeLock().lock();
        try {
            final Iterator<Player> itr = nameToChar.values().iterator();
            Player chr;
            while (itr.hasNext()) {
                chr = itr.next();
                chr.getClient().disconnect(false, false);
                chr.getClient().close();
                FindService.forceDeregister(chr.getId(), world);
                itr.remove();
            }
        } finally {
            pStorageLock.writeLock().unlock();
        }
    }

    public final String getOnlinePlayers(final boolean byGM) {
        final StringBuilder sb = new StringBuilder();

        if (byGM) {
            pStorageLock.readLock().lock();
            try {
                final Iterator<Player> itr = nameToChar.values().iterator();
                while (itr.hasNext()) {
                    sb.append(PlayerStringUtil.makeMapleReadable(itr.next().getName()));
                    sb.append(", ");
                }
            } finally {
                pStorageLock.readLock().unlock();
            }
        } else {
            pStorageLock.readLock().lock();
            try {
                final Iterator<Player> itr = nameToChar.values().iterator();
                Player chr;
                while (itr.hasNext()) {
                    chr = itr.next();

                    if (!chr.isGameMaster()) {
                        sb.append(PlayerStringUtil.makeMapleReadable(chr.getName()));
                        sb.append(", ");
                    }
                }
            } finally {
                pStorageLock.readLock().unlock();
            }
        }
        return sb.toString();
    }

    public final void broadcastPacket(final byte[] data) {
        pStorageLock.readLock().lock();
        try {
            final Iterator<Player> itr = nameToChar.values().iterator();
            while (itr.hasNext()) {
                itr.next().getClient().write(data);
            }
        } finally {
            pStorageLock.readLock().unlock();
        }
    }

    public final void broadcastSmegaPacket(final byte[] data) {
        pStorageLock.readLock().lock();
        try {
            final Iterator<Player> itr = nameToChar.values().iterator();
            Player chr;
            while (itr.hasNext()) {
                chr = itr.next();

                if (chr.getClient().isLoggedIn() && chr.getSmegaEnabled()) {
                    chr.getClient().write(data);
                }
            }
        } finally {
            pStorageLock.readLock().unlock();
        }
    }

    public final void broadcastGMPacket(final byte[] data) {
        pStorageLock.readLock().lock();
        try {
            final Iterator<Player> itr = nameToChar.values().iterator();
            Player chr;
            while (itr.hasNext()) {
                chr = itr.next();

                if (chr.getClient().isLoggedIn()) {
                    chr.getClient().write(data);
                }
            }
        } finally {
            pStorageLock.readLock().unlock();
        }
    }
}
