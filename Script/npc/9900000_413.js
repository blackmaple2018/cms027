

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
	
	if (cm.getInventory(4).isFull(4)) {
        cm.sendOk("请保证 #b其他栏#k 至少有5个位置。");
        cm.dispose();
        return;
    }
	
	var 现金券 = cm.getPlayer().获取现金券();
	if (status <= 0) {
       var selStr = " #i4030000#  #i4030001#  #i4030010# #r#e< 每日必做 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
		
		selStr += "#b任务简介#k；在游戏商城购买道具，如果贩卖道具给游戏商店可以获取更多奖励。\r\n#b任务奖励#k；点券 \r\n\r\n";
			
		if (cm.getPlayer().getbosslog("每日任务_游戏商城购买道具") <= 0) {
			selStr += "\t  #b游戏商城购买道具#k 未完成 \r\n";
		}else{
			selStr += "\t  #b游戏商城购买道具#k 已完成 \r\n";
		}
		
		if (cm.getPlayer().getbosslog("每日任务_卖道具到游戏商店") <= 0) {
			selStr += "\t  #b贩卖道具给游戏商店#k 未完成 \r\n";
		}else{
			selStr += "\t  #b贩卖道具给游戏商店#k 已完成 \r\n";
		}

		
		
		selStr += "\t #L1##b完成任务#k#l \r\n";
		selStr += "\t #L2##b多倍领取#k(#r现金券x20#k)#l\r\n";
		selStr += "\t #L3##b直接完成#k(#r现金券x30#k)#l\r\n";
		
		
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
			case 3:	
				if(cm.getPlayer().getbosslog("每日任务_游戏商城购买道具完成") <= 0){
					if(现金券 >= 30){
						任务奖励(400);
						cm.setbosslog("每日任务_游戏商城购买道具完成");
						cm.getPlayer().修改现金券(-30);
						cm.getPlayer().dropMessage("现金券-30");
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
				if(cm.getPlayer().getbosslog("每日任务_游戏商城购买道具完成") <= 0){
					if (cm.getPlayer().getbosslog("每日任务_游戏商城购买道具") > 0 ) {
						if(selection == 1){
							if(cm.getPlayer().getbosslog("每日任务_卖道具到游戏商店")>0){
								任务奖励(200);
							}else{
								任务奖励(100);
							}
							cm.setbosslog("每日任务_游戏商城购买道具完成");
						}else if (selection == 2){
							if(现金券 >= 20){
								if(cm.getPlayer().getbosslog("每日任务_卖道具到游戏商店")>0){
									任务奖励(400);
								}else{
									任务奖励(200);
								}
								cm.setbosslog("每日任务_游戏商城购买道具完成");
								cm.getPlayer().修改现金券(-20);
								cm.getPlayer().dropMessage("现金券-20");
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

	var 点券 = a ;
	cm.getPlayer().设置天梯积分(1);
	cm.getPlayer().getCashShop().gainCash(1, 点券);
    cm.sendOk("    恭喜你获得点券 x " + 点券);
}
