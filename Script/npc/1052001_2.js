
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
    if (!cm.haveItem(4031012, 1)) {
        if (!cm.haveItem(4031011, 1)) {
            cm.gainItem(4031011, 1);
            cm.sendOk("这个 #i4031011# 给你 ，去交给飞侠转职教官，通过他的考试吧。");
            cm.dispose();
            return;
        }
    }

    if (status <= 0) {
        var selStr = "";

        selStr += "变强大有很多条道路，你如果想变强，得让我看看你有没有资格。\r\n\r\n";
        if (cm.getLevel() >= 30 && cm.getJobId() == 400) {
            if (cm.haveItem(4031012, 1)) {
                selStr += "#L1##b刺客之路#k#l\r\n";
                selStr += "#L2##b侠客之路#k#l\r\n";
            }
        }
		cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
            case 1:
				/*if(cm.getPlayer().getStat().getRemainingSp()>0){
					cm.sendOk("你当前还有未使用完的SP点。");
					cm.dispose();
					return;
				}*/
                if (cm.getLevel() >= 30 && cm.getJobId() == 400) {
                    if (cm.haveItem(4031012, 1)) {
                        cm.gainItem(4031012, -1);
                        cm.changeJobById(410);
                        cm.sendOk("恭喜你转职成为一名刺客。");

                    }
                }
                cm.dispose();
                break;
            case 2:
				/*if(cm.getPlayer().getStat().getRemainingSp()>0){
					cm.sendOk("你当前还有未使用完的SP点。");
					cm.dispose();
					return;
				}*/
                if (cm.getLevel() >= 30 && cm.getJobId() == 400) {
                    if (cm.haveItem(4031012, 1)) {
                        cm.gainItem(4031012, -1);
                        cm.changeJobById(420);
                        cm.sendOk("恭喜你转职成为一名侠客。");

                    }
                }
                cm.dispose();
                break;

            default:
                break;
        }

    }
}