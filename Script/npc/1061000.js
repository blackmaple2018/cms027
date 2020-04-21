

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
	var 任务 = 1000201;
	if (status <= 0) {
        var selStr = "";
		
		selStr += "潜心修行，早日登仙，阿弥陀佛~~~关于封印的事情，你找我就对了。\r\n\r\n";
		
		if(cm.getLevel()>=25){
			if (cm.getPlayer().getQuest().getQuestData(任务) == "mc") {
				selStr += " #L1##b麦吉的旧战剑#k#l\r\n";
			}
		}

        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
            case 1:
                cm.dispose();
                cm.openNpc(1061000,1);
                break;

			default:
				break;
        }
    }
}