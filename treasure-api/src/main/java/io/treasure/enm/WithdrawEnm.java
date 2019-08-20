package io.treasure.enm;
/**
 * 提现状态
 * @author 王丽娜
 */
public enum WithdrawEnm {
	/** 未审核**/
    STATUS_NO(1),
	/**同意提现**/
	STATUS_AGREE_YES(2),
	/**不同意提现**/
	STATUS_AGREE_NO(3),
	/**微信提现**/
	TYPE_WEIXIN(1),
	/**支付宝提现**/
	TYPE_ZFB(2),
	/**银行卡提现**/
	TYPE_BANK(3),
	/**手动提现**/
	WAY_HAND(2),
	/**自动提现**/
	WAY_AUTO(1);

	private int status;
	private WithdrawEnm(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}
}
