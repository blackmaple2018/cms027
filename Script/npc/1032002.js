function start() {
    status = -1;
    action(1, 0, 0)
}
function action(mode, type, selection) {
    if (status <= 0 && mode <= 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }

    if (status <= 0) {
        var 
		selStr = "   你好冒险家，我可以打造非凡的道具效果，你想给你的道具增加特殊效果吗？附魔的道具通过移动，可以在信息栏查看详细信息。#r想给装备进行附魔，首先要打孔，装备打3孔失败如果没有保护措施，是会被损坏的。如果你有特殊的魔法石，自然石放在第一格，祝福石放在第二格，就会生效#k。\r\n";

		
		selStr += "   #L1##b给装备进行打孔#k#l\r\n";
		selStr += "   #L2##b给装备进行附魔#k#l\r\n";
		selStr += "   #L3##b给装备进行清洗#k#l\r\n";
		selStr += "   #L4##b查看所有的附魔#k#l\r\n";

		
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {

			case 1:
				cm.dispose();
				cm.openNpc(1032002,1);
				break;
			case 2:
				cm.dispose();
				cm.openNpc(1032002,2);
				break;
			case 3:
				cm.dispose();
				cm.openNpc(1032002,3);
				break;
			case 4:
				cm.sendOk("\r\n\r\n"+cm.显示所有附魔类型());
				cm.dispose();
				break;

        }
    }
}