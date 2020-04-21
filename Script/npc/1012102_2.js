
var status = 0;
function start() {
    status = -1;
    action(1, 0, 0)
}

function action(mode, type, selection) {
    if (status <= 0 && mode <= 0) {
        cm.sendOk("皮卡皮卡~");
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++
    } else {
        status--
    }


    if (cm.getLevel() >= 55 && (cm.getJobId() == 400 || cm.getJobId() == 410 || cm.getJobId() == 411 || cm.getJobId() == 420 || cm.getJobId() == 421)) {
        var 任务2 = 1000301;
        if (cm.getPlayer().getQuest().getQuestData(任务2) == "s") {
            cm.sendOk("帮我回射手村的丽娜借去的#b红色披风#k。");
            cm.dispose();
        } else if (cm.getPlayer().getQuest().getQuestData(任务2) == "p") {

            if (cm.getInventory(2).isFull(3)) {
                cm.sendOk("请保证 #b消耗栏#k 至少有4个位置。");
                cm.dispose();
                return;
            }

            if (!cm.haveItem(4011004, 15) || !cm.haveItem(4000030, 50) || !cm.haveItem(4003000, 40) || !cm.haveItem(4031043, 1)) {
                cm.sendOk("帮我收集#b15个银，50个龙皮，40个螺丝钉#k。。。 尤其找回射手村的丽娜借去的#b红色披风#k。");
                cm.dispose();
            } else {
                cm.gainItem(4011004, -15);
                cm.gainItem(4000030, -50);
                cm.gainItem(4003000, -40);
                cm.gainItem(4031043, -1);
                cm.gainItem(1102040, 1);
                cm.gainExp(10000);
                cm.completeQuest(任务2, "end");
                cm.sendOk("谢谢你，感谢你的帮助。这是我为你准备的 #i1102040# ，嘿嘿，这个比衣服好多了。");
                cm.dispose();
            }
        } else {
            cm.sendOk("你真是个好人呀。送你的披风还喜欢吗？");
            cm.dispose();
        }
    }
}

