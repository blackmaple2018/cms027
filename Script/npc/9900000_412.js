

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
		
		selStr += "#b任务简介#k；添加其他玩家角色即可完成任务，如果被其他玩家增加人气后，可以获得更多奖励。\r\n#b任务奖励#k；魔法石，人气 \r\n\r\n";
			
		if (cm.getPlayer().getbosslog("每日任务_添加其他角色人气") <= 0) {
			selStr += "\t  #b为其他角色加人气#k 未完成 \r\n";
		}else{
			selStr += "\t  #b为其他角色加人气#k 已完成 \r\n";
		}
		
		if (cm.getPlayer().getbosslog("每日任务_被其他角色添加人气") <= 0) {
			selStr += "\t  #b被其他角色添加人气#k 没有 \r\n";
		}else{
			selStr += "\t  #b被其他角色添加人气#k 完成 \r\n";
		}

		
		
		selStr += "\t #L1##b完成任务#k#l \r\n";
		selStr += "\t #L2##b多倍领取#k(#r现金券x30#k)#l\r\n";
		selStr += "\t #L3##b直接完成#k(#r现金券x50#k)#l\r\n";
		
		
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
			case 3:	
				if(cm.getPlayer().getbosslog("每日任务_添加其他角色人气完成") <= 0){
					if(现金券 >= 50){
						任务奖励(5);
						cm.setbosslog("每日任务_添加其他角色人气完成");
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
				if(cm.getPlayer().getbosslog("每日任务_添加其他角色人气完成") <= 0){
					if (cm.getPlayer().getbosslog("每日任务_添加其他角色人气") > 0 ) {
						if(selection == 1){
							if(cm.getPlayer().getbosslog("每日任务_被其他角色添加人气")>0){
								任务奖励(2);
							}else{
								任务奖励(1);
							}
							cm.setbosslog("每日任务_添加其他角色人气完成");
						}else if (selection == 2){
							if(现金券 >= 30){
								if(cm.getPlayer().getbosslog("每日任务_被其他角色添加人气")>0){
									任务奖励(5);
								}else{
									任务奖励(2);
								}
								cm.setbosslog("每日任务_添加其他角色人气完成");
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

function  任务奖励(a) {
	cm.getPlayer().设置天梯积分(1);
    var 结果 = "";
    var 档次 = a;
    for (var ii = 0; ii < a; ii++) {
        var 随机概率 = Math.floor(Math.random() * 任务完成奖励.length);
        var 物品代码 = 任务完成奖励[随机概率][0];
        var 物品数量 = 任务完成奖励[随机概率][1];
		if(a==5){
			物品数量*=2;
		}
        结果 += "    #v" + 物品代码 + "# #b#t" + 物品代码 + "##k x " + 物品数量 + "\r\n";
        cm.getPlayer().gainItem(物品代码, 物品数量, false);
    }
	var 人气 = 1 + a;
	cm.getPlayer().gainFame(人气);
	cm.getPlayer().dropMessage("人气+"+人气+"");
    cm.sendOk("    恭喜你获得;\r\n    人气 x #b"+人气+"#k\r\n" + 结果);
}


var 任务完成奖励 = [
    //魔法石
    [4006000, 10, 1],
    //召回石
    [4006001, 10, 1],
    //淬炼石头
    [4006002, 10, 1],
    //抹煞石
    [4006003, 10, 1],
    //自然石
    [4006004, 10, 1],
    //祝福石
    [4006005, 10, 1]

];