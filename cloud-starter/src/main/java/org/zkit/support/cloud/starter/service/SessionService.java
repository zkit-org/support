package org.zkit.support.cloud.starter.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.zkit.support.cloud.starter.entity.SessionUser;
import org.zkit.support.cloud.starter.utils.MD5Utils;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class SessionService {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    public void login(SessionUser user) {
        String tokenKey = "session:token:" + MD5Utils.text(user.getJwtToken());
        String sessionKey = "session:user:" + user.getUsername();
        redisTemplate.opsForValue().set(sessionKey, user, user.getExpiresIn(), TimeUnit.MILLISECONDS);
        redisTemplate.opsForValue().set(tokenKey, user.getJwtToken(), user.getExpiresIn(), TimeUnit.MILLISECONDS);
    }

}
