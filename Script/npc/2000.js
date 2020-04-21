/* 
ZEVMS冒险岛(027)游戏服务端
罗杰
新手出生地
*/
var status = -1;
function start() {
	status = -1;
	action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
            cm.sendNext("按字母I就打开背包，里面有装备，消耗品，道具和其他物品。");
        } else if (status == 1) {
            cm.sendNext("拣取的物品会在背包中自动整理。怎样？方便吧？");
        } else if (status == 2) {
			cm.sendNext("双击在背包里的道具可以使用该道具。");
		} else if (status == 3) {
			cm.sendNext("在背包中单击道具后拖到物品窗外边，可以扔掉道具。");
		} else if (status == 4) {
			cm.sendNext("如果背包满了就无法拣取新道具。可以把不用的道具扔掉或卖到商店去。");
		} else if (status == 5) {
			cm.sendNext("你可以把药品之类的消耗品拖到界面右下角指定的快捷键上。");	
		} else if (status == 6) {
			cm.sendNext("HP达到‘0’你会死掉，所以应该及时补充生命值。");	
		} else if (status == 7) {
			cm.sendNext("想知道探险的捷径吗？那就和我聊聊吧。");	
		} else if (status == 8) {
			cm.sendNext("双击对方就可以开始对话。");	
		} else if (status == 9) {
			cm.sendOk("感谢你听完我讲的话，你很有礼貌，来这是我给你的奖励。");	
			//cm.gainExp(50);
			cm.dispose();
        }
    }
}