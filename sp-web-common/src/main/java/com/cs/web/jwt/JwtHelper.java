package com.cs.web.jwt;

import com.alibaba.fastjson2.JSONObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * 使用不同的properties可以生成多个helper的bean
 * 例如：双token，密钥需要不一致，过期时间需要不一致的场景
 *
 * @author sb
 * @date 2023/5/23 16:08
 */
@Slf4j
public class JwtHelper {

    private JwtProperties jwtProperties;

    public JwtHelper(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public JwtProperties prop() {
        return jwtProperties;
    }

    /**
     * 从token中获取claim
     *
     * @param token token
     * @return claim
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(this.jwtProperties.getSignKey().getBytes());
    }

    /**
     * 根据token获取Subject，存入的字符串数据
     *
     * @return
     */
    public String getSubject(String token) {
        return getClaimsFromToken(token).getSubject();
    }


    /**
     * 根据token获取Subject，存入的对象
     *
     * @param token
     * @param clazz
     * @return
     */
    public <T> T getSubject(String token, Class<T> clazz) {
        return JSONObject.parseObject(getSubject(token), clazz);
    }


    /**
     * 获取token的过期时间
     *
     * @param token token
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token)
                .getExpiration();
    }

    /**
     * 判断token是否过期
     *
     * @param token token
     * @return 已过期返回true，未过期返回false
     */
    private Boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 计算token的过期时间
     *
     * @return 过期时间
     */
    private Date getExpirationTime() {
        return new Date(System.currentTimeMillis() + this.jwtProperties.getExpireTimeInSecond() * 1000);
    }


    /**
     * 为指定用户生成token
     *
     * @param t 用户信息
     * @return token
     */
    public <T> String sign(T t, Date expireTime) {
        Date createdTime = new Date();

        String json = JSONObject.toJSONString(t);

        return Jwts.builder()
                .setSubject(json)
                .setIssuedAt(createdTime)
                .setExpiration(expireTime)
                // 你也可以改用你喜欢的算法
                // 支持的算法详见：https://github.com/jwtk/jjwt#features
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    /**
     * jwt签名
     *
     * @param t
     * @param <T>
     * @return
     */
    public <T> String sign(T t) {
        Date expirationTime = this.getExpirationTime();
        return sign(t, expirationTime);
    }

    /**
     * 判断token是否非法
     *
     * @param token token
     * @return 未过期返回true，否则返回false
     */
    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

}
