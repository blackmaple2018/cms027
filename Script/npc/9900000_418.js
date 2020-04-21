

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
	
	
	var 现金券 = cm.getPlayer().获取现金券();
	if (status <= 0) {
       var selStr = " #i4030000#  #i4030001#  #i4030010# #r#e< 每日必做 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
		
		selStr += "#b任务简介#k；通过小z店铺，充值任意金额，即可获得奖励。\r\n#b任务奖励#k；现金券\r\n\r\n";
			
			
		
		if (cm.getPlayer().getbosslog("每日任务_花点小钱") <= 0) {
			selStr += "\t  #b充值任意金额#k 未充值 \r\n";
		}else{
			selStr += "\t  #b充值任意金额#k 已充值 \r\n";
		}
		
		
		selStr += "\r\n\r\n\t #L1##b完成任务#k#l \r\n\r\n\r\n";
		
		
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
			case 1:
				if(cm.getPlayer().getbosslog("每日任务_花点小钱完成") <= 0){
					if (cm.getPlayer().getbosslog("每日任务_花点小钱") > 0 ) {
						cm.setbosslog("每日任务_花点小钱完成");
						cm.getPlayer().修改现金券(40);
						cm.getPlayer().设置天梯积分(1);
						cm.getPlayer().dropMessage("现金券+40");
						cm.sendOk("恭喜你获得现金券奖励。");
					}else{
						cm.sendOk("完成条件未满足。");
					}
				}else{
					cm.sendOk("今日已经完成。");
				}
                cm.dispose();
                break;
			default:
                cm.dispose();
                break;
			
        }
    }
}
