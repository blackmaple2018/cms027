package client.player;

import client.Client;
import client.player.buffs.BuffStat;
import client.player.inventory.Equip;
import client.player.inventory.types.InventoryType;
import client.player.inventory.Item;
import client.player.inventory.types.WeaponType;
import client.player.skills.PlayerSkill;
import client.player.skills.PlayerSkillFactory;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import packet.creators.PacketCreator;
import packet.transfer.write.OutPacket;
import server.itens.ItemInformationProvider;
import tools.Pair;

/**
 * @author GabrielSin (http://forum.ragezone.com/members/822844.html)
 */
public class PlayerStatsManager {

    public int str;
    public int dex;
    public int luk;
    public int int_;
    public int hp;
    public int maxHP;
    public int mp;
    public int maxMP;
    public int skill = 0;
    public int maxDis;
    public int mpApUsed;
    public int hpApUsed;
    public int hpMpApUsed;
    public int remainingAp;
    public int remainingSp;
    public int 经验倍率;
    public int 物品爆率;
    public int 金币倍率;
    private double sword;
    private double blunt;
    private double axe;
    private double bow;
    private double spear;
    private double polearm;
    private double claw;
    private double dagger;
    private double crossbow;
    private final double staffwand = 0.1;
    private transient int magic;
    private transient int watk;
    private transient int acc;
    private transient int eva;
    private transient int wdef;
    private transient int mdef;
    public transient int localMaxHP = 50;
    public transient int localMaxMP = 50;
    public transient int localSTR;
    public transient int localDEX;
    public transient int localLUK;
    public transient int localINT_;
    public transient int localMaxBaseDamage;
    private transient double speedMod;
    private transient double jumpMod;
    private transient boolean isRecalc = false;
    private PlayerSkill skil;
    private final transient WeakReference<Player> p;

    public PlayerStatsManager(final Player p) {
        this.p = new WeakReference<>(p);
    }

    public int getStr() {
        return str;
    }

    public int getDex() {
        return dex;
    }

    public int getLuk() {
        return luk;
    }

    public int getInt() {
        return int_;
    }

    public int getHp() {
        return hp;
    }

    public int getMp() {
        return mp;
    }

    public int getMaxHp() {
        return maxHP;
    }

    public int getMaxMp() {
        return maxMP;
    }

    public int getTotalDex() {
        return localDEX;
    }

    public int getTotalInt() {
        return localINT_;
    }

    public int getTotalStr() {
        return localSTR;
    }

    public int getTotalLuk() {
        return localLUK;
    }

    public int getTotalMagic() {
        return magic;
    }

    public double getSpeedMod() {
        return speedMod;
    }

    public double getJumpMod() {
        return jumpMod;
    }

    public int getTotalWatk() {
        return watk;
    }

    public int getHpMpApUsed() {
        return hpMpApUsed;
    }

    public int getRemainingAp() {
        return remainingAp;
    }

    public int getRemainingSp() {
        return remainingSp;
    }

    public int getMpApUsed() {
        return mpApUsed;
    }

    public void setMpApUsed(int mpApUsed) {
        this.mpApUsed = mpApUsed;
    }

    public int getHpApUsed() {
        return hpApUsed;
    }

    public int getCurrentMaxHp() {
        return localMaxHP;
    }

    public int getCurrentMaxMp() {
        return localMaxMP;
    }

    public int getCurrentMaxBaseDamage() {
        return localMaxBaseDamage;
    }

    public int getTotalAcc() {
        return this.acc;
    }

    public int getTotalEva() {
        return this.eva;
    }

    public int getTotalWdef() {
        return wdef;
    }

    public int getTotalMdef() {
        return mdef;
    }

    public void addHP(int delta) {
        //p.get().dropMessage("生命恢复: " + delta);
        if (hp + delta > getCurrentMaxHp()) {
            delta = getCurrentMaxHp() - hp;
        }
        setHp(hp + delta);
        updateSingleStat(PlayerStat.HP, hp + delta);
    }

    public void addMP(int delta) {
        //p.get().dropMessage("魔力恢复: " + delta);
        if (mp + delta > getCurrentMaxMp()) {
            delta = getCurrentMaxMp() - mp;
        }
        setMp(mp + delta);
        updateSingleStat(PlayerStat.MP, mp);
    }

    public void addMPHP(int hpDiff, int mpDiff) {

        if (p.get().枫叶套() >= 3) {
            hpDiff += hpDiff * 0.1;
        }
        if (p.get().getEquippedFuMoMap().get(3) != null) {
            hpDiff += p.get().getEquippedFuMoMap().get(3);
        }

        setHp(hp + hpDiff);
        setMp(mp + mpDiff);
        int mask = PlayerStat.HP.getValue() | PlayerStat.MP.getValue();
        Player pr = p.get();
        OutPacket updatePacket = PacketCreator.UpdatePlayerStats(pr, mask);
        pr.getClient().write(updatePacket);
    }

    public int addHP(Client c) {
        Player player = c.getPlayer();
        PlayerJob jobtype = player.getJob();
        int MaxHP = getMaxHp();
        if (getHpMpApUsed() > 9999 || MaxHP > 29999) {
            return MaxHP;
        }
        if (jobtype.isA(PlayerJob.BEGINNER)) {
            MaxHP += 8;
        } else if (jobtype.isA(PlayerJob.WARRIOR)) {
            MaxHP += 8;
        } else if (jobtype.isA(PlayerJob.MAGICIAN)) {
            MaxHP += 6;
        } else if (jobtype.isA(PlayerJob.BOWMAN)) {
            MaxHP += 8;
        } else if (jobtype.isA(PlayerJob.THIEF)) {
            MaxHP += 8;
        } else if (jobtype.isA(PlayerJob.PIRATE)) {
            MaxHP += 8;
        }
        return MaxHP;
    }

    public int addMP(Client c) {
        Player p = c.getPlayer();
        int MaxMP = getMaxMp();
        if (getHpMpApUsed() > 9999 || getMaxMp() >= 30000) {
            return MaxMP;
        }
        if (p.getJob().isA(PlayerJob.BEGINNER)) {
            MaxMP += 6;
        } else if (p.getJob().isA(PlayerJob.WARRIOR)) {
            MaxMP += 2;
        } else if (p.getJob().isA(PlayerJob.MAGICIAN)) {
            MaxMP += 14;
        } else if (p.getJob().isA(PlayerJob.BOWMAN) || p.getJob().isA(PlayerJob.THIEF)) {
            MaxMP += 10;
        } else if (p.getJob().isA(PlayerJob.PIRATE)) {
            MaxMP += 14;
        }
        return MaxMP;
    }

    public void addStat(int type, int up) {
        switch (type) {
            case 1:
                this.str += up;
                updateSingleStat(PlayerStat.STR, str);
                break;
            case 2:
                this.dex += up;
                updateSingleStat(PlayerStat.DEX, dex);
                break;
            case 3:
                this.int_ += up;
                updateSingleStat(PlayerStat.INT, int_);
                break;
            case 4:
                this.luk += up;
                updateSingleStat(PlayerStat.LUK, luk);
                break;
            default:
                break;
        }
        recalcLocalStats();
    }

    public void setStr(int str) {
        this.str = str;
        recalcLocalStats();
    }

    public void setDex(int dex) {
        this.dex = dex;
        recalcLocalStats();
    }

    public void setLuk(int luk) {
        this.luk = luk;
        recalcLocalStats();
    }

    public void setInt(int int_) {
        this.int_ = int_;
        recalcLocalStats();
    }

    public void setMaxHp(int hp) {
        this.maxHP = hp;
        recalcLocalStats();
    }

    public void setHpMpApUsed(int mpApUsed) {
        this.hpMpApUsed = mpApUsed;
    }

    public void setHpApUsed(int hpApUsed) {
        this.hpApUsed = hpApUsed;
    }

    public void setRemainingAp(int remainingAp) {
        this.remainingAp = remainingAp;
    }

    public void setRemainingSp(int remainingSp) {
        this.remainingSp = remainingSp;
    }

    public void gainSp(int remainingSp) {
        this.remainingSp += remainingSp;
    }

    public void setMaxHp(int hp, boolean ap) {
        hp = Math.min(30000, hp);
        if (ap) {
            setHpMpApUsed(getHpMpApUsed() + 1);
        }
        this.maxHP = hp;
        recalcLocalStats();
    }

    public void setMaxMp(int mp) {
        this.maxMP = mp;
        recalcLocalStats();
    }

    public void setMaxMp(int mp, boolean ap) {
        mp = Math.min(30000, mp);
        if (ap) {
            setHpMpApUsed(getHpMpApUsed() + 1);
        }
        this.maxMP = mp;
        recalcLocalStats();
    }

    public void setHp(int newhp) {
        setHp(newhp, false);
    }

    public void setHp(int newhp, boolean silent) {
        int oldHp = hp;
        int thp = newhp;
        if (thp < 0) {
            thp = 0;
        }
        if (thp > localMaxHP) {
            thp = localMaxHP;
        }
        this.hp = thp;
        Player pr = p.get();
        if (!silent) {
            pr.updatePartyMemberHP();
        }
        if (oldHp > hp && !pr.isAlive()) {
            //角色死亡
            pr.playerDead();
        }
    }

    public void setMp(int newmp) {
        int tmp = newmp;
        if (tmp < 0) {
            tmp = 0;
        }
        if (tmp > localMaxMP) {
            tmp = localMaxMP;
        }
        this.mp = tmp;

    }

    public void enforceMaxHpMp() {
        Player pr = p.get();
        int mask = 0;
        if (getMp() > getCurrentMaxMp()) {
            setMp(getMp());
            mask |= PlayerStat.MP.getValue();
        }
        if (getHp() > getCurrentMaxHp()) {
            setHp(getHp());
            mask |= PlayerStat.HP.getValue();
        }
        if (mask > 0) {
            pr.announce(PacketCreator.UpdatePlayerStats(pr, mask));
        }
    }

    public void gainAp(int ap) {
        remainingAp += ap;
        updateSingleStat(PlayerStat.AVAILABLEAP, this.remainingAp);
    }

    public void silentEnforceMaxHpMp() {
        setMp(getMp());
        setHp(getHp(), true);
    }

    /**
     * <角色装备属性加载>
     */
    public void recalcLocalStats() {
        final Player pr = p.get();
        if (pr == null) {
            return;
        }
        int oldmaxhp = localMaxHP;
        localMaxHP = getMaxHp();
        localMaxMP = getMaxMp();
        localDEX = getDex();
        localINT_ = getInt();
        localSTR = getStr();
        localLUK = getLuk();
        int speed = 100;
        int jump = 100;
        magic = localINT_;
        watk = 0;
        acc = calculateAcc(pr.getJob());
        eva = calculateEva(pr.getJob());
        wdef = 0;
        mdef = 0;
        经验倍率 = 1;
        物品爆率 = 1;
        金币倍率 = 1;
        for (Item item : pr.getInventory(InventoryType.EQUIPPED)) {
            Equip equip = (Equip) item;
            localMaxHP += equip.getHp();
            localMaxMP += equip.getMp();
            localDEX += equip.getDex();
            localINT_ += equip.getInt();
            localSTR += equip.getStr();
            localLUK += equip.getLuk();
            magic += equip.getMatk() + equip.getInt();
            watk += equip.getWatk();
            wdef += equip.getWdef();
            mdef += equip.getMdef();
            acc += equip.getAcc();
            eva += equip.getAvoid();
            speed += equip.getSpeed();
            jump += equip.getJump();
        }
        magic = Math.min(magic, 2000);
        Integer hbhp = pr.getBuffedValue(BuffStat.MaxHP);
        if (hbhp != null) {
            localMaxHP += (hbhp.doubleValue() / 100) * localMaxHP;
        }
        Integer hbmp = pr.getBuffedValue(BuffStat.MaxMP);
        if (hbmp != null) {
            localMaxMP += (hbmp.doubleValue() / 100) * localMaxMP;
        }
        localMaxHP = Math.min(30000, localMaxHP);
        localMaxMP = Math.min(30000, localMaxMP);

        Integer watkBuff = pr.getBuffedValue(BuffStat.WeaponAttack);
        if (watkBuff != null) {
            watk += watkBuff;
        }
        Integer matkBuff = pr.getBuffedValue(BuffStat.MagicAttack);
        if (matkBuff != null) {
            magic += matkBuff;
        }
        Integer speedBuff = pr.getBuffedValue(BuffStat.Speed);
        if (speedBuff != null) {
            speed += speedBuff;
        }
        Integer jumpBuff = pr.getBuffedValue(BuffStat.Jump);
        if (jumpBuff != null) {
            jump += jumpBuff;
        }
        if (speed > 140) {
            speed = 140;
        }
        if (jump > 123) {
            jump = 123;
        }
        speedMod = speed / 100.0;
        jumpMod = jump / 100.0;
//
//        int 星期 = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
//        int 时 = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
//        for (Item item : pr.getInventory(InventoryType.ETC)) {
//            switch (item.getItemId()) {
//                //双倍经验卡(1天)
//                case 4100000:
//                //双倍经验卡(7天)
//                case 4100001:
//                    //周六, 周日 : 00:00 - 24:00
//                    if (星期 == 1 || 星期 == 7) {
//                        if (时 >= 0 && 时 <= 24) {
//                            经验倍率 = 2;
//
//                        }
//                        //周一至周五 : 10:00 - 22:00
//                    } else if (时 >= 10 && 时 <= 22) {
//                        经验倍率 = 2;
//                    }
//                    break;
//                //双倍经验卡(1天白天)
//                case 4100002:
//                //双倍经验卡(7天白天)
//                case 4100003:
//                    //周六, 周日 : 00:00 - 24:00
//                    if (星期 == 1 || 星期 == 7) {
//                        if (时 >= 0 && 时 <= 24) {
//                            经验倍率 = 2;
//
//                        }
//                        //周一至周五 : 06:00 - 18:00
//                    } else if (时 >= 6 && 时 <= 18) {
//                        经验倍率 = 2;
//                    }
//                    break;
//                //双倍经验卡(1天黑夜)
//                case 4100004:
//                //双倍经验卡(7天黑夜)
//                case 4100005:
//                    //周六, 周日 : 00:00 - 24:00
//                    if (星期 == 1 || 星期 == 7) {
//                        if (时 >= 0 && 时 <= 24) {
//                            经验倍率 = 2;
//                        }
//                        //周一至周五 : 当天18:00 - 次日06:00
//                    } else if (时 >= 18 && 时 <= 6) {
//                        经验倍率 = 2;
//                    }
//                    break;
//            }
//        }
        localMaxBaseDamage = calculateMaxBaseDamage(watk);
        if (oldmaxhp != 0 && oldmaxhp != localMaxHP) {
            pr.updatePartyMemberHP();
        }
    }

    public int calculateAcc(PlayerJob t) {
        Player pr = p.get();
        if (p != null) {
            if (t.isA(PlayerJob.BEGINNER)) {
                return this.getTotalStr() + this.getTotalDex() * 2;
            } else if (t.isA(PlayerJob.WARRIOR)) {
                return this.getTotalDex() * 5 + this.getTotalStr();
            } else if (t.isA(PlayerJob.MAGICIAN)) {
                return this.getTotalInt() * this.getTotalLuk() * 4;
            } else if (t.isA(PlayerJob.BOWMAN)) {
                return this.getTotalDex() * 6;
            } else if (t.isA(PlayerJob.THIEF)) {
                PlayerSkill skilt = PlayerSkillFactory.getSkill(4000000);
                int skillp = pr.getSkillLevel(skilt);
                return this.getTotalDex() + this.getTotalLuk() * 5 + skillp;
            } else if (t.isA(PlayerJob.PIRATE)) {
                return this.getTotalDex() * 3 + this.getTotalStr();
            } else if (t.isA(PlayerJob.GM)) {
                return Integer.MAX_VALUE;
            } else if (t.isA(PlayerJob.SUPERGM)) {
                return Integer.MAX_VALUE;
            } else {
                throw new RuntimeException("Job out of range.");
            }
        }
        return 0;
    }

    public int calculateEva(PlayerJob t) {
        Player pr = p.get();
        if (p != null) {
            if (t.isA(PlayerJob.BEGINNER)) {
                return this.getTotalDex() * 2;
            } else if (t.isA(PlayerJob.WARRIOR)) {
                return this.getTotalDex() * 4;
            } else if (t.isA(PlayerJob.MAGICIAN)) {
                return this.getTotalLuk() * 3;
            } else if (t.isA(PlayerJob.BOWMAN)) {
                return this.getTotalDex() * 2;
            } else if (t.isA(PlayerJob.THIEF)) {
                PlayerSkill skilt = PlayerSkillFactory.getSkill(4000000);
                int skillp = pr.getSkillLevel(skilt);
                return this.getTotalLuk() * 4 + skillp;
            } else if (t.isA(PlayerJob.PIRATE)) {
                return this.getTotalDex() * 3;
            } else if (t.isA(PlayerJob.GM)) {
                return Integer.MAX_VALUE;
            } else if (t.isA(PlayerJob.SUPERGM)) {
                return Integer.MAX_VALUE;
            } else {
                throw new RuntimeException("Job out of range.");
            }
        }
        return 0;
    }

    public int calculateMaxBaseDamage(int watk) {
        Player pr = p.get();
        int maxbasedamage;
        if (watk == 0) {
            maxbasedamage = 1;
        } else {
            Item weapon_item = pr.getInventory(InventoryType.EQUIPPED).getItem((byte) -11);
            if (weapon_item != null) {
                WeaponType weapon = ItemInformationProvider.getWeaponType(weapon_item.getItemId());
                int mainstat;
                int secondarystat;
                if (weapon == WeaponType.BOW || weapon == WeaponType.CROSSBOW) {
                    mainstat = localDEX;
                    secondarystat = localSTR;
                } else if (pr.getJob().isA(PlayerJob.THIEF) && (weapon == WeaponType.CLAW || weapon == WeaponType.DAGGER)) {
                    mainstat = localLUK;
                    secondarystat = localDEX + localSTR;
                } else if (pr.getJob().isA(PlayerJob.PIRATE) && (weapon == WeaponType.GUN)) {
                    mainstat = localDEX;
                    secondarystat = localSTR;
                } else if (pr.getJob().isA(PlayerJob.PIRATE) && (weapon == WeaponType.KNUCKLE)) {
                    mainstat = localSTR;
                    secondarystat = localDEX;
                } else {
                    mainstat = localSTR;
                    secondarystat = localDEX;
                }
                maxbasedamage = (int) (((weapon.getMaxDamageMultiplier() * mainstat + secondarystat) / 100.0) * watk);
                maxbasedamage += 10;
            } else {
                maxbasedamage = 0;
            }
        }
        return maxbasedamage;
    }

    public int calculateMinBaseDamage(Player player) {
        int minbasedamage = 0;
        int atk = getTotalWatk();
        if (atk == 0) {
            minbasedamage = 1;
        } else {
            Item weapon_item = player.getInventory(InventoryType.EQUIPPED).getItem((byte) - 11);
            if (weapon_item != null) {
                WeaponType weapon = ItemInformationProvider.getWeaponType(weapon_item.getItemId());
                if (player.getJob().isA(PlayerJob.FIGHTER)) {
                    skil = PlayerSkillFactory.getSkill(1100000);
                    skill = player.getSkillLevel(skil);
                    if (skill > 0) {
                        sword = ((skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10) / 100);
                    } else {
                        sword = 0.1;
                    }
                } else {
                    skil = PlayerSkillFactory.getSkill(1200000);
                    skill = player.getSkillLevel(skil);
                    if (skill > 0) {
                        sword = ((skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10) / 100);
                    } else {
                        sword = 0.1;
                    }
                }
                skil = PlayerSkillFactory.getSkill(1100001);
                skill = player.getSkillLevel(skil);
                if (skill > 0) {
                    axe = ((skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10) / 100);
                } else {
                    axe = 0.1;
                }
                skil = PlayerSkillFactory.getSkill(1200001);
                skill = player.getSkillLevel(skil);
                if (skill > 0) {
                    blunt = ((skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10) / 100);
                } else {
                    blunt = 0.1;
                }
                skil = PlayerSkillFactory.getSkill(1300000);
                skill = player.getSkillLevel(skil);
                if (skill > 0) {
                    spear = ((skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10) / 100);
                } else {
                    spear = 0.1;
                }
                skil = PlayerSkillFactory.getSkill(1300001);
                skill = player.getSkillLevel(skil);
                if (skill > 0) {
                    polearm = ((skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10) / 100);
                } else {
                    polearm = 0.1;
                }
                skil = PlayerSkillFactory.getSkill(3200000);
                skill = player.getSkillLevel(skil);
                if (skill > 0) {
                    crossbow = ((skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10) / 100);
                } else {
                    crossbow = 0.1;
                }
                skil = PlayerSkillFactory.getSkill(3100000);
                skill = player.getSkillLevel(skil);
                if (skill > 0) {
                    bow = ((skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10) / 100);
                } else {
                    bow = 0.1;
                }
                if (weapon == WeaponType.CROSSBOW) {
                    minbasedamage = (int) (localDEX * 0.9 * 3.6 * crossbow + localSTR) / 100 * (atk + 15);
                }
                if (weapon == WeaponType.BOW) {
                    minbasedamage = (int) (localDEX * 0.9 * 3.4 * bow + localSTR) / 100 * (atk + 15);
                }
                if (player.getJob().isA(PlayerJob.THIEF) && (weapon == WeaponType.DAGGER)) {
                    minbasedamage = (int) (localLUK * 0.9 * 3.6 * dagger + localSTR + localDEX) / 100 * atk;
                }
                if (!player.getJob().isA(PlayerJob.THIEF) && (weapon == WeaponType.DAGGER)) {
                    minbasedamage = (int) (localSTR * 0.9 * 4.0 * dagger + localDEX) / 100 * atk;
                }
                if (player.getJob().isA(PlayerJob.THIEF) && (weapon == WeaponType.CLAW)) {
                    minbasedamage = (int) (localLUK * 0.9 * 3.6 * claw + localSTR + localDEX) / 100 * (atk + 15);
                }
                if (weapon == WeaponType.SPEAR) {
                    minbasedamage = (int) (localSTR * 0.9 * 3.0 * spear + localDEX) / 100 * atk;
                }
                if (weapon == WeaponType.POLE_ARM) {
                    minbasedamage = (int) (localSTR * 0.9 * 3.0 * polearm + localDEX) / 100 * atk;
                }
                if (weapon == WeaponType.SWORD1H) {
                    minbasedamage = (int) (localSTR * 0.9 * 4.0 * sword + localDEX) / 100 * atk;
                }
                if (weapon == WeaponType.SWORD2H) {
                    minbasedamage = (int) (localSTR * 0.9 * 4.6 * sword + localDEX) / 100 * atk;
                }
                if (weapon == WeaponType.AXE1H) {
                    minbasedamage = (int) (localSTR * 0.9 * 3.2 * axe + localDEX) / 100 * atk;
                }
                if (weapon == WeaponType.BLUNT1H) {
                    minbasedamage = (int) (localSTR * 0.9 * 3.2 * blunt + localDEX) / 100 * atk;
                }
                if (weapon == WeaponType.AXE2H) {
                    minbasedamage = (int) (localSTR * 0.9 * 3.4 * axe + localDEX) / 100 * atk;
                }
                if (weapon == WeaponType.BLUNT2H) {
                    minbasedamage = (int) (localSTR * 0.9 * 3.4 * blunt + localDEX) / 100 * atk;
                }
                if (weapon == WeaponType.STAFF || weapon == WeaponType.WAND) {
                    minbasedamage = (int) (localSTR * 0.9 * 3.0 * staffwand + localDEX) / 100 * atk;
                }
            }
        }
        return minbasedamage;
    }

    public int calculateWorkingDamageTotal(int watk) {
        Player pr = p.get();
        int max;
        if (watk == 0) {
            max = 1;
        } else {
            Item weapon_item = pr.getInventory(InventoryType.EQUIPPED).getItem((byte) -11);
            if (weapon_item != null) {
                WeaponType weapon = ItemInformationProvider.getWeaponType(weapon_item.getItemId());
                int mainstat;
                int secondarystat;
                if (weapon == WeaponType.BOW || weapon == WeaponType.CROSSBOW) {
                    mainstat = localDEX;
                    secondarystat = localSTR;
                } else if (pr.getJob().isA(PlayerJob.THIEF) && (weapon == WeaponType.CLAW || weapon == WeaponType.DAGGER)) {
                    mainstat = localLUK;
                    secondarystat = localDEX + localSTR;
                } else {
                    mainstat = localSTR;
                    secondarystat = localDEX;
                }
                max = (int) (((weapon.getMaxDamageMultiplier() * mainstat + secondarystat)) * watk / 100);
            } else {
                max = 0;
            }
        }
        return max;
    }

    public int getMaxDis(Player player) {
        Item weapon_item = player.getInventory(InventoryType.EQUIPPED).getItem((byte) -11);
        if (weapon_item != null) {
            WeaponType weapon = ItemInformationProvider.getWeaponType(weapon_item.getItemId());
            if (weapon == WeaponType.SPEAR || weapon == WeaponType.POLE_ARM) {
                maxDis = 106;
            }
            if (weapon == WeaponType.DAGGER || weapon == WeaponType.SWORD1H || weapon == WeaponType.AXE1H || weapon == WeaponType.BLUNT1H || weapon == WeaponType.KNUCKLE) {
                maxDis = 63;
            }
            if (weapon == WeaponType.SWORD2H || weapon == WeaponType.AXE1H || weapon == WeaponType.BLUNT1H) {
                maxDis = 73;
            }
            if (weapon == WeaponType.STAFF || weapon == WeaponType.WAND) {
                maxDis = 51;
            }
            if (weapon == WeaponType.CLAW) {
                skil = PlayerSkillFactory.getSkill(4000001);
                skill = player.getSkillLevel(skil);
                if (skill > 0) {
                    maxDis = (skil.getEffect(player.getSkillLevel(skil)).getRange()) + 205;
                } else {
                    maxDis = 205;
                }
            }
            if (weapon == WeaponType.BOW || weapon == WeaponType.CROSSBOW) {
                skil = PlayerSkillFactory.getSkill(3000002);
                skill = player.getSkillLevel(skil);
                if (skill > 0) {
                    maxDis = (skil.getEffect(player.getSkillLevel(skil)).getRange()) + 270;
                } else {
                    maxDis = 270;
                }
            }
            if (weapon == WeaponType.GUN) {
                maxDis = 270;
            }
        }
        return maxDis;
    }

    public void updateSingleStat(PlayerStat stat, int newval, boolean itemReaction) {
        Player pr = p.get();
        pr.getClient().write(PacketCreator.UpdatePlayerStats(pr, itemReaction, stat.getValue()));
    }

    public void updateSingleStat(PlayerStat stat, int newval) {
        updateSingleStat(stat, newval, true);
    }
}
