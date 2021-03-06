package com.gmail.nathanryder16.finalyearproject.repository;

import com.gmail.nathanryder16.finalyearproject.model.Device;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DeviceRepository extends CrudRepository<Device, String> {

    Device findByDeviceID(String deviceID);

}
