package io.treasure.task.item;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 通运流--->文件将图片下载到本地业务--暂时停用
 */
@Service
public class DownloadPhotoService {

    int i = 0;
    public void downloadAndSave() throws Exception {

        download("http://image.jubaoapp.com/upload/20200908/83112b19d4904440917f22347240484a.jpg", "83112b19d4904440917f22347240484a.jpg","d:\\image\\");
    }

    public  void download(String urlString, String filename,String savePath) throws Exception {
        URL url = new URL(urlString);
        URLConnection con = url.openConnection();

        con.setConnectTimeout(5*1000);
        InputStream is = con.getInputStream();

        byte[] bs = new byte[1024];
        int len;
        File sf=new File(savePath);
        if(!sf.exists()){
            sf.mkdirs();
        }
        String extensionName = filename.substring(filename.lastIndexOf(".") +1);
        String newFileName = generatorPhotoshopId()+ "." + extensionName;
        OutputStream os = new FileOutputStream(sf.getPath()+"\\"+filename);
        while ((len = is.read(bs)) != -1) {
            os.write(bs, 0, len);
        }
        os.close();
        is.close();
    }
    public String generatorPhotoshopId(){
        String prefix = "qrCode"+i;
        i++;
        return prefix;
    }

}
