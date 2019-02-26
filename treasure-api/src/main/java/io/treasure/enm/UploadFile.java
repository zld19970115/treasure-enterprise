package io.treasure.enm;
/**
 * 审核状态
 * @author 王丽娜
 */
public enum UploadFile {
	/**商户上传前缀**/
    MERCHANT_FILE("merchant"),
	/**商户分类上传前缀**/
	MERCHANT_CATEGORY_FILE("category"),
	/**商品上传前缀**/
	MERCHANT_GOODS_FILE("goods");

	private String status;
	private UploadFile(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
}
