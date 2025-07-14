package org.zkit.support.starter.mybatis.entity;

import java.io.Serializable;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.Data;

@Data
public class PageRequest implements Serializable {

    private int page;
    private int size;
    private String keyword;

    public <T> Page<T> start() {
        return PageHelper.startPage(page, size);
    }

    public static PageRequest of(Integer page, Integer size) {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(page);
        pageRequest.setSize(size);
        return pageRequest;
    }

}
