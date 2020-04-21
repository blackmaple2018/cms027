var ca = java.util.Calendar.getInstance();
var year = ca.get(java.util.Calendar.YEAR);
var month = ca.get(java.util.Calendar.MONTH) + 1;
var day = ca.get(java.util.Calendar.DATE);
var hour = ca.get(java.util.Calendar.HOUR_OF_DAY);
var minute = ca.get(java.util.Calendar.MINUTE);
var second = ca.get(java.util.Calendar.SECOND);
var weekday = ca.get(java.util.Calendar.DAY_OF_WEEK);
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
	cm.角色存档();
	var 排名 = 名次(cm.getPlayer().getId());
	var 积分 = cm.getPlayer().获取天梯积分();
    if (status == 0) {
        var selStr = "  #i4030000#  #i4030001#  #i4030010# #r#e< 活跃天梯 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n\r\n";
		selStr +="  时间 : #r"+year+"#k 年 #r"+month+"#k 月 #r"+day+"#k 日 #r" + hour + "#k 时 #r" + minute + "#k 分 #r" + second + "#k 秒 "+星期()+"\r\n";
		selStr += "\t\t开始时间为周一至周六通过做每日任务增加积分，周日积分结算。每周一清空重置天梯积分，奖励分为阶段奖励，排行奖励，最低 #r200#k 积分在结算的时候才有奖励。\r\n\t\t你当前 （ #r"+最高积分()+"#k / #r"+积分+"#k）#k 活跃积分，本周在线 #r"+cm.getPlayer().totalOnlineTimett+"#k 分钟，排名是第 #r"+排名+"#k 名。\r\n";
		selStr += "\t\t\t\t  #b#L0#获取积分#l\r\n";
		selStr += "\t\t\t\t  #b#L1#查看排行榜#l\r\n";
		selStr += "\t\t\t\t  #b#L2#查看奖励和说明#l\r\n";
		selStr += "\t\t\t\t  #b#L3#领取排行奖励#l\r\n";
		selStr += "\t\t\t\t  #b#L4#领取积分奖励#l\r\n";
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
			case 0:
				cm.dispose();
				cm.openNpc(9900000,4);
                break;
			case 3:
				if(weekday == 1 && hour > 17){
					if (cm.getPlayer().getbosslog("活跃天梯排位奖励") <= 0) {
						if(积分 >= 200){
							if(名次(cm.getPlayer().getId()) == 1){//第一名
								cm.getPlayer().设置天梯奖励(5);
								cm.sendOk("恭喜你获得天梯活跃#r第 "+名次(cm.getPlayer().getId())+" 名#k，\r\n你的奖励是 #b本职业已被提高的所有附魔技能 + 5#k\r\n重新登陆游戏生效，限时6天。");
							}else if(名次(cm.getPlayer().getId()) == 2){//第二名
								if(cm.getPlayer().获取天梯积分() == 最高积分()){
									cm.getPlayer().设置天梯奖励(5);
									cm.sendOk("恭喜你获得天梯活跃#r第 "+名次(cm.getPlayer().getId())+" 名#k，由于你和排名第一的积分相同，所以享受的奖励所有改变。\r\n你的奖励是 #b本职业已被提高的所有附魔技能 + 5#k\r\n重新登陆游戏生效，限时6天。");
								}else{
									cm.getPlayer().设置天梯奖励(4);
									cm.sendOk("恭喜你获得天梯活跃#r第 "+名次(cm.getPlayer().getId())+" 名#k，\r\n你的奖励是 #b本职业已被提高的所有附魔技能 + 4#k\r\n重新登陆游戏生效，限时6天。");
								}
							}else if(名次(cm.getPlayer().getId()) == 3){//第三名
								if(cm.getPlayer().获取天梯积分() == 最高积分()){
									cm.getPlayer().设置天梯奖励(5);
									cm.sendOk("恭喜你获得天梯活跃#r第 "+名次(cm.getPlayer().getId())+" 名#k，由于你和排名第一的积分相同，所以享受的奖励所有改变。\r\n你的奖励是 #b本职业已被提高的所有附魔技能 + 5#k\r\n重新登陆游戏生效，限时6天。");
								}else{
									cm.getPlayer().设置天梯奖励(3);
									cm.sendOk("恭喜你获得天梯活跃#r第 "+名次(cm.getPlayer().getId())+" 名#k，\r\n你的奖励是 #b本职业已被提高的所有附魔技能 + 3#k\r\n重新登陆游戏生效，限时6天。");
								}
							}else if(名次(cm.getPlayer().getId()) >= 4 && 名次(cm.getPlayer().getId()) <= 10){
								if(cm.getPlayer().获取天梯积分() == 最高积分()){//第四名 - 第十名
									cm.getPlayer().设置天梯奖励(5);
									cm.sendOk("恭喜你获得天梯活跃#r第 "+名次(cm.getPlayer().getId())+" 名#k，由于你和排名第一的积分相同，所以享受的奖励所有改变。\r\n你的奖励是 #b本职业已被提高的所有附魔技能 + 5#k\r\n重新登陆游戏生效，限时6天。");
								}else{
									cm.getPlayer().设置天梯奖励(2);
									cm.sendOk("恭喜你获得天梯活跃#r第 "+名次(cm.getPlayer().getId())+" 名#k，\r\n你的奖励是 #b本职业已被提高的所有附魔技能 + 2#k\r\n重新登陆游戏生效，限时6天。");
								}
							}else if(名次(cm.getPlayer().getId()) >= 11 && 名次(cm.getPlayer().getId()) <= 20){
								if(cm.getPlayer().获取天梯积分() == 最高积分()){//第十一名 - 第二十名
									cm.getPlayer().设置天梯奖励(5);
									cm.sendOk("恭喜你获得天梯活跃#r第 "+名次(cm.getPlayer().getId())+" 名#k，由于你和排名第一的积分相同，所以享受的奖励所有改变。\r\n你的奖励是 #b本职业已被提高的所有附魔技能 + 5#k\r\n重新登陆游戏生效，限时6天。");
								}else{
									cm.getPlayer().设置天梯奖励(1);
									cm.sendOk("恭喜你获得天梯活跃#r第 "+名次(cm.getPlayer().getId())+" 名#k，\r\n你的奖励是 #b本职业已被提高的所有附魔技能 + 1#k\r\n重新登陆游戏生效，限时6天。");
								}
							}else{
								cm.sendOk("很抱歉你未进入前20，没有奖励。");
							}
							cm.setbosslog("活跃天梯排位奖励");
						}else{
							cm.sendOk("低于最低结算积分，无法领取奖励。");
						}
					}else{
						cm.sendOk("无法领取奖励或者你已经领取过了。");
					}
				}else{
					cm.sendOk("阶段奖励不在领取时间，请在#r周日下午，18 时#k后再来领取。");
				}
				cm.dispose();
                break;
			case 4:
				if(weekday == 1 && hour > 17){
					if(积分 < 200){
						cm.sendOk("低于最低结算积分，无法领取奖励。");
						cm.dispose();
						return;
					}
					if (cm.getPlayer().getbosslog("活跃天梯阶段奖励") <= 0) {
						var 数量 = 0;
						if(积分 >= 200){
							数量 += 20;
						}
						if(积分 >= 250){
							数量 += 60;
						}
						if(积分 >= 300){
							数量 += 50;
						}
						cm.getPlayer().gainItem(4006000,数量,false);
						cm.getPlayer().gainItem(4006004,数量,false);
						cm.getPlayer().gainItem(4006002,数量,false);
						cm.getPlayer().gainItem(4006003,数量,false);
						cm.setbosslog("活跃天梯阶段奖励");
						cm.sendOk("恭喜你获得 #v4006000# #v4006002# #v4006003# #v4006004# x "+数量+"");
					}else{
						cm.sendOk("已经领取过奖励了。");
					}
				}else{
					cm.sendOk("排行奖励不在领取时间，请在#r周日下午，18 时#k后再来领取。");
				}
				cm.dispose();
                break;	
			case 1:
				cm.dispose();
				cm.openNpc(9900000,20001);
                break;
			case 2:
				var x = "  #i4030000#  #i4030001#  #i4030010# #r#e< 活跃天梯 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
				x += "\t\t活跃赛季时间；\r\n";
				x += "\t\t#b累计积分#k；周一凌晨至周日下午17点整\r\n";
				x += "\t\t#b领奖时间#k；周日下午17点后\r\n";
				x += "\r\n";
				x += "\t\t积分解锁奖励；\r\n";
				x += "\t\t#b200#k 积分 - #v4006000# #v4006002# #v4006003# #v4006004# x 20\r\n";
				x += "\t\t#b250#k 积分 - #v4006000# #v4006002# #v4006003# #v4006004# x 30\r\n";
				x += "\t\t#b300#k 积分 - #v4006000# #v4006002# #v4006003# #v4006004# x 50\r\n";;
				x += "\r\n";
				x += "\t\t结算排行奖励；#r(限时6天BUFF效果)#k\r\n";
				x += "\t\t#b第1名#k     本职业已被提高的所有附魔技能 + 5\r\n";
				x += "\t\t#b第2名#k     本职业已被提高的所有附魔技能 + 4\r\n";
				x += "\t\t#b第3名#k     本职业已被提高的所有附魔技能 + 3\r\n";
				x += "\t\t#b第4-10名#k  本职业已被提高的所有附魔技能 + 2\r\n";
				x += "\t\t#b第11-20名#k 本职业已被提高的所有附魔技能 + 1\r\n";
				cm.sendSimple(x);
				cm.dispose();
                break;
			default:
                cm.dispose();
                break;
        }
    }
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


function 名次(id) {
    var con = Packages.launch.Start.getInstance().getConnection();
    var count = 0;
    var ps;
    ps = con.prepareStatement("SELECT rank FROM (SELECT @rownum := @rownum + 1 AS rank, `name`, integral*100000+totalOnlineTimett, `id` FROM characters, (SELECT @rownum := 0) r WHERE gm = 0  && banned = 0  ORDER BY integral*100000+totalOnlineTimett DESC) AS T1 WHERE `id` = ?");
    ps.setInt(1, id);
    var rs = ps.executeQuery();
    if (rs.next()) {
        count = rs.getString("rank");
    } else {
        count = 0;
    }
    rs.close();
    ps.close();
	con.close();
    return count;
}

function 最高积分() {
    var con = Packages.launch.Start.getInstance().getConnection();
    var count = 0;
    var ps;
    ps = con.prepareStatement("SELECT MAX(integral) as DATA FROM characters WHERE gm = 0");
    var rs = ps.executeQuery();
    if (rs.next()) {
        count = rs.getInt("DATA");
    } else {
        count = 0;
    }
    rs.close();
    ps.close();
	con.close();
    return count;
}























