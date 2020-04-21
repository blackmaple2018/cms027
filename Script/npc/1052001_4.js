/********************************

ZEVMS 自由冒险岛Ver.027 
@小z QQ71447500

********************************/




var 力量水晶 = 4005000;
var 智慧水晶 = 4005001;
var 敏捷水晶 = 4005002;
var 幸运水晶 = 4005003;

var status = 0;
function start() {
    status = -1;
    action(1, 0, 0)
}

function action(mode, type, selection) {
    if (status <= 0 && mode <= 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++
    } else {
        status--
    }
	var 等级 = cm.getPlayer().getMaxlevel();
	var 杀怪 = cm.getPlayer().杀怪数量();
	var 在线 = cm.总在线();
	var 金币 = cm.判断金币();
	var 需要杀怪 = 50000+((等级-100)*10000);
	var 需要在线 = 1440+((等级-100)*1440);
	var 需要金币 = 1000000+((等级-100)*1000000);
	if(等级>=110){
		需要杀怪 += 需要杀怪/2;
		需要在线 += 需要在线/2;
		需要金币 += 需要金币/2;
	} 
	if(等级>=120){
		需要杀怪 += 需要杀怪/2;
		需要在线 += 需要在线/2;
		需要金币 += 需要金币/2;
	} 
	if(等级>=130){
		需要杀怪 += 需要杀怪/2;
		需要在线 += 需要在线/2;
		需要金币 += 需要金币/2;
	} 
	if(等级>=140){
		需要杀怪 += 需要杀怪/2;
		需要在线 += 需要在线/2;
		需要金币 += 需要金币/2;
	}
	if(等级>=150){
		需要杀怪 += 需要杀怪/2;
		需要在线 += 需要在线/2;
		需要金币 += 需要金币/2;
	}
	if(等级>=160){
		需要杀怪 += 需要杀怪/2;
		需要在线 += 需要在线/2;
		需要金币 += 需要金币/2;
	}
	
	if(需要金币>=2100000000){
		需要金币 = 2100000000;
	}
	if (status <= 0) {
        var selStr = "";
		selStr += "你当前等级是 #b"+cm.getLevel()+"#k 上限是 #b"+等级+"#k 级，突破等级上限需要完成以下任务，即可提升。步入强者的境界，需要不断的累计经验和财富，以至于不断的攀登，突破自我。\r\n\r\n";
		selStr +=" #r突破条件#k:\r\n";
		
		if(杀怪>=需要杀怪){
			selStr +=" #b击杀怪物#k: "+需要杀怪+" / "+杀怪+" #b只#k [#g√#k]\r\n";
		}else{
			selStr +=" #b击杀怪物#k: "+需要杀怪+" / "+杀怪+" #b只#k [#r×#k]\r\n";
		}
		
		if(在线>=需要在线){
			selStr +=" #b在线时间#k: "+需要在线+" / "+在线+" #b分钟#k [#g√#k]\r\n";
		}else{
			selStr +=" #b在线时间#k: "+需要在线+" / "+在线+" #b分钟#k [#r×#k]\r\n";
		}
		
		if(金币>=需要金币){
			selStr +=" #b游戏金币#k: "+需要金币+" / "+金币+" #b金币#k [#g√#k]\r\n";
		}else{
			selStr +=" #b游戏金币#k: "+需要金币+" / "+金币+" #b金币#k [#r×#k]\r\n";
		}
		
		if(等级>=110){
			//战士职业群
			if((cm.getJobId() == 100 || cm.getJobId() == 110|| cm.getJobId() == 111||cm.getJobId() == 112 || cm.getJobId() == 120 ||cm.getJobId() == 121||cm.getJobId() == 122 || cm.getJobId() == 130 ||cm.getJobId() == 131||cm.getJobId() == 132 )&& 等级>=100){
				if (cm.haveItem(力量水晶,(等级-100))) {
					selStr +=" #b力量水晶#k: "+(等级-100)+" / "+cm.getItemQuantity(力量水晶)+" #k[#g√#k]\r\n";
				}else{
					selStr +=" #b力量水晶#k: "+(等级-100)+" / "+cm.getItemQuantity(力量水晶)+" #k[#r×#k]\r\n";
				}
			}
			//魔法师职业群
			if((cm.getJobId() == 200 || cm.getJobId() == 210|| cm.getJobId() == 211||cm.getJobId() == 212 || cm.getJobId() == 220 ||cm.getJobId() == 221||cm.getJobId() == 222 || cm.getJobId() == 230 ||cm.getJobId() == 231||cm.getJobId() == 232 )&& 等级>=100){
				if (cm.haveItem(智慧水晶,(等级-100))) {
					selStr +=" #b智慧水晶#k: "+(等级-100)+" / "+cm.getItemQuantity(智慧水晶)+" #k [#g√#k]\r\n";
				}else{
					selStr +=" #b智慧水晶#k: "+(等级-100)+" / "+cm.getItemQuantity(智慧水晶)+" #k [#r×#k]\r\n";
				}
			}
			//弓箭手职业群
			if((cm.getJobId() == 300 || cm.getJobId() == 310|| cm.getJobId() == 311||cm.getJobId() == 312 || cm.getJobId() == 320 ||cm.getJobId() == 321||cm.getJobId() == 322 )&& cm.getLevel()>=100){
				if (cm.haveItem(敏捷水晶,(等级-100))) {
					selStr +=" #b敏捷水晶#k: "+(等级-100)+" / "+cm.getItemQuantity(敏捷水晶)+" #k [#g√#k]\r\n";
				}else{
					selStr +=" #b敏捷水晶#k: "+(等级-100)+" / "+cm.getItemQuantity(敏捷水晶)+" #k [#r×#k]\r\n";
				}
			}
			//飞侠职业群
			if((cm.getJobId() == 400 || cm.getJobId() == 410|| cm.getJobId() == 411||cm.getJobId() == 412 || cm.getJobId() == 420 ||cm.getJobId() == 421||cm.getJobId() == 422 )&& 等级 >=110){
				if (cm.haveItem(幸运水晶,(等级-100))) {
					selStr +=" #b幸运水晶#k: "+(等级-100)+" / "+cm.getItemQuantity(幸运水晶)+" #k [#g√#k]\r\n";
				}else{
					selStr +=" #b幸运水晶#k: "+(等级-100)+" / "+cm.getItemQuantity(幸运水晶)+" #k [#r×#k]\r\n";
				}
			}
		}
		
		selStr +="\r\n";
		selStr +=" #r突破奖励#k:\r\n";
		selStr +=" 泡点经验 + #r"+等级+"#k  等级上限 + #r1#k \r\n";
		selStr +=" 能力点 + #r5#k \r\n";
		selStr += "\r\n#L1##r开始突破#k#l\r\n";
		selStr +="\r\n\r\n\r\n\r\n\r\n\r\n\r\n.";
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
            case 1:
				var 收取水晶 = -1;
				if(等级>=110){
					//战士职业群
					if((cm.getJobId() == 100 || cm.getJobId() == 110|| cm.getJobId() == 111||cm.getJobId() == 112 || cm.getJobId() == 120 ||cm.getJobId() == 121||cm.getJobId() == 122 || cm.getJobId() == 130 ||cm.getJobId() == 131||cm.getJobId() == 132 )&& 等级>=100){
						if (!cm.haveItem(力量水晶,(等级-100))) {
							cm.sendOk("缺少水晶。");
							cm.dispose();
							return;
						}else{
							收取水晶 = 0;
						}
					}
					//魔法师职业群
					if((cm.getJobId() == 200 || cm.getJobId() == 210|| cm.getJobId() == 211||cm.getJobId() == 212 || cm.getJobId() == 220 ||cm.getJobId() == 221||cm.getJobId() == 222 || cm.getJobId() == 230 ||cm.getJobId() == 231||cm.getJobId() == 232 )&& 等级>=100){
						if (!cm.haveItem(智慧水晶,(等级-100))) {
							cm.sendOk("缺少水晶。");
							cm.dispose();
							return;
						}else{
							收取水晶 = 1;
						}
					}
					//弓箭手职业群
					if((cm.getJobId() == 300 || cm.getJobId() == 310|| cm.getJobId() == 311||cm.getJobId() == 312 || cm.getJobId() == 320 ||cm.getJobId() == 321||cm.getJobId() == 322 )&& cm.getLevel()>=100){
						if (!cm.haveItem(敏捷水晶,(等级-100))) {
							cm.sendOk("缺少水晶。");
							cm.dispose();
							return;
						}else{
							收取水晶 = 2;
						}
					}
					//飞侠职业群
					if((cm.getJobId() == 400 || cm.getJobId() == 410|| cm.getJobId() == 411||cm.getJobId() == 412 || cm.getJobId() == 420 ||cm.getJobId() == 421||cm.getJobId() == 422 )&& 等级 >=110){
						if (!cm.haveItem(幸运水晶,(等级-100))) {
							cm.sendOk("缺少水晶。");
							cm.dispose();
							return;
						}else{
							收取水晶 = 3;
						}
					}
				}
				
				if(杀怪>=需要杀怪 && 在线>=需要在线 && 金币>=需要金币){
					cm.gainMeso(-需要金币);
					cm.getPlayer().set泡点经验((cm.getPlayer().get泡点经验()+(等级)));
					cm.getPlayer().setMaxlevel((cm.getPlayer().getMaxlevel()+1));
					cm.getPlayer().getStat().gainAp(5);
					if(等级>=110){
						cm.gainItem((4005000+收取水晶),-(等级-100));
					}
					cm.sendOk("恭喜你等级突破成功。");
				}else{
					cm.sendOk("你不满足条件，无法突破。");
				}
				cm.dispose();
				break;

			default:
				break;
        }
    }
}

