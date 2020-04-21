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
var 金币 = 150000;
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
	
	if(exp < 500){
		cm.sendSimple("你锻造熟练度不足500，无法进行查看。");
		cm.dispose();
        return;
	}
	
    if (status == 0) {
		var 文本 = "*—— #b[ #r装备锻造#b ]#k ———————————————————————————————————————————*\r\n\r\n";
			文本 +="\t锻造详细说明请进入群主空间日志查看。\r\n";
			文本 +="\t选择你要#r锻造#k的装备，当前最高锻造至 "+锻造上限(exp)+" 级;\r\n";
		for (var i = 0; i < 96; i++) {
			if (cm.getInventory(ls).getItem(i) != null) {
				var itemid = cm.getInventory(ls).getItem(i).getItemId();
				if(!cm.isCash(cm.getInventory(ls).getItem(i).getItemId())){
					if(cm.getInventory(ls).getItem(i).getItemId()!=1112002){
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
			}
			slot.push(i);
		}
		cm.sendSimple(文本);
	} else if (status == 1) {
		if(selection!=1){
			cm.sendOk("请将装备放置在装备栏第一格，再进行增幅锻造。");
			cm.dispose();
			return;
		}
		位置 = selection;
		var b = "";
		装备 = cm.getInventory(ls).getItem(slot[selection]).getItemId();
		b += "#b \t#v"+装备+"# #b#t"+装备+"# #k\r\n";
		
		锻造等级 = cm.getInventory(ls).getItem(slot[selection]).getDzlevel();
		b += "#b \t锻造等级:#r " + 锻造等级 + "#k + #b1#k\r\n";
		
        力量 = cm.getInventory(ls).getItem(slot[selection]).getStr();
		if (力量 > 0) {
			力量2 = (力量+(力量/100*四属性加成2(exp))).toFixed(0);
			if(力量==力量2){
				力量2 = 力量+1;
			}
            b += "#b \t力量:#r " + 力量 + "#k + #b"+四属性加成2(exp)+"%#k ("+(力量2)+")\r\n";
        }else{
			b += "#r \t预估力量:#r " + 四属性加成(exp) + "#k\r\n";
		}
		
        敏捷 = cm.getInventory(ls).getItem(slot[selection]).getDex();
		if (敏捷 > 0) {
			敏捷2 = (敏捷+(敏捷/100*四属性加成2(exp))).toFixed(0);
			if(敏捷==敏捷2){
				敏捷2 = 敏捷+1;
			}
            b += "#b \t敏捷:#r " + 敏捷 + "#k + #b"+四属性加成2(exp)+"%#k ("+(敏捷2)+")\r\n";
        }else{
			b += "#r \t预估敏捷:#r " + 四属性加成(exp) + "#k\r\n";
		}
		
        智力 = cm.getInventory(ls).getItem(slot[selection]).getInt();
		if (智力 > 0) {
			智力2 = (智力+(智力/100*四属性加成2(exp))).toFixed(0);
			if(智力==智力2){
				智力2 = 智力+1;
			}
            b += "#b \t智力:#r " + 智力 + "#k + #b"+四属性加成2(exp)+"#k ("+(智力2)+")\r\n";
        }else{
			b += "#r \t预估智力:#r " + 四属性加成(exp) + "#k\r\n";
		}
		
        运气 = cm.getInventory(ls).getItem(slot[selection]).getLuk();
		if (运气 > 0) {
			运气2 = (运气+(运气/100*四属性加成2(exp))).toFixed(0);
			if(运气==运气2){
				运气2 = 运气+1;
			}
            b += "#b \t运气:#r " + 运气 + "#k + #b"+四属性加成2(exp)+"#k ("+(运气2)+")\r\n";
        }else{
			b += "#r \t预估运气:#r " + 四属性加成(exp) + "#k\r\n";
		}
		
        攻击力 = cm.getInventory(ls).getItem(slot[selection]).getWatk();
		if (攻击力 > 0) {
			攻击力2 = (攻击力+(攻击力/100*攻击加成2(exp))).toFixed(0);
			if(攻击力==攻击力2){
				攻击力2 = 攻击力+1;
			}
            b += "#b \t攻击力:#r " + 攻击力 + "#k + #b"+攻击加成2(exp)+"%#k ("+(攻击力2)+")\r\n";
        }else{
			b += "#r \t预估攻击力:#r " + 攻击加成(exp) + "#k\r\n";
		}
		
        魔法力 = cm.getInventory(ls).getItem(slot[selection]).getMatk();
		if (魔法力 > 0) {
			魔法力2 = (魔法力+(魔法力/100*攻击加成2(exp)*5)).toFixed(0);
			if(魔法力==魔法力2){
				魔法力2 = 魔法力+1;
			}
            b += "#b \t魔法力:#r " + 魔法力 + "#k + #b"+(攻击加成2(exp)*5).toFixed(2)+"%#k ("+(魔法力2)+")\r\n";
        }else{
			b += "#r \t预估魔法力:#r " + 攻击加成(exp) + "#k\r\n";
		}
		
        物理防御 = cm.getInventory(ls).getItem(slot[selection]).getWdef();
		if (物理防御 > 0) {
			物理防御2 = (物理防御+(物理防御/100*防御加成2(exp))).toFixed(0);
			if(物理防御==物理防御2){
				物理防御2 = 物理防御+1;
			}
            b += "#b \t防御:#r " + 物理防御 + "#k + #b"+防御加成2(exp)+"%#k ("+物理防御2+")\r\n";
        }else{
			b += "#r \t预估防御:#r " + 防御加成(exp) + "#k\r\n";
		}
		
        魔法防御 = cm.getInventory(ls).getItem(slot[selection]).getMdef();
		if (魔法防御 > 0) {
			魔法防御2 = (魔法防御+(魔法防御/100*防御加成2(exp))).toFixed(0);
			if(魔法防御==魔法防御2){
				魔法防御2 = 魔法防御+1;
			}
            b += "#b \t魔抗:#r " + 魔法防御 + "#k + #b"+防御加成2(exp)+"%#k ("+(魔法防御2)+")\r\n";
        }else{
			b += "#r \t预估魔抗:#r " + 防御加成(exp) + "#k\r\n";
		}
		
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
		
        魔力 = cm.getInventory(ls).getItem(slot[selection]).getMp();
		if (魔力 > 0) {
			魔力2 = (魔力+(魔力/100*生命法力加成2(exp))).toFixed(0);
			if(魔力==魔力2){
				魔力2 = 魔力+1;
			}
            b += "#b \t法力值:#r " + 魔力 + "#k + #b"+生命法力加成2(exp)+"%#k ("+(魔力2)+")\r\n";
        }else{
			b += "#r \t预估法力值:#r " + 生命法力加成(exp) + "#k\r\n";
		}
		
        cm.sendYesNo(" \t请确定要增幅以下装备吗？\r\n\r\n"+b);
		
    } else if (status == 2) {
		cm.sendYesNo(" \t需要以下材料来锻造装备\r\n\r\n \t#v4031039# #b金币#k x "+金币*(锻造等级+1)+" \r\n \t#v4021007# #b#t4021007##k x "+(锻造等级+1)+"\r\n\t#v4021001# #b#t4021001##k x "+(锻造等级+1)+"");
	} else if (status == 3) {
		
		if(锻造等级 >= 锻造上限(exp)){
			cm.sendOk("无法锻造到更高等级。\r\n");
			cm.dispose();
			return;
		}
		
		if (cm.getMeso() < 金币*(锻造等级+1)||!cm.haveItem(4021007,(锻造等级+1)) || !cm.haveItem(4021001,(锻造等级+1))) {
			cm.sendOk("材料或者金币不够，请确认。\r\n");
			cm.dispose();
			return;
		}
		var 输出属性 = "";
		cm.gainMeso(-金币*(锻造等级+1));
		cm.gainItem(4021007,-(锻造等级+1));
		cm.gainItem(4021001,-(锻造等级+1));
		if(力量 > 0){
			cm.加属性("力量",slot[位置],四属性加成(exp));
		} else if( Math.floor(Math.random() * 100) > 98){
			cm.加属性("力量",slot[位置],四属性加成(exp));
		}
		if(cm.getInventory(ls).getItem(位置).getStr() > 0 ){
			输出属性+="\r\n力量  #b"+力量+" + #r"+四属性加成(exp)+"#k";
		}
		
		if(敏捷 > 0){
			cm.加属性("敏捷",slot[位置],四属性加成(exp));
		}else if( Math.floor(Math.random() * 100) > 98){
			cm.加属性("敏捷",slot[位置],四属性加成(exp));
		}
		if(cm.getInventory(ls).getItem(slot[位置]).getDex() > 0 ){
			输出属性+="\r\n敏捷  #b"+敏捷+" + #r"+四属性加成(exp)+"#k";
		}
		
		if(智力 > 0){
			cm.加属性("智力",slot[位置],四属性加成(exp));
		}else if( Math.floor(Math.random() * 100) > 98){
			cm.加属性("智力",slot[位置],四属性加成(exp));
		}
		if(cm.getInventory(ls).getItem(slot[位置]).getInt() > 0 ){
			输出属性+="\r\n智力  #b"+智力+" + #r"+四属性加成(exp)+"#k";
		}
		
		if(运气 > 0){
			cm.加属性("运气",slot[位置],四属性加成(exp));
		}else if( Math.floor(Math.random() * 100) > 98){
			cm.加属性("运气",slot[位置],四属性加成(exp));
		}
		if(cm.getInventory(ls).getItem(slot[位置]).getLuk() > 0 ){
			输出属性+="\r\n运气  #b"+运气+" + #r"+四属性加成(exp)+"#k";
		}
		
		if (生命 > 0) {
			cm.加属性("最大生命值",slot[位置],生命法力加成(exp));
		}else if( Math.floor(Math.random() * 100) > 90){
			cm.加属性("最大生命值",slot[位置],生命法力加成(exp));
		}
		if(cm.getInventory(ls).getItem(slot[位置]).getHp() > 0 ){
			输出属性+="\r\n最大生命值  #b"+生命+" + #r"+生命法力加成(exp)+"#k";
		}
		
		if (魔力 > 0) {
			cm.加属性("最大法力值",slot[位置],生命法力加成(exp));
		}else if( Math.floor(Math.random() * 100) > 90){
			cm.加属性("最大法力值",slot[位置],生命法力加成(exp));
		}
		if(cm.getInventory(ls).getItem(slot[位置]).getMp() > 0 ){
			输出属性+="\r\n最大法力值  #b"+魔力+" + #r"+生命法力加成(exp)+"#k";
		}
		
		if(魔法防御 > 0){
			cm.加属性("魔法防御",slot[位置],防御加成(exp));
		}else if( Math.floor(Math.random() * 100) > 80){
			cm.加属性("魔法防御",slot[位置],防御加成(exp));
		}
		if(cm.getInventory(ls).getItem(slot[位置]).getMdef() > 0 ){
			输出属性+="\r\n魔法防御  #b"+魔法防御+" + #r"+防御加成(exp)+"#k";
		}
		
		if(物理防御 > 0){
			cm.加属性("物理防御",slot[位置],防御加成(exp));
		}else if( Math.floor(Math.random() * 100) > 80){
			cm.加属性("物理防御",slot[位置],防御加成(exp));
		}
		if(cm.getInventory(ls).getItem(slot[位置]).getWdef() > 0 ){
			输出属性+="\r\n物理防御  #b"+物理防御+" + #r"+防御加成(exp)+"#k";
		}
		
		if(攻击力 > 0){
			cm.加属性("物理攻击",slot[位置],攻击加成(exp));
		}else if( Math.floor(Math.random() * 100) > 95){
			cm.加属性("物理攻击",slot[位置],攻击加成(exp));
		}
		if(cm.getInventory(ls).getItem(slot[位置]).getWatk() > 0 ){
			输出属性+="\r\n攻击力  #b"+攻击力+" + #r"+攻击加成(exp)+"#k";
		}
		
		if(魔法力 > 0){
			cm.加属性("魔法攻击",slot[位置],攻击加成(exp)*5);
		}else if( Math.floor(Math.random() * 100) > 85){
			cm.加属性("魔法攻击",slot[位置],攻击加成(exp)*5);
		}
		if(cm.getInventory(ls).getItem(slot[位置]).getMatk() >0 ){
			输出属性+="\r\n魔法力  #b"+魔法力+"#k + #r"+攻击加成(exp)*5+"#k";
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

function 四属性加成(x) {
	var 值 =0;
	值 = (1 + ((x - 500) / 300));
	if(值>5){
		值=5;
	}
	return 值.toFixed(0);
}

function 四属性加成2(x) {
	var 值 =0;
	值 = (1 + ((x - 500) / 300));
	if(值>5){
		值=5;
	}
	return 值.toFixed(2);
}

function 攻击加成2(x) {
	var 值 =0;
	值 = (1 + ((x - 500) / 500));
	if(值>5){
		值=5;
	}
	return 值.toFixed(2);
}

function 攻击加成(x) {
	var 值 =0;
	值 = (1 + ((x - 500) / 500));
	if(值>5){
		值=5;
	}
	return 值.toFixed(0);
}

function 防御加成(x) {
	var 值 =0;
	值 = (1 + ((x - 500) / 200));
	if(值>10){
		值=10;
	}
	return 值.toFixed(0);
}

function 防御加成2(x) {
	var 值 =0;
	值 = (1 + ((x - 500) / 200));
	if(值>10){
		值=10;
	}
	return 值.toFixed(2);
}

function 生命法力加成(x) {
	var 值 =0;
	值 = (2 + ((x - 500) / 500));
	if(值>10){
		值=10;
	}
	return 值.toFixed(0);
}

function 生命法力加成2(x) {
	var 值 =0;
	值 = (2 + ((x - 500) / 500));
	if(值>10){
		值=10;
	}
	return 值.toFixed(2);
}