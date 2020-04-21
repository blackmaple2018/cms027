/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
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

package client.player.buffs;

import java.io.Serializable;

public enum BuffStat implements Serializable {
    
    //v027
    //Byte 1
    WeaponAttack(0x1),
    WeaponDefense(0x2),
    MagicAttack(0x4),
    MagicDefense(0x8),
    
    Accurancy(0x10),
    Avoidability(0x20),
    Hands(0x40),
    Speed(0x80),
    
    //Byte 2
    Jump(0x100),
    MagicGuard(0x200),
    DarkSight(0x400),
    Booster(0x800),
    
    PowerGuard(0x1000),
    MaxHP(0x2000),
    MaxMP(0x4000),
    Invincible(0x8000),
    
    //Byte 3
    SoulArrow(0x10000),
    //Mob Skill
    Stun(0x20000),
    Poison(0x40000),
    Seal(0x80000),
    
    Darkness(0x100000),
    ComboAttack(0x200000),
    Summon(0x200000),
    Charges(0x400000),
    DragonBlood(0x800000),
    
    //Byte 4
    HolySymbol(0x1000000),
    MesoUP(0x2000000),
    ShadowPartner(0x4000000),
    Puppet(0x8000000),
    PickPocketMesoUP(0x8000000),
    
    MesoGuard(0x10000000),
    Thaw(0x20000000),
    Weakness(0x40000000),
    Curse(0x80000000),
    
    //
    /*MORPH(0x2),
    RECOVERY(0x4),
    MAPLE_WARRIOR(0x8),
    STANCE(0x10),
    SHARP_EYES(0x20),
    MANA_REFLECTION(0x40),
    SHADOW_CLAW(0x100),
    INFINITY(0x200), 
    HOLY_SHIELD(0x400),
    HAMSTRING(0x800),
    BLIND(0x1000),
    CONCENTRATE(0x2000),
    ECHO_OF_HERO(0x8000),
    GHOST_MORPH(0x20000),
    DASH(0x30000000),
    SHIELD(0x0000000000040000L),
    MONSTER_RIDING(0x0000000040000000L),
    WATK(0x100000000L),
    WDEF(0x200000000L),
    MATK(0x400000000L),
    MDEF(0x800000000L),
    ACC(0x1000000000L),
    AVOID(0x2000000000L),
    HANDS(0x4000000000L),
    SPEED(0x8000000000L),
    JUMP(0x10000000000L),
    MAGIC_GUARD(0x20000000000L),
    DARKSIGHT(0x40000000000L),
    BOOSTER(0x80000000000L),
    SPEED_INFUSION(0x80000000000L),
    POWERGUARD(0x100000000000L),
    HYPERBODYHP(0x200000000000L),
    HYPERBODYMP(0x400000000000L),
    INVINCIBLE(0x800000000000L),
    SOULARROW(0x1000000000000L),
    STUN(0x2000000000000L),
    POISON(0x4000000000000L),
    SEAL(0x8000000000000L),
    DARKNESS(0x10000000000000L),
    COMBO(0x20000000000000L),
    SUMMON(0x20000000000000L),
    WK_CHARGE(0x40000000000000L),
    DRAGONBLOOD(0x80000000000000L),
    HOLY_SYMBOL(0x100000000000000L),
    MESOUP(0x200000000000000L),
    SHADOWPARTNER(0x400000000000000L),
    PICKPOCKET(0x800000000000000L),
    PUPPET(0x800000000000000L), 
    CURSE(0x8000000000000000L),
    MESOGUARD(0x1000000000000000L),
    WEAKEN(0x4000000000000000L),
    SLOW(0x200000000L), 
    ELEMENTAL_RESET(0x200000000L), 
    MAGIC_SHIELD(0x400000000L), 
    MAGIC_RESISTANCE(0x800000000L),
    DASH_SPEED(0x0000000010000000L),
    DASH_JUMP(0x0000000020000000L),
    ENERGY_CHARGE(0x0000000008000000L),
    HOMING_BEACON(0x0000000000000001L)*/;
    
    static final long serialVersionUID = 0L;
    private final int i;

    private BuffStat(int i) {
        this.i = i;
    }

    public int getValue() {
        return i;
    }
}
