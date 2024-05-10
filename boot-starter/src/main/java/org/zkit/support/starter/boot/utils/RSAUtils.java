package org.zkit.support.starter.boot.utils;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import lombok.extern.slf4j.Slf4j;
import org.zkit.support.starter.boot.exception.ResultException;

@Slf4j
public class RSAUtils {

    /**
     * 公钥加密
     *
     * @param content 内容
     * @return {@link String }
     * @author qs.zhang
     */
    public static String encrypt(String content, String publicKey) {
        try {
            RSA rsa = new RSA(null, publicKey);
            return rsa.encryptBase64(content, KeyType.PublicKey);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw ResultException.internal();
        }
    }

    /**
     * 私钥解密
     *
     * @param content 内容
     * @return {@link String }
     * @author qs.zhang
     */
    public static String decrypt(String content, String privateKey) {
        try {
            RSA rsa = new RSA(privateKey, null);
            return rsa.decryptStr(content, KeyType.PrivateKey);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw ResultException.internal();
        }
    }

}
