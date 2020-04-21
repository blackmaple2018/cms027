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
	var 现金券 = cm.getPlayer().获取现金券();
    if (status == 0) {
        cm.sendYesNo("你确定要改变性别吗？需要花费一些现 #r199#k 金券哦，修改完成后需要重新登陆方可生效。");
    } else if (status == 1) {
		
		if(现金券 >= 199){
			if(cm.getPlayer().getGender()==0){
				cm.getPlayer().setGender(1);
				cm.getPlayer().setHair(31002);
				cm.getPlayer().setFace(21000);
			}else if(cm.getPlayer().getGender()==1){
				cm.getPlayer().setGender(0);
				cm.getPlayer().setHair(30030);
				cm.getPlayer().setFace(20000);
			}
			cm.getPlayer().修改现金券(-199);
			cm.getPlayer().dropMessage("现金券-199");
			cm.sendOk("修改完成，需要重新登陆方可生效。");
            cm.dispose();
		}else{
			cm.sendOk("你没有现金券，不能变性。");
            cm.dispose();
		}
    }
}

