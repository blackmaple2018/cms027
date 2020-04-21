
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
 

    var 每日次数 = 3;
    var 杀怪数量 = cm.getPlayer().获取杀怪数量();
    var 完成次数 = cm.getbosslog("日常狩猎");
	var 现金券 = cm.getPlayer().获取现金券();
	
    if (status == 0) {
        var 文本 = " #i4030000#  #i4030001#  #i4030010# #r#e< 日常狩猎 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
        文本 += "\t#b当前已经完成#k : " + 每日次数 + " / " + 完成次数 + "\r\n";
        文本 += "\t你可以和你的队友一起完成日常狩猎，必须要在同一个地图，有经验获取才可以。完成一次后会清空记录。\r\n";
        if (完成次数 < 每日次数) {
            if (cm.getPlayer().获取杀怪数量() > 0) {
                文本 += "\r\n";
				文本 += "\t 1.)#b击杀目标#k : 99 / " + 杀怪数量 + "\r\n";
                文本 += "\t#b#L11#查看奖励#l\t#b#L1#领取奖励#l\t#L111#额外领取#k(#r现金券x20#k)#l\r\n\r\n\r\n";

				文本 += "\t 2.)#b击杀目标#k : 299 / " + 杀怪数量 + "\r\n";
                文本 += "\t#b#L12#查看奖励#l\t#b#L2#领取奖励#l\t#L121#额外领取#k(#r现金券x20#k)#l\r\n\r\n\r\n";
				
				文本 += "\t 3.)#b击杀目标#k : 599 / " + 杀怪数量 + "\r\n";
                文本 += "\t#b#L13#查看奖励#l\t#b#L3#领取奖励#l\t#L131#额外领取#k(#r现金券x20#k)#l\r\n\r\n\r\n";
				
            } else {
                文本 += "\r\n";
                文本 += "   #b#L0#开始今日狩猎#l#k\r\n";
            }
        } else {
            文本 += "\t#r你今日已经完成了。#k\r\n";
        }
        文本 += "\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n.";
        cm.sendSimple(文本);
    } else if (status == 1) {
        switch (selection) {

            //第一档99
            case 1:
            case 11:
			case 111:
                var 击杀数量 = 99;
                if (selection == 1) {
                    if (杀怪数量 >= 击杀数量) {
                        任务奖励(1);
                        cm.setbosslog("日常狩猎");
                        cm.getPlayer().杀怪任务完成();
                    } else {
                        cm.sendOk("完成任务需要击杀 #b" + 击杀数量 + "#k 只怪物。");
                    }
                } else if (selection == 11) {
                    任务奖励查看(1);
                } else if (selection == 111){
					if (杀怪数量 >= 击杀数量) {
						if(现金券 >= 20){
							任务奖励(2);
							cm.setbosslog("日常狩猎");
							cm.getPlayer().杀怪任务完成();
							cm.getPlayer().修改现金券(-20);
							cm.getPlayer().dropMessage("现金券-20");
						}else{
							cm.sendOk("你没有足够的现金券，无法抽奖。");
						}
					} else {
                        cm.sendOk("完成任务需要击杀 #b" + 击杀数量 + "#k 只怪物。");
                    }
				}
                cm.dispose();
                break;

                //第一档299	
            case 2:
            case 12:
			case 121:
                var 击杀数量 = 299;
                if (selection == 2) {
                    if (杀怪数量 >= 击杀数量) {
                        任务奖励(2);
                        cm.setbosslog("日常狩猎");
                        cm.getPlayer().杀怪任务完成();
					} else {
                        cm.sendOk("完成任务需要击杀 #b" + 击杀数量 + "#k 只怪物。");
                    }
                } else if (selection == 12) {
                    任务奖励查看(2);
                } else if (selection == 121){
					if (杀怪数量 >= 击杀数量) {
						if(现金券 >= 20){
							任务奖励(3);
							cm.setbosslog("日常狩猎");
							cm.getPlayer().杀怪任务完成();
							cm.getPlayer().修改现金券(-20);
							cm.getPlayer().dropMessage("现金券-20");
						}else{
							cm.sendOk("你没有足够的现金券，无法抽奖。");
						}
					} else {
                        cm.sendOk("完成任务需要击杀 #b" + 击杀数量 + "#k 只怪物。");
                    }
				}
                cm.dispose();
                break;

                //第一档599
            case 3:
            case 13:
			case 131:
                var 击杀数量 = 599;
                if (selection == 3) {
                    if (杀怪数量 >= 击杀数量) {
                        任务奖励(3);
                        cm.setbosslog("日常狩猎");
                        cm.getPlayer().杀怪任务完成();
                    } else {
                        cm.sendOk("完成任务需要击杀 #b" + 击杀数量 + "#k 只怪物。");
                    }
                } else if (selection == 13) {
                    任务奖励查看(3);
                } else if (selection == 131){
					if (杀怪数量 >= 击杀数量) {
						if(现金券 >= 20){
							任务奖励(4);
							cm.setbosslog("日常狩猎");
							cm.getPlayer().杀怪任务完成();
							cm.getPlayer().修改现金券(-20);
							cm.getPlayer().dropMessage("现金券-20");
						}else{
							cm.sendOk("你没有足够的现金券，无法抽奖。");
						}
					} else {
                        cm.sendOk("完成任务需要击杀 #b" + 击杀数量 + "#k 只怪物。");
                    }
				}
                cm.dispose();
                break;

            case 0:
                cm.getPlayer().杀怪任务计数();
                cm.sendOk("任务开始，你可以和队伍一起组队完成。");
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
    for (var ii = 0; ii < a * 2; ii++) {
        var 随机概率 = Math.floor(Math.random() * 任务完成奖励.length);
        var 物品代码 = 任务完成奖励[随机概率][0];
        var 物品数量 = 任务完成奖励[随机概率][1];
        结果 += "    #v" + 物品代码 + "# #b#t" + 物品代码 + "##k x " + 物品数量 + "\r\n";
        cm.getPlayer().gainItem(物品代码, 物品数量, false);
    }
    cm.sendOk("    恭喜你获得;\r\n" + 结果);
}

function  任务奖励查看(a) {
    var 文本 = "";
    for (var ii = 0; ii < 任务完成奖励.length; ii++) {
        if (任务完成奖励[ii][2] > 0) {
            if (ii % 8 == 0) {
                文本 += "\r\n    ";
            }
            文本 += "#v" + 任务完成奖励[ii][0] + "#";
        }
    }
    cm.sendOk("    部分任务奖励展示  可随机抽取 #b" + (a * 2) + "#k 次;\r\n" + 文本);
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





