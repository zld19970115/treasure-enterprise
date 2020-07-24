package io.treasure.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
/**
 * 本类用于对我们二维码进行参数的设定,生成我们的二维码：
 * @author kingwen
 */
public class QRCodeFactory {

    /**
     * 给生成的二维码添加中间的logo
     * @param matrixImage 生成的二维码
     * @param logUri      logo地址
     * @return            带有logo的二维码
     * @throws IOException logo地址找不到会有io异常
     */
    public BufferedImage setMatrixLogo(BufferedImage matrixImage,String logUri) throws IOException{
        /**
         * 读取二维码图片，并构建绘图对象
         */
        Graphics2D g2 = matrixImage.createGraphics();
        int matrixWidth = matrixImage.getWidth();
        int matrixHeigh = matrixImage.getHeight();

        /**
         * 读取Logo图片
         */
        BufferedImage logo = ImageIO.read(new File(logUri));

        //开始绘制图片
        g2.drawImage(logo,matrixWidth/5*2,matrixHeigh/5*2, matrixWidth/5, matrixHeigh/5, null);
        //绘制边框
        BasicStroke stroke = new BasicStroke(5,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
        // 设置笔画对象
        g2.setStroke(stroke);
        //指定弧度的圆角矩形
        RoundRectangle2D.Float round = new RoundRectangle2D.Float(matrixWidth/5*2, matrixHeigh/5*2, matrixWidth/5, matrixHeigh/5,20,20);
        g2.setColor(Color.white);
        // 绘制圆弧矩形
        g2.draw(round);

        //设置logo 有一道灰色边框
        BasicStroke stroke2 = new BasicStroke(1,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
        // 设置笔画对象
        g2.setStroke(stroke2);
        RoundRectangle2D.Float round2 = new RoundRectangle2D.Float(matrixWidth/5*2+2, matrixHeigh/5*2+2, matrixWidth/5-4, matrixHeigh/5-4,20,20);
        g2.setColor(new Color(128,128,128));
        g2.draw(round2);// 绘制圆弧矩形


        g2.dispose();
        matrixImage.flush() ;
        return matrixImage ;

    }



    /**
     * 创建我们的二维码图片
     * @param content            二维码内容
     * @param format             生成二维码的格式
     * @param outFileUri         二维码的生成地址
//     * @param logUri             二维码中间logo的地址
     * @param size               用于设定图片大小（可变参数，宽，高）
     * @throws IOException       抛出io异常
     * @throws WriterException   抛出书写异常
     */
    public static  BitMatrix CreatQrImage(String content) throws IOException, WriterException{

//二维码的宽高
        int width = 200;
        int height = 200;

        //其他参数，如字符集编码
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        //容错级别为H
        hints.put(EncodeHintType.ERROR_CORRECTION , ErrorCorrectionLevel.H);
        //白边的宽度，可取0~4
        hints.put(EncodeHintType.MARGIN , 0);

        BitMatrix bitMatrix = null;
        try {
            //生成矩阵，因为我的业务场景传来的是编码之后的URL，所以先解码
            bitMatrix = new MultiFormatWriter().encode(content,
                    BarcodeFormat.QR_CODE, width, height, hints);

            //bitMatrix = deleteWhite(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return bitMatrix;
    }
}