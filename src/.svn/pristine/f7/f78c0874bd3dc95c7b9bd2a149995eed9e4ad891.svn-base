package console.Loadingdata;

import client.player.skills.PlayerSkill;
import client.player.skills.PlayerSummonSkillEntry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import launch.Start;
import provider.MapleData;
import provider.MapleDataDirectoryEntry;
import provider.MapleDataFileEntry;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import tools.StringUtil;

public class zevmsPlayerSkillFactory {

    private static final Map<Integer, PlayerSkill> skills = new HashMap<>();
    private static final Map<Integer, PlayerSummonSkillEntry> SummonSkillInformation = new HashMap<>();
    private static final MapleData stringData = MapleDataProviderFactory.getDataProvider("String").getData("Skill.img");
    public static Map<Integer, String> 技能名字 = new LinkedHashMap<>();

    public static void main(String args[]) {
        System.err.println("开始载入技能代码。");
        zevmsPlayerSkillFactory.cacheSkills();
    }

    public static final PlayerSkill getSkill(final int id) {
        if (!skills.isEmpty()) {
            return skills.get(Integer.valueOf(id));
        }
        final MapleDataProvider datasource = MapleDataProviderFactory.getDataProvider("Skill");
        final MapleDataDirectoryEntry root = datasource.getRoot();
        int skillid;
        for (MapleDataFileEntry topDir : root.getFiles()) {
            if (topDir.getName().length() <= 8) {
                for (MapleData data : datasource.getData(topDir.getName())) {
                    if (data.getName().equals("skill")) {
                        for (MapleData dataTwo : data) {
                            if (dataTwo != null) {
                                skillid = Integer.parseInt(dataTwo.getName());
                                Connection con = Start.getInstance().getConnection();
                                try {
                                    PreparedStatement psu = con.prepareStatement("INSERT INTO wz_mxdskillid (skillid, name,jobid) VALUES (?,?,?)");
                                    psu.setInt(1, skillid);
                                    psu.setString(2, getSkillName(skillid));
                                    psu.setInt(3, skillid / 10000);
                                    psu.executeUpdate();
                                    psu.close();
                                } catch (SQLException ex) {
                                    System.err.println("getSkill : " + ex.getMessage());
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static String getSkillName(int id) {
        String strId = Integer.toString(id);
        strId = StringUtil.getLeftPaddedStr(strId, '0', 7);
        MapleData skillroot = stringData.getChildByPath(strId);
        if (skillroot != null) {
            return MapleDataTool.getString(skillroot.getChildByPath("name"), "");
        }
        return null;
    }

    public static void cacheSkills() {
        int skillid = 9999999;
        for (MapleData skillData : stringData) {
            skillid = Integer.parseInt(skillData.getName());
            try {
                if (isExist(skillid)) {
                    getSkill(skillid);
                }
            } catch (RuntimeException e) {
            }
        }
    }

    public static boolean isExist(int skillid) {
        PlayerSkill skill = getSkill(skillid);
        return skill != null;
    }

    public static final PlayerSummonSkillEntry getSummonData(final int skillid) {
        return SummonSkillInformation.get(skillid);
    }
}
