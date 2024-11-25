package org.zkit.support.starter.mybatis.entity;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.Data;

@Data
public class PageRequest {

    private int page;
    private int size;
    private String keyword;

    public <T> Page<T> start() {
        return PageHelper.startPage(page, size);
    }

}
