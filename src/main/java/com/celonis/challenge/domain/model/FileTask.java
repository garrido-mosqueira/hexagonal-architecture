package com.celonis.challenge.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileTask {

    private String id;
    private String name;
    private LocalDate creationDate;
    private String storageLocation;

}
