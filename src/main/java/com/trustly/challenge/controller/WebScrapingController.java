package com.trustly.challenge.controller;

import com.trustly.challenge.dto.FileDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/files")
public class WebScrapingController {

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<FileDTO>> getFilesInformation(){
        FileDTO fileDTO = new FileDTO();
        fileDTO.setCount(1);
        fileDTO.setExtension(".java");
        fileDTO.setLines(145);
        fileDTO.setBytes(493349L);

        FileDTO fileDTO2 = new FileDTO();
        fileDTO2.setCount(1);
        fileDTO2.setExtension(".java");
        fileDTO2.setLines(145);
        fileDTO2.setBytes(493349L);

        return new ResponseEntity<>(Arrays.asList(fileDTO, fileDTO2), HttpStatus.OK);


    }
}
