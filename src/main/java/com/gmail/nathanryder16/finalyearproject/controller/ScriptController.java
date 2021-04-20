package com.gmail.nathanryder16.finalyearproject.controller;

import com.gmail.nathanryder16.finalyearproject.ScriptTrigger;
import com.gmail.nathanryder16.finalyearproject.model.Script;
import com.gmail.nathanryder16.finalyearproject.service.ScriptService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
public class ScriptController {

    @Autowired
    private ScriptService scripts;

    @RequestMapping("/scripts")
    public String scripts(HttpSession session, Model model) {
        if (session.getAttribute("loggedIn") == null)
            return "redirect:/login/";

        session.setAttribute("page", "scripts");
        model.addAttribute("scripts", scripts.getRepo().findAll());
        return "scripts";
    }

    @RequestMapping("/addScript")
    public String addScript(HttpSession session, Model model) {
        if (session.getAttribute("loggedIn") == null)
            return "redirect:/login/";

        session.setAttribute("page", "scripts");
        model.addAttribute("triggers", ScriptTrigger.values());
        return "addScript";
    }

    @RequestMapping("/editScript/{scriptId}")
    public String editScript(@PathVariable(value="scriptId") int scriptId, HttpSession session, Model model) {
        if (session.getAttribute("loggedIn") == null)
            return "redirect:/login/";

        Script script = scripts.getRepo().findById(scriptId);
        if (script == null) {
            return "redirect:/dashboard/";
        }

        session.setAttribute("page", "scripts");
        model.addAttribute("script", script);
        model.addAttribute("triggers", ScriptTrigger.values());
        return "editScript";
    }

    @DeleteMapping(value = "api/scripts/{scriptId}", produces = "application/json")
    public ResponseEntity deleteScript(@PathVariable(value="scriptId") int scriptId) {

        Script script = scripts.getRepo().findById(scriptId);
        if (script == null) {
            JsonObject msg = new JsonParser().parse("{\"error\": \"Script not found!\"}").getAsJsonObject();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }

        scripts.getRepo().delete(script);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(value = "api/scripts/", produces = "application/json")
    public ResponseEntity addScript(@RequestParam("name") String name,
                                    @RequestParam("description") String desc,
                                    @RequestParam("trigger") ScriptTrigger trigger,
                                    @RequestParam("triggerValue") String triggerValue,
                                    @RequestParam("enabled") int enabled,
                                    @RequestParam("script") String rawScript) {

        Script script = new Script(name, desc, trigger, rawScript);
        script.setTriggerValue(triggerValue);
        script.setEnabled(enabled);

        scripts.save(script);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping(value = "api/scripts/{scriptId}", produces = "application/json")
    public ResponseEntity editScript(@PathVariable(value="scriptId") int scriptId,
                                     @RequestParam("name") String name,
                                     @RequestParam("description") String desc,
                                     @RequestParam("trigger") ScriptTrigger trigger,
                                     @RequestParam("triggerValue") String triggerValue,
                                     @RequestParam("enabled") int enabled,
                                     @RequestParam("script") String rawScript) {

        Script script = scripts.getRepo().findById(scriptId);
        if (script == null) {
            JsonObject msg = new JsonParser().parse("{\"error\": \"Script not found!\"}").getAsJsonObject();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }

        script.setName(name);
        script.setDescription(desc);
        script.setTriggerType(trigger);
        script.setTriggerValue(triggerValue);
        script.setEnabled(enabled);
        script.setScript(rawScript);

        System.out.println("Enabled: " + enabled);
        scripts.save(script);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
