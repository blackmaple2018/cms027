

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
	var 任务 = 1001000;

	if (status <= 0) {
        var selStr = "";
		
		selStr += "我是魔法密林里的妖精，会魔法的妖精。\r\n\r\n";
		
		if(cm.getLevel()>=25 && cm.getJobId()!=0 ){
			if (cm.getPlayer().getQuest().getQuestData(任务) != "end") {
				selStr += " #L1##b艾温的玻璃鞋#k#l\r\n";
			}
		}

        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
            case 1:
                cm.dispose();
                cm.openNpc(1032100,1);
                break;

			default:
				break;
        }
    }
}