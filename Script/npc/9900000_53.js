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
	if(cm.显示伤害详细()!=""){
		cm.sendOk(cm.显示伤害详细());
	}else{
		cm.sendOk("你需要攻击过目标，才会有数据展示。");
	}
	cm.dispose();

}
















