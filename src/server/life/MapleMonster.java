package server.life;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import client.player.Player;
import client.Client;
import client.player.PlayerJob;
import client.player.buffs.BuffStat;
import client.player.skills.PlayerSkillFactory;
import community.MapleParty;
import static configure.Gamemxd.会员等级划分;
import server.life.status.MonsterStatus;
import server.life.status.MonsterStatusEffect;
import packet.transfer.write.OutPacket;
import static configure.Gamemxd.经验效率;
import static configure.Gamemxd.群头衔经验加成;
import java.util.EnumMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import static launch.Start.经验倍率;
import packet.creators.MonsterPackets;
import scripting.event.EventInstanceManager;
import tools.TimerTools.MonsterTimer;
import server.maps.Field;
import server.maps.object.FieldObject;
import server.maps.object.FieldObjectType;
import tools.Pair;

public class MapleMonster extends AbstractLoadedMapleLife {

    private AtomicInteger hp = new AtomicInteger(1);
    private int mp;
    private int dropItemPeriod;
    private int team = -1;
    private int droppedCount = 0;
    private int VenomMultiplier = 0;
    private boolean controllerHasAggro;
    private boolean controllerKnowsAboutAggro;
    private boolean fake = false;
    private boolean random = false;
    private boolean canDamage = true;
    private boolean shouldDrop = true;
    private boolean taunted = false;
    private boolean justSpawned = true;
    private boolean dropsDisabled = false;
    private Field map;
    private Player target = null;
    private Player taunter = null;
    private ChangeableStats ostats = null;
    private Player highestDamageChar;
    private MapleMonsterStats stats;
    private MapleMonsterStats overrideStats;
    private EventInstanceManager eventInstance = null;
    private Collection<MonsterListener> listeners = new LinkedList<>();
    private final Map<MonsterStatus, Integer> monsterBuffs = new HashMap<>(3);
    private List<MonsterStatusEffect> activeEffects = new ArrayList<>();
    private List<Pair<Integer, Integer>> usedSkills = new ArrayList<>();
    private EnumMap<MonsterStatus, MonsterStatusEffect> stati = new EnumMap<>(MonsterStatus.class);
    private Map<Pair<Integer, Integer>, Integer> skillsUsed = new HashMap<>();
    private WeakReference<Player> controller = new WeakReference<>(null);
    private final HashMap<Integer, AtomicInteger> takenDamage = new HashMap<>();
    private final ReentrantReadWriteLock statiLock = new ReentrantReadWriteLock();
    private boolean 军团 = true;

    public void setjt(boolean boss) {
        this.军团 = boss;
    }

    public boolean isjt() {
        return 军团;
    }

    public MapleMonster(final int id, final MapleMonsterStats stats) {
        super(id);
        initWithStats(stats);
    }

    public MapleMonster(final MapleMonster monster) {
        super(monster);
        initWithStats(monster.stats);
    }

    private final void initWithStats(final MapleMonsterStats stats) {
        setStance(5);
        this.stats = stats;
        hp.set(stats.getHp());
        mp = stats.getMp();
    }

    public final boolean hasPublicReward() {
        return stats.isPublicReward();
    }

    public final MapleMonsterStats getStats() {
        return stats;
    }

    public boolean isTaunted() {
        return taunted;
    }

    public void setTaunted(boolean taunted) {
        this.taunted = taunted;
    }

    public Player getTaunter() {
        return taunter;
    }

    public void setTaunter(Player taunter) {
        this.taunter = taunter;
    }

    public final boolean isRandom() {
        return random;
    }

    public final void setRandom(boolean random) {
        this.random = random;
    }

    public final Player getTarget() {
        return target;
    }

    public final void setTarget(final Player target) {
        this.target = target;
    }

    public boolean isExplosive() {
        return stats.isExplosive();
    }

    public final void disableDrops() {
        this.dropsDisabled = true;
    }

    public final boolean dropsDisabled() {
        return dropsDisabled;
    }

    public final void setMap(Field map) {
        this.map = map;
    }

    public final MonsterStatusEffect getBuff(final MonsterStatus status) {
        return stati.get(status);
    }

    public final int getHp() {
        return hp.get();
    }

    public final boolean canDamage() {
        return this.canDamage;
    }

    public final void setCanDamage(final boolean shit) {
        this.canDamage = shit;
    }

    public final int getDropItemPeriod() {
        return dropItemPeriod;
    }

    public final void setDropItemPeriod(final int se) {
        dropItemPeriod = se;
    }

    public final void setHp(final int hp) {
        this.hp.set(hp);
    }

    public void setHpZero() {
        applyAndGetHpDamage(Integer.MAX_VALUE, false);
    }

    public synchronized Integer applyAndGetHpDamage(int delta, boolean stayAlive) {
        int curHp = hp.get();
        if (curHp <= 0) {
            return null;
        }

        if (delta >= 0) {
            if (stayAlive) {
                curHp--;
            }
            int trueDamage = Math.min(curHp, delta);

            hp.addAndGet(-trueDamage);
            return trueDamage;
        } else {
            int trueHeal = -delta;
            int hp2Heal = curHp + trueHeal;
            int maxHp = getMaxHp();

            if (hp2Heal > maxHp) {
                trueHeal -= (hp2Heal - maxHp);
            }

            hp.addAndGet(trueHeal);
            return trueHeal;
        }
    }

    public void gainHp(int hp) {
        this.hp.addAndGet(hp);
    }

    public final boolean getJustSpawned() {
        return justSpawned;
    }

    public final void setJustSpawned(final boolean f) {
        justSpawned = f;
    }

    public final int getMaxHp() {
        if (overrideStats != null) {
            return overrideStats.getHp();
        }
        return stats.getHp();
    }

    public final int getMp() {
        return mp;
    }

    public final void setMp(int mp) {
        if (mp < 0) {
            mp = 0;
        }
        this.mp = mp;
    }

    public final int getMaxMp() {
        if (overrideStats != null) {
            return overrideStats.getMp();
        }
        return stats.getMp();
    }

    public final int getExp() {
        if (overrideStats != null) {
            return overrideStats.getExp();
        }
        return stats.getExp();
    }

    public final int getLevel() {
        return stats.getLevel();
    }

    public final int getRemoveAfter() {
        return stats.getRemoveAfter();
    }

    public final int getVenomMulti() {
        return this.VenomMultiplier;
    }

    public final void setVenomMulti(final int multiplier) {
        this.VenomMultiplier = multiplier;
    }

    public int getAccuracyBase() {
        return stats.getAccuracy();
    }

    public int getAccuracy() {
        return stats.getAccuracy() + getBuffedValue(MonsterStatus.ACC);
    }

    public final boolean isBoss() {
        return stats.isBoss() || getId() == 8810018;
    }

    public final boolean isFfaLoot() {
        return stats.isPublicReward();
    }

    public final int getAnimationTime(final String name) {
        return stats.getAnimationTime(name);
    }

    public final List<Integer> getRevives() {
        return stats.getRevives();
    }

    public final void setOverrideStats(final MapleMonsterStats overrideStats) {
        this.overrideStats = overrideStats;
    }

    public final byte getTagColor() {
        return stats.getTagColor();
    }

    public final byte getTagBgColor() {
        return stats.getTagBgColor();
    }

    public final void setShouldDrop(final boolean t) {
        shouldDrop = t;
    }

    public final boolean getShouldDrop() {
        return shouldDrop;
    }

    public final void setDropped(final int dr) {
        this.droppedCount = dr;
    }

    public final int getDropped() {
        return droppedCount;
    }

    public final boolean getUndead() {
        return stats.getUndead();
    }

    public int getBuffedValue(final MonsterStatus status) {
        final Integer val = monsterBuffs.get(status);
        return val == null ? 0 : val;
    }

    /**
     *
     * @param from the player that dealt the damage击杀怪物
     * @param damage
     */
    public synchronized void damage(Player from, int damage) {
        if (from == null || damage <= 0 || !isAlive()) {
            return;
        }
        int trueDamage = Math.min(hp.get(), damage);
        this.hp.addAndGet(-trueDamage);

        /*if (GameConstants.USE_DEBUG) {
            from.dropMessage(5, "Hit MOBID: " + this.getId() + ", OID: " + this.getObjectId());
        }*/
        dispatchMonsterDamaged(from, trueDamage);

        if (takenDamage.containsKey(from.getId())) {
            takenDamage.get(from.getId()).addAndGet(trueDamage);
        } else {
            takenDamage.put(from.getId(), new AtomicInteger(trueDamage));
        }
    }

    public final void heal(final int hp, final int mp) {
        final int TotalHP = getHp() + hp;
        final int TotalMP = getMp() + mp;

        if (TotalHP >= getMaxHp()) {
            setHp(getMaxHp());
        } else {
            setHp(TotalHP);
        }
        if (TotalMP >= getMp()) {
            setMp(getMp());
        } else {
            setMp(TotalMP);
        }
        map.broadcastMessage(MonsterPackets.HealMonster(getObjectId(), hp));
        dispatchMonsterHealed(hp);
    }

    private int getHighestDamagerId() {
        int curId = 0;
        int curDmg = 0;

        for (Entry<Integer, AtomicInteger> damage : takenDamage.entrySet()) {
            curId = damage.getValue().get() >= curDmg ? damage.getKey() : curId;
            curDmg = damage.getKey() == curId ? damage.getValue().get() : curDmg;
        }

        return curId;
    }

    public boolean isAttackedBy(Player p) {
        return takenDamage.containsKey(p.getId());
    }

    /** <队伍给经验>
     */
    private void distributeExperienceToParty(int pid, int exp, int killer, Map<Integer, Integer> expDist) {
        LinkedList<Player> members = new LinkedList<>();

        map.getCharactersThreadsafe().stream().filter((mc) -> (mc.getPartyId() == pid)).forEach((mc) -> {
            members.add(mc);
        });

        final int minLevel = getLevel() - 5;

        int partyLevel = 0;
        int leechMinLevel = 0;

        //队伍玩家等级
        for (Player mc : members) {
            if (mc.getLevel() >= minLevel) {
                leechMinLevel = Math.min(mc.getLevel() - 5, minLevel);
            }
        }

        int leechCount = 0;
        //队伍
        for (Player mc : members) {
            if (mc.getLevel() >= leechMinLevel) {
                partyLevel += mc.getLevel();
                leechCount++;
            }
        }

        final int mostDamageCid = getHighestDamagerId();

        for (Player mc : members) {
            //怪物ID
            int id = mc.getId();
            //角色等级
            int level = mc.getLevel();
            if (expDist.containsKey(id) || level >= leechMinLevel) {
                boolean isKiller = killer == id;
                boolean mostDamage = mostDamageCid == id;
                //int xp = (int) (exp * 0.80f * level / partyLevel);
                int xp = (int) (exp * 0.60f);
                if (mostDamage) {
                    //xp += (exp * 0.20f);
                    xp += (exp * 0.40f);
                }
                giveExpToCharacter(mc, xp, isKiller, leechCount);
            }
        }
    }

    public void distributeExperience(int killerId) {
        if (isAlive()) {
            return;
        }
        int exp = getExp();
        int totalHealth = getMaxHp();
        Map<Integer, Integer> expDist = new HashMap<>();
        Map<Integer, Integer> partyExp = new HashMap<>();
        for (Entry<Integer, AtomicInteger> damage : takenDamage.entrySet()) {
            expDist.put(damage.getKey(), (int) (0.80f * exp * damage.getValue().get() / totalHealth));
        }
        map.getCharactersThreadsafe().stream().filter((mc) -> (expDist.containsKey(mc.getId()))).forEach((mc) -> {
            boolean isKiller = mc.getId() == killerId;
            int xp = expDist.get(mc.getId());
            if (isKiller) {
                xp += exp / 5;
            }
            MapleParty p = mc.getParty();
            if (p != null) {
                //队伍ID
                int pID = p.getId();
                int pXP = xp + (partyExp.containsKey(pID) ? partyExp.get(pID) : 0);
                partyExp.put(pID, pXP);
            } else {
                giveExpToCharacter(mc, xp, isKiller, 1);
            }
        });
        partyExp.entrySet().forEach((party) -> {
            distributeExperienceToParty(party.getKey(), party.getValue(), killerId, expDist);
        });
    }

    /**
     * <击杀怪物给经验>
     */
    public void giveExpToCharacter(Player attacker, int exp, boolean isKiller, int numExpSharers) {
        if (isKiller) {
            if (getMap().getEventInstance() != null) {
                getMap().getEventInstance().monsterKilled(attacker, this);
            }
        }

        //final int partyModifier = numExpSharers > 1 ? ((5 * (numExpSharers - 2))) : 0;
        int partyExp = 0;

        if (attacker.getStat().getHp() > 0) {

            int personalExp = 0;

            if (exp > 0) {
                /*if (partyModifier > 0) {
                 partyExp = (int) (personalExp * partyModifier * GameConstants.PARTY_BONUS_EXP_RATE);
                 }*/
                //神圣祈祷
                personalExp = exp;
                Integer holySymbol = attacker.getBuffedValue(BuffStat.HolySymbol);
                if (holySymbol != null) {
                    if (numExpSharers == 1) {
                        personalExp *= 1.0 + (holySymbol.doubleValue() / 500.0);
                    } else {
                        personalExp *= 1.0 + (holySymbol.doubleValue() / 100.0);
                    }
                }
            }
            /*if (exp < 0) {
                personalExp = Integer.MAX_VALUE;
            }*/
            //基础怪物经验 = exp
            int 经验 = 0;
            经验 += exp * 经验倍率(attacker.getWorldId());
            //双倍经验卡
            if (attacker.经验倍率 > 1) {
                经验 += exp;
            }


            //群头衔
            if (attacker.getTX() != null) {
                经验 += exp * (群头衔经验加成(attacker.getTX()) * 0.05);
            }
            //狩猎给经验
            if (attacker.获取附魔汇总值(4) > 0) {
                if (exp >= 100) {
                    经验 += exp * attacker.获取附魔汇总值(4) / 100;
                } else {
                    经验 += 1;
                }
            }
            //会员给经验
            if (attacker.getvip() > 0) {
                经验 += exp * (0.1 + ((会员等级划分(attacker.getvip()) - 1) * 0.02));
            }
            经验 += personalExp;
            //经验效率
            经验 = (int) (经验 * 经验效率(attacker.getLevel(), getLevel()));
            if (经验 == 0) {
                经验 = 1;
            }
            //给经验
            attacker.gainExp(经验, partyExp, true, false, isKiller);
            attacker.incrementMonsterKills();
        }
    }

    public final Player killBy(final Player killer) {

        distributeExperience(killer != null ? killer.getId() : 0);

        Player controller = getController();
        if (controller != null) {
            controller.getClient().announce(MonsterPackets.StopControllingMonster(this.getObjectId()));
            controller.uncontrolMonster(this);
        }

        final List<Integer> toSpawn = this.getRevives();
        if (toSpawn != null) {
            @SuppressWarnings("null")
            final Field reviveMap = killer.getMap();
            MonsterTimer.getInstance().schedule(() -> {
                toSpawn.stream().map((mid) -> MapleLifeFactory.getMonster(mid)).map((mob) -> {
                    mob.setPosition(getPosition());
                    return mob;
                }).map((mob) -> {
                    if (dropsDisabled()) {
                        mob.disableDrops();
                    }
                    return mob;
                }).forEach((mob) -> {
                    reviveMap.spawnRevives(mob);
                });
            }, this.getAnimationTime("die1"));
        }

        Player looter = map.getCharacterById(getHighestDamagerId());
        /*if (team > -1) {
            final int cp = getCP();
            if (looter == null) {
                return killer;
            }

            if (looter.getMCPQParty() == null) {
                MCTracker.log("尝试在未指定MCPQ参与方的情况下将CP赋予字符。");
                return killer;
            }

            looter.getMCPQField().monsterKilled(looter, cp);

        }*/
        return looter != null ? looter : killer;
    }

    public final boolean isAlive() {
        return this.hp.get() > 0;
    }

    public Player getController() {
        return controller.get();
    }

    public void setController(Player controller) {
        this.controller = new WeakReference<>(controller);
    }

    public final void switchController(final Player newController, final boolean immediateAggro) {
        final Player controllers = getController();
        if (controllers == newController) {
            return;
        } else if (controllers != null) {
            controllers.uncontrolMonster(this);
            controllers.getClient().write(MonsterPackets.StopControllingMonster(getObjectId()));
        }
        newController.controlMonster(this, immediateAggro);
        setController(newController);
        if (immediateAggro) {
            setControllerHasAggro(true);
        }
        setControllerKnowsAboutAggro(false);
    }

    public final void addListener(final MonsterListener listener) {
        listeners.add(listener);
    }

    public final void removeListener(final MonsterListener listener) {
        listeners.remove(listener);
    }

    public final boolean controllerHasAggro() {
        return fake ? false : controllerHasAggro;
    }

    public final void setControllerHasAggro(final boolean controllerHasAggro) {
        if (fake) {
            return;
        }
        this.controllerHasAggro = controllerHasAggro;
    }

    public final boolean isControllerKnowsAboutAggro() {
        return fake ? false : controllerKnowsAboutAggro;
    }

    public final void setControllerKnowsAboutAggro(final boolean controllerKnowsAboutAggro) {
        if (fake) {
            return;
        }
        this.controllerKnowsAboutAggro = controllerKnowsAboutAggro;
    }

    public final OutPacket makeBossHPBarPacket() {
        return MonsterPackets.ShowBossHP(getId(), getHp(), getMaxHp(), getTagColor(), getTagBgColor());
    }

    public final boolean hasBossHPBar() {
        return (isBoss() && getTagColor() > 0) || isHT();
    }

    public final boolean isHT() {
        return this.getId() == 8810018;
    }

    @Override
    public final void sendSpawnData(final Client c) {
        if (!isAlive()) {
            return;
        }
        if (isFake()) {
            c.announce(MonsterPackets.SpawnFakeMonster(this, 0));
        } else {
            c.announce(MonsterPackets.SpawnMonster(this, false));
        }
        statiLock.readLock().lock();
        try {
            if (stati.size() > 0) {
                for (final MonsterStatusEffect mse : this.stati.values()) {
                    c.announce(MonsterPackets.ApplyMonsterStatus(getObjectId(), mse, null));
                }
            }
        } finally {
            statiLock.readLock().unlock();
        }
        if (hasBossHPBar()) {
            c.write(makeBossHPBarPacket());
        }
    }

    @Override
    public final void sendDestroyData(final Client client) {
        client.write(MonsterPackets.KillMonster(getObjectId(), false));
    }

    @Override
    /*public final String toString() {
        return getName() + "(" + getId() + ") at " + getPosition().x + "/" + getPosition().y + " with " + getHp() + "/" + getMaxHp() + "hp, " + getMp() + "/" + getMaxMp() + " mp (alive: " + isAlive() + " oid: " + getObjectId() + ")";
    }*/

    public final String toString() {
        String a = "";
        if (getId() == 9400711) {
            a = "\r\n\t\t   [" + getPosition().x + " / " + getPosition().y + ")";
        }
        return a;
    }

    @Override
    public final FieldObjectType getType() {
        return FieldObjectType.MONSTER;
    }

    public final EventInstanceManager getEventInstance() {
        return eventInstance;
    }

    public final void setEventInstance(final EventInstanceManager eventInstance) {
        this.eventInstance = eventInstance;
    }

    public final boolean isMobile() {
        return stats.isMobile();
    }

    public final ElementalEffectiveness getEffectiveness(final Element e) {
        statiLock.readLock().lock();
        try {
            if (activeEffects.size() > 0 && stati.get(MonsterStatus.DOOM) != null) {
                return ElementalEffectiveness.NORMAL;
            }
        } finally {
            statiLock.readLock().unlock();
        }
        return stats.getEffectiveness(e);
    }

    public final boolean applyStatus(final Player from, final MonsterStatusEffect status, final boolean poison, final long duration) {
        return applyStatus(from, status, poison, duration, false);
    }

    public final boolean applyStatus(final Player from, final MonsterStatusEffect status, final boolean poison, final long duration, final boolean venom) {
        switch (stats.getEffectiveness(status.getSkill().getElement())) {
            case IMMUNE:
            case STRONG:
                return false;
            case NORMAL:
            case WEAK:
                break;
            default:
                throw new RuntimeException("Unknown elemental effectiveness: " + stats.getEffectiveness(status.getSkill().getElement()));
        }
        ElementalEffectiveness effectiveness = null;
        switch (status.getSkill().getId()) {
            case 2111006:
                effectiveness = stats.getEffectiveness(Element.POISON);
                if (effectiveness == ElementalEffectiveness.IMMUNE || effectiveness == ElementalEffectiveness.STRONG) {
                    return false;
                }
                break;
            case 2211006:
                effectiveness = stats.getEffectiveness(Element.ICE);
                if (effectiveness == ElementalEffectiveness.IMMUNE || effectiveness == ElementalEffectiveness.STRONG) {
                    return false;
                }
                break;
            case 4120005:
            case 4220005:
                effectiveness = stats.getEffectiveness(Element.POISON);
                if (effectiveness == ElementalEffectiveness.WEAK) {
                    return false;
                }
                break;
        }
        if (poison && getHp() <= 1) {
            return false;
        }
        if (isBoss() && !(status.getStati().containsKey(MonsterStatus.SPEED))) {
            return false;
        }
        for (MonsterStatus stat : status.getStati().keySet()) {
            MonsterStatusEffect oldEffect = stati.get(stat);
            if (oldEffect != null) {
                oldEffect.removeActiveStatus(stat);
                if (oldEffect.getStati().isEmpty()) {
                    oldEffect.getCancelTask().cancel(false);
                    oldEffect.cancelPoisonSchedule();
                    activeEffects.remove(oldEffect);
                }
            }
        }
        final MonsterTimer timerManager = MonsterTimer.getInstance();
        final Runnable cancelTask = () -> {
            if (isAlive()) {
                OutPacket packet = MonsterPackets.CancelMonsterStatus(getObjectId(), status.getStati());
                map.broadcastMessage(packet);

                Player controller = getController();
                if (controller != null && !controller.isMapObjectVisible(MapleMonster.this)) {
                    controller.getClient().announce(packet);
                }
            }
            activeEffects.remove(status);
            for (final MonsterStatus stat : status.getStati().keySet()) {
                statiLock.writeLock().lock();
                try {
                    stati.remove(stat);
                } finally {
                    statiLock.writeLock().unlock();
                }
            }
            setVenomMulti(0);
            status.cancelPoisonSchedule();
        };
        if (poison) {
            final int poisonLevel = from.getSkillLevel(status.getSkill());
            final int poisonDamage = Math.min(Short.MAX_VALUE, (int) (getMaxHp() / (70.0 - poisonLevel) + 0.999));
            status.setValue(MonsterStatus.POISON, poisonDamage);
            status.setPoisonSchedule(timerManager.register(new PoisonTask(poisonDamage, from, status, cancelTask, false), 1000, 1000));
        } else if (venom) {
            if (from.getJob() == PlayerJob.NIGHTLORD || from.getJob() == PlayerJob.SHADOWER) {
                int poisonLevel = 0;
                int matk = 0;
                if (null == from.getJob()) {
                    return false;
                } else {
                    switch (from.getJob()) {
                        case NIGHTLORD:
                            poisonLevel = from.getSkillLevel(PlayerSkillFactory.getSkill(4120005));
                            if (poisonLevel <= 0) {
                                return false;
                            }
                            matk = PlayerSkillFactory.getSkill(4120005).getEffect(poisonLevel).getMatk();
                            break;
                        case SHADOWER:
                            poisonLevel = from.getSkillLevel(PlayerSkillFactory.getSkill(4220005));
                            if (poisonLevel <= 0) {
                                return false;
                            }
                            matk = PlayerSkillFactory.getSkill(4220005).getEffect(poisonLevel).getMatk();
                            break;
                        default:
                            return false;
                    }
                }
                Random r = new Random();
                final int luk = from.getStat().getLuk();
                final int maxDmg = (int) Math.ceil(Math.min(Short.MAX_VALUE, 0.2 * luk * matk));
                final int minDmg = (int) Math.ceil(Math.min(Short.MAX_VALUE, 0.1 * luk * matk));
                int gap = maxDmg - minDmg;
                if (gap == 0) {
                    gap = 1;
                }
                int poisonDamage = 0;
                for (int i = 0; i < getVenomMulti(); i++) {
                    poisonDamage = poisonDamage + (r.nextInt(gap) + minDmg);
                }
                poisonDamage = Math.min(Short.MAX_VALUE, poisonDamage);
                status.setValue(MonsterStatus.POISON, poisonDamage);
                status.setPoisonSchedule(timerManager.register(new PoisonTask(poisonDamage, from, status, cancelTask, false), 1000, 1000));
            } else {
                return false;
            }
        } else if (status.getSkill().getId() == 4111003) {
            int webDamage = (int) (getMaxHp() / 50.0 + 0.999);
            status.setPoisonSchedule(timerManager.schedule(new PoisonTask(webDamage, from, status, cancelTask, true), 3500));
        }
        for (final MonsterStatus stat : status.getStati().keySet()) {
            statiLock.writeLock().lock();
            try {
                stati.put(stat, status);
            } finally {
                statiLock.writeLock().unlock();
            }
        }
        activeEffects.add(status);
        int animationTime = status.getSkill().getAnimationTime();
        OutPacket packet = MonsterPackets.ApplyMonsterStatus(getObjectId(), status.getStati(), status.getSkill().getId(), false, 0);
        map.broadcastMessage(packet);

        Player controller = getController();
        if (controller != null && !controller.isMapObjectVisible(this)) {
            controller.getClient().announce(packet);
        }
        ScheduledFuture<?> schedule = timerManager.schedule(cancelTask, duration + animationTime);
        status.setCancelTask(schedule);
        return true;
    }

    public final void applyMonsterBuff(final MonsterStatus status, final int x, final int skillId, final long duration, final MobSkill skill) {
        MonsterTimer timerManager = MonsterTimer.getInstance();
        final Runnable cancelTask = () -> {
            if (isAlive()) {
                OutPacket packet = MonsterPackets.CancelMonsterStatus(getObjectId(), Collections.singletonMap(status, Integer.valueOf(x)));
                map.broadcastMessage(packet);

                Player controller = getController();
                if (controller != null && !controller.isMapObjectVisible(MapleMonster.this)) {
                    controller.getClient().announce(packet);
                }
                removeMonsterBuff(status);
            }
        };
        OutPacket packet = MonsterPackets.ApplyMonsterStatus(getObjectId(), Collections.singletonMap(status, x), skillId, true, 0, skill);
        map.broadcastMessage(packet);
        Player controller = getController();
        if (controller != null && !controller.isMapObjectVisible(this)) {
            controller.getClient().announce(packet);
        }
        timerManager.schedule(cancelTask, duration);
        addMonsterBuff(status, x);
    }

    public void addMonsterBuff(final MonsterStatus status, final int x) {
        monsterBuffs.put(status, x);
    }

    public final void removeMonsterBuff(final MonsterStatus status) {
        this.monsterBuffs.remove(status);
    }

    public final void cancelMonsterBuff(final MonsterStatus status) {
        if (isAlive()) {
            OutPacket packet = MonsterPackets.CancelMonsterStatus(getObjectId(), Collections.singletonMap(status, Integer.valueOf(1)));
            map.broadcastMessage(packet);

            Player controller = getController();
            if (controller != null && !controller.isMapObjectVisible(MapleMonster.this)) {
                controller.getClient().announce(packet);
            }
            removeMonsterBuff(status);
        }
    }

    public void dispel() {
        if (!isAlive()) {
            return;
        }
        for (MonsterStatus i : MonsterStatus.values()) {
            if (monsterBuffs.containsKey(i)) {
                removeMonsterBuff(i);
                OutPacket packet = MonsterPackets.CancelMonsterStatus(getObjectId(), Collections.singletonMap(i, Integer.valueOf(1)));
                map.broadcastMessage(packet);
                if (getController() != null && !getController().isMapObjectVisible(MapleMonster.this)) {
                    getController().getClient().write(packet);
                }
            }
        }
    }

    public final boolean isBuffed(final MonsterStatus status) {
        return this.monsterBuffs.containsKey(status);
    }

    public final void setFake(final boolean fake) {
        this.fake = fake;
    }

    public final boolean isFake() {
        return fake;
    }

    public final Field getMap() {
        return map;
    }

    public final List<Pair<Integer, Integer>> getSkills() {
        return this.stats.getSkills();
    }

    public final boolean hasSkill(final int skillId, final int level) {
        return stats.hasSkill(skillId, level);
    }

    public final boolean canUseSkill(final MobSkill toUse) {
        if (toUse == null) {
            return false;
        }
        for (Pair<Integer, Integer> skill : usedSkills) {
            if (skill.getLeft() == toUse.getSkillId() && skill.getRight() == toUse.getSkillLevel()) {
                return false;
            }
        }
        if (toUse.getLimit() > 0) {
            if (this.skillsUsed.containsKey(new Pair<>(toUse.getSkillId(), toUse.getSkillLevel()))) {
                int times = this.skillsUsed.get(new Pair<>(toUse.getSkillId(), toUse.getSkillLevel()));
                if (times >= toUse.getLimit()) {
                    return false;
                }
            }
        }
        if (toUse.getSkillId() == 200) {
            List<MapleMonster> mmo = getMap().getMonsters();
            int i = 0;
            for (FieldObject mo : mmo) {
                if (mo.getType() == FieldObjectType.MONSTER) {
                    i++;
                }
            }
            if (i > 100) {
                return false;
            }
        }
        return true;
    }

    public final void usedSkill(final int skillId, final int level, final long cooltime) {
        this.usedSkills.add(new Pair<>(skillId, level));

        if (this.skillsUsed.containsKey(new Pair<>(skillId, level))) {
            int times = this.skillsUsed.get(new Pair<>(skillId, level)) + 1;
            this.skillsUsed.remove(new Pair<>(skillId, level));
            this.skillsUsed.put(new Pair<>(skillId, level), times);
        } else {
            this.skillsUsed.put(new Pair<>(skillId, level), 1);
        }

        final MapleMonster mons = this;
        final MonsterTimer tMan = MonsterTimer.getInstance();
        tMan.schedule(() -> {
            mons.clearSkill(skillId, level);
        }, cooltime);
    }

    public final void clearSkill(final int skillId, final int level) {
        int index = -1;
        for (Pair<Integer, Integer> skill : usedSkills) {
            if (skill.getLeft() == skillId && skill.getRight() == level) {
                index = usedSkills.indexOf(skill);
                break;
            }
        }
        if (index != -1) {
            usedSkills.remove(index);
        }
    }

    public final int getNoSkills() {
        return this.stats.getNoSkills();
    }

    public final boolean isFirstAttack() {
        return this.stats.isFirstAttack();
    }

    public final int getBuffToGive() {
        return this.stats.getBuffToGive();
    }

    public int getCP() {
        return stats.getCp();
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public final int getPADamage() {
        return stats.getPADamage();
    }

    public final int getDropPeriodTime() {
        return stats.getDropPeriod();
    }

    public void debuffMob(int skillid) {
        final MonsterStatus[] stat = {MonsterStatus.WEAPON_ATTACK_UP, MonsterStatus.WEAPON_DEFENSE_UP, MonsterStatus.MAGIC_ATTACK_UP, MonsterStatus.MAGIC_DEFENSE_UP};
        for (int i = 0; i < stat.length; i++) {
            if (isBuffed(stat[i])) {
                statiLock.writeLock().lock();
                try {
                    final MonsterStatusEffect oldEffect = stati.get(stat[i]);
                    OutPacket packet = MonsterPackets.CancelMonsterStatus(getObjectId(), oldEffect.getStati());
                    map.broadcastMessage(packet);

                    Player controller = getController();
                    if (controller != null && !controller.isMapObjectVisible(MapleMonster.this)) {
                        controller.getClient().announce(packet);
                    }
                    stati.remove(stat[i]);
                } finally {
                    statiLock.writeLock().unlock();
                }
            }
        }
    }

    public Map<MonsterStatus, MonsterStatusEffect> getStati() {
        statiLock.readLock().lock();
        try {
            return Collections.unmodifiableMap(stati);
        } finally {
            statiLock.readLock().unlock();
        }
    }

    public MonsterStatusEffect getStati(MonsterStatus ms) {
        statiLock.readLock().lock();
        try {
            return stati.get(ms);
        } finally {
            statiLock.readLock().unlock();
        }
    }

    public final void cancelStatus(final MonsterStatus status) {
        final MonsterStatusEffect mse = getStati(status);
        if (mse == null || !isAlive()) {
            return;
        }
        mse.cancelPoisonSchedule();
        OutPacket packet = MonsterPackets.CancelMonsterStatus(getObjectId(), Collections.singletonMap(status, 1));
        map.broadcastMessage(packet);
        if (getController() != null && !getController().isMapObjectVisible(MapleMonster.this)) {
            getController().getClient().write(packet);
        }
        statiLock.writeLock().lock();
        try {
            stati.remove(status);
        } finally {
            statiLock.writeLock().unlock();
        }
        setVenomMulti((byte) 0);
        removeMonsterBuff(status);
    }

    private void dispatchUpdateQuestMobCount() {
        Set<Integer> attackerChrids = takenDamage.keySet();
        if (!attackerChrids.isEmpty()) {
            Map<Integer, Player> mapChars = map.getMapPlayers();
            if (!mapChars.isEmpty()) {
                int mobid = getId();

                for (Integer chrid : attackerChrids) {
                    Player chr = mapChars.get(chrid);

                    if (chr != null) {
                        chr.updateQuestMobCount(mobid);
                    }
                }
            }
        }
    }

    public void dispatchMonsterKilled(boolean hasKiller) {
        if (!hasKiller) {
            dispatchUpdateQuestMobCount();
        }

        if (getMap().getEventInstance() != null) {
            if (!this.getStats().isFriendly()) {
                getMap().getEventInstance().monsterKilled(this, hasKiller);
            } else {
                getMap().getEventInstance().friendlyKilled(this, hasKiller);
            }
        }

        for (MonsterListener listener : listeners.toArray(new MonsterListener[listeners.size()])) {
            listener.monsterKilled(getAnimationTime("die1"));
        }
    }

    private void dispatchMonsterDamaged(Player from, int trueDmg) {
        for (MonsterListener listener : listeners.toArray(new MonsterListener[listeners.size()])) {
            listener.monsterDamaged(from, trueDmg);
        }
    }

    private void dispatchMonsterHealed(int trueHeal) {
        for (MonsterListener listener : listeners.toArray(new MonsterListener[listeners.size()])) {
            listener.monsterHealed(trueHeal);
        }
    }

    private final class PoisonTask implements Runnable {

        private final int poisonDamage;
        private final boolean shadowWeb;
        private final Player chr;
        private final MonsterStatusEffect status;
        private final Runnable cancelTask;
        private final Field map;

        private PoisonTask(final int poisonDamage, final Player chr, final MonsterStatusEffect status, final Runnable cancelTask, final boolean shadowWeb) {
            this.poisonDamage = poisonDamage;
            this.chr = chr;
            this.status = status;
            this.cancelTask = cancelTask;
            this.shadowWeb = shadowWeb;
            this.map = chr.getMap();
        }

        @Override
        public void run() {
            int damage = poisonDamage;
            if (damage >= hp.get()) {
                damage = hp.get() - 1;
                if (!shadowWeb) {
                    cancelTask.run();
                    status.getCancelTask().cancel(false);
                }
            }
            if (hp.get() > 1 && damage > 0) {
                damage(chr, damage);
                if (shadowWeb) {
                    map.broadcastMessage(MonsterPackets.DamageMonster(getObjectId(), damage));
                }
            }
        }
    }

    public String getName() {
        return stats.getName();
    }

    // ---- one can always have fun trying these pieces of codes below in-game rofl ----
    public final ChangeableStats getChangedStats() {
        return ostats;
    }

    public final int getMobMaxHp() {
        if (ostats != null) {
            return ostats.hp;
        }
        return stats.getHp();
    }

    public final void setOverrideStats(final OverrideMonsterStats ostats) {
        this.ostats = new ChangeableStats(stats, ostats);
        this.hp.set(ostats.getHp());
        this.mp = ostats.getMp();
    }

    public final void changeLevel(final int newLevel) {
        changeLevel(newLevel, true);
    }

    public final void changeLevel(final int newLevel, boolean pqMob) {
        if (!stats.isChangeable()) {
            return;
        }
        this.ostats = new ChangeableStats(stats, newLevel, pqMob);
        this.hp.set(ostats.getHp());
        this.mp = ostats.getMp();
    }

    private float getDifficultyRate(final int difficulty) {
        switch (difficulty) {
            case 6:
                return (7.7f);
            case 5:
                return (5.6f);
            case 4:
                return (3.2f);
            case 3:
                return (2.1f);
            case 2:
                return (1.4f);
        }

        return (1.0f);
    }

    private void changeLevelByDifficulty(final int difficulty, boolean pqMob) {
        changeLevel((int) (this.getLevel() * getDifficultyRate(difficulty)), pqMob);
    }

    public final void changeDifficulty(final int difficulty, boolean pqMob) {
        changeLevelByDifficulty(difficulty, pqMob);
    }
}
