package org.zkit.support.starter.boot.utils;

import lombok.extern.slf4j.Slf4j;
import org.zkit.support.starter.boot.exception.ResultException;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
public class RSAUtils {

    private static final String RSA_ALGORITHM = "RSA";
    private static final String RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding";

    /**
     * 公钥加密
     *
     * @param content   内容
     * @param publicKey 公钥字符串（Base64编码）
     * @return {@link String } Base64编码的加密结果
     */
    public static String encrypt(String content, String publicKey) {
        try {
            // 解码公钥
            byte[] keyBytes = Base64.getDecoder().decode(publicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            PublicKey pubKey = keyFactory.generatePublic(keySpec);

            // 加密
            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] encryptedBytes = cipher.doFinal(content.getBytes("UTF-8"));

            // 返回Base64编码的结果
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ResultException.internal();
        }
    }

    /**
     * 私钥解密
     *
     * @param content    加密内容（Base64编码）
     * @param privateKey 私钥字符串（Base64编码）
     * @return {@link String } 解密后的明文
     */
    public static String decrypt(String content, String privateKey) {
        try {
            // 解码私钥
            byte[] keyBytes = Base64.getDecoder().decode(privateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            PrivateKey priKey = keyFactory.generatePrivate(keySpec);

            // 解密
            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            byte[] encryptedBytes = Base64.getDecoder().decode(content);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            // 返回解密后的字符串
            return new String(decryptedBytes, "UTF-8");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ResultException.internal();
        }
    }

}
