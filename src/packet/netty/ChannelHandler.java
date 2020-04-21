package packet.netty;

import cashshop.CashCouponRequest;
import cashshop.CashShopOperation;
import client.Client;
import constants.ServerProperties;
import handling.channel.handler.BuddyListHandler;
import handling.channel.handler.ChatHandler;
import handling.channel.handler.InterServerHandler;
import handling.channel.handler.InventoryHandler;
import handling.channel.handler.MobHandler;
import handling.channel.handler.NPCHandler;
import handling.channel.handler.NotificationsHandler;
import handling.channel.handler.PartyHandler;
import handling.channel.handler.PetHandler;
import handling.channel.handler.PlayerHandler;
import handling.channel.handler.PlayerInteractionHandler;
import handling.channel.handler.PlayersHandler;
import handling.channel.handler.StatsHandling;
import handling.channel.handler.SummonHandler;
import handling.login.handler.CharLoginHandler;
import handling.login.handler.CharLoginHeaders;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.io.IOException;
import org.apache.log4j.LogManager;
import packet.creators.LoginPackets;
import packet.creators.PacketCreator;
import static packet.netty.NettyClient.CLIENT_KEY;
import packet.opcode.InHeader;
import packet.transfer.read.InPacket;
import tools.FileLogger;
import tools.HexTool;
import tools.Randomizer;

public class ChannelHandler extends ChannelInboundHandlerAdapter {

    private static final org.apache.log4j.Logger log = LogManager.getRootLogger();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        //log.info("[数据] | 新连接");
        Channel ch = ctx.channel();
        final byte serverRecv[] = new byte[]{70, 114, 122, (byte) Randomizer.nextInt(255)};
        final byte serverSend[] = new byte[]{82, 48, 120, (byte) Randomizer.nextInt(255)};
        final int siv = HexTool.bytes2Int(serverSend);
        final int riv = HexTool.bytes2Int(serverRecv);
        Client c = new Client(ch, siv, riv);
        c.write(LoginPackets.GetHello(ServerProperties.World.MAPLE_VERSION, riv, siv));
        ch.attr(CLIENT_KEY).set(c);
        c.sendPing();
        if (c.getChannel() == -1) {
            //c.write(PacketCreator.ServerNotice(1, "< 自由冒险岛 >\r\nVer.027\r\n官方群：319448903\r\n目前处于开放性删档测试中，最新动态请留意群内通知。"));
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        //log.info("[数据] | 断开连接");
        Channel ch = ctx.channel();
        Client c = (Client) ch.attr(CLIENT_KEY).get();
        if (c != null) {
            try {
                c.disconnect(true, true);
            } catch (Throwable t) {
                FileLogger.printError(FileLogger.ACCOUNT_STUCK, t);
            } finally {
                c.close();
                ch.attr(CLIENT_KEY).set(null);
            }
        }
    }

    /** <玩家操作>*
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Client c = (Client) ctx.channel().attr(CLIENT_KEY).get();
        InPacket reader = (InPacket) msg;
        int op = reader.readByte();
        InHeader opHeader = InHeader.getInHeaderByOp(op);
        if (opHeader == null) {
            handleUnknown(reader, op);
            return;
        }
        /*if (!InHeader.isSpamHeader(InHeader.getInHeaderByOp(op))) {
            log.info(String.format("[In]\t| %s, %d/0x%s\t| %s", InHeader.getInHeaderByOp(op), op, Integer.toHexString(op).toUpperCase(), reader));
        }*/
        switch (opHeader) {
            case PONG:
                c.pongReceived();
                break;
            case AFTER_LOGIN:
                if (ServerProperties.Login.ENABLE_PIN) {
                    CharLoginHandler.AfterLogin(reader, c);
                } else {
                    c.write(LoginPackets.PinOperation((byte) CharLoginHeaders.PIN_ACCEPTED));
                }
                break;
            //服务器列表
            case SERVERLIST_REQUEST:
            case SERVERLIST_REREQUEST:
                CharLoginHandler.ServerListRequest(c);
                break;
            //接受，接受
            case ACCEPT_TOS:
                CharLoginHandler.AcceptToS(reader, c);
                break;
            //设置性别
            case SET_GENDER:
                CharLoginHandler.SetGender(reader, c);
                break;
            //注册密码
            case REGISTER_PIN:
                CharLoginHandler.RegisterPin(reader, c);
                break;
            //选择频道
            case CHARLIST_REQUEST:
                CharLoginHandler.CharlistRequest(reader, c);
                break;
            case CHAR_SELECT:
                CharLoginHandler.CharacterSelect(reader, c);
                break;
            case LOGIN_PASSWORD:
                CharLoginHandler.Login(reader, c);
                break;
            case RELOG:
                c.write(LoginPackets.GetRelogResponse());
                break;
            //点击大区
            case SERVERSTATUS_REQUEST:
                CharLoginHandler.ServerStatusRequest(reader, c);
                break;
            case CHECK_CHAR_NAME:
                CharLoginHandler.CheckCharName(reader.readMapleAsciiString(), c);
                break;
            case CREATE_CHAR:
                CharLoginHandler.CreateChar(reader, c);
                break;
            case DELETE_CHAR:
                CharLoginHandler.DeleteChar(reader, c);
                break;
            case VIEW_ALL_CHAR:
                CharLoginHandler.ViewChar(reader, c);
                break;
            case PICK_ALL_CHAR:
                CharLoginHandler.PickCharHandler(reader, c);
                break;
            // END OF LOGIN SERVER
            case CLIENT_ERROR:
                break;
            //切换游戏频道
            case CHANGE_CHANNEL:
                InterServerHandler.ChangeChannel(reader, c);
                break;
            //聊天
            case GENERAL_CHAT:
                ChatHandler.GeneralChat(reader, c);
                break;
            //私聊
            case WHISPER:
                ChatHandler.Whisper_Find(reader, c);
                break;
            case NPC_TALK:
                NPCHandler.NPCTalk(reader, c);
                break;
            case NPC_TALK_MORE:
                NPCHandler.NPCMoreTalk(reader, c);
                break;
            case QUEST_ACTION:
                NPCHandler.QuestAction(reader, c);
                break;
            //游戏商店
            case NPC_SHOP:
                NPCHandler.NPCShop(reader, c, c.getPlayer());
                break;
            //物品收集
            case ITEM_GATHER:
                InventoryHandler.ItemGather(reader, c);
                break;
            //物品移动
            case ITEM_MOVE:
                InventoryHandler.ItemMove(reader, c);
                break;
            //角色丢金币
            case MESO_DROP:
                PlayerHandler.DropMeso(reader, c);
                break;
            //角色进入游戏频道
            case PLAYER_LOGGEDIN:
                InterServerHandler.Loggedin(reader, c);
                break;
            //场景切换
            case CHANGE_MAP:
                if (reader.available() == 0) {
                    //进入商城
                    InterServerHandler.LeaveCS(reader, c);
                } else {
                    //切换地图
                    PlayerHandler.ChangeMap(reader, c);
                }
                break;
            //怪物移动
            case MOVE_LIFE:
                MobHandler.MoveMonster(reader, c);
                break;
            //近战攻击
            case MELEE_ATTACK:
                PlayerHandler.MeleeAttack(reader, c);
                break;
            //远程攻击
            case RANGED_ATTACK:
                PlayerHandler.RangedAttack(reader, c);
                break;
            //魔法攻击
            case MAGIC_ATTACK:
                PlayerHandler.MagicDamage(reader, c);
                break;
            //受到伤害
            case TAKE_DAMAGE:
                PlayerHandler.TakeDamage(reader, c);
                break;
            //角色移动
            case MOVE_PLAYER:
                PlayerHandler.MovePlayer(reader, c.getPlayer());
                break;
            //使用现金物品
            case USE_CASH_ITEM:
                InventoryHandler.UseCashItem(reader, c);
                break;
            //使用物品
            case USE_ITEM:
                InventoryHandler.UseItem(reader, c);
                break;
            //使用返回卷轴
            case USE_RETURN_SCROLL:
                InventoryHandler.UseItem(reader, c);
                break;
            //使用卷轴
            case USE_UPGRADE_SCROLL:
                InventoryHandler.UseUpgradeScroll(reader, c);
                break;
            //使用召唤包
            case USE_SUMMON_BAG:
                InventoryHandler.UseSummonBag(reader, c);
                break;
            //角色表情
            case FACE_EXPRESSION:
                PlayerHandler.ChangeEmotion(reader, c);
                break;
            //角色自动恢复
            case HEAL_OVER_TIME:
                PlayerHandler.ReplenishHpMp(reader, c);
                break;
            //捡物品
            case ITEM_PICKUP:
                InventoryHandler.PickupPlayer(reader, c);
                break;
            case CHAR_INFO_REQUEST:
                PlayerHandler.OpenInfo(reader, c);
                break;
            //移动技能，BUFF技能
            case SPECIAL_MOVE:
                PlayerHandler.SpecialMove(reader, c);
                break;
            case USE_INNER_PORTAL:
                PlayerHandler.InnerPortal(reader, c);
                break;
            case TROCK_ADD_MAP:
                PlayerHandler.TrockAddMap(reader, c, c.getPlayer());
                break;
            case CANCEL_BUFF:
                PlayerHandler.CancelBuffHandler(reader, c);
                break;
            case CANCEL_ITEM_EFFECT:
                PlayerHandler.CancelItemEffect(reader, c);
                break;
            case PLAYER_INTERACTION:
                PlayerInteractionHandler.handleAction(reader, c);
                break;
            case RPS_ACTION:
                PlayerInteractionHandler.RockPaperScissors(reader, c);
                break;
            //能力点操作
            case DISTRIBUTE_AP:
                StatsHandling.DistributeAP(reader, c);
                break;
            //技能点操作
            case DISTRIBUTE_SP:
                StatsHandling.DistributeSP(reader, c);
                break;
            //更改保存快捷键
            case CHANGE_KEYMAP:
                PlayerHandler.ChangeKeymap(reader, c);
                break;
            //进入市场
            case CHANGE_MAP_SPECIAL:
                PlayerHandler.ChangeMapSpecial(reader, c);
                break;
            case STORAGE:
                //NPCHandler.Storage(reader, c);
                break;
            case GIVE_FAME:
                PlayersHandler.GiveFame(reader, c);
                break;
            case PARTY_OPERATION:
                PartyHandler.handlePartyOperation(reader, c);
                break;
            case DENY_PARTY_REQUEST:
                PartyHandler.PartyResponse(reader, c);
                break;
            case PARTYCHAT:
                ChatHandler.PrivateChat(reader, c);
                break;
            case USE_DOOR:
                PlayersHandler.UseDoor(reader, c);
                break;
            //商城按钮
            case ENTER_CASH_SHOP:
                InterServerHandler.EnterCS(c);//reader
                break;
            case DAMAGE_SUMMON:
                SummonHandler.DamageSummon(reader, c);
                break;
            case MOVE_SUMMON:
                SummonHandler.MoveSummon(reader, c);
                break;
            case SUMMON_ATTACK:
                SummonHandler.SummonAttack(reader, c);
                break;
            case BUDDYLIST_MODIFY:
                BuddyListHandler.BuddyOperation(reader, c);
                break;
            case ENTERED_SHIP_MAP:
                break;
            case USE_ITEMEFFECT:
                PlayerHandler.UseItemEffect(reader, c);
                break;
            //坐椅子
            case CHAIR:
                PlayerHandler.UseChair(reader, c);
                break;
            case USE_CHAIR_ITEM:
                PlayerHandler.UseItemChair(reader, c);
                break;
            case DAMAGE_REACTOR:
                PlayersHandler.HitReactor(reader, c);
                break;
            case SKILL_EFFECT:
                PlayerHandler.SkillEffect(reader, c);
                break;
            case MESSENGER:
                ChatHandler.Messenger(reader, c);
                break;
            case NPC_ACTION:
                NPCHandler.NPCAnimation(reader, c);
                break;
            case TOUCHING_CS:
                InterServerHandler.TouchingCS(reader, c);
                break;
            case BUY_CS_ITEM:
                CashShopOperation.CashShopAction(reader, c);
                break;
            case COUPON_CODE:
                CashCouponRequest.CouponCode(reader, c);
                break;
            case SPAWN_PET:
                PetHandler.SpawnPet(reader, c);
                break;
            case MOVE_PET:
                PetHandler.MovePet(reader, c);
                break;
            case PET_CHAT:
                PetHandler.PetChat(reader, c);
                break;
            case PET_COMMAND:
                PetHandler.PetCommand(reader, c);
                break;
            case PET_FOOD:
                PetHandler.PetFood(reader, c);
                break;
            case PET_LOOT:
                InventoryHandler.PetMapItemPickUp(reader, c);
                break;
            case AUTO_AGGRO:
                MobHandler.AutoAggro(reader, c);
                break;
            case MONSTER_BOMB:
                MobHandler.MonsterBomb(reader, c);
                break;
            case CANCEL_DEBUFF:
                // BuffHandler.CancelDebuff(reader, c);
                break;
            case USE_SKILL_BOOK:
                InventoryHandler.UseSkillBook(reader, c);
                break;
            case SKILL_MACRO:
                PlayerHandler.SkillMacroAssign(reader, c);
                break;
            case NOTE_ACTION:
                PlayersHandler.Note(reader, c);
                break;
            case MAPLETV:
                break;
            case ENABLE_ACTION:
                PlayersHandler.EnableActions(reader, c);
                break;
            case USE_CATCH_ITEM:
                InventoryHandler.UseCatchItem(reader, c);
                break;
            case USE_MOUNT_FOOD:
                InventoryHandler.UseMountFood(reader, c);
                break;
            case CLOSE_CHALKBOARD:
                c.getPlayer().setChalkboard(null);
                c.getPlayer().getMap().broadcastMessage(PacketCreator.UseChalkBoard(c.getPlayer(), true));
                break;
            case RING_ACTION:
                PlayersHandler.RingAction(reader, c);
                break;
            case SPOUSE_CHAT:
                ChatHandler.Spouse_Chat(reader, c);
                break;
            case REPORT_PLAYER:
                NotificationsHandler.ReportPlayer(reader, c);
                break;
            case UNSTUCK:
                c.write(PacketCreator.EnableActions());
                break;
            case MOB_DAMAGE_MOB:
                MobHandler.FriendlyDamage(reader, c);
                break;
            case UNKNOWN:
                break;
            case SILVER_BOX:
                InventoryHandler.UseSilverBox(reader, c);
                break;
            case LOGGED_OUT:
                break;
            case PET_ITEM_IGNORE:
                PetHandler.PetExcludeItems(reader, c);
                break;
            case GRENADE:
                MobHandler.GrenadeEffect(reader, c);
                break;
            default:
                if (reader.available() >= 0) {
                    FileLogger.logPacket(String.valueOf(opHeader), "[" + opHeader.toString() + "] " + reader.toString());
                }
                break;
        }
    }

    private void handleUnknown(InPacket inPacket, int opCode) {
        /*if (!InHeader.isSpamHeader(InHeader.getInHeaderByOp(opCode))) {
            log.error(String.format("未处理的封包操作 %s/0x%s, 数据 %s", opCode, Integer.toHexString(opCode).toUpperCase(), inPacket));
        }*/
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof IOException) {
            //log.info("客户端强制关闭游戏.");
        } else {
            cause.printStackTrace();
        }
    }
}
