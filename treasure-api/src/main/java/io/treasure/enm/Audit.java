package io.treasure.enm;
/**
 * 审核状态
 * @author 王丽娜
 */
public enum Audit {
	/** 未审核**/
    STATUS_NO(1),
	/**已审核**/
	STATUS_AGREE_YES(2),
	/**审核未通过**/
	STATUS_AGREE_NO(3);

	private int status;
	private Audit(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}
}
