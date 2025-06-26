package common.core.utils;

/**
 * @author abing
 * @created 2025/5/20 15:50
 * 线上环境推荐使用配置文件获取环境变量控制密钥
 */

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HmacUtils {

    private static final String SECRET_KEY = "1314520qwertyuio";

    /**
     * 生成hmac签名
     */
    public static String hmacSignature(String data, String algorithm) throws Exception {
        SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), algorithm);
        Mac mac = Mac.getInstance(algorithm);
        mac.init(secretKey);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(rawHmac);
    }

    /**
     * 生成hmac签名
     */
    public static String generateHmacSHA256(String data) throws Exception {
        return hmacSignature(data, "HmacSHA256");
    }

    /**
     * 验证 HMAC 签名
     */
    public static boolean verifyHmac(String data, String expectedHmac) throws Exception {
        String generatedHmac = generateHmacSHA256(data);
        return generatedHmac.equals(expectedHmac);
    }


    public static void main(String[] args) {
        try {
            String data = "测试前面信息";

            String signature = generateHmacSHA256(data);
            System.out.println("签名 => " + signature);

            boolean isValid = verifyHmac(data, signature);
            System.out.println("验证结果: " + isValid);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
