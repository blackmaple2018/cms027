/**
 * Ellin Baseado em um servidor GMS-Like na v.62
 */
package server.itens;

/**
 * @brief SlotInformation
 * @author BlackRabbit (http://forum.ragezone.com/members/2000112471.html)
 */
public enum SlotInformation {

    //帽子

    CAP(100, -1),
    //脸饰
    FACE_ACCESSORY(101, -2),
    //眼饰
    EYE_ACCESSORY(102, -3),
    //耳环
    EARRINGS(103, -4),
    //上衣
    TOP(104, -5),
    //袍
    OVERCOAT(105, -5),
    //裤子
    PANTS(106, -6),
    //鞋子
    SHOES(107, -7),
    //手套
    GLOVES(108, -8),
    //盾牌
    SHIELD(109, -10),
    //披风
    CAPE(110, -9),
    //戒指
    RING(111, -12, -13, -15, -16),
    //吊坠
    PENDANT(112, -17),
    //单手剑
    ONE_HANDED_SWORD(130, -11),
    //单手斧头
    ONE_HANDED_AXE(131, -11),
    //单手钝器
    ONE_HANDED_BLUNT_WEAPON(132, -11),
    //匕首
    DAGGER(133, -11),
    //魔杖
    WAND(137, -11),
    STAFF(138, -11),
    //拳头
    FISTS(139, -11),
    //双手剑
    TWO_HANDED_SWORD(140, -11),
    //双手斧
    TWO_HANDED_AXE(141, -11),
    //双手钝器
    TWO_HANDED_BLUNT_WEAPON(142, -11),
    //矛
    SPEAR(143, -11),
    //长枪
    POLEARM(144, -11),
    //弓
    BOW(145, -11),
    //弩
    CROSSBOW(146, -11),
    //爪
    CLAW(147, -11),
    //指关节
    KNUCKLER(148, -11),
    //枪
    GUN(149, -11),
    TAMING_MOB(190, -18),
    //马鞍
    SADDLE(191, -19),
    SPECIAL_TAMING_MOB(193, -18),
    CASH_ITEM;

    private int prefix;
    private int[] allowed;

    private SlotInformation() {
        prefix = 0;
    }

    private SlotInformation(int pre, int... in) {
        prefix = pre;
        allowed = in;
    }

    public int getPrefix() {
        return prefix;
    }

    public boolean isTwoHanded() {
        return prefix >= 139 && prefix <= 149;
    }

    public boolean isAllowed(int slot, boolean cash) {
        if (allowed != null) {
            for (Integer allow : allowed) {
                int condition = cash ? allow - 100 : allow;
                if (slot == condition) {
                    return true;
                }
            }
        }
        return cash;
    }

    public static SlotInformation getFromItemId(int id) {
        int prefix = id / 10000;
        if (prefix != 0) {
            for (SlotInformation c : values()) {
                if (c.getPrefix() == prefix) {
                    return c;
                }
            }
        }
        return CASH_ITEM;
    }
}
