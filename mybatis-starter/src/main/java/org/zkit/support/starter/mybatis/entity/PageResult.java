package org.zkit.support.starter.mybatis.entity;

import com.github.pagehelper.Page;
import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {

    private long total;
    private List<T> data;

    public static <T> PageResult<T> of(Page<T> page) {
        try(page){
            PageResult<T> result = new PageResult<T>();
            result.data = page.getResult();
            result.total = page.getTotal();
            return result;
        }
    }

    public static <T> PageResult<T> of(long total, List<T> data) {
        PageResult<T> result = new PageResult<T>();
        result.data = data;
        result.total = total;
        return result;
    }

}
