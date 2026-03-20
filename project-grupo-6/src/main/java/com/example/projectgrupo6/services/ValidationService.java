package com.example.projectgrupo6.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.regex.Pattern;

@Service
public class ValidationService {
    public void validateProduct(String name,String description, Double price, MultipartFile image){
        if(name == null || name.trim().isEmpty()){
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (name.length() < 3){
            throw new IllegalArgumentException("Product name must be at least 3 characters long");
        }
        if(description == null || description.trim().isEmpty()){
            throw new IllegalArgumentException("Product description cannot be empty");
        }
        if(price == null || price <= 0){
            throw new IllegalArgumentException("Product price must be greater than zero");
        }  
        validateImage(image);
    }    
    public void validateUser(String username,String email,String password){
        if(username == null || username.trim().isEmpty()){
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (username.length() < 3){
            throw new IllegalArgumentException("Username must be at least 3 characters long");
        }
        if (!isValidEmail(email)){
            throw new IllegalArgumentException("Invalid email format");
        }
        if(!isValidPassword(password)){
            throw new IllegalArgumentException("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one number");
        }
    }
    private void validateImage(MultipartFile image){
        if(image == null || image.isEmpty()){
            throw new IllegalArgumentException("Product image cannot be empty");
        }
        String contentType = image.getContentType();
        if(contentType == null || (!contentType.startsWith("image/"))){
            throw new IllegalArgumentException("File must be an image");
        }
        long maxSize = 5 * 1024 * 1024;
        if (image.getSize() > maxSize){
            throw new IllegalArgumentException("Image size must be less than 5MB");
        }
    }
    private boolean isValidEmail(String email){
        if(email == null)            return false;
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.matches(emailRegex, email);
    }
    private boolean isValidPassword(String password){
        if(password == null)            return false;
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
        return Pattern.matches(passwordRegex, password);
    }
}
