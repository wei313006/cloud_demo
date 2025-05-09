package common.core.utils;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

/**
 * @author abing
 * @created 2025/3/24 17:47
 */

public class RSAEncipherUtils {

    // 生成RSA密钥对
    public static KeyPair generateKeys(int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(keySize);
        return keyGen.generateKeyPair();
    }

    // 使用公钥加密数据
    public static String encrypt(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // 使用私钥解密数据
    public static String decrypt(String encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

//    public static void main(String[] args) throws Exception {
//        // 生成密钥对(公钥和密钥)
//        KeyPair keyPair = generateKeys(2048);
//        PublicKey publicKey = keyPair.getPublic();
//        PrivateKey privateKey = keyPair.getPrivate();
//        // 原始数据
//        String originalText = "123123123321321321";
//
//        String encryptedText = encrypt(originalText, publicKey);
//        System.out.println(encryptedText);
//        String decryptedText = decrypt(encryptedText, privateKey);
//        System.out.println(decryptedText);
//    }

}
