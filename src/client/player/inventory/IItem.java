package client.player.inventory;

public interface IItem extends Comparable<IItem> {

    byte getType();

    short getPosition();

    byte getFlag();

    boolean getLocked();

    short getQuantity();

    String getOwner();

    String getGMLog();

    int getItemId();

    int getUniqueId();

    IItem copy();

    long getExpiration();

    void setFlag(byte flag);

    void setLocked(byte flag);

    void setUniqueId(int id);

    void setPosition(short position);

    void setExpiration(long expire);

    void setOwner(String owner);

    void setGMLog(String GameMaster_log);

    void setQuantity(short quantity);

    void setGiftFrom(String gf);

    void setEquipLevel(byte j);

    byte getEquipLevel();

    String getGiftFrom();

    String getDaKongFuMo();

    void setDaKongFuMo(final String mxmxdDaKongFuMo);

    int getPrice();

    void setPrice(int Proice);

}
