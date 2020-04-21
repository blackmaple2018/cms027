var 黑符 = 4031059;
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
            cm.sendNext("你想要变得更加强大吗？那你得去已异界，击败我的分身，给我 #i" + 黑符 + "# 黑符。");
        } else if (status == 1) {
            if (!cm.haveItem(黑符, 1)) {
                cm.sendOk("你没有 #i" + 黑符 + "# 给我。");
                cm.dispose();
                return;
            }
			if (!cm.haveItem(4031130, 1)) {
                cm.sendOk("你没有 #i" + 4031130 + "# 给我。");
                cm.dispose();
                return;
            }
			/*if(cm.getPlayer().getStat().getRemainingSp()>0){
				cm.sendOk("你当前还有未使用完的SP点。");
				cm.dispose();
				return;
			}*/
			cm.gainItem(4031130, -1);
            cm.gainItem(黑符, -1);
			cm.changeJobById((cm.getJobId()+1));
            cm.sendOk("恭喜你，成功进阶成为一名"+cm.职业(cm.getJobId())+"。");
            cm.dispose();
        }
    }
}