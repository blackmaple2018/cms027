/* 
ZEVMS冒险岛(027)游戏服务端
妮娜
新手出生地
*/

var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 1){
			status++;
		}else{
			status--;
		}
		if (status == 0) {
			cm.sendNext("我， 饿死了…");
		} else if (status == 1) {
			cm.sendOk("这个。。。能吃吗？");
			cm.dispose();
		} 
	}
}	