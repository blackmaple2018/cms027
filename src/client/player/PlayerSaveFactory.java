package client.player;

import client.player.inventory.Inventory;
import client.player.inventory.types.InventoryType;
import client.player.inventory.Item;
import client.player.inventory.ItemFactory;
import client.player.inventory.ItemPet;
import client.player.skills.PlayerSkill;
import client.player.skills.PlayerSkillEntry;
import community.MapleBuddyListEntry;
import static configure.Gamemxd.一转技能附魔;
import static configure.Gamemxd.三转技能附魔30;
import static configure.Gamemxd.二转技能附魔20;
import static configure.Gamemxd.二转技能附魔30;
import constants.MapConstants;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import server.maps.SavedLocationType;
import server.maps.portal.Portal;
import tools.Pair;

/**
 * @author GabrielSin (http://forum.ragezone.com/members/822844.html)
 */
public class PlayerSaveFactory {

    public enum DeleteType {

        CHARACTER("characters", "id"),
        SKILL("skills", "characterid"),
        BUDDY("buddies", "characterid"),
        BUDDY_ENTRIES("buddyentries", "owner"),
        BBS_THREADS("bbs_threads", "postercid"),
        KEYMAP("keymap", "characterid"),
        FAME_LOG("famelog", "characterid"),
        QUEST("queststatus", "characterid"),
        PLAYER_QUEST("player_quests", "characterid"),
        WISH_LIST("wishlist", "characterid"),
        SKILL_MACRO("scrillmacros", "characterid"),
        SKILL_COOLDOWN("cooldowns", "charid"),
        SAVED_LOCATION("savedlocations", "characterid"),
        REG_LOCATIONS("regrocklocations", "characterid"),
        INVENTORY_ITEMS("inventoryitems", "characterid"),
        TROCK_LOCATIONS("trocklocations", "characterid");

        String type, field;

        private DeleteType(String type, String field) {
            this.type = type;
            this.field = field;
        }

        public void removeFromType(Connection con, int typeInt) throws SQLException {
            try {
                String sql = "DELETE FROM " + this.type + " WHERE " + this.field + " = ?";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, typeInt);
                    ps.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * <存档角色属性>
     */
    public static void savingCharacterStats(Player ret, Connection con) {
        try {
            try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET "
                    + "`level` = ?, `exp` = ?, `str` = ?, `dex` = ?, `luk` = ?, `inte` = ?, `hp` = ?, `mp` = ?, "
                    + "`maxhp` = ?, `maxmp` = ?, `meso` = ?, `hpApUsed` = ?, `mpApUsed` = ?, `job` = ?, `skincolor` = ?, "
                    + "`gender` = ?, `fame` = ?, `hair` = ?, `face` = ?, `ap` = ?, `sp` = ?, `map` = ?, `spawnpoint` = ?, "
                    + "`gm` = ?, `party` = ?, `buddyCapacity` = ?, `omokwins` = ?, `omoklosses` = ?, omokties = ?, "
                    + "`matchcardwins` = ?, `matchcardlosses` = ?, `matchcardties` = ?, `equipslots` = ?, `useslots` = ?, "
                    + "`setupslots` = ?, `etcslots` = ?, `ariantPoints` = ?, `hpMpUsed` = ?, `pets` = ?, `playtime` = ?, "
                    + "`spouseId` = ?, `dataString` = ?, `KillMonster` = ?, `RecoveryHP` = ?, `RecoveryMP` = ? ,`RecoveryHPitemid` = ?, "
                    + "`RecoveryMPitemid` = ? ,  `paodianjinbi` = ? , `paodianexp` = ? , `paodiandianquan` = ?, `maxlevel` = ? , `pdexp` = ? , "
                    + "`todayOnlineTime` = ?, `totalOnlineTime` = ?, `totalOnlineTimett` = ? , `master` = ? , `zhujiemian` = ?, `switch_bufftime` = ? , "
                    + "`switch_zhubosx` = ? , `switch_qunltkx` = ?, `switch_skill` = ? , `switch_duanzao`= ? , `mapid` = ?  WHERE `id` = ?")) {
                int i = 0;
                ps.setInt(++i, ret.level);
                ps.setInt(++i, ret.exp.get() < 0 ? 0 : ret.exp.get());//KillMonster
                ps.setInt(++i, ret.stats.str);
                ps.setInt(++i, ret.stats.dex);
                ps.setInt(++i, ret.stats.luk);
                ps.setInt(++i, ret.stats.int_);
                ps.setInt(++i, ret.stats.hp);
                ps.setInt(++i, ret.stats.mp);
                ps.setInt(++i, ret.stats.maxHP);
                ps.setInt(++i, ret.stats.maxMP);
                ps.setInt(++i, ret.meso.get());
                ps.setInt(++i, ret.stats.hpApUsed);
                ps.setInt(++i, ret.stats.mpApUsed);
                ps.setInt(++i, ret.job.getId());
                ps.setInt(++i, ret.skin.getId());
                ps.setInt(++i, ret.gender);
                ps.setInt(++i, ret.pop);
                ps.setInt(++i, ret.hair);
                ps.setInt(++i, ret.eyes);
                ps.setInt(++i, ret.stats.remainingAp);
                ps.setInt(++i, ret.stats.remainingSp);
                if (ret.field == null) {
                    ps.setInt(++i, 0);
                } else {
                    if (ret.field.getForcedReturnId() != MapConstants.NULL_MAP) {
                        ps.setInt(++i, ret.field.getForcedReturnId());
                    } else {
                        ps.setInt(++i, ret.stats.getHp() < 1 ? ret.field.getReturnMapId() : ret.field.getId());
                    }
                }
                if (ret.field == null) {
                    ps.setInt(++i, 0);
                } else {
                    Portal closest = ret.field.findClosestPlayerSpawnpoint(ret.getPosition());
                    ps.setInt(++i, closest != null ? closest.getId() : 0);
                }

                ps.setInt(++i, ret.gm);
                ps.setInt(++i, ret.party != null ? ret.party.getId() : -1);
                ps.setInt(++i, ret.buddyList.getCapacity());
                ps.setInt(++i, ret.omokWins);
                ps.setInt(++i, ret.omokLosses);
                ps.setInt(++i, ret.omokTies);
                ps.setInt(++i, ret.matchCardWins);
                ps.setInt(++i, ret.matchCardLosses);
                ps.setInt(++i, ret.matchCardTies);
                ps.setInt(++i, ret.getInventory(InventoryType.getByType((byte) InventoryType.EQUIP.getType())).getSlotLimit());
                ps.setInt(++i, ret.getInventory(InventoryType.getByType((byte) InventoryType.USE.getType())).getSlotLimit());
                ps.setInt(++i, ret.getInventory(InventoryType.getByType((byte) InventoryType.SETUP.getType())).getSlotLimit());
                ps.setInt(++i, ret.getInventory(InventoryType.getByType((byte) InventoryType.ETC.getType())).getSlotLimit());
                ps.setInt(++i, ret.ariantPoints);
                ps.setInt(++i, ret.stats.hpMpApUsed);
                //宠物
                final StringBuilder petz = new StringBuilder();
                int petLength = 0;
                for (final ItemPet pet : ret.pets) {
                    if (pet.getSummoned()) {
                        pet.saveDatabase();
                        petz.append(pet.getInventoryPosition());
                        petz.append(",");
                        petLength++;
                    }
                }
                while (petLength < 3) {
                    petz.append("-1,");
                    petLength++;
                }
                final String petstring = petz.toString();
                ps.setString(++i, petstring.substring(0, petstring.length() - 1));
                ps.setLong(++i, ret.getPlaytime());
                ps.setInt(++i, ret.partner);
                ps.setString(++i, ret.dataString);
                ps.setInt(++i, ret.杀怪数量);
                ps.setInt(++i, ret.getRecoveryHP());
                ps.setInt(++i, ret.getRecoveryMP());
                ps.setInt(++i, ret.getRecoveryHPitemid());
                ps.setInt(++i, ret.getRecoveryMPitemid());
                ps.setInt(++i, ret.get泡点金币());
                ps.setInt(++i, ret.get泡点经验());
                ps.setInt(++i, ret.get泡点点券());
                ps.setInt(++i, ret.getMaxlevel());
                ps.setInt(++i, ret.get角色泡点经验());
                ps.setInt(++i, ret.getTodayOnlineTime());
                ps.setInt(++i, ret.getTotalOnlineTime());
                ps.setInt(++i, ret.getTotalOnlineTimett());
                ps.setInt(++i, ret.师傅());
                ps.setInt(++i, ret.主界面());
                ps.setInt(++i, ret.switch_bufftime());
                ps.setInt(++i, ret.switch_zhubosx());
                ps.setInt(++i, ret.switch_qunltkx());
                ps.setInt(++i, ret.switch_skill());
                ps.setInt(++i, ret.switch_duanzao());
                ps.setInt(++i, ret.市场传送点());
                ps.setInt(++i, ret.id);
                ps.executeUpdate();
                //System.out.println(ps.toString());
                //System.out.println("num: " + num + " i: " + i);
                ps.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * <存档技能>
     */
    public static void savingCharacterSkills(Player ret, Connection con, Player p) {
        try {
            DeleteType.SKILL.removeFromType(con, ret.id);
            PreparedStatement ps = con.prepareStatement("INSERT INTO `skills` (`characterid`, `skillid`, `skilllevel`, `masterlevel`) VALUES (?, ?, ?, ?)");
            ps.setInt(1, ret.id);
            for (Entry<PlayerSkill, PlayerSkillEntry> skill : ret.skills.entrySet()) {
                ps.setInt(2, skill.getKey().getId());
                if (一转技能附魔(skill.getKey().getId()) && skill.getValue().skillevel > 20) {
                    ps.setInt(3, 20);
                } else if (二转技能附魔20(skill.getKey().getId()) && skill.getValue().skillevel > 20) {
                    ps.setInt(3, 20);
                } else if (二转技能附魔30(skill.getKey().getId()) && skill.getValue().skillevel > 30) {
                    ps.setInt(3, 30);
                } else if (三转技能附魔30(skill.getKey().getId()) && skill.getValue().skillevel > 30) {
                    ps.setInt(3, 30);
                } else {
                    ps.setInt(3, skill.getValue().skillevel);
                }
                ps.setInt(4, skill.getValue().masterlevel);
                ps.executeUpdate();
            }
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void savingCharacterSavedLocations(Player ret, Connection con) {
        try {
            DeleteType.SAVED_LOCATION.removeFromType(con, ret.id);
            PreparedStatement ps = con.prepareStatement("INSERT INTO `savedlocations` (`characterid`, `locationtype`, `map`) VALUES (?, ?, ?)");
            ps.setInt(1, ret.id);
            for (SavedLocationType savedLocationType : SavedLocationType.values()) {
                if (ret.savedLocations[savedLocationType.ordinal()] != -1) {
                    ps.setString(2, savedLocationType.name());
                    ps.setInt(3, ret.savedLocations[savedLocationType.ordinal()]);
                    ps.addBatch();
                }
            }
            ps.executeBatch();
            ps.close();
            ret.setChangedSavedLocations(false);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void savingCharacterBuddy(Player ret, Connection con) {
        try {
            DeleteType.BUDDY_ENTRIES.removeFromType(con, ret.id);
            PreparedStatement ps = con.prepareStatement("INSERT INTO `buddyentries` (owner, `buddyid`) VALUES (?, ?)");
            ps.setInt(1, ret.id);
            for (MapleBuddyListEntry entry : ret.buddyList.getBuddies()) {
                ps.setInt(2, entry.getCharacterId());
                ps.addBatch();
            }
            ps.executeBatch();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void savingCharacterTrockLocations(Player ret, Connection con) {
        try {
            DeleteType.TROCK_LOCATIONS.removeFromType(con, ret.id);
            PreparedStatement ps = con.prepareStatement("INSERT INTO `trocklocations` (`characterid`, `mapid`) VALUES(?, ?) ");
            for (int i = 0; i < ret.rocks.length; i++) {
                if (ret.rocks[i] != MapConstants.NULL_MAP) {
                    ps.setInt(1, ret.id);
                    ps.setInt(2, ret.rocks[i]);
                    ps.addBatch();
                }
            }
            ps.executeBatch();
            ps.close();
            ret.setChangedTrockLocations(false);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void savingCharacterRegRockLocations(Player ret, Connection con) {
        try {
            DeleteType.REG_LOCATIONS.removeFromType(con, ret.id);
            PreparedStatement ps = con.prepareStatement("INSERT INTO `regrocklocations` (`characterid`, `mapid`) VALUES(?, ?) ");
            for (int i = 0; i < ret.regrocks.length; i++) {
                if (ret.regrocks[i] != MapConstants.NULL_MAP) {
                    ps.setInt(1, ret.id);
                    ps.setInt(2, ret.regrocks[i]);
                    ps.addBatch();
                }
            }
            ps.executeBatch();
            ret.setChangedRegrockLocations(false);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void savingCharacterInventory(Player ret) {
        try {
            List<Pair<Item, InventoryType>> itemsWithType = new ArrayList<>();
            for (Inventory iv : ret.inventory) {
                iv.lockInventory().readLock().lock();
                try {
                    iv.list().stream().forEach((item) -> {
                        itemsWithType.add(new Pair<>(item, iv.getType()));
                    });
                } finally {
                    iv.lockInventory().readLock().unlock();
                }
            }
            ItemFactory.INVENTORY.saveItems(itemsWithType, ret.id);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
