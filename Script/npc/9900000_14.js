


var 界面功能 = [

	["\t\t   #L101#",").#b购买点券#k         #r比列 1:10#l#k\r\n"],
	["\t\t   #L100#",").#b购买会员#k         #r现金券 x 599#l#k\r\n"],
	["\t\t   #L102#",").#b角色更名#k         #r现金券 x 199#l#k\r\n"],
	["\t\t   #L103#",").#b角色变性#k         #r现金券 x 199#l#k\r\n"],
	["\t\t   #L1#",").#b金币福利#k         #r现金券 x 99#l#k\r\n"],
	["\t\t   #L1001#",").#b金币福利2#k        #r现金券 x 299#l#k\r\n"],
	["\t\t   #L1003#",").#b金币福利3#k        #r现金券 x 599#l#k\r\n"],
	["\t\t   #L2#",").#b点券福利#k         #r现金券 x 99#l#k\r\n"],
	["\t\t   #L1002#",").#b点券福利2#k        #r现金券 x 299#l#k\r\n"],
	["\t\t   #L10#",").#b魔法石礼包1#k      #r现金券 x 99#l#k\r\n"],
	["\t\t   #L11#",").#b魔法石礼包2#k      #r现金券 x 599#l#k\r\n"],
	["\t\t   #L20#",").#b淬炼石礼包1#k      #r现金券 x 99#l#k\r\n"],
	["\t\t   #L21#",").#b淬炼石礼包2#k      #r现金券 x 599#l#k\r\n"],
	["\t\t   #L30#",").#b魔刹石礼包1#k      #r现金券 x 99#l#k\r\n"],
	["\t\t   #L31#",").#b魔刹石礼包2#k      #r现金券 x 599#l#k\r\n"]
	
	]
	
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
	

    if (status == 0) {
		var 文本 = " #i4030000#  #i4030001#  #i4030010# #r#e< 现金商店 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
			文本 += "\t\t    #b现金券:#k"+cm.getPlayer().获取现金券()+"#k\r\n\r\n";
			
			
			for (var ii = 0; ii < 界面功能.length; ii++) {
				if(ii<9){
					文本 += ""+ 界面功能[ii][0]+ "0"+(ii+1)+""+界面功能[ii][1];
				}else{
					文本 += ""+ 界面功能[ii][0]+ ""+(ii+1)+""+界面功能[ii][1];
				}	
					
			}
			
			
			cm.sendSimple(文本);
    } else if (status == 1) {
        switch (selection) {
			//魔法石礼包99
			case 10:
				cm.dispose();
				cm.openNpc(9900000,14010);
                break;
			//魔法石礼包599
			case 11:
				cm.dispose();
				cm.openNpc(9900000,14011);
                break;
			//淬炼石礼包99
			case 20:
				cm.dispose();
				cm.openNpc(9900000,14020);
                break;
			//淬炼石礼包599
			case 21:
				cm.dispose();
				cm.openNpc(9900000,14021);
                break;
			//魔刹石礼包99
			case 30:
				cm.dispose();
				cm.openNpc(9900000,14030);
                break;
			//魔刹石礼包599
			case 31:
				cm.dispose();
				cm.openNpc(9900000,14031);
                break;
			//金币福利
			case 1:
				cm.dispose();
				cm.openNpc(9900000,14001);
                break;
			//金币福利2
			case 1001:
				cm.dispose();
				cm.openNpc(9900000,140101);
                break;
			//金币福利3
			case 1003:
				cm.dispose();
				cm.openNpc(9900000,140103);
                break;
			//点券福利
			case 2:
				cm.dispose();
				cm.openNpc(9900000,14002);
                break;
			//点券福利2
			case 1002:
				cm.dispose();
				cm.openNpc(9900000,140102);
                break;
			//会员
			case 100:
				cm.dispose();
				cm.openNpc(9900000,14100);
                break;
			//会员
			case 101:
				cm.dispose();
				cm.openNpc(9900000,14101);
                break;
			case 102:
				cm.dispose();
				cm.openNpc(9900000,52);
                break;
			case 103:
				cm.dispose();
				cm.openNpc(9900000,55);
                break;
			default:
                cm.dispose();
                break;
			
        }
    }
}
















