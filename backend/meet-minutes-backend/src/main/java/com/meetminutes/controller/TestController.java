package com.meetminutes.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/hello")
    public String test() {
        return "hello from meetminutes";
    }

    @GetMapping("/api/test")
    public String test2() {
        return "test controller testing";
    }
}