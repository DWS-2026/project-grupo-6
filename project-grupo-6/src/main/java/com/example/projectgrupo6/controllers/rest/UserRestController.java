package com.example.projectgrupo6.controllers.rest;

import com.example.projectgrupo6.domain.Image;
import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.dto.ImageDTO;
import com.example.projectgrupo6.dto.mappers.ImageMapper;
import com.example.projectgrupo6.services.ImageService;
import com.example.projectgrupo6.services.UserService;

import jakarta.servlet.http.HttpServletRequest;

import com.example.projectgrupo6.dto.UserDTO;
import com.example.projectgrupo6.dto.basicDtos.UserBasicDTO;
import com.example.projectgrupo6.dto.mappers.UserMapper;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.sql.Blob;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RequestMapping("/api/v1/users")
@RestController
public class UserRestController {
    @Autowired
    private UserService userService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ImageMapper imageMapper;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            return ResponseEntity.ok(userMapper.toDTO(userService.getSessionUser(request)));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //GET
    //All
    @GetMapping("/")
    public Page<UserBasicDTO> getAllUsers(Pageable pageable) {
        return (userService.getAllPaged(pageable)).map(userMapper::toBasicDTO);
    }

    //By id
    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        return userMapper.toDTO(userService.findById(id).orElseThrow());
    }

    //POST
    //User
    @PostMapping("/")
    public ResponseEntity<UserBasicDTO> createUser(@RequestBody UserBasicDTO userDTO) {
        User user = userMapper.toDomainFromBasic(userDTO);
        User savedUser = userService.save(user);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(savedUser.getId()).toUri();
        return ResponseEntity.created(location).body(userMapper.toBasicDTO(savedUser));
    }

    //Image
    @PostMapping("/{id}/image")
    public ResponseEntity<ImageDTO> createImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException{
        if (imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        Image image = imageService.createImage(imageFile);
        userService.addImageToUser(id, image);
        URI location = fromCurrentContextPath()
                .path("/images/{imageId}/media")
                .buildAndExpand(image.getId())
                .toUri();
        return ResponseEntity.created(location).body(imageMapper.toDTO(image));
    }

    //PUT
    //User
    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
       if(userService.getById(id).isPresent()){
           User updatedUser = userMapper.toDomain(userDTO);
           updatedUser.setId(id);
           userService.save(updatedUser);
           return userMapper.toDTO(updatedUser);
       } else {
           throw new NoSuchElementException();
       }
    }

    //Image
    @PutMapping("/{id}/image")
    public ResponseEntity<Object> replaceImageFile(@PathVariable long id,
                                                   @RequestParam MultipartFile imageFile) throws IOException {
        //imageService.replaceImageFile(id, imageFile.getInputStream());
        userService.save(userService.findById(id).orElseThrow(), imageFile);
        return ResponseEntity.noContent().build();
    }

    //DELETE
    //User
    @DeleteMapping("/{id}")
    public ResponseEntity<UserBasicDTO> deleteUser(@PathVariable Long id) {
        UserBasicDTO user = userMapper.toBasicDTO(userService.findById(id).orElseThrow());
        userService.deleteById(id);
        return ResponseEntity.ok(user);
    }

    //Image
    @DeleteMapping("/{id}/image")
    public ImageDTO deleteProfileImage(@PathVariable long id) throws IOException {
        User user = userService.findById(id).orElseThrow();
        Blob avatar = user.getProfileImage();
        if (avatar == null) {
            throw new RuntimeException("El usuario no tiene foto de perfil");
        }

        user.setProfileImage(null);
        userService.save(user);

        Image image = new Image();
        image.setImageFile(avatar);
        return imageMapper.toDTO(image);
    }
}

