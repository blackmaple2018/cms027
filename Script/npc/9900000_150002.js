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
    if (status <= 0) {
        var 文本 = "*—— #b[ #r装备打孔#b ]#k ———————————————————————————————————————————*\r\n\r\n";
			文本 +="\t选择你要#r打孔#k的装备;\r\n";
        for (var i = 0; i < 96; i++) {
            if (cm.getInventory(ls).getItem(i) != null) {
                var itemid = cm.getInventory(ls).getItem(i).getItemId();
                if (!cm.isCash(cm.getInventory(ls).getItem(i).getItemId())&& itemid != 1112002) {
                    var name = cm.getItemName(itemid);
					if(cm.查询孔数(i) == 2){
						if (i < 10) {
							文本 += "\t#L" + i + "# 0" + i + ")  #v" + itemid + "# #b" + name + "#k";
							for (var j = 21 - (name.getBytes().length / 3 * 2); j > 0; j--) {
								文本 += " ";
							}
							文本 += "孔 x #r" + cm.查询孔数(i) + "#k #l\r\n";
						} else {
							文本 += "\t#L" + i + "# " + i + ")  #v" + itemid + "# #b" + name + "#k";
							for (var j = 21 - (name.getBytes().length / 3 * 2); j > 0; j--) {
								文本 += " ";
							}
							文本 += "孔 x #r" + cm.查询孔数(i) + "#k #l\r\n";
						}
					}
                }
            }
            slot.push(i);
        }
        cm.sendSimple(文本);
    } else if (status == 1) {
		if(cm.查询孔数(slot[selection])>=3){
			cm.sendOk("不能作用。");
			cm.dispose();
			return;
		}
		//装备即将提升到的孔数
        var 孔数 = cm.查询孔数(slot[selection]) + 1;
        //装备的代码
        var idid = cm.getInventory(ls).getItem(slot[selection]).getItemId();
		cm.装备打孔(slot[selection]);
		cm.sendOk("恭喜你将 #i" + idid + "# #b#t" + idid + "##k 打 #r" + 孔数 + "#k 孔成功了。");
		if (孔数 >= 3) {
			cm.全服喇叭(6,"[全服公告]:恭喜玩家 " + cm.玩家名字() + " 将 " + cm.getItemName(idid) + " 提升至 " + 孔数 + " 孔，大家恭喜他吧。");
        }
		cm.新增账号记录("648元累计礼包");
        cm.dispose();
    }
}















