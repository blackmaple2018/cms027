/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.player;

import database.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import launch.Start;
import packet.creators.PacketCreator;
import server.quest.MapleQuestInfoFactory;

/**
 *
 * @author wl
 */
public class PlayerQuest {

    public class QuestData {

        public int id, quest, state;
        public String data;

        public QuestData(int id, int quest, String data, int state) {
            this.id = id;
            this.quest = quest;
            this.data = data;
            this.state = state;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getQuest() {
            return quest;
        }

        public void setQuest(int q) {
            this.quest = q;
        }

        public String getData() {
            return data;
        }

        public void setData(String s) {
            this.data = s;
        }

        public int getState() {
            return state;
        }

        public void setState(int s) {
            this.state = s;
        }
    }

    private final Player player;
    private final Map<Integer, QuestData> quests = new HashMap<>();

    public PlayerQuest(Player p) {
        this.player = p;
    }

    public Player getPlayer() {
        return player;
    }

    public Map<Integer, QuestData> getQuests() {
        return quests;
    }

    public void saveQuests(Connection con) {
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM player_quests WHERE charid = ?");
            ps.setInt(1, getPlayer().getId());
            ps.executeUpdate();
            ps.close();

            if (quests.size() > 0) {
                ps = con.prepareStatement("INSERT INTO player_quests (id, charid, questid, data, state, `name`) VALUES (?,?,?,?,?,?)");
                for (Map.Entry<Integer, QuestData> kvp : quests.entrySet()) {
                    ps.setInt(1, kvp.getValue().getId());
                    ps.setInt(2, getPlayer().getId());
                    ps.setInt(3, kvp.getKey());
                    ps.setString(4, kvp.getValue().getData());
                    ps.setInt(5, kvp.getValue().getState());
                    ps.setString(6, MapleQuestInfoFactory.getInstance(kvp.getKey()).getSubJect());
                    ps.addBatch();
                }
                ps.executeBatch();
                ps.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void loadQuests(PreparedStatement ps, ResultSet rs, Connection con) {
        try {
            ps = con.prepareStatement("SELECT * FROM player_quests WHERE charid = ?");
            ps.setInt(1, getPlayer().getId());
            rs = ps.executeQuery();
            while (rs.next()) {
                int questid = rs.getInt("questid");
                QuestData qd = new QuestData(rs.getInt("id"), questid, rs.getString("data"), rs.getInt("state"));
                quests.put(questid, qd);
            }
            rs.close();
            ps.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public boolean addNewQuest(int questId, String data) {
        if (quests.containsKey(questId)) {
            return false;
        }
        Connection con = null;
        try {
            con = Start.getInstance().getConnection();
            int key = 0;
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO player_quests (id, charid, questid, data, state) VALUES (NULL, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, getPlayer().getId());
                ps.setInt(2, questId);
                ps.setString(3, data);
                ps.setInt(4, 0);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        key = rs.getInt(1);
                    }
                }
                ps.close();
            }
            quests.put(questId, new QuestData(key, questId, data, 0));
            player.getClient().write(PacketCreator.updateQuestData(questId, data));
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

            } catch (SQLException e) {
            }
        }
        return true;
    }

    public boolean hasQuestInProcess(int quest) {
        return quests.containsKey(quest) && quests.get(quest).getState() == 1;
    }

    public boolean hasQuestCompleted(int quest) {
        return quests.containsKey(quest) && quests.get(quest).getState() == 2;
    }

    public String getQuestData(int quest) {
        return quests.containsKey(quest) ? quests.get(quest).getData() : "";
    }

    public void appendQuestData(int quest, String data) {
        setQuestData(quest, getQuestData(quest) + data);
    }

    public void setQuestData(int quest, String data) {
        if (quests.containsKey(quest)) {
            quests.get(quest).setData(data);
            player.getClient().write(PacketCreator.updateQuestData(quest, data));
        }
    }

    public void startQuest(int quest) {
        //getPlayer().dropMessage(String.format("开始任务<%s>", MapleQuestInfoFactory.getInstance(quest).getSubJect()));
        quests.get(quest).setState(1);
    }

    public void completeQuest(int quest, String data) {
        //getPlayer().dropMessage(String.format("完成任务<%s>", MapleQuestInfoFactory.getInstance(quest).getSubJect()));
        setQuestData(quest, data);
        quests.get(quest).setState(2);
    }
}
