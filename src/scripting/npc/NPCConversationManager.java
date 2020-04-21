package scripting.npc;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import client.player.Player;
import client.Client;
import client.player.PlayerJob;
import server.itens.InventoryManipulator;
import server.itens.ItemInformationProvider;
import server.shops.ShopFactory;
import client.player.PlayerStat;
import server.maps.Field;
import java.util.Map;
import java.util.Random;
import handling.channel.ChannelServer;
import community.MaplePartyCharacter;
import handling.world.service.BroadcastService;
import packet.creators.EffectPackets;
import packet.creators.PacketCreator;
import client.player.PlayerSkin;
import client.player.SpeedQuiz;
import client.player.inventory.Equip;
import client.player.inventory.EquipOption;
import client.player.inventory.Inventory;
import client.player.inventory.types.InventoryType;
import client.player.inventory.Item;
import client.player.skills.PlayerSkill;
import client.player.skills.PlayerSkillEntry;
import client.player.skills.PlayerSkillFactory;
import static client.player.skills.PlayerSkillFactory.技能名字;
import static configure.Gamemxd.df4;
import static configure.Gamemxd.一转技能附魔;
import static configure.Gamemxd.二转技能附魔20;
import static configure.Gamemxd.二转技能附魔30;
import static configure.Gamemxd.会员等级划分;
import static configure.Gamemxd.伤害记录;
import static configure.Gamemxd.副本;
import static configure.Gamemxd.合伙群;
import static configure.Gamemxd.家族等级划分;
import static configure.Gamemxd.群头衔经验加成;
import static configure.Gamemxd.装备类型;
import static configure.worldworld.伤害排行;
import static console.MsgServer.QQMsgServer.sendMsgToQQGroup;
import constants.GameConstants;
import constants.ItemConstants;
import static constants.ServerProperties.World.开服名字;
import static gui.MySQL.角色今日在线;
import static gui.MySQL.角色取会员时间;
import static gui.MySQL.角色取会员经验;
import static gui.MySQL.角色名字;
import static gui.MySQL.角色总在线;
import static console.MsgServer.QQMsgServer.显示所有附魔;
import static console.MsgServer.QQMsgServer.群活跃;
import static constants.ExperienceConstants.getExpNeededForLevel;
import static gui.logo.group.私聊;
import static gui.logo.group.群;
import handling.channel.handler.InterServerHandler;
import handling.login.handler.AutoRegister;
import handling.world.World;
import handling.world.service.FindService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.script.Invocable;
import launch.Start;
import static launch.Start.FuMoInfoMap;
import static launch.Start.大区群号1;
import static launch.Start.废弃副本开始时间;
import static launch.Start.废弃副本随机;
import static launch.Start.抽奖广播缓存;
import static launch.Start.泡点倍率;
import static launch.Start.经验倍率;
import static launch.Start.缓存序号;
import static zevms.Todayitinerary.判断行程;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import scripting.AbstractPlayerInteraction;
import static security.jiance.写入检测;
import static security.jiance.检测坐标;
import server.MapleStatEffect;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleMonsterInformationProvider;
import server.life.MonsterDropEntry;
import server.life.npc.MapleNPC;
import server.maps.FieldManager;
import server.maps.object.FieldObject;
import server.maps.object.FieldObjectType;
import server.maps.portal.Portal;
import server.partyquest.mcpq.MCParty;
import server.transitions.Boats;
import tools.FileLogger;
import tools.Pair;
import tools.TimerTools;
import static zevms.extension.修改推广员2;
import static zevms.extension.修改推广收益2;
import static zevms.extension.收取所有推广收益2;
import static zevms.extension.显示推广关系2;
import static zevms.extension.显示推广收益2;
import static zevms.extension.获取推广员2;
import static zevms.extension.获取推广收益2;
import static zevms.extension.获取推广收益log2;
import static zevms.rank.人气排行榜2;
import static zevms.rank.击杀怪物排行榜2;
import static zevms.rank.在线排行榜2;
import static zevms.rank.天梯积分排行2;
import static zevms.rank.泡点排行榜2;
import static zevms.rank.点赞排行榜2;
import static zevms.rank.等级排行榜2;
import static zevms.rank.获取天梯线排名2;
import static zevms.rank.锻造工艺排行榜2;
import static zevms.redenvelopes.显示所有红包;
import static zevms.stock.修改股票剩余数量;
import static zevms.stock.判断股票剩余数量;
import static zevms.stock.判断股票数值;
import static zevms.stock.查看股票波动记录;
import static zevms.stock.获取股票数量;
import static zevms.warehouse.仓库物品;
import static zevms.warehouse.修改仓库物品数量;
import static zevms.warehouse.取物品代码;
import static zevms.warehouse.取物品存入时间;
import static zevms.warehouse.取物品数量;
import static zevms.warehouse.所有保管物品;
import static zevms.warehouse.所有保管费;
import static zevms.warehouse.添加物品到仓库;

public class NPCConversationManager extends AbstractPlayerInteraction {

    private int npc;
    private Player p;
    private String getText;
    private byte lastMsg = -1;
    private String fileName = null;
    private MCParty mcParty;
    private List<MaplePartyCharacter> otherParty;
    private Collection<Player> characters = new LinkedHashSet<>();
    private Invocable iv;
    private int wh = 0;

    public byte getType() {
        return lastMsg;
    }

    public NPCConversationManager(Client c, int npc) {
        super(c);
        this.c = c;
        this.npc = npc;
    }

    public NPCConversationManager(Client c, int npc, int ex) {
        super(c);
        this.c = c;
        this.npc = npc;
        this.wh = ex;
    }

    public NPCConversationManager(Client c, int npc, Player p) {
        super(c);
        this.c = c;
        this.npc = npc;
        this.p = p;
    }

    public NPCConversationManager(Client c, int npc, Player p, String fileName) {
        super(c);
        this.c = c;
        this.npc = npc;
        this.p = p;
        this.fileName = fileName;
    }

    public NPCConversationManager(Client c, int npc, List<MaplePartyCharacter> otherParty, MCParty mParty) {
        super(c);
        this.c = c;
        this.npc = npc;
        this.otherParty = otherParty;
        this.mcParty = mParty;
    }

    public int getwh() {
        return this.wh;
    }

    public Invocable getIv() {
        return iv;
    }

    public int getNpc() {
        return npc;
    }

    public String getFileName() {
        return fileName;
    }

    public void dispose() {
        NPCScriptManager.getInstance().dispose(c);
    }

    //下一步
    public void sendNext(String text) {
        getClient().write(PacketCreator.GetNPCTalk(npc, (byte) 0, text, "00 01"));
    }

    //上一步
    public void sendPrev(String text) {
        getClient().write(PacketCreator.GetNPCTalk(npc, (byte) 0, text, "01 00"));
    }

    //下一步上一步
    public void sendNextPrev(String text) {
        getClient().write(PacketCreator.GetNPCTalk(npc, (byte) 0, text, "01 01"));
    }

    //无按钮
    public void sendOk(String text) {
        getClient().write(PacketCreator.GetNPCTalk(npc, (byte) 0, text, "00 00"));
    }

    //是否
    public void sendYesNo(String text) {
        getClient().write(PacketCreator.GetNPCTalk(npc, (byte) 1, text, ""));
    }

    public void sendAcceptDecline(String text) {
        getClient().write(PacketCreator.GetNPCTalk(npc, (byte) 0x0C, text, ""));
    }

    //没有按钮
    public void sendSimple(String text) {
        getClient().write(PacketCreator.GetNPCTalk(npc, (byte) 4, text, ""));
    }

    public void sendSimplel(String text, String... selections) {
        if (selections.length > 0) {
            text += "#b\r\n";
        }

        for (int i = 0; i < selections.length; i++) {
            text += "#L" + i + "#" + selections[i] + "#l\r\n";
        }
        sendSimple(text);
    }

    public void sendStyle(String text, int styles[]) {
        getClient().write(PacketCreator.GetNPCTalkStyle(npc, text, styles));
    }

    public void sendGetNumber(String text, int def, int min, int max) {
        getClient().write(PacketCreator.GetNPCTalkNum(npc, text, def, min, max));
    }

    public void sendGetText(String text) {
        getClient().write(PacketCreator.GetNPCTalkText(npc, text));
    }

    public void setGetText(String text) {
        this.getText = text;
    }

    public String getText() {
        return this.getText;
    }

    public void openShop(int id) {
        ShopFactory.getInstance().getShop(id).sendShop(c);
    }

    public void openNpc(int id) {
        dispose();
        NPCScriptManager.getInstance().start(getClient(), id, null, null, 0);
    }

    public void openNpc(int id, int ex) {
        dispose();
        NPCScriptManager.getInstance().start(getClient(), id, ex);
    }

    public void changeJob(PlayerJob job) {
        getPlayer().changeJob(job);
    }

    public void startQuest(short id) {
        /*try {
         MapleQuest.getInstance(id).forceStart(getPlayer(), npc);
         } catch (NullPointerException ex) {
         }*/
        getPlayer().getQuest().startQuest(id);
    }

    public void completeQuest(short id, String data) {
        /*try {
         MapleQuest.getInstance(id).forceComplete(getPlayer(), npc);
         } catch (NullPointerException ex) {
         }*/
        getPlayer().getQuest().completeQuest(id, data);
    }

    public void startQuest(int id) {
        /*try {
         MapleQuest.getInstance(id).forceStart(getPlayer(), npc);
         } catch (NullPointerException ex) {
         }*/
        getPlayer().getQuest().startQuest(id);
    }

    public void completeQuest(int id, String data) {
        /*try {
         MapleQuest.getInstance(id).forceComplete(getPlayer(), npc);
         } catch (NullPointerException ex) {
         }*/
        getPlayer().getQuest().completeQuest(id, data);
    }

    public boolean hasQuestCompleted(int quest) {
        return getPlayer().getQuest().hasQuestCompleted(quest);
    }

    public boolean hasQuestInProcess(int quest) {
        return getPlayer().getQuest().hasQuestInProcess(quest);
    }

    public void forfeitQuest(int id) {
        try {
            //MapleQuest.getInstance(id).forfeit(getPlayer());
        } catch (NullPointerException ex) {
        }
    }

    public void gainMeso(int gain) {
        getPlayer().gainMeso(gain, true, false, true);
    }

    public void gainExp(int gain) {
        getPlayer().gainExperience(gain, true, true);
    }

    public int getLevel() {
        return getPlayer().getLevel();
    }

    public int getGender() {
        return getPlayer().getGender();
    }

    @Override
    public int getPlayerCount(int mapid) {
        return c.getChannelServer().getMapFactory().getMap(mapid).getCharactersThreadsafe().size();
    }

    public void teachSkill(int id, int level, int masterlevel) {
        getPlayer().changeSkillLevel(PlayerSkillFactory.getSkill(id), level, masterlevel);
    }

    public int getJobId() {
        return getPlayer().getJob().getId();
    }

    public PlayerJob getJob() {
        return getPlayer().getJob();
    }

    public void clearSkills() {
        Map<PlayerSkill, PlayerSkillEntry> skills = getPlayer().getSkills();
        skills.entrySet().forEach((skill) -> {
            getPlayer().changeSkillLevel(skill.getKey(), 0, 0);
        });
    }

    public void rechargeStars() {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        Item stars = getPlayer().getInventory(InventoryType.USE).getItem((byte) 1);
        if (ItemConstants.isThrowingStar(stars.getItemId()) || ItemConstants.isBullet(stars.getItemId())) {
            stars.setQuantity(ii.getSlotMax(getClient(), stars.getItemId()));
            c.write(PacketCreator.UpdateInventorySlot(InventoryType.USE, (Item) stars));
        }
    }

    public void showEffect(String effect) {
        getPlayer().getMap().broadcastMessage(EffectPackets.ShowEffect(effect));
    }

    public void playSound(String sound) {
        getClient().getPlayer().getMap().broadcastMessage(EffectPackets.PlaySound(sound));
    }

    @Override
    public String toString() {
        return "Conversation with NPC: " + npc;
    }

    public void updateBuddyCapacity(int capacity) {
        c.getPlayer().setBuddyCapacity((byte) capacity);
    }

    public int getBuddyCapacity() {
        return getPlayer().getBuddyCapacity();
    }

    public void setHair(int hair) {
        getPlayer().setHair(hair);
        getPlayer().getStat().updateSingleStat(PlayerStat.HAIR, hair);
        getPlayer().equipChanged();
    }

    public void setFace(int face) {
        getPlayer().setFace(face);
        getPlayer().getStat().updateSingleStat(PlayerStat.FACE, face);
        getPlayer().equipChanged();
    }

    public void setSkin(int color) {
        getPlayer().setSkinColor(PlayerSkin.getById(color));
        getPlayer().getStat().updateSingleStat(PlayerStat.SKIN, color);
        getPlayer().equipChanged();
    }

    public PlayerSkin getSkin() {
        return getPlayer().getSkinColor();
    }

    public void bosslogParty(String a) {
        getPlayer().getParty().getMembers().stream()
                .filter((curChar) -> ((curChar.getPlayer().getEventInstance() == null && getPlayer().getEventInstance() == null)
                || curChar.getPlayer().getEventInstance() == getPlayer().getEventInstance()))
                .forEach((curChar) -> {
                    curChar.getPlayer().增加每日(a);
                });
    }

    @Override
    public void warpParty(int mapId) {
        Field target = getMap(mapId);
        getPlayer().getParty().getMembers().stream()
                .filter((curChar) -> ((curChar.getPlayer().getEventInstance() == null && getPlayer().getEventInstance() == null)
                || curChar.getPlayer().getEventInstance() == getPlayer().getEventInstance())).forEach((curChar) -> {
            if (curChar.getPlayer().getTrade() == null) {
                curChar.getPlayer().changeMap(target, target.getPortal(0));
            } else {
                FileLogger.printError("warp1.txt", "尝试操作，交易状态换图 " + curChar.getName());
                sendMsgToQQGroup("尝试操作，交易状态换图 " + curChar.getName() + "", 合伙群);
            }
        });
    }

    public void warpParty(int mapId, int b) {
        Field target = getMap(mapId);
        getPlayer().getParty().getMembers().stream()
                .filter((curChar) -> ((curChar.getPlayer().getEventInstance() == null && getPlayer().getEventInstance() == null)
                || curChar.getPlayer().getEventInstance() == getPlayer().getEventInstance())).forEach((curChar) -> {
            if (curChar.getPlayer().getTrade() == null) {
                curChar.getPlayer().changeMap(target, target.getPortal(b));
            } else {
                FileLogger.printError("warp2.txt", "尝试操作，交易状态换图 " + curChar.getName());
                sendMsgToQQGroup("尝试操作，交易状态换图 " + curChar.getName() + "", 合伙群);
            }
        });
    }

    public void warpParty2(int mapId, int b) {
        Field target = getMap(mapId);
        getPlayer().getParty().getMembers().stream()
                .filter((curChar) -> ((curChar.getPlayer().getEventInstance() == null && getPlayer().getEventInstance() == null)
                || curChar.getPlayer().getEventInstance() == getPlayer().getEventInstance())).forEach((MaplePartyCharacter curChar) -> {

            curChar.getPlayer().dropMessage(5, "恭喜你成功通关，3秒后进入下一关。");
            TimerTools.MapTimer.getInstance().schedule(() -> {
                if (curChar.getPlayer().getTrade() == null) {
                    curChar.getPlayer().changeMap(target, target.getPortal(b));
                } else {
                    FileLogger.printError("warp3.txt", "尝试操作，交易状态换图 " + curChar.getName());
                    sendMsgToQQGroup("尝试操作，交易状态换图 " + curChar.getName() + "", 合伙群);
                }
            }, 3000);
        });
    }

    public void Partyexp(int exp) {
        getPlayer().getParty().getMembers().stream()
                .filter((curChar) -> ((curChar.getPlayer().getEventInstance() == null && getPlayer().getEventInstance() == null)
                || curChar.getPlayer().getEventInstance() == getPlayer().getEventInstance())).forEach((curChar) -> {
            curChar.getPlayer().gainExp(exp, true, false);
        });
    }

    public void Partyfeiqunandu(int a) {
        getPlayer().getParty().getMembers().stream()
                .filter((curChar) -> ((curChar.getPlayer().getEventInstance() == null && getPlayer().getEventInstance() == null)
                || curChar.getPlayer().getEventInstance() == getPlayer().getEventInstance())).forEach((curChar) -> {
            curChar.getPlayer().记录废弃副本难度(a);
        });
    }

    public void PartydropMessage(int a, String b) {
        getPlayer().getParty().getMembers().stream()
                .filter((curChar) -> ((curChar.getPlayer().getEventInstance() == null && getPlayer().getEventInstance() == null)
                || curChar.getPlayer().getEventInstance() == getPlayer().getEventInstance())).forEach((curChar) -> {
            curChar.getPlayer().dropMessage(a, b);
        });
    }

    public void Partyexpbosslog(int exp, String a, int b) {
        getPlayer().getParty().getMembers().stream()
                .filter((curChar) -> ((curChar.getPlayer().getEventInstance() == null && getPlayer().getEventInstance() == null)
                || curChar.getPlayer().getEventInstance() == getPlayer().getEventInstance())).forEach((curChar) -> {
            if (curChar.getPlayer().判断每日(a) < b) {
                curChar.getPlayer().增加每日(a);
                curChar.getPlayer().gainExp(exp, true, false);
            }
        });
    }

    public void Partymeso(int meso) {
        getPlayer().getParty().getMembers().stream()
                .filter((curChar) -> ((curChar.getPlayer().getEventInstance() == null && getPlayer().getEventInstance() == null)
                || curChar.getPlayer().getEventInstance() == getPlayer().getEventInstance())).forEach((curChar) -> {
            curChar.getPlayer().gainMeso(meso, true);
        });
    }

    public void Partymesobosslog(int meso, String a, int b) {
        getPlayer().getParty().getMembers().stream()
                .filter((curChar) -> ((curChar.getPlayer().getEventInstance() == null && getPlayer().getEventInstance() == null)
                || curChar.getPlayer().getEventInstance() == getPlayer().getEventInstance())).forEach((curChar) -> {
            if (curChar.getPlayer().判断每日(a) < b) {
                curChar.getPlayer().增加每日(a);
                curChar.getPlayer().gainMeso(meso, true);
            }
        });
    }

    public String getName() {
        return getPlayer().getName();
    }

    public void gainFame(int fame) {
        getPlayer().gainFame(fame);
    }

    public void showWrongEffect(int a) {
        showWrongEffect(getMapId(), a);
    }

    public void showWrongEffect2(int a) {
        showWrongEffect2(getMapId(), a);
    }

    public void showWrongEffect(int mapId, int a) {
        Field map = getMap(mapId);
        if (a == 0) {
            map.broadcastMessage(EffectPackets.ShowEffect("quest/party/wrong_kor"));
            map.broadcastMessage(EffectPackets.PlaySound("Party1/Failed"));
        } else {
            map.broadcastMessage(EffectPackets.ShowEffect("quest/party/clear"));
            map.broadcastMessage(EffectPackets.PlaySound("Party1/Clear"));
        }
    }

    public void showWrongEffect2(int mapId, int a) {
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            Player curChar = c.getChannelServer().getPlayerStorage().getCharacterByName(chr.getName());
            if ((curChar.getEventInstance() == null && c.getPlayer().getEventInstance() == null) || curChar.getEventInstance() == getPlayer().getEventInstance()) {
                if (a == 0) {
                    curChar.getClient().announce(EffectPackets.ShowEffect("quest/party/wrong_kor"));
                    curChar.getClient().announce(EffectPackets.PlaySound("Party1/Failed"));
                } else {
                    curChar.getClient().announce(EffectPackets.ShowEffect("quest/party/clear"));
                    curChar.getClient().announce(EffectPackets.PlaySound("Party1/Clear"));
                }
            }
        }
    }

    public void showClearEffect(String b, int a) {
        showClearEffect(getMapId(), b, a);
    }

    public void showClearEffect(int mapId, String mapObj, int newState) {
        showClearEffect(true, mapId, mapObj, newState);
    }

    public void showClearEffect(boolean hasGate, int mapId, String mapObj, int newState) {
        Field map = getMap(mapId);
        if (hasGate) {
            map.broadcastMessage(EffectPackets.EnvironmentChange(mapObj, newState));
        }
    }

    public void getFame() {
        getPlayer().getFame();
    }

    public byte getLastMsg() {
        return lastMsg;
    }

    public final void setLastMsg(final byte last) {
        this.lastMsg = last;
    }

    public String startSpeedQuiz() {
        if (getPlayer().getSpeedQuiz() != null) {
            getPlayer().setSpeedQuiz(null);
            return "Ahh..it seemed that something was broken. Please let the admins know about this issue right away!";
        }
        final long time = c.getPlayer().getLastSpeedQuiz();
        final long now = System.currentTimeMillis();
        if (time > 0) {
            boolean can = (time + 3600000) < now;
            if (!can) {
                int remaining = (int) ((((time + 3600000) - now) / 1000) / 60);
                return "You've already tried the speed quiz in the past hour. Please come back again in " + remaining + " minutes.";
            }
        }
        getPlayer().setLastSpeedQuiz(now);
        getPlayer().setSpeedQuiz(new SpeedQuiz(c, npc));
        return null;
    }

    public void warpPartyWithExp(int mapId, int exp) {
        Field target = getMap(mapId);
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            Player curChar = c.getChannelServer().getPlayerStorage().getCharacterByName(chr.getName());
            if ((curChar.getEventInstance() == null && c.getPlayer().getEventInstance() == null) || curChar.getEventInstance() == getPlayer().getEventInstance()) {
                curChar.changeMap(target, target.getPortal(0));
                curChar.gainExp(exp, true, false);
            }
        }
    }

    public void warpPartyWithExpMeso(int mapId, int exp, int meso) {
        Field target = getMap(mapId);
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            Player curChar = c.getChannelServer().getPlayerStorage().getCharacterByName(chr.getName());
            if ((curChar.getEventInstance() == null && c.getPlayer().getEventInstance() == null) || curChar.getEventInstance() == getPlayer().getEventInstance()) {
                curChar.changeMap(target, target.getPortal(0));
                curChar.gainExp(exp, true, false);
                curChar.gainMeso(meso, true);
            }
        }
    }

    public void warpAllInMap(int mapid, int portal) {
        Field outMap;
        FieldManager mapFactory;
        mapFactory = c.getChannelServer().getMapFactory();
        outMap = mapFactory.getMap(mapid);
        for (Player p : outMap.getCharactersThreadsafe()) {
            mapFactory = p.getChannelServer().getMapFactory();
            p.getClient().getPlayer().changeMap(outMap, outMap.getPortal(portal));
            outMap = mapFactory.getMap(mapid);
            p.getClient().getPlayer().getEventInstance().unregisterPlayer(p.getClient().getPlayer());
        }
    }

    public void warpRandom(int mapid) {
        Field target = c.getChannelServer().getMapFactory().getMap(mapid);
        Random rand = new Random();
        Portal portal = target.getPortal(rand.nextInt(target.getPortals().size()));
        getPlayer().changeMap(target, portal);
    }

    public int itemQuantity(int itemid) {
        InventoryType type = ItemInformationProvider.getInstance().getInventoryType(itemid);
        int possesed = getPlayer().getInventory(type).countById(itemid);
        return possesed;
    }

    public void challengeParty(MCParty party, int field) {
        Player leader = null;
        Field map = c.getChannelServer().getMapFactory().getMap(980000100 + 100 * field);
        for (FieldObject mmo : map.getAllPlayer()) {
            Player mc = (Player) mmo;
            if (mc.getParty() == null || mc.getMCPQParty() == null) {
                sendOk("We could not find a party in this room.\r\nProbably the group was designed inside the room!");
                return;
            }
            if (mc.getParty().getLeader().getId() == mc.getId()) {
                leader = mc;
                break;
            }
        }
        if (leader != null && leader.getMCPQField() != null) {
            if (!leader.isChallenged()) {
                List<MaplePartyCharacter> members = new LinkedList<>();
                for (MaplePartyCharacter fucker : c.getPlayer().getParty().getMembers()) {
                    members.add(fucker);
                }
                NPCScriptManager.getInstance().start("sendChallenge", leader.getClient(), npc, members, party);
            } else {
                sendOk("The other party is responding to a different challenge.");
            }
        } else {
            sendOk("Could not find the leader!");
        }
    }

    public void resetReactors() {
        getPlayer().getMap().resetReactors();
    }

    public Player getCharByName(String namee) {
        try {
            return getClient().getChannelServer().getPlayerStorage().getCharacterByName(namee);
        } catch (Exception e) {
            return null;
        }
    }

    public void addRandomItem(int id) {
        ItemInformationProvider i = ItemInformationProvider.getInstance();
        InventoryManipulator.addFromDrop(getClient(), i.randomizeStats((Equip) i.getEquipById(id)), "", true);
    }

    public void GachaMessage(int itemid) throws Exception {
        GachaMessage(itemid, false);
    }

    public void GachaMessage(int itemid, boolean rare) throws Exception {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        InventoryType type = ii.getInventoryType(itemid);
        Item item = getPlayer().getInventory(type).findById(itemid);
        String itemName = ItemInformationProvider.getInstance().getName(item.getItemId());
        int[] gacMap = {100000000, 101000000, 102000000, 103000000, 105040300, 800000000, 809000101, 809000201, 600000000, 120000000};
        String mapName = getClient().getPlayer().getMapName(gacMap[(getNpc() != 9100117 && getNpc() != 9100109) ? (getNpc() - 9100100) : getNpc() == 9100109 ? 8 : 9]);
        if (!rare) {
            getPlayer().getMap().broadcastMessage(PacketCreator.ServerNotice(2, getPlayer().getName() + " : gained a(n) " + itemName + " from the " + mapName + " Gachapon! Congrats!"));
        } else {
            BroadcastService.broadcastMessage(c.getWorld(), PacketCreator.ItemMegaphone(getPlayer().getName() + " :  gained a(n) item from " + mapName + " Gachapon! Congrats!", false, c.getChannel(), item));
        }
    }

    public void changeJobById(int a) {
        getPlayer().changeJob(PlayerJob.getById(a));
    }

    @Override
    public boolean isQuestCompleted(int quest) {
        /*try {
            //return getQuestStatus(quest) == MapleQuestStatus.Status.COMPLETED;
            return getPlayer().getQuest().hasQuestCompleted(quest);
        } catch (NullPointerException e) {
            return false;
        }*/
        return getPlayer().getQuest().hasQuestCompleted(quest);
    }

    @Override
    public boolean isQuestStarted(int quest) {
        /*try {
            return getQuestStatus(quest) == MapleQuestStatus.Status.STARTED;
        } catch (NullPointerException e) {
            return false;
        }*/
        return getPlayer().getQuest().hasQuestInProcess(quest);
    }

    @Override
    public int countMonster() {
        Field map = c.getPlayer().getMap();
        double range = Double.POSITIVE_INFINITY;
        List<FieldObject> monsters = map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(FieldObjectType.MONSTER));
        return monsters.size();
    }

    @Override
    public int countReactor() {
        Field map = c.getPlayer().getMap();
        double range = Double.POSITIVE_INFINITY;
        List<FieldObject> reactors = map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(FieldObjectType.REACTOR));
        return reactors.size();
    }

    public int getDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK);
        return day;
    }

    //使用物品
    public void giveNPCBuff(Player p, int itemID) {
        ItemInformationProvider mii = ItemInformationProvider.getInstance();
        MapleStatEffect statEffect = mii.getItemEffect(itemID);
        statEffect.applyTo(p);
    }

    public void reloadChar() {
        getPlayer().getClient().write(PacketCreator.GetCharInfo(getPlayer()));
        getPlayer().getMap().removePlayer(getPlayer());
        getPlayer().getMap().addPlayer(getPlayer());
    }

    public short gainItemRetPos(int itemid) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        Item it = ii.getEquipById(itemid);
        short ret = getPlayer().getInventory(InventoryType.EQUIP).addItem(it);
        c.write(PacketCreator.AddInventorySlot(InventoryType.EQUIP, it));
        c.write(PacketCreator.GetShowItemGain(itemid, (short) 1, true));
        c.write(PacketCreator.EnableActions());
        return ret;
    }

    public void serverNotice(String msg) {
        getPlayer().sendServerNotice(msg);
    }

    public boolean isPlayerInstance() {
        return c.getPlayer().getEventInstance() != null;
    }

    public int getMeso() {
        return getPlayer().getMeso();
    }

    public int getPlayersInMap(int mapId) {
        return (getClient().getChannelServer().getMapFactory().getMap(mapId).getAllPlayer().size());
    }

    public void showInventory(int type) {
        String send = "";
        Inventory invy = c.getPlayer().getInventory(InventoryType.getByType((byte) type));
        send = invy.list().stream().map((item) -> "#L" + item.getPosition() + "##v" + item.getItemId() + "# Quantity: #b" + item.getQuantity() + "#k#l\\r\\n").reduce(send, String::concat);
        sendSimple(send);
    }

    public String getInventory2(int type) {
        String send = "";
        Inventory invy = c.getPlayer().getInventory(InventoryType.getByType((byte) type));
        send = invy.list().stream().map((item) -> "#L" + item.getPosition() + "##v" + item.getItemId() + "# Quantity: #b" + item.getQuantity() + "#k#l\\r\\n").reduce(send, String::concat);
        return send;
    }

    public Inventory getInventory(int type) {//判断背包
        return c.getPlayer().getInventory(InventoryType.getByType((byte) type));
    }

    public Item getItem(int slot, int type) {
        Inventory invy = c.getPlayer().getInventory(InventoryType.getByType((byte) type));
        for (Item item : invy.list()) {
            if (item.getPosition() == slot) {
                return item;
            }
        }
        return null;
    }

    public void cleanItemBySlot(int slot, int type, int quantity) {
        InventoryManipulator.removeFromSlot(c, InventoryType.getByType((byte) type), (short) slot, (short) quantity, true);
    }

    public int calcAvgLvl(int map) {
        int num = 0;
        int avg = 0;
        for (FieldObject mmo : c.getChannelServer().getMapFactory().getMap(map).getAllPlayer()) {
            avg += ((Player) mmo).getLevel();
            num++;
        }
        avg /= num;
        return avg;
    }

    public Player getChrById(int id) {
        ChannelServer cs = c.getChannelServer();
        return cs.getPlayerStorage().getCharacterById(id);
    }

    public Player getSender() {
        return this.p;
    }

    @Override
    public List<Player> getPartyMembers() {
        if (getPlayer().getParty() == null) {
            return null;
        }
        List<Player> chars = new LinkedList<>();
        getPlayer().getParty().getMembers().stream()
                .filter(p -> p.getPlayer() != null && p.isOnline())
                .forEach(p -> chars.add(p.getPlayer()));
        return chars;
    }

    public boolean isUsingOldPqNpcStyle() {
        return GameConstants.USE_OLD_GMS_STYLED_PQ_NPCS && this.getPlayer().getParty() != null;
    }

    public int partyMembersInMap() {
        int inMap = 0;
        inMap = getPlayer().getMap().getCharactersThreadsafe().stream().filter((char2) -> (char2.getParty() == getPlayer().getParty())).map((_item) -> 1).reduce(inMap, Integer::sum);
        return inMap;
    }

    public void closeDoor(int mapid) {
        getClient().getChannelServer().getMapFactory().getMap(mapid).setReactorState();
    }

    public void openDoor(int mapid) {
        getClient().getChannelServer().getMapFactory().getMap(mapid).resetReactors();
    }

    public void resetMap(int mapid) {
        getClient().getChannelServer().getMapFactory().getMap(mapid).resetReactors();
    }

    public void resetStats() {
        int totAp = getPlayer().getStat().getStr() + getPlayer().getStat().getDex() + getPlayer().getStat().getLuk() + getPlayer().getStat().getInt() + getPlayer().getStat().getRemainingAp();
        getPlayer().getStat().setStr(4);
        getPlayer().getStat().setDex(4);
        getPlayer().getStat().setLuk(4);
        getPlayer().getStat().setInt(4);
        getPlayer().getStat().setRemainingAp(totAp - 16);
        getPlayer().getStat().updateSingleStat(PlayerStat.STR, 4);
        getPlayer().getStat().updateSingleStat(PlayerStat.DEX, 4);
        getPlayer().getStat().updateSingleStat(PlayerStat.LUK, 4);
        getPlayer().getStat().updateSingleStat(PlayerStat.INT, 4);
        getPlayer().getStat().updateSingleStat(PlayerStat.AVAILABLEAP, totAp);
    }

    /*public boolean isMorphed() {
     boolean morph = false;
     Integer morphed = getPlayer().getBuffedValue(BuffStat.MORPH);
     if (morphed != null) {
     morph = true;
     }
     return morph;
     }
        
     public int getMorphValue() {
     try {
     int morphid = getPlayer().getBuffedValue(BuffStat.MORPH).intValue();
     return morphid;
     } catch (NullPointerException n) {
     return -1;
     }
     }*/
    public final void sendRPS() {
        c.write(PacketCreator.GetRockPaperScissorsMode((byte) 8, -1, -1, -1));
    }

    public void displayDrops(int selection) {
        if (selection < 1) {
            sendOk("没有那个名字的怪物。");
            dispose();
            return;
        }
        MapleMonster job = MapleLifeFactory.getMonster(selection);
        String text = "";
        List ranks = MapleMonsterInformationProvider.getInstance().retrieveDrop(job.getId());
        if ((ranks == null) || (ranks.size() <= 0)) {
            sendOk("没有找到下落。");
        } else {
            int num = 0;
            ItemInformationProvider ii = ItemInformationProvider.getInstance();
            for (int i = 0; i < ranks.size(); i++) {
                if ((i >= 1) && (i < ranks.size())) {

                    MonsterDropEntry de = (MonsterDropEntry) ranks.get(i);
                    String name = ii.getName(de.itemId);
                    if ((de.chance > 0) && (name != null) && (name.length() > 0) && ((de.questid <= 0))) {
                        if (num == 0) {
                            text = new StringBuilder().append(text).append("怪物 : #b").append(job.getStats().getName()).append("#k\r\n").toString();
                            text = new StringBuilder().append(text).append("等级 : #b").append(job.getStats().getLevel()).append("#k\r\n").toString();
                            text = new StringBuilder().append(text).append("生命 : #b").append(job.getStats().getHp()).append("#k\r\n").toString();
                            text = new StringBuilder().append(text).append("经验 : #b").append(job.getStats().getExp()).append("#k\r\n").toString();

                            text = new StringBuilder().append(text).append("----------------------------------------------\r\n").toString();
                        }
                        double percent = 0.0D;
                        percent = Integer.valueOf(de.chance == 999999 ? 1000000 : de.chance).doubleValue() / 10000.0D * c.getChannelServer().getExpRate();
                        if (percent >= 100.0D) {
                            percent = 100.0D;
                        }

                        String quantity = new StringBuilder().append("quantity.").toString();
                        if (quantity.equals(" ")) {
                            text = new StringBuilder().append(text).append("#i").append(de.itemId).append("# #b#t").append(de.itemId).append("##k 掉落率 ").append(df4.format(percent)).append(" % \r\n").append(/*(de.questid > 0) && (MapleQuest.getInstance(de.questid).getName().length() > 0) ? new StringBuilder().append("#eQuest Req#n: ").append(MapleQuest.getInstance(de.questid).getName()).append(" \r\n").toString() : */"\r\n").toString();
                        } else {
                            text = new StringBuilder().append(text).append("#i").append(de.itemId).append("# #b#t").append(de.itemId).append("##k 掉落率 ").append(df4.format(percent)).append(" % \r\n").append(/*(de.questid > 0) && (MapleQuest.getInstance(de.questid).getName().length() > 0) ? new StringBuilder().append("#eQuest Req#n: ").append(MapleQuest.getInstance(de.questid).getName()).append(" \r\n").toString() : */"\r\n").toString();
                        }
                        num++;
                    }

                    if (num == 0) {
                        sendOk("找不到下落。");
                    }
                }
            }
            List<MonsterDropEntry> dropEntry = new ArrayList(MapleMonsterInformationProvider.getInstance().retrieveDrop(selection));
            int fff = 0;
            int itemid = 0;
            double percent = 0.0D;
            if (fff == 0) {
                for (MonsterDropEntry dr : dropEntry) {
                    if (dr.itemId == 0) {
                        if (dr.Minimum * c.getChannelServer().getMesoRate() < 99) {
                            itemid = 4031039;
                        } else if ((dr.Minimum * c.getChannelServer().getMesoRate() > 100) && (dr.Minimum * c.getChannelServer().getMesoRate() < 999)) {
                            itemid = 4031040;
                        } else if (dr.Minimum * c.getChannelServer().getMesoRate() > 999) {
                            itemid = 4031041;
                        }
                        percent = Integer.valueOf(dr.chance == 999999 ? 1000000 : dr.chance * c.getChannelServer().getDropRate()).doubleValue() / 10000.0D;
                        if (percent >= 100.0D) {
                            percent = 100.0D;
                        }
                        text = new StringBuilder().append(text).append("#i ").append(itemid).append("# #b金币#k ").append(dr.Minimum * c.getChannelServer().getDropRate()).append(" - ").append(dr.Maximum * c.getChannelServer().getMesoRate()).append(" 掉落率: ").append(df4.format(percent)).append(" %").toString();
                        fff = 1;
                    }
                }
            }
            sendOk(text);
        }
    }

    public void searchToItemId(String message) {
        int itemid = Integer.parseInt(message);
        if (itemid == 0 || itemid < 0) {
            sendOk("您必须输入代码。");
            dispose();
            return;
        }
        Connection con = null;
        try {
            String msg = "";
            List<String> retMobs = new ArrayList<>();
            MapleData data = null;
            MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider("String");
            data = dataProvider.getData("Mob.img");

            String nameItem = ItemInformationProvider.getInstance().getName(itemid);

            msg = new StringBuilder().append("#v" + itemid + "#\r\n物品代码#n: #b").append(itemid).append("#k\r\n物品名称#n: #b").append(nameItem).append("#k\r\n").toString();
            msg = new StringBuilder().append(msg).append("----------------------------------------------\r\n").toString();
            List<Pair<Integer, String>> mobPairList = new LinkedList<>();
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT dropperid FROM drop_data WHERE itemid = ?");
            ps.setInt(1, itemid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int mobn = rs.getInt("dropperid");
                for (MapleData mobIdData : data.getChildren()) {
                    int mobIdFromData = Integer.parseInt(mobIdData.getName());
                    String mobNameFromData = MapleDataTool.getString(mobIdData.getChildByPath("name"), "NO-NAME");
                    mobPairList.add(new Pair<>(mobIdFromData, mobNameFromData));
                }
                for (Pair<Integer, String> mobPair : mobPairList) {
                    if (mobPair.getLeft() == (mobn) && !retMobs.contains(mobPair.getRight())) {
                        retMobs.add(mobPair.getRight());
                    }
                }
            }
            rs.close();
            ps.close();
            if (retMobs != null && retMobs.size() > 0) {
                int num = 1;
                for (String singleRetMob : retMobs) {
                    if (!"????".equals(singleRetMob)) {
                        msg = new StringBuilder().append(msg).append("[").append(num).append("] #b").append(singleRetMob).append("#k\r\n").toString();
                        num++;
                    }
                }
                sendOk(msg);
            } else {
                sendOk("没有怪物掉落这个物品。");
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

    public void searchToNameMob(String name) {
        if (name.equals("")) {
            sendOk("You need to put the name!");
            dispose();
            return;
        }
        MapleData data = null;
        String msg = "";
        MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider("String");
        List<String> retMobs = new ArrayList<>();
        data = dataProvider.getData("Mob.img");
        List<Pair<Integer, String>> mobPairList = new LinkedList<>();
        for (MapleData mobIdData : data.getChildren()) {
            mobPairList.add(new Pair<>(Integer.parseInt(mobIdData.getName()), MapleDataTool.getString(mobIdData.getChildByPath("name"), "NO-NAME")));
        }
        msg = new StringBuilder().append(msg).append("Name of monsters found:\r\n").toString();
        for (Pair<Integer, String> mobPair : mobPairList) {
            if (mobPair.getRight().toLowerCase().contains(name.toLowerCase())) {
                retMobs.add("#eID#n: " + mobPair.getLeft() + " | " + ("#eMonster Name#n: ") + mobPair.getRight());
            }
        }
        if (retMobs != null && retMobs.size() > 0) {
            int num = 1;
            for (String singleRetMob : retMobs) {
                msg = new StringBuilder().append(msg).append("[").append(num).append("] ").append(singleRetMob).append("\r\n").toString();
                num++;
            }
            sendOk(msg);
        } else {
            sendOk("No monster found!");
        }
    }

    public void searchToNameItem(String name) {
        if (name.equals("")) {
            sendOk("You need to put the name!");
            dispose();
            return;
        }
        String msg = "";
        List<String> retItems = new ArrayList<>();
        ItemInformationProvider miip = ItemInformationProvider.getInstance();
        msg = new StringBuilder().append(msg).append("Name of found items:\r\n").toString();
        for (Map.Entry<Integer, String> itemEntry : miip.getAllItems().entrySet()) {
            if (itemEntry.getValue().toLowerCase().contains(name.toLowerCase())) {
                int id = itemEntry.getKey();
                retItems.add("#eID#n: " + id + " | " + ("#eItem Name#n: ") + miip.getName(id));
            }
        }
        if (retItems != null && retItems.size() > 0) {
            int num = 1;
            for (String singleRetItem : retItems) {
                msg = new StringBuilder().append(msg).append("[").append(num).append("] ").append(singleRetItem).append("\r\n").toString();
                num++;
            }
            sendOk(msg);
        } else {
            sendOk("No items found!");
        }
    }

    public void listMonsters() {
        String monster = "";
        String text = "请选择你要查看的怪物:#b\r\n";
        ArrayList monstersInMap = new ArrayList();
        List<FieldObject> monsters = getPlayer().getMap().getMapObjectsInRange(this.c.getPlayer().getPosition(), (1.0D / 0.0D), Arrays.asList(new FieldObjectType[]{FieldObjectType.MONSTER}));
        for (FieldObject curmob : monsters) {
            MapleMonster monsterlist = null;
            if (monsterlist == null) {
                monsterlist = (MapleMonster) curmob;
            }
            if (!monstersInMap.contains(monsterlist.getId())) {
                monstersInMap.add(monsterlist.getId());
            }
        }

        for (int i = 0; i < monstersInMap.size(); i++) {
            monster = MapleLifeFactory.getMonster(((Integer) monstersInMap.get(i))).getName();
            if (monster != "MISSINGNO") {
                text = new StringBuilder().append(text).append("#L").append(monstersInMap.get(i)).append("#").append(monster).append("#l\r\n").toString();
            }
        }

        if (monster.isEmpty()) {
            sendOk("地图上找不到怪物。");
            dispose();
        } else {
            sendSimple(text);
        }
    }

    private int getMobsIDFromName(String search) {
        MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider("String");
        MapleData data = dataProvider.getData("Mob.img");
        List<Pair<Integer, String>> mobPairList = new LinkedList<>();
        for (MapleData mobIdData : data.getChildren()) {
            int mobIdFromData = Integer.parseInt(mobIdData.getName());
            String mobNameFromData = MapleDataTool.getString(mobIdData.getChildByPath("name"), "NO-NAME");
            mobPairList.add(new Pair<>(mobIdFromData, mobNameFromData));
        }
        for (Pair<Integer, String> mobPair : mobPairList) {
            if (mobPair.getRight().toLowerCase().equals(search.toLowerCase()) && mobPair.getLeft() > 0) {
                return mobPair.getLeft();
            }
        }
        return 0;
    }

    public int getMobId(String mobname) {
        return getMobsIDFromName(mobname);
    }

    public String 冒险岛名称() {
        return 开服名字;
    }

    public String 玩家名字() {
        return getName();
    }

    public void 角色存档() {
        getPlayer().saveDatabase();
    }

    public String 职业() {
        return 职业(getPlayer().getJob().getId());
    }

    public String 绑定QQ() {
        return getPlayer().getQQ();
    }

    public String 群活跃等级() {
        return 群活跃(getPlayer().getWorldId(), getPlayer().getQQ());
    }

    public int 会员经验() {
        return getPlayer().getvip();
    }

    public int 会员等级() {
        return 会员等级划分(getPlayer().getvip());
    }

    public int 会员剩余时间() {
        return 角色取会员经验(getPlayer().getId());
    }

    public String 取角色名字(int a) {
        return 角色名字(a);
    }

    public String 家族地位(int job) {
        switch (job) {
            case 1:
                return "族长";
            case 2:
                return "精英";
            case 3:
                return "成员";
            default:
                return "成员";
        }
    }

    public String 职业(int job) {
        switch (job) {
            case 0:
                return "新手";
            case 100:
                return "战士[战士]";
            case 110:
                return "战士[剑客]";
            case 111:
                return "战士[勇士]";
            case 120:
                return "战士[准骑士]";
            case 121:
                return "战士[骑士]";
            case 130:
                return "战士[枪战士]";
            case 131:
                return "战士[龙骑士]";
            case 200:
                return "魔法师[魔法师]";
            case 210:
                return "魔法师[火毒法师]";
            case 211:
                return "魔法师[火毒巫师]";
            case 220:
                return "魔法师[冰雷法师]";
            case 221:
                return "魔法师[冰雷巫师]";
            case 230:
                return "魔法师[牧师]";
            case 231:
                return "魔法师[祭司]";
            case 300:
                return "弓箭手[弓箭手]";
            case 310:
                return "弓箭手[猎人]";
            case 311:
                return "弓箭手[射手]";
            case 320:
                return "弓箭手[弩弓手]";
            case 321:
                return "弓箭手[游侠]";
            case 400:
                return "飞侠[飞侠]";
            case 410:
                return "飞侠[刺客]";
            case 411:
                return "飞侠[无影人]";
            case 420:
                return "飞侠[侠客]";
            case 421:
                return "飞侠[独行客]";
            case 500:
                return "管理员";
            default:
                return "未知职业";
        }
    }

    public int 升级经验() {
        return getExpNeededForLevel(getPlayer().getLevel());
    }

    public int 升级经验(int a) {
        return getExpNeededForLevel(a);
    }

    public String 大区() {
        return 大区(getPlayer().getWorldId());
    }

    public String 大区(int a) {
        switch (a) {
            case 0:
                return "蓝蜗牛";
            case 1:
                return "蘑菇仔";
            case 2:
                return "绿水灵";
            case 3:
                return "漂漂猪";
            case 4:
                return "小青蛇";
            case 5:
                return "红螃蟹";
            case 6:
                return "大海龟";
            case 7:
                return "章鱼怪";
            case 8:
                return "顽皮猴";
            case 9:
                return "星精灵";
            case 10:
                return "胖企鹅";
            case 11:
                return "白雪人";
            case 12:
                return "石头人";
            case 13:
                return "紫色猫";
            case 14:
                return "大灰狼";
            case 15:
                return "小白兔";
            case 16:
                return "喷火龙";
            case 17:
                return "火野猪";
            case 18:
                return "青鳄鱼";
            case 19:
                return "花蘑菇";
            default:
                return "未知";
        }
    }

    public String 等级排行榜() {
        return 等级排行榜2(getPlayer().getWorldId());
    }

    public String 等级排行榜(int a) {
        return 等级排行榜2(a);
    }

    public String 锻造工艺排行榜() {
        return 锻造工艺排行榜2(getPlayer().getWorldId());
    }

    public String 锻造工艺排行榜(int a) {
        return 锻造工艺排行榜2(a);
    }

    public String 在线排行榜() {
        return 在线排行榜2(getPlayer().getWorldId());
    }

    public String 在线排行榜(int a) {
        return 在线排行榜2(a);
    }

    public String 点赞排行榜() {
        return 点赞排行榜2(getPlayer().getWorldId());
    }

    public String 点赞排行榜(int a) {
        return 点赞排行榜2(a);
    }

    public String 击杀怪物排行榜() {
        return 击杀怪物排行榜2(getPlayer().getWorldId());
    }

    public String 击杀怪物排行榜(int a) {
        return 击杀怪物排行榜2(a);
    }

    public String 人气排行榜() {
        return 人气排行榜2(getPlayer().getWorldId());
    }

    public String 人气排行榜(int a) {
        return 人气排行榜2(a);
    }

    public String 泡点排行榜() {
        return 泡点排行榜2(getPlayer().getWorldId());
    }

    public String 泡点排行榜(int a) {
        return 泡点排行榜2(a);
    }

    public void 点赞(int a) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET dianzhan = dianzhan + 1 WHERE id = " + a + "")) {
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

    public boolean 判断角色名字(String txt) {
        boolean xx = false;
        Boolean isExists = AutoRegister.getcharacters(txt);
        if (!isExists) {
            return true;
        }
        return xx;
    }

    public void 修改角色名字(String txt) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement pss = con.prepareStatement("UPDATE characters SET name = ? WHERE id = ?");
            try {
                pss.setString(1, txt);
                pss.setInt(2, getPlayer().getId());
                pss.executeUpdate();
                getPlayer().getClient().close();
                //getPlayer().getClient().disconnect(true, false);
                //刷新();
            } finally {
                pss.close();
            }
        } catch (SQLException e) {
            System.err.println("修改角色名字。" + e);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public String getItemName(int id) {//物品名字
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        return ii.getName(id);
    }

    public String 物品名字(int id) {//物品名字
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        return ii.getName(id);
    }

    public int 装备等级(int id) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        return ii.getReqLevel(id);
    }

    public int 装备属性(int id, String a) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        return ii.基础属性(id, a);
    }

    public int 装备出售价格(int id) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        return (int) ii.getPrice(id);
    }

    public int 物品叠加数量(int id) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        return ii.getSlotMax(id);
    }

    public int 物品拥有数量(int itemid) {
        return getPlayer().getItemQuantity(itemid, false);
    }

    public boolean 物品任务类型(int id) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        return ii.isQuestItem(id);
    }

    public boolean 物品时装类型(int id) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        return ii.isCash(id);
    }

    public String 装备的类型(int id) {//装备类型
        return 装备类型(id);
    }

    public void dropMessage(int a, String txt) {
        getPlayer().dropMessage(a, txt);
    }

    public int 今日在线(int a) {
        return 角色今日在线(a);
    }

    public int 今日在线() {
        return 角色今日在线(getPlayer().getId());
    }

    public int 总在线(int a) {
        return 角色总在线(a);
    }

    public int 总在线() {
        return 角色总在线(getPlayer().getId());
    }

    public void 刷新() {
        getPlayer().fakeRelog();
    }

    public int getbosslog(String a) {
        return getPlayer().判断每日(a);
    }

    public int getBossLog(String a) {
        return getPlayer().判断每日(a);
    }

    public void setbosslog(String a) {
        getPlayer().增加每日(a);
    }

    public void setBossLog(String a) {
        getPlayer().增加每日(a);
    }

    /**
     * <角色是否在线>
     */
    public boolean 在线() {
        return 在线(getPlayer().getName());
    }

    public boolean 在线(String a) {
        return FindService.findChannel(a, getPlayer().getWorldId()) > 0;
    }

    /**
     * <判断家族名字>
     */
    public boolean 判断家族名字(String txt) {
        boolean xx = false;
        Boolean isExists = AutoRegister.判断家族名字(txt);
        if (!isExists) {
            return true;
        }
        return xx;
    }

    /**
     * <创建家族>
     */
    public void 创建家族(String txt) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO jiazu (jiazuname,jiazuworld,jiazuid) VALUES (?,?,?)");
            ps.setString(1, txt);
            ps.setInt(2, getPlayer().getWorldId());
            ps.setInt(3, getPlayer().getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("创建家族失败" + ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        设置家族编号(getPlayer().getId());
    }

    /**
     * <设置角色家族 0 = 无家族>
     */
    public void 设置家族编号(int a) {
        设置家族编号(a, getPlayer().getId());
    }

    public void 设置家族编号(int a, int b) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET jiazu = " + a + " WHERE id = " + b + "")) {
                ps.executeUpdate();
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

        for (ChannelServer cserv : getPlayer().getWorld().getChannels()) {
            for (Player chr : cserv.getPlayerStorage().getAllCharacters()) {
                if (chr == null) {
                    continue;
                }
                if (chr.getId() == b) {
                    chr.setjiazu(a);
                }
            }
        }
    }

    public void 增加家族经验(int a) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE jiazu SET jiazugp = jiazugp +" + a + " WHERE jiazuid = " + 判断家族() + "")) {
                ps.executeUpdate();
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
     * <家族通知信息,让所有家族成员看到>
     */
    public void 家族通知信息(String a) {
        for (ChannelServer cserv : getPlayer().getWorld().getChannels()) {
            for (Player chr : cserv.getPlayerStorage().getAllCharacters()) {
                if (chr == null) {
                    continue;
                }
                if (chr.getjiazu() == 判断家族()) {
                    chr.dropMessage(5, a);
                }
            }
        }
    }

    public void 通知(int a, int b, String c) {
        for (ChannelServer cserv : getPlayer().getWorld().getChannels()) {
            for (Player chr : cserv.getPlayerStorage().getAllCharacters()) {
                if (chr == null) {
                    continue;
                }
                if (chr.getId() == a) {
                    chr.dropMessage(b, c);
                }
            }
        }
    }

    /**
     * <设置角色家族 1 = 族长 2 = 精英 3 = 成员>
     */
    public void 设置家族地位(int a) {
        设置家族地位(a, getPlayer().getId());
    }

    public void 设置家族地位(int a, int b) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET jiazulevel = " + a + " WHERE id = " + b + "")) {
                ps.executeUpdate();
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
     * <判断家族地位>
     */
    public int 判断家族地位() {
        return 判断家族地位(getPlayer().getId());
    }

    public int 判断家族地位(int id) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT jiazulevel as DATA FROM characters WHERE id = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();

        } catch (SQLException Ex) {
            System.err.println("判断家族地位、出错");
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
     * <显示所有家族列表>
     */
    public String 家族列表() {
        return 家族列表(getPlayer().getWorldId());
    }

    public String 家族列表(int a) {
        StringBuilder name = new StringBuilder();
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM jiazu  WHERE jiazuworld = " + a + " order by jiazujingyan desc");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String 家族名字 = rs.getString("jiazuname");
                int 序号 = rs.getInt("id");
                name.append("   #L").append(序号).append("#");
                name.append("#b").append(家族名字).append("#k#l\r\n");
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("家族列表、出错" + ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        name.append("\r\n\r\n");
        return name.toString();
    }

    /**
     * <显示家族成员列表> @return
     */
    public String 离线家族成员列表() {
        return 离线家族成员列表(判断家族());
    }

    public String 离线家族成员列表(int a) {
        StringBuilder name = new StringBuilder();
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters  WHERE jiazu = " + a + "");
            ResultSet rs = ps.executeQuery();
            name.append("\r\n  离线------------------------------------\r\n");
            while (rs.next()) {
                String 名字 = rs.getString("name");
                if (!在线(名字)) {
                    int 序号 = rs.getInt("id");
                    name.append("   #L").append(序号).append("#");
                    name.append("[").append(家族地位(rs.getInt("jiazulevel"))).append("] [").append(职业(rs.getInt("job"))).append("] #b").append(名字).append("#k Lv.").append(rs.getInt("level")).append("#l\r\n");
                }
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("离线家族成员列表、出错" + ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        name.append("\r\n\r\n");
        return name.toString();
    }

    public String 在线家族成员列表() {
        return 在线家族成员列表(判断家族());
    }

    public String 在线家族成员列表(int a) {
        StringBuilder name = new StringBuilder();
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters  WHERE jiazu = " + a + "");
            ResultSet rs = ps.executeQuery();
            name.append("\r\n  在线------------------------------------\r\n");
            while (rs.next()) {
                String 名字 = rs.getString("name");
                if (在线(名字)) {
                    int 序号 = rs.getInt("id");
                    name.append("   #L").append(序号).append("#");
                    name.append("[").append(家族地位(rs.getInt("jiazulevel"))).append("] [").append(职业(rs.getInt("job"))).append("] #b").append(名字).append("#k Lv.").append(rs.getInt("level")).append("#l\r\n");
                }
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("在线家族成员列表、出错" + ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        name.append("\r\n\r\n");
        return name.toString();
    }

    /**
     * <家族成员详细列表>
     */
    public String 家族成员详细列表(int a) {
        StringBuilder name = new StringBuilder();
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters  WHERE id = " + a + "");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                name.append("角色 : #b").append(rs.getString("name")).append("#k\r\n");
                name.append("等级 : #b").append(rs.getInt("level")).append("#k\r\n");
                name.append("职业 : #b").append(职业(rs.getInt("job"))).append("#k\r\n");
                name.append("地位 : #b").append(家族地位(rs.getInt("jiazulevel"))).append("#k\r\n");
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("家族成员详细列表、出错" + ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        name.append("\r\n\r\n");
        return name.toString();
    }

    /**
     * <家族现有人数>
     */
    public int 家族现有人数() {
        return 家族现有人数(判断家族());
    }

    public int 家族现有人数(int a) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE jiazu = " + a + "");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                data += 1;
            }
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
        return data;
    }

    public int 家族等级() {
        return 家族等级划分(家族经验());
    }

    /**
     * <家族在线人数>
     */
    public int 家族在线人数() {
        return 家族在线人数(判断家族());
    }

    public int 家族在线人数(int a) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE jiazu = " + a + "");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (在线(rs.getString("name"))) {
                    data += 1;
                }
            }
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
        return data;
    }

    /**
     * <家族最大人数>
     */
    public int 家族最大人数() {
        return 家族最大人数(判断家族());
    }

    public int 家族最大人数(int id) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT jiazurenshu as DATA FROM jiazu WHERE jiazuid = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();

        } catch (SQLException Ex) {
            System.err.println("家族最大人数、出错");
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
     * <家族经验>
     */
    public int 家族经验() {
        return 家族经验(判断家族());
    }

    public int 家族经验(int id) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT jiazujingyan as DATA FROM jiazu WHERE jiazuid = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();

        } catch (SQLException Ex) {
            System.err.println("家族经验、出错");
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

    public int 家族id取jiazuid(int id) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT jiazuid as DATA FROM jiazu WHERE id = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();

        } catch (SQLException Ex) {
            System.err.println("家族id取jiazuid、出错");
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
     * <判断玩家家族>
     */
    public int 判断家族() {
        return 判断家族(getPlayer().getId());
    }

    public int 判断家族(int id) {
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

    /**
     * <家族名字>
     */
    public String 家族名字() {
        return 家族名字(判断家族());
    }

    public String 家族名字(int id) {
        String data = "";
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT jiazuname as DATA FROM jiazu WHERE jiazuid = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getString("DATA");
                }
                rs.close();
            }
            ps.close();

        } catch (SQLException Ex) {
            System.err.println("家族名字、出错");
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

    public int 判断兑换卡是否存在(String id) {
        int a = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM nxcodez WHERE code = ?");
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    a += 1;
                }
            }
        } catch (SQLException Ex) {
            System.err.println("判断兑换卡是否存在、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return a;
    }

    public int 判断兑换卡礼包(String code) {
        int item = -1;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT `leixing` FROM nxcodez WHERE code = ?");
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                item = rs.getInt("leixing");
            }
            rs.close();
            ps.close();

        } catch (SQLException ex) {
            System.err.println("判断兑换卡礼包、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return item;
    }

    public void Deleteexchangecard(String a) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            ps = con.prepareStatement("SELECT * FROM nxcodez ");
            rs = ps.executeQuery();
            while (rs.next()) {
                String sqlstr = " delete from nxcodez where code = '" + a + "' ";
                ps.executeUpdate(sqlstr);
            }
        } catch (SQLException ex) {
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public void 消费记录(int amount) {
        getPlayer().消费记录(amount);
    }

    public void 给点券(int amount) {
        c.getPlayer().getCashShop().gainCash(1, amount);
    }

    public int 判断点券() {
        return getPlayer().getCashShop().getCash(1);
    }

    public void 给团队点券(int amount, List<Player> party) {
        for (Player chr : party) {
            chr.getCashShop().gainCash(1, amount);
        }
    }

    public void 给经验(int amount) {
        getPlayer().gainExp(amount);
    }

    public void 给团队经验(int amount, List<Player> party) {
        for (Player chr : party) {
            chr.gainExperience(amount, true, true);
        }
    }

    public int 判断金币() {
        return getPlayer().getMeso();
    }

    public void 给金币(int amount) {
        getPlayer().gainMeso(amount, true);
    }

    public void 给团队金币(int amount, List<Player> party) {
        for (Player chr : party) {
            chr.gainMeso(amount, true);
        }
    }

    public void 给物品(int id, short quantity) {
        gainItem(id, quantity, false);
    }

    public void gainItem(int id, short quantity) {
        gainItem(id, quantity, false);
    }

    public void 给团队物品(int id, short quantity, List<Player> party) {
        for (Player chr : party) {
            chr.gainItem(id, quantity, false);
        }
    }

    public void 说明文字(String text) {
        getClient().write(PacketCreator.GetNPCTalk(npc, (byte) 0, text, "00 00"));
    }

    public void 对话结束() {
        NPCScriptManager.getInstance().dispose(c);
    }

    /**
     * <游戏拍卖行>
     */
    public String 显示物品列表种类选择(int a) {
        StringBuilder name = new StringBuilder();
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT DISTINCT itemid FROM auctionitems  WHERE inventorytype = " + a + " && world = " + c.getPlayer().getWorldId() + ";");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int 物品 = rs.getInt("itemid");
                name.append("#L").append(物品).append("# #v").append(物品).append("# #b#t").append(物品).append("##k#l\r\n");
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("显示物品列表种类选择、出错" + ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        name.append("\r\n");
        return name.toString();
    }

    public String 显示我上架的物品列表种类选择(int a) {
        StringBuilder name = new StringBuilder();
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT DISTINCT itemid FROM  auctionitems  WHERE inventorytype = " + a + " && characterid = " + c.getPlayer().getId() + ";");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int 物品 = rs.getInt("itemid");
                name.append("#L").append(物品).append("# #v").append(物品).append("# #b#t").append(物品).append("##k#l\r\n");
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("显示物品列表种类选择、出错" + ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        name.append("\r\n");
        return name.toString();
    }

    public String 显示物品列表详细(int a) {
        StringBuilder name = new StringBuilder();
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM auctionitems WHERE itemid = " + a + " && world = " + c.getPlayer().getWorldId() + " order by price asc;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int 序号 = rs.getInt("id");
                int 物品 = rs.getInt("itemid");
                int 价格 = rs.getInt("price");
                int 数量 = rs.getInt("quantity");
                if (rs.getInt("inventorytype") == 1) {
                    name.append("#L").append(序号).append("# #v").append(物品).append("# #b#t").append(物品).append("##k 价格 : #r").append(价格).append("#k#l\r\n");
                } else {
                    name.append("#L").append(序号).append("# #v").append(物品).append("# #b#t").append(物品).append("##k 价格 : #r").append(价格).append("#k   数量 :  #b").append(数量).append("#k#l\r\n");
                }
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("显示物品列表详细、出错" + ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        name.append("\r\n");
        return name.toString();
    }

    public String 显示我的物品列表详细(int a) {
        StringBuilder name = new StringBuilder();
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM auctionitems WHERE itemid = " + a + " && characterid = " + c.getPlayer().getId() + "  &&  world = " + c.getPlayer().getWorldId() + " order by price asc");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int 序号 = rs.getInt("id");
                int 物品 = rs.getInt("itemid");
                int 价格 = rs.getInt("price");
                int 数量 = rs.getInt("quantity");
                if (rs.getInt("inventorytype") == 1) {
                    name.append("#L").append(序号).append("# #v").append(物品).append("# #b#t").append(物品).append("##k 价格 : #r").append(价格).append("#k#l\r\n");
                } else {
                    name.append("#L" + 序号 + "# #v" + 物品 + "# #b#t" + 物品 + "##k 价格 : #r" + 价格 + "#k   数量 :  #b" + 数量 + "#k#l\r\n");
                }
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("显示物品列表详细、出错" + ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        name.append("\r\n");
        return name.toString();
    }

    public String 显示物品数据详细(int a) {
        StringBuilder name = new StringBuilder();
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM auctionitems WHERE id = " + a + "");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int 物品 = rs.getInt("itemid");
                name.append("#b物品: #v").append(物品).append("# #b#t").append(物品).append("##k\r\n");
                int 卖家 = rs.getInt("characterid");
                name.append("#b卖家: #r").append(取角色名字(卖家)).append("#k\r\n");

                int 售价 = rs.getInt("price");
                name.append("#b售价: #r").append(售价).append("#k #b金币#k\r\n");

                int 数量 = rs.getInt("quantity");
                name.append("#b数量: #r").append(数量).append("#k\r\n");

                String 上架时间 = rs.getString("shijian");
                name.append("#b上架时间: #r").append(上架时间).append("#k\r\n");
                if (物品 < 2000000) {
                    int 攻击力 = rs.getInt("watk");
                    if (攻击力 > 0) {
                        name.append("#b攻击力 : #r").append(攻击力).append("#k\r\n");
                    }
                    int 魔法力 = rs.getInt("matk");
                    if (魔法力 > 0) {
                        name.append("#b魔法力 : #r").append(魔法力).append("#k\r\n");
                    }
                    int 物理防御 = rs.getInt("wdef");
                    if (物理防御 > 0) {
                        name.append("#b物理防御 : #r").append(物理防御).append("#k\r\n");
                    }
                    int 魔法防御 = rs.getInt("mdef");
                    if (魔法防御 > 0) {
                        name.append("#b魔法防御 : #r").append(魔法防御).append("#k\r\n");
                    }
                    int 力量 = rs.getInt("str");
                    if (力量 > 0) {
                        name.append("#b力量 : #r").append(力量).append("#k\r\n");
                    }
                    int 敏捷 = rs.getInt("dex");
                    if (敏捷 > 0) {
                        name.append("#b敏捷 : #r").append(敏捷).append("#k\r\n");
                    }
                    int 智力 = rs.getInt("_int");
                    if (智力 > 0) {
                        name.append("#b智力 : #r").append(智力).append("#k\r\n");
                    }
                    int 运气 = rs.getInt("luk");
                    if (运气 > 0) {
                        name.append("#b运气 : #r").append(运气).append("#k\r\n");
                    }
                    int 生命值 = rs.getInt("hp");
                    if (生命值 > 0) {
                        name.append("#b生命值 : #r").append(生命值).append("#k\r\n");
                    }
                    int 魔力值 = rs.getInt("mp");
                    if (魔力值 > 0) {
                        name.append("#b魔力值 : #r").append(魔力值).append("#k\r\n");
                    }
                    int 命中 = rs.getInt("acc");
                    if (命中 > 0) {
                        name.append("#b命中 : #r").append(命中).append("#k\r\n");
                    }
                    int 回避 = rs.getInt("avoid");
                    if (回避 > 0) {
                        name.append("#b回避 : #r").append(回避).append("#k\r\n");
                    }
                    int 手技 = rs.getInt("hands");
                    if (手技 > 0) {
                        name.append("#b手技 : #r").append(手技).append("#k\r\n");
                    }
                    int 移速 = rs.getInt("speed");
                    if (移速 > 0) {
                        name.append("#b移速 : #r").append(移速).append("#k\r\n");
                    }
                    int 跳跃 = rs.getInt("jump");
                    if (跳跃 > 0) {
                        name.append("#b跳跃 : #r").append(跳跃).append("#k\r\n");
                    }
                    int 可升级次数 = rs.getInt("upgradeslots");
                    if (可升级次数 > 0) {
                        name.append("#b可升级次数 : #r").append(可升级次数).append("#k\r\n");
                    }
                    int 已升级次数 = rs.getInt("level");
                    if (已升级次数 > 0) {
                        name.append("#b已升级次数 : #r").append(已升级次数).append("#k\r\n");
                    }
                    String 显示附魔效果 = rs.getString("options");
                    if (显示附魔效果 != null) {
                        name.append(显示附魔效果(显示附魔效果));
                    }

                }
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("显示物品列表详细、出错" + ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        name.append("\r\n");
        return name.toString();
    }

    /**
     * <添加道具到拍卖行> * @param a1 价钱
     *
     * @param a2 物品ID
     * @param a3 物品类型
     * @param a4 数量
     * @param a5 物品前缀
     * @param a6 GM标签
     * @param a7
     * @param a8
     * @param a9
     * @param a10
     * @param a11 可升级次数
     * @param a12 已升级次数
     * @param a13 力量
     * @param a14 敏捷
     * @param a15 智力
     * @param a16 运气
     * @param a17 生命
     * @param a18 魔力
     * @param a19 物理攻击力
     * @param a20 魔法攻击力
     * @param a21 物理防御力
     * @param a22 魔法防御力
     * @param a23 命中
     * @param a24 回避
     * @param a25 手技
     * @param a26 移送
     * @param a27 跳跃
     * @param a28 附魔
     */
    public void 添加道具到拍卖行(int a1, int a2, int a3, int a4, String a5, String a6, int a7, int a8, int a9, String a10, int a11, int a12, int a13, int a14, int a15, int a16, int a17, int a18, int a19, int a20, int a21, int a22, int a23, int a24, int a25, int a26, int a27, String a28, int a29, int a30) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        if (ii.isQuestItem(a2)) {
            c.getPlayer().dropMessage(1, "任务物品，无法上架。");
            c.write(PacketCreator.EnableActions());
            return;
        }
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO auctionitems (characterid , price ,itemid ,inventorytype ,quantity ,owner ,GM_Log ,uniqueid ,flag ,expiredate ,sender ,upgradeslots ,level ,str ,dex ,_int ,luk ,hp ,mp ,watk ,matk ,wdef ,mdef ,acc ,avoid ,hands ,speed ,jump  ,options, socket, world, dzlevel) VALUES (?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
                ps.setInt(1, getPlayer().getId());
                ps.setInt(2, a1);
                ps.setInt(3, a2);
                ps.setInt(4, a3);
                ps.setInt(5, a4);
                ps.setString(6, a5);
                ps.setString(7, a6);
                ps.setInt(8, a7);
                ps.setByte(9, (byte) a8);
                ps.setLong(10, a9);
                ps.setString(11, a10);
                ps.setInt(12, a11);
                ps.setInt(13, a12);
                ps.setInt(14, a13);
                ps.setInt(15, a14);
                ps.setInt(16, a15);
                ps.setInt(17, a16);
                ps.setInt(18, a17);
                ps.setInt(19, a18);
                ps.setInt(20, a19);
                ps.setInt(21, a20);
                ps.setInt(22, a21);
                ps.setInt(23, a22);
                ps.setInt(24, a23);
                ps.setInt(25, a24);
                ps.setInt(26, a25);
                ps.setInt(27, a26);
                ps.setInt(28, a27);
                ps.setString(29, a28);
                ps.setByte(30, (byte) a29);
                ps.setInt(31, getPlayer().getWorldId());
                ps.setInt(32, a30);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            System.out.println("添加道具到拍卖行" + ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public void 购买拍卖行装备(int a) {
        if (物品存在(a) > 0) {
            Connection con = null;
            try {
                con = Start.getInstance().getConnection();
                try (PreparedStatement ps = con.prepareStatement("SELECT * FROM auctionitems WHERE id = " + a + "")) {
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        if (rs.getInt("inventorytype") == 1) {
                            int 物品 = rs.getInt("itemid");
                            int 攻击力 = rs.getInt("watk");
                            int 魔法力 = rs.getInt("matk");
                            int 物理防御 = rs.getInt("wdef");
                            int 魔法防御 = rs.getInt("mdef");
                            int 力量 = rs.getInt("str");
                            int 敏捷 = rs.getInt("dex");
                            int 智力 = rs.getInt("_int");
                            int 运气 = rs.getInt("luk");
                            int 生命值 = rs.getInt("hp");
                            int 魔力值 = rs.getInt("mp");
                            int 命中 = rs.getInt("acc");
                            int 回避 = rs.getInt("avoid");
                            int 手技 = rs.getInt("hands");
                            int 移速 = rs.getInt("speed");
                            int 跳跃 = rs.getInt("jump");
                            int 可升级次数 = rs.getInt("upgradeslots");
                            int 已升级次数 = rs.getInt("level");
                            int 锻造等级 = rs.getInt("dzlevel");
                            byte socket = rs.getByte("socket");
                            String 附魔 = rs.getString("options");
                            给属性装备(物品, 可升级次数, 力量, 敏捷, 智力, 运气, 生命值, 魔力值, 攻击力, 魔法力, 物理防御, 魔法防御, 回避, 命中, 跳跃, 移速, 0, 已升级次数, 手技, 附魔, 锻造等级, socket);
                        } else {
                            int 物品 = rs.getInt("itemid");
                            gainItem(物品, (short) rs.getInt("quantity"));
                        }
                    }
                    rs.close();
                }
            } catch (SQLException ex) {
                System.err.println("购买拍卖行装备、出错" + ex.getMessage());
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

    public void 给属性装备(final int id, final int sj, final int str, final int dex, final int Int, final int luk, final int hp, int mp, int watk, int matk, int wdef, int mdef, int hb, int mz, int ty, int yd, long 给予时间, int lv, int hands, String FM, int dzlevel, byte socket) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        Equip item = (Equip) (ii.getEquipById(id));
        //if (sj > 0) {
        item.setUpgradeSlots((byte) (short) sj);
        //}
        if (lv > 0) {
            item.setLevel((byte) lv);
        }

        if (hands > 0) {
            item.setHands((short) hands);
        }
        if (str > 0) {
            item.setStr((short) str);
        }
        if (dex > 0) {
            item.setDex((short) dex);
        }
        if (luk > 0) {
            item.setLuk((short) luk);
        }
        if (Int > 0) {
            item.setInt((short) Int);
        }
        if (hp > 0) {
            item.setHp((short) hp);
        }
        if (mp > 0) {
            item.setMp((short) mp);
        }
        if (watk > 0) {
            item.setWatk((short) watk);
        }
        if (matk > 0) {
            item.setMatk((short) matk);
        }
        if (wdef > 0) {
            item.setWdef((short) wdef);
        }
        if (mdef > 0) {
            item.setMdef((short) mdef);
        }
        if (hb > 0) {
            item.setAvoid((short) hb);
        }
        if (mz > 0) {
            item.setAcc((short) mz);
        }
        if (ty > 0) {
            item.setJump((short) ty);
        }
        if (yd > 0) {
            item.setSpeed((short) yd);
        }
        if (dzlevel > 0) {
            item.setDzlevel((short) dzlevel);
        }
        if (FM != null) {
            item.setOptionValues(FM);
            item.setOption(new EquipOption(item));
            item.getEquipOption().rebuildEquipOptions();
        }
        if (socket > 0) {
            item.setSocket(socket);
        }
        if (给予时间 > 0) {
            item.setExpiration(System.currentTimeMillis() + (给予时间 * 60 * 60 * 1000));
        }
        InventoryManipulator.addbyItem(c, item.copy());
        c.write(PacketCreator.GetShowItemGain(id, (short) 1, true));
    }

    public void 删除购买的道具(int a) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            ps = con.prepareStatement("SELECT * FROM auctionitems WHERE id = " + a + "");
            rs = ps.executeQuery();
            while (rs.next()) {
                String sqlstr = " delete from auctionitems where id = '" + a + "' ";
                ps.executeUpdate(sqlstr);
            }
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
    }

    public int 物品价格(int id) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT price as DATA FROM auctionitems WHERE id = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("物品价格、出错");
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

    public int 物品存在(int id) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM auctionitems WHERE id = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data++;
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("物品存在、出错");
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

    public int 物品卖家(int id) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT characterid as DATA FROM auctionitems WHERE id = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("物品卖家、出错");
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

    public void 初始化拍卖储存(int a) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO auctionpoint (characterid, point) values (?,?);");
            ps.setInt(1, a);
            ps.setInt(2, 0);
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

    public void 新增拍卖存储金币(int a, int b) {
        if (判断拍卖存储金币(a) == 0) {
            初始化拍卖储存(a);
        }
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE auctionpoint SET point = point + " + b + " WHERE characterid =" + a + "")) {
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

    public int 判断拍卖存储金币(int a) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT point as DATA FROM auctionpoint WHERE characterid = ?");
            ps.setInt(1, a);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();

        } catch (SQLException Ex) {
            System.err.println("判断拍卖存储金币、出错");
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

    public void 清空拍卖存储金币() {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE auctionpoint SET point = 0 WHERE characterid =" + getPlayer().getId() + "")) {
                ps.executeUpdate();
                ps.close();
            }
        } catch (Exception Ex) {

        }
    }

    public void 增加拍卖出售栏(int a) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE auctionpoint SET selling = selling + " + a + " WHERE characterid =" + getPlayer().getId() + ";")) {
                ps.executeUpdate();
                ps.close();
            }
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

    public int 判断拍卖出售位数量() {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT selling  as DATA FROM auctionpoint WHERE characterid = ?;");
            ps.setInt(1, getPlayer().getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data += rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();

        } catch (SQLException Ex) {
            System.err.println("判断拍卖出售位数量、出错");
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

    public int 已经使用拍卖栏位() {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM auctionitems WHERE characterid = ?")) {
                ps.setInt(1, getPlayer().getId());
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    data++;
                }
                rs.close();
            }
        } catch (SQLException ex) {
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

    public int 打孔(final short equipmentPosition) {
        // 先查询装备的打孔数据
        /*String mxmxdDaKongFuMo = null;
        String sqlQuery1 = "SELECT mxmxd_dakong_fumo FROM inventoryequipment WHERE characterid = ? AND inventorytype = 1 AND position = ?";
        Connection con = Start.getInstance().getConnection();
        try {
            try (PreparedStatement ps = con.prepareStatement(sqlQuery1)) {
                ps.setInt(1, c.getPlayer().getId());
                ps.setInt(2, equipmentPosition);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        mxmxdDaKongFuMo = rs.getString("mxmxd_dakong_fumo");
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("打孔：查询查询装备的打孔数据出错：" + ex.getMessage());
            return 3;
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }

        if (mxmxdDaKongFuMo == null) {
            mxmxdDaKongFuMo = "";
        }

        // 再计算该装备已打孔数量
        int dakongCount = 0;
        if (mxmxdDaKongFuMo.length() > 0) {
            dakongCount = mxmxdDaKongFuMo.split(",").length;
        }

        if (dakongCount >= 20) {
            return 2;
        }
        c.getPlayer().getInventory(InventoryType.EQUIP).getItem(equipmentPosition).setDaKongFuMo(mxmxdDaKongFuMo + "0:0,");
        return 1;
        
         */

        Item item = getInventory(InventoryType.EQUIP.getType()).getItem(equipmentPosition);
        if (item == null) {
            return 3;
        }

        Equip equip = (Equip) item;
        if (equip.getSocket() >= 20) {
            return 2;
        }

        equip.setSocket((byte) (equip.getSocket() + 1));
        return 1;
    }

    //编号，附魔类，附魔值
    public int 附魔(final short equipmentPosition, final int fuMoType, final int fuMoValue) {
        Item item = getInventory(InventoryType.EQUIP.getType()).getItem(equipmentPosition);
        if (item == null) {
            return 3;
        }

        Equip equip = (Equip) item;
        if (equip.getSocket() == 0 || equip.getEquipOption().isFull()) {
            return 0;
        }

        if (equip.getEquipOption().setOption(String.valueOf(fuMoType), String.valueOf(fuMoValue))) {
            equip.setOptionValues(equip.getEquipOption().getEquipOptions());
            getPlayer().刷新身上装备附魔汇总数据(true);
            return 1;
        }
        return 0;
    }

    private static String replaceFirst(String source, String target, String replacement) {
        int index = source.indexOf(target);
        if (index == -1) {
            return source;
        }
        return source.substring(0, index).concat(replacement).concat(source.substring(index + target.length()));
    }

    public String 显示附魔效果上架(String a) {
        StringBuilder name = new StringBuilder();
        String arr1[] = a.split(",");
        for (int i = 0; i < arr1.length; i++) {
            String pair = arr1[i];
            if (pair.contains(":")) {
                String kongInfo = "\t●";
                String arr2[] = pair.split(":");
                int fumoType = Integer.parseInt(arr2[0]);
                int fumoVal = Integer.parseInt(arr2[1]);
                if (fumoType > 0 && FuMoInfoMap.containsKey(fumoType)) {
                    String infoArr[] = FuMoInfoMap.get(fumoType);
                    String fumoName = infoArr[0];
                    String fumoInfo = infoArr[1];
                    kongInfo += fumoName + " " + String.format(fumoInfo, fumoVal);
                } else if (一转技能附魔(fumoType) || 二转技能附魔20(fumoType) || 二转技能附魔30(fumoType)) {
                    kongInfo += "[能手册] 当(" + 技能名字2(fumoType) + ")达到满级时增加 " + fumoVal + " 级技能等级";
                } else {
                    kongInfo += "[未附魔]";
                }
                name.append("\r\n").append(kongInfo);
            }
        }
        return name.toString();
    }

    public String 技能名字2(int a) {
        if (技能名字.containsKey(a)) {
            return 技能名字.get(a);
        }
        return "攻击技能";
    }

    public String 显示附魔效果(String a) {
        StringBuilder name = new StringBuilder();
        String arr1[] = a.split(",");
        for (int i = 0; i < arr1.length; i++) {
            String pair = arr1[i];
            if (pair.contains(":")) {
                String kongInfo = "#b●";
                String arr2[] = pair.split(":");
                int fumoType = Integer.parseInt(arr2[0]);
                int fumoVal = Integer.parseInt(arr2[1]);
                if (fumoType > 0 && FuMoInfoMap.containsKey(fumoType)) {
                    String infoArr[] = FuMoInfoMap.get(fumoType);
                    String fumoName = infoArr[0];
                    String fumoInfo = infoArr[1];
                    kongInfo += fumoName + " " + String.format(fumoInfo, fumoVal);
                } else if (一转技能附魔(fumoType) || 二转技能附魔20(fumoType) || 二转技能附魔30(fumoType)) {
                    kongInfo += "[能手册] 当(" + 技能名字2(fumoType) + ")达到满级时增加 " + fumoVal + " 级技能等级";
                } else {
                    kongInfo += "[未附魔]";
                }
                name.append("\r\n").append(kongInfo);
            }
        }
        return name.toString();
    }

    public String 显示伤害详细() {
        StringBuilder name = new StringBuilder();
        for (Map.Entry<Integer, String> entry : 伤害记录.entrySet()) {
            int a = entry.getKey();
            String b = entry.getValue();
            if (a == c.getPlayer().getId()) {
                name.append("" + b + "");
            }
        }
        return name.toString();
    }

    public String 世界伤害排行() {
        return 伤害排行(getPlayer().getWorldId());
    }

    public static int appearNumber(String srcText, String findText) {
        int count = 0;
        int index = 0;
        while ((index = srcText.indexOf(findText, index)) != -1) {
            index = index + findText.length();
            count++;
        }
        return count;
    }

    // 返回值就表示已打孔数量
    public int 查询身上装备已打孔数(final short equipmentPosition) {
        Item item = getInventory(InventoryType.EQUIP.getType()).getItem(equipmentPosition);
        if (item == null) {
            return 0;
        }
        return ((Equip) item).getSocket();
    }

    // 返回值就表示可附魔数量
    public int 查询身上装备可附魔数(final short equipmentPosition) {
        Item item = getInventory(InventoryType.EQUIP.getType()).getItem(equipmentPosition);
        if (item == null) {
            return 0;
        }

        Equip equip = (Equip) item;
        if (equip.getSocket() == 0 || equip.getSocket() < equip.getEquipOption().getOptions().size()) {//equip.getEquipOption().isFull() ||
            return 0;
        }

        return equip.getEquipOption().getRemainingSockets();
    }

    public int 清洗身上装备附魔(final short equipmentPosition, final int index) {
        /*if (index < 1) {
            return 0;
        }
        String mxmxdDaKongFuMo = 查询身上装备打孔附魔数据(equipmentPosition);
        String arr[] = mxmxdDaKongFuMo.split(",");
        for (int i = 0; i < arr.length; i++) {
            if (index - 1 == i) {
                arr[i] = "0:0";
                break;
            }
        }

        StringBuilder sb = new StringBuilder();
        for (String str : arr) {
            sb.append(str).append(",");
        }
        c.getPlayer().getInventory(InventoryType.EQUIP).getItem(equipmentPosition).setDaKongFuMo(sb.toString());*/

        if (index < 1) {
            return 0;
        }

        Item item = getInventory(InventoryType.EQUIP.getType()).getItem(equipmentPosition);
        if (item == null) {
            return 0;
        }

        Equip equip = (Equip) item;
        if (index > equip.getEquipOption().getOptions().size() + 1) {
            return 0;
        }

        Iterator<Map.Entry<String, String>> optionsIterator = equip.getEquipOption().getOptions().entrySet().iterator();
        int i = 1;
        while (optionsIterator.hasNext()) {
            if (index == i) {
                optionsIterator.remove();
            }
            i++;
        }
        equip.setOptionValues(equip.getEquipOption().getEquipOptions());
        getPlayer().刷新身上装备附魔汇总数据(true);

        return 1;
    }

    // 返回值大于0表示清洗完成
    public int 清洗身上装备附魔(final short equipmentPosition) {
        /*int dakongCount = 查询身上装备已打孔数(equipmentPosition);
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= dakongCount; i++) {
            sb.append("0:0,");
        }
        c.getPlayer().getInventory(InventoryType.EQUIP).getItem(equipmentPosition).setDaKongFuMo(sb.toString());*/

        Item item = getInventory(InventoryType.EQUIP.getType()).getItem(equipmentPosition);
        if (item == null) {
            return 0;
        }
        Equip equip = (Equip) item;
        equip.getEquipOption().resetAllOptions();
        equip.setOptionValues(equip.getEquipOption().getEquipOptions());
        getPlayer().刷新身上装备附魔汇总数据(true);
        return 1;
    }

    public int 清洗(final short equipmentPosition) {
        /*int dakongCount = 查询身上装备已打孔数(equipmentPosition);
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= dakongCount; i++) {
            sb.append("0:0,");
        }
        c.getPlayer().getInventory(InventoryType.EQUIP).getItem(equipmentPosition).setDaKongFuMo(sb.toString());*/
        return 清洗身上装备附魔(equipmentPosition);
    }

    public String 查询身上装备打孔附魔数据(final short equipmentPosition) {
        /*String mxmxdDaKongFuMo = "";
        String sqlQuery1 = "SELECT mxmxd_dakong_fumo FROM inventoryequipment  WHERE characterid = ? AND inventorytype = 1 AND position = ?";
        //String sqlQuery1 = "SELECT b.mxmxd_dakong_fumo FROM inventoryitems a, inventoryequipment b WHERE a.inventoryitemid = b.inventoryitemid AND a.characterid = ? AND a.inventorytype = 1 AND a.position = ?";
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sqlQuery1);
            ps.setInt(1, c.getPlayer().getId());
            ps.setInt(2, equipmentPosition);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    mxmxdDaKongFuMo = rs.getString("mxmxd_dakong_fumo");
                }
            }
            ps.close();
        } catch (SQLException ex) {
            System.err.println("查询身上装备已打孔数：查询装备的打孔数据出错：" + ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }*/
        Item item = getInventory(InventoryType.EQUIP.getType()).getItem(equipmentPosition);
        if (item == null) {
            return "";
        }

        return ((Equip) item).getOptionValues();
    }

    public void 切换频道(int a) {
        c.getPlayer().changeChannel(a);
    }

    public void saveDatabaseItem() {
        c.getPlayer().saveDatabaseItem();
    }

    public boolean isCash(final int itemId) {
        return ItemInformationProvider.getInstance().isCash(itemId);
    }

    public void 全服喇叭(int a, String b) {
        for (ChannelServer cserv : getPlayer().getWorld().getChannels()) {
            for (Player chr : cserv.getPlayerStorage().getAllCharacters()) {
                if (chr == null) {
                    continue;
                }
                if (chr.getWorld() == getPlayer().getWorld()) {
                    chr.dropMessage(a, b);
                }
            }
        }
    }

    public void 全服公告(String b) {
        for (World world : Start.getInstance().getWorlds()) {
            BroadcastService.broadcastMessage(world.getWorldId(), PacketCreator.ServerNotice(6, b));

        }
    }

    public boolean getEventNotScriptOpen(String evento) {
        switch (evento) {
            case "Barcos":
                return Boats.boatOpen();
            case "haveBalrog":
                return Boats.hasBalrog();

            /* case "Trens":
             return Trains.trainsOpen();
             case "Cabine":
             return Cabin.cabinOpen();
             case "Genio":
             return Genie.genioOpen();
             case "RoletaRussa":
             return RussianRoulette.RoletaDisponivel();
             case "ElevadorDescendo":
             return Elevator.elevatorIsDown();
             case "ElevadorSubindo":
             return Elevator.elevatorIsUp();*/
            default:
                return false;
        }
    }

    public void TransferToCashShop() {
        InterServerHandler.TransferToCashShop(c);
    }
    public int[] 附魔技能 = {
        1001004,
        1001005,
        2001004,
        2001005,
        3001004,
        3001005,
        4001334,
        4001344,
        1100000,
        1100001,
        1101006,
        1101007,
        1200000,
        1200001,
        1201007,
        1300000,
        1300001,
        1301007,
        2101004,
        2201005,
        2301004,
        3100000,
        3200000,
        4100000,
        4200000
    };

    public String 个人信息(Player c) {
        StringBuilder name = new StringBuilder();
        ItemInformationProvider ii = ItemInformationProvider.getInstance();

        name.append("#bID#k:").append(c.getId()).append("\r\n");
        name.append("#b昵称#k:").append(c.getName()).append("\r\n");
        name.append("#b等级#k:").append(c.getMaxlevel()).append(" | ").append(c.getLevel()).append("\r\n");
        name.append("#b人气#k:").append(c.getFame()).append("\r\n");
        name.append("#b职业#k:").append(职业(c.getJobId())).append("\r\n");
        name.append("#b大区#k:").append(大区(c.getWorldId())).append("\r\n");
        if (c.判断家族() > 0) {
            name.append("#b社区/家族#k:").append(家族名字(c.判断家族())).append("\r\n");
        } else {
            name.append("#b社区/家族#k:未加入家族社区\r\n");
        }
        if (c.getLevel() < 70) {
            if (c.师傅() > 0) {
                name.append("#b师傅#k:").append(取角色名字(c.师傅())).append("\r\n");
            } else if (c.师傅() == 0) {
                name.append("#b师傅#k:未拜师\r\n");
            }
        } else if (c.getLevel() >= 70) {
            if (c.师傅() < 0) {
                name.append("#b师承#k:").append(取角色名字((-c.师傅() + c.师傅() * 2))).append("\r\n");
            }
        }
        name.append("#b在线#k:").append(角色今日在线(c.getId())).append(" | ").append(角色总在线(c.getId())).append("\r\n");
        name.append("#b死亡#k:").append(c.死亡次数()).append("\r\n");
        name.append("#b出生#k:").append(c.出生日期()).append("\r\n");
        name.append("#b群等级#k:").append(c.getTX()).append("\r\n");
        name.append("#b绑定QQ#k:").append(c.getQQ()).append("\r\n");
        //附魔
        if (c.getEquippedFuMoMap().size() > 0) {
            name.append("\r\n");
            name.append("[#b附魔属性#k]:\r\n");
            if (c.getEquippedFuMoMap().get(1) != null) {
                name.append("#b强攻#k#n:增加 #r").append(c.getEquippedFuMoMap().get(1)).append("#k 点物理伤害。\r\n");
            }

            if (c.getEquippedFuMoMap().get(11) != null) {
                name.append("#b利刃#k#n:增加 #r").append(c.getEquippedFuMoMap().get(11)).append("%#k 点物理伤害。\r\n");
            }

            if (c.getEquippedFuMoMap().get(2) != null) {
                name.append("#b魔攻#k#n:增加 #r").append(c.getEquippedFuMoMap().get(2)).append("#k 点魔法伤害。\r\n");
            }

            if (c.getEquippedFuMoMap().get(12) != null) {
                name.append("#b魔术#k#n:增加 #r").append(c.getEquippedFuMoMap().get(12)).append("%#k 点魔法伤害。\r\n");
            }

            if (c.getEquippedFuMoMap().get(3) != null) {
                name.append("#b坚韧#k#n:减少 #r").append(c.getEquippedFuMoMap().get(3)).append("#k 点角色受到的伤害。\r\n");
            }

            if (c.getEquippedFuMoMap().get(4) != null) {
                name.append("#b狩猎#k#n:增加 #r").append(c.getEquippedFuMoMap().get(4)).append("%#k 狩猎时获取的经验。\r\n");
            }

            if (c.getEquippedFuMoMap().get(20) != null) {
                name.append("#b茁壮成长#k#n:角色升级额外MaxHp+#r").append(c.getEquippedFuMoMap().get(20)).append("#k。\r\n");
            }

            if (c.getEquippedFuMoMap().get(21) != null) {
                name.append("#b茁壮生长#k#n:角色升级额外MaxMp+#r").append(c.getEquippedFuMoMap().get(21)).append("#k。\r\n");
            }

            if (c.getEquippedFuMoMap().get(5) != null) {
                name.append("#b兵不血刃#k#n:每次攻击叠加 #r").append(c.getEquippedFuMoMap().get(5)).append("#k 点物理伤害。\r\n");
            }

            if (c.getEquippedFuMoMap().get(6) != null) {
                name.append("#b窃法魔术#k#n:每次攻击叠加 #r").append(c.getEquippedFuMoMap().get(6)).append("#k 点魔法伤害。\r\n");
            }

            if (c.getEquippedFuMoMap().get(7) != null) {
                name.append("#b超然#k#n:提升角色 #r").append(c.getEquippedFuMoMap().get(7)).append("#k 点生命恢复能力。\r\n");
            }

            if (c.getEquippedFuMoMap().get(8) != null) {
                name.append("#b清晰#k#n:提升角色 #r").append(c.getEquippedFuMoMap().get(8)).append("#k 点魔法恢复能力。\r\n");
            }

            if (c.getEquippedFuMoMap().get(23) != null) {
                name.append("#b财源#k#n:增加 #r").append(c.getEquippedFuMoMap().get(23)).append("#k 点基础泡点金币。\r\n");
            }

            if (c.getEquippedFuMoMap().get(24) != null) {
                name.append("#b冥想#k#n:增加 #r").append(c.getEquippedFuMoMap().get(24)).append("#k 点基础泡点经验。\r\n");
            }

            if (c.getEquippedFuMoMap().get(25) != null) {
                name.append("#b福利#k#n:增加 #r").append(c.getEquippedFuMoMap().get(25)).append("#k 点基础泡点点券。\r\n");
            }

            if (c.getEquippedFuMoMap().get(26) != null) {
                name.append("#b坐享其成#k#n:增加 #r").append(c.getEquippedFuMoMap().get(26)).append("%#k 点基础泡点收益。\r\n");
            }

            if (c.getEquippedFuMoMap().get(27) != null) {
                name.append("#b怨念丛生#k#n:击杀怪物后 #r").append(c.getEquippedFuMoMap().get(27)).append("%#k 概率出现军团。\r\n");
            }

            if (c.getEquippedFuMoMap().get(29) != null) {
                name.append("#b怨念爆发#k#n:击杀怪物后 #r").append(c.getEquippedFuMoMap().get(29)).append("%#k 概率出现超级怪物。\r\n");
            }

            if (c.所有附魔技能() > 0) {
                name.append("#b天梯奖励#k#n:所有已被附魔提高的技能等级额外增加 #r").append(c.所有附魔技能()).append("#k 级。\r\n");
            }

            for (int i = 0; i < 附魔技能.length; i++) {
                if (c.getEquippedFuMoMap().get(附魔技能[i]) != null) {
                    name.append("#b能手册#k#n:当(").append(技能名字2(附魔技能[i])).append(")达到满级时增加 #r").append(c.getEquippedFuMoMap().get(附魔技能[i])).append("#k 级技能等级。\r\n");
                }
            }

        }
        //套装
        if (c.枫叶套() > 0) {
            name.append("\r\n");
            name.append("[#b枫叶套装#k](").append(c.枫叶套()).append("):\r\n");
            if (c.枫叶套() >= 2) {
                name.append("(2)物品爆率+#r10%#k\r\n");
            }
            if (c.枫叶套() >= 3) {
                name.append("(3)角色收到的上海-#r10%#k\r\n");
            }
            if (c.枫叶套() >= 4) {
                name.append("(4)角色死亡损失经验-#r50%#k\r\n");
                name.append("(4)幸运女神光效\r\n");
            }
        }
        name.append("\r\n[#b角色倍率#k]:\r\n");
        int 经验倍率1 = 100;
        int 经验加成 = 0;
        if (c.getvip() > 0) {
            经验加成 += 经验倍率1 * (0.1 + ((会员等级划分(c.getvip()) - 1) * 0.02));
        }

        if (c.getTX() != null) {
            经验加成 += 经验倍率1 * (群头衔经验加成(c.getTX()) * 0.05);
        }

        if (c.经验倍率 > 1) {
            经验加成 += 100;
        }

        经验加成 += 经验倍率(c.getWorldId()) * 100;

        name.append("经验倍率 #r").append((经验倍率1 + 经验加成)).append("%#k 经验\r\n");

        int 爆物倍率 = 100;
        int 爆物加成 = 0;

        if (c.getvip() > 0) {
            爆物加成 += 爆物倍率 * (0.2 + ((会员等级划分(c.getvip()) - 1) * 0.02));
        }

        name.append("爆物倍率 #r").append((爆物倍率 + 爆物加成)).append("%#k 经验\r\n");

        int 金币倍率 = 100;
        name.append("金币倍率 #r").append(金币倍率).append("%#k 经验\r\n");

        int 泡点倍率 = 100;
        泡点倍率 = 泡点倍率 * 泡点倍率(c.getWorldId());
        name.append("泡点倍率 #r").append(泡点倍率).append("%#k 经验\r\n");

        name.append("\r\n[#b恢复能力#k]:\r\n");
        int 生命恢复 = 10;
        int 法力回复 = 3;

        int 生命恢复技能 = c.getSkillLevel(1000000);
        if (生命恢复技能 > 0) {
            生命恢复 += 生命恢复技能 * 3;
            if (生命恢复技能 == 16) {
                生命恢复 += 2;
            }
        }
        if (c.getEquippedFuMoMap().get(7) != null) {
            生命恢复 += c.getEquippedFuMoMap().get(7);
        }

        int 法力回复技能 = c.getSkillLevel(2000000);
        if (法力回复技能 > 0) {
            法力回复 += 法力回复技能 * 7;
        }
        if (c.getEquippedFuMoMap().get(8) != null) {
            法力回复 += c.getEquippedFuMoMap().get(8);
        }
        name.append("生命恢复 #r").append(生命恢复).append("#k \r\n");

        name.append("法力恢复 #r").append(法力回复).append("#k \r\n");

        //泡点
        name.append("\r\n[#b福利泡点#k]:\r\n");
        name.append("角色泡点经验 #r").append(c.get角色泡点经验()).append("#k\r\n");
        int 经验 = c.get泡点经验();
        int 加成经验 = 0;
        if (c.getEquippedFuMoMap().get(24) != null) {
            经验 += c.getEquippedFuMoMap().get(24);
        }
        if (c.getEquippedFuMoMap().get(26) != null) {
            加成经验 += 经验 * (c.getEquippedFuMoMap().get(26) / 100);
        }
        if (经验 > 0) {
            经验 += 加成经验;
            经验 = 经验 * 泡点倍率(c.getWorldId());
            name.append("福利泡点每5分钟 #r").append(经验).append("#k 经验\r\n");
        }

        int 金币 = c.get泡点金币();
        int 加成金币 = 0;
        if (c.getEquippedFuMoMap().get(23) != null) {
            金币 += c.getEquippedFuMoMap().get(23);
        }
        if (c.getEquippedFuMoMap().get(26) != null) {
            加成金币 += 金币 * (c.getEquippedFuMoMap().get(26) / 100);
        }
        if (金币 > 0) {
            金币 += 加成金币;
            金币 = 金币 * 泡点倍率(c.getWorldId());
            name.append("福利泡点每5分钟 #r").append(金币).append("#k 金币\r\n");
        }

        int 点券 = c.get泡点点券();
        int 加成点券 = 0;
        if (c.getEquippedFuMoMap().get(25) != null) {
            点券 += c.getEquippedFuMoMap().get(25);
        }
        if (c.getEquippedFuMoMap().get(26) != null) {
            加成点券 += 点券 * (c.getEquippedFuMoMap().get(26) / 100);
        }
        if (点券 > 0) {
            点券 += 加成点券;
            点券 = 点券 * 泡点倍率(c.getWorldId());
            name.append("福利泡点每5分钟 #r").append(点券).append("#k 点券\r\n");
        }

        //会员
        if (c.getvip() > 0) {
            name.append("\r\n\r\n");
            name.append("[#b自由会员#k](Lv.").append(会员等级划分(c.getvip())).append("):\r\n");
            name.append("#bExp:#k").append(c.getvip()).append("     #b剩余时间:#k").append(角色取会员时间(c.getId())).append("#b天#k\r\n");
            switch (会员等级划分(c.getvip())) {
                case 1:
                    name.append("狩猎经验+#r10%#k   基础爆率+#r20%#k");
                    break;
                case 2:
                    name.append("狩猎经验+#r12%#k   基础爆率+#r22%#k");
                    break;
                case 3:
                    name.append("狩猎经验+#r14%#k   基础爆率+#r24%#k");
                    break;
                case 4:
                    name.append("狩猎经验+#r16%#k   基础爆率+#r26%#k");
                    break;
                case 5:
                    name.append("狩猎经验+#r18%#k   基础爆率+#r28%#k");
                    break;
                case 6:
                    name.append("狩猎经 +#r20%#k   基础爆率+#r30%#k");
                    break;
            }
        }
        name.append("\r\n");
        //成就
        name.append("\r\n[#b角色荣耀#k]:\r\n");
        name.append("等级排行第 #b").append(获取等级排名(c.getId(), c.getWorldId())).append("#k 名，世界排行第 #b").append(获取世界等级排名(c.getId())).append("#k 名\r\n");
        name.append("财富排行第 #b").append(获取财富排名(c.getId(), c.getWorldId())).append("#k 名，世界排行第 #b").append(获取世界财富排名(c.getId())).append("#k 名\r\n");
        name.append("泡点排行第 #b").append(获取泡点排名(c.getId(), c.getWorldId())).append("#k 名，世界排行第 #b").append(获取世界泡点排名(c.getId())).append("#k 名\r\n");
        name.append("在线时长排行第 #b").append(获取在线排名(c.getId(), c.getWorldId())).append("#k 名，世界排行第 #b").append(获取世界在线排名(c.getId())).append("#k 名\r\n");
        name.append("锻造工艺排行第 #b").append(获取锻造工艺排名(c.getId(), c.getWorldId())).append("#k 名，世界排行第 #b").append(获取世界锻造工艺排名(c.getId())).append("#k 名\r\n");
        return name.toString();
    }

    public int 获取锻造工艺排名(int a, int b) {
        int DATA = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT rank FROM (SELECT @rownum := @rownum + 1 AS rank, `name`, `forgingExp`, `id` FROM characters, (SELECT @rownum := 0) r WHERE gm = 0 && world = " + b + "  && banned = 0 ORDER BY `forgingExp` DESC) AS T1 WHERE `id` = ? ");
            ps.setInt(1, a);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DATA = rs.getInt("rank");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取锻造工艺排名 - 数据库查询失败：" + Ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return DATA;
    }

    public int 获取世界锻造工艺排名(int a) {
        int DATA = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT rank FROM (SELECT @rownum := @rownum + 1 AS rank, `name`, `forgingExp`, `id` FROM characters, (SELECT @rownum := 0) r WHERE gm = 0  && banned = 0  ORDER BY `forgingExp` DESC) AS T1 WHERE `id` = ?");
            ps.setInt(1, a);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DATA = rs.getInt("rank");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取世界锻造工艺排名 - 数据库查询失败：" + Ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return DATA;
    }

    public int 获取等级排名(int a, int b) {
        int DATA = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT rank FROM (SELECT @rownum := @rownum + 1 AS rank, `name`, `level`, `id` FROM characters, (SELECT @rownum := 0) r WHERE gm = 0 && world = " + b + " && banned = 0 ORDER BY (level*10000000000+exp+totalOnlineTime) DESC) AS T1 WHERE `id` = ?");
            ps.setInt(1, a);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DATA = rs.getInt("rank");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取等级排名 - 数据库查询失败：" + Ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return DATA;
    }

    public int 获取世界等级排名(int a) {
        int DATA = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            //level*10000000000+exp+totalOnlineTime
            PreparedStatement ps = con.prepareStatement("SELECT rank FROM (SELECT @rownum := @rownum + 1 AS rank, `name`, `level`, `id` FROM characters, (SELECT @rownum := 0) r WHERE gm = 0  && banned = 0 ORDER BY (level*10000000000+exp+totalOnlineTime) DESC) AS T1 WHERE `id` = ?");
            ps.setInt(1, a);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DATA = rs.getInt("rank");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取等级排名 - 数据库查询失败：" + Ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return DATA;
    }

    public int 获取在线排名(int a, int b) {
        int DATA = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT rank FROM (SELECT @rownum := @rownum + 1 AS rank, `name`, `totalOnlineTime`, `id` FROM characters, (SELECT @rownum := 0) r WHERE gm = 0 && world = " + b + "  && banned = 0 ORDER BY `totalOnlineTime` DESC) AS T1 WHERE `id` = ? ");
            ps.setInt(1, a);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DATA = rs.getInt("rank");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取在线排名 - 数据库查询失败：" + Ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return DATA;
    }

    public int 获取世界在线排名(int a) {
        int DATA = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT rank FROM (SELECT @rownum := @rownum + 1 AS rank, `name`, `totalOnlineTime`, `id` FROM characters, (SELECT @rownum := 0) r WHERE gm = 0  && banned = 0  ORDER BY `totalOnlineTime` DESC) AS T1 WHERE `id` = ?");
            ps.setInt(1, a);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DATA = rs.getInt("rank");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取在线排名 - 数据库查询失败：" + Ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return DATA;
    }

    public int 获取战斗力排名(int a, int b) {
        int DATA = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT rank FROM (SELECT @rownum := @rownum + 1 AS rank, `name`, `Combat`, `id` FROM characters, (SELECT @rownum := 0) r WHERE gm = 0 && world = " + b + "  && banned = 0  ORDER BY `Combat` DESC) AS T1 WHERE `id` = ?");
            ps.setInt(1, a);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DATA = rs.getInt("rank");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取战斗力排名 - 数据库查询失败：" + Ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return DATA;
    }

    public int 获取世界战斗力排名(int a) {
        int DATA = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT rank FROM (SELECT @rownum := @rownum + 1 AS rank, `name`, `Combat`, `id` FROM characters, (SELECT @rownum := 0) r WHERE gm = 0   && banned = 0 ORDER BY `Combat` DESC) AS T1 WHERE `id` = ?");
            ps.setInt(1, a);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DATA = rs.getInt("rank");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取战斗力排名 - 数据库查询失败：" + Ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return DATA;
    }

    public int 获取杀怪排名(int a, int b) {
        int DATA = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT rank FROM (SELECT @rownum := @rownum + 1 AS rank, `name`, `KillMonster`, `id` FROM characters, (SELECT @rownum := 0) r WHERE gm = 0 && world = " + b + "  && banned = 0 ORDER BY `KillMonster` DESC) AS T1 WHERE `id` = ?");
            ps.setInt(1, a);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DATA = rs.getInt("rank");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取杀怪排名 - 数据库查询失败：" + Ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return DATA;
    }

    public int 获取世界杀怪排名(int a) {
        int DATA = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT rank FROM (SELECT @rownum := @rownum + 1 AS rank, `name`, `KillMonster`, `id` FROM characters, (SELECT @rownum := 0) r WHERE gm = 0  && banned = 0 ORDER BY `KillMonster` DESC) AS T1 WHERE `id` = ?");
            ps.setInt(1, a);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DATA = rs.getInt("rank");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取世界杀怪排名 - 数据库查询失败：" + Ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return DATA;
    }

    public int 获取泡点排名(int a, int b) {
        int DATA = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT rank FROM (SELECT @rownum := @rownum + 1 AS rank, `name`, `pdexp`, `id` FROM characters, (SELECT @rownum := 0) r WHERE gm = 0 && world = " + b + "  && banned = 0 ORDER BY `pdexp` DESC) AS T1 WHERE `id` = ?");
            ps.setInt(1, a);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DATA = rs.getInt("rank");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取泡点排名 - 数据库查询失败：" + Ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return DATA;
    }

    public int 获取世界泡点排名(int a) {
        int DATA = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT rank FROM (SELECT @rownum := @rownum + 1 AS rank, `name`, `pdexp`, `id` FROM characters, (SELECT @rownum := 0) r WHERE gm = 0  && banned = 0 ORDER BY `pdexp` DESC) AS T1 WHERE `id` = ?");
            ps.setInt(1, a);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DATA = rs.getInt("rank");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取世界泡点排名 - 数据库查询失败：" + Ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return DATA;
    }

    public int 获取财富排名(int a, int b) {
        int DATA = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT rank FROM (SELECT @rownum := @rownum + 1 AS rank, `name`, `meso`, `id` FROM characters, (SELECT @rownum := 0) r WHERE gm = 0 && world = " + b + "  && banned = 0 ORDER BY `meso` DESC) AS T1 WHERE `id` = ?");
            ps.setInt(1, a);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DATA = rs.getInt("rank");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取财富排名 - 数据库查询失败：" + Ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return DATA;
    }

    public int 获取世界财富排名(int a) {
        int DATA = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT rank FROM (SELECT @rownum := @rownum + 1 AS rank, `name`, `meso`, `id` FROM characters, (SELECT @rownum := 0) r WHERE gm = 0  && banned = 0 ORDER BY `meso` DESC) AS T1 WHERE `id` = ?");
            ps.setInt(1, a);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DATA = rs.getInt("rank");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取世界财富排名 - 数据库查询失败：" + Ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return DATA;
    }

    public int 获取排名(int a, int b, String c) {
        int DATA = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT rank FROM (SELECT @rownum := @rownum + 1 AS rank, `name`, `" + c + "`, `id` FROM characters, (SELECT @rownum := 0) r WHERE gm = 0 && world = " + b + "  && banned = 0 ORDER BY `" + c + "` DESC) AS T1 WHERE `id` = ?");
            ps.setInt(1, a);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DATA = rs.getInt("rank");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取排名 - 数据库查询失败：" + Ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return DATA;
    }

    public int 获取世界排名(int a, String c) {
        int DATA = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT rank FROM (SELECT @rownum := @rownum + 1 AS rank, `name`, `" + c + "`, `id` FROM characters, (SELECT @rownum := 0) r WHERE gm = 0  && banned = 0 ORDER BY `" + c + "` DESC) AS T1 WHERE `id` = ?");
            ps.setInt(1, a);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DATA = rs.getInt("rank");
                }
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取世界排名 - 数据库查询失败：" + Ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return DATA;
    }

    public String 显示所有附魔类型() {
        return 显示所有附魔();
    }

    public void 中奖记录(String a) {
        缓存序号++;
        if (缓存序号 >= 20) {
            缓存序号 = 0;
        }
        抽奖广播缓存.put(缓存序号, a);
    }

    public String 显示中奖记录() {
        StringBuilder name = new StringBuilder();
        for (Map.Entry<Integer, String> entry : 抽奖广播缓存.entrySet()) {
            String b = entry.getValue();
            name.append(b + "\r\n");
        }

        return name.toString();
    }

    public void 废弃副本随机(int a) {
        if (getPlayer().getParty() != null) {
            int x = getPlayer().getParty().getId();
            废弃副本随机.remove(x);
            废弃副本随机.put(x, a);
        }
    }

    public void 废弃副本重置随机() {
        if (getPlayer().getParty() != null) {
            int x = getPlayer().getParty().getId();
            废弃副本随机.remove(x);
        }
    }

    public int 获取废弃副本随机() {
        int x = -1;
        if (getPlayer().getParty() != null) {
            int z = getPlayer().getParty().getId();
            if (废弃副本随机.containsKey(z)) {
                return 废弃副本随机.get(z);
            }
        }
        return x;
    }

    public void 重置目标地图(int a) {
        getMap(a).resetFully();
    }

    public void 召唤怪物(int a, int x, int y) {
        getMap().spawnMonsterOnGroundBelow(a, x, y);
    }

    public boolean 是否副本(int a) {
        if (副本(a)) {
            return true;
        }
        return false;
    }

    public int 获取账号记录(String a) {
        int data = 0;
        Connection con = null;
        ResultSet rs = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts_jilu WHERE id = ? && jilu = '" + a + "'");
            ps.setInt(1, c.getAccountID());
            rs = ps.executeQuery();
            while (rs.next()) {
                data++;
            }
            rs.close();
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取账号记录、出错");
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

    public void 新增账号记录(String a) {
        Connection con = null;

        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO accounts_jilu (id, jilu) VALUES (?,?)");
            ps.setInt(1, c.getAccountID());
            ps.setString(2, a);

            ps.executeUpdate();
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("新增账号记录、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public void 新增充值记录(int a) {
        Connection con = null;

        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO accountsrecharge (World, qq,xianjin,a,b) VALUES (?,?,?,?,?)");
            ps.setInt(1, getPlayer().getWorldId());
            ps.setString(2, getPlayer().getQQ());
            ps.setInt(3, a);
            ps.setInt(4, Calendar.getInstance().get(Calendar.YEAR));
            ps.setInt(5, Calendar.getInstance().get(Calendar.MONTH) + 1);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("新增账号记录、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public int 获取角色记录(String a) {
        int data = 0;
        Connection con = null;
        ResultSet rs = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters_jilu WHERE id = ? && jilu = '" + a + "'");
            ps.setInt(1, getPlayer().getId());
            rs = ps.executeQuery();
            while (rs.next()) {
                data++;
            }
            rs.close();
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("获取角色记录、出错");
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

    public void 新增角色记录(String a) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO characters_jilu (id, jilu) VALUES (?,?)");
            ps.setInt(1, getPlayer().getId());
            ps.setString(2, a);

            ps.executeUpdate();
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("新增角色记录、出错");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public void 指定人发送信息(String a, String b) {
        私聊(a, b);
    }

    public void 指定群发送信息(String a, String b) {
        群(a, b);
    }

    public void 群发送信息(String a) {
        群(a, 大区群号1(getPlayer().getWorldId()));
    }

    public void 记录消费(int a) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET rmb = rmb + " + a + "  WHERE id = " + c.getAccountID() + ";")) {
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

    public int 判断消费() {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT rmb as DATA FROM accounts WHERE id = ?");
            ps.setInt(1, c.getAccountID());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = rs.getInt("DATA");
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException Ex) {
            System.err.println("角色取会员等级、出错");
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

    public void 附魔数据(int a) {
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE mxmxd_fumo_info SET gailv = gailv + 1 WHERE fumoType = " + a + ";")) {
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

    public String 显示在线玩家() {
        StringBuilder name = new StringBuilder();
        getPlayer().getWorld().getChannels().forEach((cserv) -> {
            cserv.getPlayerStorage().getAllCharacters().stream().filter((chr) -> !(chr == null)).forEach((chr) -> {
                if (chr.getId() != getPlayer().getId()) {
                    name.append("\t#L").append(chr.getId())
                            .append("# #b").append(chr.getName())
                            .append("#k  在 #b").append(chr.getClient().getChannel())
                            .append("#k 频道  #r").append(chr.getMap().getMapName()).append("#k#l \r\n");
                }
            });
        });

        return name.toString();
    }

    public String 显示地图在线玩家() {
        StringBuilder name = new StringBuilder();
        getPlayer().getMap().getCharactersThreadsafe()
                .forEach(chr
                        -> name.append("\t#L").append(chr.getId())
                        .append("# #b").append(chr.getName())
                        .append("#k  在 #b").append(chr.getClient().getChannel())
                        .append("#k 频道  #r").append(chr.getMap().getMapName()).append("#k#l \r\n"));
        return name.toString();
    }

    public String 显示跟踪在线玩家() {
        StringBuilder name = new StringBuilder();
        getPlayer().getWorld().getChannels().forEach((cserv) -> {
            cserv.getPlayerStorage().getAllCharacters().stream().filter((chr) -> !(chr == null))
                    .filter((chr) -> (chr.getId() != getPlayer().getId()))
                    .forEach((chr) -> {
                        name.append("\t#L").append(chr.getMapId())
                                .append("# #b").append(chr.getName())
                                .append("#k  在 #b").append(chr.getClient().getChannel())
                                .append("#k 频道  #r").append(chr.getMap().getMapName()).append("#k#l \r\n");
                    });
        });

        return name.toString();
    }

    public int 在线人数() {
        return getPlayer().getWorld().getChannels().stream().mapToInt(ch -> ch.getConnectedClients()).sum();
    }

    public int 总在线人数() {
        int a = 0;
        for (World world : Start.getInstance().getWorlds()) {
            for (ChannelServer cserv : world.getChannels()) {
                a += cserv.getConnectedClients();
            }
        }
        return a;
    }

    public int 在线人数(int x) {
        int a = 0;
        for (ChannelServer cserv : Start.getInstance().getWorldById(x).getChannels()) {
            a += cserv.getConnectedClients();
        }
        return a;
    }

    public void 断线(int a, String b) {
        Player p = getPlayer().getWorld().getCharByID(a);
        if (p != null) {
            p.bans(b);
            p.dropMessage(1, "数据异常，网络中断。");
            p.getClient().close();
        }
        群(b, 大区群号1(getPlayer().getWorldId()));
    }

    public void 设置检测1() {
        Connection con = Start.getInstance().getConnection();
        try {
            try (PreparedStatement ps = con.prepareStatement("UPDATE jiance SET x1 = " + getPlayer().getPosition().x + " , y1 = " + getPlayer().getPosition().y + " WHERE map = " + getPlayer().getMapId() + ";")) {
                ps.executeUpdate();
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

    public void 设置检测2() {
        Connection con = Start.getInstance().getConnection();
        try {
            try (PreparedStatement ps = con.prepareStatement("UPDATE jiance SET x2 = " + getPlayer().getPosition().x + " , y2 = " + getPlayer().getPosition().y + " WHERE map = " + getPlayer().getMapId() + ";")) {
                ps.executeUpdate();
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

    public void 设置检测3() {
        Connection con = Start.getInstance().getConnection();
        try {
            try (PreparedStatement ps = con.prepareStatement("UPDATE jiance SET x3 = " + getPlayer().getPosition().x + " , y3 = " + getPlayer().getPosition().y + " WHERE map = " + getPlayer().getMapId() + ";")) {
                ps.executeUpdate();
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

    public int 判断检测() {
        int data = 0;
        Connection con = Start.getInstance().getConnection();
        try {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM jiance WHERE map = ?")) {
                ps.setInt(1, getPlayer().getMapId());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        data += 1;
                    }
                    rs.close();
                }
            }
        } catch (SQLException Ex) {
            System.err.println("判断检测、出错");
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

    public void 初始化检测() {
        if (判断检测() == 0) {
            Connection con = Start.getInstance().getConnection();
            try {
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO jiance (map) VALUES (?)")) {
                    ps.setInt(1, getPlayer().getMapId());
                    ps.executeUpdate();
                }
            } catch (SQLException Ex) {
                System.err.println("设置检测、出错");
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

    public int 获取检测(String a) {
        int data = 0;
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT " + a + " as DATA FROM jiance WHERE map = ? ")) {
                ps.setInt(1, getPlayer().getMapId());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        data = rs.getInt("DATA");
                    }
                    rs.close();
                }
            }
        } catch (SQLException Ex) {
            System.err.println("获取检测、出错");
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

    public void 清除当前地图检测() {
        int map = getPlayer().getMapId();
        getPlayer().getMap().killMonster(9400711);

        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE jiance SET x1 = 0 , y1 = 0,x2 = 0 , y2 = 0,x3 = 0 , y3 = 0 WHERE map = " + map + ";")) {
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

        检测坐标.remove(map);
    }

    public void 载入检测(Player c) {
        写入检测();
        int uu = 9400711;
        getPlayer().getMap().killMonster(uu);
        if (检测坐标.get(getPlayer().getMapId()) != null) {
            Integer[] A = 检测坐标.get(getPlayer().getMapId());
            if (getPlayer().getMap().monsterCountById(uu) == 0) {
                if (A[0] != 0 && A[1] != 0) {
                    getPlayer().getMap().spawnMonsterOnGroundBelow(uu, A[0], A[1]);

                }
                if (A[2] != 0 && A[3] != 0) {
                    getPlayer().getMap().spawnMonsterOnGroundBelow(uu, A[2], A[3]);

                }
                if (A[4] != 0 && A[5] != 0) {
                    getPlayer().getMap().spawnMonsterOnGroundBelow(uu, A[4], A[5]);
                }
            }
        }
    }

    public long 行程(int a) {
        return 判断行程(getPlayer().getId(), a);
    }

    public long 现在时间() {
        return System.currentTimeMillis();
    }

    public String 显示仓库物品() {
        return 仓库物品(getPlayer().getWorldId(), getPlayer().getAccountID());
    }

    public int 显示仓库物品数量(int a) {
        return 取物品数量(a);
    }

    public int 显示仓库物品代码(int a) {
        return 取物品代码(a);
    }

    public long 显示仓库物品时间(int a) {
        return 取物品存入时间(a);
    }

    public void 修改物品数量(int a, int b) {
        修改仓库物品数量(a, b);
    }

    public void 添加仓库物品(int a, int b) {
        添加物品到仓库(a, b, getPlayer().getWorldId(), getPlayer().getAccountID());
    }

    public int 总共保管费() {
        return 所有保管费(getPlayer().getWorldId(), getPlayer().getAccountID());
    }

    public int 总共保管费包裹() {
        return 所有保管物品(getPlayer().getWorldId(), getPlayer().getAccountID());
    }

    public String 显示红包() {
        return 显示所有红包();
    }

    public int 每股价值(int a) {
        return 判断股票数值(a);
    }

    public int 每股剩余数量(int a) {
        return 判断股票剩余数量(a);
    }

    public void 修改股票数量(int a, int b) {
        修改股票剩余数量(a, b);
    }

    public int 获取股票(int a) {
        return 获取股票数量(a, getPlayer().getAccountID());
    }

    public String 查看股票记录(int a) {
        return 查看股票波动记录(a);
    }

    public void 修改推广收益(int a) {
        修改推广收益2(a, getPlayer().getAccountID());
    }

    public void 修改推广员(String a) {
        修改推广员2(a, getPlayer().getAccountID());
    }

    public int 获取推广收益() {
        return 获取推广收益2(getPlayer().getAccountID());
    }

    public int 获取推广收益log() {
        return 获取推广收益log2(getPlayer().getAccountID());
    }

    public String 获取推广员() {
        return 获取推广员2(getPlayer().getAccountID());
    }

    public String 显示推广关系() {
        return 显示推广关系2(getPlayer().getQQ());
    }

    public int 显示推广收益() {
        return 显示推广收益2(getPlayer().getQQ());
    }

    public void 收取所有推广收益() {
        收取所有推广收益2(getPlayer().getQQ());
    }

    public String 获取NPC名字(int a) {
        MapleNPC npc = MapleLifeFactory.getNPC(a);
        return npc.getName();
    }

    public void 加属性(String x, int a, int z) {
        Equip item = (Equip) c.getPlayer().getInventory(InventoryType.EQUIP).getItem((short) a).copy();
        if (item != null) {
            if (null != x) {
                switch (x) {
                    case "最大生命值":
                        item.setHp((short) (item.getHp() + z));
                        break;
                    case "最大法力值":
                        item.setMp((short) (item.getMp() + z));
                        break;
                    case "力量":
                        item.setStr((short) (item.getStr() + z));
                        break;
                    case "运气":
                        item.setLuk((short) (item.getLuk() + z));
                        break;
                    case "智力":
                        item.setInt((short) (item.getInt() + z));
                        break;
                    case "敏捷":
                        item.setDex((short) (item.getDex() + z));
                        break;
                    case "命中率":
                        item.setAcc((short) (item.getAcc() + z));
                        break;
                    case "跳跃力":
                        item.setJump((short) (item.getJump() + z));
                        break;
                    case "移动速度":
                        item.setSpeed((short) (item.getSpeed() + z));
                        break;
                    case "闪避率":
                        item.setAvoid((short) (item.getAvoid() + z));
                        break;
                    case "魔法攻击":
                        item.setMatk((short) (item.getMatk() + z));
                        break;
                    case "魔法防御":
                        item.setMdef((short) (item.getMdef() + z));
                        break;
                    case "物理攻击":
                        item.setWatk((short) (item.getWatk() + z));
                        break;
                    case "物理防御":
                        item.setWdef((short) (item.getWdef() + z));
                        break;
                    case "升级次数":
                        item.setUpgradeSlots((byte) (item.getUpgradeSlots() + z));
                        break;
                    default:
                        break;
                }
            }
            InventoryManipulator.removeFromSlot(c, InventoryType.EQUIP, (short) a, (short) 1, true);
            ItemInformationProvider i = ItemInformationProvider.getInstance();
            InventoryManipulator.addFromDrop(this.getClient(), item, "", false);
        }
    }

    public void 加锻造等级(int a, int z) {
        Equip item = (Equip) c.getPlayer().getInventory(InventoryType.EQUIP).getItem((short) a).copy();
        item.setDzlevel((item.getDzlevel() + z));
        InventoryManipulator.removeFromSlot(c, InventoryType.EQUIP, (short) a, (short) 1, true);
        ItemInformationProvider i = ItemInformationProvider.getInstance();
        InventoryManipulator.addFromDrop(this.getClient(), item, "", false);
    }

    public int 判断锻造等级(int a) {
        Equip item = (Equip) c.getPlayer().getInventory(InventoryType.EQUIP).getItem((short) a).copy();
        return item.getDzlevel();
    }

    /**
     * *
     * 增加点数
     *
     * @param Name
     * @param Channale
     * @param Piot
     */
    public void GainPiot(int world, int leixing, int Piot) {
        try {
            int ret = GetPiot(world, leixing);
            if (ret == -1) {
                ret = 0;
                PreparedStatement ps = null;
                try {
                    ps = Start.getInstance().getConnection().prepareStatement("INSERT INTO FullPoint (leixing, world,Point) VALUES (?, ?, ?)");
                    ps.setInt(1, leixing);
                    ps.setInt(2, world);
                    ps.setInt(3, ret);

                    ps.execute();
                } catch (SQLException e) {
                    System.out.println("xxxxxxxx:" + e);
                } finally {
                    try {
                        if (ps != null) {
                            ps.close();
                        }
                    } catch (SQLException e) {
                        System.out.println("xxxxxxxxzzzzzzz:" + e);
                    }
                }
            }
            ret += Piot;
            Connection con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE FullPoint SET `Point` = ? WHERE world = ? and leixing = ?");
            ps.setInt(1, ret);
            ps.setInt(2, world);
            ps.setInt(3, leixing);
            ps.execute();
            ps.close();
        } catch (SQLException sql) {
            System.err.println("獲取錯誤!!55" + sql);
        }
    }

    /**
     * 获取点数
     *
     * @return
     */
    public int GetPiot(int world, int leixing) {
        int ret = -1;
        try {
            Connection con = Start.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM FullPoint WHERE leixing = ? and world = ?");
            ps.setInt(1, leixing);
            ps.setInt(2, world);
            ResultSet rs = ps.executeQuery();
            rs.next();
            ret = rs.getInt("Point");
            rs.close();
            ps.close();
        } catch (SQLException ex) {
        }
        return ret;
    }

    public String 天梯积分排行() {
        return 天梯积分排行2(getPlayer().getId());
    }

    public int 天梯积分排行名次() {
        return 获取天梯线排名2(getPlayer().getId());
    }

    public int 查询孔数(final short a) {
        //Equip item = (Equip) c.getPlayer().getInventory(InventoryType.EQUIP).getItem((short) a).copy();
        return this.查询身上装备已打孔数(a);
    }

    public int 查询附魔(final short a) {
        return this.查询身上装备可附魔数(a);
    }

    public void 装备打孔(int a) {
        this.打孔((short) a);
    }

    public int 装备附魔(final short a, final int fuMoType, final int fuMoValue) {
        return 附魔(a, fuMoType, fuMoValue);
    }

    public int 装备清洗(final short a) {
        return 清洗身上装备附魔(a);
    }

    public int 装备清洗(final short a, final int b) {
        return 清洗身上装备附魔(a, b);
    }
}