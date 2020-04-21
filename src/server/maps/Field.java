package server.maps;

import server.maps.object.FieldObjectType;
import server.maps.object.FieldObject;
import server.maps.reactors.Reactor;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import client.Client;
import community.MaplePartyOperation;
import server.life.status.MonsterStatus;
import server.life.status.MonsterStatusEffect;
import constants.GameConstants;
import constants.MapConstants;
import packet.transfer.write.OutPacket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import packet.creators.EffectPackets;
import packet.creators.MonsterPackets;
import packet.creators.PacketCreator;
import packet.creators.PartyPackets;
import packet.creators.PetPackets;
import client.player.Player;
import client.player.buffs.BuffStat;
import client.player.inventory.Equip;
import client.player.inventory.types.InventoryType;
import client.player.inventory.Item;
import client.player.inventory.ItemPet;
import static configure.Gamemxd.会员等级划分;
import static configure.Gamemxd.合伙群;
import static configure.Gamemxd.比例;
import constants.ItemConstants;
import static console.MsgServer.QQMsgServer.sendMsgToQQGroup;
import java.lang.ref.WeakReference;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import launch.Start;
import static launch.Start.大区;
import scripting.event.EventInstanceManager;
import server.MapleStatEffect;
import tools.TimerTools.MapTimer;
import server.PropertiesTable;
import server.itens.ItemInformationProvider;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleMonsterInformationProvider;
import server.life.MonsterDropEntry;
import server.life.MonsterGlobalDropEntry;
import server.life.npc.MapleNPC;
import server.life.SpawnPoint;
import server.life.components.SelfDestruction;
//import server.life.SpawnPointAreaBoss;
import server.maps.object.FieldDoorObject;
import tools.Pair;
import server.maps.portal.Portal;
import server.partyquest.mcpq.MCWZData;
import tools.FileLogger;
import tools.Randomizer;

public class Field {

    private final Map<FieldObjectType, LinkedHashMap<Integer, FieldObject>> mapObjects;
    private final Map<FieldObjectType, ReentrantReadWriteLock> mapobjectlocks;
    private final Set<Player> characters = new LinkedHashSet<>();
    private final ReentrantReadWriteLock charactersLock = new ReentrantReadWriteLock();

    private final Collection<SpawnPoint> monsterSpawn = Collections.synchronizedList(new LinkedList<SpawnPoint>());
    private final Collection<SpawnPoint> allMonsterSpawn = Collections.synchronizedList(new LinkedList<SpawnPoint>());
    private final Map<Integer, Set<Integer>> mapParty = new LinkedHashMap<>();
    private final AtomicInteger spawnedMonstersOnMap = new AtomicInteger(0);
    private final Map<Integer, Portal> portals = new LinkedHashMap<>();
    private final List<Rectangle> areas = new ArrayList<>();
    private MapleFootholdTree footholds = null;
    private final AtomicInteger runningOid = new AtomicInteger(1000000);
    private final int mapId;
    private int fieldLimit = 0;
    private int lastDoorOwner = -1;
    private int forcedReturnMap = MapConstants.NULL_MAP;
    private final int dropLife = 180000;
    private int decHPInterval = 10000;
    private int timeLimit;
    private int decHP = 0;
    private int protectItem = 0;
    private int returnFieldId;
    private final int channel;
    private final int world;
    private float monsterRate;
    private boolean clock;
    private boolean boat;
    private boolean town;
    protected boolean swim;
    private boolean docked = false;
    private boolean timer = false;
    private boolean respawning = true;
    private boolean disablePortal = false;
    private boolean disableInvincibilitySkills = false;
    private boolean disableDamage = false;
    private boolean disableChat = false;
    private boolean dropsDisabled = false, isSpawns = true, everlast = false;
    public boolean respawnMonsters = true;
    public boolean gDropsDisabled = false;
    private long lastHurtTime = 0;
    private final short mobInterval = 5000;
    private String mapName, streetName;
    private FieldEffect mapEffect = null;
    private FieldTimer mapTimer = null;
    private ScheduledFuture<?> sfme = null;
    private Pair<Integer, String> timeMob = null;
    private EventInstanceManager event = null;
    private MCWZData mcpqData;
    private final PropertiesTable properties;
    private boolean allowSummons = true;
    private final Map<FieldItem, Long> droppedItems = new LinkedHashMap<>();
    private final LinkedList<WeakReference<FieldObject>> registeredDrops = new LinkedList<>();
    // [HPQ]
    private int riceCakes = 0;
    private int bunnyDamage = 0;
    public Map<Integer, Integer> reactorLink = new HashMap<>();
    private boolean 燃烧地区 = false;
    public static Map<Integer, Integer> 检测坐标 = new HashMap<>();

    public Field(final int mapid, final int world, final int channel, final int returnMapId, final float monsterRate) {
        this.mapId = mapid;
        this.world = world;
        this.channel = channel + 1;
        this.returnFieldId = returnMapId;
        if (this.returnFieldId == MapConstants.NULL_MAP) {
            this.returnFieldId = mapid;
        }
        this.monsterRate = (byte) Math.round(monsterRate);
        if (this.monsterRate == 0) {
            this.monsterRate = 1;
        }
        this.properties = new PropertiesTable();
        properties.setProperty("mute", Boolean.FALSE);
        EnumMap<FieldObjectType, LinkedHashMap<Integer, FieldObject>> objsMap = new EnumMap<>(FieldObjectType.class);
        EnumMap<FieldObjectType, ReentrantReadWriteLock> objlockmap = new EnumMap<>(FieldObjectType.class);
        for (FieldObjectType type : FieldObjectType.values()) {
            objsMap.put(type, new LinkedHashMap<>());
            objlockmap.put(type, new ReentrantReadWriteLock());
        }
        mapObjects = Collections.unmodifiableMap(objsMap);
        mapobjectlocks = Collections.unmodifiableMap(objlockmap);
    }

    public final void setSpawns(final boolean fm) {
        this.isSpawns = fm;
    }

    public final boolean getSpawns() {
        return isSpawns;
    }

    public final boolean canSpawn() {
        return isSpawns;
    }

    public PropertiesTable getProperties() {
        return this.properties;
    }

    public void setMonsterRate(float monsterRate) {
        this.monsterRate = monsterRate;
    }

    public float getMonsterRate() {
        return monsterRate;
    }

    public int getChannel() {
        return channel;
    }

    public boolean canDelete() {
        return this.characters.isEmpty() && Start.getInstance().getWorldById(world).getChannelById(getChannel()).getMapFactory().isMapLoaded(mapId);
    }

    public void setFieldLimit(int fieldLimit) {
        this.fieldLimit = fieldLimit;
    }

    public int getFieldLimit() {
        return fieldLimit;
    }

    public final void toggleGDrops() {
        this.gDropsDisabled = !gDropsDisabled;
    }

    public void clearDrops() {
        for (FieldObject i : getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(FieldObjectType.ITEM))) {
            removeMapObject(i);
        }
    }

    public void killFriendlies(MapleMonster mob) {
        this.killMonster(mob, (Player) getAllPlayer().get(0), false);
    }

    public void killAllMonstersNotFriendly() {
        for (FieldObject monstermo : getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(FieldObjectType.MONSTER))) {
            MapleMonster monster = (MapleMonster) monstermo;
            if (monster.getStats().isFriendly()) {
                continue;
            }
            spawnedMonstersOnMap.decrementAndGet();
            monster.setHp(0);
            broadcastMessage(MonsterPackets.KillMonster(monster.getObjectId(), true));
            removeMapObject(monster);
        }
    }

    public boolean toggleDrops() {
        dropsDisabled = !dropsDisabled;
        return dropsDisabled;
    }

    public int getId() {
        return mapId;
    }

    public Field getReturnField() {
        int returnMap = -1;
        if (returnFieldId != MapConstants.NULL_MAP) {
            returnMap = returnFieldId;
        } else if (forcedReturnMap != MapConstants.NULL_MAP) {
            returnMap = forcedReturnMap;
        } else {
            returnMap = 100000000;
        }
        try {
            return Start.getInstance().getWorldById(world).getChannelById(channel).getMapFactory().getMap(returnMap);
        } catch (Exception ex) {
            return null;
        }
    }

    public int getReturnMapId() {
        return returnFieldId;
    }

    public int getForcedReturnId() {
        return forcedReturnMap;
    }

    public Field getForcedReturnField() {
        if (forcedReturnMap == MapConstants.NULL_MAP) {
            return getReturnField();
        }
        return Start.getInstance().getWorldById(world).getChannelById(channel).getMapFactory().getMap(forcedReturnMap);
    }

    public void setForcedReturnField(int map) {
        this.forcedReturnMap = map;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public final int getCurrentPartyId() {
        charactersLock.readLock().lock();
        try {
            final Iterator<Player> ltr = characters.iterator();
            Player p;
            while (ltr.hasNext()) {
                p = ltr.next();
                if (p.getPartyId() != -1) {
                    return p.getPartyId();
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        return -1;
    }

    public int getNewObjectID() {
        return runningOid.getAndIncrement();
    }

    public void addMapObject(FieldObject mapobject) {
        mapobject.setObjectId(getNewObjectID());
        mapobjectlocks.get(mapobject.getType()).writeLock().lock();
        try {
            this.mapObjects.get(mapobject.getType()).put(mapobject.getObjectId(), mapobject);
        } finally {
            mapobjectlocks.get(mapobject.getType()).writeLock().unlock();
        }
    }

    private void spawnAndAddRangedMapObject(FieldObject mapobject, DelayedPacketCreation packetbakery) {
        addMapObject(mapobject);
        charactersLock.readLock().lock();
        try {
            final Iterator<Player> itr = characters.iterator();
            Player p;
            while (itr.hasNext()) {
                p = itr.next();
                if (p.getPosition().distanceSq(mapobject.getPosition()) <= getRangedDistance()) {
                    packetbakery.sendPackets(p.getClient());
                    p.addVisibleMapObject(mapobject);
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
    }

    public void removeMapObject(FieldObject mapobject) {
        mapobjectlocks.get(mapobject.getType()).writeLock().lock();
        try {
            this.mapObjects.get(mapobject.getType()).remove(mapobject.getObjectId());
        } finally {
            mapobjectlocks.get(mapobject.getType()).writeLock().unlock();
        }
    }

    private static double getRangedDistance() {
        return Double.POSITIVE_INFINITY;
    }

    private Point calcPointBelow(Point initial) {
        MapleFoothold fh = footholds.findBelow(initial);
        if (fh == null) {
            return null;
        }
        int dropY = fh.getY1();
        if (!fh.isWall() && fh.getY1() != fh.getY2()) {
            double s1 = Math.abs(fh.getY2() - fh.getY1());
            double s2 = Math.abs(fh.getX2() - fh.getX1());
            double s5 = Math.cos(Math.atan(s2 / s1)) * (Math.abs(initial.x - fh.getX1()) / Math.cos(Math.atan(s1 / s2)));
            if (fh.getY2() < fh.getY1()) {
                dropY = fh.getY1() - (int) s5;
            } else {
                dropY = fh.getY1() + (int) s5;
            }
        }
        return new Point(initial.x, dropY);
    }

    public final Point calcDropPos(final Point initial, final Point fallback) {
        final Point ret = calcPointBelow(new Point(initial.x, initial.y - 50));
        if (ret == null) {
            return fallback;
        }
        return ret;
    }

    public void setReactorState(final byte state) {
        mapobjectlocks.get(FieldObjectType.REACTOR).readLock().lock();
        try {
            for (FieldObject o : mapObjects.get(FieldObjectType.REACTOR).values()) {
                if (o.getType() == FieldObjectType.REACTOR) {
                    if (((Reactor) o).getState() < 1) {
                        Reactor mr = (Reactor) o;
                        mr.setState(state);
                        broadcastMessage(PacketCreator.TriggerReactor((Reactor) o, 1));
                    }
                }
            }
        } finally {
            mapobjectlocks.get(FieldObjectType.REACTOR).readLock().unlock();
        }
    }

    public void setReactorState() {
        setReactorState((byte) 1);
    }

    public void setReactorState(Reactor reactor, byte state) {
        mapobjectlocks.get(FieldObjectType.REACTOR).readLock().lock();
        try {
            for (FieldObject o : mapObjects.get(FieldObjectType.REACTOR).values()) {
                if (o.getType() == FieldObjectType.REACTOR && o == reactor) {
                    if (reactor.getState() < 1) {
                        reactor.forceHitReactor((byte) state);
                        broadcastMessage(PacketCreator.TriggerReactor((Reactor) reactor, 1));
                    }
                }
            }
        } finally {
            mapobjectlocks.get(FieldObjectType.REACTOR).readLock().unlock();
        }
    }

    /**
     * <怪物爆物>*
     */
    private void dropFromMonster(final Player p, final MapleMonster mob) {
        if (mob == null || p == null || Start.getInstance().getWorldById(world).getChannelById(channel) == null || dropsDisabled || mob.dropsDisabled()) {
            return;
        }

        /*if (itemCount() >= 500) {
            return;
        }*/
        byte d = 1;
        final byte droptype = (byte) (mob.getStats().isExplosive() ? 3 : mob.getStats().isPublicReward() ? 2 : p.getParty() != null ? 1 : 0);

        int mobPos = mob.getPosition().x;
        //频道暴率
        int dropRate = Start.getInstance().getWorldById(world).getChannelById(channel).getDropRate();
        //会员加暴率
        if (p.getvip() > 0) {
            dropRate += 0.2 + ((会员等级划分(p.getvip()) - 1) * 0.2);
        }
        Point pos = new Point(0, mob.getPosition().y);
        Map<MonsterStatus, MonsterStatusEffect> stati = mob.getStati();

        if (stati.containsKey(MonsterStatus.MAGIC_DEFENSE_UP)) {
            dropRate *= (stati.get(MonsterStatus.MAGIC_DEFENSE_UP).getStati().get(MonsterStatus.MAGIC_DEFENSE_UP).doubleValue() / 100.0 + 1.0);
        }

        final MapleMonsterInformationProvider mi = MapleMonsterInformationProvider.getInstance();

        final List<MonsterDropEntry> dropEntry = new ArrayList<>();
        final List<MonsterDropEntry> visibleQuestEntry = new ArrayList<>();
        final List<MonsterDropEntry> otherQuestEntry = new ArrayList<>();

        sortDropEntries(mi.retrieveEffectiveDrop(mob.getId()), dropEntry, visibleQuestEntry, otherQuestEntry, p);

        // Normal Drops
        d = dropItemsFromMonsterOnMap(dropEntry, pos, d, dropRate, droptype, mobPos, p, mob);

        //  Global Drops
        final List<MonsterGlobalDropEntry> globalEntry = mi.getGlobalDrop();
        d = dropGlobalItemsFromMonsterOnMap(p.getEventInstance() != null, globalEntry, pos, d, droptype, mobPos, p, mob);

        // Quest Drops
        d = dropItemsFromMonsterOnMap(visibleQuestEntry, pos, d, dropRate, droptype, mobPos, p, mob);
        dropItemsFromMonsterOnMap(otherQuestEntry, pos, d, dropRate, droptype, mobPos, p, mob);
    }

    private byte dropItemsFromMonsterOnMap(List<MonsterDropEntry> dropEntry, Point pos, byte d, int chRate, byte droptype, int mobpos, Player chr, MapleMonster mob) {
        if (dropEntry.isEmpty()) {
            return d;
        }

        Collections.shuffle(dropEntry);

        Item idrop;
        ItemInformationProvider ii = ItemInformationProvider.getInstance();

        for (final MonsterDropEntry de : dropEntry) {
            int dropChance = (int) Math.min((float) de.chance * chRate, Integer.MAX_VALUE);

            if (Randomizer.nextInt(999999) < dropChance) {
                if (droptype == 3) {
                    pos.x = (int) (mobpos + ((d % 2 == 0) ? (40 * ((d + 1) / 2)) : -(40 * (d / 2))));
                } else {
                    pos.x = (int) (mobpos + ((d % 2 == 0) ? (25 * ((d + 1) / 2)) : -(25 * (d / 2))));
                }
                if (de.itemId == 0) { // meso
                    int mesos = Randomizer.nextInt(de.Maximum - de.Minimum) + de.Minimum;
                    if (mesos > 0) {
                        if (chr.getBuffedValue(BuffStat.MesoUP) != null) {
                            mesos = (int) (mesos * chr.getBuffedValue(BuffStat.MesoUP).doubleValue() / 100.0);
                        }
                        if (mesos <= 0) {
                            mesos = Integer.MAX_VALUE;
                        }
                        spawnMesoDrop(mesos, calcDropPos(pos, mob.getPosition()), mob, chr, false, droptype);
                    }
                } else {
                    if (ItemConstants.getInventoryType(de.itemId) == InventoryType.EQUIP) {
                        idrop = ii.randomizeStats((Equip) ii.getEquipById(de.itemId));
                    } else {
                        idrop = new Item(de.itemId, (short) 0, (short) (de.Maximum != 1 ? Randomizer.nextInt(de.Maximum - de.Minimum) + de.Minimum : 1));
                    }
                    spawnDrop(idrop, calcDropPos(pos, mob.getPosition()), mob, chr, droptype, de.questid);
                }
                d++;
            }
        }
        return d;
    }

    private byte dropGlobalItemsFromMonsterOnMap(boolean event, List<MonsterGlobalDropEntry> globalEntry, Point pos, byte d, byte droptype, int mobpos, Player p, MapleMonster mob) {
        Collections.shuffle(globalEntry);

        Item idrop;
        ItemInformationProvider ii = ItemInformationProvider.getInstance();

        for (final MonsterGlobalDropEntry de : globalEntry) {
            if (Randomizer.nextInt(999999) < de.chance) {
                if (droptype == 3) {
                    pos.x = (int) (mobpos + (d % 2 == 0 ? (40 * (d + 1) / 2) : -(40 * (d / 2))));
                } else {
                    pos.x = (int) (mobpos + ((d % 2 == 0) ? (25 * (d + 1) / 2) : -(25 * (d / 2))));
                }
                if (de.itemId != 0 && !event) {
                    if (ItemConstants.getInventoryType(de.itemId) == InventoryType.EQUIP) {
                        idrop = ii.randomizeStats((Equip) ii.getEquipById(de.itemId));
                    } else {
                        idrop = new Item(de.itemId, (short) 0, (short) (de.Maximum != 1 ? Randomizer.nextInt(de.Maximum - de.Minimum) + de.Minimum : 1));
                    }
                    spawnDrop(idrop, calcDropPos(pos, mob.getPosition()), mob, p, droptype, de.questid);
                    d++;
                }
            }
        }
        return d;
    }

    private static void sortDropEntries(List<MonsterDropEntry> from, List<MonsterDropEntry> item, List<MonsterDropEntry> visibleQuest, List<MonsterDropEntry> otherQuest, Player p) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();

        for (MonsterDropEntry mde : from) {
            if (!ii.isQuestItem(mde.itemId)) {
                item.add(mde);
            } else {
                if (p.needQuestItem(mde.questid, mde.itemId)) {
                    visibleQuest.add(mde);
                } else {
                    otherQuest.add(mde);
                }
            }
        }
    }

    public final void removeDrops() {
        List<FieldItem> items = this.getAllItemsThreadsafe();
        for (FieldItem i : items) {
            i.expire(this);
        }
    }

    private int countDrops(List<Integer> theDrop, int dropID) {
        int count = 0;
        for (int i = 0; i < theDrop.size(); i++) {
            if (theDrop.get(i) == dropID) {
                count++;
            }
        }
        return count;
    }

    public int countReactorsOnField() {
        int count = 0;
        for (FieldObject m : getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(FieldObjectType.REACTOR))) {
            Reactor reactor = (Reactor) m;
            if (reactor instanceof Reactor) {
                count++;
            }
        }
        return count;
    }

    public int countMobOnField() {
        int count = 0;
        for (FieldObject m : getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(FieldObjectType.MONSTER))) {
            MapleMonster mob = (MapleMonster) m;
            if (mob instanceof MapleMonster) {
                count++;
            }
        }
        return count;
    }

    public int countMobOnField(int id) {
        int count = 0;
        for (FieldObject m : getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(FieldObjectType.MONSTER))) {
            MapleMonster mob = (MapleMonster) m;
            if (mob.getId() == id) {
                count++;
            }
        }
        return count;
    }

    public int countMonster(Player p) {
        Field field = p.getClient().getPlayer().getMap();
        double range = Double.POSITIVE_INFINITY;
        List<FieldObject> monsters = field.getMapObjectsInRange(p.getClient().getPlayer().getPosition(), range, Arrays.asList(FieldObjectType.MONSTER));
        return monsters.size();
    }

    public void warpField(Field field) {
        charactersLock.readLock().lock();
        try {
            for (Player p : this.characters) {
                if (p.isAlive()) {
                    p.changeMap(field, field.getPortal(0));
                } else {
                    p.changeMap(p.getMap().getReturnField(), field.getPortal(0));
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
    }

    public final int getNumPlayersInArea(final int index) {
        return getNumPlayersInRect(getArea(index));
    }

    public final int getNumPlayersInRect(final Rectangle rect) {
        int ret = 0;
        charactersLock.readLock().lock();
        try {
            final Iterator<Player> ltr = characters.iterator();
            while (ltr.hasNext()) {
                if (rect.contains(ltr.next().getPosition())) {
                    ret++;
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        return ret;
    }

    public final int getNumPlayersItemsInArea(final int index) {
        return getNumPlayersItemsInRect(getArea(index));
    }

    public final int getNumPlayersItemsInRect(final Rectangle rect) {
        int retP = getNumPlayersInRect(rect);
        int retI = getMapObjectsInBox(rect, Arrays.asList(FieldObjectType.ITEM)).size();

        return retP + retI;
    }

    public void spawnMonsterOnGroudBelow(MapleMonster mob, Point pos) {
        spawnMonsterOnGroundBelow(mob, pos);
    }

    public void spawnMonsterOnGroundBelow(MapleMonster mob, Point pos) {
        //Point spos = getGroundBelow(pos);
        mob.setPosition(pos);
        spawnMonster(mob);
    }

    public void spawnMonsterOnGroundBelow2(MapleMonster mob, Point pos) {
        mob.setPosition(pos);
        spawnMonster(mob);
    }

    public final void spawnZakum(Point pos) {
        Point spos = getGroundBelow(pos);
        final MapleMonster mainb = MapleLifeFactory.getMonster(8800001);
        mainb.setPosition(spos);
        spawnMonster(mainb);

        final int[] zakpart = {8800003, 8800004, 8800005, 8800006, 8800007,
            8800008, 8800009, 8800010};

        for (final int i : zakpart) {
            final MapleMonster part = MapleLifeFactory.getMonster(i);
            part.setPosition(spos);
            spawnMonster(part);
        }

    }

    public void spawnMonsterOnGroundBelow(int mobid, int x, int y) {
        MapleMonster mob = MapleLifeFactory.getMonster(mobid);
        if (mob != null) {
            Point point = new Point(x, y);
            spawnMonsterOnGroundBelow(mob, point);
        }
    }

    public void spawnMonsterOnGroundBelow(int mobid, int x, int y, int hp) {
        MapleMonster mob = MapleLifeFactory.getMonster(mobid);
        if (mob != null) {
            Point point = new Point(x, y);
            mob.setHp(hp);
            mob.setjt(false);
            spawnMonsterOnGroundBelow(mob, point);
        }
    }

    public void spawnMonsterOnGroudBelowXY(int x, int y, int mobid) {
        MapleMonster mob = MapleLifeFactory.getMonster(mobid);
        if (mob != null) {
            Point point = new Point(x, y);
            spawnMonsterOnGroundBelow(mob, point);
        }
    }

    public void spawnMonsterOnGroundBelow(int mobid, int x, int y, String msg) {
        MapleMonster mob = MapleLifeFactory.getMonster(mobid);
        if (mob != null) {
            Point point = new Point(x, y);
            spawnMonsterOnGroundBelow(mob, point);
            this.broadcastMessage(PacketCreator.ServerNotice(6, msg));
        }
    }

    public Point getGroundBelow(Point pos) {
        Point spos = new Point(pos.x, pos.y - 14);
        spos = calcPointBelow(spos);
        if (spos == null) {
            spos = pos;
        }
        spos.y--;
        return spos;
    }

    public void spawnZakum(MapleMonster mob, Point pos) {
        spawnFakeMonsterOnGroundBelow(new MapleMonster(mob), pos);
        ArrayList<Integer> theList = new ArrayList<>(8);
        theList.addAll(Arrays.asList(8800003, 8800004, 8800005, 8800006, 8800007, 8800008, 8800009, 8800010));
        for (int mid : theList) {
            MapleMonster monsterid = MapleLifeFactory.getMonster(mid);
            spawnMonsterOnGroundBelow(monsterid, pos);
        }
    }

    public boolean damageMonster(Player p, MapleMonster monster, int damage) {
        if (monster != null) {
            if (monster.getId() == 8800000) {
                for (FieldObject object : getMonsters()) {
                    MapleMonster mons = p.getMap().getMonsterByOid(object.getObjectId());
                    if (mons != null && mons.getId() >= 8800003 && mons.getId() <= 8800010) {
                        return true;
                    }
                }
            }
            if (monster.isAlive()) {
                boolean killMonster = false;
                if (!monster.isAlive()) {
                    return false;
                }
                if (damage > 0) {
                    int monsterhp = monster.getHp();
                    monster.damage(p, damage);
                    if (!monster.isAlive()) {
                        killMonster(monster, p, true);
                        if (monster.getId() >= 8810002 && monster.getId() <= 8810009) {
                            for (FieldObject mmo : getMonsters()) {
                                MapleMonster mons = p.getMap().getMonsterByOid(mmo.getObjectId());
                                if (mons != null) {
                                    if (mons.getId() == 8810018) {
                                        damageMonster(p, mons, monsterhp);
                                    }
                                }
                            }
                        }
                    } else {
                        if (monster.getId() >= 8810002 && monster.getId() <= 8810009) {
                            for (FieldObject mmo : getMonsters()) {
                                MapleMonster mons = p.getMap().getMonsterByOid(mmo.getObjectId());
                                if (mons != null) {
                                    if (mons.getId() == 8810018) {
                                        damageMonster(p, mons, damage);
                                    }
                                }
                            }
                        }
                    }
                }
                if (monster.getStats().selfDestruction() != null && monster.getStats().selfDestruction().getHp() > -1) {// should work ;p
                    if (monster.getHp() <= monster.getStats().selfDestruction().getHp()) {
                        killMonster(monster, p, true, monster.getStats().selfDestruction().getAction());
                        return true;
                    }
                }
                if (killMonster) {
                    killMonster(monster, p, true);
                }
                return true;
            }
        }
        return false;
    }

    public void killMonster(MapleMonster monster, Player p, boolean withDrops) {
        killMonster(monster, p, withDrops, 1);
    }

    public void killAllBoogies() {
        List<FieldObject> monsters = getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(FieldObjectType.MONSTER));
        for (final FieldObject monstermo : monsters) {
            final MapleMonster monster = (MapleMonster) monstermo;
            if (monster.getId() == 3230300 || monster.getId() == 3230301 || monster.getName().toLowerCase().contains("boogie")) {
                spawnedMonstersOnMap.decrementAndGet();
                monster.setHp(0);
                broadcastMessage(MonsterPackets.KillMonster(monster.getObjectId(), true));
                removeMapObject(monster);
            }
        }
        this.broadcastMessage(PacketCreator.ServerNotice(6, "当岩石破碎时，小布吉痛苦地摔倒了，消失了。"));
    }

    public void buffField(int buffID) {
        ItemInformationProvider mii = ItemInformationProvider.getInstance();
        MapleStatEffect statEffect = mii.getItemEffect(buffID);
        charactersLock.readLock().lock();
        try {
            for (Player character : this.characters) {
                if (character.isAlive()) {
                    statEffect.applyTo(character);
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
    }

    public void addClock(int seconds) {
        broadcastMessage(PacketCreator.GetClockTimer(seconds));
    }

    /**
     * <击杀怪物>
     */
    @SuppressWarnings("static-access")
    public void killMonster(final MapleMonster monster, final Player p, final boolean withDrops, int animation) {
        if (monster == null) {
            return;
        }
        if (p == null) {
            spawnedMonstersOnMap.decrementAndGet();
            monster.setHp(0);
            removeMapObject(monster);
            monster.dispatchMonsterKilled(false);
            broadcastMessage(MonsterPackets.KillMonster(monster.getObjectId(), animation));
            return;
        }

        int buff = monster.getBuffToGive();
        if (buff > -1) {
            for (FieldObject mmo : this.getAllPlayer()) {
                Player character = (Player) mmo;
                if (character.isAlive()) {
                    ItemInformationProvider mii = ItemInformationProvider.getInstance();
                    MapleStatEffect statEffect = mii.getItemEffect(monster.getBuffToGive());
                    statEffect.applyTo(character);
                }
            }
        }

        spawnedMonstersOnMap.decrementAndGet();
        monster.setHpZero();
        removeMapObject(monster);

        Player dropOwner = monster.killBy(p);
        if (withDrops && !monster.dropsDisabled()) {
            if (dropOwner == null) {
                dropOwner = p;
            }
            //爆物
            dropFromMonster(dropOwner, monster);
        }

        if (p.getLevel() >= 10) {
            if (!monster.isBoss()) {
                if (monster.getId() != 4130102 && monster.getId() != 4230105) {
                    if (monster.isjt()) {
                        if (p.getEquippedFuMoMap().get(27) != null) {
                            if (p.getEquippedFuMoMap().get(27) > 0) {
                                怨念丛生(monster, p, p.getEquippedFuMoMap().get(27));
                            }
                        }
                        if (p.getEquippedFuMoMap().get(29) != null) {
                            if (p.getEquippedFuMoMap().get(29) > 0) {
                                怨念爆发(monster, p, p.getEquippedFuMoMap().get(29));
                            }
                        }
                    }
                }
            }
        }
        //击杀扎昆主体后清图  //击杀巨型蝙蝠怪主体后清图
        if (monster.getId() == 8800002 || monster.getId() == 8830000 || monster.getId() == 8150000) {
            if (monster.getId() == 8800002 || monster.getId() == 8830000) {
                killAllMonsters();
            }
            if (monster.getId() == 8830000) {
                broadcastMessage(EffectPackets.ShowEffect("AbyssShadow"));
            }
            if (monster.getId() == 8150000) {
                if (p.getMapId() == 200090010 || p.getMapId() == 200090000) {
                    String x = "每日任务_击杀蝙蝠魔";
                    if (p.getbosslog(x) <= 0) {
                        p.setbosslog(x);
                    }
                    if (getMonsters().isEmpty()) {
                        broadcastMessage(PacketCreator.MonsterBoat(false));
                        broadcastMessage(EffectPackets.MusicChange("Bgm04/UponTheSky"));
                    }
                }
            }
        }

        if (boss(monster.getId())) {
            p.setbosslog("每日任务_boss_" + monster.getId() + "_最后一击");
            dropMessage(5, "" + p.getName() + " 对 " + monster.getName() + " 进行最后一击。");
            setmapbosslog("每日任务_boss_" + monster.getId());
        }
        
        p.杀怪计数();
        monster.dispatchMonsterKilled(true);
        broadcastMessage(MonsterPackets.KillMonster(monster.getObjectId(), animation));
    }

    public boolean boss(int a) {
        switch (a) {
            //巨居蟹
            case 5220001:
            //蜈蚣
            case 5220004:
            //闹钟
            case 8500002:
            //鱼王
            case 8510000:
            case 8520000:
                return true;
        }
        return false;
    }

    public void 怨念丛生(MapleMonster monster, Player p, int a) {
        if (p.军团间隔() > 1000 * 30) {
            if (比例(a, 150)) {
                if (p.getMap().getMonsters().size() < 100) {
                    if (p.switch_skill() == 0) {
                        broadcastMessage(EffectPackets.ShowEffect("fumo/1"));
                    }
                    int 军团数量 = 15;
                    for (int i = 1; i <= 军团数量; i++) {
                        MapleMonster mob = MapleLifeFactory.getMonster(monster.getId());
                        mob.setjt(false);
                        p.getMap().spawnMonsterOnGroundBelow(mob, monster.getPosition());
                    }
                    dropMessage(5, "" + p.getName() + " 击杀了" + monster.getName() + "，它的怨念丛生，出现了军团。");
                }
                p.记录军团间隔();
            }
        }
    }

    public void 怨念爆发(MapleMonster monster, Player p, int a) {
        if (p.军团间隔() > 1000 * 30) {
            if (比例(a, 150)) {
                if (p.getMap().getMonsters().size() < 100) {
                    if (p.switch_skill() == 0) {
                        broadcastMessage(EffectPackets.ShowEffect("fumo/2"));
                    }
                    MapleMonster mob = MapleLifeFactory.getMonster(monster.getId());
                    mob.setHp(mob.getHp() * 20);
                    p.getMap().spawnMonsterOnGroundBelow(mob, monster.getPosition());
                    dropMessage(5, "" + p.getName() + " 击杀了" + monster.getName() + "，它的复仇执念聚集，出现了更加强力的" + monster.getName() + "。");
                }
                p.记录军团间隔();
            }
        }
    }

    public void 轮墓边狱(MapleMonster monster, Player p, int a) {
        Field map = p.getMap();
        List<FieldObject> monsters = map.getMapObjectsInRange(p.getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(FieldObjectType.MONSTER));
        int count = 0;
        for (FieldObject monstermo : monsters) {
            monster = (MapleMonster) monstermo;
            if (!monster.getStats().isFriendly() && !(monster.getId() >= 8810010 && monster.getId() <= 8810018)) {
                map.damageMonster(p, monster, Integer.MAX_VALUE);
                count++;
            }
        }
    }

    public void scheduleWarp(Field toGoto, Field frm, long time) {
        MapTimer tMan = MapTimer.getInstance();
        tMan.schedule(new warpAll(toGoto, frm), time);
    }

    public void closeMapSpawnPoints() {
        for (SpawnPoint spawnPoint : monsterSpawn) {
            spawnPoint.setDenySpawn(true);
        }
    }

    public void killAllMonsters() {
        closeMapSpawnPoints();

        for (FieldObject monstermo : getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(FieldObjectType.MONSTER))) {
            MapleMonster monster = (MapleMonster) monstermo;

            killMonster(monster, null, false, 1);
        }
    }

    public void killMonster(int mobId) {
        Player chr = (Player) getPlayers().get(0);
        List<MapleMonster> mobList = getMonsters();

        for (MapleMonster mob : mobList) {
            if (mob.getId() == mobId) {
                this.killMonster(mob, chr, false);
            }
        }
    }

    public final void destroyReactors(final int first, final int last) {
        List<Reactor> toDestroy = new ArrayList<>();
        List<FieldObject> reactors = getReactors();

        for (FieldObject obj : reactors) {
            Reactor mr = (Reactor) obj;
            if (mr.getId() >= first && mr.getId() <= last) {
                toDestroy.add(mr);
            }
        }

        for (Reactor mr : toDestroy) {
            destroyReactor(mr.getObjectId());
        }
    }

    public void destroyReactor(int oid) {
        final Reactor reactor = getReactorByOid(oid);
        broadcastMessage(PacketCreator.DestroyReactor(reactor));
        reactor.cancelReactorTimeout();
        reactor.setAlive(false);
        removeMapObject(reactor);

        if (reactor.getDelay() > 0) {
            MapTimer.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    respawnReactor(reactor);
                }
            }, reactor.getDelay());
        }
    }

    public void resetReactors() {
        List<Reactor> list = new ArrayList<>();

        this.mapobjectlocks.get(FieldObjectType.REACTOR).readLock().lock();
        try {
            for (FieldObject o : mapObjects.get(FieldObjectType.REACTOR).values()) {
                if (o.getType() == FieldObjectType.REACTOR) {
                    final Reactor r = ((Reactor) o);
                    list.add(r);
                }
            }
        } finally {
            this.mapobjectlocks.get(FieldObjectType.REACTOR).readLock().unlock();
        }

        resetReactors(list);
    }

    public final void resetReactors(List<Reactor> list) {
        for (Reactor r : list) {
            r.resetReactorActions(0);
            broadcastMessage(PacketCreator.TriggerReactor(r, 0));
        }
    }

    public void shuffleReactors() {
        List<Point> points = new ArrayList<>();
        this.mapobjectlocks.get(FieldObjectType.REACTOR).readLock().lock();
        try {
            for (FieldObject o : mapObjects.get(FieldObjectType.REACTOR).values()) {
                if (o.getType() == FieldObjectType.REACTOR) {
                    points.add(((Reactor) o).getPosition());
                }
            }
            Collections.shuffle(points);
            for (FieldObject o : mapObjects.get(FieldObjectType.REACTOR).values()) {
                if (o.getType() == FieldObjectType.REACTOR) {
                    ((Reactor) o).setPosition(points.remove(points.size() - 1));
                }
            }
        } finally {
            this.mapobjectlocks.get(FieldObjectType.REACTOR).readLock().unlock();
        }
    }

    public void updateMonsterController(MapleMonster monster) {
        if (!monster.isAlive()) {
            return;
        }
        if (monster.getController() != null) {
            if (monster.getController().getMap() != this) {
                monster.getController().uncontrolMonster(monster);
            } else {
                return;
            }
        }
        int mincontrolled = -1;
        Player newController = null;

        charactersLock.readLock().lock();
        try {
            final Iterator<Player> ltr = characters.iterator();
            Player chr;
            while (ltr.hasNext()) {
                chr = ltr.next();
                if (!chr.isHidden() && (chr.getControlledMonsters().size() < mincontrolled || mincontrolled == -1)) {
                    mincontrolled = chr.getControlledMonsters().size();
                    newController = chr;
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        if (newController != null) {
            if (monster.isFirstAttack()) {
                newController.controlMonster(monster, true);
                monster.setControllerHasAggro(true);
                monster.setControllerKnowsAboutAggro(true);
            } else {
                newController.controlMonster(monster, false);
            }
        }
    }

    public FieldObject getMapObject(int oid, FieldObjectType type) {
        this.mapobjectlocks.get(type).readLock().lock();
        try {
            return mapObjects.get(type).get(oid);
        } finally {
            this.mapobjectlocks.get(type).readLock().unlock();
        }
    }

    public FieldObject getMapObject(int oid) {
        for (FieldObjectType type : FieldObjectType.values()) {
            this.mapobjectlocks.get(type).readLock().lock();
            try {
                for (FieldObject obj : this.mapObjects.get(type).values()) {
                    if (obj.getObjectId() == oid) {
                        return obj;
                    }
                }
            } finally {
                this.mapobjectlocks.get(type).readLock().unlock();
            }
        }
        return null;
    }

    public Collection<FieldObject> getMapObjects(FieldObjectType type) {
        mapobjectlocks.get(type).readLock().lock();
        try {
            return mapObjects.get(type).values();
        } finally {
            mapobjectlocks.get(type).readLock().unlock();
        }
    }

    public boolean containsNPC(int npcid) {
        this.mapobjectlocks.get(FieldObjectType.NPC).readLock().lock();
        try {
            Iterator<FieldObject> itr = this.mapObjects.get(FieldObjectType.NPC).values().iterator();
            while (itr.hasNext()) {
                FieldObject o = itr.next();
                if (o instanceof MapleNPC) {
                    MapleNPC n = (MapleNPC) o;
                    if (n.getId() == npcid) {
                        return true;
                    }
                }
            }
            return false;
        } finally {
            this.mapobjectlocks.get(FieldObjectType.NPC).readLock().unlock();
        }
    }

    public Portal getRandomSpawnpoint() {
        List<Portal> spawnPoints = new ArrayList<>();
        portals.values().stream().filter((portal) -> (portal.getType() >= 0 && portal.getType() <= 2)).forEach((portal) -> {
            spawnPoints.add(portal);
        });
        Portal portal = spawnPoints.get(new Random().nextInt(spawnPoints.size()));
        return portal != null ? portal : getPortal(0);
    }

    public MapleMonster getMonsterByOid(int oid) {
        FieldObject mmo = getMapObject(oid, FieldObjectType.MONSTER);
        return (mmo != null && mmo.getType() == FieldObjectType.MONSTER) ? (MapleMonster) mmo : null;
    }

    public Reactor getReactorByOid(int oid) {
        FieldObject mmo = getMapObject(oid, FieldObjectType.REACTOR);
        return (mmo != null && mmo.getType() == FieldObjectType.REACTOR) ? (Reactor) mmo : null;
    }

    public MapleNPC getNPCByOid(int oid) {
        FieldObject mmo = getMapObject(oid, FieldObjectType.NPC);
        return (mmo != null && mmo.getType() == FieldObjectType.NPC) ? (MapleNPC) mmo : null;
    }

    public Reactor getReactorByName(String name) {
        this.mapobjectlocks.get(FieldObjectType.REACTOR).readLock().lock();
        try {
            Iterator<FieldObject> itr = this.mapObjects.get(FieldObjectType.REACTOR).values().iterator();
            while (itr.hasNext()) {
                FieldObject o = itr.next();
                if (o instanceof Reactor) {
                    Reactor r = (Reactor) o;
                    if (r.getName().equals(name)) {
                        return r;
                    }
                }
            }
            return null;
        } finally {
            this.mapobjectlocks.get(FieldObjectType.REACTOR).readLock().unlock();
        }
    }

    public Reactor getReactorById(int Id) {
        this.mapobjectlocks.get(FieldObjectType.REACTOR).readLock().lock();
        try {
            Iterator<FieldObject> itr = this.mapObjects.get(FieldObjectType.REACTOR).values().iterator();
            while (itr.hasNext()) {
                FieldObject o = itr.next();
                if (o instanceof Reactor) {
                    Reactor r = (Reactor) o;
                    if (r.getId() == Id) {
                        return r;
                    }
                }
            }
            return null;
        } finally {
            this.mapobjectlocks.get(FieldObjectType.REACTOR).readLock().unlock();
        }
    }

    public MapleMonster getMonsterById(int id) {
        this.mapobjectlocks.get(FieldObjectType.MONSTER).readLock().lock();
        try {
            Iterator<FieldObject> itr = this.mapObjects.get(FieldObjectType.MONSTER).values().iterator();
            while (itr.hasNext()) {
                FieldObject o = itr.next();
                if (o instanceof MapleMonster) {
                    MapleMonster m = (MapleMonster) o;
                    if (m.getId() == id) {
                        return m;
                    }
                }
            }
            return null;
        } finally {
            this.mapobjectlocks.get(FieldObjectType.MONSTER).readLock().unlock();
        }
    }

    public void spawnFakeMonsterOnGroundBelow(MapleMonster mob, Point pos) {
        Point spos = getGroundBelow(pos);
        mob.setPosition(spos);
        spawnFakeMonster(mob);
    }

    public void spawnRevives(final MapleMonster monster) {
        monster.setMap(this);

        spawnAndAddRangedMapObject(monster, (Client c) -> {
            c.announce(MonsterPackets.SpawnMonster(monster, false));
        });
        updateMonsterController(monster);
        spawnedMonstersOnMap.incrementAndGet();

        final SelfDestruction selfDestruction = monster.getStats().selfDestruction();
        if (monster.getStats().getRemoveAfter() > 0 || selfDestruction != null && selfDestruction.getHp() < 0) {
            if (selfDestruction == null) {
                MapTimer.getInstance().schedule(() -> {
                    killMonster(monster, null, false);
                }, monster.getStats().getRemoveAfter() * 1000);
            } else {
                MapTimer.getInstance().schedule(() -> {
                    killMonster(monster, null, false, selfDestruction.getAction());
                }, selfDestruction.removeAfter() * 1000);
            }
        }
    }

    public void resetRiceCakes() {
        this.riceCakes = 0;
    }

    public void addBunnyHit() {
        this.bunnyDamage++;
        if (bunnyDamage > 5) {
            broadcastMessage(PacketCreator.ServerNotice(6, "The Moon Bunny is feeling sick. Please protect it so it can make delicious rice cakes."));
            bunnyDamage = 0;
        }
    }

    private void monsterItemDrop(final MapleMonster m, long delay) {
        final ScheduledFuture<?> monsterItemDrop = MapTimer.getInstance().register(() -> {

            List<FieldObject> chrList = Field.this.getPlayers();

            if (m.isAlive() && !Field.this.getAllPlayer().isEmpty()) {
                Player p = (Player) chrList.get(0);

                switch (m.getId()) {
                    case 9300061:
                        Field.this.riceCakes++;
                        Field.this.broadcastMessage(PacketCreator.ServerNotice(6, "The Moon Bunny made rice cake number " + (Field.this.riceCakes) + "."));
                        break;
                }

                dropFromMonster(p, m);
            }
        }, delay, delay);
        if (!m.isAlive()) {
            monsterItemDrop.cancel(true);
        }
    }

    public void spawnMonster(final MapleMonster monster) {
        spawnMonster(monster, 1, false);
    }

    public void spawnMonster(final MapleMonster monster, int difficulty, boolean isPq) {
        monster.changeDifficulty(difficulty, isPq);

        monster.setMap(this);

        if (getEventInstance() != null) {
            getEventInstance().registerMonster(monster);
        }

        spawnAndAddRangedMapObject(monster, (Client c) -> {
            c.write(MonsterPackets.SpawnMonster(monster, true));
        });
        updateMonsterController(monster);

        if (monster.getDropPeriodTime() > 0) {
            switch (monster.getId()) {
                case 9300061: // Moon Bunny (HPQ)
                    monsterItemDrop(monster, monster.getDropPeriodTime() / 3);
                    break;
                case 9300102: //Watchhog
                    monsterItemDrop(monster, monster.getDropPeriodTime());
                    break;
                default:
                    FileLogger.print("spawnMonster_getDropPeriodTime", "UNCODED TIMED MOB DETECTED: " + monster.getId());
                    System.out.println("UNCODED TIMED MOB DETECTED: " + monster.getId());
                    break;
            }
        }

        spawnedMonstersOnMap.incrementAndGet();

        final SelfDestruction selfDestruction = monster.getStats().selfDestruction();
        if (monster.getStats().getRemoveAfter() > 0 || selfDestruction != null && selfDestruction.getHp() < 0) {
            if (selfDestruction == null) {
                MapTimer.getInstance().schedule(() -> {
                    killMonster(monster, null, false);
                }, monster.getStats().getRemoveAfter() * 1000);
            } else {
                MapTimer.getInstance().schedule(() -> {
                    killMonster(monster, null, false, selfDestruction.getAction());
                }, selfDestruction.removeAfter() * 1000);
            }
        }
    }

    public int spawnMonsterWithCoords(MapleMonster mob, int x, int y) {
        Point spos = new Point(x, y - 1);
        spos = calcPointBelow(spos);
        spos.y -= 1;
        mob.setPosition(spos);
        spawnMonster(mob);
        return mob.getObjectId();
    }

    public void spawnMonsterWithEffect(final MapleMonster monster, final int effect, Point pos) {
        monster.setMap(this);
        Point spos = new Point(pos.x, pos.y - 1);
        spos = calcPointBelow(spos);
        if (spos == null) {
            return;
        }

        spos.y--;
        monster.setPosition(spos);
        monster.disableDrops();
        spawnAndAddRangedMapObject(monster, (Client c) -> {
            c.write(MonsterPackets.SpawnMonster(monster, true, effect));
        });
        updateMonsterController(monster);
        spawnedMonstersOnMap.incrementAndGet();
    }

    public boolean hasTimer() {
        return timer;
    }

    public void setTimer(boolean timer) {
        this.timer = timer;
    }

    public void spawnFakeMonster(final MapleMonster monster) {
        monster.setMap(this);
        monster.setFake(true);
        spawnAndAddRangedMapObject(monster, (Client c) -> {
            c.write(MonsterPackets.SpawnFakeMonster(monster, 0));
        });
        spawnedMonstersOnMap.incrementAndGet();
    }

    public void makeMonsterReal(final MapleMonster monster) {
        monster.setFake(false);
        broadcastMessage(MonsterPackets.MakeMonsterReal(monster));
        updateMonsterController(monster);
    }

    public void spawnReactor(final Reactor reactor) {
        reactor.setMap(this);
        spawnAndAddRangedMapObject(reactor, (Client c) -> {
            c.announce(reactor.makeSpawnData());
        });
    }

    private void respawnReactor(final Reactor reactor) {
        reactor.resetReactorActions(0);
        reactor.setAlive(true);
        spawnReactor(reactor);
    }

    public void spawnDoor(final FieldDoorObject door) {
        spawnAndAddRangedMapObject(door, (Client c) -> {
            if (door.getFrom().getId() == c.getPlayer().getMapId()) {
                if (c.getPlayer().getParty() != null && (door.getOwnerId() == c.getPlayer().getId() || c.getPlayer().getParty().getMemberById(door.getOwnerId()) != null)) {
                    c.announce(PartyPackets.PartyPortal(door.getLinkedPortalId(), door.getFrom().getId(), door.getTo().getId(), door.toPosition()));
                }
                c.announce(PacketCreator.SpawnPortal(door.getFrom().getId(), door.getTo().getId(), door.toPosition()));
                /*if (!door.inTown()) {
                    c.announce(PacketCreator.SpawnDoor(door.getOwnerId(), door.getPosition(), false));
                }*/
                c.announce(PacketCreator.SpawnDoor(door.getOwnerId(), door.getPosition(), false));
            }
            c.announce(PacketCreator.EnableActions());
        });

        if (!door.inTown()) {
            setLastDoorOwner(door.getOwnerId());
        }
    }

    public boolean canDeployDoor(Point pos) {
        Point toStep = calcPointBelow(pos);
        return toStep != null && toStep.distance(pos) <= 42;
    }

    /**
     * Fetches angle relative between spawn and door points where 3 O'Clock is 0
     * and 12 O'Clock is 270 degrees
     *
     * @param spawnPoint
     * @param doorPoint
     * @return angle in degress from 0-360.
     */
    private static double getAngle(Point doorPoint, Point spawnPoint) {
        double dx = doorPoint.getX() - spawnPoint.getX();
        double dy = -(doorPoint.getY() - spawnPoint.getY());
        double inRads = Math.atan2(dy, dx);
        if (inRads < 0) {
            inRads = Math.abs(inRads);
        } else {
            inRads = 2 * Math.PI - inRads;
        }
        return Math.toDegrees(inRads);
    }

    public Pair<String, Integer> getDoorPositionStatus(Point pos) {
        Portal portal = findClosestPlayerSpawnpoint(pos);

        double angle = getAngle(portal.getPosition(), pos);
        double distn = pos.distanceSq(portal.getPosition());

        if (distn <= 777777.7) {
            return null;
        }
        distn = Math.sqrt(distn);
        return new Pair(getRoundedCoordinate(angle), (int) distn);
    }

    /**
     * Converts angle in degrees to rounded cardinal coordinate.
     *
     * @param angle
     * @return correspondent coordinate.
     */
    public static String getRoundedCoordinate(double angle) {
        String directions[] = {"E", "SE", "S", "SW", "W", "NW", "N", "NE", "E"};
        return directions[(int) Math.round((((double) angle % 360) / 45))];
    }

    public void spawnSummon(final MapleSummon summon) {
        if (summon != null) {
            summon.updateMap(this);
            spawnAndAddRangedMapObject(summon, (Client c) -> {
                c.write(PacketCreator.SpawnSpecialFieldObject(summon, false));
            });
        }
    }

    public void spawnMist(final MapleMist mist, final int duration, boolean poison, boolean fake) {
        addMapObject(mist);
        broadcastMessage(fake ? mist.makeFakeSpawnData(30) : mist.makeSpawnData());
        MapTimer tMan = MapTimer.getInstance();
        final ScheduledFuture<?> poisonSchedule;
        if (poison) {
            Runnable poisonTask = () -> {
                List<FieldObject> affectedMonsters = getMapObjectsInBox(mist.getBox(), Collections.singletonList(FieldObjectType.MONSTER));
                affectedMonsters.stream().filter((mo) -> (mist.makeChanceResult())).forEach((mo) -> {
                    MonsterStatusEffect poisonEffect = new MonsterStatusEffect(Collections.singletonMap(MonsterStatus.POISON, 1), mist.getSourceSkill(), false);
                    ((MapleMonster) mo).applyStatus(mist.getOwner(), poisonEffect, true, duration);
                });
            };
            poisonSchedule = tMan.register(poisonTask, 2000, 2500);
        } else {
            poisonSchedule = null;
        }
        tMan.schedule(() -> {
            removeMapObject(mist);
            if (poisonSchedule != null) {
                poisonSchedule.cancel(false);
            }
            broadcastMessage(mist.makeDestroyData());
        }, duration);
    }

    public void spawnLove(final MapleLove love) {
        addMapObject(love);
        this.broadcastMessage(PacketCreator.spawnLove(love));
        MapTimer tMan = MapTimer.getInstance();
        tMan.schedule(() -> {
            removeMapObject(love);
            broadcastMessage(PacketCreator.removeLove(love, (byte) 1));
        }, 1800000);
    }

    public void spawnMist(final MapleMist mist, final int duration, boolean poison, boolean fake, boolean recovery) {
        addMapObject(mist);
        broadcastMessage(fake ? mist.makeFakeSpawnData(30) : mist.makeSpawnData());
        MapTimer tMan = MapTimer.getInstance();
        final ScheduledFuture<?> poisonSchedule;
        if (poison) {
            Runnable poisonTask = () -> {
                List<FieldObject> affectedMonsters = getMapObjectsInBox(mist.getBox(), Collections.singletonList(FieldObjectType.MONSTER));
                affectedMonsters.stream().filter((mo) -> (mist.makeChanceResult())).forEach((mo) -> {
                    MonsterStatusEffect poisonEffect = new MonsterStatusEffect(Collections.singletonMap(MonsterStatus.POISON, 1), mist.getSourceSkill(), null, false);
                    ((MapleMonster) mo).applyStatus(mist.getOwner(), poisonEffect, true, duration);
                });
            };
            poisonSchedule = tMan.register(poisonTask, 2000, 2500);
        } else if (recovery) {
            Runnable poisonTask = () -> {
                List<FieldObject> affectedMonsters = getMapObjectsInBox(mist.getBox(), Collections.singletonList(FieldObjectType.MONSTER));
                affectedMonsters.stream().filter((mo) -> (mist.makeChanceResult())).map((mo) -> (Player) mo).filter((chr) -> (mist.getOwner().getId() == chr.getId() || mist.getOwner().getParty() != null && mist.getOwner().getParty().containsMembers(chr.getMPC()))).forEach((chr) -> {
                    chr.getStat().addMP((int) mist.getSourceSkill().getEffect(chr.getSkillLevel(mist.getSourceSkill().getId())).getX() * chr.getStat().getMp() / 100);
                });
            };
            poisonSchedule = tMan.register(poisonTask, 2000, 2500);
        } else {
            poisonSchedule = null;
        }
        tMan.schedule(() -> {
            removeMapObject(mist);
            if (poisonSchedule != null) {
                poisonSchedule.cancel(false);
            }
            broadcastMessage(mist.makeDestroyData());
        }, duration);
    }

    public void timeMob(int id, String msg) {
        timeMob = new Pair<>(id, msg);
    }

    public Pair<Integer, String> getTimeMob() {
        return timeMob;
    }

    public void toggleHiddenNPC(int id) {
        this.mapobjectlocks.get(FieldObjectType.NPC).readLock().lock();
        try {
            mapObjects.get(FieldObjectType.NPC).values().stream().filter((obj) -> (obj.getType() == FieldObjectType.NPC)).map((obj) -> (MapleNPC) obj).filter((npc) -> (npc.getId() == id)).map((npc) -> {
                npc.setHide(!npc.isHidden());
                return npc;
            }).filter((npc) -> (!npc.isHidden())).forEach((npc) -> {
                broadcastMessage(PacketCreator.SpawnNPC(npc));
            });
        } finally {
            this.mapobjectlocks.get(FieldObjectType.NPC).readLock().unlock();
        }
    }

    public final List<FieldObject> getReactors() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(FieldObjectType.REACTOR));
    }

    public List<FieldObject> getPlayers() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(FieldObjectType.PLAYER));
    }

    private void activateItemReactors(final FieldItem drop, final Client c) {
        final Item item = drop.getItem();
        this.mapobjectlocks.get(FieldObjectType.REACTOR).readLock().lock();
        try {
            for (final FieldObject o : mapObjects.get(FieldObjectType.REACTOR).values()) {
                final Reactor react = (Reactor) o;

                if (react.getReactorType() == 100) {
                    if (react.getReactItem(react.getEventState()).getLeft() == item.getItemId() && react.getReactItem(react.getEventState()).getRight() == item.getQuantity()) {

                        if (react.getArea().contains(drop.getPosition())) {
                            MapTimer.getInstance().schedule(new ActivateItemReactor(drop, react, c), 5000);
                            break;
                        }
                    }
                }
            }
        } finally {
            this.mapobjectlocks.get(FieldObjectType.REACTOR).readLock().unlock();
        }
    }

    public List<Reactor> getAllReactor() {
        return getAllReactorsThreadsafe();
    }

    public final List<FieldObject> getAllMonsters() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(FieldObjectType.MONSTER));
    }

    public List<FieldObject> getAllPlayer() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(FieldObjectType.PLAYER));
    }

    public int playerCount() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(FieldObjectType.PLAYER)).size();
    }

    public int monsterCount() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(FieldObjectType.MONSTER)).size();
    }

    public int itemCount() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(FieldObjectType.ITEM)).size();
    }

    /** <指定怪物数量>
     */
    public int monsterCountById(int id) {
        int mobQuantity = 0;
        mobQuantity = getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(FieldObjectType.MONSTER)).stream().map((m) -> (MapleMonster) m).filter((monster) -> (monster.getId() == id)).map((_item) -> 1).reduce(mobQuantity, Integer::sum);
        return mobQuantity;
    }

    public int countMonster(int id) {
        return countMonster(id, id);
    }

    public int countMonster(int minid, int maxid) {
        int count = 0;
        count = getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(FieldObjectType.MONSTER)).stream().map((m) -> (MapleMonster) m).filter((mob) -> (mob.getId() >= minid && mob.getId() <= maxid)).map((_item) -> 1).reduce(count, Integer::sum);
        return count;
    }

    public int countMonsters() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(FieldObjectType.MONSTER)).size();
    }

    public List<MapleMonster> getMonsters2() {
        List<MapleMonster> mobs = new ArrayList<>();
        this.mapobjectlocks.get(FieldObjectType.MONSTER).readLock().lock();
        try {
            for (FieldObject o : this.mapObjects.get(FieldObjectType.MONSTER).values()) {
                if (o instanceof MapleMonster) {

                    mobs.add((MapleMonster) o);
                }
            }
        } finally {
            this.mapobjectlocks.get(FieldObjectType.MONSTER).readLock().unlock();
        }
        return mobs;
    }

    public List<MapleMonster> getMonsters() {
        List<MapleMonster> mobs = new ArrayList<>();
        this.mapobjectlocks.get(FieldObjectType.MONSTER).readLock().lock();
        try {
            for (FieldObject o : this.mapObjects.get(FieldObjectType.MONSTER).values()) {
                if (o instanceof MapleMonster) {
                    mobs.add((MapleMonster) o);
                }
            }
        } finally {
            this.mapobjectlocks.get(FieldObjectType.MONSTER).readLock().unlock();
        }
        return mobs;
    }

    public boolean withinObjectRange(Player who, int objectid) {
        List<FieldObject> npc = getMapObjectsInRange(who.getPosition(), 850 * 850, Arrays.asList(FieldObjectType.NPC));
        return npc.stream().map((pen) -> (MapleNPC) pen).anyMatch((npcla) -> (npcla.getObjectId() == objectid));
    }

    /** <地图玩家>
     */
    public final List<Player> getCharactersThreadsafe() {
        return getCharactersThreadsafe(new ArrayList<>());
    }

    public final ArrayList<Player> getCharactersThreadsafe(ArrayList<Player> chars) {
        chars.clear();
        charactersLock.readLock().lock();
        try {
            characters.stream().forEach((mc) -> {
                chars.add(mc);
            });
        } finally {
            charactersLock.readLock().unlock();
        }
        return chars;
    }

    public List<FieldObject> getAllDoorsThreadsafe() {
        ArrayList<FieldObject> ret = new ArrayList<>();
        this.mapobjectlocks.get(FieldObjectType.DOOR).readLock().lock();
        try {
            for (FieldObject o : this.mapObjects.get(FieldObjectType.DOOR).values()) {
                if (o instanceof FieldDoorObject) {
                    ret.add(o);
                }
            }
        } finally {
            this.mapobjectlocks.get(FieldObjectType.DOOR).readLock().unlock();
        }
        return ret;
    }

    public List<MapleNPC> getAllNpcThreadsafe() {
        ArrayList<MapleNPC> ret = new ArrayList<>();
        mapobjectlocks.get(FieldObjectType.NPC).readLock().lock();
        try {
            for (FieldObject o : mapObjects.get(FieldObjectType.NPC).values()) {
                if (o instanceof MapleNPC) {
                    ret.add((MapleNPC) o);
                }
            }
        } finally {
            mapobjectlocks.get(FieldObjectType.NPC).readLock().unlock();
        }
        return ret;
    }

    public void spawnAllMonsterIdFromMapSpawnList(int id) {
        spawnAllMonsterIdFromMapSpawnList(id, 1, false);
    }

    public void spawnAllMonsterIdFromMapSpawnList(int id, int difficulty, boolean isPq) {
        allMonsterSpawn.stream().filter((sp) -> (sp.getMonsterId() == id)).forEach((sp) -> {
            spawnMonster(sp.getMonster(), difficulty, isPq);
        });
    }

    public void spawnAllMonstersFromMapSpawnList() {
        spawnAllMonstersFromMapSpawnList(1, false);
    }

    public void spawnAllMonstersFromMapSpawnList(int difficulty, boolean isPq) {
        for (SpawnPoint sp : allMonsterSpawn) {
            spawnMonster(sp.getMonster(), difficulty, isPq);
        }
    }

    public final void addAreaMonsterSpawn(final MapleMonster monster, Point pos1, Point pos2, Point pos3, final int mobTime, final String msg) {
//        pos1 = calcPointBelow(pos1);
//        pos2 = calcPointBelow(pos2);
//        pos3 = calcPointBelow(pos3);
//        if (pos1 != null) {
//            pos1.y -= 1;
//        }
//        if (pos2 != null) {
//            pos2.y -= 1;
//        }
//        if (pos3 != null) {
//            pos3.y -= 1;
//        }
//        if (pos1 == null && pos2 == null && pos3 == null) {
//            System.out.println("WARNING: mapid " + mapId + ", monster " + monster.getId() + " could not be spawned.");
//            return;
//        } else if (pos1 != null) {
//            if (pos2 == null) {
//                pos2 = new Point(pos1);
//            }
//            if (pos3 == null) {
//                pos3 = new Point(pos1);
//            }
//        } else if (pos2 != null) {
//            if (pos1 == null) {
//                pos1 = new Point(pos2);
//            }
//            if (pos3 == null) {
//                pos3 = new Point(pos2);
//            }
//        } else if (pos3 != null) {
//            if (pos1 == null) {
//                pos1 = new Point(pos3);
//            }
//            if (pos2 == null) {
//                pos2 = new Point(pos3);
//            }
//        }
//        if (monster != null) {
//           monsterSpawn.add(new SpawnPointAreaBoss(monster, pos1, pos2, pos3, mobTime, msg, true));
//        }
    }

    public MapleNPC getNPCById(int id) {
        this.mapobjectlocks.get(FieldObjectType.NPC).readLock().lock();
        try {
            for (FieldObject obj : mapObjects.get(FieldObjectType.NPC).values()) {
                if (obj.getType() == FieldObjectType.NPC) {
                    if (((MapleNPC) obj).getId() == id) {
                        return (MapleNPC) obj;
                    }
                }
            }
            return null;
        } finally {
            this.mapobjectlocks.get(FieldObjectType.NPC).readLock().unlock();
        }
    }

    public final void disappearingItemDrop(final FieldObject dropper, final Player owner, final Item item, final Point pos) {
        final Point droppos = calcDropPos(pos, pos);
        final FieldItem drop = new FieldItem(item, droppos, dropper, owner, (byte) 1, false);
        broadcastMessage(PacketCreator.DropItemFromMapObject(drop, dropper.getPosition(), droppos, (byte) 3));
    }

    public final void spawnItemDrop(final FieldObject dropper, final Player owner, final Item item, Point pos, final boolean ffaDrop, final boolean playerDrop) {
        final Point dropPos = calcDropPos(pos, pos);
        final FieldItem drop = new FieldItem(item, dropPos, dropper, owner, (byte) 2, playerDrop);

        spawnAndAddRangedMapObject(drop, (Client c) -> {
            c.write(PacketCreator.DropItemFromMapObject(drop, dropper.getPosition(), dropPos, (byte) 1));
        });

        broadcastMessage(PacketCreator.DropItemFromMapObject(drop, dropper.getPosition(), dropPos, (byte) 0));

        if (!everlast) {
            drop.registerExpire(60 * 1000);
            activateItemReactors(drop, owner.getClient());
        }
    }

    public final void spawnMesoDrop(final int meso, final Point position, final FieldObject dropper, final Player owner, final boolean playerDrop, final byte droptype) {
        final Point droppos = calcDropPos(position, position);
        final FieldItem mdrop = new FieldItem(meso, droppos, dropper, owner, droptype, playerDrop);

        spawnAndAddRangedMapObject(mdrop, (Client c) -> {
            c.write(PacketCreator.DropItemFromMapObject(mdrop, dropper.getPosition(), droppos, (byte) 1));
        });

        if (!everlast) {
            mdrop.registerExpire(60 * 1000);
            if (droptype == 0 || droptype == 1) {
                mdrop.registerFFA(30000);
            }
        }
    }

    public void dropFromReactor(final Player p, final Reactor reactor, Item drop, Point dropPos, short questid) {
        spawnDrop(drop, this.calcDropPos(dropPos, reactor.getPosition()), reactor, p, (byte) (p.getParty() != null ? 1 : 0), questid);
    }

    public void displayClock(final Player p, int time) {
        broadcastMessage(PacketCreator.GetClockTimer(time));
        MapTimer.getInstance().schedule(() -> {
            broadcastMessage(PacketCreator.DestroyClock());
        }, time * 1000);
    }

    public void setEventInstance(EventInstanceManager eim) {
        event = eim;
    }

    public EventInstanceManager getEventInstance() {
        return event;
    }

    public Map<Integer, Player> getMapPlayers() {
        charactersLock.readLock().lock();
        try {
            Map<Integer, Player> mapChars = new HashMap<>(characters.size());

            characters.stream().forEach((chr) -> {
                mapChars.put(chr.getId(), chr);
            });

            return mapChars;
        } finally {
            charactersLock.readLock().unlock();
        }
    }

    public List<Reactor> getReactorsByIdRange(final int first, final int last) {
        List<Reactor> list = new LinkedList<>();

        this.mapobjectlocks.get(FieldObjectType.REACTOR).readLock().lock();
        try {
            Iterator<FieldObject> obj = mapObjects.get(FieldObjectType.REACTOR).values().iterator();
            while (obj.hasNext()) {
                FieldObject o = obj.next();
                if (o instanceof Reactor) {
                    Reactor r = (Reactor) o;
                    if (r.getId() >= first && r.getId() <= last) {
                        list.add(r);
                    }
                }
            }
            return list;
        } finally {
            this.mapobjectlocks.get(FieldObjectType.REACTOR).readLock().unlock();
        }
    }

    public Portal getRandomPlayerSpawnpoint() {
        List<Portal> spawnPoints = new ArrayList<>();
        portals.values().stream().
                filter((portal) -> (portal.getType() >= 0
                && portal.getType() <= 1
                && portal.getTargetMapId() == MapConstants.NULL_MAP)).
                forEach((portal) -> {
                    spawnPoints.add(portal);
                });
        Portal portal = spawnPoints.get(new Random().nextInt(spawnPoints.size()));
        return portal != null ? portal : getPortal(0);
    }

    public void searchItemReactors(final Reactor react) {
        if (react.getReactorType() == 100) {
            Pair<Integer, Integer> reactProp = react.getReactItem(react.getEventState());
            int reactItem = reactProp.getLeft(), reactQty = reactProp.getRight();
            Rectangle reactArea = react.getArea();

            List<FieldItem> list = new ArrayList<>();
            this.mapobjectlocks.get(FieldObjectType.ITEM).readLock().lock();
            try {
                droppedItems.keySet().stream().
                        filter((mmi) -> (!mmi.isPickedUp())).
                        forEach((mmi) -> {
                            list.add(mmi);
                        });
            } finally {
                this.mapobjectlocks.get(FieldObjectType.ITEM).readLock().unlock();
            }

            list.stream().forEach((drop) -> {
                final Item item = drop.getItem();
                if (item != null && reactItem == item.getItemId() && reactQty == item.getQuantity()) {
                    if (reactArea.contains(drop.getPosition())) {
                        Client owner = drop.getOwnerClient();
                        if (owner != null) {
                            MapTimer.getInstance().schedule(new ActivateItemReactor(drop, react, owner), 5000);
                        }
                    }
                }
            });
        }
    }

    public void destroyNPC(int npcid) {
        List<FieldObject> npcs = getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(FieldObjectType.NPC));

        this.mapobjectlocks.get(FieldObjectType.NPC).writeLock().lock();
        try {
            npcs.stream().filter((obj) -> (((MapleNPC) obj).getId() == npcid)).map((obj) -> {
                broadcastMessage(PacketCreator.RemoveNPCController(obj.getObjectId()));
                return obj;
            }).map((obj) -> {
                broadcastMessage(PacketCreator.RemoveNPC(obj.getObjectId()));
                return obj;
            }).forEach((obj) -> {
                this.mapObjects.get(FieldObjectType.NPC).remove(obj.getObjectId());
            });
        } finally {
            this.mapobjectlocks.get(FieldObjectType.NPC).writeLock().unlock();
        }
    }

    public void broadcastBossHpMessage(MapleMonster mm, int bossHash, OutPacket packet) {
        broadcastBossHpMessage(mm, bossHash, null, packet, Double.POSITIVE_INFINITY, null);
    }

    public void broadcastBossHpMessage(MapleMonster mm, int bossHash, final OutPacket packet, Point rangedFrom) {
        broadcastBossHpMessage(mm, bossHash, null, packet, getRangedDistance(), rangedFrom);
    }

    private void broadcastBossHpMessage(MapleMonster mm, int bossHash, Player source, final OutPacket packet, double rangeSq, Point rangedFrom) {
        charactersLock.readLock().lock();
        try {
            characters.stream().filter((p) -> (p != source)).forEach((p) -> {
                if (rangeSq < Double.POSITIVE_INFINITY) {
                    if (rangedFrom.distanceSq(p.getPosition()) <= rangeSq) {
                        p.getClient().announceBossHpBar(mm, bossHash, packet);
                    }
                } else {
                    p.getClient().announceBossHpBar(mm, bossHash, packet);
                }
            });
        } finally {
            charactersLock.readLock().unlock();
        }
    }

    private class TimerDestroyWorker implements Runnable {

        @Override
        public void run() {
            if (mapTimer != null) {
                int warpMap = mapTimer.warpToField();
                int minWarp = mapTimer.minLevelToWarp();
                int maxWarp = mapTimer.maxLevelToWarp();
                mapTimer = null;
                if (warpMap != -1) {
                    Field map2wa2 = Start.getInstance().getWorldById(world).getChannelById(channel).getMapFactory().getMap(warpMap);
                    String warpmsg = "You will now be warped to " + map2wa2.getStreetName() + " : " + map2wa2.getMapName();
                    broadcastMessage(PacketCreator.ServerNotice(6, warpmsg));
                    getCharactersThreadsafe().forEach((chr) -> {
                        try {
                            if (chr.getLevel() >= minWarp && chr.getLevel() <= maxWarp) {
                                chr.changeMap(map2wa2, map2wa2.getPortal(0));
                            } else {
                                chr.getClient().write(PacketCreator.ServerNotice(5, "You are not at least level " + minWarp + " or you are higher than level " + maxWarp + "."));
                            }
                        } catch (Exception ex) {
                            chr.getClient().write(PacketCreator.ServerNotice(5, "There was a problem warping you. Please contact a GM"));
                        }
                    });
                }
            }
        }
    }

    public void addFieldTimer(int duration) {
        ScheduledFuture<?> sheduled = MapTimer.getInstance().schedule(new TimerDestroyWorker(), duration * 1000);
        mapTimer = new FieldTimer(sheduled, duration, -1, -1, -1);
        broadcastMessage(mapTimer.makeSpawnData());
    }

    public void addFieldTimer(int duration, int fieldToWarpTo) {
        ScheduledFuture<?> sheduled = MapTimer.getInstance().schedule(new TimerDestroyWorker(), duration * 1000);
        mapTimer = new FieldTimer(sheduled, duration, fieldToWarpTo, 0, 256);
        broadcastMessage(mapTimer.makeSpawnData());
    }

    public void addFieldTimer(int duration, int fieldToWarpTo, int minLevelToWarp) {
        ScheduledFuture<?> sheduled = MapTimer.getInstance().schedule(new TimerDestroyWorker(), duration * 1000);
        mapTimer = new FieldTimer(sheduled, duration, fieldToWarpTo, minLevelToWarp, 256);
        broadcastMessage(mapTimer.makeSpawnData());
    }

    public void addFieldTimer(int duration, int fieldToWarpTo, int minLevelToWarp, int maxLevelToWarp) {
        ScheduledFuture<?> sheduled = MapTimer.getInstance().schedule(new TimerDestroyWorker(), duration * 1000);
        mapTimer = new FieldTimer(sheduled, duration, fieldToWarpTo, minLevelToWarp, maxLevelToWarp);
        broadcastMessage(mapTimer.makeSpawnData());
    }

    public void clearFieldTimer() {
        if (mapTimer != null) {
            mapTimer.getSchedule().cancel(true);
        }
        mapTimer = null;
    }

    public boolean isLastDoorOwner(int cid) {
        return lastDoorOwner == cid;
    }

    public void setLastDoorOwner(int cid) {
        lastDoorOwner = cid;
    }

    public void dropMessage(int type, String message) {
        broadcastStringMessage(type, message);
    }

    public void broadcastStringMessage(int type, String message) {
        broadcastMessage(PacketCreator.ServerNotice(type, message));
    }

    public final void spawnNpc(final int id, final Point pos) {
        final MapleNPC npc = MapleLifeFactory.getNPC(id);
        npc.setPosition(pos);
        npc.setCy(pos.y);
        npc.setRx0(pos.x + 50);
        npc.setRx1(pos.x - 50);
        npc.setFh(getFootholds().findBelow(pos).getId());
        npc.getStats().setCustom(true);
        addMapObject(npc);
        broadcastMessage(PacketCreator.SpawnNPC(npc, true));
    }

    /**
     * <怪物金币存在时间>*
     */
    public final void spawnMobMesoDrop(final int meso, final Point position, final FieldObject dropper, final Player owner, final boolean playerDrop, final byte droptype) {
        final FieldItem mdrop = new FieldItem(meso, position, dropper, owner, droptype, playerDrop);

        spawnAndAddRangedMapObject(mdrop, (Client c) -> {
            c.write(PacketCreator.DropItemFromMapObject(mdrop, dropper.getPosition(), position, (byte) 1));
        });

        mdrop.registerExpire(60 * 1000);
        if (droptype == 0 || droptype == 1) {
            mdrop.registerFFA(30000);
        }
    }

    private static boolean shouldShowQuestItem(Player p, int questid, int itemid) {
        return questid <= 0 || (p.getQuestStatus(questid) == 1 && p.needQuestItem(questid, itemid));
    }

    /**
     * <怪物物品存在时间>*
     */
    public final void spawnDrop(final Item idrop, final Point dropPos, final FieldObject dropper, final Player p, final byte droptype, final short questid) {
        final FieldItem mdrop = new FieldItem(idrop, dropPos, dropper, p, droptype, false, questid);

        spawnAndAddRangedMapObject(mdrop, (Client c) -> {
            if (shouldShowQuestItem(p, questid, idrop.getItemId())) {
                c.announce(PacketCreator.DropItemFromMapObject(mdrop, dropper.getPosition(), dropPos, (byte) 1));
            }
        });

        mdrop.registerExpire(60 * 1000);
        if (droptype == 0 || droptype == 1) {
            mdrop.registerFFA(30000);
        }
        activateItemReactors(mdrop, p.getClient());
    }

    public void startMapEffect(String msg, int itemId) {
        if (mapEffect != null) {
            return;
        }
        mapEffect = new FieldEffect(msg, itemId);
        broadcastMessage(mapEffect.makeStartData());
        MapTimer tMan = MapTimer.getInstance();
        sfme = tMan.schedule(() -> {
            broadcastMessage(mapEffect.makeDestroyData());
            mapEffect = null;
        }, 30000);
    }

    public void stopMapEffect() {
        if (sfme != null) {
            sfme.cancel(false);
        }
        if (mapEffect != null) {
            broadcastMessage(mapEffect.makeDestroyData());
            mapEffect = null;
        }
    }

    public Player getAnyCharacterFromParty(int partyid) {
        for (Player p : getCharactersThreadsafe()) {
            if (p.getPartyId() == partyid) {
                return p;
            }
        }

        return null;
    }

    private void addPartyMemberInternal(Player p) {
        int partyid = p.getPartyId();
        if (partyid == -1) {
            return;
        }

        Set<Integer> partyEntry = mapParty.get(partyid);
        if (partyEntry == null) {
            partyEntry = new LinkedHashSet<>();
            partyEntry.add(p.getId());

            mapParty.put(partyid, partyEntry);
        } else {
            partyEntry.add(p.getId());
        }
    }

    private void removePartyMemberInternal(Player p) {
        int partyid = p.getPartyId();
        if (partyid == -1) {
            return;
        }

        Set<Integer> partyEntry = mapParty.get(partyid);
        if (partyEntry != null) {
            if (partyEntry.size() > 1) {
                partyEntry.remove(p.getId());
            } else {
                mapParty.remove(partyid);
            }
        }
    }

    public void addPartyMember(Player p) {
        addPartyMemberInternal(p);
    }

    public void removePartyMember(Player p) {
        removePartyMemberInternal(p);
    }

    public void removeParty(int partyid) {
        mapParty.remove(partyid);
    }

    /**
     * Adds a player to this map and sends nescessary data
     *
     * @param p
     */
    public void addPlayer(final Player p) {
        mapobjectlocks.get(FieldObjectType.PLAYER).writeLock().lock();
        try {
            mapObjects.get(FieldObjectType.PLAYER).put(p.getObjectId(), p);
        } finally {
            mapobjectlocks.get(FieldObjectType.PLAYER).writeLock().unlock();
        }
        charactersLock.writeLock().lock();
        try {
            characters.add(p);
        } finally {
            charactersLock.writeLock().unlock();
        }
        p.setMap(this);
        p.setMapId(mapId);

        if (GameConstants.USE_DEBUG) {
            System.out.println("[FIELD] Mapid: " + mapId);
        }

        /*if (FieldLimit.CANNOTUSEMOUNTS.check(fieldLimit) && p.getBuffedValue(BuffStat.MONSTER_RIDING) != null) {
         p.cancelEffectFromBuffStat(BuffStat.MONSTER_RIDING);
         p.cancelBuffStats(BuffStat.MONSTER_RIDING);
         }*/
        OutPacket packet = PacketCreator.SpawnPlayerMapObject(p);
        if (!p.isHidden()) {
            for (final ItemPet pet : p.getPets()) {
                if (pet.getSummoned()) {
                    pet.setPosition(getGroundBelow(p.getPosition()));
                    broadcastMessage(p, PetPackets.ShowPet(p, pet, false, false), false);
                }
            }
            broadcastMessage(p, packet, false);
        } else {
            for (final ItemPet pet : p.getPets()) {
                if (pet.getSummoned()) {
                    pet.setPosition(getGroundBelow(p.getPosition()));
                    broadcastGMMessage(p, PetPackets.ShowPet(p, pet, false, false), false);
                }
            }
            broadcastGMMessage(p, packet, false);
            p.getClient().announce(PacketCreator.ShowHide());

            List<Pair<BuffStat, Integer>> stat = Collections.singletonList(new Pair<BuffStat, Integer>(BuffStat.DarkSight, 0));
            broadcastGMMessage(p, PacketCreator.BuffMapEffect(p.getId(), stat, false), false);
        }

        sendObjectPlacement(p.getClient());
        p.getClient().write(packet);

        if (!FieldLimit.CANNOTUSEPET.check(fieldLimit)) {
            for (final ItemPet pet : p.getPets()) {
                if (pet.getSummoned()) {
                    pet.setPosition(getGroundBelow(p.getPosition()));
                    p.getClient().write(PetPackets.ShowPet(p, pet, false, false));
                    if (!pet.getExceptionList().isEmpty()) {
                        p.announce(PetPackets.PetExceptionListResult(p, pet));
                    }
                }
            }
            p.updatePetAuto();
        } else {
            p.unequipAllPets();
        }

        switch (this.getId()) {
            case 1:
            case 2:
            case 809000101:
            case 809000201:
                p.getClient().write(EffectPackets.ShowEquipEffect());
                break;
        }
        final MapleStatEffect stat = p.getStatForBuff(BuffStat.Summon);
        if (stat != null) {
            final MapleSummon summon = p.getSummons().get(stat.getSourceId());
            summon.setPosition(p.getPosition());
            try {
                summon.setFh(getFootholds().findBelow(p.getPosition()).getId());
            } catch (NullPointerException e) {
                summon.setFh(0);
                FileLogger.printError("Position_Summon.txt", e);
            }
            p.addVisibleMapObject(summon);
            this.spawnSummon(summon);
        }
        if (p.getParty() != null) {
            p.silentPartyUpdate();
            p.getClient().announce(PartyPackets.UpdateParty(p.getClient().getChannel(), p.getParty(), MaplePartyOperation.SILENT_UPDATE, null));
            p.updatePartyMemberHP();
            p.receivePartyMemberHP();
        }
        if (mapEffect != null) {
            mapEffect.sendStartData(p.getClient());
        }
        if (mapTimer != null) {
            mapTimer.sendSpawnData(p.getClient());
        }
        if (getTimeLimit() > 0 && getForcedReturnField() != null) {
            p.getClient().write(PacketCreator.GetClockTimer(getTimeLimit()));
            p.startMapTimeLimitTask(this, this.getForcedReturnField());
        }
        if (p.getEventInstance() != null && p.getEventInstance().isTimerStarted()) {
            p.getClient().write(PacketCreator.GetClockTimer((int) (p.getEventInstance().getTimeLeft() / 1000)));
        }
        if (hasClock()) {
            p.getClient().announce(PacketCreator.GetClock());
        }
        if (hasBoat() > 0) {
            if (hasBoat() == 1) {
                p.getClient().announce((PacketCreator.ShipEffect(true)));
            } else {
                p.getClient().announce(PacketCreator.ShipEffect(false));
            }
        }
    }

    public void addMonsterSpawn(MapleMonster monster, int mobTime, int team) {
        Point newpos = calcPointBelow(monster.getPosition());
        newpos.y -= 1;
        SpawnPoint sp = new SpawnPoint(monster, newpos, !monster.isMobile(), mobTime, mobInterval, team);
        monsterSpawn.add(sp);

        if (!respawning) {
            return;
        }

        if (sp.shouldSpawn() || mobTime == -1) {
            spawnMonster(sp.getMonster());
        }
    }

    public void addAllMonsterSpawn(MapleMonster monster, int mobTime, int team) {
        Point newpos = calcPointBelow(monster.getPosition());
        newpos.y -= 1;
        SpawnPoint sp = new SpawnPoint(monster, newpos, !monster.isMobile(), mobTime, mobInterval, team);
        allMonsterSpawn.add(sp);
    }

    public void reportMonsterSpawnPoints(Player p) {
        p.dropMessage(6, "Mob spawnpoints on map " + getId() + ", with available Mob SPs " + monsterSpawn.size() + ", used " + spawnedMonstersOnMap.get() + ":");
        for (SpawnPoint sp : allMonsterSpawn) {
            p.dropMessage(6, "  id: " + sp.getMonsterId() + " canSpawn: " + !sp.getDenySpawn() + " numSpawned: " + sp.getSpawned() + " x: " + sp.getPosition().getX() + " y: " + sp.getPosition().getY() + " time: " + sp.getMobTime() + " team: " + sp.getTeam());
        }
    }

    public void beginSpawning() {
        this.respawning = true;
        this.Respawn(true);
    }

    public boolean isRespawning() {
        return respawning;
    }

    public void setRespawning(boolean respawning) {
        this.respawning = respawning;
    }

    public void instanceMapRespawn() {
        if (!allowSummons) {
            return;
        }

        final int numShouldSpawn = (short) ((monsterSpawn.size() - spawnedMonstersOnMap.get()));
        if (numShouldSpawn > 0) {
            List<SpawnPoint> randomSpawn = new ArrayList<>(monsterSpawn);
            Collections.shuffle(randomSpawn);
            int spawned = 0;
            for (SpawnPoint spawnPoint : randomSpawn) {
                if (spawnPoint.shouldSpawn()) {
                    spawnMonster(spawnPoint.getMonster());
                    spawned++;
                    if (spawned >= numShouldSpawn) {
                        break;
                    }
                }
            }
        }
    }

    public void instanceMapForceRespawn() {
        if (!allowSummons) {
            return;
        }

        final int numShouldSpawn = (short) ((monsterSpawn.size() - spawnedMonstersOnMap.get()));
        if (numShouldSpawn > 0) {
            List<SpawnPoint> randomSpawn = new ArrayList<>(monsterSpawn);
            Collections.shuffle(randomSpawn);
            int spawned = 0;
            for (SpawnPoint spawnPoint : randomSpawn) {
                if (spawnPoint.shouldForceSpawn()) {
                    spawnMonster(spawnPoint.getMonster());
                    spawned++;
                    if (spawned >= numShouldSpawn) {
                        break;
                    }
                }
            }
        }
    }

    private int getMaxRegularSpawn() {
        return (int) (monsterSpawn.size() / monsterRate);
    }

    public void Respawn(final boolean force) {
        if (!allowSummons) {
            return;
        }

        charactersLock.readLock().lock();
        try {
            if (characters.isEmpty()) {
                return;
            }
        } finally {
            charactersLock.readLock().unlock();
        }

        if (force) {
            short numShouldSpawn = (short) ((monsterSpawn.size() - spawnedMonstersOnMap.get()));

            if (numShouldSpawn > 0) {
                int spawned = 0;

                for (SpawnPoint spawnPoint : monsterSpawn) {
                    spawnPoint.getMonster();
                    spawned++;
                    if (spawned >= numShouldSpawn) {
                        break;
                    }
                }
            }
        } else {

            int ispawnedMonstersOnMap = spawnedMonstersOnMap.get();
            double getMaxSpawn = getMaxRegularSpawn() * 1;
            if (mapId == 610020002 || mapId == 610020004) {
                getMaxSpawn *= 2;
            }
            double numShouldSpawn = getMaxSpawn - ispawnedMonstersOnMap;
            if (mapId == 610020002 || mapId == 610020004) {
                numShouldSpawn *= 2;
            }
            if (numShouldSpawn + ispawnedMonstersOnMap >= getMaxSpawn) {
                numShouldSpawn = getMaxSpawn - ispawnedMonstersOnMap;
            }
            if (numShouldSpawn <= 0) {
                return;
            }

            List<SpawnPoint> randomSpawn = new ArrayList<>(monsterSpawn);
            Collections.shuffle(randomSpawn);
            int spawned = 0;
            for (SpawnPoint spawnPoint : randomSpawn) {
                if (!isSpawns && spawnPoint.getMobTime() > 0) {
                    continue;
                }
                if (spawnPoint.shouldSpawn()) {
                    spawnMonster(spawnPoint.getMonster());
                    spawned++;
                }
                if (spawned >= numShouldSpawn) {
                    break;
                }
            }
        }
    }

    public Collection<SpawnPoint> getSpawnPoints() {
        return monsterSpawn;
    }

    public void allowSummonState(boolean b) {
        Field.this.allowSummons = b;
    }

    public boolean getSummonState() {
        return Field.this.allowSummons;
    }

    public MCWZData getMCPQData() {
        return this.mcpqData;
    }

    public void setMCPQData(MCWZData data) {
        this.mcpqData = data;
    }

    public void startMapEffect(String msg, int itemId, long time) {
        if (mapEffect != null) {
            return;
        }
        mapEffect = new FieldEffect(msg, itemId);
        broadcastMessage(mapEffect.makeStartData());
        MapTimer.getInstance().schedule(() -> {
            broadcastMessage(mapEffect.makeDestroyData());
            mapEffect = null;
        }, time);
    }

    public void warpEveryone(int to) {
        getCharactersThreadsafe().forEach((chr) -> {
            chr.changeMap(to);
        });
    }

    public void warpEveryone2(int to) {
        getCharactersThreadsafe().forEach((chr) -> {
            chr.changeMap(to, 1);
        });
    }

    public void removePlayer(Player p) {
        charactersLock.writeLock().lock();
        try {
            characters.remove(p);
        } finally {
            charactersLock.writeLock().unlock();
        }
        removeMapObject(p);
        if (!p.isHidden()) {
            broadcastMessage(PacketCreator.RemovePlayerFromMap(p.getId()));
        } else {
            broadcastGMMessage(PacketCreator.RemovePlayerFromMap(p.getId()));
        }
        p.cancelMapTimeLimitTask();
        p.getSummons().values().forEach((summon) -> {
            if (summon.isPuppet()) {
                p.cancelBuffStats(BuffStat.Puppet);
            } else {
                removeMapObject(summon);
            }
        });
        List<MapleSummon> removes = new LinkedList<>();
        p.getPirateSummons().stream().map((summon) -> {
            removeMapObject(summon);
            return summon;
        }).filter((summon) -> (summon.isOctopus())).forEach((summon) -> {
            removes.add(summon);
        });
        removes.forEach((summon) -> {
            p.removePirateSummon(summon);
        });
        p.leaveMap(this);
    }

    public void clearMapObjects() {
        clearDrops();
        killAllMonsters();
        resetReactors();
    }

    public void broadcastGMMessage(OutPacket packet) {
        broadcastGMMessage(null, packet, Double.POSITIVE_INFINITY, null);
    }

    public void broadcastMessage(Player source, OutPacket packet, boolean repeatToSource) {
        broadcastMessage(repeatToSource ? null : source, packet);
    }

    public void broadcastMessage(OutPacket packet) {
        broadcastMessage(null, packet);
    }

    public void broadcastMessage(Player source, OutPacket packet) {
        charactersLock.readLock().lock();
        try {
            for (Player chr : characters) {
                if (chr != source) {
                    chr.announce(packet);
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
    }

    public void broadcastGMMessage(Player source, final OutPacket packet, boolean repeatToSource) {
        broadcastGMMessage(repeatToSource ? null : source, packet, Double.POSITIVE_INFINITY, source.getPosition());
    }

    private void broadcastGMMessage(Player source, OutPacket packet, double rangeSq, Point rangedFrom) {
        this.charactersLock.readLock().lock();
        try {
            if (source == null) {
                for (Player chr : characters) {
                    if (chr.isGameMaster()) {
                        chr.getClient().announce(packet);
                    }
                }
            } else {
                for (Player chr : characters) {
                    if (chr != source && (chr.getAdministrativeLevel() >= source.getAdministrativeLevel())) {
                        chr.getClient().announce(packet);
                    }
                }
            }
        } finally {
            this.charactersLock.readLock().unlock();
        }
    }

    public void broadcastNONGMMessage(Player source, OutPacket packet, boolean repeatToSource) {
        broadcastNONGMMessage(repeatToSource ? null : source, packet);
    }

    public void broadcastNONGMMessage(Player source, OutPacket packet) {
        this.charactersLock.readLock().lock();
        try {
            for (Player chr : characters) {
                if (chr != source && !chr.isGameMaster()) {
                    chr.getClient().announce(packet);
                }
            }
        } finally {
            this.charactersLock.readLock().unlock();
        }
    }

    public List<FieldItem> getAllItemsThreadsafe() {
        ArrayList<FieldItem> ret = new ArrayList<>();
        this.mapobjectlocks.get(FieldObjectType.ITEM).readLock().lock();
        try {
            Iterator<FieldObject> obj = mapObjects.get(FieldObjectType.ITEM).values().iterator();
            while (obj.hasNext()) {
                FieldObject o = obj.next();
                if (o instanceof FieldItem) {
                    ret.add((FieldItem) o);
                }
            }
        } finally {
            this.mapobjectlocks.get(FieldObjectType.ITEM).readLock().unlock();
        }
        return ret;
    }

    public List<Reactor> getAllReactorsThreadsafe() {
        ArrayList<Reactor> ret = new ArrayList<>();
        this.mapobjectlocks.get(FieldObjectType.REACTOR).readLock().lock();
        try {
            Iterator<FieldObject> obj = mapObjects.get(FieldObjectType.REACTOR).values().iterator();
            while (obj.hasNext()) {
                FieldObject o = obj.next();
                if (o instanceof Reactor) {
                    ret.add((Reactor) o);
                }
            }
        } finally {
            this.mapobjectlocks.get(FieldObjectType.REACTOR).readLock().unlock();
        }
        return ret;
    }

    private void sendObjectPlacement(Client mapleClient) {
        Player p = mapleClient.getPlayer();
        Map<FieldObjectType, LinkedHashMap<Integer, FieldObject>> objects = new LinkedHashMap<>();
        for (FieldObjectType type : FieldObjectType.values()) {
            LinkedHashMap<Integer, FieldObject> ret = new LinkedHashMap<>();
            this.mapobjectlocks.get(type).readLock().lock();
            try {
                for (Map.Entry<Integer, FieldObject> kvp : this.mapObjects.get(type).entrySet()) {
                    ret.put(kvp.getKey(), kvp.getValue());
                }
            } finally {
                this.mapobjectlocks.get(type).readLock().unlock();
            }
            if (!ret.isEmpty()) {
                objects.put(type, ret);
            }
        }
        if (!objects.isEmpty()) {
            for (FieldObjectType type : objects.keySet()) {
                for (FieldObject o : objects.get(type).values()) {
                    if (o.getType() == FieldObjectType.SUMMON) {
                        MapleSummon summon = (MapleSummon) o;
                        if (summon.getOwner() == p) {
                            if (p.getSummons().isEmpty() || !p.getSummons().containsValue(summon)) {
                                this.mapobjectlocks.get(FieldObjectType.SUMMON).writeLock().lock();
                                try {
                                    mapObjects.get(FieldObjectType.SUMMON).remove(o.getObjectId());
                                } finally {
                                    this.mapobjectlocks.get(FieldObjectType.SUMMON).writeLock().unlock();
                                }
                                continue;
                            }
                        }
                    }
                    if (MapConstants.isNonRangedType(o.getType())) {
                        o.sendSpawnData(mapleClient);
                    } else if (o.getType() == FieldObjectType.MONSTER) {
                        updateMonsterController((MapleMonster) o);
                    }
                }
            }
        }
        if (p != null) {
            for (FieldObject o : getMapObjectsInRange(p.getPosition(), getRangedDistance(), MapConstants.RANGE_FIELD_OBJ)) {
                if (o.getType() == FieldObjectType.REACTOR) {
                    if (!((Reactor) o).isAlive()) {
                        continue;
                    }
                    o.sendSpawnData(p.getClient());
                    p.addVisibleMapObject(o);
                }
            }
        }
    }

    public List<FieldObject> getMapObjectsInRange(Point from, double rangeSq, List<FieldObjectType> types) {
        List<FieldObject> ret = new LinkedList<>();
        for (FieldObjectType type : types) {
            this.mapobjectlocks.get(type).readLock().lock();
            try {
                Iterator<FieldObject> objs = this.mapObjects.get(type).values().iterator();
                while (objs.hasNext()) {
                    FieldObject o = objs.next();
                    if (types.contains(o.getType()) && from.distanceSq(o.getPosition()) <= rangeSq) {
                        ret.add(o);
                    }
                }
            } finally {
                this.mapobjectlocks.get(type).readLock().unlock();
            }
        }
        return ret;
    }

    public void addPortal(Portal myPortal) {
        portals.put(myPortal.getId(), myPortal);
    }

    public Portal getPortal(String portalname) {
        for (Portal port : portals.values()) {
            if (port.getName().equals(portalname)) {
                return port;
            }
        }
        return null;
    }

    public int getPortalIndex(String portalname) {
        int index = 0;
        for (Portal port : portals.values()) {
            if (portalname.equals(port.getName())) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public Collection<Portal> getPortals() {
        return Collections.unmodifiableCollection(portals.values());
    }

    public List<Portal> getAvailableDoorPortals() {
        List<Portal> availablePortals = new ArrayList<>();

        getPortals().stream().filter((port) -> (port.getType() == Portal.DOOR_PORTAL)).forEach((port) -> {
            availablePortals.add(port);
        });
        return availablePortals;
    }

    public Portal getDoorPortal(int doorid) {
        Portal doorPortal = portals.get(0x80 + doorid);
        if (doorPortal == null) {
            //FilePrinter.printError(FilePrinter.EXCEPTION, "[DOOR] " + mapName + "(" + mapid + ") does not contain door portalid " + doorid);
            return portals.get(0x80);
        }
        return doorPortal;
    }

    public Portal findClosestPortal(Point from) {
        Portal closest = getPortal(0);
        double distance, shortestDistance = Double.POSITIVE_INFINITY;
        for (Portal portal : portals.values()) {
            distance = portal.getPosition().distanceSq(from);
            if (distance < shortestDistance) {
                closest = portal;
                shortestDistance = distance;
            }
        }
        return closest;
    }

    public void removePortals() {
        getPortals().forEach((pt) -> {
            pt.setScriptName("blank");
        });
    }

    public Portal getPortal(int portalid) {
        return portals.get(portalid);
    }

    public void addMapleArea(Rectangle rec) {
        areas.add(rec);
    }

    public List<Rectangle> getAreas() {
        return new ArrayList<>(areas);
    }

    public Rectangle getArea(int index) {
        return areas.get(index);
    }

    public void setFootholds(MapleFootholdTree footholds) {
        this.footholds = footholds;
    }

    public MapleFootholdTree getFootholds() {
        return footholds;
    }

    public void resetPQ() {
        resetPQ(1);
    }

    public void resetPQ(int difficulty) {
        resetMapObjects(difficulty, true);
    }

    public void resetMapObjects(int difficulty, boolean isPq) {
        clearMapObjects();

        restoreMapSpawnPoints();
        instanceMapFirstSpawn(difficulty, isPq);
    }

    public void restoreMapSpawnPoints() {
        for (SpawnPoint spawnPoint : monsterSpawn) {
            spawnPoint.setDenySpawn(false);
        }
    }

    public void instanceMapFirstSpawn(int difficulty, boolean isPq) {
        for (SpawnPoint spawnPoint : allMonsterSpawn) {
            if (spawnPoint.getMobTime() == -1) {   //just those allowed to be spawned only once
                spawnMonster(spawnPoint.getMonster());
            }
        }
    }

    public Player getCharacterById(int id) {
        this.charactersLock.readLock().lock();
        try {
            for (Player c : this.characters) {
                if (c.getId() == id) {
                    return c;
                }
            }
        } finally {
            this.charactersLock.readLock().unlock();
        }
        return null;
    }

    public void moveMonster(MapleMonster monster, Point reportedPos) {
        monster.setPosition(reportedPos);
        this.charactersLock.readLock().lock();
        try {
            for (Player chr : characters) {
                updateMapObjectVisibility(chr, monster);
            }
        } finally {
            this.charactersLock.readLock().unlock();
        }
    }

    public void movePlayer(Player player, Point newPosition) {
        if (player == null) {
            return;
        }
        player.setPosition(newPosition);
        try {
            Collection<FieldObject> visibleObjects = player.getAndWriteLockVisibleMapObjects();
            ArrayList<FieldObject> copy = new ArrayList<>(visibleObjects);
            Iterator<FieldObject> itr = copy.iterator();
            while (itr.hasNext()) {
                FieldObject o = itr.next();
                if (o != null) {
                    if (getMapObject(o.getObjectId(), o.getType()) == o) {
                        updateMapObjectVisibility(player, o);
                    } else {
                        visibleObjects.remove(o);
                    }
                }
            }
            for (FieldObject mo : getMapObjectsInRange(player.getPosition(), getRangedDistance(), MapConstants.RANGE_FIELD_OBJ)) {
                if (mo != null && !visibleObjects.contains(mo)) {
                    mo.sendSpawnData(player.getClient());
                    visibleObjects.add(mo);
                }
            }
        } finally {
            player.unlockWriteVisibleMapObjects();
        }
    }

    private void updateMapObjectVisibility(Player p, FieldObject mo) {
        if (!p.isMapObjectVisible(mo)) {
            if (mo.getType() == FieldObjectType.SUMMON || mo.getPosition().distanceSq(p.getPosition()) <= getRangedDistance()) {
                p.addVisibleMapObject(mo);
                mo.sendSpawnData(p.getClient());
            }
        } else {
            if (mo.getType() != FieldObjectType.SUMMON && mo.getPosition().distanceSq(p.getPosition()) > getRangedDistance()) {
                p.removeVisibleMapObject(mo);
                mo.sendDestroyData(p.getClient());
            }
            /*else if (mo.getType() == FieldObjectType.MONSTER) {
                if (p.getPosition().distanceSq(mo.getPosition()) <= 480000) {
                    updateMonsterController((MapleMonster) mo);
                }
            }*/
        }
    }

    public List<Player> getPlayersInRange(Rectangle box, List<Player> chr) {
        List<Player> character = new LinkedList<>();
        this.charactersLock.readLock().lock();
        try {
            for (Player a : characters) {
                if (chr.contains(a.getClient().getPlayer())) {
                    if (box.contains(a.getPosition())) {
                        character.add(a);
                    }
                }
            }
        } finally {
            this.charactersLock.readLock().unlock();
        }
        return character;
    }

    public List<FieldObject> getMapObjectsInBox(Rectangle box, List<FieldObjectType> types) {
        List<FieldObject> ret = new LinkedList<>();
        for (FieldObjectType type : types) {
            this.mapobjectlocks.get(type).readLock().lock();
            try {
                Iterator<FieldObject> objs = this.mapObjects.get(type).values().iterator();
                while (objs.hasNext()) {
                    FieldObject o = objs.next();
                    if (types.contains(o.getType()) && box.contains(o.getPosition())) {
                        ret.add(o);
                    }
                }
            } finally {
                this.mapobjectlocks.get(type).readLock().unlock();
            }
        }
        return ret;
    }

    public SpawnPoint findClosestSpawnpoint(Point from) {
        SpawnPoint closest = null;
        double shortestDistance = Double.POSITIVE_INFINITY;
        for (SpawnPoint sp : monsterSpawn) {
            double distance = sp.getPosition().distanceSq(from);
            if (distance < shortestDistance) {
                closest = sp;
                shortestDistance = distance;
            }
        }
        return closest;
    }

    public Portal findClosestPlayerSpawnpoint(Point from) {
        Portal closest = null;
        double shortestDistance = Double.POSITIVE_INFINITY;
        for (Portal portal : portals.values()) {
            double distance = portal.getPosition().distanceSq(from);
            if (portal.getType() >= 0 && portal.getType() <= 1 && distance < shortestDistance && portal.getTargetMapId() == MapConstants.NULL_MAP) {
                closest = portal;
                shortestDistance = distance;
            }
        }
        return closest;
    }

    public final int getMapObjectSize() {
        return mapObjects.size() + getCharactersSize() - characters.size();
    }

    public final int getCharactersSize() {
        return characters.size();
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setClock(boolean hasClock) {
        this.clock = hasClock;
    }

    public boolean hasClock() {
        return clock;
    }

    public void setTown(boolean isTown) {
        this.town = isTown;
    }

    public boolean isTown() {
        return town;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public void setEverlast(boolean everlast) {
        this.everlast = everlast;
    }

    public boolean getEverlast() {
        return everlast;
    }

    public int getSpawnedMonstersOnMap() {
        return spawnedMonstersOnMap.get();
    }

    public boolean makeDisappearItemFromMap(FieldObject mapobj) {
        if (mapobj instanceof FieldItem) {
            return makeDisappearItemFromMap((FieldItem) mapobj);
        } else {
            return mapobj == null;
        }
    }

    private class ActivateItemReactor implements Runnable {

        private final FieldItem mapitem;
        private final Reactor reactor;
        private final Client c;

        public ActivateItemReactor(FieldItem mapitem, Reactor reactor, Client c) {
            this.mapitem = mapitem;
            this.reactor = reactor;
            this.c = c;
        }

        @Override
        public void run() {
            if (reactor.getReactorType() == 100) {
                if (reactor.getShouldCollect() == true && mapitem != null && mapitem == getMapObject(mapitem.getObjectId(), FieldObjectType.ITEM)) {
                    if (mapitem.isPickedUp()) {
                        return;
                    }
                    mapitem.setPickedUp(true);

                    reactor.setShouldCollect(false);
                    broadcastMessage(PacketCreator.RemoveItemFromMap(mapitem.getObjectId(), 0, 0));

                    removeMapObject(mapitem);

                    reactor.hitReactor(c);

                    if (reactor.getDelay() > 0) {
                        MapTimer tMan = MapTimer.getInstance();
                        tMan.schedule(new Runnable() {
                            @Override
                            public void run() {
                                reactor.resetReactorActions(0);
                                broadcastMessage(PacketCreator.TriggerReactor(reactor, 0));
                            }
                        }, reactor.getDelay());
                    }
                }
            }
        }
    }

    private static interface DelayedPacketCreation {

        void sendPackets(Client c);
    }

    private static interface SpawnCondition {

        boolean canSpawn(Player chr);
    }

    public int getHPDecProtect() {
        return this.protectItem;
    }

    public void setHPDecProtect(int delta) {
        this.protectItem = delta;
    }

    private int hasBoat() {
        return !boat ? 0 : (docked ? 1 : 2);
    }

    public void setBoat(boolean hasBoat) {
        this.boat = hasBoat;
    }

    public void setDocked(boolean isDocked) {
        this.docked = isDocked;
    }

    public void mapMessage(int type, String message) {
        broadcastMessage(PacketCreator.ServerNotice(type, message));
    }

    public void removeItems() {
        Field map = this;
        double range = Double.POSITIVE_INFINITY;
        List<FieldObject> items = map.getMapObjectsInRange(new Point(0, 0), range, Arrays.asList(FieldObjectType.ITEM));
        for (FieldObject itemmo : items) {
            map.removeMapObject(itemmo);
        }
    }

    public MapleMonster findClosestMonster(Point from, double range) {
        MapleMonster closest = null;
        double shortestDistance = range;
        List<FieldObject> monstersi = this.getMapObjectsInRange(from, shortestDistance, Arrays.asList(FieldObjectType.MONSTER));
        for (FieldObject monstermo : monstersi) {
            MapleMonster mob = (MapleMonster) monstermo;
            double distance = mob.getPosition().distanceSq(from);
            if (distance < shortestDistance && mob.getId() != 9300061) {
                closest = mob;
                shortestDistance = distance;
            }
        }
        return closest;
    }

    private final class warpAll implements Runnable {

        private final Field toGo;
        private final Field from;

        public warpAll(Field toGoto, Field from) {
            this.toGo = toGoto;
            this.from = from;
        }

        @Override
        public void run() {
            synchronized (toGo) {
                for (Player ppp : characters) {
                    if (ppp.getMap().equals(from)) {
                        ppp.changeMap(toGo, toGo.getPortal(0));
                        if (ppp.getEventInstance() != null) {
                            ppp.getEventInstance().unregisterPlayer(ppp);
                        }
                    }
                }
            }
        }
    }

    public void warpAllToNearestTown(String reason) {
        this.broadcastMessage(PacketCreator.ServerNotice(5, reason));
        int rid = this.forcedReturnMap == MapConstants.NULL_MAP ? this.returnFieldId : this.forcedReturnMap;
        new warpAll(Start.getInstance().getWorldById(world).getChannelById(this.channel).getMapFactory().getMap(rid), this).run();
    }

    public void dcAllPlayers() {
        int rid = this.forcedReturnMap == MapConstants.NULL_MAP ? this.returnFieldId : this.forcedReturnMap;
        new warpAll(Start.getInstance().getWorldById(world).getChannelById(this.channel).getMapFactory().getMap(rid), this).run();
    }

    public boolean setPortalDisable(boolean v) {
        this.disablePortal = v;
        return disablePortal;
    }

    public boolean getPortalDisable() {
        return this.disablePortal;
    }

    public boolean setDisableInvincibilitySkills(boolean v) {
        this.disableInvincibilitySkills = v;
        return disableInvincibilitySkills;
    }

    public boolean getDisableInvincibilitySkills() {
        return this.disableInvincibilitySkills;
    }

    public boolean setDisableDamage(boolean v) {
        this.disableDamage = v;
        return disableDamage;
    }

    public boolean getDisableDamage() {
        return this.disableDamage;
    }

    public boolean setDisableChat(boolean v) {
        this.disableChat = v;
        return disableChat;
    }

    public boolean getDisableChat() {
        return this.disableChat;
    }

    public boolean isSwim() {
        return swim;
    }

    public void setSwim(boolean swim) {
        this.swim = swim;
    }

    public final boolean canHurt(final long now) {
        if (lastHurtTime > 0 && lastHurtTime + decHPInterval < now) {
            lastHurtTime = now;
            return true;
        }
        return false;
    }

    public final int getHPDec() {
        return decHP;
    }

    public final void setHPDec(final int delta) {
        if (delta > 0) {
            lastHurtTime = System.currentTimeMillis();
        }
        decHP = (short) delta;
    }

    public final int getHPDecInterval() {
        return decHPInterval;
    }

    public final void setHPDecInterval(final int delta) {
        decHPInterval = delta;
    }

    public final void resetFully() {
        killAllMonsters();
        removeDrops();
        Respawn(true);
    }

    public void setmapbosslog(String a) {
        charactersLock.readLock().lock();
        try {
            int size = characters.size();
            for (Player chr : characters) {
                chr.增加每日(a);
            }
        } finally {
            charactersLock.readLock().unlock();
        }
    }
}
