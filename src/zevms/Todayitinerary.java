package zevms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class Todayitinerary {

    /** <数据结构
     * ，1
     * ，2
     * ，3
     * ，4
     * 在线时间，5`
     * 泡点时间，6`
     * 泡点金币收益，7`
     * 泡点经验收益，8`
     * 泡点点券收益，9`
     * 击杀怪物，10`
     * 获取经验，11`
     * ,12
     * >
     */
    /**
     * <测试数据>*
     */
    public static void main(String args[]) {
        int 分 = Calendar.getInstance().get(Calendar.MINUTE);
        System.err.println(" 数据" +(分 % 49) );
        
        /*for (int i = 1; i <= 12; i++) {
            if (i < 10) {
                System.err.println(" 数据输出0" + i + " - " + 判断行程(1, i));
            } else {
                System.err.println(" 数据输出" + i + " - " + 判断行程(1, i));
            }
        }*/
    }

    public static Map<Integer, long[]> 今日行程记录 = new HashMap<>();

    /**
     * <提取数据>*
     */
    public static long 判断行程(int a, int b) {
        b -= 1;
        if (今日行程记录.get(a) != null) {
            long[] A = 今日行程记录.get(a);
            return A[b];
        }
        return 0;
    }

    /**
     * <清除缓存>*
     */
    public static void 清除行程(int a) {
        今日行程记录.remove(a);
    }

    /**
     * <指定位置写入数据>*
     */
    public static void 记录行程(int a, int a1, long a2) {
        switch (a1) {
            case 1:
                写入(a, a2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
                break;
            case 2:
                写入(a, 0, a2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
                break;
            case 3:
                写入(a, 0, 0, a2, 0, 0, 0, 0, 0, 0, 0, 0, 0);
                break;
            case 4:
                写入(a, 0, 0, 0, a2, 0, 0, 0, 0, 0, 0, 0, 0);
                break;
            case 5:
                写入(a, 0, 0, 0, 0, a2, 0, 0, 0, 0, 0, 0, 0);
                break;
            case 6:
                写入(a, 0, 0, 0, 0, 0, a2, 0, 0, 0, 0, 0, 0);
                break;
            case 7:
                写入(a, 0, 0, 0, 0, 0, 0, a2, 0, 0, 0, 0, 0);
                break;
            case 8:
                写入(a, 0, 0, 0, 0, 0, 0, 0, a2, 0, 0, 0, 0);
                break;
            case 9:
                写入(a, 0, 0, 0, 0, 0, 0, 0, 0, a2, 0, 0, 0);
                break;
            case 10:
                写入(a, 0, 0, 0, 0, 0, 0, 0, 0, 0, a2, 0, 0);
                break;
            case 11:
                写入(a, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, a2, 0);
                break;
            case 12:
                写入(a, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, a2);
                break;
            default:
                break;
        }

    }

    /**
     * <写入数据>*
     */
    public static void 写入(int a, long a1, long a2, long a3, long a4, long a5, long a6, long a7, long a8, long a9, long a10, long a11, long a12) {
        今日行程记录.put(a, new long[]{
            a1 + 判断行程(a, 1),
            a2 + 判断行程(a, 2),
            a3 + 判断行程(a, 3),
            a4 + 判断行程(a, 4),
            a5 + 判断行程(a, 5),
            a6 + 判断行程(a, 6),
            a7 + 判断行程(a, 7),
            a8 + 判断行程(a, 8),
            a9 + 判断行程(a, 9),
            a10 + 判断行程(a, 10),
            a11 + 判断行程(a, 11),
            a12 + 判断行程(a, 12)
        });
    }

}
