package gui;

import java.util.HashMap;
import java.util.Map;

public class copy {

    //队伍编号，频道
    public static Map<Integer, Integer> 废弃副本开始频道 = new HashMap<>();
    //队伍编号，开始的时间
    public static Map<Integer, Long> 废弃副本开始时间 = new HashMap<>();

    public static Map<Integer, Integer[]> 废弃副本成员 = new HashMap<>();

    /**
     * *
     * a 队伍编号
     * b 进行时间
     * c 开始频道
     * a1 - a6 队员
     */
    public static void 记录废弃副本成员(int a, int a1, int a2, int a3, int a4, int a5, int a6) {
        废弃副本成员.remove(a);
        废弃副本成员.put(a, new Integer[]{a1, a2, a3, a4, a5, a6});
    }

    public static Integer[] 判断队伍编号(int a) {
        return 废弃副本成员.getOrDefault(a, new Integer[1]);
    }
}