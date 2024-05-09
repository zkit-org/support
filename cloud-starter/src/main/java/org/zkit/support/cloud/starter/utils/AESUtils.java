package org.zkit.support.cloud.starter.utils;

import lombok.extern.slf4j.Slf4j;
import org.zkit.support.cloud.starter.exception.ResultException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
public class AESUtils {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";

    public static String decrypt(String cipherText, String secretKey) {
        // 将Base64编码的密文解码
        byte[] encrypted = Base64.getDecoder().decode(cipherText);
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(encrypted));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw ResultException.internal();
        }
    }

    public static String encrypt(String plainText, String secretKey) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
