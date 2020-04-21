

function start() {
    status = -1;
    action(1, 0, 0)
}

function action(mode, type, selection) {
    if (status <= 0 && mode <= 0) {
        cm.dispose();
        return
    }
    if (mode == 1) {
        status++
    } else {
        status--
    }	 
	var 任务 = 1001400;
	cm.completeQuest(任务, "end1");
	cm.sendOk("要寻找的三名部下是：#b查里中士、伊吉上等兵、巴伯下士#k，他们可能在神秘岛…算了，不用找了，就这样啊。我已经联系到他们了。 ");
	cm.dispose();
}