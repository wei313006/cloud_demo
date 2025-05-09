package common.core.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 加密工具
 */
public class AesEncipherUtils {

    private static final String SECRET_KEY = "1314520qwertyuio";

    // 16字节IV（初始向量），可自定义
    private static final String INIT_VECTOR = "-zzmxqsmysyyrps-";

    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    //    Java 默认只支持 128 位密钥   对应解密密钥长度为 16
    public static String encrypt(String data) {
        try {
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception ex) {
            throw new RuntimeException("AES encryption error", ex);
        }
    }

    public static String decrypt(String encryptedData) {
        try {
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(original, StandardCharsets.UTF_8);

        } catch (Exception ex) {
            throw new RuntimeException("AES decryption error", ex);
        }
    }

    public static void main(String[] args) {
        String originalText = "123123123321321321";

        String encryptedText = encrypt(originalText);
        System.out.println(encryptedText);

        String decryptedText = decrypt(encryptedText);
        System.out.println(decryptedText);
    }

}
