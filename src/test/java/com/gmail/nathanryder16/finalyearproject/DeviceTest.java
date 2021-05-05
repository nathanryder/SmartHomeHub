package com.gmail.nathanryder16.finalyearproject;

import com.gmail.nathanryder16.finalyearproject.repository.DeviceRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@TestPropertySource(locations="classpath:application-integrationtest.properties")
public class DeviceTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private DeviceRepository repo;

    @Test
    public void createDevice() throws Exception {
        mvc.perform(post("/api/devices/")
            .contentType(MediaType.APPLICATION_JSON)
                .param("deviceID", "test.device")
                .param("displayName", "Test")
                .param("statusTopic", "test/topic")
                .param("method", "read"))
            .andExpect(status().isCreated());
    }

    @Test
    public void failedEditDevice() throws Exception {
        mvc.perform(put("/api/devices/325h34a")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void editDevice() throws Exception {
        mvc.perform(put("/api/devices/s78yue98")
            .contentType(MediaType.APPLICATION_JSON)
                .param("deviceID", "test.device2")
                .param("displayName", "Test2")
                .param("statusTopic", "test/topic")
                .param("method", "read"))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteDevice() throws Exception {
        mvc.perform(delete("/api/devices/764sgfh5"))
                .andExpect(status().isBadRequest());
    }
}

