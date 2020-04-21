

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
		
		selStr += "#b任务简介#k；通过锻造工艺，合成矿石，母矿，水晶。\r\n#b任务奖励#k；材料 \r\n\r\n";
			
			
		
		if (cm.getPlayer().getbosslog("每日任务_合成矿石") <= 0) {
			selStr += "\t  #b合成矿石#k 未合成 \r\n";
		}else{
			selStr += "\t  #b合成矿石#k 已合成 \r\n";
		}
		
		if (cm.getPlayer().getbosslog("每日任务_合成母矿") <= 0) {
			selStr += "\t  #b合成母矿#k 未合成 \r\n";
		}else{
			selStr += "\t  #b合成母矿#k 已合成 \r\n";
		}
		
		if (cm.getPlayer().getbosslog("每日任务_合成水晶") <= 0) {
			selStr += "\t  #b合成水晶#k 未合成 \r\n";
		}else{
			selStr += "\t  #b合成水晶#k 已合成 \r\n";
		}

		
		
		selStr += "\t #L1##b完成任务#k#l \r\n";
		selStr += "\t #L2##b多倍领取#k(#r现金券x20#k)#l\r\n";
		selStr += "\t #L3##b直接完成#k(#r现金券x25#k)#l\r\n";
		
		
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
			case 3:	
				if(cm.getPlayer().getbosslog("每日任务_合成矿石完成") <= 0){
					if(现金券 >= 25){
						任务奖励(6);
						cm.setbosslog("每日任务_合成矿石完成");
						cm.getPlayer().修改现金券(-25);
						cm.getPlayer().dropMessage("现金券-25");
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
				if(cm.getPlayer().getbosslog("每日任务_合成矿石完成") <= 0){
					if (cm.getPlayer().getbosslog("每日任务_合成矿石") > 0 ) {
						if(selection == 1){
							if(cm.getPlayer().getbosslog("每日任务_合成母矿")>0){
								if(cm.getPlayer().getbosslog("每日任务_合成水晶")>0){
									任务奖励(3);
								}else{
									任务奖励(2);
								}
							}else{
								任务奖励(1);
							}
							cm.setbosslog("每日任务_合成矿石完成");
						}else if (selection == 2){
							if(现金券 >= 20){
								if(cm.getPlayer().getbosslog("每日任务_合成母矿")>0){
									if(cm.getPlayer().getbosslog("每日任务_合成水晶")>0){
										任务奖励(6);
									}else{
										任务奖励(4);
									}
								}else{
									任务奖励(3);
								}
								cm.setbosslog("每日任务_合成矿石完成");
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
	cm.getPlayer().设置天梯积分(1);
    var 结果 = "";
    var 档次 = a;
    for (var ii = 0; ii < a; ii++) {
        var 随机概率 = Math.floor(Math.random() * 任务完成奖励.length);
        var 物品代码 = 任务完成奖励[随机概率][0];
        var 物品数量 = 任务完成奖励[随机概率][1];
        结果 += "    #v" + 物品代码 + "# #b#t" + 物品代码 + "##k x " + 物品数量 + "\r\n";
        cm.getPlayer().gainItem(物品代码, 物品数量, false);
    }
    cm.sendOk("    恭喜你获得;\r\n" + 结果);
}

var 任务完成奖励 = [
    //魔法石
    [4006000, 10, 1],
    //召回石
    [4006001, 10, 1],
	//青铜母矿
	[4010001,3,1],
	//钢铁母矿
	[4010002,3,1],
	//锂矿石母矿
	[4010003,3,1],
	//紫矿石母矿
	[4010004,3,1],
	//黄金母矿
	[4010005,3,1],
	//青铜
	[4010006,3,1],
	//石榴石母矿
	[4020000,3,1],
	//紫水晶母矿
	[4020001,3,1],
	//海蓝石母矿
	[4020002,3,1],
	//祖母綠母矿
	[4020003,3,1],
	//蛋白石母矿
	[4020004,3,1],
	//蓝宝石母矿
	[4020005,3,1],
	//黄晶母矿
	[4020006,3,1],
	//钻石母矿
	[4020007,3,1],
	//黑水晶母矿
	[4020008,3,1]
];
