package io.treasure.service;


import com.google.zxing.WriterException;
import io.treasure.common.service.CrudService;
import io.treasure.dto.MerchantQrCodeDTO;
import io.treasure.dto.MerchantRoomParamsDTO;
import io.treasure.entity.MerchantQrCodeEntity;
import io.treasure.entity.MerchantRoomParamsEntity;

import java.io.IOException;
import java.util.Date;

/**
 * 商户二维码
 */
public interface MerchantQrCodeService extends CrudService<MerchantQrCodeEntity, MerchantQrCodeDTO> {

    /***
     *根据商户ID添加商户二维码
     */
    void insertMerchantQrCodeByMerchantId(String merchantId) throws IOException, WriterException;


    /***
     * 根据商户ID查询商户二维码
     */
    MerchantQrCodeEntity getMerchantQrCodeByMerchantId(Long merchantId) throws IOException, WriterException;


}
