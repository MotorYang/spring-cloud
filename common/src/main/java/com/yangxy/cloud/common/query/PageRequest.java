package com.yangxy.cloud.common.query;

/**
 * 分页请求
 * @param <T>
 */
public class PageRequest<T> {

    private long page = 1;
    private long size = 10;

    private T filter;

    public PageRequest() {}

    public PageRequest(long page, long size, T filter) {
        this.page = page;
        this.size = size;
        this.filter = filter;
    }

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public T getFilter() {
        return filter;
    }

    public void setFilter(T filter) {
        this.filter = filter;
    }
}
