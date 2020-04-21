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
			cm.sendNext("要晾的衣服怎么这么多呀~");
		} else if (status == 1) {
			cm.sendNext("你见过我妹妹希娜吗?");
		} else if (status == 2) {
			cm.sendNext("按↑↓键，可以爬梯或吊绳。");
		} else if (status == 3) {
			cm.sendNext("走到下面的白色光柱中间按↑可以去别的地图呢！");
		} else if (status == 4) {
			cm.sendNext("走到下面的光柱按↑还可以去别的地方呢！");
		} else if (status == 5) {
			cm.sendNext("按W出现大地图：大地图中有一个跳动手指，它所指的地方就是你所在的方位。");
		} else if (status == 6) {
			cm.sendNext("今天晚上吃什么好呢？");
		} else if (status == 7) {
			cm.sendOk("你该继续往那边走了~");

			cm.dispose();
		}
	}
}	