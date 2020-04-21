
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
    if (cm.getLevel() >= 15 && cm.getJobId() != 0) {
        var 任务 = 1000800;
        if (cm.getPlayer().getQuest().getQuestData(任务) == "s") {

            if (cm.getInventory(2).isFull(3)) {
                cm.sendOk("请保证 #b消耗栏#k 至少有4个位置。");
                cm.dispose();
                return;
            }

            if (!cm.haveItem(4000034, 100) || !cm.haveItem(4000042, 10) || !cm.haveItem(2020000, 1)) {
                cm.sendOk("我现在需要的#b100个青蛇的皮和10个蝙蝠的翅膀及1个沙拉#k。");
                cm.dispose();
            } else {
                cm.gainItem(4000034, -100);
                cm.gainItem(4000042, -10);
                cm.gainItem(2020000, -1);
                cm.gainItem(2000005, 50);
                cm.gainExp(10000);
                cm.completeQuest(任务, "end");
                cm.sendOk("谢谢你，感谢你的帮助。");
                cm.dispose();
            }
        } else {
            cm.sendYesNo("我是镇守在这里的护卫。你愿意帮助我吗？");
        }
    }
}

