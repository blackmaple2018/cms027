var fee;
function start() {
    status = -1;
    action(1, 0, 0);
}

function isNull( str ){
	if ( str == "" ) {
		return true;
	}
	var regu = "^[ ]+$";
	var re = new RegExp(regu);
	return re.test(str);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.sendOk("你没有卡号？");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
			var selStr = "\r\n  #i4030000#  #i4030001#  #i4030010# #r#e< 自助系统 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
			if(cm.获取账号记录("60现金券首充")==0){
				selStr+="    #b现金券 x 60 #r未首充，首次充值该金额赠送30%#k\r\n";
			}
			
			if(cm.获取账号记录("320现金券首充")==0){
				selStr+="    #b现金券 x 320 #r未首充，首次充值该金额赠送30%#k\r\n";
			}
			
			if(cm.获取账号记录("680现金券首充")==0){
				selStr+="    #b现金券 x 680 #r未首充，首次充值该金额赠送30%#k\r\n";
			}
			
			if(cm.获取账号记录("1280现金券首充")==0){
				selStr+="    #b现金券 x 1280 #r未首充，首次充值该金额赠送30%#k\r\n";
			}
			
			if(cm.获取账号记录("3280现金券首充")==0){
				selStr+="    #b现金券 x 3280 #r未首充，首次充值该金额赠送30%#k\r\n";
			}
			
			if(cm.获取账号记录("6480现金券首充")==0){
				selStr+="    #b现金券 x 6480 #r未首充，首次充值该金额赠送30%#k\r\n";
			}
			
            cm.sendGetText(selStr+"\r\n请输入合法的兑换CDK，然后你就可以领取对应的奖励了；");
		} else if (status == 1) {
            fee = cm.getText();
			if(fee==""){
				cm.sendOk("请不要加入空格。");
                cm.dispose();
                return;
			}
			//判断是偶有空格
			if(fee.indexOf(" ")!=-1){
				cm.sendOk("请不要加入空格。");
                cm.dispose();
                return;
			}
			//判断CDK兑换码的位数
			if(fee.getBytes().length!=32){
				cm.sendOk("请输入正确的32位CDK兑换码。");
                cm.dispose();
                return;
			}
            //判断卡号是否存在
            if (cm.判断兑换卡是否存在(fee)==0) {
                cm.sendOk("卡号不存在，或者该卡号未使用，请你稍后再试试。");
                cm.dispose();
                return;
            }
			//打开礼包
            var Lb = cm.判断兑换卡礼包(fee);
            cm.dispose();
			//福利礼包，金币2点券2x7
			if(Lb==8){
				if(cm.获取角色记录("福利礼包1")>0){
					cm.sendOk("每个角色只能使用一次这种类型的礼包。");
					cm.dispose();
					return;
				}else{
					cm.新增角色记录("福利礼包1");
				}
			}
			cm.openNpc(9900000, 70 + Lb);
			//使用成功后删除兑换卡
			cm.Deleteexchangecard(fee);    
        }
    }
}






























