var fee;
function start() {
    status = -1;
    action(1, 0, 0);
}

function isNull( str ){
	if ( str == "" ) {
		return true;
	}
	var regu = "^[ ]+$";
	var re = new RegExp(regu);
	return re.test(str);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.sendOk("你没有卡号？");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
            cm.sendGetText("请输入要兑换的现金券数量；");
        } else if (status == 1) {
            fee = cm.getText();
			//判断是偶有空格
			if(fee.indexOf(" ")!=-1){
				cm.sendOk("请不要加入空格。");
                cm.dispose();
                return;
			}
			if(!isNaN(fee)){
				cm.sendOk("请填入数字。");
                cm.dispose();
                return;
			}   
			if(fee<0 || fee >100000){
				cm.sendOk("不正确的数量。");
                cm.dispose();
                return;
			}
			if(cm.getPlayer().获取现金券()>=fee){
				cm.getPlayer().修改现金券(-售价);
				cm.getPlayer().getCashShop().gainCash(1, fee*10);
				cm.getPlayer().dropMessage("现金券-"+售价+"");
				cm.sendOk("恭喜你购买成功。");
			}else{
				cm.sendOk("你的现金券不够，无法购买。");
			}
			cm.dispose();
        }
    }
}






























var fee;
function start() {
    status = -1;
    action(1, 0, 0);
}

function isNull( str ){
	if ( str == "" ) {
		return true;
	}
	var regu = "^[ ]+$";
	var re = new RegExp(regu);
	return re.test(str);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.sendOk("你没有卡号？");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
            cm.sendGetText("请输入要兑换的现金券数量；");
        } else if (status == 1) {
            fee = cm.getText();
			//判断是偶有空格
			if(fee.indexOf(" ")!=-1){
				cm.sendOk("请不要加入空格。");
                cm.dispose();
                return;
			}
			if(isNaN(fee)){
				cm.sendOk("请填入数字。");
                cm.dispose();
                return;
			}   
			if(fee<0 || fee >100000){
				cm.sendOk("不正确的数量。");
                cm.dispose();
                return;
			}
			if(cm.getPlayer().获取现金券()>=fee){
				cm.getPlayer().修改现金券(-fee);
				cm.getPlayer().getCashShop().gainCash(1, fee*10);
				cm.getPlayer().dropMessage("现金券-"+fee+"");
				cm.sendOk("恭喜你成功兑换 #b"+fee*10+"#k 点券。");
			}else{
				cm.sendOk("你的现金券不够，无法购买。");
			}
			cm.dispose();
        }
    }
}






























