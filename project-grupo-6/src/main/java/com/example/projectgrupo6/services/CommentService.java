package com.example.projectgrupo6.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.projectgrupo6.domain.Comment;
import com.example.projectgrupo6.repositories.CommentRepository;

import jakarta.transaction.Transactional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository repository;

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
    public void addComment(Long user){
        
    }

}
