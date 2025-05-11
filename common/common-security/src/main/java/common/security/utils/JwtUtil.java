package common.security.utils;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class JwtUtil {
    public static String sign = "abing";

    public static final long EXPIRE_TIME = 1000L * 60 * 30;  //  半小时
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 8;  //    八天

    public static String createToken(String userid, String username, String roles, String permissions) {
        JwtBuilder jwtBuilder = Jwts.builder();
        return jwtBuilder.setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .claim("userid", userid)
                .claim("username", username)
                .claim("roles", roles)
                .claim("permissions", permissions)
                .setId(UUID.randomUUID().toString())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .setSubject("CREATE_TOKEN")
                .signWith(SignatureAlgorithm.HS256, sign).compact();
    }

    public static boolean verifyToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(sign).parseClaimsJws(token);
            Claims body = claimsJws.getBody();
            Date expiration = body.getExpiration();
//        如果调用compareTo()方法的日期在参数日期之前，返回负数。
//        如果调用compareTo()方法的日期在参数日期之后，返回正数。
//        如果两个日期相等，返回0。
//        比较token令牌的时间，小于当前时间则为过期
            return expiration.compareTo(new Date()) > 0;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException |
                 IllegalArgumentException e) {
            return false;
        }
    }

    public static long getTokenRemainingTime(String token) {
        Date expiration = Jwts.parser().setSigningKey(sign).parseClaimsJws(token).getBody().getExpiration();
        long seconds = (expiration.getTime() - System.currentTimeMillis());
        return Math.max(seconds, 60); // 至少缓存一分钟
    }

    public static Map<String, String> parseToken(String token) {
        HashMap<String, String> map = new HashMap<>();
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(sign).parseClaimsJws(token);
        Claims body = claimsJws.getBody();
        String username = (String) body.get("username");
        String userid = (String) body.get("userid");
        String password = (String) body.get("roles");
        String permissions = (String) body.get("permissions");
        map.put("userid", userid);
        map.put("username", username);
        map.put("roles", password);
        map.put("permissions", permissions);
        return map;
    }

    public static Claims parse(String token) {
        return Jwts.parser()
                .setSigningKey(sign.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();
    }

}
