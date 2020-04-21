
var status = 0;
var fee;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {

        if (mode == 0) {
            cm.sendSimple("你还没想好名字吗？");
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
            cm.sendGetText("更改名字需要#r现金券 x 200#k\r\n请输入你要修改的名字，如果角色名字修改成功后，客户端会被强制退出；");
        } else if (status == 1) {
            fee = cm.getText();
			if(现金券 < 200){
				cm.sendOk("你没有足够的现金券，无法抽奖。");
				cm.dispose();
				return;
			}
			if(fee.indexOf(" ")!=-1){
				cm.sendOk("请不要加入空格。");
                cm.dispose();
                return;
			}
			
			if(fee.getBytes().length< 3 || fee.getBytes().length> 20){
				cm.sendOk("名字不符合要求。");
                cm.dispose();
                return;
			}
          
            if (cm.判断角色名字(fee)) {
				cm.getPlayer().修改现金券(-200);
				cm.getPlayer().dropMessage("现金券-200");
                cm.修改角色名字(fee);
                cm.dispose();
            }else{
				cm.sendOk("名字已经存在。");
                cm.dispose();
			}

        }
    
}