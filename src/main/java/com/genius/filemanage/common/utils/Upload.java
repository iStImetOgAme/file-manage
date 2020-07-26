package com.genius.filemanage.common.utils;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


public class Upload {

    /**
     * 上传文件方法
     * @param stream 输入流
     * @param file 文件对象
     * @param serverPath 服务器路径
     * @param path 文件路径
     * @return
     */
    public static String uploadFile(InputStream stream, MultipartFile file, String serverPath, String path) {
        // 文件名称生成策略（日期时间+uuid ）
        UUID uuid = UUID.randomUUID();
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String formatDate = format.format(d);
        // 获取文件的扩展名
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        // 文件名
        String fileName = formatDate + "-" + uuid + "." + extension;
        //相对路径
        String relaPath = path + fileName;

        String a = serverPath + path.substring(0, path.lastIndexOf("/"));
        String realPath = serverPath + relaPath;
        File file2 = new File(a);
        if (!file2.exists()) {
            boolean mkdirs = file2.mkdirs();
            System.out.println(mkdirs);
        }
        try {
            FileOutputStream fs = new FileOutputStream(realPath);
            byte[] buffer = new byte[1024 * 1024];
            int bytesum = 0;
            int byteread = 0;
            while ((byteread = stream.read(buffer)) != -1) {
                bytesum += byteread;
                fs.write(buffer, 0, byteread);
                fs.flush();
            }
            fs.close();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName + ";" + relaPath + ";" + realPath;
    }
}
