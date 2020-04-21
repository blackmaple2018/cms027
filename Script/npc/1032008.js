var status = 0;
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    status++;
    if (mode == 0) {
        cm.sendNext("你有一些经济的负担而无法搭船对吧?");
        cm.dispose();
        return;
    }
	
    if (status == 0) {
         if (cm.getEventNotScriptOpen("Barcos")) {
            cm.sendYesNo("你想要搭上去往天空之城的船吗，请问你是否有票呢？");
        } else {
            cm.sendOk("请耐心等待，飞船还未抵达。");
            cm.dispose();
        }
    } else if (status == 1) {
        if (!cm.haveItem(4031045)) {
            cm.sendOk("你没有#b#t4031045##k 所以你不能上船，如果你要上船请去左边购买票据。");
        } else {
            cm.gainItem(4031045, -1);
            cm.warp(101000301, 0);
        }
        cm.dispose();
    }
}