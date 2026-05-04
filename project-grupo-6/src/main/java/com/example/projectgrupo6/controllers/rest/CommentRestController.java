package com.example.projectgrupo6.controllers.rest;

import com.example.projectgrupo6.domain.Comment;
import com.example.projectgrupo6.dto.CommentDTO;
import com.example.projectgrupo6.dto.basicDtos.CommentBasicDTO;
import com.example.projectgrupo6.dto.mappers.CommentMapper;
import com.example.projectgrupo6.services.CommentService;
import com.example.projectgrupo6.services.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RequestMapping("/api/v1/comments")
@RestController
public class CommentRestController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ValidationService validationService;

    //GET
    //All
    @GetMapping("/")
    public Page<CommentBasicDTO> getAllComments (Pageable pageable){
        return commentService.getAllPaged(pageable).map(commentMapper::toBasicDTO);
    }

    //By id
    @GetMapping("/{id}")
    public CommentDTO getCommentById (@PathVariable long id){
        return commentMapper.toDTO(commentService.getById(id));
    }

    //POST
    // // // // // // // //
    // Uncompleted:
    @PostMapping("/product/{productId}/user/{userId}")
    public ResponseEntity<CommentBasicDTO> createComment(@PathVariable long productId, @PathVariable long userId, @RequestBody CommentBasicDTO commentDTO){
        Comment comment = commentMapper.toDomainFromBasic(commentDTO);
        String cleanContent = ValidationService.cleanAndSanitize(comment.getContent());
        comment.setContent(cleanContent);
        //add more validation/cleaning (?)
        Comment savedComment = commentService.save(comment);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(savedComment.getId()).toUri();
        return ResponseEntity.created(location).body(commentMapper.toBasicDTO(savedComment));
    }

    //PUT
    @PutMapping("/{id}")

    //DELETE
    //Error
    @DeleteMapping("/{id}")
    public ResponseEntity<CommentBasicDTO> deleteComment (@PathVariable long id){
        CommentBasicDTO commentBasicDTO = commentMapper.toBasicDTO(commentService.getById(id));
        commentService.delete(id);
        return ResponseEntity.ok(commentBasicDTO);
    }
}
