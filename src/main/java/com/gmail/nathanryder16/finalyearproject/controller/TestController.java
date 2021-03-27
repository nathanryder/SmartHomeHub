package com.gmail.nathanryder16.finalyearproject.controller;

import com.gmail.nathanryder16.finalyearproject.cards.Card;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TestController {

    @GetMapping("/test")
    public String test(Model model) {
        List<Card> cards = new ArrayList<>();
        Card card = new Card("test");
        cards.add(card);

        model.addAttribute("cards", cards);
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
