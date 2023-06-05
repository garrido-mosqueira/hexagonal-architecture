package com.fran.quartz.tasks.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@RequiredArgsConstructor
public class Counter {
    private String id;
    private String name;
    private Date creationDate;
    private Integer begin;
    private Integer finish;
    private Integer progress;
}
