
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

    var 任务 = 1000201;
	if (cm.getPlayer().getQuest().getQuestData(任务) == "mc") {
		if (cm.getInventory(1).isFull(1)) {
            cm.sendOk("请保证 #b装备栏#k 至少有2个位置。");
            cm.dispose();
            return;
        }
		if (!cm.haveItem(4021009, 1)||!cm.haveItem(4003002, 1)||!cm.haveItem(4001005, 1)||!cm.haveItem(4001006, 1)) {
            cm.sendOk("天哪~这这~这是英雄的战剑，我需要#b1个星石，1个冰块，1个上古卷轴，1个火焰羽毛#k，才可以唤醒它。");
            cm.dispose();
        } else if (!cm.haveItem(1302014, 1)) {
			cm.sendOk("你，不会不小心丢失 #i1302014# 吧？");
            cm.dispose();
		} else {	
            cm.gainItem(4021009, -1);
			cm.gainItem(4003002, -1);
			cm.gainItem(4001005, -1);
			cm.gainItem(4001006, -1);
			cm.gainItem(1302014, -1);
            cm.gainItem(1302015, 1);
            cm.completeQuest(任务, "mh");
			cm.sendOk("感谢你，英雄战剑得以重见天日，请把它带给需要它的人吧。");
            cm.dispose();
        }
	}else{
		cm.sendOk("前面和危险，如果你不够强大，就不哟深入了。");
		cm.dispose();
	}
}

