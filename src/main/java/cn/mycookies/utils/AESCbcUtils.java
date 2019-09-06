package cn.mycookies.utils;

/**
 * @author liqiang
 * @datetime 2019/9/6 15:14
 **/
import java.nio.charset.Charset;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;

public class AESCbcUtils {
    private static int AES_128 = 128;
    private static int IV_16 = 16;
    public static String ALGORITHM = "AES";
    private static String AES_CBC_PADDING = "AES/CBC/PKCS5Padding";
    private static final Charset CHARSET = Charset.forName("UTF-8");

    public AESCbcUtils() {
    }

    public static String genHexIv() {
        byte[] iv = new byte[IV_16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return Hex.encodeHexString(iv);
    }

    public static String genSecretKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(AES_128);
        SecretKey secretKey = keyGenerator.generateKey();
        return Hex.encodeHexString(secretKey.getEncoded());
    }

    public static String encrypt(String hexKey, String hexIv, String message) throws Exception {
        byte[] key = Hex.decodeHex(hexKey.toCharArray());
        byte[] iv = Hex.decodeHex(hexIv.toCharArray());
        byte[] buffer = encrypt(key, iv, message.getBytes(CHARSET));
        return Hex.encodeHexString(buffer);
    }

    public static String decrypt(String hexKey, String hexIv, String message) throws Exception {
        byte[] buffer = Hex.decodeHex(message.toCharArray());
        byte[] key = Hex.decodeHex(hexKey.toCharArray());
        byte[] iv = Hex.decodeHex(hexIv.toCharArray());
        return new String(decrypt(key, iv, buffer), CHARSET);
    }

    public static byte[] encrypt(byte[] key, byte[] iv, byte[] message) throws Exception {
        return encryptDecrpty(1, key, iv, message);
    }

    public static byte[] decrypt(byte[] key, byte[] iv, byte[] message) throws Exception {
        return encryptDecrpty(2, key, iv, message);
    }

    private static byte[] encryptDecrpty(int mode, byte[] key, byte[] iv, byte[] message) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_CBC_PADDING);
        SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(mode, keySpec, ivSpec);
        return cipher.doFinal(message);
    }

    public static void main(String[] args) throws Exception {
        String hexKey = "d75bd94d434b58bcb02d30efe460cf67";
        genSecretKey();
        String hexIv = "c9e5a4d463721b488330dd49ea645565";
        genHexIv();
        System.out.println(hexIv);
        String randomString = "9DFDC027-D7FD-774A-A2D8-AB1F9A13D259哈哈";
        System.out.println("1. 原文: " + randomString);
        String cipherText = encrypt(hexKey, hexIv, randomString);
        System.out.println("2. 密文: " + cipherText);
        String decryptedString = decrypt(hexKey, hexIv, cipherText);
        System.out.println("3. 解密文 : " + decryptedString);
    }
}
