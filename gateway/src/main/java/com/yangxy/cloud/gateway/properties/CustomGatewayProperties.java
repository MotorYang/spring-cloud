package com.yangxy.cloud.gateway.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author MotorYang
 * @email motoyangxy@outlook.com
 * @date 2025/11/25 09:30
 */
@Component
@ConfigurationProperties(prefix = "spring.cloud.gateway")
public class CustomGatewayProperties {

    private List<String> whitelist;

    public List<String> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(List<String> whitelist) {
        this.whitelist = whitelist;
    }

}
