package com.trustly.challenge.controller;

import com.trustly.challenge.dto.FileDTO;
import com.trustly.challenge.services.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/files")
public class WebScrapingController {



    @GetMapping
    @ResponseBody
    public ResponseEntity<List<FileDTO>> getFilesInformation(@RequestParam String url, @RequestParam(required = false) boolean forceUpdate) throws Exception {
        List<FileDTO> filesInUrl;
        ExecutorService es = Executors.newCachedThreadPool();
        Future<List<FileDTO>> result = es.submit(() -> FileService.getFilesInUrl(url, forceUpdate));
        filesInUrl = result.get();

        return new ResponseEntity<>(filesInUrl, HttpStatus.OK);





    }
}
