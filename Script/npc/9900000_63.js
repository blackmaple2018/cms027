function start() {
    status = -1;
    action(1, 0, 0)
}

var id;

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
    if (status <= 0) {
        var selStr = " #i4030000#  #i4030001#  #i4030010# #r#e< 成员列表 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
		selStr += "  #b家族#k: " + cm.家族名字(cm.判断家族()) + "\r\n";
        selStr += "" + cm.在线家族成员列表() + "";
		selStr += "" + cm.离线家族成员列表() + "";
        cm.sendSimple(selStr);
    } else if (status == 1) {
        id = selection;
        selStr1 = "家族成员详细\r\n\r\n" + cm.家族成员详细列表(selection) + "";
        if (cm.判断家族() > 0) {
			if(cm.getPlayer().getId()!= id){
				if (cm.判断家族地位() == 1) {
					selStr1 += "#L1##b设置为精英#k#l\r\n";
					selStr1 += "#L2##b设置为队友#k#l\r\n";
				}
			
				if (cm.判断家族地位() == 1 || cm.判断家族地位() == 2) {
					if(cm.判断家族地位(id) == 3){
						selStr1 += "#L3##b踢出家族#k#l";
					}
				}
			}
        }
        cm.sendSimple(selStr1);
    } else if (status == 2) {
        switch (selection) {
            //设置为精英
            case 1:
                if (cm.判断家族() > 0) {
                    if (cm.判断家族地位() == 1) {
                        cm.设置家族地位(2,id);
                        cm.sendOk("设置为精英成功。");
                    }
                }
                cm.dispose();
                break;
			//设置为队友
            case 2:
                if (cm.判断家族() > 0) {
                    if (cm.判断家族地位() == 1) {
                        cm.设置家族地位(3,id);
                        cm.sendOk("设置为队员成功。");
                    }
                }
                cm.dispose();
                break;
			//踢出家族
            case 3:
                if (cm.判断家族地位() == 1 || cm.判断家族地位() == 2) {
                    cm.设置家族编号(0,id);
					cm.设置家族地位(0,id);
					cm.通知(id,1,"你已经被踢出家族。");
                    cm.sendOk("踢出家族成功。");
                }
                cm.dispose();
                break;

        }

    }
}