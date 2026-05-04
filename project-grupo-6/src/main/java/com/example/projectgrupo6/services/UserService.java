package com.example.projectgrupo6.services;

import com.example.projectgrupo6.domain.*;
import com.example.projectgrupo6.repositories.CartRepository;
import com.example.projectgrupo6.repositories.UserRepository;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.security.Principal;
import java.sql.Blob;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CommentService commentService;
    @Autowired
    private OrderService orderService;


    public List<User> getAllUsers (){
        return userRepository.findAll();
    }

    public Page<User> getAllPaged(Pageable pageable){
        return userRepository.findAll(pageable);
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

        Optional<Cart> op = cartRepository.findByUserId(user.getId());
        if(op.isPresent()){
            Cart deleteCart = op.get();
            cartRepository.delete(deleteCart);
        }

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

    public Optional<User> findByEmail (String email){
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUsername (String username){
        return userRepository.findByUsername(username);
    }

    public boolean correctLoginInput (String email, String password){
        return email.isEmpty()&&password.isEmpty();
    }

    public boolean logincheck (User user, String password){
        return user.getEncodedPassword().equals(password);
    }

    public boolean checkCreatePassword (String password, String confirmPassword){
        return password.equals(confirmPassword);
    }

    public User updateUser(User user, String firstName,String lastName, String email, String password, MultipartFile imageFile) throws IOException {
        if (!firstName.isEmpty()) {
            user.setFirstname(firstName);
        }
        if (!lastName.isEmpty()) {
            user.setLastname(lastName);
        }
        if (!email.isEmpty()) {
            user.setEmail(email);
        }
        if (!password.isEmpty()) {
            user.setEncodedPassword(password);
        }
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                user.setProfileImage(new SerialBlob(imageFile.getBytes()));
            } catch (Exception e) {
                throw new IOException("Failed to create image blob", e);
            }
        }
        return userRepository.save(user);
    }

    public Long getCurrentUserId(HttpServletRequest session) {
        User user = getSessionUser(session);
        return (user != null) ? user.getId() : null;
    }

    public boolean validateSession (User sessionUser, Long sessionId){
        return sessionUser.getId().equals(sessionId);
    }

    public boolean checkIfAdmin (User sessionUser){
        return sessionUser != null && sessionUser.getRoles() != null &&
                sessionUser.getRoles().stream()
                        .anyMatch(r -> r.equalsIgnoreCase("ADMIN"));
    }

    public User getSessionUser(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return null;
        }
        return findByEmail(principal.getName()).orElse(null);
    }

    //REST part
    public User addImageToUser(long id, Image image) {
        User user = userRepository.findById(id).orElseThrow();
        user.setProfileImage(image.getImageFile());
        userRepository.save(user);

        return user;
    }

    public User removeImageUser(long userId, Image image) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setProfileImage(null);
        userRepository.save(user);

        return user;
    }
}
