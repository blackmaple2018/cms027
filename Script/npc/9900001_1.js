/********************************

ZEVMS 自由冒险岛Ver.027 
@小z QQ71447500

********************************/

function start() {
    status = -1;
    action(1, 0, 0);
}
var x;
var x2;

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
    if (status == 0) {
		var 文本 = "  #i4030000#  #i4030001#  #i4030010# #r#e< 锻造工艺 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
		文本 +=" #b说明#k:锻造等级越高，制作成本越低，有概率制作成稀有矿石，或者额外获取。\r\n\r\n";
		文本 +=" #b锻造师#k : "+cm.取角色名字(cid)+"("+cid+")\r\n";
		//文本 +=" #b锻造等级#k : Lv.1\r\n";
		文本 +=" #b熟练度#k : "+exp+"\r\n";
		文本 +="\r\n\r\n";
			for (var ii = 0; ii < 数据.length; ii++) {
				文本 += "#L"+ii+"# #v"+数据[ii][0]+"# #b#t"+数据[ii][0]+"##k exp + "+数据[ii][4]+"\r\n";
			}
			
			文本 +="\r\n";
			cm.sendSimple(文本);
	} else if (status == 1) {
		x = selection;
		cm.sendGetText("\r\n输入你要合成的数量；");
    } else if (status == 2) {
		x2=cm.getText();
		if(x2==""){
			cm.sendOk("请不要加入空格。");
			cm.dispose();
			return;
		}
		
		if(x2.indexOf(" ")!=-1){
			cm.sendOk("请不要加入空格。");
			cm.dispose();
			return;
		}
		
		if(isNaN(x2)){
			cm.sendOk("请输入正确的数字。");
			cm.dispose();
			return;
		}
		
		if(x2 < 0){
			cm.sendOk("请输入正确的数字。");
			cm.dispose();
			return;
		}
		
		if(x2 > 100){
			cm.sendOk("一次最多制作100个。");
			cm.dispose();
			return;
		}
		
		cm.sendYesNo("制作 #v"+数据[x][0]+"# #b#t"+数据[x][0]+"##k x "+x2+" \r\n需要以下材料；\r\n\r\n #v"+数据[x][1]+"# #b#t"+数据[x][1]+"##k x "+数据[x][2]*x2+" / "+cm.getItemQuantity(数据[x][1])+"\r\n #v4031039# #b金币#k x "+数据[x][3]*x2+" / "+cm.判断金币()+"");
	
	} else if (status == 3) {
		if (!cm.haveItem(数据[x][1],数据[x][2]*x2)) {
			cm.sendOk("需要 #v"+数据[x][1]+"# #b#t"+数据[x][1]+"##k x "+数据[x][2]*x2+" 才可以制作。");
			cm.dispose();
			return;
		}
		if (cm.getMeso() < 数据[x][3]*x2){
			cm.sendOk("你没有 #v4031039# 金币 x "+数据[x][3]*x2+"");
			cm.dispose();
			return;
		}
		if (cm.getInventory(4).isFull()) {
			cm.sendOk("请保证 #b其它栏#k 至少有2个位置。");
			cm.dispose();
			return;
		} 
		if(cm.getPlayer().getbosslog("每日任务_合成矿石") <= 0){
			cm.setbosslog("每日任务_合成矿石");
		}
		cm.gainMeso(-数据[x][3]*x2);
		cm.gainItem(数据[x][1],-数据[x][2]*x2);
		cm.gainItem(数据[x][0],x2);
		cm.getPlayer().修改锻造信息("forgingExp",数据[x][4]*x2,cid);
        cm.sendOk("恭喜你成功制作 #v"+数据[x][0]+"# #b#t"+数据[x][0]+"##k x "+x2+"");
    }
}


var 数据 = [
	[4011000,4010000,10,10000,2],
	[4011001,4010001,10,10000,2],
	[4011002,4010002,10,10000,2],
	[4011003,4010003,10,10000,2],
	[4011004,4010004,10,10000,2],
	[4011005,4010005,10,10000,2],
	[4011006,4010006,10,10000,2]
]













