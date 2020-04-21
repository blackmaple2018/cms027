/*
 ZEVMS冒险岛(079)游戏服务端
 71447500
 */


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
	
	if (cm.getInventory(2).isFull(3)) {
        cm.sendOk("请保证 #b消耗栏#k 至少有4个位置。");
        cm.dispose();
        return;
    }
	
    if (cm.getInventory(4).isFull(3)) {
        cm.sendOk("请保证 #b其他栏#k 至少有4个位置。");
        cm.dispose();
        return;
    }
	
    //var 在线 = cm.今日在线();
	var 在线 = cm.getPlayer().todayOnlineTime;
	var 等级 = cm.getLevel();
	var 经验奖励 = 等级*1520;
	var 金币奖励 = 等级*1000;
	var 点券奖励 = 500;
	var 现金券 = cm.getPlayer().获取现金券();
    if (status == 0) {
	var selStr = " #i4030000#  #i4030001#  #i4030010# #r#e< 在线奖励 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";

		selStr += "         今日在线时间 : #k#b" + 在线 + " #k分钟\r\n\r\n";
		
		selStr += "\t\t1.)在线 #b60#k 分钟可领取 #b"+经验奖励+"#k 经验\r\n";
		if(cm.getbosslog("在线奖励1")==0){
			selStr += "\t\t#L1##b一般领取#k#l\r\n";
			selStr += "\t\t#L11##b多倍领取#k(#r现金券x20#k)#l\r\n";
			selStr += "\t\t#L111##b直接领取#k(#r现金券x30#k)#l\r\n\r\n\r\n";
		}
		
		selStr += "\t\t2.)在线 #b120#k 分钟可领取 #b"+金币奖励+"#k 金币\r\n";
		if(cm.getbosslog("在线奖励2")==0){
			selStr += "\t\t#L2##b一般领取#k#l\r\n";
			selStr += "\t\t#L12##b多倍领取#k(#r现金券x15#k)#l\r\n";
			selStr += "\t\t#L122##b直接领取#k(#r现金券x30#k)#l\r\n\r\n\r\n";
		}
		
		selStr += "\t\t3.)在线 #b180#k 分钟可领取 #b淬炼石#k x 20\r\n";
		if(cm.getbosslog("在线奖励3")==0){
			selStr += "\t\t#L3##b一般领取#k#l\r\n";
			selStr += "\t\t#L13##b多倍领取#k(#r现金券x20#k)#l\r\n";
			selStr += "\t\t#L133##b直接领取#k(#r现金券x30#k)#l\r\n\r\n\r\n";
		}
		
		selStr += "\t\t4.)在线 #b240#k 分钟可领取 #b魔刹石#k x 20\r\n";
		if(cm.getbosslog("在线奖励4")==0){
			selStr += "\t\t#L4##b一般领取#k#l\r\n";
			selStr += "\t\t#L14##b多倍领取#k(#r现金券x20#k)#l\r\n";
			selStr += "\t\t#L144##b直接领取#k(#r现金券x30#k)#l\r\n\r\n\r\n";
		}
		
		selStr += "\t\t5.)在线 #b300#k 分钟可领取 #b魔法石#k x 20\r\n";
		if(cm.getbosslog("在线奖励5")==0){
			selStr += "\t\t#L5##b一般领取#k#l\r\n";
			selStr += "\t\t#L15##b多倍领取#k(#r现金券x20#k)#l\r\n";
			selStr += "\t\t#L155##b直接领取#k(#r现金券x30#k)#l\r\n\r\n\r\n";
		}
		
		selStr += "\t\t6.)在线 #b360#k 分钟可领取 #b#t4001019##k x 2\r\n";
		if(cm.getbosslog("在线奖励6")==0){
			selStr += "\t\t#L6##b一般领取#k#l\r\n";
			selStr += "\t\t#L16##b多倍领取#k(#r现金券x20#k)#l\r\n";
			selStr += "\t\t#L166##b直接领取#k(#r现金券x40#k)#l\r\n\r\n\r\n";
		}

		
		
		
		
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
			case 6:
			case 16:
			case 166:
				if(selection==166){
					if(cm.getbosslog("在线奖励6")==0){
						var 花费 = 40;
						if(现金券 >= 花费){
							cm.getPlayer().设置天梯积分(3);
							cm.getPlayer().修改现金券(-花费);
							cm.getPlayer().dropMessage("现金券-"+花费+"");
							cm.setbosslog("在线奖励6");
							cm.getPlayer().gainItem(4001019, 4, false);
							cm.sendOk("恭喜你领取了 #b#t4001019##k x 4 活跃 + 3");
						}else{
							cm.sendOk("你没有足够的现金券。");
						}
					}else{
						cm.sendOk("你已经领取了奖励，今日无法再次领取。");
					}
				}else if(在线>=360){
					if(cm.getbosslog("在线奖励6")==0){
						if(selection==6){
							cm.getPlayer().设置天梯积分(3);
							cm.setbosslog("在线奖励6");
							cm.getPlayer().gainItem(4001019, 2, false);
							cm.sendOk("恭喜你领取了 #b#t4001019##k x 2 活跃 + 3");
						}else if(selection == 16){
							if(现金券 >= 20){
								cm.getPlayer().设置天梯积分(3);
								cm.getPlayer().修改现金券(-20);
								cm.getPlayer().dropMessage("现金券-20");
								cm.setbosslog("在线奖励6");
								cm.getPlayer().gainItem(4001019, 4, false);
								cm.sendOk("恭喜你领取了 #b#t4001019##k x 4 活跃 + 3");
							}else{
								cm.sendOk("你没有足够的现金券。");
							}
						}
					}else{
						cm.sendOk("你已经领取了奖励，今日无法再次领取。");
					}
				}else{
					cm.sendOk("你在线时间未达到要求。");
				}
				cm.dispose();
                break;
				
			case 1:
			case 11:
			case 111:
				if(selection==111){
					if(cm.getbosslog("在线奖励1")==0){
						if(现金券 >= 30){
							cm.getPlayer().修改现金券(-30);
							cm.getPlayer().设置天梯积分(1);
							cm.getPlayer().dropMessage("现金券-30");
							cm.setbosslog("在线奖励1");
							cm.gainExp(经验奖励*2);
							cm.sendOk("恭喜你领取了 #b"+经验奖励*2+"#k 点经验奖励。活跃 + 1");
						}else{
							cm.sendOk("你没有足够的现金券。");
						}
					}else{
						cm.sendOk("你已经领取了奖励，今日无法再次领取。");
					}
				}else if(在线>=60){
					if(cm.getJobId() == 0){
						cm.sendOk("你尚未转职，无法领取。");
						cm.dispose();
						return;
					}
					if(cm.getbosslog("在线奖励1")==0){
						if(selection==1){
							cm.setbosslog("在线奖励1");
							cm.getPlayer().设置天梯积分(1);
							cm.gainExp(经验奖励);
							cm.sendOk("恭喜你领取了 #b"+经验奖励+"#k 点经验奖励。活跃 + 1");
						}else if(selection == 11){
							if(现金券 >= 20){
								cm.getPlayer().修改现金券(-20);
								cm.getPlayer().dropMessage("现金券-20");
								cm.setbosslog("在线奖励1");
								cm.gainExp(经验奖励*2);
								cm.getPlayer().设置天梯积分(1);
								cm.sendOk("恭喜你领取了 #b"+经验奖励*2+"#k 点经验奖励。活跃 + 1");
							}else{
								cm.sendOk("你没有足够的现金券。");
							}
						}
					}else{
						cm.sendOk("你已经领取了奖励，今日无法再次领取。");
					}
				}else{
					cm.sendOk("你在线时间未达到要求。");
				}
				cm.dispose();
                break;
				
			case 2:
			case 12:
			case 122:
				if(selection==122){
					if(cm.getbosslog("在线奖励2")==0){
						var 花费 = 30;
						if(现金券 >= 花费){
							cm.getPlayer().修改现金券(-花费);
							cm.getPlayer().设置天梯积分(1);
							cm.getPlayer().dropMessage("现金券-"+花费+"");
							cm.setbosslog("在线奖励2");
							cm.gainMeso(金币奖励*2);
							cm.sendOk("恭喜你领取了 #b"+金币奖励*2+"#k 金币奖励。活跃 + 1");
						}else{
							cm.sendOk("你没有足够的现金券。");
						}
					}else{
						cm.sendOk("你已经领取了奖励，今日无法再次领取。");
					}
				}else if(在线>=120){
					if(cm.getbosslog("在线奖励2")==0){
						if(selection==2){
							cm.setbosslog("在线奖励2");
							cm.gainMeso(金币奖励);
							cm.getPlayer().设置天梯积分(1);
							cm.sendOk("恭喜你领取了 #b"+金币奖励+"#k 金币奖励。活跃 + 1");
						}else if(selection == 12){
							var 花费 = 15;
							if(现金券 >= 花费){
								cm.getPlayer().修改现金券(-花费);
								cm.getPlayer().dropMessage("现金券-"+花费+"");
								cm.setbosslog("在线奖励2");
								cm.gainMeso(金币奖励*2);
								cm.getPlayer().设置天梯积分(1);
								cm.sendOk("恭喜你领取了 #b"+金币奖励*2+"#k 金币奖励。活跃 + 1");
							}else{
								cm.sendOk("你没有足够的现金券。");
							}
						}
					}else{
						cm.sendOk("你已经领取了奖励，今日无法再次领取。");
					}
				}else{
					cm.sendOk("你在线时间未达到要求。");
				}
				cm.dispose();
                break;
				
			case 3:
			case 13:
			case 133:
				if(selection==133){
					if(cm.getbosslog("在线奖励3")==0){
						var 花费 = 30;
						if(现金券 >= 花费){
							cm.getPlayer().修改现金券(-花费);
							cm.getPlayer().dropMessage("现金券-"+花费+"");
							cm.setbosslog("在线奖励3");
							cm.getPlayer().设置天梯积分(1);
							cm.getPlayer().gainItem(4006002, 40, false);
							cm.sendOk("恭喜你领取了 #b淬炼石#k x 40 活跃 + 1");
						}else{
							cm.sendOk("你没有足够的现金券。");
						}
					}else{
						cm.sendOk("你已经领取了奖励，今日无法再次领取。");
					}
				}else if(在线>=180){
					if(cm.getbosslog("在线奖励3")==0){
						if(selection==3){
							cm.setbosslog("在线奖励3");
							cm.getPlayer().gainItem(4006002, 20, false);
							cm.getPlayer().设置天梯积分(1);
							cm.sendOk("恭喜你领取了 #b淬炼石#k x 20 活跃 + 1");
						}else if(selection == 13){
							if(现金券 >= 20){
								cm.getPlayer().修改现金券(-20);
								cm.getPlayer().dropMessage("现金券-20");
								cm.setbosslog("在线奖励3");
								cm.getPlayer().设置天梯积分(1);
								cm.getPlayer().gainItem(4006002, 40, false);
								cm.sendOk("恭喜你领取了 #b淬炼石#k x 40 活跃 + 1");
							}else{
								cm.sendOk("你没有足够的现金券。");
							}
						}
					}else{
						cm.sendOk("你已经领取了奖励，今日无法再次领取。");
					}
				}else{
					cm.sendOk("你在线时间未达到要求。");
				}
				cm.dispose();
                break;
				
			case 4:
			case 14:
			case 144:
				if(selection==144){
					if(cm.getbosslog("在线奖励4")==0){
						var 花费 = 30;
						if(现金券 >= 花费){
							cm.getPlayer().修改现金券(-花费);
							cm.getPlayer().dropMessage("现金券-"+花费+"");
							cm.setbosslog("在线奖励4");
							cm.getPlayer().gainItem(4006003, 40, false);
							cm.getPlayer().设置天梯积分(2);
							cm.sendOk("恭喜你领取了 #b魔刹石#k x 40 活跃 + 2");
						}else{
							cm.sendOk("你没有足够的现金券。");
						}
					}else{
						cm.sendOk("你已经领取了奖励，今日无法再次领取。");
					}
				}else if(在线>=240){
					if(cm.getbosslog("在线奖励4")==0){
						if(selection==4){
							cm.setbosslog("在线奖励4");
							cm.getPlayer().gainItem(4006003, 20, false);
							cm.getPlayer().设置天梯积分(2);
							cm.sendOk("恭喜你领取了 #b魔刹石#k x 20 活跃 + 2");
						}else if(selection == 14){
							if(现金券 >= 20){
								cm.getPlayer().修改现金券(-20);
								cm.getPlayer().dropMessage("现金券-20");
								cm.setbosslog("在线奖励4");
								cm.getPlayer().设置天梯积分(2);
								cm.getPlayer().gainItem(4006003, 40, false);
								cm.sendOk("恭喜你领取了 #b魔刹石#k x 40 活跃 + 2");
							}else{
								cm.sendOk("你没有足够的现金券。");
							}
						}
					}else{
						cm.sendOk("你已经领取了奖励，今日无法再次领取。");
					}
				}else{
					cm.sendOk("你在线时间未达到要求。");
				}
				cm.dispose();
                break;
				
			case 5:
			case 15:
			case 155:
				if(selection==155){
					if(cm.getbosslog("在线奖励5")==0){
						var 花费 = 30;
						if(现金券 >= 花费){
							cm.getPlayer().修改现金券(-花费);
							cm.getPlayer().dropMessage("现金券-"+花费+"");
							cm.setbosslog("在线奖励5");
							cm.getPlayer().gainItem(4006000, 40, false);
							cm.getPlayer().设置天梯积分(2);
							cm.sendOk("恭喜你领取了 #b魔法石#k x 40 活跃 + 2");
						}else{
							cm.sendOk("你没有足够的现金券。");
						}
					}else{
						cm.sendOk("你已经领取了奖励，今日无法再次领取。");
					}
				}else if(在线>=300){
					if(cm.getbosslog("在线奖励5")==0){
						if(selection==5){
							cm.setbosslog("在线奖励5");
							cm.getPlayer().gainItem(4006000, 20, false);
							cm.getPlayer().设置天梯积分(2);
							cm.sendOk("恭喜你领取了 #b魔法石#k x 20 活跃 + 2");
						}else if(selection == 15){
							if(现金券 >= 20){
								cm.getPlayer().修改现金券(-20);
								cm.getPlayer().dropMessage("现金券-20");
								cm.setbosslog("在线奖励5");
								cm.getPlayer().gainItem(4006000, 40, false);
								cm.getPlayer().设置天梯积分(2);
								cm.sendOk("恭喜你领取了 #b魔法石#k x 40 活跃 + 2");
							}else{
								cm.sendOk("你没有足够的现金券。");
							}
						}
					}else{
						cm.sendOk("你已经领取了奖励，今日无法再次领取。");
					}
				}else{
					cm.sendOk("你在线时间未达到要求。");
				}
				cm.dispose();
                break;
			default:
                cm.dispose();
                cm.openNpc(9900000, 0);
                break;
        }
    }
}

