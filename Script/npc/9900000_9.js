

function start() {
    status = -1;
    action(1, 0, 0)
}

function action(mode, type, selection) {
    if (status <= 0 && mode <= 0) {
        cm.dispose();
        return
    }
    if (mode == 1) {
        status++
    } else {
        status--
    }
    if (cm.getInventory(1).isFull(3)) {
        cm.sendOk("请保证 #b装备栏#k 至少有4个位置。");
        cm.dispose();
        return;
    }
    if (cm.getInventory(2).isFull(3)) {
        cm.sendOk("请保证 #b消耗栏#k 至少有4个位置。");
        cm.dispose();
        return;
    }
    if (cm.getInventory(4).isFull(3)) {
        cm.sendOk("请保证 #b其他栏#k 至少有4个位置。");
        cm.dispose();
        return;
    }
	
	
    if (status <= 0) {
        var selStr = "   #i4030000#  #i4030001#  #i4030010# #r#e< 游戏商行 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n";

        selStr += " 这里是冒险岛游戏世界的商行，你可以在这里上架有价值的物品，或者购买所需要的物品，在这里你可以尽情自由交易，你上架被购买的物品，金币会被保存在商行，你可以随时进行提取。#k\r\n";

        selStr += "\t\t\t出 售 栏 : #b" + cm.判断拍卖出售位数量() + "#k   已使用 : #b" + cm.已经使用拍卖栏位() + "#k\r\n";
        selStr += "\t\t\t商行金币 : #b" + cm.判断拍卖存储金币(cm.getPlayer().getId()) + "#k \r\n";
        selStr += "\t\t#L0##b取回金币#k#l  ";
		if(cm.判断拍卖出售位数量() < 20){
			selStr += "\t\t#L101##b使用初级商行扩充券#k#l\r\n";
		}else if(cm.判断拍卖出售位数量()>=20 && cm.判断拍卖出售位数量() < 40){
			selStr += "\t\t#L101##b使用中级商行扩充券#k#l\r\n";
		}else if(cm.判断拍卖出售位数量()>=40 && cm.判断拍卖出售位数量() < 60){
			selStr += "\t\t#L101##b使用高级商行扩充券#k#l\r\n";
		}else if(cm.判断拍卖出售位数量()>=60 ){
			selStr += "\t\t#L101##b使用专业商行扩充券#k#l\r\n";
		}
		
        selStr += "\t\t#L100#[#r装备类#k]#l   #L2##b购买#k#l   #L1##b上架#k#l   #L5##b我的上架#k#l\r\n";
        selStr += "\t\t#L100#[#r消耗类#k]#l   #L4##b购买#k#l   #L3##b上架#k#l   #L6##b我的上架#k#l\r\n";
        selStr += "\t\t#L100#[#r材料类#k]#l   #L8##b购买#k#l   #L7##b上架#k#l   #L9##b我的上架#k#l\r\n";
		
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
			
			case 101:
			
				if(cm.判断拍卖出售位数量() < 20){
					if (cm.haveItem(4007000, 1)) {
						cm.增加拍卖出售栏(2);
						cm.gainItem(4007000,-1);
						cm.sendOk("恭喜你，商行栏扩充成功。");
						cm.dispose();
					}else{
						cm.sendOk("你没有扩充道具。");
						cm.dispose();
					}
				}else if(cm.判断拍卖出售位数量()>=20 && cm.判断拍卖出售位数量() < 40){
					if (cm.haveItem(4007001, 1)) {
						cm.增加拍卖出售栏(2);
						cm.gainItem(4007001,-1);
						cm.sendOk("恭喜你，商行栏扩充成功。");
						cm.dispose();
					}else{
						cm.sendOk("你没有扩充道具。");
						cm.dispose();
					}
				}else if(cm.判断拍卖出售位数量()>=40 && cm.判断拍卖出售位数量() < 60){
					if (cm.haveItem(4007002, 1)) {
						cm.增加拍卖出售栏(2);
						cm.gainItem(4007002,-1);
						cm.sendOk("恭喜你，商行栏扩充成功。");
						cm.dispose();
					}else{
						cm.sendOk("你没有扩充道具。");
						cm.dispose();
					}
				}else if(cm.判断拍卖出售位数量()>=60 ){
					if (cm.haveItem(4007003, 1)) {
						cm.增加拍卖出售栏(2);
						cm.gainItem(4007003,-1);
						cm.sendOk("恭喜你，商行栏扩充成功。");
						cm.dispose();
					}else{
						cm.sendOk("你没有扩充道具。");
						cm.dispose();
					}
				}
                
                break;
			
			
			
			
            case 0:
                var 金币 = cm.判断拍卖存储金币(cm.getPlayer().getId())
                cm.gainMeso(金币);
                cm.清空拍卖存储金币();
                cm.sendSimple("成功取回 #b" + 金币 + "#k 金币。");
                cm.dispose();

                break;
            case 1:
                cm.dispose();
                cm.openNpc(9900000, 9000);
                break;
            case 3:
                cm.dispose();
                cm.openNpc(9900000, 9001);
                break;
            case 7:
                cm.dispose();
                cm.openNpc(9900000, 9002);
                break;
            case 2:
                cm.dispose();
                cm.openNpc(9900000, 9010);
                break;
            case 4:
                cm.dispose();
                cm.openNpc(9900000, 9011);
                break;
            case 8:
                cm.dispose();
                cm.openNpc(9900000, 9012);
                break;
            case 5:
                cm.dispose();
                cm.openNpc(9900000, 9020);
                break;
            case 6:
                cm.dispose();
                cm.openNpc(9900000, 9021);
                break;
            case 9:
                cm.dispose();
                cm.openNpc(9900000, 9022);
                break;
            default:
                //cm.sendSimple("未开放。");
                cm.dispose();
                break;

        }
    }
}