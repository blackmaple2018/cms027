
/**

现金券cdk1

*/
function start() {
    status = -1;
    action(1, 0, 0);
}


function action() {
	cm.getPlayer().修改增值服务数据("meso2",7);
	cm.getPlayer().修改增值服务数据("dianquan2",7);
	cm.说明文字("恭喜你获得 #b金币福利2，点券福利2#k x 7 ，请在主界面增值服务领取。");
	cm.对话结束();
}


