

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
		
		selStr += "#b任务简介#k；通关废弃副本，完成后即可获取奖励。如果队伍中存在萌新，通关时请保证在同一地图才会生效。\r\n#b任务奖励#k；"+(cm.升级经验()*0.05).toFixed(0)+" - "+(cm.升级经验()*0.12).toFixed(0)+" 经验值 \r\n\r\n";
			
				
		
		if (cm.getPlayer().getbosslog("每日任务_通关废弃副本") <= 0) {
			selStr += "\t  #b通关废弃副本#k 未通关 \r\n";
		}else{
			selStr += "\t  #b通关废弃副本#k 已通关 \r\n";
		}
		
		if (cm.getPlayer().getbosslog("每日任务_通关废弃副本带新人") <= 0) {
			selStr += "\t  #b带领队伍中存在20 - 35级玩家#k 未通关 \r\n";
		}else{
			selStr += "\t  #b带领队伍中存在20 - 35级玩家#k 已通关 \r\n";
		}
		
		
		selStr += "\t #L1##b完成任务#k#l \r\n";
		selStr += "\t #L2##b多倍领取#k(#r现金券x30#k)#l\r\n";
		selStr += "\t #L3##b直接完成#k(#r现金券x50#k)#l\r\n";
		
		
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
			case 3:	
				if(cm.getPlayer().getbosslog("每日任务_通关废弃副本完成") <= 0){
					if(现金券 >= 50){
						var 经验奖励 = cm.升级经验()*0.07;
						cm.gainExp(经验奖励);
						cm.getPlayer().设置天梯积分(1);
						cm.sendOk("恭喜你领取了 #b"+经验奖励.toFixed(0)+"#k 点经验奖励。");
						cm.setbosslog("每日任务_通关废弃副本完成");
						cm.getPlayer().修改现金券(-50);
						cm.getPlayer().dropMessage("现金券-50");
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
				if(cm.getPlayer().getbosslog("每日任务_通关废弃副本完成") <= 0){
					if (cm.getPlayer().getbosslog("每日任务_通关废弃副本") > 0 ) {
						if(selection == 1){
							if(cm.getPlayer().getbosslog("每日任务_通关废弃副本带新人")>0){
								var 经验奖励 = cm.升级经验()*0.07;
								cm.gainExp(经验奖励);
								cm.getPlayer().设置天梯积分(1);
								cm.sendOk("恭喜你领取了 #b"+经验奖励.toFixed(0)+"#k 点经验奖励。");
							}else{
								var 经验奖励 = cm.升级经验()*0.05;
								cm.gainExp(经验奖励);
								cm.getPlayer().设置天梯积分(1);
								cm.sendOk("恭喜你领取了 #b"+经验奖励.toFixed(0)+"#k 点经验奖励。");
							}
							cm.setbosslog("每日任务_通关废弃副本完成");
						}else if (selection == 2){
							if(现金券 >= 30){
								if(cm.getPlayer().getbosslog("每日任务_通关废弃副本带新人")>0){
									var 经验奖励 = cm.升级经验()*0.12;
									cm.gainExp(经验奖励);
									cm.getPlayer().设置天梯积分(1);
									cm.sendOk("恭喜你领取了 #b"+经验奖励.toFixed(0)+"#k 点经验奖励。");
								}else{
									var 经验奖励 = cm.升级经验()*0.07;
									cm.gainExp(经验奖励);
									cm.getPlayer().设置天梯积分(1);
									cm.sendOk("恭喜你领取了 #b"+经验奖励.toFixed(0)+"#k 点经验奖励。");
								}
								cm.setbosslog("每日任务_通关废弃副本完成");
								cm.getPlayer().修改现金券(-30);
								cm.getPlayer().dropMessage("现金券-30");
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

