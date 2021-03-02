package com.haif.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefaultHystrixController {

    private final Logger LOG = LoggerFactory.getLogger(DefaultHystrixController.class);

    @GetMapping("/fallback")
    public ResponseEntity<?> fallback(){

        LOG.error("DefaultHystrixFallBack 降级操作");

        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body("服务繁忙，请稍后重试");
    }
}
