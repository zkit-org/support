package org.zkit.support.starter.mybatis.entity;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

@Data
public class PageQueryRequest {

    private int page;
    private int size;
    private String keyword;

    public <T> Page<T> toPage() {
        return new Page<>(page, size);
    }

}
