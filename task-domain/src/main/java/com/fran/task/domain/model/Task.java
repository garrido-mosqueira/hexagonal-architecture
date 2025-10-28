package com.fran.task.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    private String id;
    private String name;
    private Date creationDate;
    private Date lastExecution;
    private Integer begin;
    private Integer finish;
    private Integer progress;

}
