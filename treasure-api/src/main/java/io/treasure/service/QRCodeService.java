package io.treasure.service;

import io.treasure.utils.QRCodeUtil;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.util.Map;

public interface QRCodeService {

    String generateQrAndUrl(String url,@NotNull Map<String,String> params) throws Exception;

    // 返回一个byte数组
    byte[] getBytesFromFile(File file) throws IOException;


    String generateQRCodeUri() throws FileNotFoundException;

    //生成二维码内容字符串
    String initContentOfQR(String comboContent);
    //生成二维码内容字符串
    String initContentOfQR(String content,Map<String,String> params);
}
