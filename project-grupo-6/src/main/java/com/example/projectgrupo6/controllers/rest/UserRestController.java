package com.example.projectgrupo6.controllers.rest;

import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.services.UserService;
import com.example.projectgrupo6.dto.UserDTO;
import com.example.projectgrupo6.dto.basicDtos.UserBasicDTO;
import com.example.projectgrupo6.dto.mappers.UserMapper;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RequestMapping("/api/v1/users")
@RestController
public class UserRestController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    private User getSessionUser(HttpServletRequest request) {
        return userService.getSessionUser(request);
    }
    @GetMapping("/me") 
    public ResponseEntity<UserDTO> getCurrentUser(HttpServletRequest request) {
        User user = getSessionUser(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(userMapper.toDTO(user));
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserBasicDTO> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(userMapper.toBasicDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("")
    public ResponseEntity<List<UserBasicDTO>> getAllUsers() {
        return ResponseEntity.ok(userMapper.toBasicDTOs(userService.getAllUsers()));
    }
    @PostMapping("")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        try {
            User user = userMapper.toDomain(userDTO);
            User savedUser = userService.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDTO(savedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO, HttpServletRequest request) {
        try {
            User sessionUser = getSessionUser(request);
            if (sessionUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            if (!sessionUser.getId().equals(id) && !userService.checkIfAdmin(sessionUser)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return userService.findById(id).map(user -> {
                user.setFirstname(userDTO.firstname());
                user.setLastname(userDTO.lastname());
                user.setEmail(userDTO.email());
                User updated = userService.save(user);
                return ResponseEntity.ok(userMapper.toDTO(updated));
            })
            .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, HttpServletRequest request)
    {
        try {
            User sessionUser = getSessionUser(request);
            if (sessionUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            if (!sessionUser.getId().equals(id) && !userService.checkIfAdmin(sessionUser)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            userService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}

