package com.example.projectgrupo6.services;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ValidationService {

    //Whitelist for HTML
    private static final Safelist safelist = Safelist.none()
            .addTags("p", "br")
            .addTags("b", "strong", "i", "em", "u")
            .addTags("ul", "ol", "li")
            .addTags("a")
            .addAttributes("a", "href")
            .addProtocols("a", "href", "http", "https");

    //Sanitizing method using JSoup library
    public static String cleanAndSanitize(String html) {
        if (html == null) return "";
        html = html
                .replaceAll("<p><br></p>", "")
                .replaceAll("(<p>\\s*</p>)+", "")
                .trim();
        return Jsoup.clean(html, safelist);
    }

    public static List<String> sanitizeAll(String... inputs) {
        return Arrays.stream(inputs)
                .map(ValidationService::cleanAndSanitize)
                .toList();
    }

    public void validateProduct(String name,String description, Double price, MultipartFile[] images){
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
        if (images != null && (images.length < 0 || images.length > 4)) {
            throw new IllegalArgumentException("You must upload between 0 and 4 images");
        }
        if(images != null){ 
            for (MultipartFile img : images) {
                if (img != null && !img.isEmpty()) {
                    validateImage(img); 
                }
            }
        }

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

    public boolean isValidQuantity(int quantity){
        return quantity>0 && quantity<10000;
    }

    public boolean isValidStock(int stock){
        return stock>0 && stock<10000;
    }

    public boolean isValidPrice(double prize){
        return prize>0.0 && prize<100000.0;
    }
}
