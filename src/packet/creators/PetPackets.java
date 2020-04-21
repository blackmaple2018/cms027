package packet.creators;

import client.player.Player;
import client.player.PlayerStat;
import client.player.inventory.ItemPet;
import java.awt.Point;
import java.util.List;
import packet.opcode.OutHeader;
import packet.transfer.write.OutPacket;
import server.movement.LifeMovementFragment;
import tools.HexTool;

public class PetPackets {
    
    public static byte 
        
        PET_LVL_UP = 4, 
        INVENTORY_CLEAR_SLOT = 3,
        INVENTORY_STAT_UPDATE = 0;

    public static void AddPetInfo(final OutPacket wp, ItemPet pet, boolean showpet) {
        if (showpet) {
            wp.write(1);
        }
        wp.writeInt(pet.getPetItemId());
        wp.writeMapleAsciiString(pet.getName());
        wp.writeLong(pet.getUniqueId());
        wp.writePos(pet.getPosition());
        wp.write(pet.getStance());
        wp.writeShort(pet.getFoothold());
    }
    
    public static OutPacket updatePet(ItemPet pet, boolean alive) {
        OutPacket mplew = new OutPacket();

        mplew.write(OutHeader.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(0);
        mplew.write(2);
        
        mplew.write(3);
        mplew.write(5);
        mplew.writeShort(pet.getInventoryPosition());
        
        mplew.write(0);
        
        mplew.write(5);
        mplew.writeShort(pet.getInventoryPosition());
        
        mplew.writeInt(pet.getPetItemId());
        mplew.write(1);
        mplew.writeLong(pet.getUniqueId());
        mplew.writeBytes(HexTool.getByteArrayFromHexString("00 80 05 BB 46 E6 17 02"));

        mplew.writeAsciiString(pet.getName(), 19);
        mplew.write(pet.getLevel());
        mplew.writeShort(pet.getCloseness());
        mplew.write(pet.getFullness());
        if (alive) {
            PacketCreator.AddExpirationTime(mplew, pet.getExpiration() <= System.currentTimeMillis() ? -1 : pet.getExpiration());
        } else {
            mplew.writeBytes(HexTool.getByteArrayFromHexString("00 80 05 BB 46 E6 17 02"));
        }
        return mplew;
    }
    
    public static OutPacket ShowPet(Player chr, ItemPet pet, boolean remove) {
        return ShowPet(chr, pet, remove, false);
    }

    public static OutPacket ShowPet(Player p, ItemPet pet, boolean remove, boolean hunger) {
        OutPacket wp = new OutPacket(OutHeader.SPAWN_PET);
        wp.writeInt(p.getId());
        if (remove) {
            wp.write(0);
            wp.writeBool(hunger);
        } else {
           AddPetInfo(wp, pet, true);
        }
        return wp;
    }
    
    public static OutPacket RemovePet(int owner, byte slot, byte message) {
        OutPacket wp = new OutPacket(OutHeader.SPAWN_PET);
        wp.writeInt(owner);
        wp.writeBool(false);
        wp.write(message);
        return wp;
    }
    
    public static OutPacket MovePet(int cid, List<LifeMovementFragment> moves, Point pos) {
        OutPacket wp = new OutPacket(OutHeader.MOVE_PET);
        wp.writeInt(cid);
        wp.writePos(pos);
        HelpPackets.SerializeMovementList(wp, moves);
        wp.writeInt(0);
        return wp;
    }
    
    public static OutPacket PetChat(int cid, byte nType, byte nAction, String text, boolean hasRing) {
        OutPacket wp = new OutPacket(OutHeader.PET_CHAT);
        wp.writeInt(cid);
        wp.write(nType);
        wp.write(nAction);
        wp.writeMapleAsciiString(text);
        wp.writeBool(hasRing);
        return wp;
    }
    
    public static OutPacket CommandPetResponse(int cid, byte command, int slot, boolean success, boolean food) {
        OutPacket wp = new OutPacket(OutHeader.PET_RESPONSE);
        wp.writeInt(cid);
        wp.writeBool(food);
        wp.write(command);
        wp.writeBool(success);
        wp.write(0);
        wp.writeInt(0);
        wp.writeInt(0);
        return wp;
    }
    
    public static OutPacket ShowOwnPetLevelUp(int petSlot) { 
        OutPacket wp = new OutPacket(OutHeader.FIRST_PERSON_VISUAL_EFFECT);
        wp.write(0x4);
        wp.write(0);
        wp.write(petSlot);
        return wp;
    }
    
    public static OutPacket ShowPetLevelUp(Player p, int petSlot) {
        OutPacket wp = new OutPacket(OutHeader.THIRD_PERSON_VISUAL_EFFECT);
        wp.writeInt(p.getId());
        wp.write(0x4);
        wp.write(0);
        wp.write(petSlot);
        return wp;
    }
    
    public static OutPacket ChangePetName(Player p, String newname) {
        OutPacket wp = new OutPacket(OutHeader.PET_NAMECHANGE);
        wp.writeInt(p.getId());
        wp.writeMapleAsciiString(newname);
        return wp;
    }
    
    public static OutPacket PetStatUpdate(Player p) {
        OutPacket wp = new OutPacket(OutHeader.UPDATE_STATS);
        wp.write(0);
        wp.writeInt(PlayerStat.PET.getValue());
        byte count = 0;
        for (final ItemPet pet : p.getPets()) {
            if (pet.getSummoned()) {
                wp.writeLong(pet.getUniqueId());
                count++;
            }
        }
        while (count < 3) {
            wp.writeZeroBytes(8);
            count++;
        }
        wp.write(0);
        return wp;
    }
    
    public static OutPacket AutoHpPot(int itemId) {
        OutPacket wp = new OutPacket(6);
        wp.write(OutHeader.PET_AUTO_HP_POT.getValue());
        wp.writeInt(itemId);
        return wp;
    }

    public static OutPacket AutoMpPot(int itemId) {
        OutPacket wp = new OutPacket(6);
        wp.write(OutHeader.PET_AUTO_MP_POT.getValue());
        wp.writeInt(itemId);
        return wp;
    }
    
    public static final OutPacket EmptyStatUpdate() {
        return PacketCreator.EnableActions();
    } 

    public static OutPacket PetExceptionListResult(Player p, ItemPet pet) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.PET_ITEM_IGNORE.getValue());
        wp.writeInt(p.getId());
        wp.write(p.getPetIndex(pet));
        wp.writeLong(pet.getUniqueId());
        wp.write(pet.getExceptionList().size());
        for (int itemId : pet.getExceptionList()) {
            wp.writeInt(itemId);
        }
        return wp;
    }
}
