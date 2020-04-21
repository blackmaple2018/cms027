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

    if (status == 0) {
        var selStr = " #i4030000#  #i4030001#  #i4030010# #r#e< 狩猎排行榜 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
		selStr+="        - 我的排名:#b"+cm.获取杀怪排名(cm.getPlayer().getId(), cm.getPlayer().getWorldId())+"#k\r\n"+cm.击杀怪物排行榜()+"";
		cm.sendSimple(selStr);
    } else if (status == 1) {
		if(cm.getLevel()>=30){
			if(cm.今日在线()>=120){
				if(cm.getbosslog("点赞")==0){
					cm.setbosslog("点赞");
					cm.点赞(selection);
					cm.sendOk("点赞成功。");
				}else{
					cm.sendOk("你已经点过赞了。");
				}
			}else{
				cm.sendOk("在线120分钟才可以点赞。");
			}
		}else{
			cm.sendOk("30级以上才可以点赞。");
		}
		cm.dispose();
    }
}

