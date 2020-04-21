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
        var selStr = "  #i4030000#  #i4030001#  #i4030010# #r#e< 个人信息 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
		selStr += ""+cm.个人信息(cm.getPlayer())+"\r\n";
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
			default:
                cm.dispose();
                cm.openNpc(9900000, 0);
                break;
        }
    }
}

