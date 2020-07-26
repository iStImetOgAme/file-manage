package com.genius.filemanage.common.utils;

import com.alibaba.fastjson.JSON;
import com.genius.filemanage.business.entity.File;
import com.genius.filemanage.business.entity.FileResult;
import com.genius.filemanage.config.GlobalSettingProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class FileUtils {

    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * 上传文件的方法
     * @param multipartHttpServletRequest 包含文件的请求
     * @param relativeDirPath 文件保存的文件夹的相对路径
     * @param needTimeStamp 文件后面是否需要添加时间戳
     * @param type 文件类型(有时候需要制定文件类型)
     * @return
     */
    public static FileResult upload(MultipartHttpServletRequest multipartHttpServletRequest, String relativeDirPath, boolean needTimeStamp, String type) {

        List<File> success = new ArrayList<>();
        List<File> fail = new ArrayList<>();
        List<File> invalid = new ArrayList<>();

        // 文件保存的文件夹
        String absoluteDirPath = GlobalSettingProperties.globalFilePath;
        if(relativeDirPath != null && !"".equals(relativeDirPath)){
            absoluteDirPath = absoluteDirPath + "/" + relativeDirPath;
        }
        java.io.File dir = new java.io.File(absoluteDirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        MultiValueMap<String, MultipartFile> map = multipartHttpServletRequest.getMultiFileMap();

        if (map != null) {
            Iterator<String> iterator = map.keySet().iterator();
            String key;
            List<MultipartFile> multipartFiles;
            java.io.File targetFile;
            while (iterator.hasNext()) {

                key = iterator.next();
                multipartFiles = map.get(key);

                for (MultipartFile multipartFile : multipartFiles) {

                    String fileRealName = multipartFile.getOriginalFilename();
                    logger.info("-->upload fileRealName : {}", fileRealName);

                    // 有的浏览器上传文件OriginalFilename不是文件本身的name，需要特殊处理一下
                    int index = fileRealName.lastIndexOf("\\");
                    if (index > 0) {
                        fileRealName = fileRealName.substring(index + 1);
                    }

                    // 请求内的文件名
                    String fileName = "";
                    // 文件类型
                    String fileType = "";
                    // 文件保存在服务器的文件名
                    String fileSaveName = "";

                    if(fileRealName.contains(".") && fileRealName.lastIndexOf(".") < fileRealName.length()){
                        // 真实的文件名
                        fileName = fileRealName.substring(0, fileRealName.lastIndexOf("."));
                        fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
                        logger.info("-->upload fileName : {}", fileName);

                        // 文件类型
                        fileType = fileRealName.substring(fileRealName.lastIndexOf(".") + 1);

                    } else {
                        fileType = type;
                    }

                    // 是否需要文件
                    if (needTimeStamp) {
                        fileSaveName = fileName + ("".equals(fileName) ? "" : "_") + new Date().getTime() + "." + fileType;
                    } else {
                        fileSaveName = fileName + "." + fileType;
                    }

                    String relativeFilePath = "";
                    if(relativeDirPath != null && !"".equals(relativeDirPath)){
                        relativeFilePath = "/" + relativeDirPath + fileSaveName;
                    } else {
                        relativeFilePath = fileSaveName;
                    }
                    try {

                        if ("".equals(fileType) || !GlobalSettingProperties.globalInvalidType.contains(fileType.toLowerCase())) {

                            File file = new File();

                            // 新建目标文件，只有被流写入时才会真正存在
                            targetFile = new java.io.File(dir, fileSaveName);

                            // 只有一块，就直接拷贝文件内容
                            multipartFile.transferTo(targetFile);

                            // 生成缩略图和裁剪图
                            if (GlobalSettingProperties.globalThumbnailType.contains("|" + fileType.toLowerCase() + "|")) {
                                // 先裁剪生成宽高比固定的裁剪图
                                String tempImagePath = ImageUtils.cutAspectRatioImage
                                        (targetFile.getAbsolutePath(), 4.0 / 3.0, 1.0f, "temp_" + targetFile.getName());

                                // 按照比例需要的宽高缩放的缩略图
                                try {
                                    String thumbnailPath = ImageUtils.compressAspectImage
                                            (tempImagePath, 800, 600, 1.0f, "thumb_" + targetFile.getName());
                                    java.io.File thumbnailImage = new java.io.File(thumbnailPath);
                                    if (thumbnailImage.exists()) {
                                        String thumbnailRelativePath = thumbnailPath.replace("\\", "/").replace(GlobalSettingProperties.globalFilePath, "");
                                        file.setThumbnailPath(thumbnailRelativePath);
                                    }
                                } catch (RuntimeException e) {
                                    logger.info("-->upload Generate Thumbnail Image Failed, Error Message : {}", e.getLocalizedMessage());
                                    file.setThumbnailPath("");
                                }

                                // mini
                                try {
                                    String miniPath = ImageUtils.compressAspectImage
                                            (tempImagePath, 200, 150, 0.8f, "mini_" + targetFile.getName());
                                    java.io.File miniImage = new java.io.File(miniPath);
                                    if (miniImage.exists()) {
                                        String miniRelativePath = miniPath.replace("\\", "/").replace(GlobalSettingProperties.globalFilePath, "");
                                        file.setMiniThumbnailPath(miniRelativePath);
                                    }
                                } catch (RuntimeException e) {
                                    logger.info("-->upload Generate MiniThumbnail Image Failed, Error Message : {}", e.getLocalizedMessage());
                                    file.setMiniThumbnailPath("");
                                }

                                java.io.File tempFile = new java.io.File(tempImagePath);
                                if (tempFile.exists()) {
                                    tempFile.delete();
                                }
                            }

                            file.setName(fileRealName);
                            file.setType(fileType.toLowerCase());
                            file.setSize(multipartFile.getSize());
                            file.setPath(relativeFilePath);
                            success.add(file);
                        } else {
                            File invalidFile = new File();
                            invalidFile.setName(fileRealName);
                            invalidFile.setType(fileType.toLowerCase());
                            invalidFile.setSize(multipartFile.getSize());
                            invalid.add(invalidFile);
                        }
                    } catch (Exception e) {
                        File failFile = new File();
                        failFile.setName(fileRealName);
                        failFile.setSize(multipartFile.getSize());
                        failFile.setType(fileType.toLowerCase());
                        failFile.setErrMsg(e.getLocalizedMessage());
                        fail.add(failFile);
                    }
                }
            }
            logger.info("-->upload Success file : {}" , JSON.toJSONString(success));
            logger.info("-->upload Invalid file : {}" , JSON.toJSONString(invalid));
            logger.info("-->upload Fail file : {}" , JSON.toJSONString(fail));
        } else {
            logger.error("-->upload file is empty");
        }

        return FileResult.success(success, fail, invalid);
    }

    /**
     * 断点续传，下载文件
     * @param file 文件实体
     * @param fileName 文件名
     * @param req 请求
     * @param res 响应
     */
    public static void download(java.io.File file, String fileName, HttpServletRequest req, HttpServletResponse res) throws UnsupportedEncodingException {

        // get MIME type of the file
        String mimeType = "application/octet-stream";

        // set content attributes for the response
        res.setContentType(mimeType);
        // response.setContentLength((int) downloadFile.length());

        // set headers for the response
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", new String(fileName.getBytes(),"iso-8859-1"));
        res.setHeader(headerKey, headerValue);
        // 解析断点续传相关信息
        res.setHeader("Accept-Ranges", "bytes");
        long downloadSize = file.length();
        long fromPos = 0, toPos = 0;
        if (req.getHeader("Range") == null) {
            res.setHeader("Content-Length", downloadSize + "");
        } else {
            // 若客户端传来Range，说明之前下载了一部分，设置206状态(SC_PARTIAL_CONTENT)
            res.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            String range = req.getHeader("Range");
            String bytes = range.replaceAll("bytes=", "");
            String[] ary = bytes.split("-");
            fromPos = Long.parseLong(ary[0]);
            if (ary.length == 2) {
                toPos = Long.parseLong(ary[1]);
            }
            int size;
            if (toPos > fromPos) {
                size = (int) (toPos - fromPos);
            } else {
                size = (int) (downloadSize - fromPos);
            }
            res.setHeader("Content-Length", size + "");
            downloadSize = size;
        }
        // Copy the stream to the response's output stream.
        RandomAccessFile in = null;
        OutputStream out = null;
        try {
            in = new RandomAccessFile(file, "rw");
            // 设置下载起始位置
            if (fromPos > 0) {
                in.seek(fromPos);
            }
            // 缓冲区大小
            int bufLen = (int) (downloadSize < 2048 ? downloadSize : 2048);
            byte[] buffer = new byte[bufLen];
            int num;
            int count = 0; // 当前写到客户端的大小
            out = res.getOutputStream();
            while ((num = in.read(buffer)) > 0) {
                out.write(buffer, 0, num);
                count += num;
                //处理最后一段，计算不满缓冲区的大小
                if (downloadSize - count < bufLen) {
                    bufLen = (int) (downloadSize-count);
                    if(bufLen==0){
                        break;
                    }
                    buffer = new byte[bufLen];
                }
            }
            res.flushBuffer();
        } catch (IOException e) {
            logger.error("-->download error msg : {}", e.getMessage());
            e.printStackTrace();
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 预览视频文件
     * @param file
     * @param response
     * @throws UnsupportedEncodingException
     */
    public static void previewVideo(java.io.File file, HttpServletResponse response) throws UnsupportedEncodingException {
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            byte[] buffer = bos.toByteArray();
            response.setContentType("application/octet-stream");
            response.setContentLength(buffer.length);
            response.getOutputStream().write(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 描述 : 写入文件到磁盘. <br>
     *
     * @author liuxh
     * @date 2018年8月10日
     * @param inputStream 数据流
     * @param tempFile 临时文件
     */
    public static void writeFile(InputStream inputStream, java.io.File tempFile) {
        OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(tempFile));
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = (inputStream.read(bytes))) > 0) {
                outputStream.write(bytes, 0, len);
            }
        } catch (FileNotFoundException e) {
            logger.error("-->writeFile error msg : {}" , e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("-->writeFile error msg : {}" , e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                logger.error("-->writeFile error msg : {}" , e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 文件未找到时下载空文件
     * @param req
     * @param res
     */
    public static void downloadFileNotFound(HttpServletRequest req, HttpServletResponse res) {
        java.io.File tempFile = null;
        try {
            tempFile = java.io.File.createTempFile("empty", ".txt");
            FileUtils.download(tempFile, "文件未找到", req, res);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}
