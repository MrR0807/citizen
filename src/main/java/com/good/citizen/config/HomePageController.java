package com.good.citizen.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomePageController {

    // redirects to swagger ui from base path
    @RequestMapping("/")
    public String home() {
        return "redirect:/swagger-ui.html";
    }
}