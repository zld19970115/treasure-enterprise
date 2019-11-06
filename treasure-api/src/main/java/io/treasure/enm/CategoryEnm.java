package io.treasure.enm;
/**
 * 菜品分类
 * @author 王丽娜
 */
public enum CategoryEnm {
	/** 推荐**/
	SHOW_IN_COMMEND_YES(1),
	/**不推荐**/
    SHOW_IN_COMMEND_NO(0);


	private int status;
	private CategoryEnm(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}
}
