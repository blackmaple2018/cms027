

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
	var 任务 = 1000200;
	if (status <= 0) {
        var selStr = "";
		
		selStr += "我是索非亚，你要找我买药水吗？\r\n\r\n";
		//玛亚和奇怪的药//等级15，新手不能做
		if(cm.getLevel()>=15 && cm.getJobId()!=0){
			if (cm.getPlayer().getQuest().getQuestData(任务)=="m2" || cm.getPlayer().getQuest().getQuestData(任务)=="m3"|| cm.getPlayer().getQuest().getQuestData(任务)=="m5" ){
				selStr += " #L1##b玛亚和奇怪的药#k#l\r\n";
			}
		}
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
            case 1:
                cm.dispose();
                cm.openNpc(1022100,1);
                break;
			default:
				break;
        }
    }
}