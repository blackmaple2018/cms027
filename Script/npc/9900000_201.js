
function start() {
    status = -1;
    action(1, 0, 0);
}


var id;
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
	
	if (status <= 0) {
		var 文本="\t选择要操作的玩家；\r\n"+cm.显示在线玩家();
		cm.sendSimple(文本);
    } else if (status == 1) {
		id = selection;
		var 文本2="\t选择操作；\r\n";
		文本2 +="\t#L1##b封号#k#l\r\n";
		文本2 +="\t#L2##b踢下线#k#l\r\n";
		文本2 +="\t#L3##b设置或者取消主播#k#l\r\n";
		cm.sendSimple(文本2);
	} else if (status == 2) {
		
		switch (selection) {
			case 1:
				cm.断线(id,"[系统公告]:玩家 "+cm.取角色名字(id)+" 被系统永久封禁，原因是使用非法程序。");
				cm.sendOk("封禁成功。");
				cm.dispose();
                break;
			case 2:
				if(cm.getPlayer().getMap().getMapObject(id)!=null){
					cm.getPlayer().getMap().getMapObject(id).dropMessage(1,"你已经被管理员踢下线。");
					cm.getPlayer().getMap().getMapObject(id).getClient().close();
					cm.sendOk("踢下线成功。");
				}else{
					cm.sendOk("地图上无法查询到目标，操作失败。");
				}
				cm.dispose();
                break;
			case 3:
				if(cm.getPlayer().getMap().getMapObject(id)!=null){
					if(cm.getPlayer().getMap().getMapObject(id).主播()==0){
						cm.getPlayer().getMap().getMapObject(id).dropMessage(1,"你已经被管理员设置为主播。");
						cm.getPlayer().getMap().getMapObject(id).主播(1);
						cm.sendOk("设置主播成功。");
					}else{
						cm.getPlayer().getMap().getMapObject(id).dropMessage(1,"你已经被管理员取消主播身份。");
						cm.getPlayer().getMap().getMapObject(id).主播(0);
						cm.sendOk("取消主播成功。");
					}
				}else{
					cm.sendOk("地图上无法查询到目标，操作失败。");
				}
				cm.dispose();
                break;
		}
    }
}