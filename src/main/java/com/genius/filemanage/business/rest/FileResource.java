package com.genius.filemanage.business.rest;

import com.genius.filemanage.business.entity.FileResult;
import com.genius.filemanage.business.service.FileService;
import com.genius.filemanage.common.entity.ResponseResult;
import com.genius.filemanage.common.entity.ZipEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileResource {
    private static Logger logger = LoggerFactory.getLogger(FileResource.class);

    @Autowired
    private FileService fileService;

    /**
     * 上传文件的方法
     * @param request
     * @param path 存放文件的文件夹相对路径
     * @param needTimeStamp 是否需要在文件名后加时间戳
     * @param type 特殊情况需要传文件类型
     * @return
     * @throws UnsupportedEncodingException
     */
    @PostMapping("/upload")
    public ResponseResult<FileResult> upload(
            HttpServletRequest request,
            @RequestParam(value = "path", required = false, defaultValue = "/") String path,
            @RequestParam(value = "needTimeStamp", required = false, defaultValue = "1") String needTimeStamp,
            @RequestParam(value = "type", required = false) String type
    ) throws UnsupportedEncodingException {

        request.setCharacterEncoding("utf-8");

        StandardServletMultipartResolver resolver = new StandardServletMultipartResolver();
        MultipartHttpServletRequest multipartRequest = resolver.resolveMultipart(request);

        FileResult result = fileService.upload(multipartRequest, path, needTimeStamp, type);

        return ResponseResult.success(result);
    }

    /**
     * 根据文件地址下载文件
     * @param path
     */
    @GetMapping("/download")
    public void download(@RequestParam("path") String path, HttpServletRequest req, HttpServletResponse res) throws IOException {
        fileService.download(path, req, res);
    }

    /**
     * 根据路径下载(pathVariable方式不能用/\这些特殊字符，所以特殊处理一下)
     * @param req
     * @param res
     */
    @GetMapping("/download/**")
    public void downloadByPathVariable(HttpServletRequest req, HttpServletResponse res) {
        try {
            fileService.download(req, res);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 多文件压缩下载
     * @param zipEntrys
     * @param req
     * @param res
     */
    @PostMapping("/download-zip")
    public void downloadZip(@RequestBody List<ZipEntry> zipEntrys, HttpServletRequest req, HttpServletResponse res) {
        try {
            fileService.downloadZip(zipEntrys, req, res);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    /**
     * 多文件压缩下载
     * @param req
     * @param res
     */
    @GetMapping("/download-zip")
    public void downloadZipGet(HttpServletRequest req, HttpServletResponse res) {
        try {
            List<ZipEntry> zipEntries = new ArrayList<>();
            ZipEntry ze1 = new ZipEntry();
            ze1.setEntryName("/AAA/BBB/CCC/doge.jpg");
            ze1.setEntryPath("/test/aaa/aaa/doge_1555332990583.jpg");

            ZipEntry ze2 = new ZipEntry();
            ze2.setEntryName("/AAA/BBB/鸟哥Linux私房菜.pdf");
            ze2.setEntryPath("/test/aaa/aaa/鸟哥Linux私房菜_1555332991013.pdf");

            ZipEntry ze3 = new ZipEntry();
            ze3.setEntryName("/AAA/BBB/CCC/DDBBB.doge.jpg");
            ze3.setEntryPath("/test/doge_1555332615490.jpg");
            zipEntries.add(ze1);
            zipEntries.add(ze2);
            zipEntries.add(ze3);

            fileService.downloadZip(zipEntries, req, res);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

}
