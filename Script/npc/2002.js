
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
			cm.sendNext("探险必须要有强健的体魄!");
		} else if (status == 1) {
			cm.sendNext("一路辛苦了!");
		} else if (status == 2) {
			cm.sendNext("每次升级都有5点能力点（AP）可以分配，按S键点击橙色箭头就可进行分配。");
		} else if (status == 3) {
			cm.sendNext("增加力量主要提高攻击力，是战士的关键属性；增加敏捷可以提高命中率和手技，也会适当增加攻击力和回避率，是弓箭手的关键属性。");
		} else if (status == 4) {
			cm.sendNext("增加智力可以提高魔法攻击力、魔法防御力和手技，是魔法师关键属性；增加运气可以提高命中率、回避率以及手技，对所有职业都有益处，特别是飞侠。");
		} else if (status == 5) {
			cm.sendNext("按Alt键－跳跃，用方向键→ ←－移动角色，Ctrl－攻击。");
		} else if (status == 6) {
			cm.sendOk("这里是蘑菇村的出口，可以到外面的世界。");
	
			cm.dispose();
		}
	}
}	