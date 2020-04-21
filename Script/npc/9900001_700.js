/********************************

ZEVMS 自由冒险岛Ver.027 
@小z QQ71447500

********************************/
var a;
var s = 4;
var x;
var id;
var exxp;
var exxxp;
var mseo;
var 制作卷 = 4131003;
var 减少比例;
/*var 矿石 = [
	//青铜
	[4011000],
	//钢铁
	[4011001],
	//锂矿石
	[4011002],
	//朱矿石
	[4011003],
	//银
	[4011004],
	//紫矿石
	[4011005],
	//黄金
	[4011006]
]

var 母矿 = [
	//石榴石
	[4021000],
	//紫水晶
	[4021001],
	//海蓝宝石
	[4021002],
	//祖母綠
	[4021003],
	//蛋白石
	[4021004],
	//蓝宝石
	[4021005],
	//黄晶
	[4021006],
	//钻石
	[4021007],
	//黑水晶
	[4021008]
}
*/

//装备代码，制作需要经验，锻造获得经验，金币，材料，数量 ** ** ** 
var 数据 = [
	//蝙蝠怪的双手镭剑
	[1402059,4444,50,100*10000,4131003,50,4021006,10,4011006,10,4021005,10],
	//蝙蝠怪的格洛斧
	[1412039,4444,50,100*10000,4131004,50,4021006,10,4011006,10,4021005,10],
	//蝙蝠怪的镭奥钉锤
	[1422043,4444,50,100*10000,4131005,50,4021006,10,4011006,10,4021005,10],
	//蝙蝠怪的破魔枪
	[1432055,4444,50,100*10000,4131006,50,4021006,10,4011006,10,4021005,10],
	//蝙蝠怪的飞翔斧
	[1442075,4444,50,100*10000,4131007,50,4021006,10,4011006,10,4021005,10],
	//蝙蝠怪的梅杜斯
	[1452070,4444,50,100*10000,4131010,50,4021006,10,4011006,10,4021005,10],
	//蝙蝠怪的可撒之弩
	[1462063,4444,50,100*10000,4131011,50,4021006,10,4011006,10,4021005,10],
	//蝙蝠怪的霸杰之刃
	[1332087,4444,50,100*10000,4131012,50,4021006,10,4011006,10,4021005,10],
	//蝙蝠怪的拳套
	[1472084,4444,50,100*10000,4131013,50,4021006,10,4011006,10,4021005,10],
	//蝙蝠怪的怒涛之杖
	[1382067,4444,50,100*10000,4131009,50,4021006,10,4011006,10,4021005,10]
]


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
	
	//设置目标
	var	cid = cm.getPlayer().getId();
	cm.getPlayer().设置目标(cid);
	var exp = cm.getPlayer().获取锻造信息("forgingExp",cid);
    if (status == 0) {
		var 文本 = "#i4030000#  #i4030001#  #i4030010# #r#e< 高级的类制造 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\t\t\t该类型装备可进行附魔属性继承。\r\n";
		for (var ii = 0; ii < 数据.length; ii++) {
			//if(exp >= 数据[ii][1]){
				a++;
				文本 += "\t\t   #L"+ii+"# #v"+数据[ii][0]+"# #b#t"+数据[ii][0]+"#  Lv."+cm.装备属性(数据[ii][0],"reqLevel")+"#k#l\r\n";
			//}
		}
		if(a==0){
			文本 +="\r\n\r\n\t  没有可以制作的装备，提高锻造熟练度可解锁制作。\r\n";
		}
		文本 +="\r\n";
		cm.sendSimple(文本);
    } else if (status == 1) {
		x = selection;
		id = 数据[x][0];
		exxp = 数据[x][1];
		exxxp = 数据[x][2];
		mseo = 数据[x][3];
		//制作条件显示
		var 文本2 ="";
		文本2 += "制作 #v"+id+"# #e#b#t"+id+"##k#n\r\n\r\n";
		文本2 += "锻造需要熟练度: #b"+exxp+" / "+exp+"#k\r\n";
		文本2 += "锻造获得熟练度: #b"+exxxp+"#k\r\n";
		文本2 += "需要猎魔气息: #b2000 / 0#k\r\n";
		文本2 += "#v4031039# #b金币#k x "+mseo+" / "+cm.判断金币()+"\r\n";
		
		for (var iii = s; iii < 数据[x].length; iii+=2) {
			var itemID = 数据[x][iii];
			var itemcout = 数据[x][iii+1];
			文本2 += "#v"+itemID+"# #b#t"+itemID+"##k  x "+itemcout+" / "+cm.getItemQuantity(itemID)+"\r\n";
		}
		cm.sendNextPrev(文本2);
	} else if (status == 2) {
		//装备属性显示
		var 文本3 ="";
		文本3 += "#v"+id+"# #b#e#t"+id+"##n#k \r\n\r\n";
		文本3+=" #b佩戴等级#k : "+cm.装备属性(id,"reqLevel")+"\r\n";
		if(cm.装备属性(id,"reqSTR")>0){
			文本3+=" #b要求力量#k : "+cm.装备属性(id,"reqSTR")+"\r\n";
		}
		if(cm.装备属性(id,"reqDEX")>0){
			文本3+=" #b要求敏捷#k : "+cm.装备属性(id,"reqDEX")+"\r\n";
		}
		if(cm.装备属性(id,"reqINT")>0){
			文本3+=" #b要求智力#k : "+cm.装备属性(id,"reqINT")+"\r\n";
		}
		if(cm.装备属性(id,"reqLUK")>0){
			文本3+=" #b要求运气#k : "+cm.装备属性(id,"reqLUK")+"\r\n";
		}
		if(cm.装备属性(id,"reqPOP")>0){
			文本3+=" #b要求人气#k : "+cm.装备属性(id,"reqPOP")+"\r\n";
		}
		文本3+= "\r\n #r基础属性，制作完成后可能有浮动#k#k\r\n\r\n";
		
		if(cm.装备属性(id,"incPAD")>0){
			文本3+=" #b物理攻击力#k : "+cm.装备属性(id,"incPAD")+"\r\n";
		}
		if(cm.装备属性(id,"incMAD")>0){
			文本3+=" #b魔法攻击力#k : "+cm.装备属性(id,"incMAD")+"\r\n";
		}
		if(cm.装备属性(id,"incPDD")>0){
			文本3+=" #b物理防御力#k : "+cm.装备属性(id,"incPDD")+"\r\n";
		}
		if(cm.装备属性(id,"incMDD")>0){
			文本3+=" #b魔法防御力#k : "+cm.装备属性(id,"incMDD")+"\r\n";
		}
		if(cm.装备属性(id,"incSTR")>0){
			文本3+=" #b增加力量#k : "+cm.装备属性(id,"incSTR")+"\r\n";
		}
		if(cm.装备属性(id,"incDEX")>0){
			文本3+=" #b增加敏捷#k : "+cm.装备属性(id,"incDEX")+"\r\n";
		}
		if(cm.装备属性(id,"incINT")>0){
			文本3+=" #b增加运气#k : "+cm.装备属性(id,"incINT")+"\r\n";
		}
		if(cm.装备属性(id,"incLUK")>0){
			文本3+=" #b增加智力#k : "+cm.装备属性(id,"incLUK")+"\r\n";
		}
		if(cm.装备属性(id,"incSpeed")>0){
			文本3+=" #b增加移动速度#k : "+cm.装备属性(id,"incSpeed")+"\r\n";
		}
		if(cm.装备属性(id,"incJump")>0){
			文本3+=" #b增加跳跃力#k : "+cm.装备属性(id,"incJump")+"\r\n";
		}
		if(cm.装备属性(id,"incACC")>0){
			文本3+=" #b增加回避率#k : "+cm.装备属性(id,"incACC")+"\r\n";
		}
		if(cm.装备属性(id,"incEVA")>0){
			文本3+=" #b增加命中率#k : "+cm.装备属性(id,"incEVA")+"\r\n";
		}
		cm.sendNextPrev(文本3);
	} else if (status == 3) {
		cm.sendYesNo("是否要制作 #v"+id+"# #b#e#t"+id+"##n#k ? ");
	} else if (status == 4) {	
			cm.sendOk("你猎魔气息不够。");
				cm.dispose();
				return;
		//判断材料
		for (var iii = s; iii < 数据[x].length; iii+=2) {
			var itemID = 数据[x][iii];
			var itemcout = 数据[x][iii+1];
			if (!cm.haveItem(itemID,itemcout)) {
				cm.sendOk("你没有 #v"+itemID+"# #b#t"+itemID+"##k x "+itemcout+"。");
				cm.dispose();
				return;
			}
		}
		//判断锻造经验
		if(exp < exxp){
			cm.sendOk("锻造经验不够，无法制作。");
			cm.dispose();
			return;
		}
		//判断锻造金币
		if(cm.getMeso() < mseo){
			cm.sendOk("你没有 #v4031039# #b金币#k x "+mseo+"，我不能为你服务。");
			cm.dispose();
			return;
		}
		//判断装备栏
		if (cm.getInventory(1).isFull()) {
			cm.sendOk("请保证 #b装备栏#k 至少有2个位置。");
			cm.dispose();
			return;
		}
		//收取材料
		for (var iii = s; iii < 数据[x].length; iii+=2) {
			var itemID = 数据[x][iii];
			var itemcout = 数据[x][iii+1];
			cm.gainItem(itemID,-itemcout);
		}
		//收取金币
		cm.gainMeso(-mseo);
		//增加锻造经验
		cm.getPlayer().修改锻造信息("forgingExp",exxxp,cid);
		//给装备
		cm.gainItem(id,1);
		cm.sendOk("恭喜你成功制作 #v"+id+"# #b#t"+id+"##k。");
		cm.dispose();
    }
}




/*
var 水晶 = [
	//力量水晶
	[4005000],
	//智慧水晶
	[4005001],
	//敏捷水晶
	[4005002],
	//幸运水晶
	[4005003],
	//黑暗水晶
	[4005004]
}
*/



