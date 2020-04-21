
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
	if(weekday == 1 && hour > 17){
		cm.sendOk("天梯赛结算时间，将无法做每日任务。");
		cm.dispose();
        return
	}
	
	var 活跃总积分 = 35;
	
	if (status <= 0) {
       var selStr = " #i4030000#  #i4030001#  #i4030010# #r#e< 每日必做 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
		
		selStr += "这里有记录着你每日的福利和一些任务，如果你能满足条件的完成他们，你将会获取丰厚的奖励和活跃天梯积分，#r当天梯赛处于领奖阶段，将不能再做每日任务。#k#k\r\n";
		
		selStr += "\t\t #L200##b口令福利#k           进度 (1 / "+cm.getbosslog("口令奖励活跃积分")+")#l\r\n";
		/**
		<每日收集>2*5=10 ·
		**/
		selStr += "\t\t #L20##b每日收集#k           进度 (5 / "+cm.getbosslog("每日任务_跑环_收集物品完成")+")#l\r\n";
		
		/**
		<每日拜访>
		**/
		//selStr += "\t\t #L21##b每日拜访#k           进度 (5 / "+cm.getbosslog("每日任务_跑环_拜访岛民完成")+")#l\r\n";
		
		/**
		<每日签到> 1  ·
		**/
		selStr += "\t\t #L3##b每日签到奖励#k       ";
		if(cm.getbosslog("每日签到")<=0){
			selStr += "进度 (1 / 0)#l\r\n";
		}else{
			selStr += "进度 (1 / 1)#l\r\n";
		}
		
		/**
		<在线奖励> 1 1 1 2 2 3  = 10 ·
		**/
		selStr += "\t\t #L1##b在线时常奖励#k       ";
		if(cm.getbosslog("在线奖励6")>0){
			selStr += "进度 (6 / 6)#l\r\n";
		}else if(cm.getbosslog("在线奖励5")>0){
			selStr += "进度 (6 / 5)#l\r\n";
		}else if(cm.getbosslog("在线奖励4")>0){
			selStr += "进度 (6 / 4)#l\r\n";
		}else if(cm.getbosslog("在线奖励3")>0){
			selStr += "进度 (6 / 3)#l\r\n";
		}else if(cm.getbosslog("在线奖励2")>0){
			selStr += "进度 (6 / 2)#l\r\n";
		}else if(cm.getbosslog("在线奖励1")>0){
			selStr += "进度 (6 / 1)#l\r\n";
		}else{
			selStr += "进度 (6 / 0)#l\r\n";
		}
		
		/**
		<日常狩猎>6  1 1 1 = 3 ·
		**/
		//selStr += "\t\t #L2##b日常狩猎奖励#k       进度 (3 / "+cm.getbosslog("日常狩猎")+")#l\r\n";
		
		
		/**
		<金银岛的主城> 1 ·
		**/
		selStr += "\t\t #L10##b金银岛的主城#k       进度 (1 / "+cm.getbosslog("每日任务_拜访金银岛主城完成")+")#l\r\n";
		
		
		
		/**
		<天空之城的航班> 1 ·
		**/
		selStr += "\t\t #L11##b天空之城的航班#k     进度 (1 / "+cm.getbosslog("每日任务_坐密林和天空的航班完成")+")#l\r\n";
		
		/**
		<友好的交流>1 ·
		**/
		selStr += "\t\t #L12##b友好的交流#k         进度 (1 / "+cm.getbosslog("每日任务_添加其他角色人气完成")+")#l\r\n";

		
		/**
		<光顾商店>1 ·
		**/	
		selStr += "\t\t #L13##b光顾商店#k           进度 (1 / "+cm.getbosslog("每日任务_游戏商城购买道具完成")+")#l\r\n";
			
		/**
		<问候职业导师> 3 ·
		**/		
		selStr += "\t\t #L14##b问候职业导师#k       进度 (1 / "+cm.getbosslog("每日任务_问候职业导师完成")+")#l\r\n";
		
		/**
		<地下的沼泽>1 ·
		**/	
		selStr += "\t\t #L15##b地下的沼泽#k         进度 (1 / "+cm.getbosslog("每日任务_通关废弃副本完成")+")#l\r\n";
		
		/**
		<工艺能手>1·
		**/
		selStr += "\t\t #L16##b熟练的工艺#k         进度 (1 / "+cm.getbosslog("每日任务_合成矿石完成")+")#l\r\n";
		
		
		/**
		<装备强化>1·
		**/
		selStr += "\t\t #L17##b装备强化#k           进度 (1 / "+cm.getbosslog("每日任务_装备打孔完成")+")#l\r\n";
		
		
		/**
		<花点小钱>1 ·
		**/
		selStr += "\t\t #L18##b花点小钱#k           进度 (1 / "+cm.getbosslog("每日任务_花点小钱完成")+")#l\r\n";
		
		
		/**
		<修养生息>1 ·
		**/
		//selStr += "\t\t #L19##b修养生息#k           进度 (1 / "+cm.getbosslog("每日任务_修养生息完成")+")#l\r\n";
		
		/**
		<屠魔能手>
		**/
		//selStr += "\t\t #L20##b屠魔能手#k           进度 (1 / 0)#l\r\n";
		
		
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
			//每日拜访
			case 21:
				var 等级 = 999999;
				if(cm.getLevel()>=等级){
					cm.dispose();
					cm.openNpc(9900000,421);
				}else{
					cm.sendOk("你需要等级达到 "+等级+" 级才可以进行该任务。");
					cm.dispose();
				}
                break;
			//每日收集
			case 20:
				var 等级 = 49;
				if(cm.getLevel()>=等级){
					cm.dispose();
					cm.openNpc(9900000,420);
				}else{
					cm.sendOk("你需要等级达到 "+等级+" 级才可以进行该任务。");
					cm.dispose();
				}
                break;
			//修养生息
			case 19:
				var 等级 = 10;
				if(cm.getLevel()>=等级){
					cm.dispose();
					cm.openNpc(9900000,419);
				}else{
					cm.sendOk("你需要等级达到 "+等级+" 级才可以进行该任务。");
					cm.dispose();
				}
                break;
			//花点小钱
			case 18:
				var 等级 = 1;
				if(cm.getLevel()>=等级){
					cm.dispose();
					cm.openNpc(9900000,418);
				}else{
					cm.sendOk("你需要等级达到 "+等级+" 级才可以进行该任务。");
					cm.dispose();
				}
                break;
			//装备强化
			case 17:
				var 等级 = 10;
				if(cm.getLevel()>=等级){
					cm.dispose();
					cm.openNpc(9900000,417);
				}else{
					cm.sendOk("你需要等级达到 "+等级+" 级才可以进行该任务。");
					cm.dispose();
				}
                break;
			//工艺能手
			case 16:
				var 等级 = 30;
				if(cm.getLevel()>=等级){
					cm.dispose();
					cm.openNpc(9900000,416);
				}else{
					cm.sendOk("你需要等级达到 "+等级+" 级才可以进行该任务。");
					cm.dispose();
				}
                break;
			//金银岛的主城
			case 10:
				var 等级 = 30;
				if(cm.getLevel()>=等级){
					cm.dispose();
					cm.openNpc(9900000,410);
				}else{
					cm.sendOk("你需要等级达到 "+等级+" 级才可以进行该任务。");
					cm.dispose();
				}
                break;
			//天空之城的航班
			case 11:
                var 等级 = 30;
				if(cm.getLevel()>=等级){
					cm.dispose();
					cm.openNpc(9900000,411);
				}else{
					cm.sendOk("你需要等级达到 "+等级+" 级才可以进行该任务。");
					cm.dispose();
				}
                break;
			//友好的交流
			case 12:
                var 等级 = 30;
				if(cm.getLevel()>=等级){
					cm.dispose();
					cm.openNpc(9900000,412);
				}else{
					cm.sendOk("你需要等级达到 "+等级+" 级才可以进行该任务。");
					cm.dispose();
				}
                break;
			//光顾商店
			case 13:
                var 等级 = 30;
				if(cm.getLevel()>=等级){
					cm.dispose();
					cm.openNpc(9900000,413);
				}else{
					cm.sendOk("你需要等级达到 "+等级+" 级才可以进行该任务。");
					cm.dispose();
				}
                break;
			//问候职业导师
			case 14:
                var 等级 = 70;
				if(cm.getLevel()>=等级){
					cm.dispose();
					cm.openNpc(9900000,414);
				}else{
					cm.sendOk("你需要等级达到 "+等级+" 级才可以进行该任务。");
					cm.dispose();
				}
                break;
			//地下的沼泽
			case 15:
                var 等级 = 20;
				if(cm.getLevel()>=等级){
					cm.dispose();
					cm.openNpc(9900000,415);
				}else{
					cm.sendOk("你需要等级达到 "+等级+" 级才可以进行该任务。");
					cm.dispose();
				}
                break;
			//每日口令
			case 200:
                var 等级 = 10;
				if(cm.getLevel()>=等级){
					cm.dispose();
					cm.openNpc(9900000,204);
				}else{
					cm.sendOk("你需要等级达到 "+等级+" 级才可以进行该任务。");
					cm.dispose();
				}
                break;
			//每日签到
			case 3:
                var 等级 = 10;
				if(cm.getLevel()>=等级){
					cm.dispose();
					cm.openNpc(9900000,43);
				}else{
					cm.sendOk("你需要等级达到 "+等级+" 级才可以进行该任务。");
					cm.dispose();
				}
                break;
			//在线奖励
            case 1:
                var 等级 = 10;
				if(cm.getLevel()>=等级){
					cm.dispose();
					cm.openNpc(9900000,41);
				}else{
					cm.sendOk("你需要等级达到 "+等级+" 级才可以进行该任务。");
					cm.dispose();
				}
                break;
			//日常狩猎
			case 2:
                var 等级 = 5;
				if(cm.getLevel()>=等级){
					cm.dispose();
					cm.openNpc(9900000,42);
				}else{
					cm.sendOk("你需要等级达到 "+等级+" 级才可以进行该任务。");
					cm.dispose();
				}
                break;
			default:
                cm.dispose();
                break;
			
        }
    }
}