
var status = 0;
var beauty = 0;
var fhair = Array(30030, 30020, 30000, 30270, 30230, 30260, 30280, 30240, 30290);
var mhair = Array(31040, 31000, 31250, 31220, 31260, 31240, 31110, 31270, 31030, 31230);
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
			cm.sendSimple("你想要更换发型或者染色吗？\r\n\r\n#L0##b随便做一个发型#l\r\n#L1#随便染一个颜色#l");						
		} else if (status == 1) {
			if (selection == 0) {
				beauty = 1;
				hairnew = Array();
				if (cm.getPlayer().getGender() == 0) {
					for(var i = 0; i < fhair.length; i++) {
						hairnew.push(fhair[i] + parseInt(cm.getPlayer().getHair() % 10));
					}
				} 
				if (cm.getPlayer().getGender() == 1) {
					for(var i = 0; i < mhair.length; i++) {
						hairnew.push(mhair[i] + parseInt(cm.getPlayer().getHair() % 10));
					}
				}
				cm.sendYesNo("你想要随便做一个发型吗？");
			} else if (selection == 1) {
				beauty = 2;
				haircolor = Array();
				var current = parseInt(cm.getPlayer().getHair() / 10) * 10;
				for(var i = 0; i < 8; i++) {
					haircolor.push(current + i);
				}
				cm.sendYesNo("你想要随便染一个颜色吗？");
			}
		} else if (status == 2){
			
			if (beauty == 1){
				if (cm.haveItem(4050004)){
					cm.gainItem(4050004, -1);
					cm.setHair(hairnew[Math.floor(Math.random() * hairnew.length)]);
					cm.sendOk("弄好了，你看看是不是更加好看了。");
				} else {
					cm.sendOk("亲，你没有 #i4050004# 所以我不能为你服务。");
				}
				cm.dispose();
			}
			if (beauty == 2){
				if (cm.haveItem(4051004)){
					cm.gainItem(4051004, -1);
					cm.setHair(haircolor[Math.floor(Math.random() * haircolor.length)]);
					cm.sendOk("弄好了，你看看是不是更加好看了。");
				} else {
					cm.sendOk("亲，你没有 #i4051004# 所以我不能为你服务。");
				}
				cm.dispose();
			}
		}
	}
}
