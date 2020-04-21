
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
    var 任务2 = 1000201;
    if (status == 0) {
        if (cm.getPlayer().getQuest().getQuestData(任务2) == "mc") {
            cm.sendOk("你有办法让 #i1302014# 觉醒吗？");
            cm.dispose();
        } else if (cm.getPlayer().getQuest().getQuestData(任务2) == "ms") {
            if (cm.getInventory(1).isFull(3)) {
                cm.sendOk("请保证 #b装备栏#k 至少有4个位置。");
                cm.dispose();
                return;
            }
            cm.gainItem(1302014, 1);
            cm.completeQuest(任务2, "mc");
            cm.sendOk("拿着这把 #i1302014# ，你有办法让它觉醒吗？");
            cm.dispose();
        } else if (cm.getPlayer().getQuest().getQuestData(任务2) == "mh") {
            if (cm.getInventory(1).isFull(3)) {
                cm.sendOk("请保证 #b装备栏#k 至少有4个位置。");
                cm.dispose();
                return;
            }
            if (!cm.haveItem(1302015, 1)) {
                cm.sendOk("英雄战剑呢？？？");
                cm.dispose();
            } else {
                cm.sendSimple("请问你如何处理 #i1302015# 战剑。\r\n\r\n#b#L1#还给麦吉#l  \r\n#L2#留着自己用#l");
            }

        } else {
            cm.sendOk("面对~疾风咯~");
            cm.dispose();
        }

    } else if (status == 1) {
        if (selection == 1) {
            cm.gainItem(1302015, 1);
            cm.gainItem(1032012, 1);
            cm.completeQuest(任务2, "yes");
            cm.sendOk("感谢你，我为你准备了一双耳环，这是送给你的。");
            cm.dispose();
        } else if (selection == 2) {
            cm.completeQuest(任务2, "no");
            cm.sendOk("你竟然要留下这把剑，行吧，你滚吧，不要让我再看到你。");
            cm.dispose();
        }
    }
}

