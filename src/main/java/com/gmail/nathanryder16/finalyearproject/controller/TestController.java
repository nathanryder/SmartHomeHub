package com.gmail.nathanryder16.finalyearproject.controller;

import com.gmail.nathanryder16.finalyearproject.cards.Card;
import com.gmail.nathanryder16.finalyearproject.cards.CardType;
import com.gmail.nathanryder16.finalyearproject.dashboard.Dashboard;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TestController {

    @GetMapping("/test")
    public String test(Model model) {
        List<Card> cards = new ArrayList<>();
//        Card card = new Card("test");
//        cards.add(card);

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
        session.setAttribute("loggedIn", "1");


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
    public String dashboard(HttpSession session, Model model) {
        String userID = (String) session.getAttribute("loggedIn");
        if (userID == null)
            return "redirect:/login/";

        Dashboard dashboard = Dashboard.findDashboard(userID);

        List<String> css = new ArrayList<>();
        List<String> js = new ArrayList<>();
        try {
            for (Card card : dashboard.getCards()) {
                String cssPath = card.getType().getCss();
                if (!css.contains(cssPath))
                    css.add(cssPath);

                String jsPath = card.getType().getJs();
                if (!js.contains(jsPath))
                    js.add(jsPath);

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "error";
        }

        model.addAttribute("availableCards", CardType.values());
        model.addAttribute("dashboard", dashboard);
        model.addAttribute("cssPaths", css);
        model.addAttribute("jsPaths", js);
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
