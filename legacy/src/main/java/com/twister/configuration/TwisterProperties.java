package com.twister.configuration;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "twister")
public class TwisterProperties {

    String appName;
    String appVersion;

    public String getFullVersion() {
        return appName + " " + appVersion;
    }
}
