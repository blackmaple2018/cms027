var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
	if (status >= 0 && mode == 0) {
		cm.dispose();
		return;
	}
	if (mode == 1)
		status++;
	else
		status--;
	if (status == 0) {
			cm.sendOk("嘿嘿嘿！!! 找到宝卷我把地图弄丢了，没有它我不能离开。");
			cm.dispose();
		}
	}
}