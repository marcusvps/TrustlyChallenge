package com.trustly.challenge.controller;

import com.trustly.challenge.dto.FileDTO;
import com.trustly.challenge.services.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/files")
public class WebScrapingController {



    @GetMapping
    @ResponseBody
    public ResponseEntity<List<FileDTO>> getFilesInformation(@RequestParam String url, @RequestParam(required = false) boolean forceUpdate) throws Exception {
        return new ResponseEntity<>(FileService.getFilesInUrl(url,forceUpdate), HttpStatus.OK);


    }
}
