
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

    if (cm.getLevel() >= 40 && cm.getJobId() > 0) {
        var 任务2 = 1000401;
        if (cm.getPlayer().getQuest().getQuestData(任务2) == "j0") {
            if (cm.getInventory(2).isFull(1)) {
                cm.sendOk("请保证 #b消耗栏#k 至少有2个位置。");
                cm.dispose();
                return;
            }
            if (!cm.haveItem(2022000, 1) || !cm.haveItem(4000036, 30) || !cm.haveItem(4000041, 20)) {
                cm.sendOk("你好勇士，感谢你上次帮助了我证明，现在我想要做炼金实验，需要#b1瓶矿泉水和30个奇秒的药和20个巫婆的试验用青蛙#k，你可以帮助我吗？");
                cm.dispose();
            } else {
                cm.gainItem(2022000, -1);
                cm.gainItem(4000036, -30);
                cm.gainItem(4000041, -20);
                cm.gainItem(2000004, 100);
                cm.gainExp(200000);
                cm.completeQuest(任务2, "j1");
                cm.sendOk("哇，感谢你呀勇士，你帮助我收集了这些物品，我可以进行实验了。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务2) == "j1") {
            cm.sendOk("哈哈···不好意思，你听说过超级药水吗，你收集物品，我可以帮你制作哦。");
            cm.dispose();
            cm.completeQuest(任务2, "j2");
        } else if (cm.getPlayer().getQuest().getQuestData(任务2) == "j2") {
            if (cm.getInventory(2).isFull(1)) {
                cm.sendOk("请保证 #b消耗栏#k 至少有2个位置。");
                cm.dispose();
                return;
            }
            if (!cm.haveItem(4000040, 1) || !cm.haveItem(2012000, 3) || !cm.haveItem(4000036, 100) || !cm.haveItem(4000041, 30)) {
                cm.sendOk("帮我收集#b1个蘑菇王的芽孢，3个龙血，100个奇怪的药，30个巫婆的试验用青蛙#k，我就可以制作出超级药水。");
                cm.dispose();
            } else {
                cm.gainItem(4000040, -1);
                cm.gainItem(2012000, -3);
                cm.gainItem(4000036, -100);
                cm.gainItem(4000041, -30);
                cm.gainItem(2000005, 100);
                cm.gainExp(200000);
                cm.completeQuest(任务2, "j3");
                cm.sendOk("哇，感谢你呀勇士，你帮助我收集了这些物品，我可以进行实验了。");
                cm.dispose();
            }
        } else if (cm.getPlayer().getQuest().getQuestData(任务2) == "j3") {
            cm.sendOk("没有比超级药水更好的恢复品了，但是炼金术不仅仅是只能制作药水，还能制作神奇的卷轴和强化物。");
            cm.dispose();
            cm.completeQuest(任务2, "j4");
        } else if (cm.getPlayer().getQuest().getQuestData(任务2) == "j4") {
            if (cm.getInventory(2).isFull(3)) {
                cm.sendOk("请保证 #b消耗栏#k 至少有4个位置。");
                cm.dispose();
                return;
            }
            if (!cm.haveItem(4011007, 1) || !cm.haveItem(4000030, 20) || !cm.haveItem(2012002, 10)) {
                cm.sendOk("想知道卷轴怎么制作？那就是#b1个月石，20个龙皮，10个古木树液#k。你收集一下，我给制作强力的卷轴。");
                cm.dispose();
            } else {
                cm.gainItem(4011007, -1);
                cm.gainItem(4000030, -20);
                cm.gainItem(2012002, -10);
                cm.gainItem(2000004, 100);
                cm.gainItem(2000005, 100);
                cm.gainItem(2040809, 10);
                cm.gainItem(2040804, 5);
                cm.gainExp(200000);
                cm.completeQuest(任务2, "je");
                cm.sendOk("感谢你帮助我做的一切，我的炼金术将会改变世界。。");
                cm.dispose();
            }
        } else {
            cm.sendOk("我正在潜心研究炼金术，你有什么事情吗。");
            cm.dispose();
        }
    }
}