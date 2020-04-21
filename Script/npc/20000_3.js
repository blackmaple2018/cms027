
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

    if (cm.getLevel() >= 61) {
        var 任务3 = 1000902;
        if (cm.getPlayer().getQuest().getQuestData(任务3) == "30") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "29");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务3) == "29") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "28");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务3) == "28") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "27");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务3) == "27") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "26");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务3) == "26") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "25");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务3) == "25") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "24");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务3) == "24") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "23");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务3) == "23") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "22");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务3) == "22") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "21");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务3) == "21") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "20");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务3) == "20") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "19");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }

        } else if (cm.getPlayer().getQuest().getQuestData(任务3) == "19") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "18");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务3) == "18") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "17");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务3) == "17") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "16");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务3) == "16") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "15");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务3) == "15") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "14");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务3) == "14") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "13");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务3) == "13") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "12");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务3) == "12") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "11");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务3) == "11") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "10");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务3) == "10") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "9");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务3) == "9") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "8");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务3) == "8") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "7");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务3) == "7") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "6");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务3) == "6") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "5");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务3) == "5") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "4");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务3) == "4") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "3");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务3) == "3") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "2");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务3) == "2") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "1");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务3) == "1") {
            if (!cm.haveItem(4031028, 1)) {
                cm.sendOk("你有拿到 #i4031028# 吗？");
                cm.dispose();
            } else {
                cm.gainItem(4031028, -1);
                cm.gainExp(80000);
                cm.completeQuest(任务3, "end");
                cm.sendOk("谢谢你，请再帮我多采一些。");
                cm.dispose();
            }
        }

	}
}