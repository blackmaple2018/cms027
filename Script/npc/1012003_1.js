
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

    var 任务 = 1000500;

    if (cm.getPlayer().getQuest().getQuestData(任务) == "t") {

        if (cm.getInventory(2).isFull(1)) {
            cm.sendOk("请保证 #b消耗栏#k 至少有2个位置。");
            cm.dispose();
            return;
        }
		if(cm.haveItem(4001003, 1)){
			cm.sendOk("去找我的儿子吧。");
            cm.dispose();
            return;
		}
        if (!cm.haveItem(4000007, 50) || !cm.haveItem(4000002, 100)) {
            cm.sendOk("你如果收集来为维修射手村需要的#b50个火独眼兽之尾和100个猪猪的蝴蝶结#k，我就听听你说说看。");
            cm.dispose();
        } else {
            cm.gainItem(4000007, -50);
			cm.gainItem(4000002, -100);
            cm.gainExp(100000);
			cm.gainItem(4001003, 1);
            cm.sendOk("感谢你为射手村的帮助，来拿着 #i4001003# ，你拿着这个，去找我儿子吧。");
            cm.dispose();
        }
    } 
}


