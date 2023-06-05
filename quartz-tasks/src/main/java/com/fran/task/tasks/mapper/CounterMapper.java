package com.fran.task.tasks.mapper;

import com.fran.task.domain.model.Task;
import com.fran.task.tasks.model.Counter;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CounterMapper {

    Counter toCounter(Task task);

    Task toDomain(Counter counter);

    List<Task> toDomain(List<Counter> counter);

}
