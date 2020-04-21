importPackage(java.lang);
importPackage(Packages.client);
importPackage(Packages.client.inventory);
importPackage(Packages.server);
importPackage(Packages.constants);
importPackage(Packages.net.channel);
importPackage(Packages.tools);
importPackage(Packages.scripting);
importPackage(Packages.tools.packet);
importPackage(Packages.tools.data);
importPackage(Packages.tools);
var itemss;
var slot = Array();
var ls = 4;
var vv;
var fee;
var ree;		
function start() {
	status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {

    if (status == 0 && mode == 0) {
		cm.sendSimple("好了，那就先不上架。");
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
	
	if(cm.判断拍卖出售位数量()-cm.已经使用拍卖栏位()<=0){
		cm.sendOk("没有足够的出售栏。");
		cm.dispose();
		return;
	}
	
	
    if (status == 0) {
        var avail = "";
        for (var i = 0; i < 96; i++) {
            if (cm.getInventory(ls).getItem(i) != null) {
				if (!cm.isCash(cm.getInventory(ls).getItem(i).getItemId())) {
					avail += "#L" + i + "# #v" + cm.getInventory(ls).getItem(i).getItemId() + "# #b#t" + cm.getInventory(ls).getItem(i).getItemId() + "##l\r\n";
				}
            }
            slot.push(i);
        }
        cm.sendSimple("请选择你要添加到拍卖行的物品。#k#n\r\n\r\n" + avail);
    } else if (status == 1) {
		vv = selection;
		var b = "";
		装备 = cm.getInventory(ls).getItem(slot[selection]).getItemId();
		b += "#b \t#v"+装备+"# #b#t"+装备+"##k\r\n";
        cm.sendYesNo(" \t确定你要上架的物品是:\r\n\r\n"+b);
	} else if (status == 2) {
		cm.sendGetText("请输入你要出售的数量；");
	} else if (status == 3) {
		ree = cm.getText();
		if(isNaN(ree)){
			cm.sendOk("只能填入数字。");
			cm.dispose();
			return;
		}
		if(ree==""){
			cm.sendOk("请不要加入空格。");
			cm.dispose();
			return;
		}
		if(ree.indexOf(" ")!=-1){
			cm.sendOk("请不要加入空格。");
			cm.dispose();
			return;
		}

		if(fee < 0 || fee > cm.物品叠加数量(装备)){
			cm.sendOk("数值不正确。该物品有一次最多上架一组。");
			cm.dispose();
			return;
		}
		
		if(!cm.haveItem(装备,ree)){
			cm.sendOk("你并没有那么多物品。");
			cm.dispose();
			return;
		}
		cm.sendGetText("请输入你要出售的价格；");
	} else if (status == 4) {
		fee = cm.getText();
		if(isNaN(fee)){
			cm.sendOk("只能填入数字。");
			cm.dispose();
			return;
		}
		if(fee.indexOf(" ")!=-1){
			cm.sendOk("请不要加入空格。");
			cm.dispose();
			return;
		}
		if(fee < 0 || fee > 1000000000){
			cm.sendOk("数值不正确。");
			cm.dispose();
			return;
		}
		
		
		cm.添加道具到拍卖行(
		Integer.parseInt(fee),
		装备,
		4,
		Integer.parseInt(ree),
		"",
		"",
		0,
		0,
		0,
		"",		
		0,
		0,
		0,
		0,
		0,
		0,
		0,
		0,
		0,
		0,
		0,
		0,
		0,
		0,
		0,
		0,
		0,
		"",
		0
		);

		//cm.cleanItemBySlot(slot[vv],4,ree);	
		cm.gainItem(装备,-ree);		
		cm.sendOk("上架成功。");
		cm.dispose();

    }
}


































