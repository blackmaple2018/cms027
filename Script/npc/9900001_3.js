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
		文本 +=" #b说明#k:锻造等级越高，制作成本越低，有概率制作成诅咒卷，或者额外获取。\r\n\r\n";
		文本 +=" #b锻造师#k : "+cm.取角色名字(cid)+"("+cid+")\r\n";
		//文本 +=" #b锻造等级#k : Lv.1\r\n";
		文本 +=" #b熟练度#k : "+exp+"\r\n";
		文本 +="\r\n\r\n";
			for (var ii = 0; ii < 数据.length; ii++) {
				文本 += "#L"+ii+"# #v"+数据[ii][1]+"# #b#t"+数据[ii][1]+"##k exp + "+数据[ii][4]+"\r\n";
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
		cm.sendYesNo("制作 #v"+数据[x][1]+"# #b#t"+数据[x][1]+"##k x "+x2+" \r\n需要以下材料；\r\n\r\n #v"+数据[x][0]+"# #b#t"+数据[x][0]+"##k x "+数据[x][2]*x2+" / "+cm.getItemQuantity(数据[x][0])+"\r\n #v4031039# #b金币#k x "+数据[x][3]*x2+" / "+cm.判断金币());
	} else if (status == 3) {
		if (!cm.haveItem(数据[x][0],数据[x][2]*x2)) {
			cm.sendOk("需要 #v"+数据[x]+"# #b#t"+数据[x]+"##k x "+数据[x][2]*x2+" 才可以制作。");
			cm.dispose();
			return;
		}
		if (cm.getMeso() < 数据[x][3]*x2){
			cm.sendOk("你没有 #v4031039# 金币 x "+数据[x][3]*x2+"");
			cm.dispose();
			return;
		}
		if (cm.getInventory(2).isFull()) {
			cm.sendOk("请保证 #b消耗栏#k 至少有2个位置。");
			cm.dispose();
			return;
		} 
		
		cm.gainMeso(-数据[x][3]*x2);
		cm.gainItem(数据[x][0],-数据[x][2]*x2);
		cm.gainItem(数据[x][1],x2);
		cm.getPlayer().修改锻造信息("forgingExp",数据[x][4]*x2,cid);
        cm.sendOk("恭喜你成功制作 #v"+数据[x][1]+"# #b#t"+数据[x][1]+"##k x "+x2+"");
    }
}


var 数据 = [
	//单手剑攻击卷轴
	[2043001,2043002,5,10000,2],
	//单手斧攻击卷轴
	[2043101,2043102,5,10000,2],
	//单手钝器攻击卷轴
	[2043201,2043202,5,10000,2],
	//短剑攻击卷轴
	[2043301,2043302,5,10000,2],
	//短杖魔力卷轴
	[2043701,2043702,5,10000,2],
	//长杖魔力卷轴
	[2043801,2043802,5,10000,2],
	//双手剑攻击卷轴
	[2044001,2044002,5,10000,2],
	//双手斧攻击卷轴
	[2044101,2044102,5,10000,2],
	//双手钝器攻击卷轴
	[2044201,2044202,5,10000,2],
	//枪攻击卷轴
	[2044301,2044302,5,10000,2],
	//矛攻击卷轴
	[2044401,2044402,5,10000,2],
	//弓攻击卷轴
	[2044501,2044502,5,10000,2],
	//弩攻击卷轴
	[2044601,2044602,5,10000,2],
	//拳套攻击卷轴
	[2044701,2044702,5,10000,2]

]













