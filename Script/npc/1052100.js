var status = 0;
var beauty = 0;
var mhair = Array(30030, 30020, 30000, 30130, 30190, 30110, 30180, 30050, 30040, 30160);
var fhair = Array(31050, 31040, 31000, 31060, 31090, 31020, 31130, 31120, 31140, 31010);
var hairnew = Array();


function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 0 && status == 0) {
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			cm.sendSimple("我是这里的老板，请问你要找我做发型吗。\r\n\r\n#L0##b我要换个发型#k#l");						
		} else if (status == 1) {
		    if (selection == 0) {
				beauty = 1;
				if (cm.getPlayer().getGender() == 1) {
					for (var i = 0; i < fhair.length; i++)
					    hairnew.push(fhair[i] + parseInt(cm.getPlayer().getHair() % 10));
				} else {
					for (var i = 0; i < mhair.length; i++)
					    hairnew.push(mhair[i] + parseInt(cm.getPlayer().getHair() % 10));
				}
				cm.sendStyle("请选择你喜欢的发型吧。", hairnew);
			} else if (selection == 1) {
				beauty = 2;
				var current = parseInt(cm.getPlayer().getHair() / 10) * 10;
				for(var i = 0; i < 8; i++) {
					haircolor.push(current + i);
				}
				cm.sendStyle("请选择你要染的发色吧。", haircolor);
			}
		} else if (status == 2){
			//cm.dispose();
			if (beauty == 1){
				if (cm.haveItem(4050003)){
					cm.gainItem(4050003, -1);
					cm.setHair(hairnew[selection]);
					cm.sendOk("弄好了，你看看是不是更加好看了。");
				} else {
					cm.sendOk("亲，你没有 #i4050003# 所以我不能为你服务。");
				}
				cm.dispose();
			}
			if (beauty == 2){
				if (cm.haveItem(4051003)){
					cm.gainItem(4051003, -1);
					cm.setHair(haircolor[selection]);
					cm.sendOk("弄好了，你看看是不是更加好看了。");
				} else {
					cm.sendOk("亲，你没有 #i4051003# 所以我不能为你服务。");
				}
				cm.dispose();
			}
		}
	}
}
