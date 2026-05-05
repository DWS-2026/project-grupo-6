package com.example.projectgrupo6.services;

//import java.lang.foreign.Linker.Option;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.projectgrupo6.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.projectgrupo6.domain.Comment;
import com.example.projectgrupo6.domain.Product;
import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.repositories.CommentRepository;

import jakarta.transaction.Transactional;

@Service
public class CommentService {
    
    @Autowired
    private CommentRepository repository;
    
    @Autowired
    private ProductService productService;

    @Autowired
    private UserRepository userRepository;

    public Comment save(Comment comment) {
        repository.save(comment);
        return comment;
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Comment getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional
    public Comment addComment(Long userid, Long productid, String content) {
        
        Optional <Product> productOpt = productService.getById(productid);
        Optional <User> userOpt = userRepository.findById(userid);
        
        if (productOpt.isPresent() && userOpt.isPresent()) {
            
            Product product = productOpt.get();
            User user = userOpt.get();

            Comment comment = new Comment(content, user.getUsername(), user, product);
            
            product.addComment(comment);
            product.setReviewCount(product.getReviewCount() + 1);

            user.addReview(comment);

            repository.save(comment);
            return comment;
        }
        return null;
    }

    @Transactional
    public Comment editComment(Long commentId, Long userId, String newContent){
        Optional<Comment> commentOpt = repository.findById(commentId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (commentOpt.isPresent() && userOpt.isPresent()) {
            
            User user = userOpt.get();
            Comment comment = commentOpt.get();

            if (comment.getOwner() != null && (comment.getOwner().getId().equals(user.getId()) || user.getRoles().stream()
                        .anyMatch(r -> r.equalsIgnoreCase("ADMIN")))){
                comment.setContent(newContent);
                repository.save(comment);
                return comment;
            }
        }
        return null;
    }

   /* public boolean validateAuthorInComment(long commentId, long userId){
        Optional<Comment> commentOptional = repository.findById(commentId);
        Optional<User> userOptional = userRepository.findById(userId);

        if (commentOptional.isPresent() && userOptional.isPresent()){
            User user = userOptional.get();
            Comment comment = commentOptional.get();

            if (comment.getOwner() != null && (comment.getOwner().getId().equals(user.getId()) || user.getRoles().stream()
                    .anyMatch(r -> r.equalsIgnoreCase("ADMIN")))){
                return true;
            }
        } else {
            return false;
        }
        return false;
    }*/

    @Transactional
    public void deleteComment(Long commentId, Long userId){
        Optional<Comment> commentOpt = repository.findById(commentId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (commentOpt.isPresent() && userOpt.isPresent()) {
            
            User user = userOpt.get();
            Comment comment = commentOpt.get();

            if (comment.getOwner() != null && (comment.getOwner().getId().equals(user.getId()) || user.getRoles().stream()
                        .anyMatch(r -> r.equalsIgnoreCase("ADMIN")))){
                if (comment.getProduct() != null) {
                    comment.getProduct().removeComment(comment);
                    comment.getProduct().setReviewCount(Math.max(0, comment.getProduct().getReviewCount() - 1));
                }
                repository.delete(comment);
            }
        }
    }

    public List<Map<String, Object>> getCommentsForProductView(Long productId, Long loggedInUserId) {
        
        List<Comment> comments = repository.findByProductId(productId);
        
        List<Map<String, Object>> viewList = new ArrayList<>();

        for (Comment comment : comments) {
            Map<String, Object> map = new HashMap<>();
            
            // We put in pure data of the comment
            map.put("id", comment.getId());
            map.put("content", comment.getContent());
            map.put("productId", productId);
            
            if (comment.getOwner() != null) {
                map.put("ownerName", comment.getOwner().getUsername());
                
                boolean isMine = (loggedInUserId != null && comment.getOwner().getId().equals(loggedInUserId));
                map.put("isMine", isMine);
            } else {
                map.put("ownerName", comment.getAuthor());
                map.put("isMine", false);
            }

            viewList.add(map);
        }

        return viewList;
    }

    public List<Comment> findAllByUser (User user){
        return repository.findByOwner(user);
    }

    public List <Comment> findAllByUser (Long userId){
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isPresent()) {
            return repository.findByOwner(userOpt.get());
        } else {
            return new ArrayList<>();
        }
    }

    public Page<Comment> getAllPaged(Pageable pageable){
        return repository.findAll(pageable);
    }
    public List<Comment> findAllByProductId (Long productId){
        return repository.findByProductId(productId);
    }

    public void deleteList (List<Comment> comments){
        repository.deleteAll(comments);
    }
}
