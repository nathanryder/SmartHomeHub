package com.gmail.nathanryder16.finalyearproject.controller;

import com.gmail.nathanryder16.finalyearproject.FileStorage;
import com.gmail.nathanryder16.finalyearproject.model.Config;
import com.gmail.nathanryder16.finalyearproject.model.User;
import com.gmail.nathanryder16.finalyearproject.repository.UserRepository;
import com.gmail.nathanryder16.finalyearproject.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping("settings")
public class SetupController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private Config config;

    @Autowired
    private FileStorage storage;

    @RequestMapping("/")
    public String setup(HttpSession session, Model model) {
        model.addAttribute("sqlHost", config.getSqlHost());
        model.addAttribute("sqlPort", config.getSqlPort());
        model.addAttribute("sqlUser", config.getSqlUser());
        model.addAttribute("sqlDatabase", config.getSqlDatabase());
        model.addAttribute("mqttHost", config.getMqttHost());
        model.addAttribute("mqttPort", config.getMqttPort());
        model.addAttribute("mqttUser", config.getMqttUser());
        model.addAttribute("webPort", config.getWebPort());

        session.setAttribute("page", "settings");
        return "setup";
    }

    @PutMapping(value = "/edit", produces = "application/json")
    public ResponseEntity<Object> setConfig(@RequestParam("sqlHost") String sqlHost,
                                            @RequestParam("sqlPort") int sqlPort,
                                            @RequestParam("sqlUser") String sqlUser,
                                            @RequestParam("sqlPasswd") String sqlPasswd,
                                            @RequestParam("sqlDatabase") String sqlDatabase,
                                            @RequestParam("mqttHost") String mqttHost,
                                            @RequestParam("mqttPort") int mqttPort,
                                            @RequestParam("mqttUser") String mqttUser,
                                            @RequestParam("mqttPasswd") String mqttPasswd,
                                            @RequestParam("webPort") int webPort,
                                            @RequestParam("adminPass") String adminPass) {

        if (!adminPass.equals("")) {
            User user = userRepo.findByEmail("admin@localhost.com");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            user.setPassword(adminPass);
            userRepo.save(user);
        }

        config.setSqlHost(sqlHost);
        config.setSqlPort(sqlPort);
        config.setSqlUser(sqlUser);
        config.setSqlDatabase(sqlDatabase);
        if (!sqlPasswd.equals(""))
            config.setSqlPassword(sqlPasswd);

        config.setMqttHost(mqttHost);
        config.setMqttUser(mqttUser);
        config.setMqttPort(mqttPort);
        if (!mqttPasswd.equals(""))
            config.setMqttPassword(mqttPasswd);
        config.setWebPort(webPort);

        try {
            config.save();
        } catch (IOException e) {
            System.out.println("Save failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(value = "/voiceCredentials", produces = "application/json")
    public ResponseEntity<Object> setVoiceCredentials(@RequestParam("voiceFile") MultipartFile voiceFile) {
        if (voiceFile == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        storage.store(voiceFile);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
