function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else if (mode == 0) {
        cm.dispose();
    } else {
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
		if (!cm.haveItem(4031130, 1)) {
			cm.sendOk("你没有证明，所以你还是不要来尝试了 。");
			cm.dispose();
			return;
		}
        if (status == 0) {
            cm.sendYesNo("你要是要挑战更强的怪物吗？这里的怪物是每隔一段时间刷新的，所以如果你进去后没遇到，可能就要小等一会儿了。");
        } else if (status == 1) {
            var mapid = cm.getMapId();
            //弓箭手
            if (mapid == 105040305) {
                if ((cm.getJobId() == 310 || cm.getJobId() == 320) && cm.getLevel() >= 70) {
                    if (cm.getMap(108010100).getCharactersSize() == 0 && cm.getMap(108010101).getCharactersSize() == 0) {
                        cm.getPlayer().changeMap(108010100);
						//召唤怪物
						cm.getMap(108010101).spawnMonsterOnGroundBelow(9001002,142,20);
                    } else {
                        cm.sendOk("已经有人在挑战中了，请你稍后再试试。");
                    }
                } else {
                    cm.sendOk("这里是弓箭手的异界之门，你不符合要求 。");
                }
                //魔法师
            } else if (mapid == 100040106) {
                if ((cm.getJobId() == 210 || cm.getJobId() == 220 || cm.getJobId() == 230) && cm.getLevel() >= 70) {
                    if (cm.getMap(108010200).getCharactersSize() == 0 && cm.getMap(108010201).getCharactersSize() == 0) {
                        cm.getPlayer().changeMap(108010200);
						//召唤怪物
						cm.getMap(108010201).spawnMonsterOnGroundBelow(9001001,142,20);
                    } else {
                        cm.sendOk("已经有人在挑战中了，请你稍后再试试。");
                    }
                } else {
                    cm.sendOk("这里是魔法师的异界之门，你不符合要求 。");
                }
                //战士
            } else if (mapid == 105070001) {
                if ((cm.getJobId() == 110 || cm.getJobId() == 120 || cm.getJobId() == 130) && cm.getLevel() >= 70) {
                    if (cm.getMap(108010300).getCharactersSize() == 0 && cm.getMap(108010301).getCharactersSize() == 0) {
                        cm.getPlayer().changeMap(108010300);
						//召唤怪物
						cm.getMap(108010301).spawnMonsterOnGroundBelow(9001000,142,20);
                    } else {
                        cm.sendOk("已经有人在挑战中了，请你稍后再试试。");
                    }
                } else {
                    cm.sendOk("这里是战士的异界之门，你不符合要求 。");
                }
                //飞侠
            } else if (mapid == 107000402) {
                if ((cm.getJobId() == 410 || cm.getJobId() == 420) && cm.getLevel() >= 70) {
                    if (cm.getMap(108010400).getCharactersSize() == 0 && cm.getMap(108010401).getCharactersSize() == 0) {
                        cm.getPlayer().changeMap(108010400);
						//召唤怪物
						cm.getMap(108010401).spawnMonsterOnGroundBelow(9001003,142,20);
                    } else {
                        cm.sendOk("已经有人在挑战中了，请你稍后再试试。");
                    }
                } else {
                    cm.sendOk("这里是飞侠的异界之门，你不符合要求 。");
                }
            }
            cm.dispose();
        }
    }
}

























