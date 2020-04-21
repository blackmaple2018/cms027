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
	
	if (cm.getInventory(2).isFull(1)) {
        cm.sendOk("请保证 #b消耗栏#k 至少有2个位置。");
        cm.dispose();
        return;
    }
	
    if (cm.getInventory(4).isFull(1)) {
        cm.sendOk("请保证 #b其他栏#k 至少有2个位置。");
        cm.dispose();
        return;
    }
	
	var 声望值 = 声望(cm.getPlayer().getId());
    if (status == 0) {
        var selStr = " #i4030000#  #i4030001#  #i4030010# #r#e< 江湖名师 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
		
		/*if(cm.getPlayer().师傅()){
			selStr += "\t#b师傅#k : "+ cm.取角色名字(cm.getPlayer().师傅())+" \r\n";
		}*/
		
		selStr += "\t#b声望#k : "+ 声望值+" \r\n";
		selStr += "\t#L0##b怎样获取声望#k#l\r\n\r\n";
		selStr += "\r\n";
		selStr += "\t每日奖励 - \r\n";
		selStr += "\t1.)声望 #b2000#k  以上每日可领取 #b#t4001019##k x 1\r\n";
		if(cm.getbosslog("师傅每日奖励1")==0){
			if(声望值 >= 2000){
				selStr += "\t\t#L1##b领取#k#l\r\n\r\n";
			}	
		}
		
		selStr += "\t2.)声望 #b4000#k 以上每日可领取 #b#t4006003##k x 15\r\n";
		if(cm.getbosslog("师傅每日奖励2")==0){
			if(声望值 >= 4000){
				selStr += "\t\t#L2##b领取#k#l\r\n\r\n";
			}	
		}
		
		selStr += "\t3.)声望 #b8000#k 以上每日可领取 #b#t4031071##k x 1\r\n";
		if(cm.getbosslog("师傅每日奖励3")==0){
			if(声望值 >= 8000){
				selStr += "\t\t#L3##b领取#k#l\r\n\r\n";
			}	
		}
		
		selStr += "\t4.)声望 #b15000#k 以上每日可领取 #b#t4006000##k x 50\r\n";
		if(cm.getbosslog("师傅每日奖励4")==0){
			if(声望值 >= 15000){
				selStr += "\t\t#L4##b领取#k#l\r\n\r\n";
			}	
		}
		
		selStr += "\r\n\t成就奖励 - \r\n";

		selStr += "\t1.)声望 #b15000#k #b三孔以下装备打孔必成一次#r(不包括3孔)#k#l\r\n";
		if(cm.获取账号记录("师傅成就奖励1")==0){
			if(声望值 >= 15000){
				selStr += "\t\t#L101##b领取#k#l\r\n\r\n";
			}	
		}
		
		selStr += "\t2.)声望 #b30000#k #b四孔以下装备打孔必成一次#r(不包括4孔)#k#l\r\n";
		if(cm.获取账号记录("师傅成就奖励2")==0){
			if(声望值 >= 30000){
				selStr += "\t\t#L102#b领取#k#l\r\n\r\n";
			}	
		}
		
		selStr += "\t3.)声望 #b80000#k #b五孔以下装备打孔必成一次#r(不包括5孔)#k#l\r\n";
		if(cm.获取账号记录("师傅成就奖励3")==0){
			if(声望值 >= 80000){
				selStr += "\t\t#L103##b领取#k#l\r\n\r\n";
			}	
		}
		
		selStr+="\r\n\r\n\r\n\r\n\r\n\r\n\r\n. ";
		cm.sendSimple(selStr);
    } else if (status == 1) {
		switch (selection) {
			case 0:
				cm.sendOk("徒弟升级后，会给予师傅声望，获得的声望数值跟徒弟的等级有关。#r拜师的话点击目标个人情报，在最下面可以看到。#l");
				cm.dispose();
				break;
			case 103:
				if(声望值 >= 80000){
					if(cm.获取账号记录("师傅成就奖励3")==0){
						cm.dispose();
						cm.openNpc(9900000,280003);
					}else{
						cm.sendOk("已经领取过奖励了。");
						cm.dispose();
					}
				}
				break;
				
			case 102:
				if(声望值 >= 30000){
					if(cm.获取账号记录("师傅成就奖励2")==0){
						cm.dispose();
						cm.openNpc(9900000,280002);
					}else{
						cm.sendOk("已经领取过奖励了。");
						cm.dispose();
					}
				}
				break;
			case 101:
				if(声望值 >= 15000){
					if(cm.获取账号记录("师傅成就奖励1")==0){
						cm.dispose();
						cm.openNpc(9900000,280001);
					}else{
						cm.sendOk("已经领取过奖励了。");
						cm.dispose();
					}
				}
				break;
				
			case 4:
				if(声望值 >= 15000){
					if(cm.getbosslog("师傅每日奖励4")==0){
						cm.getPlayer().gainItem(4006000,50,false);
						cm.setbosslog("师傅每日奖励4");
						cm.sendOk("恭喜你领取成功。");
					}else{
						cm.sendOk("已经领取过奖励了。");
					}
				}
				cm.dispose();
				break;
			case 3:
				if(声望值 >= 8000){
					if(cm.getbosslog("师傅每日奖励3")==0){
						cm.getPlayer().gainItem(4031071,1,false);
						cm.setbosslog("师傅每日奖励3");
						cm.sendOk("恭喜你领取成功。");
					}else{
						cm.sendOk("已经领取过奖励了。");
					}
				}
				cm.dispose();
				break;
			case 2:
				if(声望值 >= 4000){
					if(cm.getbosslog("师傅每日奖励2")==0){
						cm.getPlayer().gainItem(4006003,15,false);
						cm.setbosslog("师傅每日奖励2");
						cm.sendOk("恭喜你领取成功。");
					}else{
						cm.sendOk("已经领取过奖励了。");
					}
				}
				cm.dispose();
				break;
			case 1:
				if(声望值 >= 2000){
					if(cm.getbosslog("师傅每日奖励1")==0){
						cm.getPlayer().gainItem(4001019,1,false);
						cm.setbosslog("师傅每日奖励1");
						cm.sendOk("恭喜你领取成功。");
					}else{
						cm.sendOk("已经领取过奖励了。");
					}
				}
				cm.dispose();
				break;
		}
    }
}

function 声望(id) {
    var con = Packages.launch.Start.getInstance().getConnection();
    var count = 0;
    var ps;
    ps = con.prepareStatement("SELECT prestige as DATA FROM characters WHERE `id` = ?");
    ps.setInt(1, id);
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