package com.example.projectgrupo6.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.projectgrupo6.domain.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

}