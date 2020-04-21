
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
        if (!cm.haveItem(4031010, 1)) {
            cm.gainItem(4031010, 1);
            cm.sendOk("这个 #i4031010# 给你 ，去交给弓箭手转职教官，通过他的考试吧。");
            cm.dispose();
            return;
        }
    }

    if (status <= 0) {
        var selStr = "";

        selStr += "变强大有很多条道路，你如果想变强，得让我看看你有没有资格。\r\n\r\n";
        if (cm.getLevel() >= 30 && cm.getJobId() == 300) {
            if (cm.haveItem(4031012, 1)) {
                selStr += "#L1##b猎人之路#k#l\r\n";
                selStr += "#L2##b游侠之路#k#l\r\n";
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
                if (cm.getLevel() >= 30 && cm.getJobId() == 300) {
                    if (cm.haveItem(4031012, 1)) {
                        cm.gainItem(4031012, -1);
                        cm.changeJobById(310);
                        cm.sendOk("恭喜你转职成为一名猎人。");

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
                if (cm.getLevel() >= 30 && cm.getJobId() == 300) {
                    if (cm.haveItem(4031012, 1)) {
                        cm.gainItem(4031012, -1);
                        cm.changeJobById(320);
                        cm.sendOk("恭喜你转职成为一名游侠。");

                    }
                }
                cm.dispose();
                break;

            default:
                break;
        }

    }
}