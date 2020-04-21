package scripting;

import scripting.event.EventManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import client.player.Player;
import client.Client;
import client.player.PlayerQuery;
import client.player.inventory.Equip;
import client.player.inventory.Inventory;
import client.player.inventory.types.InventoryType;
import client.player.inventory.Item;
import client.player.inventory.ItemPet;
import server.quest.MapleQuestStatus;
import java.util.LinkedList;
import handling.channel.ChannelServer;
import community.MapleParty;
import community.MaplePartyCharacter;
import constants.ExperienceConstants;
import constants.GameConstants;
import constants.ItemConstants;
import constants.ServerProperties;
import handling.world.service.BroadcastService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import launch.Start;
import packet.creators.EffectPackets;
import packet.creators.PacketCreator;
import packet.creators.PetPackets;
import scripting.bossrank.BossRankInfo;
import scripting.bossrank.BossRankManager;
import scripting.event.EventInstanceManager;
import server.itens.InventoryManipulator;
import server.itens.ItemInformationProvider;
import server.transitions.Boats;
import server.transitions.Cabin;
import server.transitions.Elevator;
import server.transitions.Genie;
import server.transitions.Trains;
import server.life.MapleMonster;
import server.maps.Field;
import server.maps.object.FieldObject;
import server.maps.object.FieldObjectType;
import server.maps.reactors.Reactor;
import server.quest.MapleQuest;
import tools.Randomizer;
import tools.TimerTools.MapTimer;
import tools.TimerTools.MiscTimer;

public class AbstractPlayerInteraction {

    public Client c;

    public AbstractPlayerInteraction(final Client c) {
        this.c = c;
    }

    public Client getClient() {
        return c;
    }

    public Player getPlayer() {
        return c.getPlayer();
    }

    public final void warp(final int map) {
        Field target = getWarpMap(map);
        c.getPlayer().changeMap(target, target.getPortal(0));
        /* final Field mapz = getWarpMap(map);
        try {
            c.getPlayer().changeMap(mapz, mapz.getPortal(Randomizer.nextInt(mapz.getPortals().size())));
        } catch (Exception e) {
            c.getPlayer().changeMap(mapz, mapz.getPortal(0));
        }*/
    }

    public void warp(int map, int portal) {
        Field target = getWarpMap(map);
        c.getPlayer().changeMap(target, target.getPortal(portal));
    }

    public void warp(int map, String portal) {
        Field target = getWarpMap(map);
        c.getPlayer().changeMap(target, target.getPortal(portal));
    }

    public List<Player> getPartyMembers() {
        if (getPlayer().getParty() == null) {
            return null;
        }
        List<Player> chars = new LinkedList<>();
        for (ChannelServer channel : getPlayer().getWorld().getChannels()) {
            for (Player chr : channel.getPartyMembers(getPlayer().getParty())) {
                if (chr != null) {
                    chars.add(chr);
                }
            }
        }
        return chars;
    }

    public void givePartyExp(String PQ, boolean instance) {
        //1 player  =  +0% bonus (100)
        //2 players =  +0% bonus (100)
        //3 players =  +0% bonus (100)
        //4 players = +10% bonus (110)
        //5 players = +20% bonus (120)
        //6 players = +30% bonus (130)
        MapleParty party = getPlayer().getParty();
        int size = party.getMembers().size();

        if (instance) {
            for (MaplePartyCharacter member : party.getMembers()) {
                if (member == null || !member.isOnline() || member.getPlayer().getEventInstance() == null) {
                    size--;
                }
            }
        }

        int bonus = size < 4 ? 100 : 70 + (size * 10);
        for (MaplePartyCharacter member : party.getMembers()) {
            if (member == null || !member.isOnline()) {
                continue;
            }
            Player player = member.getPlayer();
            if (instance && player.getEventInstance() == null) {
                continue;
            }
            int base = ExperienceConstants.getPartyQuestExp(PQ, player.getLevel());
            int exp = base * bonus / 100;
            player.gainExp(exp, true, true);
            if (GameConstants.PQ_BONUS_EXP_RATE > 0 && System.currentTimeMillis() <= GameConstants.EVENT_END_TIMESTAMP) {
                player.gainExp((int) (exp * GameConstants.PQ_BONUS_EXP_RATE), true, true);
            }
        }
    }

    public EventInstanceManager getEventInstance() {
        return getPlayer().getEventInstance();
    }

    public void warpParty(int id) {
        for (Player mc : getPartyMembers()) {
            mc.changeMap(getWarpMap(id));
        }
    }

    public final void warpMap(final int mapid, final int portal) {
        final Field map = getMap(mapid);
        for (Player p : c.getPlayer().getMap().getCharactersThreadsafe()) {
            p.changeMap(map, map.getPortal(portal));
        }
    }

    private Field getWarpMap(int map) {
        Field target;
        if (getPlayer().getEventInstance() == null) {
            target = c.getChannelServer().getMapFactory().getMap(map);
        } else {
            target = getPlayer().getEventInstance().getMapInstance(map);
        }
        return target;
    }

    public int getItemQuantity(int itemid) {
        return getPlayer().getItemQuantity(itemid, false);
    }

    public void fakeRelog() {
        getPlayer().fakeRelog();
    }

    public Field getMap(int map) {
        return getWarpMap(map);
    }

    public final Field getMap() {
        return c.getPlayer().getMap();
    }

    public boolean haveItem(int itemid) {
        return haveItem(itemid, 1);
    }

    public boolean haveItem(int itemid, int quantity) {
        return haveItem(itemid, quantity, false, true);
    }

    public boolean haveItem(int itemid, int quantity, boolean checkEquipped, boolean greaterOrEquals) {
        return c.getPlayer().haveItem(itemid, quantity, checkEquipped, greaterOrEquals);
    }

    public void giveItemBuff(int itemID) {
        c.getPlayer().giveItemBuff(itemID);
    }

    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public boolean isEventLeader() {
        return getEventInstance() != null && getPlayer().getId() == getEventInstance().getLeaderId();
    }

    public EventManager getEventManager(String event) {
        return getClient().getEventManager(event);
    }

    public final void playPortalSE() {
        c.write(EffectPackets.ShowOwnBuffEffect(0, 7));
    }

    public boolean hasBalrogBoat() {
        return Boats.hasBalrog();
    }

    public boolean getEventNotScriptOpen(String evento) {
        switch (evento) {
            case "Trens":
                return Trains.trainsOpen();
            case "Barcos":
                return Boats.boatOpen();
            case "Cabine":
                return Cabin.cabinOpen();
            case "Genio":
                return Genie.genioOpen();
            case "ElevadorDescendo":
                return Elevator.elevatorIsDown();
            case "ElevadorSubindo":
                return Elevator.elevatorIsUp();
            default:
                return false;
        }
    }

    public final boolean canHold(final int itemid) {
        return c.getPlayer().getInventory(ItemConstants.getInventoryType(itemid)).getNextFreeSlot() > -1;
    }

    public final boolean canHold(final int itemid, final int quantity) {
        return InventoryManipulator.checkSpace(c, itemid, quantity, "");
    }

    public final boolean canHold() {
        for (int i = 1; i <= 5; i++) {
            if (c.getPlayer().getInventory(InventoryType.getByType((byte) i)).getNextFreeSlot() <= -1) {
                return false;
            }
        }
        return true;
    }

    public void gainItem(int id, short quantity) {
        gainItem(id, quantity, false);
    }

    public void gainItem(int id) {
        gainItem(id, (short) 1, false);
    }

    public void givePartyExp(int amount, List<Player> party) {
        for (Player chr : party) {
            chr.gainExperience((amount * c.getChannelServer().getExpRate()), true, true);
        }
    }

    public void givePartyNX(int amount, List<Player> party) {
        for (Player chr : party) {
            chr.getCashShop().gainCash(1, amount);
            chr.dropMessage("[" + ServerProperties.Login.SERVER_NAME + " PartyQuest] You earned " + amount + " from NX.");
        }
    }

    public void updateQuest(int questid, String data) {
        MapleQuestStatus status = c.getPlayer().getQuest(MapleQuest.getInstance(questid));
        updateQuest(questid, status.getAnyProgressKey(), data);
    }

    public void updateQuest(int questid, int data) {
        MapleQuestStatus status = c.getPlayer().getQuest(MapleQuest.getInstance(questid));
        updateQuest(questid, status.getAnyProgressKey(), data);
    }

    public void updateQuest(int questid, int pid, int data) {
        updateQuest(questid, pid, String.valueOf(data));
    }

    public void updateQuest(int questid, int pid, String data) {
        MapleQuestStatus status = c.getPlayer().getQuest(MapleQuest.getInstance(questid));
        status.setStatus(MapleQuestStatus.Status.STARTED);
        status.setProgress(pid, data);
        c.getPlayer().updateQuest(status);
    }

    public MapleQuestStatus.Status getQuestStatus(int id) {
        return c.getPlayer().getQuest(MapleQuest.getInstance(id)).getStatus();
    }

    public boolean isQuestCompleted(int quest) {
        try {
            return getQuestStatus(quest) == MapleQuestStatus.Status.COMPLETED;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public boolean isQuestStarted(int quest) {
        try {
            return getQuestStatus(quest) == MapleQuestStatus.Status.STARTED;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public int getQuestProgress(int qid) {
        return Integer.parseInt(getPlayer().getQuest(MapleQuest.getInstance(qid)).getProgress().get(0));
    }

    /**
     * Gives item with the specified id or takes it if the quantity is negative.
     * Note that this does NOT take items from the equipped inventory.
     * randomStats for generating random stats on the generated equip.
     *
     * @param id
     * @param quantity
     * @param randomStats
     */
    public void gainItem(int id, short quantity, boolean randomStats) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        if (!ii.isItemValid(id)) {
            System.out.println("[REMOVER] Item invalido: " + id);
            return;
        }
        if (quantity >= 0) {
            boolean space = false;
            Item item = ii.getEquipById(id);
            InventoryType type = ii.getInventoryType(id);
            StringBuilder logInfo = new StringBuilder(c.getPlayer().getName());
            logInfo.append(" received ");
            logInfo.append(quantity);
            logInfo.append(" from a scripted PlayerInteraction (");
            logInfo.append(this.toString());
            logInfo.append(")");
            if (!InventoryManipulator.checkSpace(c, id, quantity, "")) {
                c.write(PacketCreator.ServerNotice(1, "Your inventory is full. Please remove an item from your " + type.name() + " inventory."));
                return;
            }
            if (type.equals(InventoryType.EQUIP) && !ii.isThrowingStar(item.getItemId()) && !ii.isBullet(item.getItemId())) {
                if (randomStats) {
                    InventoryManipulator.addFromDrop(c, ii.randomizeStats((Equip) item), logInfo.toString(), false);
                } else {
                    InventoryManipulator.addFromDrop(c, (Equip) item, logInfo.toString(), false);
                }
            } else {
                InventoryManipulator.addById(c, id, quantity, logInfo.toString(), null, null);
            }
        } else {
            InventoryManipulator.removeById(c, ItemInformationProvider.getInstance().getInventoryType(id), id, -quantity, true, false);
        }
        c.write(PacketCreator.GetShowItemGain(id, quantity, true));
    }

    public void changeMusic(String songName) {
        getPlayer().getMap().broadcastMessage(EffectPackets.MusicChange(songName));
    }

    public void setPlayerVariable(String name, String value) {
        PlayerQuery.setPlayerVariable(name, value, getPlayer());
    }

    public String getPlayerVariable(String name) {
        return PlayerQuery.getPlayerVariable(name, getPlayer());
    }

    public String getSiteVariable(String name) {
        return PlayerQuery.getPlayerVariable(name, getPlayer());
    }

    public void deletePlayerVariable(String name) {
        PlayerQuery.deletePlayerVariable(name, getPlayer());
    }

    public void playerMessage(String message) {
        playerMessage(5, message);
    }

    public void mapMessage(String message) {
        mapMessage(5, message);
    }

    public void playerMessage(int type, String message) {
        c.write(PacketCreator.ServerNotice(type, message));
    }

    public void mapMessage(int type, String message) {
        getPlayer().getMap().broadcastMessage(PacketCreator.ServerNotice(type, message));
    }

    public void mapClock(int time) {
        getPlayer().getMap().broadcastMessage(PacketCreator.GetClockTimer(time));
    }

    public MapleParty getParty() {
        return getPlayer().getParty();
    }

    public boolean isLeader() {
        return isPartyLeader();
    }

    public boolean isPartyLeader() {
        if (getParty() == null) {
            return false;
        }
        return getParty().getLeader().getId() == getPlayer().getId();
    }

    public Item AddItemExpiration(Item cii) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        InventoryType mit = ii.getInventoryType(cii.getItemId());
        long l = Long.valueOf(3);
        long time = 1000L * 60L * 60L * 24L * l;
        for (int cards : ItemConstants.CARDS_4HRS) {
            if (cards == cii.getItemId()) {
                time = 1000L * 60L * 60L * 4L;
            }
        }
        if (mit.equals(InventoryType.EQUIP)) {
            Item ret = ii.getEquipById(cii.getItemId());
            ret.setExpiration(System.currentTimeMillis() + time);
            return ret;
        } else {
            Item nItem = new Item(cii.getItemId(), (byte) -1, (short) cii.getQuantity());
            nItem.setExpiration(System.currentTimeMillis() + time);
            return nItem;
        }
    }

    public void givePartyItemsExpiration(int id, short quantity, List<Player> party) {
        for (Player p : party) {
            Client c = p.getClient();
            if (quantity >= 0) {
                Item nItem = new Item(id, (byte) -1, quantity);
                InventoryManipulator.addFromDrop(c, AddItemExpiration(nItem), null, true);
            } else {
                InventoryManipulator.removeById(c, ItemInformationProvider.getInstance().getInventoryType(id), id, -quantity, true, false);
            }
            c.write(PacketCreator.GetShowItemGain(id, quantity, true));
        }
    }

    public void givePartyItems(int id, short quantity, List<Player> party) {
        for (Player chr : party) {
            Client cl = chr.getClient();
            if (quantity >= 0) {
                StringBuilder logInfo = new StringBuilder(cl.getPlayer().getName());
                logInfo.append(" received ");
                logInfo.append(quantity);
                logInfo.append(" from event ");
                logInfo.append(chr.getEventInstance().getName());
                InventoryManipulator.addById(cl, id, quantity, logInfo.toString(), null, null);
            } else {
                InventoryManipulator.removeById(cl, ItemInformationProvider.getInstance().getInventoryType(id), id, -quantity, true, false);
            }
            cl.write(PacketCreator.GetShowItemGain(id, quantity, true));
        }
    }

    public void removeFromParty(int id, List<Player> party) {
        for (Player p : party) {
            Client c = p.getClient();
            InventoryType type = ItemInformationProvider.getInstance().getInventoryType(id);
            Inventory iv = c.getPlayer().getInventory(type);
            int possesed = iv.countById(id);
            if (possesed > 0) {
                InventoryManipulator.removeById(c, ItemInformationProvider.getInstance().getInventoryType(id), id, possesed, true, false);
                c.announce(PacketCreator.GetShowItemGain(id, (short) -possesed, true));
            }
        }
    }

    public void removeAll(int id) {
        removeAll(id, c);
    }

    public void removeAll(int id, Client cl) {
        int possessed = cl.getPlayer().getInventory(ItemInformationProvider.getInstance().getInventoryType(id)).countById(id);
        if (possessed > 0) {
            InventoryManipulator.removeById(cl, ItemInformationProvider.getInstance().getInventoryType(id), id, possessed, true, false);
            cl.announce(PacketCreator.GetShowItemGain(id, (short) -possessed, true));
        }
    }

    public void removePartyItems(int id) {
        if (getParty() == null) {
            removeAll(id);
            return;
        }
        for (MaplePartyCharacter chr : getParty().getMembers()) {
            if (chr != null && chr.isOnline() && chr.getPlayer().getClient() != null) {
                removeAll(id, chr.getPlayer().getClient());
            }
        }
    }

    public int countMonster() {
        Field map = c.getPlayer().getMap();
        double range = Double.POSITIVE_INFINITY;
        List<FieldObject> monsters = map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(FieldObjectType.MONSTER));
        monsters.stream().forEach((monstermo) -> {
            final MapleMonster monster = (MapleMonster) monstermo;
        });
        return monsters.size();
    }

    public int countReactor() {
        Field map = c.getPlayer().getMap();
        double range = Double.POSITIVE_INFINITY;
        List<FieldObject> reactors = map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(FieldObjectType.REACTOR));
        reactors.stream().forEach((reactormo) -> {
            Reactor reactor = (Reactor) reactormo;
        });
        return reactors.size();
    }

    public int countReactor(byte st) {
        Field map = c.getPlayer().getMap();
        double range = Double.POSITIVE_INFINITY;
        List<FieldObject> reactorz = map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(FieldObjectType.REACTOR));
        ArrayList<FieldObject> reactors = new ArrayList<>();
        reactorz.stream().map((reactormo) -> (Reactor) reactormo).filter((reactor) -> (reactor.getState() == st)).forEach((reactor) -> {
            reactors.add(reactor);
        });
        return reactors.size();
    }

    public final void gainCloseness(final int closeness, final int index) {
        final ItemPet pet = getPlayer().getPet(index);
        if (pet != null) {
            pet.setCloseness(pet.getCloseness() + closeness);
            getClient().write(PetPackets.updatePet(pet, true));
        }
    }

    public final void gainClosenessAll(final int closeness) {
        for (final ItemPet pet : getPlayer().getPets()) {
            if (pet != null) {
                pet.setCloseness(pet.getCloseness() + closeness);
                getClient().write(PetPackets.updatePet(pet, true));
            }
        }
    }

    public int getMapId() {
        return getPlayer().getMap().getId();
    }

    public int getPlayerCount(int mapid) {
        return c.getChannelServer().getMapFactory().getMap(mapid).getCharactersThreadsafe().size();
    }

    public int getCurrentPartyId(int mapid) {
        return getMap(mapid).getCurrentPartyId();
    }

    public void showInstruction(String msg, short width, short height) {
        c.write(PacketCreator.SendHint(msg, width, height));
    }

    public final void worldMessage(final int type, final String message) {
        BroadcastService.broadcastMessage(c.getWorld(), PacketCreator.ServerNotice(type, message));
    }

    /**
     * This function autorandomizes equips.
     *
     * @param itemId
     * @param quantity
     */
    public void gainEquip(int itemId, int quantity) {
        if (ItemInformationProvider.getInstance().isEquip(itemId)) {
            Equip eq = (Equip) ItemInformationProvider.getInstance().getEquipById(itemId);
            ItemInformationProvider.getInstance().randomizeStats(eq);
            InventoryManipulator.addFromDrop(this.getClient(), eq, "", false);
            this.getClient().write(PacketCreator.GetShowItemGain(itemId, (short) 1, true));
        } else {
            Item item = new Item(itemId, (byte) -1, (short) quantity);
            InventoryManipulator.addFromDrop(this.getClient(), item, "", false);
            this.getClient().write(PacketCreator.GetShowItemGain(itemId, (short) quantity, true));
        }
    }

    public boolean canGetFirstJob(int jobType) {
        Player p = this.getPlayer();
        switch (jobType) {
            case 1:
                return p.getStr() >= 35;
            case 2:
                return p.getInt() >= 20;
            case 3:
            case 4:
                return p.getDex() >= 25;
            case 5:
                return p.getDex() >= 20;
            default:
                return true;
        }
    }

    public void gainEquip(int itemId) {
        gainEquip(itemId, 1);
    }

    public final void startAriantPQ(int mapid) {
        for (final Player p : getPlayer().getMap().getCharactersThreadsafe()) {

            p.updateAriantScore();
            p.changeMap(mapid);

            getPlayer().getMap().clearDrops();

            p.getClient().write(PacketCreator.GetClockTimer(8 * 60));
            p.dropMessage(5, "");

            MapTimer.getInstance().schedule(p::updateAriantScore, 800);
            MiscTimer.getInstance().schedule(() -> {
                p.getClient().write(PacketCreator.ShowAriantScoreBoard());
                getPlayer().getMap().killAllMonsters();
                MapTimer.getInstance().schedule(() -> {
                    p.changeMap(980010010, 0);
                }, 9000);
            }, (8 * 60) * 1000);
        }
    }

    public int GetPiot(String Name, int Channale) {
        int ret = -1;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM global WHERE channel = ? and Name = ?");
            ps.setInt(1, Channale);
            ps.setString(2, Name);
            ResultSet rs = ps.executeQuery();
            rs.next();
            ret = rs.getInt("Point");
            rs.close();
            ps.close();
        } catch (SQLException ex) {
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
        return ret;
    }

    public void GainPiot(String Name, int Channale, int Piot) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            int ret = GetPiot(Name, Channale);
            if (ret == -1) {
                ret = 0;
                PreparedStatement ps = null;
                try {
                    ps = con.prepareStatement("INSERT INTO global (channel, Name,Point) VALUES (?, ?, ?)");
                    ps.setInt(1, Channale);
                    ps.setString(2, Name);
                    ps.setInt(3, ret);
                    ps.execute();
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("GainPiot1 : " + e);
                } finally {
                    try {
                        if (ps != null) {
                            ps.close();
                        }
                    } catch (SQLException e) {
                        System.out.println("GainPiot2 : " + e);
                    }
                }
            }
            ret += Piot;

            PreparedStatement ps = con.prepareStatement("UPDATE global SET `Point` = ? WHERE Name = ? and channel = ?");
            ps.setInt(1, ret);
            ps.setString(2, Name);
            ps.setInt(3, Channale);
            ps.execute();
            ps.close();
        } catch (SQLException sql) {
            System.err.println("GainPiot3 : " + sql);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
    }

    public List<BossRankInfo> getBossRankPointsTop(String bossname) {
        return BossRankManager.getInstance().getRank(bossname, 1);
    }

    public List<BossRankInfo> getBossRankCountTop(String bossname) {
        return BossRankManager.getInstance().getRank(bossname, 2);
    }

    public List<BossRankInfo> getBossRankTop(String bossname, byte type) {
        return BossRankManager.getInstance().getRank(bossname, type);
    }

    public int setBossRankPoints(String bossname) {
        return setBossRank(getPlayer().getId(), getPlayer().getName(), bossname, (byte) 1, 1);
    }

    public int setBossRankCount(String bossname) {
        return setBossRank(getPlayer().getId(), getPlayer().getName(), bossname, (byte) 2, 1);
    }

    public int setBossRankPoints(String bossname, int add) {
        return setBossRank(getPlayer().getId(), getPlayer().getName(), bossname, (byte) 1, add);
    }

    public int setBossRankCount(String bossname, int add) {
        return setBossRank(getPlayer().getId(), getPlayer().getName(), bossname, (byte) 2, add);
    }

    public int setBossRank(String bossname, byte type, int add) {
        return setBossRank(getPlayer().getId(), getPlayer().getName(), bossname, type, add);
    }

    public int setBossRank(int cid, String cname, String bossname, byte type, int add) {
        return BossRankManager.getInstance().setLog(cid, cname, bossname, type, add);
    }

    public int getBossRankPoints(String bossname) {
        return getBossRank(bossname, (byte) 1);
    }

    public int getBossRankCount(String bossname) {
        return getBossRank(bossname, (byte) 2);
    }

    public int getBossRank(String bossname, byte type) {
        return getBossRank(getPlayer().getId(), bossname, type);
    }

    public int getBossRank(int cid, String bossname, byte type) {
        int ret = -1;
        BossRankInfo info = BossRankManager.getInstance().getInfo(cid, bossname);
        if (null == info) {
            return ret;
        }
        switch (type) {
            case 1:
                ret = info.getPoints();
                break;
            case 2:
                ret = info.getCount();
                break;
        }
        return ret;
    }

    public int setrank0(String bossname) {
        return setBossRank(getPlayer().getId(), "" + getPlayer().getId() + "", bossname, (byte) 2, 1);
    }

    public int setrank0(String bossname, int add) {
        return setBossRank(getPlayer().getId(), "" + getPlayer().getId() + "", bossname, (byte) 2, add);
    }

    public int getrank0(String bossname) {
        return getBossRank(getPlayer().getId(), bossname, (byte) 2);
    }

    public int getrank0(String bossname, byte type) {
        return getBossRank(getPlayer().getId(), bossname, type);
    }

}
