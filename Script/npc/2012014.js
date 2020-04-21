function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 0) {
		cm.dispose();
		return;
	}
	status++;
	if (status == 0) {
		if(cm.haveItem(4001019)) {
			cm.sendYesNo("你是要移动到冰封雪域吗? 如果你身上有#b#t4001019##k我可以帮助你。");
		} else {
			cm.sendOk("很抱歉，你没有#b#t4001019##k，我不能帮助你移动。");
			cm.dispose();
		}
	}
	if (status == 1) {
			cm.gainItem(4001019, -1);
			cm.warp(200082100, 0);
			cm.dispose();
	}
}
