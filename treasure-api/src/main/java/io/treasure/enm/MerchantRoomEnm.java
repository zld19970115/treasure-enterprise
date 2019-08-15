package io.treasure.enm;
/**
 * 包房状态
 * @author 王丽娜
 */
public enum MerchantRoomEnm {
	/** 包房**/
    TYPE_ROOM(1),
	/**桌**/
	TYPE_DESK(2),
	/**未使用**/
	STATE_USE_NO(0),
	/**使用**/
	STATE_USE_YEA(1);
	private int type;
	private MerchantRoomEnm(int type) {
		this.type = type;
	}
	public int getType() {
		return type;
	}
}
