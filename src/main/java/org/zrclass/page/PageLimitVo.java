package org.zrclass.page;

import javax.validation.constraints.NotNull;

/**
 * 查询分页对象
 */
public class PageLimitVo {

    @NotNull(message = "分页时必填项：当前页数")
    private int pageNum = 1;

    @NotNull(message = "分页时必填项：每页条数")
    private int pageSize = 10;

    public PageLimitVo() {
    }

    public PageLimitVo(@NotNull(message = "分页时必填项：每页条数") int pageSize, @NotNull(message = "分页时必填项：当前页数") int pageNum) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
}
