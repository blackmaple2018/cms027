var ca = java.util.Calendar.getInstance();
var year = ca.get(java.util.Calendar.YEAR);
var month = ca.get(java.util.Calendar.MONTH) + 1;
var day = ca.get(java.util.Calendar.DATE);
var hour = ca.get(java.util.Calendar.HOUR_OF_DAY);
var minute = ca.get(java.util.Calendar.MINUTE);
var second = ca.get(java.util.Calendar.SECOND);
var weekday = ca.get(java.util.Calendar.DAY_OF_WEEK);
function start() {
   status = -1;
   action(1, 0, 0);
}



function action(mode, type, selection) {
    if (status == 0 && mode == 0) {
        cm.dispose();
        return;
    } else if (mode == 0 && selection == -1) {
		cm.dispose();
        return;
	}
	
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
	
    if (status == 0) {
		var 文本 = " #i4030000#  #i4030001#  #i4030010# #r#e<  密林码头  >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
		 	文本 +="   时间 : #r"+year+"#k 年 #r"+month+"#k 月 #r"+day+"#k 日 #r" + hour + "#k 时 #r" + minute + "#k 分 #r" + second + "#k 秒 "+星期()+"\r\n\r\n";
	
			//主菜单
			文本 += "\t\t\t  #L1##b购买前往天空之城的船票#l\r\n";
			文本 += "\t\t\t  #L2##b运送货物前往到天空之城#l\r\n";

			cm.sendSimple(文本);
    } else if (status == 1) {
        switch (selection) {
			case 1:
				cm.dispose();
				cm.openNpc(1032007,1);
                break;
			case 2:
				cm.sendSimple("未开放。");
				cm.dispose();
				//cm.openNpc(1032007,2);//4031017
                break;
			default:
                cm.dispose();
                break;
        }
    }
}

function  星期() {
	switch (weekday) {
		case 1:
			return "星期日";
		case 2:
			return "星期一";
		case 3:
			return "星期二";
		case 4:
			return "星期三";
		case 5:
			return "星期四";
		case 6:
			return "星期五";
		case 7:
			return "星期六";
		default:
			return 0;
	}
}












