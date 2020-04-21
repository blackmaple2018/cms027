package packet.creators;

import client.Client;
import client.player.PlayerKeyBinding;
import client.player.Player;
import client.player.PlayerQuest;
import client.player.PlayerStat;
import client.player.buffs.BuffStat;
import client.player.buffs.Disease;
import client.player.inventory.Equip;
import client.player.inventory.EquipScrollResult;
import static client.player.inventory.EquipScrollResult.FAIL;
import static client.player.inventory.EquipScrollResult.SUCCESS;
import client.player.inventory.Inventory;
import client.player.inventory.types.InventoryType;
import client.player.inventory.Item;
import client.player.inventory.ItemPet;
import client.player.inventory.ItemRing;
import client.player.inventory.types.ItemRingType;
import client.player.inventory.types.ItemType;
import client.player.inventory.TamingMob;
import client.player.skills.PlayerSkill;
import client.player.skills.PlayerSkillEntry;
import client.player.skills.PlayerSkillMacro;
import community.MapleBuddyListEntry;
import constants.MapConstants;
import constants.SkillConstants.ChiefBandit;
import handling.channel.handler.ChannelHeaders.*;
import handling.channel.handler.SummonHandler;
import java.awt.Point;
import java.awt.Rectangle;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import launch.Start;
import packet.opcode.OutHeader;
import packet.transfer.write.OutPacket;
import server.itens.ItemInformationProvider;
import server.shops.ShopItem;
import server.life.npc.MapleNPC;
import server.life.MobSkill;
import server.life.status.MonsterStatus;
import server.maps.Field;
import server.maps.FieldItem;
import server.maps.MapleLove;
import server.maps.MapleMist;
import server.maps.reactors.Reactor;
import server.maps.MapleSummon;
import server.movement.LifeMovementFragment;
import server.quest.MapleQuestStatus;
import tools.BitTools;
import tools.HexTool;
import tools.KoreanDateUtil;
import tools.Pair;
import tools.Randomizer;
import tools.StringUtil;

/**
 * @author GabrielSin
 */
public class PacketCreator {

    public static final List<Pair<PlayerStat, Integer>> EMPTY_STATUPDATE = Collections.emptyList();
    public final static byte[] CHAR_INFO_MAGIC = new byte[]{(byte) 0xff, (byte) 0xc9, (byte) 0x9a, 0x3b};
    public final static byte[] ITEM_MAGIC = new byte[]{(byte) 0x80, 5};
    public final static long FT_UT_OFFSET = 116445024000000000L;
    //116444592000000000L
    //116445024000000000L

    public static long GetKoreanTimestamp(long realTimestamp) {
        long time = (realTimestamp / 1000 / 60);
        return ((time * 600000000) + FT_UT_OFFSET);
    }

    public static long GetTime(long realTimestamp) {
        return ((realTimestamp * 10000) + FT_UT_OFFSET);
    }

    public static void AddExpirationTime(OutPacket wp, long time) {
        AddExpirationTime(wp, time, true);
    }

    private static void AddExpirationTime(OutPacket wp, long time, boolean addZero) {
        if (time > 0) {
            wp.writeLong(GetTime(time));
        } else {
            wp.writeBytes(HexTool.getByteArrayFromHexString("00 80 05 BB 46 E6 17 02"));
        }
    }

    public static OutPacket EnableActions() {
        return UpdatePlayerStats(null, true, 0);
    }

    public static OutPacket UpdatePlayerStats(Player p, int updateMask) {//自己操作
        return UpdatePlayerStats(p, true, updateMask);
    }

    public static OutPacket UpdatePlayerStats(Player p, boolean itemReaction, int updateMask) {
        OutPacket wp = new OutPacket(OutHeader.UPDATE_STATS);
        //19 01 00 3C 00 00 27 0D 3B 07 F8 00 F8 00
        if (itemReaction) {
            wp.writeBool(true);
        } else {
            wp.writeBool(false);
        }
        wp.writeInt(updateMask);
        if ((updateMask & PlayerStat.SKIN.getValue()) == PlayerStat.SKIN.getValue()) {
            wp.write(p.getSkinColor().getId());
        }
        if ((updateMask & PlayerStat.FACE.getValue()) == PlayerStat.FACE.getValue()) {
            wp.writeInt(p.getFace());
        }
        if ((updateMask & PlayerStat.HAIR.getValue()) == PlayerStat.HAIR.getValue()) {
            wp.writeInt(p.getHair());
        }
        if ((updateMask & PlayerStat.PET.getValue()) == PlayerStat.PET.getValue()) {
            wp.writeLong(0);//petCashSN
        }
        if ((updateMask & PlayerStat.LEVEL.getValue()) == PlayerStat.LEVEL.getValue()) {
            wp.write(p.getLevel());
        }
        if ((updateMask & PlayerStat.JOB.getValue()) == PlayerStat.JOB.getValue()) {
            wp.writeShort(p.getJobId());
        }
        if ((updateMask & PlayerStat.STR.getValue()) == PlayerStat.STR.getValue()) {
            wp.writeShort(p.getStr());
        }
        if ((updateMask & PlayerStat.DEX.getValue()) == PlayerStat.DEX.getValue()) {
            wp.writeShort(p.getDex());
        }
        if ((updateMask & PlayerStat.INT.getValue()) == PlayerStat.INT.getValue()) {
            wp.writeShort(p.getInt());
        }
        if ((updateMask & PlayerStat.LUK.getValue()) == PlayerStat.LUK.getValue()) {
            wp.writeShort(p.getLuk());
        }
        if ((updateMask & PlayerStat.HP.getValue()) == PlayerStat.HP.getValue()) {
            wp.writeShort(p.getHp());
        }
        if ((updateMask & PlayerStat.MAXHP.getValue()) == PlayerStat.MAXHP.getValue()) {
            wp.writeShort(p.getMaxHp());
        }
        if ((updateMask & PlayerStat.MP.getValue()) == PlayerStat.MP.getValue()) {
            wp.writeShort(p.getMp());
        }
        if ((updateMask & PlayerStat.MAXMP.getValue()) == PlayerStat.MAXMP.getValue()) {
            wp.writeShort(p.getMaxMp());
        }
        if ((updateMask & PlayerStat.AVAILABLEAP.getValue()) == PlayerStat.AVAILABLEAP.getValue()) {
            wp.writeShort(p.getRemainingAp());
        }
        if ((updateMask & PlayerStat.AVAILABLESP.getValue()) == PlayerStat.AVAILABLESP.getValue()) {
            wp.writeShort(p.getRemainingSp());
        }
        if ((updateMask & PlayerStat.EXP.getValue()) == PlayerStat.EXP.getValue()) {
            wp.writeInt(p.getCurrentExp());
        }
        if ((updateMask & PlayerStat.FAME.getValue()) == PlayerStat.FAME.getValue()) {
            wp.writeShort(p.getFame());
        }
        if ((updateMask & PlayerStat.MESO.getValue()) == PlayerStat.MESO.getValue()) {
            wp.writeInt(p.getMeso());
        }
        return wp;
    }

    /**
     * Gets a server message packet.
     *
     * @param message The message to convey.
     * @return The server message packet.
     */
    public static OutPacket ServerMessage(String message) {
        return ServerMessage(4, 0, message, true, false);
    }

    public static OutPacket ServerMessage(int type, String message) {
        return ServerMessage(4, 0, message, true, false);
    }

    /**
     * Gets a server notice packet.
     * <p>
     * Possible values for <code>type</code>:<br>
     * 0: [Notice]<br>
     * 1: Popup<br>
     * 2: Megaphone<br>
     * 3: Super Megaphone<br>
     * 4: Scrolling message at top<br>
     * 5: Pink Text<br>
     * 6: Lightblue Text
     *
     * @param type The type of the notice.
     * @param message The message to convey.
     * @return The server notice packet.
     */
    public static OutPacket ServerNotice(int type, String message) {
        return ServerMessage(type, 0, message, false, false);
    }

    /**
     * Gets a server notice packet.
     * <p>
     * Possible values for <code>type</code>:<br>
     * 0: [Notice]<br>
     * 1: Popup<br>
     * 2: Megaphone<br>
     * 3: Super Megaphone<br>
     * 4: Scrolling message at top<br>
     * 5: Pink Text<br>
     * 6: Lightblue Text
     *
     * @param type The type of the notice.
     * @param channel The channel this notice was sent on.
     * @param message The message to convey.
     * @return The server notice packet.
     */
    public static OutPacket ServerNotice(int type, int channel, String message) {
        return ServerMessage(type, channel, message, false, false);
    }

    public static OutPacket ServerNotice(int type, int channel, String message, boolean smegaEar) {
        return ServerMessage(type, channel, message, false, smegaEar);
    }

    /**
     * Gets a server message packet.
     * <p>
     * Possible values for <code>type</code>:<br>
     * 0: [Notice]<br>
     * 1: Popup<br>
     * 2: Megaphone<br>
     * 3: Super Megaphone<br>
     * 4: Scrolling message at top<br>
     * 5: Pink Text<br>
     * 6: Lightblue Text
     *
     * @param type The type of the notice.
     * @param channel The channel this notice was sent on.
     * @param message The message to convey.
     * @param servermessage Is this a scrolling ticker?
     * @return The server notice packet.
     */
    private static OutPacket ServerMessage(int type, int channel, String message, boolean servermessage, boolean megaEar) {
        OutPacket wp = new OutPacket(OutHeader.SERVERMESSAGE);
        wp.write(type);
        if (servermessage) {
            wp.writeBool(true);
        }
        wp.writeMapleAsciiString(message);
        if (type == 3) {
            wp.write(channel - 1);
            wp.write(megaEar ? 1 : 0);
        }
        return wp;
    }

    /**
     * Gets an avatar megaphone packet.
     *
     * @param chr The character using the avatar megaphone.
     * @param channel The channel the character is on.
     * @param itemId The ID of the avatar-mega.
     * @param message The message that is sent.
     * @param ear
     * @return The avatar mega packet.
     */
    public static OutPacket GetAvatarMega(Player p, int channel, int itemID, List<String> message, boolean ear) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.AVATAR_MEGA.getValue());
        wp.writeInt(itemID);
        wp.writeMapleAsciiString(p.getName());
        for (String s : message) {
            wp.writeMapleAsciiString(s);
        }
        wp.writeInt(channel - 1);
        wp.write(ear ? 1 : 0);
        AddCharLook(wp, p, true);
        return wp;
    }

    public static OutPacket SendHint(String message, short width, short height) {
        if (width < 1) {
            width = (short) Math.max(message.length() * 10, 40);
        }
        if (height < 5) {
            height = 5;
        }
        OutPacket wp = new OutPacket(9 + message.length());
        wp.write(OutHeader.PLAYER_HINT.getValue());
        wp.writeMapleAsciiString(message);
        wp.writeShort(width);
        wp.writeShort(height);
        wp.writeBool(true);
        return wp;
    }

    public static OutPacket SendYellowTip(String tip) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.TIP_MESSAGE.getValue());
        wp.write(0xFF);
        wp.writeMapleAsciiString(tip);
        wp.writeShort(0);
        return wp;
    }

    public static OutPacket CompleteQuest(short quest, long time) {
        OutPacket wp = new OutPacket(OutHeader.SHOW_STATUS_INFO);
        wp.write(PacketHeaders.STATUS_INFO_QUEST);
        wp.writeShort(quest);
        wp.write(MapleQuestStatus.Status.COMPLETED.getId());
        wp.writeLong(time);
        return wp;
    }

    public static OutPacket UpdateQuestInfo(short quest, int npc, byte progress) {
        OutPacket wp = new OutPacket(OutHeader.SHOW_STATUS_INFO);
        wp.write(progress);
        wp.writeShort(quest);
        wp.writeInt(npc);
        wp.writeInt(0);
        return wp;
    }

    public static OutPacket updateQuestData(int quest, String data) {
        OutPacket wp = new OutPacket(OutHeader.SHOW_STATUS_INFO);
        wp.write(1);
        wp.write(1);
        wp.writeInt(quest);
        wp.writeMapleAsciiString(data);
        return wp;
    }

    public static OutPacket removeQuest(int quest) {
        OutPacket wp = new OutPacket(OutHeader.SHOW_STATUS_INFO);
        wp.write(1);
        wp.write(0);
        wp.writeInt(quest);
        return wp;
    }

    public static OutPacket GetShowQuestCompletion(int id) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.SHOW_QUEST_COMPLETION.getValue());
        wp.writeShort(id);
        return wp;
    }

    public static OutPacket RemoveQuestTimeLimit(final short quest) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.UPDATE_QUEST_INFO.getValue());
        wp.write(7);
        wp.writeShort(1);
        wp.writeShort(quest);
        return wp;
    }

    public static OutPacket AddQuestTimeLimit(final short quest, final int time) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.UPDATE_QUEST_INFO.getValue());
        wp.write(6);
        wp.writeShort(1);
        wp.writeShort(quest);
        wp.writeInt(time);
        return wp;
    }

    public static OutPacket UpdateQuestFinish(short quest, int npc, short nextQuest) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.UPDATE_QUEST_INFO.getValue());
        wp.write(8);
        wp.writeShort(quest);
        wp.writeInt(npc);
        wp.writeShort(nextQuest);
        return wp;
    }

    public static OutPacket QuestError(short quest) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.UPDATE_QUEST_INFO.getValue());
        wp.write(0x0A);
        wp.writeShort(quest);
        return wp;
    }

    public static OutPacket QuestFailure(byte type) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.UPDATE_QUEST_INFO.getValue());
        wp.write(type);
        return wp;
    }

    public static OutPacket QuestExpire(short quest) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.UPDATE_QUEST_INFO.getValue());
        wp.write(0x0F);
        wp.writeShort(quest);
        return wp;
    }

    public static OutPacket UpdateQuest(MapleQuestStatus quest, boolean infoUpdate) {
        OutPacket wp = new OutPacket(OutHeader.SHOW_STATUS_INFO);
        wp.write(PacketHeaders.STATUS_INFO_QUEST);
        wp.writeShort(infoUpdate ? quest.getQuest().getInfoNumber() : quest.getQuest().getId());
        if (infoUpdate) {
            wp.write(MapleQuestStatus.Status.STARTED.getId());
        } else {
            wp.write(quest.getStatus().getId());
        }
        wp.writeMapleAsciiString(quest.getQuestData());
        return wp;
    }

    public static OutPacket UpdateQuestInfo(short quest, int npc) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.UPDATE_QUEST_INFO.getValue());
        wp.write(8);
        wp.writeShort(quest);
        wp.writeInt(npc);
        wp.writeInt(0);
        return wp;
    }

    public static OutPacket ItemExpired(int itemID) {
        OutPacket wp = new OutPacket(OutHeader.SHOW_STATUS_INFO);
        wp.write(PacketHeaders.STATUS_INFO_EXPIRE);
        wp.writeInt(itemID);
        return wp;
    }

    public static OutPacket RemovePlayerFromMap(int cID) {
        OutPacket wp = new OutPacket(OutHeader.REMOVE_PLAYER_FROM_MAP);
        wp.writeInt(cID);
        return wp;
    }

    public static OutPacket RemoveSpecialMapObject(MapleSummon summon, boolean animated) {
        OutPacket wp = new OutPacket(OutHeader.REMOVE_SPECIAL_MAPOBJECT);
        wp.writeInt(summon.getOwnerId());
        wp.writeInt(summon.getSkill());
        wp.write(animated ? 4 : 1);
        return wp;
    }

    public static OutPacket SpawnPlayerMapObject(Player p) {
        OutPacket wp = new OutPacket(OutHeader.SPAWN_PLAYER);

        wp.writeInt(p.getId());
        wp.writeMapleAsciiString(p.getName());

        /*
         角色第二人进入地图技能BUFF数据解析v27.1
         By WL
         */
        int mask = 0;
        List<BuffStat> buffstats = Arrays.asList(
                BuffStat.Speed, BuffStat.ComboAttack, BuffStat.Charges,
                BuffStat.Stun, BuffStat.Darkness, BuffStat.Seal, BuffStat.Weakness,
                BuffStat.Poison, BuffStat.SoulArrow, BuffStat.ShadowPartner, BuffStat.DarkSight);

        mask = buffstats.stream()
                .filter((bs) -> (p.getBuffedValue(bs) != null))
                .map((bs) -> bs.getValue())
                .reduce(mask, (accumulator, _item) -> accumulator | _item);
        wp.writeInt(mask);
        for (BuffStat bs : buffstats) {
            if (p.getBuffedValue(bs) != null) {
                switch (bs) {
                    case Speed:
                    case ComboAttack:
                        wp.write(p.getBuffedValue(bs).byteValue());
                        break;
                    case Charges:
                    case Stun:
                    case Darkness:
                    case Seal:
                    case Weakness:
                        wp.writeInt(p.getBuffSource(bs));
                        break;
                    case Poison:
                        wp.writeShort(p.getBuffedValue(bs).shortValue());
                        wp.writeInt(p.getBuffSource(bs));
                        break;
                }
            }
        }

        wp.write(p.getGender());
        wp.write(p.getSkinColor().getId());
        wp.writeInt(p.getFace());
        wp.write(0);
        wp.writeInt(p.getHair());

        Inventory equip = p.getInventory(InventoryType.EQUIPPED);
        Map<Short, Integer> myEquip = new LinkedHashMap<>();
        Map<Short, Integer> maskedEquip = new LinkedHashMap<>();

        equip.list().stream().forEach((item) -> {
            short pos = (short) Math.abs(item.getPosition());
            if (pos != 111) {
                if (pos > 100) {
                    pos -= 100;
                    myEquip.put(pos, item.getItemId());
                } else {
                    if (myEquip.get(pos) != null) {
                        maskedEquip.put(pos, item.getItemId());
                    } else {
                        myEquip.put(pos, item.getItemId());
                    }
                }
            }
        });

        myEquip.entrySet().stream().map((entry) -> {
            wp.write(entry.getKey());
            return entry;
        }).forEach((entry) -> {
            wp.writeInt(entry.getValue());
        });
        wp.write(111);
        Item cWeapon = equip.getItem((short) -111);
        wp.writeInt(cWeapon != null ? cWeapon.getItemId() : 0);
        wp.write(-1);
        wp.writeInt(cWeapon != null ? cWeapon.getItemId() : 0);

        wp.writeInt(0);
        wp.writeInt(p.getItemEffect());
        wp.writeInt(p.getInventory(InventoryType.USE).countById(5110000));
        wp.writePos(p.getPosition());
        wp.write(p.getStance());
        wp.writeShort(0);
        ItemPet pet = p.getPet(0);
        wp.writeBool(pet != null);
        if (pet != null) {
            PetPackets.AddPetInfo(wp, pet, false);
        }
        wp.write(0);//minigames
        wp.write(0);//ringLooks
        //AddRingLooks(wp, p);
        wp.write(0);
        wp.write(p.getTeam());
        return wp;
    }

    private static void AddRingLooks(OutPacket wp, Player p) {
        wp.write(p.getEquippedRing(ItemRingType.CRUSH_RING.getType()) != 0 ? 1 : 0);
        p.getCrushRings().stream().filter((ring) -> (ring.getRingDatabaseId() == p.getEquippedRing(ItemRingType.CRUSH_RING.getType()))).map((ring) -> {
            wp.writeInt(ring.getRingDatabaseId());
            return ring;
        }).map((ring) -> {
            wp.writeInt(0);
            wp.writeInt(ring.getPartnerRingDatabaseId());
            return ring;
        }).forEach((ring) -> {
            wp.writeInt(0);
            wp.writeInt(ring.getItemId());
        });
        wp.write(p.getEquippedRing(ItemRingType.FRIENDSHIP_RING.getType()) != 0 ? 1 : 0);
        p.getFriendshipRings().stream().filter((ring) -> (ring.getRingDatabaseId() == p.getEquippedRing(ItemRingType.FRIENDSHIP_RING.getType()))).map((ring) -> {
            wp.writeInt(ring.getRingDatabaseId());
            return ring;
        }).map((ring) -> {
            wp.writeInt(0);
            wp.writeInt(ring.getPartnerRingDatabaseId());
            return ring;
        }).forEach((ring) -> {
            wp.writeInt(0);
            wp.writeInt(ring.getItemId());
        });
        wp.write(p.getEquippedRing(ItemRingType.WEDDING_RING.getType()) != 0 ? 1 : 0);
        for (ItemRing ring : p.getWeddingRings()) {
            if (ring.getRingDatabaseId() == p.getEquippedRing(ItemRingType.WEDDING_RING.getType())) {
                wp.writeInt(ring.getPartnerCharacterId());
                wp.writeInt(p.getId());
                wp.writeInt(ring.getItemId());
            }
        }
    }

    public static OutPacket UseChalkBoard(Player p, boolean close) {
        final OutPacket wp = new OutPacket();
        wp.write(OutHeader.CHANGE_BINDING.getValue());
        wp.writeInt(p.getId());
        if (close) {
            wp.writeBool(false);
        } else {
            wp.writeBool(true);
            wp.writeMapleAsciiString(p.getChalkboard());
        }
        return wp;
    }

    private static int GetLongMask(List<Pair<BuffStat, Integer>> statups) {
        int mask = 0;
        for (Pair<BuffStat, Integer> statup : statups) {
            mask |= statup.getLeft().getValue();
        }
        return mask;
    }

    private static int GetLongMaskFromList(List<BuffStat> statups) {
        int mask = 0;
        for (BuffStat statup : statups) {
            mask |= statup.getValue();
        }
        return mask;
    }

    private static int GetLongMaskFromListD(List<Disease> statups) {
        int mask = 0;
        for (Disease statup : statups) {
            mask |= statup.getValue();
        }
        return mask;
    }

    private static int GetLongMaskD(List<Pair<Disease, Integer>> statups) {
        int mask = 0;
        for (Pair<Disease, Integer> statup : statups) {
            mask |= statup.getLeft().getValue();
        }
        return mask;
    }

    public static OutPacket CancelBuff(List<BuffStat> statups) {
        OutPacket wp = new OutPacket(OutHeader.FIRST_PERSON_CANCEL_STATUS_EFFECT);
        int mask = GetLongMaskFromList(statups);
        wp.writeInt(mask);
        wp.write(3);
        return wp;
    }

    public static OutPacket CancelDebuff(int mask) {
        OutPacket wp = new OutPacket(OutHeader.FIRST_PERSON_CANCEL_STATUS_EFFECT);
        wp.writeInt(mask);
        wp.write(0);
        return wp;
    }

    public static OutPacket CancelDebuff(List<Disease> statups) {
        OutPacket wp = new OutPacket(OutHeader.FIRST_PERSON_CANCEL_STATUS_EFFECT);
        int mask = GetLongMaskFromListD(statups);
        wp.writeInt(mask);
        wp.write(0);
        return wp;
    }

    public static OutPacket CancelDebuff(int mask, boolean first) {
        OutPacket mplew = new OutPacket(OutHeader.FIRST_PERSON_CANCEL_STATUS_EFFECT);

        mplew.writeInt(first ? 0 : mask);
        mplew.write(1);

        return mplew;
    }

    public static OutPacket GiveBuffTest(int buffID, int buffLength, int mask) {
        OutPacket wp = new OutPacket(OutHeader.FIRST_PERSON_APPLY_STATUS_EFFECT);
        wp.writeInt(mask);
        wp.writeShort(1);//nValue
        wp.writeInt(buffID);//nReason
        wp.writeShort(buffLength);
        wp.writeShort(0);
        return wp;
    }

    public static OutPacket GiveBuff(int buffID, int buffLength, List<Pair<BuffStat, Integer>> statups) {
        OutPacket wp = new OutPacket(OutHeader.FIRST_PERSON_APPLY_STATUS_EFFECT);
        int mask = GetLongMask(statups);
        wp.writeInt(mask);
        for (Pair<BuffStat, Integer> statup : statups) {
            wp.writeShort(statup.getRight().shortValue());
            wp.writeInt(buffID);
            wp.writeShort(buffLength);
        }
        if (buffID == 1004 || buffID == 5221006) {
            wp.writeInt(0x2F258DB9);
        } else {
            wp.writeShort(0);
        }
        wp.write(0);
        wp.write(0);
        wp.write(0);
        return wp;
    }

//     public static OutPacket GiveBuff(int buffid, int bufflength, List<Pair<BuffStat, Integer>> statups) {
//        OutPacket mplew = new OutPacket();
//        mplew.write(OutHeader.GIVE_BUFF.getValue());
//        int mask = GetLongMask(statups);
//        if (bufflength % 10000000 != 1004 && bufflength != 5221006) {
//            mplew.writeLong(0);
//        } else {
//            mplew.writeInt(0);
//        }
//        mplew.writeLong(mask);
//        if (bufflength % 10000000 == 1004 || bufflength == 5221006) {
//            mplew.writeInt(0);
//        }
//        for (Pair<BuffStat, Integer> statup : statups) {
//            mplew.writeShort(statup.getRight().shortValue());
//            mplew.writeInt(buffid);
//            mplew.writeInt(bufflength);
//        }
//        if (bufflength % 10000000 == 1004 || bufflength == 5221006) {
//            mplew.writeInt(0);
//        } else {
//            mplew.writeShort(0);
//        }
//        mplew.write(0);  
//        mplew.write(0);  
//        mplew.write(0);
//        if (bufflength % 10000000 == 1004 || bufflength == 5221006) {
//            mplew.write(0);
//        }
//        return mplew;
//    }
    public static OutPacket GiveBuff(int buffID, int buffLength, List<Pair<BuffStat, Integer>> statups, boolean morph, boolean ismount, TamingMob mount) {
        OutPacket wp = new OutPacket(OutHeader.FIRST_PERSON_APPLY_STATUS_EFFECT);
        //1A [02 00 00 00] [28 00] [2B 46 0F 00] [E0 93] 28 00 2B 46 0F 00 E0 93 00 00 00 00 00
        int mask = GetLongMask(statups);
        wp.writeInt(mask);
        if (!ismount) {
            for (Pair<BuffStat, Integer> statup : statups) {
                wp.writeShort(statup.getRight().shortValue());
                wp.writeInt(buffID);
                wp.writeShort(buffLength);
            }
            wp.writeShort(0);
            wp.write(0);
            wp.write(0);
            wp.write(0);
        } else {
            if (ismount) {
                wp.writeShort(0);
                wp.writeInt(mount.getItemId());
                wp.writeInt(mount.getSkillId());
                wp.writeInt(0);
                wp.writeShort(0);
                wp.write(0);
            } else {
                return null;
            }
        }
        return wp;
    }

//    public static OutPacket GiveForeignBuff(int cID, List<Pair<BuffStat, Integer>> statups, boolean morph) {
//        OutPacket wp = new OutPacket();
//        wp.write(OutHeader.GIVE_FOREIGN_BUFF.getValue());
//        wp.writeInt(cID);
//        int mask = GetLongMask(statups);
//        wp.writeLong(0);
//        wp.writeLong(mask);
//        for (Pair<BuffStat, Integer> statup : statups) {
//            if (morph) {
//                wp.write(statup.getRight().byteValue());
//            } else {
//                wp.writeShort(statup.getRight().shortValue());
//            }
//        }
//        wp.writeShort(0);
//        if (morph) {
//            wp.writeShort(0);
//        }
//        wp.write(0);
//        return wp;
//    }
    public static OutPacket BuffMapEffect(int cid, List<Pair<BuffStat, Integer>> statups, boolean morph) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.THIRD_PERSON_APPLY_STATUS_EFFECT.getValue());
        wp.writeInt(cid);
        int mask = GetLongMask(statups);
        wp.writeInt(mask);
        for (Pair<BuffStat, Integer> statup : statups) {
            wp.writeShort(statup.getRight().shortValue());
            if (morph) {
                wp.write(statup.getRight().byteValue());
            }
        }
        wp.write(0);
        wp.writeShort(0);
        return wp;
    }

    public static OutPacket GiveForeignDebuff(int cID, List<Pair<Disease, Integer>> statups, MobSkill skill) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.THIRD_PERSON_APPLY_STATUS_EFFECT.getValue());
        wp.writeInt(cID);
        int mask = GetLongMaskD(statups);
        wp.writeInt(mask);
        for (@SuppressWarnings("unused") Pair<Disease, Integer> statup : statups) {
            wp.writeShort(skill.getSkillId());
            wp.writeShort(skill.getSkillLevel());
        }
        wp.writeShort(0);
        wp.writeShort(900);
        return wp;
    }

    public static OutPacket GiveForeignDebuff(int cid, final List<Pair<Disease, Integer>> statups, int skillid, int level) {

        OutPacket wp = new OutPacket();

        wp.write(OutHeader.THIRD_PERSON_APPLY_STATUS_EFFECT.getValue());
        wp.writeInt(cid);
        int mask = GetLongMaskD(statups);
        wp.writeInt(mask);

        if (skillid == 125) {
            wp.writeShort(0);
        }
        wp.writeShort(skillid);
        wp.writeShort(level);
        wp.writeShort(0);
        wp.writeShort(900);
        return wp;
    }

    public static OutPacket GiveForeignDebuff(int cID, int mask, MobSkill skill) {
        OutPacket wp = new OutPacket(OutHeader.THIRD_PERSON_APPLY_STATUS_EFFECT);
        wp.writeInt(cID);
        wp.writeInt(mask);
        wp.writeShort(skill.getSkillId());
        wp.writeShort(skill.getSkillLevel());
        wp.writeShort(0);
        wp.writeShort(900);
        return wp;
    }

    public static OutPacket GiveDebuff(int mask, List<Pair<Disease, Integer>> statups, MobSkill skill) {
        OutPacket wp = new OutPacket(OutHeader.FIRST_PERSON_APPLY_STATUS_EFFECT);
        wp.writeLong(0);
        wp.writeLong(mask);
        for (Pair<Disease, Integer> statup : statups) {
            wp.writeShort(statup.getRight().shortValue());
            wp.writeShort(skill.getSkillId());
            wp.writeShort(skill.getSkillLevel());
            wp.writeInt((int) skill.getDuration());
        }
        wp.writeShort(0);
        wp.writeShort(900);
        wp.write(1);

        return wp;
    }

    public static OutPacket GiveDebuff(List<Pair<Disease, Integer>> statups, MobSkill skill) {
        OutPacket wp = new OutPacket(OutHeader.FIRST_PERSON_APPLY_STATUS_EFFECT);
        int mask = GetLongMaskD(statups);
        wp.writeLong(0);
        wp.writeLong(mask);
        for (Pair<Disease, Integer> statup : statups) {
            wp.writeShort(statup.getRight().shortValue());
            wp.writeShort(skill.getSkillId());
            wp.writeShort(skill.getSkillLevel());
            wp.writeInt((int) skill.getDuration());
        }
        wp.writeShort(0);
        wp.writeShort(900);
        wp.write(1);
        return wp;
    }

    public static OutPacket GiveDebuff(final List<Pair<Disease, Integer>> statups, int skillid, int level, int duration) {
        OutPacket wp = new OutPacket(OutHeader.FIRST_PERSON_APPLY_STATUS_EFFECT);
        int mask = GetLongMaskD(statups);
        wp.writeLong(0);
        wp.writeLong(mask);

        for (Pair<Disease, Integer> statup : statups) {
            wp.writeShort(statup.getRight().shortValue());
            wp.writeShort(skillid);
            wp.writeShort(level);
            wp.writeInt((int) duration);
        }
        wp.writeShort(0);
        wp.writeShort(900);
        wp.write(1);
        return wp;
    }

    public static OutPacket CancelForeignBuff(int cID, List<BuffStat> statups) {
        OutPacket wp = new OutPacket(OutHeader.THIRD_PERSON_CANCEL_STATUS_EFFECT);
        wp.writeInt(cID);
        int mask = GetLongMaskFromList(statups);
        wp.writeInt(mask);
        return wp;
    }

    public static OutPacket CancelForeignDebuff(int cID, int mask) {
        OutPacket wp = new OutPacket(OutHeader.THIRD_PERSON_CANCEL_STATUS_EFFECT);
        wp.writeInt(cID);
        wp.writeInt(mask);
        return wp;
    }

    public static OutPacket CancelForeignDebuff(int cID, List<Disease> statups) {
        OutPacket wp = new OutPacket(OutHeader.THIRD_PERSON_CANCEL_STATUS_EFFECT);
        wp.writeInt(cID);
        int mask = GetLongMaskFromListD(statups);
        wp.writeInt(mask);
        return wp;
    }

    public static OutPacket CancelForeignDebuff(int cid, int mask, boolean first) {
        OutPacket mplew = new OutPacket(OutHeader.THIRD_PERSON_CANCEL_STATUS_EFFECT);
        mplew.writeInt(cid);
        mplew.writeInt(mask);
        return mplew;
    }

    public static OutPacket UpdateAriantPQRanking(String name, int score, boolean empty) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.ARIANT_PQ_START.getValue());
        wp.write(empty ? 0 : 1);
        if (!empty) {
            wp.writeMapleAsciiString(name);
            wp.writeInt(score);
        }
        return wp;
    }

    public static OutPacket ShowAriantScoreBoard() {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.ARIANT_SCOREBOARD.getValue());
        return wp;
    }

    public static OutPacket GetShowItemGain(int itemID, short quantity) {
        return GetShowItemGain(itemID, quantity, true);
    }

    public static OutPacket GetShowItemGain(int itemID, short quantity, boolean inChat) {
        OutPacket wp;
        if (inChat) {
            wp = new OutPacket(OutHeader.FIRST_PERSON_VISUAL_EFFECT);
            wp.write(3);
            wp.write(1);
            wp.writeInt(itemID);
            wp.writeInt(quantity);
        } else {
            wp = new OutPacket(OutHeader.SHOW_STATUS_INFO);
            wp.write(0);
            wp.write(0);
            wp.writeInt(itemID);
            wp.writeInt(quantity);
            wp.writeInt(0);
            wp.writeInt(0);
        }
        return wp;
    }

    public static OutPacket GetWarpToMap(Field to, int spawnPoint, Player p) {
        OutPacket wp = new OutPacket(OutHeader.WARP_TO_MAP);
        wp.writeInt(p.getClient().getChannel() - 1);
        wp.write(0x2);
        wp.write(0);
        wp.writeInt(to.getId());
        wp.write(spawnPoint);
        wp.writeShort(p.getStat().getHp());
        return wp;
    }

    public static OutPacket ShowThirdPersonEffect(int cID, int effect) {
        OutPacket wp = new OutPacket(OutHeader.THIRD_PERSON_VISUAL_EFFECT);
        wp.writeInt(cID);
        wp.write(effect);
        return wp;
    }

    public static OutPacket UpdateSkill(int skillID, int level, int masterLevel) {
        OutPacket wp = new OutPacket(OutHeader.UPDATE_SKILLS);
        wp.write(1);
        wp.writeShort(1);
        wp.writeInt(skillID);
        wp.writeInt(level);
        wp.write(1);
        return wp;
    }

    public static OutPacket GetShowExpGain(int base, int party,/* int thirdBonus, int hours,*/ boolean quest, boolean killer) {
        OutPacket pw = new OutPacket(OutHeader.SHOW_STATUS_INFO);
        pw.write(3); // EXP stat
        pw.writeBool(killer); // white text = killer
        pw.writeInt(base); // base experience
        pw.writeBool(quest); // "onQuest"
        pw.writeInt(0); // base experience
        pw.writeInt(0); // base experience
        pw.writeInt(0); // base experience
        return pw;
    }

    public static OutPacket GetShowMesoGain(int gain) {
        return GetShowMesoGain(gain, false);
    }

    public static OutPacket GetShowMesoGain(int gain, boolean inChat) {
        OutPacket wp = new OutPacket(OutHeader.SHOW_STATUS_INFO);
        if (!inChat) {
            wp.write(PacketHeaders.STATUS_INFO_INVENTORY);
            wp.write(1);
        } else {
            wp.write(5);
        }
        wp.writeInt(gain);
        wp.writeShort(0);
        return wp;
    }

    public static OutPacket GetShowFameGain(int gain) {
        OutPacket wp = new OutPacket(OutHeader.SHOW_STATUS_INFO);
        wp.write(4);
        wp.writeInt(gain);
        return wp;
    }

    public static OutPacket BuffMapPirateEffect(Player p, List<Pair<BuffStat, Integer>> stats, int skillId, int duration) {
        OutPacket wp = new OutPacket();

        wp.write(OutHeader.THIRD_PERSON_APPLY_STATUS_EFFECT.getValue());
        wp.writeInt(p.getId());
        int updateMask = GetLongMask(stats);
        wp.writeInt(updateMask);
        wp.writeShort((short) 0);
        for (Pair<BuffStat, Integer> statupdate : stats) {
            wp.writeShort(statupdate.getRight().shortValue());
            wp.writeShort(0);
            wp.writeInt(skillId);
            wp.writeInt(0);
            wp.write(0);
            wp.writeShort((short) duration);
        }
        wp.writeShort((short) 0);
        return wp;
    }

    public static OutPacket UsePirateSkill(List<Pair<BuffStat, Integer>> stats, int skillId, int duration, short delay) {
        OutPacket wp = new OutPacket(OutHeader.FIRST_PERSON_APPLY_STATUS_EFFECT);
        int updateMask = GetLongMask(stats);
        wp.writeInt(updateMask);
        wp.writeShort((short) 0);
        for (Pair<BuffStat, Integer> statupdate : stats) {
            wp.writeShort(statupdate.getRight().shortValue());
            wp.writeShort((short) 0);
            wp.writeInt(skillId);
            wp.writeInt(0);
            wp.write(0);
            wp.writeShort(duration);
        }
        wp.writeShort(delay);
        wp.write(0);
        return wp;
    }

    public static OutPacket GetNPCTalk(int npc, byte msgType, String talk, String endBytes) {
        OutPacket wp = new OutPacket(OutHeader.NPC_TALK);
        wp.write(4);
        wp.writeInt(npc);
        wp.write(msgType);
        wp.writeMapleAsciiString(talk);
        wp.writeBytes(HexTool.getByteArrayFromHexString(endBytes));
        return wp;
    }

    public static OutPacket GetNPCTalkStyle(int npc, String talk, int styles[]) {
        OutPacket wp = new OutPacket(OutHeader.NPC_TALK);
        wp.write(4);
        wp.writeInt(npc);
        wp.write(5);
        wp.writeMapleAsciiString(talk);
        wp.write(styles.length);
        for (int i = 0; i < styles.length; i++) {
            wp.writeInt(styles[i]);
        }
        return wp;
    }

    public static OutPacket GetNPCTalkNum(int npc, String talk, int def, int min, int max) {
        OutPacket wp = new OutPacket(OutHeader.NPC_TALK);
        wp.write(4);
        wp.writeInt(npc);
        wp.write(3);
        wp.writeMapleAsciiString(talk);
        wp.writeInt(def);
        wp.writeInt(min);
        wp.writeInt(max);
        wp.writeInt(0);
        return wp;
    }

    public static OutPacket GetNPCTalkText(int npc, String talk) {
        OutPacket wp = new OutPacket(OutHeader.NPC_TALK);
        wp.write(4);
        wp.writeInt(npc);
        wp.write(2);
        wp.writeMapleAsciiString(talk);
        wp.writeInt(0);
        wp.writeInt(0);
        return wp;
    }

    public static OutPacket SkillCooldown(int sID, int time) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.COOLDOWN.getValue());
        wp.writeInt(sID);
        wp.writeShort(time);
        return wp;
    }

    /**
     * <键盘快捷键>
     */
    public static OutPacket GetKeyMap(Map<Integer, PlayerKeyBinding> keyBindings) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.KEYMAP.getValue());
        wp.write(0);
        for (int x = 0; x < 90; x++) {
            PlayerKeyBinding binding = keyBindings.get(Integer.valueOf(x));
            if (binding != null) {
                wp.write(binding.getType());
                wp.writeInt(binding.getAction());
            } else {
                wp.write(0);
                wp.writeInt(0);
            }
        }
        return wp;
    }

    public static OutPacket GetMacros(PlayerSkillMacro[] macros) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.SKILL_MACRO.getValue());
        wp.write(macros.length);
        for (byte i = 0; i < macros.length; i++) {
            PlayerSkillMacro macro = macros[i];
            wp.writeMapleAsciiString(macro.getName());
            wp.writeBool(macro.isSilent());
            wp.writeInt(macro.getFirstSkill());
            wp.writeInt(macro.getSecondSkill());
            wp.writeInt(macro.getThirdSkill());
        }
        return wp;
    }

    public static OutPacket GetClock() {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.CLOCK.getValue());
        wp.write(1);
        Calendar now = Calendar.getInstance();
        wp.write(now.get(Calendar.HOUR_OF_DAY));
        wp.write(now.get(Calendar.MINUTE));
        wp.write(now.get(Calendar.SECOND));
        return wp;
    }

    public static OutPacket GetClockTimer(int seconds) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.CLOCK.getValue());
        wp.write(2);
        wp.writeInt(seconds);
        return wp;
    }

    public static OutPacket DestroyClock() {
        OutPacket wp = new OutPacket();
        //wp.write(OutHeader.DESTROY_CLOCK.getValue());
        return wp;
    }

    public static OutPacket UpdateCharLook(Player p, int flag) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.UPDATE_CHAR_LOOK.getValue());
        wp.writeInt(p.getId());
        wp.writeInt(flag);
        if (flag == 1) {
            wp.write(p.getSkinColor().getId());
        }
        if (flag == 2) {
            wp.writeInt(p.getFace());
        }
        wp.writeBool(flag == 4);
        if (flag == 4) {
            wp.write(0);
            wp.writeInt(p.getHair());

            Inventory equip = p.getInventory(InventoryType.EQUIPPED);
            Map<Short, Integer> myEquip = new LinkedHashMap<>();
            Map<Short, Integer> maskedEquip = new LinkedHashMap<>();

            equip.list().stream().forEach((item) -> {
                short pos = (short) Math.abs(item.getPosition());
                if (pos > 100) {
                    pos -= 100;
                    if (pos != 11) {
                        myEquip.put(pos, item.getItemId());
                    }
                } else {
                    if (myEquip.get(pos) != null) {
                        maskedEquip.put(pos, item.getItemId());
                    } else {
                        myEquip.put(pos, item.getItemId());
                    }
                }
                /*short pos = (short) (item.getPosition() * -1);
                 if (pos < 100 && myEquip.get(pos) == null) {
                 myEquip.put(pos, item.getItemId());
                 } else if (pos > 100 && pos != 111) {
                 pos -= 100;
                 if (myEquip.get(pos) != null) {
                 maskedEquip.put(pos, myEquip.get(pos));
                 }
                 myEquip.put(pos, item.getItemId());
                 } else if (myEquip.get(pos) != null) {
                 maskedEquip.put(pos, item.getItemId());
                 }*/
            });

            myEquip.entrySet().stream().map((entry) -> {
                wp.write(entry.getKey());
                return entry;
            }).forEach((entry) -> {
                wp.writeInt(entry.getValue());
            });

            maskedEquip.entrySet().stream().map((entry) -> {
                wp.write(entry.getKey());
                return entry;
            }).forEach((entry) -> {
                wp.writeInt(entry.getValue());
            });

            wp.write(-1);
            Item cWeapon = equip.getItem((short) -111);
            wp.writeInt(cWeapon != null ? cWeapon.getItemId() : 0);
            wp.writeInt(p.getSummonedPets().size() > 0 ? p.getSummonedPets().get(0).getPetItemId() : 0);
        }

        wp.writeBool(flag == (8 | 0x10));
        if (flag == (8 | 0x10)) {
            wp.writeInt(0);//ActiveItem
            wp.writeInt(0);//ChocoCount
        }
        wp.writeBool(flag == 0x10);
        if (flag == 0x10) {
            wp.write(0);//speed
        }
        wp.writeBool(flag == 0x20);
        if (flag == 0x20) {
            AddRingLooks(wp, p);
        }
        return wp;
    }

    /**
     * Adds the aesthetic aspects of a character to an existing
     * MaplePacketLittleEndianWriter.
     *
     * @param wp The MaplePacketLittleEndianWrite instance to write the stats
     * to.
     * @param p The character to add the looks of.
     * @param mega Unknown
     */
    public static void AddCharLook(OutPacket wp, Player p, boolean mega) {
        AddCharLook(wp, p, mega, false);
    }

    public static void AddCharLook(OutPacket wp, Player p, boolean mega, boolean login) {
        if (!login) {
            wp.write(p.getGender());
            wp.write(p.getSkinColor().getId());
            wp.writeInt(p.getFace());
            wp.writeBool(mega);
            wp.writeInt(p.getHair());
        }
        Inventory equip = p.getInventory(InventoryType.EQUIPPED);
        Map<Short, Integer> myEquip = new LinkedHashMap<>();
        Map<Short, Integer> maskedEquip = new LinkedHashMap<>();

        equip.list().stream().forEach((item) -> {
            short pos = (short) Math.abs(item.getPosition());
            if (pos != 111) {
                if (pos > 100) {
                    pos -= 100;
                    myEquip.put(pos, item.getItemId());
                } else {
                    if (myEquip.get(pos) != null) {
                        maskedEquip.put(pos, item.getItemId());
                    } else {
                        myEquip.put(pos, item.getItemId());
                    }
                }
            }
        });

        myEquip.entrySet().stream().map((entry) -> {
            wp.write(entry.getKey());
            return entry;
        }).forEach((entry) -> {
            wp.writeInt(entry.getValue());
        });
        wp.write(0);

        wp.write(11);
        Item cWeapon = equip.getItem((short) -111);
        wp.writeInt(cWeapon != null ? cWeapon.getItemId() : 0);
        wp.write(0);

        if (!login) {
            if (p.getPet(0) != null) {
                wp.writeInt(p.getPet(0).getPetItemId());
            } else {
                wp.writeInt(0);
            }
        }
    }

    /**
     * Gets character info for a character.
     *
     * @param p The character to get info about.
     * @return The character info packet.
     */
    public static OutPacket GetCharInfo(Player p) {
        OutPacket wp = new OutPacket(OutHeader.WARP_TO_MAP);
        wp.writeInt(p.getClient().getChannel() - 1);
        wp.write(0);
        wp.write(1);

        wp.writeInt(Randomizer.nextInt());
        wp.writeInt(Randomizer.nextInt());
        wp.writeInt(Randomizer.nextInt());
        wp.writeInt(0);
        AddCharacterData(wp, p);

        wp.writeLong(GetTime(System.currentTimeMillis()));
        return wp;
    }

    /**
     * <个人情报>
     */
    public static OutPacket PersonalInfo(Player p, boolean self) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.CHAR_INFO.getValue());
        wp.writeInt(p.getId());
        wp.write(p.getLevel());
        wp.writeShort(p.getJob().getId());
        wp.writeShort(p.getFame());
        if (p.判断家族() > 0) {
            wp.writeMapleAsciiString(家族名字(p.判断家族()));
        } else {
            wp.writeMapleAsciiString("未加入家族社区");
        }
        ItemPet pet = p.getPet(0);
        if (pet != null) {
            wp.writeBool(true);
            wp.writeInt(pet.getPetItemId());
            wp.writeMapleAsciiString(pet.getName());
            wp.write(pet.getLevel());
            wp.writeShort(pet.getCloseness());
            wp.write(pet.getFullness());
            final Item inv = p.getInventory(InventoryType.EQUIPPED).getItem((byte) -114);
            wp.writeInt(inv == null ? 0 : inv.getItemId());
        } else {
            wp.write(0);
        }
        wp.writeBool(false);
        wp.writeBool(false);
        wp.write(p.getCashShop().getWishList().size());
        for (int sn : p.getCashShop().getWishList()) {
            wp.writeInt(sn);
        }
        return wp;
    }

    public static String 家族名字(int id) {
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
            System.err.println("个人情报家族名字、出错");
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

    public static void addCharStats(OutPacket wp, Player p) {
        wp.writeInt(p.getId());
        wp.writeAsciiString(p.getName(), 19);
        wp.write(p.getGender());
        wp.write(p.getSkinColor().getId());
        wp.writeInt(p.getFace());
        wp.writeInt(p.getHair());
        if (p.getPet(0) != null) {
            wp.writeLong(p.getPet(0).getUniqueId());
        } else {
            wp.writeLong(0);
        }
        wp.write(p.getLevel());
        wp.writeShort(p.getJob().getId());
        wp.writeShort(p.getStat().getStr());
        wp.writeShort(p.getStat().getDex());
        wp.writeShort(p.getStat().getInt());
        wp.writeShort(p.getStat().getLuk());
        wp.writeShort(p.getStat().getHp());
        wp.writeShort(p.getStat().getMaxHp());
        wp.writeShort(p.getStat().getMp());
        wp.writeShort(p.getStat().getMaxMp());
        wp.writeShort(p.getStat().getRemainingAp());
        wp.writeShort(p.getStat().getRemainingSp());
        wp.writeInt(p.getCurrentExp());
        wp.writeShort(p.getFame());
        wp.writeInt(p.getMapId());
        wp.write(p.getInitialSpawnpoint());
        wp.writeLong(GetTime(System.currentTimeMillis()));
        wp.writeInt(0);
        wp.writeInt(0);
    }

    public static void AddInventoryInfo(OutPacket wp, Player p) {
        wp.writeInt(p.getMeso());
        for (byte i = 1; i <= 5; i++) {
            wp.write(p.getInventory(InventoryType.getByType(i)).getSlotLimit());
        }
        Inventory iv = p.getInventory(InventoryType.EQUIPPED);
        Collection<Item> equippedList = iv.list();
        Item[] equipped = new Item[17];
        Item[] equippedCash = new Item[17];
        for (Item item : equippedList) {
            short pos = item.getPosition();
            if (pos < 0) {
                pos = (byte) Math.abs(pos);
                if (pos > 100) {
                    equippedCash[(byte) (pos - 100)] = (Item) item;
                } else {
                    equipped[(byte) pos] = (Item) item;
                }
            }
            if (pos < 0) {
                if (pos < -100) {
                    pos += 100;
                    pos = (byte) Math.abs(pos);
                    equippedCash[(byte) (pos - 100)] = (Item) item;
                } else {
                    pos = (byte) Math.abs(pos);
                    equipped[(byte) pos] = (Item) item;
                }
            }
        }
        for (Item item : equipped) {
            if (item != null) {
                AddItemInfo(wp, item);
            }
        }
        wp.write(0);
        for (Item item : equippedCash) {
            if (item != null) {
                AddItemInfo(wp, item);
            }
        }
        wp.write(0);
        for (byte i = 1; i < 6; i++) {
            iv = p.getInventory(InventoryType.getByType((byte) i));
            for (Item item : iv.list()) {
                if (item != null && item.getPosition() > 0) {
                    AddItemInfo(wp, item);
                }
            }
            wp.write(0);
        }
    }

    protected static void AddItemInfo(OutPacket wp, Item item) {
        AddItemInfo(wp, item, false, false);
    }

    public static void AddItemInfo(OutPacket wp, Item item, boolean zeroPosition, boolean leaveOut) {
        boolean hasUniqueId = item.getUniqueId() > 0;
        short pos = item.getPosition();
        if (zeroPosition) {
            if (!leaveOut) {
                wp.write(0);
            }
        } else {
            if (pos <= -1) {
                pos *= -1;
                if (pos > 100 && pos < 1000) {
                    pos -= 100;
                }
            }
            wp.write(pos);
        }
        wp.writeInt(item.getItemId());
        wp.writeBool(hasUniqueId);
        if (hasUniqueId) {
            wp.writeLong(item.getUniqueId());
        }
        if (item.getPet() != null) {
            AddPetItemInfo(wp, item, item.getPet());
        } else {
            AddExpirationTime(wp, item.getExpiration());
            if (item.getType() == ItemType.EQUIP) {
                Equip equip = (Equip) item;
                wp.write(equip.getUpgradeSlots());
                wp.write(equip.getLevel());
                wp.writeShort(equip.getStr());
                wp.writeShort(equip.getDex());
                wp.writeShort(equip.getInt());
                wp.writeShort(equip.getLuk());
                wp.writeShort(equip.getHp());
                wp.writeShort(equip.getMp());
                wp.writeShort(equip.getWatk());
                wp.writeShort(equip.getMatk());
                wp.writeShort(equip.getWdef());
                wp.writeShort(equip.getMdef());
                wp.writeShort(equip.getAcc());
                wp.writeShort(equip.getAvoid());
                wp.writeShort(equip.getHands());
                wp.writeShort(equip.getSpeed());
                wp.writeShort(equip.getJump());
                wp.writeMapleAsciiString(equip.getOwner());
            } else {
                wp.writeShort(item.getQuantity());
                wp.writeMapleAsciiString(item.getOwner());
            }
        }
    }

    public static final void AddPetItemInfo(final OutPacket wp, final Item item, final ItemPet pet) {
        wp.writeBytes(HexTool.getByteArrayFromHexString("00 80 05 BB 46 E6 17 02"));
        wp.writeAsciiString(pet.getName(), 19);
        wp.write(pet.getLevel());
        wp.writeShort(pet.getCloseness());
        wp.write(pet.getFullness());
        AddExpirationTime(wp, item.getExpiration() <= System.currentTimeMillis() ? -1 : item.getExpiration());
    }

    public static OutPacket ShowOwnBerserk(int skillLevel, boolean Berserk) {
        OutPacket wp = new OutPacket(OutHeader.FIRST_PERSON_VISUAL_EFFECT);
        wp.write(1);
        wp.writeInt(1320006);
        wp.write(skillLevel);
        wp.write(Berserk ? 1 : 0);
        return wp;
    }

    public static OutPacket ShowBerserk(int cID, int skillLevel, boolean Berserk) {
        OutPacket wp = new OutPacket(OutHeader.THIRD_PERSON_VISUAL_EFFECT);
        wp.writeInt(cID);
        wp.write(1);
        wp.writeInt(1320006);
        wp.write(skillLevel);
        wp.write(Berserk ? 1 : 0);
        return wp;
    }

    public static OutPacket UpdateBuddyCapacity(int capacity) {
        OutPacket wp = new OutPacket(OutHeader.BUDDYLIST);
        wp.write(21);
        wp.write(capacity);
        return wp;
    }

    public static OutPacket ShowNotes(ResultSet notes, int count) throws SQLException {
        OutPacket wp = new OutPacket(OutHeader.SHOW_NOTES);
        wp.write(2);
        wp.write(count);
        for (int i = 0; i < count; i++) {
            wp.writeInt(notes.getInt("id"));
            wp.writeMapleAsciiString(notes.getString("from"));
            wp.writeMapleAsciiString(notes.getString("message"));
            wp.writeLong(GetKoreanTimestamp(notes.getLong("timestamp")));
            wp.write(notes.getInt("fame"));
            notes.next();
        }
        return wp;
    }

    public static OutPacket SummonAttack(int cID, int skillid, int newStance, List<SummonHandler.SummonAttackEntry> allDamage, int level) {
        OutPacket wp = new OutPacket(OutHeader.SUMMON_ATTACK);

        wp.writeInt(cID);
        wp.writeInt(skillid);

        wp.write(newStance);
        wp.write(allDamage.size());

        for (SummonHandler.SummonAttackEntry sae : allDamage) {
            wp.writeInt(sae.getMob());
            wp.write(6);//hitAction
            wp.writeInt(sae.getDamage());
            wp.writeInt(sae.getDamage());
        }
        return wp;
    }

    public static OutPacket SummonSkill(int cID, int summonSkillID, int newStance) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.SUMMON_SKILL.getValue());
        wp.writeInt(cID);
        wp.writeInt(summonSkillID);
        wp.write(newStance);
        return wp;
    }

    public static OutPacket UpdateMount(Player owner, boolean levelup) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.UPDATE_MOUNT.getValue());
        wp.writeInt(owner.getId());
        wp.writeInt(owner.getMount().getLevel());
        wp.writeInt(owner.getMount().getExp());
        wp.writeInt(owner.getMount().getTiredness());
        wp.write(levelup ? (byte) 1 : (byte) 0);
        return wp;
    }

    public static OutPacket GetWhisper(String sender, int channel, String text) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.WHISPER.getValue());
        wp.write(18);
        wp.writeMapleAsciiString(sender);
        wp.write(channel - 1);
        wp.writeMapleAsciiString(text);
        return wp;
    }

    public static OutPacket PrivateChatMessage(String name, String chatText, int mode) {
        OutPacket wp = new OutPacket(OutHeader.PRIVATE_CHAT);
        wp.write(mode);
        wp.writeMapleAsciiString(name);
        wp.writeMapleAsciiString(chatText);
        return wp;
    }

    public static OutPacket ToSpouse(String sender, String text, int type) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.SPOUSE_CHAT.getValue());
        wp.write(type);
        if (type == 4) {
            wp.write(1);
        } else {
            wp.writeMapleAsciiString(sender);
            wp.write(5);
        }
        wp.writeMapleAsciiString(text);
        return wp;
    }

    public static OutPacket SendSpouseChat(Player wife, String msg) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.SPOUSE_CHAT.getValue());
        wp.writeMapleAsciiString(wife.getName());
        wp.writeMapleAsciiString(msg);
        return wp;
    }

    public static OutPacket UpdateBuddyChannel(int characterID, int channel) {
        OutPacket wp = new OutPacket(OutHeader.BUDDYLIST);
        wp.write(20);
        wp.writeInt(characterID);
        wp.write(0);
        wp.writeInt(channel);
        return wp;
    }

    public static OutPacket MessengerInvite(String from, int messengerID) {
        OutPacket wp = new OutPacket(OutHeader.MESSENGER);
        wp.write(0x03);
        wp.writeMapleAsciiString(from);
        wp.write(0x00);
        wp.writeInt(messengerID);
        wp.write(0x00);
        return wp;
    }

    public static OutPacket AddMessengerPlayer(String from, Player p, int position, int channel) {
        OutPacket wp = new OutPacket(OutHeader.MESSENGER);
        wp.write(0x00);
        wp.write(position);
        InteractionPackets.AddCharLook(wp, p);
        wp.writeMapleAsciiString(from);
        wp.write(channel);
        wp.write(0x00);
        return wp;
    }

    public static OutPacket RemoveMessengerPlayer(int position) {
        OutPacket wp = new OutPacket(OutHeader.MESSENGER);
        wp.write(0x02);
        wp.write(position);
        return wp;
    }

    public static OutPacket UpdateMessengerPlayer(String from, Player p, int position, int channel) {
        OutPacket wp = new OutPacket(OutHeader.MESSENGER);
        wp.write(0x07);
        wp.write(position);
        InteractionPackets.AddCharLook(wp, p);
        wp.writeMapleAsciiString(from);
        wp.write(channel);
        wp.write(0x00);
        return wp;
    }

    public static OutPacket JoinMessenger(int position) {
        OutPacket wp = new OutPacket(OutHeader.MESSENGER);
        wp.write(0x01);
        wp.write(position);
        return wp;
    }

    public static OutPacket MessengerChat(String text) {
        OutPacket wp = new OutPacket(OutHeader.MESSENGER);
        wp.write(0x06);
        wp.writeMapleAsciiString(text);
        return wp;
    }

    public static OutPacket MessengerNote(String text, int mode, int mode2) {
        OutPacket wp = new OutPacket(OutHeader.MESSENGER);
        wp.write(mode);
        wp.writeMapleAsciiString(text);
        wp.write(mode2);
        return wp;
    }

    public static OutPacket UpdateBuddylist(byte action, Collection<MapleBuddyListEntry> buddylist) {
        OutPacket mplew = new OutPacket(OutHeader.BUDDYLIST);
        mplew.write(action);
        mplew.write(buddylist.size());

        for (MapleBuddyListEntry buddy : buddylist) {
            mplew.writeInt(buddy.getCharacterId());
            mplew.writeAsciiString(buddy.getName(), 19);
            mplew.write(0);
            mplew.writeInt(buddy.getChannel() - 1);
        }
        for (int x = 0; x < buddylist.size(); x++) {
            mplew.writeInt(0);
        }
        return mplew;
    }

    public static OutPacket BuddylistMessage(byte message) {
        OutPacket wp = new OutPacket(OutHeader.BUDDYLIST);
        wp.write(message);
        return wp;
    }

    public static OutPacket RequestBuddylistAdd(int cIDFrom, String nameFrom, int channel) {
        OutPacket wp = new OutPacket(OutHeader.BUDDYLIST);
        wp.write(9);
        wp.writeInt(cIDFrom);
        wp.writeMapleAsciiString(nameFrom);
        wp.writeInt(channel);//channel
        wp.writeAsciiString(nameFrom, 19);
        wp.write(channel);
        wp.writeInt(channel);
        wp.write(0);
        return wp;
    }

    public static OutPacket GetChatText(int cid, String text, boolean whiteBG) {
        OutPacket wp = new OutPacket(OutHeader.MAP_CHAT);
        wp.writeInt(cid);
        wp.writeBool(whiteBG);
        wp.writeMapleAsciiString(text);
        return wp;
    }

    public static OutPacket GetWhisperReply(String target, byte reply) {
        OutPacket wp = new OutPacket(OutHeader.WHISPER);
        wp.write(0x0A);
        wp.writeMapleAsciiString(target);
        wp.write(reply);
        return wp;
    }

    public static OutPacket GetFindReplyWithMap(String target, int mapID) {
        OutPacket wp = new OutPacket(OutHeader.WHISPER);
        wp.write(9);
        wp.writeMapleAsciiString(target);
        wp.write(ChatHeaders.FIND_RESPONSE_MAP);
        wp.writeInt(mapID);
        wp.writeLong(0);
        return wp;
    }

    public static OutPacket GetFindReply(String target, int channel) {
        OutPacket wp = new OutPacket(OutHeader.WHISPER);
        wp.write(9);
        wp.writeMapleAsciiString(target);
        wp.write(ChatHeaders.FIND_RESPONSE_CHANNEL);
        wp.writeInt(channel - 1);
        return wp;
    }

    public static OutPacket GetFindReplyWithCS(String target) {
        OutPacket wp = new OutPacket(OutHeader.WHISPER);
        wp.write(9);
        wp.writeMapleAsciiString(target);
        wp.write(ChatHeaders.FIND_RESPONSE_CASH_SHOP);
        wp.writeInt(-1);
        return wp;
    }

    public static OutPacket OnCoupleMessage(String fiance, String text, boolean spouse) {
        OutPacket wp = new OutPacket(OutHeader.SPOUSE_CHAT);
        wp.write(spouse ? 5 : 4);
        if (spouse) {
            wp.writeMapleAsciiString(fiance);
        }
        wp.write(spouse ? 5 : 1);
        wp.writeMapleAsciiString(text);
        return wp;
    }

    public static OutPacket showEventInstructions() {
        final OutPacket wp = new OutPacket();
        wp.write(OutHeader.GMEVENT_INSTRUCTIONS.getValue());
        wp.write(0);
        return wp;
    }

    public static OutPacket RemoveItemFromMap(int objectID, int animation, int cID) {
        return RemoveItemFromMap(objectID, animation, cID, false, 0);
    }

    public static OutPacket RemoveItemFromMap(int objectID, int animation, int cID, boolean pet, int slot) {
        OutPacket warp = new OutPacket(OutHeader.REMOVE_ITEM_FROM_MAP);
        warp.write(animation);
        warp.writeInt(objectID);
        if (animation >= 2) {
            warp.writeInt(cID);
            if (pet) {
                warp.write(slot);
            }
        }
        return warp;
    }

    public static OutPacket GetShowInventoryFull() {
        return GetShowInventoryStatus(0xff);
    }

    public static OutPacket ShowItemUnavailable() {
        return GetShowInventoryStatus(0xfe);
    }

    public static OutPacket GetInventoryFull() {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.MODIFY_INVENTORY_ITEM.getValue());
        wp.write(1);
        wp.write(0);
        return wp;
    }

    public static OutPacket GetShowInventoryStatus(int mode) {
        OutPacket wp = new OutPacket(OutHeader.SHOW_STATUS_INFO);
        wp.write(0);
        wp.write(mode);
        wp.writeInt(0);
        wp.writeInt(0);
        return wp;
    }

    public static OutPacket GetStorage(int npcID, byte slots, Collection<Item> items, int meso) {
        OutPacket wp = new OutPacket(OutHeader.NPC_STORAGE);
        wp.writeInt(npcID);
        wp.write(slots);
        wp.writeShort(126);
        wp.writeInt(meso);
        byte size[] = new byte[]{0, 0, 0, 0, 0, 0};
        for (Item item : items) {
            switch (item.getItemId() / 1000000) {
                case 1:
                    size[1]++;
                    break;
                case 2:
                    size[2]++;
                    break;
                case 3:
                    size[3]++;
                    break;
                case 4:
                    size[4]++;
                    break;
                case 5:
                    size[5]++;
                    break;
            }
        }

        for (int i = 1; i < 6; i++) {
            if (size[i] > 0) {
                wp.write(size[i]);
                for (Item item : items) {
                    if (item.getItemId() / 1000000 == i) {
                        AddItemInfo(wp, item, true, true);
                    }
                }
            } else {
                wp.write(0);
            }
        }

        wp.writeLong(0);
        return wp;
    }

    public static OutPacket GetStorageInsufficientFunds() {
        OutPacket wp = new OutPacket(OutHeader.STORAGE_OPERATION);
        wp.write(13);
        return wp;
    }

    public static OutPacket StorageWithdrawInventoryFull() {
        OutPacket wp = new OutPacket(OutHeader.STORAGE_OPERATION);
        wp.write(8);
        return wp;
    }

    public static OutPacket GetStorageFull() {
        OutPacket wp = new OutPacket(OutHeader.STORAGE_OPERATION);
        wp.write(9);
        return wp;
    }

    public static OutPacket MesoStorage(byte slots, int meso) {
        OutPacket wp = new OutPacket(OutHeader.STORAGE_OPERATION);
        wp.write(15);
        wp.write(slots);
        wp.writeShort(2);
        wp.writeInt(meso);
        wp.writeLong(0);
        return wp;
    }

    public static OutPacket StoreStorage(byte slots, InventoryType type, Collection<Item> items) {
        OutPacket wp = new OutPacket(OutHeader.STORAGE_OPERATION);
        wp.write(15);
        wp.write(slots);
        wp.writeShort(124);
        //wp.writeShort(type.getBitfieldEncoding());
        byte size[] = new byte[]{0, 0, 0, 0, 0, 0};
        for (Item item : items) {
            switch (item.getItemId() / 1000000) {
                case 1:
                    size[1]++;
                    break;
                case 2:
                    size[2]++;
                    break;
                case 3:
                    size[3]++;
                    break;
                case 4:
                    size[4]++;
                    break;
                case 5:
                    size[5]++;
                    break;
            }
        }

        for (int i = 1; i < 6; i++) {
            if (size[i] > 0) {
                wp.write(size[i]);
                for (Item item : items) {
                    if (item.getItemId() / 1000000 == i) {
                        AddItemInfo(wp, item, true, true);
                    }
                }
            } else {
                wp.write(0);
            }
        }
        wp.writeLong(0);
        return wp;
    }

    public static OutPacket TakeOutStorage(byte slots, InventoryType type, Collection<Item> items) {
        OutPacket wp = new OutPacket(OutHeader.STORAGE_OPERATION);
        wp.write(15);
        wp.write(slots);
        wp.writeShort(124);
        //wp.writeShort(type.getBitfieldEncoding());
        byte size[] = new byte[]{0, 0, 0, 0, 0, 0};
        for (Item item : items) {
            switch (item.getItemId() / 1000000) {
                case 1:
                    size[1]++;
                    break;
                case 2:
                    size[2]++;
                    break;
                case 3:
                    size[3]++;
                    break;
                case 4:
                    size[4]++;
                    break;
                case 5:
                    size[5]++;
                    break;
            }
        }

        for (int i = 1; i < 6; i++) {
            if (size[i] > 0) {
                wp.write(size[i]);
                for (Item item : items) {
                    if (item.getItemId() / 1000000 == i) {
                        AddItemInfo(wp, item, true, true);
                    }
                }
            } else {
                wp.write(0);
            }
        }
        return wp;
    }

    public static OutPacket GetChannelChange(InetAddress inetAddr, int port) {
        OutPacket wp = new OutPacket(OutHeader.GAME_HOST_ADDRESS);
        //10 01 31 EA B2 40 99 1D
        wp.writeBool(true);
        wp.writeBytes(inetAddr.getAddress());
        wp.writeShort(port);
        return wp;
    }

    public static OutPacket UpdateGender(Player p) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.GENDER.getValue());
        wp.write(p.getGender());
        return wp;
    }

    public static void AddCharacterData(OutPacket wp, Player p) {
        wp.writeShort(-1);
        addCharStats(wp, p);
        wp.write(p.getBuddylist().getCapacity());

        wp.writeInt(p.getMeso());
        Inventory iv = p.getInventory(InventoryType.EQUIPPED);
        Collection<Item> equippedC = iv.list();
        List<Item> equipped = new ArrayList<>(equippedC.size());
        List<Item> equippedCash = new ArrayList<>(equippedC.size());
        for (Item item : equippedC) {
            if (item.getPosition() <= -100) {
                equippedCash.add(item);
            } else {
                equipped.add(item);
            }
        }
        Collections.sort(equipped);
        for (Item item : equipped) {
            AddItemInfo(wp, item);
        }
        wp.write(0);
        for (Item item : equippedCash) {
            AddItemInfo(wp, item);
        }
        wp.write(0);
        wp.write(p.getInventory(InventoryType.EQUIP).getSlotLimit());
        for (Item item : p.getInventory(InventoryType.EQUIP).list()) {
            AddItemInfo(wp, item);
        }
        wp.write(0);
        wp.write(p.getInventory(InventoryType.USE).getSlotLimit());
        for (Item item : p.getInventory(InventoryType.USE).list()) {
            AddItemInfo(wp, item);
        }
        wp.write(0);
        wp.write(p.getInventory(InventoryType.SETUP).getSlotLimit());
        for (Item item : p.getInventory(InventoryType.SETUP).list()) {
            AddItemInfo(wp, item);
        }
        wp.write(0);
        wp.write(p.getInventory(InventoryType.ETC).getSlotLimit());
        for (Item item : p.getInventory(InventoryType.ETC).list()) {
            AddItemInfo(wp, item);
        }
        wp.write(0);
        wp.write(p.getInventory(InventoryType.CASH).getSlotLimit());
        p.getInventory(InventoryType.CASH).list().stream().forEach((item) -> {
            AddItemInfo(wp, item);
        });
        wp.write(0);

        Map<PlayerSkill, PlayerSkillEntry> skills = p.getSkills();
        wp.writeShort(skills.size());
        skills.entrySet().stream().map((skill) -> {
            wp.writeInt(skill.getKey().getId());
            return skill;
        }).forEach((skill) -> {
            wp.writeInt(skill.getValue().skillevel);
        });

        PlayerQuest pq = p.getQuest();
        if (pq != null) {
            wp.writeShort(pq.getQuests().size());
            pq.getQuests().entrySet().stream().map((kvp) -> {
                wp.writeInt(kvp.getKey());
                return kvp;
            }).forEach((kvp) -> {
                wp.writeMapleAsciiString(kvp.getValue().getData());
            });
        } else {
            wp.writeShort(0);
        }

        AddRingInfo(wp, p);
        AddRocksInfo(wp, p);
    }

    public static void AddRingInfo(OutPacket wp, Player p) {
        wp.writeShort(0);//minigames
        wp.writeShort(p.getCrushRings().size());
        for (ItemRing ring : p.getCrushRings()) {
            wp.writeInt(ring.getPartnerCharacterId());
            wp.writeAsciiString(ring.getPartnerName(), 19);
            wp.writeInt(ring.getRingDatabaseId());
            wp.writeInt(0);
            wp.writeInt(ring.getPartnerRingDatabaseId());
            wp.writeInt(0);
        }
    }

    public static final void AddRocksInfo(final OutPacket wp, final Player p) {
        final int[] mapz = p.getRegRocks();
        for (int i = 0; i < 5; i++) {
            wp.writeInt(mapz[i]);
        }
        /*final int[] map = p.getRocks();
         for (int i = 0; i < 10; i++) {
         wp.writeInt(map[i]);
         }*/
    }

    public static OutPacket ItemMegaphone(String msg, boolean whisper, int channel, Item item) {
        OutPacket wp = new OutPacket(OutHeader.SERVERMESSAGE);
        wp.write(8);
        wp.writeMapleAsciiString(msg);
        wp.write(channel - 1);
        wp.writeBool(whisper);
        if (item == null) {
            wp.writeBool(false);
        } else {
            AddItemInfo(wp, item);
        }
        return wp;
    }

    public static OutPacket UpdateInventorySlot(InventoryType type, Item item) {
        return UpdateInventorySlot(type, item, false);
    }

    public static OutPacket UpdateInventorySlot(InventoryType type, Item item, boolean fromDrop) {
        OutPacket wp = new OutPacket(OutHeader.MODIFY_INVENTORY_ITEM);
        if (fromDrop) {
            wp.writeBool(true);
        } else {
            wp.writeBool(false);
        }
        wp.write(1);
        wp.write(PacketHeaders.INVENTORY_QUANTITY_UPDATE);
        wp.write(Math.abs(type.getType()));
        wp.writeShort(item.getPosition());
        wp.writeShort(item.getQuantity());
        wp.writeInt(0);
        wp.writeInt(0);
        return wp;
    }

    public static OutPacket MoveInventoryItem(InventoryType type, short src, short dst) {
        return MoveInventoryItem(type, src, dst, (byte) -1);
    }

    public static OutPacket MoveInventoryItem(InventoryType type, short src, short dst, short equipIndicator) {
        OutPacket wp = new OutPacket(OutHeader.MODIFY_INVENTORY_ITEM);
        wp.writeBool(true);
        wp.write(1);
        wp.write(PacketHeaders.INVENTORY_CHANGE_POSITION);
        wp.write(type.getType());
        wp.writeShort(src);
        wp.writeShort(dst);
        if (equipIndicator != -1) {
            wp.write(equipIndicator);
        }
        wp.writeInt(0);
        wp.writeInt(0);
        return wp;
    }

    public static OutPacket MoveAndMergeInventoryItem(InventoryType type, short src, short dst, short total) {
        OutPacket wp = new OutPacket(OutHeader.MODIFY_INVENTORY_ITEM);
        wp.writeBool(true);
        wp.write(2);
        wp.write(PacketHeaders.INVENTORY_CLEAR_SLOT);
        wp.write(type.getType());
        wp.writeShort(src);
        wp.write(1);
        wp.write(type.getType());
        wp.writeShort(dst);
        wp.writeShort(total);
        wp.writeInt(0);
        wp.writeInt(0);
        return wp;
    }

    public static OutPacket MoveAndMergeWithRestInventoryItem(InventoryType type, short src, short dst, short srcQ, short dstQ) {
        OutPacket wp = new OutPacket(OutHeader.MODIFY_INVENTORY_ITEM);
        wp.writeBool(true);
        wp.write(2);
        wp.write(PacketHeaders.INVENTORY_QUANTITY_UPDATE);
        wp.write(type.getType());
        wp.writeShort(src);
        wp.writeShort(srcQ);
        wp.write(PacketHeaders.INVENTORY_QUANTITY_UPDATE);
        wp.write(type.getType());
        wp.writeShort(dst);
        wp.writeShort(dstQ);
        wp.writeInt(0);
        wp.writeInt(0);
        return wp;
    }

    public static OutPacket ClearInventoryItem(InventoryType type, short slot, boolean fromDrop) {
        OutPacket wp = new OutPacket(OutHeader.MODIFY_INVENTORY_ITEM);
        wp.write(fromDrop ? 1 : 0);
        wp.write(1);
        wp.write(PacketHeaders.INVENTORY_CLEAR_SLOT);
        wp.write(Math.abs(type.getType()));
        wp.writeShort(slot);
        if (slot < 0) {
            wp.writeBool(true);
        }
        wp.writeInt(0);
        wp.writeInt(0);
        return wp;
    }

    public static OutPacket ScrolledItem(Item scroll, Item item, boolean destroyed) {
        OutPacket wp = new OutPacket(OutHeader.MODIFY_INVENTORY_ITEM);
        wp.writeBool(true);
        wp.write(destroyed ? 2 : 3);
        wp.write(scroll.getQuantity() > 0 ? 1 : 3);
        wp.write(InventoryType.USE.getType());
        wp.writeShort(scroll.getPosition());
        if (scroll.getQuantity() > 0) {
            wp.writeShort(scroll.getQuantity());
        }
        wp.write(3);
        if (!destroyed) {
            wp.write(InventoryType.EQUIP.getType());
            wp.writeShort(item.getPosition());
            wp.write(0);
        }
        wp.write(InventoryType.EQUIP.getType());
        wp.writeShort(item.getPosition());
        if (!destroyed) {
            AddItemInfo(wp, item, true, true);
        }
        wp.write(1);
        wp.writeInt(0);
        wp.writeInt(0);
        return wp;
    }

    public static OutPacket GetScrollEffect(int charID, EquipScrollResult scrollSuccess, boolean legendarySpirit) {
        OutPacket wp = new OutPacket(OutHeader.SHOW_STATUS_INFO);
        wp.write(4);
        switch (scrollSuccess) {
            case SUCCESS:
                wp.write(1);
                break;
            case FAIL:
                wp.write(0);
                break;
            case CURSE:
                wp.write(0);
                break;
            default:
                throw new IllegalArgumentException("effect in illegal range");
        }
        return wp;
    }

    public static OutPacket useSkillBook(Player p, int skillid, int maxlevel, boolean canuse, boolean success) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.USE_SKILL_BOOK.getValue());
        wp.writeInt(p.getId());
        wp.writeBool(true);
        wp.writeInt(skillid);
        wp.writeInt(maxlevel);
        wp.writeBool(canuse);
        wp.writeBool(success);
        return wp;
    }

    public static OutPacket CatchMonster(int mobID, int itemID, byte success) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.CATCH_MONSTER.getValue());
        wp.writeInt(mobID);
        wp.writeInt(itemID);
        wp.write(success);
        return wp;
    }

    public static OutPacket SilverBoxOpened(int itemID) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.SILVER_BOX_OPEN.getValue());
        wp.writeInt(itemID);
        return wp;
    }

    /* 00 = /
     * 01 = You don't have enough in stock
     * 02 = You do not have enough mesos
     * 03 = Please check if your inventory is full or not
     * 05 = You don't have enough in stock
     * 06 = Due to an error, the trade did not happen
     * 07 = Due to an error, the trade did not happen
     * 08 = /
     * 0D = You need more items
     * 0E = CRASH; LENGTH NEEDS TO BE LONGER :O
     */
    public static OutPacket ConfirmShopTransaction(byte code) {
        OutPacket wp = new OutPacket(OutHeader.CONFIRM_SHOP_TRANSACTION);
        wp.write(code);
        return wp;
    }

    public static OutPacket ReportReply(byte type) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.REPORTREPLY.getValue());
        wp.write(type);
        return wp;
    }

    private static void AddAttackBody(OutPacket wp, int cid, int skill, int skillLevel, int mask, int attackType, int numAttackedAndDamage, int projecTile, Map<Integer, List<Integer>> damage, int hitAction, byte range) {
        //58 [0A 00 00 00] [12] [14] [AD CA 2D 00] [16] 06 41 00 00 00 00 6D 00 00 00 06 55 04 00 00 24 03 00 00
        wp.writeInt(cid);
        wp.write(numAttackedAndDamage);
        wp.write(skillLevel);
        if (skillLevel > 0) {
            wp.writeInt(skill);
        }
        wp.write(mask);
        wp.write(attackType);
        wp.write(range);
        wp.writeInt(projecTile);
        damage.keySet().stream().forEach((oned) -> {
            List<Integer> onedList = damage.get(oned);
            if (onedList != null) {
                wp.writeInt(oned & 0x7FFFFFFF);
                wp.write(hitAction);
                if (skill == ChiefBandit.MesoExplosion) {
                    wp.write(onedList.size());
                }
                onedList.stream().forEach((eachd) -> {
                    wp.writeInt(eachd);
                });
            }
        });
    }

    public static OutPacket EnergyChargeAttack(int cID, int skill, int skillLevel, int mask, int attackType, int numAttackedAndDamage, Map<Integer, List<Integer>> damage, int hitAction) {
        OutPacket wp = new OutPacket(OutHeader.ENERGY_CHARGE_ATTACK);
        AddAttackBody(wp, cID, skill, skillLevel, mask, attackType, numAttackedAndDamage, 0, damage, hitAction, (byte) 0);
        return wp;
    }

    public static OutPacket CloseRangeAttack(int cID, int skill, int skillLevel, int mask, int attackType, int numAttackedAndDamage, Map<Integer, List<Integer>> damage, int hitAction) {
        OutPacket wp = new OutPacket(OutHeader.CLOSE_RANGE_ATTACK);
        AddAttackBody(wp, cID, skill, skillLevel, mask, attackType, numAttackedAndDamage, 0, damage, hitAction, (byte) 0);
        return wp;
    }

    public static OutPacket RangedAttack(int cID, int skill, int skillLevel, int mask, int attackType, int numAttackedAndDamage, int projectile, Map<Integer, List<Integer>> damage, int hitAction, byte range) {
        OutPacket wp = new OutPacket(OutHeader.RANGED_ATTACK);
        AddAttackBody(wp, cID, skill, skillLevel, mask, attackType, numAttackedAndDamage, projectile, damage, hitAction, range);
        return wp;
    }

    public static OutPacket MagicAttack(int cID, int skill, int skillLevel, int mask, int attackType, int numAttackedAndDamage, Map<Integer, List<Integer>> damage, int charge, int hitAction) {
        OutPacket wp = new OutPacket(OutHeader.MAGIC_ATTACK);
        AddAttackBody(wp, cID, skill, skillLevel, mask, attackType, numAttackedAndDamage, 0, damage, hitAction, (byte) 0);
        if (charge != -1) {
            wp.writeInt(charge);
        }
        return wp;
    }

    public static OutPacket DamagePlayer(int skill, int monsterIDFrom, int cID, int damage) {
        OutPacket wp = new OutPacket(OutHeader.DAMAGE_PLAYER);
        wp.writeInt(cID);
        wp.write(skill);
        wp.writeInt(0);
        wp.writeInt(monsterIDFrom);
        wp.write(1);
        wp.write(0);
        wp.write(0);
        wp.writeInt(damage);
        return wp;
    }

    public static OutPacket DamagePlayer(int mobAttack, int monsterIDFrom, int cID, int damage, int fake, int direction, boolean pgmr, int pgmr_1, boolean is_pg, int oid, int pos_x, int pos_y) {
        OutPacket wp = new OutPacket(OutHeader.DAMAGE_PLAYER);
        wp.writeInt(cID);
        wp.write(mobAttack);
        wp.writeInt(damage);

        wp.writeInt(oid);
        wp.writeInt(monsterIDFrom);
        wp.write(direction);
        wp.write(pgmr_1);
        if (pgmr) {
            wp.writeBool(is_pg);
            wp.writeShort(pos_x);
            wp.writeShort(pos_y);
        }
        wp.writeInt(damage);
        return wp;
    }

    public static OutPacket MovePlayer(int cID, Point start, List<LifeMovementFragment> moves) {
        OutPacket wp = new OutPacket(OutHeader.MOVE_PLAYER);
        wp.writeInt(cID);
        wp.writePos(start);
        HelpPackets.SerializeMovementList(wp, moves);
        return wp;
    }

    public static OutPacket MoveSummon(int cID, int objectID, Point startPos, List<LifeMovementFragment> moves) {
        OutPacket wp = new OutPacket(OutHeader.MOVE_SUMMON);
        wp.writeInt(cID);
        wp.writeInt(objectID);
        wp.writePos(startPos);
        HelpPackets.SerializeMovementList(wp, moves);
        return wp;
    }

    public static OutPacket SkillCancel(Player from, int skillID) {
        OutPacket wp = new OutPacket(OutHeader.CANCEL_SKILL_EFFECT);
        wp.writeInt(from.getId());
        wp.writeInt(skillID);
        return wp;
    }

    public static OutPacket GiveFameResponse(int mode, String charName, int newFame) {
        OutPacket wp = new OutPacket(OutHeader.FAME_RESPONSE);
        wp.write(PlayersHeaders.FAME_OPERATION_RESPONSE_SUCCESS);
        wp.writeMapleAsciiString(charName);
        wp.write(mode);
        wp.writeShort(newFame);
        wp.writeShort(0);
        return wp;
    }

    public static OutPacket GiveFameErrorResponse(int status) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.FAME_RESPONSE.getValue());
        wp.write(status);
        return wp;
    }

    public static OutPacket ReceiveFame(int mode, String charNameFrom) {
        OutPacket wp = new OutPacket(OutHeader.FAME_RESPONSE);
        wp.write(PlayersHeaders.FAME_OPERATION_FAME_CHANGED);
        wp.writeMapleAsciiString(charNameFrom);
        wp.write(mode);
        return wp;
    }

    public static OutPacket DamageSummon(int dwCharacterID, int objectId, int nDamage, int nAttackIdx, int dwMobTemplateID, boolean bLeft) {
        OutPacket wp = new OutPacket(OutHeader.DAMAGE_SUMMON);
        wp.writeInt(dwCharacterID);
        wp.writeInt(objectId);
        wp.write(nAttackIdx);
        wp.writeInt(dwMobTemplateID);
        wp.writeInt(nDamage);
        wp.writeBool(bLeft);
        return wp;
    }

    public static OutPacket DamageSummon(int dwCharacterID, int dwSummonedID, int nAttackIdx, int nDamage, int dwMobTemplateID, int bLeft) {
        OutPacket wp = new OutPacket(OutHeader.DAMAGE_SUMMON);
        wp.writeInt(dwCharacterID);
        wp.writeInt(dwSummonedID);
        wp.write(nAttackIdx);
        wp.writeInt(nDamage);
        if (nAttackIdx > -2) {
            wp.writeInt(dwMobTemplateID);
            wp.write(bLeft);
        }
        return wp;
    }

    public static OutPacket DamageSummon(Player p, int summon, int damage, byte action, int monsterIdFrom) {
        OutPacket wp = new OutPacket(OutHeader.DAMAGE_SUMMON);
        wp.writeInt(p.getId());
        wp.writeInt(summon);
        wp.write(action);
        wp.writeInt(damage);
        wp.writeInt(monsterIdFrom);
        wp.write(0);
        return wp;
    }

    public static OutPacket AddInventorySlot(InventoryType type, Item item) {
        return AddInventorySlot(type, item, false);
    }

    public static OutPacket AddInventorySlot(InventoryType type, Item item, boolean fromDrop) {
        OutPacket wp = new OutPacket(OutHeader.MODIFY_INVENTORY_ITEM);
        wp.write(fromDrop ? 1 : 0);
        wp.writeBytes(HexTool.getByteArrayFromHexString("01 00"));
        wp.write(type.getType());
        wp.writeShort(item.getPosition());
        AddItemInfo(wp, item, true, true);
        wp.writeLong(0);
        return wp;
    }

    public static OutPacket SpawnNPC(MapleNPC npc) {
        OutPacket wp = new OutPacket(OutHeader.SPAWN_NPC);
        wp.writeInt(npc.getObjectId());
        wp.writeInt(npc.getId());
        wp.writeShort(npc.getPosition().x);
        wp.writeShort(npc.getCy());
        if (npc.getF() == 1) {
            wp.write(0);
        } else {
            wp.write(1);
        }
        wp.writeShort(npc.getFh());
        wp.writeShort(npc.getRx0());
        wp.writeShort(npc.getRx1());
        wp.write(1);
        return wp;
    }

    public static OutPacket SpawnNPC(MapleNPC npc, boolean show) {
        OutPacket wp = new OutPacket(OutHeader.SPAWN_NPC);
        wp.writeInt(npc.getObjectId());
        wp.writeInt(npc.getId());
        wp.writeShort(npc.getPosition().x);
        wp.writeShort(npc.getCy());
        wp.write(npc.getF() == 1 ? 0 : 1);
        wp.writeShort(npc.getFh());
        wp.writeShort(npc.getRx0());
        wp.writeShort(npc.getRx1());
        wp.write(show ? 1 : 0);
        return wp;
    }

    public static OutPacket SpawnNPCRequestController(MapleNPC npc, boolean MiniMap) {
        OutPacket wp = new OutPacket(OutHeader.SPAWN_NPC_REQUEST_CONTROLLER);
        wp.write(1);
        wp.writeInt(npc.getObjectId());
        wp.writeInt(npc.getId());
        wp.writeShort(npc.getPosition().x);
        wp.writeShort(npc.getCy());
        wp.write(npc.getF() == 1 ? 0 : 1);
        wp.writeShort(npc.getFh());
        wp.writeShort(npc.getRx0());
        wp.writeShort(npc.getRx1());
        wp.write(MiniMap ? 1 : 0);
        return wp;
    }

    public static OutPacket RemoveNPC(int oid) {
        final OutPacket wp = new OutPacket(OutHeader.REMOVE_NPC);
        wp.writeInt(oid);
        return wp;
    }

    public static OutPacket RemoveNPCController(int objectid) {
        OutPacket wp = new OutPacket(OutHeader.SPAWN_NPC_REQUEST_CONTROLLER);
        wp.write(0);
        wp.writeInt(objectid);
        return wp;
    }

    public static OutPacket ShowDashP(List<Pair<BuffStat, Integer>> statups, int x, int y, int duration) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.FIRST_PERSON_APPLY_STATUS_EFFECT.getValue());
        wp.writeLong(0);
        int mask = GetLongMask(statups);
        wp.writeLong(mask);
        wp.writeShort(0);
        wp.writeInt(x);
        wp.writeInt(5001005);
        wp.writeBytes(HexTool.getByteArrayFromHexString("1A 7C 8D 35"));
        wp.writeShort(duration);
        wp.writeInt(y);
        wp.writeInt(5001005);
        wp.writeBytes(HexTool.getByteArrayFromHexString("1A 7C 8D 35"));
        wp.writeShort(duration);
        wp.writeShort(0);
        wp.write(2);
        return wp;
    }

    public static OutPacket GiveInfusion(int buffLength, int speed) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.FIRST_PERSON_APPLY_STATUS_EFFECT.getValue());
        wp.writeLong(0);
        //wp.writeLong(BuffStat.MORPH.getValue());
        wp.writeShort(speed);
        wp.writeInt(0000000);
        wp.writeLong(0);
        wp.writeShort(buffLength);
        wp.writeShort(0);
        return wp;
    }

    public static OutPacket GiveForeignInfusion(int cID, int speed, int duration) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.THIRD_PERSON_APPLY_STATUS_EFFECT.getValue());
        wp.writeInt(cID);
        wp.writeLong(0);
        //wp.writeLong(BuffStat.MORPH.getValue());
        wp.writeShort(0);
        wp.writeInt(speed);
        wp.writeInt(0000000);
        wp.writeLong(0);
        wp.writeInt(duration);
        wp.writeShort(0);
        return wp;
    }

    public static OutPacket ShowMonsterRiding(int cid, List<Pair<BuffStat, Integer>> statups, TamingMob mount) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.THIRD_PERSON_APPLY_STATUS_EFFECT.getValue());
        wp.writeInt(cid);
        int mask = GetLongMask(statups);
        wp.writeLong(0);
        wp.writeLong(mask);
        wp.writeShort(0);
        wp.writeInt(mount.getItemId());
        wp.writeInt(mount.getSkillId());
        wp.writeInt(0x2D4DFC2A);
        wp.writeShort(0);
        return wp;
    }

    public static OutPacket DropInventoryItem(InventoryType type, short src) {
        OutPacket wp = new OutPacket(OutHeader.MODIFY_INVENTORY_ITEM);
        wp.writeBytes(HexTool.getByteArrayFromHexString("01 01 03"));
        wp.write(type.getType());
        wp.writeShort(src);
        if (src < 0) {
            wp.write(1);
        }
        wp.writeLong(0);
        return wp;
    }

    public static OutPacket DropInventoryItemUpdate(InventoryType type, Item item) {
        OutPacket wp = new OutPacket(OutHeader.MODIFY_INVENTORY_ITEM);
        wp.writeBytes(HexTool.getByteArrayFromHexString("01 01 01"));
        wp.write(type.getType());
        wp.writeShort(item.getPosition());
        wp.writeShort(item.getQuantity());
        wp.writeLong(0);
        return wp;
    }

    public static OutPacket GetNPCShop(Client c, int sID, List<ShopItem> items) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        OutPacket wp = new OutPacket(OutHeader.OPEN_NPC_SHOP);
        wp.writeInt(sID);
        wp.writeShort(items.size());
        items.stream().map((item) -> {
            wp.writeInt(item.getItemId());
            return item;
        }).map((item) -> {
            wp.writeInt(item.getPrice());
            return item;
        }).forEach((item) -> {
            if (ii.isThrowingStar(item.getItemId()) || ii.isBullet(item.getItemId())) {
                wp.writeInt(0);
                wp.writeShort(BitTools.doubleToShortBits(ii.getPrice(item.getItemId())));
                wp.writeShort(ii.getSlotMax(c, item.getItemId()));
            }
            wp.writeShort(item.getBuyable());
        });
        return wp;
    }

    public static OutPacket DropMesoFromFieldObject(int amount, int itemOID, int dropperOID, int ownerID, Point dropFrom, Point dropTo, byte mod) {
        return DropItemFromFieldObjectInternal(amount, itemOID, dropperOID, ownerID, dropFrom, dropTo, mod, true);
    }

    public static OutPacket DropItemFromFieldObject(int itemID, int itemOID, int dropperOID, int ownerID, Point dropFrom, Point dropTo, byte mod) {
        return DropItemFromFieldObjectInternal(itemID, itemOID, dropperOID, ownerID, dropFrom, dropTo, mod, false);
    }

    public static OutPacket DropItemFromMapObject(FieldItem drop, Point dropfrom, Point dropto, byte mod) {
        OutPacket wp = new OutPacket(OutHeader.DROP_ITEM_FROM_MAPOBJECT);
        wp.write(mod);
        wp.writeInt(drop.getObjectId());
        wp.write(drop.getMeso() > 0 ? 1 : 0);
        wp.writeInt(drop.getItemId());
        wp.writeInt(drop.getOwnerId());
        wp.write(drop.getDropType());
        wp.writePos(dropto);
        wp.writeInt(0);
        //wp.writeInt(drop.getOwnerId());
        if (mod != 2) {
            wp.writePos(dropfrom);
            wp.writeShort(0);
        }
        boolean mesos = drop.getMeso() > 0;
        if (!mesos) {
            wp.writeBytes(HexTool.getByteArrayFromHexString("00 80 05 BB 46 E6 17 02"));
        }
        wp.write(drop.isPlayerDrop() ? 0 : 1);
        wp.writeLong(0);
        return wp;
    }

    public static OutPacket DropItemFromFieldObjectInternal(int itemID, int itemOID, int dropperOID, int ownerID, Point dropFrom, Point dropTo, byte mod, boolean mesos) {
        OutPacket wp = new OutPacket(OutHeader.DROP_ITEM_FROM_MAPOBJECT);
        wp.write(mod);
        wp.writeInt(itemOID);
        wp.write(mesos ? 1 : 0);
        wp.writeInt(itemID);
        wp.writeInt(ownerID);
        wp.write(0);
        wp.writeShort(dropTo.x);
        wp.writeShort(dropTo.y);
        wp.writeInt(ownerID);
        if (mod != 2) {
            wp.writeShort(dropFrom.x);
            wp.writeShort(dropFrom.y);
            wp.writeShort(0);
        }
        if (!mesos) {
            wp.writeBytes(HexTool.getByteArrayFromHexString("00 80 05 BB 46 E6 17 02"));
            //AddExpirationTime(wp, System.currentTimeMillis(), false);
        }
        wp.write(0);
        wp.writeLong(0);
        return wp;
    }

    public static OutPacket TriggerReactor(Reactor reactor, int stance) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.REACTOR_HIT.getValue());
        wp.writeInt(reactor.getObjectId());
        wp.write(reactor.getState());
        wp.writePos(reactor.getPosition());
        wp.writeShort(stance);
        wp.writeBool(false);
        wp.write(5);
        return wp;
    }

    public static OutPacket DestroyReactor(Reactor reactor) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.REACTOR_DESTROY.getValue());
        wp.writeInt(reactor.getObjectId());
        wp.write(reactor.getState());
        wp.writePos(reactor.getPosition());
        return wp;
    }

    public static OutPacket SpawnDoor(int objectID, Point pos, boolean town) {
        OutPacket wp = new OutPacket(OutHeader.SPAWN_DOOR);
        wp.writeBool(town);
        wp.writeInt(objectID);
        wp.writePos(pos);
        return wp;
    }

    public static OutPacket RemoveDoor(int objectID, boolean town) {
        OutPacket wp;
        if (town) {
            wp = new OutPacket(OutHeader.SPAWN_PORTAL);
            wp.writeInt(MapConstants.NULL_MAP);
            wp.writeInt(MapConstants.NULL_MAP);
        } else {
            wp = new OutPacket(OutHeader.REMOVE_DOOR);
            wp.write(0);
            wp.writeInt(objectID);
        }
        return wp;
    }

    public static OutPacket SpawnPortal(int townId, int targetId, Point pos) {
        OutPacket wp = new OutPacket(OutHeader.SPAWN_PORTAL);
        wp.writeInt(townId);
        wp.writeInt(targetId);
        if (pos != null) {
            wp.writePos(pos);
        }
        return wp;
    }

    public static OutPacket SpawnSpecialFieldObject(MapleSummon summon, boolean animated) {
        OutPacket wp = new OutPacket(OutHeader.SPAWN_SPECIAL_MAPOBJECT);
        wp.writeInt(summon.getOwnerId());
        wp.writeInt(summon.getSkill());
        wp.write(summon.getSkillLevel());
        wp.writePos(summon.getPosition());
        wp.write(summon.getStance());
        wp.writeShort(summon.getFh());
        wp.write(summon.getMovementType().getValue());
        wp.writeBool(animated);
        return wp;
    }

    public static OutPacket ShipEffect(boolean type) {
        final OutPacket wp = new OutPacket(OutHeader.SHIP_EFFECT);
        wp.write(type ? (byte) 13 : (byte) 9);
        wp.write(type ? (byte) 7 : (byte) 3);
        //todo: short
        return wp;
    }

    public static OutPacket MonsterBoat(boolean isEnter) {
        // 3C 0B 05 // 蝙蝠魔的船来了
        // 3C 0B 06 // 蝙蝠魔的船走了
        final OutPacket wp = new OutPacket(OutHeader.SHIP_EFFECT);
        if (isEnter == true) {
            wp.write(0x0B);
            wp.write(0x05);
        } else {
            wp.write(0x0B);
            wp.write(0x06);
        }
        return wp;
    }

    public static OutPacket SpawnReactor(Reactor reactor) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.REACTOR_SPAWN.getValue());
        wp.writeInt(reactor.getObjectId());
        wp.writeInt(reactor.getId());
        wp.write(reactor.getState());
        wp.writePos(reactor.getPosition());
        wp.writeBool(false);
        return wp;
    }

    public static OutPacket SpawnMist(int objectID, int ownerCid, int skill, int level, MapleMist mist) {
        OutPacket wp = new OutPacket(OutHeader.SPAWN_MIST);
        wp.writeInt(objectID);
        wp.writeBool(mist.isMobMist()); //1 = mob mist 0 = player mist
        wp.writeInt(skill);
        wp.write(level);
        wp.writeShort(mist.getSkillDelay());
        wp.writeInt(mist.getBox().x);
        wp.writeInt(mist.getBox().y);
        wp.writeInt(mist.getBox().x + mist.getBox().width);
        wp.writeInt(mist.getBox().y + mist.getBox().height);
        wp.writeInt(0);
        return wp;
    }

    public static OutPacket SpawnMobMist(int objectID, int ownerCid, int skillId, Rectangle mistPosition, int level) {
        OutPacket wp = new OutPacket(OutHeader.SPAWN_MIST);
        wp.writeInt(objectID);
        wp.write(1);// 1 = mob mist
        wp.writeInt(skillId);
        wp.write(level);
        wp.writeShort(8);
        wp.writeInt(mistPosition.x);
        wp.writeInt(mistPosition.y);
        wp.writeInt(mistPosition.x + mistPosition.width);
        wp.writeInt(mistPosition.y + mistPosition.height);
        wp.writeInt(1);
        return wp;
    }

    public static OutPacket RemoveMist(int objectID) {
        OutPacket wp = new OutPacket(OutHeader.REMOVE_MIST);
        wp.writeInt(objectID);
        return wp;
    }

    public static OutPacket spawnLove(MapleLove love) {
        OutPacket wp = new OutPacket(OutHeader.SPAWN_LOVE);
        wp.writeInt(love.getObjectId());
        wp.writeInt(love.itemID);
        wp.writeMapleAsciiString(love.message);
        wp.writeMapleAsciiString(love.player.getName());
        wp.writePos(love.getPosition());
        return wp;
    }

    public static OutPacket removeLove(MapleLove love, byte lType) {
        OutPacket wp = new OutPacket(OutHeader.REMOVE_LOVE);
        wp.write(lType);
        wp.writeInt(love.getObjectId());
        return wp;
    }

    public static OutPacket SendTV(Player chr, List<String> messages, int type, Player partner) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.SEND_TV.getValue());
        wp.write(partner != null ? 2 : 0);
        wp.write(type);
        AddCharLook(wp, chr, false);
        wp.writeMapleAsciiString(chr.getName());
        if (partner != null) {
            wp.writeMapleAsciiString(partner.getName());
        } else {
            wp.writeShort(0);
        }
        for (int i = 0; i < messages.size(); i++) {
            if (i == 4 && messages.get(4).length() > 15) {
                wp.writeMapleAsciiString(messages.get(4).substring(0, 15));
            } else {
                wp.writeMapleAsciiString(messages.get(i));
            }
        }
        wp.writeInt(1337);
        if (partner != null) {
            AddCharLook(wp, partner, false);
        }
        return wp;
    }

    public static OutPacket EnableTV() {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.ENABLE_TV.getValue());
        wp.writeInt(0);
        wp.write(0);
        return wp;
    }

    public static OutPacket RemoveTV() {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.REMOVE_TV.getValue());
        return wp;
    }

    public static OutPacket shopChat(final String message, final int slot) {
        OutPacket wp = new OutPacket(OutHeader.PLAYER_INTERACTION);
        wp.write(6);
        wp.write(8);
        wp.write(slot);
        wp.writeMapleAsciiString(message);

        return wp;
    }

    public static OutPacket YellowChat(String msg) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.TIP_MESSAGE.getValue());
        wp.write(5);
        wp.writeMapleAsciiString(msg);
        return wp;
    }

    public static void SendUnkwnNote(String to, String msg, String from) throws SQLException {
        Connection con = Start.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement("INSERT INTO notes (`to`, `from`, `message`, `timestamp`) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, to);
            ps.setString(2, from);
            ps.setString(3, msg);
            ps.setLong(4, System.currentTimeMillis());
            ps.executeUpdate();
        }
        con.close();
    }

    public static OutPacket ShowHide() {
        OutPacket wp = new OutPacket(4);
        wp.write(OutHeader.GM.getValue());
        wp.write(16);
        wp.writeBool(true);
        return wp;
    }

    public static OutPacket StopHide() {
        OutPacket wp = new OutPacket(4);
        wp.write(OutHeader.GM.getValue());
        wp.write(16);
        wp.writeBool(false);
        return wp;
    }

    public static OutPacket ArrangeStorage(byte slots, Collection<Item> items, boolean changed) {
        OutPacket mplew = new OutPacket();
        mplew.write(OutHeader.NPC_STORAGE.getValue());
        mplew.write(0x0E);
        mplew.write(slots);
        mplew.write(0x7C);
        mplew.writeZeroBytes(10);
        mplew.write(items.size());
        for (Item item : items) {
            AddItemInfo(mplew, item, true, true);
        }
        mplew.write(0);
        return mplew;
    }

    /**
     * Error message to the client if a user cannot warp to another area.
     *
     * @param type Message to be sent. Possible values :<br>
     * 0x01 (1) - You cannot move that channel. Please try again later. 0x02 (2)
     * - You cannot go into the cash shop. Please try again later. 0x03 (3) -
     * The Item-Trading shop is currently unavailable, please try again later.
     * 0x04 (4) - You cannot go into the trade shop, due to the limitation of
     * user count. 0x05 (5) - You do not meet the minimum level requirement to
     * access the Trade Shop.
     * @return
     */
    public static OutPacket ServerMigrateFailed(byte msg) {
        OutPacket mplew = new OutPacket();
        mplew.write(OutHeader.BLOCK_MIGRATE.getValue());
        mplew.write(msg);
        return mplew;
    }

    public static OutPacket GiveDash(List<Pair<BuffStat, Integer>> statups, int x, int y, int duration) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.FIRST_PERSON_APPLY_STATUS_EFFECT.getValue());
        int mask = GetLongMask(statups);
        wp.writeInt(mask);
        wp.writeShort(0);
        wp.writeInt(x);
        wp.writeInt(5001005);
        wp.writeBytes(HexTool.getByteArrayFromHexString("1A 7C 8D 35"));
        wp.writeShort(duration);
        wp.writeInt(y);
        wp.writeInt(5001005);
        wp.writeBytes(HexTool.getByteArrayFromHexString("1A 7C 8D 35"));
        wp.writeShort(duration);
        wp.writeShort(0);
        wp.write(2);
        return wp;
    }

    public static OutPacket ShowSpeedInfusion(int cid, int skillid, int time, List<Pair<BuffStat, Integer>> statups) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.THIRD_PERSON_APPLY_STATUS_EFFECT.getValue());
        wp.writeInt(cid);
        int mask = GetLongMask(statups);
        wp.writeInt(mask);
        wp.writeShort(0);
        wp.writeInt(statups.get(0).getRight());
        wp.writeInt(skillid);
        wp.writeInt(0);
        wp.writeInt(0);
        wp.writeShort(0);
        wp.writeShort(time);
        wp.writeShort(0);
        return wp;
    }

    public static OutPacket UpdateBattleShipHP(int chr, int hp) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.SHOW_MONSTER_HP.getValue());
        wp.writeInt(chr);
        wp.write(hp);
        return wp;
    }

    public static OutPacket PortalBlocked(int message) {
        OutPacket wp = new OutPacket(3);
        wp.write(OutHeader.BLOCK_PORTAL.getValue());
        wp.write(message);
        return wp;
    }

    public static OutPacket GetTrockRefresh(Player p, boolean delete) {
        OutPacket wp = new OutPacket();

        wp.write(OutHeader.TROCK_LOCATIONS.getValue());
        wp.write(delete ? 2 : 3);
        int[] map = p.getRegRocks();
        for (int i = 0; i < 5; i++) {
            wp.writeInt(map[i]);
        }
        return wp;
    }

    public static OutPacket CancelMonsterStatus(int oid, Map<MonsterStatus, Integer> stats) {
        OutPacket mplew = new OutPacket();
        mplew.write(OutHeader.CANCEL_MONSTER_STATUS.getValue());
        mplew.writeInt(oid);
        int mask = 0;
        for (MonsterStatus stat : stats.keySet()) {
            mask |= stat.getValue();
        }
        mplew.writeInt(mask);
        mplew.write(1);

        return mplew;
    }

    public static OutPacket SendPolice(String text) {
        final OutPacket mplew = new OutPacket();
        mplew.write(OutHeader.GM_POLICE.getValue());
        mplew.writeMapleAsciiString(text);
        return mplew;
    }

    public static OutPacket FinishedGather(int type) {
        final OutPacket wp = new OutPacket();
        wp.write(OutHeader.FINISH_SORT.getValue());
        wp.write(0);
        wp.write(type);
        return wp;
    }

    public static OutPacket FinishedSort(int type) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.FINISH_SORT2.getValue());
        wp.write(0);
        wp.write(type);
        return wp;
    }

    public static OutPacket UpdateInventorySlotLimit(int type, int newLimit) {
        final OutPacket wp = new OutPacket(OutHeader.UPDATE_INVENTORY_CAPACITY);
        wp.write(type);
        wp.write(newLimit);
        return wp;
    }

    public static OutPacket ShowOwnRecovery(byte heal) {
        final OutPacket wp = new OutPacket(OutHeader.FIRST_PERSON_VISUAL_EFFECT);
        wp.write(0x0A);
        wp.write(heal);
        return wp;
    }

    public static OutPacket ShowRecovery(int cid, byte amount) {
        final OutPacket wp = new OutPacket(OutHeader.THIRD_PERSON_VISUAL_EFFECT);
        wp.writeInt(cid);
        wp.write(0x0A);
        wp.write(amount);
        return wp;
    }

    public static OutPacket GetRockPaperScissorsMode(byte mode, int mesos, int selection, int answer) {
        OutPacket wp = new OutPacket();

        wp.write(OutHeader.RPS_GAME.getValue());
        wp.write(mode);
        switch (mode) {
            case 6: {
                if (mesos != -1) {
                    wp.writeInt(mesos);
                }
                break;
            }
            case 8: {
                wp.writeInt(9000019);
                break;
            }
            case 11: {
                wp.write(selection);
                wp.write(answer);
                break;
            }
        }
        return wp;
    }

    public static OutPacket ThrowGrenade(int cid, Point p, int keyDown, int skillId, int skillLevel) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.THROW_GRENADE.getValue());
        wp.writeInt(cid);
        wp.writeInt(p.x);
        wp.writeInt(p.y);
        wp.writeInt(keyDown);
        wp.writeInt(skillId);
        wp.writeInt(skillLevel);
        return wp;
    }

    public static final OutPacket GetSpeedQuiz(int npc, byte type, int oid, int points, int questionNo, int time) {
        OutPacket wp = new OutPacket(OutHeader.NPC_TALK);

        wp.write(4);
        wp.writeInt(npc);
        wp.writeShort(6);
        wp.write(0);
        wp.writeInt(type);
        wp.writeInt(oid);
        wp.writeInt(points);
        wp.writeInt(questionNo);
        wp.writeInt(time);

        return wp;
    }

    public static OutPacket SetNPCScriptable(int npcId) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.SET_NPC_SCRIPTABLE.getValue());
        wp.write(1); // following structure is repeated n times
        wp.writeInt(npcId);
        wp.writeMapleAsciiString("Talk to npcid" + npcId);
        wp.writeInt(0); // start time
        wp.writeInt(Integer.MAX_VALUE); // end time
        return wp;
    }
}
