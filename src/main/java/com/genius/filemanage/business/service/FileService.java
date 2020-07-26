package com.genius.filemanage.business.service;


import com.alibaba.fastjson.JSON;
import com.genius.filemanage.business.entity.FileResult;
import com.genius.filemanage.common.entity.ZipEntry;
import com.genius.filemanage.common.utils.CompressUtils;
import com.genius.filemanage.common.utils.FileUtils;
import com.genius.filemanage.common.utils.PathUtils;
import com.genius.filemanage.config.GlobalSettingProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileService {
    private static Logger logger = LoggerFactory.getLogger(FileService.class);


    /**
     * 文件上传的方法
     * @param multipartHttpServletRequest 包含文件的请求
     * @param path 文件保存的文件夹的相对路径
     * @param needTimeStamp 是否需要在文件名后添加时间戳
     * @param type 特殊情况添加文件类型
     * @return 文件上传包装类
     */
    public FileResult upload(MultipartHttpServletRequest multipartHttpServletRequest, String path, String needTimeStamp, String type) throws UnsupportedEncodingException {
        logger.info("-->upload Request Address : {}", multipartHttpServletRequest.getRemoteAddr());
        logger.info("-->upload Begin. Path : {}", URLDecoder.decode(path, "UTF-8"));
        String[] paths = path.split("/");
        String relativeDirPath = "";
        for (String p : paths) {
            if (!"".equals(p.trim())) {
                relativeDirPath += com.genius.filemanage.common.utils.StringUtils.trimRight(p) + "/";
            }
        }
        if (!"0".equals(needTimeStamp)) {
            needTimeStamp = "1";
        }

        logger.info("-->upload Finish. Path : {}", relativeDirPath);

        return FileUtils.upload(multipartHttpServletRequest, relativeDirPath, "1".equals(needTimeStamp), type);
    }

    /**
     * 用路径参数下载
     * @param path 路径
     * @param req
     * @param res
     * @throws UnsupportedEncodingException
     */
    public void download(String path, HttpServletRequest req, HttpServletResponse res) throws UnsupportedEncodingException {
        logger.info("-->downloadByPath Request Address : {}", req.getRemoteAddr());
        logger.info("-->downloadByPath Begin. Path : {}", path);
        if (path == null || "".equals(path)) return;

        String filePathSource = GlobalSettingProperties.globalFilePath + "/" + path;

        java.io.File file = new java.io.File(filePathSource);
        if(!file.exists()){
            FileUtils.downloadFileNotFound(req, res);
            return;
        }

        String fileName = "";
        if (path.indexOf("/") > -1) {
            fileName = path.substring(path.lastIndexOf("/") + 1);
        } else {
            fileName = path;
        }

        FileUtils.download(file, fileName, req, res);
        logger.info("-->downloadByPath Finish. Path : {}", path);
    }

    /**
     * 用路径的方式下载
     * @param req
     * @param res
     * @throws IOException
     */
    public void download(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String path = (String) req.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        path = path.replace("/file/download", "");
        // 前端调用需要编码两次，处理#等特殊字符
        path = URLDecoder.decode(path, "UTF-8");
        logger.info("-->downloadByPathVariable Request Address : {}", req.getRemoteAddr());
        logger.info("-->downloadByPathVariable Begin. Path : {}", path);
        if (path == null || "".equals(path)) return;
        String filePath = GlobalSettingProperties.globalFilePath + "/" + path;

        java.io.File file = new java.io.File(filePath);
        if(!file.exists()){
            FileUtils.downloadFileNotFound(req, res);
            return;
        }

        String fileName = "";
        if (path.indexOf("/") > -1) {
            fileName = path.substring(path.lastIndexOf("/") + 1);
        } else {
            fileName = path;
        }
        if (fileName.indexOf(".") > 0) {
            String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (GlobalSettingProperties.globalVideoType.indexOf("|" + fileType.toLowerCase() + "|") > -1) {
                FileUtils.previewVideo(file, res);
            } else {
                FileUtils.download(file, fileName, req, res);
            }
        } else {
            FileUtils.download(file, fileName, req, res);
        }

        logger.info("-->downloadByPathVariable Finish. Path : {}", path);
    }

    /**
     * 压缩文件下载
     * @param zipEntries
     * @param req
     * @param res
     * @throws IOException
     */
    public void downloadZip(List<ZipEntry> zipEntries, HttpServletRequest req, HttpServletResponse res) {
        logger.info("-->downloadByZip Begin. Paths : {}", JSON.toJSON(zipEntries));
        // 文件公共的父级目录
        String commonPrefixPath = PathUtils.prefix(zipEntries.stream().map(
                item -> item.getEntryName()
        ).collect(Collectors.toList()));

        commonPrefixPath = commonPrefixPath.substring(0, commonPrefixPath.lastIndexOf("/"));

        if (StringUtils.isEmpty(commonPrefixPath)) {
            commonPrefixPath = "/download";
        } else {
            for (ZipEntry entry : zipEntries) {
                entry.setEntryName(entry.getEntryName().substring(commonPrefixPath.lastIndexOf("/") + 1));
            }
        }

        // 压缩包的名称用公共目录的最后一级
        String zipName = commonPrefixPath.substring(commonPrefixPath.lastIndexOf("/")).replace("/", "");

        java.io.File file = null;
        String path = "";
        zipName = zipName + ".zip";
        String zipFilePath = GlobalSettingProperties.globalFilePath + "/" + zipName;
        try {
            path = CompressUtils.compress(zipEntries, zipFilePath, zipEntries.size() + " files");
            file = new java.io.File(path);
            if (!file.exists()) {
                return;
            }
            FileUtils.download(file, zipName, req, res);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 下载完成后删除压缩包
            if (file.exists()) {
                file.delete();
                logger.info("-->downloadZip Delete exist zip files. Path : {}", path);
            }
        }

        logger.info("-->downloadByZip Finish. Paths : {}", JSON.toJSON(zipEntries));
    }
}
