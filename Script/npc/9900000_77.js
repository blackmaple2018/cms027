
/**

现金券

*/
function start() {
    status = -1;
    action(1, 0, 0);
}


function action() {
	var 数量 = 6480;
	cm.记录消费(数量/10);
	cm.新增充值记录(648);
	//首充
	if(cm.获取账号记录("6480现金券首充")==0){
		数量+= 1944;
		cm.新增账号记录("6480现金券首充");
	}
	//2020年春节充值活动
	/*if(cm.获取账号记录("20206480年春节充值活动")==0){
		数量+= 3240;
		cm.新增账号记录("20206480年春节充值活动");
	}*/
	if(cm.获取推广员()!=null && cm.获取推广员()!=""){
		cm.修改推广收益(648);
	}
	if (cm.getPlayer().getbosslog("每日任务_花点小钱") <= 0 ) {
		cm.setbosslog("每日任务_花点小钱");
	}
	cm.getPlayer().修改现金券(数量);
	cm.getPlayer().dropMessage("现金券+"+数量);
	cm.指定群发送信息("游戏充值信息\r\n玩家:"+cm.getPlayer().getName()+"\r\n区域:"+cm.大区()+"\r\n本次充值:"+数量+" 现金券\r\n累计充值:"+cm.判断消费()+" RMB\r\nQ Q:"+cm.绑定QQ()+"","815717096");
	
	var 大区编号 = cm.getPlayer().getWorldId();
	var 充值信息 = "游戏充值信息\r\n玩家:"+cm.getPlayer().getName()+"\r\n本次充值:"+数量+" 现金券\r\n累计充值:"+cm.判断消费()+" RMB\r\nQ Q:"+cm.绑定QQ()+"";
	if(大区编号 == 3){
		//漂漂猪
		cm.指定人发送信息(充值信息,"564298174");
	}else if(大区编号 == 4){
		//小青蛇
		cm.指定人发送信息(充值信息,"314469527");
	}else if(大区编号 == 5){
		//红螃蟹
		cm.指定人发送信息(充值信息,"504603558");
	}
	
	cm.说明文字("恭喜你获得 #b"+数量+"#k 现金券。");
	cm.对话结束();
}


