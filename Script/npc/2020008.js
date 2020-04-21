
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


	if (status <= 0) {
        var selStr = "";
		
		selStr += "我是泰勒斯，一名隐匿的战士，你找我，是为了变得更强吗？\r\n\r\n";
		
		if((cm.getJobId() == 110||cm.getJobId() == 120||cm.getJobId() == 130)&& cm.getLevel()>=70){
			selStr += " #L1##b我想变得更加强大#k#l\r\n";
		}
		
		if(cm.getJobId() >= 100 && cm.getJobId() < 200){
			if (cm.getPlayer().getbosslog("每日任务_问候职业导师3") <= 0 ) {
				selStr += " #L10##b问好#k#l\r\n";
			}
		}
		

        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
			case 10:
				if(cm.getJobId() >= 100 && cm.getJobId() < 200){
					if (cm.getPlayer().getbosslog("每日任务_问候职业导师3") <= 0 ) {
						cm.setbosslog("每日任务_问候职业导师3");
						cm.sendOk("小伙子，你也好。");
						cm.dispose();
					}
				}
				break;
            case 1:
				if((cm.getJobId() == 110||cm.getJobId() == 120||cm.getJobId() == 130)&& cm.getLevel()>=70){
					cm.dispose();
					cm.openNpc(2020008,1);
				}
				break;

			default:
				break;
        }
    }

}

