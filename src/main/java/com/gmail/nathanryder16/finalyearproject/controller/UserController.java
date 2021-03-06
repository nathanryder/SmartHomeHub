package com.gmail.nathanryder16.finalyearproject.controller;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.gmail.nathanryder16.finalyearproject.model.User;
import com.gmail.nathanryder16.finalyearproject.service.UserService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private UserService users;

    @PostMapping(value = "/", produces = "application/json")
    public ResponseEntity<Object> addUser(@RequestParam("password") String password,
                                          @RequestParam("email") String email, HttpSession session) {

        if (!users.isEmailValid(email)) {
            JsonObject msg = new JsonParser().parse("{\"error\": \"invalid email address\"}").getAsJsonObject();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        if (users.isEmailTaken(email)) {
            JsonObject msg = new JsonParser().parse("{\"error\": \"email already taken\"}").getAsJsonObject();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }

        String hashed = BCrypt.withDefaults().hashToString(12, password.toCharArray());

        User user = new User(email, hashed, 1);
        users.save(user);

        session.setAttribute("loggedIn", user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping(value = "/auth", produces = "application/json")
    public ResponseEntity<Object> auth(@RequestParam("email") String email,
                                       @RequestParam("password") String password, HttpSession session) {

        User user = users.findByEmail(email);
        if (user == null) {
            JsonObject msg = new JsonParser().parse("{\"error\": \"invalid email address\"}").getAsJsonObject();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }

        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());

        if (result.verified) {
            session.setAttribute("loggedIn", user.getEmail());
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }

}
