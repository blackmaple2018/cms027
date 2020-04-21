
var a;
var b;
var c;
var d;
var 随机;

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
		cm.dispose();
		return;
    }
	
    if (status <= 0) {
		var 文本 = "   #i4001126# #i4001124# #i4001125##r#e < 冒险摇摇乐 > #k#n#i4001125# #i4001124# #i4001126#\r\n\r\n";
		文本 += "\t你需要摇四次，摇完之后根据图标来决定奖励，4个一样的图标将会获取大奖。\r\n\r\n";
		文本 += "\r\n\t\t\t      ?     ?     ?     ?\r\n\r\n\r\n";
		文本 += "\t\t\t\t\t  #L0##b开始摇摇乐#k#l";
        cm.sendSimple(文本);
	} else if (status == 1) {
		var 文本2 = "   #i4001126# #i4001124# #i4001125##r#e < 冒险摇摇乐 > #k#n#i4001125# #i4001124# #i4001126#\r\n\r\n";
		文本2 += "\t\t\t\t你还需要摇三次，加油。\r\n\r\n";
		a = yyl();	
		文本2 += "\r\n\t\t     #v"+a+"#     ?     ?     ?\r\n\r\n";
		文本2 += "\t\t\t\t\t  #L0##b继续摇摇乐#k#l";
        cm.sendSimple(文本2);
	} else if (status == 2) {
		var 文本3 = "   #i4001126# #i4001124# #i4001125##r#e < 冒险摇摇乐 > #k#n#i4001125# #i4001124# #i4001126#\r\n\r\n";
		文本3 += "\t\t\t\t你还需要摇二次，继续。\r\n\r\n";
		b = yyl();	
		文本3 += "\r\n\t\t     #v"+a+"#     #v"+b+"#     ?     ?\r\n\r\n";
		文本3 += "\t\t\t\t\t  #L0##b继续摇摇乐#k#l";
        cm.sendSimple(文本3);
	} else if (status == 3) {
		var 文本4 = "   #i4001126# #i4001124# #i4001125##r#e < 冒险摇摇乐 > #k#n#i4001125# #i4001124# #i4001126#\r\n\r\n";
		文本4 += "\t\t\t\t再摇一次就可以揭晓奖励了。\r\n\r\n";
		c = yyl();	
		文本4 += "\r\n\t\t     #v"+a+"#     #v"+b+"#     #v"+c+"#     ?\r\n\r\n";
		文本4 += "\t\t\t\t\t  #L0##b继续摇摇乐#k#l";
        cm.sendSimple(文本4);
	} else if (status == 4) {
		var 文本5 = "   #i4001126# #i4001124# #i4001125##r#e < 冒险摇摇乐 > #k#n#i4001125# #i4001124# #i4001126#\r\n\r\n";
		文本5 += "\t\t\t\t领取奖励吧，快看看结果。\r\n\r\n";
		d = yyl();	
		文本5 += "\r\n\t\t #v"+a+"#     #v"+b+"#     #v"+c+"#     #v"+d+"#\r\n\r\n";
		文本5 += "\t\t\t\t\t  #L0##b领取奖励#k#l";
        cm.sendSimple(文本5);
	} else if (status == 5) {
		yyljl();
    }
}


function  yyljl() {
	
	cm.sendOk("测试阶段,目前没有奖励。");
	
}











function  yyl() {
	随机 = Math.floor(Math.random() * 6);
	switch (随机) {
		case 0:
		case 1:
			return 2000000;
		case 2:
			return 2000001;
		case 3:
			return 2000002;
		case 4:
			return 2000003;
		case 5:
			return 2000004;
		case 6:
			return 2000005;
		default:
			return 2000000;
	}
}










