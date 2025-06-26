package common.core.utils;

import common.config.CommonConfig;
import jakarta.annotation.Resource;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author abing
 * @created 2025/4/20 15:50
 * 线上环境推荐使用配置文件获取环境变量控制密钥
 */
public class AesEncipherUtils {

//    private static final String SECRET_KEY = "1314520QWERTYUIO";
//
//    // 16字节IV（初始向量），可自定义
//    private static final String INIT_VECTOR = "-ZZMXQSMYSYYRPS-";
//
//    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    //    Java 默认只支持 128 位密钥   对应解密密钥长度为 16


    public static String encrypt(String aesKey, String ivKey, String pattern, String data) {
        try {
            IvParameterSpec iv = new IvParameterSpec(ivKey.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(aesKey.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance(pattern);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception ex) {
            throw new RuntimeException("AES encryption error", ex);
        }
    }

    public static String decrypt(String aesKey, String ivKey, String pattern, String encryptedData) {
        try {

            IvParameterSpec iv = new IvParameterSpec(ivKey.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(aesKey.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance(pattern);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(original, StandardCharsets.UTF_8);

        } catch (Exception ex) {
            throw new RuntimeException("AES decryption error", ex);
        }
    }

}
