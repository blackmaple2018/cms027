
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

    if (cm.getLevel() >= 30) {
        var 任务2 = 1000901;
        if (cm.getPlayer().getQuest().getQuestData(任务2) == "20") {
            if (!cm.haveItem(4031022, 1)) {
                cm.sendOk("你有拿到 #i4031022# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031022, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务2, "19");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务2) == "19") {
            if (!cm.haveItem(4031022, 1)) {
                cm.sendOk("你有拿到 #i4031022# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031022, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务2, "18");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务2) == "18") {
            if (!cm.haveItem(4031022, 1)) {
                cm.sendOk("你有拿到 #i4031022# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031022, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务2, "17");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务2) == "17") {
            if (!cm.haveItem(4031022, 1)) {
                cm.sendOk("你有拿到 #i4031022# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031022, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务2, "16");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务2) == "16") {
            if (!cm.haveItem(4031022, 1)) {
                cm.sendOk("你有拿到 #i4031022# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031022, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务2, "15");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务2) == "15") {
            if (!cm.haveItem(4031022, 1)) {
                cm.sendOk("你有拿到 #i4031022# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031022, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务2, "14");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务2) == "14") {
            if (!cm.haveItem(4031022, 1)) {
                cm.sendOk("你有拿到 #i4031022# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031022, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务2, "13");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务2) == "13") {
            if (!cm.haveItem(4031022, 1)) {
                cm.sendOk("你有拿到 #i4031022# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031022, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务2, "12");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务2) == "12") {
            if (!cm.haveItem(4031022, 1)) {
                cm.sendOk("你有拿到 #i4031022# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031022, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务2, "11");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务2) == "11") {
            if (!cm.haveItem(4031022, 1)) {
                cm.sendOk("你有拿到 #i4031022# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031022, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务2, "10");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务2) == "10") {
            if (!cm.haveItem(4031022, 1)) {
                cm.sendOk("你有拿到 #i4031022# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031022, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务2, "9");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务2) == "9") {
            if (!cm.haveItem(4031022, 1)) {
                cm.sendOk("你有拿到 #i4031022# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031022, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务2, "8");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务2) == "8") {
            if (!cm.haveItem(4031022, 1)) {
                cm.sendOk("你有拿到 #i4031022# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031022, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务2, "7");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务2) == "7") {
            if (!cm.haveItem(4031022, 1)) {
                cm.sendOk("你有拿到 #i4031022# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031022, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务2, "6");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务2) == "6") {
            if (!cm.haveItem(4031022, 1)) {
                cm.sendOk("你有拿到 #i4031022# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031022, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务2, "5");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务2) == "5") {
            if (!cm.haveItem(4031022, 1)) {
                cm.sendOk("你有拿到 #i4031022# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031022, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务2, "4");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务2) == "4") {
            if (!cm.haveItem(4031022, 1)) {
                cm.sendOk("你有拿到 #i4031022# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031022, -1);
                cm.gainExp(580000);
                cm.completeQuest(任务2, "3");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务2) == "3") {
            if (!cm.haveItem(4031022, 1)) {
                cm.sendOk("你有拿到 #i4031022# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031022, -1);
                cm.gainExp(580000);
                cm.completeQuest(任务2, "2");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务2) == "2") {
            if (!cm.haveItem(4031022, 1)) {
                cm.sendOk("你有拿到 #i4031022# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031022, -1);
                cm.gainExp(580000);
                cm.completeQuest(任务2, "1");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务2) == "1") {
            if (!cm.haveItem(4031022, 1)) {
                cm.sendOk("你有拿到 #i4031022# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031022, -1);
                cm.gainExp(580000);
                cm.completeQuest(任务2, "end");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        }
    }
}