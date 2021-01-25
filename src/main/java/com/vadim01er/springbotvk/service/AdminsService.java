package com.vadim01er.springbotvk.service;

import com.vadim01er.springbotvk.entities.Admin;
import com.vadim01er.springbotvk.entities.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminsService {

    private final AdminRepository adminRepository;

    @Autowired
    public AdminsService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public boolean adminIsExist(int userId) {
        return adminRepository.existsById(userId);
    }

    public void insert(Admin admin) {
        adminRepository.save(admin);
    }
}
