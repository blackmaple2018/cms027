
var status = 0;
function start() {
    status = -1;
    action(1, 0, 0)
}

function action(mode, type, selection) {
    if (status <= 0 && mode <= 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++
    } else {
        status--
    }

    var 任务 = 1000600;
	var 任务2 = 1000602;
	if (status <= 0) {
        var selStr = "";
		
		selStr += "嗯，我是出来找吃的。其实悄悄把爸爸的书拿出来了。我太小，还看不懂啊... 这本书 #i4031016# 到底是什么内容呢?\r\n\r\n";
		
		if(cm.getLevel()>=30 && cm.getJobId()!=0 ){
			if (cm.getPlayer().getQuest().getQuestData(任务) != "e" && cm.getPlayer().getQuest().getQuestData(任务) == "2") {
				selStr += " #L1##b泰素夫的秘密之书#k#l\r\n";
			}
		}
		if(cm.getLevel()>=55 && (cm.getJobId() >= 200 && cm.getJobId() < 300)) {
			if (cm.getPlayer().getQuest().getQuestData(任务2) != "end") {
				selStr += " #L2##b洛尼和妖精们的盖新房子#k#l\r\n";
			}
		}

        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
            case 1:
                cm.dispose();
                cm.openNpc(1061004,1);
				break;
			case 2:
                cm.dispose();
                cm.openNpc(1061004,2);
				break;
			default:
				break;
        }
    }

}

