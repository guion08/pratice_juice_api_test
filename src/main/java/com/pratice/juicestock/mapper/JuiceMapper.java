package com.pratice.juicestock.mapper;

import com.pratice.juicestock.dto.JuiceDTO;
import com.pratice.juicestock.entity.Juice;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface JuiceMapper {

    JuiceMapper INSTANCE = Mappers.getMapper(JuiceMapper.class);

    Juice toModel(JuiceDTO juiceDTO);

    JuiceDTO toDTO(Juice juice);
}
