

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
		
		selStr += "#b任务简介#k；在市场泡点休息，达到一定时间即可获得奖励。\r\n#b任务奖励#k；金币，经验基础泡点收益 \r\n\r\n";
			
			
		
		if (cm.行程(6) < 12) {
			selStr += "\t  #b泡点60分钟#k 未完成 \r\n";
		}else{
			selStr += "\t  #b泡点60分钟#k 已完成 \r\n";
		}
		
		if (cm.行程(6) < 24) {
			selStr += "\t  #b泡点120分钟#k 未完成 \r\n";
		}else{
			selStr += "\t  #b泡点120分钟#k 已完成 \r\n";
		}


		
		
		selStr += "\r\n\r\n\t #L1##b完成任务#k#l   #L2##b多倍领取#k(#r现金券x50#k)#l\r\n\r\n\r\n";
		
		
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
			case 1:
			case 2:
				if(cm.getPlayer().getbosslog("每日任务_修养生息完成") <= 0){
					if (cm.行程(6) >= 60 ) {
						if(selection == 1){
							if (cm.行程(6) >= 120 ) {
								var 金币 = 10;
								var 经验 = 15;
								cm.getPlayer().设置天梯积分(1);
								cm.getPlayer().set泡点经验((cm.getPlayer().get泡点经验()+(经验)));
								cm.getPlayer().set泡点金币((cm.getPlayer().get泡点金币()+(金币)));
								cm.sendOk("恭喜你完成任务，泡点金币收益 + "+金币+" ，泡点经验收益 + "+经验+" 。");
							}else{
								var 金币 = 5;
								var 经验 = 10;
								cm.getPlayer().设置天梯积分(1);
								cm.getPlayer().set泡点经验((cm.getPlayer().get泡点经验()+(经验)));
								cm.getPlayer().set泡点金币((cm.getPlayer().get泡点金币()+(金币)));
								cm.sendOk("恭喜你完成任务，泡点金币收益 + "+金币+" ，泡点经验收益 + "+经验+" 。");
							}
							cm.setbosslog("每日任务_修养生息完成");
						}else if (selection == 2){
							if(现金券 >= 50){
								if (cm.行程(6) >= 120 ) {
									var 金币 = 20;
									var 经验 = 30;
									cm.getPlayer().设置天梯积分(1);
									cm.getPlayer().set泡点经验((cm.getPlayer().get泡点经验()+(经验)));
									cm.getPlayer().set泡点金币((cm.getPlayer().get泡点金币()+(金币)));
									cm.sendOk("恭喜你完成任务，泡点金币收益 + "+金币+" ，泡点经验收益 + "+经验+" 。");
								}else{
									var 金币 = 10;
									var 经验 = 15;
									cm.getPlayer().设置天梯积分(1);
									cm.getPlayer().set泡点经验((cm.getPlayer().get泡点经验()+(经验)));
									cm.getPlayer().set泡点金币((cm.getPlayer().get泡点金币()+(金币)));
									cm.sendOk("恭喜你完成任务，泡点金币收益 + "+金币+" ，泡点经验收益 + "+经验+" 。");
								}
								cm.setbosslog("每日任务_修养生息完成");
								cm.getPlayer().修改现金券(-50);
								cm.getPlayer().dropMessage("现金券-50");
							}else{
								cm.sendOk("你没有足够的现金券。");
							}
						}
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