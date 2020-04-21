
var status = 0;
function start() {
    status = -1;
    action(1, 0, 0)
}

function action(mode, type, selection) {
    if (status <= 0 && mode <= 0) {
        cm.sendOk("我想家了~");
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++
    } else {
        status--
    }

    var 任务 = 1000500;
    if (status == 0) {
        if (cm.getPlayer().getQuest().getQuestData(任务) == "end") {
            cm.sendOk("我是斯坦长老的儿子。");
            cm.dispose();
        } else if (cm.getPlayer().getQuest().getQuestData(任务) == "t") {
            if (cm.getInventory(1).isFull(3)) {
                cm.sendOk("请保证 #b装备栏#k 至少有4个位置。");
                cm.dispose();
                return;
            }


            if (!cm.haveItem(4001003, 1)) {
                cm.sendOk("你有拿来什么证明吗？你根本就没去见我的父亲吧。");
                cm.dispose();
            } else  if (cm.haveItem(4001003, 1)) {
                cm.gainItem(1032002, 1);
                cm.gainItem(4001003, -1);
                cm.gainExp(100000);
                cm.completeQuest(任务, "end");
                cm.sendOk("这是~~这个 #i4001003# 是我母亲的，我的父亲真的原谅我了，太感谢你了，我是来这边调查的，我会马上回去的。我送你一个耳环，真谢谢你，");
                cm.dispose();
            } else{

				cm.sendOk("去射手村找我父亲，帮我探探口风。");
				cm.dispose();
			}

        } else {
            cm.sendYesNo("啊~ 真没意思~我的父亲有找我吗？我才不信，你肯定是想把我忽悠回去，我父亲会揍死我的~，你帮我去射手村打听一下风声吧？");
        }
    } else if (status == 1) {
        cm.sendNext("谢谢你~我的父亲叫做斯坦，就在射手村公园那里。");
        cm.completeQuest(任务, "t");
        cm.dispose();
    }
}

