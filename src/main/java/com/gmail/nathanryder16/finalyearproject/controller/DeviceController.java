package com.gmail.nathanryder16.finalyearproject.controller;

import com.gmail.nathanryder16.finalyearproject.model.Device;
import com.gmail.nathanryder16.finalyearproject.service.DeviceService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class DeviceController {

    @Autowired
    private DeviceService devices;

    @RequestMapping("/devices")
    public String devices(HttpSession session, Model model) {
        if (session.getAttribute("loggedIn") == null)
            return "redirect:/login/";

        session.setAttribute("page", "devices");
        model.addAttribute("devices", devices.getRepo().findAll());
        return "devices";
    }

    @RequestMapping("/addDevice")
    public String addDevice(HttpSession session) {
        if (session.getAttribute("loggedIn") == null)
            return "redirect:/login/";

        session.setAttribute("page", "devices");
        return "addDevice";
    }

    @PostMapping(value = "api/devices/", produces = "application/json")
    public ResponseEntity addDevice(@RequestParam("deviceID") String deviceID,
                                    @RequestParam("displayName") String displayName,
                                    @RequestParam("statusTopic") String statusTopic,
                                    @RequestParam("method") String method,
                                    @RequestParam(value = "updateTopic", required = false) String updateTopic,
                                    @RequestParam(value = "activePayload", required = false) String activePayload,
                                    @RequestParam(value = "inactivePayload", required = false) String inactivePayload) {

        if (deviceID.contains(" ")) {
            JsonObject msg = new JsonParser().parse("{\"error\": \"Device ID cannot contain spaces!\"}").getAsJsonObject();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }

        Device device = new Device(deviceID, displayName, statusTopic, method);

        if (method.equals("write")) {
            if (updateTopic == null || activePayload == null || inactivePayload == null) {
                JsonObject msg = new JsonParser().parse("{\"error\": \"Please fill out all required fields!\"}").getAsJsonObject();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
            }

            device.setUpdateTopic(updateTopic);
            device.setActivePayload(activePayload);
            device.setInactivePayload(inactivePayload);
        }

        devices.save(device);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
