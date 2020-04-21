

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
		
		selStr += "咳咳~咳咳~~我有点不舒服，有咩什么药品可以帮助到我的，好像~~~咳咳~~咳咳~~~\r\n\n";
		//玛亚和奇怪的药//等级15，新手不能做
		if(cm.getLevel()>=15 && cm.getJobId()!=0){
			if (cm.getPlayer().getQuest().getQuestData(1000200) != "end") {	
				selStr += " #L1##b玛亚和奇怪的药#k#l\r\n";
			}
		}
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
            case 1:
                cm.dispose();
                cm.openNpc(1012101,1);
                break;
			default:
				break;
        }
    }
}