/*
 ZEVMS冒险岛(079)游戏服务端
 */
var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status <= 1) {
            cm.sendNext("需要去再来找我吧!");
            cm.dispose();
            return;
        }
        status--;
    }
	if (status == 0) {
        cm.sendYesNo("你确定你想回到 #b#m104000000##k? 好吧，我们得走快点了");
    } else if (status == 1) {
		cm.warp(104000000, 0);
		cm.dispose();
    }
}