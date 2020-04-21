/* 
ZEVMS冒险岛(027)游戏服务端
武术教练
战士转职
相关任务 
100003
100005
*/
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
		if(cm.getJobId() > 0){
			cm.sendOk("你想要变得更强吗？嗯，现在还不是时候。");
			cm.dispose();
			return;
		}
		var 力量 = cm.getPlayer().getStat().getStr();
		var 等级 =cm.getLevel();
		if (status == 0) {
			cm.sendNext("	Hi~#b"+cm.玩家名字()+"#k，你想成为一名战士吗？如果你想成为一名战士，首先你得满足以下条件。");
		} else if (status == 1) {
			cm.sendOk("角色等级达到 #r10#k 级或者以上，力量必须达到 #r35#k 才能满足转职条件。");
		} else if (status == 2) {
			
			if(力量 < 35){
				cm.sendOk("你的力量只有 #r"+力量+"#k 哦，没有满足转职条件。");
				cm.dispose();
				return;
			}
			
			if(等级 < 10){
				cm.sendOk("你的等级只有 #r"+等级+"#k 哦，没有满足转职条件。");
				cm.dispose();
				return;
			}
			
			if (cm.getInventory(1).isFull()) {
				cm.sendOk("请保证 #b装备栏#k 至少有2个位置。");
				cm.dispose();
				return;
			} 
			if (cm.getInventory(2).isFull()) {
				cm.sendOk("请保证 #b消耗栏#k 至少有2个位置。");
				cm.dispose();
				return;
			} 

			
			if (等级 >= 10 && 力量>=35){
				cm.changeJobById(100);
				//大剑
				cm.gainItem(1302007, 1);
				//白色药水
				cm.gainItem(2000002, 100);
				//红色药水
				cm.gainItem(2000003, 100);
				cm.sendNext("恭喜你成为一名战士。我为你准备了一些礼物。\r\n\r\n#v1302007# #b#t1302007##k x 1 \r\n#v2000002# #b#t2000002##k x 100  \r\n#v2000003# #b#t2000003##k x 100");
			}
			cm.dispose();
		}
	}
}	
