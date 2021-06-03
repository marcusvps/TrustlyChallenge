package com.trustly.challenge.controller;

import com.trustly.challenge.dto.FileDTO;
import com.trustly.challenge.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/files")
public class WebScrapingController {

    @Autowired
    private FileService service;

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<FileDTO>> getFilesInformation() throws Exception {
        String url = "https://github.com/marcusvps/angular-with-json-server/tree/master/json-server"; //todo receber como parametro
        List<FileDTO> retorno = new ArrayList<>();

        Map<String, List<FileDTO>> mapFilesInUrl = service.getFilesInUrl(url);
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
