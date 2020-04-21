
importPackage(Packages.client);

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (status >= 2 && mode == 0) {
			cm.sendOk("Alright, see you next time.");
			cm.dispose();
			return;
		}
		if (mode == 1) {
			status++;
		}
		else {
			status--;
		}
		if (status == 0) {
			/*if (cm.getQuestStatus(2053).equals(MapleQuestStatus.Status.STARTED) && !cm.haveItem(4031026)) {
				cm.gainItem(4031026, 20);
				cm.warp(105040300, 0);
			}else {*/
				var rand = 1 + Math.floor(Math.random() * 6);
				if (rand == 1) {
					cm.gainItem(4020005, 2); // Sapphire Ore
				}else if (rand == 2) {
					cm.gainItem(4020006, 2); // Topaz Ore
				}else if (rand == 3) {
					cm.gainItem(4020004, 2); // Opal Ore
				}else if (rand == 4) {
					cm.gainItem(4020001, 2); // Amethyst Ore
				}else if (rand == 5) {
					cm.gainItem(4020003, 2); // Emerald Ore
				}else if (rand == 6) {
					cm.gainItem(4020000, 2); // Garnet Ore
				}else if (rand == 7) {
					cm.gainItem(4020002, 2); // AquaMarine Ore
				}
				cm.warp(105040300, 0);
			//}
			cm.dispose();	
		}
	}
}	


