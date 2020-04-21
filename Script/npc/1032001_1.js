/* 
ZEVMS冒险岛(027)游戏服务端
汉斯
魔法师转职
*/
/* 
	Victoria Road : Magic Library (101000003)
	Custom Quest 100006, 100008, 100100, 100101
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
		if(cm.getJobId()>0){
			cm.sendOk("你想要变得更强吗？嗯，现在还不是时候。");
			cm.dispose();
			return;
		}		
		var 智力 = cm.getPlayer().getStat().getInt();
		var 等级 =cm.getLevel();
		if (status == 0) {
			cm.sendNext("	Hi~#b"+cm.玩家名字()+"#k，你想成为一名魔法师吗？如果你想成为一名魔法师，首先你得满足以下条件。");
		} else if (status == 1) {
			cm.sendOk("角色等级达到 #r8#k 级或者以上，智力必须达到 #r20#k 才能满足转职条件。");
		} else if (status == 2) {
			
			if(智力 < 20){
				cm.sendOk("你的智力只有 #r"+智力+"#k 哦，没有满足转职条件。");
				cm.dispose();
				return;
			}
			
			if(等级 < 8){
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

			
			if (等级 >= 8 && 智力 >= 20){
				cm.changeJobById(200);
				//木杖
				cm.gainItem(1372005, 1);
				//白色药水
				cm.gainItem(2000002, 100);
				//红色药水
				cm.gainItem(2000003, 100);
				cm.sendNext("恭喜你成为一名魔法师。我为你准备了一些礼物。\r\n\r\n#v1372005# #b#t1372005##k x 1 \r\n#v2000002# #b#t2000002##k x 100  \r\n#v2000003# #b#t2000003##k x 100");
			}
			cm.dispose();
		}
	}
}	
