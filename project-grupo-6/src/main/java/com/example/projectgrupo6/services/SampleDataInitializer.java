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
        p1.setImages(Arrays.asList("/css/img/HX416AssaultAEG.png", "/css/img/HX416-side.png")); // Varias imágenes
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


        //CREATE USERS
        User u1 = new User();
        u1.setFirstname("Alex");
        u1.setLastname("Murphy");
        u1.setUsername("a.murph$");
        u1.setEmail("lex_murph@airsoft.com");
        u1.setProfileImage("pfp1.jpg");
        //Encode when ready
        u1.setPassword("1234");
        u1.setRol("Admin");
        userService.save(u1);

        User u2 = new User();
        u2.setFirstname("Charlie");
        u2.setLastname("Brown");
        u2.setUsername("puppycharlie");
        u2.setEmail("charlie.brown@mailcute.com");
        u2.setProfileImage("pfp2.jpg");
        //Encode when ready
        u2.setPassword("5678");
        u2.setRol("User");
        userService.save(u2);

        User u3 = new User();
        u3.setFirstname("Pandora");
        u3.setLastname("James");
        u3.setUsername("pandyJames");
        u3.setEmail("pan.james@cozymail.com");
        u3.setProfileImage("pfp3.jpg");
        //Encode when ready
        u3.setPassword("134340");
        u3.setRol("User");
        userService.save(u3);

    }
}