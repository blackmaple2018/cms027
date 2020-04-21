
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

    var 任务 = 1001000;
    if (cm.getLevel() >= 25 && cm.getJobId() != 0) {
        if (cm.getPlayer().getQuest().getQuestData(任务) == "s") {

            if (cm.getInventory(4).isFull(1)) {
                cm.sendOk("请保证 #b其他栏#k 至少有2个位置。");
                cm.dispose();
                return;
            }

            if (!cm.haveItem(4001000, 1)) {
                cm.sendOk("去勇士部落，帮我找回的#b玻璃鞋#k，拜托你了。");
                cm.dispose();
            } else if (cm.haveItem(4001000, 1)) {
                cm.gainItem(4001000, -1);
                cm.gainItem(4006002, 50);
                cm.gainExp(200000);
                cm.completeQuest(任务, "end");
                cm.sendOk("谢谢你为我找回了鞋子，这是给你的报酬，用这个石头，可以强化装备哦。");
                cm.dispose();
            } else {
                cm.sendOk("嗯~");
                cm.dispose();
            }
        }
    }
}