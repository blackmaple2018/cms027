package scripting.quest;

import client.Client;
import scripting.npc.NPCConversationManager;
import server.quest.MapleQuest;

public class QuestActionManager extends NPCConversationManager {
    private final boolean start; 
    private final int quest;

    public QuestActionManager(Client c, int quest, int npc, boolean start) {
        super(c, npc, null);
        this.quest = quest;
        this.start = start;
    }

    public int getQuest() {
        return quest;
    }

    public boolean isStart() {
        return start;
    }

    @Override
    public void dispose() {
        QuestScriptManager.getInstance().dispose(this, getClient());
    }

    public boolean forceStartQuest() {
        return forceStartQuest(quest);
    }

    public boolean forceStartQuest(int id) {
        return MapleQuest.getInstance(id).forceStart(getPlayer(), getNpc());
    }

    public boolean forceCompleteQuest() {
        return forceCompleteQuest(quest);
    }
    
    public void startQuest() {
        forceStartQuest();
    }

    public void completeQuest() {
        forceCompleteQuest();
    }

    public boolean forceCompleteQuest(int id) {
        return MapleQuest.getInstance(id).forceComplete(getPlayer(), getNpc());
    }
}
