package com.vadim01er.springbotvk.service;

import com.vadim01er.springbotvk.entities.Newsletter;
import com.vadim01er.springbotvk.entities.NewslettersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewslettersService {

    private final NewslettersRepository newslettersRepository;

    private final static String DATA_FORMAT = "dd-MM";

    public NewslettersService(NewslettersRepository newslettersRepository) {
        this.newslettersRepository = newslettersRepository;
    }

    public boolean insert(String time, String text) {
        SimpleDateFormat df = new SimpleDateFormat(DATA_FORMAT);
        df.setLenient(false);
        try {
            df.parse(time);
        } catch (ParseException e) {
            return false;
        }
        newslettersRepository.save(new Newsletter(time, text));
        return true;
    }

    public List<Newsletter> findAll(){
        ArrayList<Newsletter> newsletters = new ArrayList<>();
        newslettersRepository.findAll().forEach(newsletters::add);
        return newsletters;
    }
}
