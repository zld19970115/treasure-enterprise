package io.treasure.enm;
/**
 * 角色
 * @author 王丽娜
 */
public enum Role {
	/** 超级管理园*/
    ADMIN("1");

	private String status;
	private Role(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
}
