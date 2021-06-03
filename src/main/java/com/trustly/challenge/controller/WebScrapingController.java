package com.trustly.challenge.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ws")
public class WebScrapingController {

    @GetMapping(path = "/test")
    @ResponseBody
    public ResponseEntity<String> test(){
        return new ResponseEntity<>("Is online", HttpStatus.OK);

    }
}
