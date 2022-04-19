package com.celonis.challenge.persistence.mapper;

import com.celonis.challenge.domain.model.CounterTask;
import com.celonis.challenge.persistence.entities.CounterEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CounterEntityMapper {

    CounterTask toDomain(CounterEntity counterTask);

    List<CounterTask> toDomain(List<CounterEntity> counterTask);

    CounterEntity toEntity(CounterTask counterTask);

}
