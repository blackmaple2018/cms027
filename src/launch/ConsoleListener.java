package launch;

import client.player.Player;
import handling.channel.ChannelServer;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import packet.transfer.write.OutPacket;

/**
 *
 * @author wl
 */
public class ConsoleListener implements Runnable {

    /**
     * When an object implementing interface <code>Runnable</code> is used to
     * create a thread, starting the thread causes the object's <code>run</code>
     * method to be called in that separately executing thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may take
     * any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                String cmd = reader.readLine();
                String args[] = cmd.split(" ");
                switch (args[0]) {
                    case "packet":
                        System.out.println("选择在线的角色：");
                        Start.getInstance().getWorldById(0).getChannels().stream().forEach((cs) -> {
                            cs.getPlayerStorage().getAllCharacters().forEach((x) -> System.out.println(String.format("ID：%d，名称：%s", x.getId(), x.getName())));
                        });
                        String selectedName = reader.readLine();
                        if ("".equals(selectedName) || !isNumber(selectedName)) {
                            continue;
                        }
                        int selectedID = Integer.parseInt(selectedName);
                        System.out.println("chrID：" + selectedID);

                        Player p = null;
                        for (ChannelServer cs : Start.getInstance().getWorldById(0).getChannels()) {
                            p = cs.getPlayerStorage().getCharacterById(selectedID);
                            if (p != null) {
                                break;
                            }
                        }

                        String packet = joinStringFrom(args, 1);

                        System.out.println("Packet：" + packet);
                        if (p != null) {
                            OutPacket wp = new OutPacket();
                            wp.writeBytes(getByteArrayFromHexString(packet));
                            p.getClient().write(wp);
                        }
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isNumber(String string) {
        return string != null && string.matches("-?\\d+(\\.\\d+)?");
    }

    public static String joinStringFrom(String args[], int start) {
        StringBuilder append = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            append.append(args[i]);
            append.append(" ");
        }
        return append.substring(0, append.length() - 1);
    }

    public static byte[] getByteArrayFromHexString(String hex) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int nexti = 0;
        int nextb = 0;
        boolean highoc = true;
        outer:
        for (;;) {
            int number = -1;
            while (number == -1) {
                if (nexti == hex.length()) {
                    break outer;
                }
                char chr = hex.charAt(nexti);
                if (chr >= '0' && chr <= '9') {
                    number = chr - '0';
                } else if (chr >= 'a' && chr <= 'f') {
                    number = chr - 'a' + 10;
                } else if (chr >= 'A' && chr <= 'F') {
                    number = chr - 'A' + 10;
                } else {
                    number = -1;
                }
                nexti++;
            }
            if (highoc) {
                nextb = number << 4;
                highoc = false;
            } else {
                nextb |= number;
                highoc = true;
                baos.write(nextb);
            }
        }
        return baos.toByteArray();
    }
}
