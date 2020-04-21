var ca = java.util.Calendar.getInstance();
var year = ca.get(java.util.Calendar.YEAR);
var month = ca.get(java.util.Calendar.MONTH) + 1;
var day = ca.get(java.util.Calendar.DATE);
var hour = ca.get(java.util.Calendar.HOUR_OF_DAY);
var minute = ca.get(java.util.Calendar.MINUTE);
var second = ca.get(java.util.Calendar.SECOND);
var weekday = ca.get(java.util.Calendar.DAY_OF_WEEK);
var s;
function start() {
   status = -1;
   action(1, 0, 0);
}


/*

1@ 按钮名称,
2@ 1 = 显示,0 = 不现实,
3@ 1 = 显示,0 = GM显示,
*/
var 界面功能 = [


	["  #L20##r活跃天梯#k#l  ",1,1],
	["  #L4##b每日必做#k#l  ",1,1],
	
	["  #L0##b游戏商城#k#l  ",1,1],
	["  #L1##b个人信息#k#l  ",1,1],
	["  #L3##b背包清理#k#l  ",1,1],	

	["  #L5##b快捷指令#k#l  ",1,1],
	["  #L6##b家族组织#k#l  ",1,1],
	["  #L28##b江湖名师#k#l  ",1,1],
	["  #L7##b自助系统#k#l  ",1,1],
	["  #L9##b游戏商行#k#l  ",1,1],
	["  #L10##b增值服务#k#l  ",1,1],
	["  #L14##b现金商店#k#l  ",1,1],
	["  #L12##b幸运抽奖#k#l  ",1,1],
	["  #L15##b赞助礼包#k#l  ",1,1],
	["  #L11##b锻造工艺#k#l  ",1,1],
	//["  #L13##b冒险百科#k#l  ",1,1],
	["  #L2##b荣耀排行#k#l  ",1,1],
	["  #L16##b随身仓库#k#l  ",1,1],
	["  #L17##b股票市场#k#l  ",1,1],
	["  #L18##b游戏推广#k#l  ",1,1],
	["  #L19##bBGM-点播#k#l  ",1,1],
	["  #L9900000##b列表模式#k#l  ",1,1],
	

	
	]

function action(mode, type, selection) {
	
    if (status == 0 && mode == 0) {
        cm.dispose();
        return;
    } else if (mode == 0 && selection == -1) {
		cm.dispose();
        return;
	}
	
    if (mode == 1) {
        status++;
    } else {
        status--;
    }

	//废弃都市副本中
	if((cm.getPlayer().getMapId()>=103000800 && cm.getPlayer().getMapId()<=103000805)||cm.getPlayer().getMapId()==103000890){
		cm.getPlayer().dropMessage(1,"无法使用。");
		cm.dispose();
        return;
	}

	//显示
    if (status == 0) {
		var 文本 = "#i4001126# #i4001124# #i4001125##r#e<"+cm.冒险岛名称()+"-"+cm.大区()+">#k#n#i4001125# #i4001124# #i4001126#\r\n\r\n";
		 	文本 +="   时间 : #r"+year+"#k 年 #r"+month+"#k 月 #r"+day+"#k 日 #r" + hour + "#k 时 #r" + minute + "#k 分 #r" + second + "#k 秒 "+星期()+"\r\n";
			
			//管理员功能
			if(cm.getPlayer().getGM()>=3){
				文本 +=" \r\n";
				文本 +="    #b在线玩家#k:"+cm.在线人数()+"\r\n";
				文本 +="    #L200##r[GM]#k#b跟踪玩家#k#l   #L201##r[GM]#k#b玩家操作#k#l\r\n";
				if(cm.getPlayer().getGM()>=3){
					文本 +="    #L202##r[GM]#k#b本区盈利#k#l   #L203##r[GM]#k#b装备修改#k#l\r\n";
				}else{
					文本 +="    #L202##r[GM]#k#b本区盈利#k#l\r\n";
				}
			}
			
			if(cm.获取角色记录("回归奖励")==0){
				文本 +="			   #L1000##r回归好礼，欢乐相送#k#l\r\n";
			}
				
			if(cm.获取角色记录("商行出售栏奖励")==0 || cm.判断拍卖出售位数量()==0){
				文本 +="			   #L1001##r做一个快乐的小商人#k#l\r\n";
			}
				
			if(cm.getLevel() >= 1 && cm.获取角色记录("初入冒险")==0){
				文本 += "			         #L100001##b初入冒险#k#l\r\n";
			}
				
			if(cm.getLevel() >= 3 && cm.获取角色记录("萌新启航")==0){
				文本 += "			         #L100002##b萌新启航#k#l\r\n";
			}
				
			if(cm.getLevel() >= 5 && cm.获取角色记录("萌新启航2")==0){
				文本 += "			         #L100003##b萌新启航2#k#l\r\n";
			}

			//主菜单
			if(cm.getLevel() >= 8){
				s = 0;
				for (var ii = 0; ii < 界面功能.length; ii++) {
					if(界面功能[ii][1] > 0){
						if(界面功能[ii][2] > 0 || cm.getPlayer().getGM()>=3){
							if(s%4==0){
								文本 += "\r\n  ";
							}
							s++;
							文本 += ""+ 界面功能[ii][0]+ "";
						}
					}	
				}
			}else{
				文本 += "\r\n\r\n\r\n\t\t\t    8 级后解锁主界面功能。";

				if(cm.getLevel() >= 1 && cm.获取角色记录("初入冒险")==0){
					文本 += "\r\n\t\t\t\t\t#L100001##b初入冒险#k#l";
				}
				
				if(cm.getLevel() >= 3 && cm.获取角色记录("萌新启航")==0){
					文本 += "\r\n\t\t\t\t\t#L100002##b萌新启航#k#l";
				}
				
				if(cm.getLevel() >= 5 && cm.获取角色记录("萌新启航2")==0){
					文本 += "\r\n\t\t\t\t\t#L100003##b萌新启航2#k#l";
				}
				
				文本 += "\r\n\r\n\r\n\t   #b战士#k要求 #r10#k 级，力量 #r35#k 点，勇士部落转职。";
				文本 += "\r\n\t   #b魔法师#k要求 #r8#k 级，智力 #r20#k 点，魔法密林转职。";
				文本 += "\r\n\t   #b弓箭手#k要求 #r10#k 级，敏捷 #r25#k 点，射手村转职。";
				文本 += "\r\n\t   #b飞侠#k要求 #r10#k 级，敏捷 #r25#k 点，废弃都市转职。";
			}
			
			//显示区域
			文本 += "\r\n\r\n\r\n";
			if(cm.getLevel() >= 8){
				文本 +=	信息展示();
			}
			文本 +="\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n.";
			cm.sendGetText(文本);
    } else if (status == 1) {
        switch (selection) {
			case 9900000:
				cm.getPlayer().主界面(1);
				cm.dispose();
				cm.openNpc(9900000,9900000);
				cm.getPlayer().dropMessage(1, "主界面默认切换至列表模式");
                break;
			case 100001:
				if(cm.获取角色记录("初入冒险")==0){
					cm.gainExp(50);
					var 物品1 = 2000000;
					var 物品1数量 = 50;
					var 物品2 = 2000001;
					var 物品2数量 = 50;
					cm.getPlayer().gainItem(物品1, 物品1数量, false);
					cm.getPlayer().gainItem(物品2, 物品2数量, false);
					cm.新增角色记录("初入冒险");
					cm.sendOk("\r\n领取奖励成功。\r\n\r\n#v"+物品1+"# #b#t"+物品1+"##k x "+物品1数量+"\r\n#v"+物品2+"# #b#t"+物品2+"##k x "+物品2数量+"\r\n\r\n3级后可奖励下一阶段奖励。");
				}
				cm.dispose();
                break;
			case 100002:
				if(cm.获取角色记录("萌新启航")==0){
					cm.gainExp(200);
					var 物品1 = 2000000;
					var 物品1数量 = 100;
					var 物品2 = 2000001;
					var 物品2数量 = 100;
					var 金币 = 100000;
					cm.gainMeso(金币);
					cm.getPlayer().gainItem(物品1, 物品1数量, false);
					cm.getPlayer().gainItem(物品2, 物品2数量, false);
					cm.新增角色记录("萌新启航");
					cm.sendOk("\r\n领取奖励成功。\r\n\r\n#v4031039# #b金币#k x "+金币+"\r\n#v"+物品1+"# #b#t"+物品1+"##k x "+物品1数量+"\r\n#v"+物品2+"# #b#t"+物品2+"##k x "+物品2数量+"\r\n\r\n5级后可奖励下一阶段奖励。");
				}
				cm.dispose();
                break;
			case 100003:
				if(cm.获取角色记录("萌新启航2")==0){
					cm.gainExp(300);
					var 物品1 = 2000000;
					var 物品1数量 = 100;
					var 物品2 = 2000001;
					var 物品2数量 = 100;
					var 金币 = 100000;
					cm.gainMeso(金币);
					cm.getPlayer().gainItem(物品1, 物品1数量, false);
					cm.getPlayer().gainItem(物品2, 物品2数量, false);
					cm.新增角色记录("萌新启航2");
					cm.sendOk("\r\n领取奖励成功。\r\n\r\n#v4031039# #b金币#k x "+金币+"\r\n#v"+物品1+"# #b#t"+物品1+"##k x "+物品1数量+"\r\n#v"+物品2+"# #b#t"+物品2+"##k x "+物品2数量+"\r\n\r\n恭喜你领取完所有奖励。");
				}
				cm.dispose();
                break;
			case 28:
				cm.dispose();
				cm.openNpc(9900000,28);
                break;
			case 20:
				cm.dispose();
				cm.openNpc(9900000,20);
                break;
			case 19:
				cm.dispose();
				cm.openNpc(9900000,19);
                break;
			case 18:
				cm.dispose();
				cm.openNpc(9900000,18);
                break;
			case 17:
				cm.dispose();
				cm.openNpc(9900000,17);
                break;
			case 16:
				cm.dispose();
				cm.openNpc(9900000,16);
                break;
			case 15:
				cm.dispose();
				cm.openNpc(9900000,15);
                break;
			case 1003:
				if(cm.获取账号记录("维护补偿")==0){
					cm.getPlayer().getCashShop().gainCash(1, 666);
					cm.新增账号记录("维护补偿");
					cm.sendOk("领取成功。");
				}
				cm.dispose();
                break;
			case 1000:
				//其实是回归奖励
				if(cm.获取角色记录("回归奖励")==0){
					cm.getPlayer().gainItem(1112002, 1, false);
					cm.新增角色记录("回归奖励");
					cm.sendOk("恭喜你领取回归奖励成功。");
				}
				cm.dispose();
                break;
			case 1001:
				//商行出售栏奖励
				if(cm.获取角色记录("商行出售栏奖励")==0|| cm.判断拍卖出售位数量()==0){
					cm.新增拍卖存储金币(cm.getPlayer().getId(),1);
					cm.新增角色记录("商行出售栏奖励");
					cm.sendOk("恭喜你领取商行出售栏 * 10。");
				}
				cm.dispose();
                break;
				
			case 200:
				cm.dispose();
				if(cm.getPlayer().getGM()>=3){
					cm.openNpc(9900000,200);
				}
                break;
			case 204:
				cm.dispose();
				cm.openNpc(9900000,204);
                break;
			case 203:
				cm.dispose();
				if(cm.getPlayer().getGM()>=4){
					cm.openNpc(9900000,203);
				}
                break;
			case 201:
				cm.dispose();
				if(cm.getPlayer().getGM()>=3){
					cm.openNpc(9900000,201);
				}
                break;
			case 202:
				cm.dispose();
				if(cm.getPlayer().getGM()>=3){
					cm.openNpc(9900000,202);
				}
                break;
			case 14:
				cm.dispose();
				cm.openNpc(9900000,14);
                break;
			case 11:
				cm.dispose();
				cm.openNpc(9900001);
                break;
			case 13:
				cm.dispose();
				cm.openNpc(9900000, 13);
                break;
			//进入游戏商城
			case 0:
				cm.TransferToCashShop();
				cm.dispose();
                break;
			//查看个人信息
			case 1:
				cm.dispose();
                cm.openNpc(9900000, 1);
                break;
			//游戏排行榜
			case 2:
				cm.dispose();
                cm.openNpc(9900000, 2);
                break;
			//清理背包
			case 3:
				cm.dispose();
                cm.openNpc(9900000, 3);
                break;
			//每日任务和福利
			case 4:
				cm.dispose();
                cm.openNpc(9900000, 4);
                break;
			//快捷指令
			case 5:
				cm.dispose();
                cm.openNpc(9900000, 5);
                break;
			//家族组织
			case 6:
				cm.dispose();
                cm.openNpc(9900000, 6);
                break;
			//活跃天梯
			case 20:
				cm.dispose();
                cm.openNpc(9900000, 20);
                break;
			//自助系统
			case 7:
				cm.dispose();
                cm.openNpc(9900000, 7);
                break;
			//游戏商行
			case 9:
				cm.dispose();
                cm.openNpc(9900000, 9);
                break;
			//增值服务
			case 10:
				cm.dispose();
                cm.openNpc(9900000, 10);
                break;
			case 12:
				cm.dispose();
                cm.openNpc(9900000, 12);
                break;
			default:
                cm.dispose();
                break;
        }
    }
}


function  信息展示() {
	var 文本 = "";
	var 金币 = ""+cm.getPlayer().getMeso()+"";
	var 点券 = ""+cm.getPlayer().getCashShop().getCash(1)+"";
	var 现金券 = ""+cm.getPlayer().获取现金券()+"";
	var exp = cm.getPlayer().获取锻造信息("forgingExp",cm.getPlayer().getId());
	var 战斗力 = ""+exp+"";
	文本 +="   ( 金    币 : #r"+金币+"#k";
	for (var j = 13 - 金币.getBytes().length; j > 0; j--) {
		文本 += " ";
	}
	文本 +="点  券 : #r"+点券+"#k";
	for (var j = 13 - 点券.getBytes().length; j > 0; j--) {
		文本 += " ";
	}
	文本 +=" )\r\n";
	文本 +="   ( 锻造工艺 : #r"+战斗力+"#k";
	for (var j = 13 - 战斗力.getBytes().length; j > 0; j--) {
		文本 += " ";
	}
	文本 +="现金券 : #r"+现金券+"#k";
	for (var j = 13 - 现金券.getBytes().length; j > 0; j--) {
		文本 += " ";
	}
	文本 +=" )\r\n";
	
	return 文本;
}


function  星期() {
	switch (weekday) {
		case 1:
			return "星期日";
		case 2:
			return "星期一";
		case 3:
			return "星期二";
		case 4:
			return "星期三";
		case 5:
			return "星期四";
		case 6:
			return "星期五";
		case 7:
			return "星期六";
		default:
			return 0;
	}
}










