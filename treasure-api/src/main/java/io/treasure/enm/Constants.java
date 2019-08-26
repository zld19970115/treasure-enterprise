package io.treasure.enm;

import java.util.HashMap;
import java.util.Map;

/**
 * 常量
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-25
 */
public interface Constants {

    /**
     * 支付方式
     */
    enum PayMode {
        /**
         * 余额支付
         */
        BALANCEPAY("1"),
        /**
         * 支付宝支付
         */
        ALIPAY("2"),
        /**
         * 微信支付
         */
        WXPAY("3"),
        /**
         * 银行卡支付
         */
        BANKCARDPAY("4");

        private String value;

        PayMode(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
    /**
     * 结账方式
     */
    enum CheckMode {
        /**
         * 消费者结账
         */
        USERCHECK("1"),
        /**
         * 商家结账
         */
        MERCHANTCHECK("2"),
        /**
         * 系统结账
         */
        SYSTEMCHECK("3");

        private String value;

        CheckMode(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }


    /**
     * 预订类型
     */
    enum ReservationType {
        /**
         * 正常预订
         */
        NORMALRESERVATION(1),
        /**
         * 只预订包房/散台
         */
        ONLYROOMRESERVATION(2),
        /**
         * 只预订菜品
         */
        ONLYGOODRESERVATION(3);

        private int value;

        ReservationType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 订单状态
     */
    enum OrderStatus {
        /**
         * 未支付订单
         */
        NOPAYORDER(1),
        /**
         * 商家已接单
         */
        MERCHANTRECEIPTORDER(2),
        /**
         * 商户拒接单
         */
        MERCHANTREFUSALORDER(3),
        /**
         * 支付完成订单
         */
        PAYORDER(4),
        /**
         * 取消未支付订单
         */
        CANCELNOPAYORDER(5),
        /**
         * 消费者申请退款订单
         */
        USERAPPLYREFUNDORDER(6),
        /**
         * 商户拒绝退款订单
         */
        MERCHANTREFUSESREFUNDORDER(7),
        /**
         * 商家同意退款订单
         */
        MERCHANTAGREEREFUNDORDER(8),
        /**
         * 删除订单
         */
        DELETEORDER(9);
        private int value;

        OrderStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
