var status = 0;
var regcost = 499;
var vipcost = 999;
var tempvar;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 1)
            status++; if (mode == 0 && status == 1) {
			cm.dispose();
			return;
		} if (mode == 0 && status == 2) {
			cm.sendNext("我们也提供其他类型的服务，所以请仔细考虑，然后做出决定。");
			cm.dispose();
			return;
		}
        if (status == 0) {            
			cm.sendNext("欢迎来到桑拿房。如果你因为打猎而疲惫不堪，那么在我们桑拿房休息一下怎么样？");
		}
		if (status == 1) {
			cm.sendSimple("我们提供两种服务房间。请选一个你喜欢的。\r\n#b#L0#普通桑拿 (" + regcost + " 金币)#l\r\n#L1#高级桑拿 (" + vipcost + " 金币)#l");
		}
		if (status == 2) {
			tempvar = selection;
			if (tempvar == 0) {
				cm.sendYesNo("你选择了普通的桑拿。您的HP和MP将快速恢复，您甚至可以在那里购买一些物品。你确定要进去吗？");
			}
			if (tempvar == 1) {
				cm.sendYesNo("你选择了高级桑拿您的HP和MP恢复速度将比普通桑拿更快，您甚至可以在那里找到一个特殊的项目。你确定要进去吗？");
			}
		}
		if (status == 3) {
			if (tempvar == 0) {
				if (cm.getPlayer().getMeso() >= regcost) {
					cm.warp(105040401, 0);
					cm.gainMeso(-regcost);
				} else {
					cm.sendNext("我很抱歉。看起来你没有足够的金币。你至少得花点钱。");
				}
			} if (tempvar == 1) {
				if (cm.getPlayer().getMeso() >= vipcost) {
					cm.warp(105040402, 0);
					cm.gainMeso(-vipcost);
				} else {
					cm.sendNext("我很抱歉。看起来你没有足够的金币。你至少得花点钱。");
				}
			}
			cm.dispose();
		}
	}
}
