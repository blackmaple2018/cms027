package constants;

import static constants.ServerProperties.World.开服名字;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class ServerProperties {

    public final static class Login {

        public static int INTERVAL;
        public static int USER_LIMIT;
        public static int FLAG;
        public static long RANKING_INTERVAL;
        public static short PORT;
        public static Boolean ENABLE_PIN;
        public static Boolean ENABLE_BALLONS;
        public static String SERVER_NAME;
        public static String EVENT_MESSAGE;

        static {
            Properties p = new Properties();
            try {
                p.load(new FileInputStream(System.getProperty("user.dir") + "/Configuration.ini"));
                PORT = Short.parseShort(p.getProperty("LoginPort"));
                INTERVAL = 5000;//Integer.parseInt(p.getProperty("LoginInterval"));
                USER_LIMIT = 9999;//Integer.parseInt(p.getProperty("LoginUserLimit"));
                FLAG = 1;//Integer.parseInt(p.getProperty("LoginFlag"));
                RANKING_INTERVAL = 1800000;//Integer.parseInt(p.getProperty("LoginRankingInterval"));

                ENABLE_PIN = false;//Boolean.parseBoolean(p.getProperty("LoginEnablePin"));
                ENABLE_BALLONS = false;//Boolean.parseBoolean(p.getProperty("LoginEnableBallons"));

                SERVER_NAME = 开服名字;//p.getProperty("LoginServerName")
                EVENT_MESSAGE = "";//p.getProperty("LoginEventMessage");
            } catch (IOException e) {
                System.out.println("Failed loading Login.ini - " + e);
            }
        }
    }

    public final static class Channel {

        public static int PORT;
        public static int COUNT;
        public static String EVENTS;

        static {
            Properties p = new Properties();
            try {
                p.load(new FileInputStream(System.getProperty("user.dir") + "/Configuration.ini"));
                PORT = Integer.parseInt(p.getProperty("ChannelPort"));
                COUNT = Integer.parseInt(p.getProperty("ChannelCount"));
                EVENTS = p.getProperty("ChannelEvents");
            } catch (IOException e) {
                System.out.println("Failed loading Channel.ini - " + e);
            }
        }
    }

    public final static class World {

        public static int EXP;
        public static int QUEST_EXP;
        public static int MESO;
        public static int DROP;
        public static int BOSS_DROP;
        public static int PET_EXP;
        public static int MOUNT_EXP;
        public static String SERVER_MESSAGE;
        public static String HOST;
        public static short MAPLE_VERSION;
        public static byte[] HOST_BYTE = {(byte) 127, (byte) 0, (byte) 0, (byte) 1};
        public static String REVISION;
        public static String 开服名字;
        public static int COUNT;

        static {
            Properties p = new Properties();
            try {
                p.load(new InputStreamReader(new BufferedInputStream(new FileInputStream(System.getProperty("user.dir") + "/Configuration.ini")), "UTF-8"));

                EXP = 1;//Integer.parseInt(p.getProperty("GameExpRate"));
                QUEST_EXP = 1;//Integer.parseInt(p.getProperty("GameQuestExpRate"));
                MESO = 1;//Integer.parseInt(p.getProperty("GameMesoRate"));
                DROP = 1;//Integer.parseInt(p.getProperty("GameDropRate"));
                BOSS_DROP = 1;//Integer.parseInt(p.getProperty("GameBossDropRate"));
                PET_EXP = 1;//Integer.parseInt(p.getProperty("GamePetExpRate"));
                MOUNT_EXP = 1;//Integer.parseInt(p.getProperty("GameTamingMobRate"));
                SERVER_MESSAGE = p.getProperty("GameServerMessage");
                开服名字 = p.getProperty("GameServerName");
                HOST = p.getProperty("GameServerIP");
                REVISION = p.getProperty("GameRevision");
                COUNT = Integer.parseInt(p.getProperty("WorldCount"));
                //版本
                MAPLE_VERSION = 28;
            } catch (IOException e) {
                System.out.println("Failed loading World.ini - " + e);
            }
        }
    }

    public final static class DatabaseConfig {

        public static String DB_URL = "";
        public static String DB_USER = "";
        public static String DB_PASS = "";

        static {
            Properties p = new Properties();
            try {
                p.load(new FileInputStream(System.getProperty("user.dir") + "/Configuration.ini"));

                DB_URL = "jdbc:mysql://localhost:3306/leaderms?autoReconnect=true&useSSL=false&useUnicode=true&characterEncoding=GBK";
                DB_USER = "root";
                DB_PASS = "";

            } catch (IOException e) {
                System.out.println("Failed loading Database.ini - " + e);
            }
        }
    }

    public final static class Misc {

        public static boolean CASHSHOP_AVAILABLE;
        public static boolean USE_JAVA8;
        public static boolean VPS;
        public static boolean HAMACHI;
        public static boolean RELEASE;
        public static boolean VOTE_MESSAGE;
        public static boolean WELCOME_MESSAGE;
        public static String WEB_SITE;
        public static String DATA_ROOT = System.getProperty("user.dir") + "/wz";

        static {
            Properties p = new Properties();
            try {
                p.load(new FileInputStream(System.getProperty("user.dir") + "/Configuration.ini"));

                CASHSHOP_AVAILABLE = p.getProperty("CashShopAvailable").equalsIgnoreCase("True");
                USE_JAVA8 = p.getProperty("UseJava8").equalsIgnoreCase("True");
                VPS = p.getProperty("UseVPS").equalsIgnoreCase("True");
                HAMACHI = p.getProperty("UseHamachi").equalsIgnoreCase("True");
                RELEASE = p.getProperty("UseRelease").equalsIgnoreCase("True");
                VOTE_MESSAGE = p.getProperty("ShowVoteMessage").equalsIgnoreCase("True");
                WELCOME_MESSAGE = p.getProperty("ShowWelcomeMessage").equalsIgnoreCase("True");

                WEB_SITE = p.getProperty("WebSiteLink");

            } catch (IOException e) {
                System.out.println("Failed loading Misc.ini - " + e);
            }
        }
    }
}
