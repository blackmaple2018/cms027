package handling.channel.handler;

import client.player.Player;
import client.Client;
import client.player.RockPaperScissors;
import client.player.inventory.types.InventoryType;
import client.player.inventory.Item;
import security.violation.AutobanManager;
import security.violation.CheatingOffense;
import constants.GameConstants;
import constants.ItemConstants;
import static handling.channel.handler.ChannelHeaders.PlayerInteractionHeaders.*;
import packet.transfer.read.InPacket;
import java.util.Arrays;
import packet.creators.MinigamePackets;
import packet.creators.PacketCreator;
import packet.creators.PersonalShopPackets;
import server.itens.Trade;
import server.itens.InventoryManipulator;
import server.itens.ItemInformationProvider;
import server.maps.FieldLimit;
import server.maps.object.FieldObject;
import server.maps.object.FieldObjectType;
import server.minirooms.Minigame;
import server.minirooms.PlayerShop;
import server.minirooms.PlayerShopItem;
import tools.FileLogger;

public class PlayerInteractionHandler {

    public static void handleAction(InPacket packet, Client c) {
        Player p = c.getPlayer();
        //recv：42 00 03 00
        //recv：42 02 03 00 00 00
        byte b = packet.readByte();
        switch (b) {
            case ACT_CREATE:
                createRoom(p, packet);
                break;
            case ACT_INVITE:
                inviteToRoom(p, packet);
                break;
            case ACT_DECLINE:
                declineInvite(p);
                break;
            case ACT_VISIT:
                joinRoom(p, packet);
                break;
            case ACT_CHAT:
                chat(p, packet);
                break;
            case ACT_EXIT:
                leaveRoom(p);
                break;
            case ACT_OPEN:
                openRoom(p, packet);
                break;
            case ACT_READY:
                gameReady(p);
                break;
            case ACT_UN_READY:
                gameUnready(p);
                break;
            case ACT_START:
                gameStart(p);
                break;
            case ACT_GIVE_UP:
                gameGiveUp(p);
                break;
            case ACT_REQUEST_TIE:
                gameRequestTie(p);
                break;
            case ACT_ANSWER_TIE:
                gameAnswerTie(p, packet);
                break;
            case ACT_SKIP:
                gameSkip(p);
                break;
            case ACT_MOVE_OMOK:
                gameMoveOmok(p, packet);
                break;
            case ACT_SELECT_CARD:
                gameSelectCard(p, packet);
                break;
            case ACT_SET_MESO:
                setMeso(p, packet);
                break;
            case ACT_SET_ITEMS:
                setItems(p, packet);
                break;
            case ACT_CONFIRM:
                sendConfirm(p);
                break;
            case ACT_ADD_ITEM:
            case ACT_PUT_ITEM:
                addItem(p, packet);
                break;
            case ACT_BUY:
            case ACT_MERCHANT_BUY:
                buyItem(p, packet);
                break;
            case ACT_REMOVE_ITEM:
                removeItem(p, packet);
                break;
            case ACT_BAN_PLAYER:
                banPlayer(p, packet);
                break;
            case ACT_EXPEL:
                expelMember(p);
                break;
            case ACT_SPAWN_SHOP:
                break;
            default:
                System.out.println("Handler undefined: " + b);
                break;
        }
    }

    private static boolean cantCreateInteraction(Player p, byte type, boolean creation) {
        if (!creation && p.getTrade() != null) {
            p.announce(PacketCreator.EnableActions());
            return false;
        }
        switch (type) {
            case MODE_OMOK:
            case MODE_MATCH_CARDS:
                if (p.getChalkboard() != null || FieldLimit.CANNOTMINIGAME.check(p.getMap().getFieldLimit())) {
                    p.announce(PacketCreator.EnableActions());
                    return false;
                }
                break;
            case MODE_PLAYER_SHOP:
            case MODE_HIRED_MERCHANT:
                if (!p.getMap().getMapObjectsInRange(p.getPosition(), 23000, Arrays.asList(FieldObjectType.HIRED_MERCHANT)).isEmpty() || p.getMapId() < 910000000 && p.getMapId() > 910000023) {
                    p.announce(PacketCreator.EnableActions());
                    return false;
                }
                break;
        }
        return true;
    }

    private static void createRoom(Player p, InPacket packet) {
        String text, password;
        //42 00 03 00
        switch (packet.readByte()) {
            case MODE_OMOK: {

                if (!cantCreateInteraction(p, MODE_OMOK, true)) {
                    return;
                }

                text = packet.readMapleAsciiString();
                if (packet.readBool()) {
                    password = packet.readMapleAsciiString();
                } else {
                    password = null;
                }
                int subType = packet.readByte();
                Minigame game = new Minigame(p, text, password);
                p.setMiniGame(game);
                game.setPieceType(subType);
                game.setGameType("omok");
                p.getMap().addMapObject(game);
                p.getMap().broadcastMessage(MinigamePackets.AddBoxGame(p, 1, 0, true));
                game.sendOmok(p.getClient(), subType);
                break;
            }
            case MODE_MATCH_CARDS: {

                if (!cantCreateInteraction(p, MODE_MATCH_CARDS, true)) {
                    return;
                }

                text = packet.readMapleAsciiString();
                if (packet.readBool()) {
                    password = packet.readMapleAsciiString();
                } else {
                    password = null;
                }
                int subType = packet.readByte();
                Minigame game = new Minigame(p, text, password);
                game.setPieceType(subType);
                switch (subType) {
                    case 0:
                        game.setMatchesToWin(6);
                        break;
                    case 1:
                        game.setMatchesToWin(10);
                        break;
                    case 2:
                        game.setMatchesToWin(15);
                        break;
                }
                game.setGameType("matchcard");
                p.setMiniGame(game);
                p.getMap().addMapObject(game);
                p.getMap().broadcastMessage(MinigamePackets.AddBoxGame(p, 1, 0, false));
                game.sendMatchCard(p.getClient(), subType);
                break;
            }
            case MODE_TRADE: {
                if (p.getTrade() == null) {
                    Trade.startTrade(p);
                }
                break;
            }
            case MODE_PLAYER_SHOP: {
                if (!cantCreateInteraction(p, MODE_PLAYER_SHOP, true)) {
                    return;
                }

                text = packet.readMapleAsciiString();
                packet.skip(1);
                short slot = packet.readShort();
                int itemId = packet.readInt();
                if (p.getInventory(InventoryType.ETC).countById(itemId) < 1) {
                    p.getCheatTracker().registerOffense(CheatingOffense.USING_UNAVAILABLE_ITEM, Integer.toString(itemId));
                    return;
                }
                PlayerShop shop = new PlayerShop(p, text);
                p.setPlayerShop(shop);
                p.getMap().addMapObject(shop);
                shop.sendShop(p.getClient());
                p.getClient().getChannelServer().registerPlayerShop(shop);
                break;
            }
        }
    }

    private static void inviteToRoom(Player p, InPacket packet) {
        int otherPlayer = packet.readInt();
        Player otherChar = p.getMap().getCharacterById(otherPlayer);
        if (p.getMap() == null || otherChar == null) {
            return;
        }
        if (p.getId() == otherChar.getId()) {
            return;
        }
        if (p.getClient().getChannelServer().isShutdown()) {
            p.announce(PacketCreator.EnableActions());
            return;
        }
        Trade.inviteTrade(p, otherChar);
    }

    private static void declineInvite(Player p) {
        Trade.declineTrade(p);
    }
    //
    private static void joinRoom(Player p, InPacket packet) {
        if (p.getClient().getChannelServer().isShutdown()) {
            p.announce(PacketCreator.EnableActions());
            return;
        }
        if (p.getTrade() != null && p.getTrade().getPartner() != null && !p.getTrade().inTrade()) {
            Trade.visitTrade(p, p.getTrade().getPartner().getChr());
        } else {
            int oid = packet.readInt();
            FieldObject ob = p.getMap().getMapObject(oid);
            if (ob == null) {
                ob = p.getMap().getMapObject(oid);
            }

            if (ob instanceof PlayerShop) {
                PlayerShop shop = (PlayerShop) ob;
                shop.visitShop(p);
            } else if (ob instanceof Minigame) {
                String pass;
                Minigame game = (Minigame) ob;
                boolean enteredPassword = packet.readBool();
                if (enteredPassword && game.getPassword() == null) {
                    return;
                }
                if (game.getPassword() != null) {
                    pass = packet.readMapleAsciiString();
                    if (!game.getPassword().equals(pass)) {
                        p.dropMessage(1, "The password is incorrect!");
                        return;
                    }
                }
                if (game.hasFreeSlot() && !game.isVisitor(p)) {
                    game.addVisitor(p);
                    p.setMiniGame(game);
                    switch (game.getGameType()) {
                        case "omok":
                            game.sendOmok(p.getClient(), game.getPieceType());
                            break;
                        case "matchcard":
                            game.sendMatchCard(p.getClient(), game.getPieceType());
                            break;
                    }
                } else {
                    p.getClient().write(MinigamePackets.GetMiniGameFull());
                }
            }
        }
    }

    private static void chat(Player p, InPacket packet) {
        String txt = packet.readMapleAsciiString();
        if (p.getTrade() != null) {
            p.getTrade().chat(txt);
        } else if (p.getPlayerShop() != null) {
            PlayerShop shop = p.getPlayerShop();
            if (shop != null) {
                shop.chat(p.getClient(), txt);
            }
        } else if (p.getMiniGame() != null) {
            Minigame game = p.getMiniGame();
            if (game != null) {
                game.chat(p.getClient(), txt);
            }
        }
    }

    private static void leaveRoom(Player p) {
        if (p.getTrade() != null) {
            Trade.cancelTrade(p.getTrade(), p);
        } else {
            p.closePlayerShop();
            p.closeMiniGame();
        }
    }

    private static void openRoom(Player p, InPacket packet) {
        if (!cantCreateInteraction(p, (byte) 0, false)) {
            return;
        }

        PlayerShop shop = p.getPlayerShop();
        if (shop != null && shop.isOwner(p)) {
            packet.readByte();
            p.getMap().broadcastMessage(PersonalShopPackets.AddCharBox(p, 4));
            shop.setOpen(true);
        }
    }

    private static void gameReady(Player p) {
        Minigame game = p.getMiniGame();
        game.broadcast(MinigamePackets.GetMiniGameReady(game));
    }

    private static void gameUnready(Player p) {
        Minigame game = p.getMiniGame();
        game.broadcast(MinigamePackets.GetMiniGameUnReady(game));
    }

    private static void gameStart(Player p) {
        Minigame game = p.getMiniGame();
        if (game.getGameType().equals("omok")) {
            game.broadcast(MinigamePackets.GetMiniGameStart(game, game.getLoser()));
            p.getMap().broadcastMessage(MinigamePackets.AddBoxGame(game.getOwner(), 2, 1, true));
        }
        if (game.getGameType().equals("matchcard")) {
            game.shuffleList();
            game.broadcast(MinigamePackets.GetMatchCardStart(game, game.getLoser()));
            p.getMap().broadcastMessage(MinigamePackets.AddBoxGame(game.getOwner(), 2, 1, false));
        }
    }

    private static void gameGiveUp(Player p) {
        Minigame game = p.getMiniGame();
        if (game.getGameType().equals("omok")) {
            if (game.isOwner(p)) {
                game.broadcast(MinigamePackets.GetMiniGameOwnerForfeit(game));
            } else {
                game.broadcast(MinigamePackets.GetMiniGameVisitorForfeit(game));
            }
        }
        if (game.getGameType().equals("matchcard")) {
            if (game.isOwner(p)) {
                game.broadcast(MinigamePackets.GetMatchCardVisitorWin(game));
            } else {
                game.broadcast(MinigamePackets.GetMatchCardOwnerWin(game));
            }
        }
    }

    private static void gameRequestTie(Player p) {
        Minigame game = p.getMiniGame();
        if (game.isOwner(p)) {
            game.broadcastToVisitor(MinigamePackets.GetMiniGameRequestTie(game));
        } else {
            game.getOwner().getClient().write(MinigamePackets.GetMiniGameRequestTie(game));
        }
    }

    private static void gameAnswerTie(Player p, InPacket packet) {
        Minigame game = p.getMiniGame();
        int type = packet.readByte();
        switch (game.getGameType()) {
            case "omok":
                game.broadcast(MinigamePackets.GetMiniGameTie(game));
                break;
            case "matchcard":
                game.broadcast(MinigamePackets.GetMatchCardTie(game));
                break;
        }
    }

    private static void gameSkip(Player p) {
        Minigame game = p.getMiniGame();
        if (game.isOwner(p)) {
            game.broadcast(MinigamePackets.GetMiniGameSkipOwner(game));
        } else {
            game.broadcast(MinigamePackets.GetMiniGameSkipVisitor(game));
        }
    }

    private static void gameMoveOmok(Player p, InPacket packet) {
        int posX = packet.readInt();
        int posY = packet.readInt();
        int type = packet.readByte();
        p.getMiniGame().setPiece(posX, posY, type, p);
    }

    private static void gameSelectCard(Player p, InPacket packet) {
        int turn = packet.readByte();
        int slot = packet.readByte();
        Minigame game = p.getMiniGame();
        int firstslot = game.getFirstSlot();
        if (turn == 1) {
            game.setFirstSlot(slot);
            if (game.isOwner(p)) {
                game.broadcastToVisitor(MinigamePackets.GetMatchCardSelect(game, turn, slot, firstslot, turn));
            } else {
                game.getOwner().getClient().write(MinigamePackets.GetMatchCardSelect(game, turn, slot, firstslot, turn));
            }
        } else if ((game.getCardId(firstslot + 1)) == (game.getCardId(slot + 1))) {
            if (game.isOwner(p)) {
                game.broadcast(MinigamePackets.GetMatchCardSelect(game, turn, slot, firstslot, 2));
                game.setOwnerPoints();
            } else {
                game.broadcast(MinigamePackets.GetMatchCardSelect(game, turn, slot, firstslot, 3));
                game.setVisitorPoints();
            }
        } else if (game.isOwner(p)) {
            game.broadcast(MinigamePackets.GetMatchCardSelect(game, turn, slot, firstslot, 0));
        } else {
            game.broadcast(MinigamePackets.GetMatchCardSelect(game, turn, slot, firstslot, 1));
        }
    }

    private static void setMeso(Player p, InPacket packet) {
        final Trade trade = p.getTrade();
        if (trade != null) {
            p.getTrade().setMeso(packet.readInt());
        }
    }

    private static void setItems(Player p, InPacket packet) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        InventoryType ivType = InventoryType.getByType(packet.readByte());
        Item item = p.getInventory(ivType).getItem((byte) packet.readShort());
        short quantity = packet.readShort();
        byte targetSlot = packet.readByte();

        if (quantity < 1 || quantity > item.getQuantity()) {
            p.announce(PacketCreator.ServerNotice(1, "你没有足够的数量。"));
            p.announce(PacketCreator.EnableActions());
            return;
        }
        if (p.getTrade() != null) {
            if ((quantity <= item.getQuantity() && quantity >= 0) || ItemConstants.isRechargeable(item.getItemId())) {
                if (ii.isDropRestricted(item.getItemId())) {
                    p.announce(PacketCreator.ServerNotice(1, "该物品无法进行操作。"));
                    p.announce(PacketCreator.EnableActions());
                    return;
                }
                Item tradeItem = item.copy();
                if (ItemConstants.isThrowingStar(item.getItemId()) || ItemConstants.isBullet(item.getItemId())) {
                    tradeItem.setQuantity(item.getQuantity());
                    InventoryManipulator.removeFromSlot(p.getClient(), ivType, item.getPosition(), item.getQuantity(), true);
                } else {
                    tradeItem.setQuantity(quantity);
                    InventoryManipulator.removeFromSlot(p.getClient(), ivType, item.getPosition(), quantity, true);
                }
                tradeItem.setPosition(targetSlot);
                p.getTrade().addItem(tradeItem);
            }
        }
    }

    private static void sendConfirm(Player p) {
        final Trade trade = p.getTrade();
        if (trade != null) {
            Trade.completeTrade(p);
        }
    }

    private static void addItem(Player p, InPacket packet) {
        if (!cantCreateInteraction(p, (byte) 0, false)) {
            return;
        }

        //42 12 01 01 00 01 00 01 00 57 04 00 00
        InventoryType type = InventoryType.getByType(packet.readByte());
        short slot = packet.readShort();
        short bundles = packet.readShort();
        Item ivItem = p.getInventory(type).getItem(slot);

        if (ivItem == null || !ItemInformationProvider.getInstance().isItemValid(ivItem.getItemId())) {
            System.out.println("[REMOVER] Item invalido: " + ivItem.getItemId());
            p.announce(PacketCreator.EnableActions());
            return;
        }

        if (ivItem.getExpiration() > 0) {
            p.dropMessage(1, "限时道具无法操作。");
            p.announce(PacketCreator.EnableActions());
            return;
        }
        if (ivItem.getItemId() >= 2070000 && ivItem.getItemId() < 2080000) {
            p.dropMessage(1, "该类型物品无法操作。");
            return;
        }

        if (p.getItemQuantity(p.getInventory(type).getItem(slot).getItemId(), false) < bundles) {
            p.announce(PacketCreator.EnableActions());
            return;
        }

        short perBundle = packet.readShort();
        if (ItemConstants.isRechargeable(ivItem.getItemId())) {
            perBundle = 1;
            bundles = 1;
        } else if (p.getItemQuantity(ivItem.getItemId(), false) < perBundle * bundles) {
            p.announce(PacketCreator.ServerNotice(1, "Could not perform shop operation with that item."));
            p.announce(PacketCreator.EnableActions());
            return;
        }

        if (!p.haveItem(p.getInventory(type).getItem(slot).getItemId(), (perBundle * bundles), false, true)) {
            p.announce(PacketCreator.EnableActions());
            return;
        }

        int price = packet.readInt();
        if (perBundle <= 0 || perBundle * bundles > 2000 || bundles <= 0 || price <= 0 || price > Integer.MAX_VALUE) {
            AutobanManager.getInstance().autoban(p.getClient(), p.getName() + " 试图与雇佣的商人打包编辑。");
            FileLogger.printError(FileLogger.EXPLOITS + p.getName() + ".txt", p.getName() + " 可能是包编辑雇佣的商人: " + perBundle + "\nperBundle * 束（此倍数不能大于2000): " + perBundle * bundles + "\nbundles: " + bundles + "\nprice: " + price);
            return;
        }

        Item sellItem = ivItem.copy();
        if (!ItemConstants.isRechargeable(ivItem.getItemId())) {
            sellItem.setQuantity(perBundle);
        }

        PlayerShopItem item = new PlayerShopItem(sellItem, bundles, price);
        PlayerShop shop = p.getPlayerShop();

        if (shop != null && shop.isOwner(p)) {
            if (shop.isOpen()) {
                p.announce(PacketCreator.ServerNotice(1, "You can't sell it anymore."));
                return;
            }
            shop.addItem(item);
            p.announce(PersonalShopPackets.ShopItemUpdate(shop));
        }
        if (ItemConstants.isRechargeable(ivItem.getItemId())) {
            InventoryManipulator.removeFromSlot(p.getClient(), type, slot, ivItem.getQuantity(), true);
        } else {
            InventoryManipulator.removeFromSlot(p.getClient(), type, slot, (short) (bundles * perBundle), true);
        }
    }

    private static void buyItem(Player p, InPacket packet) {
        if (!cantCreateInteraction(p, (byte) 0, false)) {
            return;
        }

        int item = packet.readByte();
        short quantity = packet.readShort();
        if (quantity < 1) {
            AutobanManager.getInstance().autoban(p.getClient(), p.getName() + " tried to packet edit with a hired merchant and or player shop.");
            FileLogger.printError(FileLogger.EXPLOITS + p.getName() + ".txt", p.getName() + " tried to buy item " + item + " with quantity " + quantity + "\r\n");
            return;
        }

        PlayerShop shop = p.getPlayerShop();
        if (shop != null && shop.isVisitor(p)) {
            shop.buy(p.getClient(), item, quantity);
            shop.broadcast(PersonalShopPackets.ShopItemUpdate(shop));
        }
    }

    private static void removeItem(Player p, InPacket packet) {

        if (!cantCreateInteraction(p, (byte) 0, false)) {
            return;
        }

        PlayerShop shop = p.getPlayerShop();
        if (shop != null && shop.isOwner(p)) {
            if (shop.isOpen()) {
                p.announce(PacketCreator.ServerNotice(1, "You can't take it with the store open."));
                return;
            }
            int slot = packet.readShort();
            if (slot >= shop.getItems().size() || slot < 0) {
                AutobanManager.getInstance().autoban(p.getClient(), p.getName() + " tried to packet edit with a player shop.");
                FileLogger.printError(FileLogger.EXPLOITS + p.getName() + ".txt", p.getName() + " tried to remove item at slot " + slot + "\r\n");
                return;
            }

            shop.takeItemBack(slot, p);
        }
    }

    private static void banPlayer(Player p, InPacket packet) {
        packet.readByte();
        if (p.getPlayerShop() != null && p.getPlayerShop().isOwner(p)) {
            p.getPlayerShop().banPlayer(packet.readMapleAsciiString());
        }
    }

    private static void expelMember(Player p) {
        Minigame game = p.getMiniGame();
        if (game.isOwner(p)) {
            Player visitor = game.getVisitor();
            if (visitor != null) {
                visitor.setMiniGame(null);
                game.removeVisitor(visitor);
                visitor.dropMessage(1, "You were kicked out of the game..");
            }
        }
    }

    public static void RockPaperScissors(InPacket slea, Client c) {
        if (slea.available() == 0 || !c.getPlayer().getMap().containsNPC(9000019)) {
            if (c.getPlayer().getRPS() != null) {
                c.getPlayer().getRPS().dispose(c);
            }
            return;
        }
        final byte mode = slea.readByte();
        switch (mode) {
            case START_RPS:
            case RETRY_RPS:
                if (c.getPlayer().getRPS() != null) {
                    c.getPlayer().getRPS().reward(c);
                }
                if (c.getPlayer().getMeso() >= GameConstants.MIN_MESO_RPS) {
                    c.getPlayer().setRPS(new RockPaperScissors(c, mode));
                } else {
                    c.write(PacketCreator.GetRockPaperScissorsMode((byte) 0x06, -1, -1, -1));
                }
                break;
            case ANSWER_RPS:
                if (c.getPlayer().getRPS() == null || !c.getPlayer().getRPS().answer(c, slea.readByte())) {
                    c.write(PacketCreator.GetRockPaperScissorsMode((byte) 0x0D, -1, -1, -1));
                }
                break;
            case TIME_OVER_RPS:
                if (c.getPlayer().getRPS() == null || !c.getPlayer().getRPS().timeOut(c)) {
                    c.write(PacketCreator.GetRockPaperScissorsMode((byte) 0x0D, -1, -1, -1));
                }
                break;
            case CONTINUE_RPS:
                if (c.getPlayer().getRPS() == null || !c.getPlayer().getRPS().nextRound(c)) {
                    c.write(PacketCreator.GetRockPaperScissorsMode((byte) 0x0D, -1, -1, -1));
                }
                break;
            case LEAVE_RPS:
                if (c.getPlayer().getRPS() != null) {
                    c.getPlayer().getRPS().dispose(c);
                } else {
                    c.write(PacketCreator.GetRockPaperScissorsMode((byte) 0x0D, -1, -1, -1));
                }
                break;
        }
    }
}
