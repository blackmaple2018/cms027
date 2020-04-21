/* 
 ZEVMS冒险岛(027)游戏服务端
 废弃副本
 */

var status = 0;
var state;
var em = null;

function start() {
    status = -1;
    state = (cm.getMapId() >= 103000800 && cm.getMapId() <= 103000805) ? 1 : 0;
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
        if (mode == 1) {
            status++;
        }else {
            status--;
		}
        if (status == 0) {
            if (state == 1) {
                cm.sendYesNo("你想放弃这个地区吗？");
            }
            else {
                em = cm.getEventManager("KerningPQ");
                if (em == null) {
                    cm.sendOk("副本错误。");
                    cm.dispose();
                }

                cm.sendSimple("#e#b<废弃沼泽副本>\r\n#k#n" + em.getProperty("party") + "\r\n\r\n你和你的党员一起去完成一项任务怎么样？在这里你会发现障碍和问题，如果没有很好的团队合作，你将无法战胜它们。如果你想试试，请告诉你的队长和我谈谈。#b\r\n#L0#我想参加组队副本。\r\n#L1#我想找队伍。\r\n#L2#我想听更多的细节。");
            }
        } else if (status == 1) {
            if (state == 1) {
                cm.warp(103000000);
                cm.dispose();
            }
            else {
                if (selection == 0) {
                    if (cm.getParty() == null) {
                        cm.sendOk("只有在你在队伍中的时候，你才能参加副本。");
                        cm.dispose();
                    } else if (!cm.isLeader()) {
                        cm.sendOk("请让你的队长和我交谈。");
                        cm.dispose();
                    } else {
                        var eli = em.getEligibleParty(cm.getParty());
                        if (eli.size() > 0) {
                            if (!em.startInstance(cm.getParty(), cm.getPlayer().getMap(), 1)) {
                                cm.sendOk("当前频道已经有队伍在挑战中，你可以更换频道尝试一下，或者等待他们挑战完成。");
                            }
                        } else {
                            cm.sendOk("你还不能开始这个副本，你的一些队友没有资格尝试，或者他们不在这张地图上。如果你找不到队友。");
                        }

                        cm.dispose();
                    }
                } else if (selection == 1) {
                    cm.sendOk("尝试邀请你的朋友加入！");
                    cm.dispose();
                } else {
                    cm.sendOk("#e#b<废弃沼泽副本>\r\n#k#n\r\n你的团队，必须经历许多障碍和困惑。与你的团队协调，以进一步推进和击败最终的BOSS，并收集掉的物品，以进入奖励和奖金阶段。");
                    cm.dispose();
                }
            }
        }
    }
}