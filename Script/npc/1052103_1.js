
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

    var 任务2 = 1001801;
    if (cm.getPlayer().getQuest().getQuestData(任务2) == "1s") {
        if (cm.getInventory(2).isFull(3)) {
            cm.sendOk("请保证 #b消耗栏#k 至少有4个位置。");
            cm.dispose();
            return;
        }
        if (!cm.haveItem(4000015, 100) || !cm.haveItem(4000034, 50)) {
            cm.sendOk("我还差#b100个刺蘑菇的盖#k和#b50个青蛇皮#k… ");
            cm.dispose();
        } else {
            cm.gainItem(4000015, -100);
            cm.gainItem(4000034, -50);
            cm.gainExp(100000);
            cm.gainMeso(50000);
            cm.completeQuest(任务2, "end");
            cm.sendOk("谢谢你，感谢你的帮助。帮药店老板找所需要的东西，会得到多个白色药水。很期待铭仁用那些材料做出的药水！而且可能接受新的委托，然后，去找内拉~");
            cm.dispose();
        }
    }
}

