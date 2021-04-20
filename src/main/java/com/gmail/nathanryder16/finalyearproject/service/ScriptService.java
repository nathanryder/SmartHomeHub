package com.gmail.nathanryder16.finalyearproject.service;

import com.gmail.nathanryder16.finalyearproject.model.Script;
import com.gmail.nathanryder16.finalyearproject.repository.ScriptRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScriptService {

    @Autowired
    private @Getter ScriptRepository repo;

    public void save(Script script) {
        repo.save(script);
    }


}
