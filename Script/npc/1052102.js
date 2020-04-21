

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
	var 任务 = 1001200;
    var 任务2 = 1001201;
	var 任务3 = 1001202;
	if (status <= 0) {
        var selStr = "";
		
		selStr += "你好呀，我叫休咪，我并不是胆小鬼。\r\n\r\n";
		//休咪丢失的金币/等级等级20
		if(cm.getLevel()>=20 ){
			if (cm.getPlayer().getQuest().getQuestData(任务) != "end") {
				selStr += " #L1##b休咪丢失的金币#k#l\r\n";
			}
		}
		//休咪丢失的钞票/等级等级30
		if(cm.getLevel()>=30 ){
			if (cm.getPlayer().getQuest().getQuestData(任务2) != "end") {
				selStr += " #L2##b休咪丢失的钞票#k#l\r\n";
			}
		}
		//休咪丢失的钱包/等级等级40
		if(cm.getLevel()>=40 ){
			if (cm.getPlayer().getQuest().getQuestData(任务3) != "end" ) {
				selStr += " #L3##b休咪丢失的钱包#k#l\r\n";
			}
		}
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
            case 1:
                cm.dispose();
                cm.openNpc(1052102,1);
                break;
			case 2:
                cm.dispose();
                cm.openNpc(1052102,2);
                break;
			case 3:
                cm.dispose();
                cm.openNpc(1052102,3);
                break;
			default:
				break;
        }
    }
}