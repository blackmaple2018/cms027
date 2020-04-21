package server;

import server.itens.InventoryManipulator;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import client.player.Player;
import client.player.PlayerJob;
import client.player.PlayerStat;
import client.player.buffs.BuffStat;
import client.player.buffs.Disease;
import client.player.PlayerEffects;
import client.player.PlayerStatsManager;
import client.player.inventory.Inventory;
import client.player.inventory.types.InventoryType;
import client.player.inventory.Item;
import client.player.inventory.TamingMob;
import client.player.skills.PlayerSkill;
import client.player.skills.PlayerSkillFactory;
import community.MaplePartyCharacter;
import server.life.status.MonsterStatus;
import server.life.status.MonsterStatusEffect;
import constants.ItemConstants;
import constants.MapConstants;
import constants.SkillConstants.*;
import handling.channel.ChannelServer;
import provider.MapleData;
import provider.MapleDataTool;
import server.life.MapleMonster;
import server.maps.MapleDoor;
import server.maps.Field;
import server.maps.object.FieldObject;
import server.maps.object.FieldObjectType;
import server.maps.MapleMist;
import server.maps.MapleSummon;
import handling.world.PlayerCoolDownValueHolder;
import packet.creators.EffectPackets;
import packet.creators.PacketCreator;
import tools.TimerTools.SkillTimer;
import server.maps.FieldLimit;
import server.maps.SummonMovementType;
import server.maps.portal.Portal;
import tools.ArrayMap;
import tools.Pair;

public class MapleStatEffect implements Serializable {

    static final long serialVersionUID = 9179541993413738569L;
    private short watk, matk, wdef, mdef, acc, avoid, hands, speed, jump;
    private short hp, mp;
    private double hpR, mpR;
    private short mpCon, hpCon;
    private int duration;
    private boolean overTime;
    private int sourceid;
    private int moveTo;
    private boolean skill;
    private List<Pair<BuffStat, Integer>> statups;
    private Map<MonsterStatus, Integer> monsterStatus;
    private int x, y, z;
    private double prop;
    private int itemCon, itemConNo;
    private int damage, attackCount, bulletCount, bulletConsume;
    private Point lt, rb;
    private int mobCount;
    private int moneyCon;
    private int cooldown;
    private boolean isMorph = false;
    private int morphId = 0, shield = 0;
    private List<Disease> cureDebuffs;
    private int mastery, range, fixDamage;
    private byte level;
    private boolean consumeOnPickup, party;
    private int cp, nuffSkill;

    public static MapleStatEffect loadSkillEffectFromData(MapleData source, int skillid, boolean overtime, byte level) {
        return loadFromData(source, skillid, true, overtime, level);
    }

    public static MapleStatEffect loadItemEffectFromData(MapleData source, int itemid) {
        return loadFromData(source, itemid, false, false, (byte) 1);
    }

    private static void addBuffStatPairToListIfNotZero(List<Pair<BuffStat, Integer>> list, BuffStat buffstat, Integer val) {
        if (val != 0) {
            list.add(new Pair<>(buffstat, val));
        }
    }

    private static MapleStatEffect loadFromData(MapleData source, int sourceid, boolean skill, boolean overTime, final byte level) {
        MapleStatEffect ret = new MapleStatEffect();
        ret.level = level;
        ret.duration = MapleDataTool.getIntConvert("time", source, -1);
        ret.hp = (short) MapleDataTool.getInt("hp", source, 0);
        ret.hpR = MapleDataTool.getInt("hpR", source, 0) / 100.0;
        ret.mp = (short) MapleDataTool.getInt("mp", source, 0);
        ret.mpR = MapleDataTool.getInt("mpR", source, 0) / 100.0;
        ret.mpCon = (short) MapleDataTool.getInt("mpCon", source, 0);
        ret.hpCon = (short) MapleDataTool.getInt("hpCon", source, 0);
        int iprop = MapleDataTool.getInt("prop", source, 100);
        ret.prop = iprop / 100.0;
        ret.mobCount = MapleDataTool.getInt("mobCount", source, 1);
        ret.cooldown = MapleDataTool.getInt("cooltime", source, 0);
        ret.morphId = MapleDataTool.getInt("morph", source, 0);
        ret.fixDamage = MapleDataTool.getInt("fixdamage", source, -1);
        ret.isMorph = ret.morphId > 0 ? true : false;
        ret.shield = MapleDataTool.getInt("barrier", source, 0);

        ret.sourceid = sourceid;
        ret.skill = skill;

        if (!ret.skill && ret.duration > -1) {
            ret.overTime = true;
        } else {
            ret.duration *= 1000;
            ret.overTime = overTime || ret.isMorph() || ret.isPirateMorph();
        }
        ArrayList<Pair<BuffStat, Integer>> statups = new ArrayList<>();

        ret.watk = (short) MapleDataTool.getInt("pad", source, 0);
        ret.wdef = (short) MapleDataTool.getInt("pdd", source, 0);
        ret.matk = (short) MapleDataTool.getInt("mad", source, 0);
        ret.mdef = (short) MapleDataTool.getInt("mdd", source, 0);
        ret.acc = (short) MapleDataTool.getIntConvert("acc", source, 0);
        ret.avoid = (short) MapleDataTool.getInt("eva", source, 0);
        ret.speed = (short) MapleDataTool.getInt("speed", source, 0);
        ret.jump = (short) MapleDataTool.getInt("jump", source, 0);

        if (ret.overTime && ret.getSummonMovementType() == null) {
            addBuffStatPairToListIfNotZero(statups, BuffStat.WeaponAttack, Integer.valueOf(ret.watk));
            addBuffStatPairToListIfNotZero(statups, BuffStat.WeaponDefense, Integer.valueOf(ret.wdef));
            addBuffStatPairToListIfNotZero(statups, BuffStat.MagicAttack, Integer.valueOf(ret.matk));
            addBuffStatPairToListIfNotZero(statups, BuffStat.MagicDefense, Integer.valueOf(ret.mdef));
            addBuffStatPairToListIfNotZero(statups, BuffStat.Accurancy, Integer.valueOf(ret.acc));
            addBuffStatPairToListIfNotZero(statups, BuffStat.Avoidability, Integer.valueOf(ret.avoid));
            addBuffStatPairToListIfNotZero(statups, BuffStat.Speed, Integer.valueOf(ret.speed));
            addBuffStatPairToListIfNotZero(statups, BuffStat.Jump, Integer.valueOf(ret.jump));
            //addBuffStatPairToListIfNotZero(statups, BuffStat.SHIELD, Integer.valueOf(ret.shield));
        }

        MapleData ltd = source.getChildByPath("lt");
        if (ltd != null) {
            ret.lt = (Point) ltd.getData();
            ret.rb = (Point) source.getChildByPath("rb").getData();
        }

        int x = MapleDataTool.getInt("x", source, 0);
        if (sourceid == Beginner.Recovery) {
            x *= 10;
        }
        ret.x = x;
        ret.y = MapleDataTool.getInt("y", source, 0);
        ret.z = MapleDataTool.getInt("z", source, 0);
        ret.damage = MapleDataTool.getIntConvert("damage", source, 100);
        ret.attackCount = MapleDataTool.getIntConvert("attackCount", source, 1);
        ret.bulletCount = MapleDataTool.getIntConvert("bulletCount", source, 1);
        ret.bulletConsume = MapleDataTool.getIntConvert("bulletConsume", source, 0);
        ret.moneyCon = MapleDataTool.getIntConvert("moneyCon", source, 0);
        ret.mastery = MapleDataTool.getIntConvert("mastery", source, 0);
        ret.range = MapleDataTool.getIntConvert("range", source, 0);

        ret.itemCon = MapleDataTool.getInt("itemCon", source, 0);
        ret.itemConNo = MapleDataTool.getInt("itemConNo", source, 0);
        ret.moveTo = MapleDataTool.getInt("moveTo", source, -1);

        ret.cp = MapleDataTool.getInt("cp", source, 0);
        ret.party = MapleDataTool.getInt("party", source, 0) > 0;
        ret.consumeOnPickup = MapleDataTool.getInt("consumeOnPickup", source, 0) > 0;
        ret.nuffSkill = MapleDataTool.getInt("nuffSkill", source, -1);

        List<Disease> localCureDebuffs = new ArrayList<>();
        if (MapleDataTool.getInt("poison", source, 0) > 0) {
            localCureDebuffs.add(Disease.POISON);
        }
        if (MapleDataTool.getInt("seal", source, 0) > 0) {
            localCureDebuffs.add(Disease.SEAL);
        }
        if (MapleDataTool.getInt("darkness", source, 0) > 0) {
            localCureDebuffs.add(Disease.DARKNESS);
        }
        if (MapleDataTool.getInt("weakness", source, 0) > 0) {
            localCureDebuffs.add(Disease.WEAKEN);
        }
        if (MapleDataTool.getInt("curse", source, 0) > 0) {
            localCureDebuffs.add(Disease.CURSE);
        }
        ret.cureDebuffs = localCureDebuffs;

        Map<MonsterStatus, Integer> monsterStatus = new ArrayMap<>();

        if (skill) {
            switch (sourceid) {
                case Beginner.Recovery:
                    //statups.add(new Pair<>(BuffStat.RECOVERY, Integer.valueOf(x)));
                    break;
                case Magician.MagicGuard:
                    statups.add(new Pair<>(BuffStat.MagicGuard, Integer.valueOf(x)));
                    break;
                case Cleric.Invincible:
                    statups.add(new Pair<>(BuffStat.Invincible, Integer.valueOf(x)));
                    break;
                case SuperGm.Hide:
                    ret.duration = 2100000000;
                    ret.overTime = true;
                case Rogue.DarkSight:
                case Gm.Hide:
                    statups.add(new Pair<>(BuffStat.DarkSight, Integer.valueOf(x)));
                    break;
                case ChiefBandit.Pickpocket:
                    statups.add(new Pair<>(BuffStat.PickPocketMesoUP, Integer.valueOf(x)));
                    break;
                case ChiefBandit.MesoGuard:
                    statups.add(new Pair<>(BuffStat.MesoGuard, x));
                    ret.duration = 2100000000;
                    break;
                case Hermit.MesoUp:
                    statups.add(new Pair<>(BuffStat.MesoUP, x));
                    break;
                case Hermit.ShadowPartner:
                    statups.add(new Pair<>(BuffStat.ShadowPartner, Integer.valueOf(x)));
                    break;
                case Hunter.SoulArrow:
                case Crossbowman.SoulArrow:
                case Priest.MysticDoor:
                    statups.add(new Pair<>(BuffStat.SoulArrow, Integer.valueOf(x)));
                    break;
                case WhiteKnight.SwordFireCharge:
                case WhiteKnight.BwFireCharge:
                case WhiteKnight.SwordIceCharge:
                case WhiteKnight.BwIceCharge:
                case WhiteKnight.SwordLitCharge:
                case WhiteKnight.BwLitCharge:
                case Paladin.SwordHolyCharge:
                case Paladin.BwHolyCharge:
                    statups.add(new Pair<>(BuffStat.Charges, Integer.valueOf(x)));
                    break;
                case Fighter.SwordBooster:
                case Fighter.AxeBooster:
                case Page.SwordBooster:
                case Page.BwBooster:
                case SpearMan.SpearBooster:
                case SpearMan.PolearmBooster:
                case FPMage.SpellBooster:
                case ILMage.SpellBooster:
                case Hunter.BowBooster:
                case Crossbowman.CrossbowBooster:
                case Assassin.ClawBooster:
                case Bandit.DaggerBooster:
                    statups.add(new Pair<>(BuffStat.Booster, x));
                    break;
                case Buccaneer.SpeedInfusion:
                case Corsair.HerosWill:
                    //statups.add(new Pair<>(BuffStat.SPEED_INFUSION, Integer.valueOf(-4)));
                    break;
                case Fighter.Rage:
                    statups.add(new Pair<>(BuffStat.WeaponDefense, Integer.valueOf(ret.wdef)));
                case Hero.Enrage:
                    statups.add(new Pair<>(BuffStat.WeaponAttack, Integer.valueOf(ret.watk)));
                    break;
                case SpearMan.IronWill:
                    statups.add(new Pair<>(BuffStat.MagicDefense, Integer.valueOf(ret.mdef)));
                case 1001003: // iron body
                    statups.add(new Pair<>(BuffStat.WeaponDefense, Integer.valueOf(ret.wdef)));
                    break;
                case Magician.MagicArmor:
                    statups.add(new Pair<>(BuffStat.WeaponDefense, Integer.valueOf(ret.wdef)));
                    break;
                case FPWizard.Meditation:
                case ILWizard.Meditation:
                    statups.add(new Pair<>(BuffStat.MagicAttack, Integer.valueOf(ret.matk)));
                    break;
                case Assassin.Haste:
                case Bandit.Haste:
                case SuperGm.Haste:
                    statups.add(new Pair<>(BuffStat.Speed, Integer.valueOf(ret.speed)));
                    statups.add(new Pair<>(BuffStat.Jump, Integer.valueOf(ret.jump)));
                    break;
                case Cleric.Bless:
                    statups.add(new Pair<>(BuffStat.WeaponDefense, Integer.valueOf(ret.wdef)));
                    statups.add(new Pair<>(BuffStat.MagicDefense, Integer.valueOf(ret.mdef)));
                case Archer.Focus:
                    statups.add(new Pair<>(BuffStat.Accurancy, Integer.valueOf(ret.acc)));
                    statups.add(new Pair<>(BuffStat.Avoidability, Integer.valueOf(ret.avoid)));
                    break;
                case SuperGm.Bless:
                    statups.add(new Pair<>(BuffStat.MagicAttack, Integer.valueOf(ret.matk)));
                case Bowmaster.Concentrate:
                    statups.add(new Pair<>(BuffStat.WeaponAttack, Integer.valueOf(ret.watk)));
                    break;
                case Pirate.Dash:
                    //statups.add(new Pair<>(BuffStat.DASH, Integer.valueOf(1)));
                    break;
                case Fighter.PowerGuard:
                case Page.PowerGuard:
                    statups.add(new Pair<>(BuffStat.PowerGuard, x));
                    break;
                //神圣之火
                case SpearMan.HyperBody:
                case SuperGm.HyperBody:
                    statups.add(new Pair<>(BuffStat.MaxHP, ret.x));
                    statups.add(new Pair<>(BuffStat.MaxMP, ret.y));
                    break;
                case Crusader.斗气集中:
                    statups.add(new Pair<>(BuffStat.ComboAttack, 1));
                    break;
                case Beginner.MonsterRider:
                    //statups.add(new Pair<>(BuffStat.MONSTER_RIDING, Integer.valueOf(1)));
                    break;
                case 5221006: // 4th Job - Pirate riding 
                    //statups.add(new Pair<BuffStat, Integer>(BuffStat.MONSTER_RIDING, 1932000));
                    break;
//                case Corsair.Battleship:
//                    
//                    statups.add(new Pair<>(BuffStat.MONSTER_RIDING, Integer.valueOf(1)));
//                    break;
                case DragonKnight.DragonRoar:
                    ret.hpR = -x / 100.0;
                    break;
                case DragonKnight.DragonBlood:
                    statups.add(new Pair<>(BuffStat.DragonBlood, ret.x));
                    break;
                case Hero.MapleWarrior:
                case Paladin.MapleWarrior:
                case DarkKnight.MapleWarrior:
                case FPArchMage.MapleWarrior:
                case ILArchMage.MapleWarrior:
                case Bishop.MapleWarrior:
                case Bowmaster.MapleWarrior:
                case Marksman.MapleWarrior:
                case NightLord.MapleWarrior:
                case Shadower.MapleWarrior:
                case Buccaneer.MapleWarrior:
                case Corsair.MapleWarrior:
                    //statups.add(new Pair<>(BuffStat.MAPLE_WARRIOR, Integer.valueOf(ret.x)));
                    break;
                case Bowmaster.SharpEyes:
                case Marksman.SharpEyes:
                    //statups.add(new Pair<>(BuffStat.SHARP_EYES, Integer.valueOf(ret.x << 8 | ret.y)));
                    break;
                case DarkKnight.Beholder:
                case ILArchMage.Ifrit:
                case Priest.SummonDragon:
                case Bishop.Bahamut:
                case Bowmaster.Phoenix:
                case Outlaw.Octopus:
                case Outlaw.Gaviota:
                case Corsair.WrathOfTheOctopi:
                    statups.add(new Pair<>(BuffStat.Summon, 1));
                    break;
                case Priest.HolySymbol:
                case SuperGm.HolySymbol:
                    statups.add(new Pair<>(BuffStat.HolySymbol, x));
                    break;
                case Ranger.Puppet:
                case Sniper.Puppet:
                    statups.add(new Pair<>(BuffStat.Puppet, 1));
                    break;

                // ----------------------------- MONSTER STATUS PUT! ----------------------------- //
                case Rogue.Disorder:
                    monsterStatus.put(MonsterStatus.WATK, ret.x);
                    monsterStatus.put(MonsterStatus.WDEF, ret.y);
                    break;
                case Page.Threaten:
                    monsterStatus.put(MonsterStatus.WATK, ret.x);
                    monsterStatus.put(MonsterStatus.WDEF, ret.y);
                    break;
                case Crusader.SwordComa:
                case Crusader.气绝斧:
                case Crusader.Shout:
                case WhiteKnight.ChargeBlow:
                case Hunter.ArrowBomb:
                case ChiefBandit.Assaulter:
                case Shadower.BoomerangStep:
                case Brawler.BackspinBlow:
                //case Brawler.DoubleUppercut: 
                case Buccaneer.Demolition:
                case Buccaneer.Snatch:
                case Buccaneer.Barrage:
                case Gunslinger.BlankShot:
                    monsterStatus.put(MonsterStatus.STUN, 1);
                    break;
                case NightLord.Taunt:
                case Shadower.Taunt:
                    monsterStatus.put(MonsterStatus.MAGIC_DEFENSE_UP, 1);
                    break;
                case ILWizard.ColdBeam:
                case ILMage.IceStrike:
                case ILMage.ElementComposition:
                case ILArchMage.Blizzard:
                case Sniper.Blizzard:
                case Outlaw.IceSplitter:
                    monsterStatus.put(MonsterStatus.FREEZE, 1);
                    ret.duration *= 2;
                    break;
                case FPArchMage.Paralyze:
                case FPWizard.Slow:
                case ILWizard.Slow:
                    monsterStatus.put(MonsterStatus.SPEED, ret.x);
                    break;
                case FPWizard.PoisonBreath:
                case FPMage.ElementComposition:
                    monsterStatus.put(MonsterStatus.POISON, 1);
                    break;
                case Priest.Doom:
                    monsterStatus.put(MonsterStatus.DOOM, 1);
                    break;
                case Ranger.SilverHawk:
                case Sniper.GoldenEagle:
                    statups.add(new Pair<>(BuffStat.Summon, 1));
                    monsterStatus.put(MonsterStatus.STUN, 1);
                    break;
                case FPArchMage.Elquines:
                case Marksman.Frostprey:
                    statups.add(new Pair<>(BuffStat.Summon, 1));
                    monsterStatus.put(MonsterStatus.FREEZE, 1);
                    break;
                case FPMage.Seal:
                case ILMage.Seal:
                    monsterStatus.put(MonsterStatus.SEAL, 1);
                    break;
                case Hermit.ShadowWeb:
                    monsterStatus.put(MonsterStatus.SHADOW_WEB, 1);
                    break;
                case Bowmaster.Hamstring:
                    //statups.add(new Pair<>(BuffStat.HAMSTRING, Integer.valueOf(x)));
                    monsterStatus.put(MonsterStatus.SPEED, x);
                    break;
                case Marksman.Blind:
                    //statups.add(new Pair<>(BuffStat.BLIND, Integer.valueOf(x)));
                    monsterStatus.put(MonsterStatus.ACC, x);
                    break;
                default:
            }
        }

        if (ret.isShield()) {
            //statups.add(new Pair<>(BuffStat.SHIELD, Integer.valueOf(ret.shield)));
        }

        if (ret.isMorph()) {
            //statups.add(new Pair<>(BuffStat.MORPH, Integer.valueOf(ret.getMorph())));
        }
        ret.monsterStatus = monsterStatus;
        statups.trimToSize();
        ret.statups = statups;
        return ret;
    }

    /**
     * @param applyto
     * @param obj
     * @param attack damage done by the skill
     */
    public void applyPassive(Player applyto, FieldObject obj, int attack) {
        if (makeChanceResult()) {
            switch (sourceid) {
                case FPWizard.MpEater:
                case ILWizard.MpEater:
                case Cleric.MpEater:
                    if (obj == null || obj.getType() != FieldObjectType.MONSTER) {
                        return;
                    }
                    final MapleMonster mob = (MapleMonster) obj;
                    if (!mob.isBoss()) {
                        int absorbMp = Math.min((int) (mob.getMaxMp() * (getX() / 100.0)), mob.getMp());
                        if (absorbMp > 0) {
                            mob.setMp(mob.getMp() - absorbMp);
                            applyto.getStat().addMP(absorbMp);
                            applyto.getClient().write(EffectPackets.ShowOwnBuffEffect(sourceid, 1));
                            applyto.getMap().broadcastMessage(applyto, EffectPackets.BuffMapVisualEffect(applyto.getId(), sourceid, PlayerEffects.SKILL_USE.getEffect(), (byte) 3), false);
                        }
                    }
                    break;
            }
        }
    }

    public boolean applyTo(Player chr) {
        return applyTo(chr, chr, true, null, false);
    }

    public boolean applyTo(Player chr, boolean useMaxRange) {
        return applyTo(chr, chr, true, null, useMaxRange);
    }

    public boolean applyTo(Player chr, Point pos) {
        return applyTo(chr, chr, true, pos, false);
    }

    private boolean applyTo(Player applyfrom, Player applyto, boolean primary, Point pos, boolean useMaxRange) {
        //管理员隐身
        /*if (skill && (sourceid == Gm.Hide )) {
         applyto.toggleVisibility(false);
         return true;
         }*/
        int hpChange = calcHPChange(applyfrom, primary);
        int mpChange = calcMPChange(applyfrom, primary);

        if (primary) {
            if (itemConNo != 0) {
                if (!applyto.getClient().getAbstractPlayerInteraction().haveItem(itemCon, itemConNo)) {
                    applyto.getClient().announce(PacketCreator.EnableActions());
                    return false;
                }
                InventoryManipulator.removeById(applyto.getClient(), ItemConstants.getInventoryType(itemCon), itemCon, itemConNo, false, true);
            }
        } else if (!primary && isResurrection()) {
            hpChange = applyto.getStat().getMaxHp();
            applyto.setStance(0);
            applyto.getMap().broadcastMessage(applyto, PacketCreator.RemovePlayerFromMap(applyto.getId()), false);
            applyto.getMap().broadcastMessage(applyto, PacketCreator.SpawnPlayerMapObject(applyto), false);

            if (applyto.getChalkboard() != null) {
                applyto.getMap().broadcastMessage(applyto, (PacketCreator.UseChalkBoard(applyto, false)), false);
            }
        }

        if (cureDebuffs.size() > 0) {
            cureDebuffs.stream().forEach((debuff) -> {
                applyfrom.dispelDebuff(debuff);
            });
        }
        //给怪物上的技能
        if (skill) {
            removeMonsterBuff(applyfrom);
        }
        if (isDispel() && makeChanceResult()) {
            applyto.dispelDebuffs();
        }

        if (isHeroWill()) {
            applyto.dispelDebuff(Disease.SEDUCE);
            applyto.dispelDebuffs();
        }

        int mask = 0;
        if (hpChange != 0) {
            if (hpChange < 0 && (-hpChange) >= applyto.getHp()) {
                if (!applyto.isGameMaster()) {
                    applyto.getClient().announce(PacketCreator.EnableActions());
                    return false;
                }
            }
            int newHp = applyto.getHp() + hpChange;
            if (newHp < 1) {
                newHp = 1;
            }
            applyto.getStat().setHp(newHp);
            mask |= PlayerStat.HP.getValue();
        }
        if (mpChange != 0) {
            int newMp = applyto.getMp() + mpChange;
            if (mpChange < 0 && (-mpChange) > applyto.getMp()) {
                if (!applyto.isGameMaster()) {
                    applyto.getClient().announce(PacketCreator.EnableActions());
                    return false;
                } else {
                    newMp = 0;
                }
            }
            applyto.getStat().setMp(newMp);
            mask |= PlayerStat.MP.getValue();
        }
        if (mask != 0) {
            applyto.getClient().write(PacketCreator.UpdatePlayerStats(applyto, primary, mask));
        }
        if (moveTo != -1) {
            if (moveTo != applyto.getMapId()) {
                Field target;
                Portal pt;

                if (moveTo == MapConstants.NULL_MAP) {
                    target = applyto.getMap().getForcedReturnField();
                    pt = target.getRandomPlayerSpawnpoint();
                } else {
                    target = applyto.getChannelServer().getMapFactory().getMap(moveTo);
                    if (target.getId() / 10000000 != 60 && applyto.getMapId() / 10000000 != 61) {
                        if (target.getId() / 10000000 != 21 && applyto.getMapId() / 10000000 != 20) {
                            if (target.getId() / 10000000 != applyto.getMapId() / 10000000) {
                                return false;
                            }
                        }
                    }
                    pt = target.getRandomPlayerSpawnpoint();
                }
                applyto.changeMap(target, pt);
            } else {
                return false;
            }
        }
        if (isShadowClaw()) {
            int projectile = 0;
            Inventory use = applyto.getInventory(InventoryType.USE);
            for (int i = 1; i <= use.getSlotLimit(); i++) {
                Item item = use.getItem((byte) i);
                if (item != null) {
                    if (ItemConstants.isThrowingStar(item.getItemId()) && item.getQuantity() >= 200) {
                        projectile = item.getItemId();
                        break;
                    }
                }
            }
            if (projectile == 0) {
                return false;
            } else {
                InventoryManipulator.removeById(applyto.getClient(), InventoryType.USE, projectile, 200, false, true);
            }
        }
        SummonMovementType summonMovementType = getSummonMovementType();
        if (overTime || summonMovementType != null) {
            applyBuffEffect(applyfrom, applyto, primary);
        }

        if (primary && (overTime || isHeal())) {
            applyBuff(applyfrom, useMaxRange);
        }

        if (primary && isMonsterBuff()) {
            applyMonsterBuff(applyfrom);
        }

        if (summonMovementType != null && pos != null) {
            final MapleSummon tosummon = new MapleSummon(applyfrom, sourceid, pos, summonMovementType);
            applyfrom.getMap().spawnSummon(tosummon);
            applyfrom.addSummon(sourceid, tosummon);
            tosummon.addHP(x);
            if (isBeholder()) {
                tosummon.addHP(1);
            }
        }

        if (isMysticDoor() && !FieldLimit.DOOR.check(applyto.getMap().getFieldLimit())) {
            int y = applyto.getFh();
            if (y == 0) {
                y = applyto.getPosition().y;
            }
            Point doorPosition = new Point(applyto.getPosition().x, y);
            MapleDoor door = new MapleDoor(applyto, doorPosition);
            if (door.getOwnerId() >= 0) {
                if (applyto.getParty() != null) {
                    applyto.getParty().getMembers().stream().map((partyMember) -> {
                        partyMember.getPlayer().addDoor(door.getOwnerId(), door);
                        return partyMember;
                    }).forEach((partyMember) -> {
                        partyMember.addDoor(door.getOwnerId(), door);
                    });
                    applyto.silentPartyUpdate();
                } else {
                    applyto.addDoor(door.getOwnerId(), door);
                }
                door.getTarget().spawnDoor(door.getAreaDoor());
                door.getTown().spawnDoor(door.getTownDoor());
                applyto.disableDoor();
            } else {
                applyto.dropMessage(5, "传送门使用失败，返还材料。");
                InventoryManipulator.addFromDrop(applyto.getClient(), new Item(4006000, (byte) 0, (short) 1), "", true);
                switch (door.getOwnerId()) {
                    case -3:
                        applyto.dropMessage(5, "神秘之门不能在离传送点很远的地方施放。最近的一个在 " + door.getDoorStatus().getRight() + " pts " + door.getDoorStatus().getLeft());
                        break;
                    case -2:
                        applyto.dropMessage(5, "神秘之门不能放在斜坡上，去别处试试。");
                        break;
                    default:
                        applyto.dropMessage(5, "这个城镇目前没有门户。请稍后再试。");
                        break;
                }
                applyto.cancelBuffStats(BuffStat.SoulArrow);
            }
        }
        if (isMist()) {
            Rectangle bounds = calculateBoundingBox(applyfrom.getPosition(), applyfrom.isFacingLeft());
            MapleMist mist = new MapleMist(bounds, applyfrom, this);
            applyfrom.getMap().spawnMist(mist, getDuration(), mist.isPoisonMist(), false, mist.isRecoveryMist());
        }
        /*if (isTimeLeap()) {
            applyto.removeAllCooldownsExcept(Buccaneer.TimeLeap, true);
        }*/
        return true;
    }

    private void applyBuff(Player applyfrom, boolean useMaxRange) {
        if (applyfrom != null) {
            if (isPartyBuff() && (applyfrom.getParty() != null || isGameMasterBuff())) {
                Rectangle bounds = (!useMaxRange) ? calculateBoundingBox(applyfrom.getPosition(), applyfrom.isFacingLeft()) : new Rectangle(Integer.MIN_VALUE / 2, Integer.MIN_VALUE / 2, Integer.MAX_VALUE, Integer.MAX_VALUE);
                List<FieldObject> affecteds = applyfrom.getMap().getMapObjectsInBox(bounds, Arrays.asList(FieldObjectType.PLAYER));
                List<Player> affectedp = new ArrayList<>(affecteds.size());
                for (FieldObject affectedmo : affecteds) {
                    Player affected = (Player) affectedmo;
                    if (affected != null && isHeal() && affected != applyfrom && affected.getParty() == applyfrom.getParty() && affected.isAlive()) {
                        int expadd = (int) ((calcHPChange(applyfrom, true) / 10) * (applyfrom.getClient().getChannelServer().getExpRate() + ((Math.random() * 10) + 30)) * (Math.floor(Math.random() * (applyfrom.getSkillLevel(PlayerSkillFactory.getSkill(Cleric.Heal))) / 100) * (applyfrom.getLevel() / 30)));
                        if (affected.getStat().getHp() < affected.getStat().getMaxHp() - affected.getStat().getMaxHp() / 20) {
                            applyfrom.gainExp(expadd, true, false);
                        }
                    }
                    if (affected != null) {
                        if (affected != applyfrom && (isGameMasterBuff() || applyfrom.getParty().equals(affected.getParty()))) {
                            boolean isRessurection = isResurrection();
                            if ((isRessurection && !affected.isAlive()) || (!isRessurection && affected.isAlive())) {
                                affectedp.add(affected);
                            }
                            /*boolean isTimeLeap = isTimeLeap();
                            if (isTimeLeap) {
                                for (PlayerCoolDownValueHolder i : affected.getAllCooldowns()) {
                                    affected.removeCooldown(i.skillId);
                                    affected.getClient().write(PacketCreator.SkillCooldown(i.skillId, 0));
                                }
                            }*/
                        }
                    }
                }
                for (Player affected : affectedp) {
                    //System.out.println("affected: " + affected.getName() + " from: " + applyfrom.getName());
                    applyTo(applyfrom, affected, false, null, useMaxRange);
                    affected.getClient().write(EffectPackets.ShowOwnBuffEffect(sourceid, 2));
                    affected.getMap().broadcastMessage(affected, EffectPackets.BuffMapVisualEffect(affected.getId(), sourceid, 2), false);
                }
            }
        }
    }

    private void applyMonsterBuff(Player applyfrom) {
        Rectangle bounds = calculateBoundingBox(applyfrom.getPosition(), applyfrom.isFacingLeft());
        List<FieldObject> affected = applyfrom.getMap().getMapObjectsInBox(bounds, Arrays.asList(FieldObjectType.MONSTER));
        PlayerSkill skill_ = PlayerSkillFactory.getSkill(sourceid);
        int i = 0;
        for (FieldObject mo : affected) {
            final MapleMonster monster = (MapleMonster) mo;
            if (makeChanceResult()) {
                monster.applyStatus(applyfrom, new MonsterStatusEffect(getMonsterStati(), skill_, false), isPoison(), getDuration());
                if (isCrash()) {
                    monster.debuffMob(skill_.getId());
                }
            }
            i++;
            if (i >= mobCount) {
                break;
            }
        }
    }

    public void applyMonsterBuff(Player applyfrom, int[] monsters) {
        PlayerSkill skill_ = PlayerSkillFactory.getSkill(sourceid);
        int i = 0;
        for (int oid : monsters) {
            final MapleMonster monster = (MapleMonster) applyfrom.getMap().getMonsterByOid(oid);
            if (monster != null && makeChanceResult()) {
                monster.applyStatus(applyfrom, new MonsterStatusEffect(getMonsterStati(), skill_, false), isPoison(), getDuration());
                if (isCrash()) {
                    monster.debuffMob(skill_.getId());
                }
                i++;
            }
            if (i >= mobCount) {
                break;
            }
        }
    }

    private final void removeMonsterBuff(final Player applyfrom) {
        List<MonsterStatus> cancel = new ArrayList<>();
        switch (sourceid) {
            //防御崩坏
            case 1111007:
                cancel.add(MonsterStatus.WDEF);
                cancel.add(MonsterStatus.WEAPON_DEFENSE_UP);
                //cancel.add(MonsterStatus.WEAPON_IMMUNITY);
                break;
            //魔击无效
            case 1211009:
                cancel.add(MonsterStatus.MDEF);
                cancel.add(MonsterStatus.MAGIC_DEFENSE_UP);
                //cancel.add(MonsterStatus.MAGIC_IMMUNITY);
                break;
            //力量崩坏
            case 1311007:
                cancel.add(MonsterStatus.WATK);
                cancel.add(MonsterStatus.WEAPON_ATTACK_UP);
                cancel.add(MonsterStatus.MATK);
                cancel.add(MonsterStatus.MAGIC_ATTACK_UP);
                break;
            default:
                return;
        }
        final Rectangle bounds = calculateBoundingBox(applyfrom.getPosition(), applyfrom.isFacingLeft());
        final List<FieldObject> affected = applyfrom.getMap().getMapObjectsInBox(bounds, Arrays.asList(FieldObjectType.MONSTER));
        int i = 0;

        for (final FieldObject mo : affected) {
            if (makeChanceResult()) {
                for (MonsterStatus stat : cancel) {
                    ((MapleMonster) mo).cancelStatus(stat);
                }
            }
            i++;
            if (i >= mobCount) {
                break;
            }
        }
    }

    private Rectangle calculateBoundingBox(Point posFrom, boolean facingLeft) {
        Point mylt;
        Point myrb;
        if (facingLeft) {
            mylt = new Point(lt.x + posFrom.x, lt.y + posFrom.y);
            myrb = new Point(rb.x + posFrom.x, rb.y + posFrom.y);
        } else {
            myrb = new Point(-lt.x + posFrom.x, rb.y + posFrom.y);
            mylt = new Point(-rb.x + posFrom.x, lt.y + posFrom.y);
        }
        Rectangle bounds = new Rectangle(mylt.x, mylt.y, myrb.x - mylt.x, myrb.y - mylt.y);
        return bounds;
    }

    public void silentApplyBuff(Player p, long starttime) {
        final int localDuration = alchemistModifyVal(p, duration, false);
        p.registerEffect(this, starttime, SkillTimer.getInstance().schedule(new CancelEffectAction(p, this, starttime), ((starttime + localDuration) - System.currentTimeMillis())));

        SummonMovementType summonMovementType = getSummonMovementType();
        if (summonMovementType != null) {
            final MapleSummon tosummon = new MapleSummon(p, sourceid, p.getPosition(), summonMovementType);
            if (!tosummon.isStationary()) {
                p.addSummon(sourceid, tosummon);
                tosummon.addHP(x);
            }
        }
        if (sourceid == Corsair.Battleship) {
            //p.announce(PacketCreator.SkillCooldown(5221999, p.getCurrentBattleShipHP()));
        }
    }

    private void applyBuffEffect(Player applyfrom, Player applyto, boolean primary) {
        if (!isMonsterRiding() && !isMysticDoor()) {
            applyto.cancelEffect(this, true, -1);
        }

        int localDuration = duration;
        int localsourceid = sourceid;
        int localX = x;
        int localY = y;
        int seconds = localDuration / 1000;
        List<Pair<BuffStat, Integer>> localStatups = statups;

        TamingMob givemount = null;
        if (isMonsterRiding()) {
            int ridingLevel = 0;
            Item mount = applyfrom.getInventory(InventoryType.EQUIPPED).getItem((byte) -18);
            if (mount != null) {
                ridingLevel = mount.getItemId();
            }
            if (sourceid == Corsair.Battleship) {
                ridingLevel = 1932000;
            } else {
                if (applyto.getMount() == null) {
                    applyto.TamingMob(ridingLevel, sourceid);
                }
            }
            if (sourceid == Corsair.Battleship) {
                givemount = new TamingMob(applyto, ridingLevel, Corsair.Battleship);
            } else {
                givemount = applyto.getMount();
            }
            localDuration = sourceid;
            localsourceid = ridingLevel;
//            localStatups = Collections.singletonList(new Pair<>(BuffStat.MONSTER_RIDING, 0));
        }

//        else if (isPirateMorph()) {
//            localStatups = Collections.singletonList(new Pair<>(BuffStat.MORPH, getMorph(applyto)));
//        }
        if (localStatups.size() > 0) {
            /*if (isDash()) {
                if (!applyto.getJob().isA(PlayerJob.PIRATE)) {
                    applyto.changeSkillLevel(PlayerSkillFactory.getSkill(sourceid), 0, 10);
                } else {
                    //localStatups = Collections.singletonList(new Pair<>(BuffStat.DASH, 1));
                    //applyto.getClient().write(PacketCreator.GiveDash(localStatups, localX, localY, seconds));
                }
            } else if (isInfusion()) {
                applyto.getClient().write(PacketCreator.GiveInfusion(seconds, x));
            } else {
                
            }*/
            int 技能 = localsourceid;
            int 持续时间 = localDuration;
            applyto.getClient().write(PacketCreator.GiveBuff((skill ? 技能 : -技能), 持续时间, localStatups, isMorph(), isMonsterRiding(), givemount));
        }

        if (isMonsterRiding()) {
            //List<Pair<BuffStat, Integer>> stat = Collections.singletonList(new Pair<>(BuffStat.MONSTER_RIDING, 1));
            //if (applyto.getMount().getItemId() != 0) {
            //    applyto.getMap().broadcastMessage(applyto, PacketCreator.ShowMonsterRiding(applyto.getId(), stat, givemount), false);
            //}
            ///if (sourceid == Corsair.Battleship) {
            //    if (applyto.getCurrentBattleShipHP() <= 0) {
            //        applyto.resetBattleshipHp();
            //    }
            //}
            //localDuration = duration;
        } else if (isDs()) {
            List<Pair<BuffStat, Integer>> dsstat = Collections.singletonList(new Pair<>(BuffStat.DarkSight, 0));
            applyto.getMap().broadcastMessage(applyto, PacketCreator.BuffMapEffect(applyto.getId(), dsstat, false), false);
        } else if (isCombo()) {
            List<Pair<BuffStat, Integer>> stat = Collections.singletonList(new Pair<>(BuffStat.ComboAttack, 1));
            applyto.getMap().broadcastMessage(applyto, PacketCreator.BuffMapEffect(applyto.getId(), stat, false), false);
        } else if (isShadowPartner()) {
            List<Pair<BuffStat, Integer>> stat = Collections.singletonList(new Pair<>(BuffStat.ShadowPartner, 0));
            applyto.getMap().broadcastMessage(applyto, PacketCreator.BuffMapEffect(applyto.getId(), stat, false), false);
        } else if (isSoulArrow()) {
            List<Pair<BuffStat, Integer>> stat = Collections.singletonList(new Pair<>(BuffStat.SoulArrow, 0));
            applyto.getMap().broadcastMessage(applyto, PacketCreator.BuffMapEffect(applyto.getId(), stat, false), false);
        } else if (isShield()) {
            //List<Pair<BuffStat, Integer>> stat = Collections.singletonList(new Pair<>(BuffStat.SHIELD, shield));
            //applyto.getMap().broadcastMessage(applyto, PacketCreator.BuffMapEffect(applyto.getId(), stat, false), false);
        } else if (isEnrage()) {
            applyto.handleOrbconsume();
        }/* else if (isMorph()) {
//            final List<Pair<BuffStat, Integer>> stat = Collections.singletonList(new Pair<BuffStat, Integer>(BuffStat.MORPH, Integer.valueOf(getMorph(applyto))));
//            applyto.getMap().broadcastMessage(applyto, PacketCreator.BuffMapEffect(applyto.getId(), stat, true), false);
            //List<Pair<BuffStat, Integer>> stat = Collections.singletonList(new Pair<BuffStat, Integer>(BuffStat.MORPH, Integer.valueOf(getMorph(applyto))));
            //applyto.getMap().broadcastMessage(applyto, PacketCreator.BuffMapEffect(applyto.getId(), stat, true), false);
        }else if (isTimeLeap()) {
            applyto.removeAllCooldownsExcept(Buccaneer.TimeLeap, true);
        }

//        else if (isOakBarrel()) {
//            final List<Pair<BuffStat, Integer>> stat = Collections.singletonList(new Pair<>(BuffStat.MORPH, 1002));
//            applyto.getMap().broadcastMessage(applyto, PacketCreator.BuffMapEffect(applyto.getId(), stat, true), false);
//        }*/
        if (localStatups.size() > 0) {
            long starttime = System.currentTimeMillis();
            CancelEffectAction cancelAction = new CancelEffectAction(applyto, this, starttime);
            ScheduledFuture<?> schedule = SkillTimer.getInstance().schedule(cancelAction, localDuration);
            applyto.registerEffect(this, starttime, schedule);
        }

        if (primary) {
            /*if (isDash()) {
                applyto.getMap().broadcastMessage(applyto, EffectPackets.ShowDashEffecttoOthers(applyto.getId(), localX, localY, seconds), false);
            } else if (isInfusion()) {
                applyto.getMap().broadcastMessage(applyto, PacketCreator.GiveForeignInfusion(applyto.getId(), x, seconds), false);
            } else {
                
            }*/
            applyto.getMap().broadcastMessage(applyto, EffectPackets.BuffMapVisualEffect(applyto.getId(), sourceid, PlayerEffects.SKILL_USE.getEffect(), (byte) 3), false);
        }

        if (sourceid == Corsair.Battleship) {
            //applyto.announce(PacketCreator.SkillCooldown(5221999, applyto.getCurrentBattleShipHP() / 10));
        }
    }

    private int calcHPChange(Player applyfrom, boolean primary) {
        int hpchange = 0;
        if (hp != 0) {
            if (!skill) {
                if (primary) {
                    hpchange += alchemistModifyVal(applyfrom, hp, true);
                } else {
                    hpchange += hp;
                }
            } else {
                hpchange += makeHealHP(hp / 100.0, applyfrom.getStat().getTotalMagic(), 3, 5);
            }
        }
        if (hpR != 0) {
            hpchange += (int) (applyfrom.getStat().getCurrentMaxHp() * hpR);
            applyfrom.checkBerserk(applyfrom.isHidden());
        }
        if (primary) {
            if (hpCon != 0) {
                hpchange -= hpCon;
            }
        }
        if (isChakra()) {
            hpchange += makeHealHP(getY() / 100.0, applyfrom.getStat().getTotalLuk(), 2.3, 3.5);
        }
        return hpchange;
    }

    private int makeHealHP(double rate, double stat, double lowerfactor, double upperfactor) {
        return (int) ((Math.random() * ((int) (stat * upperfactor * rate) - (int) (stat * lowerfactor * rate) + 1)) + (int) (stat * lowerfactor * rate));
    }

    private int calcMPChange(Player applyfrom, boolean primary) {
        int mpchange = 0;
        if (mp != 0) {
            if (primary) {
                mpchange += alchemistModifyVal(applyfrom, mp, true);
            } else {
                mpchange += mp;
            }
        }
        if (mpR != 0) {
            mpchange += (int) (applyfrom.getStat().getCurrentMaxMp() * mpR);
        }
        if (primary) {
            if (mpCon != 0) {
                double mod = 1.0;
                boolean isAFpMage = applyfrom.getJob().isA(PlayerJob.FP_MAGE);
                if (isAFpMage || applyfrom.getJob().isA(PlayerJob.IL_MAGE)) {
                    PlayerSkill amp;
                    if (isAFpMage) {
                        amp = PlayerSkillFactory.getSkill(FPMage.ElementAmplification);
                    } else {
                        amp = PlayerSkillFactory.getSkill(ILMage.ElementAmplification);
                    }
                    int ampLevel = applyfrom.getSkillLevel(amp);
                    if (ampLevel > 0) {
                        MapleStatEffect ampStat = amp.getEffect(ampLevel);
                        mod = ampStat.getX() / 100.0;
                    }
                }
                mpchange -= mpCon * mod;
                /*                if (applyfrom.getBuffedValue(BuffStat.INFINITY) != null) {
                 mpchange = 0;
                 }*/
            }
        }
//        if (isPirateMpRecovery()) {
//            mpchange += y * x / 10000.0 * applyfrom.getStat().getCurrentMaxHp();
//        }
        return mpchange;
    }

    private int alchemistModifyVal(Player chr, int val, boolean withX) {
        if (!skill && (chr.getJob().isA(PlayerJob.HERMIT) || chr.getJob().isA(PlayerJob.NIGHTLORD))) {
            MapleStatEffect alchemistEffect = getAlchemistEffect(chr);
            if (alchemistEffect != null) {
                return (int) (val * ((withX ? alchemistEffect.getX() : alchemistEffect.getY()) / 100.0));
            }
        }
        return val;
    }

    private MapleStatEffect getAlchemistEffect(Player chr) {
        PlayerSkill alchemist = PlayerSkillFactory.getSkill(Hermit.Alchemist);
        int alchemistLevel = chr.getSkillLevel(alchemist);
        if (alchemistLevel == 0) {
            return null;
        }
        return alchemist.getEffect(alchemistLevel);
    }

    public void setSourceId(int newid) {
        sourceid = newid;
    }

    public short getHpCon() {
        return hpCon;
    }

    public short getMpCon() {
        return mpCon;
    }

    private boolean isGameMasterBuff() {
        switch (sourceid) {
            case Beginner.EchoOfHero:
            //case Gm.Haste:
            case SuperGm.Haste:
            case SuperGm.HolySymbol:
            case SuperGm.Bless:
            case SuperGm.Resurrection:
            case SuperGm.HyperBody:
                return true;
            default:
                return false;
        }
    }

    private boolean isMonsterBuff() {
        if (!skill) {
            return false;
        }
        switch (sourceid) {
            case Page.Threaten:
            case FPWizard.Slow:
            case ILWizard.Slow:
            case FPMage.Seal:
            case ILMage.Seal:
            case Priest.Doom:
            case Hermit.ShadowWeb:
            case NightLord.NinjaAmbush:
            case Shadower.NinjaAmbush:
            case Crusader.防御崩坏:
            case DragonKnight.PowerCrash:
            case WhiteKnight.MagicCrash:
            case Priest.Dispel:
            case SuperGm.HealnDispel:
                return true;
        }
        return false;
    }

    private boolean isPartyBuff() {
        if (lt == null || rb == null) {
            return false;
        }
        return !((sourceid >= WhiteKnight.SwordFireCharge && sourceid <= WhiteKnight.BwLitCharge) || sourceid == Paladin.SwordHolyCharge || sourceid == Paladin.BwHolyCharge);
    }

    private boolean isSkillMorph() {
        return skill && (sourceid == Buccaneer.SuperTransformation || sourceid == Marauder.Transformation);
    }

    public boolean isHeal() {
        return sourceid == Cleric.Heal || sourceid == SuperGm.HealnDispel;
    }

    public boolean isResurrection() {
        return sourceid == SuperGm.Resurrection || sourceid == Bishop.Resurrection;
    }

    public boolean isTimeLeap() {
        return sourceid == Buccaneer.TimeLeap;
    }

    public short getHp() {
        return hp;
    }

    public short getMp() {
        return mp;
    }

    public short getWatk() {
        return watk;
    }

    public short getMatk() {
        return matk;
    }

    public short getWdef() {
        return wdef;
    }

    public short getMdef() {
        return mdef;
    }

    public short getAcc() {
        return acc;
    }

    public short getAvoid() {
        return avoid;
    }

    public short getHands() {
        return hands;
    }

    public short getSpeed() {
        return speed;
    }

    public short getJump() {
        return jump;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isOverTime() {
        return overTime;
    }

    public List<Pair<BuffStat, Integer>> getStatups() {
        return statups;
    }

    public boolean isBattleship() {
        return skill && (sourceid == Corsair.Battleship);
    }

    public boolean sameSource(MapleStatEffect effect) {
        return this.sourceid == effect.sourceid && this.skill == effect.skill;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getDamage() {
        return damage;
    }

    public int getAttackCount() {
        return attackCount;
    }

    public int getBulletCount() {
        return bulletCount;
    }

    public int getBulletConsume() {
        return bulletConsume;
    }

    public int getMoneyCon() {
        return moneyCon;
    }

    public int getCoolDown() {
        return cooldown;
    }

    public Map<MonsterStatus, Integer> getMonsterStati() {
        return monsterStatus;
    }

    public final boolean isHide() {
        return skill && (sourceid == Gm.Hide || sourceid == SuperGm.Hide);
    }

    public boolean isDragonBlood() {
        return skill && sourceid == DragonKnight.DragonBlood;
    }

    public boolean isBerserk() {
        return skill && sourceid == DarkKnight.Berserk;
    }

    private boolean isDs() {
        return skill && (sourceid == Rogue.DarkSight || sourceid == Gm.Hide);
    }

    private boolean isCombo() {
        return skill && sourceid == Crusader.斗气集中;
    }

    private boolean isEnrage() {
        return skill && sourceid == Hero.Enrage;
    }

    public boolean isPirateMpRecovery() {
        return skill && sourceid == Brawler.MpRecovery;
    }

    public boolean isBeholder() {
        return skill && sourceid == DarkKnight.Beholder;
    }

    public boolean isPhoenix() {
        return skill && sourceid == Bowmaster.Phoenix;
    }

    public boolean isMarksMan() {
        return skill && sourceid == Marksman.Frostprey;
    }

    public boolean isMageSummon() {
        return skill && sourceid == FPArchMage.Elquines;
    }

    public boolean isMageSummon2() {
        return skill && sourceid == ILArchMage.Ifrit;
    }

    private boolean isShadowPartner() {
        return skill && sourceid == Hermit.ShadowPartner;
    }

    private boolean isChakra() {
        return skill && sourceid == ChiefBandit.Chakra;
    }

    public boolean isMonsterRiding() {
        return skill && (sourceid == Beginner.MonsterRider || sourceid == Corsair.Battleship);
    }

    public boolean isBattleShip() {
        return skill && sourceid == Corsair.Battleship;
    }

    public boolean isMysticDoor() {
        return skill && sourceid == Priest.MysticDoor;
    }

    public boolean isMesoGuard() {
        return skill && sourceid == ChiefBandit.MesoGuard;
    }

    public boolean isCharge() {
        return skill && sourceid >= WhiteKnight.SwordFireCharge && sourceid <= WhiteKnight.BwLitCharge;
    }

    public boolean isPoison() {
        return skill && (sourceid == FPMage.PoisonMist || sourceid == FPWizard.PoisonBreath || sourceid == FPMage.ElementComposition);
    }

    private boolean isMist() {
        return skill && (sourceid == FPMage.PoisonMist || sourceid == Shadower.Smokescreen);
    }

    private boolean isSoulArrow() {
        return skill && (sourceid == Hunter.SoulArrow || sourceid == Crossbowman.SoulArrow);
    }

    private boolean isShadowClaw() {
        return skill && sourceid == NightLord.ShadowStars;
    }

    private boolean isDispel() {
        return skill && (sourceid == Priest.Dispel || sourceid == SuperGm.HealnDispel);
    }

    private boolean isHeroWill() {
        return skill && (sourceid == Hero.HerosWill || sourceid == Paladin.HerosWill || sourceid == DarkKnight.HerosWill || sourceid == FPArchMage.HerosWill || sourceid == ILArchMage.HerosWill || sourceid == Bishop.HerosWill || sourceid == Bowmaster.HerosWill || sourceid == Marksman.HerosWill || sourceid == NightLord.HerosWill || sourceid == Shadower.HerosWill || sourceid == Buccaneer.PiratesRage || sourceid == Corsair.HerosWill);
    }

    private boolean isDash() {
        return skill && sourceid == Pirate.Dash;
    }

    public boolean hasNoIcon() {
        return (sourceid == 3111002 || sourceid == 3211002 || + // puppet, puppet
                sourceid == 3211005 || + // golden eagle
                sourceid == 2121005 || sourceid == 2221005 || + // elquines, ifrit
                sourceid == 2321003 || sourceid == 3121006 || + // bahamut, phoenix
                sourceid == 3221005 || sourceid == 3111005 || + // frostprey, silver hawk
                sourceid == 2311006 || sourceid == 5220002 || + // summon dragon, wrath of the octopi
                sourceid == 5211001 || sourceid == 5211002); // octopus, gaviota
    }

    public final boolean isPirateMorph() {
        switch (sourceid) {
            case Marauder.Transformation:
            case Buccaneer.SuperTransformation:
            case Brawler.OakBarrel:
                return skill;
        }
        return false;
    }

    public boolean isMorph() {
        return morphId > 0;
    }

    private boolean isCrash() {
        return skill && (sourceid == DragonKnight.PowerCrash || sourceid == WhiteKnight.MagicCrash);
    }

    public boolean isRecovery() {
        return sourceid == Beginner.Recovery;
    }

    private boolean isOakBarrel() {
        return skill && sourceid == Brawler.OakBarrel;
    }

    public int getMorph() {
        return morphId;
    }

    public int getCP() {
        return cp;
    }

    public boolean isParty() {
        return party;
    }

    public boolean isConsumeOnPickup() {
        return consumeOnPickup;
    }

    public int getNuffSkill() {
        return nuffSkill;
    }

    public boolean isGMHeal() {
        return skill && sourceid == SuperGm.HealnDispel;
    }

    public boolean isEnergy() {
        return skill && sourceid == Marauder.EnergyCharge;
    }

    private boolean isShield() {
        return !skill && sourceid == ItemConstants.PROTECTIVE_SHIELD;
    }

    private int getMorph(Player p) {
        if (morphId % 10 == 0) {
            return morphId + p.getGender();
        }
        return morphId + 100 * p.getGender();
    }

    public SummonMovementType getSummonMovementType() {
        if (!skill) {
            return null;
        }
        switch (sourceid) {
            case Sniper.Puppet:
            case Ranger.Puppet:
            case Outlaw.Octopus:
            case Corsair.WrathOfTheOctopi:
                return SummonMovementType.STATIONARY;
            case Sniper.GoldenEagle:
            case Ranger.SilverHawk:
            case Priest.SummonDragon:
            case Marksman.Frostprey:
            case Bowmaster.Phoenix:
            case Outlaw.Gaviota:
                return SummonMovementType.CIRCLE_FOLLOW;
            case DarkKnight.Beholder:
            case FPArchMage.Elquines:
            case ILArchMage.Ifrit:
            case Bishop.Bahamut:
                return SummonMovementType.FOLLOW;
        }
        return null;
    }

    public boolean isSkill() {
        return skill;
    }

    public int getSourceId() {
        return sourceid;
    }

    public int getMastery() {
        return mastery;
    }

    public int getRange() {
        return range;
    }

    public int getMobCount() {
        return mobCount;
    }

    public int getFixDamage() {
        return fixDamage;
    }

    public final byte getLevel() {
        return level;
    }

    /**
     *
     * @return true if the effect should happen based on it's probablity, false
     * otherwise
     */
    public boolean makeChanceResult() {
        return prop == 1.0 || Math.random() < prop;
    }

    public final void applyEnergyBuff(final Player applyto, int skillId, List<Pair<BuffStat, Integer>> stat) {
        final long starttime = System.currentTimeMillis();
        applyto.cancelEffect(this, true, -1);
        applyto.announce(PacketCreator.UsePirateSkill(stat, skillId, duration / 1000, (short) 10));
        final CancelEffectAction cancelAction = new CancelEffectAction(applyto, this, starttime);
        final ScheduledFuture<?> schedule = SkillTimer.getInstance().schedule(cancelAction, ((starttime + duration) - System.currentTimeMillis()));
//        this.statups = Collections.singletonList(new Pair<BuffStat, Integer>(BuffStat.ENERGY_CHARGE, 10000));
        applyto.registerEffect(this, starttime, schedule);
        this.statups = stat;

    }

    public static class CancelEffectAction implements Runnable {

        private final MapleStatEffect effect;
        private final WeakReference<Player> target;
        private final long startTime;

        public CancelEffectAction(final Player target, final MapleStatEffect effect, final long startTime) {
            this.effect = effect;
            this.target = new WeakReference<>(target);
            this.startTime = startTime;
        }

        @Override
        public void run() {
            final Player realTarget = target.get();
            if (realTarget != null) {
                realTarget.cancelEffect(effect, false, startTime);
            }
        }
    }
}
