

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
	var 任务 = 1001801;
	if (status <= 0) {
        var selStr = "";
		
		selStr += "你好，我是内拉~感谢你帮助了废弃都市的居民。\r\n\r\n";
		
		if(cm.getLevel()>=25){
			if (cm.getPlayer().getQuest().getQuestData(任务) != "1end") {
				selStr += " #L1##b帮助废弃都市居民们#k#l\r\n";
			}
		}

        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
            case 1:
                cm.dispose();
                cm.openNpc(1052103,1);
                break;

			default:
				break;
        }
    }
}