
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
    } else if (mode == 0 && selection == -1) {
		cm.dispose();
        return;
	}
	
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
	if(cm.显示仓库物品()=="\r\n"){
		cm.sendOk("没有物品。");
		cm.dispose();
		return;
	}
	
	if (cm.getInventory(2).isFull()) {
        cm.说明文字("请保证 #b消耗栏#k 至少有2个位置。");
        cm.对话结束();
        return;
    }
	
	if (cm.getInventory(4).isFull()) {
        cm.说明文字("请保证 #b其它栏#k 至少有2个位置。");
        cm.对话结束();
        return;
    }

    if (status == 0) {
		var 文本 = " #i4030000#  #i4030001#  #i4030010# #r#e< 随身仓库 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
			文本 +=cm.显示仓库物品();
			cm.sendSimple(文本);
    } else if (status == 1) {
        x = selection;
		item = cm.显示仓库物品代码(selection);
		cout = cm.显示仓库物品数量(selection);
		Max = cm.物品叠加数量(item);
		cm.sendGetText("\r\n输入你要取出的数量；");
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
		
		if(x2 > cout){
			cm.sendOk("仓库里没有这么多物品。");
			cm.dispose();
			return;
		}
		
		if(x2 > Max){
			cm.sendOk("一次最多取出 "+Max+" 个。");
			cm.dispose();
			return;
		}
		
		var 保管费 = (cm.现在时间()-cm.显示仓库物品时间(x))/60000*2;
		
		if(保管费>1000){
			保管费 = 1000;
		}
		
		if(cm.getMeso() < 保管费){
			cm.sendOk("你没有 "+保管费+" 金币保管费，我不能为你服务。");
			cm.dispose();
			return;
		}
		if(cm.显示仓库物品数量(x)>=x2){
			
			if(cm.getbosslog("仓库手术费")>=50){
				cm.gainMeso(-保管费);
			}else{
				cm.setbosslog("仓库手术费")
			}
			
			cm.getPlayer().gainItem(item, x2, false);
			cm.修改物品数量(-x2,x);
			//背包存档
			cm.saveDatabaseItem();
			cm.sendOk("恭喜你取出#v"+item+"##b#t"+item+"##k x "+x2+"");
		}
		cm.dispose();
    }
}