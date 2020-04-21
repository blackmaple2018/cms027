package zevms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import launch.Start;
import static zevms.Todayitinerary.今日行程记录;

public class Deletemysql {

    public static void Deletelog() {
        //清空角色每日信息
        Delete("bosslog");

        //清空账号每日信息
        Delete("bossloa");

        //清空人气记录
        Delete("famelog");

        重置今日();
    }

    public static void 重置今日() {
        int 星期 = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        //重置今日在线时间
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET viptime = viptime - 1 WHERE viptime > 0")) {
                ps.executeUpdate();
            }
            //如果当天在线时间达到2小时，就会增加1点会员经验
            try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET vip = vip + 1 WHERE viptime > 0 && todayOnlineTime >=120")) {
                ps.executeUpdate();
            }
            //重置每日在线时间
            try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET todayOnlineTime = 0")) {
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET daily_itemid = 0")) {
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET daily_npcid = 0")) {
                ps.executeUpdate();
            }
            //周一重置排位
            if (星期 == 1) {
                //排位积分
                try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET integral = 100")) {
                    ps.executeUpdate();
                }
                //本周在线
                try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET totalOnlineTimett = 0")) {
                    ps.executeUpdate();
                }
            }
            //周日重置技能奖励
            if (星期 == 0) {
                try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET tianti = 0")) {
                    ps.executeUpdate();
                }
            }
            今日行程记录.clear();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
    }

    private static void Delete(String a) {
        try (Connection con = Start.getInstance().getConnection()) {
            PreparedStatement ps = con.prepareStatement("Delete from " + a + "");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Deletemysql/" + a + ":" + e);
        }
    }
}
