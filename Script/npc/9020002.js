var status;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1)
        status++;
    else {
        cm.dispose();
        return;
    }
    var mapId = cm.getPlayer().getMapId();
    if (mapId == 103000890) {
        if (status == 0) {
            cm.sendNext("我送你回废弃都市吧。");
        } else {
            cm.warp(103000000, 31);
            cm.removeAll(4001007);
            cm.removeAll(4001008);
            cm.dispose();
        }
    } else {
        if (status == 0) {
			if (mapId == 103000800) {
				var outText = "你要离开打猎场吗？";
			}else{
				var outText = "你要放弃副本吗？退出后无法继续哦。";
			}
            if (mapId == 103000805) {
                outText = "恭喜你完成了副本。";
            }
            cm.sendYesNo(outText);
        } else if (mode == 1) {
			if (mapId == 103000805) {
				if (cm.getInventory(1).isFull(4)) {
					cm.sendOk("请保证 #b装备栏#k 至少有5个位置。");
					cm.dispose();
					return;
				}
				if (cm.getInventory(2).isFull(4)) {
					cm.sendOk("请保证 #b消耗栏#k 至少有5个位置。");
					cm.dispose();
					return;
				}
				if (cm.getInventory(4).isFull(4)) {
					cm.sendOk("请保证 #b其他栏#k 至少有5个位置。");
					cm.dispose();
					return;
				}
				cm.warp(103000000,1);
				cm.getPlayer().记录废弃副本难度(0);
				cm.getPlayer().修改个人记录("feiqifubentongguan1",1);
				cm.setbosslog("feiqifubentongguan1");
				var 今日通关次数 = cm.getbosslog("feiqifubentongguan1");
				if(今日通关次数 < 10){
					cm.getPlayer().dropMessage(1,"友情提示\r\n你今日已经通关 "+今日通关次数+ " 次废弃副本。");
				}else{
					cm.getPlayer().dropMessage(1,"友情提示\r\n你今日已经通关 "+今日通关次数+ " 次废弃副本，将不再获取奖励。");
				}
				if(今日通关次数 <= 5){
					if(cm.getPlayer().废弃副本难度()==1){
						任务奖励(4);
					}else{
						任务奖励(2);
					}
					if(cm.getPlayer().废弃副本难度()==10){
						cm.getPlayer().gainItem(4006000,7,false);
						cm.getPlayer().gainItem(4006001,7,false);
						cm.getPlayer().gainItem(4006002,7,false);
						cm.getPlayer().gainItem(4006003,7,false);
					}
					
				}else if(今日通关次数>5 && 今日通关次数 <= 10){
					if(cm.getPlayer().废弃副本难度()==1){
						任务奖励(2);
					}else{
						任务奖励(1);
					}
					if(cm.getPlayer().废弃副本难度()==10){
						cm.getPlayer().gainItem(4006000,5,false);
						cm.getPlayer().gainItem(4006001,5,false);
						cm.getPlayer().gainItem(4006002,5,false);
						cm.getPlayer().gainItem(4006003,5,false);
					}
				}
				if (cm.getPlayer().getbosslog("每日任务_通关废弃副本") <= 0 ) {
					cm.setbosslog("每日任务_通关废弃副本");
				}
				if(团队2()!=1){
					if(团队2()!=2){
						if(团队2()==3){
							if (cm.getPlayer().getbosslog("每日任务_通关废弃副本带新人") <= 0 ) {
								cm.setbosslog("每日任务_通关废弃副本带新人");
							}
						}
					}
				}
			}else{
				cm.warp(103000000,1);
				cm.getPlayer().记录废弃副本难度(0);
			}
            cm.dispose();
        }
    }
}

function 团队2() {
    var 队伍 = cm.getParty().getMembers();
    var mapId = cm.getPlayer().getMapId();
    var Channel = cm.getPlayer().getClient().getChannel();
    var valid = 0;
    var it = 队伍.iterator();
    while (it.hasNext()) {
        var cPlayer = it.next();
        if (Channel != cPlayer.getChannel()) {
            valid = 1;
        }
        if (mapId != cPlayer.getMapId()) {
            valid = 2;
        }
        if (cPlayer.getLevel() >= 20 && cPlayer.getLevel() < 35) {
            valid = 3;
        }
		if(cPlayer.getParty().getTrade()!=null ||cm.getParty().getTrade()!=null ){
			valid = 4;
		}
    }
    return valid;
}

function  任务奖励(a) {
    //var 结果 = "";
    var 档次 = a;
    for (var ii = 0; ii < a; ii++) {
        var 随机概率 = Math.floor(Math.random() * 任务完成奖励.length);
        var 物品代码 = 任务完成奖励[随机概率][0];
        var 物品数量 = 任务完成奖励[随机概率][1];
        //结果 += "    #v" + 物品代码 + "# #b#t" + 物品代码 + "##k x " + 物品数量 + "\r\n";
        cm.getPlayer().gainItem(物品代码, 物品数量, false);
    }
    //cm.sendOk("    恭喜你获得;\r\n" + 结果);
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
	[4131013,3,1],
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
