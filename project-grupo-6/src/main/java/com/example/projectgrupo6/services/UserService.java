package com.example.projectgrupo6.services;

import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void deleteById (Long id){
        userRepository.deleteById(id);
    }

    public User findByEmail (String email){
        return userRepository.findByEmail(email);
    }

    public boolean logincheck (User user, String password){
        return user.getPassword().equals(password);
    }
}
