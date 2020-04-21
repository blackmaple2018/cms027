var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status == 1 && mode == 0) {
            cm.sendNext("即使你的水平很高，也很难真正进入，但如果你改变主意，请找我。毕竟，我的工作是保护这个地方。");
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            cm.sendNext("嘿，你看起来想越走越远。不过，在那里，你会发现自己被咄咄逼人、危险的怪物包围，所以即使你觉得自己已经准备好出发了，也请小心。很久以前，我们镇上有几个勇士想消灭任何威胁镇上的人，但是他们再也没有出来。。。");
        } else if (status == 1) {
            if (cm.getLevel() >= 50) {
                cm.sendYesNo("如果你想进去，我建议你改变主意。但如果你真的想进去。。。我只让那些足够强壮的人留在里面。我不希望看到其他人死去。让我们看看。。。。。。！你看起来很强壮。好吧，你想进去吗？");
            } else {
                cm.sendOk("如果你想进去，我建议你改变主意。但如果你真的想进去。。。我只让那些足够强壮的人留在里面。我不希望看到其他人死去。让我们看看。。。。。。你还没有达到50级。那我不能让你进去，算了吧。");
                cm.dispose();
            }
        } else if (status == 2) {
            if (cm.getLevel() >= 50) {
                cm.warp(211040300, 5);
            }
            cm.dispose();
        }
    }
}