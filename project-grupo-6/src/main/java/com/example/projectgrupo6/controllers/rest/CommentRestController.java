package com.example.projectgrupo6.controllers.rest;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import com.example.projectgrupo6.domain.Comment;
import com.example.projectgrupo6.dto.CommentDTO;
import com.example.projectgrupo6.dto.basicDtos.CommentBasicDTO;
import com.example.projectgrupo6.dto.mappers.CommentMapper;
import com.example.projectgrupo6.services.CommentService;
import com.example.projectgrupo6.services.ProductService;
import com.example.projectgrupo6.services.UserService;
import com.example.projectgrupo6.services.ValidationService;

import jakarta.servlet.http.HttpServletRequest;

@RequestMapping("/api/v1/comments")
@RestController
public class CommentRestController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
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
    @PostMapping("/products/{productId}/users/{userId}")
    public ResponseEntity<CommentBasicDTO> createComment(@PathVariable long productId, @PathVariable long userId, @RequestBody CommentBasicDTO commentDTO, HttpServletRequest request){
        
        if(!userService.isAuthorized(userId, request)){
            throw new IllegalArgumentException("Acceso denegado: No puedes crear un comentario en nombre de otro usuario");
        }

        long currentUserId = userService.getCurrentUserId(request);
        
        Comment comment = commentMapper.toDomainFromBasic(commentDTO);
        if(userService.findById(currentUserId).isEmpty()){
            return ResponseEntity.ofNullable(commentDTO); //
        }
        if(productService.getById(productId).isEmpty()){
            return ResponseEntity.ofNullable(commentDTO); //
        }

        String cleanContent = validationService.cleanAndSanitize(comment.getContent());

        Comment savedComment = commentService.addComment(currentUserId, productId, cleanContent);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(savedComment.getId()).toUri();
        return ResponseEntity.created(location).body(commentMapper.toBasicDTO(savedComment));
    }

    //PUT
    @PutMapping("/{id}/users/{userId}")
    public CommentBasicDTO editComment (@PathVariable long id, @PathVariable long userId, @RequestBody CommentBasicDTO commentBasicDTO, HttpServletRequest request){
        
        if (!userService.isAuthorized(userId, request)) {
            throw new IllegalArgumentException("Acceso denegado: No puedes editar el comentario de otro usuario");
        }

        long currentUserId = userService.getCurrentUserId(request);
        
        Comment comment = commentMapper.toDomainFromBasic(commentBasicDTO);
        String newContent = validationService.cleanAndSanitize(comment.getContent());
        Comment saved = commentService.editComment(id, currentUserId, newContent);
        return commentMapper.toBasicDTO(saved);
    }

    //DELETE
    @DeleteMapping("/{id}/users/{userId}")
    public ResponseEntity<CommentBasicDTO> deleteComment (@PathVariable long id, @PathVariable long userId, HttpServletRequest request){
        
        if (!userService.isAuthorized(userId, request)) {
            throw new IllegalArgumentException("Acceso denegado: No puedes eliminar el comentario de otro usuario");
        }
        
        CommentBasicDTO commentBasicDTO = commentMapper.toBasicDTO(commentService.getById(id));
        commentService.deleteComment(id, userId);
        return ResponseEntity.ok(commentBasicDTO);
    }
}
