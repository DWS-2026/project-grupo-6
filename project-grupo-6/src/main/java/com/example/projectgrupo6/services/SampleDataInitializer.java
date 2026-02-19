package com.example.projectgrupo6.services;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.projectgrupo6.domain.Comment;
import com.example.projectgrupo6.domain.Product;
import com.example.projectgrupo6.domain.User;

import jakarta.annotation.PostConstruct;

@Component
public class SampleDataInitializer {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @PostConstruct
    public void initData() {
        // 1. HX416 Assault AEG 
        Product p1 = new Product();
        p1.setName("HX416 Assault AEG");
        p1.setDescription("Precision-engineered for the modern operator. High-performance AEG.");
        p1.setPrice(249.00);
        p1.setCategory("Airsoft Guns");
        p1.setPowerSource("AEG");
        p1.setBrand("Specna Arms");
        p1.addColor("Black");
        p1.setReviewCount(24);
        p1.setImages(Arrays.asList("/css/img/HX416AssaultAEG.png", "/css/img/HX416AssaultAEG_2.png", "/css/img/HX416AssaultAEG_3.png", "/css/img/HX416AssaultAEG_4.png")); // Varias imágenes
        productService.save(p1);

        // 2. Specter M4 CQB 
        Product p2 = new Product();
        p2.setName("Specter M4 CQB");
        p2.setDescription("Compact and reliable for close quarters combat.");
        p2.setPrice(190.99);
        p2.setCategory("Airsoft Guns");
        p2.setPowerSource("AEG");
        p2.setBrand("G&G Armament");
        p2.addColor("Tan / Coyote");
        p2.setReviewCount(48);
        p2.setImages(Arrays.asList("/css/img/SpecterM4CQBpng.png","/css/img/SpecterM4CQBpng_2.png","/css/img/SpecterM4CQBpng_3.png","/css/img/SpecterM4CQBpng_4.png"));
        productService.save(p2);

        // 3. AKR74 Tactical 
        Product p3 = new Product();
        p3.setName("AKR74 Tactical");
        p3.setDescription("Modernized AK platform with tactical rails.");
        p3.setPrice(229.99);
        p3.setCategory("Airsoft Guns");
        p3.setPowerSource("AEG");
        p3.setBrand("Cyma");
        p3.addColor("Black");
        p3.setReviewCount(74);
        p3.setImages(Arrays.asList("/css/img/AKR74Tactical.png","/css/img/AKR74Tactical_2.png","/css/img/AKR74Tactical_3.png","/css/img/AKR74Tactical_4.png"));
        productService.save(p3);

        // 4. Raptor G17 GBB 
        Product p4 = new Product();
        p4.setName("Raptor G17 GBB");
        p4.setDescription("Gas Blowback pistol with realistic recoil.");
        p4.setPrice(129.90);
        p4.setCategory("Airsoft Guns");
        p4.setPowerSource("GBB");
        p4.setBrand("Umarex");
        p4.addColor("Black");
        p4.setImages(Arrays.asList("/css/img/RaptorG17GBB.png","/css/img/RaptorG17GBB_2.png","/css/img/RaptorG17GBB_3.png","/css/img/RaptorG17GBB_4.png"));
        productService.save(p4);
    
        // 2. CREAR USUARIOS
        User u1 = new User();
        u1.setFirstname("Alex Murphy");
        u1.setLastname("Robocop");
        u1.setUsername("robocop");
        u1.setEmail("alex.murphy@robocop.com");
        userService.save(u1);

        User u2 = new User();
        u2.setFirstname("John Wick");
        u2.setLastname("Baba Yaga");
        u2.setUsername("johnwick");
        u2.setEmail("john.wick@matrix.com");
        userService.save(u2);
    
        // 3. AÑADIR COMENTARIOS (Nueva sección)
        // Comentario de Robocop en el Fusil HX416
        Comment c1 = new Comment("Highly accurate, feels just like the real thing.", u1.getUsername(), u1, p1);
        commentService.save(c1);

        // Comentario de John Wick en la Pistola G17
        Comment c2 = new Comment("Reliable sidearm. The blowback is very crisp.", u2.getUsername(), u2, p4);
        commentService.save(c2);

        // Otro comentario para el mismo producto
        Comment c3 = new Comment("I prefer pencils, but this gun is a close second.", u2.getUsername(), u2, p1);
        commentService.save(c3);

    }
}