/********************************

ZEVMS 自由冒险岛Ver.027 
@小z QQ71447500

********************************/
var slot = Array();
var x1;
var x2;

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
	var Level = cm.会员等级();
	var Exp = cm.会员经验();
	var Time = cm.会员剩余时间();
    if (status == 0) {
		var 文本 = "  #i4030000#  #i4030001#  #i4030010# #r#e< 会员中心 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
		文本 += "\t\t #b会员等级#k : "+Level+" #b      当前经验#k : "+Exp+"\r\n";
		文本 += "\t\t #b剩余时间#k : "+Time+" #k#b天#k\r\n";
		文本 += "\r\n\t\t 会员特权; \r\n";
		if(Level == 1){
			文本 += "\t\t 狩猎经验 + #r10%#k   基础爆率 + #r20%#k \r\n";
		}else if(Level == 2){
			文本 += "\t\t 狩猎经验 + #r12%#k   基础爆率 + #r22%#k \r\n";
		}else if(Level == 3){
			文本 += "\t\t 狩猎经验 + #r14%#k   基础爆率 + #r24%#k \r\n";
		}else if(Level == 4){
			文本 += "\t\t 狩猎经验 + #r16%#k   基础爆率 + #r26%#k \r\n";
		}else if(Level == 5){
			文本 += "\t\t 狩猎经验 + #r18%#k   基础爆率 + #r28%#k \r\n";
		}else if(Level == 6){
			文本 += "\t\t 狩猎经验 + #r20%#k   基础爆率 + #r30%#k \r\n";
		}
		文本 += "\t\t 自动使用消耗品\r\n";
		
		
		if(cm.getPlayer().getRecoveryHPitemid()==0){
			文本 += "\t\t#L3##b设置HP恢复物品#k#l  ";
		}else{
			文本 += "\t\t#L3##bHP恢复品:  #v"+cm.getPlayer().getRecoveryHPitemid()+"##k#l";
		}
		
		
		if(cm.getPlayer().getRecoveryHP()==0){
			文本 += "\t#L1##b设置HP恢复线#k#l\r\n";
		}else{
			文本 += "\t#L1##bHP恢复线:#r"+cm.getPlayer().getRecoveryHP()+"#k#l\r\n";
		}
		
		if(cm.getPlayer().getRecoveryMPitemid()==0){
			文本 += "\t\t#L4##b设置MP恢复物品#k#l  ";
		}else{
			文本 += "\t\t#L4##bMP恢复品:  #v"+cm.getPlayer().getRecoveryMPitemid()+"##k#l";
		}
		
		if(cm.getPlayer().getRecoveryMP()==0){
			文本 += "\t#L2##b设置MP恢复线#k#l\r\n";
		}else{
			文本 += "\t#L2##bMP恢复线:#r"+cm.getPlayer().getRecoveryMP()+"#k#l\r\n";
		}
		
		文本 += "\r\n";
		文本 += "\t\t#L10##b重置回复设置#k#l";
		
		

		cm.sendSimple(文本);
	} else if (status == 1) {
		x1 = selection;
		if(x1 == 1){
			cm.sendGetText("设置HP回复线，低于该回复线将使用恢复品。");
		}else if(x1 == 2){
			cm.sendGetText("设置MP回复线，低于该回复线将使用恢复品。");
		}else if(x1 == 3){
			var 文本3 = "\t\t 选择你要设置的回复药品 - HP;\r\n";
			for (var i = 0; i < 96; i++) {
				if (cm.getInventory(2).getItem(i) != null) {
					var itemid = cm.getInventory(2).getItem(i).getItemId();
					if(恢复药水(itemid)){
						文本3 += "\t\t #L"+itemid+"##v"+itemid+"# #b#t"+itemid+"##l\r\n";
					}
                }
            }
			cm.sendSimple(文本3);
		}else if(x1 == 4){
			var 文本4 = "\t\t 选择你要设置的回复药品 - MP;\r\n";
			for (var i = 0; i < 96; i++) {
				if (cm.getInventory(2).getItem(i) != null) {
					var itemid = cm.getInventory(2).getItem(i).getItemId();
					if(恢复药水(itemid)){
						文本4 += "\t\t #L"+itemid+"##v"+itemid+"# #b#t"+itemid+"##l\r\n";
					}
                }
            }
			cm.sendSimple(文本4);
		}else{
			cm.getPlayer().setRecoveryHP(0);
			cm.getPlayer().setRecoveryHPitemid(0);
			cm.getPlayer().setRecoveryMP(0);
			cm.getPlayer().setRecoveryMPitemid(0);
			cm.sendOk("重置回复数据成功，请重新设置。");
			cm.dispose();
		}
	} else if (status == 2) {
		x2 = selection;
		if(x1 == 1 || x1 == 2){
			fee = cm.getText();
			if(isNaN(fee)){
				cm.sendOk("请输入正确的数字。");
				cm.dispose();
				return;
			}
			
			if(fee > 30000 || fee < 0){
				cm.sendOk("设置回复只能 #b0 - 30000#k之间。");
				cm.dispose();
				return;
			}
			
			if(x1 == 1){
				cm.getPlayer().setRecoveryHP(fee);
				cm.sendOk("设置角色受伤时生命值低于 #b"+fee+"#k 自动使用回复品。");
				cm.dispose();
			}
			
			if(x1 == 2){
				cm.getPlayer().setRecoveryMP(fee);
				cm.sendOk("设置角色使用技能时魔法值低于 #b"+fee+"#k 自动使用回复品。");
				cm.dispose();
			}
		}else if(x1 == 3 || x1 == 4){
			if(x1 == 3){
				cm.getPlayer().setRecoveryHPitemid(x2);
				cm.sendOk("设置生命回复品为 #v"+x2+"# #b#t"+x2+"##k。");
				cm.dispose();
			}
			if(x1 == 4){
				cm.getPlayer().setRecoveryMPitemid(x2);
				cm.sendOk("设置法力回复品为 #v"+x2+"# #b#t"+x2+"##k。");
				cm.dispose();
			}
		}else{
			cm.dispose();
		}
	} 
}




function 恢复药水(sk) {
	switch (sk) {
		//红色药水
		case 2000000:
		//橙色药水
		case 2000001:
		//白色药水
		case 2000002:
		//蓝色药水
		case 2000003:
		//特殊药水
		case 2000004:
		//超级药水
		case 2000005:
		//活力神水
		case 2000006:
		//西瓜
		case 2001000:
		//棒棒冰
		case 2001001:
		//红豆刨冰
		case 2001002:
		//苹果
		case 2010000:
		//烤肉
		case 2010001:
		//鸡蛋
		case 2010002:
		//橙子
		case 2010003:
		//柠檬
		case 2010004:
		//蜂蜜
		case 2010005:
		//蜂蜜罐
		case 2010006:
		//沙拉
		case 2020000:
		case 2020001:
		case 2020002:
		case 2020003:
		case 2020004:
		case 2020005:
		case 2020006:
		case 2020007:
		case 2020008:
		case 2020009:
		case 2020010:
		case 2020011:
		case 2020012:
		case 2020013:
		case 2020014:
		case 2020015:
		case 2022000:
		case 2022003:
		return true;
        }
	return false;
}





