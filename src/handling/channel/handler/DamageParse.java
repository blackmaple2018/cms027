package handling.channel.handler;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import constants.SkillConstants.Assassin;
import constants.SkillConstants.Bowmaster;
import client.player.Player;
import client.player.PlayerJob;
import client.player.PlayerStat;
import client.player.buffs.BuffStat;
import client.player.inventory.types.InventoryType;
import client.player.skills.PlayerSkill;
import client.player.skills.PlayerSkillFactory;
import static client.player.skills.PlayerSkillFactory.技能名字;
import security.violation.AutobanManager;
import security.violation.CheatingOffense;
import static configure.Gamemxd.合伙群;
import static configure.Gamemxd.恢复药水;
import static configure.Gamemxd.攻击数量检测;
import static configure.Gamemxd.攻击段数检测;
import static configure.Gamemxd.终极技能;
import constants.SkillConstants.Bandit;
import constants.SkillConstants.Bishop;
import constants.SkillConstants.Brawler;
import server.life.status.MonsterStatus;
import server.life.status.MonsterStatusEffect;
import constants.SkillConstants.Buccaneer;
import constants.SkillConstants.ChiefBandit;
import constants.SkillConstants.Cleric;
import constants.SkillConstants.Corsair;
import constants.SkillConstants.DragonKnight;
import constants.SkillConstants.FPArchMage;
import constants.SkillConstants.Gunslinger;
import constants.SkillConstants.Hermit;
import constants.SkillConstants.Hero;
import constants.SkillConstants.ILArchMage;
import constants.SkillConstants.Marauder;
import constants.SkillConstants.Marksman;
import constants.SkillConstants.NightLord;
import constants.SkillConstants.Paladin;
import constants.SkillConstants.Rogue;
import constants.SkillConstants.Shadower;
import constants.SkillConstants.Sniper;
import constants.SkillConstants.SuperGm;
import constants.SkillConstants.WhiteKnight;
import static console.MsgServer.QQMsgServer.sendMsgToQQGroup;
import packet.transfer.read.InPacket;
import java.util.HashMap;
import static launch.Start.大区;
import static launch.Start.攻击数量检测;
import static launch.Start.攻击段数检测;
import packet.creators.EffectPackets;
import packet.creators.PacketCreator;
import server.MapleStatEffect;
import server.itens.ItemInformationProvider;
import tools.TimerTools.ItemTimer;
import server.life.Element;
import server.life.ElementalEffectiveness;
import server.life.MapleMonster;
import server.maps.Field;
import server.maps.FieldItem;
import server.maps.object.FieldObject;
import server.maps.object.FieldObjectType;
import tools.Randomizer;

public abstract class DamageParse {

    public static String 技能名字(int a) {
        if (技能名字.containsKey(a)) {
            return 技能名字.get(a);
        }
        return "攻击技能";
    }

    /**
     * <物理攻击>
     */
    public static void applyAttack(AttackInfo attack, Player p, int maxDamagePerMonster, int attackCount) {
        p.getCheatTracker().resetHPRegen();
        p.getCheatTracker().checkAttack(attack.skill);
        PlayerSkill skill = null;
        MapleStatEffect effect = null;

        if (p.检测() > 0) {
            return;
        }

        if (attack.skill != 0) {
            if (p.switch_skill() == 0) {
                p.announce(EffectPackets.ShowEffect("Skill/" + attack.skill + ""));
            }

            skill = PlayerSkillFactory.getSkill(attack.skill);
            effect = attack.getAttackEffect(p, skill);
            if (effect.getMpCon() > 0) {
                int 消耗蓝量 = p.getStat().getMp() - effect.getMpCon();
                p.getStat().setMp((int) 消耗蓝量);
                p.getStat().updateSingleStat(PlayerStat.MP, (int) 消耗蓝量);
            }

            if (effect.getHpCon() > 0) {
                int 消耗血量 = p.getStat().getHp() - effect.getHpCon();
                p.getStat().setHp(消耗血量);
                p.getStat().updateSingleStat(PlayerStat.HP, 消耗血量);
            }

            //龙之献祭
            if (attack.skill == 1311005) {
                int 需求血量 = p.getStat().getMaxHp() / 100 * effect.getX();
                int 消耗血量 = p.getStat().getHp() - (p.getStat().getMaxHp() / 100 * effect.getX());
                if (p.getStat().getHp() > 需求血量) {
                    p.getStat().setHp(消耗血量);
                    p.getStat().updateSingleStat(PlayerStat.HP, 消耗血量);
                } else {
                    p.getStat().setHp(1);
                    p.getStat().updateSingleStat(PlayerStat.HP, 1);
                }
            }
            //龙咆哮
            if (attack.skill == 1311006) {
                int 消耗血量 = p.getStat().getHp() - (p.getStat().getMaxHp() / 100 * effect.getX());
                p.getStat().setHp(消耗血量);
                p.getStat().updateSingleStat(PlayerStat.HP, 消耗血量);
            }
            if (p.判断攻击回蓝判断() >= 5) {
                if (p.getvip() > 0) {
                    if (p.isAlive()) {
                        if (p.getRecoveryMP() > 0) {
                            if (p.getMp() < p.getRecoveryMP()) {
                                int MP = 0;
                                MP = p.getRecoveryMPitemid();
                                if (MP > 0) {
                                    if (p.haveItem(MP)) {
                                        ItemInformationProvider mii = ItemInformationProvider.getInstance();
                                        MapleStatEffect statEffect = mii.getItemEffect(MP);
                                        if (statEffect != null) {
                                            statEffect.applyTo(p);
                                            p.gainItem(MP, (short) -1, false);
                                        }
                                    } else {
                                        p.dropMessage(5, "自动恢复药品 " + ItemInformationProvider.getInstance().getName(MP) + "使用殆尽，请尽快补充。");
                                    }
                                }
                            }
                        }
                    }
                }
                p.重置攻击回蓝判断();
            } else {
                p.攻击回蓝判断();
            }
            //技能攻击怪物数量检测
            if (攻击数量检测) {
                if (!终极技能(attack.skill)) {
                    int mobCount = 攻击数量检测(attack.skill, attack.skillLevel);
                    if (attack.numAttacked > mobCount) {
                        p.dropMessage(1, "数据异常，网络中断。");
                        p.bans("攻击怪物数量异常," + attack.numAttacked + ">" + mobCount + "");
                        p.getClient().close();
                        sendMsgToQQGroup(""
                                + "QQ：" + p.getQQ() + "\r\n"
                                + "大区：" + 大区(p.getWorldId()) + "\r\n"
                                + "玩家：" + p.getName() + "\r\n"
                                + "地图：" + p.getMapName(p.getMapId()) + "\r\n"
                                + "原因：攻击怪物数量异常\r\n"
                                + "技能：" + 技能名字(attack.skill) + "\r\n"
                                + "正常：" + mobCount + "\r\n"
                                + "实际：" + attack.numAttacked + "", 合伙群);
                        return;
                    }
                }
            }
            //攻击次数
            if (攻击段数检测) {
                int 攻击段数 = 攻击段数检测(attack.skill, attack.skillLevel);
                if (p.getJobId() == 411 || p.getJobId() == 412) {
                    攻击段数 *= 2;
                }
                if (attack.numDamage > 攻击段数) {
                    if (attack.skill != 4211006) {
                        p.dropMessage(1, "数据异常，网络中断。");
                        p.bans("攻击技能段数异常," + attack.numDamage + ">" + 攻击段数 + "");
                        p.getClient().close();
                        sendMsgToQQGroup(""
                                + "QQ：" + p.getQQ() + "\r\n"
                                + "大区：" + 大区(p.getWorldId()) + "\r\n"
                                + "玩家：" + p.getName() + "\r\n"
                                + "地图：" + p.getMapName(p.getMapId()) + "\r\n"
                                + "原因：攻击技能段数异常\r\n"
                                + "技能：" + 技能名字(attack.skill) + "\r\n"
                                + "正常：" + 攻击段数 + "\r\n"
                                + "实际：" + attack.numDamage + "", 合伙群);
                        return;
                    }
                }
            }
        }

        int totDamage = 0;
        final Field map = p.getMap();
        //金钱炸弹
        if (attack.skill == 4211006) {
            for (Integer oned : attack.allDamage.keySet()) {
                final FieldObject mapObject = map.getMapObject(oned, FieldObjectType.ITEM);
                if (mapObject != null && mapObject.getType() == FieldObjectType.ITEM) {
                    final FieldItem mapItem = (FieldItem) mapObject;
                    synchronized (mapItem) {
                        if (mapItem.getMeso() > 0) {
                            if (mapItem.isPickedUp()) {
                                return;
                            }
                            map.removeMapObject(mapItem);
                            map.broadcastMessage(PacketCreator.RemoveItemFromMap(mapItem.getObjectId(), 4, 0));
                            mapItem.setPickedUp(true);
                        } else {
                            p.getCheatTracker().registerOffense(CheatingOffense.ETC_EXPLOSION);
                            return;
                        }
                    }
                }
            }
        }
        //怪物数量
        for (Integer oned : attack.allDamage.keySet()) {
            final MapleMonster monster = map.getMonsterByOid(oned);
            int totDamageToOneMonster = 0;
            int 附魔伤害 = 0;
            if (monster != null) {
                int 段数 = 0;
                List<Integer> onedList = attack.allDamage.get(oned);
                //攻击技能段数
                totDamageToOneMonster = 0;
                for (Integer eachd : onedList) {
                    if (eachd < 0) {
                        eachd += Integer.MAX_VALUE;
                    }
                    段数++;
                    totDamageToOneMonster += eachd;
                }
                //增加固定伤害
                if (totDamageToOneMonster > 0) {
                    附魔伤害 = 0;
                    totDamage = 0;
                    if (p.getJobId() >= 200 && p.getJobId() < 300) {
                        if (attack.skill > 0) {
                            if (p.getEquippedFuMoMap().get(2) != null) {
                                附魔伤害 += p.getEquippedFuMoMap().get(2);
                                totDamage += p.getEquippedFuMoMap().get(2);
                            }
                            //叠加魔法伤害
                            if (p.getEquippedFuMoMap().get(6) != null) {
                                p.叠加伤害(p.getEquippedFuMoMap().get(6));
                                附魔伤害 += p.叠加伤害();
                                totDamage += p.叠加伤害();
                            }
                        } else if (attack.skill == 0) {
                            if (p.getEquippedFuMoMap().get(1) != null) {
                                附魔伤害 += p.getEquippedFuMoMap().get(1);
                                totDamage += p.getEquippedFuMoMap().get(1);
                            }
                            //叠加物理伤害
                            if (p.getEquippedFuMoMap().get(5) != null) {
                                p.叠加伤害(p.getEquippedFuMoMap().get(5));
                                附魔伤害 += p.叠加伤害();
                                totDamage += p.叠加伤害();
                            }
                        }
                    } else {
                        if (p.getEquippedFuMoMap().get(1) != null) {
                            附魔伤害 += p.getEquippedFuMoMap().get(1);
                            totDamage += p.getEquippedFuMoMap().get(1);
                        }
                        //叠加物理伤害
                        if (p.getEquippedFuMoMap().get(5) != null) {
                            p.叠加伤害(p.getEquippedFuMoMap().get(5));
                            附魔伤害 += p.叠加伤害();
                            totDamage += p.叠加伤害();
                        }
                    }
                    //增加百分比伤害
                    if (totDamageToOneMonster > 100) {
                        if (p.getJobId() >= 200 && p.getJobId() < 300) {
                            if (attack.skill > 0) {
                                if (p.getEquippedFuMoMap().get(21) != null) {
                                    附魔伤害 += totDamageToOneMonster / 100 * p.getEquippedFuMoMap().get(21);
                                    totDamage += totDamageToOneMonster / 100 * p.getEquippedFuMoMap().get(21);
                                }
                            }
                        } else {
                            if (p.getEquippedFuMoMap().get(11) != null) {
                                附魔伤害 += totDamageToOneMonster / 100 * p.getEquippedFuMoMap().get(11);
                                totDamage += totDamageToOneMonster / 100 * p.getEquippedFuMoMap().get(11);
                            }
                        }
                    }
                }
                totDamage += totDamageToOneMonster;
                totDamageToOneMonster = totDamage;

                p.checkMonsterAggro(monster);
                if (attack.skill == Cleric.Heal && !monster.getUndead()) {
                    p.getCheatTracker().registerOffense(CheatingOffense.HEAL_ATTACKING_UNDEAD);
                    return;
                }
                checkHighDamage(p, monster, attack, skill, effect, totDamageToOneMonster, maxDamagePerMonster);
                if (p.getBuffedValue(BuffStat.PickPocketMesoUP) != null) {
                    switch (attack.skill) {
                        case 0:
                        case Rogue.DoubleStab:
                        case Bandit.SavageBlow:
                        case ChiefBandit.Assaulter:
                        case ChiefBandit.BandOfThieves:
                        case ChiefBandit.Chakra:
                        case Shadower.Taunt:
                        case Shadower.BoomerangStep:
                            final int maxMeso = p.getBuffedValue(BuffStat.PickPocketMesoUP);
                            final PlayerSkill skillPocket = PlayerSkillFactory.getSkill(ChiefBandit.Pickpocket);
                            final MapleStatEffect s = skillPocket.getEffect(p.getSkillLevel(skillPocket));
                            for (Integer eachd : onedList) {
                                eachd += Integer.MAX_VALUE;
                                if (skillPocket.getEffect(p.getSkillLevel(skillPocket)).makeChanceResult()) {
                                    final Integer eachdf;
                                    if (eachd < 0) {
                                        eachdf = eachd + Integer.MAX_VALUE;
                                    } else {
                                        eachdf = eachd;
                                    }
                                    ItemTimer.getInstance().schedule(() -> {
                                        p.getMap().spawnMesoDrop(Math.min((int) Math.max(((double) eachdf / (double) 20000) * (double) maxMeso, (double) 1), maxMeso), new Point((int) (monster.getPosition().getX() + Randomizer.nextInt(100) - 50), (int) (monster.getPosition().getY())), monster, p, true, (byte) 0);
                                    }, 100);
                                }
                            }
                            break;
                    }
                }

                switch (attack.skill) {
                    case Paladin.HeavensHammer:
                        if (attack.isHH) {
                            int HHDmg = (p.getStat().calculateMaxBaseDamage(p.getStat().getTotalWatk()) * (PlayerSkillFactory.getSkill(Paladin.HeavensHammer).getEffect(p.getSkillLevel(PlayerSkillFactory.getSkill(Paladin.HeavensHammer))).getDamage() / 100));
                            map.damageMonster(p, monster, (int) (Math.floor(Math.random() * (HHDmg / 5) + HHDmg * .8)));
                        } else {
                            map.damageMonster(p, monster, totDamageToOneMonster);
                        }
                        break;
                    case Marksman.Snipe:
                        totDamageToOneMonster = (int) (195000 + Math.random() * 4999);
                        break;
                    case NightLord.Taunt:
                        monster.setTaunted(true);
                        monster.setTaunter(p);
                        break;
                    case Assassin.Drain:
                    case Marauder.EnergyDrain:
                        int gainHp = (int) ((double) totDamageToOneMonster * (double) PlayerSkillFactory.getSkill(attack.skill).getEffect(p.getSkillLevel(PlayerSkillFactory.getSkill(attack.skill))).getX() / 100.0);
                        gainHp = Math.min(monster.getMaxHp(), Math.min(gainHp, p.getStat().getCurrentMaxHp() / 2));
                        p.getStat().addHP(gainHp);
                        break;
                    default:
                        if (attack.skill != 0) {
                            if (effect != null) {
                                if (effect.getFixDamage() != -1) {
                                    if (totDamageToOneMonster != effect.getFixDamage() && totDamageToOneMonster != 0) {
                                        AutobanManager.getInstance().autoban(p.getClient(), String.valueOf(totDamageToOneMonster) + " damage");
                                    }
                                }
                            }
                        }
                        if (totDamageToOneMonster > 0 && monster.isAlive()) {
                            if (p.getJob().isA(PlayerJob.WHITEKNIGHT)) {
                                int[] charges = {WhiteKnight.SwordIceCharge, WhiteKnight.BwIceCharge};
                                for (int charge : charges) {
                                    if (p.isBuffFrom(BuffStat.Charges, PlayerSkillFactory.getSkill(charge))) {
                                        final ElementalEffectiveness iceEffectiveness = monster.getEffectiveness(Element.ICE);
                                        if (iceEffectiveness == ElementalEffectiveness.NORMAL || iceEffectiveness == ElementalEffectiveness.WEAK) {
                                            MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(Collections.singletonMap(MonsterStatus.FREEZE, 1), PlayerSkillFactory.getSkill(charge), false);
                                            monster.applyStatus(p, monsterStatusEffect, false, PlayerSkillFactory.getSkill(charge).getEffect(p.getSkillLevel(PlayerSkillFactory.getSkill(charge))).getY() * 2000);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                }
                if (p.getSkillLevel(PlayerSkillFactory.getSkill(NightLord.VenomousStar)) > 0) {
                    MapleStatEffect venomEffect = PlayerSkillFactory.getSkill(NightLord.VenomousStar).getEffect(p.getSkillLevel(PlayerSkillFactory.getSkill(NightLord.VenomousStar)));
                    for (int i = 0; i < attackCount; i++) {
                        if (venomEffect.makeChanceResult()) {
                            if (monster.getVenomMulti() < 3) {
                                monster.setVenomMulti((monster.getVenomMulti() + 1));
                                MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(Collections.singletonMap(MonsterStatus.POISON, 1), PlayerSkillFactory.getSkill(NightLord.VenomousStar), false);
                                monster.applyStatus(p, monsterStatusEffect, false, venomEffect.getDuration(), true);
                            }
                        }
                    }
                } else if (p.getSkillLevel(PlayerSkillFactory.getSkill(Shadower.VenomousStab)) > 0) {
                    MapleStatEffect venomEffect = PlayerSkillFactory.getSkill(Shadower.VenomousStab).getEffect(p.getSkillLevel(PlayerSkillFactory.getSkill(Shadower.VenomousStab)));
                    for (int i = 0; i < attackCount; i++) {
                        if (venomEffect.makeChanceResult()) {
                            if (monster.getVenomMulti() < 3) {
                                monster.setVenomMulti((monster.getVenomMulti() + 1));
                                MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(Collections.singletonMap(MonsterStatus.POISON, 1), PlayerSkillFactory.getSkill(Shadower.VenomousStab), false);
                                monster.applyStatus(p, monsterStatusEffect, false, venomEffect.getDuration(), true);
                            }
                        }
                    }
                }
                if (totDamageToOneMonster > 0 && effect != null && effect.getMonsterStati().size() > 0) {
                    if (effect.makeChanceResult()) {
                        MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(effect.getMonsterStati(), skill, false);
                        monster.applyStatus(p, monsterStatusEffect, effect.isPoison(), effect.getDuration());
                    }
                }
                if (!attack.isHH) {
                    map.damageMonster(p, monster, totDamageToOneMonster);
                    p.announce(PacketCreator.EnableActions());
                }
            }
        }
        if (totDamage > 1) {
            p.getCheatTracker().setAttacksWithoutHit(p.getCheatTracker().getAttacksWithoutHit() + 1);
            final int offenseLimit;
            switch (attack.skill) {
                case Bowmaster.Hurricane:
                case Corsair.RapidFire:
                    offenseLimit = 100;
                    break;
                default:
                    offenseLimit = 500;
                    break;
            }
            if (p.getCheatTracker().getAttacksWithoutHit() > offenseLimit) {
                p.getCheatTracker().registerOffense(CheatingOffense.ATTACK_WITHOUT_GETTING_HIT, Integer.toString(p.getCheatTracker().getAttacksWithoutHit()));
            }
        }
    }

    public static void checkHighDamage(Player p, MapleMonster monster, AttackInfo attack, PlayerSkill theSkill, MapleStatEffect attackEffect, int damageToMonster, int maximumDamageToMonster) {
        if (!p.isGameMaster()) {
            int elementalMaxDamagePerMonster;
            int multiplyer = p.getJob().isA(PlayerJob.PIRATE) ? 40 : 4;
            Element element = Element.NEUTRAL;
            if (theSkill != null) {
                element = theSkill.getElement();
                int skillId = theSkill.getId();
                switch (skillId) {
                    case 3221007:
                        maximumDamageToMonster = 99999;
                        break;
                    case 4221001:
                        maximumDamageToMonster = 400000;
                        break;
                    default:
                        break;
                }
            }
            if (p.getBuffedValue(BuffStat.Charges) != null) {
                int chargeSkillId = p.getBuffSource(BuffStat.Charges);
                switch (chargeSkillId) {
                    case WhiteKnight.SwordFireCharge:
                    case WhiteKnight.BwFireCharge:
                        element = Element.FIRE;
                        break;
                    case WhiteKnight.SwordIceCharge:
                    case WhiteKnight.BwIceCharge:
                        element = Element.ICE;
                        break;
                    case WhiteKnight.SwordLitCharge:
                    case WhiteKnight.BwLitCharge:
                        element = Element.LIGHTING;
                        break;
                    case Paladin.SwordHolyCharge:
                    case Paladin.BwHolyCharge:
                        element = Element.HOLY;
                        break;
                }
                PlayerSkill chargeSkill = PlayerSkillFactory.getSkill(chargeSkillId);
                maximumDamageToMonster *= chargeSkill.getEffect(p.getSkillLevel(chargeSkill)).getDamage() / 100.0;
            }
            if (element != Element.NEUTRAL) {
                double elementalEffect;
                if (attack.skill == Sniper.Blizzard || attack.skill == 3111003) {
                    elementalEffect = attackEffect.getX() / 200.0;
                } else {
                    elementalEffect = 0.5;
                }
                switch (monster.getEffectiveness(element)) {
                    case IMMUNE:
                        elementalMaxDamagePerMonster = 1;
                        break;
                    case NORMAL:
                        elementalMaxDamagePerMonster = maximumDamageToMonster;
                        break;
                    case WEAK:
                        elementalMaxDamagePerMonster = (int) (maximumDamageToMonster * (1.0 + elementalEffect));
                        break;
                    case STRONG:
                        elementalMaxDamagePerMonster = (int) (maximumDamageToMonster * (1.0 - elementalEffect));
                        break;
                    default:
                        throw new RuntimeException("Unknown enum constant");
                }
            } else {
                elementalMaxDamagePerMonster = maximumDamageToMonster;
            }
        }
    }

    /**
     * <攻击>
     */
    public static final AttackInfo parseDamage(InPacket packet, Player p, boolean ranged, boolean magic) {
        final AttackInfo ret = new AttackInfo();
        ////1A [11] [00 00 00 00] [00] [07] [04]        [69 00 00 00] 06 01 00 00 A3 01 8B 01 A3 01 8B 01 89 01 06 00 00 00 8F 01 8B 01
        ////1B [11] [00 00 00 00] [00] [96] [06] [01 00] 41 [88 00 00 00] 06 00 01 00 CF 05 07 08 CC 05 07 08 9B 02 1E 01 00 00 A0 06 F2 07
        ////1B [02] [40 0E 3D 00] [00] [1A] [06] [01 00] 41 70 00 12 01
        //  1B [12] [AD CA 2D 00] [02] [16] [06] [00 00] 41 7C 00 00 00 06 81 01 00 9F 03 D7 00 9F 03 D7 00 FD 02 1D 03 00 00 20 06 00 00 90 02 D7 00
        //  1B [02] [40 0E 3D 00] [00] [19] [06] [01 00] 41 3C 0A 4E 01
        //  1B [02] [40 0E 3D 00] [00] [98] [06] 01 00 41 3A 0A 4E 0
        //  1A [11] [3E 41 40 00] [00] [35] [04]        [77 00 00 00] 06 00 02 00 46 04 D7 00 42 04 D7 00 01 77 00 00 00 AC 03 D7 00 01 AF 00 00 00 01 63 02
        //  1A 00 3E 41 40 00 00 35 04 39 05 D7 00 01 94 00 00 00 00 63 02
        //     [11] [1B 36 20 00] [00] [A9] [06]        [7D 00 00 00] 06 80 00 00 06 02 D7 00 0A 02 D7 00 E8 03 11 03 00 00 69 02 D7 00
        //     [11] [9C BA 3E 00] [00] [1A] [06] [00 00] [41] [78 00 00 00] 06 80 02 00 DB 02 D7 00 DF 02 D7 00 62 02 82 05 00 00 2B 02 D7 00 
        ret.numAttackedAndDamage = packet.readByte();
        ret.numAttacked = (ret.numAttackedAndDamage >>> 4) & 0xF;
        ret.numDamage = ret.numAttackedAndDamage & 0xF;
        ret.allDamage = new HashMap<>();
        ret.skill = packet.readInt();
        ret.skillLevel = (byte) p.getSkillLevel(ret.skill);
        ret.ranged = ranged;
        ret.magic = magic;

        switch (ret.skill) {
            case FPArchMage.BigBang:
            case ILArchMage.BigBang:
            case Bishop.BigBang:
            case Brawler.CorkscrewBlow:
            case Gunslinger.Grenade:
                ret.charge = packet.readInt();
                break;
            default:
                ret.charge = 0;
                break;
        }

        if (ret.skill == Paladin.HeavensHammer) {
            ret.isHH = true;
        }
        ret.option = packet.readByte();
        //ret.action = (byte) (mask & 0x7F);
        //ret.facesLeft = (mask >> 7) == 1;
        ret.mask = packet.readByte() & 0xFF;
        ret.attackType = packet.readByte();

        if (ret.skill == ChiefBandit.MesoExplosion) {
            return parseMesoExplosion(packet, ret);
        }
        if (ranged) {
            ret.consumeSlot = packet.readShort();
            ret.range = packet.readByte();//mastey
            switch (ret.skill) {
                case Bowmaster.Hurricane:
                case Marksman.PiercingArrow:
                case Corsair.RapidFire:
                    packet.skip(4);
                    break;
                default:
                    break;
            }
        }

        int calcDmgMax;
        if (magic && ret.skill != 0) {
            calcDmgMax = (p.getStat().getTotalMagic() * p.getStat().getTotalMagic() / 1000 + p.getStat().getTotalMagic()) / 30 + p.getStat().getTotalInt() / 200;
        } else if (ret.skill == 4001344) {
            calcDmgMax = (p.getStat().getTotalLuk() * 5) * p.getStat().getTotalWatk() / 100;
        } else if (ret.skill == DragonKnight.DragonRoar) {
            calcDmgMax = (p.getStat().getTotalStr() * 4 + p.getStat().getTotalDex()) * p.getStat().getTotalWatk() / 100;
        } else if (ret.skill == Shadower.VenomousStab) {
            calcDmgMax = (int) (18.5 * (p.getStat().getTotalStr() + p.getStat().getTotalLuk()) + p.getStat().getTotalDex() * 2) / 100 * p.getStat().calculateMaxBaseDamage(p.getStat().getTotalWatk());
        } else {
            calcDmgMax = p.getStat().calculateMaxBaseDamage(p.getStat().getTotalWatk());
        }

        boolean canCrit = false;
        if (p.getJob().isA((PlayerJob.BOWMAN)) || p.getJob().isA(PlayerJob.THIEF) || p.getJob() == PlayerJob.MARAUDER || p.getJob() == PlayerJob.BUCCANEER) {
            canCrit = true;
        }

        boolean shadowPartner = false;
        if (p.getBuffEffect(BuffStat.ShadowPartner) != null) {
            shadowPartner = true;
        }

        if (ret.skill != 0) {
            int fixed = ret.getAttackEffect(p, PlayerSkillFactory.getSkill(ret.skill)).getFixDamage();
            if (fixed > 0) {
                calcDmgMax = fixed;
            }
        }
        int mfgj = 0;
        //[69 00 00 00] [06 01 00 00] [A3 01 8B 01] [A3 01 8B 01] [89 01] 06 00 00 00 8F 01 8B 01
        //[7C 00 00 00] [06] [81] [01 00] [9F 03 D7 00] [9F 03 D7 00] [FD 02] 1D 03 00 00 20 06 00 00 90 02 D7 00
        //[7D 00 00 00] [06] [80] 00 00 06 02 D7 00 0A 02 D7 00 E8 03 [11 03 00 00] 69 02 D7 00
        //[78 00 00 00] [06] [80] 02 00 DB 02 D7 00 DF 02 D7 00 62 02 82 05 00 00 2B 02 D7 00 
        for (int i = 0; i < ret.numAttacked; i++) {
            int objectId = packet.readInt();
            ret.hitAction = packet.readByte();
            byte b = packet.readByte();
            //ret.foreAction = (byte) (b & 0x7F);
            //ret.facesLeft = b >> 7 == 1;
            packet.skip(12);
            List<Integer> allDamageNumbers = new ArrayList<>();

            MapleMonster monster = p.getMap().getMonsterByOid(objectId);

            if (p.getBuffEffect(BuffStat.Charges) != null) {
                int sourceID = p.getBuffSource(BuffStat.Charges);
                int level = p.getBuffedValue(BuffStat.Charges);
                if (monster != null) {
                    switch (sourceID) {
                        case WhiteKnight.BwFireCharge:
                        case WhiteKnight.SwordFireCharge:
                            if (monster.getStats().getEffectiveness(Element.FIRE) == ElementalEffectiveness.WEAK) {
                                calcDmgMax *= 1.05 + level * 0.015;
                            }
                            break;
                        case WhiteKnight.BwIceCharge:
                        case WhiteKnight.SwordIceCharge:
                            if (monster.getStats().getEffectiveness(Element.ICE) == ElementalEffectiveness.WEAK) {
                                calcDmgMax *= 1.05 + level * 0.015;
                            }
                            break;
                        case WhiteKnight.BwLitCharge:
                        case WhiteKnight.SwordLitCharge:
                            if (monster.getStats().getEffectiveness(Element.LIGHTING) == ElementalEffectiveness.WEAK) {
                                calcDmgMax *= 1.05 + level * 0.015;
                            }
                            break;
                        case Paladin.BwHolyCharge:
                        case Paladin.SwordHolyCharge:
                            if (monster.getStats().getEffectiveness(Element.HOLY) == ElementalEffectiveness.WEAK) {
                                calcDmgMax *= 1.2 + level * 0.015;
                            }
                            break;
                        default:
                            break;
                    }
                } else {
                    calcDmgMax *= 1.5;
                }
            }

            if (ret.skill != 0) {
                PlayerSkill skill = PlayerSkillFactory.getSkill(ret.skill);
                if (ret.skill == Hermit.ShadowWeb) {
                    if (monster != null) {
                        calcDmgMax = monster.getHp() / (50 - p.getSkillLevel(skill));
                    }
                }
            }
            for (int j = 0; j < ret.numDamage; j++) {
                int damage = packet.readInt();
                int hitDmgMax = calcDmgMax;
                if (ret.skill == Buccaneer.Barrage) {
                    if (j > 3) {
                        hitDmgMax *= Math.pow(2, (j - 3));
                    }
                }
                if (shadowPartner) {
                    if (j >= ret.numDamage / 2) {
                        hitDmgMax *= 0.5;
                    }
                }
                if (ret.skill == Marksman.Snipe) {
                    damage = 95000 + Randomizer.nextInt(4999);
                    hitDmgMax = 99999;
                }
                //checkDamage(p, damage, ret);
                allDamageNumbers.add(damage);
            }
            ret.allDamage.put(objectId, allDamageNumbers);
        }
        if (packet.available() >= 4) {
            ret.pos = packet.readPos();
        }
        return ret;
    }

    public static void checkDamage(Player p, int damage, AttackInfo ret) {
        if ((damage > p.getStat().calculateWorkingDamageTotal(p.getStat().getTotalWatk()) * 1.3) && ret.skill == 0) {
            switch (p.getJob().getId()) {
                case 311:
                case 312:
                case 321:
                case 322:
                case 410:
                case 411:
                case 412:
                case 500:
                case 512:
                case 520:
                case 521:
                    CheatingOffense.DAMAGE_HACK.cheatingSuspicious(p, "Player with damage " + damage + " with rangeattack "
                            + p.getStat().calculateWorkingDamageTotal(p.getStat().getTotalWatk()) + " using a basic attack. Player was level " + p.getLevel()
                            + ". With the JobID " + p.getJob().getId() + "\n");
                    break;
            }
        } else if ((damage > p.getStat().calculateWorkingDamageTotal(p.getStat().getTotalWatk())) && ret.skill == 0 && p.getJob().getId() < 300) {
            CheatingOffense.DAMAGE_HACK.cheatingSuspicious(p, "Player with damage  " + damage + " with rangeattack "
                    + p.getStat().calculateWorkingDamageTotal(p.getStat().getTotalWatk()) + " using a basic attack. Player was level " + p.getLevel()
                    + ". CWith the JobID " + p.getJob().getId() + "\n");
        }
    }

    public static AttackInfo parseMesoExplosion(InPacket r, AttackInfo ret) {
        // [77 00 00 00] 06 00 02 00 46 04 D7 00 42 04 D7 00 01 
        // [77 00 00 00] AC 03 D7 00 01 AF 00 00 00 01 63 02
        //1A 00 3E 41 40 00 00 35 04 39 05 D7 00 01 94 00 00 00 00 63 02
        //1A 00 3E 41 40 00 00 B5 04 A8 04 25 00 02 97 00 00 00 00 98 00 00 00 00 63 02
        if (ret.numAttackedAndDamage == 0) {
            r.skip(4);
            int bullets = r.readByte();
            for (int j = 0; j < bullets; j++) {
                int mesoid = r.readInt();
                r.skip(1);
                ret.allDamage.put(mesoid, null);
            }
            return ret;
        }
        for (int i = 0; i < ret.numAttacked + 1; i++) {
            if (i < ret.numAttacked) {
                int oid = r.readInt();
                r.skip(12);
                int bullets = r.readByte();
                List<Integer> allDamageNumbers = new ArrayList<>();
                for (int j = 0; j < bullets; j++) {
                    allDamageNumbers.add(r.readInt());
                }
                ret.allDamage.put(oid, allDamageNumbers);
            } else {
                r.skip(4);
                int bullets = r.readByte();
                for (int j = 0; j < bullets; j++) {
                    int mesoid = r.readInt();
                    r.skip(1);
                    ret.allDamage.put(mesoid, null);
                }
            }
        }
        return ret;
    }
}
