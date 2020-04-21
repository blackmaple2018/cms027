
var status = 0;
function start() {
    status = -1;
    action(1, 0, 0)
}

function action(mode, type, selection) {
    if (status <= 0 && mode <= 0) {
        cm.dispose();
        return
    }
    if (mode == 1) {
        status++
    } else {
        status--
    }	 
	if (status <= 0) {
        var selStr = " #i4030000#  #i4030001#  #i4030010# #r#e< 家族组织 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
		
		selStr += "你可以创建或者加入冒险家族组织。加入家族后会获取特殊的增益，随着家族经验等级的提高，增益也会越来越好。退出家族后，需要第二天才可以重新加入。#k\r\n";
		//家族编号大于0代表有家族
		if(cm.判断家族() > 0){
			selStr += " \r\n";
			selStr += "    #b家族等级#k : "+cm.家族名字(cm.判断家族())+"#k\r\n";
			selStr += "    #b家族等级#k : Lv."+cm.家族等级()+"#k\r\n";
			selStr += "    #b家族经验#k : "+cm.家族经验()+"#k\r\n";
			selStr += "    #b家族人数#k : "+cm.家族现有人数()+" #k\r\n";
			selStr += "    #b家族在线#k : "+cm.家族在线人数()+"#k\r\n";
			selStr += " \r\n";
			selStr += "   #L3##b查看成员家族#k#l\r\n";
			selStr += "   #L400##b退出当前家族#k#l\r\n";
		}
		//这些事无家族的操作
		if(cm.判断家族()==0){
			selStr += "   #L1##b创建家族#k#l\r\n";
			selStr += "   #L2##b加入家族#k#l\r\n";
		}
		
		
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
			//创建家族
            case 1:
                cm.dispose();
                cm.openNpc(9900000,61);
                break;
			//加入家族
			case 2:
				if(cm.getbosslog("退出家族")==0){
					cm.dispose();
					cm.openNpc(9900000,62);
				}else{
					cm.sendOk("你今天已经退出过家族组织，无法加入其他家族组织，请明天再来尝试加入。");
					cm.dispose();
				}
                break;
			//家族列表
			case 3:
                cm.dispose();
                cm.openNpc(9900000,63);
                break;
			//退出家族
			case 400:
				cm.setbosslog("退出家族");
				cm.家族通知信息("玩家 "+cm.玩家名字()+" 退出了家族。");
                cm.设置家族编号(0);
				cm.设置家族地位(0);
				cm.sendOk("成功退出当前家族。");
				cm.dispose();
                break;	
				
				
				
				
				
				
				
				
				
			
        }
    }
}