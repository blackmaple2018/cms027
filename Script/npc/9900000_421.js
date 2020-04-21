/********************************

ZEVMS 自由冒险岛Ver.027 
@小z QQ71447500

********************************/




var 拜访数组 = [
	[1010100],//射手村，丽娜
	[1012003],//射手村，长老斯坦
	[1012101]//射手村民民宅，玛娅
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
	var 等级 = cm.getLevel();
	var 完成环数 = cm.getPlayer().getbosslog("每日任务_跑环_拜访岛民完成");
	var 当前环数 = cm.getPlayer().getbosslog("每日任务_跑环_拜访岛民");
	var 拜访岛民 = cm.getPlayer().获取每日访问NPC代码();
	var 最大环数 = 5;
	var 现金券 = cm.getPlayer().获取现金券();
	
    if (status == 0) {
		var 文本 = "  #i4030000#  #i4030001#  #i4030010# #r#e< 拜访岛民 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";

		if(完成环数<最大环数){
			文本 +="   开始任务后将会随机指定去访问冒险岛内的村民，每次完成访问都会获取奖励，最终完成后将会获取丰厚的特殊奖励。\r\n";
			文本 +="   #b每环奖励#k: 金币\r\n";
			文本 +="   #b最终奖励#k: 泡点经验收益 + 100 人气 + 1 能力点 + 1\r\n";
			文本 +="   #b当前环数#k: "+最大环数+" / "+当前环数+"\r\n";
			if(拜访岛民>0){
				文本 +="   #b拜访岛民#k: "+cm.获取NPC名字(拜访岛民)+"\r\n";
			}
			if(拜访岛民==0){
				文本 +="\r\n";
				文本 +="  #L1##b开始第 "+(当前环数+1)+" 环拜访#k#l\r\n";
			}else{
				文本 +="\r\n";
				//文本 +="  #L2##b完成拜访任务#k#l\r\n";
			}
		}else{
			文本 +="\r\n\r\n\t\t\t\t  已经完成今日拜访。\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n.";
		}
		cm.sendSimple(文本);
    } else if (status == 1) {
		switch (selection) {
			case 1:
				if(拜访岛民==0){
					var 随机 = Math.floor(Math.random() * 拜访数组.length);
					cm.getPlayer().setbosslog("每日任务_跑环_拜访岛民");
					cm.getPlayer().设置每日访问NPC代码(拜访数组[随机]);
					cm.sendOk("开始拜访任务，请拜访 "+cm.获取NPC名字(拜访数组[随机])+" 。");
				}
				cm.dispose();
                break;
			
			default:
                cm.dispose();
                break;
		}
    }
}
