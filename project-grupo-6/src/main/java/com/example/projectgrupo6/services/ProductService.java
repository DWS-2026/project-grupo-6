package com.example.projectgrupo6.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.example.projectgrupo6.domain.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.projectgrupo6.domain.Product;
import com.example.projectgrupo6.repositories.ProductRepository;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public Page<Product> getAll(Pageable page){return repository.findAll(page);}

    public List<Product> filterByPowerSource(String source) {
        return repository.findByPowerSource(source);
    }

    public List<Product> filterByCategory(String category) {
        return repository.findByCategory(category);
    }

    public Optional<Product> getById(Long id) {
        return repository.findById(id);
    }

    public void save(Product product) {
        repository.save(product);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<Product> getThreeRandomProducts() {
        List<Product> allProducts = repository.findAll();
        
        List<Product> randomList = new ArrayList<>(allProducts);
        
        Collections.shuffle(randomList);
        
        return randomList.subList(0, Math.min(3, randomList.size()));
    }

    public void setAttbProduct (Product product, String name, String brand, double price, String categ, String powerS, String desc, String spec){
        product.setName(name);
        product.setBrand(brand);
        product.setPrice(price);
        product.setCategory(categ);
        product.setPowerSource(powerS);
        product.setDescription(desc);
        product.setSpecification(spec);
    }

    public long checkNumberImages (MultipartFile[] imageFiles){
        long validImagesCount = 0;
        if (imageFiles != null) {
            for (MultipartFile file : imageFiles) {
                if (!file.isEmpty()) validImagesCount++;
            }
        }
        return validImagesCount;
    }

    public boolean setProductImages(Product p, MultipartFile[] imageFiles){
        long numberImages = checkNumberImages(imageFiles);
        boolean error = true;
        if(numberImages <= 4){
            try{
                p.setImages(new ArrayList<>()); //Deletes prior images
                for (MultipartFile file : imageFiles) {
                    if (!file.isEmpty()) {
                        byte[] bytes = file.getBytes();
                        java.sql.Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
                        p.getImages().add(blob);
                    }
                }
                return false;

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return error;
    }

}