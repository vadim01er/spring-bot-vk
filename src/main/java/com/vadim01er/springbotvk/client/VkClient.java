package com.vadim01er.springbotvk.client;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vadim01er.springbotvk.client.answers.Message;
import com.vadim01er.springbotvk.client.answers.VkResponse;
import com.vadim01er.springbotvk.keyboard.Keyboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VkClient {

    private static final Logger logger = LoggerFactory.getLogger(VkClient.class);

    private final RestTemplate template;
    private final ClientConfig clientConfig;

    @Autowired
    public VkClient(RestTemplate template, ClientConfig clientConfig) {
        this.template = template;
        this.clientConfig = clientConfig;
    }

    private String getUrlRequest(String methodName, Map<String, Object> props) {
        String properties = props.entrySet().stream()
                .map(Object::toString)
                .collect(Collectors.joining("&"));
        String requestURL =
                String.format(clientConfig.getRequestUrlTemplate(), methodName);
        if (!props.isEmpty()) {
            requestURL += "?" + properties;
        }
        return requestURL;
    }

    public ResponseEntity<MessageResponse> sendMessage(String message, int peerId) {
        final String request = getUrlRequest(
                "messages.send",
                new HashMap<String, Object>() {{
                    put("message", message);
                    put("peer_id", peerId);
                    put("random_id", System.currentTimeMillis());
                    put("access_token", clientConfig.getToken());
                    put("v", clientConfig.getVersionAPI());
                }}

        );
        logger.info("trying GET request: " + request);
        final ResponseEntity<MessageResponse> response = template.getForEntity(request, MessageResponse.class);
        return response;
    }

    public ResponseEntity<MessageResponse> sendMessage(String message, int peerId, VkResponse vkResponse) {
        final ResponseEntity<MessageResponse> response;
        if (vkResponse.getVkObject().getMessage().getAttachments().isEmpty()) {
            response = sendMessage(message, peerId);
        } else {
            StringBuilder attachments = new StringBuilder();
            for (Message.Attachment attachment : vkResponse.getVkObject().getMessage().getAttachments()) {
                attachments.append(attachment.getType()).append(attachment.getPhoto().getOwnerId())
                        .append("_").append(attachment.getPhoto().getId()).append("_")
                        .append(attachment.getPhoto().getAccessKey()).append(",");
            }
            attachments.deleteCharAt(attachments.length() - 1);

            final String request = getUrlRequest(
                    "messages.send",
                    new HashMap<String, Object>() {{
                        put("message", message);
                        put("peer_id", peerId);
                        put("random_id", System.currentTimeMillis());
                        put("access_token", clientConfig.getToken());
                        put("v", clientConfig.getVersionAPI());
                        put("attachment", attachments);
                    }}
            );
            logger.info("trying GET request with attachment: " + request);
            response = template.getForEntity(request, MessageResponse.class);
        }
        return response;
    }

    public ResponseEntity<String> sendMessage(String message, int peerId, Keyboard keyboard) throws JsonProcessingException {

        final String url = getUrlRequest(
                "messages.send",
                new HashMap<String, Object>() {{
                    put("message", message);
                    put("peer_id", peerId);
                    put("random_id", System.currentTimeMillis());
                    put("access_token", clientConfig.getToken());
                    put("v", clientConfig.getVersionAPI());
                }}
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        map.add("keyboard", objectMapper.writeValueAsString(keyboard));
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        logger.info("trying POST request " + url + " with keyboard: " + request);
        ResponseEntity<String> response = template.postForEntity(url, request, String.class);
        return response;
    }

}
