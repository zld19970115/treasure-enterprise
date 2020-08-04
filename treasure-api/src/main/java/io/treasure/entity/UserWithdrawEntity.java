package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;

import java.util.Date;
@Data
@TableName("ct_user_withdraw")
public class UserWithdrawEntity extends BaseEntity{

        private static final long serialVersionUID = 1L;

        /**
         * 提现金额
         */
        private Double money;
        /**
         * 类型:1-微信，2-支付宝，3-银行卡
         */
        private Integer type;
        /**
         * 方式：1-自动提现，2-手动提现
         */
        private Integer way;
        /**
         * 商户
         */
        private Long userId;
        /**
         *
         */
        private Integer status;
        /**
         * 备注
         */
        private String remark;
        /**
         * 修改时间
         */
        private Date updateDate;
        /**
         * 修改者
         */
        private Long updater;
        /**
         * 审核人
         */
        private Long verify;
        /**
         * 审核时间
         */
        private Date verifyDate;
        /**
         * 审核意见
         */
        private String verifyReason;
        /**
         * 审核状态
         */
        private Integer verifyState;

}
