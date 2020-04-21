package server.quest;

import constants.SkillConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import provider.MapleData;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import tools.StringUtil;

/**
 *
 * @author wl
 */
public class MapleQuestInfoFactory {

    private static final MapleData data = MapleDataProviderFactory.getDataProvider("Etc").getData("QuestInfo.img");
    private final int quest;
    private String subJect;
    private String reqJob;
    private final List<String> status = new ArrayList<>();
    private static final Map<Integer, MapleQuestInfoFactory> quests = new HashMap<>();

    public MapleQuestInfoFactory(int id) {
        this.quest = id;
        MapleData reqInfo = data.getChildByPath(StringUtil.getLeftPaddedStr(String.valueOf(id), '0', 7));
        if (reqInfo != null) {
            reqInfo.getChildren().stream().map((md) -> {
                return md;
            }).forEach((md) -> {
                if (md.getName().equals("info")) {
                    this.reqJob = MapleDataTool.getString("req", md, "没有等级限制");
                    this.subJect = MapleDataTool.getString("subject", md, "");
                } else {
                    status.add(md.getName());
                }
            });
        }
    }
    
    public int getQuest() {
        return quest;
    }
    
    public String getSubJect() {
        return subJect;
    }
    
    public String getStartData() {
        return status.size() > 0 ? status.get(0) : "000";
    }

    public static MapleQuestInfoFactory getInstance(int quest) {
        MapleQuestInfoFactory ret = quests.get(quest);
        if (ret == null) {
            ret = new MapleQuestInfoFactory(quest);
            quests.put(quest, ret);
        }
        return ret;
    }

    public boolean canStartQuest(int level, short job) {
        String req = this.reqJob;
        if (req == null) {
            return true;
        }
        if (req.contains("没有等级限制")) {
            return true;
        }
        if (req.contains("等级15，新手不能做")) {
            if (level >= 15 && job != 0) {
                return true;
            }
        }
        if (req.contains("等级15，全职业都可以")) {
            if (level >= 15) {
                return true;
            }
        }
        if (req.contains("等级20")) {
            if (level >= 20) {
                return true;
            }
        }
        if (req.contains("等级20，新手不能做。")) {
            if (level >= 20 && job != 0) {
                return true;
            }
        }
        if (req.contains("等级25，新手不能做。")) {
            if (level >= 25 && job != 0) {
                return true;
            }
        }
        if (req.contains("等级25，全职业都可以。") || req.contains("等级 25, 全职业")) {
            if (level >= 25) {
                return true;
            }
        }
        if (req.contains("等级30，新手不能做。")) {
            if (level >= 30 && job != 0) {
                return true;
            }
        }
        if (req.contains("等级30，全职业都可以") || req.contains("等级30")) {
            if (level >= 30) {
                return true;
            }
        }
        if (req.contains("等级35，新手不能做。")) {
            if (level >= 35 && job != 0) {
                return true;
            }
        }
        if (req.contains("等级40，新手不能做。")) {
            if (level >= 40 && job != 0) {
                return true;
            }
        }
        if (req.contains("等级40，全职业都可以") || req.contains("等级40")) {
            if (level >= 40) {
                return true;
            }
        }
        if (req.contains("等级45，新手不能做。")) {
            if (level >= 45 && job != 0) {
                return true;
            }
        }
        if (req.contains("等级50，全职业都可以")) {
            if (level > 50) {
                return true;
            }
        }
        if (req.contains("等级50，只有战士可以挑战。")) {
            if (level >= 50 && SkillConstants.isWarrior(job)) {
                return true;
            }
        }
        if (req.contains("等级55，只有盗贼可以挑战。")) {
            if (level >= 55 && SkillConstants.isThief(job)) {
                return true;
            }
        }
        if (req.contains("等级55，只有魔法师可以挑战。")) {
            if (level >= 55 && SkillConstants.isMagician(job)) {
                return true;
            }
        }
        if (req.contains("等级55，只有弓箭受可以挑战。")) {
            if (level >= 55 && SkillConstants.isArcher(job)) {
                return true;
            }
        }
        if (req.contains("等级55，全职业都可以。")) {
            if (level >= 55) {
                return true;
            }
        }
        if (req.contains("等级61，全职业都可以。")) {
            if (level >= 61) {
                return true;
            }
        }
        if (req.contains("等级60，全职业都可以。")) {
            if (level >= 60) {
                return true;
            }
        }
        if (req.contains("等级 65, 所有职也都可以")) {
            if (level >= 65) {
                return true;
            }
        }
        return false;
    }

    public static List<MapleQuestInfoFactory> getEligibleQuests(int level, short job) {
        List<MapleQuestInfoFactory> ret = new ArrayList<>();
        quests.values().stream().filter((mqif) -> (mqif.canStartQuest(level, job))).forEach((mqif) -> {
            ret.add(mqif);
        });
        return ret;
    }
    
    public static void main(String args[]) {
       loadQuestInfo();
    }
    
    public static void loadQuestInfo() {
        for (MapleData md : data.getChildren()) {
            getInstance(Integer.parseInt(md.getName()));
        }
        //System.out.println("共载入任务数量：" + quests.size());
    }

    public boolean checkChinese(String d) {
        return Pattern.compile("[\u4e00-\u9fa5]").matcher(d).find();
    }
}
