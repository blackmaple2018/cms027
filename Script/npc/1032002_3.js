var slot = Array();
var ls = 1;
var 清洗材料 = 4006000;
var 清洗金币 = 22500;
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
		var 文本 = "*—— #b[ #r装备附魔#b ]#k ———————————————————————————————————————————*\r\n\r\n";
			文本 +="\t需要材料#v"+清洗材料+"# #t"+清洗材料+"# x #b5#k   #v4031039# 金币 x #b"+清洗金币+"#k\r\n";
			文本 +="\t选择你要#r清洗#k的装备;\r\n";
		for (var i = 0; i < 96; i++) {
			if (cm.getInventory(ls).getItem(i) != null) {
				var itemid = cm.getInventory(ls).getItem(i).getItemId();
				if(!cm.isCash(cm.getInventory(ls).getItem(i).getItemId())){
					var name = cm.getItemName(itemid);
					if(i < 10){
						文本 += "\t#L" + i + "# 0"+i+")  #v" + itemid + "# #b" + name + "#k";
						for (var j = 18 - (name.getBytes().length/3*2); j > 0; j--) {
                            文本 += " ";
                        }
						文本 += "孔 x #r"+cm.查询孔数(i)+"#k|魔 x #r"+(cm.查询孔数(i)-cm.查询附魔(i))+"#k#l\r\n";
					}else{
						文本 += "\t#L" + i + "# "+i+")  #v" + itemid + "# #b" + name + "#k";
						for (var j = 18 - (name.getBytes().length/3*2); j > 0; j--) {
                            文本 += " ";
                        }
						文本 += "孔 x #r"+cm.查询孔数(i)+"#k|魔 x #r"+(cm.查询孔数(i)-cm.查询附魔(i))+"#k#l\r\n";
					}
				}
			}
			slot.push(i);
		}
		cm.sendSimple(文本);
    } else if (status == 1) {
		if (!cm.haveItem(清洗材料, 5)) {
			cm.sendOk("你没有 #i"+清洗材料+"# #b#t"+清洗材料+"##k x 5 ，我不能为你服务。");
			cm.dispose();
			return;
		}
		if(cm.getMeso() < 清洗金币){
			cm.sendOk("你没有 "+清洗金币+" 金币，我不能为你服务。");
			cm.dispose();
			return;
		}
		cm.gainMeso(-清洗金币);
		cm.gainItem(清洗材料,-5);
		cm.装备清洗(slot[selection]);
		cm.sendOk("清洗成功。");		
		cm.dispose();
    }
}















