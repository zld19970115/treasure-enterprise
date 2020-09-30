package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.MerchantCouponDao;
import io.treasure.dao.MerchantQrCodeDao;
import io.treasure.dto.MerchantCouponDTO;
import io.treasure.dto.MerchantQrCodeDTO;
import io.treasure.entity.BusinessManagerEntity;
import io.treasure.entity.MerchantCouponEntity;
import io.treasure.entity.MerchantQrCodeEntity;
import io.treasure.oss.cloud.OSSFactory;
import io.treasure.service.BusinessManagerService;
import io.treasure.service.MerchantCouponService;
import io.treasure.service.MerchantQrCodeService;
import io.treasure.service.SysOssService;
import io.treasure.utils.QRCodeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

/**
 * 商户二维码
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-12
 */
@Service
public class MerchantQrCodeServiceImpl extends CrudServiceImpl<MerchantQrCodeDao, MerchantQrCodeEntity, MerchantQrCodeDTO> implements MerchantQrCodeService {

    @Autowired
    private BusinessManagerService businessManagerService;
    @Override
    public QueryWrapper<MerchantQrCodeEntity> getWrapper(Map<String, Object> params) {
        return null;
    }


    @Override
    public void insertMerchantQrCodeByMerchantId(String merchantId) throws IOException, WriterException {

        // 设置响应流信息
        QRCodeFactory qrCodeFactory = new QRCodeFactory();
        //二维码内容
//        String content = ("https://jubaoapp.com:8443/treasure-api/merchant/getById?id="+merchantId);
//        String content = ("https://jubaoapp.com:8443/treasure-api/merchant/getById?id="+merchantId);
        String content = ("https://jubaoapp.com:8443/treasure-api/pages/reserve/reserve?shopid=" + merchantId + "&type=1");
//
//        String content = ("https://jubaoapp.com:8888/pages/tabBar/personal/invest/invest?id=" + merchantId);


        //获取一个二维码图片
        System.out.println("https://api.jubaoapp.com:8443/treasure-api/pages/reserve/reserve?shopid=" + merchantId + "&type=1");
        BitMatrix bitMatrix = qrCodeFactory.CreatQrImage(content);
        ByteArrayOutputStream bs = new ByteArrayOutputStream();

        BufferedImage posterBufImage = MatrixToImageWriter.toBufferedImage(bitMatrix, new MatrixToImageConfig(Color.BLACK.getRGB(), Color.WHITE.getRGB()));
        ImageOutputStream imgOut = ImageIO.createImageOutputStream(bs);
        ImageIO.write(posterBufImage, "jpg", imgOut);
        final String IMAGE_SUFFIX = "jpg";
        InputStream inSteam = new ByteArrayInputStream(bs.toByteArray());
        String url = OSSFactory.build().uploadSuffix(inSteam, IMAGE_SUFFIX);
        System.out.println("二维码存储地址"+url);
        Date date = new Date();
        baseDao.insertMerchantQrCodeByMerchantId(url, Long.valueOf(merchantId),date);

    }

    @Override
    public void createQRCodeForBm(String bmID) throws IOException, WriterException {
        // 设置响应流信息
        QRCodeFactory qrCodeFactory = new QRCodeFactory();
        //二维码内容
//        String content = ("https://jubaoapp.com:8443/treasure-api/merchant/getById?id="+merchantId);
//        String content = ("https://jubaoapp.com:8443/treasure-api/merchant/getById?id="+merchantId);
        String content = ("https://jubaoapp.com:9527?id=" + bmID);
//
//        String content = ("https://jubaoapp.com:8888/pages/tabBar/personal/invest/invest?id=" + merchantId);


        //获取一个二维码图片
       // System.out.println("https://api.jubaoapp.com:8443/treasure-api/pages/reserve/reserve?shopid=" + merchantId + "&type=1");
        BitMatrix bitMatrix = qrCodeFactory.CreatQrImage(content);
        ByteArrayOutputStream bs = new ByteArrayOutputStream();

        BufferedImage posterBufImage = MatrixToImageWriter.toBufferedImage(bitMatrix, new MatrixToImageConfig(Color.BLACK.getRGB(), Color.WHITE.getRGB()));
        ImageOutputStream imgOut = ImageIO.createImageOutputStream(bs);
        ImageIO.write(posterBufImage, "jpg", imgOut);
        final String IMAGE_SUFFIX = "jpg";
        InputStream inSteam = new ByteArrayInputStream(bs.toByteArray());
        String url = OSSFactory.build().uploadSuffix(inSteam, IMAGE_SUFFIX);
        System.out.println("二维码存储地址"+url);

        BusinessManagerEntity businessManagerEntity = businessManagerService.selectById(Long.valueOf(bmID));
        businessManagerEntity.setIcon(url);
        businessManagerService.updateById(businessManagerEntity);
    }

    @Override
    public void createQRCodeForBm1() throws IOException, WriterException {

    }

    @Override
    public void createQRCodeForBm2() throws IOException, WriterException {

    }

    @Override
    public MerchantQrCodeEntity getMerchantQrCodeByMerchantId(Long merchantId) throws IOException, WriterException {
        MerchantQrCodeEntity merchantQrCodeByMerchantId = baseDao.getMerchantQrCodeByMerchantId(merchantId);
             if (merchantQrCodeByMerchantId==null){
                 insertMerchantQrCodeByMerchantId(String.valueOf(merchantId));
             }

        return baseDao.getMerchantQrCodeByMerchantId(merchantId);
    }
}
