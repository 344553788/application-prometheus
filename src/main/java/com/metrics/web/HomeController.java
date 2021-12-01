package com.metrics.web;

import com.metrics.annotation.Count;
import com.metrics.annotation.Monitor;
import com.metrics.annotation.Tp;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Tp
    @Count
    @RequestMapping("/endpointA")
    public void handlerA() throws InterruptedException {
        logger.info("/endpointA");
        Thread.sleep(RandomUtils.nextLong(0, 5000));
    }


}
