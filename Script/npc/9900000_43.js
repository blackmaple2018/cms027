
function start() {
	status = -1;
    action(1, 0, 0);
}



function action(mode, type, selection) {
     if (status == 0 && mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }

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
 


	var 今日在线 = cm.今日在线();
	var 角色等级 = cm.getLevel();
	var 奖励编号 = cm.getPlayer().获取签到奖励();
	var 现金券 = cm.getPlayer().获取现金券();
	var 物品代码 = 任务完成奖励[奖励编号][0];
	var 物品数量 = 任务完成奖励[奖励编号][1]*2;
    if (status == 0) {
        var 文本 = " #i4030000#  #i4030001#  #i4030010# #r#e< 每日签到 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
        文本 += "\t#b今日在线#k : " + 今日在线 + " \r\n";
        文本 += "\t每天在线 120 分钟，等级 30 级后可以签到领取奖励哦。每次签到后，使用现金券签到会获得一个特殊奖励和两个随机奖励。一般只会获得两个随机奖励。\r\n\r\n";
        文本 += "\t\t#L1##b今日签到#k#l\r\n";
		文本 += "\t\t#L2##b今日签到#k(#r现金券x30#k)#l\r\n";
		文本 += "\r\n\r\n\t\t  #r特殊奖励#k:#v"+物品代码+"# #t"+物品代码+"# x "+物品数量+"";
        文本 += "\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n.";
        cm.sendSimple(文本);
    } else if (status == 1) {
        switch (selection) {
			case 1:
				if(cm.getbosslog("每日签到")<=0){
					if(今日在线>120){
						if(角色等级>=30){
							cm.setbosslog("每日签到");
							cm.getPlayer().签到();
							奖励(2,0);
						}else{
							cm.sendOk("等级不满足。");
						}
					}else{
						cm.sendOk("在线时常不满足。");
					}
				}else{
					cm.sendOk("请明天再来领取。");
				}
				cm.dispose();
                break;
			case 2:
				if(cm.getbosslog("每日签到")<=0){
					if(今日在线>120){
						if(角色等级>=30){
							if(现金券 >= 30){
								cm.setbosslog("每日签到");
								cm.getPlayer().签到();
								奖励(2,1);
								cm.getPlayer().修改现金券(-30);
								cm.getPlayer().dropMessage("现金券-30");
							}else{
								cm.sendOk("你没有足够的现金券。");
							}
						}else{
							cm.sendOk("等级不满足。");
						}
					}else{
						cm.sendOk("在线时常不满足。");
					}
				}else{
					cm.sendOk("请明天再来领取。");
				}
				cm.dispose();
                break;
            default:
                cm.dispose();
                break;
        }
    }
}

function  奖励(a,b) {
	cm.getPlayer().设置天梯积分(1);
    var 结果 = "";
	if(b>0){
		var 奖励编号 = cm.getPlayer().获取签到奖励();
		var 随机 = Math.floor(Math.random() * 任务完成奖励.length);
		var 物品代码 = 任务完成奖励[奖励编号][0];
		var 物品数量 = 任务完成奖励[奖励编号][1]*2;
		结果 += "    #v" + 物品代码 + "# #b#t" + 物品代码 + "##k x " + 物品数量 + "\r\n\r\n";
		cm.getPlayer().gainItem(物品代码, 物品数量, false);
		cm.getPlayer().签到奖励(随机);
	}
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
    [4006000, 20, 1],
    //召回石
    [4006001, 20, 1],
    //淬炼石头
    [4006002, 20, 1],
    //抹煞石
    [4006003, 20, 1],
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
    [2041022, 2, 1]
];





