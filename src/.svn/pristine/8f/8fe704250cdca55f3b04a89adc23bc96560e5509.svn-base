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

public enum Disease implements Serializable {

    SLOW(0x1, 2, 126),
    SEDUCE(0x80, 2, 128),
    
    STUN(0x20000, 1, 123),
    POISON(0x40000, 1, 125),
    SEAL(0x80000, 1, 120),
    DARKNESS(0x100000, 1, 121),
    WEAKEN(0x40000000, 1, 122),
    CURSE(0x80000000, 1, 124),
    
    CONFUSE(0x80000, 1, 132);

    private static final long serialVersionUID = 0L;
    private final int i;
    private boolean first;
    private int disease;
    private int firsta;
    
   private Disease(int i) {
        this.i = i;
        first = false;
    }

    private Disease(int i, boolean first, int disease) {
        this.i = i;
        this.first = first;
        this.disease = disease;
    }
    
    private Disease(int i, int first, int disease) {
        this.i = i;
        this.firsta = first;
        this.disease = disease;
    }
    
    public boolean isFirst() {
        return first;
    }

    public int getValue() {
        return i;
    }
    
    public int getDisease() {
        return disease;
    }
   
    public static Disease getType(int skill) {
        switch (skill) {
            case 120:
                return SEAL;
            case 123:
                return STUN;
            case 128:
                return SEDUCE;
            case 125:
                return POISON;
            case 121:
                return DARKNESS;
            case 122:
                return WEAKEN;
            case 124:
                return CURSE;
            case 132:
                return CONFUSE;
            default:
                return null;
        }
    }
}