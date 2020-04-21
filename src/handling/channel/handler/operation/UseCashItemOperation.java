package handling.channel.handler.operation;

import client.player.Player;
import client.player.PlayerJob;
import client.player.PlayerNote;
import client.player.PlayerStat;
import client.player.PlayerStringUtil;
import client.player.inventory.Equip;
import client.player.inventory.types.InventoryType;
import client.player.inventory.Item;
import client.player.inventory.ItemPet;
import client.player.skills.PlayerSkill;
import client.player.skills.PlayerSkillFactory;
import static configure.Gamemxd.一转技能附魔;
import static configure.Gamemxd.三转技能附魔30;
import static configure.Gamemxd.二转技能附魔20;
import static configure.Gamemxd.二转技能附魔30;
import constants.ExperienceConstants;
import constants.GameConstants;
import constants.ItemConstants;
import constants.NPCConstants;
import constants.SkillConstants;
import static handling.channel.handler.ChannelHeaders.InventoryHeaders.*;
import static handling.channel.handler.ChannelHeaders.StatsHeaders.*;
import static handling.channel.handler.InventoryHandler.UseIncubator;
import handling.channel.handler.StatsHandling;
import packet.transfer.read.InPacket;
import handling.world.service.BroadcastService;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;
import packet.creators.EffectPackets;
import packet.creators.InteractionPackets;
import packet.creators.PacketCreator;
import packet.creators.PetPackets;
import server.MapleStatEffect;
import server.itens.InventoryManipulator;
import server.itens.ItemInformationProvider;
import server.maps.Field;
import server.maps.FieldLimit;
import server.maps.MapleLove;
import server.maps.MapleMist;
import server.maps.object.AbstractMapleFieldObject;
import server.minirooms.PlayerShopItem;
import server.shops.Shop;
import server.shops.ShopFactory;
import tools.Pair;

/**
 *
 * @author GabrielSin
 */
public class UseCashItemOperation {

    public static void UseMyoMyo(Player p) {
        if (p.getEventInstance() != null || !p.isAlive() || p.getPlayerShop() != null || p.getTrade() != null) {
            p.dropMessage(5, "You can not use this here.");
            p.announce(PacketCreator.EnableActions());
            return;
        }
        if ((p.getMapId() >= 680000210 && p.getMapId() <= 680000502) || (p.getMapId() / 1000 == 980000 && p.getMapId() != 980000000) || (p.getMapId() / 100 == 1030008) || (p.getMapId() / 100 == 922010) || (p.getMapId() / 10 == 13003000)) {
            p.dropMessage(5, "You can not use this here.");
            p.announce(PacketCreator.EnableActions());
            return;
        }
        if (p.getShop() == null) {
            Shop shop = ShopFactory.getInstance().getShop(NPCConstants.MYOMYO_SHOP);
            if (shop != null) {
                shop.sendShop(p.getClient());
            }
        } else {
            p.announce(PacketCreator.EnableActions());
        }
    }

    public static void UseAPandSP(InPacket packet, Player p, int itemId, short slot) {
        Boolean 使用 = true;
        if (itemId > 2180000) {
            //增加
            int SPTo = packet.readInt();
            //减少
            int SPFrom = packet.readInt();
            PlayerSkill skillSPTo = PlayerSkillFactory.getSkill(SPTo);
            PlayerSkill skillSPFrom = PlayerSkillFactory.getSkill(SPFrom);
            //被加的技能
            int curLevel = p.getSkillLevel(skillSPTo);
            //减少的技能
            int curLevelSPFrom = p.getSkillLevel(skillSPFrom);
            if (一转技能附魔(SPFrom) || 二转技能附魔20(SPFrom)) {
                if (curLevelSPFrom > 20) {
                    p.dropMessage(1, "不允许的操作，无法清洗被附魔的技能等级。");
                    return;
                }
            } else if (一转技能附魔(SPTo) || 二转技能附魔20(SPTo)) {
                if (curLevel >= 20) {
                    p.dropMessage(1, "不允许的操作，无法清洗被附魔的技能等级。");
                    return;
                }
            } else if (二转技能附魔30(SPFrom)) {
                if (curLevelSPFrom > 30) {
                    p.dropMessage(1, "不允许的操作，无法清洗被附魔的技能等级。");
                    return;
                }
            } else if (二转技能附魔30(SPTo)) {
                if (curLevel >= 30) {
                    p.dropMessage(1, "不允许的操作，无法清洗被附魔的技能等级。");
                    return;
                }
            } else if (三转技能附魔30(SPFrom)) {
                if (curLevelSPFrom > 30) {
                    p.dropMessage(1, "不允许的操作，无法清洗被附魔的技能等级。");
                    return;
                }
            } else if (三转技能附魔30(SPTo)) {
                if (curLevel >= 30) {
                    p.dropMessage(1, "不允许的操作，无法清洗被附魔的技能等级。");
                    return;
                }
            }

            if ((curLevel < skillSPTo.getMaxLevel()) && curLevelSPFrom > 0) {
                p.changeSkillLevel(skillSPFrom, curLevelSPFrom - 1, p.getMasterLevel(skillSPFrom));
                p.changeSkillLevel(skillSPTo, curLevel + 1, p.getMasterLevel(skillSPTo));
            }
        } else {
            int mask = 0;
            int APTo = packet.readInt();
            int APFrom = packet.readInt();

            if (APTo == STATS_HP || APTo == STATS_MP || APFrom == STATS_HP || APFrom == STATS_MP) {
                使用 = false;
                p.dropMessage(1, "不允许的操作。无法操作血量和魔力值的清洗。");
                return;
            }

            switch (APFrom) {
                case STATS_STR:
                    if (p.getStat().getStr() < 5) {
                        return;
                    }
                    p.getStat().addStat(1, -1);
                    break;
                case STATS_DEX:
                    if (p.getStat().getDex() < 5) {
                        return;
                    }
                    p.getStat().addStat(2, -1);
                    break;
                case STATS_INT:
                    if (p.getStat().getInt() < 5) {
                        return;
                    }
                    p.getStat().addStat(3, -1);
                    break;
                case STATS_LUK:
                    if (p.getStat().getLuk() < 5) {
                        return;
                    }
                    p.getStat().addStat(4, -1);
                    break;
                case STATS_HP:
                    int maxHP = p.getStat().getMaxHp();
                    if (p.getJob().isA(PlayerJob.BEGINNER)) {
                        maxHP -= 12;
                    } else if (p.getJob().isA(PlayerJob.WARRIOR)) {
                        PlayerSkill improvingMaxHP = PlayerSkillFactory.getSkill(1000001);
                        int improvingMaxHPLevel = p.getSkillLevel(improvingMaxHP);
                        maxHP -= 24;
                        if (improvingMaxHPLevel >= 1) {
                            maxHP -= improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                        }
                    } else if (p.getJob().isA(PlayerJob.MAGICIAN)) {
                        maxHP -= 10;
                    } else if (p.getJob().isA(PlayerJob.BOWMAN)) {
                        maxHP -= 20;
                    } else if (p.getJob().isA(PlayerJob.THIEF)) {
                        maxHP -= 20;
                    } else if (p.getJob().isA(PlayerJob.PIRATE)) {
                        PlayerSkill improvingMaxHP = PlayerSkillFactory.getSkill(5100000);
                        int improvingMaxHPLevel = p.getSkillLevel(improvingMaxHP);
                        maxHP -= 20;
                        if (improvingMaxHPLevel >= 1) {
                            maxHP -= improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                        }
                    }
                    if (maxHP < p.getLevel() * 2 + 148) {
                        return;
                    }
                    p.getStat().setHp(maxHP);
                    p.getStat().setMaxHp(maxHP);
                    p.getStat().setHpApUsed(p.getStat().getHpApUsed() - 1);
                    mask |= PlayerStat.HP.getValue();
                    mask |= PlayerStat.MAXHP.getValue();
                    break;
                case STATS_MP:
                    int maxMP = p.getStat().getMaxMp();
                    if (p.getJob().isA(PlayerJob.BEGINNER)) {
                        maxMP -= 8;
                    } else if (p.getJob().isA(PlayerJob.WARRIOR)) {
                        maxMP -= 4;
                    } else if (p.getJob().isA(PlayerJob.MAGICIAN)) {
                        PlayerSkill improvingMaxMP = PlayerSkillFactory.getSkill(2000001);
                        int improvingMaxMPLevel = p.getSkillLevel(improvingMaxMP);
                        maxMP -= 20;
                        if (improvingMaxMPLevel >= 1) {
                            maxMP -= improvingMaxMP.getEffect(improvingMaxMPLevel).getY();
                        }
                    } else if (p.getJob().isA(PlayerJob.BOWMAN)) {
                        maxMP -= 12;
                    } else if (p.getJob().isA(PlayerJob.THIEF)) {
                        maxMP -= 12;
                    } else if (p.getJob().isA(PlayerJob.PIRATE)) {
                        maxMP -= 16;
                    }
                    if (maxMP < ((p.getLevel() * 2) + 148)) {
                        break;
                    }
                    p.getStat().setMp(maxMP);
                    p.getStat().setMaxMp(maxMP);
                    p.getStat().setMpApUsed(p.getStat().getMpApUsed() - 1);
                    mask |= PlayerStat.MP.getValue();
                    mask |= PlayerStat.MAXMP.getValue();
                    break;
                default:
                    p.announce(PacketCreator.EnableActions());
                    return;
            }
            switch (APTo) {
                case STATS_STR:
                    if (p.getStat().getStr() >= GameConstants.MAX_STATS) {
                        return;
                    }
                    p.getStat().addStat(1, 1);
                    break;
                case STATS_DEX:
                    if (p.getStat().getDex() >= GameConstants.MAX_STATS) {
                        return;
                    }
                    p.getStat().addStat(2, 1);
                    break;
                case STATS_INT:
                    if (p.getStat().getInt() >= GameConstants.MAX_STATS) {
                        return;
                    }
                    p.getStat().addStat(3, 1);
                    break;
                case STATS_LUK:
                    if (p.getStat().getLuk() >= GameConstants.MAX_STATS) {
                        return;
                    }
                    p.getStat().addStat(4, 1);
                    break;
                case STATS_HP:
                    StatsHandling.addHP(p);
                    break;
                case STATS_MP:
                    StatsHandling.addMP(p);
                    break;
                default:
                    p.announce(PacketCreator.EnableActions());
                    return;
            }
            p.announce(PacketCreator.UpdatePlayerStats(p, mask));
        }
        if (使用) {
            InventoryManipulator.removeFromSlot(p.getClient(), InventoryType.USE, slot, (short) 1, false);
        }
    }

    public static void UseUndefined(InPacket packet, Player p, int itemId, short slot) {
        switch (itemId % 10) {
            case ITEM_TAG: {
                int equipSlot = packet.readShort();
                if (equipSlot == 0) {
                    break;
                }
                Item eq = p.getInventory(InventoryType.EQUIPPED).getItem((byte) equipSlot);
                eq.setOwner(p.getName());
                InventoryManipulator.removeFromSlot(p.getClient(), InventoryType.USE, slot, (short) 1, false);
                break;
            }
            case ITEM_SEALING_LOCK: {
                byte type = (byte) packet.readInt();
                if (type == 2) {
                    break;
                }
                byte slot_ = (byte) packet.readInt();
                Item eq = p.getInventory(InventoryType.getByType(type)).getItem(slot_);
                Equip equip = (Equip) eq;
                equip.setLocked((byte) 1);
                InventoryManipulator.removeFromSlot(p.getClient(), InventoryType.CASH, slot, (short) 1, false);
                break;
            }
            case INCUBATOR: {
                byte inventory = (byte) packet.readInt();
                byte slot_ = (byte) packet.readInt();
                Item item = p.getInventory(InventoryType.getByType(inventory)).getItem(slot_);
                if (item == null) {
                    return;
                }
                if (UseIncubator(p.getClient(), item.getItemId())) {
                    InventoryManipulator.removeFromSlot(p.getClient(), InventoryType.getByType(inventory), slot_, (short) 1, false);
                }
                break;
            }
            default:
                break;
        }
    }

    public static void UseMegaphoneItem(InPacket packet, Player p, int itemId, short slot, Item item) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        boolean sendWhisper;
        switch (itemId) {
            case 2081000:
                if (p.getLevel() >= 10) {
                    p.getMap().broadcastMessage(PacketCreator.ServerNotice(2, p.getName() + " : " + packet.readMapleAsciiString()));
                    InventoryManipulator.removeFromSlot(p.getClient(), InventoryType.USE, slot, (short) 1, false);
                } else {
                    p.dropMessage("低于10级无法使用!");
                }
                break;
            case 2082000:
                String x = "每日任务_高质地喇叭";
                if (p.getbosslog(x) <= 0) {
                    p.setbosslog(x);
                }
                BroadcastService.broadcastSmega(p.getWorldId(), PacketCreator.ServerNotice(3, p.getClient().getChannel(), p.getName() + " : " + packet.readMapleAsciiString(), (packet.readByte() != 0)));
                InventoryManipulator.removeFromSlot(p.getClient(), InventoryType.USE, slot, (short) 1, false);
                break;
        }
    }

    public static void UseMegaAvatar(InPacket packet, Player p, int itemId, short slot) {
        List<String> lines = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            lines.add(packet.readMapleAsciiString());
        }
        BroadcastService.broadcastSmega(p.getWorldId(), PacketCreator.GetAvatarMega(p, p.getClient().getChannel(), itemId, lines, (packet.readByte() != 0)));
        InventoryManipulator.removeFromSlot(p.getClient(), InventoryType.USE, slot, (short) 1, false);
    }

    public static void UseBagMeso(Player p, int itemId, short slot) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        if (ii.getMeso(itemId) + p.getMeso() < Integer.MAX_VALUE) {
            p.gainMeso(ii.getMeso(itemId), true, false, true);
            InventoryManipulator.removeFromSlot(p.getClient(), InventoryType.USE, slot, (short) 1, false);
            p.announce(PacketCreator.EnableActions());
        } else {
            p.dropMessage(1, "You can not have more mesos.");
        }
    }

    public static void UseJukeBox(Player p, short slot) {
        p.getMap().broadcastMessage(p, EffectPackets.MusicChange("Jukebox/Congratulation"), true);
        InventoryManipulator.removeFromSlot(p.getClient(), InventoryType.USE, slot, (short) 1, false);
    }

    public static void UseMapEffectItem(InPacket packet, Player p, int itemId, short slot) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        if (ii.getStateChangeItem(itemId) != 0) {
            for (Player mChar : p.getMap().getCharactersThreadsafe()) {
                ii.getItemEffect(ii.getStateChangeItem(itemId)).applyTo(mChar);
            }
        }
        //String msg = p.getName() + " " + ii.getName(itemId) + " : " + packet.readMapleAsciiString();
        p.getMap().startMapEffect(packet.readMapleAsciiString(), itemId);
        InventoryManipulator.removeFromSlot(p.getClient(), InventoryType.USE, slot, (short) 1, false);
    }

    public static void UseLoveItem(InPacket packet, Player p, int itemid, short slot) {
        MapleLove love = new MapleLove(itemid, packet.readMapleAsciiString(), p);
        love.setPosition(p.getPosition());
        p.getMap().spawnLove(love);
        InventoryManipulator.removeFromSlot(p.getClient(), InventoryType.USE, slot, (short) 1, false);
    }

    public static void UsePassedGas(Player p, int itemId, short slot) {
        if (itemId == ItemConstants.PASSED_GAS) {
            Rectangle bounds = new Rectangle((int) p.getPosition().getX(), (int) p.getPosition().getY(), 1, 1);
            MapleStatEffect mse = new MapleStatEffect();
            mse.setSourceId(SkillConstants.FPMage.PoisonMist);
            MapleMist mist = new MapleMist(bounds, p, mse);
            p.getMap().spawnMist(mist, 10000, false, true);
            p.getMap().broadcastMessage(PacketCreator.GetChatText(p.getId(), "Oh no, I farted!", false));
            InventoryManipulator.removeFromSlot(p.getClient(), InventoryType.CASH, slot, (short) 1, false);
        }
    }

    public static void UseSendNote(InPacket packet, Player p, short slot) {
        PlayerNote.sendNote(p, packet.readMapleAsciiString(), packet.readMapleAsciiString());
        InventoryManipulator.removeFromSlot(p.getClient(), InventoryType.USE, slot, (short) 1, false);
    }

    public static void UsePetFood(Player p, int itemId, short slot) {
        ItemPet pet = p.getPet(0);
        if (pet == null) {
            return;
        }
        if (!pet.canConsume(itemId)) {
            pet = p.getPet(1);
            if (pet != null) {
                if (!pet.canConsume(itemId)) {
                    pet = p.getPet(2);
                    if (pet != null) {
                        if (!pet.canConsume(itemId)) {
                            return;
                        }
                    } else {
                        return;
                    }
                }
            } else {
                return;
            }
        }
        final byte petindex = p.getPetIndex(pet);
        pet.setFullness(100);
        if (pet.getCloseness() < 30000) {
            if (pet.getCloseness() + 100 > 30000) {
                pet.setCloseness(30000);
            } else {
                pet.setCloseness(pet.getCloseness() + 100);
            }
            if (pet.getCloseness() >= ExperienceConstants.getClosenessNeededForLevel(pet.getLevel() + 1)) {
                pet.setLevel(pet.getLevel() + 1);
                p.announce(PetPackets.ShowOwnPetLevelUp(p.getPetIndex(pet)));
                p.getMap().broadcastMessage(PetPackets.ShowPetLevelUp(p, petindex));
            }
        }
        p.announce(PetPackets.updatePet(pet, true));
        p.getMap().broadcastMessage(p, PetPackets.CommandPetResponse(p.getId(), (byte) 1, petindex, true, true), true);
        InventoryManipulator.removeFromSlot(p.getClient(), InventoryType.USE, slot, (short) 1, false);
    }

    public static void UsePetNameChange(InPacket packet, Player p, short slot) {
        ItemPet pet = p.getPet(0);
        if (pet == null) {
            return;
        }
        //0F 00 30 32 20 00 08 00 B0 A1 B0 A1 B0 A1 B0 A1 
        String nName = packet.readMapleAsciiString();
        for (String z : PlayerStringUtil.RESERVED) {
            if (pet.getName().contains(z) || nName.contains(z)) {
                break;
            }
        }
        if (PlayerStringUtil.canChangePetName(nName)) {
            pet.setName(nName);
            p.announce(PetPackets.updatePet(pet, true));
            p.announce(PacketCreator.EnableActions());
            p.getMap().broadcastMessage(PetPackets.ChangePetName(p, nName));
            InventoryManipulator.removeFromSlot(p.getClient(), InventoryType.USE, slot, (short) 1, false);
        }
    }

    public static void UseShopScanner(InPacket packet, Player p, short slot) {
        if (p.getMapId() / 10000000 != 91) {
            p.announce(PacketCreator.EnableActions());
            return;
        }

        int itemWanted = packet.readInt();

        p.getClient().getChannelServer().checkSearchedItems(itemWanted);
        p.setOwlSearch(itemWanted);

        List<Pair<PlayerShopItem, AbstractMapleFieldObject>> hmsAvailable = p.getClient().getChannelServer().getAvailableItemBundles(itemWanted);
        if (!hmsAvailable.isEmpty()) {
            InventoryManipulator.removeFromSlot(p.getClient(), InventoryType.CASH, slot, (short) 1, false);
        }

        p.announce(InteractionPackets.ShopScannerResult(p.getClient(), false, itemWanted, hmsAvailable, null));
        p.announce(PacketCreator.EnableActions());
    }

    public static void UseChalkBoard(InPacket packet, Player p) {
        p.setChalkboard(packet.readMapleAsciiString());
        p.getMap().broadcastMessage(PacketCreator.UseChalkBoard(p, false));
        p.announce(PacketCreator.EnableActions());
    }

    public static void UseItemEffect(Player p, int itemId, short slot) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        ii.getItemEffect(itemId).applyTo(p);
        InventoryManipulator.removeFromSlot(p.getClient(), InventoryType.USE, slot, (short) 1, false);
    }

    public static void UseVipTeleport(InPacket packet, Player p, int itemId, short slot) {
        //2B 19 00 90 1C 21 00 00 40 86 33 06
        if (packet.readByte() == 0) {
            final Field target = p.getClient().getChannelServer().getMapFactory().getMap(packet.readInt());
            if (itemId == 2170000 && p.isRegRockMap(target.getId())) {
                p.changeMap(target, target.getPortal(0));
                InventoryManipulator.removeFromSlot(p.getClient(), InventoryType.USE, slot, (short) 1, false);
            }
        } else {
            final Player victim = p.getClient().getChannelServer().getPlayerStorage().getCharacterByName(packet.readMapleAsciiString());
            if (victim != null && !victim.isGameMaster() && p.getEventInstance() == null && victim.getEventInstance() == null) {
                if (!FieldLimit.CANNOTVIPROCK.check(p.getMap().getFieldLimit()) && !FieldLimit.CANNOTVIPROCK.check(p.getClient().getChannelServer().getMapFactory().getMap(victim.getMapId()).getFieldLimit())) {
                    if (itemId == 2170000 || (victim.getMapId() / 100000000) == (p.getMapId() / 100000000)) {
                        p.changeMap(victim.getMap(), victim.getMap().findClosestPortal(victim.getPosition()));
                        InventoryManipulator.removeFromSlot(p.getClient(), InventoryType.USE, slot, (short) 1, false);
                    }
                }
            }
        }
    }
}