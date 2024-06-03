package org.zkit.support.starter.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.zkit.support.starter.boot.utils.MD5Utils;
import org.zkit.support.starter.security.entity.SessionUser;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class SessionService {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private TokenService tokenService;

    public void login(SessionUser user) {
        String tokenKey = "session:token:" + MD5Utils.text(user.getJwtToken());
        String sessionKey = "session:user:" + user.getUsername();
        redisTemplate.opsForValue().set(sessionKey, user, user.getExpiresIn(), TimeUnit.MILLISECONDS);
        redisTemplate.opsForValue().set(tokenKey, user.getJwtToken(), user.getExpiresIn(), TimeUnit.MILLISECONDS);
    }

    public void logout(String token) {
        SessionUser user = this.get(token);
        if(user == null) {
            return;
        }
        String tokenKey = "session:token:" + token;
        String sessionKey = "session:user:" + user.getUsername();
        redisTemplate.delete(tokenKey);
        redisTemplate.delete(sessionKey);
    }

    public SessionUser get(String token) {
        String tokenKey = "session:token:" + token;
        String jwtToken = (String) redisTemplate.opsForValue().get(tokenKey);
        if(StringUtils.isEmpty(jwtToken))
            return null;
        Jws<Claims> jws = tokenService.parse(jwtToken);
        if(jws == null) { // token 不正确或者已过期
            return null;
        }
        String username = jws.getPayload().getSubject();
        String sessionKey = "session:user:" + username;
        return (SessionUser) redisTemplate.opsForValue().get(sessionKey);
    }

}
