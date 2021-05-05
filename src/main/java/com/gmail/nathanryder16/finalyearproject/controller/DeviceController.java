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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Optional;

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

    @RequestMapping("/editDevice/{deviceId}")
    public String addDevice(@PathVariable(value="deviceId") String deviceId, HttpSession session, Model model) {
        if (session.getAttribute("loggedIn") == null)
            return "redirect:/login/";

        Optional<Device> device = devices.getRepo().findById(deviceId);
        if (device.isEmpty()) {
            return "redirect:/dashboard/";
        }

        model.addAttribute("device", device.get());
        session.setAttribute("page", "devices");
        return "editDevice";
    }

    @DeleteMapping(value = "api/devices/{deviceId}", produces = "application/json")
    public ResponseEntity deleteDevice(@PathVariable(value="deviceId") String deviceId) {

        Device device = devices.getRepo().findByDeviceID(deviceId);
        if (device == null) {
            JsonObject msg = new JsonParser().parse("{\"error\": \"Device not found!\"}").getAsJsonObject();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }

        devices.getRepo().delete(device);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(value = "api/devices/", produces = "application/json")
    public ResponseEntity addDevice(@RequestParam("deviceID") String deviceID,
                                    @RequestParam("displayName") String displayName,
                                    @RequestParam("statusTopic") String statusTopic,
                                    @RequestParam("method") String method,
                                    @RequestParam(value = "updateTopic", required = false) String updateTopic,
                                    @RequestParam(value = "activePayload", required = false) String activePayload,
                                    @RequestParam(value = "inactivePayload", required = false) String inactivePayload,
                                    @RequestParam(value = "statusPattern", required = false) String statusPattern,
                                    @RequestParam(value = "updatePattern", required = false) String updatePattern) {

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

        }
        device.setActivePayload(activePayload);
        device.setInactivePayload(inactivePayload);

        if (statusPattern != null) {
            device.setStatusPattern(statusPattern);
        }
        if (updatePattern != null) {
            device.setUpdatePattern(updatePattern);
        }

        devices.save(device);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping(value = "api/devices/{deviceId}", produces = "application/json")
    public ResponseEntity editDevice(@PathVariable(value="deviceId") String deviceId,
                                    @RequestParam("deviceID") String newdeviceId,
                                    @RequestParam("displayName") String displayName,
                                    @RequestParam("statusTopic") String statusTopic,
                                    @RequestParam("method") String method,
                                    @RequestParam(value = "updateTopic", required = false) String updateTopic,
                                    @RequestParam(value = "activePayload", required = false) String activePayload,
                                    @RequestParam(value = "inactivePayload", required = false) String inactivePayload,
                                    @RequestParam(value = "statusPattern", required = false) String statusPattern,
                                    @RequestParam(value = "updatePattern", required = false) String updatePattern) {

        if (deviceId.contains(" ")) {
            JsonObject msg = new JsonParser().parse("{\"error\": \"Device ID cannot contain spaces!\"}").getAsJsonObject();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }

        Device device = devices.getRepo().findByDeviceID(deviceId);
        if (device == null) {
            JsonObject msg = new JsonParser().parse("{\"error\": \"Device not found!\"}").getAsJsonObject();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }

        device.setDeviceID(newdeviceId);
        device.setDisplayName(displayName);
        device.setStatusTopic(statusTopic);

        if (method.equals("write")) {
            if (updateTopic == null || activePayload == null || inactivePayload == null) {
                JsonObject msg = new JsonParser().parse("{\"error\": \"Please fill out all required fields!\"}").getAsJsonObject();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
            }

            device.setUpdateTopic(updateTopic);

        }
        device.setActivePayload(activePayload);
        device.setInactivePayload(inactivePayload);

        if (statusPattern != null) {
            device.setStatusPattern(statusPattern);
        }
        if (updatePattern != null) {
            device.setUpdatePattern(updatePattern);
        }

        devices.save(device);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
