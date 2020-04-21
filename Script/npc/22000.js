/* 
ZEVMS冒险岛(027)游戏服务端
桑克斯
前往明珠港
*/
var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1 || status == 4) {
		cm.dispose();
	} else {
		if (status == 2 && mode == 0) {
			cm.sendOk("好吧，我会在这里等着你的！");
			status = 4;
			return;
		}
		if (mode == 1){
			status++;
		}else{
			status--;
		}
		if(cm.getPlayer().getMapId()==104000000){
			if (status == 0) {
				cm.sendNext("	Hi~#b"+cm.玩家名字()+"#k，你想前往彩虹村吗？如果你考虑好了，我会送你过去的。");
			} else if (status == 1) {
				cm.sendYesNo("你准备好了么？");
			} else if (status == 2) {
				cm.warp(60000,0);
				cm.dispose();
			}
		}else{
			if (status == 0) {
				cm.sendNext("	Hi~#b"+cm.玩家名字()+"#k，你想前往金银岛吗？如果你考虑好了，我会送你过去的。");
			} else if (status == 1) {
				cm.sendYesNo("你准备好了么？");
			} else if (status == 2) {
				cm.warp(104000000,0);
				cm.dispose();
			}
		}
		
	}
}