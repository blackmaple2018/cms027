function start() {
    status = -1;
    action(1, 0, 0);
}

function 进行第二关2() {
	var 经验 = 2000;
	var 金币 = 5000;
	/*if(cm.getPlayer().废弃副本难度()==1){
		经验*=2;
		金币*=2
	}*/
    cm.Partyexpbosslog(经验,"废弃第二关经验",10);
	cm.Partymesobosslog(金币,"废弃第二关金币",10);
	cm.warpParty2(103000802,1);
	cm.dispose();
}

function 进行第三关2() {
	var 经验 = 4000;
	var 金币 = 10000;
	if(cm.getPlayer().废弃副本难度()==1){
		经验*=3;
		金币*=3;
	}
    cm.Partyexpbosslog(经验,"废弃第三关经验",10);
	cm.Partymesobosslog(金币,"废弃第三关金币",10);
	cm.warpParty2(103000803,1);
	cm.dispose();
}

function 进行第四关2() {
	var 经验 = 8000;
	var 金币 = 12000;
	if(cm.getPlayer().废弃副本难度()==1){
		经验*=3;
		金币*=3;
	}
	cm.Partyfeiqunandu(1);
    cm.Partyexpbosslog(经验,"废弃第四关经验",10);
	cm.Partymesobosslog(金币,"废弃第四关金币",10);
	cm.warpParty2(103000805,0);
	cm.dispose();
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

	if(cm.getPlayer().getMapId()!=103000800){	
		if (cm.getParty() == null) {
			cm.warp(103000000, 0);
			cm.removeAll(4001007);
			cm.removeAll(4001008);
			cm.dispose();
			return;
		}
	}
	
	var 第一关证书 = cm.getParty().getMembers().size()*20;

    if (status == 0) {
		var 文本 = "#i4030000#  #i4030001#  #i4030010# #r#e< 废弃沼泽副本 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
		
		//文本+="    "+cm.获取废弃副本随机()+" ";
		
		if(cm.getPlayer().getMapId()==103000800){
			文本+="    这里是废弃打猎场，你可以通过击杀怪物来获取证书，然后来跟我兑换奖励哦。\r\n\r\n";	
		}
		
		if(cm.getPlayer().getMapId()==103000801){
			文本+="    请让队友爬上藤曼，其中只有一种组合是对的。\r\n\r\n";
			if (cm.isLeader()) {
				文本+="   #b#L2#验证组合#l#k";
			}
		}
		
		if(cm.getPlayer().getMapId()==103000802){
			文本+="    请让队友踩上平台，其中只有一种组合是对的。\r\n\r\n";
			if (cm.isLeader()) {
				文本+="   #b#L3#验证组合#l#k";
			}
		}
		
		if(cm.getPlayer().getMapId()==103000803){
			文本+="    请让队友踩上木桶，其中只有一种组合是对的。\r\n\r\n";
			if (cm.isLeader()) {

				文本+="   #b#L4#验证组合#l#k";
			}
		}
		

		
		 	
		cm.sendSimple(文本);
    } else if (status == 1) {
        switch (selection) {
			case 2:
				if(cm.获取废弃副本随机() == -1){
					var 随机 = Math.floor(Math.random() * 第二关.length);
					cm.废弃副本随机(随机);
				}
				var 条件1 = 0;
				var 条件2 = 0;
				var 条件3 = 0;
				var 条件4 = 0;
				var 通关2 = 0;
				var 第二关1 = 0;
				var 第二关2 = 0;
				var 第二关3 = 0;
				var 第二关4 = 0;
				var 人数 = 0;
				var x = cm.获取废弃副本随机();

				if(第二关[x][0]>0){
					条件1 = 1;
				}
				if(第二关[x][1]>0){
					条件2 = 1;
				}
				if(第二关[x][2]>0){
					条件3 = 1;
				}
				if(第二关[x][3]>0){
					条件4 = 1;
				}

				var it = cm.getParty().getMembers().iterator();
				while (it.hasNext()) {
					var cPlayer = it.next();
					if(条件1>0){
						if( cPlayer.getPlayer().getPosition().x == -719  && cPlayer.getPlayer().getPosition().y <= -175 && cPlayer.getPlayer().getPosition().y > -290){
							通关2++;
							第二关1++;
						}
					}					
					
					if(条件2>0){
						if( cPlayer.getPlayer().getPosition().x == -584  && cPlayer.getPlayer().getPosition().y <= -175 && cPlayer.getPlayer().getPosition().y > -290){
							通关2++;
							第二关2++;
						}
					}
					
					if(条件3>0){
						if( cPlayer.getPlayer().getPosition().x == -753 &&cPlayer.getPlayer().getPosition().y <= 85 && cPlayer.getPlayer().getPosition().y > -100){
							通关2++;
							第二关3++;
						}
					}
					
					if(条件4>0){
						if( cPlayer.getPlayer().getPosition().x == -481 && cPlayer.getPlayer().getPosition().y <= 85 && cPlayer.getPlayer().getPosition().y > -100){
							通关2++;
							第二关4++;
						}
					}
					
					if((cPlayer.getPlayer().getPosition().x == -481 && cPlayer.getPlayer().getPosition().y <= 85 && cPlayer.getPlayer().getPosition().y > -100)||
				(cPlayer.getPlayer().getPosition().x == -753 &&cPlayer.getPlayer().getPosition().y <= 85 && cPlayer.getPlayer().getPosition().y > -100)||
				(cPlayer.getPlayer().getPosition().x == -584  && cPlayer.getPlayer().getPosition().y <= -175 && cPlayer.getPlayer().getPosition().y > -290)||
				(cPlayer.getPlayer().getPosition().x == -719  && cPlayer.getPlayer().getPosition().y <= -175 && cPlayer.getPlayer().getPosition().y > -290)
					
					){
						人数++;
					}

				}
				if(人数!=3){
					cm.sendSimple("你需要 #b3#k 位队友配合一起，完成任务。");
					cm.dispose();
				}else if( 第二关1 > 1 || 第二关2 > 1 || 第二关3 > 1 || 第二关4 > 1){
					cm.sendSimple("不要都在一个地方。");
					cm.dispose();
				}else if(通关2 >= 3 && 人数 == 3){
					cm.showWrongEffect2(1);
					cm.废弃副本重置随机();
					if(cm.getPlayer().废弃副本难度()==1){
						cm.getPlayer().重置废弃副本错误次数();
					}
					进行第二关2();
				}else{
					
					if(cm.getPlayer().废弃副本难度()==10){
						var 随机 = Math.floor(Math.random() * 100);
						if(随机<=30){
							蝙蝠干扰();
						}
					}
					cm.showWrongEffect2(0);
					cm.dispose();
				}
				break;
			
			case 3:
				if(cm.获取废弃副本随机() == -1){
					var 随机 = Math.floor(Math.random() * 第三关.length);
					cm.废弃副本随机(随机);
				}
				var 条件1 = 0;
				var 条件2 = 0;
				var 条件3 = 0;
				var 条件4 = 0;
				var 条件5 = 0;
				var 第三关1 = 0;
				var 第三关2 = 0;
				var 第三关3 = 0;
				var 第三关4 = 0;
				var 第三关5 = 0;
				var 通关2 = 0;
				var 人数 = 0;
				var x = cm.获取废弃副本随机();

				if(第三关[x][0]>0){
					条件1 = 1;
				}
				if(第三关[x][1]>0){
					条件2 = 1;
				}
				if(第三关[x][2]>0){
					条件3 = 1;
				}
				if(第三关[x][3]>0){
					条件4 = 1;
				}
				if(第三关[x][4]>0){
					条件5 = 1;
				}

				var it = cm.getParty().getMembers().iterator();
				while (it.hasNext()) {
					var cPlayer = it.next();
					if(条件1>0){
						if( cPlayer.getPlayer().getPosition().x >=605 && cPlayer.getPlayer().getPosition().x <=740  && cPlayer.getPlayer().getPosition().y == -135){
							通关2++;
							第三关1++;
						}
					}	
					if(条件2>0){
						if( cPlayer.getPlayer().getPosition().x >=780 && cPlayer.getPlayer().getPosition().x <=925  && cPlayer.getPlayer().getPosition().y == -75){
							通关2++;
							第三关2++;
						}
					}
					if(条件3>0){
						if( cPlayer.getPlayer().getPosition().x >=965 && cPlayer.getPlayer().getPosition().x <=1100  && cPlayer.getPlayer().getPosition().y == -135){
							通关2++;
							第三关3++;
						}
					}
					if(条件4>0){
						if( cPlayer.getPlayer().getPosition().x >=880 && cPlayer.getPlayer().getPosition().x <=1010  && cPlayer.getPlayer().getPosition().y == -195){
							通关2++;
							第三关4++;
						}
					}	
					if(条件5>0){
						if( cPlayer.getPlayer().getPosition().x >=690 && cPlayer.getPlayer().getPosition().x <=830  && cPlayer.getPlayer().getPosition().y == -195){
							通关2++;
							第三关5++;
						}
					}
					if((cPlayer.getPlayer().getPosition().x >=690 && cPlayer.getPlayer().getPosition().x <=830  && cPlayer.getPlayer().getPosition().y == -195)||
					(cPlayer.getPlayer().getPosition().x >=880 && cPlayer.getPlayer().getPosition().x <=1010  && cPlayer.getPlayer().getPosition().y == -195)||
					(cPlayer.getPlayer().getPosition().x >=965 && cPlayer.getPlayer().getPosition().x <=1100  && cPlayer.getPlayer().getPosition().y == -135)||
					(cPlayer.getPlayer().getPosition().x >=780 && cPlayer.getPlayer().getPosition().x <=925  && cPlayer.getPlayer().getPosition().y == -75)||
					(cPlayer.getPlayer().getPosition().x >=605 && cPlayer.getPlayer().getPosition().x <=740  && cPlayer.getPlayer().getPosition().y == -135)
					){
						人数++;
					}
				}
				if(人数!=3 || 第三关1 > 1 || 第三关2 > 1 || 第三关3 > 1 || 第三关4 > 1 || 第三关5 > 1){
					cm.sendSimple("你需要 #b3#k 位队友配合一起，完成任务。");
					cm.dispose();
				}else if( 第三关1 > 1 || 第三关2 > 1 || 第三关3 > 1 || 第三关4 > 1 || 第三关5 > 1){
					cm.sendSimple("不要都在一起啦，兄弟。");
					cm.dispose();
				}else if(通关2 >= 3 && 人数 == 3){
					cm.showWrongEffect2(1);
					cm.废弃副本重置随机();
					if(cm.getPlayer().废弃副本难度()==1){
						cm.getPlayer().重置废弃副本错误次数();
					}
					进行第三关2();
				}else{
					cm.showWrongEffect2(0);
					if(cm.getPlayer().废弃副本难度()==1){
						cm.getPlayer().记录废弃副本错误次数(1);
						var 机会 = 4;
						if(cm.getPlayer().废弃副本错误次数() > 机会){
							cm.废弃副本重置随机();
							cm.getPlayer().重置废弃副本错误次数();
							cm.PartydropMessage(5,"已结束副本。");
							cm.warpParty2(103000000,1);
						}
						cm.PartydropMessage(5,"本回合机会 "+机会+" / "+cm.getPlayer().废弃副本错误次数());
					}
					
					if(cm.getPlayer().废弃副本难度()==10){
						var 随机 = Math.floor(Math.random() * 100);
						if(随机<=30){
							蝙蝠干扰();
						}
					}
					cm.dispose();
				}
				break;
				
			case 4:
				if(cm.获取废弃副本随机() == -1){
					var 随机 = Math.floor(Math.random() * 第四关.length);
					cm.废弃副本随机(随机);
				}
				var 条件1 = 0;
				var 条件2 = 0;
				var 条件3 = 0;
				var 条件4 = 0;
				var 条件5 = 0;
				var 条件6 = 0;
				var 通关2 = 0;
				var 第四关1 = 0;
				var 第四关2 = 0;
				var 第四关3 = 0;
				var 第四关4 = 0;
				var 第四关5 = 0;
				var 第四关6 = 0;
				var 人数 = 0;
				var x = cm.获取废弃副本随机();
				if(第四关[x][0]>0){
					条件1 = 1;
				}
				if(第四关[x][1]>0){
					条件2 = 1;
				}
				if(第四关[x][2]>0){
					条件3 = 1;
				}
				if(第四关[x][3]>0){
					条件4 = 1;
				}
				if(第四关[x][4]>0){
					条件5 = 1;
				}
				if(第四关[x][5]>0){
					条件6 = 1;
				}

				var it = cm.getParty().getMembers().iterator();
				while (it.hasNext()) {
					var cPlayer = it.next();
					
					if(条件1>0){
						if( cPlayer.getPlayer().getPosition().x >=910  && cPlayer.getPlayer().getPosition().x <= 950 && cPlayer.getPlayer().getPosition().y == -234){
							通关2++;
							第四关1++;
						}
					}
					if(条件2>0){
						if( cPlayer.getPlayer().getPosition().x >=870  && cPlayer.getPlayer().getPosition().x <= 910 && cPlayer.getPlayer().getPosition().y == -182){
							通关2++;
							第四关2++;
						}
					}
					if(条件3>0){
						if( cPlayer.getPlayer().getPosition().x >=945  && cPlayer.getPlayer().getPosition().x <= 975 && cPlayer.getPlayer().getPosition().y == -182){
							通关2++;
							第四关3++;
						}
					}
					if(条件4>0){
						if( cPlayer.getPlayer().getPosition().x >=840  && cPlayer.getPlayer().getPosition().x <= 875 && cPlayer.getPlayer().getPosition().y == -130){
							通关2++;
							第四关4++;
						}
					}
					if(条件5>0){
						if( cPlayer.getPlayer().getPosition().x >=910  && cPlayer.getPlayer().getPosition().x <= 945 && cPlayer.getPlayer().getPosition().y == -130){
							通关2++;
							第四关5++;
						}
					}
					if(条件6>0){
						if( cPlayer.getPlayer().getPosition().x >=975  && cPlayer.getPlayer().getPosition().x <= 1010 && cPlayer.getPlayer().getPosition().y == -130){
							通关2++;
							第四关6++;
						}
					}
					if(
					(cPlayer.getPlayer().getPosition().x >=975  && cPlayer.getPlayer().getPosition().x <= 1010 && cPlayer.getPlayer().getPosition().y == -130)||
					(cPlayer.getPlayer().getPosition().x >=910  && cPlayer.getPlayer().getPosition().x <= 945 && cPlayer.getPlayer().getPosition().y == -130)||
					(cPlayer.getPlayer().getPosition().x >=840  && cPlayer.getPlayer().getPosition().x <= 875 && cPlayer.getPlayer().getPosition().y == -130)||
					(cPlayer.getPlayer().getPosition().x >=945  && cPlayer.getPlayer().getPosition().x <= 975 && cPlayer.getPlayer().getPosition().y == -182)||
					(cPlayer.getPlayer().getPosition().x >=870  && cPlayer.getPlayer().getPosition().x <= 910 && cPlayer.getPlayer().getPosition().y == -182)||
					(cPlayer.getPlayer().getPosition().x >=910  && cPlayer.getPlayer().getPosition().x <= 950 && cPlayer.getPlayer().getPosition().y == -234)
					){
						人数++;
					}
				}
				if(人数!=3){
					cm.sendSimple("你需要 #b3#k 位队友配合一起，完成任务。");
					cm.dispose();
				}else if(第四关1 > 1 || 第四关2 > 1 || 第四关3 > 1 || 第四关4 > 1 || 第四关5 > 1 || 第四关6 > 1){
					cm.sendSimple("站一起做啥啊你们到底想。");
					cm.dispose();
				}else if(通关2 >= 3 && 人数 == 3){
					cm.showWrongEffect2(1);
					if(cm.getPlayer().废弃副本难度()==1){
						cm.getPlayer().重置废弃副本错误次数();
					}
					cm.废弃副本重置随机();
					进行第四关2();
				}else{
					cm.showWrongEffect2(0);
					if(cm.getPlayer().废弃副本难度()==1){
						cm.getPlayer().记录废弃副本错误次数(1);
						var 机会 = 10;
						if(cm.getPlayer().废弃副本错误次数() > 机会){
							cm.废弃副本重置随机();
							cm.getPlayer().重置废弃副本错误次数();
							cm.PartydropMessage(5,"已结束副本。");
							cm.warpParty2(103000000,1);
						}
						cm.PartydropMessage(5,"本回合机会 "+机会+" / "+cm.getPlayer().废弃副本错误次数());
					}
					
					if(cm.getPlayer().废弃副本难度()==10){
						var 随机 = Math.floor(Math.random() * 100);
						if(随机<=30){
							蝙蝠干扰();
						}
					}
					
					cm.dispose();
				}
				break;
			default:
                cm.dispose();
                break;
        }
    }
}

function 蝙蝠干扰() {
    var 队伍 = cm.getParty().getMembers();
    var mapId = cm.getPlayer().getMapId();
    var Channel = cm.getPlayer().getClient().getChannel();
    var it = 队伍.iterator();
    while (it.hasNext()) {
        var cPlayer = it.next();
		cm.getMap(cPlayer.getMapId()).spawnMonsterOnGroundBelow(2300100,cPlayer.getPlayer().getPosition().x,cPlayer.getPlayer().getPosition().y);
    }
}

var 第二关 = [
    [0,1,1,1],
	[1,1,1,0],
	[1,1,0,1],
	[1,0,1,1]
];

var 第三关 = [
    [0,0,1,1,1],
	[0,1,0,1,1],
	[0,1,1,0,1],
    [0,1,1,1,0],
	[1,0,0,1,1],
	[1,0,1,0,1],
    [1,0,1,1,0],
	[1,1,0,0,1],
	[1,1,0,1,0],
    [1,1,1,0,0]
];

var 第四关 = [
    [0,0,0,1,1,1],
	[0,0,1,0,1,1],
	[0,0,1,1,0,1],
    [0,0,1,1,1,0],
	[0,1,0,0,1,1],
	[0,1,0,1,0,1],
    [0,1,0,1,1,0],
	[0,1,1,0,0,1],
	[0,1,1,0,1,0],
    [0,1,1,1,0,0],
	[1,0,0,0,1,1],
	[1,0,0,1,0,1],
    [1,0,0,1,1,0],
	[1,0,1,0,0,1],
	[1,0,1,0,1,0],
    [1,0,1,1,0,0],
	[1,1,0,0,0,1],
	[1,1,0,0,1,0],
    [1,1,0,1,0,0],
	[1,1,1,0,0,0]
];









