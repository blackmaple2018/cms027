var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
	if (status <= 0 && mode <= 0) {
        cm.dispose();
        return
    }
    if (mode == 1) {
        status++
    } else {
        status--
    }	 
    if (status == 0) {
        if (cm.getLevel() < 25) {
            cm.sendOk("你的等级不允许你涉足。");
            cm.dispose();
        } else {
            cm.sendYesNo("嗨，我是塞恩。我可以让你花一点钱进入忍苦森林。需要#b 5000 #k金币哦？");
        }
    } else if (status == 1) {
        if (cm.getPlayer().getMeso() < 5000) {
            cm.sendOk("你竟然连 5000 金币都么有!")
            cm.dispose();
        } else {
            cm.warp(101000100,0);
            cm.gainMeso(-5000);
            cm.dispose();
        }
    }
}