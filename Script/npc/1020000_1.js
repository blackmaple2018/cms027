
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
    if (status == 0) {
        if (cm.hasQuestCompleted(1000100)) {
			cm.sendOk("勇士部落，那就应该是勇士来的地方。");
			cm.dispose();
        } else if (cm.hasQuestInProcess(1000100)) {
            if (cm.getInventory(1).isFull(3)) {
                cm.sendOk("请保证 #b装备栏#k 至少有4个位置。");
                cm.dispose();
                return;
            }
            if (!cm.haveItem(4000018, 50) || !cm.haveItem(4000003, 30)) {
                cm.sendOk("你好像还没有收集收集完#b30个树枝和50个木块#k。");
                cm.dispose();
            } else {
                cm.sendOk("速度真快，把材料交给我吧，作为答礼我将给你我的盾牌");
                cm.gainItem(4000018, -50);
                cm.gainItem(4000003, -30);
                cm.gainItem(1092003);
                cm.completeQuest(1000100, "end");
                cm.dispose();
            }
        } else {
            cm.sendYesNo("哎呀~屋漏偏逢连夜雨，勇士，能否帮我个小忙？");
        }
    } else if (status == 1) {
        cm.sendNext("真是太感谢了，我现在需要#b30个树枝和50个木块#k来修复我的屋子，你如果能帮我收集到，我会有份礼物给你的。");
        cm.startQuest(1000100);
        cm.dispose();
    }
}