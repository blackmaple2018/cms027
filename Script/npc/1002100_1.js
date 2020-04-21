
var status = 0;
function start() {
    status = -1;
    action(1, 0, 0)
}

function action(mode, type, selection) {
    if (status <= 0 && mode <= 0) {
        cm.sendOk("我想家了~");
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++
    } else {
        status--
    }

    var 任务 = 1000400;
    if (status == 0) {
        if (cm.getPlayer().getQuest().getQuestData(任务) == "j") {
            if (cm.getInventory(2).isFull(1)) {
                cm.sendOk("请保证 #b消耗栏#k 至少有2个位置。");
                cm.dispose();
                return;
            }
            if (!cm.haveItem(4000015, 120) || !cm.haveItem(4000020, 100)) {
                cm.sendOk("我想要去冒险，但是我父亲不允许我出去闯荡，如果我可以收集一些证明，你还没收集到#b100个野猪的尖牙和120个角蘑菇的盖#k。");
                cm.dispose();
            } else if (cm.haveItem(4000015, 120) || cm.haveItem(4000020, 100)) {
                cm.gainItem(4000015, -120);
                cm.gainItem(4000020, -100);
                cm.gainItem(2040805, 10);
                cm.gainExp(200000);
                cm.completeQuest(任务, "end");
                cm.sendOk("哇，感谢你呀勇士，你帮助我收集了这些物品，我父亲肯定会认可我的，这是给你的一点点报酬。");
                cm.dispose();
            } 
        }
    }
}