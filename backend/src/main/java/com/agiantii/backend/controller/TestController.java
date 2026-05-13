package com.agiantii.backend.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// 【已废弃】调试控制器，已从 Spring 容器移除
// @RestController
public class TestController {
    @GetMapping("test")
    public String test(){
        return "test";
    }

}
