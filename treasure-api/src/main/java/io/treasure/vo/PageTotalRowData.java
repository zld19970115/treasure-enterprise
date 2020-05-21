package io.treasure.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class PageTotalRowData<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("总记录数")
    private int total;
    @ApiModelProperty("列表数据")
    private List<T> list;

    private Map<String,Object> totalRow;

    public PageTotalRowData(List<T> list, long total,Map<String,Object> totalRow) {
        this.list = list;
        this.total = (int) total;
        this.totalRow = totalRow;
    }

}
