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
		var 文本 = " #i4030000#  #i4030001#  #i4030010# #r#e< 吸怪检测 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
		
			文本+="\t\t #b地图#k :"+ cm.getPlayer().getMap().getMapName()+"\r\n";
			
			文本+="\t\t #b监测点#k :"+ cm.getPlayer().getMap().monsterCountById(9400711)+"\r\n";
			
			文本+="\t\t #b角色坐标#k : x"+  cm.getPlayer().getPosition().x+" y"+cm.getPlayer().getPosition().y+"\r\n";
			
			if(cm.判断检测()>0){
			
				文本+="\t\t#L1##b检测设置 - 1 #k:  "+ cm.获取检测("x1")+" / "+ cm.获取检测("y1")+"#l\r\n";
		
				文本+="\t\t#L2##b检测设置 - 2 #k:  "+ cm.获取检测("x2")+" / "+ cm.获取检测("y2")+"#l\r\n";
			
				文本+="\t\t#L3##b检测设置 - 3 #k:  "+ cm.获取检测("x3")+" / "+ cm.获取检测("y3")+"#l\r\n";
				文本+="\r\n\t\t#L11##b载入地图检测#l\r\n";
				文本+="\t\t#L10##b初始化地图检测#l\r\n";
				
				文本+="\r\n";
				文本+="\t\t #r实际坐标 - "+cm.getPlayer().getMap().getAllMonsters()+"";
			
			}else{
				文本+="\t\t#L0##b开启地图吸怪设置#l\r\n";
			}
			
			cm.sendSimple(文本);
    } else if (status == 1) {
        switch (selection) {
			case 11:
				cm.载入检测();
				cm.sendOk("载入成功。");
				cm.dispose();
                break;
			case 10:
				cm.清除当前地图检测();
				cm.sendOk("初始化成功，请重新设置。");
				cm.dispose();
                break;
			case 0:
				cm.初始化检测();
				cm.sendOk("初始化成功，请设置检测数据。");
				cm.dispose();
                break;
			case 1:
				cm.设置检测1();
				cm.sendOk("检测1坐标位置配置成功。");
				cm.dispose();
                break;
			case 2:
				cm.设置检测2();
				cm.sendOk("检测2坐标位置配置成功。");
				cm.dispose();
                break;
			case 3:
				cm.设置检测3();
				cm.sendOk("检测3坐标位置配置成功。");
				cm.dispose();
                break;
			default:
                cm.dispose();
                break;
        }
    }
}
















