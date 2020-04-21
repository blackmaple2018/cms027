
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


    if (cm.getInventory(4).isFull(1)) {
        cm.sendOk("请保证 #b其他栏#k 至少有2个位置。");
        cm.dispose();
        return;
    }

    if (!cm.haveItem(4021007, 1)) {
        cm.sendOk("你给我一个钻石，我才会给你牛奶。");
        cm.dispose();
    } else {
        cm.gainItem(4021007, -1);
        cm.gainItem(4031015, 1);
        cm.gainExp(100000);
        cm.sendOk("拿着吧，这是新鲜的牛奶。");
        cm.dispose();
    }


}

