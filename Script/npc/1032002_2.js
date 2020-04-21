var slot = Array();
var ls = 1;
var 附魔材料 = 4006003;
var 附魔金币 = 5000;
var ID;
var LV = 0;
var ZZ = 0;
function start() {
    status = -1;
    action(1, 0, 0);
}

function random(min,max){
    return (Math.random()*(max-min) + min).toFixed(2);    
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
	
	/*if(cm.玩家名字()!="小z"){
		cm.sendOk("维护中。");
		cm.dispose();
        return;
	}*/

	//首个页面
    if (status <= 0) {
		var 文本 = "*—— #b[ #r装备附魔#b ]#k ———————————————————————————————————————————*\r\n\r\n";
			文本 +="\t需要材料#v"+附魔材料+"# #t"+附魔材料+"# x #b1#k   #v4031039# 金币 x #b"+附魔金币+"#k\r\n";
			文本 +="\t选择你要#r附魔#k的装备;\r\n";
		for (var i = 0; i < 96; i++) {
			if (cm.getInventory(ls).getItem(i) != null) {
				var itemid = cm.getInventory(ls).getItem(i).getItemId();
				if(!cm.isCash(cm.getInventory(ls).getItem(i).getItemId())){
					var name = cm.getItemName(itemid);
					if(i < 10){
						文本 += "\t#L" + i + "# 0"+i+")  #v" + itemid + "# #b" + name + "#k";
						for (var j = 18 - (name.getBytes().length/3*2); j > 0; j--) {
                            文本 += " ";
                        }
						文本 += "孔 x #r"+cm.查询孔数(i)+"#k|空 x #r"+cm.查询附魔(i)+"#k#l\r\n";
					}else{
						文本 += "\t#L" + i + "# "+i+")  #v" + itemid + "# #b" + name + "#k";
						for (var j = 18 - (name.getBytes().length/3*2); j > 0; j--) {
                            文本 += " ";
                        }
						文本 += "孔 x #r"+cm.查询孔数(i)+"#k|空 x #r"+cm.查询附魔(i)+"#k#l\r\n";
					}
				}
			}
			slot.push(i);
		}
		cm.sendSimple(文本);
    } else if (status == 1) {
		
		//判断附魔的装备有没有空的孔进行附魔
		if(cm.查询附魔(slot[selection]) <= 0){
			cm.sendOk("该装备没有附魔的条件。");
			cm.dispose();
			return;
		}
		
		//判断有没有附魔材料
		if (!cm.haveItem(附魔材料, 1)) {
			cm.sendOk("你没有 #i"+附魔材料+"# #b#t"+附魔材料+"##k x 1 ，我不能为你服务。");
			cm.dispose();
			return;
		}
		
		//判断有没有附魔费用
		if(cm.getMeso()<附魔金币){
			cm.sendOk("你没有 "+附魔金币+" 金币，我不能为你服务。");
			cm.dispose();
			return;
		}
		
		//第一次随机附魔区域
        var 第一阶段随机 = Math.floor(Math.random() * 99)+1;
		
		//第二次随机附魔区域
		var 第二阶段随机 = Math.floor(Math.random() * 99)+1;
		
        //装备代码
        ID = cm.getInventory(ls).getItem(slot[selection]).getItemId();
		
		//装备等级
		LV = cm.装备等级(ID);
		
		//装备位置
	    ZZ = slot[selection];
		
		//收取金币
		cm.gainMeso(-附魔金币);
		
		//收取材料
		cm.gainItem(附魔材料,-1);
		
		//附魔随机区域
		if (数值范围(0,10,第一阶段随机)) {
			
			if(数值范围(1,50,第二阶段随机)){
				狩猎(ZZ);
			}else{
				坐享其成(ZZ);
			}

		}else if (数值范围(11,20,第一阶段随机)) {
			
			if(数值范围(1,50,第二阶段随机)){
				利刃(ZZ);
			}else{
				魔术(ZZ);
			}
			
		}else if (数值范围(21,30,第一阶段随机)) {
			
			if(数值范围(1,10,第二阶段随机)){
				//福利(ZZ);
				冥想(ZZ);
			}else if(数值范围(11,60,第二阶段随机)){
				财源(ZZ);
			}else{
				冥想(ZZ);
			}
			
		}else if (数值范围(31,40,第一阶段随机)) {
			
			能手册(ZZ);
			
		}else if (数值范围(41,50,第一阶段随机)) {
			
			if(数值范围(1,50,第二阶段随机)){
				怨念丛生(ZZ);
			}else{
				怨念爆发(ZZ);
			}	
			
		}else if (数值范围(51,55,第一阶段随机)) {
			
			if(数值范围(1,50,第二阶段随机)){
				兵不血刃(ZZ);
			}else{
				窃法魔术(ZZ);
			}
			
		}else if (数值范围(56,60,第一阶段随机)) {
			
			if(数值范围(1,50,第二阶段随机)){
				茁壮成长(ZZ);
			}else{
				茁壮生长(ZZ);
			}	
			
		}else if (数值范围(61,65,第一阶段随机)) {
			
			坚韧(ZZ);
			
		}else if (数值范围(66,70,第一阶段随机)) {
			
			if(数值范围(1,50,第二阶段随机)){
				超然(ZZ);
			}else{
				清晰(ZZ);
			}
			
		}else{
			
			if(cm.getJobId() >= 200 && cm.getJobId() < 300){
				魔攻(ZZ);
			}else{
				强攻(ZZ);
			}
			
		}
		if (cm.getPlayer().getbosslog("每日任务_装备附魔") <= 0 ) {
			cm.setbosslog("每日任务_装备附魔");
		}
    }
}
function 强攻(x) {
	var 随机 = Math.floor(Math.random() * 300);
	var 类型 = 1;
	var 数值 = 0;
	
	if(随机 <= 1){
		数值 = 生成数值(1000);
		if(数值>=500){
			if(Math.floor(Math.random() * 99)>=1){
				数值/=2;
			}
		}
	}else if(随机 <= 5){
		数值 = 生成数值(800);
		if(数值>=500){
			if(Math.floor(Math.random() * 99)>=1){
				数值/=2;
			}
		}
	}else if(随机 <= 7){
		数值 = 生成数值(600);
		if(数值>=500){
			if(Math.floor(Math.random() * 99)>=10){
				数值/=2;
			}
		}
	}else if(随机 <= 10){
		数值 = 生成数值(400);
	}else if(随机 <= 30){
		数值 = 生成数值(200);
	}else{
		数值 = 生成数值(100);
	}
	
	//防止低级装备出现过高数值
	if(LV == 0){
		if(数值 >= 50){
			数值 = 生成数值(50);
		}
	}else if(LV > 0 && LV <= 10){
		if(数值 >= 100){
			数值 = 生成数值(100);
		}
	}else if(LV > 10 && LV <= 30){
		if(数值 >= 200){
			数值 = 生成数值(200);
		}
	}else if(LV > 30 && LV <= 50){
		if(数值 >= 300){
			数值 = 生成数值(300);
		}
	}else if(LV > 50 && LV <= 70){
		if(数值 >= 500){
			数值 = 生成数值(500);
		}
	}
	
	cm.装备附魔(x,类型,数值);
	cm.sendOk("#v"+ID+"# #b"+cm.getItemName(ID)+"#k\r\n\r\n#b[强攻]#k 增加 #r "+数值+"#k 点物理伤害。");
	cm.dispose();
}

function 魔攻(x) {
	var 随机 = Math.floor(Math.random() * 300)+1;
	var 类型 = 2;
	var 数值 = 0;
	if(随机 <= 1){
		数值 = 生成数值(1500);
		if(数值>=500){
			if(Math.floor(Math.random() * 99)>=1){
				数值/=2;
			}
		}
	}else if(随机 <= 5){
		数值 = 生成数值(1000);
		if(数值>=500){
			if(Math.floor(Math.random() * 99)>=1){
				数值/=2;
			}
		}
	}else if(随机 <= 7){
		数值 = 生成数值(700);
		if(数值>=500){
			if(Math.floor(Math.random() * 99)>=10){
				数值/=2;
			}
		}
	}else if(随机 <= 10){
		数值 = 生成数值(500);
	}else if(随机 <= 30){
		数值 = 生成数值(300);
	}else{
		数值 = 生成数值(100);
	}
	
	//防止低级装备出现过高数值
	if(LV == 0){
		if(数值 >= 50){
			数值 = 生成数值(50);
		}
	}else if(LV > 0 && LV <= 10){
		if(数值 >= 100){
			数值 = 生成数值(100);
		}
	}else if(LV > 10 && LV <= 30){
		if(数值 >= 200){
			数值 = 生成数值(200);
		}
	}else if(LV > 30 && LV <= 50){
		if(数值 >= 300){
			数值 = 生成数值(300);
		}
	}else if(LV > 50 && LV <= 70){
		if(数值 >= 500){
			数值 = 生成数值(500);
		}
	}
	
	cm.装备附魔(x,类型,数值);
	cm.sendOk("#v"+ID+"# #b"+cm.getItemName(ID)+"#k\r\n\r\n#b[魔攻]#k 增加 #r "+数值+"#k 点魔法伤害。");
	cm.dispose();
}

function 超然(x) {
	var 随机 = Math.floor(Math.random() * 200);
	var 类型 = 7;
	var 数值 = 0;
	
	if(随机 <= 1){
		数值 = 生成数值(60);
	}else if(随机 <= 25){
		数值 = 生成数值(40);
	}else{
		数值 = 生成数值(20);
	}
	
	cm.装备附魔(x,类型,数值);
	cm.sendOk("#v"+ID+"# #b"+cm.getItemName(ID)+"#k\r\n\r\n#b[超然]#k 提升角色 #r"+数值+"#k 点生命恢复能力。");
	cm.dispose();
}


function 清晰(x) {
	var 随机 = Math.floor(Math.random() * 200);
	var 类型 = 8;
	var 数值 = 0;
	
	if(随机 <= 1){
		数值 = 生成数值(60);
	}else if(随机 <= 25){
		数值 = 生成数值(40);
	}else{
		数值 = 生成数值(20);
	}
	
	cm.装备附魔(x,类型,数值);
	cm.sendOk("#v"+ID+"# #b"+cm.getItemName(ID)+"#k\r\n\r\n#b[清晰]#k 提升角色 #r"+数值+"#k 点魔法恢复能力。");
	cm.dispose();
}

function 坚韧(x) {
	var 随机 = Math.floor(Math.random() * 200);
	var 类型 = 3;
	var 数值 = 0;
	
	if(随机 <= 1){
		数值 = 生成数值(50);
	}else if(随机 <= 5){
		数值 = 生成数值(30);
	}else if(随机 <= 7){
		数值 = 生成数值(20);
	}else{
		数值 = 生成数值(10);
	}
	
	//防止低级装备出现过高数值
	if(LV == 0){
		if(数值 >= 10){
			数值 = 生成数值(10);
		}
	}else if(LV > 0 && LV <= 20){
		if(数值 >= 20){
			数值 = 生成数值(20);
		}
	}
	
	cm.装备附魔(x,类型,数值);
	cm.sendOk("#v"+ID+"# #b"+cm.getItemName(ID)+"#k\r\n\r\n#b[坚韧]#k 减少 #r"+数值+"#k 点角色受到的伤害。");
	cm.dispose();
}

function 茁壮生长(x) {
	var 随机 = Math.floor(Math.random() * 200);
	var 类型 = 21;
	var 数值 = 0;
	
	if(随机 <= 1){
		数值 = 生成数值(20);
	}else if(随机 <= 5){
		数值 = 生成数值(9);
	}else if(随机 <= 7){
		数值 = 生成数值(7);
	}else{
		数值 = 生成数值(5);
	}
	
	if(LV == 0){
		if(数值 >= 5){
			数值 = 生成数值(5);
		}
	}else if(LV > 0 && LV <= 20){
		if(数值 >= 10){
			数值 = 生成数值(10);
		}
	}
	
	cm.装备附魔(x,类型,数值);
	cm.sendOk("#v"+ID+"# #b"+cm.getItemName(ID)+"#k\r\n\r\n#b[茁壮生长]#k 角色升级额外MaxMp + #r"+数值+"#k");
	cm.dispose();
}

function 茁壮成长(x) {
	var 随机 = Math.floor(Math.random() * 200);
	var 类型 = 20;
	var 数值 = 0;
	
	if(随机 <= 1){
		数值 = 生成数值(20);
	}else if(随机 <= 5){
		数值 = 生成数值(9);
	}else if(随机 <= 7){
		数值 = 生成数值(7);
	}else{
		数值 = 生成数值(5);
	}
	
	if(LV == 0){
		if(数值 >= 5){
			数值 = 生成数值(5);
		}
	}else if(LV > 0 && LV <= 20){
		if(数值 >= 10){
			数值 = 生成数值(10);
		}
	}
	
	
	cm.装备附魔(x,类型,数值);
	cm.sendOk("#v"+ID+"# #b"+cm.getItemName(ID)+"#k\r\n\r\n#b[茁壮成长]#k 角色升级额外MaxHp + #r"+数值+"#k");
	cm.dispose();
}

function 兵不血刃(x) {
	var 随机 = Math.floor(Math.random() * 200);
	var 类型 = 5;
	var 数值 = 0;
	
	if(随机 <= 1){
		数值 = 生成数值(50);
	}else if(随机 <= 5){
		数值 = 生成数值(40);
	}else if(随机 <= 7){
		数值 = 生成数值(20);
	}else{
		数值 = 生成数值(10);
	}
	
	if(LV == 0){
		if(数值 >= 5){
			数值 = 生成数值(5);
		}
	}else if(LV > 0 && LV <= 30){
		if(数值 >= 10){
			数值 = 生成数值(10);
		}
	}
	
	cm.装备附魔(x,类型,数值);
	cm.sendOk("#v"+ID+"# #b"+cm.getItemName(ID)+"#k\r\n\r\n#b[兵不血刃]#k 每次攻击叠加 #r "+数值+"#k 点物理伤害。");
	cm.dispose();
}

function 窃法魔术(x) {
	var 随机 = Math.floor(Math.random() * 200);
	var 类型 = 6;
	var 数值 = 0;
	
	if(随机 <= 1){
		数值 = 生成数值(50);
	}else if(随机 <= 5){
		数值 = 生成数值(40);
	}else if(随机 <= 7){
		数值 = 生成数值(20);
	}else{
		数值 = 生成数值(10);
	}
	
	if(LV == 0){
		if(数值 >= 10){
			数值 = 生成数值(10);
		}
	}else if(LV > 0 && LV <= 50){
		if(数值 >= 30){
			数值 = 生成数值(30);
		}
	}
	
	cm.装备附魔(x,类型,数值);
	cm.sendOk("#v"+ID+"# #b"+cm.getItemName(ID)+"#k\r\n\r\n#b[窃法魔术]#k 每次攻击叠加 #r "+数值+"#k 点魔法伤害。");
	cm.dispose();
}

function 能手册(x) {
	var 随机 = Math.floor(Math.random() * 150);
	var 随机2 = Math.floor(Math.random() * 24);
	var 类型 = 0;
	var 技能 = "";
	switch (随机2) {
		case 0:
			类型 = 1001004;
			技能 = "强力攻击";
			break;
		case 1:
			类型 = 1001005;
			技能 = "群体攻击";
			break;
		case 2:
			类型 = 2001004;
			技能 = "魔法弹";
			break;
		case 3:
			类型 = 2001005;
			技能 = "魔法双击";
			break;
		case 4:
			类型 = 3001004;
			技能 = "断魂箭";
			break;
		case 5:
			类型 = 3001005;
			技能 = "二连射";
			break;
		case 6:
			类型 = 4001334;
			技能 = "二连击";
			break;
		case 7:
			类型 = 4001344;
			技能 = "双飞斩";
			break;
		case 8:
			类型 = 1100000;
			break;
		case 9:
			类型 = 1100001;
			break;
		case 10:
			类型 = 1101006;
			break;
		case 11:
			类型 = 1101007;
			break;
		case 12:
			类型 = 1200000;
			break;
		case 13:
			类型 = 1200001;
			break;
		case 14:
			类型 = 1201007;
			break;
		case 15:
			类型 = 1300000;
			break;
		case 16:
			类型 = 1300001;
			break;
		case 17:
			类型 = 1301007;
			break;
		case 18:
			类型 = 2101004;
			break;
		case 19:
			类型 = 2201005;
			break;
		case 20:
			类型 = 2301004;
			break;
		case 21:
			类型 = 3100000;
			break;
		case 22:
			类型 = 3200000;
			break;
		case 23:
			类型 = 4100000;
			break;
		case 24:
			类型 = 4200000;
			break;
		default:
			类型 = 1100000;
			break;
	}
	
	var 数值 = 0;
	if(随机 <= 1){
		数值 = 生成数值(5);
	}else if(随机 <= 10){
		数值 = 生成数值(3);
	}else{
		数值 = 生成数值(2);
	}
	
	cm.装备附魔(x,类型,数值);
	cm.sendOk("#v"+ID+"# #b"+cm.getItemName(ID)+"#k\r\n\r\n#b[能手册]#k 当("+cm.技能名字2(类型)+")达到满级时增加 #r "+数值+"#k 级技能等级。");
	cm.dispose();
}

function 怨念丛生(x) {
	var 随机 = Math.floor(Math.random() * 200);
	var 类型 = 27;
	var 数值 = 0;
	
	if(随机 <= 1){
		数值 = 生成数值(7);
	}else if(随机 <= 2){
		数值 = 生成数值(6);
	}else if(随机 <= 3){
		数值 = 生成数值(5);
	}else if(随机 <= 4){
		数值 = 生成数值(4);
	}else if(随机 <= 10){
		数值 = 生成数值(3);
	}else{
		数值 = 生成数值(2);
	}
	
	cm.装备附魔(x,类型,数值);
	cm.sendOk("#v"+ID+"# #b"+cm.getItemName(ID)+"#k\r\n\r\n#b[怨念丛生]#k 击杀怪物后 #r "+数值+" %#k 概率出现军团。");
	cm.dispose();
}

function 怨念爆发(x) {
	var 随机 = Math.floor(Math.random() * 200);
	var 类型 = 29;
	var 数值 = 0;
	
	if(随机 <= 1){
		数值 = 生成数值(7);
	}else if(随机 <= 2){
		数值 = 生成数值(6);
	}else if(随机 <= 3){
		数值 = 生成数值(5);
	}else if(随机 <= 4){
		数值 = 生成数值(4);
	}else if(随机 <= 10){
		数值 = 生成数值(3);
	}else{
		数值 = 生成数值(2);
	}
	
	cm.装备附魔(x,类型,数值);
	cm.sendOk("#v"+ID+"# #b"+cm.getItemName(ID)+"#k\r\n\r\n#b[怨念爆发]#k 击杀怪物后 #r "+数值+" %#k 概率出现超级怪物。");
	cm.dispose();
}

function 财源(x) {
	var 随机 = Math.floor(Math.random() * 200);
	var 类型 = 23;
	var 数值 = 0;
	
	if(随机 <= 1){
		数值 = 生成数值(150);
	}else if(随机 <= 5){
		数值 = 生成数值(100);
	}else if(随机 <= 7){
		数值 = 生成数值(80);
	}else if(随机 <= 10){
		数值 = 生成数值(60);
	}else if(随机 <= 30){
		数值 = 生成数值(40);
	}else{
		数值 = 生成数值(20);
	}
	
	cm.装备附魔(x,类型,数值);
	cm.sendOk("#v"+ID+"# #b"+cm.getItemName(ID)+"#k\r\n\r\n#b[财源]#k 增加 #r "+数值+"#k 点基础泡点金币。");
	cm.dispose();
}

function 冥想(x) {
	var 随机 = Math.floor(Math.random() * 200);
	var 类型 = 24;
	var 数值 = 0;
	
	if(随机 <= 1){
		数值 = 生成数值(150);
	}else if(随机 <= 5){
		数值 = 生成数值(100);
	}else if(随机 <= 7){
		数值 = 生成数值(80);
	}else if(随机 <= 10){
		数值 = 生成数值(60);
	}else if(随机 <= 30){
		数值 = 生成数值(40);
	}else{
		数值 = 生成数值(20);
	}
	
	cm.装备附魔(x,类型,数值);
	cm.sendOk("#v"+ID+"# #b"+cm.getItemName(ID)+"#k\r\n\r\n#b[冥想]#k 增加 #r "+数值+"#k 点基础泡点经验。");
	cm.dispose();
}

function 福利(x) {
	var 随机 = Math.floor(Math.random() * 200);
	var 类型 = 25;
	var 数值 = 0;
	
	if(随机 <= 1){
		数值 = 生成数值(50);
	}else if(随机 <= 2){
		数值 = 生成数值(40);
	}else if(随机 <= 3){
		数值 = 生成数值(30);
	}else if(随机 <= 5){
		数值 = 生成数值(20);
	}else{
		数值 = 生成数值(10);
	}
	
	cm.装备附魔(x,类型,数值);
	cm.sendOk("#v"+ID+"# #b"+cm.getItemName(ID)+"#k\r\n\r\n#b[福利]#k 增加 #r "+数值+"#k 点基础泡点点券。");
	cm.dispose();
}

function 坐享其成(x) {
	var 随机 = Math.floor(Math.random() * 200);
	var 类型 = 26;
	var 数值 = 0;
	
	if(随机 <= 1){
		数值 = 生成数值(10);
	}else if(随机 <= 5){
		数值 = 生成数值(7);
	}else{
		数值 = 生成数值(5);
	}
	
	cm.装备附魔(x,类型,数值);
	cm.sendOk("#v"+ID+"# #b"+cm.getItemName(ID)+"#k\r\n\r\n#b[坐享其成]#k 增加 #r "+数值+" %#k 点基础泡点收益。");
	cm.dispose();
}

function 狩猎(x) {
	var 随机 = Math.floor(Math.random() * 200)+1;
	var 类型 = 4;
	var 数值 = 0;
	
    if(随机 <= 1){
		数值 = 生成数值(10);
	}else if(随机 <= 5){
		数值 = 生成数值(8);
	}else if(随机 <= 7){
		数值 = 生成数值(6);
	}else if(随机 <= 10){
		数值 = 生成数值(4);
	}else{
		数值 = 生成数值(2);
	}
	
	cm.装备附魔(x,类型,数值);
	cm.sendOk("#v"+ID+"# #b"+cm.getItemName(ID)+"#k\r\n\r\n#b[狩猎]#k 增加 #r "+ 数值+" %#k 狩猎时获取的经验。");
	cm.dispose();
}

function 利刃(x) {
	var 随机 = Math.floor(Math.random() * 200)+1;
	var 类型 = 11;
	var 数值 = 0;
	
    if(随机 <= 1){
		数值 = 生成数值(10);
	}else if(随机 <= 3){
		数值 = 生成数值(8);
	}else if(随机 <= 5){
		数值 = 生成数值(6);
	}else if(随机 <= 7){
		数值 = 生成数值(4);
	}else{
		数值 = 生成数值(2);
	}
	
	cm.装备附魔(x,类型,数值);
	cm.sendOk("#v"+ID+"# #b"+cm.getItemName(ID)+"#k\r\n\r\n#b[利刃]#k 增加 #r "+数值+" %#k 点物理伤害。");
	cm.dispose();
}


function 魔术(x) {
	var 随机 = Math.floor(Math.random() * 200)+1;
	var 类型 = 12;
	var 数值 = 0;
	
    if(随机 <= 2){
		数值 = 生成数值(15);
	}else if(随机 <= 3){
		数值 = 生成数值(12);
	}else if(随机 <= 5){
		数值 = 生成数值(9);
	}else if(随机 <= 7){
		数值 = 生成数值(6);
	}else{
		数值 = 生成数值(3);
	}
	
	cm.装备附魔(x,类型,数值);
	cm.sendOk("#v"+ID+"# #b"+cm.getItemName(ID)+"#k\r\n\r\n#b[魔术]#k 增加 #r "+数值+" %#k 点魔法伤害。");
	cm.dispose();
}


function 生成数值(x) {
	var 值 = Math.floor(Math.random() * ( x - 1) ) + 1;
	return 值;
}

function 数值范围(x,y,z) {
	var 值 = false;
	if(z >= x && z <= y){
		值 = true;
	}
	return 值;
}




























