package com.good.citizen.micrometertest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController("${application.endpoints.second}")
public class SecondEndPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecondEndPoint.class);

    private static final Random ran = new Random();

    @GetMapping("/fast")
    public String getHello() {
        LOGGER.info("Got request: {}");
        return "hello";
    }

    @GetMapping("/slow")
    public String getSlowHello() throws InterruptedException {
        LOGGER.info("Got request: {}");
        TimeUnit.SECONDS.sleep(2);
        return "hello";
    }

    @GetMapping("/sometimes-slow")
    public String getSometimesSlowHello() throws InterruptedException {
        LOGGER.info("Got request: {}");
        if (ran.nextBoolean()) {
            TimeUnit.SECONDS.sleep(2);
        }
        return "hello";
    }
}