package handling.channel.handler;

import client.player.Player;
import client.Client;
import static constants.GameConstants.*;
import static handling.channel.handler.ChannelHeaders.StatsHeaders.*;
import client.player.PlayerJob;
import client.player.PlayerStat;
import client.player.skills.PlayerSkill;
import client.player.skills.PlayerSkillFactory;
import static configure.Gamemxd.一转技能附魔;
import static configure.Gamemxd.三转技能附魔30;
import static configure.Gamemxd.二转技能附魔20;
import static configure.Gamemxd.二转技能附魔30;
import constants.SkillConstants.Beginner;
import packet.transfer.read.InPacket;
import packet.creators.PacketCreator;

/**
 *
 * @author GabrielSin
 */
public class StatsHandling {

    public static void DistributeAP(InPacket packet, Client c) {
        int update = packet.readInt();
        if (c.getPlayer().getStat().getRemainingAp() > 0) {
            switch (update) {
                case STATS_STR:
                    if (c.getPlayer().getStat().getStr() >= MAX_STATS) {
                        return;
                    }
                    c.getPlayer().getStat().addStat(1, 1);
                    break;
                case STATS_DEX:
                    if (c.getPlayer().getStat().getDex() >= MAX_STATS) {
                        return;
                    }
                    c.getPlayer().getStat().addStat(2, 1);
                    break;
                case STATS_INT:
                    if (c.getPlayer().getStat().getInt() >= MAX_STATS) {
                        return;
                    }
                    c.getPlayer().getStat().addStat(3, 1);
                    break;
                case STATS_LUK:
                    if (c.getPlayer().getStat().getLuk() >= MAX_STATS) {
                        return;
                    }
                    c.getPlayer().getStat().addStat(4, 1);
                    break;
                case STATS_MP:
                    addHP(c.getPlayer());
                    break;
                case STATS_HP:
                    addMP(c.getPlayer());
                    break;
                default:
                    c.write(PacketCreator.EnableActions());
                    return;
            }
            c.getPlayer().getStat().setRemainingAp(c.getPlayer().getStat().getRemainingAp() - 1);
            c.getPlayer().getStat().updateSingleStat(PlayerStat.AVAILABLEAP, c.getPlayer().getStat().getRemainingAp());
        }
        c.write(PacketCreator.EnableActions());
    }

    public static void addHP(Player p) {
        if (p == null || p.getMap() == null) {
            return;
        }
        PlayerJob job = p.getJob();
        int MaxHP = p.getStat().getMaxHp();
        PlayerSkill improvingMaxHP = null;
        int improvingMaxHPLevel = 0;
        if (p.getStat().getHpMpApUsed() > 9999 || MaxHP >= 30000) {
            return;
        }
        if (job.isA(PlayerJob.WARRIOR)) {
            improvingMaxHP = PlayerSkillFactory.getSkill(1000001);
            improvingMaxHPLevel = p.getSkillLevel(improvingMaxHP);
            if (improvingMaxHPLevel >= 1) {
                MaxHP += rand(20, 24) + improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
            } else {
                MaxHP += rand(20, 24);
            }
        }
        if (job.isA(PlayerJob.WARRIOR)) {
            MaxHP += 20;
        } else if (job.isA(PlayerJob.MAGICIAN)) {
            MaxHP += 6;
        } else if (job.isA(PlayerJob.BOWMAN) || job.isA(PlayerJob.THIEF)) {
            MaxHP += 16;
        } else if (job.isA(PlayerJob.PIRATE)) {
            MaxHP += 18;
        } else {
            MaxHP += 8;
        }
        MaxHP = Math.min(30000, MaxHP);
        p.getStat().setMaxHp(MaxHP);
        p.getStat().setHpApUsed(p.getStat().getHpApUsed() + 1);
        p.getStat().updateSingleStat(PlayerStat.MAXHP, MaxHP);
    }

    public static void addMP(Player p) {
        if (p == null || p.getMap() == null) {
            return;
        }
        int MaxMP = p.getStat().getMaxMp();
        PlayerJob job = p.getJob();
        if (p.getStat().getHpMpApUsed() > 9999 || p.getStat().getMaxMp() >= 30000) {
            return;
        }

        if (job.isA(PlayerJob.BEGINNER)) {
            MaxMP += 6;
        } else if (job.isA(PlayerJob.MAGICIAN)) {
            PlayerSkill improvingMaxMP = PlayerSkillFactory.getSkill(2000001);
            int improvingMaxMPLevel = p.getSkillLevel(improvingMaxMP);
            if (improvingMaxMPLevel >= 1) {
                MaxMP += rand(18, 20) + improvingMaxMP.getEffect(improvingMaxMPLevel).getY();
            } else {
                MaxMP += rand(18, 20);
            }
        } else if (job.isA(PlayerJob.MAGICIAN)) {
            MaxMP += 18;
        } else if (job.isA(PlayerJob.BOWMAN) || job.isA(PlayerJob.THIEF)) {
            MaxMP += 10;
        } else if (job.isA(PlayerJob.PIRATE)) {
            MaxMP += 14;
        }
        MaxMP = Math.min(30000, MaxMP);
        p.getStat().setMaxMp(MaxMP);
        p.getStat().setMpApUsed(p.getStat().getMpApUsed() + 1);
        p.getStat().updateSingleStat(PlayerStat.MAXMP, MaxMP);
    }

    private static void addSP(SP sp) {
        Player p = sp.getClient().getPlayer();
        if (p == null || p.getMap() == null) {
            return;
        }
        int skillID = sp.getId();
        int remainingSp = p.getStat().getRemainingSp();
        boolean isBegginnerSkill = false;
        switch (skillID) {
            case Beginner.FollowTheLead:
            case 1003:
            case Beginner.MonsterRider:
            case Beginner.EchoOfHero:
                if (!p.isGameMaster()) {
                    return;
                }
                break;
            case 1000:
            case 1001:
            case 1002:
                int snailsLevel = p.getSkillLevel(PlayerSkillFactory.getSkill(1000));
                int recoveryLevel = p.getSkillLevel(PlayerSkillFactory.getSkill(1001));
                int nimbleFeetLevel = p.getSkillLevel(PlayerSkillFactory.getSkill(1002));
                remainingSp = Math.min((p.getLevel() - 1), 6) - snailsLevel - recoveryLevel - nimbleFeetLevel;
                isBegginnerSkill = true;
                break;
        }

        PlayerSkill skill = PlayerSkillFactory.getSkill(skillID);
        int maxLevel = skill.isFourthJob() ? p.getMasterLevel(skill) : skill.getMaxLevel();
        //int maxLevel = skill.getMaxLevel();
        int curLevel = p.getSkillLevel(skill);
        if (一转技能附魔(skillID)) {
            if (curLevel >= 20) {
                p.dropMessage(1, "技能无法继续提升等级。");
                p.announce(PacketCreator.EnableActions());
                return;
            }
        } else if (二转技能附魔20(skillID)) {
            if (curLevel >= 20) {
                p.dropMessage(1, "技能无法继续提升等级。");
                p.announce(PacketCreator.EnableActions());
                return;
            }
        } else if (二转技能附魔30(skillID)) {
            if (curLevel >= 30) {
                p.dropMessage(1, "技能无法继续提升等级。");
                p.announce(PacketCreator.EnableActions());
                return;
            }
        } else if (三转技能附魔30(skillID)) {
            if (curLevel >= 30) {
                p.dropMessage(1, "技能无法继续提升等级。");
                p.announce(PacketCreator.EnableActions());
                return;
            }
        }
        
        /*if (skillID / 10000 == 210 || skillID / 10000 == 220 || skillID / 10000 == 230) {
            if (已经使用技能点(p.getId()) < 67) {
                p.dropMessage(1, "一转技能点使用不足。已经使用了 "+已经使用技能点(p.getId()));
                p.announce(PacketCreator.EnableActions());
                return;
            }
        }
        if (skillID / 10000 == 211 || skillID / 10000 == 221 || skillID / 10000 == 231) {
            if (已经使用技能点(p.getId()) < 185) {
                p.dropMessage(1, "二转技能点使用不足。已经使用了 "+已经使用技能点(p.getId()));
                p.announce(PacketCreator.EnableActions());
                return;
            }
        }
        if (skillID / 10000 == 110 || skillID / 10000 == 120 || skillID / 10000 == 130 || skillID / 10000 == 310 || skillID / 10000 == 320 || skillID / 10000 == 410 || skillID / 10000 == 420) {
            if (已经使用技能点(p.getId()) < 61) {
                p.dropMessage(1, "一转技能点使用不足。已经使用了 "+已经使用技能点(p.getId()));
                p.announce(PacketCreator.EnableActions());
                return;
            }
        }
        if (skillID / 10000 == 111 || skillID / 10000 == 121 || skillID / 10000 == 131 || skillID / 10000 == 311 || skillID / 10000 == 321 || skillID / 10000 == 411 || skillID / 10000 == 421) {
            if (已经使用技能点(p.getId()) < 179) {
                p.dropMessage(1, "二转技能点使用不足。已经使用了 "+已经使用技能点(p.getId()));
                p.announce(PacketCreator.EnableActions());
                return;
            }
        }*/

        if ((remainingSp > 0 && curLevel + 1 <= maxLevel) && skill.canBeLearnedBy(p.getJob())) {
            if (!isBegginnerSkill) {
                p.getStat().setRemainingSp(p.getStat().getRemainingSp() - 1);
            }
            p.getStat().updateSingleStat(PlayerStat.AVAILABLESP, p.getStat().getRemainingSp());
            p.changeSkillLevel(skill, curLevel + 1, p.getMasterLevel(skill));
        } else if (!skill.canBeLearnedBy(p.getJob())) {
            p.announce(PacketCreator.EnableActions());
        } else if (!(remainingSp > 0 && curLevel + 1 <= maxLevel)) {
            p.announce(PacketCreator.EnableActions());
        }
    }

    public static void DistributeSP(InPacket packet, Client c) {
        SP sp = new SP();
        sp.setClient(c);
        sp.setId(packet.readInt());
        addSP(sp);
    }

    private static class SP {

        private int id;
        private Client c;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Client getClient() {
            return c;
        }

        public void setClient(Client c) {
            this.c = c;
        }
    }

    private static int rand(int lbound, int ubound) {
        return (int) ((Math.random() * (ubound - lbound + 1)) + lbound);
    }
}
