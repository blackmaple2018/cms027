

function start() {
    status = -1;
    action(1, 0, 0)
}

function action(mode, type, selection) {
    if (status <= 0 && mode <= 0) {
        cm.dispose();
        return
    }
    if (mode == 1) {
        status++
    } else {
        status--
    }	 
	var 任务 = 1000500;
	if (status <= 0) {
        var selStr = "";
		
		selStr += "我是射手村的村子，你找我有事情吗？如果你对村子的发展有更好的建议，不妨跟我说说看。\r\n\r\n";
		//休咪丢失的金币/等级等级20
		if(cm.getLevel() >= 20 && cm.getJobId()!=0){
			if (cm.getPlayer().getQuest().getQuestData(任务) != "end") {
				if(!cm.haveItem(4001003, 1)){
					selStr += " #L1##b离家出去的阿勒斯#k#l\r\n";
				}
			}
		}

        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
            case 1:
                cm.dispose();
                cm.openNpc(1012003,1);
                break;

			default:
				break;
        }
    }
}