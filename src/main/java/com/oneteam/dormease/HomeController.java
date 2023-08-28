package com.oneteam.dormease;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
public class HomeController {
    @RequestMapping(value = {"/user", "/user/"})
    public String home() {
        log.info("home");

        return "home";
    }
}