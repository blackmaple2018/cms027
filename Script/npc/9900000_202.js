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


var id;
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
		var 文本 = " #i4001124# #i4001125##r#e<"+cm.冒险岛名称()+"-营收收入一览>#k#n#i4001125# #i4001124# \r\n";
		if(cm.getPlayer().getGM()>=4){
			文本 += "\r\n\t\t\t#b全区当前在线#k:"+在线玩家()+"#n\r\n";
			文本 += "\r\n\t\t\t#e#r蓝蜗牛#k#n\r\n";
			for (var i = 1; i <= 12; i++) {
				if(month < i){
					break;
				}
				if(充值金额(0,i)>0){
					文本 += "\t\t\t2020 年 #b"+i+"#k 月 盈利收入 : #r"+充值金额(0,i).toFixed(2)+"#k\r\n";
				}
			}
			
			文本 += "\r\n\t\t\t#e#r漂漂猪#k#n\r\n";
			for (var i = 1; i <= 12; i++) {
				if(month < i){
					break;
				}
				if(充值金额(3,i)>0){
					文本 += "\t\t\t2020 年 #b"+i+"#k 月 盈利收入 : #r"+充值金额(3,i).toFixed(2)+"#k\r\n";
				}
			}
			
			文本 += "\r\n\t\t\t#e#r小青蛇#k#n\r\n";
			for (var i = 1; i <= 12; i++) {
				if(month < i){
					break;
				}
				if(充值金额(4,i)>0){
					文本 += "\t\t\t2020 年 #b"+i+"#k 月 盈利收入 : #r"+充值金额(4,i).toFixed(2)+"#k\r\n";
				}
			}
			
			文本 += "\r\n\t\t\t#e#r红螃蟹#k#n\r\n";
			for (var i = 1; i <= 12; i++) {
				if(month < i){
					break;
				}
				if(充值金额(5,i)>0){
					文本 += "\t\t\t2020 年 #b"+i+"#k 月 盈利收入 : #r"+充值金额(5,i).toFixed(2)+"#k\r\n";
				}
			}
		}else{
			文本 += "\r\n\t\t\t#e#r"+cm.大区()+"#k#n\r\n";
			for (var i = 1; i <= 12; i++) {
				if(month < i){
					break;
				}
				if(充值金额(cm.getPlayer().getWorldId(),i)>0){
					文本 += "\t\t\t2020 年 #b"+i+"#k 月 盈利收入 : #r"+充值金额(cm.getPlayer().getWorldId(),i).toFixed(2)+"#k\r\n";
				}
			}
		}
		
		cm.sendSimple(文本);
    }
}


function 充值金额(a,b) {
    var con = Packages.launch.Start.getInstance().getConnection();
    var count = 0;
    var ps;
    ps = con.prepareStatement("SELECT * FROM accountsrecharge WHERE World = ? && b = ?");
    ps.setInt(1, a);
	ps.setInt(2, b);
    var rs = ps.executeQuery();
    while (rs.next()) {
        count += rs.getInt("xianjin");
    } 
    rs.close();
    ps.close();
	con.close();
    return count*0.97;
}

function 在线玩家() {
    var con = Packages.launch.Start.getInstance().getConnection();
    var count = 0;
    var ps;
    ps = con.prepareStatement("SELECT * FROM accounts WHERE loggedin > 0");
    var rs = ps.executeQuery();
    while (rs.next()) {
        count ++;
    } 
    rs.close();
    ps.close();
	con.close();
    return count;
}





