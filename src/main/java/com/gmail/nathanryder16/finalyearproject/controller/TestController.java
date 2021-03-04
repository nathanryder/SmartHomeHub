package com.gmail.nathanryder16.finalyearproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class TestController {

    @GetMapping("/test")
    public String test(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "test";
    }

    @RequestMapping("register")
    public String register(HttpSession session) {
        if (session.getAttribute("loggedIn") != null)
            return "redirect:/dashboard/";

        return "register";
    }

    @RequestMapping("/login")
    public String login(HttpSession session) {
        session.setAttribute("loggedIn", true); //TODO remove

        if (session.getAttribute("loggedIn") != null)
            return "redirect:/dashboard/";

        return "login";
    }

    @RequestMapping("/")
    public String index(HttpSession session) {
        if (session.getAttribute("loggedIn") == null)
            return "redirect:/login/";

        return "redirect:/dashboard/";
    }

    @RequestMapping("/dashboard")
    public String dashboard(HttpSession session) {
        if (session.getAttribute("loggedIn") == null)
            return "redirect:/login/";

        session.setAttribute("page", "dashboard");
        return "dashboard";
    }

    @RequestMapping("/floorplan")
    public String floorplan(HttpSession session) {
        if (session.getAttribute("loggedIn") == null)
            return "redirect:/login/";

        session.setAttribute("page", "floorplan");
        return "floorplan";
    }

}
