package com.gmail.nathanryder16.finalyearproject.controller;

import com.gmail.nathanryder16.finalyearproject.cards.Card;
import com.gmail.nathanryder16.finalyearproject.cards.CardType;
import com.gmail.nathanryder16.finalyearproject.model.Dashboard;
import com.gmail.nathanryder16.finalyearproject.model.User;
import com.gmail.nathanryder16.finalyearproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class TestController {

    @Autowired
    private UserService users;

    @GetMapping("/test")
    public String test(Model model) {
        List<Card> cards = new ArrayList<>();

        model.addAttribute("cards", cards);
        return "test";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.setAttribute("loggedIn", null);
        return "redirect:/login/";
    }

    @RequestMapping("register")
    public String register(HttpSession session) {
        if (session.getAttribute("loggedIn") != null)
            return "redirect:/dashboard/";

        return "register";
    }

    @RequestMapping("/login")
    public String login(HttpSession session) {
//        session.setAttribute("loggedIn", "admin@localhost.com");

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

    @RequestMapping("/users")
    public String users(HttpSession session, Model model) {
        if (session.getAttribute("loggedIn") == null)
            return "redirect:/login/";

        model.addAttribute("users", users.findAll());
        session.setAttribute("page", "users");
        return "users";
    }

    @RequestMapping("/editUser/{userId}")
    public String users(@PathVariable("userId") String userId, HttpSession session, Model model) {
        if (session.getAttribute("loggedIn") == null)
            return "redirect:/login/";

        Optional<User> user = users.findById(userId);
        if (user.isEmpty()) {
            return "users";
        }

        model.addAttribute("user", user.get());
        return "editUser";
    }

    @RequestMapping("/addUser")
    public String addUser(HttpSession session) {
        if (session.getAttribute("loggedIn") == null)
            return "redirect:/login/";

        return "addUser";
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
