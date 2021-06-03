package com.trustly.challenge.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FileDTO {
    private String extension;
    private Integer count;
    private Integer lines;
    private float bytes;


}
