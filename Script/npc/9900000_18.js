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
	
	
	var 推广员 ="";
	if(cm.获取推广员()!=null && cm.获取推广员()!=""){
		var 推广员 = "你的推广员QQ号是 #r"+cm.获取推广员()+"#k，";
	}
	//显示
    if (status == 0) {
		var 文本 = " #i4030000#  #i4030001#  #i4030010# #r#e< 游戏推广 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
			文本 +="  游戏推广系统，"+推广员+"你的#r推广号码为你的QQ号码#k，如果是你邀请加入本服的小伙伴，可以让他通过机器人，绑定推广人为自己，这样当被推广的小伙伴充值时，你将获取10%的奖励，你当前可以提取 #b"+cm.显示推广收益()+"#k 现金券奖励。";
			
			文本 +="\r\n\t\t\t\t   #L1##r提取推广奖励#k#l\r\n";
			
			文本 +="\r\n\r\n"+cm.显示推广关系()+"";
			

			文本 +="\r\n\r\n\r\n\r\n\r\n.";
			cm.sendSimple(文本);
    } else if (status == 1) {
        switch (selection) {
			case 1:
				if(cm.显示推广收益()>0){
					cm.getPlayer().修改现金券(cm.显示推广收益());
					cm.getPlayer().dropMessage("现金券+"+cm.显示推广收益());
					cm.说明文字("恭喜你获得 #b"+cm.显示推广收益()+"#k 现金券。");
					cm.收取所有推广收益();
				}else{
					cm.说明文字("你没有现金券可以提取哦。");
				}
				cm.dispose();
                break;
			default:
                cm.dispose();
                break;
        }
    }
}





