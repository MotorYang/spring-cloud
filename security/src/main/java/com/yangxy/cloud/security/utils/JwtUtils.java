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
    private static final long EXPIRATION = 86400000L; // 1天
    private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // 生成 Token
    public static String generateToken(String account, String userId) {
        return Jwts.builder()
                .setSubject(account)
                .claim("userId", userId) // 把用户ID放入Token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 校验 Token
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 获取用户名
    public static String getUserAccount(String token) {
        return getClaims(token).getSubject();
    }

    // 获取用户ID
    public static Long getUserId(String token) {
        return getClaims(token).get("userId", Long.class);
    }

    private static Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}
