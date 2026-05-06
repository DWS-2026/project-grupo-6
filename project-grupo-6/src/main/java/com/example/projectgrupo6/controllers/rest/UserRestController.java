package com.example.projectgrupo6.controllers.rest;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import com.example.projectgrupo6.domain.Image;
import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.dto.ImageDTO;
import com.example.projectgrupo6.dto.UserDTO;
import com.example.projectgrupo6.dto.basicDtos.UserBasicDTO;
import com.example.projectgrupo6.dto.mappers.ImageMapper;
import com.example.projectgrupo6.dto.mappers.UserMapper;
import com.example.projectgrupo6.security.jwt.RegisterRequest;
import com.example.projectgrupo6.services.ImageService;
import com.example.projectgrupo6.services.UserService;
import com.example.projectgrupo6.services.ValidationService;

import jakarta.servlet.http.HttpServletRequest;

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
    @Autowired
    private ValidationService validationService;
    @Autowired
	private PasswordEncoder passwordEncoder;

    //GET
    //Actual user
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            return ResponseEntity.ok(userMapper.toDTO(userService.getSessionUser(request)));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //All
    @GetMapping("/")
    public Page<UserBasicDTO> getAllUsers(Pageable pageable) {
        return (userService.getAllPaged(pageable)).map(userMapper::toBasicDTO);
    }

    //By id
    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id, HttpServletRequest request) {
        if (!userService.isAuthorized(id, request)) {
            throw new IllegalArgumentException("Access denied to this profile");
        }
        return userMapper.toDTO(userService.findById(id).orElseThrow());
    }

    //POST
    //User
    //Add sanitization
    @PostMapping("/")
    public ResponseEntity<UserBasicDTO> createUser(@RequestBody RegisterRequest userDTO) {
        validationService.validateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getUsername(), userDTO.getEmail());
        if(userService.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("The user with that email already exists");
        }
        
        if(!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords don't match");
        }
        User newUser = new User();
        newUser.setUsername(userDTO.getUsername());
        newUser.setEmail(userDTO.getEmail());
        newUser.setEncodedPassword(passwordEncoder.encode(userDTO.getPassword()));
        
        String assignedRole = (userDTO.getRole() != null && !userDTO.getRole().isEmpty()) 
                              ? userDTO.getRole() 
                              : "USER";
        newUser.setRol(assignedRole);
        
        newUser.setFirstname(userDTO.getFirstName());
        newUser.setLastname(userDTO.getLastName());

        newUser.setProfileImage(imageService.loadImage("defaultUserImage.png"));
        User savedUser = userService.save(newUser);
        
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(savedUser.getId()).toUri();
        return ResponseEntity.created(location).body(userMapper.toBasicDTO(savedUser));
    }

    //image
    //get
    @GetMapping("/{id}/images")
    public ResponseEntity<Object> getProfileImage(@PathVariable long id, HttpServletRequest request) throws SQLException {
        
        if(!userService.isAuthorized(id, request)) {
            throw new IllegalArgumentException("Access denied");
        }

        Optional<User> userOp = userService.findById(id);
        
        if (userOp.isPresent() && userOp.get().getProfileImage() != null) {
            
            Blob imageBlob = userOp.get().getProfileImage();
            Resource imageResource = new InputStreamResource(imageBlob.getBinaryStream());
            
            
            MediaType mediaType = MediaTypeFactory
                    .getMediaType(imageResource)
                    .orElse(MediaType.IMAGE_JPEG);
                    
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(imageResource);
        } else {
            
            throw new RuntimeException("Profile image for user " + id + " not found");
        }
    }
    //Image
    @PostMapping("/{id}/images")
    public ResponseEntity<ImageDTO> createImage(@PathVariable long id, @RequestParam MultipartFile imageFile, HttpServletRequest request) throws IOException{
        
        if (!userService.isAuthorized(id, request)) {
            throw new IllegalArgumentException("Access denied: You can't add an image to another user");
        }
        
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
    //Add sanitization
    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable Long id, @RequestBody RegisterRequest userDTO, HttpServletRequest request) {

        if (!userService.isAuthorized(id, request)) {
            throw new IllegalArgumentException("Access denied: You can't edit another user's profile");
        }

        User existingUser = userService.getById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + id));


        validationService.validateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getUsername(), userDTO.getEmail());


        if (!existingUser.getEmail().equals(userDTO.getEmail()) && 
            userService.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("The new email is already in use by another user");
        }

        existingUser.setUsername(userDTO.getUsername());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setFirstname(userDTO.getFirstName());
        existingUser.setLastname(userDTO.getLastName());

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
                throw new IllegalArgumentException("Passwords don't match");
            }
            existingUser.setEncodedPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        if (userDTO.getRole() != null && !userDTO.getRole().isEmpty()) {
            existingUser.setRol(userDTO.getRole());
        }

        userService.save(existingUser);
        return userMapper.toDTO(existingUser);
    }

    //Image
    @PutMapping("/{id}/images")
    public ResponseEntity<Object> replaceImageFile(@PathVariable long id,
                                                   @RequestParam MultipartFile imageFile,
                                                   HttpServletRequest request) throws IOException {
        //imageService.replaceImageFile(id, imageFile.getInputStream());
        if (!userService.isAuthorized(id, request)) {
            throw new IllegalArgumentException("Access denied");
        }
        userService.save(userService.findById(id).orElseThrow(), imageFile);
        return ResponseEntity.noContent().build();
    }

    //DELETE
    //User
    @DeleteMapping("/{id}")
    public ResponseEntity<UserBasicDTO> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        
        if (!userService.isAuthorized(id, request)) {
            throw new IllegalArgumentException("Access denied: You can't delete another user's account");
        }

        UserBasicDTO user = userMapper.toBasicDTO(userService.findById(id).orElseThrow());
        userService.deleteById(id);
        return ResponseEntity.ok(user);
    }

    //Image
    @DeleteMapping("/{id}/images")
    public ImageDTO deleteProfileImage(@PathVariable long id, HttpServletRequest request) throws IOException {
        
        if (!userService.isAuthorized(id, request)) {
            throw new IllegalArgumentException("Access denied");
        }
        User user = userService.findById(id).orElseThrow();
        Blob avatar = user.getProfileImage();
        if (avatar == null) {
            throw new RuntimeException("The user does not have a profile picture");
        }

        user.setProfileImage(null);
        userService.save(user);

        Image image = new Image();
        image.setImageFile(avatar);
        return imageMapper.toDTO(image);
    }
}

