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
		var 任务 = 1;
		if (status == 0) {
			cm.sendNext("	Hi~#b"+cm.玩家名字()+"#k，欢迎来到 #r"+cm.冒险岛名称()+"#k，按Alt键－跳跃，用方向键→ ←－移动角色，Ctrl－攻击。");
		} else if (status == 1) {
			cm.sendNext("按Alt键－跳跃，用方向键→ ←－移动角色，Ctrl－攻击。");
		} else if (status == 2) {
			cm.sendNext("按↑↓键，可以爬梯或吊绳。");
		} else if (status == 3) {
			cm.sendNext("左上角小地图说明：蓝点－地图跳转点、黄点－自己的位置、红点－其他玩家、绿点－NCP位置、橙色点－与你组队的玩家。");
		} else if (status == 4) {
			cm.sendNext("嘿嘿…");
		} else if (status == 5) {
			cm.sendOk("去找莎丽吧，她有话对你说。");
			if (cm.getPlayer().getQuest().getQuestData(任务)=="f" ){
				cm.gainExp(15);
				cm.completeQuest(任务, "end");
			}
			cm.dispose();
		}
	}
}	