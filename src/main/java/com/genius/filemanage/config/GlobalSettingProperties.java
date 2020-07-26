package com.genius.filemanage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 配置文件类
 */
@Component
public class GlobalSettingProperties {

    public static String globalFilePath;
    public static String globalThumbnailType;
    public static String globalInvalidType;
    public static String globalVideoType;

    @Value("${global.setting.file.path}")
    public void setGlobalFilePath(String globalFilePath) {
        GlobalSettingProperties.globalFilePath = globalFilePath;
    }

    @Value("${global.setting.thumbnail.type}")
    public void setGlobalThumbnailType(String globalThumbnailType) {
        GlobalSettingProperties.globalThumbnailType = globalThumbnailType;
    }

    @Value("${global.setting.invalid.type}")
    public void setGlobalInvalidType(String globalInvalidType) {
        GlobalSettingProperties.globalInvalidType = globalInvalidType;
    }

    @Value("${global.setting.video.type}")
    public void setGlobalVideoType(String globalVideoType) {
        GlobalSettingProperties.globalVideoType = globalVideoType;
    }
}
