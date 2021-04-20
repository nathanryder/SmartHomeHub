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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Optional;

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

        User user = new User(email, password, 0);
        users.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping(value = "/{userId}", produces = "application/json")
    public ResponseEntity<Object> editUser(@PathVariable("userId") String userId,
                                           @RequestParam("password") String password,
                                           @RequestParam("email") String email,
                                           @RequestParam("validated") int validated) {

        Optional<User> ouser = users.findById(userId);
        if (ouser.isEmpty()) {
            JsonObject msg = new JsonParser().parse("{\"error\": \"invalid user id\"}").getAsJsonObject();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }

        User user = ouser.get();

        user.setEmail(email);

        if (password != null && !password.equals("")) {
            user.setPassword(password);
        }
        user.setValidated(validated);

        users.save(user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping(value = "/{userId}", produces = "application/json")
    public ResponseEntity<Object> deleteUser(@PathVariable("userId") String userId) {

        Optional<User> ouser = users.findById(userId);
        if (ouser.isEmpty()) {
            JsonObject msg = new JsonParser().parse("{\"error\": \"invalid user id\"}").getAsJsonObject();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }

        users.delete(ouser.get());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(value = "/auth", produces = "application/json")
    public ResponseEntity<Object> auth(@RequestParam("email") String email,
                                       @RequestParam("password") String password, HttpSession session) {

        User user = users.findByEmail(email);
        if (user == null) {
            JsonObject msg = new JsonParser().parse("{\"error\": \"invalid email address\"}").getAsJsonObject();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }

        if (user.getValidated() == 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());

        if (result.verified) {
            session.setAttribute("loggedIn", user.getId());
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }

}
