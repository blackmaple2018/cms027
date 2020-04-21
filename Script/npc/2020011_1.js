var 证明 = 4031058;
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
            cm.sendNext("你想要变得更加强大吗？那你得给我 #i" + 证明 + "# 证明。");
        } else if (status == 1) {
            if (!cm.haveItem(证明, 1)) {
                cm.sendOk("你想要变得更强吗？嗯，现在还不是时候。你没有 #i" + 证明 + "# 给我。");
                cm.dispose();
                return;
            }
			if (cm.getInventory(4).isFull()) {
				cm.sendOk("请保证 #b其他栏#k 至少有2个位置。");
				cm.dispose();
				return;
			} 
            cm.gainItem(证明, -1);
			cm.gainItem(4031130, 1);
            cm.sendOk("你很不错，我已经认可你了，拿着 #i4031130# 请去找你的一转教官吧。");
            cm.dispose();
        }
    }
}
