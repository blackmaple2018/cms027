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
var itemss;
var slot = Array();
var ls = 1;
var vv;
var fee;
var 装备;
var 力量;
var 敏捷;
var 智力;
var 运气;
var 攻击力;
var 魔法力;
var 物理防御;
var 魔法防御;
var 生命;
var 魔力;
var 命中;
var 回避;
var 手技;
var 移速;
var 跳跃;
var 可升级次数;
var 已升级次数;
var 附魔;
var 插槽;
var 锻造等级;

function start() {
	status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {

    if (status == 0 && mode == 0) {
		cm.sendSimple("好了，那就先不上架。");
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
	
	if(cm.判断拍卖出售位数量()-cm.已经使用拍卖栏位()<=0){
		cm.sendOk("没有足够的出售栏。");
		cm.dispose();
		return;
	}
	
    if (status == 0) {
        var avail = "";
        for (var i = 0; i < 96; i++) {
            if (cm.getInventory(ls).getItem(i) != null) {
				if (!cm.isCash(cm.getInventory(ls).getItem(i).getItemId())) {
					if(i<10){
						avail += "#L" + i + "# 0"+i+") #v" + cm.getInventory(ls).getItem(i).getItemId() + "# #b#t" + cm.getInventory(ls).getItem(i).getItemId() + "##k#l\r\n";
					}else{
						avail += "#L" + i + "# "+i+") #v" + cm.getInventory(ls).getItem(i).getItemId() + "# #b#t" + cm.getInventory(ls).getItem(i).getItemId() + "##k#l\r\n";
					}
				}
            }
            slot.push(i);
        }
        cm.sendSimple("请选择你要添加到拍卖行的装备。#k#n\r\n\r\n" + avail);
    } else if (status == 1) {
		vv = selection;
		var b = "";
		装备 = cm.getInventory(ls).getItem(slot[selection]).getItemId();
		b += "#b \t#v"+装备+"# #b#t"+装备+"##k\r\n";
		
         力量 = cm.getInventory(ls).getItem(slot[selection]).getStr();
		if (力量 > 0) {
            b += "#b \t力量:#r " + 力量 + "#k\r\n";
        }
         敏捷 = cm.getInventory(ls).getItem(slot[selection]).getDex();
		if (敏捷 > 0) {
            b += "#b \t敏捷:#r " + 敏捷 + "#k\r\n";
        }
         智力 = cm.getInventory(ls).getItem(slot[selection]).getInt();
		if (智力 > 0) {
            b += "#b \t智力:#r " + 智力 + "#k\r\n";
        }
         运气 = cm.getInventory(ls).getItem(slot[selection]).getLuk();
		if (运气 > 0) {
            b += "#b \t运气:#r " + 运气 + "#k\r\n";
        }
         攻击力 = cm.getInventory(ls).getItem(slot[selection]).getWatk();
		if (攻击力 > 0) {
            b += "#b \t攻击力:#r " + 攻击力 + "#k\r\n";
        }
         魔法力 = cm.getInventory(ls).getItem(slot[selection]).getMatk();
		if (魔法力 > 0) {
            b += "#b \t魔法力:#r " + 魔法力 + "#k\r\n";
        }
         物理防御 = cm.getInventory(ls).getItem(slot[selection]).getWdef();
		if (物理防御 > 0) {
            b += "#b \t防御:#r " + 物理防御 + "#k\r\n";
        }
         魔法防御 = cm.getInventory(ls).getItem(slot[selection]).getMdef();
		if (魔法防御 > 0) {
            b += "#b \t魔抗:#r " + 魔法防御 + "#k\r\n";
        }
         生命 = cm.getInventory(ls).getItem(slot[selection]).getHp();
		if (生命 > 0) {
            b += "#b \tHP:#r " + 生命 + "#k\r\n";
        }
         魔力 = cm.getInventory(ls).getItem(slot[selection]).getMp();
		if (魔力 > 0) {
            b += "#b \tMP:#r " + 魔力 + "#k\r\n";
        }
         命中 = cm.getInventory(ls).getItem(slot[selection]).getAcc();
		if (命中 > 0) {
            b += "#b \t命中:#r " + 命中 + "#k\r\n";
        }
         回避 = cm.getInventory(ls).getItem(slot[selection]).getAvoid();
		if (回避 > 0) {
            b += "#b \t回避:#r " + 回避 + "#k\r\n";
        }
         手技 = cm.getInventory(ls).getItem(slot[selection]).getHands();
		if (手技 > 0) {
            b += "#b \t手技:#r " + 手技 + "#k\r\n";
        }
         移速 = cm.getInventory(ls).getItem(slot[selection]).getSpeed();
		if (移速 > 0) {
            b += "#b \t移速:#r " + 移速 + "#k\r\n";
        }
         跳跃 = cm.getInventory(ls).getItem(slot[selection]).getJump();
		if (跳跃 > 0) {
            b += "#b \t跳跃:#r " + 跳跃 + "#k\r\n";
        }
         可升级次数 = cm.getInventory(ls).getItem(slot[selection]).getUpgradeSlots();
		if (可升级次数 > 0) {
            b += "#b \t可升级次数:#r " + 可升级次数 + "#k\r\n";
        }
         已升级次数 = cm.getInventory(ls).getItem(slot[selection]).getLevel();
		if (已升级次数 >= 0) {
            b += "#b \t已升级次数:#r " + 已升级次数 + "#k\r\n";
        }
		锻造等级 = cm.getInventory(ls).getItem(slot[selection]).getDzlevel();
		if (锻造等级 > 0) {
			b += "#b \t锻造等级:#r " + 锻造等级 + "#k\r\n";
        }
		附魔 = cm.getInventory(ls).getItem(slot[selection]).getOptionValues();
		if (附魔 != null) {
            b += "#b \t" + cm.显示附魔效果上架(附魔) + "\r\n";
        }
		插槽 = cm.getInventory(ls).getItem(slot[selection]).getSocket();
		if (插槽 > 0) {
			b += "#b \t插槽:#r " + 插槽 + "#k\r\n";
		}
		
		
        cm.sendYesNo(" \t确定你要上架的装备是:\r\n\r\n"+b);
	} else if (status == 2) {
		cm.sendGetText("请输入你要出售的价格；");
	} else if (status == 3) {
		fee = cm.getText();
		if(isNaN(fee)){
			cm.sendOk("只能填入数字。");
			cm.dispose();
			return;
		}
		if(fee.indexOf(" ")!=-1){
			cm.sendOk("请不要加入空格。");
			cm.dispose();
			return;
		}
		if(fee < 0 || fee > 1000000000){
			cm.sendOk("数值不正确。");
			cm.dispose();
			return;
		}
		
		cm.添加道具到拍卖行(
		Integer.parseInt(fee),
		装备,
		1,
		1,
		"",
		"",
		0,
		0,
		0,
		"",		
		可升级次数,
		已升级次数,
		力量,
		敏捷,
		智力,
		运气,
		生命,
		魔力,
		攻击力,
		魔法力,
		物理防御,
		魔法防御,
		命中,
		回避,
		手技,
		移速,
		跳跃,
		附魔,
		插槽,
		锻造等级
		);
		
		cm.cleanItemBySlot(slot[vv],1,1);		  
		cm.sendOk("上架成功。");
		cm.dispose();

    }
}


































