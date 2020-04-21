function start() {
    if(cm.haveItem(4031047)){
        if (cm.getEventNotScriptOpen("Barcos")){
            cm.sendYesNo("你想要搭上去往天空之城的船吗，请问你是否有票呢？");
		}else{
            cm.sendOk("请耐心等待，飞船还未抵达。");
            cm.dispose();
        }
    } else {
        cm.sendOk("哎你没有票，那怎么办····还不快去站台购买。");
        cm.dispose();
    }
}

function action(mode, type, selection) {
    cm.gainItem(4031047, -1);
    cm.warp(200000112, 0);
    cm.dispose();
}