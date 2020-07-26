package com.genius.filemanage.common.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseUtils {
    public static void render(HttpServletResponse response, String contentType, String text){
        response.setContentType(contentType);
        try {
            response.getWriter().write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //发送json
    public static void renderJson(HttpServletResponse response, String text){
        render(response, "application/json;charset=utf-8", text);
    }
    //发送xml
    public static void renderXml(HttpServletResponse response, String text){
        render(response, "text/xml;charset=utf-8", text);
    }
    //发送text
    public static void renderText(HttpServletResponse response, String text){
        render(response, "text/plain;charset=utf-8", text);
    }
}
