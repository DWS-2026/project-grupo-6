package com.example.projectgrupo6.controllers.rest;

import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.services.UserService;
import com.example.projectgrupo6.dto.UserDTO;
import com.example.projectgrupo6.dto.basicDtos.UserBasicDTO;
import com.example.projectgrupo6.dto.mappers.UserMapper;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RequestMapping("/api/v1/users")
@RestController
public class UserRestController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    //GET
    //All
    @GetMapping("/")
    public Page<UserBasicDTO> getAllUsers(Pageable pageable) {
        return (userService.getAllPaged(pageable)).map(userMapper::toBasicDTO);
    }

    //By id
    @GetMapping("/{id}")
    public UserBasicDTO getUserById(@PathVariable Long id) {
        return userMapper.toBasicDTO(userService.findById(id).orElseThrow());
    }

    //POST
    @PostMapping("/")
    public ResponseEntity<UserBasicDTO> createUser(@RequestBody UserBasicDTO userDTO) {
        User user = userMapper.toDomainFromBasic(userDTO);
        User savedUser = userService.save(user);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(savedUser.getId()).toUri();
        return ResponseEntity.created(location).body(userMapper.toBasicDTO(savedUser));
    }

    //PUT
    @PutMapping("/{id}")
    public UserBasicDTO updateUser(@PathVariable Long id, @RequestBody UserBasicDTO userDTO) {
       if(userService.getById(id).isPresent()){
           User updatedUser = userMapper.toDomainFromBasic(userDTO);
           updatedUser.setId(id);
           userService.save(updatedUser);
           return userMapper.toBasicDTO(updatedUser);
       } else {
           throw new NoSuchElementException();
       }
    }

    //DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<UserBasicDTO> deleteUser(@PathVariable Long id) {
        UserBasicDTO user = userMapper.toBasicDTO(userService.findById(id).orElseThrow());
        userService.deleteById(id);
        return ResponseEntity.ok(user);
    }
}

