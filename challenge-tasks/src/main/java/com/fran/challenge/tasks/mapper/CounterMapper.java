package com.fran.challenge.tasks.mapper;

import com.fran.challenge.domain.model.Task;
import com.fran.challenge.tasks.model.Counter;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface CounterMapper {

    Counter toCounter(Task task);

    Task toDomain(Counter counter);

    List<Task> toDomain(List<Counter> counter);

}
