

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
	var 任务 = 1000601;
	var 任务2 = 1000600;
	
	if (status <= 0) {
        var selStr = "";
		
		selStr += "我是魔法密林的妖精，我也会魔法哦，我喜欢娃娃，你有娃娃送给我吗？~呜呜。\r\n\r\n";
		
		if(cm.getLevel()>=35 && cm.getJobId()!=0 ){
			if (cm.getPlayer().getQuest().getQuestData(任务) != "re") {
				selStr += " #L1##b妖精罗雯和诅咒的娃娃#k#l\r\n";
			}
		}
		if (cm.getPlayer().getQuest().getQuestData(任务2) == "2") {
			if (!cm.haveItem(4031015, 1)) {
				selStr += " #L2##b泰素夫的秘密之书#k#l\r\n";
			}
		}
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
            case 1:
                cm.dispose();
                cm.openNpc(1032101,1);
				break;
			case 2:
                cm.dispose();
                cm.openNpc(1032101,2);
				break;
			default:
				break;
        }
    }
}