
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

    var 任务 = 1000200;
    if (cm.getPlayer().getQuest().getQuestData(任务) == "m3") {
        cm.completeQuest(任务, "m4");
        cm.sendOk("去下去洞穴帮我收集40个#b道符#k吧，我就给你变异蘑菇的血。");
        cm.dispose();
    } else if (cm.getPlayer().getQuest().getQuestData(任务) == "m4") {
        if (cm.getInventory(4).isFull(3)) {
            cm.sendOk("请保证 #b其它栏#k 至少有4个位置。");
            cm.dispose();
            return;
        }
        if (!cm.haveItem(4000008, 40)) {
            cm.sendOk("你还没收集到#b道符#k吗，地下洞穴你知道吧，就是在林中之城。");
            cm.dispose();
        } else {
            cm.sendOk("谢谢你咯，来，给你 #i4031005# 去见索非亚吧~");
            cm.gainItem(4000008, -40);
            cm.gainItem(4031005, 1);
            cm.completeQuest(任务, "m5");
            cm.dispose();
        }
    } else {
        cm.sendOk("面对~疾风咯~");
        cm.dispose();
    }


}

