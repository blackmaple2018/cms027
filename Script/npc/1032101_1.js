
var status = 0;
function start() {
    status = -1;
    action(1, 0, 0)
}

function action(mode, type, selection) {
    if (status <= 0 && mode <= 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++
    } else {
        status--
    }

    var 任务 = 1000601;
	if (cm.getPlayer().getQuest().getQuestData(任务) == "r0") {
		if (cm.getInventory(2).isFull(1)) {
            cm.sendOk("请保证 #b消耗栏#k 至少有2个位置。");
            cm.dispose();
            return;
        }
		if (cm.getInventory(4).isFull(1)) {
            cm.sendOk("请保证 #b其他栏#k 至少有2个位置。");
            cm.dispose();
            return;
        }
		if (!cm.haveItem(4000031, 100)) {
            cm.sendOk("在魔法密林附近的树林打退僵尸猴收集了#b100个诅咒娃娃#k。");
            cm.dispose();
		} else {	
            cm.gainItem(4000031, -100);
            cm.gainItem(2000005, 20);
			cm.gainItem(4020000, 7);
            cm.completeQuest(任务, "r1");
			cm.sendOk("谢谢你，再帮我收集一下。");
            cm.dispose();
        }
	}else if (cm.getPlayer().getQuest().getQuestData(任务) == "r1") {
		cm.sendOk("在魔法密林附近的树林打退僵尸猴收集了#b200个诅咒娃娃#k。");
		cm.completeQuest(任务, "r2");
		cm.dispose();
	}else if (cm.getPlayer().getQuest().getQuestData(任务) == "r2") {
		if (cm.getInventory(2).isFull(1)) {
            cm.sendOk("请保证 #b消耗栏#k 至少有2个位置。");
            cm.dispose();
            return;
        }
		if (cm.getInventory(4).isFull(1)) {
            cm.sendOk("请保证 #b其他栏#k 至少有2个位置。");
            cm.dispose();
            return;
        }
		if (!cm.haveItem(4000031, 200)) {
            cm.sendOk("在魔法密林附近的树林打退僵尸猴收集了#b200个诅咒娃娃#k。");
            cm.dispose();
		} else {	
            cm.gainItem(4000031, -200);
            cm.gainItem(2000005, 50);
			cm.gainItem(4020000, 10);
            cm.completeQuest(任务, "r3");
			cm.sendOk("谢谢你，再帮我收集一下。");
            cm.dispose();
        }
	}else if (cm.getPlayer().getQuest().getQuestData(任务) == "r3") {
		cm.sendOk("在魔法密林附近的树林打退僵尸猴收集了#b400个诅咒娃娃#k。");
		cm.completeQuest(任务, "r4");
		cm.dispose();
	}else if (cm.getPlayer().getQuest().getQuestData(任务) == "r4") {
		if (cm.getInventory(2).isFull(1)) {
            cm.sendOk("请保证 #b消耗栏#k 至少有2个位置。");
            cm.dispose();
            return;
        }
		if (cm.getInventory(4).isFull(1)) {
            cm.sendOk("请保证 #b其他栏#k 至少有2个位置。");
            cm.dispose();
            return;
        }
		if (!cm.haveItem(4000031, 400)) {
            cm.sendOk("在魔法密林附近的树林打退僵尸猴收集了#b400个诅咒娃娃#k。");
            cm.dispose();
		} else {	
            cm.gainItem(4000031, -400);
            cm.gainItem(2000005, 100);
			cm.gainItem(4020000, 10);
            cm.completeQuest(任务, "r5");
			cm.sendOk("谢谢你，再帮我收集一下。");
            cm.dispose();
        }	
	}else if (cm.getPlayer().getQuest().getQuestData(任务) == "r5") {
		cm.sendOk("在魔法密林附近的树林打退僵尸猴收集了#b600个诅咒娃娃#k。");
		cm.completeQuest(任务, "r6");
		cm.dispose();	
	}else if (cm.getPlayer().getQuest().getQuestData(任务) == "r6") {
		if (cm.getInventory(2).isFull(1)) {
            cm.sendOk("请保证 #b消耗栏#k 至少有2个位置。");
            cm.dispose();
            return;
        }
		if (cm.getInventory(4).isFull(1)) {
            cm.sendOk("请保证 #b其他栏#k 至少有2个位置。");
            cm.dispose();
            return;
        }
		if (!cm.haveItem(4000031, 600)) {
            cm.sendOk("在魔法密林附近的树林打退僵尸猴收集了#b600个诅咒娃娃#k。");
            cm.dispose();
		} else {	
            cm.gainItem(4000031, -600);
            cm.gainItem(2000005, 200);
			cm.gainItem(4020000, 10);
			cm.gainItem(2040804, 5);
            cm.completeQuest(任务, "r7");
			cm.sendOk("谢谢你，再帮我收集一下。");
            cm.dispose();
        }		
	}else if (cm.getPlayer().getQuest().getQuestData(任务) == "r7") {
		cm.sendOk("在魔法密林附近的树林打退僵尸猴收集了#b1000个诅咒娃娃#k。");
		cm.completeQuest(任务, "r8");
		cm.dispose();		
	}else if (cm.getPlayer().getQuest().getQuestData(任务) == "r8") {
		if (cm.getInventory(2).isFull(1)) {
            cm.sendOk("请保证 #b消耗栏#k 至少有2个位置。");
            cm.dispose();
            return;
        }
		if (cm.getInventory(4).isFull(1)) {
            cm.sendOk("请保证 #b其他栏#k 至少有2个位置。");
            cm.dispose();
            return;
        }
		if (!cm.haveItem(4000031, 1000)) {
            cm.sendOk("在魔法密林附近的树林打退僵尸猴收集了#b1000个诅咒娃娃#k。");
            cm.dispose();
		} else {	
            cm.gainItem(4000031, -1000);
            cm.gainItem(2000005, 200);
			cm.gainItem(4020000, 10);
			cm.gainItem(2040804, 5);
            cm.completeQuest(任务, "re");
			cm.sendOk("谢谢哈，你给我了好多娃娃。");
            cm.dispose();
        }	
		
	}
}

