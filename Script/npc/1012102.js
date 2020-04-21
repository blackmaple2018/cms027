

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
	var 任务 = 1000300;
    var 任务2 = 1000301;
	
	if (status <= 0) {
        var selStr = "";
		
		selStr += "那些蘑菇就知道欺负我~呜呜。\r\n\r\n";
		
		if(cm.getLevel()>=20 && cm.getJobId()!=0 ){
			if (cm.getPlayer().getQuest().getQuestData(任务) != "end") {
				selStr += " #L1##b皮亚和蓝蘑菇#k#l\r\n";
			}
		}
		if (cm.getLevel() >= 55 && (cm.getJobId() == 400 || cm.getJobId() == 410 || cm.getJobId() == 411 || cm.getJobId() == 420 || cm.getJobId() == 421)) {
			if (cm.getPlayer().getQuest().getQuestData(任务2) != "end") {
				selStr += " #L2##b皮亚的为朋友的礼物#k#l\r\n";
			}
		}
		

		
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
            case 1:
                cm.dispose();
                cm.openNpc(1012102,1);
                break;
			case 2:
                cm.dispose();
                cm.openNpc(1012102,2);
                break;
			default:
				break;
        }
    }
}