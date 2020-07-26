package com.genius.filemanage.common.utils;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {

    private static Logger log = LoggerFactory.getLogger(ImageUtils.class);

    /**
     * 直接指定压缩后的宽高：
     * (先保存原文件，再压缩、上传)
     * 壹拍项目中用于二维码压缩
     * @param oldFile 要进行压缩的文件全路径
     * @param width 压缩后的宽
     * @param height 压缩后的高
     * @param quality 图片质量
     * @return 返回压缩后的文件的全路径
     */
    public static String compressAspectImage(String oldFile, int width, int height, float quality, String fileName) {
        if (oldFile == null) {
            return null;
        }
        File imgFile = new File(oldFile);
        String newImage = null;
        if (imgFile.exists()) {
            try {
                /**对服务器上的临时文件进行处理 */
                Image srcFile = ImageIO.read(new File(oldFile));
                int w = ((BufferedImage) srcFile).getWidth();
                int h = ((BufferedImage) srcFile).getHeight();
                if (width > w) {
                    width = w;
                }
                if (height > h) {
                    height = h;
                }
                /** 宽,高设定 */
                BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                tag.getGraphics().drawImage(srcFile, 0, 0, width, height, null);
                /** 压缩后的文件名 */
                String p = imgFile.getPath();
                newImage = p.substring(0, p.lastIndexOf(File.separator)) + File.separator + fileName;
                /** 压缩之后临时存放位置 */
                FileOutputStream out = new FileOutputStream(newImage);
                JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
                JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(tag);
                /** 压缩质量 */
                jep.setQuality(quality, true);
                encoder.encode(tag, jep);
                out.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException("文件未找到");
            } catch (IOException e) {
                throw new RuntimeException("IO流异常");
            }
        } else {
            throw new RuntimeException("文件未找到");
        }
        return newImage;
    }

    /**
     * 裁剪固定宽高比图片
     * @param oldFile 源文件
     * @param aspectRatio 宽高比
     * @return
     */
    public static String cutAspectRatioImage(String oldFile, double aspectRatio, float quality, String fileName) {
        if (oldFile == null) {
            return null;
        }
        File imgFile = new File(oldFile);
        String cutImage = "";
        if (imgFile.exists()) {
            try {
                Image srcFile = ImageIO.read(imgFile);
                int width = ((BufferedImage) srcFile).getWidth();
                int height = ((BufferedImage) srcFile).getHeight();
                // 1.裁剪成临时文件
                BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                bufferedImage.getGraphics().drawImage(srcFile, 0, 0, width, height, null);
                int startX = 0;
                int startY = 0;
                int endX = width;
                int endY = height;
                // TODO: 获取短边
                // 1.假如图片的宽高比比需要的宽高比要大，则高是短边，按高为参照计算宽
                if ((double)width / height >= aspectRatio)
                {
                    int w = new Double(height * aspectRatio).intValue();
                    int offsetWidth = (width - w) / 2;
                    startX = offsetWidth;
                    endX = endX - offsetWidth;
                }
                // 2.假如图片的宽高比比需要的宽高比要小，则宽是短边，按宽为参照计算高
                else
                {
                    int h = new Double(width / aspectRatio).intValue();
                    int offsetHeight = (height - h) / 2;
                    startY = offsetHeight;
                    endY = endY - offsetHeight;
                }
                if (startX == -1) {
                    startX = 0;
                }
                if (startY == -1) {
                    startY = 0;
                }
                if (endX == -1) {
                    endX = width - 1;
                }
                if (endY == -1) {
                    endY = height - 1;
                }
                String p = imgFile.getPath();
                cutImage = p.substring(0, p.lastIndexOf(File.separator)) + File.separator + fileName;
                BufferedImage result = new BufferedImage(endX - startX, endY - startY, Image.SCALE_SMOOTH);
                for (int x = startX; x < endX; ++x) {
                    for (int y = startY; y < endY; ++y) {
                        int rgb = bufferedImage.getRGB(x, y);
                        result.setRGB(x - startX, y - startY, rgb);
                    }
                }
                FileOutputStream cutOut = new FileOutputStream(cutImage);
                JPEGImageEncoder cutEncoder = JPEGCodec.createJPEGEncoder(cutOut);
                JPEGEncodeParam cutJep = JPEGCodec.getDefaultJPEGEncodeParam(result);
                cutJep.setQuality(quality, true);
                cutEncoder.encode(result, cutJep);
                cutOut.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException("文件未找到");
            } catch (IOException e) {
                throw new RuntimeException("IO流异常");
            }
        } else {
            throw new RuntimeException("文件未找到");
        }
        return cutImage;
    }
}
