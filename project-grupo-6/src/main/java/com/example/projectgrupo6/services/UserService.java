package com.example.projectgrupo6.services;

import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers (){
        return userRepository.findAll();
    }

    public void save(User user){
        userRepository.save(user);
    }

    public void delete(User user){
        userRepository.delete(user);
    }

    public Optional <User> getById (Long id){
        return userRepository.findById(id);
    }

    public void deleteById (Long id){
        userRepository.deleteById(id);
    }

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
        oldUser.setRol(newUser.getRol());

        userRepository.save(oldUser);
    }


}
