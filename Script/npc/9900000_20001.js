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
        var selStr = " #i4030000#  #i4030001#  #i4030010# #r#e< 天梯排行榜 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
		selStr+=""+cm.天梯积分排行()+"";
		cm.sendSimple(selStr);
    } else if (status == 1) {
		cm.dispose();
    }
}

