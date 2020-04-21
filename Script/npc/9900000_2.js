/*
 ZEVMS冒险岛(079)游戏服务端
 71447500
 */

var ca = java.util.Calendar.getInstance();
var year = ca.get(java.util.Calendar.YEAR);
var month = ca.get(java.util.Calendar.MONTH) + 1;
var day = ca.get(java.util.Calendar.DATE);
var hour = ca.get(java.util.Calendar.HOUR_OF_DAY);
var minute = ca.get(java.util.Calendar.MINUTE);
var second = ca.get(java.util.Calendar.SECOND);
var weekday = ca.get(java.util.Calendar.DAY_OF_WEEK);

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

	
	//显示
    if (status == 0) {
        var selStr = " #i4030000#  #i4030001#  #i4030010# #r#e< 荣耀排行榜 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
		selStr+="      这里都是冒险世界的强者，你必须已经通过了不断的变强，不断的努力，你才有机会登上这至高无上的光荣排行榜。\r\n\r\n";
		
		selStr+="\t\t\t\t\t#L1##b等级排行榜#k#l\r\n";
		selStr+="\t\t\t\t\t#L2##b在线排行榜#k#l\r\n";
		selStr+="\t\t\t\t\t#L4##b狩猎排行榜#k#l\r\n";
		selStr+="\t\t\t\t\t#L3##b点赞排行榜#k#l\r\n";
		selStr+="\t\t\t\t\t#L5##b人气排行榜#k#l\r\n";
		selStr+="\t\t\t\t\t#L6##b泡点排行榜#k#l\r\n";
		selStr+="\t\t\t\t\t#L7##b锻造排行榜#k#l\r\n";
		cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
			case 1:
				cm.dispose();
                cm.openNpc(9900000,21);
                break;
			case 2:
				cm.dispose();
                cm.openNpc(9900000,22);
                break;
			case 3:
				cm.dispose();
                cm.openNpc(9900000,23);
                break;
			case 4:
				cm.dispose();
                cm.openNpc(9900000,24);
                break;
			case 5:
				cm.dispose();
                cm.openNpc(9900000,25);
                break;
			case 6:
				cm.dispose();
                cm.openNpc(9900000,26);
                break;
			case 7:
				cm.dispose();
                cm.openNpc(9900000,27);
                break;
			case 10:
				cm.sendOk("\t\t\t\t<实时巅峰输出排行榜>\r\n\r\n"+cm.世界伤害排行());
				cm.dispose();
                break;
			default:
                cm.dispose();
                break;
        }
    }
}

