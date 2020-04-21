var status = 0;
var maps = Array(104000000, 100000000, 101000000, 102000000,103000000,60000);
var rCost = Array(1000, 1000, 1000, 1000, 1000,1000);
var costBeginner = Array(100, 100, 100, 100, 100,1000);
var cost = new Array("1000", "1000", "1000", "1000", "1000","1000");
var show;
var sCost;
var selectedMap = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status == 1 && mode == 0) {
        cm.dispose();
        return;
    } else if (status >= 2 && mode == 0) {
        cm.sendNext("这里还有很多地方可以逛。当你想要去不同的城镇的时候，欢迎随时来找我吧。");
        cm.dispose();
        return;
    }
    if (mode == 1)
        status++;
    else
        status--;
    if (status == 0) {
        cm.sendNext("您好~!  请问你想要往其他村庄安全又快速的移动吗? ");
    } else if (status == 1) {
        var job = cm.getJobId();
        if (job == 0) {
            var selStr = "我们有特殊90%折扣，对于新手选择你的目的地#b \n\r请选择目的地.#b";
            for (var i = 0; i < maps.length; i++) {
                selStr += "\r\n#L" + i + "##m" + maps[i] + "# (" + costBeginner[i] + " 金币)#l";
            }
        } else {
            var selStr = "请选择目的地；#b";
            for (var i = 0; i < maps.length; i++) {
                selStr += "\r\n#L" + i + "##m" + maps[i] + "# (" + cost[i] + " 金币)#l";
            }
        }
        cm.sendSimple(selStr);
    } else if (status == 2) {
        var job = cm.getJobId();
        if (job == 0 ) {
            sCost = costBeginner[selection];
            show = costBeginner[selection];
        } else {
            sCost = rCost[selection];
            show = cost[selection];
        }
        cm.sendYesNo("你在这里没有任何事情做，前往 #b#m" + maps[selection] + "##k 将花费你的 #b" + show + " 金币#k.");
        selectedMap = selection;
    } else if (status == 3) {
        if (cm.getMeso() < sCost) {
            cm.sendNext("很抱歉由于你没有足够的金币.");
        } else {
            cm.gainMeso(-sCost);
            cm.warp(maps[selectedMap],0);
        }
        cm.dispose();
    }
}
