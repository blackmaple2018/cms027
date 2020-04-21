var x = 2;
var id;
var id2;
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
        status = 0;
    }
	if (cm.getInventory(1).isFull()) {
        cm.说明文字("请保证 #b装备栏#k 至少有2个位置。");
        cm.对话结束();
        return;
    } 
    if (status == 0) {
		cm.sendSimple("选择你要查看物品；#k#n\r\n\r\n" + cm.显示我上架的物品列表种类选择(x));
    } else if (status == 1) {
		id2 = selection;
        cm.sendSimple("选择你要下架的物品。#k#n\r\n\r\n" + cm.显示我的物品列表详细(id2));
	} else if (status == 2) {
		id = selection;
        cm.sendSimple("如果想要下架请点击下方下架按键。\r\n"+cm.显示物品数据详细(id)+"\r\n#L1##b下架#k#l ");	
	} else if (status == 3) {	
		if(selection == 1){
			if(cm.物品存在(id)>0){
				cm.购买拍卖行装备(id);
				cm.删除购买的道具(id);
				cm.sendOk("下架成功。");
			}
			cm.dispose();
		}
    } 
}










