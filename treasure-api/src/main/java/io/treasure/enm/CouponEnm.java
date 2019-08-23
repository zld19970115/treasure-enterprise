package io.treasure.enm;
/**
 * 审核状态
 * @author 王丽娜
 */
public enum CouponEnm {
	/** 自己领取**/
    STATUS_GRANTS(1),
	/**满额自动领取**/
	STATUS_AGREE_YES(2),
	/**自动发放**/
	STATUS_AGREE_NO(3),
	/**可重复领取**/
	STATUS_REPEAT_YES(1),
	/**不可重复领取**/
	STATUS_REPEAT_NO(2),
	/**满减卷**/
	STATUS_TYPE_1(1),
	/**满赠卷**/
	STATUS_TYPE_2(2),
	/**优惠卷**/
	STATUS_TYPE_3(3);

	private int status;
	private CouponEnm(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}
}
