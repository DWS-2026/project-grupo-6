package com.example.projectgrupo6.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.projectgrupo6.services.CartService;
import com.example.projectgrupo6.services.UserService;
import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.dto.CartDTO;
import com.example.projectgrupo6.dto.mappers.CartMapper;
import com.example.projectgrupo6.domain.Cart;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/v1/carts")
public class CartRestController {
        @Autowired
        private CartService cartService;
        @Autowired
        private UserService userService;

        @Autowired
        private CartMapper cartMapper;
    
    private User getSessionUser(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal == null) return null;
        return userService.findByUsername(principal.getName()).orElse(null);
    }
    @GetMapping("")
    public ResponseEntity<CartDTO> getCart(HttpServletRequest request){
        User user = getSessionUser(request);
        if(user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Cart cart = cartService.getCartByUserId(user.getId());
        if(cart == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(cartMapper.toDTO(cart));
    }
    @PostMapping("/add/{productId}")
    public ResponseEntity<Void> addToCart(@PathVariable Long productId, @RequestParam(defaultValue = "1") int quantity, HttpServletRequest request) {
        try{
            User user = getSessionUser(request);
            if(user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            cartService.addProductToCart(user.getId(), productId, quantity);
            return ResponseEntity.ok().build();
        }catch(RuntimeException e){
            return ResponseEntity.status(400).body(null);
        }
    }
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long productId, HttpServletRequest request) {
        try{
            User user = getSessionUser(request);
            if(user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            cartService.removeProductFromCart(user.getId(), productId);
            return ResponseEntity.ok().build();
        }catch(RuntimeException e){
            return ResponseEntity.status(400).body(null);
        }
    }
    @PutMapping("/update/{productId}")
    public ResponseEntity<Void> updateCartItem(@PathVariable Long productId, @RequestParam int quantity, HttpServletRequest request) {
        try{
            User user = getSessionUser(request);
            if(user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            cartService.updateProductQuantity(user.getId(), productId, quantity);
            return ResponseEntity.ok().build();
        }catch(RuntimeException e){
            return ResponseEntity.status(400).body(null);
        }
    }
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(HttpServletRequest request) {
        try{
            User user = getSessionUser(request);
            if(user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            cartService.clearCart(user.getId());
            return ResponseEntity.ok().build();
        }catch(RuntimeException e){
            return ResponseEntity.status(400).body(null);
        }
    }
}