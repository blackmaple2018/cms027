

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
	var 任务2 = 1000600;
	
	if (status <= 0) {
        var selStr = "";
		
		selStr += "欢迎来到射手村，嗯，这里的风景也很不错噢。\r\n\r\n";
		
		if(cm.getLevel()>=20 && cm.getJobId()!=0 ){
			if (cm.getPlayer().getQuest().getQuestData(任务) != "end") {
				selStr += " #L1##b皮亚和蓝蘑菇#k#l\r\n";
			}
		}
		
		if (cm.getPlayer().getQuest().getQuestData(任务2) == "2") {
			if (!cm.haveItem(4031014, 1)) {
				selStr += " #L2##b泰素夫的秘密之书#k#l\r\n";
			}
		}

		
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
            case 1:
                cm.dispose();
                cm.openNpc(1010100,1);
                break;
			case 2:
                cm.dispose();
                cm.openNpc(1010100,2);
                break;
			default:
				break;
        }
    }
}