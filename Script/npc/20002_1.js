
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
	
	if (status == 0) {
		if (cm.hasQuestCompleted(100)) {
			cm.sendOk("我是水手，感谢你帮助过我。");
			cm.dispose();
	    } else if (cm.hasQuestInProcess(100)) {
			if (cm.getInventory(1).isFull(3)) {
				cm.sendOk("请保证 #b装备栏#k 至少有4个位置。");
				cm.dispose();
				return;
			}
			if (!cm.haveItem(4000001, 10) || !cm.haveItem(4000000, 30) ) {
			    cm.sendOk("你好像还没有收集10个#b花蘑菇的盖#k和30个#b蓝蜗牛的壳。");
				cm.dispose();
		    } else {
			    cm.sendNext("感谢你，请收下这些药品。另外我还送你一把 #i1332005#，帮助你冒险。");
			    cm.gainItem(4000001,-10);
			    cm.gainItem(4000000,-30);
				cm.gainItem(1332005,1);
				cm.gainItem(2000000,10);
			    cm.completeQuest(100, "end");
				cm.dispose();
		    }
	    } else {
			cm.sendYesNo("帮我收集10个#b花蘑菇的盖#k和30个#b蓝蜗牛的壳#k好不，我需要用来研究，你会帮助我吗？");
		}
	} else if (status == 1) {
		cm.sendNext("感谢你，我在这里等着你，在彩虹村附近看看，这些物品可以搜到的。");
		cm.startQuest(100);
		cm.dispose();
	}
	
}