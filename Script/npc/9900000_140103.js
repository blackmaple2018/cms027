
function start() {
   status = -1;
   action(1, 0, 0);
}

var 售价 = 599;

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

			文本 += "  #b礼包:#k金币福利3\r\n";
			文本 += "  #b说明:#k购买后可在增值服务项目里，每天领取 #r金币 x 100W#k，持续7天。\r\n";
			文本 += "  #b售价:#k#r"+售价+"现金券#k\r\n";
			文本 += "  #b拥有现金券:#k"+cm.getPlayer().获取现金券()+"#k\r\n\r\n";
			
			文本 += "  #L1##b返回#l  #L2#购买#k#l\r\n";
			cm.sendSimple(文本);
    } else if (status == 1) {
        switch (selection) {
			case 2:
				if(cm.getPlayer().获取现金券()>=售价){
					cm.getPlayer().修改现金券(-售价);
					cm.getPlayer().修改增值服务数据("meso3",7);
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















