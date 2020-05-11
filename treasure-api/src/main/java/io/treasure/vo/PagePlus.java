package io.treasure.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
public class PagePlus<T> extends Page<T> {

    private Double wxMoney;
    private Double aliMoney;
    private Double cardMoney;

    private Double total_cash;
    private Double already_cash;
    private Double not_cash;

    public PagePlus(long current, long size) {
        new Page<T>(current,size);
    }

    public PagePlus(){

    }
}
