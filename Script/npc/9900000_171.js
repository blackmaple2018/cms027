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
		var 文本 = "  #i4030000#  #i4030001#  #i4030010# #r#e< 股票浮动 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
			文本 +="\t\t\t\t  #L1##b查看绿蜗牛记录#l#k\r\n";
			文本 +="\t\t\t\t  #L2##b查看蓝蜗牛记录#l#k\r\n";
			文本 +="\t\t\t\t  #L3##b查看红蜗牛记录#l#k\r\n";
			文本 +="\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n.";
			cm.sendSimple(文本);
    } else if (status == 1) {
        switch (selection) {
			case 1:
				cm.sendOk("绿蜗牛记录:\r\n\r\n"+cm.查看股票记录(1));
				cm.dispose();
                break;
			case 2:
				cm.sendOk("蓝蜗牛记录:\r\n\r\n"+cm.查看股票记录(2));
				cm.dispose();
                break;
			case 3:
				cm.sendOk("红蜗牛记录:\r\n\r\n"+cm.查看股票记录(3));
				cm.dispose();
                break;
			default:
                cm.dispose();
                break;
        }
    }
}