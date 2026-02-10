package com.example.projectgrupo6.services;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.projectgrupo6.domain.Product;

import jakarta.annotation.PostConstruct;

@Component
public class SampleDataInitializer {

    @Autowired
    private ProductService productService;

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
        p2.setImages(Arrays.asList("/css/img/SpecterM4CQBpng.png"));
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
        p3.setImages(Arrays.asList("/css/img/AKR74Tactical.png"));
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
        p4.setImages(Arrays.asList("/css/img/RaptorG17GBB.png"));
        productService.save(p4);
    }
}