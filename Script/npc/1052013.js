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
		var 文本 = "  #i4030000#  #i4030001#  #i4030010# #r#e< 网    吧 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";

			
			
			
			文本 += "    网吧联赛，每个玩家以个人的形式参加赛事，进入任意网吧地图击杀怪物，获取积分活动结束后即可根据排名获取对应奖励。\r\n";
			文本 += "\t\t#r活动时间#k:周六周日 20：00 -20：30\r\n";
			
			文本 += "\t\t#b积分#k: 0   #b当前排名#k: 0\r\n";
			
			文本 += "\t\t#L1##b呼吸寺院#k#l\r\n";
			
			文本 += "\t\t#L2##b猴子森林#k#l\r\n";
			
			文本 += "\t\t#L3##b火焰山#k#l\r\n";
			
			文本 += "\t\t#L4##b危险的蚂蚁洞穴#k#l\r\n";
			
			
			
			文本 +="\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n.";
			cm.sendSimple(文本);
    } else if (status == 1) {
        switch (selection) {
			/*case 1:
				cm.dispose();
                cm.warp(190000000,4);
                break;
			case 2:
				cm.dispose();
                cm.warp(191000000,15);
                break;
			case 3:
				cm.dispose();
                cm.warp(192000000,1);
                break;
			case 4:
				cm.dispose();
                cm.warp(195000000);
                break;*/
			default:
				cm.sendSimple("未开放。");
                cm.dispose();
                break;
        }
    }
}
