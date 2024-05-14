package org.zkit.support.starter.boot.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.zkit.support.starter.boot.code.MailCode;
import org.zkit.support.starter.boot.code.PublicCode;
import org.zkit.support.starter.boot.configuration.AuthConfiguration;
import org.zkit.support.starter.boot.entity.EmailCode;
import org.zkit.support.starter.boot.entity.EmailData;
import org.zkit.support.starter.boot.exception.ResultException;
import org.zkit.support.starter.boot.utils.MessageUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class EmailCodeService {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private AuthConfiguration configuration;
    @Resource
    private EmailService emailService;

    private EmailCode newCode(String email, String action) {
        String codeKey = "email:code:" + action + ":" + email;
        EmailCode code = new EmailCode();
        code.setCode(configuration.isDebug() ? configuration.getEmailCode() : String.valueOf((int)((Math.random() * 9 + 1) * 100000)));
        code.setCreatedAt(new Date());
        redisTemplate.opsForValue().set(codeKey, code, 5, TimeUnit.MINUTES);
        return code;
    }

    public void send(String email, String action) {
        String codeKey = "email:code:" + action + ":" + email;
        String timeKey = "email:code:" + action + ":" + email + ":send:time";
        EmailCode code = (EmailCode) redisTemplate.opsForValue().get(codeKey);
        if (code != null) {
            long now = System.currentTimeMillis();
            Long lastTime = (Long)redisTemplate.opsForValue().get(timeKey);
            long offset = now - code.getCreatedAt().getTime();
            if (lastTime != null && now - lastTime < 60 * 1000)
                throw new ResultException(PublicCode.THROTTLE.code, MessageUtils.get(PublicCode.THROTTLE.key));
            if (offset < 60 * 1000)
                code = this.newCode(email, action);
        }else{
            code = this.newCode(email, action);
        }
        redisTemplate.opsForValue().set(timeKey, System.currentTimeMillis(), 1, TimeUnit.MINUTES);
        if(configuration.isDebug()) {
            return;
        }
        EmailData data = new EmailData();
        String title = MessageUtils.get("mail.title.code." + action);
        data.setEmail(email);
        data.setSubject(title);
        data.setTemplate("code");
        Map<String, Object> map = new HashMap<>();
        map.put("code", code.getCode());
        map.put("title", title);
        data.setData(map);
        boolean sent = emailService.singleSendMail(data);
        if(!sent) {
            redisTemplate.delete(codeKey);
            redisTemplate.delete(timeKey);
            throw new ResultException(MailCode.FAIL.code, MessageUtils.get(MailCode.FAIL.key));
        }
    }

    public boolean check(String email, String code, String action) {
        String codeKey = "email:code:" + action + ":" + email;
        EmailCode emailCode = (EmailCode) redisTemplate.opsForValue().get(codeKey);
        return !(emailCode == null || !emailCode.getCode().equals(code));
    }

}
