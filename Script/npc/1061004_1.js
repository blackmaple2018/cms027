
var status = 0;
function start() {
    status = -1;
    action(1, 0, 0)
}

function action(mode, type, selection) {
    if (status <= 0 && mode <= 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++
    } else {
        status--
    }

    var 任务 = 1000600;
    if (cm.getInventory(1).isFull(1)) {
        cm.sendOk("请保证 #b装备栏#k 至少有2个位置。");
        cm.dispose();
        return;
    }
    if (!cm.haveItem(4031014, 1) || !cm.haveItem(4031015, 1) || !cm.haveItem(4000029, 50)) {
        cm.sendOk("我好饿，你给我#b50个猴子的香蕉和丽娜的特质烤鳗鱼和新鲜的牛奶#k，我就把书给你。");
        cm.dispose();
    } else {
        cm.gainItem(4031014, -1);
        cm.gainItem(4031015, -1);
        cm.gainItem(4000029, -50);
        cm.gainExp(20000);
        cm.gainItem(4031016, 1);
        cm.completeQuest(任务, "2");
        cm.sendOk("哪··书给你了，你还回去吧。");
        cm.dispose();
    }
}

