package com.example.projectgrupo6.repositories;

import com.example.projectgrupo6.domain.Image;
import com.example.projectgrupo6.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    //Optional<Image> findByUser (User user);
    //Image doesnt have a user field, its the user that has an image field, so we need to find the image by the user id
}
