

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
    var 任务2 = 1000201;
	if (status <= 0) {
        var selStr = "";
		
		selStr += "死亡如飓风，常伴吾之身？\r\n\r\n";
		//玛亚和奇怪的药//等级15，新手不能做
		if(cm.getLevel()>=15 && cm.getJobId()!=0){
			if (cm.getPlayer().getQuest().getQuestData(任务)=="m3" || cm.getPlayer().getQuest().getQuestData(任务)=="m4"){
				selStr += " #L1##b玛亚和奇怪的药#k#l\r\n";
			}
		}
		//等级45，新手不能做。//麦吉的旧战剑
		if(cm.getLevel()>=45 && cm.getJobId()!=0){
		if (cm.getPlayer().getQuest().getQuestData(任务2) == "mc" || cm.getPlayer().getQuest().getQuestData(任务2) == "ms" || cm.getPlayer().getQuest().getQuestData(任务2) == "mh") {
				selStr += " #L2##b麦吉的旧战剑#k#l\r\n";
			}
		}
		
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
            case 1:
                cm.dispose();
                cm.openNpc(1022002,1);
                break;
			case 2:
                cm.dispose();
                cm.openNpc(1022002,2);
                break;
			default:
				break;
        }
    }
}