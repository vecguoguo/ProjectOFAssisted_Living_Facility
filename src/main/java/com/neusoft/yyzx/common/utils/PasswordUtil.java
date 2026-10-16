package com.neusoft.yyzx.common.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码工具类（纯 JDK，无第三方依赖）：PBKDF2-HMAC-SHA256 加盐哈希。
 * <p>
 * 存储格式：{@code pbkdf2$<iterations>$<base64(salt)>$<base64(hash)>}。
 * 校验时对同一盐重新派生并做常量时间比较。
 * <p>
 * 兼容历史明文：{@link #isHashed(String)} 为 false 时视为旧明文，登录成功后应调用
 * {@link #hash(String)} 透明升级，逐步替换存量明文。
 */
public final class PasswordUtil {

    private static final String PREFIX = "pbkdf2";
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 120000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    /** 生成加盐哈希串。 */
    public static String hash(String rawPassword) {
        if (rawPassword == null) {
            return null;
        }
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        byte[] hash = pbkdf2(rawPassword.toCharArray(), salt, ITERATIONS);
        Base64.Encoder encoder = Base64.getEncoder();
        return PREFIX + "$" + ITERATIONS + "$" + encoder.encodeToString(salt) + "$" + encoder.encodeToString(hash);
    }

    /** 校验明文是否与存储串匹配。 */
    public static boolean matches(String rawPassword, String stored) {
        if (rawPassword == null || stored == null || !isHashed(stored)) {
            return false;
        }
        try {
            String[] parts = stored.split("\\$");
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            byte[] actual = pbkdf2(rawPassword.toCharArray(), salt, iterations);
            return MessageDigest.isEqual(expected, actual);
        } catch (RuntimeException e) {
            return false;
        }
    }

    /** 存储串是否为本工具生成的哈希格式（否则视为历史明文）。 */
    public static boolean isHashed(String stored) {
        if (stored == null) {
            return false;
        }
        String[] parts = stored.split("\\$");
        return parts.length == 4 && PREFIX.equals(parts[0]);
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            return factory.generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("密码哈希计算失败", e);
        }
    }
}
