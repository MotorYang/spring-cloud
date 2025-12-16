package com.yangxy.cloud.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

/**
 * @author MotorYang
 * @email motoyangxy@outlook.com
 * @date 2025/11/25 06:05
 * JWT 工具类
 */
public class JwtUtils {
    // 生产环境请放入配置中心
    private static final String SECRET = "YangxyCloudProjectSecretKeyMustBeVeryLong32Chars+";
    private static final long EXPIRATION = 86400000L; // token过期时间：1天
    private static final long REFRESH_EXPIRATION = 604800000L; // refreshToken过期时间：7天
    private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    /**
     * 生成 Token 和 RefreshToken
     * @param account 用户账号
     * @param userId 用户ID
     * @return TokenResponse 包含token和refreshToken
     */
    public static TokenResponse generateTokens(String account, String userId) {
        String token = generateToken(account, userId, EXPIRATION);
        String refreshToken = generateToken(account, userId, REFRESH_EXPIRATION);
        return new TokenResponse(token, refreshToken);
    }

    /**
     * 生成 Token（内部方法）
     * @param account 用户账号
     * @param userId 用户ID
     * @param expiration 过期时间（毫秒）
     * @return token字符串
     */
    private static String generateToken(String account, String userId, long expiration) {
        return Jwts.builder()
                .setSubject(account)
                .claim("userId", userId) // 把用户ID放入Token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 生成 Token（仅生成访问令牌，过期时间较短）
     */
    public static String generateToken(String account, String userId) {
        return generateToken(account, userId, EXPIRATION);
    }

    /**
     * 生成 RefreshToken（仅生成刷新令牌，过期时间较长）
     */
    public static String generateRefreshToken(String account, String userId) {
        return generateToken(account, userId, REFRESH_EXPIRATION);
    }

    /**
     * 使用 RefreshToken 刷新生成新的 Token
     */
    public static TokenResponse refreshAccessToken(String refreshToken) {
        if (!validateToken(refreshToken)) {
            throw new RuntimeException("RefreshToken已过期或无效");
        }
        String account = getUserAccount(refreshToken);
        Long userId = getUserId(refreshToken);
        return generateTokens(account, userId.toString());
    }

    /**
     * 校验 Token
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取用户账号
     */
    public static String getUserAccount(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * 获取用户ID
     */
    public static Long getUserId(String token) {
        return getClaims(token).get("userId", Long.class);
    }

    /**
     * 获取 Token 中的所有声明
     */
    private static Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    /**
     * Token响应类
     */
    public static class TokenResponse {
        private String token;
        private String refreshToken;
        private long expiresIn; // token过期时间（秒）

        public TokenResponse(String token, String refreshToken) {
            this.token = token;
            this.refreshToken = refreshToken;
            this.expiresIn = EXPIRATION / 1000; // 转换为秒
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public long getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(long expiresIn) {
            this.expiresIn = expiresIn;
        }

        @Override
        public String toString() {
            return "TokenResponse{" +
                    "token='" + token + '\'' +
                    ", refreshToken='" + refreshToken + '\'' +
                    ", expiresIn=" + expiresIn +
                    '}';
        }
    }
}
