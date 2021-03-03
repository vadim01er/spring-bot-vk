package com.vadim01er.springbotvk.client;

import com.vadim01er.springbotvk.client.answers.Message;
import com.vadim01er.springbotvk.client.answers.VkResponse;
import com.vadim01er.springbotvk.entities.Admin;
import com.vadim01er.springbotvk.entities.User;
import com.vadim01er.springbotvk.keyboard.Keyboard;
import com.vadim01er.springbotvk.service.AdminsService;
import com.vadim01er.springbotvk.service.AnswersService;
import com.vadim01er.springbotvk.service.NewslettersService;
import com.vadim01er.springbotvk.service.UsersService;
import org.springframework.stereotype.Component;

import java.util.*;

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
        put("Отписаться от рассылки", "начать2");
        put("Подписаться на рассылку", "начать2");
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

    private class Newsletter implements Runnable {

        private final VkResponse msg;

        public Newsletter(VkResponse msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            List<User> allUsersWithNewsletter = usersService.findAllUsersWithNewsletter();
            for (User user : allUsersWithNewsletter) {
                client.sendMessage(
                        msg.getVkObject().getMessage().getText().substring(11),
                        user.getUserId(),
                        msg.getVkObject().getMessage().getAttachments()
                );
            }
        }
    }

    public void replayAdmin(VkResponse msg) {
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
                        Runnable r = new Newsletter(msg);
                        new Thread(r).start();
                        client.sendMessage("Newsletter send", msg.getVkObject().getMessage().getPeerId());
                    }
                    break;
            }
        } else {
            replay(msg);
        }
    }

    public void replay(VkResponse msg) {
        String textMsg = msg.getVkObject().getMessage().getText();
        int peerId = msg.getVkObject().getMessage().getPeerId();
        User user = usersService.findUserById(peerId);
        String answer;
        String nameNewsletter = "Отписаться от рассылки";
        Keyboard.Color color = Keyboard.Color.NEGATIVE;
        if (user != null) {
            nameNewsletter = user.isNewsletter()? "Отписаться от рассылки": "Подписаться на рассылку";
            color = user.isNewsletter()? Keyboard.Color.NEGATIVE: Keyboard.Color.SECONDARY;
        }

        if (textMsg.equals("Назад")) {
            User userById = usersService.findUserById(peerId);
            textMsg = mapBack.get(userById.getNow());
        } else if (textMsg.equals("Начать") || textMsg.equals("начать")
                || textMsg.equals("Start") || textMsg.equals("start")) {
            usersService.insert(new User(peerId, "начать2", true));
            answer = answersService.findAnswer("Начать");
            client.sendMessage(answer, peerId, new Keyboard().addButtons(
                    new String[]{"Основная информация о курсе ОПД", "Методическое пособие для студентов",
                            "Контакты руководителей курса", "Task list"},
                    false).addButtonNegative(nameNewsletter, color));
            return;
        }

        switch (textMsg) {
            case "начать2":
                answer = answersService.findAnswer("начать2");
                client.sendMessage(answer, peerId, new Keyboard().addButtons(
                        new String[]{"Основная информация о курсе ОПД", "Методическое пособие для студентов",
                                "Контакты руководителей курса", "Task list"},
                        false).addButtonNegative(nameNewsletter, color));
                break;
            // Основная информация о курсе ОПД
            case "Основная информация о курсе ОПД":
                answer = answersService.findAnswer("Основная информация о курсе ОПД");
                client.sendMessage(answer, peerId, new Keyboard().addButtons(
                        new String[]{"Что такое ОПД?", "Польза проектной деятельности", "Командообразование + тест",
                                "Тайм-менеджмент", "Сроки курса"}, true));
                break;
            case "Что такое ОПД?":
                answer = answersService.findAnswer("Что такое ОПД?");
                client.sendMessage(answer, peerId);
                answer = answersService.findAnswer("Что такое ОПД?2");
                client.sendMessage(answer, peerId);
                answer = answersService.findAnswer("Что такое ОПД?3");
                client.sendMessage(answer, peerId, new Keyboard().addButtonBack());
                break;
            case "Польза проектной деятельности":
                answer = answersService.findAnswer("Польза проектной деятельности");
                client.sendMessage(answer, peerId, new Keyboard().addButtonBack());
                break;
            case "Командообразование + тест":
                answer = answersService.findAnswer("Командообразование + тест");
                client.sendMessage(answer, peerId);
                answer = answersService.findAnswer("Командообразование + тест2");
                client.sendMessage(answer, peerId);
                answer = answersService.findAnswer("Командообразование + тест3");
                Message.Attachment.Builder builder = new Message.Attachment.Builder();
                builder.addType("photo").addPhoto(162870639, 590701124);
                client.sendMessageWithDocAndKeyboard(answer, peerId,
                        new ArrayList<>(1){{add(builder.createAttachment());}},
                        new Keyboard().addButtonBack());
                break;
            case "Тайм-менеджмент":
                answer = answersService.findAnswer("Тайм-менеджмент");
                client.sendMessage(answer, peerId);
                answer = answersService.findAnswer("Тайм-менеджмент2");
                client.sendMessage(answer, peerId);
                answer = answersService.findAnswer("Тайм-менеджмент3");
                builder = new Message.Attachment.Builder();
                builder.addType("photo").addPhoto(162870639, 590701146);
                client.sendMessageWithDocAndKeyboard(answer, peerId,
                        new ArrayList<>(1){{add(builder.createAttachment());}},
                        new Keyboard().addButtonBack());
                break;
            case "Сроки курса":
                answer = answersService.findAnswer("Сроки курса");
                client.sendMessage(answer, peerId, new Keyboard().addButtonBack());
                break;

            // END Основная информация о курсе ОПД

            //Методическое пособие для студентов
            case "Методическое пособие для студентов":
                answer = answersService.findAnswer("Методическое пособие для студентов");
                builder = new Message.Attachment.Builder();
                builder.addType("doc").addDoc(162870639, 590703138);
                client.sendMessage(answer, peerId, new ArrayList<>(1){{add(builder.createAttachment());}});
                break;
            // END Методическое пособие для студентов

            case "Контакты руководителей курса":
                answer = answersService.findAnswer("Контакты руководителей курса");
                client.sendMessage(answer, peerId, new Keyboard()
                        .addButtons(new String[]{"Ссылка на учебный портал"},
                                new String[]{"https://project.spbstu.ru/"}, false)
                        .addButtons(new String[]{"Контактные данные", "Местоположение"}, true));
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
                builder = new Message.Attachment.Builder();
                builder.addType("doc").addDoc(162870639, 590701252);
                client.sendMessage(answer, peerId,  new ArrayList<>(1){{add(builder.createAttachment());}});
                break;
            case "Подписаться на рассылку":
                usersService.updateNewsletter(peerId, true);
                answer = "Вы подписались на рассылку";
                client.sendMessage(answer, peerId, new Keyboard().addButtons(
                        new String[]{"Основная информация о курсе ОПД", "Методическое пособие для студентов",
                                "Контакты руководителей курса", "Task list"},
                        false).addButtonNegative("Отписаться от рассылки", Keyboard.Color.NEGATIVE));
                break;
            case "Отписаться от рассылки":
                usersService.updateNewsletter(peerId, false);
                answer = "Вы отписались от рассылки";
                client.sendMessage(answer, peerId, new Keyboard().addButtons(
                        new String[]{"Основная информация о курсе ОПД", "Методическое пособие для студентов",
                                "Контакты руководителей курса", "Task list"},
                        false).addButtonNegative("Подписаться на рассылку", Keyboard.Color.SECONDARY));
                break;
            // END Task list
        }
        usersService.updateNowTxt(peerId, textMsg);
    }
}
