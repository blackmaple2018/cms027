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
		var 敏捷 = cm.getPlayer().getStat().getDex();
		var 等级 =cm.getLevel();
		if (status == 0) {
			cm.sendNext("	Hi~#b"+cm.玩家名字()+"#k，你想成为一名飞侠吗？如果你想成为一名飞侠，首先你得满足以下条件。");
		} else if (status == 1) {
			cm.sendOk("角色等级达到 #r10#k 级或者以上，敏捷必须达到 #r25#k 才能满足转职条件。");
		} else if (status == 2) {
			
			if(敏捷 < 25){
				cm.sendOk("你的敏捷只有 #r"+敏捷+"#k 哦，没有满足转职条件。");
				cm.dispose();
				return;
			}
			
			if(等级 < 10){
				cm.sendOk("你的等级只有 #r"+等级+"#k 哦，没有满足转职条件。");
				cm.dispose();
				return;
			}
			
			
			if (等级 >= 10 && 敏捷>=25){
				cm.changeJobById(400);
				//短刀
				cm.gainItem(1332007, 1);
				//海星标
				cm.gainItem(2070000, 500);
				//拳套
				cm.gainItem(1472000, 1);
				//白色药水
				cm.gainItem(2000002, 100);
				//红色药水
				cm.gainItem(2000003, 100);
				cm.sendNext("恭喜你成为一名飞侠。我为你准备了一些礼物。\r\n\r\n#v1332007# #b#t1332007##k x 1\r\n#v1472000# #b#t1472000##k x 1 \r\n#v2070000# #b#t2070000##k x 500 \r\n#v2000002# #b#t2000002##k x 100  \r\n#v2000003# #b#t2000003##k x 100");
			}
			cm.dispose();
		}
	}
}	
