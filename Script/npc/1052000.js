

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
	var 任务 = 1000500;
	if (status <= 0) {
        var selStr = "";
		
		selStr += "我是阿勒斯，是以后要成为一村之长的人，你可别小瞧我，我才不会小鬼头。\r\n\r\n";
		//休咪丢失的金币/等级等级20
		if(cm.getLevel() >= 20 && cm.getJobId()!=0){
			if (cm.getPlayer().getQuest().getQuestData(任务) != "end") {
				selStr += " #L1##b离家出去的阿勒斯#k#l\r\n";
			}
		}

        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
            case 1:
                cm.dispose();
                cm.openNpc(1052000,1);
                break;

			default:
				break;
        }
    }
}