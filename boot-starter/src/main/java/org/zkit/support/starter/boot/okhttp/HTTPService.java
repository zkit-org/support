package org.zkit.support.starter.boot.okhttp;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HTTPService {

    @Resource
    private OkHttpClient client;

    public String get(String url) {
        return get(url, null);
    }

    public String get(String url, Headers headers) {
        if(headers == null) headers = new Headers.Builder().build();
        Request request = new Request.Builder().url(url).headers(headers).get().build();
        try {
            try (Response response = client.newCall(request).execute()) {
                if (response.body() != null) {
                    return response.body().string();
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

}
