package com.gmail.nathanryder16.finalyearproject;

import com.gmail.nathanryder16.finalyearproject.model.User;
import com.gmail.nathanryder16.finalyearproject.repository.UserRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@TestPropertySource(locations="classpath:application-integrationtest.properties")
public class UserTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository repo;

    @Test
    public void createUser() throws Exception {
        MockHttpServletResponse res = mvc.perform(post("/api/users/")
            .contentType(MediaType.APPLICATION_JSON)
                .param("email", "testuser@localhost.com")
                .param("password", "test"))
            .andExpect(status().isCreated())
            .andReturn().getResponse();

        assertNotNull(res);
        JsonObject json = new JsonParser().parse(res.getContentAsString()).getAsJsonObject();
        assertTrue(json.keySet().contains("id"));

        MockHttpServletResponse res2 = mvc.perform(post("/api/users/")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "testuser@localhost.com")
                .param("password", "test"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse();

        assertNotNull(res2);
        JsonObject bad = new JsonParser().parse(res2.getContentAsString()).getAsJsonObject();
        assertTrue(bad.keySet().contains("error"));
    }

    @Test
    public void failedEditUser() throws Exception {
        MockHttpServletResponse res = mvc.perform(put("/api/users/47u5948035323")
            .contentType(MediaType.APPLICATION_JSON)
                .param("email", "changedtest@localhost.com")
                .param("password", "test2")
                .param("validated", "1"))
            .andExpect(status().isBadRequest())
                .andReturn().getResponse();

        assertNotNull(res);
        JsonObject json = new JsonParser().parse(res.getContentAsString()).getAsJsonObject();

        assertTrue(json.keySet().contains("error"));
    }

    @Test
    public void editUser() throws Exception {
        User user = repo.findByEmail("testuser@localhost.com");
        mvc.perform(put("/api/users/" + user.getId())
            .contentType(MediaType.APPLICATION_JSON)
                .param("email", "changedtest@localhost.com")
                .param("password", "test2")
                .param("validated", "1"))
            .andExpect(status().isOk());
    }

    @Test
    public void invalidAuth() throws Exception {
        MockHttpServletResponse res = mvc.perform(post("/api/users/auth/")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "testuser@localhost.com")
                .param("password", "test"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse();

        assertNotNull(res);
        JsonObject json = new JsonParser().parse(res.getContentAsString()).getAsJsonObject();
        assertTrue(json.keySet().contains("error"));
    }

    @Test
    public void deleteUser() throws Exception {
        User user = repo.findByEmail("changedtest@localhost.com");
        mvc.perform(delete("/api/users/" + user.getId()))
                .andExpect(status().isOk());
    }
}
