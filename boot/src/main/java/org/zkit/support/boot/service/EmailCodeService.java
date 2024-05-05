package org.zkit.support.boot.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.zkit.support.boot.configuration.AuthConfiguration;
import org.zkit.support.boot.entity.EmailCode;
import org.zkit.support.boot.entity.EmailData;
import org.zkit.support.boot.exception.ResultException;
import org.zkit.support.boot.utils.MessageUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class EmailCodeService {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    private AuthConfiguration configuration;
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
                throw new ResultException(1, MessageUtils.get("public.throttle"));
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
        data.setEmail(email);
        data.setSubject(MessageUtils.get("mail.title.code." + action));
        data.setTemplate("code");
        Map<String, Object> map = new HashMap<>();
        map.put("code", code.getCode());
        data.setData(map);
        boolean sent = emailService.singleSendMail(data);
        if(!sent) {
            redisTemplate.delete(codeKey);
            redisTemplate.delete(timeKey);
            throw new ResultException(2, MessageUtils.get("mail.fail"));
        }
    }

    @Autowired
    public void setConfiguration(AuthConfiguration configuration) {
        this.configuration = configuration;
    }

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
}
