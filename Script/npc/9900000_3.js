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

function start() {
    status = -1;
    action(1, 0, 0)
}

function action(mode, type, selection) {
    if (status <= 0 && mode <= 0) {
        cm.dispose();
        return
    }
    if (mode == 1) {
        status++
    } else {
        status--
    }	 
	if (status <= 0) {
        var selStr = " #i4030000#  #i4030001#  #i4030010# #r#e< 背包清理 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
		
		selStr += "我可以帮你清除掉背包的物品哦，比如那些你不想要又懒得丢弃的物品，又或者那些丢弃不掉的物品，我都可以帮你处理哦。#k\r\n";
			
		selStr += "\t\t\t\t#L10##b清理装备栏指定物品#k#l\r\n";
		selStr += "\t\t\t\t#L20##b清理消耗栏指定物品#k#l\r\n";
		selStr += "\t\t\t\t#L30##b清理设置栏指定物品#k#l\r\n";
		selStr += "\t\t\t\t#L40##b清理其他栏指定物品#k#l\r\n";
		selStr += "\t\t\t\t#L50##b清理宠物栏指定物品#k#l\r\n";
		
		selStr += "\t\t\t\t#L1##r清空装备栏所有物品#k#l\r\n";
		selStr += "\t\t\t\t#L2##r清空消耗栏所有物品#k#l\r\n";
		selStr += "\t\t\t\t#L3##r清空设置栏所有物品#k#l\r\n";
		selStr += "\t\t\t\t#L4##r清空其它栏所有物品#k#l\r\n";
		selStr += "\t\t\t\t#L5##r清空宠物栏所有物品#k#l\r\n";

        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
			case 1:
				for (var i = 0; i < 96; i++) {
					if (cm.getInventory(1).getItem(i) != null) {
						cm.getPlayer().removeAll(cm.getInventory(1).getItem(i).getItemId());
					}
				}
				cm.dropMessage(1,"清空成功。");
                cm.dispose();
                break;
			case 2:
				for (var i = 0; i < 96; i++) {
					if (cm.getInventory(2).getItem(i) != null) {
						cm.getPlayer().removeAll(cm.getInventory(2).getItem(i).getItemId());
					}
				}
				cm.dropMessage(1,"清空成功。");
                cm.dispose();
                break;
			case 3:
				for (var i = 0; i < 96; i++) {
					if (cm.getInventory(2).getItem(i) != null) {
						cm.getPlayer().removeAll(cm.getInventory(3).getItem(i).getItemId());
					}
				}
				cm.dropMessage(1,"清空成功。");
                cm.dispose();
                break;
			case 4:
				for (var i = 0; i < 96; i++) {
					if (cm.getInventory(4).getItem(i) != null) {
						cm.getPlayer().removeAll(cm.getInventory(4).getItem(i).getItemId());
					}
				}
				cm.dropMessage(1,"清空成功。");
                cm.dispose();
                break;
			case 5:
				for (var i = 0; i < 96; i++) {
					if (cm.getInventory(5).getItem(i) != null) {
						cm.getPlayer().removeAll(cm.getInventory(5).getItem(i).getItemId());
					}
				}
				cm.dropMessage(1,"清空成功。");
                cm.dispose();
                break;
            case 10:
                cm.dispose();
                cm.openNpc(9900000,31);
                break;
			case 20:
                cm.dispose();
                cm.openNpc(9900000,32);
                break;
			case 30:
                cm.dispose();
                cm.openNpc(9900000,33);
                break;	
			case 40:
                cm.dispose();
                cm.openNpc(9900000,34);
                break;		
			case 50:
                cm.dispose();
                cm.openNpc(9900000,35);
                break;		

			
        }
    }
}