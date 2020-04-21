package handling.channel.handler;

import client.Client;
import static handling.channel.handler.MovementParse.parseMovement;
import static handling.channel.handler.MovementParse.updatePosition;
import packet.transfer.read.InPacket;
import java.awt.Point;
import java.util.List;
import java.util.Random;
import packet.creators.MonsterPackets;
import packet.creators.PacketCreator;
import client.player.Player;
import client.player.skills.PlayerSkillFactory;
import constants.SkillConstants.Gunslinger;
import server.life.MapleMonster;
import server.life.MobSkill;
import server.life.MobSkillFactory;
import server.maps.Field;
import server.maps.object.FieldObjectType;
import server.movement.LifeMovementFragment;
import tools.Pair;

public class MobHandler {

    /**
     * <怪物移动>
     */
    public static void MoveMonster(InPacket packet, Client c) {
        //5A 
        //86 00 00 00 
        //[01 00] 
        //[00] nextAttackPossible
        //[FF] action
        // 00 00 00 00 CA 07 8B 01 02 00 BC 07 8B 01 C2 FF 00 00 52 00 03 0D 01 00 89 07 8B 01 C2 FF 00 00 51 00 03 2B 03 00
        //5A [87 00 00 00] [01 00] [00] [FF] [00 00 00 00] [7A 03 8B 01] [01] [00] [39 03] [8B 01] [C2 FF] [00 00] [30 00] 03 38 04 00
        //5A [86 00 00 00] [27 00] [00] [FF] [00 00 00 00] [B2 FA E5 05] [03] 00 D5 FA D8 05 55 00 CC FF DA 00 02 79 01 00 01 FB BD 05 49 00 BD FF DB 00 02 04 02 00 0F FB B0 05 49 00 BD FF DC 00 02 BB 00 00
        Player p = c.getPlayer();
        int objectid = packet.readInt();
        short moveid = packet.readShort();

        if (p == null) {
            return;
        }
        final MapleMonster mmo = p.getMap().getMonsterByOid(objectid);
        if (mmo == null || mmo.getType() != FieldObjectType.MONSTER) {
            return;
        }

        final MapleMonster monster = (MapleMonster) mmo;

        int skillByte = packet.readByte();
        int skill = packet.readByte();

        MobSkill toUse = null;
        Random rand = new Random();

        if (skillByte == 1 && monster.getNoSkills() > 0) {
            int random = rand.nextInt(monster.getNoSkills());
            Pair<Integer, Integer> skillToUse = monster.getSkills().get(random);
            toUse = MobSkillFactory.getMobSkill(skillToUse.getLeft(), skillToUse.getRight());
            int percHpLeft = (int) ((monster.getHp() / monster.getMaxHp()) * 100);
            if (toUse != null) {
                if (toUse.getHP() < percHpLeft || !monster.canUseSkill(toUse)) {
                    toUse = null;
                }
            }
        }

        Point Pos = new Point(packet.readShort(), packet.readShort());
        Point VPos = new Point(packet.readShort(), packet.readShort());

        @SuppressWarnings("UnusedAssignment")
        List<LifeMovementFragment> mov = null;
        try {
            mov = parseMovement(packet);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("AIOBE Type2:\n" + packet.toString());
            return;
        }

        if (monster.getController() != p) {
            if (monster.isAttackedBy(p)) {
                monster.switchController(p, true);
            }
        } else {
            if (skill == -1 && monster.isControllerKnowsAboutAggro() && !monster.isMobile() && !monster.isFirstAttack()) {
                monster.setControllerHasAggro(false);
                monster.setControllerKnowsAboutAggro(false);
            }
        }

        boolean aggro = monster.controllerHasAggro();

        if (toUse != null) {
            c.write(MonsterPackets.MoveMonsterResponse(objectid, moveid, monster.getMp(), aggro, toUse.getSkillId(), toUse.getSkillLevel()));
        } else {
            c.write(MonsterPackets.MoveMonsterResponse(objectid, moveid, monster.getMp(), aggro));
        }

        if (aggro) {
            monster.setControllerKnowsAboutAggro(true);
        }

        if (mov != null) {
            updatePosition(mov, monster, -2);
            p.getMap().moveMonster(monster, monster.getPosition());
            p.getCheatTracker().checkMoveMonster(monster.getPosition());
            p.getMap().broadcastMessage(c.getPlayer(), MonsterPackets.MoveMonster(skillByte, skill, objectid, Pos, VPos, mov));
        }
    }

    public static void AutoAggro(InPacket slea, Client c) {
        Player p = c.getPlayer();
        if (p.isHidden()) {
            return;
        }

        final MapleMonster monster = p.getMap().getMonsterByOid(slea.readInt());

        if (monster != null && monster.getController() != null) {
            if (!monster.controllerHasAggro()) {
                if (p.getMap().getCharacterById(monster.getController().getId()) == null) {
                    monster.switchController(c.getPlayer(), true);
                } else {
                    monster.switchController(monster.getController(), true);
                }
            } else {
                if (p.getMap().getCharacterById(monster.getController().getId()) == null) {
                    monster.switchController(c.getPlayer(), true);
                }
            }
        } else if (monster != null && monster.getController() == null) {
            monster.switchController(c.getPlayer(), true);
        }
    }

    public static void MonsterBomb(InPacket slea, Client c) {
        Player p = c.getPlayer();
        final MapleMonster monster = p.getMap().getMonsterByOid(slea.readInt());
        if (monster == null || p.getMap() == null || !p.isAlive() || p.isHidden()) {
            return;
        }
        if (monster.getStats().selfDestruction().getAction() == 2) {
            monster.getMap().broadcastMessage(MonsterPackets.KillMonster(monster.getObjectId(), 4));
            p.getMap().removeMapObject(monster);
        }
    }

    public static void FriendlyDamage(InPacket packet, Client c) {
        MapleMonster attacker = c.getPlayer().getMap().getMonsterByOid(packet.readInt());
        packet.readInt();
        MapleMonster attacked = c.getPlayer().getMap().getMonsterByOid(packet.readInt());

        if ((attacker != null) && (attacked != null) && (attacked.getStats().isFriendly())) {

            int damage = attacker.getStats().getPADamage() + attacker.getStats().getPDDamage() - 1;

            if (attacked.getHp() - damage < 1) {
                if (attacked.getId() == 9300102) {
                    attacked.getMap().broadcastMessage(PacketCreator.ServerNotice(6, "守望者被外星人打伤了。祝你下次好运。"));
                } else if (attacked.getId() == 9300061) {  //moon bunny
                    attacked.getMap().broadcastMessage(PacketCreator.ServerNotice(6, "月亮兔因为生病回家了。"));
                }
                c.getPlayer().getMap().killFriendlies(attacked);
            } else {
                if (attacked.getId() == 9300061) {
                    Field map = c.getPlayer().getEventInstance().getMapInstance(attacked.getMap().getId());
                    map.addBunnyHit();
                }
            }

            c.getPlayer().getMap().broadcastMessage(MonsterPackets.MobDamageMob(attacked, damage, true));
            c.announce(PacketCreator.EnableActions());
        }
    }

    public static void GrenadeEffect(InPacket packet, Client c) {
        Player p = c.getPlayer();
        Point position = new Point(packet.readInt(), packet.readInt());
        int keyDown = packet.readInt();

        int skillId = Gunslinger.Grenade;
        if (PlayerSkillFactory.getSkill(skillId) != null || c.getPlayer().getSkillLevel(skillId) > 0) {
            p.getMap().broadcastMessage(p, PacketCreator.ThrowGrenade(p.getId(), position, keyDown, Gunslinger.Grenade, p.getSkillLevel(Gunslinger.Grenade)));
        }
    }
}
