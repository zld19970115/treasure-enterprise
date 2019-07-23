package io.treasure.enm;
/**
 * 通用状态
 * @author 王丽娜
 */
public enum Common {
	/** 正常**/
    STATUS_ON(1),
	/**禁用**/
	STATUS_OFF(0),
	/**删除**/
	STATUS_DELETE(9);

	private int status;
	private Common(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}
}
