package io.treasure.enm;
/**
 * 审核状态
 * @author 王丽娜
 */
public enum Order {
	/** 1-未支付**/
	PAY_STTAUS_1(1),
	/**2-已支付**/
	PAY_STTAUS_2(2),
	/**3-已接受，**/
	PAY_STTAUS_3(3),
	/** 4-已完成，**/
	PAY_STTAUS_4(4),
	/**5-取消**/
	PAY_STTAUS_5(5),
	/**6-申请退款,**/
	PAY_STTAUS_6(6),
	/**7-退款成功**/
	PAY_STTAUS_7(7),
	/**预约订单**/
	PAY_STATUS_8(8),
	/**拒绝退款**/
	PAY_STATUS_9(9),
	/**申请取消订单**/
	PAY_STATUS_10(10),
	/**微信支付**/
	PAY_TYPE_WEIXIN(1),
	/**支付宝支付**/
	PAY_TYPE_ALI(2);

	private int status;
	private Order(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}
}
