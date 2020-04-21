status = -1;
var sel;

function start() {
	//cm.sendSimple("你好？我可以帮你查询掉落。在下面选择一些选项！\r\n\r\n#b#L0#查询当前地图上的怪物掉落#l\r\n#L2#根据物品的代码搜索掉落#l");//#L1#根据怪物的名字搜索掉落#l
	action(1,0,0);
}
function action (mode, type, selection) {

	if (status == 0 && mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }

    if (status == 0) {
        sel = selection;
        if (selection == 0) {
            cm.listMonsters();
		} else if (selection == 1) {
            cm.sendGetText("请输入怪物名称；");
		}  else if (selection == 2) {
            cm.sendGetText("请输入物品代码；");
		}
    } else if (status == 1) {
        if (sel == 0) {
            cm.displayDrops(selection);
			status = -1;
            cm.dispose();
        } if (sel == 1) {
            cm.displayDrops(cm.getMobId(cm.getText()));
            cm.dispose();
        } if (sel == 2) {
            if (isNaN(cm.getText())) {
                cm.sendOk("只有几个。");
                cm.dispose();
            } else {
                cm.searchToItemId(parseInt(cm.getText()));
                cm.dispose();
            }
        } 
    }
}  

function isNumber(n) {
    return !isNaN(parseFloat(n)) && isFinite(n);
}