package client.player;

import client.Client;
import client.player.Player.FameStatus;
import client.player.buffs.BuffStat;
import static client.player.buffs.BuffStat.*;
import client.player.buffs.BuffStatValueHolder;
import client.player.buffs.Disease;
import client.player.buffs.DiseaseValueHolder;
import client.player.inventory.Equip;
import client.player.inventory.Inventory;
import client.player.inventory.InventoryIdentifier;
import client.player.inventory.types.InventoryType;
import client.player.inventory.Item;
import client.player.inventory.ItemFactory;
import client.player.inventory.ItemPet;
import client.player.inventory.ItemRing;
import client.player.inventory.TamingMob;
import client.player.skills.PlayerSkill;
import client.player.skills.PlayerSkillEntry;
import client.player.skills.PlayerSkillFactory;
import client.player.skills.PlayerSkillMacro;
import security.violation.CheatTracker;
import community.MapleBuddyList;
import constants.ExperienceConstants;
import server.quest.MapleQuestStatus;
import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import community.MapleParty;
import constants.*;
import database.Database;
import database.DatabaseException;
import java.util.Date;
import packet.transfer.write.OutPacket;
import handling.channel.ChannelServer;
import handling.world.messenger.MapleMessenger;
import handling.world.messenger.MapleMessengerCharacter;
import community.MaplePartyCharacter;
import community.MaplePartyOperation;
import handling.world.PlayerBuffValueHolder;
import handling.world.PlayerCoolDownValueHolder;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import scripting.event.EventInstanceManager;
import server.itens.ItemInformationProvider;
import server.shops.Shop;
import server.MapleStatEffect;
import server.itens.StorageKeeper;
import server.itens.Trade;
import server.life.MapleMonster;
import server.maps.object.AbstractAnimatedFieldObject;
import server.maps.MapleDoor;
import server.maps.Field;
import server.maps.object.FieldObject;
import server.maps.object.FieldObjectType;
import server.maps.MapleSummon;
import server.quest.MapleQuest;
import tools.Pair;
import server.itens.InventoryManipulator;
import server.minirooms.PlayerShop;
import server.life.MobSkill;
import cashshop.CashShop;
import client.ClientLoginState;
import client.player.inventory.types.ItemRingType;
import static configure.Gamemxd.一转技能附魔;
import static configure.Gamemxd.二转技能附魔20;
import static configure.Gamemxd.二转技能附魔30;
import static configure.Gamemxd.合伙群;
import static configure.Gamemxd.附魔技能;
import constants.SkillConstants;
import handling.world.PlayerBuffStorage;
import handling.world.service.BroadcastService;
import handling.world.service.MessengerService;
import handling.world.service.PartyService;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import packet.creators.EffectPackets;
import packet.creators.MonsterPackets;
import packet.creators.PacketCreator;
import packet.creators.PartyPackets;
import packet.creators.PetPackets;
import constants.SkillConstants.Beginner;
import constants.SkillConstants.Bishop;
import constants.SkillConstants.Brawler;
import constants.SkillConstants.Buccaneer;
import constants.SkillConstants.Corsair;
import constants.SkillConstants.Crusader;
import constants.SkillConstants.DarkKnight;
import constants.SkillConstants.FPArchMage;
import constants.SkillConstants.Hermit;
import constants.SkillConstants.Hero;
import constants.SkillConstants.ILArchMage;
import constants.SkillConstants.Magician;
import constants.SkillConstants.Marauder;
import constants.SkillConstants.Priest;
import constants.SkillConstants.Ranger;
import constants.SkillConstants.Sniper;
import constants.SkillConstants.Swordman;
import static gui.MySQL.取绑定手机;
import static console.MsgServer.QQMsgServer.sendMsgToQQGroup;
import static gui.logo.group.群;
import static handling.channel.handler.DamageParse.技能名字;
import handling.world.World;
import java.util.Comparator;
import java.util.HashMap;
import launch.Start;
import static launch.Start.大区;
import static launch.Start.大区群号1;
import static launch.Start.杀怪任务计数;
import org.apache.log4j.LogManager;
import packet.creators.MinigamePackets;
import server.shops.ShopFactory;
import tools.TimerTools.CharacterTimer;
import server.minirooms.Minigame;
import server.minirooms.PlayerShopItem;
import server.life.MapleLifeFactory;
import server.maps.FieldLimit;
import server.maps.MapleFoothold;
import server.maps.SavedLocationType;
import server.maps.portal.Portal;
import tools.ConcurrentEnumMap;
import tools.FileLogger;
import server.partyquest.mcpq.MCField;
import server.partyquest.mcpq.MCField.MCTeam;
import server.partyquest.mcpq.MCParty;
import server.partyquest.mcpq.MonsterCarnival;
import tools.Randomizer;
import tools.TimerTools.EventTimer;
import static zevms.Time.VeDate.secondToTime;

public class Player extends AbstractAnimatedFieldObject implements Serializable {

    /**
     * @return the maxlevel
     */
    public int getMaxlevel() {
        return maxlevel;
    }

    /**
     * @param maxlevel the maxlevel to set
     */
    public void setMaxlevel(int maxlevel) {
        this.maxlevel = maxlevel;
    }

    public int getRecoveryHP() {
        return RecoveryHP;
    }

    public void setRecoveryHP(int RecoveryHP) {
        this.RecoveryHP = RecoveryHP;
    }

    public int getRecoveryMP() {
        return RecoveryMP;
    }

    public void setRecoveryMP(int RecoveryMP) {
        this.RecoveryMP = RecoveryMP;
    }

    public int getRecoveryHPitemid() {
        return RecoveryHPitemid;
    }

    public void setRecoveryHPitemid(int RecoveryHP) {
        this.RecoveryHPitemid = RecoveryHP;
    }

    public int getRecoveryMPitemid() {
        return RecoveryMPitemid;
    }

    public void setRecoveryMPitemid(int RecoveryMP) {
        this.RecoveryMPitemid = RecoveryMP;
    }
    private static final org.apache.log4j.Logger log = LogManager.getRootLogger();
    protected int id;
    protected int world;
    protected int world2;
    protected int accountid;
    protected int jiazu;
    protected int level;
    private int maxlevel;
    protected int worldRanking;
    protected int worldRankingChange;
    protected int jobRanking;
    protected int jobRankingChange;
    protected int hair;
    protected int eyes;
    protected int gender;
    protected int pop;
    protected int gm;
    private int 泡点金币;
    private int 泡点点券;
    private int 泡点经验;
    protected String name;
    protected int savedLocations[];
    protected final AtomicInteger exp = new AtomicInteger();
    protected final AtomicInteger meso = new AtomicInteger();
    protected int savedSpawnPoint;
    protected PlayerSkin skin = PlayerSkin.NORMAL;
    protected PlayerJob job = PlayerJob.BEGINNER;
    private Client client;
    protected final PlayerStatsManager stats;
    protected Map<Short, MapleQuestStatus> quests;
    private transient EventInstanceManager eventInstance;
    private transient Map<BuffStat, BuffStatValueHolder> effects = new HashMap<>();
    private ReentrantReadWriteLock effectLock = new ReentrantReadWriteLock();
    protected Map<Integer, PlayerKeyBinding> keymap = new LinkedHashMap<>();
    // Field
    protected int mapId, doorSlot = -1;
    private boolean canDoor = true;
    public Field field;
    private Set<MapleMonster> controlled;
    private ReentrantReadWriteLock controlledLock;
    private Set<FieldObject> visibleMapObjects;
    private ReentrantReadWriteLock visibleMapObjectsLock;
    private Map<Integer, MapleDoor> doors = new LinkedHashMap<>();
    // Skills
    private int battleShipHP = 0;
    private int energyBar = 0;
    private boolean Berserk = false;
    protected Map<PlayerSkill, PlayerSkillEntry> skills = new LinkedHashMap<>();
    public PlayerSkillMacro[] skillMacros;
    private ScheduledFuture<?> beholderHealingSchedule;
    private ScheduledFuture<?> beholderBuffSchedule;
    private ScheduledFuture<?> BerserkSchedule;
    private transient Map<Disease, DiseaseValueHolder> diseases;
    private transient Map<Integer, PlayerCoolDownValueHolder> coolDowns;
    // AntiCheater
    private boolean hasCheat = false;
    public long lastUsedCashItem;
    public long lastSelectNPCTime = 0;
    public long lastAttackTime = 0;
    public long lastHitTime = System.currentTimeMillis();
    public long lastHPTime, lastMPTime;
    private final transient CheatTracker antiCheat;
    private ScheduledFuture<?> dragonBloodSchedule;
    private ScheduledFuture<?> mapTimeLimitTask = null;
    //Aneis
    private List<ItemRing> crushRings = new LinkedList<>();
    private List<ItemRing> friendshipRings = new LinkedList<>();
    private List<ItemRing> weddingRings = new LinkedList<>();
    // Mounts
    protected TamingMob tamingMob;
    // Game
    protected int omokWins;
    protected int omokTies;
    protected int omokLosses;
    protected int matchCardWins;
    protected int matchCardTies;
    protected int matchCardLosses;
    private Minigame miniGame;
    // Party
    protected MapleParty party;
    protected MaplePartyCharacter mpc = null;
    // Interaction
    protected MapleBuddyList buddyList;
    private transient Trade trade = null;
    private Shop shop = null;
    private PlayerShop playerShop = null;
    // Messenger
    private MapleMessenger messenger;
    // Fame
    protected long lastFameTime;
    protected List<Integer> lastMonthFameIDs;
    // Summons
    private Map<Integer, MapleSummon> summons;
    private final List<MapleSummon> pirateSummons = new LinkedList<>();
    // Inventory
    protected final Inventory[] inventory;
    private boolean shield = false;
    private int chair, itemEffect, slots = 0;
    protected int petAutoHP, petAutoMP;
    private StorageKeeper storage = null;
    private static final List<Pair<Byte, Integer>> inventorySlots = new ArrayList<>();
    private static ItemInformationProvider ii = ItemInformationProvider.getInstance();
    // Mariage
    protected int partner;
    private int spouseItemId = 0;
    // GM
    private boolean hidden;
    // Pet
    protected List<ItemPet> pets;
    protected byte[] petStore;
    // Carnival
    private MCTeam MCPQTeam;
    private MCParty MCPQParty;
    private MCField MCPQField;
    private int availableCP = 0;
    private int totalCP = 0;
    // Playtime
    public long playtimeStart;
    public long playtime;
    // Others
    private int targetHpBarHash = 0;
    private long targetHpBarTime = 0;
    protected int[] rocks, regrocks;
    private String chalkBoardText;
    private boolean challenged = false, allowMapChange = true, canSmega = true, smegaEnabled = true;
    private boolean changedTrockLocations, changedRegrockLocations, changedSavedLocations, changedReports, changedSkillMacros;
    protected int votePoints, ariantPoints, ringRequest;
    private long lastPortalEntry = 0, lastCatch = 0, useTime = 0;
    private Date time;
    private CashShop cashShop;
    private long npcCd;
    private long mesoCd;
    private long mapCd;
    private long itemCd;
    private RockPaperScissors rps = null;
    private int owlSearch;
    // Speed Quiz Test 
    private transient SpeedQuiz sq;
    private long lastSpeedQuiz;
    // Third kill feature
    private long loginTime = System.currentTimeMillis();
    private AtomicInteger mobKills = new AtomicInteger(0);

    protected ScheduledFuture<?> expireTask;
    protected ScheduledFuture<?> recoveryTask;
    private final List<ScheduledFuture<?>> timers = new ArrayList<>();

    public String dataString;
    public String[] ariantRoomLeader = new String[3];
    public int[] ariantRoomSlot = new int[3];

    private int newWarpMap = -1;
    private boolean canWarpMap = true;
    private int canWarpCounter = 0;

    private int RecoveryHP = 0;
    private int RecoveryHPitemid = 0;
    private int RecoveryMP = 0;
    private int RecoveryMPitemid = 0;
    private String QQ = "";
    private String SJ = "";
    private String 群头衔 = "";
    private int vip = 0;
    private int 枫叶套 = 0;
    private long 攻击间隔 = 0;
    private long 捡东西间隔 = 0;
    private int 攻击间隔判断 = 0;
    private int 捡物距离 = 0;
    private long 军团间隔 = 0;
    private int 临时查看目标 = 0;
    public int 经验倍率 = 0;
    public int 废弃副本难度 = 0;
    public int 废弃副本错误次数 = 0;
    protected int 杀怪数量 = 0;
    protected int 主播 = 0;
    private PlayerQuest quest;
    private int mesoGuard;
    private int 叠加伤害 = 0;
    private int 叠加次数 = 0;
    private int 死亡攻击次数 = 0;
    private int 吸怪检测1数值 = 0;
    private int 吸怪检测1x坐标 = 0;
    private int 吸怪检测1y坐标 = 0;
    private long 本次上线在线时间 = 0;
    private int 显示红包类型 = 0;
    private long 买卖股票冷却 = 0;
    private long 群说话冷却 = 0;
    private int 角色泡点经验 = 0;
    private int 吸怪检测 = 0;
    private int todayOnlineTime = 0;
    private int totalOnlineTime = 0;
    private int totalOnlineTimett = 0;
    private int totalOnlineTimejc = 0;
    protected int 所有附魔技能 = 0;
    protected int master = 0;
    protected int 主界面 = 0;
    private int 检测 = 0;
    private int 无敌 = 0;
    protected int 攻击回蓝判断 = 0;
    protected int 市场传送点 = 0;
    //开关
    protected int switch_bufftime = 0;
    protected int switch_zhubosx = 0;
    protected int switch_qunltkx = 0;
    protected int switch_skill = 0;
    protected int switch_duanzao = 0;
    public int switch_duanzao() {
        return switch_duanzao;
    }

    public void switch_duanzao(int a) {
        switch_duanzao = a;
    }
    
    public int 判断攻击回蓝判断() {
        return 攻击回蓝判断;
    }

    public void 攻击回蓝判断() {
        攻击回蓝判断++;
    }

    public void 重置攻击回蓝判断() {
        攻击回蓝判断 = 0;
    }

    public int switch_skill() {
        return switch_skill;
    }

    public void switch_skill(int a) {
        switch_skill = a;
    }

    public int switch_qunltkx() {
        return switch_qunltkx;
    }

    public void switch_qunltkx(int a) {
        switch_qunltkx = a;
    }

    public int switch_zhubosx() {
        return switch_zhubosx;
    }

    public void switch_zhubosx(int a) {
        switch_zhubosx = a;
    }

    public int switch_bufftime() {
        return switch_bufftime;
    }

    public void switch_bufftime(int a) {
        switch_bufftime = a;
    }

    public int 市场传送点() {
        return 市场传送点;
    }

    public void 市场传送点(int a) {
        市场传送点 = a;
    }

    public int 检测() {
        return 检测;
    }

    public void 检测(int a) {
        检测 = a;
    }

    public int 无敌() {
        return 无敌;
    }

    public void 无敌(int a) {
        无敌 += a;
    }

    public int 主界面() {
        return 主界面;
    }

    public void 主界面(int a) {
        主界面 = a;
    }

    public int 师傅() {
        return master;
    }

    public void 拜师(int a) {
        master = a;
    }

    public void 记录在线时间() {
        todayOnlineTime++;
        totalOnlineTime++;
        totalOnlineTimett++;
        totalOnlineTimejc++;
        if (totalOnlineTimejc >= 60 && 检测 == 0) {
            if (getMap().monsterCount() > 0) {
                int x = (int) (Math.ceil(Math.random() * 8999) + 1000);
                检测 = x;
                无敌 = 3;
                totalOnlineTimejc = 0;
                dropMessage(5, "[提示]:你已被系统检测，请在聊天栏输入 " + x + " 来解除检测限制。");
                dropMessage(5, "[提示]:你已被系统检测，请在聊天栏输入 " + x + " 来解除检测限制。");
                dropMessage(5, "[提示]:你已被系统检测，请在聊天栏输入 " + x + " 来解除检测限制。");
            }
        }
        //刷新身上装备附魔汇总数据(false);
    }

    public void 重置在线时间(int a) {
        todayOnlineTime = 0;
        if (a == 1) {
            totalOnlineTimett = 0;
        }
    }

    public int 主播() {
        return 主播;
    }

    public void 主播(int a) {
        主播 = a;
    }

    public int 所有附魔技能() {
        return 所有附魔技能;
    }

    public long 群说话冷却() {
        return 群说话冷却;
    }

    public void 记录群说话冷却() {
        群说话冷却 = System.currentTimeMillis();
    }

    public long 买卖股票冷却() {
        return 买卖股票冷却;
    }

    public void 记录买卖股票冷却() {
        买卖股票冷却 = System.currentTimeMillis();
    }

    public long 本次上线在线时间() {
        return 本次上线在线时间;
    }

    public void 记录本次上线在线时间() {
        本次上线在线时间 = System.currentTimeMillis();
    }

    public void 增加捡物距离() {
        捡物距离++;
    }

    public void 减少捡物距离() {
        捡物距离--;
    }

    public int 判断捡物距离() {
        return 捡物距离;
    }

    private int 全屏检测数值 = 0;

    public void 增加全屏检测() {
        全屏检测数值++;
    }

    public void 减少全屏检测() {
        全屏检测数值--;
    }

    public int 判断全屏检测() {
        return 全屏检测数值;
    }

    public void 重置全屏检测() {
        全屏检测数值 = 0;
    }

    public void 重置吸怪检测1() {
        吸怪检测1数值 = 0;
        吸怪检测1x坐标 = 0;
        吸怪检测1y坐标 = 0;
    }

    public void 吸怪检测预警1() {
        吸怪检测1数值++;
    }

    public int 判断吸怪检测预警1() {
        return 吸怪检测1数值;
    }

    public void 记录吸怪检测1(int x, int y) {
        吸怪检测1x坐标 = x;
        吸怪检测1y坐标 = y;
    }

    public int 判断吸怪检测1x() {
        return 吸怪检测1x坐标;
    }

    public int 判断吸怪检测1y() {
        return 吸怪检测1y坐标;
    }

    public void 杀怪计数() {
        杀怪数量++;
    }

    public int 杀怪数量() {
        return 杀怪数量;
    }

    public void 叠加伤害(int a) {
        if (叠加次数 <= getLevel() / 10) {
            叠加次数++;
            叠加伤害 += a;
        }
    }

    public int 叠加伤害() {
        return 叠加伤害;
    }

    public void 攻击检测() {
        //没有武器的情况下
        if (getInventory(InventoryType.EQUIPPED).getItem((byte) ItemConstants.WEAPON) == null) {
            dropMessage(1, "数据异常，网络中断。");
            bans("没有武器的情况下进行攻击");
            getClient().close();
            sendMsgToQQGroup(""
                    + "QQ：" + getQQ() + "\r\n"
                    + "大区：" + 大区(getWorldId()) + "\r\n"
                    + "玩家：" + getName() + "\r\n"
                    + "地图：" + getMapName(getMapId()) + "\r\n"
                    + "原因：没有武器的情况下进行攻击\r\n",
                    合伙群);
        }
        //死亡攻击判定
        if (!isAlive()) {
            死亡攻击次数++;
            if (死亡攻击次数 == 5) {
                dropMessage(1, "数据异常，网络中断。");
                bans("角色死亡下进行攻击");
                getClient().close();
                sendMsgToQQGroup(""
                        + "QQ：" + getQQ() + "\r\n"
                        + "大区：" + 大区(getWorldId()) + "\r\n"
                        + "玩家：" + getName() + "\r\n"
                        + "地图：" + getMapName(getMapId()) + "\r\n"
                        + "原因：角色死亡下进行攻击\r\n",
                        合伙群);
            }
        } else {
            死亡攻击次数 = 0;
        }
        //攻击间隔判断
        if (攻击间隔 > 0) {
            if (攻击间隔() < 200) {
                攻击间隔判断++;
                if (攻击间隔判断 == 20) {
                    dropMessage(1, "数据异常，网络中断。");
                    bans("攻击速度异常,频率：" + 攻击间隔() + "");
                    getClient().close();
                    sendMsgToQQGroup(""
                            + "QQ：" + getQQ() + "\r\n"
                            + "大区：" + 大区(getWorldId()) + "\r\n"
                            + "玩家：" + getName() + "\r\n"
                            + "地图：" + getMapName(getMapId()) + "\r\n"
                            + "原因：攻击速度异常\r\n"
                            + "频率：" + 攻击间隔() + "\r\n"
                            + "连续：" + 攻击间隔判断 + "次", 合伙群);
                }
            } else {
                攻击间隔判断 = 0;
            }
        }
        记录攻击间隔();
    }

    public void 记录废弃副本难度(int a) {
        废弃副本难度 = a;
    }

    public long 废弃副本难度() {
        return 废弃副本难度;
    }

    public void 重置废弃副本错误次数() {
        废弃副本错误次数 = 0;
    }

    public void 记录废弃副本错误次数(int a) {
        废弃副本错误次数 = 废弃副本错误次数 + a;
    }

    public long 废弃副本错误次数() {
        return 废弃副本错误次数;
    }

    public void 记录军团间隔() {
        军团间隔 = System.currentTimeMillis();
    }

    public long 军团间隔() {
        return System.currentTimeMillis() - 军团间隔;
    }

    public void 记录攻击间隔() {
        攻击间隔 = System.currentTimeMillis();
    }

    public long 攻击间隔() {
        return System.currentTimeMillis() - 攻击间隔;
    }

    public void 记录捡东西间隔() {
        捡东西间隔 = System.currentTimeMillis();
    }

    public long 捡东西间隔() {
        return System.currentTimeMillis() - 捡东西间隔;
    }

    public void 设置目标(int a) {
        临时查看目标 = a;
    }

    public int 查看目标() {
        return 临时查看目标;
    }

    public int getEXPMod() {
        return stats.经验倍率;
    }

    public int 枫叶套() {
        return 枫叶套;
    }

    private int 伤害记录 = 0;

    public int 伤害记录() {
        return 伤害记录;
    }

    public void 设置最高伤害记录(int a) {
        伤害记录 = a;
    }

    private Player(final boolean ChannelServer) {
        setStance(0);
        inventory = new Inventory[InventoryType.values().length];
        for (InventoryType type : InventoryType.values()) {
            inventory[type.ordinal()] = new Inventory(type, (byte) 100);
        }

        savedLocations = new int[SavedLocationType.values().length];
        for (int i = 0; i < SavedLocationType.values().length; i++) {
            savedLocations[i] = -1;
        }
        if (ChannelServer) {
            changedReports = false;
            changedTrockLocations = false;
            changedRegrockLocations = false;
            changedSavedLocations = false;
            changedSkillMacros = false;
            lastHPTime = 0;
            lastMPTime = 0;
            petStore = new byte[3];
            for (int i = 0; i < petStore.length; i++) {
                petStore[i] = (byte) -1;
            }
            pets = new ArrayList<>();
            visibleMapObjects = new LinkedHashSet<>();
            visibleMapObjectsLock = new ReentrantReadWriteLock();
            controlled = new LinkedHashSet<>();
            controlledLock = new ReentrantReadWriteLock();
            summons = new LinkedHashMap<>();
            diseases = new ConcurrentEnumMap<>(Disease.class);
            coolDowns = new LinkedHashMap<>();
            rocks = new int[10];
            regrocks = new int[5];
        }
        stats = new PlayerStatsManager(this);
        quests = new LinkedHashMap<>();
        antiCheat = new CheatTracker(this);
        setPosition(new Point(0, 0));
        quest = new PlayerQuest(this);
    }

    public Player getThis() {
        return this;
    }

    /**
     * <载入角色信息>
     */
    public static Player loadinCharacterDatabase(int characterId, Client client, boolean channelserver) throws SQLException {
        final Player ret = new Player(channelserver);
        ret.client = client;
        ret.id = characterId;

        PreparedStatement ps = null;
        PreparedStatement pse = null;
        ResultSet rs = null;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            ps = con.prepareStatement("SELECT * FROM characters WHERE id = ?");
            ps.setInt(1, ret.getId());
            rs = ps.executeQuery();

            PlayerFactory.loadingCharacterStats(ret, rs, con);

            PlayerFactory.loadingCharacterItems(ret, channelserver);

            PlayerFactory.loadingCharacterIntoGame(ret, channelserver, rs);

            rs.close();
            ps.close();

            PlayerFactory.loadingCharacterAccountStats(ret, ps, rs, con);

            ret.cashShop = new CashShop(ret.accountid, ret.id);

            if (channelserver) {

                PlayerFactory.loadingCharacterSkillsAndMacros(ret, ps, rs, con);

                PlayerFactory.loadingCharacterLocations(ret, ps, rs, con);

                PlayerFactory.loadingCharacterFame(ret, ps, rs, con);

                ret.buddyList.loadFromDb(characterId);
                ret.storage = StorageKeeper.loadStorage(ret.accountid, ret.world);
                ret.stats.recalcLocalStats();
                ret.stats.silentEnforceMaxHpMp();
                ret.quest.loadQuests(ps, rs, con);
            }
            return ret;
        } catch (SQLException ex) {
            System.out.println("[ERROR] Failed to load character!");
            FileLogger.printError(FileLogger.DATABASE_EXCEPTION, ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (pse != null) {
                    pse.close();
                }
                if (rs != null) {
                    rs.close();
                }

                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException ex) {
                System.out.println("[ERROR] Failed to load character!");
                FileLogger.printError(FileLogger.DATABASE_EXCEPTION, ex);
            }
        }
        return null;
    }

    public static Player 载入技能(int characterId, Client client, boolean channelserver) throws SQLException {
        final Player ret = new Player(channelserver);
        ret.client = client;
        ret.id = characterId;
        PreparedStatement ps = null;
        PreparedStatement pse = null;
        ResultSet rs = null;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            ps = con.prepareStatement("SELECT * FROM characters WHERE id = ?");
            ps.setInt(1, ret.getId());
            rs = ps.executeQuery();
            if (channelserver) {
                PlayerFactory.loadingCharacterSkillsAndMacros(ret, ps, rs, con);
            }
            rs.close();
            ps.close();
            return ret;
        } catch (SQLException ex) {
            System.out.println("[ERROR] Failed to load character!");
            FileLogger.printError(FileLogger.DATABASE_EXCEPTION, ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (pse != null) {
                    pse.close();
                }
                if (rs != null) {
                    rs.close();
                }

                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException ex) {
                System.out.println("[ERROR] Failed to load character!");
                FileLogger.printError(FileLogger.DATABASE_EXCEPTION, ex);
            }
        }
        return null;
    }

    public static Player getDefault(Client client, int chrid) {
        Player ret = getDefault(client);
        ret.id = chrid;
        return ret;
    }

    public static Player getDefault(Client client) {
        Player ret = new Player(false);

        ret.client = client;
        ret.stats.maxHP = GameConstants.DEFAULT_MAXHP;
        ret.stats.hp = GameConstants.DEFAULT_HP;
        ret.stats.maxMP = GameConstants.DEFAULT_MAXMP;
        ret.stats.mp = GameConstants.DEFAULT_MP;

        ret.field = null;
        ret.exp.set(0);
        ret.meso.set(0);
        ret.gm = 0;
        ret.job = GameConstants.DEFAULT_JOB;
        ret.level = GameConstants.DEFAULT_LEVEL;
        ret.accountid = client.getAccountID();
        ret.buddyList = new MapleBuddyList(GameConstants.DEFAULT_BUDDY);
        ret.tamingMob = null;

        ret.getInventory(InventoryType.EQUIP).setSlotLimit(GameConstants.DEFAULT_SLOTLIMIT);
        ret.getInventory(InventoryType.USE).setSlotLimit(GameConstants.DEFAULT_SLOTLIMIT);
        ret.getInventory(InventoryType.SETUP).setSlotLimit(GameConstants.DEFAULT_SLOTLIMIT);
        ret.getInventory(InventoryType.ETC).setSlotLimit(GameConstants.DEFAULT_SLOTLIMIT);
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?")) {
                ps.setInt(1, ret.accountid);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ret.client.setAccountName(rs.getString("name"));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed getDefault function!");
            FileLogger.printError(FileLogger.DATABASE_EXCEPTION, e);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException ex) {
            }
        }
        for (int i = 0; i < GameConstants.DEFAULT_KEY.length; i++) {
            ret.keymap.put(GameConstants.DEFAULT_KEY[i], new PlayerKeyBinding(GameConstants.DEFAULT_TYPE[i], GameConstants.DEFAULT_ACTION[i]));
        }
        ret.stats.recalcLocalStats();
        return ret;
    }

    /**
     * <创建角色信息>
     */
    public void saveNewCharDB(Player p) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = Start.getInstance().getConnection();
            con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            ps = con.prepareStatement("INSERT INTO characters (`level`, `str`, `dex`, `luk`, `inte`, `hp`, `mp`, `maxhp`, `maxmp`, `skincolor`, `gender`, `job`, `hair`, `face`, `buddyCapacity`, `accountid`, `world`, `name`, `world2`) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            int order = 0;
            ps.setInt(++order, 1);
            final PlayerStatsManager stat = p.stats;
            ps.setInt(++order, stat.getStr());
            ps.setInt(++order, stat.getDex());
            ps.setInt(++order, stat.getLuk());
            ps.setInt(++order, stat.getInt());
            ps.setInt(++order, stat.getHp());
            ps.setInt(++order, stat.getMp());
            ps.setInt(++order, stat.getCurrentMaxHp());
            ps.setInt(++order, stat.getCurrentMaxMp());
            ps.setInt(++order, skin.getId());
            ps.setInt(++order, client.getGender());
            ps.setInt(++order, job.getId());
            ps.setInt(++order, hair);
            ps.setInt(++order, eyes);
            ps.setInt(++order, buddyList.getCapacity());
            ps.setInt(++order, p.getAccountID());
            ps.setInt(++order, p.world);
            ps.setString(++order, p.name);
            ps.setInt(++order, p.world);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                p.id = rs.getInt(1);
            }
            rs.close();
            ps.close();

            List<Pair<Item, InventoryType>> itemsWithType = new ArrayList<>();
            for (Inventory iv : inventory) {
                iv.lockInventory().readLock().lock();
                try {
                    iv.list().stream().forEach((item) -> {
                        itemsWithType.add(new Pair<>(item, iv.getType()));
                    });
                } finally {
                    iv.lockInventory().readLock().unlock();
                }
            }
            ItemFactory.INVENTORY.saveItems(itemsWithType, id);
        } catch (DatabaseException | SQLException e) {
            System.out.println("[ERROR]  Error saving character data saveNewCharDB!");
            FileLogger.printError(FileLogger.DATABASE_EXCEPTION, e);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException ex) {
                System.out.println("[ERROR] Error rolling back saveNewCharDB!");
                FileLogger.printError(FileLogger.DATABASE_EXCEPTION, e);
            }
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (con != null && !con.isClosed()) {
                    con.setAutoCommit(true);
                    con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("[ERROR] Error going back to autocommit mode in saveNewCharDB!");
                FileLogger.printError(FileLogger.DATABASE_EXCEPTION, e);
            }
        }

    }

    /**
     * <存档角色信息>
     */
    public void saveDatabase() {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            con.setAutoCommit(false);

            PlayerSaveFactory.savingCharacterInventory(this);
            PlayerSaveFactory.savingCharacterStats(this, con);
            PlayerSaveFactory.savingCharacterSkills(this, con, this);

            if (getBuddylist().changed()) {
                PlayerSaveFactory.savingCharacterBuddy(this, con);
                getBuddylist().setChanged(false);
            }

            if (getCashShop() != null) {
                getCashShop().saveToDB();
            }

            if (getStorage() != null) {
                getStorage().saveToDB();
            }

            if (getChangedSavedLocations()) {
                PlayerSaveFactory.savingCharacterSavedLocations(this, con);
            }

            if (getChangedTrockLocations()) {
                PlayerSaveFactory.savingCharacterTrockLocations(this, con);
            }

            if (getChangedRegrockLocations()) {
                PlayerSaveFactory.savingCharacterRegRockLocations(this, con);
            }

            if (quest != null) {
                quest.saveQuests(con);
            }

            con.commit();
        } catch (DatabaseException | SQLException e) {
            e.printStackTrace();
            System.out.println("[ERROR] Error rolling back saveToDB!");
            FileLogger.printError(FileLogger.DATABASE_EXCEPTION, e);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException ex) {
                System.out.println("[ERROR] Error rolling back saveToDB (" + getName() + ")!");
                FileLogger.printError(FileLogger.DATABASE_EXCEPTION, ex);
            }
        } catch (Exception e) {
            e.printStackTrace();
            FileLogger.printError(FileLogger.DATABASE_EXCEPTION, e, "Error saving " + name + " Level: " + level + " Job: " + job.getId());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.setAutoCommit(true);
                    con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("[ERROR] Error rolling back saveToDB (" + getName() + ")!");
                FileLogger.printError(FileLogger.DATABASE_EXCEPTION, e);
            }
        }
    }

    /**
     * <存档角色道具>
     */
    public void saveDatabaseItem() {
        saveDatabase();
        //PlayerSaveFactory.savingCharacterInventory(this);
    }

    /**
     * <存档角色技能>
     */
    public void saveDatabaseSkills() {
        Connection con = Start.getInstance().getConnection();
        try {
            PlayerSaveFactory.savingCharacterSkills(this, con, this);
        } catch (Exception e) {
            FileLogger.printError(FileLogger.DATABASE_EXCEPTION, e, "Error saving " + name + " Level: " + level + " Job: " + job.getId());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("[ERROR] Error rolling back saveToDB (" + getName() + ")!");
                FileLogger.printError(FileLogger.DATABASE_EXCEPTION, e);
            }
        }
    }

    public void updateQuestInfo(int quest, String info) {
        MapleQuest q = MapleQuest.getInstance(quest);
        MapleQuestStatus qs = getQuest(q);
        qs.setInfo(info);

        synchronized (quests) {
            quests.put(q.getId(), qs);
        }

        announce(PacketCreator.UpdateQuest(qs, false));
        if (qs.getQuest().getInfoNumber() > 0) {
            announce(PacketCreator.UpdateQuest(qs, true));
        }
        announce(PacketCreator.UpdateQuestInfo((short) qs.getQuest().getId(), qs.getNpc()));
    }

    public void updateQuest(MapleQuestStatus quest) {
        synchronized (quests) {
            quests.put(quest.getQuestID(), quest);
        }
        switch (quest.getStatus()) {
            case STARTED:
                announce(PacketCreator.UpdateQuest(quest, false));
                if (quest.getQuest().getInfoNumber() > 0) {
                    announce(PacketCreator.UpdateQuest(quest, true));
                }
                announce(PacketCreator.UpdateQuestInfo((short) quest.getQuest().getId(), quest.getNpc()));
                break;
            case COMPLETED:
                announce(PacketCreator.CompleteQuest((short) quest.getQuest().getId(), quest.getCompletionTime()));
                if (GameConstants.EARN_QUESTPOINT) {
                    quest.setCompleted(quest.getCompleted() + GameConstants.QUESTPOINT_QTY);
                }
                break;
            case NOT_STARTED:
                announce(PacketCreator.UpdateQuest(quest, false));
                if (quest.getQuest().getInfoNumber() > 0) {
                    announce(PacketCreator.UpdateQuest(quest, true));
                }
                break;
            case UNDEFINED:
                break;
            default:
                break;
        }
    }

    public PlayerQuest getQuest() {
        return quest;
    }

    public void cancelExpirationTask() {
        if (expireTask != null) {
            expireTask.cancel(false);
            expireTask = null;
        }
    }

    public void expirationTask() {
        if (expireTask == null) {
            expireTask = CharacterTimer.getInstance().register(() -> {
                long currenttime = System.currentTimeMillis();
                List<Item> toberemove = new ArrayList<>();
                for (Inventory inv : this.inventory) {
                    inv.list().stream().forEach((item) -> {
                        if (item.getExpiration() > 0 && currenttime >= item.getExpiration()) {
                            boolean sendPetExpiration = false;
                            if (item.getPet() != null) {
                                sendPetExpiration = true;
                                if (ItemInformationProvider.getInstance().cannotRevive(item.getItemId())) {
                                    toberemove.add(item);
                                } else {
                                    item.setExpiration(0);
                                }
                            } else {
                                toberemove.add(item);
                            }
                            if (sendPetExpiration) {
                                announce(PetPackets.RemovePet(getId(), getPetIndex(item.getPet()), (byte) 2));
                            }
                            announce(PacketCreator.ItemExpired(item.getItemId()));
                        }
                    });
                }
                toberemove.stream().forEach((item) -> {
                    InventoryManipulator.removeFromSlot(client, item.getInventoryType(), item.getPosition(), item.getQuantity(), true);
                });
                toberemove.clear();
            }, 60000);
        }
    }

    public boolean 技能BUFF(int skillid) {
        return isActiveBuffedValue(skillid);
    }

    public boolean isActiveBuffedValue(int skillid) {
        LinkedList<BuffStatValueHolder> allBuffs;
        effectLock.readLock().lock();
        try {
            allBuffs = new LinkedList<>(effects.values());
        } finally {
            effectLock.readLock().unlock();
        }
        for (BuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.isSkill() && mbsvh.effect.getSourceId() == skillid) {
                return true;
            }
        }
        return false;
    }

    public Integer getBuffedValue(BuffStat effect) {
        BuffStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return null;
        }
        return mbsvh.value;
    }

    public boolean isBuffFrom(BuffStat stat, PlayerSkill skill) {
        BuffStatValueHolder mbsvh = effects.get(stat);
        if (mbsvh == null) {
            return false;
        }
        return mbsvh.effect.isSkill() && mbsvh.effect.getSourceId() == skill.getId();
    }

    public int getBuffSource(BuffStat stat) {
        BuffStatValueHolder mbsvh = effects.get(stat);
        if (mbsvh == null) {
            return -1;
        }
        return mbsvh.effect.getSourceId();
    }

    public int getItemQuantity(int itemid) {
        return inventory[ItemInformationProvider.getInstance().getInventoryType(itemid).ordinal()].countById(itemid);
    }

    public int getItemQuantity(int itemid, boolean checkEquipped) {
        int possesed = inventory[ItemInformationProvider.getInstance().getInventoryType(itemid).ordinal()].countById(itemid);
        if (checkEquipped) {
            possesed += inventory[InventoryType.EQUIPPED.ordinal()].countById(itemid);
        }
        return possesed;
    }

    public void setBuffedValue(BuffStat effect, int value) {
        BuffStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return;
        }
        mbsvh.value = value;
    }

    public Long getBuffedStarttime(BuffStat effect) {
        BuffStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return null;
        }
        return mbsvh.startTime;
    }

    public MapleStatEffect getStatForBuff(BuffStat effect) {
        BuffStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return null;
        }
        return mbsvh.effect;
    }

    private void prepareDragonBlood(final MapleStatEffect bloodEffect) {
        if (this.dragonBloodSchedule != null) {
            this.dragonBloodSchedule.cancel(false);
        }
        this.dragonBloodSchedule = CharacterTimer.getInstance().register(new DragonBloodRunnable(bloodEffect), 4000, 4000);
    }

    public RockPaperScissors getRPS() {
        return rps;
    }

    public void setRPS(RockPaperScissors rps) {
        this.rps = rps;
    }

    public void setOwlSearch(int id) {
        owlSearch = id;
    }

    public int getOwlSearch() {
        return owlSearch;
    }

    public void addSummon(int id, MapleSummon summon) {
        summons.put(id, summon);
    }

    public int getTargetHpBarHash() {
        return this.targetHpBarHash;
    }

    public void setTargetHpBarHash(int mobHash) {
        this.targetHpBarHash = mobHash;
    }

    public long getTargetHpBarTime() {
        return this.targetHpBarTime;
    }

    public void setTargetHpBarTime(long timeNow) {
        this.targetHpBarTime = timeNow;
    }

    public void setPlayerAggro(int mobHash) {
        setTargetHpBarHash(mobHash);
        setTargetHpBarTime(System.currentTimeMillis());
    }

    public void resetPlayerAggro() {
        if (getChannelServer().unregisterDisabledServerMessage(id)) {
            client.announceServerMessage();
        }
        setTargetHpBarHash(0);
        setTargetHpBarTime(0);
    }

    public final ChannelServer getChannelServer() {
        return getWorld().getChannelById(getClient().getChannel());
    }

    private final class DragonBloodRunnable implements Runnable {

        private final MapleStatEffect effect;

        public DragonBloodRunnable(final MapleStatEffect effect) {
            this.effect = effect;
        }

        @Override
        public void run() {
            if (Player.this.stats.getHp() - this.effect.getX() > 1) {
                Player.this.cancelBuffStats(BuffStat.DragonBlood);
            } else {
                stats.addHP(-this.effect.getX());
                final int bloodEffectSourceId = this.effect.getSourceId();
                final OutPacket ownEffectPacket = EffectPackets.ShowOwnBuffEffect(bloodEffectSourceId, PlayerEffects.SKILL_SPECIAL.getEffect());
                client.write(ownEffectPacket);
                final OutPacket otherEffectPacket = EffectPackets.BuffMapVisualEffect(Player.this.getId(), bloodEffectSourceId, 5);
                field.broadcastMessage(Player.this, otherEffectPacket, false);
            }
        }
    }

    public void startMapTimeLimitTask(final Field from, final Field to) {
        if (to.getTimeLimit() > 0 && from != null) {
            final Player p = this;
            mapTimeLimitTask = CharacterTimer.getInstance().register(() -> {
                Portal pfrom = null;
                if (MapConstants.isMiniDungeonMap(getMap().getId())) {
                    pfrom = from.getPortal("MD00");
                } else {
                    pfrom = from.getPortal(0);
                }
                if (pfrom != null) {
                    p.changeMap(from, pfrom);
                }
            }, from.getTimeLimit() * 1000, from.getTimeLimit() * 1000);
        }
    }

    public void cancelMapTimeLimitTask() {
        if (mapTimeLimitTask != null) {
            mapTimeLimitTask.cancel(false);
        }
    }

    public void toggleVisibility(boolean login) {
        setVisibility(!isHidden());
    }

    public void setVisibility(boolean hide) {
        setVisibility(hide, false);
    }

    public void setVisibility(boolean hide, boolean login) {
        if (isGameMaster() && hide != this.hidden) {
            if (!hide) {
                this.hidden = false;

                announce(PacketCreator.StopHide());

                List<BuffStat> stat = Collections.singletonList(BuffStat.DarkSight);
                field.broadcastGMMessage(this, PacketCreator.CancelForeignBuff(id, stat), false);
                field.broadcastMessage(this, PacketCreator.SpawnPlayerMapObject(this), false);

                for (MapleSummon ms : this.getSummonsValues()) {
                    field.broadcastNONGMMessage(this, PacketCreator.SpawnSpecialFieldObject(ms, false), false);
                }

                updatePartyMemberHP();

            } else {
                this.hidden = true;
                announce(PacketCreator.ShowHide());
                if (!login) {
                    getMap().broadcastMessage(this, PacketCreator.RemovePlayerFromMap(getId()), false);
                }

                field.broadcastGMMessage(this, PacketCreator.SpawnPlayerMapObject(this), false);

                List<Pair<BuffStat, Integer>> ldsstat = Collections.singletonList(new Pair<BuffStat, Integer>(BuffStat.DarkSight, 0));
                field.broadcastGMMessage(this, PacketCreator.BuffMapEffect(id, ldsstat, false), false);

                for (MapleMonster mon : this.getControlledMonsters()) {
                    mon.setController(null);
                    mon.setControllerHasAggro(false);
                    mon.setControllerKnowsAboutAggro(false);
                    mon.getMap().updateMonsterController(mon);
                }
            }
            //showHint("You are currently " + (hidden ? "with " : "without ") + "hide.");
            announce(PacketCreator.EnableActions());
        }
    }

    public void registerEffect(MapleStatEffect effect, long starttime, ScheduledFuture<?> schedule) {
        registerEffect(effect, starttime, schedule, effect.getStatups());
    }

    public void registerEffect(MapleStatEffect effect, long starttime, ScheduledFuture<?> schedule, List<Pair<BuffStat, Integer>> statups) {
        if (effect.isHide()) {
            field.broadcastMessage(this, PacketCreator.RemovePlayerFromMap(getId()), false);
        } else if (effect.isDragonBlood()) {
            prepareDragonBlood(effect);
        } else if (effect.isBerserk()) {
            checkBerserk(isHidden());
        } else if (effect.isMonsterRiding()) {
            getMount().startSchedule();
        } else if (effect.isBeholder()) {
            prepareBeholderEffect();
        }
        statups.stream().forEach((statup) -> {
            int value = statup.getRight();
            effectLock.writeLock().lock();
            try {
                effects.put(statup.getLeft(), new BuffStatValueHolder(effect, starttime, schedule, value));
            } finally {
                effectLock.writeLock().unlock();
            }
        });
        stats.recalcLocalStats();
    }

    public int getSlot() {
        return slots;
    }

    public byte getSlots(int type) {
        return type == InventoryType.CASH.getType() ? 96 : inventory[type].getSlotLimit();
    }

    private List<BuffStat> getBuffStats(MapleStatEffect effect, long startTime) {
        final List<BuffStat> bstats = new ArrayList<>();
        Map<BuffStat, BuffStatValueHolder> allBuffs;
        effectLock.readLock().lock();
        try {
            allBuffs = new LinkedHashMap<>(effects);
        } finally {
            effectLock.readLock().unlock();
        }
        for (Entry<BuffStat, BuffStatValueHolder> stateffect : allBuffs.entrySet()) {
            final BuffStatValueHolder mbsvh = stateffect.getValue();
            if (mbsvh.effect.sameSource(effect) && (startTime == -1 || startTime == mbsvh.startTime)) {
                bstats.add(stateffect.getKey());
            }
        }
        return bstats;
    }

    private void deregisterBuffStats(List<BuffStat> stats) {
        List<BuffStatValueHolder> effectsToCancel = new ArrayList<>(stats.size());
        for (BuffStat stat : stats) {
            BuffStatValueHolder mbsvh = null;
            effectLock.writeLock().lock();
            try {
                mbsvh = effects.remove(stat);
            } finally {
                effectLock.writeLock().unlock();
            }
            if (mbsvh != null) {
                boolean addMbsvh = true;
                for (BuffStatValueHolder contained : effectsToCancel) {
                    if (mbsvh.startTime == contained.startTime && contained.effect == mbsvh.effect) {
                        addMbsvh = false;
                    }
                }
                if (addMbsvh) {
                    effectsToCancel.add(mbsvh);
                }
                if (stat != null) {
                    switch (stat) {
                        case Summon:
                        case Puppet:
                            final int summonId = mbsvh.effect.getSourceId();
                            final MapleSummon summon = summons.get(summonId);
                            if (summon != null) {
                                getMap().broadcastMessage(PacketCreator.RemoveSpecialMapObject(summon, true));
                                getMap().removeMapObject(summon);
                                removeVisibleMapObject(summon);
                                summons.remove(summonId);

                                if (summon.getSkill() == DarkKnight.Beholder) {
                                    if (beholderHealingSchedule != null) {
                                        beholderHealingSchedule.cancel(false);
                                        beholderHealingSchedule = null;
                                    }
                                    if (beholderBuffSchedule != null) {
                                        beholderBuffSchedule.cancel(false);
                                        beholderBuffSchedule = null;
                                    }
                                }
                            }

                        case DragonBlood:
                            if (dragonBloodSchedule != null) {
                                dragonBloodSchedule.cancel(false);
                                dragonBloodSchedule = null;
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        effectsToCancel.stream().filter((cancelEffectCancelTasks) -> (getBuffStats(cancelEffectCancelTasks.effect, cancelEffectCancelTasks.startTime).isEmpty()))
                .filter((cancelEffectCancelTasks) -> (cancelEffectCancelTasks.schedule != null)).forEach((cancelEffectCancelTasks) -> {
            cancelEffectCancelTasks.schedule.cancel(false);
        });
    }

    public void cancelEffect(int itemId) {
        cancelEffect(ii.getItemEffect(itemId), false, -1);
    }

    /**
     * @param effect
     * @param overwrite when overwrite is set no data is sent and all the
     * Buffstats in the StatEffect are deregistered
     * @param startTime
     */
    public void cancelEffect(MapleStatEffect effect, boolean overwrite, long startTime) {
        List<BuffStat> buffstats;
        if (!overwrite) {
            buffstats = getBuffStats(effect, startTime);
            if (switch_bufftime == 0) {
                dropMessage(5, "[提示]:BUFF消失 - " + PlayerSkillFactory.getSkillName(effect.getSourceId()));
            }
            if (switch_skill == 0) {
                announce(EffectPackets.ShowEffect("Skill2/" + effect.getSourceId() + ""));
            }
        } else {
            List<Pair<BuffStat, Integer>> statups = effect.getStatups();
            buffstats = new ArrayList<>(statups.size());
            statups.stream().forEach((statup) -> {
                buffstats.add(statup.getLeft());
            });
        }
        if (buffstats.size() < 1) {
            return;
        }
        deregisterBuffStats(buffstats);
        if (effect.isMysticDoor()) {
            MapleDoor destroyDoor = doors.remove(this.getId());

            if (destroyDoor != null) {
                destroyDoor.getTarget().removeMapObject(destroyDoor.getAreaDoor());
                destroyDoor.getTown().removeMapObject(destroyDoor.getTownDoor());

                destroyDoor.getTarget().getCharactersThreadsafe().stream().forEach((chr) -> {
                    destroyDoor.getAreaDoor().sendDestroyData(chr.getClient());
                });
                destroyDoor.getTown().getCharactersThreadsafe().stream().forEach((chr) -> {
                    destroyDoor.getTownDoor().sendDestroyData(chr.getClient());
                });
                if (party != null) {
                    getParty().getMembers().stream().map((partyMembers) -> {
                        partyMembers.getPlayer().removeDoor(this.getId());
                        return partyMembers;
                    }).forEach((partyMembers) -> {
                        partyMembers.removeDoor(this.getId());
                    });
                    silentPartyUpdate();
                }
            }

        }
        if (effect.isMonsterRiding()) {
            if (effect.getSourceId() != Corsair.Battleship) {
                this.getMount().cancelSchedule();
            }
        }
        if (effect.isEnergy()) {
            if (getEnergy() > 0) {
                this.setEnergyBar(0);
            }
        }
        if (!overwrite) {
            cancelPlayerBuffs(buffstats);
        }
    }

    public void cancelBuffStats(BuffStat stat) {
        List<BuffStat> buffStatList = Arrays.asList(stat);
        deregisterBuffStats(buffStatList);
        cancelPlayerBuffs(buffStatList);
    }

    public void cancelEffectFromBuffStat(BuffStat stat) {
        BuffStatValueHolder effect = effects.get(stat);
        if (effect != null) {
            cancelEffect(effect.effect, false, -1);
        }
    }

    /**
     * <BUFF消失>
     */
    private void cancelPlayerBuffs(List<BuffStat> buffstats) {
        if (client.getChannelServer().getPlayerStorage().getCharacterById(getId()) != null) {
            stats.recalcLocalStats();
            //恢复血量
            stats.enforceMaxHpMp();
            client.announce(PacketCreator.CancelBuff(buffstats));
            if (buffstats.size() > 0) {
                getMap().broadcastMessage(this, PacketCreator.CancelForeignBuff(getId(), buffstats), false);
            }
        }
    }

    public void dispel() {
        List<BuffStatValueHolder> allBuffs;
        effectLock.readLock().lock();
        try {
            allBuffs = new ArrayList<>(effects.values());
        } finally {
            effectLock.readLock().unlock();
        }
        if (!isHidden()) {
            for (BuffStatValueHolder mbsvh : allBuffs) {
                if (mbsvh.effect.isSkill() && mbsvh.schedule != null && !mbsvh.effect.isMorph()) {
                    cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                }
            }
        }
    }

    public void cancelAllBuffs(boolean disconnect) {
        if (disconnect) {
            effectLock.writeLock().lock();
            try {
                effects.clear();
            } finally {
                effectLock.writeLock().unlock();
            }
        } else {
            List<BuffStatValueHolder> allBuffs;
            effectLock.readLock().lock();
            try {
                allBuffs = new ArrayList<>(effects.values());
            } finally {
                effectLock.readLock().unlock();
            }
            for (BuffStatValueHolder mbsvh : allBuffs) {
                cancelEffect(mbsvh.effect, false, mbsvh.startTime);
            }
        }
    }

    public void cancelMorphs() {
        List<BuffStatValueHolder> allBuffs;
        effectLock.readLock().lock();
        try {
            allBuffs = new ArrayList<>(effects.values());
        } finally {
            effectLock.readLock().unlock();
        }
        for (BuffStatValueHolder mbsvh : allBuffs) {
            switch (mbsvh.effect.getSourceId()) {
                case Marauder.Transformation:
                case Buccaneer.SuperTransformation:
                    return;
                default:
                    if (mbsvh.effect.isMorph()) {
                        cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                        return;
                    }
            }
        }
    }

    public void silentGiveBuffs(List<PlayerBuffValueHolder> buffs) {
        if (buffs == null) {
            return;
        }
        for (PlayerBuffValueHolder mbsvh : buffs) {
            mbsvh.effect.silentApplyBuff(this, mbsvh.startTime);
        }
    }

    public List<PlayerBuffValueHolder> getAllBuffs() {
        List<PlayerBuffValueHolder> ret = new ArrayList<>();
        effectLock.readLock().lock();
        try {
            for (BuffStatValueHolder mbsvh : effects.values()) {
                ret.add(new PlayerBuffValueHolder(mbsvh.startTime, mbsvh.effect));
            }
        } finally {
            effectLock.readLock().unlock();
        }
        return ret;
    }

    public void cancelMagicDoor() {
        List<BuffStatValueHolder> mbsvhList;
        effectLock.readLock().lock();
        try {
            mbsvhList = new ArrayList<>(effects.values());
        } finally {
            effectLock.readLock().unlock();
        }

        for (BuffStatValueHolder mbsvh : mbsvhList) {
            if (mbsvh.effect.isMysticDoor()) {
                cancelEffect(mbsvh.effect, false, mbsvh.startTime);
            }
        }
    }

    public void handleOrbgain() {
        int orbCount = getBuffedValue(BuffStat.ComboAttack);
        PlayerSkill combo = PlayerSkillFactory.getSkill(Crusader.斗气集中);
        PlayerSkill advCombo = PlayerSkillFactory.getSkill(Hero.AdvancedComboAttack);

        MapleStatEffect cEffect = null;
        int advComboSkillLevel = getSkillLevel(advCombo);
        if (advComboSkillLevel > 0) {
            cEffect = advCombo.getEffect(advComboSkillLevel);
        } else {
            cEffect = combo.getEffect(getSkillLevel(combo));
        }

        if (orbCount < cEffect.getX() + 1) {
            int newOrbCount = orbCount + 1;
            if (advComboSkillLevel > 0 && cEffect.makeChanceResult()) {
                if (newOrbCount < cEffect.getX() + 1) {
                    newOrbCount++;
                }
            }

            List<Pair<BuffStat, Integer>> stat = Collections.singletonList(new Pair<>(BuffStat.ComboAttack, newOrbCount));
            setBuffedValue(BuffStat.ComboAttack, newOrbCount);
            int duration = cEffect.getDuration();
            duration += (int) ((getBuffedStarttime(BuffStat.ComboAttack) - System.currentTimeMillis()));

            getClient().write(PacketCreator.GiveBuff(1111002, duration, stat));
            getMap().broadcastMessage(this, PacketCreator.BuffMapEffect(getId(), stat, false), false);
        }
    }

    public void handleOrbconsume() {
        PlayerSkill combo = PlayerSkillFactory.getSkill(Crusader.斗气集中);
        MapleStatEffect cEffect = combo.getEffect(getSkillLevel(combo));
        List<Pair<BuffStat, Integer>> stat = Collections.singletonList(new Pair<>(BuffStat.ComboAttack, 1));
        setBuffedValue(BuffStat.ComboAttack, 1);
        int duration = cEffect.getDuration();
        duration += (int) ((getBuffedStarttime(BuffStat.ComboAttack) - System.currentTimeMillis()));

        getClient().write(PacketCreator.GiveBuff(Crusader.斗气集中, duration, stat, false, false, getMount()));
        getMap().broadcastMessage(this, PacketCreator.BuffMapEffect(getId(), stat, false), false);
    }

    public boolean isLeader() {
        return (getParty().getLeader().equals(new MaplePartyCharacter(client.getPlayer())));
    }

    /**
     * only for tests
     *
     * @param newmap
     */
    public void setMap(Field newmap) {
        this.field = newmap;
    }

    public int getMapId() {
        if (field != null) {
            return field.getId();
        }
        return mapId;
    }

    public int getInitialSpawnpoint() {
        return savedSpawnPoint;
    }

    public Field getMap() {
        return field;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getWorldId() {
        return world;
    }

    public int getWorldRank() {
        return worldRanking;
    }

    public int getWorldRankChange() {
        return worldRankingChange;
    }

    public int getJobRank() {
        return jobRanking;
    }

    public int getJobRankChange() {
        return jobRankingChange;
    }

    public String getAccountName() {
        return client == null ? "" : client.getAccountName();
    }

    public int getFame() {
        return pop;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client c) {
        this.client = c;
    }

    public int getCurrentExp() {
        return exp.get();
    }

    public boolean isHidden() {
        return hidden;
    }

    public PlayerSkin getSkinColor() {
        return skin;
    }

    public PlayerJob getJob() {
        return job;
    }

    public int getGender() {
        return gender;
    }

    public int getHair() {
        return hair;
    }

    public int getFace() {
        return eyes;
    }

    public boolean getChangedTrockLocations() {
        return this.changedTrockLocations;
    }

    public boolean getChangedRegrockLocations() {
        return this.changedRegrockLocations;
    }

    public boolean getChangedSavedLocations() {
        return this.changedSavedLocations;
    }

    public boolean getChangedSkillMacros() {
        return this.changedSkillMacros;
    }

    public boolean getChangedReports() {
        return this.changedReports;
    }

    public void setChangedTrockLocations(boolean set) {
        this.changedTrockLocations = set;
    }

    public void setChangedRegrockLocations(boolean set) {
        this.changedRegrockLocations = set;
    }

    public void setChangedSavedLocations(boolean set) {
        this.changedSavedLocations = set;
    }

    public void setChangedSkillMacros(boolean set) {
        this.changedSkillMacros = set;
    }

    public void setChangedReports(boolean set) {
        this.changedReports = set;
    }

    public final boolean hasEquipped(int itemid) {
        return inventory[InventoryType.EQUIPPED.ordinal()].countById(itemid) >= 1;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setName(String name, boolean changeName) {
        if (!changeName) {
            this.name = name;
        } else {
            Connection con = Start.getInstance().getConnection();
            try {
                try (PreparedStatement sn = con.prepareStatement("UPDATE characters SET name = ? WHERE id = ?")) {
                    sn.setString(1, name);
                    sn.setInt(2, id);
                    sn.execute();
                    sn.close();
                    con.commit();
                }
                this.name = name;
            } catch (SQLException e) {
                System.err.println(e);
            } finally {
                try {
                    if (con != null && !con.isClosed()) {
                        con.close();
                    }
                } catch (SQLException e) {
                }
            }
        }
    }

    public void setExp(int exp) {
        this.exp.set(exp);
    }

    public void setHair(int hair) {
        this.hair = hair;
    }

    public void setFace(int face) {
        this.eyes = face;
    }

    public void setFame(int fame) {
        this.pop = fame;
    }

    public void setSkinColor(PlayerSkin skinColor) {
        this.skin = skinColor;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setGM(int gmlevel) {
        this.gm = gmlevel;
    }

    public CheatTracker getCheatTracker() {
        return antiCheat;
    }

    public MapleBuddyList getBuddylist() {
        return buddyList;
    }

    public void addPirateSummon(MapleSummon summon) {
        summon.lockSummon();
        try {
            pirateSummons.add(summon);
        } finally {
            summon.unlockSummon();
        }
    }

    public void removePirateSummon(MapleSummon summon) {
        summon.lockSummon();
        try {
            if (!pirateSummons.contains(summon)) {
                return;
            }
            pirateSummons.remove(pirateSummons.indexOf(summon));
        } finally {
            summon.unlockSummon();
        }
    }

    public void setChalkboard(String text) {
        this.chalkBoardText = text;
        if (chalkBoardText == null) {
            getMap().broadcastMessage(PacketCreator.UseChalkBoard(this, true));
        } else {
            getMap().broadcastMessage(PacketCreator.UseChalkBoard(this, false));
        }
    }

    public String getChalkboard() {
        return chalkBoardText;
    }

    public List<MapleSummon> getPirateSummons() {
        return pirateSummons;
    }

    public boolean hasPirateSummon(MapleSummon summon) {
        return pirateSummons.contains(summon);
    }

    public void addFame(int famechange) {
        this.pop += famechange;
    }

    public void removeItem(int id, int quantity) {
        InventoryManipulator.removeById(client, ItemConstants.getInventoryType(id), id, -quantity, true, false);
        client.write(PacketCreator.GetShowItemGain(id, (short) quantity, true));
    }

    public void removeAll(int id) {
        removeAll(id, true);
    }

    public void removeAll(int id, boolean show) {
        InventoryType type = ItemConstants.getInventoryType(id);
        int possessed = getInventory(type).countById(id);

        if (possessed > 0) {
            InventoryManipulator.removeById(getClient(), type, id, possessed, true, false);
            if (show) {
                getClient().write(PacketCreator.GetShowItemGain(id, (short) -possessed, true));
            }
        }
    }

    public void changeMap(int map) {
        Field warpMap;
        EventInstanceManager eim = getEventInstance();

        if (eim != null) {
            warpMap = eim.getMapInstance(map);
        } else {
            warpMap = client.getChannelServer().getMapFactory().getMap(map);
        }

        changeMap(warpMap, warpMap.getRandomPlayerSpawnpoint());
    }

    public void changeMap(int map, int portal) {

        Field warpMap;
        EventInstanceManager eim = getEventInstance();

        if (eim != null) {
            warpMap = eim.getMapInstance(map);
        } else {
            warpMap = client.getChannelServer().getMapFactory().getMap(map);
        }
        changeMap(warpMap, warpMap.getPortal(portal));
    }

    public void changeMap(int map, String portal) {
        Field warpMap;
        EventInstanceManager eim = getEventInstance();

        if (eim != null) {
            warpMap = eim.getMapInstance(map);
        } else {
            warpMap = client.getChannelServer().getMapFactory().getMap(map);
        }

        changeMap(warpMap, warpMap.getPortal(portal));
    }

    public void changeMap(int map, Portal portal) {
        Field warpMap;
        EventInstanceManager eim = getEventInstance();

        if (eim != null) {
            warpMap = eim.getMapInstance(map);
        } else {
            warpMap = client.getChannelServer().getMapFactory().getMap(map);
        }

        changeMap(warpMap, portal);
    }

    public void changeMap(Field to) {
        changeMap(to, to.getPortal(0));
    }

    public void changeMap(Field to, int portal) {
        changeMap(to, to.getPortal(portal));
    }

    public void changeMap(final Field target, Portal pto) {
        canWarpCounter++;
        //要进入地图
        eventChangedMap(target.getId());
        Field to = getWarpMap(target.getId());

        int fixedPortalId = target.getPortalIndex(pto.getName());
        if (getGM() > 0) {
            dropMessage("changeMap orgin id : " + pto.getId() + " fixed id: " + fixedPortalId);
        }
        Portal fixpto = target.getPortal(fixedPortalId);
        if (fixpto == null) {
            log.error("Field: " + target.getId() + " origin pto id: " + pto.getId() + " fixpto == null");
            fixpto = pto;
            fixedPortalId = pto.getId();
        }
        pto = fixpto;
        try {
            changeMapInternal(to, pto.getPosition(), PacketCreator.GetWarpToMap(to, fixedPortalId, this));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        canWarpMap = false;
        canWarpCounter--;
        if (canWarpCounter == 0) {
            canWarpMap = true;
        }

        eventAfterChangedMap(this.getMapId());
    }

    public void changeMap(final Field target, final Point pos) {
        canWarpCounter++;

        eventChangedMap(target.getId());
        Field to = getWarpMap(target.getId());
        try {
            changeMapInternal(to, pos, PacketCreator.GetWarpToMap(to, 0x80, this));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        canWarpMap = false;
        canWarpCounter--;
        if (canWarpCounter == 0) {
            canWarpMap = true;
        }

        eventAfterChangedMap(this.getMapId());
    }

    public void changeMapBanish(int mapid, String portal, String msg) {
        dropMessage(5, msg);
        Field map_ = client.getChannelServer().getMapFactory().getMap(mapid);
        changeMap(map_, map_.getPortal(portal));
    }

    private void changeMapInternal(final Field to, final Point pos, OutPacket warpPacket) {
        if (!canWarpMap || to == null) {
            return;
        }
        this.closePlayerInteractions();
        client.write(warpPacket);
        field.removePlayer(this);

        if (getChannelServer().getPlayerStorage().getCharacterById(getId()) != null) {
            field = to;
            setPosition(pos);
            to.addPlayer(this);

            stats.recalcLocalStats();

            if (party != null) {
                mpc.setMapId(to.getId());
                silentPartyUpdate();
                client.announce(PartyPackets.UpdateParty(client.getChannel(), party, MaplePartyOperation.SILENT_UPDATE, null));
                updatePartyMemberHP();
            }
            for (final ItemPet pet : getPets()) {
                if (pet.getSummoned()) {
                    updatePetAuto();
                }
            }
        } else {
            FileLogger.printError(FileLogger.FIELD, "Character " + this.getName() + " got stuck when moving to map " + field.getId() + ".");
        }
        if (newWarpMap != -1) {
            canWarpMap = true;

            int temp = newWarpMap;
            newWarpMap = -1;
            changeMap(temp);
        } else {
            EventInstanceManager eim = getEventInstance();
            if (eim != null) {
                eim.recoverOpenedGate(this, field.getId());
            }
        }
        client.write(PacketCreator.EnableActions());
    }

    public void closePlayerInteractions() {
        closeNpcShop();
        closeTrade();
        closePlayerShop();
        closeMiniGame();
        closeMessenger();
    }

    public void closeNpcShop() {
        setShop(null);
    }

    public void closeTrade() {
        if (getTrade() != null) {
            Trade.cancelTrade(getTrade(), this);
        }
    }

    public void closeMessenger() {
        if (getMessenger() != null) {
            MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(this);
            MessengerService.leaveMessenger(this, getMessenger().getId(), messengerplayer);
        }
    }

    public void setMapId(int mapid) {
        this.mapId = mapid;
    }

    public final Map<Integer, PlayerKeyBinding> getKeyLayout() {
        return this.keymap;
    }

    public int getRingRequested() {
        return this.ringRequest;
    }

    public void setRingRequested(int set) {
        ringRequest = set;
    }

    public void leaveMap(Field map) {
        visibleMapObjectsLock.writeLock().lock();
        controlledLock.writeLock().lock();
        try {
            for (MapleMonster mons : controlled) {
                if (mons != null) {
                    mons.setController(null);
                    mons.setControllerHasAggro(false);
                    mons.setControllerKnowsAboutAggro(false);
                    map.updateMonsterController(mons);
                }
            }
            controlled.clear();
            visibleMapObjects.clear();
        } finally {
            visibleMapObjectsLock.writeLock().unlock();
            controlledLock.writeLock().unlock();
        }
        if (chair != 0) {
            chair = 0;
        }
    }

    /**
     * <转职>
     */
    public void changeJob(PlayerJob newJob) {
        if (newJob == null) {
            return;
        }
        try {
            this.job = newJob;
            stats.remainingSp += 1;
            if (newJob.getId() % 10 == 2) {
                stats.remainingSp += 2;
            }

            /*stats.updateSingleStat(PlayerStat.AVAILABLESP, stats.getRemainingSp());
            stats.updateSingleStat(PlayerStat.JOB, newJob.getId());

            int maxHP = stats.getMaxHp();
            int maxMP = stats.getMaxMp();

            if (job.getId() == 100) {
                maxHP += Randomizer.rand(200, 250);
            } else if (job.getId() == 200) {
                maxMP += Randomizer.rand(100, 150);
            } else if (job.getId() % 100 == 0) {
                maxHP += Randomizer.rand(100, 150);
                maxHP += Randomizer.rand(25, 50);
            } else if (job.getId() > 0 && job.getId() < 200) {
                maxHP += Randomizer.rand(300, 350);
            } else if (job.getId() < 300) {
                maxMP += Randomizer.rand(450, 500);
            } else if (job.getId() > 0) {
                maxHP += Randomizer.rand(300, 350);
                maxMP += Randomizer.rand(150, 200);
            }
            if (maxHP >= 30000) {
                maxHP = 30000;
            }
            if (maxMP >= 30000) {
                maxMP = 30000;
            }

            stats.hp = maxHP;
            stats.mp = maxMP;*/
            int mask = PlayerStat.HP.getValue() | PlayerStat.MP.getValue() | PlayerStat.MAXHP.getValue() | PlayerStat.MAXMP.getValue() | PlayerStat.AVAILABLEAP.getValue() | PlayerStat.AVAILABLESP.getValue() | PlayerStat.JOB.getValue();
            getClient().write(PacketCreator.UpdatePlayerStats(this, mask));

            stats.recalcLocalStats();
            getMap().broadcastMessage(this, PacketCreator.ShowThirdPersonEffect(getId(), PlayerEffects.JOB_ADVANCEMENT.getEffect()), false);

            if (!isGameMaster() && GameConstants.SHOW_JOB_UPDATE) {
                broadcastChangeJob(SkillConstants.getJobNameById(getJob().getId()), PlayerJob.getAdvancement(getJob().getId()));
            }
            if (getParty() != null) {
                silentPartyUpdate();
            }

        } catch (Exception ex) {
            FileLogger.printError(FileLogger.PLAYER_STUCK, ex);
        }
        saveDatabase();
    }

    public void changeSkillLevel(PlayerSkill skill, int newLevel, int newMasterlevel) {
        skills.put(skill, new PlayerSkillEntry(newLevel, newMasterlevel));
        this.getClient().write(PacketCreator.UpdateSkill(skill.getId(), newLevel, newMasterlevel));
    }

    public void cancelAllBuffs() {
        List<BuffStatValueHolder> stats;
        effectLock.readLock().lock();
        try {
            stats = new ArrayList<>(effects.values());
        } finally {
            effectLock.readLock().unlock();
        }
        for (BuffStatValueHolder mbsvh : stats) {
            cancelEffect(mbsvh.effect, false, mbsvh.startTime);
        }
    }

    public void 增加死亡次数() {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET death = death + 1  WHERE id =" + id + "")) {
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            Database.cleanUP(null, null, con);
        }
    }

    /**
     * <角色死亡>
     */
    public void playerDead() {
        增加死亡次数();
        if (getEventInstance() != null) {
            getEventInstance().playerKilled(this);
        }

        dispelSkill();
        cancelMorphs();
        cancelAllBuffs(false);
        dispelDebuffs();

        int possesed = 0;
        int i;
        for (i = 0; i < ItemConstants.CHARM_ITEM.length; i++) {
            int quantity = getItemQuantity(ItemConstants.CHARM_ITEM[i], false);
            if (possesed == 0 && quantity > 0) {
                possesed = quantity;
                break;
            }
        }

        if (possesed > 0) {
            possesed -= 1;
            getClient().write(EffectPackets.SelfCharmEffect((short) Math.min(0xFF, possesed), (short) 90));
            dropMessage(5, "角色死亡，由于你有护身符，不损失损失经验。");
            InventoryManipulator.removeById(getClient(), ItemInformationProvider.getInstance().getInventoryType(ItemConstants.CHARM_ITEM[i]), ItemConstants.CHARM_ITEM[i], 1, true, false);
        } else {
            if (this.getJob() != PlayerJob.BEGINNER) {
                int diePercentage = ExperienceConstants.getExpNeededForLevel(this.getLevel() + 1);
                if (this.getMap().isTown() || FieldLimit.REGULAREXPLOSS.check(field.getFieldLimit())) {
                    diePercentage *= 0.01;
                } else if (MonsterCarnival.isBattlefieldMap(this.getMapId())) {
                    diePercentage = 0;
                }
                if (diePercentage == ExperienceConstants.getExpNeededForLevel(this.getLevel() + 1)) {
                    if (stats.getLuk() <= 100 && stats.getLuk() > 8) {
                        diePercentage *= 0.10 - (stats.getLuk() * 0.0005);
                    } else if (stats.getLuk() < 8) {
                        diePercentage *= 0.10;
                    } else {
                        diePercentage *= 0.10 - (100 * 0.0005);
                    }
                }
                //死亡扣除经验
                if ((getCurrentExp() - diePercentage) > 0) {
                    if (枫叶套() >= 1) {
                        setExp((int) (getCurrentExp() - diePercentage * 0.5));
                        stats.updateSingleStat(PlayerStat.EXP, (int) (getCurrentExp() - diePercentage * 0.5));
                        dropMessage(5, "角色死亡，由于你没有护身符，但是你有特殊的保护道具，损失 " + (diePercentage * 0.5) + " 点经验。");
                    } else {
                        setExp(getCurrentExp() - diePercentage);
                        stats.updateSingleStat(PlayerStat.EXP, getCurrentExp() - diePercentage);
                        dropMessage(5, "角色死亡，由于你没有护身符，损失 " + diePercentage + " 点经验。");
                    }
                } else {
                    setExp(0);
                    stats.updateSingleStat(PlayerStat.EXP, 0);
                    dropMessage(5, "角色死亡，由于你没有护身符，损失 " + diePercentage + " 点经验。");
                }
            }
        }
    }

    public void updatePartyMemberHP() {
        if (party != null) {
            int channel = client.getChannel();
            for (MaplePartyCharacter partychar : party.getMembers()) {
                if (partychar.getMapId() == getMapId() && partychar.getChannel() == channel) {
                    Player other = getChannelServer().getPlayerStorage().getCharacterByName(partychar.getName());
                    if (other != null) {
                        other.getClient().write(PartyPackets.UpdatePartyMemberHP(getId(), stats.getHp(), stats.getCurrentMaxHp()));
                    }
                }
            }
        }
    }

    public void receivePartyMemberHP() {
        if (party != null) {
            int channel = client.getChannel();
            for (MaplePartyCharacter partychar : party.getMembers()) {
                if (partychar.getMapId() == getMapId() && partychar.getChannel() == channel) {
                    Player other = getChannelServer().getPlayerStorage().getCharacterByName(partychar.getName());
                    if (other != null) {
                        client.announce(PartyPackets.UpdatePartyMemberHP(other.getId(), other.getStat().getHp(), other.getStat().getCurrentMaxHp()));
                    }
                }
            }
        }
    }

    public final PlayerStatsManager getStat() {
        return stats;
    }

    /**
     * <物理攻击力>
     */
    public int getWatk() {
        return getStat().getTotalWatk();
    }

    /**
     * <物理输出>
     */
    public int getCurrentMaxBaseDamage() {
        return getStat().getCurrentMaxBaseDamage();
    }

    /**
     * <魔法攻击力>
     */
    public int getMagic() {
        return getStat().getTotalMagic();
    }

    /**
     * <当前法力>
     */
    public int getMp() {
        return getStat().getMp();
    }

    /**
     * <最大法力>
     */
    public int getMaxMp() {
        return getStat().getMaxMp();
    }

    /**
     * <当前生命>
     */
    public int getHp() {
        return getStat().getHp();
    }

    /**
     * <最大生命>
     */
    public int getMaxHp() {
        return getStat().getMaxHp();
    }

    /**
     * <力量>
     */
    public int getStr() {
        return getStat().getStr();
    }

    public int getTotalStr() {
        return getStat().getTotalStr();
    }

    /**
     * <敏捷>
     */
    public int getDex() {
        return getStat().getDex();
    }

    public int getTotalDex() {
        return getStat().getTotalDex();
    }

    /**
     * <智力>
     */
    public int getInt() {
        return getStat().getInt();
    }

    public int getTotalInt() {
        return getStat().getTotalInt();
    }

    /**
     * <运气>
     */
    public int getLuk() {
        return getStat().getLuk();
    }

    public int getTotalLuk() {
        return getStat().getTotalLuk();
    }

    public int getRemainingAp() {
        return getStat().getRemainingAp();
    }

    public int getRemainingSp() {
        return getStat().getRemainingSp();
    }

    public boolean haveItem(int itemid) {
        return haveItem(itemid, 1, false, true);
    }

    public boolean haveItemEquiped(int itemid) {
        return haveItem(itemid, 1, true, false);
    }

    public int hasEXPCard() {
        Inventory iv = getInventory(InventoryType.ETC);
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        for (Integer id : ItemConstants.EXP_CARDS) {
            if (iv.countById(id) > 0) {
                if (ii.isExpOrDropCardTime(id)) {
                    return 2;
                }
            }
        }
        return 1;
    }

    public int hasDropCard() {
        Inventory iv = getInventory(InventoryType.ETC);
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        for (Integer id : ItemConstants.DROP_CARDS) {
            if (iv.countById(id) > 0) {
                if (ii.isExpOrDropCardTime(id)) {
                    return 2;
                }
            }
        }
        return 1;
    }

    public void incrementMonsterKills() {
        if (GameConstants.THIRD_KILL_EVENT) {
            mobKills.incrementAndGet();
        }
    }

    public int getThirdKillPercentage(int hours) {
        int mod = 0;
        if (GameConstants.THIRD_KILL_EVENT) {
            switch (hours) {
                case 0:
                    mod = 0;
                    break;
                case 1:
                    mod = 30;
                    break;
                case 2:
                    mod = 100;
                    break;
                case 3:
                    mod = 150;
                    break;
                case 4:
                    mod = 180;
                    break;
                default:
                    mod = 200;
                    break;
            }
        }
        return mod;
    }

    public int getMonsterkills() {
        return mobKills.get();
    }

    public void resetMonsterkills() {
        mobKills.set(0);
    }

    public void gainExpRiche() {
        int gainFirst = ExperienceConstants.getExpNeededForLevel(level);
        double realgain = gainFirst * 0.05;
        this.gainExp((int) realgain, 0, true, false, true);
    }

    public void gainExperience(int gain, boolean show, boolean inChat) {
        gainExp(gain, show, inChat);
    }

    public void gainExp(int gain) {
        gainExp(gain, true, true);
    }

    public void gainExp(int gain, boolean show, boolean inChat) {
        gainExp(gain, show, inChat, true);
    }

    public void gainExp(int gain, boolean show, boolean inChat, boolean white) {
        gainExp(gain, 0, show, inChat, white);
    }

    public void gainExp(int gain, int party, boolean show, boolean inChat, boolean white) {
        if (hasDisease(Disease.CURSE)) {
            gain *= 0.5;
            party *= 0.5;
        }

        if (gain < 0) {
            gain = Integer.MAX_VALUE;
        }
        if (party < 0) {
            party = Integer.MAX_VALUE;
        }
        incrementMonsterKills();
        int total = gain + party;
        gainExpInternal(total, party, show, inChat, white);
    }

    public void 杀怪任务计数() {
        杀怪任务计数.put(id, 获取杀怪数量() + 1);
    }

    public void 杀怪任务完成() {
        杀怪任务计数.remove(id);
    }

    public int 获取杀怪数量() {
        int x = 0;
        if (杀怪任务计数.containsKey(id)) {
            return 杀怪任务计数.get(id);
        }
        return x;
    }

    public void gainExpInternal(int gain, int party, boolean show, boolean inChat, boolean white) {
        /*if (获取杀怪数量() > 0 && 获取杀怪数量() <= 999) {
            杀怪任务计数();
        }*/

        if (level < getMaxlevel()) {
            if ((long) this.exp.get() + (long) gain > (long) Integer.MAX_VALUE) {
                int gainFirst = ExperienceConstants.getExpNeededForLevel(level) - this.exp.get();
                gain -= gainFirst + 1;
                this.gainExp(gainFirst + 1, false, inChat, white);
            }
            stats.updateSingleStat(PlayerStat.EXP, this.exp.addAndGet(gain), white);
            if (show && gain != 0) {
                client.announce(PacketCreator.GetShowExpGain(gain, party != 0 ? (party - 100) : 0, /*thirdKillBonusPercentage, hours,*/ inChat, white));
            }
            if (gm > 0) {
                while (exp.get() >= ExperienceConstants.getExpNeededForLevel(level)) {
                    levelUp(true);
                }
            } else if (exp.get() >= ExperienceConstants.getExpNeededForLevel(level)) {
                levelUp(true);
                int need = ExperienceConstants.getExpNeededForLevel(level);
                if (exp.get() >= need) {
                    setExp(need - 1);
                    stats.updateSingleStat(PlayerStat.EXP, need, white);
                }
            }
        }
    }

    public void energyChargeGain() {
    }

    public int getEnergy() {
        return energyBar;
    }

    public void gainEnergy(int gain) {
        energyBar += gain;
    }

    public void setEnergyBar(int set) {
        energyBar = set;
    }

    public void setWorld(int world) {
        this.world = world;
    }

    public void setWorld2(int world) {
        this.world2 = world;
    }

    public void silentPartyUpdate() {
        mpc = new MaplePartyCharacter(this);
        if (party != null) {
            PartyService.updateParty(this, party.getId(), MaplePartyOperation.SILENT_UPDATE, getMPC());
        }
    }

    public MaplePartyCharacter getMPC() {
        if (mpc == null) {
            mpc = new MaplePartyCharacter(this);
        }
        return mpc;
    }

    public boolean isGameMaster() {
        return gm > 1;
    }

    public int getGM() {
        return gm;
    }

    public int getAdministrativeLevel() {
        return gm;
    }

    public boolean hasGmLevel(int level) {
        return gm >= level;
    }

    public Inventory getInventory(InventoryType type) {
        return inventory[type.ordinal()];
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public int getMeso() {
        return meso.get();
    }

    public int getSavedLocation(SavedLocationType type) {
        return savedLocations[type.ordinal()];
    }

    public int getSavedLocation(String type) {
        return savedLocations[SavedLocationType.fromString(type).ordinal()];
    }

    public void saveLocation(SavedLocationType type) {
        savedLocations[type.ordinal()] = getMapId();
        changedSavedLocations = true;
    }

    public void saveLocation(String type) {
        savedLocations[SavedLocationType.fromString(type).ordinal()] = getMapId();
        changedSavedLocations = true;
    }

    public void clearSavedLocation(SavedLocationType type) {
        savedLocations[type.ordinal()] = -1;
        changedSavedLocations = true;
    }

    public void clearSavedLocation(String type) {
        savedLocations[SavedLocationType.fromString(type).ordinal()] = -1;
        changedSavedLocations = true;
    }

    public void gainMeso(int gain, boolean show) {
        gainMeso(gain, show, false, false);
    }

    public void gainMeso(int gain, boolean show, boolean enableActions) {
        gainMeso(gain, show, enableActions, false);
    }

    public void gainMeso(int gain, boolean show, boolean enableActions, boolean inChat) {
        if (meso.get() + gain < 0) {
            client.write(PacketCreator.EnableActions());
            return;
        }
        int newVal = meso.addAndGet(gain);
        stats.updateSingleStat(PlayerStat.MESO, newVal, enableActions);
        if (show) {
            client.write(PacketCreator.GetShowMesoGain(gain, inChat));
        }
    }

    public void setbosslog(String a) {
        增加每日(a);
    }

    public int getbosslog(String a) {
        return 判断每日(a);
    }

    /**
     * Adds this monster to the controlled list. The monster must exist on the
     * Map.
     *
     * @param monster
     * @param aggro
     */
    public void controlMonster(MapleMonster monster, boolean aggro) {
        monster.setController(this);
        this.controlledLock.writeLock().lock();
        try {
            controlled.add(monster);
        } finally {
            this.controlledLock.writeLock().unlock();
        }
        client.write(MonsterPackets.ControlMonster(monster, false, aggro));
    }

    public void uncontrolMonster(MapleMonster monster) {
        this.controlledLock.writeLock().lock();
        try {
            controlled.remove(monster);
        } finally {
            this.controlledLock.writeLock().unlock();
        }
    }

    public void checkMonsterAggro(MapleMonster monster) {
        if (!monster.controllerHasAggro()) {
            if (monster.getController() == this) {
                monster.setControllerHasAggro(true);
            } else {
                monster.switchController(this, true);
            }
        }
    }

    public Collection<MapleMonster> getControlledMonsters() {
        return Collections.unmodifiableCollection(controlled);
    }

    public int getNumControlledMonsters() {
        return controlled.size();
    }

    public Set<MapleMonster> getControlled() {
        return controlled;
    }

    @Override
    public String toString() {
        return "Character: " + this.name;
    }

    public int getAccountID() {
        return accountid;
    }

    public int getjiazu() {
        return jiazu;
    }

    public void setjiazu(int jiazu) {
        this.jiazu = jiazu;
    }

    public void setworld(int world) {
        this.world = world;
    }

    public void setvip(int vip) {
        this.vip = vip;
    }

    public int getvip() {
        return vip;
    }

    public void setQQ(String QQ) {
        this.QQ = QQ;
    }

    public String getQQ() {
        return QQ;
    }

    public void setTX(String TX) {
        this.群头衔 = TX;
    }

    public String getTX() {
        return 群头衔;
    }

    public void setSJ(String SJ) {
        this.SJ = SJ;
    }

    public String getSJ() {
        return SJ;
    }

    public void dispelDebuff(Disease debuff) {
        if (hasDisease(debuff)) {
            int mask = debuff.getValue();

            this.announce(PacketCreator.CancelDebuff(mask));
            field.broadcastMessage(this, PacketCreator.CancelForeignDebuff(id, mask), false);

            diseases.remove(debuff);
        }
    }

    public void dispelDebuffs() {
        diseases.keySet().stream().forEach((d) -> {
            dispelDebuff(d);
        });
    }

    /**
     * <死亡自杀>
     */
    public void kill() {
        stats.setHp(0);
        stats.setMp(0);
        stats.updateSingleStat(PlayerStat.HP, 0);
        stats.updateSingleStat(PlayerStat.MP, 0);
    }

    public void updateQuestMobCount(int id) {
        int lastQuestProcessed = 0;
        try {
            synchronized (quests) {
                for (MapleQuestStatus q : quests.values()) {
                    lastQuestProcessed = q.getQuest().getId();
                    if (q.getStatus() == MapleQuestStatus.Status.COMPLETED || q.getQuest().canComplete(this, null)) {
                        continue;
                    }
                    String progress = q.getProgress(id);
                    if (!progress.isEmpty() && Integer.parseInt(progress) >= q.getQuest().getMobAmountNeeded(id)) {
                        continue;
                    }
                    if (q.progress(id)) {
                        client.announce(PacketCreator.UpdateQuest(q, false));
                    }
                }
            }
        } catch (NumberFormatException e) {
            FileLogger.printError(FileLogger.EXCEPTION_CAUGHT, e, "MapleCharacter.mobKilled. CID: " + this.id + " last Quest Processed: " + lastQuestProcessed);
        }
    }

    public final byte getQuestStatus(final int quest) {
        synchronized (quests) {
            for (final MapleQuestStatus q : quests.values()) {
                if (q.getQuest().getId() == quest) {
                    return (byte) q.getStatus().getId();
                }
            }
            return 0;
        }
    }

    public final MapleQuestStatus getMapleQuestStatus(final int quest) {
        synchronized (quests) {
            for (final MapleQuestStatus q : quests.values()) {
                if (q.getQuest().getId() == quest) {
                    return q;
                }
            }
            return null;
        }
    }

    public MapleQuestStatus getQuest(MapleQuest quest) {
        synchronized (quests) {
            if (!quests.containsKey(quest.getId())) {
                return new MapleQuestStatus(quest, MapleQuestStatus.Status.NOT_STARTED);
            }
            return quests.get(quest.getId());
        }
    }

    public boolean needQuestItem(int questid, int itemid) {
        if (questid <= 0) {
            return true;
        }
        MapleQuest quest = MapleQuest.getInstance(questid);
        return getInventory(ItemConstants.getInventoryType(itemid)).countById(itemid) < quest.getItemAmountNeeded(itemid);
    }

    public final List<MapleQuestStatus> getStartedQuests() {
        List<MapleQuestStatus> ret = new LinkedList<>();
        for (MapleQuestStatus q : quests.values()) {
            if (q.getStatus().equals(MapleQuestStatus.Status.STARTED)) {
                ret.add(q);
            }
        }
        return Collections.unmodifiableList(ret);
    }

    public final int getStartedQuestsSize() {
        synchronized (quests) {
            int i = 0;
            for (MapleQuestStatus q : quests.values()) {
                if (q.getStatus().equals(MapleQuestStatus.Status.STARTED)) {
                    if (q.getQuest().getInfoNumber() > 0) {
                        i++;
                    }
                    i++;
                }
            }
            return i;
        }
    }

    public final List<MapleQuestStatus> getCompletedQuests() {
        synchronized (quests) {
            List<MapleQuestStatus> ret = new LinkedList<>();
            for (MapleQuestStatus q : quests.values()) {
                if (q.getStatus().equals(MapleQuestStatus.Status.COMPLETED)) {
                    ret.add(q);
                }
            }

            return Collections.unmodifiableList(ret);
        }
    }

    public PlayerShop getPlayerShop() {
        return playerShop;
    }

    public void setPlayerShop(PlayerShop playerShop) {
        this.playerShop = playerShop;
    }

    public Map<PlayerSkill, PlayerSkillEntry> getSkills() {
        return Collections.unmodifiableMap(skills);
    }

    public void removeBuffs() {
        List<BuffStatValueHolder> stats;
        effectLock.readLock().lock();
        try {
            stats = new ArrayList<>(effects.values());
        } finally {
            effectLock.readLock().unlock();
        }
        for (BuffStatValueHolder mbsvh : stats) {
            cancelEffect(mbsvh.effect, false, mbsvh.startTime);
        }
    }

    public void dispelSkill() {
        dispelSkill(0);
    }

    public void dispelSkill(int skillId) {
        List<BuffStatValueHolder> stats;
        effectLock.readLock().lock();
        try {
            stats = new ArrayList<>(effects.values());
        } finally {
            effectLock.readLock().unlock();
        }
        for (BuffStatValueHolder mbsvh : stats) {
            if (skillId == 0) {
                if (mbsvh.effect.isSkill()) {
                    switch (mbsvh.effect.getSourceId()) {
                        case Beginner.MonsterRider:
                        case DarkKnight.Beholder:
                        case FPArchMage.Elquines:
                        case ILArchMage.Ifrit:
                        case Priest.SummonDragon:
                        case Bishop.Bahamut:
                        case Ranger.Puppet:
                        case Ranger.SilverHawk:
                        case Sniper.Puppet:
                        case Sniper.GoldenEagle:
                        case Hermit.ShadowPartner:
                            cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                            break;
                    }
                }
            } else {
                if (mbsvh.effect.isSkill() && mbsvh.effect.getSourceId() == skillId) {
                    cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                    break;
                }
            }
        }
    }

    public int getSkillLevel(PlayerSkill skill) {
        PlayerSkillEntry ret = skills.get(skill);
        if (ret == null) {
            return 0;
        }
        return ret.skillevel;
    }

    public int getSkillLevel(int skill) {
        PlayerSkillEntry ret = skills.get(PlayerSkillFactory.getSkill(skill));
        if (ret == null) {
            return 0;
        }
        return ret.skillevel;
    }

    public int getMasterLevel(PlayerSkill skill) {
        PlayerSkillEntry ret = skills.get(skill);
        if (ret == null) {
            return 0;
        }
        return ret.masterlevel;
    }

    public boolean isRingEquipped(int ringId) {
        for (Item item : getInventory(InventoryType.EQUIPPED)) {
            Equip equip = (Equip) item;
            if (equip.getRing().getRingDatabaseId() == ringId) {
                return equip.getPosition() <= (byte) -1;
            }
        }
        return false;
    }

    public int getEquippedRing(int type) {
        for (Item item : getInventory(InventoryType.EQUIPPED)) {
            Equip equip = (Equip) item;
            if (equip.getRing() != null) {
                int itemId = equip.getItemId();
                if (ItemConstants.isCrushRing(itemId) && type == ItemRingType.CRUSH_RING.getType()) {
                    return equip.getRing().getRingDatabaseId();
                }
                if (ItemConstants.isFriendshipRing(itemId) && type == ItemRingType.FRIENDSHIP_RING.getType()) {
                    return equip.getRing().getRingDatabaseId();
                }
                if (ItemConstants.isWeddingRing(itemId) && type == ItemRingType.WEDDING_RING.getType()) {
                    return equip.getRing().getRingDatabaseId();
                }
            }
        }
        return 0;
    }

    public List<ItemRing> getCrushRings() {
        Collections.sort(crushRings);
        return crushRings;
    }

    public List<ItemRing> getFriendshipRings() {
        Collections.sort(friendshipRings);
        return friendshipRings;
    }

    public List<ItemRing> getWeddingRings() {
        Collections.sort(weddingRings);
        return weddingRings;
    }

    public void addRingToCache(int ringId) {
        ItemRing ring = ItemRing.loadingRing(ringId);
        if (ring != null) {
            if (ItemConstants.isCrushRing(ring.getItemId())) {
                crushRings.add(ring);
            } else if (ItemConstants.isFriendshipRing(ring.getItemId())) {
                friendshipRings.add(ring);
            } else if (ItemConstants.isWeddingRing(ring.getItemId())) {
                weddingRings.add(ring);
            }
        }
    }

    /**
     * <角色升级>
     */
    public void levelUp(boolean takeExp) {
        PlayerSkill improvingMaxHP = null;
        PlayerSkill improvingMaxMP = null;

        int improvingMaxHPLevel = 0;
        int improvingMaxMPLevel = 0;

        stats.remainingAp += 5;

        int maxHP = stats.maxHP;
        int maxMP = stats.maxMP;

        if (job == PlayerJob.BEGINNER) {
            maxHP += Randomizer.rand(12, 16);
            maxMP += Randomizer.rand(10, 12);
        } else if (job.isA(PlayerJob.WARRIOR)) {
            improvingMaxHP = PlayerSkillFactory.getSkill(Swordman.ImprovedMaxHpIncrease);
            improvingMaxHPLevel = getSkillLevel(improvingMaxHP);
            maxHP += Randomizer.rand(24, 28);
            maxMP += Randomizer.rand(4, 6);
            stats.remainingSp += 3;
        } else if (job.isA(PlayerJob.MAGICIAN)) {
            improvingMaxMP = PlayerSkillFactory.getSkill(Magician.ImprovedMaxMpIncrease);
            improvingMaxMPLevel = getSkillLevel(improvingMaxMP);
            maxHP += Randomizer.rand(10, 14);
            maxMP += Randomizer.rand(22, 24);
            stats.remainingSp += 3;
        } else if (job.isA(PlayerJob.BOWMAN) || job.isA(PlayerJob.THIEF) || job.isA(PlayerJob.GM)) {
            maxHP += Randomizer.rand(20, 24);
            maxMP += Randomizer.rand(14, 16);
            stats.remainingSp += 3;
        } else if (job.isA(PlayerJob.PIRATE)) {
            improvingMaxHP = PlayerSkillFactory.getSkill(Brawler.ImproveMaxHp);
            improvingMaxHPLevel = getSkillLevel(improvingMaxHP);
            maxHP += Randomizer.rand(22, 28);
            maxMP += Randomizer.rand(18, 23);
            stats.remainingSp += 3;
        }
        if (improvingMaxHPLevel > 0) {
            if (improvingMaxHP != null) {
                maxHP += improvingMaxHP.getEffect(improvingMaxHPLevel).getX();
            }
        }
        if (improvingMaxMPLevel > 0) {
            if (improvingMaxMP != null) {
                maxMP += improvingMaxMP.getEffect(improvingMaxMPLevel).getX();
            }
        }

        maxMP += stats.getTotalInt() / 10;
        if (takeExp) {
            exp.addAndGet(-ExperienceConstants.getExpNeededForLevel(level));
            if (exp.get() < 0) {
                exp.set(0);
            }
        }

        if (level > 10) {
            //茁壮成长
            if (getEquippedFuMoMap().get(20) != null) {
                if (getEquippedFuMoMap().get(20) > 50) {
                    maxHP += 50;
                    dropMessage(5, "额外成长生命+50");
                } else {
                    maxHP += getEquippedFuMoMap().get(20);
                    dropMessage(5, "额外成长生命+" + getEquippedFuMoMap().get(20) + "");
                }
            }
            //茁壮生长
            if (getEquippedFuMoMap().get(21) != null) {
                if (getEquippedFuMoMap().get(21) > 50) {
                    maxMP += 50;
                    dropMessage(5, "额外成长法力+50");
                } else {
                    maxMP += getEquippedFuMoMap().get(21);
                    dropMessage(5, "额外成长法力+" + getEquippedFuMoMap().get(21) + "");
                }
            }
        }

        level++;

        if (level >= 200) {
            exp.set(0);
            level = 200;
        }

        stats.maxHP = (Math.min(30000, maxHP));
        stats.maxMP = (Math.min(30000, maxMP));

        if (level == 200) {
            exp.set(0);
        }

        stats.recalcLocalStats();

        stats.hp = maxHP;
        stats.mp = maxMP;

        int mask = PlayerStat.AVAILABLEAP.getValue() | PlayerStat.AVAILABLESP.getValue() | PlayerStat.HP.getValue() | PlayerStat.MP.getValue() | PlayerStat.MAXHP.getValue() | PlayerStat.MAXMP.getValue() | PlayerStat.EXP.getValue() | PlayerStat.LEVEL.getValue();
        this.announce(PacketCreator.UpdatePlayerStats(this, mask));
        getMap().broadcastMessage(this, PacketCreator.ShowThirdPersonEffect(getId(), PlayerEffects.LEVEL_UP.getEffect()), false);

        stats.recalcLocalStats();

        if (getParty() != null) {
            silentPartyUpdate();
        }

        //记录升级
        setLevelUpHistory(this, level);
        if (gm == 0) {
            String txtx = "";
            String 升级时间 = 升级时间(level);
            if (升级时间 != null) {
                txtx = "从 " + (level - 1) + " 级到 " + level + " 一共花了 " + 升级时间 + "，";
            }

            for (ChannelServer cserv : getWorld().getChannels()) {
                for (Player chr : cserv.getPlayerStorage().getAllCharacters()) {
                    if (chr == null) {
                        continue;
                    }
                    if (chr.getjiazu() > 0) {
                        if (chr.getjiazu() == getjiazu() && chr.getId() != getId()) {
                            chr.dropMessage(5, "[系统公告]：家族成员 " + getName() + " 等级达到 " + level + " 级了，" + txtx + "大家恭喜他/她吧。");
                        }
                    }
                }
            }

            if (level >= 10) {
                String txt = "恭喜玩家 " + getName() + " 在 " + getMapName(getMapId()) + " 等级提升至 " + level + " 级，" + txtx + "大家恭喜他/她吧。";
                群("[系统公告]：" + txt, 大区群号1(getWorldId()));
            }
        }
        if (level == 8) {
            dropMessage(5, "恭喜你等级达到了8级，可以在魔法密林转职成为一名魔法师。");
        }

        if (level == 10 && job.getId() == 0) {
            dropMessage(5, "恭喜你等级达到了10级，可以在射手村，勇士部落，废弃都市转职。");
        }

        if (level == 30) {
            dropMessage(5, "恭喜你可以进行第二次转职。");
        }

        if (level == 70) {
            dropMessage(5, "恭喜你可以进行第三次转职。");
        }

        if (level == 100) {
            dropMessage(5, "恭喜你的等级已经达到满级,请通过职业导师突破上限。");
        }

        if (getjiazu() > 0) {
            int 随机 = (int) Math.ceil(Math.random() * 10);
            增加家族经验(随机);
            dropMessage(5, "家族经验 + " + 随机 + "");
        }
        if (level >= 10 && level <= 70) {
            if (师傅() > 0) {
                if (level == 70) {
                    拜师(-师傅() * 2);
                }
                增加师傅声望(level);
                dropMessage(5, "你的师傅声望 + " + level + "");
            }
        }
        saveDatabase();
    }

    public World getWorld() {
        return Start.getInstance().getWorldById(getWorldId());
    }

    public void setLevelUpHistory(Player p, int level) {
        if (this.isGameMaster()) {
            return;
        }
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO levelhistory (accountid, characterid, level, date,time) VALUES (?, ?, ?, ?, ?)")) {
                ps.setInt(1, this.accountid);
                ps.setInt(2, this.id);
                ps.setInt(3, level);
                ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                ps.setLong(5, System.currentTimeMillis());
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("setLevelUpHistory、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
    }

    public String 升级时间(int level) {
        long data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT time as DATA FROM levelhistory WHERE characterid = ? && level = ?");
            ps.setInt(1, this.id);
            ps.setInt(2, (level - 1));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getLong("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("取升级间隔时间、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        if (data > 0) {
            return secondToTime((System.currentTimeMillis() - data) / 1000);
        }
        return null;
    }

    public void resetBattleshipHp() {
        this.battleShipHP = 4000 * getSkillLevel(PlayerSkillFactory.getSkill(Corsair.Battleship)) + ((getLevel() - 120) * 2000);
    }

    public int getCurrentBattleShipHP() {
        return battleShipHP;
    }

    public final void sendBattleshipHP(int damage) {
        this.battleShipHP -= damage;
        if (battleShipHP <= 0) {
            this.battleShipHP = 0;
            PlayerSkill battleship = PlayerSkillFactory.getSkill(Corsair.Battleship);
            int cooldown = battleship.getEffect(getSkillLevel(battleship)).getCoolDown();
            client.write(PacketCreator.SkillCooldown(Corsair.Battleship, cooldown));
            addCoolDown(Corsair.Battleship, System.currentTimeMillis(), cooldown * 1000);
            dispelSkill(Corsair.Battleship);
        }
    }

    public void changeKeybinding(int key, PlayerKeyBinding keybinding) {
        if (keybinding.getType() != 0) {
            keymap.put(key, keybinding);
        } else {
            keymap.remove(key);
        }
    }

    public Field getWarpMap(int map) {
        Field target;
        EventInstanceManager eim = getEventInstance();
        if (eim == null) {
            target = client.getChannelServer().getMapFactory().getMap(map);
        } else {
            target = eim.getMapInstance(map);
        }
        return target == null ? client.getChannelServer().getMapFactory().getMap(100000000) : target;
    }

    private void eventChangedMap(int map) {
        EventInstanceManager eim = getEventInstance();
        if (eim != null) {
            eim.changedMap(this, map);
        }
    }

    private void eventAfterChangedMap(int map) {
        EventInstanceManager eim = getEventInstance();
        if (eim != null) {
            eim.afterChangedMap(this, map);
        }
    }

    public void broadcastChangeJob(String newJob, int typeJob) {
        BroadcastService.broadcastMessage(getWorldId(), PacketCreator.ServerNotice(6, "[" + typeJob + "st Job] Congratulations to <" + getName() + "> on becoming a < " + newJob + ">!"));
    }

    public void sendKeymap() {
        client.write(PacketCreator.GetKeyMap(keymap));
    }

    public PlayerSkillMacro[] getMacros() {
        return skillMacros;
    }

    public void setMacros(PlayerSkillMacro[] newMacros) {
        skillMacros = newMacros;
        this.setChangedSkillMacros(true);
    }

    public void tempban(String reason, Calendar duration, int greason) {
        if (lastMonthFameIDs == null) {
            throw new RuntimeException("Trying to ban a non-loaded character (testhack)");
        }
        tempban(reason, duration, greason, client.getAccountID());
        client.close();
    }

    public static boolean tempban(String reason, Calendar duration, int greason, int accountid) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET tempban = ?, banreason = ?, greason = ? WHERE id = ?")) {
                Timestamp TS = new Timestamp(duration.getTimeInMillis());
                ps.setTimestamp(1, TS);
                ps.setString(2, reason);
                ps.setInt(3, greason);
                ps.setInt(4, accountid);
                ps.executeUpdate();
                ps.close();
            }
            return true;
        } catch (SQLException ex) {
            FileLogger.printError("TempBan.txt", ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return false;
    }

    public void bans(String text) {
        //封禁角色
        Connection con = Start.getInstance().getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("UPDATE characters SET banned = 1  WHERE accountid = ?");
            ps.setInt(1, accountid);
            ps.executeUpdate();
            ps.close();

            ps = con.prepareStatement("UPDATE accounts SET banned = 1 , banreason = ? WHERE id = ?");
            ps.setString(1, text);
            ps.setInt(2, accountid);
            ps.executeUpdate();
            ps.close();

            ps = con.prepareStatement("UPDATE mxmxd_qq_sj SET banned = 1 WHERE sj = ?");
            ps.setString(1, 取绑定手机(QQ));
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            FileLogger.printError(FileLogger.DATABASE_EXCEPTION, ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public void ban(String reason) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET banned = 1, banreason = ? WHERE id = ?");
            ps.setString(1, reason);
            ps.setInt(2, accountid);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            FileLogger.printError(FileLogger.DATABASE_EXCEPTION, ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
    }

    /**
     * <封禁>
     */
    public static boolean ban(String id, String reason, boolean accountId) {
        PreparedStatement ps = null;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            if (id.matches("/[0-9]{1,3}\\..*")) {
                ps = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)");
                ps.setString(1, id);
                ps.executeUpdate();
                ps.close();
                return true;
            }
            if (accountId) {
                ps = con.prepareStatement("SELECT id FROM accounts WHERE name = ?");
            } else {
                ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?");
            }

            boolean ret = false;
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    try (PreparedStatement psb = Start.getInstance().getConnection().prepareStatement("UPDATE accounts SET banned = 1, banreason = ? WHERE id = ?")) {
                        psb.setString(1, reason);
                        psb.setInt(2, rs.getInt(1));
                        psb.executeUpdate();
                        psb.close();
                    }
                    ret = true;
                }
                rs.close();
            }
            ps.close();
            return ret;
        } catch (SQLException ex) {
        } finally {
            try {
                if (ps != null && !ps.isClosed()) {
                    ps.close();
                }
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return false;
    }

    public void ban(String reason, boolean dc) {
        Connection con = null;
        try {
            //client.banMacs();
            //   client.banHWID();
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET banned = 1, banreason = ? WHERE id = ?");
            ps.setString(1, reason);
            ps.setInt(2, accountid);
            ps.executeUpdate();
            ps.close();
            ps = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)");
            ps.setString(1, client.getIP());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
        if (dc) {
            getClient().disconnect(true, true);
        }
    }

    /**
     * Oid of players is always = the cid
     *
     * @return
     */
    @Override
    public int getObjectId() {
        return getId();
    }

    /**
     * Throws unsupported operation exception, oid of players is read only
     *
     * @param id
     */
    @Override
    public void setObjectId(int id) {
        throw new UnsupportedOperationException();
    }

    public StorageKeeper getStorage() {
        return storage;
    }

    public List<Player> getPartyMembers() {
        List<Player> list = new LinkedList<>();

        if (party != null) {
            for (MaplePartyCharacter partyMembers : party.getMembers()) {
                list.add(partyMembers.getPlayer());
            }
        }
        return list;
    }

    public int getAriantPoints() {
        return this.ariantPoints;
    }

    public void gainAriantPoints(int gain) {
        this.ariantPoints += gain;
        dropMessage(5, "You " + (gain > 0 ? "gained" : "lost") + " (" + gain + ") point(s).");
    }

    public void gainVotePoints(int gain) {
        this.votePoints += gain;
    }

    public int getvotePoints() {
        return this.votePoints;
    }

    public boolean allowedMapChange() {
        return this.allowMapChange;
    }

    public void setallowedMapChange(boolean allowed) {
        this.allowMapChange = allowed;
    }

    public void addVisibleMapObject(FieldObject mo) {
        this.visibleMapObjectsLock.writeLock().lock();
        try {
            visibleMapObjects.add(mo);
        } finally {
            this.visibleMapObjectsLock.writeLock().unlock();
        }
    }

    public void removeVisibleMapObject(FieldObject mo) {
        this.visibleMapObjectsLock.writeLock().lock();
        try {
            visibleMapObjects.remove(mo);
        } finally {
            this.visibleMapObjectsLock.writeLock().unlock();
        }
    }

    public boolean isMapObjectVisible(FieldObject mo) {
        this.visibleMapObjectsLock.readLock().lock();
        try {
            return visibleMapObjects.contains(mo);
        } finally {
            this.visibleMapObjectsLock.readLock().unlock();
        }
    }

    public Collection<FieldObject> getAndWriteLockVisibleMapObjects() {
        visibleMapObjectsLock.writeLock().lock();
        return visibleMapObjects;
    }

    public void unlockWriteVisibleMapObjects() {
        visibleMapObjectsLock.writeLock().unlock();
    }

    public String getPartyQuestItems() {
        return dataString;
    }

    public boolean gotPartyQuestItem(String partyquestchar) {
        return dataString.contains(partyquestchar);
    }

    public void removePartyQuestItem(String letter) {
        if (gotPartyQuestItem(letter)) {
            dataString = dataString.substring(0, dataString.indexOf(letter)) + dataString.substring(dataString.indexOf(letter) + letter.length());
        }
    }

    public void setPartyQuestItemObtained(String partyquestchar) {
        if (!dataString.contains(partyquestchar)) {
            this.dataString += partyquestchar;
        }
    }

    public boolean isAlive() {
        return stats.getHp() > 0;
    }

    public void setSlot(int slotid) {
        slots = slotid;
    }

    public boolean allowedToTarget(Player other) {
        return other != null && !other.isHidden() || this.getAdministrativeLevel() >= 3;
    }

    @Override
    public void sendDestroyData(Client client) {
        client.write(PacketCreator.RemovePlayerFromMap(this.getObjectId()));
    }

    @Override
    public void sendSpawnData(Client client) {
        if (!this.isHidden() || client.getPlayer().getAdministrativeLevel() > 1) {
            client.write(PacketCreator.SpawnPlayerMapObject(this));
        }
        if (this.isHidden()) {
            List<Pair<BuffStat, Integer>> stat = Collections.singletonList(new Pair<>(BuffStat.DarkSight, 0));
            field.broadcastGMMessage(this, PacketCreator.BuffMapEffect(getId(), stat, false), false);
        }

        if (!this.isHidden() || client.getPlayer().getGM() > 1) {
            for (final ItemPet pet : pets) {
                if (pet.getSummoned()) {
                    client.write(PetPackets.ShowPet(this, pet, false, false));
                }
            }
            if (chalkBoardText != null) {
                client.write(PacketCreator.UseChalkBoard(this, false));
            }
        }
    }

    public void setLastSelectNPCTime(long time) {
        this.lastSelectNPCTime = System.currentTimeMillis();
    }

    public long getLastSelectNPCTime() {
        return lastSelectNPCTime;
    }

    public void setLastTalkTime(long time) {
        lastSelectNPCTime = time;
    }

    public long getLastAttackTime() {
        return lastAttackTime;
    }

    public void setLastAttackTime(long time) {
        lastAttackTime = time;
    }

    public long getLastHitTime() {
        return lastHitTime;
    }

    public void setLastHitTime(long time) {
        lastHitTime = time;
    }

    public boolean canAction() {
        if (System.currentTimeMillis() > (lastHitTime + 5000)) {
            return true;
        } else if (System.currentTimeMillis() < (lastHitTime + 5000)) {
            return false;
        }
        return true;
    }

    public void TamingMob(int id, int skillid) {
        tamingMob = new TamingMob(this, id, skillid);
    }

    public TamingMob getMount() {
        return tamingMob;
    }

    public void equipChanged() {
        getMap().broadcastMessage(this, PacketCreator.UpdateCharLook(this, 0x4), false);
        stats.recalcLocalStats();
        stats.enforceMaxHpMp();
        if (getMessenger() != null) {
            MessengerService.updateMessenger(this, getMessenger().getId(), getName(), client.getChannel());
        }
    }

    public final ItemPet getPet(final int index) {
        byte count = 0;
        if (pets == null) {
            return null;
        }
        for (final ItemPet pet : pets) {
            if (pet.getSummoned()) {
                if (count == index) {
                    return pet;
                }
                count++;
            }
        }
        return null;
    }

    public final ItemPet getPetByUID(final int uid) {
        if (pets == null) {
            return null;
        }
        for (final ItemPet pet : pets) {
            if (pet.getSummoned()) {
                if (pet.getUniqueId() == uid) {
                    return pet;
                }
            }
        }
        return null;
    }

    public void removePetCS(ItemPet pet) {
        pets.remove(pet);
    }

    public void addPet(final ItemPet pet) {
        if (pets.contains(pet)) {
            pets.remove(pet);
        }
        pets.add(pet);
    }

    public void removePet(ItemPet pet, boolean shiftLeft) {
        pet.setSummoned(0);
    }

    public final byte getPetIndex(final ItemPet petz) {
        byte count = 0;
        for (final ItemPet pet : pets) {
            if (pet.getSummoned()) {
                if (pet.getUniqueId() == petz.getUniqueId()) {
                    return count;
                }
                count++;
            }
        }
        return -1;
    }

    public final ArrayList<ItemPet> getSummonedPets() {
        return getSummonedPets(new ArrayList<>());
    }

    public final ArrayList<ItemPet> getSummonedPets(ArrayList<ItemPet> ret) {
        ret.clear();
        for (final ItemPet pet : pets) {
            if (pet.getSummoned()) {
                ret.add(pet);
            }
        }
        return ret;
    }

    public final byte getPetIndex(final int petId) {
        byte count = 0;
        for (final ItemPet pet : pets) {
            if (pet.getSummoned()) {
                if (pet.getUniqueId() == petId) {
                    return count;
                }
                count++;
            }
        }
        return -1;
    }

    public final byte getPetById(final int petId) {
        byte count = 0;
        for (final ItemPet pet : pets) {
            if (pet.getSummoned()) {
                if (pet.getPetItemId() == petId) {
                    return count;
                }
                count++;
            }
        }
        return -1;
    }

    public final List<ItemPet> getPets() {
        return pets;
    }

    public final void unequipAllPets() {
        for (final ItemPet pet : pets) {
            if (pet != null) {
                unequipPet(pet, true, false);
            }
        }
    }

    public void unequipPet(ItemPet pet, boolean shiftLeft, boolean hunger) {
        if (pet.getSummoned()) {
            pet.saveDatabase();

            client.write(PetPackets.PetStatUpdate(this));

            if (field != null) {
                field.broadcastMessage(this, PetPackets.ShowPet(this, pet, true, hunger), true);
            }
            removePet(pet, shiftLeft);
            if (GameConstants.GMS) {
                client.write(PetPackets.PetStatUpdate(this));
            }
            client.write(PacketCreator.EnableActions());
        }
    }

    public int getActivePets() {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT `pets` FROM characters WHERE id = ?")) {
                ps.setInt(1, getId());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        final String[] petss = rs.getString("pets").split(",");
                        List<Integer> pet_data = new ArrayList<>();
                        for (int i = 0; i < 3; i++) {
                            int v1 = Integer.parseInt(petss[i]);
                            if (v1 != -1) {
                                pet_data.add(Integer.parseInt(petss[i]));
                            }
                        }
                        rs.close();
                        return pet_data.size();
                    }
                    rs.close();
                    ps.close();
                }
            }
        } catch (SQLException e) {
            System.out.println("Player was not added to the map due to a pet error!\r\nError: " + e.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
        return 0;
    }

    public void spawnPet(byte slot) {
        spawnPet(slot, false, true);
    }

    public void spawnPet(byte slot, boolean lead) {
        spawnPet(slot, lead, true);
    }

    public void spawnPet(byte slot, boolean lead, boolean broadcast) {
        final Item item = getInventory(InventoryType.CASH).getItem(slot);
        if (item == null) {
            return;
        }
        switch (item.getItemId()) {
            case 5000047:
            case 5000028: {
                final ItemPet pet = ItemPet.createPet(item.getItemId() + 1, InventoryIdentifier.getInstance());
                if (pet != null) {
                    InventoryManipulator.addById(client, item.getItemId() + 1, (short) 1, item.getOwner(), "", pet);
                    InventoryManipulator.removeFromSlot(client, InventoryType.CASH, slot, (short) 1, false);
                }
                break;
            }
            default: {
                final ItemPet pet = item.getPet();
                if (FieldLimit.CANNOTUSEPET.check(field.getFieldLimit())) {
                    announce(PetPackets.RemovePet(this.getId(), getPetIndex(pet), (byte) 3));
                    return;
                }
                if (pet != null && (item.getItemId() != 5000054 || pet.getSecondsLeft() > 0) && (item.getExpiration() == -1 || item.getExpiration() > System.currentTimeMillis())) {
                    if (pet.getSummoned()) {
                        unequipPet(pet, true, false);
                    } else {
                        if (getSkillLevel(PlayerSkillFactory.getSkill(Beginner.FollowTheLead)) == 0 && getPet(0) != null) {
                            unequipPet(getPet(0), false, false);
                        }
                        final Point pos = getPosition();
                        pos.y -= 12;
                        pet.setPosition(pos);
                        MapleFoothold fh = field.getFootholds().findBelow(pet.getPosition());
                        pet.setFoothold(fh != null ? fh.getId() : 0);
                        pet.setStance(0);
                        pet.setSummoned(1);
                        addPet(pet);
                        pet.setSummoned(getPetIndex(pet) + 1);
                        if (broadcast && getMap() != null) {
                            field.broadcastMessage(this, PetPackets.ShowPet(this, pet, false, false), true);
                            client.write(PetPackets.PetStatUpdate(this));
                        }
                    }
                }
                break;
            }
        }
        client.write(PetPackets.EmptyStatUpdate());
    }

    public void updatePetAuto() {
        if (getAutoHpPot() > 0) {
            client.write(PetPackets.AutoHpPot(getAutoHpPot()));
        }
        if (getAutoMpPot() > 0) {
            client.write(PetPackets.AutoMpPot(getAutoMpPot()));
        }
    }

    public void expireOnLogout() {
        for (Inventory inv : this.inventory) {
            for (Item item : inv.list()) {
                if (ItemInformationProvider.getInstance().isExpireOnLogout(item.getItemId())) {
                    inv.removeItem(item.getPosition());
                }
            }
        }
    }

    public boolean isChallenged() {
        return challenged;
    }

    public void setChallenged(boolean challenged) {
        this.challenged = challenged;
    }

    public FameStatus canGiveFame(Player from) {
        if (gm > 0) {
            return FameStatus.OK;
        } else if (from == null || lastMonthFameIDs == null || lastMonthFameIDs.contains(Integer.valueOf(from.getId()))) {
            return FameStatus.NOT_THIS_MONTH;
        } else {
            return FameStatus.OK;
        }
    }

    public void hasGivenFame(Player to) {
        lastFameTime = System.currentTimeMillis();
        lastMonthFameIDs.add(to.getId());
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO famelog (characterid, characterid_to) VALUES (?, ?)")) {
                ps.setInt(1, getId());
                ps.setInt(2, to.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
    }

    public MapleParty getParty() {
        return party;
    }

    public int getPartyId() {
        return (party != null ? party.getId() : -1);
    }

    public void setParty(MapleParty p) {
        if (p == null) {
            this.mpc = null;
            doorSlot = -1;
            party = null;
        } else {
            party = p;
        }
    }

    public Trade getTrade() {
        return trade;
    }

    public void setTrade(Trade trade) {
        this.trade = trade;
    }

    public EventInstanceManager getEventInstance() {
        return eventInstance;
    }

    public void setEventInstance(EventInstanceManager eventInstance) {
        this.eventInstance = eventInstance;
    }

    public void addDoor(Integer owner, MapleDoor door) {
        doors.put(owner, door);
    }

    public void removeDoor(Integer owner) {
        doors.remove(owner);
    }

    public int getDoorSlot() {
        if (doorSlot == -1) {
            doorSlot = (party == null) ? 0 : party.getPartyDoor(this.getId());
        }
        return doorSlot;
    }

    public void clearDoors() {
        doors.clear();
    }

    public Map<Integer, MapleDoor> getDoors() {
        return doors;
    }

    public boolean canDoor() {
        return canDoor;
    }

    public void fakeRelog() {
        client.write(PacketCreator.GetCharInfo(this));
        final Field mapp = getMap();
        mapp.removePlayer(this);
        mapp.addPlayer(this);
    }

    public void disableDoor() {
        canDoor = false;
        CharacterTimer.getInstance().schedule(() -> {
            canDoor = true;
        }, 5000);
    }

    public Map<Integer, MapleSummon> getSummons() {
        return summons;
    }

    public Collection<MapleSummon> getSummonsValues() {
        return summons.values();
    }

    public int getChair() {
        return chair;
    }

    public int getItemEffect() {
        return itemEffect;
    }

    public void setChair(int chair) {
        this.chair = chair;
    }

    public void setItemEffect(int itemEffect) {
        this.itemEffect = itemEffect;
    }

    public Collection<Inventory> allInventories() {
        return Arrays.asList(inventory);
    }

    @Override
    public FieldObjectType getType() {
        return FieldObjectType.PLAYER;
    }

    public Minigame getMiniGame() {
        return miniGame;
    }

    public void setMiniGame(Minigame miniGame) {
        this.miniGame = miniGame;
    }

    public int getMiniGamePoints(String type, boolean omok) {
        if (omok) {
            switch (type) {
                case "wins":
                    return omokWins;
                case "losses":
                    return omokLosses;
                default:
                    return omokTies;
            }
        } else {
            switch (type) {
                case "wins":
                    return matchCardWins;
                case "losses":
                    return matchCardLosses;
                default:
                    return matchCardTies;
            }
        }
    }

    public void setMiniGamePoints(Player visitor, int winnerslot, boolean omok) {
        if (omok) {
            switch (winnerslot) {
                case 1:
                    this.omokWins++;
                    visitor.omokLosses++;
                    break;
                case 2:
                    visitor.omokWins++;
                    this.omokLosses++;
                    break;
                default:
                    this.omokTies++;
                    visitor.omokTies++;
                    break;
            }
        } else {
            switch (winnerslot) {
                case 1:
                    this.matchCardWins++;
                    visitor.matchCardLosses++;
                    break;
                case 2:
                    visitor.matchCardWins++;
                    this.matchCardLosses++;
                    break;
                default:
                    this.matchCardTies++;
                    visitor.matchCardTies++;
                    break;
            }
        }
    }

    public void empty() {
        try {
            if (client.getChannelServer().getPlayerStorage().getCharacterByName(getName()) != null) {
                client.getChannelServer().removePlayer(this);
            }
            if (getMount() != null) {
                this.getMount().cancelSchedule();
            }
            if (BerserkSchedule != null) {
                this.BerserkSchedule.cancel(true);
                this.BerserkSchedule = null;
            }
            if (beholderBuffSchedule != null) {
                this.beholderBuffSchedule.cancel(true);
                this.beholderBuffSchedule = null;
            }
            if (beholderHealingSchedule != null) {
                this.beholderHealingSchedule.cancel(true);
                this.beholderHealingSchedule = null;
            }
            this.buddyList = null;
            this.controlled = null;
            this.coolDowns = null;
            this.diseases = null;
            this.rps = null;
            if (recoveryTask != null) {
                recoveryTask.cancel(false);
            }
            if (dragonBloodSchedule != null) {
                this.dragonBloodSchedule.cancel(true);
                this.dragonBloodSchedule = null;
            }
            this.doors = null;
            this.effects = null;
            this.eventInstance = null;
            if (this.expireTask != null) {
                this.expireTask.cancel(true);
                this.expireTask = null;
            }
            this.crushRings = null;
            this.weddingRings = null;
            this.friendshipRings = null;
            if (keymap != null) {
                this.keymap.clear();
                this.keymap = null;
            }
            this.lastMonthFameIDs.clear();
            this.lastMonthFameIDs = null;
            if (mapTimeLimitTask != null) {
                this.mapTimeLimitTask.cancel(true);
                this.mapTimeLimitTask = null;
            }
            if (tamingMob != null) {
                tamingMob = null;
            }
            this.mpc = null;
            this.field = null;
            this.pets = null;
            this.party = null;
            if (quests != null) {
                this.quests.clear();
                this.quests = null;
            }
            this.savedLocations = null;
            this.shop = null;
            this.skillMacros = null;
            this.skills.clear();
            this.skills = null;
            this.storage = null;
            this.summons.clear();
            this.summons = null;
            this.visibleMapObjects.clear();
            this.visibleMapObjects = null;
            timers.forEach((sf) -> {
                sf.cancel(false);
            });
            this.timers.clear();
        } catch (final Throwable e) {
            FileLogger.printError("Account_Empty.txt", e);
        }
    }

    public boolean haveItemEquipped(int itemid) {
        return getInventory(InventoryType.EQUIPPED).findById(itemid) != null;
    }

    public boolean haveItem(int itemid, int quantity, boolean checkEquipped, boolean greaterOrEquals) {
        int possesed = inventory[ItemInformationProvider.getInstance().getInventoryType(itemid).ordinal()].countById(itemid);

        if (checkEquipped) {
            possesed += inventory[InventoryType.EQUIPPED.ordinal()].countById(itemid);
        }
        return greaterOrEquals ? possesed >= quantity : possesed == quantity;
    }

    public int haveItem2(int a) {
        return inventory[ItemInformationProvider.getInstance().getInventoryType(a).ordinal()].countById(a);
    }

    public void setMeso(int set) {
        meso.set(set);
        stats.updateSingleStat(PlayerStat.MESO, set, false);
    }

    public boolean getCanSmega() {
        return canSmega;
    }

    public void setCanSmega(boolean yn) {
        canSmega = yn;
    }

    public boolean getSmegaEnabled() {
        return smegaEnabled;
    }

    public void setSmegaEnabled(boolean yn) {
        smegaEnabled = yn;
    }

    public void sendServerNotice(String msg) {
        OutPacket packet = PacketCreator.ServerNotice(5, msg);
        BroadcastService.broadcastMessage(getWorldId(), packet);
    }

    public void gainFame(int delta) {
        this.addFame(delta);
        stats.updateSingleStat(PlayerStat.FAME, this.pop);
    }

    public void yellowMessage(String m) {
        announce(PacketCreator.SendYellowTip(m));
    }

    public int getJobId() {
        return this.getJob().getId();
    }

    public void checkBerserk(final boolean isHidden) {
        if (BerserkSchedule != null) {
            BerserkSchedule.cancel(false);
        }
        final Player p = this;
        if (job.equals(PlayerJob.DARKKNIGHT)) {
            PlayerSkill BerserkX = PlayerSkillFactory.getSkill(DarkKnight.Berserk);
            final int skilllevel = getSkillLevel(BerserkX);
            if (skilllevel > 0) {
                Berserk = p.getHp() * 100 / p.getStat().getMaxHp() < BerserkX.getEffect(skilllevel).getX();
                BerserkSchedule = CharacterTimer.getInstance().register(() -> {
                    getClient().write(PacketCreator.ShowOwnBerserk(skilllevel, Berserk));
                    if (!isHidden) {
                        field.broadcastMessage(Player.this, PacketCreator.ShowBerserk(getId(), skilllevel, Berserk), false);
                    } else {
                        field.broadcastGMMessage(Player.this, PacketCreator.ShowBerserk(getId(), skilllevel, Berserk), false);
                    }
                }, 5000, 3000);
            }
        }
    }

    public void setGMLevel(int level) {
        if (level >= 5) {
            this.gm = 5;
        } else if (level < 0) {
            this.gm = 0;
        } else {
            this.gm = level;
        }
    }

    public long getUseTime() {
        return useTime;
    }

    public void sendPolice(String text) {
    }

    public void questTimeLimit(final MapleQuest quest, int time) {
        ScheduledFuture<?> sf = CharacterTimer.getInstance().schedule(() -> {
            announce(PacketCreator.QuestExpire(quest.getId()));
            MapleQuestStatus newStatus = new MapleQuestStatus(quest, MapleQuestStatus.Status.NOT_STARTED);
            newStatus.setForfeited(getQuest(quest).getForfeited() + 1);
            updateQuest(newStatus);
        }, time * 60 * 1000);
        announce(PacketCreator.AddQuestTimeLimit(quest.getId(), time * 60 * 1000));
        timers.add(sf);
    }

    public void setMPC(MaplePartyCharacter mpc) {
        this.mpc = mpc;
    }

    public MapleStatEffect getBuffEffect(BuffStat stat) {
        BuffStatValueHolder mbsvh = effects.get(stat);
        if (mbsvh == null) {
            return null;
        } else {
            return mbsvh.effect;
        }
    }

    public int getJobType() {
        return job.getId() / 1000;
    }

    public CashShop getCashShop() {
        return cashShop;
    }

    public final Inventory[] getInventorys() {
        return inventory;
    }

    public final long getLastFameTime() {
        return lastFameTime;
    }

    public final List<Integer> getFamedCharacters() {
        return lastMonthFameIDs;
    }

    public final int[] getSavedLocations() {
        return savedLocations;
    }

    public final void spawnSavedPets() {
        spawnSavedPets(false, false);
    }

    public final void spawnSavedPets(boolean lead, boolean broadcast) {
        for (int i = 0; i < petStore.length; i++) {
            if (petStore[i] > -1) {
                spawnPet(petStore[i], lead, broadcast);
            }
        }
        if (GameConstants.GMS) {
            client.write(PetPackets.PetStatUpdate(this));
        }
        petStore = new byte[]{-1, -1, -1};
    }

    public final byte[] getPetStores() {
        return petStore;
    }

    /**
     * <游戏频道变更>
     */
    public void changeChannel(final int channel) {
        final ChannelServer toch = getWorld().getChannelById(channel);
        if (channel == client.getChannel() || toch == null || toch.isShutdown()) {
            return;
        }

        if (this.getTrade() != null) {
            Trade.cancelTrade(getTrade(), this);
        }

        final ChannelServer ch = getChannelServer();
        if (getMessenger() != null) {
            MessengerService.silentLeaveMessenger(getMessenger().getId(), new MapleMessengerCharacter(this));
        }

        PlayerBuffStorage.addBuffsToStorage(getId(), getAllBuffs());
        PlayerBuffStorage.addCooldownsToStorage(getId(), getAllCooldowns());
        PlayerBuffStorage.addDiseaseToStorage(getId(), getAllDiseases());

        this.cancelAllBuffs(true);
        this.cancelAllDebuffs();

        if (this.getBuffedValue(BuffStat.Puppet) != null) {
            this.cancelEffectFromBuffStat(BuffStat.Puppet);
        }
        if (this.getBuffedValue(BuffStat.ComboAttack) != null) {
            this.cancelEffectFromBuffStat(BuffStat.ComboAttack);
        }

        getMap().removePlayer(this);
        ch.removePlayer(this);

        toch.addClientInTransfer(channel, getId(), getClient());
        client.updateLoginState(ClientLoginState.CHANGE_CHANNEL, client.getSessionIPAddress());
        String[] socket = getChannelServer().getIP().split(":");
        try {
            announce(PacketCreator.GetChannelChange(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1])));
        } catch (UnknownHostException ex) {
            FileLogger.printError(FileLogger.PLAYER_STUCK, ex);
        }
    }

    public int getAutoHpPot() {
        return petAutoHP;
    }

    public void setAutoHpPot(int itemId) {
        petAutoHP = itemId;
    }

    public int getAutoMpPot() {
        return petAutoMP;
    }

    public void setAutoMpPot(int itemId) {
        petAutoMP = itemId;
    }

    public long getPlaytime() {
        long time = Calendar.getInstance().getTimeInMillis();
        playtime += time - playtimeStart;
        playtimeStart = time;
        return playtime;
    }

    public void spawnBomb() {
        final MapleMonster bomb = MapleLifeFactory.getMonster(9300166);
        getMap().spawnMonsterOnGroudBelow(bomb, getPosition());
        EventTimer.getInstance().schedule(() -> {
            field.killMonster(bomb, client.getPlayer(), false, (byte) 1);
        }, 10 * 1000);
    }

    public int[] getRocks() {
        return rocks;
    }

    public int getRockSize() {
        int ret = 0;
        for (int i = 0; i < 10; i++) {
            if (rocks[i] != 999999999) {
                ret++;
            }
        }
        return ret;
    }

    public void deleteRocks(int map) {
        for (int i = 0; i < 5; i++) {
            if (regrocks[i] == map) {
                regrocks[i] = 999999999;
                changedRegrockLocations = true;
                break;
            }
        }
    }

    public void addRockMap() {
        if (getRegRockSize() >= 5) {
            return;
        }
        regrocks[getRegRockSize()] = getMapId();
        changedRegrockLocations = true;
    }

    public boolean isRockMap(int id) {
        for (int i = 0; i < 10; i++) {
            if (rocks[i] == id) {
                return true;
            }
        }
        return false;
    }

    public int[] getRegRocks() {
        return regrocks;
    }

    public int getRegRockSize() {
        int ret = 0;
        for (int i = 0; i < 5; i++) {
            if (regrocks[i] != 999999999) {
                ret++;
            }
        }
        return ret;
    }

    public boolean isRegRockMap(int id) {
        for (int i = 0; i < 5; i++) {
            if (regrocks[i] == id) {
                return true;
            }
        }
        return false;
    }

    public void openShop(int id) {
        ShopFactory.getInstance().getShop(id).sendShop(getClient());
    }

    public void closePlayerShop() {
        PlayerShop mps = this.getPlayerShop();
        if (mps == null) {
            return;
        }

        if (mps.isOwner(this)) {
            mps.setOpen(false);

            client.getChannelServer().unregisterPlayerShop(mps);

            for (PlayerShopItem mpsi : mps.getItems()) {
                if (mpsi.getBundles() >= 2) {
                    Item iItem = mpsi.getItem().copy();
                    iItem.setQuantity((short) (mpsi.getBundles() * iItem.getQuantity()));
                    InventoryManipulator.addFromDrop(this.getClient(), iItem, "", false);
                } else if (mpsi.isExist()) {
                    InventoryManipulator.addFromDrop(this.getClient(), mpsi.getItem(), "", true);
                }
            }
            mps.closeShop();
        } else {
            mps.removeVisitor(this);
        }
        this.setPlayerShop(null);
    }

    public void closeMiniGame() {
        Minigame game = this.getMiniGame();
        if (game == null) {
            return;
        }

        this.setMiniGame(null);
        if (game.isOwner(this)) {
            this.getMap().broadcastMessage(MinigamePackets.RemoveCharBox(this));
            game.broadcastToVisitor(MinigamePackets.GetMiniGameClose());
        } else {
            game.removeVisitor(this);
        }
    }

    public MapleFoothold getFoothold() {
        Point pos = this.getPosition();
        pos.y -= 6;
        return getMap().getFootholds().findBelow(pos);
    }

    public final boolean canHP(long now) {
        if (lastHPTime + 5000 < now) {
            lastHPTime = now;
            return true;
        }
        return false;
    }

    public final boolean canMP(long now) {
        if (lastMPTime + 5000 < now) {
            lastMPTime = now;
            return true;
        }
        return false;
    }

    public long getLastHPTime() {
        return lastHPTime;
    }

    public long getLastMPTime() {
        return lastMPTime;
    }

    public boolean isPartyLeader() {
        return party.getLeader().getId() == getId();
    }

    public boolean hasEmptySlot(byte invType) {
        return getInventory(InventoryType.getByType(invType)).getNextFreeSlot() > -1;
    }

    public long getNpcCooldown() {
        return npcCd;
    }

    public void setNpcCooldown(long d) {
        npcCd = d;
    }

    public long getItemCooldown() {
        return itemCd;
    }

    public void setItemCooldown(long d) {
        itemCd = d;
    }

    public long getMapCooldown() {
        return mapCd;
    }

    public void setMapCooldown(long d) {
        mapCd = d;
    }

    public long getMesoCooldown() {
        return mesoCd;
    }

    public void setmesoCooldown(long d) {
        mesoCd = d;
    }

    public long getLastUsedCashItem() {
        return lastUsedCashItem;
    }

    public void setLastUsedCashItem(long time) {
        this.lastUsedCashItem = time;
    }

    public enum FameStatus {

        OK, NOT_TODAY, NOT_THIS_MONTH
    }

    public int getBuddyCapacity() {
        return buddyList.getCapacity();
    }

    public void setBuddyCapacity(byte capacity) {
        buddyList.setCapacity(capacity);
        client.write(PacketCreator.UpdateBuddyCapacity(capacity));
    }

    public MapleMessenger getMessenger() {
        return messenger;
    }

    public void setMessenger(MapleMessenger messenger) {
        this.messenger = messenger;
    }

    public int getFh() {
        Point pos = this.getPosition();
        pos.y -= 6;
        if (getMap().getFootholds().findBelow(pos) == null) {
            return 0;
        } else {
            return getMap().getFootholds().findBelow(pos).getY1();
        }
    }

    public void setMatchCardPoints(Player visitor, int winnerslot) {
    }

    public void setHasCheat(boolean cheat) {
        this.hasCheat = cheat;
    }

    public boolean isHasCheat() {
        return this.hasCheat;
    }

    public void addCoolDown(int skillId, long startTime, long length) {
        if (this.coolDowns.containsKey(skillId)) {
            this.coolDowns.remove(skillId);
        }
        coolDowns.put(skillId, new PlayerCoolDownValueHolder(skillId, startTime, length));
    }

    public void removeCooldown(int skillId) {
        if (coolDowns.containsKey(skillId)) {
            coolDowns.remove(skillId);
        }
    }

    public boolean skillisCooling(int skillId) {
        return coolDowns.containsKey(skillId);
    }

    public void giveCoolDowns(final int skillid, long starttime, long length) {
        addCoolDown(skillid, starttime, length);
    }

    public int getCooldownSize() {
        return coolDowns.size();
    }

    public List<PlayerCoolDownValueHolder> getAllCooldowns() {
        List<PlayerCoolDownValueHolder> ret = new ArrayList<>();

        for (PlayerCoolDownValueHolder mcdvh : coolDowns.values()) {
            ret.add(new PlayerCoolDownValueHolder(mcdvh.skillId, mcdvh.startTime, mcdvh.length));
        }

        return ret;
    }

    public void removeAllCooldownsExcept(int id, boolean packet) {
        ArrayList<PlayerCoolDownValueHolder> list = new ArrayList<>(coolDowns.values());
        for (PlayerCoolDownValueHolder mcvh : list) {
            if (mcvh.skillId != id) {
                coolDowns.remove(mcvh.skillId);
                if (packet) {
                    client.announce(PacketCreator.SkillCooldown(mcvh.skillId, 0));
                }
            }
        }
    }

    public final void giveSilentDebuff(final List<DiseaseValueHolder> ld) {
        if (ld != null) {
            ld.stream().forEach((disease) -> {
                diseases.put(disease.disease, disease);
            });
        }
    }

    public String getMapName(int mapId) {
        return client.getChannelServer().getMapFactory().getMap(mapId).getMapName();
    }

    public int getDiseaseSize() {
        return diseases.size();
    }

    public void giveDebuff(final Disease disease, MobSkill skill) {
        giveDebuff(disease, skill.getX(), skill.getDuration(), skill.getSkillId(), skill.getSkillLevel());
    }

    public void giveDebuff(final Disease disease, int x, long duration, int skillid, int level) {
        if (isAlive() && !isActiveBuffedValue(Bishop.HolyShield) && !hasDisease(disease) && diseases.size() < 2) {
            if ((disease != Disease.SEDUCE) && (disease != Disease.STUN) && (getBuffedValue(BuffStat.HolySymbol) != null)) {
                return;
            }
            this.diseases.put(disease, new DiseaseValueHolder(disease, System.currentTimeMillis(), duration));

            final List<Pair<Disease, Integer>> debuff = Collections.singletonList(new Pair<>(disease, Integer.valueOf(x)));
            this.announce(PacketCreator.GiveDebuff(debuff, skillid, level, (int) duration));
            field.broadcastMessage(this, PacketCreator.GiveForeignDebuff(id, debuff, skillid, level), false);
        }
    }

    public final boolean hasDisease(final Disease dis) {
        return diseases.containsKey(dis);
    }

    public final int getDiseasesSize() {
        return diseases.size();
    }

    public int getTeam() {
        if (this.MCPQTeam == null) {
            return -1;
        }
        return this.MCPQTeam.code;
    }

    public MCField.MCTeam getMCPQTeam() {
        return MCPQTeam;
    }

    public void setMCPQTeam(MCField.MCTeam MCPQTeam) {
        this.MCPQTeam = MCPQTeam;
    }

    public MCParty getMCPQParty() {
        return MCPQParty;
    }

    public void setMCPQParty(MCParty MCPQParty) {
        this.MCPQParty = MCPQParty;
    }

    public MCField getMCPQField() {
        return MCPQField;
    }

    public void setMCPQField(MCField MCPQField) {
        this.MCPQField = MCPQField;
    }

    public int getAvailableCP() {
        return availableCP;
    }

    public void setAvailableCP(int availableCP) {
        this.availableCP = availableCP;
    }

    public int getTotalCP() {
        return totalCP;
    }

    public void setTotalCP(int totalCP) {
        this.totalCP = totalCP;
    }

    public void gainCP(int cp) {
        this.availableCP += cp;
        this.totalCP += cp;
    }

    public void loseCP(int cp) {
        this.availableCP -= cp;
    }

    public final ArrayList<DiseaseValueHolder> getAllDiseases(ArrayList<DiseaseValueHolder> ret) {
        ret.clear();
        for (DiseaseValueHolder mc : diseases.values()) {
            if (mc != null) {
                ret.add(mc);
            }
        }
        return ret;
    }

    public final List<DiseaseValueHolder> getAllDiseases() {
        return new ArrayList<>(diseases.values());
    }

    public void cancelAllDebuffs() {
        diseases.clear();
    }

    public void setLevel(int level) {
        this.level = level - 1;
    }

    public void setMap(int PmapId) {
        this.mapId = PmapId;
    }

    private void prepareBeholderEffect() {
        if (beholderHealingSchedule != null) {
            beholderHealingSchedule.cancel(false);
        }
        if (beholderBuffSchedule != null) {
            beholderBuffSchedule.cancel(false);
        }
        PlayerSkill healing = PlayerSkillFactory.getSkill(DarkKnight.AuraOfBeholder);
        int healingLevel = getSkillLevel(healing);
        if (healingLevel > 0) {
            final MapleStatEffect healEffect = healing.getEffect(healingLevel);
            int healInterval = healEffect.getX() * 1000;
            beholderHealingSchedule = CharacterTimer.getInstance().register(() -> {
                stats.addHP(healEffect.getHp());
                client.write(EffectPackets.ShowOwnBuffEffect(DarkKnight.Beholder, PlayerEffects.SKILL_AFFECTED.getEffect()));
                field.broadcastMessage(Player.this, PacketCreator.SummonSkill(getId(), DarkKnight.Beholder, 5), true);
                field.broadcastMessage(Player.this, EffectPackets.BuffMapVisualEffect(getId(), DarkKnight.Beholder, PlayerEffects.SKILL_AFFECTED.getEffect()), false);
            }, healInterval, healInterval);
        }
        PlayerSkill buff = PlayerSkillFactory.getSkill(DarkKnight.HexOfBeholder);
        int buffLevel = getSkillLevel(buff);
        if (buffLevel > 0) {
            final MapleStatEffect buffEffect = buff.getEffect(buffLevel);
            int buffInterval = buffEffect.getX() * 1000;
            beholderBuffSchedule = CharacterTimer.getInstance().register(() -> {
                buffEffect.applyTo(Player.this);
                client.write(EffectPackets.ShowOwnBuffEffect(DarkKnight.Beholder, PlayerEffects.SKILL_AFFECTED.getEffect()));
                field.broadcastMessage(Player.this, PacketCreator.SummonSkill(getId(), DarkKnight.Beholder, (int) (Math.random() * 3) + 6), true);
                field.broadcastMessage(Player.this, EffectPackets.BuffMapVisualEffect(getId(), DarkKnight.Beholder, PlayerEffects.SKILL_AFFECTED.getEffect()), false);
            }, buffInterval, buffInterval);
        }
    }

    public int getPartnerId() {
        return partner;
    }

    public void setPartnerId(final int mi) {
        this.partner = mi;
    }

    public int getMarriageItemId() {
        return spouseItemId;
    }

    public void setMarriageItemId(final int mi) {
        this.spouseItemId = mi;
    }

    public String getPartner() {
        return PlayerQuery.getNameById(partner);
    }

    public int countItem(int itemid) {
        InventoryType type = ItemInformationProvider.getInstance().getInventoryType(itemid);
        Inventory iv = inventory[type.ordinal()];
        int possesed = iv.countById(itemid);
        return possesed;
    }

    public long getLastPortalEntry() {
        return lastPortalEntry;
    }

    public void setLastPortalEntry(long lastPortalEntry) {
        this.lastPortalEntry = lastPortalEntry;
    }

    public void assassinate() {
        stats.addHP(-30000);
    }

    public void giveItemBuff(int itemID) {
        ItemInformationProvider mii = ItemInformationProvider.getInstance();
        MapleStatEffect statEffect = mii.getItemEffect(itemID);
        statEffect.applyTo(this);
    }

    public long getLastCatch() {
        return lastCatch;
    }

    public void setLastCatch(long lastCatch) {
        this.lastCatch = lastCatch;
    }

    public boolean hasShield() {
        return shield;
    }

    public void setShield(boolean shield) {
        this.shield = shield;
    }

    public void shield(ScheduledFuture<?> schedule) {
        if (this.shield) {
            return;
        }
        /*
         List<Pair<BuffStat, Integer>> stat = Collections.singletonList(new Pair<>(BuffStat.SHIELD, Integer.valueOf(1)));

         setBuffedValue(BuffStat.SHIELD, Integer.valueOf(1));


         getClient().write(PacketCreator.GiveBuff(2022269, 60 * 1000, stat));

         getMap().broadcastMessage(this, PacketCreator.BuffMapEffect(getId(), stat, false), false);

         */
        this.shield = true;

    }

    public void cancelShield() {
        if (getClient().getChannelServer().getPlayerStorage().getCharacterById(getId()) != null) {
            if (!this.shield) {
                return;
            }
            stats.recalcLocalStats();
            stats.enforceMaxHpMp();
            /*
             List<BuffStat> stat = Collections.singletonList(BuffStat.SHIELD);

             client.write(PacketCreator.CancelBuff(stat));
             field.broadcastMessage(this, PacketCreator.CancelForeignBuff(getId(), stat), false);
             */
            this.shield = false;
        }
    }

    public void message(String m) {
        dropMessage(5, m);
    }

    public void dropMessage(int a, String string) {
        announce(PacketCreator.ServerNotice(a, string));
    }

    public void dropMessage(String string) {
        dropMessage(5, string);
    }

    public void announce(OutPacket packet) {
        client.announce(packet);
    }

    public final void showHint(String msg) {
        showHint(msg, (short) 500);
    }

    public void showHint(String msg, short length) {
        client.announceHint(msg, (short) length);
    }

    public void updateAriantScore() {
        this.getMap().broadcastMessage(PacketCreator.UpdateAriantPQRanking(this.getName(), this.countItem(ItemConstants.ARIANT_JEWEL), false));
    }

    public int getRandomage(Player player) {
        int maxdamage = player.getStat().getCurrentMaxBaseDamage();
        int mindamage = player.getStat().calculateMinBaseDamage(player);
        return Randomizer.rand(mindamage, maxdamage);
    }

    public int getMinDmg(Player player) {
        int mindamage = player.getStat().calculateMinBaseDamage(player);
        return mindamage;
    }

    public int getMaxDmg(Player player) {
        int maxdamage = player.getStat().getCurrentMaxBaseDamage();
        return maxdamage;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void removeAriantRoom(int room) {
        ariantRoomLeader[room] = "";
        ariantRoomSlot[room] = 0;
    }

    public String getAriantRoomLeaderName(int room) {
        return ariantRoomLeader[room];
    }

    public int getAriantSlotsRoom(int room) {
        return ariantRoomSlot[room];
    }

    public void setAriantRoomLeader(int room, String charname) {
        ariantRoomLeader[room] = charname;
    }

    public void setAriantSlotRoom(int room, int slot) {
        ariantRoomSlot[room] = slot;
    }

    public boolean getInteractionsOpen() {
        return trade != null || this.playerShop != null;
    }

    public SpeedQuiz getSpeedQuiz() {
        return sq;
    }

    public void setSpeedQuiz(SpeedQuiz sq) {
        this.sq = sq;
    }

    public long getLastSpeedQuiz() {
        return lastSpeedQuiz;
    }

    public void setLastSpeedQuiz(final long t) {
        this.lastSpeedQuiz = t;
    }

    public void maxAllSkills() {
        for (int skills : SkillConstants.allSkills) {
            maxSkill(skills);
        }
    }

    public void maxSkill(int skillid) {
        if (Math.floor(skillid / 10000) == getJob().getId() || isGameMaster() || skillid < 2000) {
            PlayerSkill skill = PlayerSkillFactory.getSkill(skillid);
            int maxLevel = skill.getMaxLevel();
            changeSkillLevel(skill, maxLevel, maxLevel);
        }
    }

    public int getAveragePartyLevel() {
        int averageLevel = 0, size = 0;
        for (MaplePartyCharacter pl : getParty().getMembers()) {
            averageLevel += pl.getLevel();
            size++;
        }
        if (size <= 0) {
            return level;
        }
        return averageLevel /= size;
    }

    public int getAverageMapLevel() {
        int averageLevel = 0, size = 0;
        for (Player pl : getMap().getCharactersThreadsafe()) {
            averageLevel += pl.getLevel();
            size++;
        }
        if (size <= 0) {
            return level;
        }
        return averageLevel /= size;
    }

    public void autoban(String reason) {
        this.ban(reason);
        announce(PacketCreator.SendPolice(String.format("You have been blocked by the#b %s Police for HACK reason.#k", ServerProperties.Login.SERVER_NAME)));
        CharacterTimer.getInstance().schedule(() -> {
            client.disconnect(false, false);
        }, 9000);

        BroadcastService.broadcastGMMessage(getWorldId(), PacketCreator.ServerNotice(6, PlayerStringUtil.makeMapleReadable(this.name) + " was autobanned for " + reason));
    }

    public void gainItem(int id, short quantity, boolean showMessage) {
        gainItem(id, quantity, false, showMessage, -1);
    }

    public Item gainItem(int id, short quantity, boolean randomStats, boolean showMessage, long expires) {
        Item item = null;
        if (quantity >= 0) {
            ItemInformationProvider ii = ItemInformationProvider.getInstance();

            if (ItemConstants.getInventoryType(id).equals(InventoryType.EQUIP)) {
                item = ii.getEquipById(id);
            } else {
                item = new Item(id, (byte) 0, (short) quantity);
            }

            long l = expires;
            long time = 1000L * 60L * 60L * 24L * l;

            if (expires != -1) {
                for (int cards : ItemConstants.CARDS_4HRS) {
                    if (cards == item.getItemId()) {
                        item.setExpiration(System.currentTimeMillis() + 1000L * 60L * 60L * 4L);
                    } else {
                        item.setExpiration(System.currentTimeMillis() + expires);
                    }
                }
                if (item.getItemId() == 5211048 || item.getItemId() == 5360042) {
                    time = 1000L * 60L * 60L * 4L;
                    item.setExpiration(System.currentTimeMillis() + time);
                } else {
                    item.setExpiration(System.currentTimeMillis() + expires);
                }
            }
            if (!InventoryManipulator.checkSpace(getClient(), id, quantity, "")) {
                //this.getClient().getPlayer().dropMessage(1, "背包满");
                return null;
            }
            if (ItemConstants.getInventoryType(id).equals(InventoryType.EQUIP) && !ItemConstants.isThrowingStar(id) && !ItemConstants.isBullet(id)) {
                if (randomStats) {
                    item = ii.randomizeStats((Equip) item);
                    InventoryManipulator.addFromDrop(getClient(), ii.randomizeStats((Equip) item), "", true);
                } else {
                    InventoryManipulator.addFromDrop(getClient(), (Equip) item, "", true);
                }
            } else {
                InventoryManipulator.addFromDrop(getClient(), item, "", true);
            }
        } else {
            InventoryManipulator.removeById(getClient(), ItemConstants.getInventoryType(id), id, -quantity, true, false);
        }
        if (showMessage) {
            this.getClient().write(PacketCreator.GetShowItemGain(id, quantity, true));
        }
        return item;
    }

    public boolean gainSlots(int type, int slots, boolean update) {
        slots += inventory[type].getSlotLimit();
        if (slots <= 96) {
            inventory[type].setSlotLimit(slots);

            this.saveDatabase();
            if (update) {
                client.announce(PacketCreator.UpdateInventorySlotLimit(type, slots));
            }

            return true;
        }
        return false;
    }

    public int 判断每日(String bossid) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            int ret_count = 0;
            PreparedStatement ps = con.prepareStatement("SELECT * FROM bosslog WHERE characterid = ? && bossid = ?");
            ps.setInt(1, id);
            ps.setString(2, bossid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ret_count += 1;
            }
            rs.close();
            ps.close();
            return ret_count;
        } catch (Exception Ex) {
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

    public void 增加每日(String bossid) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO bosslog (characterid, bossid) values (?,?);");
            ps.setInt(1, id);
            ps.setString(2, bossid);
            ps.executeUpdate();
            ps.close();
        } catch (Exception Ex) {

        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
    }

    public int 判断每日2(String bossid) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            int ret_count = 0;
            PreparedStatement ps = con.prepareStatement("SELECT * FROM bossloa WHERE accountid = ? && bossid = ?");
            ps.setInt(1, accountid);
            ps.setString(2, bossid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ret_count += 1;
            }
            rs.close();
            ps.close();
            return ret_count;
        } catch (Exception Ex) {
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

    public void 增加每日2(String bossid) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO bossloa (accountid, bossid) values (?,?);");
            ps.setInt(1, accountid);
            ps.setString(2, bossid);
            ps.executeUpdate();
            ps.close();
        } catch (Exception Ex) {

        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
    }

    public int 死亡次数() {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT death as DATA FROM characters WHERE id = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();

        } catch (SQLException Ex) {
            System.err.println("死亡次数、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return data;
    }

    public String 出生日期() {
        String data = "";
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT createdate as DATA FROM characters WHERE id = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getString("DATA");
                }
                rs.close();
            }
            ps.close();

        } catch (SQLException Ex) {
            System.err.println("出生日期、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
        return data;
    }

    public int 判断家族() {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT jiazu as DATA FROM characters WHERE id = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();

        } catch (SQLException Ex) {
            System.err.println("判断家族、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return data;
    }

    public void 增加家族经验(int a) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE jiazu SET jiazujingyan = jiazujingyan + " + a + " WHERE jiazuid = " + getjiazu() + "")) {
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
    }

    public void 增加师傅声望(int a) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET prestige = prestige + " + a + " WHERE id = " + 师傅() + "")) {
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
    }

    public void 新增进市场的记录点(int a) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO changemapspecial (charactersid, mapid) values (?,?);");
            ps.setInt(1, id);
            ps.setInt(2, a);
            ps.executeUpdate();
            ps.close();
        } catch (Exception Ex) {

        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
    }

    public void 修改进市场的记录点(int a) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET mapid = ? WHERE id = ?")) {
                ps.setInt(1, a);
                ps.setInt(2, id);
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
    }

    public int 判断进市场的记录点() {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT mapid as DATA FROM characters WHERE id = ? ");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("判断进市场的记录点、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
        return data;
    }

    /**
     * <记录玩家的消费情况>
     */
    public void 消费记录(int a) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO consumption (charactersid, accountsid,qq,year,month,day,NX) values (?,?,?,?,?,?,?);");
            ps.setInt(1, id);
            ps.setInt(2, accountid);
            ps.setString(3, QQ);
            ps.setInt(4, Calendar.getInstance().get(Calendar.YEAR));
            ps.setInt(5, Calendar.getInstance().get(Calendar.MONTH) + 1);
            ps.setInt(6, Calendar.getInstance().get(Calendar.DATE));
            ps.setInt(7, a);
            ps.executeUpdate();
            ps.close();
        } catch (Exception Ex) {

        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
    }

    public int 判断玩家本月消费() {
        return 判断玩家月份消费(Calendar.getInstance().get(Calendar.MONTH) + 1);
    }

    public int 判断玩家月份消费(int a) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT NX as DATA FROM consumption WHERE charactersid = ? && year = ? && month = ? ");
            ps.setInt(1, id);
            ps.setInt(2, Calendar.getInstance().get(Calendar.YEAR));
            ps.setInt(3, a);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data += rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("判断玩家月份消费、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
        return data;
    }

    public int 判断玩家总共消费() {
        int data = 0;
        Connection con = Start.getInstance().getConnection();
        try {
            try (PreparedStatement ps = con.prepareStatement("SELECT NX as DATA FROM consumption WHERE charactersid = ? ")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        data += rs.getInt("DATA");
                    }
                }
            }
        } catch (SQLException Ex) {
            Ex.printStackTrace(System.err);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return data;
    }

    // 身上装备附魔汇总数据
    private Map<Integer, Integer> _equippedFuMoMap = new HashMap<>();

    public Map<Integer, Integer> getEquippedFuMoMap() {
        return _equippedFuMoMap;
    }

    public Map<Integer, Integer> F() {
        return _equippedFuMoMap;
    }

    public void 刷新身上装备附魔汇总数据(boolean a) {
        _equippedFuMoMap.clear();
        for (Item item : getInventory(InventoryType.EQUIPPED)) {
            /*String mxmxdDaKongFuMo = IIIitem.getDaKongFuMo();
            if (mxmxdDaKongFuMo != null && mxmxdDaKongFuMo.length() > 0) {
                String arr[] = mxmxdDaKongFuMo.split(",");
                for (String pair : arr) {
                    if (pair.length() == 0) {
                        continue;
                    }
                    String arr2[] = pair.split(":");
                    int fumoType = Integer.parseInt(arr2[0]);
                    int fumoVal = Integer.parseInt(arr2[1]);
                    if (_equippedFuMoMap.containsKey(fumoType)) {
                        _equippedFuMoMap.put(fumoType, _equippedFuMoMap.get(fumoType) + fumoVal);
                    } else {
                        _equippedFuMoMap.put(fumoType, fumoVal);
                    }
                }
            }*/
            Equip equip = (Equip) item;
            for (Map.Entry<String, String> kvp : equip.getEquipOption().getOptions().entrySet()) {
                int type = Integer.parseInt(kvp.getKey());
                int val = Integer.parseInt(kvp.getValue());

                if (_equippedFuMoMap.containsKey(type)) {
                    val += _equippedFuMoMap.get(type);
                }
                _equippedFuMoMap.put(type, val);
            }
        }
        /*for (int i = 0; i < 附魔技能.length; i++) {
            刷新技能(附魔技能[i], a);
        }*/
        刷新技能(a);
    }

    public void 刷新技能(boolean a) {
        Map<Integer, Integer> skillOptions = getEquippedFuMoMap().entrySet().stream()
                .filter(s -> s.getKey() >= 1000000 && s.getValue() > 0)
                .sorted(Comparator.comparingInt(s -> s.getKey()))
                .collect(LinkedHashMap::new, (k, v) -> k.put(v.getKey(), v.getValue()), LinkedHashMap::putAll);

        for (int s : 附魔技能) {
            PlayerSkill skill = PlayerSkillFactory.getSkill(s);
            int slv = getSkillLevel(s);
            if (slv >= 20) {
                changeSkillLevel(skill, skill.getMaxLevel() > 30 && slv >= 30 ? 30 : slv, 0);
            }
        }

        for (Map.Entry<Integer, Integer> kvp : skillOptions.entrySet()) {
            PlayerSkill skill = PlayerSkillFactory.getSkill(kvp.getKey());
            int slv = getSkillLevel(kvp.getKey());
            if ((skill.getMaxLevel() > 30 && slv >= 30) || (skill.getMaxLevel() > 20 && slv >= 20)) {
                changeSkillLevel(skill, (skill.getMaxLevel() > 30 ? 30 : 20) + kvp.getValue() + 所有附魔技能, 0);

                if (a) {
                    if (所有附魔技能 > 0) {
                        dropMessage(5, "" + 技能名字(kvp.getKey()) + " 等级 + " + kvp.getValue() + " + " + 所有附魔技能);
                    } else {
                        dropMessage(5, "" + 技能名字(kvp.getKey()) + " 等级 + " + kvp.getValue());
                    }
                }
            }
        }

        /*if (一转技能附魔(skillID) || 二转技能附魔20(skillID)) {
            if (getSkillLevel(skill) >= 20) {
                if (getEquippedFuMoMap().get(skillID) != null) {
                    changeSkillLevel(skill, 20, 0);
                    changeSkillLevel(skill, 20 + getEquippedFuMoMap().get(skillID) + 所有附魔技能, 0);
                    if (a) {
                        if (所有附魔技能 > 0) {
                            dropMessage(5, "" + 技能名字(skillID) + " 等级 + " + getEquippedFuMoMap().get(skillID) + " + " + 所有附魔技能);
                        } else {
                            dropMessage(5, "" + 技能名字(skillID) + " 等级 + " + getEquippedFuMoMap().get(skillID));
                        }
                    }
                } else {
                    changeSkillLevel(skill, 20, 0);
                }
            }
        } else if (二转技能附魔30(skillID)) {
            if (getSkillLevel(skill) >= 30) {
                if (getEquippedFuMoMap().get(skillID) != null) {
                    changeSkillLevel(skill, 30, 0);
                    changeSkillLevel(skill, 30 + getEquippedFuMoMap().get(skillID) + 所有附魔技能, 0);
                    if (a) {
                        if (所有附魔技能 > 0) {
                            dropMessage(5, "" + 技能名字(skillID) + " 等级 + " + getEquippedFuMoMap().get(skillID) + " + " + 所有附魔技能);
                        } else {
                            dropMessage(5, "" + 技能名字(skillID) + " 等级 + " + getEquippedFuMoMap().get(skillID));
                        }
                    }
                } else {
                    changeSkillLevel(skill, 30, 0);
                }
            }
        }*/
    }

    //cm.getPlayer().获附魔汇总值(6)
    public int 获取附魔汇总值(int fumoType) {
        int val = 0;
        if (_equippedFuMoMap.containsKey(fumoType)) {
            return _equippedFuMoMap.get(fumoType);
        }
        return 0;
    }

    public void 刷新套装() {
        枫叶套 = 0;
        for (Item IIIitem : getInventory(InventoryType.EQUIPPED)) {
            Equip equip = (Equip) IIIitem;
            if (枫叶套装(equip.getItemId())) {
                枫叶套 += 1;
            }
        }
    }

    public boolean 枫叶套装(int a) {
        switch (a) {
            //枫叶帽
            case 1002419:
            //枫叶披风
            case 1102071:
            //枫叶耳环
            case 1032035:
            //枫叶手套
            case 1082441:
                return true;
            default:
                return false;
        }
    }

    public int 战斗力评估() {
        int 战斗力 = 0;
        //生命值
        战斗力 += getMaxHp() * 2;
        //法力值
        战斗力 += getMaxMp() * 2;
        //力量
        战斗力 += getTotalStr() * 5;
        //敏捷
        战斗力 += getTotalDex() * 5;
        //智力
        战斗力 += getTotalInt() * 7;
        //运气
        战斗力 += getTotalLuk() * 5;
        //物理防御力
        战斗力 += getStat().getTotalWdef() * 11;
        //魔法防御力
        战斗力 += getStat().getTotalMdef() * 12;
        //命中率
        战斗力 += getStat().getTotalAcc() * 10;
        //回避中率
        战斗力 += getStat().getTotalEva() * 10;

        if (getEquippedFuMoMap() != null) {
            int a1 = 1;
            if (getEquippedFuMoMap().get(a1) != null) {
                战斗力 += getEquippedFuMoMap().get(a1) * 10;
            }
            int a2 = 11;
            if (getEquippedFuMoMap().get(a2) != null) {
                if (getEquippedFuMoMap().get(a2) >= 200) {
                    战斗力 += getEquippedFuMoMap().get(a2) * 120;
                } else if (getEquippedFuMoMap().get(a2) >= 100) {
                    战斗力 += getEquippedFuMoMap().get(a2) * 80;
                } else {
                    战斗力 += getEquippedFuMoMap().get(a2) * 50;
                }
            }
            int a3 = 2;
            if (getEquippedFuMoMap().get(a3) != null) {
                战斗力 += getEquippedFuMoMap().get(a3) * 10;
            }
            int a4 = 12;
            if (getEquippedFuMoMap().get(a4) != null) {
                if (getEquippedFuMoMap().get(a4) >= 200) {
                    战斗力 += getEquippedFuMoMap().get(a4) * 120;
                } else if (getEquippedFuMoMap().get(a4) >= 100) {
                    战斗力 += getEquippedFuMoMap().get(a4) * 80;
                } else {
                    战斗力 += getEquippedFuMoMap().get(a4) * 50;
                }
            }

        }
        更新战斗力(战斗力);
        return 战斗力;
    }

    public void 更新战斗力(int a) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET Combat = " + a + " WHERE id = " + id + "")) {
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
    }

    public void 签到() {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET Signin = Signin + 1 WHERE id = " + id + "")) {
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public void 签到奖励(int a) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET Signinreward = " + a + " WHERE id = " + id + "")) {
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
    }

    public int 获取签到奖励() {

        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT Signinreward as DATA FROM characters WHERE id = ? ");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取签到奖励、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
        return data;
    }

    public void 修改增值服务数据(String b, int a) {
        if (判断增值服务数据() == 0) {
            初始化增值服务数据();
        }
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE charactersin SET " + b + " = " + b + " + " + a + " WHERE id =" + id + "")) {
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
    }

    public int 获取增值服务数据(String a) {

        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT " + a + " as DATA FROM charactersin WHERE id = ? ");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data += rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取增值服务数据、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
        return data;
    }

    public void 初始化增值服务数据() {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO charactersin (id) values (?);");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
        } catch (Exception Ex) {
            System.err.println("初始化增值服务数据、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public int 判断增值服务数据() {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM charactersin WHERE id = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data += 1;
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("判断增值服务数据、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
        return data;
    }

    public int getMesoGuard() {
        return mesoGuard;
    }

    public void setMesoGuard(int s) {
        this.mesoGuard = s;
    }

    public void 修改个人记录(String b, int a) {
        if (判断个人记录() == 0) {
            初始化个人记录();
        }
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE charactersiy SET " + b + " = " + b + " + " + a + " WHERE id =" + id + "")) {
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
    }

    public int 获取个人记录(String a) {

        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT " + a + " as DATA FROM charactersiy WHERE id = ? ");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data += rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取个人记录据、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
        return data;
    }

    public void 初始化个人记录() {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO charactersiy (id) values (?);");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
        } catch (Exception Ex) {
            System.err.println("初始化个人记录、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public int 判断个人记录() {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM charactersiy WHERE id = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data += 1;
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("判断个人记录、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
        return data;
    }

    public void 同步锻造信息(String b, int a) {

        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET " + b + " = " + a + " WHERE id =?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
    }

    public int 获取锻造信息(String a) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT " + a + " as DATA FROM charactersforging WHERE id = ? ");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取锻造信息、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
        return data;
    }

    public void 修改锻造信息(String b, int a, int c) {
        if (判断锻造信息(c) == 0) {
            初始化锻造信息(c);
        }
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE charactersforging SET " + b + " = " + b + " + " + a + " WHERE id =" + c + "")) {
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
    }

    public int 获取锻造信息(String a, int b) {

        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT " + a + " as DATA FROM charactersforging WHERE id = ? ");
            ps.setInt(1, b);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取锻造信息、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
        return data;
    }

    public void 初始化锻造信息(int b) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO charactersforging (id) values (?);");
            ps.setInt(1, b);
            ps.executeUpdate();
            ps.close();
        } catch (Exception Ex) {
            System.err.println("初始化个人记录、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public int 判断锻造信息(int b) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM charactersforging WHERE id = ?");
            ps.setInt(1, b);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data += 1;
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("判断锻造信息、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
        return data;
    }

    public void 修改现金券(int a) {
        if (判断现金券() == 0) {
            初始化现金券();
        }
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts_xj SET xianjin = xianjin + " + a + " WHERE id =" + accountid + "")) {
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
    }

    public int 获取现金券() {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT xianjin as DATA FROM accounts_xj WHERE id = ? ");
            ps.setInt(1, accountid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data += rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取现金券、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
        return data;
    }

    public void 初始化现金券() {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO accounts_xj (id) values (?);");
            ps.setInt(1, accountid);
            ps.executeUpdate();
            ps.close();
        } catch (Exception Ex) {
            System.err.println("初始化现金券、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public int 判断现金券() {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts_xj WHERE id = ?");
            ps.setInt(1, accountid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data += 1;
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("判断现金券、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
        return data;
    }

    public void vip(int a) {

        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET viptime = viptime + " + a + " WHERE id =" + id + "")) {
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
    }

    public void 账号领取记录(int a) {

        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts_jilu SET viptime = viptime + " + a + " WHERE id =" + id + "")) {
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
    }

    public void 封号状态解除() {

        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET banned = 0 WHERE id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
    }

    /**
     * @return the 显示红包了类型
     */
    public int 红包类型() {
        return 显示红包类型;
    }

    /**
     * @param 显示红包了类型 the 显示红包了类型 to set
     */
    public void 红包类型(int 显示类型) {
        this.显示红包类型 = 显示红包类型;
    }

    /**
     * @return the 泡点金币
     */
    public int get泡点金币() {
        return 泡点金币;
    }

    /**
     * @param 泡点金币 the 泡点金币 to set
     */
    public void set泡点金币(int 泡点金币) {
        this.泡点金币 = 泡点金币;
    }

    /**
     * @return the 泡点点券
     */
    public int get泡点点券() {
        return 泡点点券;
    }

    /**
     * @param 泡点点券 the 泡点点券 to set
     */
    public void set泡点点券(int 泡点点券) {
        this.泡点点券 = 泡点点券;
    }

    /**
     * @return the 泡点经验
     */
    public int get泡点经验() {
        return 泡点经验;
    }

    /**
     * @param 泡点经验 the 泡点经验 to set
     */
    public void set泡点经验(int 泡点经验) {
        this.泡点经验 = 泡点经验;
    }

    public void 修改股票(int b, int a) {
        if (获取股票(b) == -1) {
            初始化股票(b);
        }
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE stocklog SET shuliang = shuliang + " + a + " WHERE id = ? && accountid = ?")) {
                ps.setInt(1, b);
                ps.setInt(2, accountid);
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public int 获取股票(int a) {
        int data = -1;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT shuliang as DATA FROM stocklog WHERE id = ? && accountid = ? ");
            ps.setInt(1, a);
            ps.setInt(2, accountid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取股票、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
        return data;
    }

    public void 初始化股票(int a) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO stocklog (accountid,id) values (?,?);");
            ps.setInt(1, accountid);
            ps.setInt(2, a);
            ps.executeUpdate();
            ps.close();
        } catch (Exception Ex) {
            System.err.println("初始化股票、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    /**
     * @return the 角色泡点经验
     */
    public int get角色泡点经验() {
        return 角色泡点经验;
    }

    /**
     * @param 角色泡点经验 the 角色泡点经验 to set
     */
    public void set角色泡点经验(int 角色泡点经验) {
        this.角色泡点经验 = 角色泡点经验;
    }

    public void 增加角色泡点经验() {
        this.角色泡点经验++;
    }

    public void 设置每日收集物品代码(int a) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET daily_itemid = ? WHERE id = ?")) {
                ps.setInt(1, a);
                ps.setInt(2, id);
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public int 获取每日收集物品代码() {
        int data = -1;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT daily_itemid as DATA FROM characters WHERE id = ? ");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取每日收集物品代码、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
        return data;
    }

    public void 设置每日访问NPC代码(int a) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET daily_npcid = ? WHERE id = ?")) {
                ps.setInt(1, a);
                ps.setInt(2, id);
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public int 获取每日访问NPC代码() {
        int data = -1;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT daily_npcid as DATA FROM characters WHERE id = ? ");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取每日访问NPC代码、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
        return data;
    }

    public int getTodayOnlineTime() {
        return todayOnlineTime;
    }

    public void setTodayOnlineTime(int todayOnlineTime) {
        this.todayOnlineTime = todayOnlineTime;
    }

    public int getTotalOnlineTime() {
        return totalOnlineTime;
    }

    public void setTotalOnlineTime(int totalOnlineTime) {
        this.totalOnlineTime = totalOnlineTime;
    }

    public int getTotalOnlineTimett() {
        return totalOnlineTimett;
    }

    public void setTotalOnlineTimett(int totalOnlineTime) {
        this.totalOnlineTimett = totalOnlineTime;
    }

    public void 设置天梯积分(int a) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET integral = integral + ? WHERE id = ?")) {
                ps.setInt(1, a);
                ps.setInt(2, id);
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public int 获取天梯积分() {
        int data = -1;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT integral as DATA FROM characters WHERE id = ? ");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取天梯积分、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
        return data;
    }

    public void 设置天梯奖励(int a) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET tianti =  ? WHERE id = ?")) {
                ps.setInt(1, a);
                ps.setInt(2, id);
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }

}
