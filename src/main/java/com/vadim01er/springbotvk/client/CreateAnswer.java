package com.vadim01er.springbotvk.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vadim01er.springbotvk.client.answers.VkResponse;
import com.vadim01er.springbotvk.entities.Admin;
import com.vadim01er.springbotvk.entities.User;
import com.vadim01er.springbotvk.keyboard.Keyboard;
import com.vadim01er.springbotvk.service.AdminsService;
import com.vadim01er.springbotvk.service.AnswersService;
import com.vadim01er.springbotvk.service.NewslettersService;
import com.vadim01er.springbotvk.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CreateAnswer {

    private final VkClient client;
    private final AdminsService adminsService;
    private final UsersService usersService;
    private final AnswersService answersService;
    private final NewslettersService newslettersService;

    private final Map<String, String> mapBack = new HashMap<String, String>() {{
        put("Основная информация о курсе ОПД", "начать2");
        put("Методическое пособие для студентов", "начать2");
        put("Контакты руководителей курса", "начать2");
        put("Task list", "начать2");
        put("Совет дня", "начать2");
    }}; // add back!!!

    @Autowired
    public CreateAnswer(VkClient client, AdminsService adminsService,
                        UsersService usersService, AnswersService answersService,
                        NewslettersService newslettersService) {
        this.client = client;
        this.adminsService = adminsService;
        this.usersService = usersService;
        this.answersService = answersService;
        this.newslettersService = newslettersService;
    }

    public void replayAdmin(VkResponse msg) throws JsonProcessingException {
        String[] split = msg.getVkObject().getMessage().getText().split(" ");
        if (split[0].startsWith("/")) {
            switch (split[0]) {
                case "/addAdmin":
                case "/addadmin":
                    adminsService.insert(new Admin(Integer.parseInt(split[1])));
                    client.sendMessage("admin is added: " + split[1], msg.getVkObject().getMessage().getPeerId());
                    break;
                case "/newsletter":
                    if (split[1].equals("-d")) {
                        if (!newslettersService.insert(split[2], msg.getVkObject().getMessage().getText().substring(14))) {
                            client.sendMessage("Newsletter NOT added", msg.getVkObject().getMessage().getPeerId());
                        } else {
                            client.sendMessage("Newsletter added", msg.getVkObject().getMessage().getPeerId());
                        }
                    } else if (split[1].equals("-date")) {
                        if (!newslettersService.insert(split[2], msg.getVkObject().getMessage().getText().substring(17))) {
                            client.sendMessage("Newsletter NOT added", msg.getVkObject().getMessage().getPeerId());
                        } else {
                            client.sendMessage("Newsletter added", msg.getVkObject().getMessage().getPeerId());
                        }
                    } else {
                        List<User> allUsersWithNewsletter = usersService.findAllUsersWithNewsletter();
                        for (User user : allUsersWithNewsletter) {
                            client.sendMessage(
                                    msg.getVkObject().getMessage().getText().substring(11),
                                    user.getUserId(),
                                    msg
                            );
                        }
                        client.sendMessage("Newsletter send", msg.getVkObject().getMessage().getPeerId());
                    }
                    break;
            }
        } else {
            replay(msg, true);
        }
    }

    public void replay(VkResponse msg) throws JsonProcessingException {
        replay(msg, false);
    }

    private void replay(VkResponse msg, boolean isAdmin) throws JsonProcessingException {
        String textMsg = msg.getVkObject().getMessage().getText();
        int peerId = msg.getVkObject().getMessage().getPeerId();
        boolean haveUser = usersService.userIsExist(peerId);

        if (msg.getVkObject().getMessage().getPayload().isEmpty() && !isAdmin) {
            client.sendMessage("Пожалуйста, используйте кнопки", peerId);
            return;
        }

        if (textMsg.equals("Назад")) {
            if (haveUser) {
                User userById = usersService.findUserById(peerId);
                textMsg = mapBack.get(userById.getNow());
            }
        } else if (textMsg.equals("Начать") || textMsg.equals("начать")
                || textMsg.equals("Start") || textMsg.equals("start")) {

            usersService.insert(new User(peerId, "начать2", true));
            String answer = answersService.findAnswer("Начать");
            client.sendMessage(answer, peerId,
                    new Keyboard().addButtons(new String[]{"Основная информация о курсе ОПД",
                            "Методическое пособие для студентов", "Контакты руководителей курса",
                            "Task list", "Совет дня"}, false));
            return;
        }

        String answer;

        switch (textMsg) {
            case "начать2":
                answer = answersService.findAnswer("начать2");
                client.sendMessage(answer, peerId, new Keyboard().addButtons(
                        new String[]{"Основная информация о курсе ОПД",
                                "Методическое пособие для студентов", "Контакты руководителей курса",
                                "Task list", "Совет дня"}, false));
                break;

            //Основная информация о курсе ОПД
            case "Основная информация о курсе ОПД":
                answer = answersService.findAnswer("Основная информация о курсе ОПД");
                client.sendMessage(answer, peerId, new Keyboard().addButtons(
                        new String[]{"Основная информация о курсе ОПД",
                                "Методическое пособие для студентов", "Контакты руководителей курса",
                                "Task list", "Совет дня"}, false));
                break;
            case "Что такое ОПД?":

                break;

            case "Методическое пособие для студентов":

                break;
            case "Контакты руководителей курса":

                break;
            case "Task list":

                break;
            case "Совет дня":

                break;
            case "":

                break;


            default:
                client.sendMessage("Возможно какие то неполадки", peerId);
                break;
        }
    }
}
