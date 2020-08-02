package io.treasure.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.treasure.dto.MerchantQrCodeDTO;
import io.treasure.entity.MerchantQrCodeEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;

@Mapper
public interface MerchantQrCodeDao extends BaseMapper<MerchantQrCodeEntity> {


    /***
     *根据商户ID添加商户二维码
     */
    void insertMerchantQrCodeByMerchantId(String qrUrl, Long merchantId, Date creatTime);


    /***
     * 根据商户ID查询商户二维码
     */
    MerchantQrCodeEntity getMerchantQrCodeByMerchantId(Long merchantId);



}
