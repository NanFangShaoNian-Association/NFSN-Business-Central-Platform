package com.nfsn.common.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author gjj
 */
public class MD5Utils {
    private static final Logger log = LoggerFactory.getLogger(MD5Utils.class);
    /**
     * 对输入的密码进行MD5加密，并返回加密后的字符串
     *
     * @param password 需要加密的密码
     * @return 加密后的字符串
     */
    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // 异常处理
            log.error("MD5加密出现异常");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 检查密码是否匹配
     *
     * @param password      用户输入的密码
     * @param hashedPassword 存储在数据库中的已加密密码
     * @return 如果密码匹配，则返回true；否则返回false。
     */
    public static boolean check(String password, String hashedPassword) {
        String hashedInputPassword = hash(password);
        return hashedPassword.equals(hashedInputPassword);
    }
}
