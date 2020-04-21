
var status = 0;
var beauty = 0;
var price = 1000000;
var mface = Array(20000, 20001, 20002, 20003, 20004, 20005, 20006, 20007, 20008);
var fface = Array(21000, 21001, 21002, 21003, 21004, 21005, 21006, 21007, 21008);
var facenew = Array();

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0 && status == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            cm.sendSimple("你是不是觉得自己太丑了？整容一下看看，说不定会觉得自己变好看了。如果你觉得我整的不好，那去找我师父吧。哈哈哈·····\r\n#L1##b随便给我整一下吧#k#l");
        } else if (status == 1) {
            if (selection == 1) {
                facenew = Array();
                if (cm.getPlayer().getGender() == 0) {
                    for (var i = 0; i < mface.length; i++) {
                        facenew.push(mface[i] + cm.getPlayer().getFace()
                                % 1000 - (cm.getPlayer().getFace()
                                        % 100));
                    }
                }
                if (cm.getPlayer().getGender() == 1) {
                    for (var i = 0; i < fface.length; i++) {
                        facenew.push(fface[i] + cm.getPlayer().getFace()
                                % 1000 - (cm.getPlayer().getFace()
                                        % 100));
                    }
                }
                cm.sendYesNo("确定要整容？");
            }
        }
        else if (status == 2) {
            cm.dispose();
            if (cm.haveItem(4052004) == true) {
                cm.gainItem(4052004, -1);
                cm.setFace(facenew[Math.floor(Math.random() * facenew.length)]);
                cm.sendOk("看看~!");
            } else {
                cm.sendOk("亲，你没有 #i4052004# 所以我不能为你服务。");
            }
            cm.dispose();
        }
    }
}
