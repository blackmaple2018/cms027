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
    
    if (status == 0) {
        var selStr = " #i4030000#  #i4030001#  #i4030010# #r#e< 快捷指令 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";

		selStr += "\t#b@jk/@解卡#k 解除角色无反应\r\n";
		selStr += "\t#b@bw/@dl/@爆物/@掉落#k 查询物品掉落\r\n";
		selStr += "\t#b@name/@改名/@更名#k 更改角色名字\r\n";
		selStr += "\t#b@sh/@伤害#k 呼出伤害详细\r\n";
		selStr += "\t#b游戏中说话开头加上#rq#b可以显示在群里,有10秒冷却\r\n";
		selStr += "\t#b群中说话开头加上#ry#b可以显示在游戏里\r\n";
		selStr += "\t#b@zdhfhp 500  设置自动恢复血量 \r\n";
		selStr += "\t#b@zdhfmp 500  设置自动恢复法力 \r\n";
		
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
			default:
                cm.dispose();
                cm.openNpc(9900000, 0);
                break;
        }
    }
}

