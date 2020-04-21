
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

    var 任务 = 1000600;

    if (cm.getInventory(2).isFull(1)) {
        cm.sendOk("请保证 #b消耗栏#k 至少有2个位置。");
        cm.dispose();
        return;
    }

    if (!cm.haveItem(4000013, 50) || !cm.haveItem(4000017, 5)) {
        cm.sendOk("做烤鳗鱼需要#b50个风独眼兽之尾和5个猪头#k。");
        cm.dispose();
    } else {
        cm.gainItem(4000013, -50);
        cm.gainItem(4000017, -5);
        cm.gainItem(4031014, 1);
        cm.gainExp(100000);
        cm.sendOk("拿着吧，这是做好的鳗鱼。");
        cm.dispose();
    }


}

