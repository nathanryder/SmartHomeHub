package com.gmail.nathanryder16.finalyearproject.service;

import com.gmail.nathanryder16.finalyearproject.model.User;
import com.gmail.nathanryder16.finalyearproject.repository.UserRepository;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public Iterable<User> findAll() {
        return repo.findAll();
    }

    public User findByEmail(String email) {
        return repo.findByEmail(email);
    }

    public Optional<User> findById(String id) {
        return repo.findById(id);
    }

    public void delete(User user) {
        repo.delete(user);
    }

    public void save(User user) {
        repo.save(user);
    }

}
