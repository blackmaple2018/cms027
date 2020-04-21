
var id;
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
	if (cm.getInventory(2).isFull()) {
        cm.说明文字("请保证 #b消耗栏#k 至少有2个位置。");
        cm.对话结束();
        return;
    } 
	if(cm.显示物品列表种类选择(2)=="\r\n"){
		cm.sendOk("没有物品出售。");
		cm.dispose();
		return;
	}
    if (status == 0) {
		cm.sendSimple("选择你要购买的物品类型；#k#n\r\n\r\n" + cm.显示物品列表种类选择(2));
    } else if (status == 1) {
        cm.sendSimple("选择你要购买的物品。#k#n\r\n\r\n" + cm.显示物品列表详细(selection));
	} else if (status == 2) {
		id = selection;
        cm.sendSimple("装备详细信息，如果想要购买请点击下方购买按键。\r\n"+cm.显示物品数据详细(selection)+"\r\n#L1##b购买#k#l");	
	} else if (status == 3) {	
		if(selection == 1){
			var 价格 = cm.物品价格(id);
			if(cm.getMeso()>=价格){
				if(cm.物品存在(id)>0){
					var 卖家 = cm.物品卖家(id);
					cm.gainMeso(-价格);
					cm.新增拍卖存储金币(卖家,价格);
					cm.购买拍卖行装备(id);
					cm.删除购买的道具(id);
					cm.sendOk("购买成功。");
				}
			}else{
				cm.sendOk("你的金币不够购买该物品。");
			}
			cm.dispose();
		}
    } 
}