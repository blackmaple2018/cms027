var a;
var b;
var c;
var d;
var 股票;
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
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status=0;
        }
        if (status == 0) {
			var selStr1 = "\r\n  #i4030000#  #i4030001#  #i4030010# #r#e< 股票买卖 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
			selStr1+="               #b#L1#购买股票#l    #L2#卖出股票#l#k\r\n";
			//selStr1+="                 #L2#卖出股票#l#k\r\n";
            cm.sendSimple(selStr1);
		} else if (status == 1) {
            a = selection;   
			var selStr2 = "\r\n  #i4030000#  #i4030001#  #i4030010# #r#e< 股票买卖 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
			if(a==1){
				selStr2+="\t\t\t#b #L1#购买绿蜗牛股票#k #r"+cm.每股价值(1)+"#k 金币/股#l\r\n";
				selStr2+="\t\t\t#b #L2#购买蓝蜗牛股票#k #r"+cm.每股价值(2)+"#k 金币/股#l\r\n";
				selStr2+="\t\t\t#b #L3#购买红蜗牛股票#k #r"+cm.每股价值(3)+"#k 金币/股#l\r\n";
			}else if(a==2){
				selStr2+="\t\t\t#b #L101#卖出绿蜗牛股票 #r"+cm.每股价值(1)+"#k 金币/股#l\r\n";
				selStr2+="\t\t\t#b #L102#卖出蓝蜗牛股票 #r"+cm.每股价值(2)+"#k 金币/股#l\r\n";
				selStr2+="\t\t\t#b #L103#卖出红蜗牛股票 #r"+cm.每股价值(3)+"#k 金币/股#l\r\n";
			}else{
				selStr2+="系统错误-1";
			}
            cm.sendSimple(selStr2);
		} else if (status == 2) {
			b = selection;  
			if(b==1||b==101){
			    股票 = "绿蜗牛";
				d = 1;
			}else if(b==2||b==102){
				股票 = "蓝蜗牛";
				d = 2;
			}else if(b==3||b==103){
				股票 = "红蜗牛";	
				d = 3;
			}
			if(b>=1&&b<=3){
				cm.sendGetText("请输入你要购买 #b"+股票+"#k 的数量；");
			}else if(b>=101&&b<=103){
				cm.sendGetText("请输入你要卖出 #b"+股票+"#k 的数量；");
			}else{
				cm.sendOk("系统错误-2");
				cm.dispose();
			}
		} else if (status == 3) {
			c = cm.getText();
			if(c==""){
				cm.sendOk("请输入数量。");
				cm.dispose();
				return;
			}
			
			if(c.indexOf(" ")!=-1){
				cm.sendOk("请不要加入空格。");
				cm.dispose();
				return;
			}
		
			if(isNaN(c)){
				cm.sendOk("请输入正确的数字。");
				cm.dispose();
				return;
			}
		
			if(c < 0){
				cm.sendOk("请输入正确的数字。");
				cm.dispose();
				return;
			}
			
			if(c > 200){
				cm.sendOk("一次最多购买或者贩卖200以内。");
				cm.dispose();
				return;
			}
			//买进
			if(a==1){
				var 需要金币 = c*cm.每股价值(d);
				if(cm.getMeso() < 需要金币){
					cm.sendOk("根据当前 #b"+股票+"#k 的价值为 #r"+cm.每股价值(d)+"#k/股。你没有足够的金币购买。");
					cm.dispose();
					return;
				}
				if(c > cm.每股剩余数量(d)){
					cm.sendOk("当前 #b"+股票+"#k 股票已经售完。");
					cm.dispose();
					return;
				}
				if(cm.每股剩余数量(d)>=c){
					cm.gainMeso(-需要金币);
					cm.修改股票数量(d,-c);
					cm.getPlayer().修改股票(d,c);
					cm.sendOk("成功购买 #b"+股票+"#k 股票 x "+c+" ，花费 #r"+需要金币+"#k 金币。");
				}
				cm.dispose();
			}else if(a==2){
				var 持有股票 = cm.getPlayer().获取股票(d);
				if(c > 持有股票){
					cm.sendOk("你持有的股票好像没有这么多哦。");
					cm.dispose();
					return;
				}
				if(cm.getPlayer().获取股票(d)>=c){
					cm.gainMeso(c*cm.每股价值(d));
					cm.修改股票数量(d,c);
					cm.getPlayer().修改股票(d,-c);
					cm.sendOk("成功售出 #b"+股票+"#k 股票 x "+c+" ，收入 #r"+c*cm.每股价值(d)+"#k 金币。");
				}
				cm.dispose();
			}else{
				cm.sendOk("系统错误-3");
				cm.dispose();
			}
        }
    }
}






























