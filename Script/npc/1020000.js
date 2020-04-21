

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
		
		selStr += "你好啊勇士，我是勇士部落的酋长，或者你有什么可以帮到我的吧。这里是勇士才该来的地方，你是勇士吗？啊？\r\n\r\n";
		//酋长的家修理
		if (cm.getPlayer().getQuest().getQuestData(1000100) != "end") {	
			selStr += " #L1##b酋长的家修理#k#l\r\n";
		}
		//酋长盖房子//等级30，人气度10，新手不能做.
		if(cm.getLevel()>=30 && cm.getJobId()!=0 && cm.getFame()>=10){
			if (cm.getPlayer().getQuest().getQuestData(1000101) != "pe") {	
				selStr += " #L2##b酋长盖房子#k#l\r\n";
			}
		}
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
            case 1:
                cm.dispose();
                cm.openNpc(1020000,1);
                break;
			 case 2:
                cm.dispose();
                cm.openNpc(1020000,2);
                break;
			default:
				break;
        }
    }
}