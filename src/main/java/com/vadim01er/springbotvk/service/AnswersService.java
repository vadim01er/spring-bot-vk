package com.vadim01er.springbotvk.service;

import com.vadim01er.springbotvk.entities.AnswersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnswersService {

    private final AnswersRepository answersRepository;

    @Autowired
    public AnswersService(AnswersRepository answersRepository) {
        this.answersRepository = answersRepository;
    }

    public String findAnswer(String text) {
        return answersRepository.findById(text).get().getAnswerText();
    }
}
