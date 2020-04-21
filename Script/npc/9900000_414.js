

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
		
		selStr += "#b任务简介#k；问候自己职业的导师，即可获取奖励，问候得越多，奖励越丰富。\r\n#b任务奖励#k；金币，能力点 \r\n\r\n";
			
		if (cm.getPlayer().getbosslog("每日任务_问候职业导师1") <= 0) {
			selStr += "\t  #b一转导师#k 未问候 \r\n";
		}else{
			selStr += "\t  #b一转导师#k 已问候 \r\n";
		}
		
		if (cm.getPlayer().getbosslog("每日任务_问候职业导师2") <= 0) {
			selStr += "\t  #b二转导师#k 未问候 \r\n";
		}else{
			selStr += "\t  #b二转导师#k 已问候 \r\n";
		}
		
		if (cm.getPlayer().getbosslog("每日任务_问候职业导师3") <= 0) {
			selStr += "\t  #b三转导师#k 未问候 \r\n";
		}else{
			selStr += "\t  #b三转导师#k 已问候 \r\n";
		}

		
		
		selStr += "\t #L1##b完成任务#k#l \r\n";
		selStr += "\t #L2##b多倍领取#k(#r现金券x50#k)#l\r\n";
		selStr += "\t #L3##b直接完成#k(#r现金券x70#k)#l\r\n";
		
		
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
			case 3:	
				if(cm.getPlayer().getbosslog("每日任务_问候职业导师完成") <= 0){
					if(现金券 >= 70){
						任务奖励(70);
						cm.getPlayer().getStat().gainAp(2);
						cm.getPlayer().dropMessage("AP+2");
						cm.setbosslog("每日任务_问候职业导师完成");
						cm.getPlayer().修改现金券(-70);
						cm.getPlayer().dropMessage("现金券-70");
					}else{
						cm.sendOk("你没有足够的现金券。");
					}
				}else{
					cm.sendOk("今日已经完成。");
				}
				cm.dispose();
				break;
			case 1:
			case 2:
				if(cm.getPlayer().getbosslog("每日任务_问候职业导师完成") <= 0){
					if (cm.getPlayer().getbosslog("每日任务_问候职业导师1") > 0 ) {
						if(selection == 1){
							if(cm.getPlayer().getbosslog("每日任务_问候职业导师2")>0){
								任务奖励(30);
								if(cm.getPlayer().getbosslog("每日任务_问候职业导师3")>0){
									cm.getPlayer().getStat().gainAp(1);
									cm.getPlayer().dropMessage("AP+1");
								}
							}else{
								任务奖励(10);
							}
							cm.setbosslog("每日任务_问候职业导师完成");
						}else if (selection == 2){
							if(现金券 >= 50){
								if(cm.getPlayer().getbosslog("每日任务_问候职业导师2")>0){
									任务奖励(70);
									if(cm.getPlayer().getbosslog("每日任务_问候职业导师3")>0){
										cm.getPlayer().getStat().gainAp(2);
										cm.getPlayer().dropMessage("AP+2");
									}
								}else{
									任务奖励(30);
								}
								cm.setbosslog("每日任务_问候职业导师完成");
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

function  任务奖励(a) {
	var 金币奖励 = 10000 * a;
	cm.gainMeso(金币奖励);
	cm.getPlayer().设置天梯积分(3);
    cm.sendOk("    恭喜你获得;\r\n    #v4031039# 金币 x #b"+金币奖励+"#k\r\n");
}