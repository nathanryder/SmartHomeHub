package com.gmail.nathanryder16.finalyearproject.repository;

import com.gmail.nathanryder16.finalyearproject.ScriptTrigger;
import com.gmail.nathanryder16.finalyearproject.model.Script;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ScriptRepository extends CrudRepository<Script, String> {

    Script findById(int id);

    List<Script> findByTriggerType(ScriptTrigger triggerType);

}
