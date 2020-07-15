package io.treasure.service.impl;

import io.swagger.annotations.ApiOperation;
import io.treasure.common.constant.Constant;
import io.treasure.common.exception.ErrorCode;
import io.treasure.common.utils.Result;
import io.treasure.entity.SysOssEntity;
import io.treasure.oss.cloud.OSSFactory;
import io.treasure.service.SysOssService;
import io.treasure.utils.QRCodeUtil;
import javassist.NotFoundException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class QRcodeServiceImpl {


    @Autowired
    private SysOssService sysOssService;

    private final static String KEY = Constant.CLOUD_STORAGE_CONFIG_KEY;


    public static void main(String[] args) throws Exception {
        // 存放在二维码中的内容
        String text = "http://api.jubaoapp.com";

        // 嵌入二维码的图片路径
        String imgPath = "D:/qrCode/dog.jpg";
        // 生成的二维码的路径及名称
        String destPath = "D:/qrCode/jam.jpg";
        //生成二维码
        QRCodeUtil.encode(text, imgPath, destPath, true);
        // 解析二维码
        String str = QRCodeUtil.decode(destPath);
        // 打印出解析出的内容

        File file = new File("D:/qrCode/jam.jpg");


        //上传文件
        String url = OSSFactory.build().uploadSuffix(getBytesFromFile(file), "jpg");

        System.out.println("url......"+url);


    }


    // 返回一个byte数组
	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);// 获取文件大小
		long lengths = file.length();
		System.out.println("lengths = " + lengths);
		if (lengths > Integer.MAX_VALUE) {
			// 文件太大，无法读取
			throw new IOException("File is to large "+file.getName());
		}
		// 创建一个数据来保存文件数据
		byte[] bytes = new byte[(int)lengths];// 读取数据到byte数组中
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
			offset += numRead;
		}
		// 确保所有数据均被读取
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "+file.getName());
		}
		// Close the input stream and return bytes
		is.close();
		return bytes;
        }


    public String generateQRCodeUri() throws FileNotFoundException {
       return null;
    }

    //生成二维码内容字符串
    public String initContentOfQR(String comboContent){
        return comboContent;
    }
    //生成二维码内容字符串
    public String initContentOfQR(String content,Map<String,String> params){
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String,String> entry :params.entrySet()){
            sb.append("&"+entry.getKey()+"="+entry.getValue());
        }
        String requestParams = sb.toString();
        if(!requestParams.equals(null))
            return content+"?"+requestParams.substring(1);
        return content;
    }
}
