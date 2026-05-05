package com.example.projectgrupo6.dto.mappers;

import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.dto.ImageDTO;
import com.example.projectgrupo6.dto.UserDTO;
import com.example.projectgrupo6.dto.basicDtos.UserBasicDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.sql.Blob;
import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface UserMapper {


    @Mapping(target = "profileImage", expression = "java(mapProfileImage(user))")
    UserDTO toDTO(User user);
    UserBasicDTO toBasicDTO (User user);

    List<UserDTO> toDTOs (Collection<User> users);
    List<UserBasicDTO> toBasicDTOs (Collection<User> users);

    @Mapping(target = "profileImage", ignore = true)
    User toDomain (UserDTO userDTO);

    User toDomainFromBasic (UserBasicDTO userBasicDTO);

    // Personalized mapping logic
    default ImageDTO mapProfileImage(User user) {
        if (user == null || user.getProfileImage() == null) {
            return null;
        }
        return new ImageDTO(user.getId());
    }
}
