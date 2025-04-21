package com.example.talkdemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class MainContraller {
    @GetMapping("/hello")
    public String hello() {
        return " ";
    }
}