package com.example.projectgrupo6.controllers;

import com.example.projectgrupo6.domain.Image;
import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.services.CartService;
import com.example.projectgrupo6.services.CommentService;
import com.example.projectgrupo6.services.ImageService;
import com.example.projectgrupo6.services.ProductService;
import com.example.projectgrupo6.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ProductService productService;
    
    @Autowired
    private CartService cartService;

    @Autowired
    private CommentService commentService;


}
