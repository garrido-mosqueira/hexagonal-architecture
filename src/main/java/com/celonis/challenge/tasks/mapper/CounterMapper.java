package com.celonis.challenge.tasks.mapper;

import com.celonis.challenge.domain.model.Task;
import com.celonis.challenge.tasks.model.Counter;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CounterMapper {

    Counter toCounter(Task task);

    Task toDomain(Counter counter);

    List<Task> toDomain(List<Counter> counter);

}
