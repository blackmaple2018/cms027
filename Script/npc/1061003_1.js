
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

    var 任务 = 1000600;
    if (cm.getInventory(1).isFull(1)) {
        cm.sendOk("请保证 #b装备栏#k 至少有2个位置。");
        cm.dispose();
        return;
    }
    if (!cm.haveItem(4031016, 1)) {
        cm.sendOk("帮我去找回 #i4031016# 吧，我儿子偷偷拿出去了。");
		cm.completeQuest(任务, "2");
        cm.dispose();
    } else {
        cm.gainItem(4031016, -1);
        cm.gainExp(20000);
		if(cm.getGender()==0){
			cm.gainItem(1050018, 1);
		}else{
			cm.gainItem(1051017, 1);
		}
        cm.completeQuest(任务, "e");
        cm.sendOk("谢谢你帮我找回了这本书，这是给你的衣服，你试试吧。");
        cm.dispose();
    }
}

