

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
	var 任务 = 1001400;

	if (status <= 0) {
        var selStr = "";
		
		selStr += "小伙子，你想加入空军吗？翱翔在蓝天之上的那种。我可是特种部队的，你有兴趣吗？\r\n\r\n";
		
		if(cm.getLevel()>=40){
			if (cm.getPlayer().getQuest().getQuestData(任务) == "s") {
				selStr += " #L1##b阿尔法部队联络网#k#l\r\n";
			}
		}


        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
            case 1:
                cm.dispose();
                cm.openNpc(2020003,1);
                break;

			default:
				break;
        }
    }
}