/*
 ZEVMS冒险岛(079)游戏服务端
 脚本：好友列表
 */
var status = -1;
var 金币 = 250000;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.sendNext("你有好友吗？");
            cm.对话结束();
            return;
        } else if (status >= 1) {
            cm.sendNext("我不认为你没有朋友，你只是不想花 #b" + 金币 + "#k 金币 来扩充自己的好友栏。");
            cm.对话结束();
            return;
        }
        status--;
    }
    if (status == 0) {
        cm.sendYesNo("    Hi~ 你的好友栏满了？我可以为你增加好友的位置哦，不过你得花费 #b" + 金币 + "#k 金币，我才能帮助你。 ");
    } else if (status == 1) {
        var capacity = cm.getBuddyCapacity();
        if (capacity >= 100 || cm.getMeso() < 金币) {
            cm.sendNext("嘿 你确定你有 #b" + 金币 + "#k 金币 如果足够确认是不是你的好友栏已经 #b100#k 格了。");
        } else {
            var newcapacity = capacity + 5;
            cm.gainMeso(-金币);
            cm.updateBuddyCapacity(newcapacity);
            cm.sendOk("  好了已经多增加 #r5#k 个好友栏了，如果你还需要可以再来找我，当然他并不是免费的。");
        }
        cm.对话结束();
    }
}