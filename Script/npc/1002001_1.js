

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
		if (cm.getPlayer().getQuest().getQuestData(任务)=="m1"){
			cm.completeQuest(任务, "m2");
			cm.sendOk("   啊，我有你需要的药水，是这个 #i4031006#  吧，但是给你的话，你得帮我取勇士部落取得#b发光的石头#k，这种东西，去见勇士部落杂货店的#r索非亚#k吧。");
			cm.dispose();
		}else if (cm.getPlayer().getQuest().getQuestData(任务)=="m6"){
			if (cm.getInventory(4).isFull(3)) {
                cm.sendOk("请保证 #b其他栏#k 至少有4个位置。");
                cm.dispose();
                return;
            }
			if (!cm.haveItem(4031004,1)){
				cm.sendOk("你····似乎没有找到发光的石头。");
				cm.dispose();
			} else { 
				cm.sendOk("感谢你拿来了发光的石头，给你药水 #i4031006#  。");
				cm.gainItem(4031004,-1);
				cm.gainItem(4031006,1);
				cm.completeQuest(任务, "m7");
				cm.dispose();
			}
		}else{
			cm.sendOk("我是水手，不怕水的水手，特~奥。");
			cm.dispose();
		}
	} 
}