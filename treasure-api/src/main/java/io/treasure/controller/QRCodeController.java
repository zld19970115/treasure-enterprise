package io.treasure.controller;


import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.utils.Result;
import io.treasure.entity.MerchantQrCodeEntity;
import io.treasure.entity.SysOssEntity;
import io.treasure.oss.cloud.OSSFactory;
import io.treasure.service.MerchantQrCodeService;
import io.treasure.service.SysOssService;
import io.treasure.utils.QRCodeFactory;
import oracle.jdbc.proxy.annotation.Post;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/QRCode")
@Api(tags="商户二维码")
public class QRCodeController {

    @Autowired
    private SysOssService sysOssService;

    @Autowired
    private MerchantQrCodeService merchantQrCodeService;

    @CrossOrigin
    @Login
    @PostMapping("createQRCode")
    @ApiOperation("生成商户二维码")
    @ApiImplicitParams({
            @ApiImplicitParam(name ="id", value = "商户id", paramType = "query",required = true, dataType="long"),
    })
    public Result<Map<String, Object>> createQRCode(@ApiIgnore @RequestParam Long id) throws IOException, WriterException {


        Map<String, Object> data = new HashMap<>(1);
        merchantQrCodeService.insertMerchantQrCodeByMerchantId(String.valueOf(id));
        return new Result<Map<String, Object>>().ok(data);
    }
    @GetMapping("createQRCodeForBm")
    @ApiOperation("生成业务员二维码")
    @ApiImplicitParams({
            @ApiImplicitParam(name ="id", value = "业务员id", paramType = "query",required = true, dataType="long"),
    })
    public Result<Map<String, Object>> createQRCodeForBm(@ApiIgnore @RequestParam Long id) throws IOException, WriterException {


        Map<String, Object> data = new HashMap<>(1);
        merchantQrCodeService.createQRCodeForBm(String.valueOf(id));
        return new Result<Map<String, Object>>().ok(data);
    }

    @GetMapping("createQRCodeForBm1")
    @ApiOperation("createQRCodeForBm1")
    public Result createQRCodeForBm1() throws IOException, WriterException {


        merchantQrCodeService.createQRCodeForBm1();
        return new Result<>().ok(1);
    }
    @GetMapping("createQRCodeForBm2")
    @ApiOperation("createQRCodeForBm2")
    public Result createQRCodeForBm2() throws IOException, WriterException {


        merchantQrCodeService.createQRCodeForBm2();
        return new Result<>().ok(1);
    }

    @CrossOrigin
    @Login
    @GetMapping("getQRCode")
    @ApiOperation("生成商户二维码")
    @ApiImplicitParams({
            @ApiImplicitParam(name ="id", value = "商户id", paramType = "query",required = true, dataType="long"),
    })
    public Result<MerchantQrCodeEntity> getQRCode(@ApiIgnore @RequestParam Long id) throws IOException, WriterException {

        MerchantQrCodeEntity merchantQrCodeByMerchantId = merchantQrCodeService.getMerchantQrCodeByMerchantId(id);
        return new Result<MerchantQrCodeEntity>().ok(merchantQrCodeByMerchantId);
    }









}
