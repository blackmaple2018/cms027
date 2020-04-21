

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
	if (status <= 0) {
        var selStr = "";
		
		selStr += "我是水手，在彩虹村南港，我可是很受欢迎的哦。\r\n\r\n";
		//比格斯的物品收集任务
		if (cm.getPlayer().getQuest().getQuestData(100) != "end") {	
			selStr += " #L1##b比格斯的物品收集#k#l\r\n";
		}

        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
            case 1:
                cm.dispose();
                cm.openNpc(20002,1);
                break;
			default:
				break;
        }
    }
}