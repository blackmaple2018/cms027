/********************************

ZEVMS 自由冒险岛Ver.027 
@小z QQ71447500

********************************/

var x;

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
		x = 0;
		var 文本 = "  #i4030000#  #i4030001#  #i4030010# #r#e< 手工打造 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n\r\n";
		文本+="\t\t\t";
		for (var i = 0; i <= 4; i++) {
            if (cm.getInventory(4).getItem(i) != null) {
                var itemid = cm.getInventory(4).getItem(i).getItemId();
				文本+="#v"+itemid+"#  ";
				x++;
            }
        }
		if(x == 0){
			文本 += "请将材料放置背包#r其它栏第一行，1，\r\n\t\t\t2，3，4位置#k组成组合，即可合成其他物品。";
		}
		文本+="\r\n\r\n\t\t\t\t\t #b#L1#开始合成#l#k";
		cm.sendSimple(文本);
	} else if (status == 1) {
		if(x<2){
			cm.sendOk("单个物品无法合成，需要2种或者2种以上方可合成。");
			cm.dispose();
			return;
		}
		cm.sendOk("暂时未开放。");
		cm.dispose();
	
	} 
}


