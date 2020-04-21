

function start() {
    status = -1;
    action(1, 0, 0)
}

function action(mode, type, selection) {
    if (status <= 0 && mode <= 0) {
        cm.dispose();
        return
    }
	
    if (mode == 1) {
        status++
    } else {
        status--
    }	 
	
	if(cm.判断家族()>0){
		cm.sendOk("你已经有家族了，不能再在加入别的家族。");
		cm.dispose();
		return;
	}
		
	if (status <= 0) {
        var selStr = " #i4030000#  #i4030001#  #i4030010# #r#e< 家族列表 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
		selStr+=""+cm.家族列表()+"";
        cm.sendSimple(selStr);
    } else if (status == 1) {
		cm.设置家族编号(cm.家族id取jiazuid(selection));
		cm.设置家族地位(3);
		cm.家族通知信息("恭喜 "+cm.玩家名字()+" 加入家族。");
		cm.sendOk("恭喜你成功加入 #b"+cm.家族名字(cm.家族id取jiazuid(selection))+"#k 家族。");
        cm.dispose();
    }
}