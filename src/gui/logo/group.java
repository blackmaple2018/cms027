package gui.logo;

import static console.MsgServer.QQMsgServer.sendMsgToQQ;
import static console.MsgServer.QQMsgServer.sendMsgToQQGroup;



public class group {


    public static void 私聊(String a, String b) {
        sendMsgToQQ(a, b);
    }

    public static void 群(String a, String b) {
        sendMsgToQQGroup(a, b);
    }

}
