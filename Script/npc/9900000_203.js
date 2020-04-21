/********************************

ZEVMS 自由冒险岛Ver.027 
@小z QQ71447500

********************************/

function start() {
    status = -1;
    action(1, 0, 0);
}
var slot = Array();
var ls = 1;
var x;
var z;

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
	

	if(cm.getPlayer().getGM()<4){
		cm.dispose();
        return;
	}
	
    if (status == 0) {
		var 文本 = "*—— #b[ #r属性修改#b ]#k ———————————————————————————————————————————*\r\n\r\n";
		for (var i = 0; i < 96; i++) {
			if (cm.getInventory(ls).getItem(i) != null) {
				var itemid = cm.getInventory(ls).getItem(i).getItemId();
				if(!cm.isCash(cm.getInventory(ls).getItem(i).getItemId())){
						var name = cm.getItemName(itemid);
						if(i < 10){
							文本 += "\t#L" + i + "# 0"+i+")  #v" + itemid + "# #b" + name + "#k#l\r\n";
						}else{
							文本 += "\t#L" + i + "# "+i+")  #v" + itemid + "# #b" + name + "#k#l\r\n";
						}
					
				}
			}
			slot.push(i);
		}
		cm.sendSimple(文本);
	} else if (status == 1) {
		位置 = selection;
		var b = "";
		b += "#L1##b力量#k#l\r\n";
		b += "#L2##b敏捷#k#l\r\n";
		b += "#L3##b智力#k#l\r\n";
		b += "#L4##b运气#k#l\r\n";
		b += "#L5##b物理攻击力#k#l\r\n";
		b += "#L6##b物理防御力#k#l\r\n";
		b += "#L7##b魔法攻击力#k#l\r\n";
		b += "#L8##b魔法防御力#k#l\r\n";
		b += "#L9##b生命值#k#l\r\n";
		b += "#L10##b法力值#k#l\r\n";
		b += "#L11##b移动速度#k#l\r\n";
		b += "#L12##b跳跃力#k#l\r\n";
		b += "#L13##b命中率#k#l\r\n";
		b += "#L14##b回避率#k#l\r\n";
        cm.sendSimple("选择你要修改的熟属性:\r\n\r\n"+b);
    } else if (status == 2) {
		z = selection;
		cm.sendGetText("\r\n输入要增加的数值；");
	} else if (status == 3) {
		x = cm.getText();
		if(x < 0 || x >999){
			cm.dispose();
			return;
		}
		if(z == 1){
			cm.加属性("力量",slot[位置],x);
		}
		if(z == 2){
			cm.加属性("敏捷",slot[位置],x);
		}
		if(z == 3){
			cm.加属性("智力",slot[位置],x);
		}
		if(z == 4){
			cm.加属性("运气",slot[位置],x);
		}
		if(z == 5){
			cm.加属性("物理攻击",slot[位置],x);
		}
		if(z == 6){
			cm.加属性("物理防御",slot[位置],x);
		}
		if(z == 7){
			cm.加属性("魔法攻击",slot[位置],x);
		}
		if(z == 8){
			cm.加属性("魔法防御",slot[位置],x);
		}
		if(z == 9){
			cm.加属性("最大生命值",slot[位置],x);
		}
		if(z == 10){
			cm.加属性("最大法力值",slot[位置],x);
		}
		if(z == 11){
			cm.加属性("移动速度",slot[位置],x);
		}
		if(z == 12){
			cm.加属性("跳跃力",slot[位置],x);
		}
		if(z == 13){
			cm.加属性("命中率",slot[位置],x);
		}
		if(z == 14){
			cm.加属性("闪避率",slot[位置],x);
		}
		cm.sendOk("修改成功。");
		cm.dispose();
	} 
}


















