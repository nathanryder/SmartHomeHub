package com.gmail.nathanryder16.finalyearproject;

import com.gmail.nathanryder16.finalyearproject.model.Script;
import com.gmail.nathanryder16.finalyearproject.model.User;
import com.gmail.nathanryder16.finalyearproject.repository.ScriptRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@TestPropertySource(locations="classpath:application-integrationtest.properties")
public class ScriptTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ScriptRepository repo;

    @Test
    public void failCreateScript() throws Exception {
        mvc.perform(post("/api/scripts/")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void createScript() throws Exception {
        MockHttpServletResponse res = mvc.perform(post("/api/scripts/")
            .contentType(MediaType.APPLICATION_JSON)
                .param("name", "Test")
                .param("description", "Test Script")
                .param("trigger", ScriptTrigger.TIME.name())
                .param("triggerValue", "14:00")
                .param("enabled", "1")
                .param("script", "print('Ran')"))
            .andExpect(status().isCreated())
            .andReturn().getResponse();

        assertNotNull(res);
        JsonObject json = new JsonParser().parse(res.getContentAsString()).getAsJsonObject();
        assertTrue(json.keySet().contains("id"));
    }

    @Test
    public void failedEditScript() throws Exception {
        mvc.perform(put("/api/scripts/475")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void editScript() throws Exception {
        mvc.perform(put("/api/scripts/1")
            .contentType(MediaType.APPLICATION_JSON)
                .param("name", "Test 2")
                .param("description", "Test Script 2")
                .param("trigger", ScriptTrigger.TIME.name())
                .param("triggerValue", "13:00")
                .param("enabled", "1")
                .param("script", "print('Ran 2')"))
            .andExpect(status().isOk());
    }

    @Test
    public void failDeleteScript() throws Exception {
        mvc.perform(delete("/api/scripts/38959"))
                .andExpect(status().isBadRequest());
    }
}
