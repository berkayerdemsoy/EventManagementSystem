package com.example.user_service_app.mapper;

import com.example.user_service_app.entity.User;
import com.example.user_service_app.entity.UserProfile;
import com.example.user_service_client.dto.UserCreateDto;
import com.example.user_service_client.dto.UserResponseDto;
import com.example.user_service_client.dto.UserUpdateDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // ─── Entity → ResponseDto ───
    @Mapping(source = "userProfile.firstName", target = "firstName")
    @Mapping(source = "userProfile.lastName", target = "lastName")
    @Mapping(source = "userProfile.phoneNumber", target = "phoneNumber")
    @Mapping(source = "verified", target = "verified")
    UserResponseDto toResponseDto(User user);

    // ─── CreateDto → User Entity (profil alanları hariç) ───
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "userProfile", ignore = true)
    @Mapping(target = "verified", constant = "false")
    User toEntity(UserCreateDto dto);

    // ─── CreateDto → UserProfile Entity ───
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    UserProfile toUserProfile(UserCreateDto dto);

    // ─── UpdateDto → mevcut User Entity'sine güncelleme ───
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "userProfile", ignore = true)
    @Mapping(target = "verified", ignore = true)
    void updateUserFromDto(UserUpdateDto dto, @MappingTarget User user);

    // ─── UpdateDto → mevcut UserProfile Entity'sine güncelleme ───
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateUserProfileFromDto(UserUpdateDto dto, @MappingTarget UserProfile profile);
}
