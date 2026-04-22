package com.example.projectgrupo6.repositories;

import com.example.projectgrupo6.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByUser (long id);
}
