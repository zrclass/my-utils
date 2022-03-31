package org.zrclass.page;

import java.util.List;

/**
 * @module
 * @Author zhourui
 * @Date 2021/08/04/17:28
 * @description 对pageHelper分页数据重新包装类，去除冗余字段
 */
public class PageResult<T> {

    /**
     * 总数
     */
    private Long total;


    /**
     * 分页结果列表
     */
    private List<T> list;


    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public PageResult() {
    }

    public PageResult(List<T> list) {
        this.list = list;
    }

    public PageResult(Long total, List<T> list) {
        this.total = total;
        this.list = list;
    }

    public static <T> PageResult<T> getPage(Long total, List<T> list){
        return new PageResult<>(total,list);
    }

    public static <T> PageResult<T> getPage(List<T> list){
        return new PageResult<>(list);
    }
}
