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
var x;
var z;
var t;
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
	

	
	if (status == 0) {
		t = 0;
		var 文本 = " #i4030000#  #i4030001#  #i4030010# #r#e< 口令口令 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n输入口令后可以领取随机奖励，每种口令每日都可以领取一次奖励。如果你不知道口令，请进入群主的空间日志查看吧。群主的QQ是 #r71447500#k 哦，口令不定时会更改，每日仅能获取1次天梯积分。\r\n";
		cm.sendGetText(文本+"\r\n请输入口令；");
	} else if (status == 1) {
		x = cm.getText();

		
		if( x == "最帅的人是我" ){
			t++;
			if(cm.getbosslog("口令奖励1")==0){
				var 金币奖励 = 100000;
				cm.setbosslog("口令奖励1");
				cm.gainMeso(金币奖励);
				cm.sendOk("恭喜你领取 #v4031039# 金币 x "+金币奖励+" 成功。");
				if(cm.getbosslog("口令奖励活跃积分")==0){
					cm.setbosslog("口令奖励活跃积分");
					cm.getPlayer().设置天梯积分(1);
				}
			}else{
				cm.sendOk("今日已经领取过该福利。");
			}
		}
		
		
		if( x == "魔法石头我也要" ){
			t++;
			if(cm.getbosslog("口令奖励2")==0){
				var 物品数量 = 10;
				cm.setbosslog("口令奖励2");
				cm.getPlayer().gainItem(4006000, 物品数量, false);
				cm.sendOk("恭喜你领取 #v4006000# #t4006000# x "+物品数量+" 成功。");
				if(cm.getbosslog("口令奖励活跃积分")==0){
					cm.setbosslog("口令奖励活跃积分");
					cm.getPlayer().设置天梯积分(1);
				}
			}else{
				cm.sendOk("今日已经领取过该福利。");
			}
		}
		
		if( x == "我要泡点我爱泡点" ){
			t++;
			if(cm.getbosslog("口令奖励3")==0){
				var 物品数量 = 5;
				cm.setbosslog("口令奖励3");
				cm.getPlayer().set泡点经验((cm.getPlayer().get泡点经验()+(物品数量)));
				cm.sendOk("恭喜你领取 基础泡点经验 + 5 ");
				if(cm.getbosslog("口令奖励活跃积分")==0){
					cm.setbosslog("口令奖励活跃积分");
					cm.getPlayer().设置天梯积分(1);
				}
			}else{
				cm.sendOk("今日已经领取过该福利。");
			}
		}

		
		if(t==0){
			cm.sendOk("不存在的口令。");
		}
		cm.dispose();
	} 
}


















