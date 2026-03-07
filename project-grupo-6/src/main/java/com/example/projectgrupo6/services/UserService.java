package com.example.projectgrupo6.services;

import com.example.projectgrupo6.domain.Comment;
import com.example.projectgrupo6.domain.Image;
import com.example.projectgrupo6.domain.Order;
import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentService commentService;
    @Autowired
    private OrderService orderService;


    public List<User> getAllUsers (){
        return userRepository.findAll();
    }

    public User save(User user){
        userRepository.save(user);
        return user;
    }

    public void save(User user, MultipartFile imageFile) throws IOException {
        if(!imageFile.isEmpty()) {
            try {
                user.setProfileImage(new SerialBlob(imageFile.getBytes()));
            } catch (Exception e) {
                throw new IOException("Failed to create image blob", e);
            }
        }
        userRepository.save(user);
    }

    public void delete(User user){
        //Deletes comments and orders before deleting user
        List<Comment> comments = commentService.findAllByUser(user);
        commentService.deleteList(comments);

        List<Order> orders = orderService.findAllByUser(user);
        orderService.deleteList(orders);

        userRepository.delete(user);
    }

    public void deleteById (Long id){
        Optional<User> optionalUser = userRepository.findById(id);

        if(optionalUser.isPresent()) {
            User userToDelete = optionalUser.get();

            List<Comment> comments = commentService.findAllByUser(userToDelete);
            commentService.deleteList(comments);

            List<Order> orders = orderService.findAllByUser(userToDelete);
            orderService.deleteList(orders);
        }

        userRepository.deleteById(id);
    }

    public Optional <User> getById (Long id){
        return userRepository.findById(id);
    }

    public Optional<User> findById (Long id){return userRepository.findById(id);}

    public User findByEmail (String email){
        return userRepository.findByEmail(email);
    }

    public User findByUsername (String username){
        return userRepository.findByUsername(username);
    }

    public boolean correctLoginInput (String email, String password){
        return email.isEmpty()&&password.isEmpty();
    }

    public boolean logincheck (User user, String password){
        return user.getPassword().equals(password);
    }

    public boolean checkCreatePassword (String password, String confirmPassword){
        return password.equals(confirmPassword);
    }

    public void updateDataUser(User oldUser, User newUser){
        oldUser.setFirstname(newUser.getFirstname());
        oldUser.setLastname(newUser.getLastname());
        oldUser.setUsername(newUser.getUsername());
        oldUser.setEmail(newUser.getEmail());
        oldUser.setPassword(newUser.getPassword());
        oldUser.setProfileImage(newUser.getProfileImage());
        oldUser.setRoles(newUser.getRoles());

        userRepository.save(oldUser);
    }

    public Long getCurrentUserId(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("Unauthenticated user");
        }
        return user.getId();
    }

    public boolean validateSession (User sessionUser, Long sessionId){
        return sessionUser.getId().equals(sessionId);
    }

    public boolean checkIfAdmin (User sessionUser){
        return sessionUser != null && sessionUser.getRoles() != null &&
                sessionUser.getRoles().stream()
                        .anyMatch(r -> r.equalsIgnoreCase("ADMIN"));
    }

    /*  //With Spring Security (?)
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Unauthenticated user");
        }

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return userDetails.getId();
    }*/

}
