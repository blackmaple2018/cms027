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
            cm.sendSimple("你是不是觉得自己太丑了？整容一下看看，说不定会觉得自己变好看了。虽然你丑，但是人要自信。\r\n#L1##b我要整容#k#l");
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
                cm.sendStyle("选择你喜欢的样子吧。", facenew);
            }
        } else if (status == 2) {
            if (cm.haveItem(4052001) == true) {
                cm.gainItem(4052001, -1);
                cm.setFace(facenew[selection]);
                cm.sendOk("弄好了，你看看是不是更加好看了。");
            } else {
                cm.sendOk("亲，你没有 #i4052001# 所以我不能为你服务。");
            }
            cm.dispose();
        }
    }
}
