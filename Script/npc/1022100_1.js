
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
		
		if (cm.getPlayer().getQuest().getQuestData(任务)=="m2" ){
			cm.completeQuest(任务, "m3");
			cm.sendOk("		你是要制作发光的石头？需要#b50个软软的液体，50个叶子，20个三眼章鱼的足，一瓶变异蘑菇的血#k，你帮我找我这些东西，我就可以帮你制作。");
			cm.dispose();
		}else if (cm.getPlayer().getQuest().getQuestData(任务)=="m3" || cm.getPlayer().getQuest().getQuestData(任务)=="m5"){
			if (cm.getInventory(4).isFull(3)) {
                cm.sendOk("请保证 #b其他栏#k 至少有4个位置。");
                cm.dispose();
                return;
            }
			if (!cm.haveItem(4000004,50)||!cm.haveItem(4000005,50)||!cm.haveItem(4000006,20)||!cm.haveItem(4031005,1)) {
				cm.sendOk("	需要#b50个软软的液体，50个叶子，20个三眼章鱼的足，一瓶变异蘑菇的血#k，你帮我找我这些东西，我就可以帮你制作。");
				cm.dispose();
			}else{
				cm.gainItem(4000004,-50);
				cm.gainItem(4000005,-50);
				cm.gainItem(4000006,-20);
				cm.gainItem(4031005,-1);
				cm.gainItem(4031004,1);
				cm.completeQuest(任务, "m6");
				cm.sendOk("很不错，你收集来了这些东西，来，给你 #i4031004# ~");
				cm.dispose();
			}
		}else{
			cm.sendOk("我是索非亚，你要找我买药水吗？");
			cm.dispose();
		}
	} 
}

