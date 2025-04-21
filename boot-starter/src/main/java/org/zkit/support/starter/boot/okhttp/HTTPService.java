package org.zkit.support.starter.boot.okhttp;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HTTPService {

    @Resource
    private OkHttpClient client;

    public String post(String url, String data) {
        return post(url, data, null);
    }

    public String post(String url, String data, Headers headers) {
        if(headers == null) headers = new Headers.Builder().build();
        RequestBody requestBody = RequestBody.create(data, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(url).post(requestBody).headers(headers).build();
        log.info("post: {}", request);
        try{
            try (Response response = client.newCall(request).execute()) {
                if (response.body() != null) {
                    return response.body().string();
                }
            }
        }catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public String get(String url) {
        return get(url, null);
    }

    public String get(String url, Headers headers) {
        if(headers == null) headers = new Headers.Builder().build();
        Request request = new Request.Builder().url(url).headers(headers).get().build();
        log.info("get: {}", request);
        try {
            try (Response response = client.newCall(request).execute()) {
                if (response.body() != null) {
                    return response.body().string();
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

}
