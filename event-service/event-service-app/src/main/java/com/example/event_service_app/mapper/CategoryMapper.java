package com.example.event_service_app.mapper;

import com.example.event_service_app.entity.Category;
import com.example.event_service_client.dto.CategoryDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toDto(Category category);

    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateCategoryFromDto(CategoryDto dto, @MappingTarget Category category);
}

