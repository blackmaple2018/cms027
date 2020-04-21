package handling.channel.handler;

import constants.ExperienceConstants;
import client.player.Player;
import client.Client;
import client.player.inventory.Inventory;
import client.player.inventory.types.InventoryType;
import client.player.inventory.Item;
import client.player.inventory.ItemPet;
import client.player.inventory.ItemPetCommand;
import client.player.inventory.ItemPetFactory;
import security.violation.AutobanManager;
import security.violation.CheatingOffense;
import constants.ItemConstants;
import static handling.channel.handler.MovementParse.parseMovement;
import java.awt.Point;
import packet.transfer.read.InPacket;
import java.util.List;
import java.util.Random;
import packet.creators.PacketCreator;
import packet.creators.PetPackets;
import server.itens.InventoryManipulator;
import server.itens.ItemInformationProvider;
import server.maps.FieldLimit;
import server.movement.LifeMovementFragment;
import tools.Randomizer;

/**
 *
 * @author GabrielSin
 */
public class PetHandler {

    public static void SpawnPet(InPacket packet, Client c) {
        byte petSlot = packet.readByte();
        c.getPlayer().spawnPet(petSlot, packet.readBool());
    }

    public static void MovePet(InPacket packet, Client c) {
        Point pos = packet.readPos();
        List<LifeMovementFragment> mov = parseMovement(packet);
        Player p = c.getPlayer();
        
        if (mov != null && p != null && !mov.isEmpty()) {
            ItemPet pet = p.getPet(0);
            if (pet == null) {
                return;
            }
            pet.updatePosition(mov);
            p.getMap().broadcastMessage(p, PetPackets.MovePet(p.getId(), mov, pos), false);
        }
    }

    public static void PetChat(InPacket packet, Client c) {
        Player p = c.getPlayer();
        if (p == null || p.getMap() == null) {
            return;
        }
        byte type = packet.readByte();
        byte action = packet.readByte();
        String message = packet.readMapleAsciiString();
        if (message.length() > Byte.MAX_VALUE) {
            AutobanManager.getInstance().autoban(p.getClient(), p.getName() + " tried to packet edit with pets.");
            return;
        }
        p.getMap().broadcastMessage(p, PetPackets.PetChat(p.getId(), type, action, message, c.getPlayer().haveItem(1832000, 1, true, false)), true);
        c.write(PacketCreator.EnableActions());
    }

    public static void PetExcludeItems(InPacket packet, Client c) {
        Player p = c.getPlayer();
        int uniqueId = (int) packet.readLong();
        if (p == null) {
            return;
        }
        if (p.getPetIndex(uniqueId) == -1) {
            p.getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT, "Tried to use pet item ignore with nonexistent pet");
            return;
        }
        final ItemPet pet = p.getPetByUID(uniqueId);
        if (pet == null) {
            p.getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT, "Tried to use pet item ignore with nonexistent pet");
            return;

        }
        pet.getExceptionList().clear();
        byte amount = (byte) Math.min(10, packet.readByte());
        for (int i = 0; i < amount; i++) {
            pet.addItemException(packet.readInt());
        }
    }

    public static void PetCommand(InPacket packet, Client c) {
        ItemPet pet = c.getPlayer().getSummonedPets().get(0);
        packet.readByte();

        byte command = packet.readByte();
        final ItemPetCommand petCommand = ItemPetFactory.getPetCommand(pet.getPetItemId(), (int) command);
        if (petCommand == null) {
            c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT, "Tried to use nonexistent pet command");
            return;
        }
        boolean success = false;
        if (Randomizer.nextInt(99) <= petCommand.getProbability()) {
            success = true;
            if (pet.getCloseness() < 30000) {
                int newCloseness = pet.getCloseness() + petCommand.getIncrease();
                if (newCloseness > 30000) {
                    newCloseness = 30000;
                }
                pet.setCloseness(newCloseness);
                if (newCloseness >= ExperienceConstants.getClosenessNeededForLevel(pet.getLevel() + 1)) {
                    pet.setLevel(pet.getLevel() + 1);
                    c.write(PetPackets.ShowOwnPetLevelUp(0));
                    c.getPlayer().getMap().broadcastMessage(PetPackets.ShowPetLevelUp(c.getPlayer(), 0));
                }
                c.write(PetPackets.updatePet(pet, true));
            }
        }
        Player p = c.getPlayer();
        p.getMap().broadcastMessage(p, PetPackets.CommandPetResponse(p.getId(), command, 0, success, false), true);
        c.write(PacketCreator.EnableActions());
    }

    public static void PetFood(InPacket r, Client c) {
        Player p = c.getPlayer();
        int previousFullness = 100;
        ItemPet pet = null;
        if (p == null) {
            return;
        }
        for (final ItemPet pets : p.getPets()) {
            if (pets.getSummoned()) {
                if (pets.getFullness() < previousFullness) {
                    previousFullness = pets.getFullness();
                    pet = pets;
                }
            }
        }
        if (pet == null) {
            c.write(PacketCreator.EnableActions());
            return;
        }
        short foodSlot = r.readShort();
        int foodItemId = r.readInt();

        Item petFood = c.getPlayer().getInventory(InventoryType.USE).getItem((byte) foodSlot);
        if (petFood == null || petFood.getItemId() != foodItemId || petFood.getQuantity() <= 0 || foodItemId / 10000 != 212) {
            p.getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT, "Tried to use nonexistent pet food");
            return;
        }

        boolean gainCloseness = new Random().nextInt(101) <= 50;
        short closeness = pet.getCloseness();
        byte fullness = pet.getFullness();

        if (fullness < 100) {
            int newFullness = fullness + 30;
            if (newFullness > 100) {
                newFullness = 100;
            }

            pet.setFullness(newFullness);
            final byte index = p.getPetIndex(pet);

            if (gainCloseness && closeness < 30000) {
                int newCloseness = closeness + 1;
                if (newCloseness > 30000) {
                    newCloseness = 30000;
                }

                pet.setCloseness(newCloseness);
                if (newCloseness >= ExperienceConstants.getClosenessNeededForLevel(pet.getLevel() + 1)) {
                    pet.setLevel(pet.getLevel() + 1);

                    c.write(PetPackets.ShowOwnPetLevelUp(index));
                    p.getMap().broadcastMessage(PetPackets.ShowPetLevelUp(c.getPlayer(), index));
                }
            }
            c.write(PetPackets.updatePet(pet, true));
            p.getMap().broadcastMessage(p, PetPackets.CommandPetResponse(p.getId(), (byte) 1, index, true, true), true);
        } else {
            if (gainCloseness) {
                int newCloseness = closeness - 1;
                if (newCloseness < 0) {
                    newCloseness = 0;
                }
                pet.setCloseness(newCloseness);
                if (newCloseness < ExperienceConstants.getClosenessNeededForLevel(pet.getLevel())) {
                    pet.setLevel(pet.getLevel() - 1);
                }
            }
            c.write(PetPackets.updatePet(pet, true));
            p.getMap().broadcastMessage(p, PetPackets.CommandPetResponse(p.getId(), (byte) 1, c.getPlayer().getPetIndex(pet), false, true), true);
        }

        InventoryManipulator.removeById(c, InventoryType.USE, foodItemId, 1, true, false);
        c.write(PacketCreator.EnableActions());
    }

    public static void PetAutoPotion(InPacket r, Client c) {
        Player p = c.getPlayer();

        int uniqueId = (int) r.readLong();
        r.readByte();
        r.readInt();
        short slot = r.readShort();
        int itemId = r.readInt();

        Inventory useInv = p.getInventory(InventoryType.USE);
        if (!p.isAlive() || p.getPetIndex(uniqueId) < 0 || p.getMap() == null) {
            p.announce(PacketCreator.EnableActions());
            return;
        }
        if (p.getPetIndex(uniqueId) == -1) {
            p.getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT, "Tried to use pet auto potion with nonexistent pet");
            return;
        }

        Item toUse = c.getPlayer().getInventory(InventoryType.USE).getItem(slot);
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        if (toUse == null || toUse.getItemId() != itemId || toUse.getQuantity() < 0) {
            p.getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT, "Tried to use nonexistent potion for pet auto potion");
            return;
        }
        if (p.getAutoHpPot() != toUse.getItemId() && p.getAutoMpPot() != toUse.getItemId()) {
            p.getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT, "Tried to use wrong potion for pet auto potion");
            return;
        }
        if (p.getAutoHpPot() == toUse.getItemId() && !p.haveItemEquiped(ItemConstants.HP_ITEM) || p.getAutoMpPot() == toUse.getItemId() && !p.haveItemEquiped(ItemConstants.MP_ITEM)) {
            p.getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT, "Tried to use pet auto potion without equip");
            return;
        }
        if (!FieldLimit.CANNOTUSEPOTION.check(p.getMap().getFieldLimit())) {
            InventoryManipulator.removeFromSlot(c, InventoryType.USE, slot, (short) 1, false);
            ii.getItemEffect(toUse.getItemId()).applyTo(c.getPlayer());
        }
    }
}
