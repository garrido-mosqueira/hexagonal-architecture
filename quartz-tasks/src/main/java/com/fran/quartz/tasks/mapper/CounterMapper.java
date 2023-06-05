package com.fran.quartz.tasks.mapper;

import com.fran.quartz.tasks.model.Counter;
import com.fran.task.domain.model.Task;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CounterMapper {

    Counter toCounter(Task task);

    Task toDomain(Counter counter);

    List<Task> toDomain(List<Counter> counter);

}
