
var status = 0;
function start() {
    status = -1;
    action(1, 0, 0)
}

function action(mode, type, selection) {
    if (status <= 0 && mode <= 0) {
        cm.sendOk("我是镇守在这里的护卫。");
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++
    } else {
        status--
    }
    if (cm.getLevel() >= 50 && (cm.getJobId() >= 100 && cm.getJobId()<200)) {
        var 任务 = 1000801;
        if (cm.getPlayer().getQuest().getQuestData(任务) == "s") {

            if (cm.getInventory(1).isFull(2)) {
                cm.sendOk("请保证 #b装备栏#k 至少有2个位置。");
                cm.dispose();
                return;
            }

            if (!cm.haveItem(4031042, 1) || !cm.haveItem(4011005, 10) || !cm.haveItem(4000030, 50)|| !cm.haveItem(4003000, 40)|| !cm.haveItem(4000046, 3)) {
                cm.sendOk("做手套需要#b1个黑翼毛，10个紫矿石，50个龙皮，40个螺丝钉，3个长枪牛魔王之角#k！");
                cm.dispose();
            } else {
                cm.gainItem(4031042, -1);
                cm.gainItem(4011005, -10);
                cm.gainItem(4000030, -50);
				cm.gainItem(4003000, -40);
                cm.gainItem(4000046, -3);
				cm.gainItem(1002039, 1);
				//1002051 1002039 1002043
                cm.gainExp(100000);
                cm.completeQuest(任务, "end");
                cm.sendOk("谢谢你，感谢你的帮助。");
                cm.dispose();
            }
        } else {
            cm.sendYesNo("我是镇守在这里的护卫。你愿意帮助我吗？");
        }
    }
}

