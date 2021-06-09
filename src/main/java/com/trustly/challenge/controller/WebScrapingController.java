package com.trustly.challenge.controller;

import com.trustly.challenge.dto.FileDTO;
import com.trustly.challenge.exception.BusinessException;
import com.trustly.challenge.services.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@RestController
@RequestMapping("/files")
public class WebScrapingController {
    List<FileDTO> filesInUrl = new ArrayList<>();


    @GetMapping
    @ResponseBody
    public ResponseEntity<List<FileDTO>> getFilesInformation(@RequestParam String url, @RequestParam(required = false) boolean forceUpdate) throws BusinessException {
        ExecutorService executor = Executors.newCachedThreadPool();
        List<FileDTO> fileDTOS;

        try {
            Callable<List<FileDTO>> task = () -> FileService.getFilesInUrl(url, forceUpdate);
            Future<List<FileDTO>> future = executor.submit(task);

            System.out.println("future done? " + future.isDone());
            fileDTOS = future.get();
            System.out.println("future done? " + future.isDone());
            System.out.print("result: " + fileDTOS);


        } catch (InterruptedException | ExecutionException e) {
            throw new BusinessException(e.getCause().getMessage());

        }

        return new ResponseEntity<>(fileDTOS, HttpStatus.OK);


    }
}
