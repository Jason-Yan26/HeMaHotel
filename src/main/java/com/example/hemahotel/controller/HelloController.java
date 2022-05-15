package com.example.hemahotel.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@CrossOrigin(origins = "*")
public class HelloController {

    @ResponseBody
    @PostMapping("/hello")
    public String hello(){
        return "Hello World 6666";
    }
}
