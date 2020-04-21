

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
	var 任务 = 1000700;
	var 任务2 = 1000701;
	if (status <= 0) {
        var selStr = "";
		
		selStr += "草药，草药，我要草药，草药可以制作很多神奇功效的药水。\r\n\r\n";
		
		if(cm.getLevel()>=25){
			if (cm.getPlayer().getQuest().getQuestData(任务) != "1_99") {
				selStr += " #L1##b萨比特拉玛的减肥药#k#l\r\n";
			}
		}
		if(cm.getLevel()>=50){
			if (cm.getPlayer().getQuest().getQuestData(任务2) != "2_99") {
				selStr += " #L2##b萨比特拉玛的年轻药#k#l\r\n";
			}
		}

        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
            case 1:
                cm.dispose();
                cm.openNpc(1061005,1);
                break;
			case 2:
                cm.dispose();
                cm.openNpc(1061005,2);
                break;
			default:
				break;
        }
    }
}