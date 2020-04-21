
var status = 0;
function start() {
    status = -1;
    action(1, 0, 0)
}

function action(mode, type, selection) {
    if (status <= 0 && mode <= 0) {
        cm.sendOk("喵~");
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++
    } else {
        status--
    }

    if (cm.getLevel() >= 30) {
        var 任务2 = 1001201;
        if (cm.getPlayer().getQuest().getQuestData(任务2) == "end") {

            cm.sendOk("你帮我找回了钞票，太谢谢你了。");
            cm.dispose();

        } else if (cm.getPlayer().getQuest().getQuestData(任务2) == "s") {
            if (cm.getInventory(2).isFull(3)) {
                cm.sendOk("请保证 #b消耗栏#k 至少有4个位置。");
                cm.dispose();
                return;
            }
            if (!cm.haveItem(4031040, 1)) {
                cm.sendOk("请在#b工地B2#k里找到休咪弄丢的钞票。");
                cm.dispose();
            } else {
                cm.gainItem(4031040, -1);
                cm.gainItem(2000003, 100);
                cm.gainExp(100000);
                cm.gainMeso(500000);
                cm.completeQuest(任务2, "end");
                cm.sendOk("谢谢你，感谢你的帮助。");
                cm.dispose();
            }
        }
    }
}

