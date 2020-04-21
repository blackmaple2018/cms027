/* 
ZEVMS冒险岛(027)游戏服务端
罗宾
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
			cm.sendNext("	Hi~#b"+cm.玩家名字()+"#k，这儿会有很多怪物出没，要特别小心啊!");
		} else if (status == 1) {
			cm.sendOk("按Alt键－跳跃，用方向键→ ←－移动角色，Ctrl－攻击。");
		} else if (status == 2) {
			cm.sendOk("按z 键，就可以拣取怪物掉在地上的道具。");
		} else if (status == 3) {
			cm.sendOk("如果遇到强大的怪物，可以尝试与别人一起击退怪物。");
		} else if (status == 4) {
			cm.sendOk("应该掌握攻击时间和节奏，这样可容易打退怪物。");
		} else if (status == 5) {
			cm.sendOk("刚才你出来的村落是蘑菇村。在这个岛上还有彩虹村和南港，这两个村落。");
		} else if (status == 6) {
			cm.sendOk("如果你想去更广阔的地方，就往东北方向去吧。");
		} else if (status == 7) {
			cm.sendOk("你应该见过罗杰哥吧?");
		} else if (status == 8) {
			cm.sendOk("祝你一路顺风啊。");
		} else if (status == 9) {
			cm.sendOk("哟唿~");
			cm.dispose();
		}
	}
}	