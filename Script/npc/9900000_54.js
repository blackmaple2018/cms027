var ca = java.util.Calendar.getInstance();
var year = ca.get(java.util.Calendar.YEAR); //获得年份
var month = ca.get(java.util.Calendar.MONTH) + 1; //获得月份
var day = ca.get(java.util.Calendar.DATE);//获取日
var hour = ca.get(java.util.Calendar.HOUR_OF_DAY); //获得小时
var minute = ca.get(java.util.Calendar.MINUTE);//获得分钟
var second = ca.get(java.util.Calendar.SECOND); //获得秒
var weekday = ca.get(java.util.Calendar.DAY_OF_WEEK);

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status == 0 && mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
        var selStr = "    #r#e < 航线状态 > #k#n \r\n";
		selStr +=" 时间 : #r"+year+"#k 年 #r"+month+"#k 月 #r"+day+"#k 日 #r" + hour + "#k 时 #r" + minute + "#k 分 #r" + second + "#k 秒 "+星期()+"\r\n\r\n";
        if (cm.getEventNotScriptOpen("Barcos")) {
            selStr += "#d\t\t [魔法密林] → [天空之城]: #g船来了#k\r\n";
        } else {
            selStr += "#d\t\t [魔法密林] → [天空之城]: #r船未来#k\r\n";
        }
       /* if (boat.getProperty("entry").equals("true")) {
            selStr += "#d\t\t [天空之城] → [魔法密林]: #g船来了#k\r\n\r\n";
        } else if (boat.getProperty("entry").equals("false") && boat.getProperty("docked").equals("true")) {
            selStr += "#d\t\t [天空之城] → [魔法密林]: #g船来了,检票中#k\r\n\r\n";
        } else {
            selStr += "#d\t\t [天空之城] → [魔法密林]: #r船未来#k\r\n\r\n";
        }*/



        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
            default:
                cm.dispose();
                cm.openNpc(990000, 0);
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