

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
	var 任务 = 1000900;
    var 任务2 = 1000901;
	var 任务3 = 1000902;
	
	if (status <= 0) {
        var selStr = "";
		
		selStr += "我的妻子喜欢花，特别的长在洞穴深处的花。\r\n\r\n";
		
		if(cm.getLevel()>=15 ){
			if (cm.getPlayer().getQuest().getQuestData(任务) != "end") {
				selStr += " #L1##b约翰的粉红花篮#k#l\r\n";
			}
		}
		
		if(cm.getLevel()>=30 ){
			if (cm.getPlayer().getQuest().getQuestData(任务2) != "end") {
				selStr += " #L2##b约翰的礼物#k#l\r\n";
			}
		}
		
		if(cm.getLevel()>=61 ){
			if (cm.getPlayer().getQuest().getQuestData(任务3) != "end" ) {
				selStr += " #L3##b约翰的最后礼物#k#l\r\n";
			}
		}
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
            case 1:
                cm.dispose();
                cm.openNpc(20000,1);
                break;
			case 2:
                cm.dispose();
                cm.openNpc(20000,2);
                break;
			case 3:
                cm.dispose();
                cm.openNpc(20000,3);
                break;
			default:
				break;
        }
    }
}