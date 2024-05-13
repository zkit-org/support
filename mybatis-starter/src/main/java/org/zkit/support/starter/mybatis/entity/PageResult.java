package org.zkit.support.starter.mybatis.entity;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {

    private long total;
    private List<T> data;

    public static <T> PageResult<T> of(long total, List<T> data) {
        PageResult<T> result = new PageResult<T>();
        result.data = data;
        result.total = total;
        return result;
    }

}
