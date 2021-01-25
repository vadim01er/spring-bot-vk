package com.vadim01er.springbotvk.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="client")
@Data
public class ClientConfig {
    private String botHook;
    private String requestUrlTemplate;
    private String token;
    private String confirmationToken;
    private long groupId;
    private float versionAPI;
    private String callbackRequestTemplate;
}
