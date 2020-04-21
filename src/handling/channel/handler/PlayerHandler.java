package handling.channel.handler;

import client.Client;
import client.player.PlayerKeyBinding;
import client.player.Player;
import client.player.PlayerJob;
import client.player.PlayerStat;
import client.player.buffs.BuffStat;
import client.player.inventory.types.InventoryType;
import client.player.inventory.Item;
import client.player.inventory.types.WeaponType;
import client.player.skills.PlayerSkill;
import client.player.skills.PlayerSkillFactory;
import client.player.skills.PlayerSkillMacro;
import static configure.Gamemxd.丢金币间隔;
import static configure.Gamemxd.副本;
import static configure.Gamemxd.合伙群;
import static configure.Gamemxd.恢复药水;
import static configure.Gamemxd.过图间隔;
import static configure.Gamemxd.金银岛主城;
import security.violation.CheatingOffense;
import constants.GameConstants;
import constants.ItemConstants;
import constants.SkillConstants;
import constants.SkillConstants.Archer;
import constants.SkillConstants.Assassin;
import constants.SkillConstants.Bishop;
import constants.SkillConstants.Bowmaster;
import constants.SkillConstants.Brawler;
import constants.SkillConstants.ChiefBandit;
import constants.SkillConstants.Corsair;
import constants.SkillConstants.Crusader;
import constants.SkillConstants.DarkKnight;
import constants.SkillConstants.DragonKnight;
import constants.SkillConstants.FPArchMage;
import constants.SkillConstants.FPMage;
import constants.SkillConstants.Gunslinger;
import constants.SkillConstants.Hermit;
import constants.SkillConstants.Hero;
import constants.SkillConstants.Hunter;
import constants.SkillConstants.ILArchMage;
import constants.SkillConstants.Marauder;
import constants.SkillConstants.Marksman;
import constants.SkillConstants.Paladin;
import constants.SkillConstants.Priest;
import constants.SkillConstants.Rogue;
import constants.SkillConstants.WhiteKnight;
import static console.MsgServer.QQMsgServer.sendMsgToQQGroup;
import static handling.channel.handler.ChannelHeaders.PlayerHeaders.*;
import static handling.channel.handler.DamageParse.*;
import packet.transfer.read.InPacket;
import handling.world.service.BroadcastService;
import java.awt.Point;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static launch.Start.地图是否通行;
import static launch.Start.大区;
import static launch.Start.角色恢复检测;
import static launch.Start.角色无敌检测;
import packet.creators.EffectPackets;
import packet.creators.MonsterPackets;
import packet.creators.PacketCreator;
import packet.transfer.write.OutPacket;
import scripting.npc.NPCScriptManager;
import server.MapleStatEffect;
import server.itens.InventoryManipulator;
import server.itens.ItemInformationProvider;
import server.life.MapleMonster;
import server.life.MobAttackInfo;
import server.life.MobAttackInfoFactory;
import server.life.MobSkill;
import server.life.MobSkillFactory;
import server.life.status.MonsterStatus;
import server.life.status.MonsterStatusEffect;
import server.maps.Field;
import server.maps.FieldLimit;
import server.maps.object.FieldObjectType;
import server.movement.LifeMovementFragment;
import server.maps.portal.Portal;

public class PlayerHandler {

    /**
     * <角色丢出金币>* @param packet
     *
     * @param c
     */
    public static final void DropMeso(final InPacket packet, final Client c) {
        Player p = c.getPlayer();
        final int meso = packet.readInt();
        if ((meso < 10 || meso > 50000) || (meso > p.getMeso()) || p.getCheatTracker().Spam(500, 2)) {
            c.write(PacketCreator.EnableActions());
            return;
        }
        if (System.currentTimeMillis() - c.getPlayer().getNpcCooldown() < 丢金币间隔) {
            c.getPlayer().dropMessage(1, "操作过快，请你慢点。");
            c.write(PacketCreator.EnableActions());
            return;
        }

        p.gainMeso(-meso, false, true);
        p.getMap().spawnMesoDrop(meso, p.getPosition(), p, p, true, (byte) 0);
        c.getPlayer().setmesoCooldown(System.currentTimeMillis());
    }

    /**
     * <地图切换,切图>
     */
    public static void ChangeMap(InPacket packet, Client c) {
        //15 [02] [FF FF FF FF] [06 00 77 65 73 74 30 30] EF FF 13 01 00 00
        //15 [02] [FF FF FF FF] 06 00 65 61 73 74 30 30 8D 0A 8B 01 00 00
        //15 00 FF FF FF FF 05 00 6F 75 74 30 30 14 04 6D 01 00 00
        Player p = c.getPlayer();
        if (地图是否通行(c.getPlayer().getWorldId()) > 0) {
            c.getPlayer().dropMessage(1, "被神秘的力量阻止了。");
            c.announce(PacketCreator.EnableActions());
            return;
        }
        if (副本(c.getPlayer().getMapId())) {
            c.announce(PacketCreator.EnableActions());
            return;
        }

        if (System.currentTimeMillis() - c.getPlayer().getMapCooldown() < 过图间隔) {
            c.getPlayer().dropMessage(5, "操作过快，请你慢点。");
            c.announce(PacketCreator.EnableActions());
            return;
        }

        if (packet.available() != 0) {
            packet.readByte();
            int dest = packet.readInt();
            String portalName = packet.readMapleAsciiString();
            Portal portal = p.getMap().getPortal(portalName);
            if (c.getPlayer().getGM() > 0) {
                c.getPlayer().dropMessage(5, "handling/channel/handler/ChangeMap/" + c.getPlayer().getMap().getStreetName() + "/" + c.getPlayer().getMap().getMapName() + "/" + c.getPlayer().getMapId() + "/" + portalName + "");
            }

            c.getPlayer().setMapCooldown(System.currentTimeMillis());
            if (dest != -1 && !p.isAlive()) {
                boolean executeStandardPath = true;
                if (p.getEventInstance() != null) {
                    executeStandardPath = p.getEventInstance().revivePlayer(p);
                }
                if (executeStandardPath) {
                    if (p.getMCPQField() != null) {
                        p.getMCPQField().onPlayerRespawn(p);
                        return;
                    }
                    p.cancelAllBuffs();
                    p.getStat().setHp(50);
                    p.updatePartyMemberHP();
                    final Field to = p.getMap().getReturnField();
                    p.changeMap(to, to.getRandomPlayerSpawnpoint());
                }
            } else if (dest != -1 && p.isGameMaster()) {
                final Field to = p.getWarpMap(dest);
                p.changeMap(to, to.getPortal(0));
            } else if (dest != -1 && !p.isGameMaster()) {
                p.getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT, "玩家 " + p.getName() + " 尝试不移动就过地图");
            } else {
                if (portal != null) {
                    portal.enterPortal(c);
                } else {
                    c.announce(PacketCreator.EnableActions());
                }
            }
            //每日拜访
            if (金银岛主城(c.getPlayer().getMapId())) {
                String x = "每日任务_拜访金银岛主城" + c.getPlayer().getMapId() + "";
                if (c.getPlayer().getbosslog(x) <= 0) {
                    c.getPlayer().setbosslog(x);
                }
            }
            if (p.主播() > 0) {
                c.getPlayer().getMap().dropMessage(5, "" + c.getPlayer().getName() + " 进入 " + c.getPlayer().getMap().getMapName() + " 地图。");
            }
            //c.getPlayer().刷新身上装备附魔汇总数据(false);
        }
    }

    /**
     * <近战攻击> @param packet
     *
     * @param c
     */
    public static void MeleeAttack(InPacket packet, Client c) {
        Player p = c.getPlayer();
        if (p == null) {
            return;
        }

        AttackInfo attack = DamageParse.parseDamage(packet, p, false, false);
        int skillId = attack.skill;

        p.getMap().broadcastMessage(p, PacketCreator.CloseRangeAttack(p.getId(), attack.skill, attack.skillLevel, attack.mask, attack.attackType, attack.numAttackedAndDamage, attack.allDamage, attack.hitAction));

        int numFinisherOrbs = 0;
        Integer comboBuff = p.getBuffedValue(BuffStat.ComboAttack);
        if (SkillConstants.isFinisherSkill(attack.skill)) {
            if (comboBuff != null) {
                try {
                    numFinisherOrbs = comboBuff - 1;
                } catch (ArrayIndexOutOfBoundsException e) {
                }
            }
            p.handleOrbconsume();
        } else if (attack.numAttacked > 0) {
            if (comboBuff != null) {
                if (attack.skill != Crusader.Shout) {
                    p.handleOrbgain();
                }
            } else if ((p.getJob().equals(PlayerJob.BUCCANEER) || p.getJob().equals(PlayerJob.MARAUDER)) && p.getSkillLevel(PlayerSkillFactory.getSkill(Marauder.EnergyCharge)) > 0) {
                for (int i = 0; i < attack.numAttacked; i++) {
                    p.energyChargeGain();
                }
            }
        }
        if (attack.numAttacked > 0 && attack.skill == DragonKnight.Sacrifice) {
            int totDamageToOneMonster = 0;
            final Iterator<List<Integer>> dmgIt = attack.allDamage.values().iterator();
            if (dmgIt.hasNext()) {
                totDamageToOneMonster = dmgIt.next().get(0);
            }
            int remainingHP = p.getStat().getHp() - totDamageToOneMonster * attack.getAttackEffect(p).getX() / 100;
            if (remainingHP > 1) {
                p.getStat().setHp(remainingHP);
            } else {
                p.getStat().setHp(1);
            }
            p.getStat().updateSingleStat(PlayerStat.HP, p.getStat().getHp());
        }
        if (attack.numAttacked > 0 && attack.skill == WhiteKnight.ChargeBlow) {
            boolean advchargeProb = false;
            int advchargeLevel = p.getSkillLevel(PlayerSkillFactory.getSkill(1220010));
            if (advchargeLevel > 0) {
                advchargeProb = PlayerSkillFactory.getSkill(1220010).getEffect(advchargeLevel).makeChanceResult();
            }
            if (!advchargeProb) {
                p.cancelEffectFromBuffStat(BuffStat.Charges);
            }
        }
        int maxDamage = p.getStat().getCurrentMaxBaseDamage();
        int attackCount = 1;
        if (skillId != 0) {
            MapleStatEffect effect = attack.getAttackEffect(p);
            attackCount = effect.getAttackCount();
            maxDamage *= effect.getDamage() / 100.0;
            maxDamage *= attackCount;
        }
        maxDamage = Math.min(maxDamage, 99999);
        if (skillId == ChiefBandit.MesoExplosion) {
            maxDamage = 70000;
        } else if (numFinisherOrbs > 0) {
            maxDamage *= numFinisherOrbs;
        } else if (comboBuff != null) {
            PlayerSkill combo = PlayerSkillFactory.getSkill(Crusader.斗气集中);
            maxDamage *= (double) 1.0 + (combo.getEffect(p.getSkillLevel(combo)).getDamage() / 100.0 - 1.0) * (comboBuff - 1);
        }
        if (numFinisherOrbs == 0 && SkillConstants.isFinisherSkill(skillId)) {
            return;
        }
        if (SkillConstants.isFinisherSkill(skillId)) {
            maxDamage = 99999;
        }
        DamageParse.applyAttack(attack, p, maxDamage, attackCount);
    }

    /**
     * <远程攻击>
     */
    public static void RangedAttack(InPacket packet, Client c) {
        Player p = c.getPlayer();
        AttackInfo attack = parseDamage(packet, p, true, false);
        if (attack == null) {
            if (GameConstants.USE_DEBUG) {
                System.out.println("Player {" + p.getName() + "} null attack {RangedAttack}");
            }
            c.write(PacketCreator.EnableActions());
            return;
        }
        int skillId = attack.skill;
        PlayerSkill skill = PlayerSkillFactory.getSkill(skillId);

        Item weapon = p.getInventory(InventoryType.EQUIPPED).getItem((byte) -11);
        ItemInformationProvider mii = ItemInformationProvider.getInstance();
        WeaponType type = ItemInformationProvider.getWeaponType(weapon.getItemId());

        int projectile = 0;
        int bulletCount = 1;
        MapleStatEffect effect = null;
        if (skillId != 0) {
            effect = attack.getAttackEffect(p);
            bulletCount = effect.getBulletCount();
        }

        boolean hasShadowPartner = p.getBuffedValue(BuffStat.ShadowPartner) != null;
        int damageBulletCount = bulletCount;
        if (hasShadowPartner) {
            bulletCount *= 2;
        }

        if (attack.consumeSlot != 0) {
            Item item = p.getInventory(InventoryType.USE).getItem((byte) attack.consumeSlot);
            if (item != null) {
                boolean clawCondition = type == WeaponType.CLAW && ItemConstants.isThrowingStar(item.getItemId()) && weapon.getItemId() != 1472063;
                boolean bowCondition = type == WeaponType.BOW && ItemConstants.isArrowForBow(item.getItemId());
                boolean crossbowCondition = type == WeaponType.CROSSBOW && ItemConstants.isArrowForCrossBow(item.getItemId());

                if ((clawCondition || bowCondition || crossbowCondition) && item.getQuantity() >= bulletCount) {
                    projectile = item.getItemId();
                }
            }
        }
        boolean soulArrow = p.getBuffedValue(BuffStat.SoulArrow) != null;
        if (!soulArrow && !c.getPlayer().isGameMaster()) {
            int bulletConsume = bulletCount;
            if (effect != null && effect.getBulletConsume() != 0) {
                bulletConsume = effect.getBulletConsume() * (hasShadowPartner ? 2 : 1);
            }
            InventoryManipulator.removeById(c, InventoryType.USE, projectile, bulletConsume, false, true);
        }
        if (projectile != 0 || soulArrow || skillId == Hermit.ShadowMeso) {
            int visProjectile = projectile;
            if (mii.isThrowingStar(projectile)) {
                for (int i = 0; i < 255; i++) {
                    Item item = p.getInventory(InventoryType.CASH).getItem((byte) i);
                    if (item != null) {
                        if (item.getItemId() / 1000 == 5021) {
                            visProjectile = item.getItemId();
                            break;
                        }
                    }
                }
            } else if (soulArrow || skillId == Hermit.ShadowMeso) {
                visProjectile = skillId;
            }
            int stance = attack.attackType;
            switch (skillId) {
                case Bowmaster.Hurricane:
                case Marksman.PiercingArrow:
                case Corsair.RapidFire:
                    stance = attack.direction;
                    break;
            }
            p.getMap().broadcastMessage(p, PacketCreator.RangedAttack(p.getId(), attack.skill, attack.skillLevel, attack.mask, attack.attackType, attack.numAttackedAndDamage, visProjectile, attack.allDamage, attack.hitAction, attack.range));
            int baseDamage;
            int projectileWatk = 0;
            int totalWatk = p.getStat().getTotalWatk();
            if (projectile != 0) {
                projectileWatk = mii.getWatkForProjectile(projectile);
            }
            if (skillId != Rogue.LuckySeven) {
                baseDamage = (projectileWatk != 0) ? p.getStat().calculateMaxBaseDamage(totalWatk + projectileWatk) : p.getStat().getCurrentMaxBaseDamage();
            } else {
                baseDamage = (int) (((p.getStat().getTotalLuk() * 5.0) / 100.0) * (totalWatk + projectileWatk));
            }
            if (skillId == Hunter.ArrowBomb) {
                if (effect != null) {
                    baseDamage *= effect.getX() / 100.0;
                }
            }
            // Todo
            double critdamagerate = 0.0;
            if (p.getJob().isA(PlayerJob.ASSASSIN)) {
                PlayerSkill criticalthrow = PlayerSkillFactory.getSkill(Assassin.CriticalThrow);
                if (p.getSkillLevel(criticalthrow) > 0) {
                    critdamagerate = (criticalthrow.getEffect(p.getSkillLevel(criticalthrow)).getDamage() / 100.0);
                }
            } else if (p.getJob().isA(PlayerJob.BOWMAN)) {
                PlayerSkill criticalshot = PlayerSkillFactory.getSkill(Archer.CriticalShot);
                int critlevel = p.getSkillLevel(criticalshot);
                if (critlevel > 0) {
                    critdamagerate = (criticalshot.getEffect(critlevel).getDamage() / 100.0) - 1.0;
                }
            }
            int maxDamage = baseDamage;
            if (effect != null) {
                maxDamage *= effect.getDamage() / 100.0;
            }
            maxDamage += (int) (baseDamage * critdamagerate);
            maxDamage *= damageBulletCount;
            if (hasShadowPartner) {
                PlayerSkill shadowPartner = PlayerSkillFactory.getSkill(Hermit.ShadowPartner);
                MapleStatEffect shadowPartnerEffect = shadowPartner.getEffect(p.getSkillLevel(shadowPartner));
                maxDamage *= (skillId != 0) ? (1.0 + shadowPartnerEffect.getY() / 100.0) : (1.0 + shadowPartnerEffect.getX() / 100.0);
            }
            if (skillId == Hermit.ShadowMeso) {
                maxDamage = 35000;
            }
            if (effect != null) {
                int money = effect.getMoneyCon();
                if (money != 0) {
                    money = (int) (money + Math.random() * (money * 0.5));
                    if (money > p.getMeso()) {
                        money = p.getMeso();
                    }
                    p.gainMeso(-money, false);
                }
            }
            applyAttack(attack, p, maxDamage, bulletCount);
        }
    }

    /**
     * <魔法攻击>
     */
    public static void MagicDamage(InPacket packet, Client c) {
        Player p = c.getPlayer();
        //1C 11 1B 36 20 00 00 A9 06 7D 00 00 00 06 80 00 00 06 02 D7 00 0A 02 D7 00 E8 03 11 03 00 00 69 02 D7 00
        //1C 01 1B 36 20 00 00 A9 06 [33 04] [25 00]
        AttackInfo attack = parseDamage(packet, p, false, true);
        if (attack == null) {
            if (GameConstants.USE_DEBUG) {
                System.out.println("Player {" + p.getName() + "} null attack {MagicDamage}");
            }
            c.write(PacketCreator.EnableActions());
            return;
        }

        final PlayerSkill skill = PlayerSkillFactory.getSkill(attack.skill);

        int charge = -1;
        switch (attack.skill) {
            case FPArchMage.BigBang:
            case ILArchMage.BigBang:
            case Bishop.BigBang:
                charge = attack.charge;
                break;
        }
        p.getMap().broadcastMessage(p, PacketCreator.MagicAttack(p.getId(), attack.skill, attack.skillLevel, attack.mask, attack.attackType, attack.numAttackedAndDamage, attack.allDamage, charge, attack.hitAction));

        int maxdamage = (int) (((p.getStat().getTotalMagic() * 0.8) + (p.getStat().getLuk() / 4) / 18) * skill.getEffect(p.getSkillLevel(skill)).getDamage() * 0.8 * (p.getMasterLevel(skill) * 10 / 100));
        if (attack.numDamage > maxdamage) {
            maxdamage = 99999;
        }
        applyAttack(attack, p, maxdamage, attack.getAttackEffect(p).getAttackCount());

        PlayerSkill eaterSkill = PlayerSkillFactory.getSkill((p.getJob().getId() - (p.getJob().getId() % 10)) * 10000);
        int eaterLevel = p.getSkillLevel(eaterSkill);
        if (eaterLevel > 0) {
            for (Integer singleDamage : attack.allDamage.keySet()) {
                eaterSkill.getEffect(eaterLevel).applyPassive(p, p.getMap().getMapObject(singleDamage, FieldObjectType.MONSTER), 0);
            }
        }
        if (attack.skill == FPMage.PoisonMist) {
            int slv = p.getSkillLevel(attack.skill);
            if (slv > 0 && attack.pos != null) {
                skill.getEffect(slv).applyTo(p, attack.pos);
            }
        }
    }

    /**
     * <受到伤害>
     */
    public static void TakeDamage(InPacket packet, Client c) {
        //1E [FF] [01 00 00 00] [00] [67 00 00 00] [04 87 01 00] 01 00
        //1E FF 00 00 00 00 00 78 00 00 00 C0 65 52 00 01 00
        //1E FF 8A 00 00 00 00 7A 00 00 00 C0 65 52 00 01 00
        //   [00] [17 10 00 00] [01] [00 00 00 00] [49 42 0F 00] F0 5B 7C 00 00 00 
        Player p = c.getPlayer();
        int damagefrom = packet.readByte();
        //受到的伤害数值
        int damage = packet.readInt();
        boolean magic = packet.readByte() == 1;
        if (magic) {
            packet.readInt();
        }
        int objectId = 0, monsterIdFrom = 0, pgmr = 0, mpAttack = 0;
        int direction = 0, posX = 0, posY = 0, fake = 0;
        boolean isPgmr = false, isPg = true;

        if (GameConstants.USE_DEBUG) {
            System.out.println("Takedamaged from player {" + p.getName() + "}");
        }

        MapleMonster attacker = null;
        final Field map = p.getMap();
        if (p.getGM() > 0) {
            return;
        }

        if (p.无敌() > 0) {
            p.无敌(-1);
            return;
        }

        if (p.switch_skill() == 0) {
            p.announce(EffectPackets.ShowEffect("s/2"));
        }
        if (damagefrom != -2) {
            objectId = packet.readInt();
            monsterIdFrom = packet.readInt();

            attacker = (MapleMonster) p.getMap().getMapObject(objectId, FieldObjectType.MONSTER);

            if ((p.getMap().getMonsterById(monsterIdFrom) == null || attacker == null) && monsterIdFrom != 9300166) {
                return;
            }
            //被指定怪物攻击
            if (monsterIdFrom == 9300166) {
                if (p.haveItem(4031868)) {
                    if (p.getItemQuantity(4031868, false) > 1) {
                        int amount = p.getItemQuantity(4031868, false) / 2;
                        Point position = new Point(c.getPlayer().getPosition().x, c.getPlayer().getPosition().y);
                        InventoryManipulator.removeById(c, ItemInformationProvider.getInstance().getInventoryType(4031868), 4031868, amount, false, false);
                        for (int i = 0; i < amount; i++) {
                            position.setLocation(c.getPlayer().getPosition().x + (i % 2 == 0 ? (i * 15) : (-i * 15)), c.getPlayer().getPosition().y);
                            map.spawnItemDrop(p, p, new Item(4031868, (short) 0, (short) 1), map.calcDropPos(position, p.getPosition()), true, true);
                        }
                    } else {
                        InventoryManipulator.removeById(c, ItemInformationProvider.getInstance().getInventoryType(4031868), 4031868, 1, false, false);
                        map.spawnItemDrop(p, p, new Item(4031868, (short) 0, (short) 1), c.getPlayer().getPosition(), true, true);
                    }
                }
            }
            direction = packet.readByte();
        }

        if (attacker != null && GameConstants.TRACK_MISSGODMODE) {
            if (damage < 1) {
                final double difference = (double) Math.max(p.getLevel() - attacker.getLevel(), 0);
                final double chanceToBeHit = (double) attacker.getAccuracy() / ((1.84d + 0.07d * difference) * (double) p.getStat().getTotalEva()) - 1.0d;
                if (chanceToBeHit > 0.85d) {
                    p.getCheatTracker().incrementNumGotMissed();
                }
            } else {
                p.getCheatTracker().setNumGotMissed(0);
            }
            if (角色无敌检测) {
                if (p.getCheatTracker().getNumGotMissed() == 15) {
                    p.dropMessage(1, "数据异常，网络中断。");
                    p.bans("无敌状态 " + p.getCheatTracker().getNumGotMissed() + "");
                    p.getClient().close();
                    sendMsgToQQGroup(""
                            + "QQ：" + p.getQQ() + "\r\n"
                            + "大区：" + 大区(p.getWorldId()) + "\r\n"
                            + "玩家：" + p.getName() + "\r\n"
                            + "地图：" + p.getMapName(p.getMapId()) + "\r\n"
                            + "原因：无敌状态\r\n"
                            + "无伤次数：" + p.getCheatTracker().getNumGotMissed() + "", 合伙群);
                }
            }
        }

        if (damagefrom != -1 && damagefrom != -2 && attacker != null) {
            final MobAttackInfo attackInfo = MobAttackInfoFactory.getInstance().getMobAttackInfo(attacker, damagefrom);
            if (attackInfo.isDeadlyAttack()) {
                mpAttack = p.getStat().getMp() - 1;
            }
            mpAttack += attackInfo.getMpBurn();
            MobSkill skill = MobSkillFactory.getMobSkill(attackInfo.getDiseaseSkill(), attackInfo.getDiseaseLevel());
            if (skill != null && damage > 0) {
                skill.applyEffect(p, attacker, false);
            }
            if (attacker != null) {
                attacker.setMp(attacker.getMp() - attackInfo.getMpCon());
            }
        }

        if (damage == -1) {
            int job = (int) (p.getJob().getId() / 10 - 40);
            fake = 4020002 + (job * 100000);
            if (damagefrom == -1 && damagefrom != -2 && p.getInventory(InventoryType.EQUIPPED).getItem((byte) -10) != null) {
                int[] guardianSkillId = {1120005, 1220006};
                for (int guardian : guardianSkillId) {
                    PlayerSkill guardianSkill = PlayerSkillFactory.getSkill(guardian);
                    if (p.getSkillLevel(guardianSkill) > 0 && attacker != null) {
                        MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(Collections.singletonMap(MonsterStatus.STUN, 1), guardianSkill, false);
                        attacker.applyStatus(p, monsterStatusEffect, false, 2 * 1000);
                    }
                }
            }
        }

        if (damage > 0) {
            if (attacker != null && !attacker.isBoss()) {
                if (damagefrom == BUMP_DAMAGE && p.getBuffedValue(BuffStat.PowerGuard) != null) {
                    int bouncedamage = (int) (damage * (p.getBuffedValue(BuffStat.PowerGuard).doubleValue() / 100));
                    bouncedamage = Math.min(bouncedamage, attacker.getMaxHp() / 10);
                    p.getMap().damageMonster(p, attacker, bouncedamage);
                    damage -= bouncedamage;
                    p.getMap().broadcastMessage(MonsterPackets.DamageMonster(objectId, bouncedamage));
                    p.checkMonsterAggro(attacker);
                }
            }
            if (damagefrom != MAP_DAMAGE) {
                int achilles = 0;
                PlayerSkill achilles1 = null;
                int jobid = p.getJob().getId();
                if (jobid < 200 && jobid % 10 == 2) {
                    achilles1 = PlayerSkillFactory.getSkill(jobid * 10000 + jobid == 112 ? 4 : 5);
                    achilles = p.getSkillLevel(achilles);
                }
                if (achilles != 0 && achilles1 != null) {
                    damage *= (int) (achilles1.getEffect(achilles).getX() / 1000.0 * damage);
                }
            }
            Integer mesoguard = p.getBuffedValue(BuffStat.MesoGuard);
            if (p.getBuffedValue(BuffStat.MagicGuard) != null && mpAttack == 0) {
                int mploss = (int) (damage * (p.getBuffedValue(BuffStat.MagicGuard).doubleValue() / 100.0));
                int hploss = damage - mploss;
                if (mploss > p.getStat().getMp()) {
                    hploss += mploss - p.getStat().getMp();
                    mploss = p.getStat().getMp();
                }
                p.getStat().addMPHP(-hploss, -mploss);
            } else if (mesoguard != null) {
                int originDamage = damage;
                damage = (int) (damage * (mesoguard / 100d));
                int mesoLoss = p.getBuffEffect(BuffStat.MesoGuard).getMoneyCon();
                if (p.getMeso() >= mesoLoss) {
                    if (damage > mesoLoss) {
                        p.gainMeso(-mesoLoss, false, true, true);
                        p.cancelBuffStats(BuffStat.MesoGuard);
                    } else {
                        p.gainMeso(-damage, false, true, true);
                        if (p.getMesoGuard() >= mesoLoss) {
                            p.cancelBuffStats(BuffStat.MesoGuard);
                            p.setMesoGuard(0);
                        } else {
                            p.setMesoGuard(p.getMesoGuard() + originDamage);
                        }
                    }
                } else {
                    p.gainMeso(-p.getMeso(), false, true, true);
                    p.cancelBuffStats(BuffStat.MesoGuard);
                }
                p.getStat().addMPHP(-damage, -mpAttack);
            } else {
                //减少生命值
                p.getStat().addMPHP(-damage, -mpAttack);
            }
        }

        if (p.getvip() > 0) {
            if (p.isAlive()) {
                if (p.getRecoveryHP() > 0) {
                    if (p.getHp() < p.getRecoveryHP()) {
                        int HP = 0;
                        HP = p.getRecoveryHPitemid();
                        if (HP > 0) {
                            if (p.haveItem(HP)) {
                                ItemInformationProvider mii = ItemInformationProvider.getInstance();
                                MapleStatEffect statEffect = mii.getItemEffect(HP);
                                if (statEffect != null) {
                                    statEffect.applyTo(p);
                                    p.gainItem(HP, (short) -1, false);
                                }
                            } else {
                                p.dropMessage(5, "自动恢复药品 " + ItemInformationProvider.getInstance().getName(HP) + "使用殆尽，请尽快补充。");
                            }
                        }
                    }
                }
                if (p.技能BUFF(2001002)) {
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
        }

        if (!p.isHidden()) {
            p.getMap().broadcastMessage(p, PacketCreator.DamagePlayer(damagefrom, monsterIdFrom, p.getId(), damage, fake, direction, isPgmr, pgmr, isPg, objectId, posX, posY), false);
            p.getStat().updateSingleStat(PlayerStat.HP, p.getStat().getHp());
            p.getStat().updateSingleStat(PlayerStat.MP, p.getStat().getMp());
            p.checkBerserk(true);
        } else {
            p.getMap().broadcastGMMessage(p, PacketCreator.DamagePlayer(damagefrom, monsterIdFrom, p.getId(), damage, fake, direction, isPgmr, pgmr, isPg, objectId, posX, posY), false);
            p.getStat().updateSingleStat(PlayerStat.HP, p.getStat().getHp());
            p.getStat().updateSingleStat(PlayerStat.MP, p.getStat().getMp());
            p.checkBerserk(false);
        }
    }

    /**
     * <角色移动>**
     */
    public static void MovePlayer(InPacket r, Player p) {
        //18
        // 00
        // 41 FF 70 00
        // 04 00 41 FF 6B 00 00 00 F1 FF 00 00 06 3C 00 00 41 FF B0 00 00 00 0D 02 00 00 06 0E 01 00 41 FF B9 00 00 00 00 00 00 00 06 0F 00 00 41 FF B9 00 00 00 00 00 1C 00 04 A5 00 11 00 00 00 00 00 00 00 00 00
        r.readByte();
        Point ps = r.readPos();
        if (p == null) {
            return;
        }
        final List<LifeMovementFragment> mov = MovementParse.parseMovement(r);
        if (mov != null) {
            MovementParse.updatePosition(mov, p, 0);
            p.getMap().movePlayer(p, p.getPosition());

            OutPacket packet = PacketCreator.MovePlayer(p.getId(), ps, mov);
            if (p.isHidden()) {
                p.getMap().broadcastGMMessage(p, packet, false);
            } else {
                p.getMap().broadcastMessage(p, packet, false);
            }
        }
    }

    /**
     * <角色表情>
     */
    public static void ChangeEmotion(InPacket packet, Client c) {
        int emote = packet.readInt();
        if (emote > 7) {
            final int emoteId = 5159992 + emote;
            final InventoryType type = ItemConstants.getInventoryType(emoteId);
            if (!ItemConstants.isFaceExpression(emoteId) || c.getPlayer().getInventory(type).findById(emoteId) == null) {
                return;
            }
        }
        if (emote > 0 && c.getPlayer() != null && c.getPlayer().getMap() != null) {
            c.getPlayer().getMap().broadcastMessage(c.getPlayer(), EffectPackets.ExpressionChange(c.getPlayer(), emote), false);
        }
    }

    /**
     * <角色每10秒自动恢复>
     */
    public static void ReplenishHpMp(InPacket packet, Client c) {
        Player p = c.getPlayer();
        packet.skip(4);
        if (packet.available() < 4) {
            return;
        }
        int hp = packet.readShort();
        int mp = packet.readShort();

        if (p == null) {
            return;
        }
        if (p.getHp() == 0) {
            return;
        }
        if (p.isActiveBuffedValue(1301007)) {
            return;
        }
        long now = System.currentTimeMillis();
        if ((hp != 0) && (p.canHP(now + 1000L))) {
            if (p.getMapId() != 105040401) {
                if (p.getMapId() != 105040402) {
                    if (hp > 200) {
                        if (角色恢复检测) {
                            p.dropMessage(1, "数据异常，网络中断。");
                            p.bans("过量回复生命值 " + hp + "");
                            p.getClient().close();
                            sendMsgToQQGroup(""
                                    + "QQ：" + p.getQQ() + "\r\n"
                                    + "大区：" + 大区(p.getWorldId()) + "\r\n"
                                    + "玩家：" + p.getName() + "\r\n"
                                    + "地图：" + p.getMapName(p.getMapId()) + "\r\n"
                                    + "原因：过量回复生命值\r\n"
                                    + "效果：" + hp + "",
                                    合伙群);
                        }
                        return;
                    }
                }
            }
            if (p.getEquippedFuMoMap().get(7) != null) {
                hp += p.getEquippedFuMoMap().get(7);
            }
            p.getStat().addHP(hp);
        }
        if (mp != 0 && (p.canMP(now + 1000L))) {
            if (p.getMapId() != 105040401) {
                if (p.getMapId() != 105040402) {
                    if (mp > 400) {
                        if (角色恢复检测) {
                            p.dropMessage(1, "数据异常，网络中断。");
                            p.bans("过量回复魔力值 " + mp + "");
                            p.getClient().close();
                            sendMsgToQQGroup(""
                                    + "QQ：" + p.getQQ() + "\r\n"
                                    + "大区：" + 大区(p.getWorldId()) + "\r\n"
                                    + "玩家：" + p.getName() + "\r\n"
                                    + "地图：" + p.getMapName(p.getMapId()) + "\r\n"
                                    + "原因：过量回复魔力值\r\n"
                                    + "效果：" + mp + "",
                                    合伙群);
                        }
                        return;
                    }
                }
            }
            if (p.getEquippedFuMoMap().get(8) != null) {
                mp += p.getEquippedFuMoMap().get(8);
            }
            p.getStat().addMP(mp);
        }
    }

    /**
     * <玩家个人情报>
     */
    public static void OpenInfo(InPacket packet, Client c) {
        //自己的
        Player p = c.getPlayer();
        //目标ID
        int cid = packet.readInt();
        //目标
        final Player target = (Player) c.getPlayer().getMap().getMapObject(cid, FieldObjectType.PLAYER);
        if (target != null) {
            //打开个人情报面板
            c.write(PacketCreator.PersonalInfo(target, false));
            //打开个人信息面板
            p.设置目标(cid);
            NPCScriptManager.getInstance().dispose(c);
            NPCScriptManager.getInstance().start(c, 9900000, 11);
        }
    }

    /**
     * <移动技能>
     */
    public static void SpecialMove(InPacket packet, Client c) {
        Player p = c.getPlayer();
        //31 [2B 46 0F 00] [14] 00 00
        //[9B BA 3E 00] [05] 03 66 00 00 00 6C 00 00 00 70 00 00 00 C2 01 
        Point pos = null;
        final int skillId = packet.readInt();
        //p.dropMessage("skillId: " + skillId);
        final int skillLevel = packet.readByte();
        PlayerSkill skill = PlayerSkillFactory.getSkill(skillId);

        if (skill.isGMSkill() && !p.isGameMaster()) {
            c.write(PacketCreator.EnableActions());
            return;
        }
        if (p.switch_skill() == 0) {
            p.announce(EffectPackets.ShowEffect("Skill2/" + skillId + ""));
        }
        if (skillLevel != skillLevel) {
            p.getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT, p.getName() + " is using a movement skill that he does not have, ID: " + skill.getId());
            return;
        }

        if (skill.getId() == Hermit.ShadowWeb) {
            byte mobs = packet.readByte();
            int[] mobsObjectid = new int[mobs];
            for (int i = 0; i < mobs; i++) {
                mobsObjectid[i] = packet.readInt();
            }
            skill.getEffect(skillLevel).applyMonsterBuff(p, mobsObjectid);
            return;
        }
        if (packet.available() >= 4) {
            pos = new Point(packet.readShort(), packet.readShort());
        }
        if (p.isAlive()) {
            if (skill.getId() != Priest.MysticDoor) {
                skill.getEffect(skillLevel).applyTo(p, pos);
            } else if (p.canDoor()) {
                if (!FieldLimit.DOOR.check(p.getMap().getFieldLimit())) {
                    p.cancelMagicDoor();
                    skill.getEffect(skillLevel).applyTo(p, pos);
                } else {
                    c.write(PacketCreator.EnableActions());
                }
            } else {
                p.message("请稍等5秒，然后再次施放神秘之门。");
            }
        } else {
            c.write(PacketCreator.EnableActions());
        }
    }

    public static void InnerPortal(InPacket r, Client c) {
        r.readByte();
        String portalName = r.readMapleAsciiString();
        Point startPos = r.readPos();
        Point endPos = r.readPos();

        if (c.getPlayer().getMap().getPortal(portalName) == null) {
            c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT, "Tried to enter the nonexistent portal.");
            return;
        }

        boolean foundPortal = false;
        for (Portal portal : c.getPlayer().getMap().getPortals()) {
            if (portal.getType() == 1 || portal.getType() == 2 || portal.getType() == 10 || portal.getType() == 20) {
                if (portal.getPosition().equals(startPos) || portal.getPosition().equals(endPos)) {
                    foundPortal = true;
                }
            }
        }
        if (!foundPortal) {
            c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.WZ_EDIT, "Used inner portal: " + portalName + " in " + c.getPlayer().getMapId() + " targetPos: " + endPos.toString() + " when it doesn't exist.");
        }
    }

    public static final void TrockAddMap(final InPacket packet, final Client c, final Player p) {
        final byte operation = packet.readByte();

        switch (operation) {
            case 0:
                p.deleteRocks(packet.readInt());
                break;
            case 1:
                if (!FieldLimit.CANNOTVIPROCK.check(p.getMap().getFieldLimit())) {
                    p.addRockMap();
                } else {
                    p.dropMessage(1, "不能添加此地图。");
                }
                break;
            default:
                break;
        }
        c.write(PacketCreator.GetTrockRefresh(p, operation == 3));
    }

    public static void CancelBuffHandler(InPacket packet, Client c) {
        Player p = c.getPlayer();
        if (p == null) {
            return;
        }
        int sourceid = packet.readInt();
        switch (sourceid) {
            case FPArchMage.BigBang:
            case ILArchMage.BigBang:
            case Bishop.BigBang:
            case Bowmaster.Hurricane:
            case Marksman.PiercingArrow:
            case Corsair.RapidFire:
                p.getMap().broadcastMessage(p, PacketCreator.SkillCancel(p, sourceid), false);
                break;
            default:
                p.cancelEffect(PlayerSkillFactory.getSkill(sourceid).getEffect(1), false, -1);
                break;
        }
    }

    public static final void CancelItemEffect(final InPacket packet, final Client c) {
        MapleStatEffect effect = ItemInformationProvider.getInstance().getItemEffect(-packet.readInt());
        c.getPlayer().cancelEffect(effect, false, -1);
    }

    /**
     * <更改快捷键记录>
     */
    public static final void ChangeKeymap(final InPacket r, final Client c) {
        Player p = c.getPlayer();
        int actionType = r.readInt();
        switch (actionType) {
            case BINDING_CHANGE_KEY_MAPPING:
                for (int i = r.readInt(); i > 0; --i) {
                    final int key = r.readInt();
                    final int type = r.readByte();
                    final int action = r.readInt();
                    final PlayerSkill skill = PlayerSkillFactory.getSkill(action);
                    if (skill != null) {
                        if (!p.isGameMaster() && SkillConstants.isGMSkill(skill.getId())) {
                            p.getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT, p.getName() + " 尝试了数据包密钥映射。");
                            return;
                        }
                        if (p.getSkillLevel(skill) < 1) {
                            continue;
                        }
                    }
                    PlayerKeyBinding newbinding = new PlayerKeyBinding(type, action);
                    p.changeKeybinding(key, newbinding);
                }
                break;
            case BINDING_CHANGE_AUTO_HP_POT: {
                int itemId = r.readInt();
                if (itemId == 0) {
                    p.setAutoHpPot(0);
                    return;
                }
                if (!p.haveItem(ItemConstants.HP_ITEM, 1, true, false)) {
                    p.getCheatTracker().registerOffense(CheatingOffense.USING_UNAVAILABLE_ITEM, Integer.toString(itemId));
                    return;
                }

                p.setAutoHpPot(itemId);
                break;
            }
            case BINDING_CHANGE_AUTO_MP_POT: {
                int itemId = r.readInt();
                if (itemId == 0) {
                    p.setAutoMpPot(0);
                    return;
                }
                if (!p.haveItem(ItemConstants.MP_ITEM, 1, true, false)) {
                    p.getCheatTracker().registerOffense(CheatingOffense.USING_UNAVAILABLE_ITEM, Integer.toString(itemId));
                    return;
                }
                p.setAutoMpPot(itemId);
                break;
            }
        }
    }
    public static Map<Integer, Long> 进出市场冷却 = new LinkedHashMap<>();

    /**
     * *<进出市场传送>
     */
    public static final void ChangeMapSpecial(InPacket r, Client c) {
        Player p = c.getPlayer();
        if (p == null) {
            return;
        }
        //从市场外面进市场
        if (System.currentTimeMillis() - 进出市场冷却判断(p.getId()) > 1000 * 3) {
            if (p.getMapId() != 910000000) {
                p.市场传送点(p.getMapId());
                p.changeMap(910000000, 28);
                进出市场冷却.put(p.getId(), System.currentTimeMillis());
            } else if (p.getMapId() == 910000000) {
                switch (p.市场传送点()) {
                    //射手村
                    case 100000100:
                        p.changeMap(100000100, 21);
                        p.市场传送点(0);
                        break;
                    //勇士部落
                    case 102000000:
                        p.changeMap(102000000, 23);
                        p.市场传送点(0);
                        break;
                    //雪域集市
                    case 211000100:
                        p.changeMap(211000100, 9);
                        p.市场传送点(0);
                        break;
                    default:
                        p.changeMap(100000000, 0);
                        p.市场传送点(0);
                        p.dropMessage(1, "传送点异常,已经重置。");
                        break;
                }
                进出市场冷却.put(p.getId(), System.currentTimeMillis());
            }
        } else {
            p.dropMessage(1, "使用传送点过于频繁，请稍后再试试。");
        }
        c.announce(PacketCreator.EnableActions());
    }

    public static long 进出市场冷却判断(int a) {
        if (进出市场冷却.containsKey(a)) {
            return 进出市场冷却.get(a);
        }
        return 1;
    }

    public static void UseItemEffect(InPacket packet, Client c) {
        Player p = c.getPlayer();
        if (p == null) {
            return;
        }
        int itemId = packet.readInt();
        final Item toUse = p.getInventory(InventoryType.CASH).findById(itemId);
        if (toUse == null || toUse.getQuantity() < 1) {
            if (itemId != 0) {
                return;
            }
            p.getCheatTracker().registerOffense(CheatingOffense.USING_UNAVAILABLE_ITEM, Integer.toString(itemId));
            return;
        }
        p.setItemEffect(toUse.getItemId());
        p.getMap().broadcastMessage(p, EffectPackets.ItemEffect(p.getId(), toUse.getItemId()), false);
    }

    /**
     * <使用椅子>
     */
    public static final void UseChair(InPacket packet, Client c) {
        int id = packet.readShort();
        Player p = c.getPlayer();

        if (id == -1) {
            if (p.getChair() != 0) {
                p.setChair(0);
                //p.getMap().broadcastMessage(p, EffectPackets.ShowChair(p.getId(), 0), false);
            }
            p.getClient().write(EffectPackets.RiseFromChair());
        } else {
            p.setChair(id);
            p.getClient().write(EffectPackets.SitOnChair((short) id));
        }
    }

    public static final void UseItemChair(InPacket packet, Client c) {
        Player p = c.getPlayer();
        final int itemId = packet.readInt();
        Item toUse = p.getInventory(InventoryType.SETUP).findById(itemId);
        if (toUse == null) {
            p.getCheatTracker().registerOffense(CheatingOffense.USING_UNAVAILABLE_ITEM, Integer.toString(itemId));
            return;
        } else {
            p.setChair(itemId);
            //p.getMap().broadcastMessage(p, EffectPackets.ShowChair(p.getId(), itemId), false);
        }
        c.write(PacketCreator.EnableActions());
    }

    public static final void SkillEffect(InPacket r, Client c) {
        Player p = c.getPlayer();
        if (p.isHidden()) {
            return;
        }

        final int skillID = r.readInt();
        final int level = r.readByte();
        final byte flags = r.readByte();
        final int speed = r.readByte();
        final PlayerSkill skill = PlayerSkillFactory.getSkill(skillID);
        final int currentLevel = p.getSkillLevel(skill);
        if (currentLevel > 0 && currentLevel == level && skill.isChargeSkill()) {
            switch (skillID) {
                case Bowmaster.Hurricane:
                case Corsair.RapidFire:
                case Hero.MonsterMagnet:
                case Paladin.MonsterMagnet:
                case DarkKnight.MonsterMagnet:
                case FPArchMage.BigBang:
                case ILArchMage.BigBang:
                case Bishop.BigBang:
                case Brawler.CorkscrewBlow:
                case Gunslinger.Grenade:
                case FPMage.Explosion:
                case ChiefBandit.Chakra:
                case Marksman.PiercingArrow:
                    p.getMap().broadcastMessage(p, EffectPackets.SkillEffect(p, skillID, level, flags, speed), false);
                    break;
                default:
                    BroadcastService.broadcastGMMessage(c.getWorld(), PacketCreator.ServerNotice(5, p.getName() + " is using an unusable skill, skillID: " + skillID));
                    break;
            }
        } else {
            p.getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT, "Tried to charge non-prepared skill");
        }
    }

    public static void SkillMacroAssign(final InPacket packet, final Client c) {
        final int count = packet.readByte();
        PlayerSkillMacro[] macros = new PlayerSkillMacro[count];
        for (int i = 0; i < count; i++) {
            String name = packet.readMapleAsciiString();
            boolean silent = packet.readBool();
            PlayerSkill skill1 = PlayerSkillFactory.getSkill(packet.readInt());
            PlayerSkill skill2 = PlayerSkillFactory.getSkill(packet.readInt());
            PlayerSkill skill3 = PlayerSkillFactory.getSkill(packet.readInt());
            if (!c.getPlayer().isGameMaster()) {
                if ((skill1 != null && !ItemConstants.canEquip(skill1, c)) || (skill2 != null && !ItemConstants.canEquip(skill2, c)) || (skill3 != null && !ItemConstants.canEquip(skill3, c))) {
                    c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT, "Tried to force a skill he should not have on a macro.");
                    return;
                }
            }
            macros[i] = new PlayerSkillMacro(name, silent, skill1 != null ? skill1.getId() : 0, skill2 != null ? skill2.getId() : 0, skill3 != null ? skill3.getId() : 0);
        }
        c.getPlayer().setMacros(macros);
    }
}
