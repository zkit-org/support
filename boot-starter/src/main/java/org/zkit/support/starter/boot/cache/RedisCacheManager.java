package org.zkit.support.starter.boot.cache;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.zkit.support.starter.boot.utils.TimeUtils;

public class RedisCacheManager extends org.springframework.data.redis.cache.RedisCacheManager {
    public RedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
        super(cacheWriter, defaultCacheConfiguration);
    }

    @NotNull
    @Override
    protected RedisCache createRedisCache(@NotNull String name, RedisCacheConfiguration cacheConfig) {
        String[] array = StringUtils.split(name, "#");
        name = array[0];
        // 解析 @Cacheable 注解的 value 属性用以单独设置有效期
        if (array.length > 1) {
            cacheConfig = cacheConfig.entryTtl(TimeUtils.of(array[1]));
        }
        return super.createRedisCache(name, cacheConfig);
    }
}
