
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

   
    if (cm.getLevel() >= 20) {
		var 任务 = 1001200;
        if (cm.getPlayer().getQuest().getQuestData(任务) == "s") {
            if (cm.getInventory(2).isFull(3)) {
                cm.sendOk("请保证 #b消耗栏#k 至少有4个位置。");
                cm.dispose();
                return;
            }

            if (!cm.haveItem(4031039, 1)) {
                cm.sendOk("请在#b工地B1#k里找到休咪弄丢的那金币。");
                cm.dispose();
            } else {
                cm.gainItem(4031039, -1);
                cm.gainExp(100000);
                cm.completeQuest(任务, "end");
                cm.sendOk("谢谢你，感谢你的帮助。");
                cm.dispose();
            }
        }
    }
}
