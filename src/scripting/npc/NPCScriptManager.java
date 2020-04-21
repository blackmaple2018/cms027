package scripting.npc;

import java.util.List;
import java.util.Map;
import javax.script.Invocable;
import client.Client;
import client.player.Player;
import security.violation.CheatingOffense;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.WeakHashMap;
import community.MaplePartyCharacter;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import packet.creators.PacketCreator;
import scripting.AbstractScriptManager;
import server.partyquest.mcpq.MCParty;
import tools.FileLogger;

/**
 * @author Matze
 */
public class NPCScriptManager extends AbstractScriptManager {

    private final transient Lock npcLock = new ReentrantLock();
    private final Map<Client, NPCScript> scripts = new WeakHashMap<>();
    private final Map<Client, NPCConversationManager> cms = new WeakHashMap<>();
    private static final NPCScriptManager instance = new NPCScriptManager();

    public static final NPCScriptManager getInstance() {
        return instance;
    }

    public final void start(final Client c, final int npc) {
        start(c, npc, null, null, 0);
    }

    public final void start(final Client c, final int npc, final int ex) {
        start(c, npc, null, null, ex);
    }

    public void start(String filename, Client c, int npc, List<MaplePartyCharacter> chrs, MCParty pty) {
        npcLock.lock();
        try {
            NPCConversationManager cm = new NPCConversationManager(c, npc, chrs, pty);
            cm.dispose();
            if (cms.containsKey(c)) {
                return;
            }
            if (c.canClickNPC()) {
                cms.put(c, cm);
                Invocable iv = null;
                if (filename != null) {
                    iv = getInvocable("npc/" + filename + ".js", c);
                }
                NPCScriptManager npcsm = NPCScriptManager.getInstance();
                if (iv == null || NPCScriptManager.getInstance() == null) {
                    cm.dispose();
                    return;
                }
                if (npcsm == null) {
                    cm.dispose();
                    return;
                }
                engine.put("cm", cm);
                NPCScript ns = iv.getInterface(NPCScript.class);
                scripts.put(c, ns);
                ns.start(chrs, pty);
                c.setClickedNPC();
            } else {
                c.announce(PacketCreator.EnableActions());
            }
        } catch (Exception e) {
            FileLogger.printError(FileLogger.QQMsgServer + npc + ".txt", e);
            dispose(c);
            cms.remove(c);
        } finally {
            npcLock.unlock();
        }
    }

    public void start(Client c, int npc, String filename, Player chr, int ex) {
        npcLock.lock();
        try {
            NPCConversationManager cm = new NPCConversationManager(c, npc, ex);
            if (cms.containsKey(c)) {
                return;
            }
            if (c.canClickNPC()) {
                cms.put(c, cm);
                Invocable iv = null;
                String path = "";
                if (filename != null) {
                    path = "npc/" + filename + ".js";
                    iv = getInvocable("npc/" + filename + ".js", c);
                }
                if (iv == null) {
                    path = "npc/" + npc + ".js";
                    if (ex == 0) {
                        iv = getInvocable("npc/" + npc + ".js", c);
                        if (c.getPlayer().getGM() > 0) {
                            c.getPlayer().dropMessage(5, "scripts/npc/" + npc + ".js");
                        }
                    } else {
                        iv = getInvocable("npc/" + npc + "_" + ex + ".js", c);
                        if (c.getPlayer().getGM() > 0) {
                            c.getPlayer().dropMessage(5, "scripts/npc/" + npc + "_" + ex + ".js");
                        }
                    }
                }
                if (iv == null || NPCScriptManager.getInstance() == null) {
                    /*if (ex == 0) {
                        c.getPlayer().dropMessage(5, "[脚本错误]: " + npc + "");
                    } else {
                        c.getPlayer().dropMessage(5, "[脚本错误]: " + npc + "_" + ex + "");
                    }*/
                    dispose(c);
                    return;
                }
                engine.put("cm", cm);
                NPCScript ns = iv.getInterface(NPCScript.class);
                scripts.put(c, ns);
                c.setClickedNPC();
                if (chr == null) {
                    ns.start();
                } else {
                    ns.start(chr);
                }
            } else {
                c.announce(PacketCreator.EnableActions());
            }
        } catch (UndeclaredThrowableException ute) {
            FileLogger.printError(FileLogger.NPC + npc + ".txt", ute);
            dispose(c);
            cms.remove(c);
            notice(c, npc);
        } catch (Exception e) {
            FileLogger.printError(FileLogger.NPC + npc + ".txt", e);
            dispose(c);
            cms.remove(c);
            notice(c, npc);
        } catch (NoClassDefFoundError ndfe) {
            ndfe.printStackTrace();
        } finally {
            npcLock.unlock();
        }
    }

    public void action(Client c, byte mode, byte type, int selection) {
        NPCScript ns = scripts.get(c);
        if (ns != null) {
            npcLock.lock();
            try {
                if (selection < -1) {
                    CheatingOffense.PACKET_EDIT.cheatingSuspicious(c.getPlayer(), "玩家试图向npc发送一个否定的选择攻击。请监视此人，并让管理人员知道。");
                    return;
                }
                c.setClickedNPC();
                ns.action(mode, type, selection);
            } catch (Exception e) {
                FileLogger.printError(FileLogger.NPC + getCM(c).getNpc() + ".txt", e);
                notice(c, getCM(c).getNpc());
                dispose(c);
            } finally {
                npcLock.unlock();
            }
        }
    }

    public void dispose(NPCConversationManager cm) {
        Client c = cm.getClient();
        cms.remove(c);
        scripts.remove(c);
        c.removeClickedNPC();
        //c.getPlayer().setNpcCooldown(System.currentTimeMillis());
        if (cm.getwh() == 0) {
            resetContext("npc/" + cm.getNpc() + ".js", c);
        } else {
            resetContext("npc/" + cm.getNpc() + "_" + cm.getwh() + ".js", c);
        }
    }

    public void dispose(Client c) {
        if (cms.get(c) != null) {
            dispose(cms.get(c));
        }
        c.write(PacketCreator.EnableActions());
    }

    private void notice(Client c, int id) {
        if (c == null) {
            c.getPlayer().dropMessage(1, "运行此NPC时出错，向管理员报告。\r\n ID : " + id + "");
        }
    }

    public NPCConversationManager getCM(Client c) {
        return cms.get(c);
    }
}
