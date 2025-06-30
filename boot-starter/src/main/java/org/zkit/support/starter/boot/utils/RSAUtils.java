package org.zkit.support.starter.boot.utils;

import lombok.extern.slf4j.Slf4j;
import org.zkit.support.starter.boot.exception.ResultException;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.Security;
import java.security.spec.RSAPrivateCrtKeySpec;

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

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 公钥加密
     *
     * @param content   内容
     * @param publicKey 公钥字符串
     * @return {@link String } Base64编码的加密结果
     */
    public static String encrypt(String content, String publicKey) {
        try {
            // 解码公钥
            String realKey = cleanPem(publicKey, "-----BEGIN PUBLIC KEY-----", "-----END PUBLIC KEY-----");
            byte[] keyBytes = Base64.getDecoder().decode(realKey);
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
     * @param privateKey 私钥字符串
     * @return {@link String } 解密后的明文
     */
    public static String decrypt(String content, String privateKey) {
        try {
            PrivateKey priKey = getPrivateKey(privateKey);
            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            byte[] encryptedBytes = Base64.getDecoder().decode(content);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, "UTF-8");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ResultException.internal();
        }
    }

    public static PrivateKey getPrivateKey(String privateKey) throws Exception {
        // 去除头尾和换行
        String realKey = cleanPem(privateKey, "-----BEGIN RSA PRIVATE KEY-----", "-----END RSA PRIVATE KEY-----");

        byte[] keyBytes = Base64.getDecoder().decode(realKey);

        try {
            // 尝试用 PKCS#8 解析
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            // 如果失败，尝试用 PKCS#1 解析
            ASN1Sequence primitive = ASN1Sequence.getInstance(keyBytes);
            RSAPrivateKey rsaPrivKey = RSAPrivateKey.getInstance(primitive);
            RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(
                rsaPrivKey.getModulus(),
                rsaPrivKey.getPublicExponent(),
                rsaPrivKey.getPrivateExponent(),
                rsaPrivKey.getPrime1(),
                rsaPrivKey.getPrime2(),
                rsaPrivKey.getExponent1(),
                rsaPrivKey.getExponent2(),
                rsaPrivKey.getCoefficient()
            );
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        }
    }

    public static String cleanPem(String pem, String begin, String end) {
        return pem.replace(begin, "")
                  .replace(end, "")
                  .replaceAll("\\s", "");
    }

}
