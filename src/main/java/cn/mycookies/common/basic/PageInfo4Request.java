package cn.mycookies.common.basic;

import com.mysql.jdbc.StringUtils;
import lombok.Data;

import java.util.Objects;

/**
 * 通用分页查询请求，分页查询的bean的基类
 *
 * @author Jann Lee
 * @date 2019-05-08 23:43
 **/
@Data
public class PageInfo4Request {

    /**
     * 排序信息
     */
    private static final String DESC = "desc";
    private static final String ASC = "asc";
    /**
     * 默认排序字段
     */
    private static final String DEFAULT_SORT_FIELD = "id";

    /**
     * 通用查询关键词，用来进行模糊匹配
     */
    public String keyWord;

    /**
     * 当前页数
     */
    public Integer pageNum;

    /**
     * 每页记录数
     */
    public Integer pageSize;

    /**
     * 排序字段
     */
    public String sortField;

    /***
     * 排序信息：升序/ 降序
     */
    private String order;

    /**
     * 当前页数默认值为1
     *
     * @return
     */
    public int getPageNum() {
        if (Objects.isNull(pageNum) || pageNum == 0) {
            return 1;
        }
        return pageNum;
    }

    /**
     * 每页大小默认为10
     *
     * @return
     */
    public int getPageSize() {
        if (Objects.isNull(pageNum) || pageNum == 0) {
            return 10;
        }
        return pageSize == null ? 10 : pageSize;
    }

    /**
     * 排序字段,默认是id
     *
     * @return
     */
    public String getSortField() {
        if (StringUtils.isNullOrEmpty(sortField)) {
            return DEFAULT_SORT_FIELD;
        }
        return sortField;
    }

    /**
     * 默认为降序排序
     *
     * @return
     */
    public String getOrder() {
        if (StringUtils.isNullOrEmpty(order) || Objects.equals(DESC, order)) {
            return DESC;
        }
        return ASC;
    }

    /**
     * 获取order by信息，共pageHelper使用
     * @return
     */
    public String getOrderBy() {
        return getSortField() + " " + getOrder();
    }
}
