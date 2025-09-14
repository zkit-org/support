package org.zkit.support.starter.boot.fastjson;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.filter.Filter;
import org.springframework.data.redis.serializer.RedisSerializer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FastJsonRedisSerializer<T> implements RedisSerializer<T> {
    private final Class<T> clazz;
    private volatile Filter autoTypeFilter;

    public FastJsonRedisSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

    private Filter getAutoTypeFilter() {
        if (autoTypeFilter == null) {
            synchronized (this) {
                if (autoTypeFilter == null) {
                    String[] prefixes = FastjsonAutoTypeConfigurer.fastjsonTypePrefixes;
                    if (prefixes == null)
                        prefixes = new String[0];
                    log.info("FastJsonRedisSerializer lazy-init prefixes: {}", JSONObject.toJSONString(prefixes));
                    autoTypeFilter = JSONReader.autoTypeFilter(prefixes);
                }
            }
        }
        return autoTypeFilter;
    }

    @Override
    public byte[] serialize(T t) {
        if (t == null) {
            return new byte[0];
        }
        return JSON.toJSONBytes(t, JSONWriter.Feature.WriteClassName);
    }

    @Override
    public T deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        Filter filter = getAutoTypeFilter();
        log.info("FastJsonRedisSerializer deserialize autoTypeFilter: {}", filter);
        return JSON.parseObject(bytes, clazz, filter);
    }
}
