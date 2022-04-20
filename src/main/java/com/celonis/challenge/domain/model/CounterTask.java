package com.celonis.challenge.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CounterTask {

    private String id;
    private String name;
    private LocalDate creationDate;
    private LocalDateTime lastExecution;
    private Integer begin;
    private Integer finish;
    private Integer progress;
    private String storageLocation;

}
