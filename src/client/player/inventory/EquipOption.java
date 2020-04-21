package client.player.inventory;

import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author wl
 */
public class EquipOption {

    private byte socket;
    private final Equip equip;
    private final IdentityHashMap<String, String> options;

    public EquipOption(Equip equip) {
        this.equip = equip;
        this.options = new IdentityHashMap<>();
    }

    public Map<String, String> getOptions() {
        return options;
    }

    //设置附魔值
    public boolean setOption(String key, String value) {
        if (key == null || "".equals(key) || key.isEmpty() ||"0".equals(key)
                || value == null || "".equals(value) || value.isEmpty() || isFull()) {
            return false;
        }
        getOptions().put(key, value);
        return true;
    }

    //获取单一附魔 1:77 Key = 1 返回 77
    public String getOptionByKey(String key) {
        return getOptions().getOrDefault(key, "");
    }

    //重置所有附魔
    public void resetAllOptions() {
        getOptions().clear();
        equip.setOptionValues("");
    }
    
    public Equip getEquip() {
        return equip;
    }
    
    public byte getSocket() {
        return socket;
    }
    
    public void setSocket(byte s) {
        this.socket = s;
    }
   
    
    //刷新装备潜能数据
    public void rebuildEquipOptions() {
        if (equip.getOptionValues() == null || equip.getOptionValues().isEmpty() || "".equals(equip.getOptionValues())) {
            return;
        }
        getOptions().clear();
        String[] values = equip.getOptionValues().split(",");
        for (String v : values) {
            String[] val = v.split(":");
            if (val.length > 0) {
                setOption(val[0], val[1]);
            }
        }
    }
    
    /*
    获取所有附魔
     */
    public String getEquipOptions() {
        String ret = "";
        //1:77,29:1,1:54,
        for (Map.Entry<String, String> kvp : getOptions().entrySet()) {
            if (!"".equals(kvp.getValue())) {
                ret += String.format("%s:%s,", kvp.getKey(), kvp.getValue());
            } 
        }
        if (ret.length() > 0) {
            ret = ret.substring(0, ret.length() - 1);
        }
        return ret;
    }
    
    //检查插槽是否已上满;
    public boolean isFull() {
        return getOptions().size() >= getSocket();
    }
    
    public int getRemainingSockets() {
        return isFull() ? 0 : getSocket() - getOptions().size();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Map<String, String> vals = (Map<String, String>) obj;
        if (vals.size() != options.size()) {
            return false;
        }
        return options.entrySet().stream().filter(value
                -> vals.entrySet().stream().anyMatch(value1
                        -> (value1.getKey().equals(value.getKey())
                && value1.getValue().equals(value.getValue())))).findAny().isPresent();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.options);
        return hash;
    }
}
