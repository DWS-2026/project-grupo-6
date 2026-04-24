package com.example.projectgrupo6.services;

import com.example.projectgrupo6.domain.Image;
import com.example.projectgrupo6.domain.Product;
import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.repositories.ImageRepository;
import com.example.projectgrupo6.repositories.ProductRepository;
import com.example.projectgrupo6.repositories.UserRepository;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public Image createImage(MultipartFile imageFile) throws IOException { //save
        Image image = new Image();

        try {
            image.setImageFile(new SerialBlob(imageFile.getBytes()));
        } catch (Exception e) {
            throw new IOException("Failed to create image", e);
        }

        imageRepository.save(image);
        return image;
    }

    //For one image
    public Resource getImageFile(long id) throws SQLException { //show
        Optional<Image> image = imageRepository.findById(id);

        if (image.isPresent()) {
            return new InputStreamResource(image.get().getImageFile().getBinaryStream());
        } else {
            throw new RuntimeException("Image file not found");
            //return new ClassPathResource("static/images/default-profile.png");
        }
    }

    //For multiple images
    public Resource getImageFile(long productId, int index) throws SQLException {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Depends on the images in Product
        List<Blob> images = product.getImages();

        if (images == null || images.isEmpty() || index >= images.size()) {
            return new ClassPathResource("static/css/img/default-product.png");
        }

        Blob image = images.get(index);
        return new InputStreamResource(image.getBinaryStream());
    }

    public Blob loadImage(String fileName) { //Image by default
    try {
        // Route starts directly from what's inside 'resources'
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
        return null; //If it fails, it won't have image
    }

    //For an specific product image given an index, if it doesn't exist, it will return the default image
    public Resource getImageResources(List<Blob> images, int index) throws SQLException{
        Blob imageBlob = images.get(index);
        return new InputStreamResource(imageBlob.getBinaryStream());
    }

    //REST part:
    public Image getImage(long id) {
        return imageRepository.findById(id).orElseThrow();
    }

    public Image createImage(InputStream inputStream) throws IOException {
        Image image = new Image();
        try {
            image.setImageFile(new SerialBlob(inputStream.readAllBytes()));
        } catch (Exception e) {
            throw new IOException("Failed to create image", e);
        }

        imageRepository.save(image);
        return image;
    }

    public void replaceImageFile(long id, InputStream inputStream) throws IOException {
        Image image = imageRepository.findById(id).orElseThrow();

        try {
            image.setImageFile(new SerialBlob(inputStream.readAllBytes()));
        } catch (Exception e) {
            throw new IOException("Failed to create image", e);
        }
        imageRepository.save(image);
    }

    public Image deleteImage(long id) {
        Image image = imageRepository.findById(id).orElseThrow();
        imageRepository.deleteById(id);
        return image;
    }

   public Blob getFromUser(long userId) {
        
        User user = userRepository.findById(userId).orElseThrow();
        
        Blob avatar = user.getProfileImage();
        
        if (avatar == null) {
            throw new RuntimeException("El usuario no tiene foto de perfil");
 }
        
        return avatar;
    }
}
