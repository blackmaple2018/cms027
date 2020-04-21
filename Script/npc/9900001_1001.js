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

	[2100001,100,1,10*10000,4021003,1],
	[2100002,300,1,20*10000,4021003,1],
	[2100003,500,1,30*10000,4021003,1],
	[2100004,700,1,40*10000,4021003,1],
	[2100005,900,1,50*10000,4021003,1],
	[2100006,1100,1,60*10000,4021003,1],
	[2100007,1300,1,70*10000,4021003,1],
	[2100009,1500,1,80*10000,4021003,1]
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
		var 文本 = " #i4030000#  #i4030001#  #i4030010# #r#e< 恢复药类制造 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
		for (var ii = 0; ii < 数据.length; ii++) {
			//if(exp >= 数据[ii][1]){
				a++;
				文本 += "\t\t\t#L"+ii+"# #v"+数据[ii][0]+"# #b#t"+数据[ii][0]+"##l\r\n";
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
		文本2 += "#v4031039# #b金币#k x "+mseo+"\r\n";
		for (var iii = s; iii < 数据[x].length; iii+=2) {
			var itemID = 数据[x][iii];
			var itemcout = 数据[x][iii+1];
			文本2 += "#v"+itemID+"# #b#t"+itemID+"##k  x "+itemcout+" 个\r\n";
		}
		cm.sendNextPrev(文本2);
	} else if (status == 2) {
		cm.sendYesNo("是否要制作 #v"+id+"# #b#e#t"+id+"##n#k ? ");
	} else if (status == 3) {	
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



