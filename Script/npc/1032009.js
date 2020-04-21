function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if(mode == -1) {
		cm.dispose();
		return;
	} else {
		status++;
		if(mode == 0) {
			cm.sendOk("你会在正确的时间到达目的地。去和其他人谈谈，在你知道之前，你已经在那里了。");
			cm.dispose();
			return;
		}
		if(status == 0) {
			cm.sendYesNo("你想离开吗？你可以，但票不退。你确定你还想离开这个房间吗？");
		} else if(status == 1) {
			cm.warp(101000300, 0);
			cm.dispose();
		}
	}
}
