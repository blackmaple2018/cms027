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
	var 信件 = 4031008;

	if (status <= 0) {
        var selStr = "";
		
		selStr += "你是来考试的吗？你必须给我武术教练的信件，我才会让你进去考试。\r\n\r\n";
		
		if (cm.haveItem(信件, 1)) {
			selStr += " #L1##b前往二转战士考验#k#l\r\n";
		}
		
		if(cm.getJobId() >= 100 && cm.getJobId() < 200){
			if (cm.getPlayer().getbosslog("每日任务_问候职业导师2") <= 0 ) {
				selStr += " #L10##b问好#k#l\r\n";
			}
		}

        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
			case 10:
				if(cm.getJobId() >= 100 && cm.getJobId() < 200){
					if (cm.getPlayer().getbosslog("每日任务_问候职业导师2") <= 0 ) {
						cm.setbosslog("每日任务_问候职业导师2");
						cm.sendOk("小伙子，你也好。");
						cm.dispose();
					}
				}
				break;
            case 1:
				if (cm.haveItem(信件, 1)) {
					cm.gainItem(信件, -1);
					cm.warp(108000300, 0);
				}
                cm.dispose();
                break;
			default:
				break;
        }
    }
}