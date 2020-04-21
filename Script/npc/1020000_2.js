
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


    if (cm.getPlayer().getQuest().getQuestData(1000101) == "p0") {
        if (cm.getInventory(2).isFull(3)) {
            cm.sendOk("请保证 #b消耗栏#k 至少有4个位置。");
            cm.dispose();
            return;
        }
        if (!cm.haveItem(4000022, 100) || !cm.haveItem(4003000, 30) || !cm.haveItem(4001004, 1)) {
            cm.sendOk("你可以帮助我修复房子吗？把#b100个石头人的石头，30个螺丝钉，一张地契#k都交给我，你就是帮到我了。");
            cm.dispose();
        } else {
            cm.gainItem(4000022, -100);
            cm.gainItem(4003000, -30);
            cm.gainItem(4001004, -1);
            cm.gainItem(1092003);
            cm.gainItem(2040805, 3);
            cm.gainExp(200000);
            cm.gainMeso(15000);
			cm.gainFame(2);
            cm.completeQuest(1000101, "pe");
            cm.sendOk("谢谢你，又帮我一次，做为报道，我也送你一些特殊的道具吧。");
            cm.dispose();
        }
    } else if (cm.getPlayer().getQuest().getQuestData(1000101) == "pe") {
        cm.sendOk("感谢你为我修理好了房子，你是真正的勇士。");
        cm.dispose();
    }

}