

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
		
		selStr += "#b任务简介#k；前往金银岛的五座主城，明珠港，射手村，魔法密林，勇士部落，废弃都市，即可完成任务。\r\n#b任务奖励#k；卷轴，魔法石，制作卷 \r\n\r\n";
			
		if (cm.getPlayer().getbosslog("每日任务_拜访金银岛主城104000000") <= 0) {
			selStr += "\t  #b明珠港#k 未拜访 \r\n";
		}else{
			selStr += "\t  #b明珠港#k 已拜访 \r\n";
		}
		
		if (cm.getPlayer().getbosslog("每日任务_拜访金银岛主城100000000") <= 0) {
			selStr += "\t  #b射手村#k 未拜访 \r\n";
		}else{
			selStr += "\t  #b射手村#k 已拜访 \r\n";
		}
		
		if (cm.getPlayer().getbosslog("每日任务_拜访金银岛主城101000000") <= 0) {
			selStr += "\t  #b魔法密林#k 未拜访 \r\n";
		}else{
			selStr += "\t  #b魔法密林#k 已拜访 \r\n";
		}
		
		if (cm.getPlayer().getbosslog("每日任务_拜访金银岛主城102000000") <= 0) {
			selStr += "\t  #b勇士部落#k 未拜访 \r\n";
		}else{
			selStr += "\t  #b勇士部落#k 已拜访 \r\n";
		}
		
		if (cm.getPlayer().getbosslog("每日任务_拜访金银岛主城103000000") <= 0) {
			selStr += "\t  #b废弃都市#k 未拜访 \r\n";
		}else{
			selStr += "\t  #b废弃都市#k 已拜访 \r\n";
		}
		
		
		selStr += "\t #L1##b完成任务#k#l \r\n";
		selStr += "\t #L2##b多倍领取#k(#r现金券x30#k)#l\r\n";
		selStr += "\t #L3##b直接完成#k(#r现金券x50#k)#l\r\n";
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
			case 1:
			case 2:
				if(cm.getPlayer().getbosslog("每日任务_拜访金银岛主城完成") <= 0){
					if (cm.getPlayer().getbosslog("每日任务_拜访金银岛主城100000000") > 0 &&cm.getPlayer().getbosslog("每日任务_拜访金银岛主城101000000") > 0 && cm.getPlayer().getbosslog("每日任务_拜访金银岛主城102000000") > 0 && cm.getPlayer().getbosslog("每日任务_拜访金银岛主城103000000") > 0 && cm.getPlayer().getbosslog("每日任务_拜访金银岛主城104000000") > 0) {
						if(selection == 1){
							cm.setbosslog("每日任务_拜访金银岛主城完成");
							任务奖励(1);
						}else if (selection == 2){
							if(现金券 >= 30){
								任务奖励(4);
								cm.setbosslog("每日任务_拜访金银岛主城完成");
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
			case 3:	
				if(cm.getPlayer().getbosslog("每日任务_拜访金银岛主城完成") <= 0){
					if(现金券 >= 50){
						任务奖励(4);
						cm.setbosslog("每日任务_拜访金银岛主城完成");
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
		if(a==4){
			物品数量+=3;
		}
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
    //淬炼石头
    [4006002, 10, 1],
    //抹煞石
    [4006003, 10, 1],
    //自然石
    [4006004, 10, 1],
    //祝福石
    [4006005, 10, 1],
    //头盔防御卷轴
    [2040001, 2, 1],
    //头盔体力卷轴
    [2040004, 2, 1],
    //耳环智力卷轴
    [2040301, 2, 1],
    //上衣防御卷轴
    [2040401, 2, 1],
    //全身铠甲敏捷卷轴
    [2040501, 2, 1],
    //全身铠甲防御卷轴
    [2040504, 2, 1],
    //裙防御卷轴
    [2040601, 2, 1],
    //鞋子敏捷度卷轴
    [2040701, 2, 1],
    //鞋子跳跃卷轴
    [2040704, 2, 1],
    //鞋子速度卷轴
    [2040707, 2, 1],
    //手套敏捷卷轴
    [2040801, 2, 1],
    //手套攻击卷轴
    [2040804, 2, 1],
    //盾牌防御卷轴
    [2040901, 2, 1],
    //披风魔防卷轴
    [2041001, 2, 1],
    //披风防御卷轴
    [2041004, 2, 1],
    //披风体力卷轴
    [2041007, 2, 1],
    //披风魔力卷轴
    [2041010, 2, 1],
    //披风力量卷轴
    [2041013, 2, 1],
    //披风智力卷轴
    [2041016, 2, 1],
    //披风敏捷卷轴
    [2041019, 2, 1],
    //披风运气卷轴
    [2041022, 2, 1],
	//单手剑制作
	[4131000,1,1],
	//单手斧制作
	[4131001,1,1],
	//单手钝器制作
	[4131002,1,1],
	//双手剑制作
	[4131003,1,1],
	//双手斧制作
	[4131004,1,1],
	//双手钝器制作
	[4131005,1,1],
	//枪制作
	[4131006,1,1],
	//矛制作
	[4131007,1,1],
	//短杖制作
	[4131008,1,1],
	//长杖制作
	[4131009,1,1],
	//弓制作
	[4131010,1,1],
	//弩制作
	[4131011,1,1],
	//短剑制作
	[4131012,1,1],
	//拳套制作
	[4131013,1,1]

];