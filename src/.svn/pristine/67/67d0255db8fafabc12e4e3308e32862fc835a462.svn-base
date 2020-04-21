package handling;

public class ServerHandler {

    /*private int channel = -1;
    private static AtomicLong sessionId = new AtomicLong(7777);

    public ServerHandler(final int channel) {
        this.channel = channel;
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        Runnable r = ((OutPacket) message).getOnSend();
        if (r != null) {
            r.run();
        }
        super.messageSent(session, message);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        if (cause instanceof IOException || cause instanceof ClassCastException) {
            return;
        }
        Client mc = (Client) session.getAttribute(Client.CLIENT_KEY);
        if (mc != null && mc.getPlayer() != null) {
            FileLogger.printError(FileLogger.EXCEPTION_CAUGHT, cause, "引起的异常: " + mc.getPlayer());
        }
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        if (channel > -1) {
            if (ChannelServer.getInstance(channel).isShutdown() || ChannelServer.getInstance(channel) == null) {
                session.close();
                return;
            }
        } else {
            if (LoginServer.isShutdown()) {
                session.close();
                return;
            }
        }

        final byte serverRecv[] = new byte[]{70, 114, 122, (byte) Randomizer.nextInt(255)};
        final byte serverSend[] = new byte[]{82, 48, 120, (byte) Randomizer.nextInt(255)};
        final byte ivRecv[] = serverRecv;
        final byte ivSend[] = serverSend;
        byte key[] = {
            0x13, 0x00, 0x00, 0x00,
            0x08, 0x00, 0x00, 0x00,
            0x06, 0x00, 0x00, 0x00,
            (byte) 0xB4, 0x00, 0x00, 0x00,
            0x1B, 0x00, 0x00, 0x00,
            0x0F, 0x00, 0x00, 0x00,
            0x33, 0x00, 0x00, 0x00,
            0x52, 0x00, 0x00, 0x00};

        MapleCrypto sendCypher = new MapleCrypto(key, ivSend, (short) (0xFFFF - ServerProperties.World.MAPLE_VERSION));
        MapleCrypto recvCypher = new MapleCrypto(key, ivRecv, ServerProperties.World.MAPLE_VERSION);
        Client client = new Client(sendCypher, recvCypher, session);

        client.setChannel(channel);
        client.setSessionId(sessionId.getAndIncrement());
        session.write(LoginPackets.GetHello(ServerProperties.World.MAPLE_VERSION, ivSend, ivRecv, false));
        session.setAttribute(Client.CLIENT_KEY, client);
        System.out.println("IP : " + session.getRemoteAddress());
        if (channel == -1) {
            session.write(PacketCreator.ServerNotice(1, "< 自由冒险岛 >\r\nVer.027\r\n官方群：319448903\r\n目前处于开放性删档测试中，最新动态请留意群内通知。"));
        }
        //FileLogger.print("ListIP.txt", "IP: " + session.getRemoteAddress());
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        Client client = (Client) session.getAttribute(Client.CLIENT_KEY);
        if (client != null) {
            try {
                client.disconnect(true, true);
            } catch (Throwable t) {
                FileLogger.printError(FileLogger.ACCOUNT_STUCK, t);
            } finally {
                session.close();
                session.removeAttribute(Client.CLIENT_KEY);
            }
        }
        super.sessionClosed(session);
    }

    @Override
    public void messageReceived(final IoSession session, final Object message) {
        try {
            InPacket packet = new InPacket((byte[]) message);
            final byte header_num = packet.readByte();
            for (final InHeader recv : InHeader.values()) {
                if (recv.getValue() == header_num) {
                    final Client c = (Client) session.getAttribute(Client.CLIENT_KEY);
                    handlePacket(recv, packet, c);
                    return;
                }
            }
        } catch (Exception e) {
            FileLogger.printError(FileLogger.PACKET_LOG, e);
        }
    }

    @Override
    public void sessionIdle(final IoSession session, final IdleStatus status) throws Exception {
        final Client client = (Client) session.getAttribute(Client.CLIENT_KEY);
        if (client != null) {
            client.sendPing();
        }
        super.sessionIdle(session, status);
    }

    public static boolean isSpamHeader(InHeader header) {
        switch (header) {
            case MOVE_LIFE:
            case MOVE_PLAYER:
            case SPECIAL_MOVE:
            case MOVE_SUMMON:
            case MOVE_PET:
            case QUEST_ACTION:
            case HEAL_OVER_TIME:
            case CHANGE_KEYMAP:
            case USE_INNER_PORTAL:
            case TAKE_DAMAGE:
            case NPC_ACTION:
            case STRANGE_DATA:
                return true;
        }
        return false;
    }


    public static final void handlePacket(final InHeader header, final InPacket reader, final Client c) throws Exception {
        if (GameConstants.LOG_PACKETS && !isSpamHeader(header)) {
            String tab = "";
            for (int i = 4; i > header.name().length() / 8; i--) {
                tab += "\t";
            }
            System.out.println("[Recv]\t\t" + header.name() + tab + "|\t" + header.getValue() + "\t|\t" + HexTool.getOpcodeToString(header.getValue()));
            FileLogger.log("PacketLog.txt", "\r\n\r\n[Recv]\t" + header.name() + tab + "|\t" + header.getValue() + "\t|\t" + HexTool.getOpcodeToString(header.getValue()) + "\r\n\r\n");
        }
        //System.out.println(header);
        switch (header) {
            
        }
    }*/
}
