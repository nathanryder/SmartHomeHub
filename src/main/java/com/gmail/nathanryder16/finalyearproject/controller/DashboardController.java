package com.gmail.nathanryder16.finalyearproject.controller;

import com.gmail.nathanryder16.finalyearproject.cards.Card;
import com.gmail.nathanryder16.finalyearproject.cards.CardType;
import com.gmail.nathanryder16.finalyearproject.model.Dashboard;
import com.gmail.nathanryder16.finalyearproject.model.Device;
import com.gmail.nathanryder16.finalyearproject.repository.DeviceRepository;
import com.gmail.nathanryder16.finalyearproject.repository.UserRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;

@Controller
@RequestMapping("api/dashboards")
public class DashboardController {

    @Autowired
    private DeviceRepository deviceRepo;

    @Autowired
    private UserRepository userRepo;

    @PostMapping(value = "/", produces = "application/json")
    public ResponseEntity<Object> addCard(@RequestParam("cardType") String type,
                                          @RequestParam("deviceID") String deviceID,
                                          HttpSession session) {


        Device device = deviceRepo.findByDeviceID(deviceID);
        if (device == null) {
            JsonObject msg = new JsonParser().parse("{\"error\": \"invalid device id\"}").getAsJsonObject();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }

        //TODO enable
//        if (userRepo.findById(userid).isEmpty()) {
//            JsonObject msg = new JsonParser().parse("{\"error\": \"invalid user id\"}").getAsJsonObject();
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
//        }
        String userid = (String) session.getAttribute("loggedIn");

        CardType cardType = null;
        try {
            cardType = CardType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            JsonObject msg = new JsonParser().parse("{\"error\": \"invalid card type\"}").getAsJsonObject();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }

        Card card = new Card(cardType, device);
        Dashboard dashboard = Dashboard.findDashboard(userid);

        try {
            dashboard.addCard(card);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JsonObject msg = new JsonParser().parse("{\"error\": \"failed to find user dashboard file\"}").getAsJsonObject();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping(value = "/", produces = "application/json")
    public ResponseEntity<Object> deleteCard(@RequestParam("cardID") String cardid, HttpSession session) {

//        if (userRepo.findById(userid).isEmpty()) {
//            JsonObject msg = new JsonParser().sparse("{\"error\": \"invalid user id\"}").getAsJsonObject();
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
//        }

        String userid = (String) session.getAttribute("loggedIn");
        Dashboard dashboard = Dashboard.findDashboard(userid);

        try {
            dashboard.deleteCard(cardid);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JsonObject msg = new JsonParser().parse("{\"error\": \"failed to find user dashboard file\"}").getAsJsonObject();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping(value = "/", produces = "application/json")
    public ResponseEntity<Object> moveCard(@RequestParam("cardID") String cardid,
                                           @RequestParam("direction") String direction,
                                           HttpSession session) {

//        if (userRepo.findById(userid).isEmpty()) {
//            JsonObject msg = new JsonParser().sparse("{\"error\": \"invalid user id\"}").getAsJsonObject();
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
//        }

        String userid = (String) session.getAttribute("loggedIn");
        Dashboard dashboard = Dashboard.findDashboard(userid);

        try {

            if (direction.equalsIgnoreCase("up")) {
                dashboard.moveCardUp(cardid);
            } else if (direction.equalsIgnoreCase("down")) {
                dashboard.moveCardDown(cardid);
            } else {
                JsonObject msg = new JsonParser().parse("{\"error\": \"invalid direction given\"}").getAsJsonObject();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JsonObject msg = new JsonParser().parse("{\"error\": \"failed to find user dashboard file\"}").getAsJsonObject();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
