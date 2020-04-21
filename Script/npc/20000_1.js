
var status = 0;
function start() {
    status = -1;
    action(1, 0, 0)
}

function action(mode, type, selection) {
    if (status <= 0 && mode <= 0) {
        cm.sendOk("你好~");
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++
    } else {
        status--
    }
	
	
    if (cm.getLevel() >= 15) {
        var 任务 = 1000900;
		if (cm.getPlayer().getQuest().getQuestData(任务) == "10") {
            if (!cm.haveItem(4031020, 1)) {
                cm.sendOk("你有拿到 #i4031020# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031020, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务, "9");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务) == "9") {
            if (!cm.haveItem(4031020, 1)) {
                cm.sendOk("你有拿到 #i4031020# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031020, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务, "8");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务) == "8") {
            if (!cm.haveItem(4031020, 1)) {
                cm.sendOk("你有拿到 #i4031020# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031020, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务, "7");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务) == "7") {
            if (!cm.haveItem(4031020, 1)) {
                cm.sendOk("你有拿到 #i4031020# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031020, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务, "6");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务) == "6") {
            if (!cm.haveItem(4031020, 1)) {
                cm.sendOk("你有拿到 #i4031020# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031020, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务, "5");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务) == "5") {
            if (!cm.haveItem(4031020, 1)) {
                cm.sendOk("你有拿到 #i4031020# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031020, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务, "4");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务) == "4") {
            if (!cm.haveItem(4031020, 1)) {
                cm.sendOk("你有拿到 #i4031020# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031020, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务, "3");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务) == "3") {
            if (!cm.haveItem(4031020, 1)) {
                cm.sendOk("你有拿到 #i4031020# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031020, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务, "2");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务) == "2") {
            if (!cm.haveItem(4031020, 1)) {
                cm.sendOk("你有拿到 #i4031020# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031020, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务, "1");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务) == "1") {
            if (!cm.haveItem(4031020, 1)) {
                cm.sendOk("你有拿到 #i4031020# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031020, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务, "end");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        }
    }
}