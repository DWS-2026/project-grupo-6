package com.example.projectgrupo6.services;

import com.example.projectgrupo6.domain.Image;
import com.example.projectgrupo6.repositories.ImageRepository;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;

@Service
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;

    public Image createImage(MultipartFile imageFile) throws IOException {
        Image image = new Image();

        try {
            image.setImageFile(new SerialBlob(imageFile.getBytes()));
        } catch (Exception e) {
            throw new IOException("Failed to create image", e);
        }

        imageRepository.save(image);

        return image;
    }

    public Resource getImageFile(long id) throws SQLException {
        Image image = imageRepository.findById(id).orElseThrow();

        if (image.getImageFile() != null) {
            return new InputStreamResource(image.getImageFile().getBinaryStream());
        } else {
            throw new RuntimeException("Image file not found");
        }
    }
    public Blob loadImage(String fileName) {
    try {
        // La ruta empieza directamente desde lo que hay dentro de 'resources'
        org.springframework.core.io.Resource resource = new org.springframework.core.io.ClassPathResource("static/css/img/" + fileName);
        if (resource.exists()) {
            byte[] bytes = resource.getInputStream().readAllBytes();
            return new javax.sql.rowset.serial.SerialBlob(bytes);
        } else {
            System.err.println("⚠️ No se encontró el archivo en el classpath: static/css/img/" + fileName);
        }
    } catch (Exception e) {
        System.err.println("❌ Error procesando " + fileName + ": " + e.getMessage());
    }
    return null; // Si falla, el usuario simplemente no tendrá foto
}
}
