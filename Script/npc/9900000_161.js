var itemss;
var slot = Array();
var lz = 2;
var ls = 4;
var x;
var x2;
var item;
var cout;
var Max;
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
	//背包存档
	cm.saveDatabaseItem();
    if (status == 0) {
		var avail = "";
		//消耗栏
		for (var i = 0; i < 96; i++) {
			//位置不为空
			if (cm.getInventory(lz).getItem(i) != null) {
				//不是现金物品，不是任务物品
				if(!cm.物品任务类型(cm.getInventory(lz).getItem(i).getItemId())&&!cm.物品时装类型(cm.getInventory(lz).getItem(i).getItemId())){
					//不是飞镖物品
					if(cm.getInventory(lz).getItem(i).getItemId() >= 2080000 || cm.getInventory(lz).getItem(i).getItemId() < 2070000){
						avail += "#L" + cm.getInventory(lz).getItem(i).getItemId() + "# #v" + cm.getInventory(lz).getItem(i).getItemId() + "# #b#t" + cm.getInventory(lz).getItem(i).getItemId() + "##k #l\r\n";
					}
				}
			}
		}
		avail += "\r\n---------------------------------------------------\r\n";
		//其他栏
		for (var ii = 0; ii < 96; ii++) {
			//位置不为空
			if (cm.getInventory(ls).getItem(ii) != null) {
				//不是现金物品，不是任务物品
				if(!cm.物品任务类型(cm.getInventory(ls).getItem(ii).getItemId())&&!cm.物品时装类型(cm.getInventory(ls).getItem(ii).getItemId())){
				    avail += "#L" + cm.getInventory(ls).getItem(ii).getItemId() + "# #v" + cm.getInventory(ls).getItem(ii).getItemId() + "# #b#t" + cm.getInventory(ls).getItem(ii).getItemId() + "##k #l\r\n";
				}
			}
		}
		
		if(avail==""){
			cm.dropMessage(1,"没有物品。");
			cm.dispose();			 
			return;
		}
		cm.sendSimple("#i4030000#  #i4030001#  #i4030010# #r#e< 随身仓库 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n"+avail);
    } else if (status == 1) {
		x = selection;
		cm.sendGetText("\r\n输入你要存入的数量；");
	} else if (status == 2) {	
		x2=cm.getText();
		if(x2==""){
			cm.sendOk("请不要加入空格。");
			cm.dispose();
			return;
		}
		
		if(x2.indexOf(" ")!=-1){
			cm.sendOk("请不要加入空格。");
			cm.dispose();
			return;
		}
		
		if(isNaN(x2)){
			cm.sendOk("请输入正确的数字。");
			cm.dispose();
			return;
		}
		
		if(x2 < 0){
			cm.sendOk("请输入正确的数字。");
			cm.dispose();
			return;
		}
		
		if(x > Max){
			cm.sendOk("一次最多存入 "+Max+" 个。");
			cm.dispose();
			return;
		}
		
		if (!cm.haveItem(x,x2)) {
			cm.sendOk("物品数量不足。");
			cm.dispose();
			return;
		}

		cm.getPlayer().gainItem(x, -x2, false);
		cm.添加仓库物品(x2,x);
		cm.sendOk("恭喜你存入#v"+x+"##b#t"+x+"##k x "+x2+"");
		//背包存档
		cm.saveDatabaseItem();
		cm.dispose();
    } 
}