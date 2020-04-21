var slot = Array();
var ls = 1;
var x;
var 自然石 = false;
var 祝福石 = false;
var 打孔材料 = 4006002;
var 打孔金币 = 5000;
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
        //初始化
        自然石 = false;
        祝福石 = false;
        //自然石
        if (cm.getInventory(4).getItem(1) != null) {
            if (cm.getInventory(4).getItem(1).getItemId() == 4006004) {
                cm.getPlayer().dropMessage(5, "" + cm.getItemName(4006004) + " 已经准备，防止装备损坏。");
                if (cm.haveItem(4006004, 1)) {
                    自然石 = true;
                }
            }
        }
		//祝福石
		if (cm.getInventory(4).getItem(2) != null) {
            if (cm.getInventory(4).getItem(2).getItemId() == 4006005) {
                cm.getPlayer().dropMessage(5, "" + cm.getItemName(4006005) + " 已经准备。增加1%-5%打孔成功率");
                if (cm.haveItem(4006005, 1)) {
                    祝福石 = true;
                }
            }
        }
        var 文本 = "*—— #b[ #r装备打孔#b ]#k ———————————————————————————————————————————*\r\n\r\n";
			文本 +="\t需要材料#v"+打孔材料+"# #t"+打孔材料+"# x #b1#k   #v4031039# 金币 x #b"+打孔金币+"#k\r\n";
			文本 +="\t选择你要#r打孔#k的装备;\r\n";
        for (var i = 0; i < 96; i++) {
            if (cm.getInventory(ls).getItem(i) != null) {
                var itemid = cm.getInventory(ls).getItem(i).getItemId();
                if (!cm.isCash(cm.getInventory(ls).getItem(i).getItemId())) {
                    var name = cm.getItemName(itemid);
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
            slot.push(i);
        }
        cm.sendSimple(文本);
	} else if (status == 1) {
		x = slot[selection];
		cm.sendYesNo("#e#r重要提示，请认真阅读#k#n\r\n\r\n当前装备 #b"+cm.查询孔数(slot[selection])+"#k 孔\r\n装备打至 #b3#k 孔后失败将会损坏装备。\r\n#b祝福石#k(增加1%打孔成功率) #r"+祝福石+"#k\r\n#b自然石#k(保护装备不损坏) #r"+自然石+"#k\r\n\r\n是否继续？");
    } else if (status == 2) {
		
		if (!cm.haveItem(打孔材料, 1)) {
			cm.sendOk("你没有 #i"+打孔材料+"# #b#t"+打孔材料+"##k x 1 ，我不能为你服务。");
			cm.dispose();
			return;
		}
		if(cm.getMeso() < 打孔金币){
			cm.sendOk("你没有 "+打孔金币+" 金币，我不能为你服务。");
			cm.dispose();
			return;
		}
		
        //装备即将提升到的孔数
        var 孔数 = cm.查询孔数(x) + 1;
        //100随机数
        var 随机百分率 = Math.floor(Math.random() * 100);
        //装备的代码
        var idid = cm.getInventory(ls).getItem(x).getItemId();
        var 成功率;
        //孔数成功率设定区域
        switch (孔数) {
            case 1:
                成功率 = 50;
                break;
            case 2:
                成功率 = 30;
                break;
            case 3:
                成功率 = 5;
                break;
            case 4:
                成功率 = 3;
                break;
            case 5:
                成功率 = 2;
                break;
            default:
                成功率 = 1;
                break;
        }
		
		if(祝福石 == true){
			if (cm.haveItem(4006005, 1)) {
				cm.gainItem(4006005, -1);
				成功率+=1;
			} 
		}
		
		cm.gainMeso(-打孔金币);
		cm.gainItem(打孔材料,-1);
		var 成功 = 0;
		if(孔数 >= 2){
			var 随机百分率2 = Math.floor(Math.random() * 99);
			if(随机百分率2 >= 5){
				成功 = 1;
			}
			if(孔数 >= 4){
				var 随机百分率3 = Math.floor(Math.random() * 99);
				if(随机百分率3 >= 1){
					成功 = 1;
				}
			}
		}
		
		if(孔数 >= 2){
		if(cm.判断消费() < 500){
			if(cm.判断消费() == 0){
				if(Math.floor(Math.random() * 99 ) <= 70){
					成功 = 1;
				}
			}else if( cm.判断消费() > 0 && cm.判断消费() < 100){
				if(Math.floor(Math.random() * 99 ) <= 60){
					成功 = 1;
				}
			}else if( cm.判断消费() >= 100 && cm.判断消费() < 200){
				if(Math.floor(Math.random() * 99 ) <= 50){
					成功 = 1;
				}
			}else if( cm.判断消费() >= 200 && cm.判断消费() < 300){
				if(Math.floor(Math.random() * 99 ) <= 40){
					成功 = 1;
				}
			}else if( cm.判断消费() >= 300 && cm.判断消费() < 400){
				if(Math.floor(Math.random() * 99 ) <= 30){
					成功 = 1;
				}
			}else if( cm.判断消费() >= 400 && cm.判断消费() < 500){
				if(Math.floor(Math.random() * 99 ) <= 20){
					成功 = 1;
				}
			}
		}
		}
		if(cm.判断消费() >= 648){
			if(cm.getbosslog("充值金额打孔") <= 2){
				if(孔数 <= 2){
					成功率 = 10;
					if(Math.floor(Math.random() * 99 ) <= 30){
						成功 = 0;
					}
					cm.setbosslog("充值金额打孔");
				}
			}
		}
		
        if (随机百分率 <= 成功率 && 成功 == 0) {
            cm.装备打孔(x);
            cm.sendOk("恭喜你将 #i" + idid + "# #b#t" + idid + "##k 打 #r" + 孔数 + "#k 孔成功了。");
            if (孔数 >= 3) {
                cm.全服喇叭(6,"[全服公告]:恭喜玩家 " + cm.玩家名字() + " 将 " + cm.getItemName(idid) + " 提升至 " + 孔数 + " 孔，大家恭喜他吧。");
            }
        } else if (孔数 >= 3) {
            if (自然石 == true) {
                if (cm.haveItem(4006004, 1)) {
                    cm.gainItem(4006004, -1);
                    cm.sendOk("很遗憾你将 #i" + idid + "# #b#t" + idid + "##k 打 #r" + 孔数 + "#k 孔失败了，由于某种特殊的保护，装备没有损坏。");
					cm.全服喇叭(6,"[全服公告]:玩家 " + cm.玩家名字() + " 将 " + cm.getItemName(idid) + " 提升至 " + 孔数 + " 孔失败了。");
                } else {
                    cm.cleanItemBySlot(x, 1, 1);
                    cm.sendOk("很遗憾你将 #i" + idid + "# #b#t" + idid + "##k 打 #r" + 孔数 + "#k 孔失败了，装备损坏。");
					cm.全服喇叭(6,"[全服公告]:玩家 " + cm.玩家名字() + " 将 " + cm.getItemName(idid) + " 提升至 " + 孔数 + " 孔失败了，装备损坏。");
                }
            } else {
                cm.cleanItemBySlot(x, 1, 1);
                cm.sendOk("很遗憾你将 #i" + idid + "# #b#t" + idid + "##k 打 #r" + 孔数 + "#k 孔失败了，装备损坏。");
				cm.全服喇叭(6,"[全服公告]:玩家 " + cm.玩家名字() + " 将 " + cm.getItemName(idid) + " 提升至 " + 孔数 + " 孔失败了，装备损坏。");
            }
        } else {
            cm.sendOk("很遗憾你将 #i" + idid + "# #b#t" + idid + "##k 打 #r" + 孔数 + "#k 孔失败了。");
        }
		if (cm.getPlayer().getbosslog("每日任务_装备打孔") <= 0 ) {
			cm.setbosslog("每日任务_装备打孔");
		}
        cm.dispose();
    }
}















