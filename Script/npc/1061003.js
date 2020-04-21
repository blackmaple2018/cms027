
var status = 0;
function start() {
    status = -1;
    action(1, 0, 0)
}

function action(mode, type, selection) {
    if (status <= 0 && mode <= 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++
    } else {
        status--
    }

    var 任务 = 1000600;
	if (status <= 0) {
        var selStr = "";
		
		selStr += "如果你觉得疲惫了，一定要注意休息哦，在这里休息的话，会比外面要舒服很多。\r\n\r\n";
		
		if(cm.getLevel()>=30 && cm.getJobId()!=0 ){
			if (cm.getPlayer().getQuest().getQuestData(任务) != "e") {
				selStr += " #L1##b泰素夫的秘密之书#k#l\r\n";
			}
		}

        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
            case 1:
                cm.dispose();
                cm.openNpc(1061003,1);
				break;
			default:
				break;
        }
    }

}

