
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
	var 任务 = 1000200;
	if (status == 0) {
		if (cm.getPlayer().getQuest().getQuestData(任务)=="m7" ||cm.hasQuestInProcess(任务)){
			if (cm.getInventory(1).isFull(3)) {
                cm.sendOk("请保证 #b装备栏#k 至少有4个位置。");
                cm.dispose();
                return;
            } 
			if (cm.haveItem(4031006,1)){
				cm.gainItem(4031006,-1);
				cm.gainItem(1002026);
				cm.gainMeso(5000);
			    cm.completeQuest(任务, "end");
				cm.sendOk(" 感谢你送来的药水，一点心意，请收下吧。");
				cm.dispose();
		    } else{
				cm.sendOk("你~咳咳~你没有带来药水吗？");
				cm.dispose();
			}
		} else if (cm.hasQuestCompleted(任务)) {
			cm.sendOk("咳咳~咳咳~~");
			cm.dispose();
	    } else {
			cm.sendYesNo("咳咳~咳咳~你~可以帮我去拿药吗，我~~咳咳~~现在很难受。");
		}
	} else if (status == 1) {
		cm.sendNext("真是太感谢了，去明珠港见一个叫特奥的水手，他那里有我需要的药水。");
		cm.startQuest(任务);
		//cm.completeQuest(任务, "m1");
		cm.dispose();
	}
}