package com.vadim01er.springbotvk.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vadim01er.springbotvk.client.answers.Message;
import com.vadim01er.springbotvk.keyboard.Keyboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VkClient {

    private static final Logger logger = LoggerFactory.getLogger(VkClient.class);

    private final RestTemplate template;
    private final ClientConfig clientConfig;

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
        final String url = getUrlRequest(
                "messages.send",
                new HashMap<>() {{
                    put("message", message);
                    put("peer_id", peerId);
                    put("random_id", System.currentTimeMillis());
                    put("access_token", clientConfig.getToken());
                    put("v", clientConfig.getVersionAPI());
                }}
        );
        logger.info("trying GET request: " + url);
        final ResponseEntity<MessageResponse> response = template.getForEntity(url, MessageResponse.class);
        return response;
    }

    public ResponseEntity<MessageResponse> sendMessage(String message, int peerId, List<Message.Attachment> attachments) {
        final ResponseEntity<MessageResponse> response;
        if (attachments.isEmpty()) {
            response = sendMessage(message, peerId);
        } else {
            final String request = getAttachments(message, peerId, attachments);
            logger.info("trying GET request with attachment: " + request);
            response = template.getForEntity(request, MessageResponse.class);
        }
        return response;
    }

    public ResponseEntity<String> sendMessage(String message, int peerId, Keyboard keyboard) {
        final String url = getUrlRequest(
                "messages.send",
                new HashMap<>() {{
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
        ResponseEntity<String> response = null;
        try {
            map.add("keyboard", objectMapper.writeValueAsString(keyboard));
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            logger.info("trying POST request " + url + " with keyboard: " + request);
            response = template.postForEntity(url, request, String.class);
        } catch (JsonProcessingException e) {
            logger.error("some problem with creating JSON Keyboard");
        }

        return response;
    }

    public ResponseEntity<String> sendMessageWithDocAndKeyboard(String message, int peerId, List<Message.Attachment> attachments,
                                                                Keyboard keyboard) {
        final String url = getAttachments(message, peerId, attachments);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseEntity<String> response = null;
        try {
            map.add("keyboard", objectMapper.writeValueAsString(keyboard));
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            logger.info("trying POST request " + url + " with keyboard: " + request);
            response = template.postForEntity(url, request, String.class);
        } catch (JsonProcessingException e) {
            logger.error("some problem with creating JSON Keyboard");
        }

        return response;
    }

    private String getAttachments(String message, int peerId, List<Message.Attachment> attachments) {
        StringBuilder attachmentsStr = new StringBuilder();
        for (Message.Attachment attachment : attachments) {
            attachmentsStr.append(attachment.getType());
            switch (attachment.getType()) {
                case "photo":
                    attachmentsStr.append(attachment.getPhoto().getOwnerId()).append("_")
                            .append(attachment.getPhoto().getId());
                    if (!(attachment.getPhoto().getAccessKey() == null)) {
                        attachmentsStr.append("_").append(attachment.getPhoto().getAccessKey());
                    }
                    attachmentsStr.append(",");
                    break;
                case "video":
                    attachmentsStr.append(attachment.getVideo().getOwnerId()).append("_")
                            .append(attachment.getVideo().getId());
                    if (!(attachment.getVideo().getAccessKey() == null)) {
                        attachmentsStr.append("_").append(attachment.getVideo().getAccessKey());
                    }
                    attachmentsStr.append(",");
                    break;
                case "doc":
                    attachmentsStr.append(attachment.getDoc().getOwnerId()).append("_")
                            .append(attachment.getDoc().getId());
                    if (!(attachment.getDoc().getAccessKey() == null)) {
                        attachmentsStr.append("_").append(attachment.getDoc().getAccessKey());
                    }
                    attachmentsStr.append(",");
                    break;
                case "wall":
                    attachmentsStr.append(attachment.getWall().getToId()).append("_")
                            .append(attachment.getWall().getId());
                    if (!(attachment.getWall().getAccessKey() == null)) {
                        attachmentsStr.append("_").append(attachment.getWall().getAccessKey());
                    }
                    attachmentsStr.append(",");
            }
        }
        if (!attachments.isEmpty()) {
            attachmentsStr.deleteCharAt(attachmentsStr.length() - 1);
        }
        return getUrlRequest(
                "messages.send",
                new HashMap<>() {{
                    put("message", message);
                    put("peer_id", peerId);
                    put("random_id", System.currentTimeMillis());
                    put("access_token", clientConfig.getToken());
                    put("v", clientConfig.getVersionAPI());
                    put("attachment", attachmentsStr.toString());
                }}
        );
    }

}
