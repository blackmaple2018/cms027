
var status = 0;

function start() {
    cm.sendYesNo("你要离开这里吗？");
}

function action(mode, type, selection) {
    if (mode > 0) {
        status++;
    } else {
        cm.dispose();
    }
    if (status == 1) {
        cm.sendOk("好吧，下次见。");
    } else if (status == 2) {
        if (cm.getPlayer().getMap().getId() == 101000301) {
            cm.warp(101000300, 0);
        } else {
            cm.warp(200000111, 0);
        }
        cm.dispose();
    }
}