
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
	
    var 任务 = 1000301;
	
    if (cm.getPlayer().getQuest().getQuestData(任务) == "s") {
        if (cm.getInventory(4).isFull(3)) {
            cm.sendOk("请保证 #b其他栏#k 至少有4个位置。");
            cm.dispose();
            return;
        }

        if (!cm.haveItem(4021008, 1) || !cm.haveItem(4001005, 1) || !cm.haveItem(4000028, 30)) {
            cm.sendOk("如你替皮亚还给我皮亚借过去的物品，我就给你披风...皮亚借去的物品是#b1个黑水晶，1个古代的卷轴，30个月牙牛魔王的角#k…");
            cm.dispose();
        } else {
            cm.gainItem(4021008, -1);
            cm.gainItem(4001005, -1);
            cm.gainItem(4000028, -30);
            cm.gainItem(4031043, 1);
            cm.gainExp(100000);
            cm.completeQuest(任务, "p");
            cm.sendOk("既然你替皮亚还了东西，那么这个披风就还给你吧。");
            cm.dispose();
        }
    }else{
		cm.sendOk("欢迎来到射手村。");
		cm.dispose();
	}

}

