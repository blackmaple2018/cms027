var slot1 = Array();
var xx1;
var xx2;
var slot2 = Array();
var ls = 1;
var ID;
var 金币 = 10000;
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
	//cm.getPlayer().setMaxlevel(101);
	cm.saveDatabaseItem();
    if (status <= 0) {
        var 文本 = "*—— #b[ #r装备属性重置#b ]#k ———————————————————————————————————————————*\r\n\r\n";
			文本 +="\t#v4031039# 金币 x #b"+金币+"#k\r\n";
			文本 +="\t将两把相同的装备，进行融合随机，重置属性。#r该操作会清空装备附魔和打孔属性。#k\r\n\选择你要#r重置属性#k的装备;\r\n";
        for (var i = 0; i < 96; i++) {
            if (cm.getInventory(ls).getItem(i) != null) {
                var itemid = cm.getInventory(ls).getItem(i).getItemId();
                if (!cm.isCash(cm.getInventory(ls).getItem(i).getItemId())) {
                    var name = cm.getItemName(itemid);
                    if (i < 10) {
                        文本 += "\t#L" + i + "# 0" + i + ")  #v" + itemid + "# #b" + name + "#k#l\r\n";
                        
                    } else {
                        文本 += "\t#L" + i + "# " + i + ")  #v" + itemid + "# #b" + name + "#k#l\r\n";
                        
                    }
                }
            }
            slot1.push(i);
        }
        cm.sendSimple(文本);
    } else if (status == 1) {
		xx1 = slot1[selection];
		var 文本 = "*—— #b[ #r装备属性重置#b ]#k ———————————————————————————————————————————*\r\n\r\n";
			文本 +="\t#v4031039# 金币 x #b"+金币+"#k\r\n";
			文本 +="\t将两把相同的装备，进行融合随机，重置属性。选择你要#r重置属性#k的装备;\r\n";
        for (var i = 0; i < 96; i++) {
            if (cm.getInventory(ls).getItem(i) != null) {
                var itemid = cm.getInventory(ls).getItem(i).getItemId();
                if (!cm.isCash(cm.getInventory(ls).getItem(i).getItemId())) {
					if(i!=xx1){
						var name = cm.getItemName(itemid);
						if (i < 10) {
							文本 += "\t#L" + i + "# 0" + i + ")  #v" + itemid + "# #b" + name + "#k#l\r\n";
                        
						} else {
							文本 += "\t#L" + i + "# " + i + ")  #v" + itemid + "# #b" + name + "#k#l\r\n";
                        
						}
					}
                }
            }
            slot2.push(i);
        }
        cm.sendSimple(文本);
    } else if (status == 2) {
		xx2 = slot2[selection];
		ID1 = cm.getInventory(ls).getItem(xx1).getItemId();
		ID2 = cm.getInventory(ls).getItem(xx2).getItemId();
		if(ID1 != ID2){
			cm.sendSimple("相同的装备才可以进入融合重置属性。");
			cm.dispose();
			return;
		}
		if(cm.getMeso() < 金币){
			cm.sendOk("你没有 "+金币+" 金币，我不能为你服务。");
			cm.dispose();
			return;
		}
	    ID = cm.getInventory(ls).getItem(xx1).getItemId();
		cm.sendYesNo("你要将 #v"+ID+"# 融合吗？ 我将退还给你 #b"+cm.装备出售价格(ID1)+"#k 金币。并给你一个全新随机的属性装备。");
	} else if (status == 3) {
		cm.gainMeso(-金币);
		cm.gainMeso(cm.装备出售价格(ID));
		cm.cleanItemBySlot(xx1, 1, 1);
		cm.cleanItemBySlot(xx2, 1, 1);
		cm.gainItem(ID,1);
		cm.sendOk("恭喜你，装备重置属性成功。");
	}
		
}















