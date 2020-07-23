package io.treasure.service.impl;

import io.swagger.annotations.ApiOperation;
import io.treasure.common.constant.Constant;
import io.treasure.common.exception.ErrorCode;
import io.treasure.common.utils.Result;
import io.treasure.entity.SysOssEntity;
import io.treasure.oss.cloud.OSSFactory;
import io.treasure.service.QRCodeService;
import io.treasure.service.SysOssService;
import io.treasure.utils.QRCodeUtil;
import javassist.NotFoundException;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.schema.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.util.*;

@Service
public class QRcodeServiceImpl implements QRCodeService {


    @Autowired
    private SysOssService sysOssService;

    private final static String KEY = Constant.CLOUD_STORAGE_CONFIG_KEY;

    /**
     *
     * https://jubaoapp.com:8443/treasure-api/sharing_activitys/Readed?client_id=1272363843756167170&sharingId=777
     */
    public  String generateQrAndUrl(String url,@NotNull Map<String,String> params) throws Exception{
        // 存放在二维码中的内容
        String content = null;
        StringBuilder sb = new StringBuilder();
        if(params != null)
        for(Map.Entry<String,String> entry:params.entrySet()){
            sb.append("&"+entry.getKey()+"="+entry.getValue());
        }
        content = sb.toString().trim();
        if(content != null){
            if(content.length()>3)
            content = url +"?"+ content.substring(1);
        }
        System.out.println("内容为："+content);
        String iconPath = "D:/qrCode/jubao_logo.png";//图标d:\\qrcode\\jubao_log.png
        String tmpFilePath = "D:/qrCode/sharing_activity_code.jpg";//临时二维码



        String osName = System.getProperty("os.name");
        System.out.println("osName:"+osName);
        if(!osName.toLowerCase().startsWith("win")){
            iconPath = "/www/qrcode/jubao_logo.png";
            tmpFilePath = "/www/qrcode/sharing_activity_code.jpg";
        }

        File tmpFile = new File(tmpFilePath);
        //生成二维码
        QRCodeUtil.encode(content, iconPath, tmpFilePath, true);
        //String str = QRCodeUtil.decode(tmpFilePath);
        String qrURL = OSSFactory.build().uploadSuffix(getBytesFromFile(tmpFile), "jpg");
        System.out.println("generator QRCode url:"+qrURL);
        return qrURL;
    }


    // 返回一个byte数组
	public byte[] getBytesFromFile(File file) throws IOException {
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
