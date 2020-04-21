var status;
 
function start() {
        status = -1;
        action(1, 0, 0);
}

function action(mode, type, selection) {
        if (mode == -1) {
                cm.dispose();
        } else {
                if (mode == 0 && type > 0) {
                        cm.dispose();
                        return;
                }
                if (mode == 1)
                        status++;
                else
                        status--;
    
                if(status == 0) {
                        cm.sendYesNo("那么，你会去网吧吗？ 使用那里的空间是要收费的，价格是 #b5000#k 金币，你要进入吗？");
                } else if(status == 1) {
                        if(cm.getMeso() < 5000) {
                                cm.sendOk("哦，你没有钱，对吧？ 对不起，我不能让你进去。");
                        } else {
                                cm.gainMeso(-5000);
                                cm.warp(193000000,7);
                        }
                    
                        cm.dispose();
                }
        }
}