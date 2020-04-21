
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

    if (cm.getLevel() < 20 || cm.getJobId() == 0) {
        cm.sendOk("那些蘑菇就知道欺负我~呜呜。");
        cm.dispose();
        return;
    }
    var 任务 = 1000300;
    if (cm.getPlayer().getQuest().getQuestData(任务) == "p") {
        if (cm.getInventory(2).isFull(3)) {
            cm.sendOk("请保证 #b消耗栏#k 至少有4个位置。");
            cm.dispose();
            return;
        }
        if (!cm.haveItem(4000009, 60) || !cm.haveItem(4000012, 60)) {
            cm.sendOk("收集#b60个蓝蘑菇的盖和60个绿蘑菇的盖#k作为替我报仇的证据。你好像没带来证据啊？");
            cm.dispose();
        } else {
            cm.gainItem(4000009, -60);
            cm.gainItem(4000012, -60);
            cm.gainItem(2000000, 100);
            cm.gainItem(2000001, 100);
            cm.gainItem(2040000, 5);
            cm.gainItem(2040001, 5);
            cm.gainItem(2040002, 5);
            cm.gainExp(10000);
            cm.completeQuest(任务, "end");
            cm.sendOk("谢谢你，感谢你的帮助。这是给你的报酬 #i2040000# #i2040001# #i2040002# ");
            cm.dispose();
        }
    }
}