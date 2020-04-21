package packet.creators;

import client.player.Player;
import client.player.PlayerEffects;
import packet.opcode.OutHeader;
import packet.transfer.write.OutPacket;
import packet.transfer.write.OutPacket;
import tools.HexTool;

public class EffectPackets {
    
    public static OutPacket PlayPortalSound() {
        return ShowSpecialEffect(PlayerEffects.PLAY_PORTAL_SE.getEffect());
    }

    public static OutPacket ShowMonsterBookPickup() {
        return ShowSpecialEffect(PlayerEffects.MONSTERBOOK_CARD_GET.getEffect());
    }

    public static OutPacket ShowEquipmentLevelUp() {
        return ShowSpecialEffect(PlayerEffects.ITEM_LEVEL_UP.getEffect());
    }

    public static OutPacket ShowItemLevelup() {
        return ShowSpecialEffect(PlayerEffects.ITEM_LEVEL_UP.getEffect());
    }
    
    public static OutPacket ShowSelfQuestComplete() {
        return ShowSpecialEffect(PlayerEffects.QUEST_COMPLETE.getEffect());
    }
    
    public static OutPacket MusicChange(String song) {
	return EnvironmentChange(song, 6);
    }
    public static OutPacket ShowEffect(String effect) {
        return EnvironmentChange(effect, 3);
    }

    public static OutPacket PlaySound(String sound) {
        return EnvironmentChange(sound, 4);
    }
    
    public static OutPacket EnvironmentChange(String env, int mode) {
        OutPacket wp = new OutPacket(OutHeader.FIELD_EFFECT);
        wp.write(mode);
        wp.writeMapleAsciiString(env);
        return wp;
    }
    
    public static OutPacket StartMapEffect(String msg, int itemid, boolean active) {
        OutPacket wp = new OutPacket(OutHeader.BLOW_WEATHER);
        wp.writeInt(itemid);
        wp.writeMapleAsciiString(msg);
        return wp;
    }

    public static OutPacket RemoveMapEffect() {
        OutPacket wp = new OutPacket(OutHeader.BLOW_WEATHER);
        wp.writeInt(0);
        return wp;
    }
    
    public static OutPacket ShowEquipEffect() {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.SHOW_EQUIP_EFFECT.getValue());
        return wp;
    }
    
    public static OutPacket ShowOwnBuffEffect(int skillID, int effectID) {
        OutPacket wp = new OutPacket(OutHeader.FIRST_PERSON_VISUAL_EFFECT);
        wp.write(effectID);
        wp.writeInt(skillID);
        wp.writeBool(true);
        return wp;
    }
    
    public static OutPacket BuffMapVisualEffect(int cID, int skillID, int effectID) {
        return BuffMapVisualEffect(cID, skillID, effectID, (byte) 3);
    }

    public static OutPacket BuffMapVisualEffect(int cID, int skillID, int effectID, byte direction) {
        OutPacket wp = new OutPacket(OutHeader.THIRD_PERSON_VISUAL_EFFECT);
        wp.writeInt(cID); 
        wp.write(effectID);
        wp.writeInt(skillID);
        wp.writeBool(true);
        if (direction != (byte) -3) {
            wp.write(direction);
        }
        return wp;
    }
    
    public static OutPacket ExpressionChange(Player from, int expression) {
        OutPacket wp = new OutPacket(OutHeader.FACIAL_EXPRESSION);
        wp.writeInt(from.getId());
        wp.writeInt(expression);
        return wp;
    }
    
    public static OutPacket ItemEffect(int charID ,int itemID ){
        OutPacket wp = new OutPacket(OutHeader.SHOW_ITEM_EFFECT);
        wp.writeInt(charID);
        wp.writeInt(itemID);
        return wp;
    }
    
    public static OutPacket ShowChair(int charID, int itemID) {
        OutPacket wp = new OutPacket(OutHeader.ITEM_CHAIR);
        wp.writeInt(charID);
        wp.writeInt(itemID);
        return wp;
    }

    public static OutPacket SitOnChair(short itemid) {
        OutPacket wp = new OutPacket(OutHeader.CHAIR);
        wp.writeBool((itemid != -1));
        if (itemid != -1) {
            wp.writeShort(itemid);
        }
        return wp;
    }
    
    public static OutPacket RiseFromChair() {
        OutPacket wp = new OutPacket(OutHeader.CHAIR);
        wp.writeBool(false);
        return wp;
    }
    
    public static OutPacket SelfCharmEffect(short charmsLeft, short daysLeft) {
        OutPacket wp = new OutPacket(OutHeader.FIRST_PERSON_VISUAL_EFFECT);
        wp.write(PlayerEffects.SAFETY_CHARM.getEffect());
        wp.writeBool(true);
        wp.write(charmsLeft);
        wp.write(daysLeft);
        return wp;
    }
    
    public static OutPacket SkillEffect(Player from, int skillID, int level, byte flags, int speed) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.SKILL_EFFECT.getValue());
        wp.writeInt(from.getId());
        wp.writeInt(skillID);
        wp.write(level);
        wp.write(flags);
        wp.write(speed);
        return wp;
    }
    
    /**
     * 6 = Exp did not drop (Safety Charms)
     * 7 = Enter portal sound
     * 8 = Job change
     * 9 = Quest complete
     * 10 = damage O.O
     * 14 = Monster book pickup
     * 15 = Equipment levelup
     * 16 = Maker Skill Success
     * 19 = Exp card [500, 200, 50]
     * @param effect
     * @return 
     */
    public static OutPacket ShowSpecialEffect(int effect) {
        OutPacket wp = new OutPacket(OutHeader.FIRST_PERSON_VISUAL_EFFECT);
        wp.write(effect);
        return wp;
    }
    
    public static OutPacket ShowThirdPersonEffect(int cID, int effect) {
        OutPacket wp = new OutPacket(OutHeader.THIRD_PERSON_VISUAL_EFFECT);
        wp.writeInt(cID);
        wp.write(effect);
        return wp;
    }
    
    public static OutPacket ShowDashEffecttoOthers(int Cid, int x, int y, int duration) {
        OutPacket wp = new OutPacket();
        wp.write(OutHeader.THIRD_PERSON_APPLY_STATUS_EFFECT.getValue());
        wp.writeInt(Cid);
        wp.writeLong(0);
        wp.writeBytes(HexTool.getByteArrayFromHexString("00 00 00 30 00 00 00 00"));
        wp.writeShort(0);
        wp.writeInt(x);
        wp.writeInt(5001005);
        wp.writeBytes(HexTool.getByteArrayFromHexString("1A 7C 8D 35"));
        wp.writeShort(duration);
        wp.writeInt(y);
        wp.writeInt(5001005);
        wp.writeBytes(HexTool.getByteArrayFromHexString("1A 7C 8D 35"));
        wp.writeShort(duration);
        wp.writeShort(0);
        return wp;
    }	
}
