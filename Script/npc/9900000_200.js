
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
	
	if (status <= 0) {
		var 文本="\t选择跟踪的玩家；\r\n"+cm.显示跟踪在线玩家();
		cm.sendSimple(文本);
    } else if (status == 1) {
		cm.warp(selection);
    }
}