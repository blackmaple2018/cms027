
function start() {
   status = -1;
   action(1, 0, 0);
}

var 售价 = 99;

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

			文本 += "  #b礼包:#k魔法石礼包\r\n";
			文本 += "  #b说明:#k购买后获取 #v4006000# #t4006000# x 50\r\n";
			文本 += "  #b售价:#k#r"+售价+"现金券#k\r\n";
			文本 += "  #b拥有现金券:#k"+cm.getPlayer().获取现金券()+"#k\r\n\r\n";
			
			文本 += "  #L1##b返回#l  #L2#购买#k#l\r\n";
			cm.sendSimple(文本);
    } else if (status == 1) {
        switch (selection) {
			case 2:
				if (cm.getInventory(4).isFull()) {
					cm.sendOk("请保证 #b其它栏#k 至少有2个位置。");
					cm.dispose();
					return;
				} 

				if(cm.getPlayer().获取现金券()>=售价){
					cm.getPlayer().gainItem(4006000,50,false);
					cm.getPlayer().修改现金券(-售价);					
					cm.getPlayer().dropMessage("现金券-"+售价+"");
					cm.sendOk("恭喜你购买成功。");
				}else{
					cm.sendOk("你的现金券不够，无法购买。");
				}
				cm.dispose();
                break;
			case 1:
				cm.dispose();
				cm.openNpc(9900000,14);
                break;
			
        }
    }
}
















