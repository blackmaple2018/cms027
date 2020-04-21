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
		var 文本 = "  #i4030000#  #i4030001#  #i4030010# #r#e< 股票市场 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n";
		 	文本 +="   时间 : #r"+year+"#k 年 #r"+month+"#k 月 #r"+day+"#k 日 #r" + hour + "#k 时 #r" + minute + "#k 分 #r" + second + "#k 秒 "+星期()+"\r\n";
			文本 +="股票在每日#b5#k点之后，每隔#b60#k分钟会刷新一次，购买和售出都以当前实时价格为准，当某股跌停时，所有该支股票数据将会清空，并且重新开盘。\r\n\r\n";
			文本 +="\t绿蜗牛股票 #b"+cm.每股价值(1)+"#k 金币/股  剩余:#b"+cm.每股剩余数量(1)+"#k  持有:#b"+cm.getPlayer().获取股票(1)+"#k\r\n";
			文本 +="\t蓝蜗牛股票 #b"+cm.每股价值(2)+"#k 金币/股  剩余:#b"+cm.每股剩余数量(2)+"#k  持有:#b"+cm.getPlayer().获取股票(2)+"#k\r\n";
			文本 +="\t红蜗牛股票 #b"+cm.每股价值(3)+"#k 金币/股  剩余:#b"+cm.每股剩余数量(3)+"#k  持有:#b"+cm.getPlayer().获取股票(3)+"#k\r\n";
			文本 +="\t\t\t\t#L1##b查看股票浮动记录#l#k\r\n";
			文本 +="\t\t\t\t#L2##b购买或者售出股票#l#k\r\n";
			文本 +="\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n.";
			cm.sendSimple(文本);
    } else if (status == 1) {
        switch (selection) {
			case 1:
				cm.dispose();
				cm.openNpc(9900000,171);
                break;
			case 2:
				cm.dispose();
				cm.openNpc(9900000,172);
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
















