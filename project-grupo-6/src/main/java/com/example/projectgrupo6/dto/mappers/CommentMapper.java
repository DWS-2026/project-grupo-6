package com.example.projectgrupo6.dto.mappers;

import com.example.projectgrupo6.domain.Comment;
import com.example.projectgrupo6.dto.CommentDTO;
import com.example.projectgrupo6.dto.basicDtos.CommentBasicDTO;
import org.mapstruct.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentDTO toDTO(Comment comment);
    CommentBasicDTO toBasicDTO (Comment comment);

    List<CommentDTO> toDTOs (Collection<Comment> comments);
    List<CommentBasicDTO> toBasicDTOs (Collection<Comment> comments);

    Comment toDomain (CommentDTO commentDTO);
    Comment toDomainFromBasic (CommentBasicDTO commentBasicDTO);
}
