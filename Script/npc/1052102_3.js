
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


    if (cm.getLevel() >= 40) {
        var 任务3 = 1001202;
        if (cm.getPlayer().getQuest().getQuestData(任务3) == "s") {
            if (cm.getInventory(2).isFull(3)) {
                cm.sendOk("请保证 #b消耗栏#k 至少有4个位置。");
                cm.dispose();
                return;
            }
            if (!cm.haveItem(4031041, 1)) {
                cm.sendOk("请在#b工地B3#k里找到休咪弄丢的钱包。");
                cm.dispose();
            } else {
                cm.gainItem(4031041, -1);
                cm.gainItem(2040702, 5);
                cm.gainExp(100000);
                cm.gainMeso(1000000);
                cm.completeQuest(任务3, "end");
                cm.sendOk("谢谢你，感谢你的帮助。");
                cm.dispose();
            }

        }
    }
}