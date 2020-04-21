
function start() {
   status = -1;
   action(1, 0, 0);
}



function action(mode, type, selection) {
    if (status == 0 && mode == 0) {
        cm.dispose();
        return;
    } else if (mode == 0 && selection == -1) {
		cm.dispose();
        return;
	}
	
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
	var 免费 = 50;
	if(免费 - cm.getbosslog("仓库手术费") >= 0){
		var 免费次数 = 免费 - cm.getbosslog("仓库手术费");
	}else{
		var 免费次数 = 0;
	}
	
    if (status == 0) {
		var 文本 = "  #i4030000#  #i4030001#  #i4030010# #r#e< 随身仓库 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
			文本 +="随身仓库，可以用于存储消耗，材料等物品，物品保管费用为每分钟 #r2#k 金币，在你取出来的时候，需要缴纳这笔费用，每笔保管费用最多只收取#r1000#k金币，剩下免费取"+免费次数+"次。\r\n";
			文本 +="\r\n\r\n";
			文本 +="\t\t #b保管占位#k:"+cm.总共保管费包裹()+" | #b总计保管费用/金币#k:"+cm.总共保管费()+"\r\n\r\n";
			文本 +="\t\t\t\t  #L1##b存入物品到仓库#k#l\r\n";
			文本 +="\t\t\t\t  #L2##b取出物品到仓库#k#l\r\n";
			
			文本 +="\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n.";
			cm.sendSimple(文本);
    } else if (status == 1) {
        switch (selection) {
			case 1:
				cm.dispose();
				cm.openNpc(9900000,161);
                break;
			case 2:
				cm.dispose();
				cm.openNpc(9900000,162);
                break;
			default:
                cm.dispose();
                break;
        }
    }
}
