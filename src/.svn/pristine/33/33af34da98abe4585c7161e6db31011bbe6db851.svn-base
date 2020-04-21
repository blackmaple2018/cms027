/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.crypto;

import constants.ServerProperties;

/**
 *
 * @author Administrator
 */
public class AESCipher {
    private static final AES pCipher;

    public static final short nVersion = ServerProperties.World.MAPLE_VERSION;
    
    private static final byte[] aKey = new byte[]{
            (byte) 0x13, 0x00, 0x00, 0x00,
            (byte) 0x08, 0x00, 0x00, 0x00,
            (byte) 0x06, 0x00, 0x00, 0x00,
            (byte) 0xB4, 0x00, 0x00, 0x00,
            (byte) 0x1B, 0x00, 0x00, 0x00,
            (byte) 0x0F, 0x00, 0x00, 0x00,
            (byte) 0x33, 0x00, 0x00, 0x00,
            (byte) 0x52, 0x00, 0x00, 0x00
    };
    
    static {
        pCipher = new AES();
        pCipher.setKey(aKey);
    }

    public static void Crypt(byte[] aData, int pSrc) {
        byte[] pdwKey = new byte[]{
                (byte) (pSrc & 0xFF), (byte) ((pSrc >> 8) & 0xFF), (byte) ((pSrc >> 16) & 0xFF), (byte) ((pSrc >> 24) & 0xFF)
        };
        Crypt(aData, pdwKey);
    }

    public static void Crypt(byte[] aData, byte[] aSeqKey) {
        int a = aData.length;
        int b = 1456;
        int c = 0;
        while (a > 0) {
            byte[] d = multiplyBytes(aSeqKey, 4, 4);
            if (a < b) {
                b = a;
            }
            for (int e = c; e < (c + b); e++) {
                if ((e - c) % d.length == 0) {
                    pCipher.encrypt(d);
                }
                aData[e] ^= d[(e - c) % d.length];
            }
            c += b;
            a -= b;
            b = 1460;
        }
    }

    public static byte[] multiplyBytes(byte[] iv, int i, int i0) {
        byte[] ret = new byte[i * i0];
        for (int x = 0; x < ret.length; x++) {
            ret[x] = iv[x % i];
        }
        return ret;
    }
}
