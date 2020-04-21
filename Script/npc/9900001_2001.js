/********************************

ZEVMS 自由冒险岛Ver.027 
@小z QQ71447500

********************************/

function start() {
    status = -1;
    action(1, 0, 0);
}
var slot = Array();
var ls = 1;
var 金币 = 100000;
var x2;
var 力量2;
var 敏捷2;
var 智力2;
var 运气2;
var 攻击力2;
var 魔法力2;
var 物理防御2;
var 魔法防御2;
var 生命2;
var 魔力2;
var 位置;
var 装备;
var 锻造等级;

function 锻造上限(x) {
	
	var 值 = x / 200;
	if( x > 2000){
		值 = 10 + ((400 + (x - 2000)) / 400);
	}
	if( x > 4000){
		值 = 16 + ((1000 + (x - 4000)) / 1000);
	}
	if( 值 >= 20){
		值 = 20;
	}
	
	return 值.toFixed(0);
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
	
	var cid = cm.getPlayer().查看目标();
	var exp = cm.getPlayer().获取锻造信息("forgingExp",cid);
	
	if(exp < 1000){
		cm.sendSimple("你锻造熟练度不足1000，无法进行查看。");
		cm.dispose();
        return;
	}
	
    if (status == 0) {
		var 文本 = "*—— #b[ #r装备锻造#b ]#k ———————————————————————————————————————————*\r\n\r\n";
			文本 +="\t锻造详细说明请进入群主空间日志查看。\r\n";
			文本 +="\t选择你要#r锻造血量#k的装备，当前最高锻造至 "+锻造上限(exp)+" 级;\r\n";
		for (var i = 0; i < 96; i++) {
			if (cm.getInventory(ls).getItem(i) != null) {
				var itemid = cm.getInventory(ls).getItem(i).getItemId();
				if(!cm.isCash(cm.getInventory(ls).getItem(i).getItemId())){
					var name = cm.getItemName(itemid);
					if(i < 10){
						文本 += "\t#L" + i + "# 0"+i+")  #v" + itemid + "# #b" + name + "#k";
						文本 += "锻造  #r"+cm.判断锻造等级(i)+"#k#l\r\n";
					}else{
						文本 += "\t#L" + i + "# "+i+")  #v" + itemid + "# #b" + name + "#k";
						文本 += "锻造  #r"+cm.判断锻造等级(i)+"#k#l\r\n";
					}
				}
			}
			slot.push(i);
		}
		cm.sendSimple(文本);
	} else if (status == 1) {
		位置 = selection;
		var b = "";
		装备 = cm.getInventory(ls).getItem(slot[selection]).getItemId();
		b += "#b \t#v"+装备+"# #b#t"+装备+"# #k\r\n";
		
		锻造等级 = cm.getInventory(ls).getItem(slot[selection]).getDzlevel();
		b += "#b \t锻造等级:#r " + 锻造等级 + "#k + #b1#k\r\n";
		
        生命 = cm.getInventory(ls).getItem(slot[selection]).getHp();
		if (生命 > 0) {
			生命2 = (生命+(生命/100*生命法力加成2(exp))).toFixed(0);
			if(生命==生命2){
				生命2 = 生命+1;
			}
            b += "#b \t生命值:#r " + 生命 + "#k + #b"+生命法力加成2(exp)+"%#k ("+(生命2)+")\r\n";
        }else{
			b += "#r \t预估生命值:#r " + 生命法力加成(exp) + "#k\r\n";
		}
		
        cm.sendYesNo(" \t请确定要增幅以下装备吗？\r\n\r\n"+b);
		
    } else if (status == 2) {
		cm.sendYesNo(" \t需要以下材料来锻造装备\r\n\r\n \t#v4031039# #b金币#k x "+金币*(锻造等级+1)+" \r\n \t#v4021008# #b#t4021008##k x "+(锻造等级+1)+"");
	} else if (status == 3) {
		
		if(锻造等级 >= 锻造上限(exp)){
			cm.sendOk("无法锻造到更高等级。\r\n");
			cm.dispose();
			return;
		}
		
		if (cm.getMeso() < 金币*(锻造等级+1)||!cm.haveItem(4021008,(锻造等级+1))) {
			cm.sendOk("材料或者金币不够，请确认。\r\n");
			cm.dispose();
			return;
		}
		var 输出属性 = "";
		cm.gainMeso(-金币*(锻造等级+1));
		cm.gainItem(4021008,-(锻造等级+1));
		
		if (生命 > 0) {
			cm.加属性("最大生命值",slot[位置],生命法力加成(exp));
		}else{
			cm.加属性("最大生命值",slot[位置],生命法力加成(exp));
		}
		if(cm.getInventory(ls).getItem(slot[位置]).getHp() > 0 ){
			输出属性+="\r\n最大生命值  #b"+生命+" + #r"+生命法力加成(exp)+"#k";
		}
		
		cm.加锻造等级(slot[位置],1);
		cm.sendOk("恭喜你，#v"+装备+"# #b#t"+装备+"# #k锻造属性成功：\r\n锻造等级 #b"+锻造等级+" + #r1#k"+输出属性);
		cm.dispose();
	} 
}



function 锻造花费(x) {
	
	var 值 = 0;
	
	if(x>=0 && x<5){
		值 += 5000 * x;
	}else if(x>=5 && x<10){
		值 += 7500 * x;
	}else if(x>=10 && x<15){
		值 += 10000 * x;
	}else if(x>=15 && x<20){
		值 += 15000 * x;
	}

	return 值.toFixed(0);
}


function 随机() {
	var 值 = Math.floor(Math.random() * 100);
	return 值;
}


function 随机数值(x) {
	var 值 = Math.floor(Math.random() * x)+1;
	return 值;
}

function 生命法力加成(x) {
	var 值 =0;
	值 = (5 + ((x - 500) / 400));
	if(值>20){
		值=20;
	}
	return 值.toFixed(0);
}

function 生命法力加成2(x) {
	var 值 =0;
	值 = (5 + ((x - 500) / 400));
	if(值>20){
		值=20;
	}
	return 值.toFixed(2);
}