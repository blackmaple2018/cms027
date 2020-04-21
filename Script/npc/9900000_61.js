importPackage(java.lang);
importPackage(Packages.client);
importPackage(Packages.client.inventory);
importPackage(Packages.server);
importPackage(Packages.constants);
importPackage(Packages.net.channel);
importPackage(Packages.tools);
importPackage(Packages.scripting);
importPackage(Packages.tools.packet);
importPackage(Packages.tools.data);
importPackage(Packages.tools);
function start() {
    status = -1;
    action(1, 0, 0);
}
var fee;
function action(mode, type, selection) {

        if (mode == 0) {
            cm.sendSimple("你还没想好名字吗？");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
		
		if(cm.判断家族()>0){
			cm.sendOk("你已经有家族了，不能再创建了。");
			cm.dispose();
			return;
		}
	
        if (status == 0) {
            cm.sendGetText("请输入你要创建的家族名字；");
        } else if (status == 1) {
            fee = cm.getText()
			if(fee.indexOf(" ")!=-1){
				cm.sendOk("请不要加入空格。");
                cm.dispose();
                return;
			}
			
			if(fee.getBytes().length< 1 || fee.getBytes().length> 21){
				cm.sendOk("名字不符合要求。");
                cm.dispose();
                return;
			}
			
            if (cm.判断家族名字(fee)) {
				cm.设置家族地位(1);
                cm.创建家族(fee);
				cm.sendOk(" 恭喜你 #b"+fee+"#k 家族组织创建成功。");
                cm.dispose();
            }else{
				cm.sendOk("家族名字已经存在。");
                cm.dispose();
			}

        }
    
}