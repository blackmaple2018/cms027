package handling.login.handler;

/**
 *
 * @author GabrielSin
 */
public class CharLoginHeaders {

    public static final byte SERVER_JAPAN = 3,
            SERVER_TEST = 5,
            SERVER_SEA = 7,
            SERVER_GLOBAL = 8,
            SERVER_BRAZIL = 9;

    public static final byte PIN_ACCEPTED = 0x00,
            PIN_REGISTER = 0x01,
            PIN_REJECTED = 0x02,
            PIN_REQUEST = 0x04;

    public static final int LOGIN_OK = 0,
            LOGIN_BANNED = 2,
            //账号被封停
            LOGIN_BLOCKED = 3,
            //密码错误
            LOGIN_WRONG = 4,
            //未登陆的账号
            LOGIN_NOT_EXIST = 5,
            //链接服务器
            系统错误 = 6,
            LOGIN_ALREADY = 7,
            //系统错误无法连接
            LOGIN_ERROR = 8,
            LOGIN_ERROR_ = 9,
            //服务器正忙
            LOGIN_CONNECTION = 10,
   
            只有20岁可以链接 = 11,
            IP地址不能链接 = 12,
            LOGIN_EMAIL = 21,
            LOGIN_TOS = 23;
}
