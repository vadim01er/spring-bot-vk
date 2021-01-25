package com.vadim01er.springbotvk.service;

import com.vadim01er.springbotvk.entities.User;
import com.vadim01er.springbotvk.entities.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsersService {

    private UserRepository userRepository;

    @Autowired
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

    public boolean userIsExist(long userId) {
        return userRepository.findAll().stream()
                .anyMatch(user -> user.getUserId() == userId);
    }


    public User findUserById(int userId) {
        Optional<User> res = userRepository.findAll().stream()
                .filter(user -> user.getUserId() == userId)
                .findFirst();
        return res.orElse(null);
    }

    public List<User> findAllUsersWithNewsletter() {
        return userRepository.findAll().stream().filter(User::isNewsletter).collect(Collectors.toList());
    }
}
