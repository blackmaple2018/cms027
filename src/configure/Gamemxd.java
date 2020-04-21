package configure;

import java.util.LinkedHashMap;
import java.util.Map;

public class Gamemxd {

    public static int 游戏账号注册上限 = 1;
    public static String 弹窗标题 = "<自由冒险岛Ver.027>";
    public static String 合伙群 = "815717096";
    public static int 点NPC间隔 = 1000;
    public static int 过图间隔 = 1000;
    public static int 丢金币间隔 = 300;
    public static int 物品操作间隔 = 500;
    public static Map<Integer, String> 伤害记录 = new LinkedHashMap<>();
    public static Map<Integer, Integer> 蓝蜗牛伤害排行 = new LinkedHashMap<>();
    public static Map<Integer, Integer> 蘑菇仔伤害排行 = new LinkedHashMap<>();

    public static java.text.DecimalFormat df1 = new java.text.DecimalFormat("#.#");
    public static java.text.DecimalFormat df2 = new java.text.DecimalFormat("#.##");
    public static java.text.DecimalFormat df3 = new java.text.DecimalFormat("#.###");
    public static java.text.DecimalFormat df4 = new java.text.DecimalFormat("#.####");

    public static int[] 附魔技能 = {

        1001004,
        1001005,
        2001004,
        2001005,
        3001004,
        3001005,
        4001334,
        4001344,
  
        1100000,
        1100001,
        1101006,
        1101007,
        1200000,
        1200001,
        1201007,
        1300000,
        1300001,
        1301007,
        2101004,
        2201005,
        2301004,
        3100000,
        3200000,
        4100000,
        4200000,

        1111003,
        1111004,
        1111005,
        1111006,
        1111008
    };

    public static int 攻击数量检测(int a, int b) {
        int 数量 = 1;
        switch (a) {
            //群体攻击
            case 1001005:
                if (b >= 22 && b < 26) {
                    数量 = 9;
                } else if (b >= 26 && b < 29) {
                    数量 = 12;
                } else if (b >= 30) {
                    数量 = 15;
                } else {
                    数量 = 6;
                }
                break;
            //虎咆哮
            case 1111008:
                if (b > 30) {
                    数量 = b - 30 + 6;
                } else {
                    数量 = 6;
                }
                break;
            //落雷
            case 2201005:
                if (b > 31) {
                    数量 = b - 31 + 6;
                } else {
                    数量 = 6;
                }
                break;
            case 1311001:
            case 1311002:
                数量 = 3;
                break;
            case 1111005:
            case 1111006:
            case 1211002:
            case 1211003:
            case 1211004:
            case 1211005:
            case 1211006:
            case 1211007:
            case 1211008:
            case 1211009:
            case 1311003:
            case 1311004:
            case 2111002:
            case 2211002:
            case 2311004:
            case 3101003:
            case 3101005:
            case 3111003:
            case 3111004:
            case 3201003:
            case 3201005:
            case 3211003:
            case 3211004:
            case 4111005:
            case 4211006:
            case 4211004:
            case 5001005:
            case 2301002:

                数量 = 6;
                break;
            case 1311006:
            case 5001006:
                数量 = 15;
                break;
            default:
                数量 = 1;
                break;
        }
        return 数量;
    }

    public static int 攻击段数检测(int a, int b) {
        int 段数 = 1;
        switch (a) {
            //魔法双击
            case 2001005:
                if (b >= 22 && b < 26) {
                    段数 = 3;
                } else if (b >= 26 && b < 30) {
                    段数 = 4;
                } else if (b >= 30) {
                    段数 = 5;
                } else {
                    段数 = 2;
                }
                break;
            //二连射
            case 3001005:
                if (b >= 22 && b < 26) {
                    段数 = 3;
                } else if (b >= 26 && b < 30) {
                    段数 = 4;
                } else if (b >= 30) {
                    段数 = 5;
                } else {
                    段数 = 2;
                }
                break;
            //二连击     
            case 4001334:
                if (b >= 22 && b < 26) {
                    段数 = 3;
                } else if (b >= 26 && b < 30) {
                    段数 = 4;
                } else if (b >= 30) {
                    段数 = 5;
                } else {
                    段数 = 2;
                }
                break;
            //双飞斩
            case 4001344:
                段数 = 2;
                break;
            //回旋斩     
            case 4201005:
                if (b >= 1 && b < 11) {
                    段数 = 2;
                } else if (b >= 11 && b < 21) {
                    段数 = 4;
                } else if (b >= 21) {
                    段数 = 6;
                } else {
                    段数 = 2;
                }
                break;
            //抢矛连
            case 1311001:
            case 1311002:
                if (b >= 1 && b < 16) {
                    段数 = 2;
                } else {
                    段数 = 3;
                }
                break;
            //烈焰箭
            case 2101004:
                if (b >= 31 && b < 35) {
                    段数 = 2;
                } else if (b >= 35 && b < 40) {
                    段数 = 3;
                } else if (b >= 40) {
                    段数 = 4;
                } else {
                    段数 = 1;
                }
                break;
            //箭扫射
            case 3111006:
            case 3211006:
                段数 = 4;
                break;
            default:
                段数 = 1;
                break;
        }

        return 段数;
    }

    /**
     * <20 - 30>
     */
    public static boolean 一转技能附魔(int sk) {
        switch (sk) {
            //战士
            case 1001004://强力攻击
            case 1001005://群体攻击
            //魔法师
            case 2001004://魔法弹
            case 2001005://魔法双击
            //弓箭手
            case 3001004://断魂箭
            case 3001005://二连射
            //飞侠
            case 4001334://二连击
            case 4001344://双飞斩
                return true;
        }
        return false;
    }

    /**
     * <20 - 30>
     */
    public static boolean 二转技能附魔20(int sk) {
        switch (sk) {
            //剑客
            case 1100000://精准剑
            case 1100001://精准斧
            case 1101006://愤怒之火
            //准骑士
            case 1200000://精准剑
            case 1200001://精准钝器
            //抢战士
            case 1300000://精准枪
            case 1300001://精准矛
            //牧师
            case 2301004://祝福
            //猎人   
            case 3100000://精准弓
            //弩弓手
            case 3200000://精准弩
            //刺客
            case 4100000://精准暗器
            //侠客
            case 4200000://精准短刀
                return true;
        }
        return false;
    }

    /**
     * <30 - 40>
     */
    public static boolean 二转技能附魔30(int sk) {
        switch (sk) {
            //剑客
            case 1101007://伤害反击
            //准骑士
            case 1201007://伤害反击
            //抢战士
            case 1301007://神圣之火
            //火毒2    
            case 2101004://火焰箭
            //雷电术
            case 2201005://雷电术
                return true;
        }
        return false;
    }

    /**
     * <30 - 40>
     */
    public static boolean 三转技能附魔30(int sk) {
        switch (sk) {
            //勇士
            case 1111003://黑暗之剑
            case 1111004://黑暗之斧
            case 1111005://气绝剑
            case 1111006://气绝斧
            case 1111008://虎咆哮
            //龙骑
            case 1311001://枪连击
            case 1311002://矛连击
            case 1311003://无双枪
            case 1311004://无双矛
                return true;
        }
        return false;
    }

    public static boolean 恢复药水(int sk) {
        switch (sk) {
            //红色药水
            case 2000000:
            //橙色药水
            case 2000001:
            //白色药水
            case 2000002:
            //蓝色药水
            case 2000003:
            //特殊药水
            case 2000004:
            //超级药水
            case 2000005:
            //活力神水
            case 2000006:
            //西瓜
            case 2001000:
            //棒棒冰
            case 2001001:
            //红豆刨冰
            case 2001002:
            //苹果
            case 2010000:
            //烤肉
            case 2010001:
            //鸡蛋
            case 2010002:
            //橙子
            case 2010003:
            //柠檬
            case 2010004:
            //蜂蜜
            case 2010005:
            //蜂蜜罐
            case 2010006:
            //沙拉
            case 2020000:
            case 2020001:
            case 2020002:
            case 2020003:
            case 2020004:
            case 2020005:
            case 2020006:
            case 2020007:
            case 2020008:
            case 2020009:
            case 2020010:
            case 2020011:
            case 2020012:
            case 2020013:
            case 2020014:
            case 2020015:
            case 2022000:
            case 2022003:
                return true;
        }
        return false;
    }

    public static boolean 金银岛主城(int sk) {
        switch (sk) {
            //射手村
            case 100000000:
            //魔法密林
            case 101000000:
            //勇士部落
            case 102000000:
            //废弃都市
            case 103000000:
            //明珠港
            case 104000000:
                return true;
        }
        return false;
    }

    public static boolean 终极技能(int sk) {
        switch (sk) {
            case 1100002:
            case 1100003:
            case 1200002:
            case 1200003:
            case 1300002:
            case 1300003:
            case 3100001:
            case 3200001:
                return true;
        }
        return false;
    }

    public static boolean 副本(int sk) {
        switch (sk) {
            case 103000800:
            case 103000801:
            case 103000802:
            case 103000803:
            case 103000804:
            case 103000805:
                return true;
        }
        return false;
    }

    public static boolean 宠物(int mobId) {
        switch (mobId) {
            case 5000000:
            case 5000001:
            case 5000002:
            case 5000003:
            case 5000004:
            case 5000005:
            case 5000006:
            case 5000007:
            case 5000008:
                return true;
        }
        return false;
    }

    public static boolean 比例(int a, int b) {
        double 随机 = Math.ceil(Math.random() * b);
        if (随机 <= a) {
            return true;
        }
        return false;
    }

    public static double 经验效率(int 角色等级, int 怪物等级) {
        if (角色等级 > 怪物等级) {
            if (角色等级 - 怪物等级 >= 30) {
                return 0.4;
            } else if (角色等级 - 怪物等级 >= 25) {
                return 0.5;
            } else if (角色等级 - 怪物等级 >= 20) {
                return 0.6;
            } else if (角色等级 - 怪物等级 >= 15) {
                return 0.7;
            } else if (角色等级 - 怪物等级 >= 10) {
                return 0.8;
            }
        }
        return 1;
    }

    public static int 会员等级划分(int exp) {
        if (exp >= 160) {
            return 6;
        } else if (exp >= 100) {
            return 5;
        } else if (exp >= 70) {
            return 4;
        } else if (exp >= 40) {
            return 5;
        } else if (exp >= 10) {
            return 2;
        }
        return 1;
    }

    public static int 群头衔经验加成(String exp) {
        switch (exp) {
            case "Lv.4":
            case "Lv.5":
            case "Lv.6":
                return 4;
        }
        return 0;
    }

    public static int 家族等级划分(int exp) {
        if (exp >= 10000) {
            return 5;
        } else if (exp >= 5000) {
            return 4;
        } else if (exp >= 1500) {
            return 3;
        } else if (exp >= 500) {
            return 2;
        } else if (exp >= 0) {
            return 1;
        }
        return 1;
    }

    public static String 装备类型(int id) {
        int prefix = id / 10000;
        if (prefix == 100) {
            return "帽子";
        } else if (prefix == 101) {
            return "脸饰";
        } else if (prefix == 102) {
            return "眼饰";
        } else if (prefix == 103) {
            return "耳环";
        } else if (prefix == 104) {
            return "上衣";
        } else if (prefix == 105) {
            return "群袍";
        } else if (prefix == 106) {
            return "裤子";
        } else if (prefix == 107) {
            return "鞋子";
        } else if (prefix == 108) {
            return "手套";
        } else if (prefix == 109) {
            return "盾牌";
        } else if (prefix == 110) {
            return "披风";
        } else if (prefix == 111) {
            return "戒指";
        } else if (prefix == 112) {
            return "吊坠";
        }
        return "未知";
    }

    public static String 地图类型(int a) {
        switch (a) {
            case 100000000:
            case 101000000:
            case 102000000:
            case 103000000:
            case 104000000:
                return "主城";

        }

        return "野外";
    }
}
