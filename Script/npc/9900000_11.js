/********************************

ZEVMS 自由冒险岛Ver.027 
@小z QQ71447500

********************************/

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
	var cid = 0;
	if(cm.getPlayer().查看目标()==0){
		cid = cm.getPlayer().getId();
	}else{
		cid = cm.getPlayer().查看目标();
	}
	var exp = cm.getPlayer().获取锻造信息("forgingExp",cid);
    if (status == 0) {
        var 文本 = "  #i4030000#  #i4030001#  #i4030010# #r#e< 个人信息 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
		文本 += ""+cm.个人信息(cm.getPlayer().getMap().getMapObject(cid))+"\r\n";
		
		
		文本 +="\t----------------------------------------------\r\n";
		文本 +="\t\t锻造工艺；#k#n\r\n";
		
		文本 += "\t\t#b锻造师#k : "+cm.取角色名字(cid)+"("+cid+")\r\n";
		文本 += "\t\t#b熟练度#k : "+exp+"\r\n";
		文本 +="\t----------------------------------------------\r\n";
		//if(cm.getPlayer().getMapId()==180000000){
			
		//}
		//文本 +="       #L1999##b手工台#k#l\r\n";
		文本 +="       #L2000##b装备锻造-全面#k#l\r\n";
		文本 +="       #L2001##b装备锻造-体力#k#l\r\n";
		文本 +="       #L700##b蝙蝠怪的利器#k#l\r\n";
		文本 +="       #L701##b枫叶套服#k#l\r\n";
		文本 +="       #L1##b合成矿石#k#l\r\n";
		文本 +="       #L4##b合成母矿#k#l\r\n";
		文本 +="       #L5##b合成水晶#k#l\r\n";
		文本 +="       #L6##b合成材料#k#l\r\n";
		文本 +="       #L2##b合成防御卷轴#k#l\r\n";
		文本 +="       #L3##b合成武器卷轴#k#l\r\n";
		文本 +="       #L104##b制作长枪#k#l\r\n";
		文本 +="       #L105##b制作长矛#k#l\r\n";
		文本 +="       #L200##b制作长杖#k#l\r\n";
		文本 +="       #L300##b制作长弓#k#l\r\n";
		文本 +="       #L301##b制作弩弓#k#l\r\n";
		文本 +="       #L400##b制作拳甲#k#l\r\n";
		文本 +="       #L401##b制作短刀#k#l\r\n";
		文本 +="       #L500##b制作盾牌#k#l\r\n";
		文本 +="       #L600##b制作鞋履#k#l\r\n";
		文本 +="       #L100##b制作双手剑#k#l\r\n";
		文本 +="       #L101##b制作单手剑#k#l\r\n";
		文本 +="       #L102##b制作双手斧#k#l\r\n";
		文本 +="       #L103##b制作单手斧#k#l\r\n";
		文本 +="       #L1000##b制作召唤包#k#l\r\n";
		
		
		
		文本 +="\r\n";
		if(cm.getPlayer().师傅()==0){
			if(cm.getLevel() >= 10 && cm.getLevel() < 70){
				if(cm.getPlayer().getMap().getMapObject(cid).getLevel()>70){
					文本 +="       #L500##b拜师#k#l\r\n";
				}
			}
		}
		if(cm.getPlayer().师傅()>0){
			文本 +="       #L501##b解除师徒关系#k#l\r\n";
		}
		if(cm.getPlayer().getId() == cm.getPlayer().getMap().getMapObject(cid).师傅()){
			文本 +="       #L510##b逐出师门#k#l\r\n";
		}
		文本 +="\r\n\r\n\r\n.";
        cm.sendSimple(文本);
    } else if (status == 1) {
        switch (selection) {
			case 500:
				if(cm.getLevel() >= 10 && cm.getLevel() < 70){
					if(cm.getPlayer().getMap().getMapObject(cid).getLevel()>70){
						if(cm.getbosslog("拜师")==0){
							cm.getPlayer().拜师(cm.getPlayer().查看目标());
							cm.getPlayer().getMap().getMapObject(cid).dropMessage(1, ""+cm.玩家名字()+" 成为你的徒弟。");
							cm.sendOk("恭喜你拜师成功。");
						}else{
							cm.sendOk("你今天已解除过师徒关系，请明日在拜师。");
						}
					}
				}
				cm.dispose();
                break;
			case 501:
				cm.getPlayer().拜师(0);
				cm.sendOk("解除师徒关系成功，拜师需要明天才可以。");
				cm.setbosslog("拜师");
				cm.dispose();
                break;
			case 510:
				if(cm.getPlayer().getId() == cm.getPlayer().getMap().getMapObject(cid).师傅()){
					cm.getPlayer().getMap().getMapObject(cid).拜师(0);
					cm.getPlayer().getMap().getMapObject(cid).dropMessage(1, ""+cm.玩家名字()+" 将你逐出师门。");
					cm.sendOk("你已经将他逐出师门，解除师徒关系。");
				}
				cm.dispose();
                break;
			case 1:
				cm.dispose();
                cm.openNpc(9900001, 1);
                break;
			case 2:
				cm.dispose();
                cm.openNpc(9900001, 2);
                break;
			case 3:
				cm.dispose();
                cm.openNpc(9900001, 3);
                break;
			case 4:
				cm.dispose();
                cm.openNpc(9900001, 3);
                break;
			default:
                cm.dispose();
				cm.openNpc(9900001, selection);
                break;
        }
    }
}

