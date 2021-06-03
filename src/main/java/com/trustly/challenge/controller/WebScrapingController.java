package com.trustly.challenge.controller;

import com.trustly.challenge.dto.FileDTO;
import com.trustly.challenge.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/files")
public class WebScrapingController {

    @Autowired
    private FileService service;

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<FileDTO>> getFilesInformation(@RequestParam String url, @RequestParam(required = false) boolean forceUpdate) throws Exception {
        //String url = "https://github.com/marcusvps/angular-with-json-server/"; //todo receber como parametro
        List<FileDTO> retorno = new ArrayList<>();

        Map<String, List<FileDTO>> mapFilesInUrl = service.getFilesInUrl(url,forceUpdate);
        mapFilesInUrl.forEach((extension, files) -> {
                FileDTO file = new FileDTO();
                file.setExtension(extension);
                file.setCount(files.size());
                file.setBytes(0L);
                file.setLines(0);
                retorno.add(file);
                });


        return new ResponseEntity<>(retorno, HttpStatus.OK);


    }
}
