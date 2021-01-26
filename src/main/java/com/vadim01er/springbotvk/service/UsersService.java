package com.vadim01er.springbotvk.service;

import com.vadim01er.springbotvk.entities.User;
import com.vadim01er.springbotvk.entities.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsersService {

    private final UserRepository userRepository;

    public UsersService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User insert(User client) {
        return userRepository.save(client);
    }

    public User update(int userId, User newUser) {
        User user = findUserById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with id " + userId);
        }
        user.setUserId(newUser.getUserId());
        user.setNow(newUser.getNow());
        user.setNewsletter(newUser.isNewsletter());
        return userRepository.save(user);
    }

    public User updateNow(int userId, String newNow) {
        User user = findUserById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with id " + userId);
        }
        user.setNow(newNow);
        return userRepository.save(user);
    }

    public boolean userIsExist(int userId) {
        return userRepository.existsById(userId);
    }


    public User findUserById(int userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public List<User> findAllUsersWithNewsletter() {
        ArrayList<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users.stream().filter(User::isNewsletter).collect(Collectors.toList());
    }
}
