package com.gmail.nathanryder16.finalyearproject;

import com.gmail.nathanryder16.finalyearproject.model.User;
import com.gmail.nathanryder16.finalyearproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Startup implements CommandLineRunner {

    @Autowired
    private UserService users;

    @Override
    public void run(String... args) throws Exception {
//        String email, String password, int validated
        if (users.findByEmail("admin@localhost.com") == null) {
            User user = new User("admin@localhost.com", "admin", 1);
            user.setSuperuser(1);

            users.save(user);
        }
    }

}
