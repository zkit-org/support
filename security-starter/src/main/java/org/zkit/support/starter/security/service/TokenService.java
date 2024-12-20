package org.zkit.support.starter.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zkit.support.starter.security.configuration.JWTConfiguration;
import org.zkit.support.starter.security.entity.CreateTokenData;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class TokenService {

    @Resource
    private JWTConfiguration configuration;

    public String create(CreateTokenData data) {
        Date now = new Date();
        SecretKey key = Keys.hmacShaKeyFor(configuration.getSecret().getBytes(StandardCharsets.UTF_8));
        JwtBuilder jwtBuilder =  Jwts.builder()
                .id(data.getId().toString())
                .subject(data.getUsername()) //用户名
                .issuedAt(now)//登录时间
                .signWith(key)
                .expiration(new Date(now.getTime()+data.getExpiresIn()));
        return  jwtBuilder.compact();
    }

    public boolean checked(String token) {
        SecretKey key = Keys.hmacShaKeyFor(configuration.getSecret().getBytes(StandardCharsets.UTF_8));
        try{
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        }catch (Exception e){
            log.error("parse token error", e);
        }
        return false;
    }

    public Jws<Claims> parse(String token) {
        SecretKey key = Keys.hmacShaKeyFor(configuration.getSecret().getBytes(StandardCharsets.UTF_8));
        Jws<Claims> jws = null;
        try{
            jws = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
        }catch (Exception e){
            log.error("parse token error", e);
        }
        return jws;
    }

}
