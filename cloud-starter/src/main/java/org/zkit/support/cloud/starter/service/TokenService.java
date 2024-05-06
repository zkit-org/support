package org.zkit.support.cloud.starter.service;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zkit.support.cloud.starter.configuration.AuthConfiguration;
import org.zkit.support.cloud.starter.entity.CreateTokenData;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class TokenService {

    private AuthConfiguration configuration;

    public String create(CreateTokenData data) {
        Date now = new Date();
        SecretKey key = Keys.hmacShaKeyFor(configuration.getJwtSecret().getBytes(StandardCharsets.UTF_8));
        JwtBuilder jwtBuilder =  Jwts.builder()
                .id(data.getId().toString())
                .subject(data.getUsername()) //用户名
                .issuedAt(now)//登录时间
                .signWith(key)
                .expiration(new Date(now.getTime()+data.getExpiresIn()));
        return  jwtBuilder.compact();
    }

    @Autowired
    public void setConfiguration(AuthConfiguration configuration) {
        this.configuration = configuration;
    }
}
