var itemss;
var slot = Array();
var ls = 1;
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
		var avail = "";
		for (var i = 0; i < 96; i++) {
			if (cm.getInventory(ls).getItem(i) != null) {
				avail += "#L" + i + "# #v" + cm.getInventory(ls).getItem(i).getItemId() + "# #r清理 #b#t" + cm.getInventory(ls).getItem(i).getItemId() + "##k #l\r\n";
			}
			slot.push(i);
		}
		if(avail==""){
			cm.dropMessage(1,"没有物品可清理。");
			cm.dispose();			 
			return;
		}
		cm.sendSimple("#e#r提示；选择要清理的物品。#k#n\r\n\r\n" + avail);
    } else if (status == 1) {
		cm.cleanItemBySlot(slot[selection], ls, 2000);
		cm.dropMessage(5,"清理成功。");
		cm.dispose();			
    } 
}