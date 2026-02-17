package com.example.projectgrupo6.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.projectgrupo6.domain.Comment;
import com.example.projectgrupo6.domain.Product;
import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.repositories.CommentRepository;
import com.example.projectgrupo6.repositories.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class CommentService {
    
    @Autowired
    private CommentRepository repository;
    
    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    public void save(Comment comment) {
        repository.save(comment);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Comment getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional
    public void addComment(Long userid,Long productid, String content) {
        
        Optional <Product> productOpt = productService.getById(productid);
        Optional <User> userOpt = userService.getById(userid);
        
        if (productOpt.isPresent() && userOpt.isPresent()) {
            
            Product product = productOpt.get();
            User user = userOpt.get();

            Comment comment = new Comment(content, user.getUsername(), user, product);
            
            product.addComment(comment);

            user.addReview(comment);

            repository.save(comment);
        


        }
    }

}
