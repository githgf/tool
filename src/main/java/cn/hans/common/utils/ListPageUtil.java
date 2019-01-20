package cn.hans.common.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * desc：     list分页代码 <br>
 * author：   wangsong <br>
 * date：     2018/5/17 上午5:38 <br>
 * version：  v1.0.0 <br>
 */
public class ListPageUtil<T> {

    /**
     * 每页显示条数
     */
    private int pageSize;

    /**
     * 总页数
     */
    private int pageCount;

    /**
     * 原集合
     */
    private List<T> data;

    public ListPageUtil(List<T> data, int pageSize) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("data must be not empty!");
        }

        this.data = data;
        this.pageSize = pageSize;
        this.pageCount = data.size()/pageSize;
        if(data.size()%pageSize!=0){
            this.pageCount++;
        }
    }

    /**
     * 得到分页后的数据
     *
     * @param pageNum 页码
     * @return 分页后结果
     */
    public List<T> getPagedList(int pageNum) {
        int fromIndex = (pageNum - 1) * pageSize;
        if (fromIndex >= data.size()) {
            return Collections.emptyList();
        }

        int toIndex = pageNum * pageSize;
        if (toIndex >= data.size()) {
            toIndex = data.size();
        }
        return data.subList(fromIndex, toIndex);
    }

    /**
     * 逻辑分页
     *
     * @param list     待分页的数组
     * @param pageNo   当前页码
     * @param pageSize 每页记录数
     * @return 分页后的数组
     */
    public static <T> List<T> pagination(List<T> list, int pageNo, int pageSize) {
        if (pageNo < 1) {
            pageNo = 1;
        }
        //如果limit<0，list就不分页
        if (pageSize < 0){
            return list;
        }
        int offset = pageSize * (pageNo - 1);

        if (offset + pageSize < list.size()) {
            return list.subList(offset, offset + pageSize);
        } else if (offset < list.size()) {
            return list.subList(offset, list.size());
        } else {
            return new ArrayList<>();
        }
    }

    public int getPageSize() {
        return pageSize;
    }

    public List<T> getData() {
        return data;
    }

    public int getPageCount() {
        return pageCount;
    }

}
