
var status = 0;
function start() {
    status = -1;
    action(1, 0, 0)
}

function action(mode, type, selection) {
    if (status <= 0 && mode <= 0) {
        cm.sendOk("我是会魔法的妖精~~");
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++
    } else {
        status--
    }

    var 任务 = 1000700;
    if (cm.getLevel() >= 25) {
        if (cm.getPlayer().getQuest().getQuestData(任务) == "1_01") {
            if (cm.getInventory(2).isFull(1)) {
                cm.sendOk("请保证 #b消耗栏#k 至少有2个位置。");
                cm.dispose();
                return;
            }

            if (!cm.haveItem(4031020, 1)) {
                cm.sendOk("去魔法密林吧，有人知道这个东西的。我要的是 #i4031020#　嗯，这个。");
                cm.dispose();
            } else if (cm.haveItem(4031020, 1)) {
                cm.gainItem(4031020, -1);
				cm.gainItem(2000004, 10);
                cm.gainExp(200000);
                cm.completeQuest(任务, "1_99");
                cm.sendOk("这个草药，嗯还不错，这是给你的报酬。");
                cm.dispose();
            } 
        }
    }
}