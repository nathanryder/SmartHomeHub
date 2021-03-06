package com.gmail.nathanryder16.finalyearproject.service;

import com.gmail.nathanryder16.finalyearproject.model.Device;
import com.gmail.nathanryder16.finalyearproject.repository.DeviceRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceService {

    @Autowired
    private @Getter DeviceRepository repo;

    public void save(Device device) {
        repo.save(device);
    }

}
