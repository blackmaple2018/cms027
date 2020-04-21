
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

    var 任务 = 1000201;
	if (cm.getPlayer().getQuest().getQuestData(任务) == "mc") {
		cm.sendOk("这个啊为了唤醒剑，需要#b1个星石，1个冰块，1个上古卷轴，1个火焰翼毛#k，迷宫入口的麦克可能知道，如果你不懂或者不知道就去问问他把。");
		cm.dispose();
	}else{
		cm.sendOk("Emmmm，别打扰我。");
		cm.dispose();
	}
}

