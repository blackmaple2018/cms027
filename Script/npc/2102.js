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
			cm.sendNext("按E－查看装备，按S－查看角色能力值~");
		} else if (status == 1) {
			cm.sendNext("按Z可以拣取物品，拣取的物品会在背包中自动整理。");
		} else if (status == 2) {
			cm.sendOk("你该继续往那边走了~");

			cm.dispose();
		}
	}
}	