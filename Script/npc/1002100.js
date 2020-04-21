

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
    var 任务 = 1000400;
    var 任务2 = 1000401;
    if (status <= 0) {
        var selStr = "";
        if (cm.getPlayer().getQuest().getQuestData(任务) == "end") {
            selStr += "我好想去冒险~可是我父亲觉得我一个人太危险。\r\n";
        } else {
            selStr += "感谢你呀勇士，你帮助我收集了这些物品，我父亲肯定会认可我，他同意让我去冒险了。\r\n";
        }
		if (cm.getPlayer().getQuest().getQuestData(任务2) == "je") {
			selStr += "我还会炼金术哦，我要潜心钻研。\r\n";
		}
		
		selStr += "\r\n";
		
        if (cm.getLevel() >= 25 && cm.getJobId() != 0) {
            if (cm.getPlayer().getQuest().getQuestData(任务) != "end") {
                selStr += " #L1##b简和野猪#k#l\r\n";
            }
        }
		
		if (cm.getLevel() >= 40 && cm.getJobId() != 0) {
            if (cm.getPlayer().getQuest().getQuestData(任务2) != "je") {
                selStr += " #L2##b炼金术士，简#k#l\r\n";
            }
        }

        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
            case 1:
                cm.dispose();
                cm.openNpc(1002100, 1);
                break;
            case 2:
                cm.dispose();
                cm.openNpc(1002100, 2);
                break;
            default:
                break;
        }
    }
}