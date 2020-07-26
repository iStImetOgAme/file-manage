package com.genius.filemanage.business.rest;

import com.baidu.ueditor.ActionEnter;
import com.genius.filemanage.common.utils.ResponseUtils;
import com.genius.filemanage.common.utils.Upload;
import net.sf.json.JSONObject;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

@Controller
@RequestMapping(value = "/ueditor")
/**
 * UEditor文件上传
 */
public class UEditorResource {
    //后台图片保存地址
    @Value(value = "${global.setting.ueditor}")
    private String ueditor;

    @Value(value = "${global.setting.file.path}")
    private String uploadHost;

    @Value(value = "${server.servlet.context-path}")
    private String contextPath;

    @ResponseBody
    @RequestMapping(value="/ueditorUpload", method={RequestMethod.GET, RequestMethod.POST})

    public void editorUpload(HttpServletRequest request, HttpServletResponse response, String action) throws Exception {
        response.setContentType("application/json");
        //  String rootPath = request.getSession().getServletContext().getRealPath("/");
        // 获取项目根路径
        String rootPath = ClassUtils.getDefaultClassLoader().getResource("").toURI().getPath();
        try {
            if("config".equals(action))
            {    //如果是初始化
                String exec = new ActionEnter(request, rootPath).exec();
                PrintWriter writer = response.getWriter();
                writer.write(exec);
                writer.flush();
                writer.close();
            }
            //如果是上传图片、视频、和其他文件
            else if ("uploadimage".equals(action) || "uploadvideo".equals(action) || "uploadfile".equals(action))
            {
                try {
                    //MultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
                    MultipartHttpServletRequest Murequest = (MultipartHttpServletRequest)request;
                    Map<String, MultipartFile> files = Murequest.getFileMap();//得到文件map对象
                    // 实例化一个jersey
                    for(MultipartFile pic: files.values()){
                        JSONObject jo = new JSONObject();
                        long size = pic.getSize();    //文件大小
                        String originalFilename = pic.getOriginalFilename();  //原来的文件名
                        int index = originalFilename.lastIndexOf("\\");
                        if (index > 0) {
                            originalFilename = originalFilename.substring(index + 1);
                        }
                        String uploadInfo = Upload.uploadFile(pic.getInputStream(), pic, uploadHost, ueditor);
                        if(!"".equals(uploadInfo)){    //如果上传成功
                            String[] infoList = uploadInfo.split(";");
                            jo.put("state", "SUCCESS");
                            jo.put("original", originalFilename);//原来的文件名
                            jo.put("size", size); //文件大小
                            jo.put("title", infoList[1]); //随意，代表的是鼠标经过图片时显示的文字
                            jo.put("type", FilenameUtils.getExtension(pic.getOriginalFilename())); //文件后缀名
                            //jo.put("url", infoList[2]);//这里的url字段表示的是上传后的图片在图片服务器的完整地址（http://ip:端口/***/***/***.jpg）
                            jo.put("url", contextPath + "/ueditor/jsp/upload?filePath="+ infoList[2]);//这里的url字段表示的是上传后的图片在图片服务器的完整地址
                        }else{    //如果上传失败
                        }
                        ResponseUtils.renderJson(response, jo.toString());
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
        }
    }

    /**
     * 下载上传的文件
     * @param filePath
     * @param response
     * @throws IOException
     */
    @RequestMapping(value="/jsp/upload",method= RequestMethod.GET)
    public void download(@RequestParam String filePath, HttpServletResponse response) throws IOException {
        File file=new File(filePath);
        OutputStream writer= response.getOutputStream();
        FileInputStream fileInputStream=new FileInputStream(file);
        byte[] fileBt=new byte[1024];
        while (fileInputStream.read(fileBt)!=-1){
            writer.write(fileBt);
            fileBt=new byte[1024];
        }
        writer.close();
        fileInputStream.close();
    }

}
