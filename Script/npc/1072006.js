var status;
var completed;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0 && type > 0) {
            cm.dispose();
            return;
        }
        if (mode == 1){
            status++;
		}else{
            status--;
		}
        if (status == 0) {
            if (cm.haveItem(4031013, 30)) {
                completed = true;
                cm.sendNext("哦。。你收集了所有30颗黑色弹珠！！应该很难。。。太不可思议了！好吧。你通过了测试，为此，我会奖励你一个英雄的证明。");
            } else {
                completed = false;
                cm.sendSimple("你需要收集 30个 #b#t4031013##k，我才会给你证明。 \r\n#b#L1#我要离开#l");
            }
        } else if (status == 1) {
            if (completed) {
                cm.removeAll(4031013);
                cm.gainItem(4031012);
            }
			cm.warp(106010000, 9);
            cm.dispose();
        }
    }
}
