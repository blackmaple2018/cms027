/********************************

ZEVMS 自由冒险岛Ver.027 
@小z QQ71447500

********************************/

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
		
		selStr += "我是武术教练，能够帮你成为这个世界上伟大的战士。\r\n\r\n";
		if(cm.getJobId() >= 100 && cm.getJobId() < 200){
			if (cm.getPlayer().getbosslog("每日任务_问候职业导师1") <= 0 ) {
				selStr += " #L10##b问好#k#l\r\n";
			}
		}
		
		if(cm.getJobId() == 0){
			selStr += " #L1##r[一转]#k#b我想成为战士#k#l\r\n";
		}
		
		if(cm.getJobId() == 100 && cm.getLevel()>=30){
			selStr += " #L2##r[二转]#k#b我想变得更加强大#k#l\r\n";
		}
		
		if((cm.getJobId() == 110||cm.getJobId() == 120||cm.getJobId() == 130 )&& cm.getLevel()>=70){
			selStr += " #L3##r[三转]#k#b我想变得更加强大#k#l\r\n";
		}
		
		if((cm.getJobId() == 100 || cm.getJobId() == 110|| cm.getJobId() == 111||cm.getJobId() == 112 || cm.getJobId() == 120 ||cm.getJobId() == 121||cm.getJobId() == 122 || cm.getJobId() == 130 ||cm.getJobId() == 131||cm.getJobId() == 132 )&& cm.getLevel()>=100){
			selStr += " #L4##r[等级]#k#b突破等级上限#k#l\r\n";
		}
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
			case 10:
				if(cm.getJobId() >= 100 && cm.getJobId() < 200){
					if (cm.getPlayer().getbosslog("每日任务_问候职业导师1") <= 0 ) {
						cm.setbosslog("每日任务_问候职业导师1");
						cm.sendOk("小伙子，你也好。");
						cm.dispose();
					}
				}
				break;
            case 1:
				if(cm.getJobId() == 0){
					cm.dispose();
					cm.openNpc(1022000,1);
				}
				break;
			case 2:
				if(cm.getJobId() == 100 && cm.getLevel()>=30){
					if(cm.getPlayer().getStat().getRemainingSp()==0){
						cm.dispose();
						cm.openNpc(1022000,2);
					}else{
						cm.sendOk("你身上有多余的技能点，请使用完后再进行转职操作。");
						cm.dispose();
					}
				}
				break;
			case 3:
				if((cm.getJobId() == 110||cm.getJobId() == 120||cm.getJobId() == 130 )&& cm.getLevel()>=70){
					if(cm.getPlayer().getStat().getRemainingSp()==0){
						cm.dispose();
						cm.openNpc(1022000,3);
					}else{
						cm.sendOk("你身上有多余的技能点，请使用完后再进行转职操作。");
						cm.dispose();
					}
				}
				break;
			case 4:
				if((cm.getJobId() == 100 || cm.getJobId() == 110|| cm.getJobId() == 111||cm.getJobId() == 112 || cm.getJobId() == 120 ||cm.getJobId() == 121||cm.getJobId() == 122 || cm.getJobId() == 130 ||cm.getJobId() == 131||cm.getJobId() == 132 )&& cm.getLevel()>=100){
					cm.dispose();
					cm.openNpc(1052001,4);
				}
				break;
			default:
				break;
        }
    }

}

