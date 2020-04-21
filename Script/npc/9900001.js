/********************************

ZEVMS 自由冒险岛Ver.027 
@小z QQ71447500

********************************/

function start() {
    status = -1;
    action(1, 0, 0);
}
var x = 0;


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
	
	var	cid = cm.getPlayer().getId();
	cm.getPlayer().设置目标(cid);
	var exp = cm.getPlayer().获取锻造信息("forgingExp",cid);
	
    if (status == 0) {
		var 文本 = "  #i4030000#  #i4030001#  #i4030010# #r#e< 锻造工艺 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
		文本 +=" #b说明#k:锻造等级越高能制作的东西也就越多，越好。如果你是通过其他玩家制作的，那么自己不会获取锻造经验，而帮你制作的玩家才会获得经验。\r\n\r\n";
		
		文本 +=" #b锻造师#k : "+cm.取角色名字(cid)+"("+cid+")\r\n";
		文本 +=" #b熟练度#k : "+exp+"\r\n";
		
		
		//if(cm.getPlayer().getGM()>=3){
			文本 +="#L2000##b装备锻造-全面#k#l\r\n";
			文本 +="#L2001##b装备锻造-体力#k#l\r\n";
		//}
		文本 +="#L700##b蝙蝠怪的利器#k#l\r\n";
		文本 +="#L701##b枫叶套服#k#l\r\n";
		文本 +="#L1##b合成矿石#k#l\r\n";
		文本 +="#L4##b合成母矿#k#l\r\n";
		文本 +="#L5##b合成水晶#k#l\r\n";
		文本 +="#L6##b合成材料#k#l\r\n";
		文本 +="#L2##b合成防御卷轴#k#l\r\n";
		文本 +="#L3##b合成武器卷轴#k#l\r\n";
		
		
		文本 +="#L104##b制作长枪#k#l\r\n";
		文本 +="#L105##b制作长矛#k#l\r\n";
		文本 +="#L200##b制作长杖#k#l\r\n";
		文本 +="#L300##b制作长弓#k#l\r\n";
		文本 +="#L301##b制作弩弓#k#l\r\n";
		文本 +="#L400##b制作拳甲#k#l\r\n";
		文本 +="#L401##b制作短刀#k#l\r\n";
		文本 +="#L500##b制作盾牌#k#l\r\n";
		文本 +="#L600##b制作鞋履#k#l\r\n";
		文本 +="#L100##b制作双手剑#k#l\r\n";
		文本 +="#L101##b制作单手剑#k#l\r\n";
		文本 +="#L102##b制作双手斧#k#l\r\n";
		文本 +="#L103##b制作单手斧#k#l\r\n";
		
		
		文本 +="#L1000##b制作召唤包#k#l\r\n";
		
		
		//文本 +="#L2000##b附加力量属性#k#l\r\n";
		cm.sendSimple(文本);
    } else if (status == 1) {
		switch (selection) {
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
				cm.openNpc(9900001, 4);
                break;
			case 5:
				cm.dispose();
				cm.openNpc(9900001, 5);
                break;
			default:
                cm.dispose();
				cm.openNpc(9900001, selection);
                break;
		}
    }
}
