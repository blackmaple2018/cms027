
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

    var 任务 = 1000602;
    if (cm.getPlayer().getQuest().getQuestData(任务) != "1") {
 
        if (!cm.haveItem(4000001, 50) || !cm.haveItem(4000005, 50) || !cm.haveItem(4000004, 50)|| !cm.haveItem(4000016, 50)) {
            cm.sendOk("需要#b花蘑菇的盖、叶子、软软的液体、红蜗牛的壳各50个#k...可能从草原上的怪物可以得到吧？");
            cm.dispose();
        } else {
            cm.gainItem(4000001, -50);
            cm.gainItem(4000005, -50);
            cm.gainItem(4000004, -50);
			cm.gainItem(4000016, -50);
            cm.gainExp(120000);
            cm.completeQuest(任务, "1e");
			cm.completeQuest(任务, "2");
            cm.sendOk("谢谢你的帮助，嗯。");
            cm.dispose();
        }
    }else if (cm.getPlayer().getQuest().getQuestData(任务) != "2") {
        if (!cm.haveItem(4000006, 50) || !cm.haveItem(4000013, 50) || !cm.haveItem(4000020, 50)|| !cm.haveItem(4000008, 50)) {
            cm.sendOk("需要#b三眼章鱼的足、风独眼兽之尾、野猪的尖牙、道符各50个#k。");
            cm.dispose();
        } else {
            cm.gainItem(4000006, -50);
            cm.gainItem(4000013, -50);
            cm.gainItem(4000020, -50);
			cm.gainItem(4000008, -50);
            cm.gainExp(220000);
            cm.completeQuest(任务, "2e");
			cm.completeQuest(任务, "3");
            cm.sendOk("谢谢你的帮助，嗯。还要其他的材料，你会帮助我的吗？");
            cm.dispose();
        }
	 }else if (cm.getPlayer().getQuest().getQuestData(任务) != "3") {
        if (!cm.haveItem(4000014, 50) || !cm.haveItem(4000022, 50) || !cm.haveItem(4000033, 50)|| !cm.haveItem(4000029, 50)) {
            cm.sendOk("收集#b龙的头骨、石块、黑鳄鱼之皮、猴子的香蕉各50个的#k。");
            cm.dispose();
        } else {
            cm.gainItem(4000014, -50);
            cm.gainItem(4000022, -50);
            cm.gainItem(4000033, -50);
			cm.gainItem(4000029, -50);
            cm.gainExp(320000);
            cm.completeQuest(任务, "3e");
			cm.completeQuest(任务, "4");
            cm.sendOk("谢谢你的帮助，嗯。还要其他的材料，你会帮助我的吗？");
            cm.dispose();
        }	
	}else if (cm.getPlayer().getQuest().getQuestData(任务) != "4") {
        if (!cm.haveItem(4031006, 50) || !cm.haveItem(4000025, 50) || !cm.haveItem(4000027, 50)|| !cm.haveItem(4000028, 50)|| !cm.haveItem(4021007, 1)) {
            cm.sendOk("收集#b奇怪的药、黑石头、怪猫的眼睛、月牙牛魔王的角各50个和1个钻石#k。");
            cm.dispose();
        } else {
            cm.gainItem(4031006, -50);
            cm.gainItem(4000025, -50);
            cm.gainItem(4000027, -50);
			cm.gainItem(4000028, -50);
			cm.gainItem(4021007, -1);
            cm.gainExp(520000);
            cm.completeQuest(任务, "end");
            cm.sendOk("谢谢你的帮助，房子盖起来了。");
            cm.dispose();
        }		
		
	}
}























































