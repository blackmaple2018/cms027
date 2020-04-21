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
    } else if (mode == 0 && selection == -1) {
		cm.dispose();
        return;
	}
	
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
	
    if (status == 0) {
		var 文本 = "  #i4030000#  #i4030001#  #i4030010# #r#e< 赞助礼包 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
		 	文本 +="   时间 : #r"+year+"#k 年 #r"+month+"#k 月 #r"+day+"#k 日 #r" + hour + "#k 时 #r" + minute + "#k 分 #r" + second + "#k 秒 "+星期()+"\r\n\r\n";
			文本 +="     你当前累计充值赞助:#r "+cm.判断消费()+"#k RMB\r\n";
			文本 +="     #e#r每种福利可以领取一次#k#n\r\n";
			
			
			if(cm.获取账号记录("6元累计礼包")==0){
				文本 +="\t#L1##i4030001##r#e[6 RMB]#n #b泡点金币/经验+10#k#l\r\n";
			}else{
				文本 +="\t#L1##i4030001##r#e[6 RMB]#n #r泡点金币/经验+10#k#l\r\n";
			}
			
			
			if(cm.获取账号记录("68元累计礼包")==0){
				文本 +="\t#L2##i4030001##r#e[68 RMB]#n #b泡点点券+10#k#l\r\n";
			}else{
				文本 +="\t#L2##i4030001##r#e[68 RMB]#n #r泡点点券+10#k#l\r\n";
			}
			
			
			if(cm.获取账号记录("198元累计礼包")==0){
				文本 +="\t#L3##i4030001##r#e[198 RMB]#n #b泡点金币/经验/点券+10#k#l\r\n";
			}else{
				文本 +="\t#L3##i4030001##r#e[198 RMB]#n #r泡点金币/经验/点券+10#k#l\r\n";
			}
			
			//if(cm.判断消费()>=328){
			if(cm.获取账号记录("328元累计礼包")==0){
				文本 +="\t#L4##i4030001##r#e[328 RMB]#n #b二孔装备打孔必成一次#k#l\r\n";
			}else{
				文本 +="\t#L4##i4030001##r#e[328 RMB]#n #r二孔装备打孔必成一次#k#l\r\n";
			}
			//}
			//if(cm.判断消费()>=648){
			if(cm.获取账号记录("648元累计礼包")==0){
				文本 +="\t#L5##i4030001##r#e[648 RMB]#n #b三孔装备打孔必成一次#k#l\r\n";
			}else{
				文本 +="\t#L5##i4030001##r#e[648 RMB]#n #r三孔装备打孔必成一次#k#l\r\n";
			}

			
			if(cm.获取账号记录("1648元累计礼包")==0){
				文本 +="\t#L6##i4030001##r#e[1648 RMB]#n #b四孔装备打孔必成一次#k#l\r\n";
			}else{
				文本 +="\t#L6##i4030001##r#e[1648 RMB]#n #b四孔装备打孔必成一次#k#l\r\n";
			}
			//}
			
			
			
			文本 +="\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n.";
			cm.sendSimple(文本);
    } else if (status == 1) {
        switch (selection) {
			case 6:
				if(cm.判断消费()>=1648){
					if(cm.获取账号记录("1648元累计礼包")==0){
						cm.dispose();
						cm.openNpc(9900000,150003);
					}else{
						cm.sendOk("无法重复领取。");
						cm.dispose();
					}
				}else{
					cm.sendOk("累计赞助金额没达到要求。");
					cm.dispose();
				}
                break;
			case 5:
				if(cm.判断消费()>=648){
					if(cm.获取账号记录("648元累计礼包")==0){
						cm.dispose();
						cm.openNpc(9900000,150002);
					}else{
						cm.sendOk("无法重复领取。");
						cm.dispose();
					}
				}else{
					cm.sendOk("累计赞助金额没达到要求。");
					cm.dispose();
				}
                break;
			case 4:
				if(cm.判断消费()>=328){
					if(cm.获取账号记录("328元累计礼包")==0){
						cm.dispose();
						cm.openNpc(9900000,150001);
					}else{
						cm.sendOk("无法重复领取。");
						cm.dispose();
					}
				}else{
					cm.sendOk("累计赞助金额没达到要求。");
					cm.dispose();
				}              
				break;
			case 3:
				if(cm.判断消费()>=198){
					if(cm.获取账号记录("198元累计礼包")==0){
						cm.新增账号记录("198元累计礼包");
						cm.getPlayer().set泡点点券((cm.getPlayer().get泡点点券()+(10)));
						cm.getPlayer().set泡点经验((cm.getPlayer().get泡点经验()+(10)));
						cm.getPlayer().set泡点金币((cm.getPlayer().get泡点金币()+(10)));
						cm.sendOk("恭喜你领取成功。");
					}else{
						cm.sendOk("无法重复领取。");
					}
				}else{
					cm.sendOk("累计赞助金额没达到要求。");
				}
				cm.dispose();
                break;
			case 2:
				if(cm.判断消费()>=68){
					if(cm.获取账号记录("68元累计礼包")==0){
						cm.新增账号记录("68元累计礼包");
						cm.getPlayer().set泡点点券((cm.getPlayer().get泡点点券()+(10)));
						cm.sendOk("恭喜你领取成功。");
					}else{
						cm.sendOk("无法重复领取。");
					}
				}else{
					cm.sendOk("累计赞助金额没达到要求。");
				}
				cm.dispose();
                break;
			case 1:
				if(cm.判断消费()>=6){
					if(cm.获取账号记录("6元累计礼包")==0){
						cm.新增账号记录("6元累计礼包");
						cm.getPlayer().set泡点经验((cm.getPlayer().get泡点经验()+(10)));
						cm.getPlayer().set泡点金币((cm.getPlayer().get泡点金币()+(10)));
						cm.sendOk("恭喜你领取成功。");
					}else{
						cm.sendOk("无法重复领取。");
					}
				}else{
					cm.sendOk("累计赞助金额没达到要求。");
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
















