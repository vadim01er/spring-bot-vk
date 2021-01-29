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

    private final Map<String, String> mapBack = new HashMap<>() {{
        put("Основная информация о курсе ОПД", "начать2");
        put("Что такое ОПД?", "Основная информация о курсе ОПД");
        put("Польза проектной деятельности", "Основная информация о курсе ОПД");
        put("Командообразование + тест", "Основная информация о курсе ОПД");
        put("Тайм-менеджмент", "Основная информация о курсе ОПД");
        put("Сроки курса", "Основная информация о курсе ОПД");
        put("Методическое пособие для студентов", "начать2");
        put("Контакты руководителей курса", "начать2");
        put("Контактные данные", "Контакты руководителей курса");
        put("Местоположение", "Контакты руководителей курса");
        put("Task list", "начать2");
        put("Совет дня", "начать2");
    }};

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
        String answer;

        if (!isAdmin
                && !msg.getVkObject().getMessage().getText().equals("Начать")
                && msg.getVkObject().getMessage().getPayload().isEmpty()) {
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
            answer = answersService.findAnswer("Начать");
            client.sendMessage(answer, peerId, new Keyboard().addButtons(
                    new String[]{"Основная информация о курсе ОПД", "Методическое пособие для студентов",
                            "Контакты руководителей курса", "Task list"/*, "Совет дня"*/},
                    false));
            return;
        }

        switch (textMsg) {
            case "начать2":
                answer = answersService.findAnswer("начать2");
                client.sendMessage(answer, peerId, new Keyboard().addButtons(
                        new String[]{"Основная информация о курсе ОПД", "Методическое пособие для студентов",
                                "Контакты руководителей курса", "Task list"/*, "Совет дня"*/},
                        false));
                break;
            // Основная информация о курсе ОПД
            case "Основная информация о курсе ОПД":
                answer = answersService.findAnswer("Основная информация о курсе ОПД");
                client.sendMessage(answer, peerId, new Keyboard().addButtons(
                        new String[]{"Что такое ОПД?", "Польза проектной деятельности", "Командообразование + тест",
                                "Тайм-менеджмент", "Сроки курса"}, false));
                break;
            case "Что такое ОПД?":
                answer = answersService.findAnswer("Что такое ОПД?");
                client.sendMessage(answer, peerId, new Keyboard().addButtonBack());
                break;
            case "Польза проектной деятельности":
                answer = answersService.findAnswer("Польза проектной деятельности");
                client.sendMessage(answer, peerId, new Keyboard().addButtonBack());
                break;
            case "Командообразование + тест":
                answer = answersService.findAnswer("Командообразование + тест");
                client.sendMessage(answer, peerId, new Keyboard().addButtonBack());
                break;
            case "Тайм-менеджмент":
                answer = answersService.findAnswer("Тайм-менеджмент");
                client.sendMessage(answer, peerId, new Keyboard().addButtonBack());
                break;
            case "Сроки курса":
                answer = answersService.findAnswer("Сроки курса");
                client.sendMessage(answer, peerId, new Keyboard().addButtonBack());
                break;

            // END Основная информация о курсе ОПД

            //Методическое пособие для студентов
            case "Методическое пособие для студентов":
                answer = answersService.findAnswer("Методическое пособие для студентов");
                client.sendMessage(answer, peerId, new Keyboard().addButtons(
                        new String[]{"Методическое пособие"},
//                        new String[]{"linkМетодическое пособие"},
                        true));
                break;
            // END Методическое пособие для студентов


            case "Контакты руководителей курса":
                answer = answersService.findAnswer("Контакты руководителей курса");
                client.sendMessage(answer, peerId, new Keyboard().addButtons(
                        new String[]{"Курс на DL"}, new String[]{"https://dl.spbstu.ru/course/view.php?id=2660"},
                        false).addButtons(new String[]{"Контактные данные", "Местоположение"}, true));
                break;
            case "Контактные данные":
                answer = answersService.findAnswer("Контактные данные");
                client.sendMessage(answer, peerId, new Keyboard().addButtonBack());
                break;
            case "Местоположение":
                answer = answersService.findAnswer("Местоположение");
                client.sendMessage(answer, peerId, new Keyboard().addButtonBack());
                break;


            // Task list
            case "Task list":
                answer = answersService.findAnswer("Task list");
                client.sendMessage(answer, peerId, new Keyboard()
                        .addButtons(new String[]{"Что такое task list?", "Ссылка на task list"},
                                new String[]{"--", "++"}, false)
                        .addButtons(new String[]{"Подписаться на рассылку"}, true));
                break;
            case "Подписаться на рассылку":
                answer = answersService.findAnswer("Подписаться на рассылку");
                client.sendMessage(answer, peerId,
                        new Keyboard().addButtonsInLine(new String[][]{new String[]{"Да", "Нет"}}, false));
                break;
            case "Да":
                usersService.updateNewsletter(peerId, true);
                answer = answersService.findAnswer("Task list");
                client.sendMessage(answer, peerId, new Keyboard()
                        .addButtons(new String[]{"Что такое task list?", "Ссылка на task list"},
                                new String[]{"--", "++"}, false)
                        .addButtons(new String[]{"Подписаться на рассылку"}, true));
                textMsg = "Task list";
                break;
            case "Нет":
                usersService.updateNewsletter(peerId, false);
                answer = answersService.findAnswer("Task list");
                client.sendMessage(answer, peerId, new Keyboard()
                        .addButtons(new String[]{"Что такое task list?", "Ссылка на task list"},
                                new String[]{"--", "++"}, false)
                        .addButtons(new String[]{"Подписаться на рассылку"}, true));
                textMsg = "Task list";
                break;
            // END Task list
            default:
                client.sendMessage("Возможно какие то неполадки", peerId);
                return;
        }
        usersService.updateNowTxt(peerId, textMsg);
    }
}
