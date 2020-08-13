package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.common.utils.Result;
import io.treasure.dao.CardMakeDao;
import io.treasure.dao.UserCardDao;
import io.treasure.dto.CardInfoDTO;
import io.treasure.dto.CardMakeDTO;
import io.treasure.entity.ActivityEntity;
import io.treasure.entity.CardInfoEntity;
import io.treasure.entity.CardMakeEntity;
import io.treasure.oss.cloud.OSSFactory;
import io.treasure.service.CardMakeService;
import io.treasure.service.UserCardService;
import io.treasure.utils.QRCodeFactory;
import io.treasure.utils.RandomPwd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class CardMakeServiceImpl extends CrudServiceImpl<CardMakeDao, CardMakeEntity, CardMakeDTO> implements CardMakeService {

    @Autowired
    private CardMakeDao dao;

    @Autowired
    private UserCardService userCardService;

    @Override
    public QueryWrapper<CardMakeEntity> getWrapper(Map<String, Object> params) {
        return null;
    }

    @Override
    public PageData<CardMakeDTO> pageList(Map<String, Object> params) {
        PageHelper.startPage(Integer.parseInt(params.get("page")+""),Integer.parseInt(params.get("limit")+""));
        Page<CardMakeDTO> page = (Page) dao.pageList(params);
        return new PageData<>(page.getResult(),page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result makeCard(CardMakeEntity dto) throws IOException, WriterException {
        if(dto.getCardNum() == null || dto.getCardMoney() == null) {
            return new Result<>().error("参数不正确");
        }
        int batch = dao.queryMax();
        dto.setCreateDate(new Date());
        dto.setCardBatch(batch);
        if(dao.insert(dto) > 0) {
            List<CardInfoEntity> list =new ArrayList<>();
            for(int n = 0;n < dto.getCardNum();n++){
                CardInfoEntity info = new CardInfoEntity();

                info.setBatch(batch);
                info.setMoney(dto.getCardMoney());
                info.setStatus(1);
                info.setType(dto.getCardType());
                info.setPassword(RandomPwd.getRandomPwd(16));
                list.add(info);
            }
            userCardService.insertBatch(list);

            List<CardInfoDTO> cardInfoDTOS = userCardService.selectByNoCode();
            for (CardInfoDTO cardInfoDTO : cardInfoDTOS) {
                String s = insertMerchantQrCodeByMerchantId(cardInfoDTO.getId());

                userCardService.updateCode(s,cardInfoDTO.getId());
            }


        }
        return new Result<>().ok("操作成功");
    }
    public String insertMerchantQrCodeByMerchantId(Long id) throws IOException, WriterException {

        // 设置响应流信息
        QRCodeFactory qrCodeFactory = new QRCodeFactory();
        //二维码内容
//        String content = ("https://jubaoapp.com:8443/treasure-api/merchant/getById?id="+merchantId);
//        String content = ("https://jubaoapp.com:8443/treasure-api/merchant/getById?id="+merchantId);
//        String content = ("https://jubaoapp.com:8443/treasure-api/pages/reserve/reserve?shopid=" + merchantId + "&type=1");
//
        String content = ("https://jubaoapp.com:8888?id=" + id);


        //获取一个二维码图片
        System.out.println("https://jubaoapp.com:8888?id=" + id);
        BitMatrix bitMatrix = qrCodeFactory.CreatQrImage(content);
        ByteArrayOutputStream bs = new ByteArrayOutputStream();

        BufferedImage posterBufImage = MatrixToImageWriter.toBufferedImage(bitMatrix, new MatrixToImageConfig(Color.BLACK.getRGB(), Color.WHITE.getRGB()));
        ImageOutputStream imgOut = ImageIO.createImageOutputStream(bs);
        ImageIO.write(posterBufImage, "jpg", imgOut);
        final String IMAGE_SUFFIX = "jpg";
        InputStream inSteam = new ByteArrayInputStream(bs.toByteArray());
        String url = OSSFactory.build().uploadSuffix(inSteam, IMAGE_SUFFIX);
        System.out.println("二维码存储地址"+url);
return url;
    }
}
