package com.vadim01er.springbotvk.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vadim01er.springbotvk.client.ClientConfig;
import com.vadim01er.springbotvk.client.CreateAnswer;
import com.vadim01er.springbotvk.client.answers.VkResponse;
import com.vadim01er.springbotvk.service.AdminsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    private final ClientConfig clientConfig;
    private final CreateAnswer createAnswer;
    private final AdminsService adminsService;

    public Controller(ClientConfig clientConfig, CreateAnswer createAnswer, AdminsService adminsService) {
        this.clientConfig = clientConfig;
        this.createAnswer = createAnswer;
        this.adminsService = adminsService;
    }

    @PostMapping("/botvk")
    public String server(@RequestBody VkResponse msg) throws JsonProcessingException {
        if (msg.getType().equals("confirmation")) {
            return clientConfig.getConfirmationToken();
        }

        if (adminsService.adminIsExist(msg.getVkObject().getMessage().getPeerId())) {
            createAnswer.replayAdmin(msg);
        } else {
            createAnswer.replay(msg);
        }

        return HttpStatus.OK.getReasonPhrase();
    }
}
