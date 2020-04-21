package security.violation;

import java.util.LinkedHashSet;
import java.util.Set;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import tools.TimerTools.CheatTrackerTimer;

public class CheatingOffensePersister {
	
    private final Lock mutex = new ReentrantLock();
    private final Set<CheatingOffenseEntry> toPersist = new LinkedHashSet<>();
    private final static CheatingOffensePersister instance = new CheatingOffensePersister();

    private CheatingOffensePersister() {
        CheatTrackerTimer.getInstance().register(new PersistingTask(), 61000);
    }

    public static CheatingOffensePersister getInstance() {
        return instance;
    }
	
    public void persistEntry(CheatingOffenseEntry coe) {
        mutex.lock();
        try {
            toPersist.remove(coe); 
            toPersist.add(coe);
        } finally {
            mutex.unlock();
        }
    }
	
    public class PersistingTask implements Runnable {

        @Override
        public void run() {
            mutex.lock();
            try {
                toPersist.clear();
            } finally {
                mutex.unlock();
            }
        }
    }
}
