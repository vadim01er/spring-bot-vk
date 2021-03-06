package com.vadim01er.springbotvk.client;

import com.vadim01er.springbotvk.entities.Newsletter;
import com.vadim01er.springbotvk.entities.User;
import com.vadim01er.springbotvk.service.NewslettersService;
import com.vadim01er.springbotvk.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
public class NewsletterScheduled {

    private final VkClient client;
    private final UsersService usersService;
    private final NewslettersService newslettersService;

    public NewsletterScheduled(VkClient client, UsersService usersService, NewslettersService newslettersService) {
        this.client = client;
        this.usersService = usersService;
        this.newslettersService = newslettersService;
    }

    @Scheduled(cron="0 0 10 * * *")
//    @Scheduled(cron="0/10 * * * * *")
    public void notifyNewsletter() {
        long l = System.currentTimeMillis();
        String currentTime = new SimpleDateFormat("dd-MM").format(l);
        System.out.println(currentTime);
        for (Newsletter newsletter : newslettersService.findAll()) {
            if (newsletter.getTime().equals(currentTime)) {
                for (User user : usersService.findAllUsersWithNewsletter()) {
                    client.sendMessage(newsletter.getTextNewsletter(), user.getUserId());
                }
            }
        }
    }
}
