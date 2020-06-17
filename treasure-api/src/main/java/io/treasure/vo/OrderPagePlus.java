package io.treasure.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

@Data
public class OrderPagePlus<T> extends Page<T> {

   private int newOrderNum;
   private int inProcessNum;
   private int addOrderNum;
   private int refundOrderNum;

    public OrderPagePlus(long current, long size) {
        new Page<T>(current,size);
    }

    public OrderPagePlus(){

    }
}
