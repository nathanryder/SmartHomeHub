package com.gmail.nathanryder16.finalyearproject.service;

import com.gmail.nathanryder16.finalyearproject.model.User;
import com.gmail.nathanryder16.finalyearproject.repository.UserRepository;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    public boolean isEmailTaken(String email) {
        for (User user : repo.findAll()) {
            if (user.getEmail().equalsIgnoreCase(email))
                return true;
        }

        return false;
    }

    public boolean isEmailValid(String email) {
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(email);
    }

    public User findByEmail(String email) {
        return repo.findByEmail(email);
    }

    public void save(User user) {
        repo.save(user);
    }

}
