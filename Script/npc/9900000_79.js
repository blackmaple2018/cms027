
/**

现金券cdk1

*/
function start() {
    status = -1;
    action(1, 0, 0);
}


function action() {
	var 数量 = 60;
	cm.getPlayer().修改现金券(数量);
	cm.getPlayer().dropMessage("现金券+"+数量);
	cm.说明文字("恭喜你获得 #b"+数量+"#k 现金券。");
	cm.对话结束();
}


