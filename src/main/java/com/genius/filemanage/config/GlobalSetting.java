package com.genius.filemanage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "global.setting")
@PropertySource(value = "classpath:/config/setting.properties",encoding = "UTF-8")
public class GlobalSetting {
}
