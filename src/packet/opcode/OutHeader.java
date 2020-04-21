/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package packet.opcode;

import java.util.Arrays;
import java.util.List;

public enum OutHeader {

    PING(15),
    // LOGIN
    LOGIN_STATUS(1),
    CHOOSE_GENDER(2),
    GENDER_SET(3),
    SERVERSTATUS(4),
    GENDER_DONE((short) 0x04),
    CONFIRM_EULA_RESULT((short) 0x05),
    PIN_OPERATION((short) 0x06),
    SERVERLIST(5),
    CHARLIST(6),
    SERVER_IP(7),
    CHAR_NAME_RESPONSE(8),
    RELOG_RESPONSE((short) 0x16),
    ADD_NEW_CHAR_ENTRY(12),
    GAME_HOST_ADDRESS(14),
    DELETE_CHAR_RESPONSE((short) 0x0F),
    CHANNEL_SELECTED((short) 0x14),
    PIN_ASSIGNED(-1),
    ALL_CHARLIST(-1),
    // CHANNEL
    MODIFY_INVENTORY_ITEM(23),
    UPDATE_INVENTORY_CAPACITY(24),
    UPDATE_STATS(25),
    FIRST_PERSON_APPLY_STATUS_EFFECT(26),
    FIRST_PERSON_CANCEL_STATUS_EFFECT(27),
    UPDATE_SKILLS(29),
    FAME_RESPONSE(31),
    SHOW_STATUS_INFO(32),
    SHOW_NOTES(33),
    TROCK_LOCATIONS(34),
    CHAR_INFO(36),
    PARTY_OPERATION(37),
    BUDDYLIST(38),
    SPAWN_PORTAL(39),
    SERVERMESSAGE(40),
    WARP_TO_MAP(43),
    CS_OPEN(44),
    PRIVATE_CHAT(50),
    WHISPER(51),
    FIELD_EFFECT(53),
    BLOW_WEATHER(54),
    GM(56),
    CLOCK(59),
    SHIP_EFFECT(60),
    TRAIN_EFFECT(61),
    SPAWN_PLAYER(64),
    REMOVE_PLAYER_FROM_MAP(65),
    MAP_CHAT(67),
    UPDATE_CHAR_BOX(68),
    SPAWN_PET(71),
    MOVE_PET(72),
    PET_CHAT(73),
    PET_NAMECHANGE(74),
    PET_RESPONSE(75),
    SPAWN_SPECIAL_MAPOBJECT(78),
    REMOVE_SPECIAL_MAPOBJECT(79),
    MOVE_SUMMON(80),
    SUMMON_ATTACK(81),
    DAMAGE_SUMMON(82),
    MOVE_PLAYER(86),
    CLOSE_RANGE_ATTACK(87),
    RANGED_ATTACK(88),
    MAGIC_ATTACK(89),
    SKILL_EFFECT(90),
    CANCEL_SKILL_EFFECT(91),
    DAMAGE_PLAYER(92),
    FACIAL_EXPRESSION(93),
    UPDATE_CHAR_LOOK(94),
    THIRD_PERSON_VISUAL_EFFECT(95),
    THIRD_PERSON_APPLY_STATUS_EFFECT(96),
    THIRD_PERSON_CANCEL_STATUS_EFFECT(97),
    UPDATE_PARTYMEMBER_HP(98),
    CHAIR(101),
    FIRST_PERSON_VISUAL_EFFECT(102),
    SPAWN_MONSTER(111),
    KILL_MONSTER(112),
    SPAWN_MONSTER_CONTROL(113),
    MOVE_MONSTER(115),
    MOVE_MONSTER_RESPONSE(116),
    APPLY_MONSTER_STATUS(118),
    CANCEL_MONSTER_STATUS(119),
    DAMAGE_MONSTER(122),
    SPAWN_NPC(128),
    REMOVE_NPC(129),
    SPAWN_NPC_REQUEST_CONTROLLER(130),
    NPC_ACTION(132),
    DROP_ITEM_FROM_MAPOBJECT(136),
    REMOVE_ITEM_FROM_MAP(137),
    LOVE_MESSAGE(140),
    SPAWN_LOVE(141),
    REMOVE_LOVE(142),
    SPAWN_MIST(145),
    REMOVE_MIST(146),
    SPAWN_DOOR(149),
    REMOVE_DOOR(150),
    REACTOR_HIT(153),
    REACTOR_SPAWN(155),
    REACTOR_DESTROY(156),
    NPC_TALK(165),
    OPEN_NPC_SHOP(168),
    CONFIRM_SHOP_TRANSACTION(169),
    NPC_STORAGE(173),
    STORAGE_OPERATION(174),
    MESSENGER(177),
    PLAYER_INTERACTION(180),
    CS_UPDATE(192),
    CASH_SHOP(193),
    SHOP_SCANNER_RESULT((short) 0x43),
    SHOP_LINK_RESULT((short) 0x44),
    AVATAR_MEGA((short) 0x54),
    NPC_SUMMON_EFFECT((short) 0xC6),
    NPC_SPECIAL_ACTION((short) 0xC7),
    SET_NPC_SCRIPTABLE((short) 0xC8),
    END_NPCPOOL((short) 0xC9),
    FINISH_SORT((short) 0x31),
    FINISH_SORT2((short) 0x32),
    SHOW_MESO_GAIN((short) 0x22),
    SHOW_QUEST_COMPLETION((short) 0x2E),
    SHOW_HIRED_MERCHANT_AGREEMENT((short) 0x2F),
    GMEVENT_INSTRUCTIONS((short) 0x6D),
    BLOCK_PORTAL((short) 0x61),
    BLOCK_MIGRATE((short) 0x62),
    GM_POLICE((short) 0x59),
    ANNOUNCE_PLAYER_SHOP((short) 0x67),
    SHOW_SCROLL_EFFECT((short) 0x7E),
    TIP_MESSAGE((short) 0x4A),
    UPDATE_QUEST_INFO((short) 0xA6),
    KEYMAP((short) 0x107),
    SHOW_MONSTER_HP((short) -1),
    UPDATE_GUILD_MEMBERSHIP((short) 0x9D),
    UPDATE_GUILD_EMBLEM((short) 0x9E),
    THROW_GRENADE((short) 0x9F),
    SHOW_ITEM_EFFECT((short) 0x96),
    ITEM_CHAIR((short) 0x97),
    GUILD_OPERATION((short) 0x3E),
    BBS_OPERATION((short) 0x38),
    SHOW_MAGNET((short) 0xBE),
    FREDRICK_MESSAGE((short) 0xF1),
    FREDRICK((short) 0xF2),
    RPS_GAME((short) 0xF3),
    
    PET_ITEM_IGNORE(-1),
    COOLDOWN((short) 0xAD),
    PLAYER_HINT((short) 0xA9),
    USE_SKILL_BOOK((short) 0x30),
    SHOW_EQUIP_EFFECT((short) 0x63),
    SKILL_MACRO((short) 0x5B),
    PLAYER_NPC((short) 0x4E),
    SUMMON_SKILL((short) 0x8B),
    ARIANT_PQ_START((short) 0xEA),
    CATCH_MONSTER((short) 0xBF),
    ARIANT_SCOREBOARD((short) 0x76),
    ZAKUM_SHRINE((short) 0xEC),
    CHANGE_BINDING((short) 0x7B),
    UPDATE_MOUNT((short) 0x2D),
    DUEY((short) 0xFD),
    MONSTER_CARNIVAL_START((short) 0xE2),
    MONSTER_CARNIVAL_OBTAINED_CP((short) 0xE3),
    MONSTER_CARNIVAL_PARTY_CP((short) 0xE4),
    MONSTER_CARNIVAL_SUMMON((short) 0xE5),
    MONSTER_CARNIVAL_MESSAGE((short) 0xE6),
    MONSTER_CARNIVAL_DIED((short) 0xE7),
    MONSTER_CARNIVAL_LEAVE((short) 0xE8),
    SEND_TV((short) 0x10D),
    REMOVE_TV((short) 0x10E),
    ENABLE_TV((short) 0x10F),
    SPAWN_HIRED_MERCHANT((short) 0xCA),
    DESTROY_HIRED_MERCHANT((short) 0xCB),
    UPDATE_HIRED_MERCHANT((short) 0xCC),
    SPOUSE_CHAT((short) 0x66),
    REPORTREPLY((short) 0x34),
    MTS_OPEN((short) 0x5D),
    MTS_OPERATION((short) 0x114),
    MTS_OPERATION2((short) 0x113),
    ALLIANCE_OPERATION((short) 0x3F),
    SILVER_BOX_OPEN((short) 0x5A),
    GENDER((short) 0x37),
    PET_AUTO_HP_POT((short) 0x108),
    PET_AUTO_MP_POT((short) 0x109),
    TRADE_MONEY_LIMIT((short) 0x36),
    ENERGY_CHARGE_ATTACK((short) 0x91);

    private short code = -2;

    private static final List<OutHeader> spam = Arrays.asList(
            GAME_HOST_ADDRESS,
            PING,
            UPDATE_STATS,
            NPC_ACTION,
            MOVE_PLAYER,
            MOVE_SUMMON,
            MOVE_MONSTER_RESPONSE,
            MOVE_MONSTER,
            SPAWN_MONSTER_CONTROL,
            SPAWN_MONSTER,
            KILL_MONSTER,
            DROP_ITEM_FROM_MAPOBJECT,
            REMOVE_ITEM_FROM_MAP,
            SHOW_STATUS_INFO,
            CLOSE_RANGE_ATTACK,
            RANGED_ATTACK,
            MAGIC_ATTACK,
            SKILL_EFFECT,
            DAMAGE_PLAYER,
            MODIFY_INVENTORY_ITEM,
            FIRST_PERSON_VISUAL_EFFECT,
            UPDATE_PARTYMEMBER_HP,
            NPC_TALK,
            FIRST_PERSON_APPLY_STATUS_EFFECT,
            PARTY_OPERATION,
            MAP_CHAT,
            MOVE_PET,
            SPAWN_NPC_REQUEST_CONTROLLER,
            SPAWN_NPC,
            SPAWN_PLAYER,
            SERVERMESSAGE,
            SPAWN_PET,
            REMOVE_PLAYER_FROM_MAP,
            FIELD_EFFECT,
            PRIVATE_CHAT,
            //WARP_TO_MAP,
            //THIRD_PERSON_VISUAL_EFFECT,
            //THIRD_PERSON_CANCEL_STATUS_EFFECT,
            FIRST_PERSON_CANCEL_STATUS_EFFECT
    );

    public static boolean isSpamHeader(OutHeader outHeader) {
        return spam.contains(outHeader);
    }

    OutHeader(int code) {
        this.code = (short) code;
    }

    public short getValue() {
        return code;
    }

    public static String getOpcodeName(int value) {
        for (OutHeader opcode : OutHeader.values()) {
            if (opcode.getValue() == value) {
                return opcode.name();
            }
        }
        return "UNKNOWN";
    }

    public static OutHeader getOutHeaderByOp(int op) {
        for (OutHeader outHeader : OutHeader.values()) {
            if (outHeader.getValue() == op) {
                return outHeader;
            }
        }
        return null;
    }

}
