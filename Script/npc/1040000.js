

function start() {
    status = -1;
    action(1, 0, 0)
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
    var 任务 = 1000800;
	var 任务2 = 1000801;
    if (status <= 0) {
        var selStr = "";

		selStr += "我是镇守在这里的护卫，你帮助过我，我也会帮助你。\r\n\r\n";

        if (cm.getLevel() >= 15 && cm.getJobId() != 0) {
            if (cm.getPlayer().getQuest().getQuestData(任务) != "end") {
                selStr += " #L1##b迷宫入口的守卫兵，鲁克#k#l\r\n";
            }
        }
		
		if (cm.getLevel() >= 50 && (cm.getJobId() >= 100 && cm.getJobId() < 200)) {
            if (cm.getPlayer().getQuest().getQuestData(任务2) != "end") {
                selStr += " #L2##b守卫兵鲁克的决心#k#l\r\n";
            }
        }

        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
            case 1:
                cm.dispose();
                cm.openNpc(1040000, 1);
                break;
			case 2:
                cm.dispose();
                cm.openNpc(1040000, 2);
                break;
            default:
                break;
        }
    }
}