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
	
	if (cm.getPlayer().判断增值服务数据() == 0) {
		cm.getPlayer().初始化增值服务数据();
	}
	
	var 点券 = cm.getPlayer().获取增值服务数据("dianquan");
	var 点券2 = cm.getPlayer().获取增值服务数据("dianquan2");
	var 金币 = cm.getPlayer().获取增值服务数据("meso");
	var 金币2 = cm.getPlayer().获取增值服务数据("meso2");
	var 金币3 = cm.getPlayer().获取增值服务数据("meso3");
	var 材料 = cm.getPlayer().获取增值服务数据("cailiao");
	var 综合 = 点券+金币+材料;
	
    if (status == 0) {
		var 文本 = "  #i4030000#  #i4030001#  #i4030010# #r#e< 增值服务 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
		 	文本 +=" 时间 : #r"+year+"#k 年 #r"+month+"#k 月 #r"+day+"#k 日 #r" + hour + "#k 时 #r" + minute + "#k 分 #r" + second + "#k 秒 "+星期()+"\r\n\r\n";
			if(综合 ==0 ){
				文本 +="\r\n\r\n    非常抱歉，你尚未开通任何增值服务，如果你需要购买请在群里私聊游戏机器人，访问小z店铺进行购买。 \r\n";
			}
			/**
			<点券类型福利>
			**/
			if(点券 > 0 ){
				文本 +="  \r\n";
				文本 +="  #e#r点券福利#k#n  \r\n";
				文本 +="  已开通，剩余领取 #b"+点券+"#k 次";
				if(cm.getbosslog("点券福利")==0){
					文本 += "\t\t\t\t\t\t#L1##r[领取]#k#l";
				}else{
					文本 += "\t\t\t\t\t\t#L1##b[已领]#k#l";
				}
				文本 +="  \r\n";
				文本 +="  角色每日上线可领取 #b200#k 点券 \r\n";
			}
			if(点券2 > 0 ){
				文本 +="  \r\n";
				文本 +="  #e#r点券福利2#k#n  \r\n";
				文本 +="  已开通，剩余领取 #b"+点券2+"#k 次";
				if(cm.getbosslog("点券福利2")==0){
					文本 += "\t\t\t\t\t\t#L11##r[领取]#k#l";
				}else{
					文本 += "\t\t\t\t\t\t#L11##b[已领]#k#l";
				}
				文本 +="  \r\n";
				文本 +="  角色每日上线可领取 #b500#k 点券 \r\n";
			}
			
			/**
			<金币类型福利>
			**/
			if(金币 > 0 ){
				文本 +="  \r\n";
				文本 +="  #e#r金币福利#k#n  \r\n";
				文本 +="  已开通，剩余领取 #b"+金币+"#k 次";
				if(cm.getbosslog("金币福利")==0){
					文本 += "\t\t\t\t\t\t#L2##r[领取]#k#l";
				}else{
					文本 += "\t\t\t\t\t\t#L2##b[已领]#k#l";
				}
				文本 +="  \r\n";
				文本 +="  角色每日上线可领取 #b20W#k 金币 \r\n";
			}
			
			if(金币2 > 0 ){
				文本 +="  \r\n";
				文本 +="  #e#r金币福利2#k#n  \r\n";
				文本 +="  已开通，剩余领取 #b"+金币2+"#k 次";
				if(cm.getbosslog("金币福利2")==0){
					文本 += "\t\t\t\t\t\t#L22##r[领取]#k#l";
				}else{
					文本 += "\t\t\t\t\t\t#L22##b[已领]#k#l";
				}
				文本 +="  \r\n";
				文本 +="  角色每日上线可领取 #b50W#k 金币 \r\n";
			}
			
			if(金币3 > 0 ){
				文本 +="  \r\n";
				文本 +="  #e#r金币福利3#k#n  \r\n";
				文本 +="  已开通，剩余领取 #b"+金币3+"#k 次";
				if(cm.getbosslog("金币福利3")==0){
					文本 += "\t\t\t\t\t\t#L222##r[领取]#k#l";
				}else{
					文本 += "\t\t\t\t\t\t#L222##b[已领]#k#l";
				}
				文本 +="  \r\n";
				文本 +="  角色每日上线可领取 #b100W#k 金币 \r\n";
			}
			
			/**
			<材料类型福利>
			**/
			if(材料 > 0 ){
				文本 +="  \r\n";
				文本 +="  #e#r材料福利#k#n  \r\n";
				文本 +="  已开通，剩余领取 #b"+材料+"#k 次";
				if(cm.getbosslog("材料福利")==0){
					文本 += "\t\t\t\t\t\t#L3##r[领取]#k#l";
				}else{
					文本 += "\t\t\t\t\t\t#L3##b[已领]#k#l";
				}
				文本 +="  \r\n";
				文本 +="  角色每日上线可领取一定数量的#b魔法石#k材料 \r\n";
			}
			
			文本 +="\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n.";
			cm.sendSimple(文本);
    } else if (status == 1) {
        switch (selection) {
			case 1:
				if(点券 > 0 ){
					if(cm.getbosslog("点券福利")==0){
						cm.getPlayer().getCashShop().gainCash(1, 200);
						cm.setbosslog("点券福利");
						cm.getPlayer().修改增值服务数据("dianquan",-1);
						cm.sendOk("恭喜你获得 #b200#k 点券。");
					}else{
						cm.sendOk("请明天再来领取吧。");
					}
				}else{
					cm.sendOk("你不可以领取哦。");
				}
				cm.dispose();
                break;
			case 11:
				if(点券2 > 0 ){
					if(cm.getbosslog("点券福利2")==0){
						cm.getPlayer().getCashShop().gainCash(1, 500);
						cm.setbosslog("点券福利2");
						cm.getPlayer().修改增值服务数据("dianquan2",-1);
						cm.sendOk("恭喜你获得 #b500#k 点券。");
					}else{
						cm.sendOk("请明天再来领取吧。");
					}
				}else{
					cm.sendOk("你不可以领取哦。");
				}
				cm.dispose();
                break;
			case 2:
				if(金币 > 0 ){
					if(cm.getbosslog("金币福利")==0){
						cm.getPlayer().gainMeso(20*10000,false);
						cm.setbosslog("金币福利");
						cm.getPlayer().修改增值服务数据("meso",-1);
						cm.sendOk("恭喜你获得 #b20W#k 金币。");
					}else{
						cm.sendOk("请明天再来领取吧。");
					}
				}else{
					cm.sendOk("你不可以领取哦。");
				}
				cm.dispose();
                break;
			case 22:
				if(金币2 > 0 ){
					if(cm.getbosslog("金币福利2")==0){
						cm.getPlayer().gainMeso(50*10000,false);
						cm.setbosslog("金币福利2");
						cm.getPlayer().修改增值服务数据("meso2",-1);
						cm.sendOk("恭喜你获得 #b50W#k 金币。");
					}else{
						cm.sendOk("请明天再来领取吧。");
					}
				}else{
					cm.sendOk("你不可以领取哦。");
				}
				cm.dispose();
                break;
			case 222:
				if(金币3 > 0 ){
					if(cm.getbosslog("金币福利3")==0){
						cm.getPlayer().gainMeso(100*10000,false);
						cm.setbosslog("金币福利3");
						cm.getPlayer().修改增值服务数据("meso3",-1);
						cm.sendOk("恭喜你获得 #b100W#k 金币。");
					}else{
						cm.sendOk("请明天再来领取吧。");
					}
				}else{
					cm.sendOk("你不可以领取哦。");
				}
				cm.dispose();
                break;
			case 3:
				if(材料 > 0 ){
					if (cm.getInventory(4).isFull(3)) {
						cm.sendOk("请保证 #b其他栏#k 至少有4个位置。");
						cm.dispose();
					return;
					}
					if(cm.getbosslog("材料福利")==0){
						cm.getPlayer().gainItem(4006000,50,false);
						cm.getPlayer().gainItem(4006001,50,false);
						cm.getPlayer().gainItem(4006002,50,false);
						cm.getPlayer().gainItem(4006003,50,false);
						cm.setbosslog("材料福利");
						cm.getPlayer().修改增值服务数据("cailiao",-1);
						cm.sendOk("恭喜你获得 #v4006000# #v4006001# #v4006002# #v4006003# x 50 ");
					}else{
						cm.sendOk("请明天再来领取吧。");
					}
				}else{
					cm.sendOk("你不可以领取哦。");
				}
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



