package com.example.projectgrupo6.dto.mappers;

import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.dto.UserDTO;
import com.example.projectgrupo6.dto.basicDtos.UserBasicDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(User user);
    UserBasicDTO toBasicDTO (User user);

    List<UserDTO> toDTOs (Collection<User> users);
    List<UserBasicDTO> toBasicDTOs (Collection<User> users);

    @Mapping(target = "profileImage", ignore = true)
    User toDomain (UserDTO userDTO);

    User toDomainFromBasic (UserBasicDTO userBasicDTO);
}
