

var ca = java.util.Calendar.getInstance();
var year = ca.get(java.util.Calendar.YEAR);
var month = ca.get(java.util.Calendar.MONTH) + 1;
var day = ca.get(java.util.Calendar.DATE);
var hour = ca.get(java.util.Calendar.HOUR_OF_DAY);
var minute = ca.get(java.util.Calendar.MINUTE);
var second = ca.get(java.util.Calendar.SECOND);
var weekday = ca.get(java.util.Calendar.DAY_OF_WEEK);

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
	//cm.showWrongEffect(1);
    var 最小人数 = 3;
    var 最大人数 = 6;
    var 最低等级 = 20;
    var 最高等级 = 250;
	var 通关次数 = cm.getPlayer().获取个人记录("feiqifubentongguan1");
	var 今日通关次数 = cm.getbosslog("feiqifubentongguan1");
    if (status == 0) {
        var 文本 = "#i4030000#  #i4030001#  #i4030010# #r#e< 废弃沼泽副本 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
        if (cm.getParty() == null) {
            文本 += "\t\t#r提示;你当前尚未组队，无法进行副本。#k#n\r\n\r\n";
        } else if (!cm.isLeader()) {
            文本 += "\t\t#r提示;你不是队长，无法进行副本。#k#n\r\n\r\n";
        } else if (cm.isLeader()) {
            文本 += "\t\t#r提示;你是队长，可以召集队友进行副本。#k#n\r\n\r\n";
        }
		文本 += "\t\t副本中只有三种组合推算关卡，每关都会获取经验奖励，通过三关后可以领取丰富的奖励。根据选择副本的难度，奖励会有所不同。每日到达10次后将不再获得通关奖励。\r\n\r\n";
        文本 += "\t\t副本要求；\r\n";
        文本 += "\t\t人数要求；#b" + 最小人数 + " - " + 最大人数 + "#k      等级要求；#b" + 最低等级 + " - " + 最高等级 + "\r\n";
	
		
		文本 += "\t\t#k总计通关；#b" + 通关次数 + " #k";
		var x = ""+通关次数+"";
		for (var j = 10 - x.length; j > 0; j--) {
			文本 += " ";
		}
		文本 += "今日通关；#b" + 今日通关次数 + " #k\r\n";
        if (cm.getParty() != null) {
			文本 += "\r\n";
            文本 += "\t\t#L2##b开始副本[普通]#l#k\r\n";
			文本 += "\t\t#L3##r开始副本[困难]#l#k";
			文本 += "\r\n";
        }
		
		文本 += "\t\t#L10##b废弃打猎场#l#k\r\n";
		

		文本 += "\r\n\r\n";
		文本 += "\t\t#k第一关\r\n";
		文本 += "\t\t[普通] 经验 + #b2000#k 金币 + #b5000#k\r\n";
		文本 += "\t\t[困难] 经验 + #b2000*5#k 金币 + #b5000*5#k\r\n";
		
		文本 += "\r\n\t\t第二关\r\n";
		文本 += "\t\t[普通] 经验 + #b4000#k 金币 + #b10000#k\r\n";
		文本 += "\t\t[困难] 经验 + #b4000*5#k 金币 + #b10000*5#k\r\n";
		
		文本 += "\r\n\t\t第三关\r\n";
		文本 += "\t\t[普通] 经验 + #b8000#k 金币 + #b12000#k\r\n";
		文本 += "\t\t[困难] 经验 + #b8000*5#k 金币 + #b12000*5#k\r\n";
		
		文本 += "\r\n\t\t通  关\r\n";
		文本 += "\t\t[普通] 随机材料\r\n";
		文本 += "\t\t[困难] 随机材料\r\n";

        cm.sendSimple(文本);
		//cm.sendOk("当前暂时无法进行副本。");
       
    } else if (status == 1) {
        switch (selection) {
			case 10:
				cm.warp(103000800,1);
				cm.dispose();
                break;
			case 2:
            case 3:
                if (cm.getParty() != null) {
					
					if (!cm.isLeader()) {
						cm.sendOk("你不是队长，无法进行副本。");
                        cm.dispose();
                        return;
					}
					
                    if (cm.getParty().getMembers().size() < 最小人数) {
                        cm.sendOk("参加人数不足，无法进行。");
                        cm.dispose();
                        return;
                    }
					
                    if (团队() > 0) {
                        /*if (团队() == 1 || 团队() == 2) {
                            cm.sendOk("请集合你的队友。");
                            cm.dispose();
                            return;
                        }
                        if (团队() == 3) {
                            cm.sendOk("队伍中有玩家等级低于要求。");
                            cm.dispose();
                            return;
                        }*/
						if (团队() == 4) {
                            cm.sendOk("队伍中有人处于交易状态。");
                            cm.dispose();
                            return;
                        }
                    }
					if(selection==3){
						if (团队2() == 3) {
							cm.sendOk("困难模式最低 35 级入内。");
							cm.dispose();
							return;
						}
					}
					/*for (var ii = 0; ii <= 5; ii++) {
						if (cm.getMap(103000800+ii).getCharactersSize() > 0){
							cm.sendOk("副本已经有人在挑战了。");
                            cm.dispose();
                            return;
						}
					}*/
					cm.Partyfeiqunandu(0);
					if(selection==3){
						cm.Partyfeiqunandu(1);
						cm.PartydropMessage(1,"友情提示\r\n困难模式开启，组合失败次数过多会重新开始，奖励将会翻5倍。");
					}
					if(selection==2){
						var 随机 = Math.floor(Math.random() * 100);
						if(随机 <= 10 ){
							cm.Partyfeiqunandu(10);
							cm.PartydropMessage(1,"友情提示\r\n奇遇模式开启，组合失败时会出现蝙蝠干扰，最终将会获取额外奖励。");
						}
					}
					cm.warpParty(103000801,1);
                }else{
					cm.sendOk("参加人数不足，无法进行。");
				}
				cm.dispose();
                break;

            default:
                cm.dispose();
                break;
        }
    }
}


function 团队() {
    var 队伍 = cm.getParty().getMembers();
    var mapId = cm.getPlayer().getMapId();
    var Channel = cm.getPlayer().getClient().getChannel();
    var valid = 0;
    var it = 队伍.iterator();
    while (it.hasNext()) {
        var cPlayer = it.next();
        if (Channel != cPlayer.getChannel()) {
            valid = 1;
        }
        if (mapId != cPlayer.getMapId()) {
            valid = 2;
        }
        if (cPlayer.getLevel() < 20) {
            valid = 3;
        }

    }
    return valid;
}

function 团队2() {
    var 队伍 = cm.getParty().getMembers();
    var mapId = cm.getPlayer().getMapId();
    var Channel = cm.getPlayer().getClient().getChannel();
    var valid = 0;
    var it = 队伍.iterator();
    while (it.hasNext()) {
        var cPlayer = it.next();
        if (Channel != cPlayer.getChannel()) {
            valid = 1;
        }
        if (mapId != cPlayer.getMapId()) {
            valid = 2;
        }
        if (cPlayer.getLevel() < 35) {
            valid = 3;
        }
    }
    return valid;
}










