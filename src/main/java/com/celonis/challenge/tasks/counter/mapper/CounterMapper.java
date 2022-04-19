package com.celonis.challenge.tasks.counter.mapper;


import com.celonis.challenge.domain.model.CounterTask;
import com.celonis.challenge.tasks.counter.model.Counter;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CounterMapper {

    Counter toCounter(CounterTask counterTask);

    CounterTask toDomain(Counter counter);

    List<CounterTask> toDomain(List<Counter> counter);
}
