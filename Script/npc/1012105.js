
var status = 0;
var skin = Array(0, 1);

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 0 && status == 0) {
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			cm.sendYesNo("你是要晒个皮肤颜色吗？不过有时候晒的颜色可能没变化，不过没事，多晒几次就行了。");
		} else if (status == 1) {
			cm.sendStyle("选择一个颜色吧。", skin);
		} else if (status == 2){
			if (cm.haveItem(4053000)) {
				cm.gainItem(4053000, -1);
				cm.setSkin(skin[selection]);
				cm.sendOk("弄好了，你看看是不是更加好看了。");
				cm.dispose();
			} else {
				cm.sendOk("亲，你没有 #i4053000# 所以我不能为你服务。");
				cm.dispose();
			}
		}
	}
}
