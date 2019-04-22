package eu.nexwell.android.nexovision.misc;

import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Calendar;
import java.util.Random;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class JCrypto {
    private static int RC4_KEY_SIZE = 30;
    private static byte[] SBox = new byte[256];
    private static int SIZE = 256;
    private static int rc4_i;
    private static int rc4_j;
    public static byte[] rc4_key = new byte[RC4_KEY_SIZE];

    public static void md5(byte[] p, byte[] o) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(p, 0, p.length - 1);
            byte[] output_m = m.digest();
            for (int i = 0; i < 16; i++) {
                o[i] = output_m[i];
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static void generate_rc4() {
        Random generator = new Random(Calendar.getInstance().getTimeInMillis());
        for (int i = 0; i < RC4_KEY_SIZE; i++) {
            rc4_key[i] = (byte) (generator.nextInt(Callback.DEFAULT_SWIPE_ANIMATION_DURATION) + 2);
        }
    }

    public static void rc4(byte[] k, byte[] i, int ilen) {
        generateKey(k, RC4_KEY_SIZE);
        rc4_i = 0;
        rc4_j = 0;
        for (int a = 0; a < ilen; a++) {
            i[a] = (byte) (i[a] ^ getByte());
        }
    }

    public static void rsaEnc(byte[] rsaPK, byte[] message, int mlen, byte[] output) {
        int i;
        String exp = new String();
        String mod = new String();
        String tmp = new String();
        for (i = 0; i < 32; i++) {
            tmp = Integer.toHexString(rsaPK[31 - i]);
            if (tmp.length() > 1) {
                exp = exp + tmp.substring(tmp.length() - 2);
            } else {
                exp = exp + "0" + tmp;
            }
            tmp = Integer.toHexString(rsaPK[63 - i]);
            if (tmp.length() > 1) {
                mod = mod + tmp.substring(tmp.length() - 2);
            } else {
                mod = mod + "0" + tmp;
            }
        }
        BigInteger modulus = new BigInteger(mod, 16);
        BigInteger bigInteger = new BigInteger(exp, 16);
        byte[] m = new byte[RC4_KEY_SIZE];
        for (i = 0; i < RC4_KEY_SIZE; i++) {
            m[i] = message[(RC4_KEY_SIZE - 1) - i];
        }
        BigInteger mess = new BigInteger(m);
        try {
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
            cipher.init(1, (RSAPublicKey) KeyFactory.getInstance("RSA", "BC").generatePublic(new RSAPublicKeySpec(modulus, bigInteger)));
            byte[] cipherText = cipher.doFinal(m);
            for (i = 0; i < 32; i++) {
                output[i] = cipherText[31 - i];
            }
        } catch (NoSuchAlgorithmException e2) {
            e2.printStackTrace();
        } catch (NoSuchProviderException e22) {
            e22.printStackTrace();
        } catch (NoSuchPaddingException e23) {
            e23.printStackTrace();
        } catch (InvalidKeySpecException e24) {
            e24.printStackTrace();
        } catch (InvalidKeyException e1) {
            e1.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e3) {
            e3.printStackTrace();
        }
    }

    static void generateKey(byte[] Key, int len) {
        int i;
        byte[] KBox = new byte[SIZE];
        for (i = 0; i < SIZE; i++) {
            SBox[i] = (byte) i;
        }
        for (i = 0; i < SIZE; i++) {
            int tmplen = len;
            if (tmplen > SIZE) {
                tmplen = SIZE;
            }
            KBox[i] = Key[i % tmplen];
        }
        int j = 0;
        for (i = 0; i < SIZE; i++) {
            j = ((((SIZE * 2) + j) + SBox[i]) + KBox[i]) % SIZE;
            byte tmp = SBox[i];
            SBox[i] = SBox[j];
            SBox[j] = tmp;
        }
    }

    static byte getByte() {
        rc4_i = (((SIZE * 2) + rc4_i) + 1) % SIZE;
        rc4_j = (((SIZE * 2) + rc4_j) + SBox[rc4_i]) % SIZE;
        byte tmp = SBox[rc4_i];
        SBox[rc4_i] = SBox[rc4_j];
        SBox[rc4_j] = tmp;
        return SBox[(((SIZE * 2) + SBox[rc4_i]) + SBox[rc4_j]) % SIZE];
    }
}
