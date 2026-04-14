package com.example.projectgrupo6.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.projectgrupo6.services.CartService;
import com.example.projectgrupo6.services.UserService;
import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.dto.basicDtos.CartItemBasicDTO;
import com.example.projectgrupo6.dto.ProductCartDTO;
import com.example.projectgrupo6.domain.CartItem;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/cart")
public class CartRestController {
        @Autowired
        private CartService cartService;
        @Autowired
        private UserService userService;
    
    private User getSessionUser(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal == null) return null;
        return userService.findByUsername(principal.getName()).orElse(null);
    }
    private CartItemBasicDTO toDTO(CartItem item){
        return new CartItemBasicDTO(
            item.getId(),
            new ProductCartDTO(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getPrice()
            ),
            item.getQuantity()
        );
    }
    private List<CartItemBasicDTO> toDTOList(List<CartItem> cartItems){
        return cartItems.stream().map(this::toDTO).toList();
    }
    @GetMapping("")
    public ResponseEntity<List<CartItemBasicDTO>> getCart(HttpServletRequest request){
        User user = getSessionUser(request);
        if (user == null) return ResponseEntity.status(401).build();
        List<CartItem> cartItems = cartService.getCartItems(user.getId());
        return ResponseEntity.ok(toDTOList(cartItems));
    }
    @PostMapping("/add/{productId}")
    public ResponseEntity<Void> addToCart(@PathVariable Long productId, @RequestParam(defaultValue = "1") int quantity, HttpServletRequest request){
        User user = getSessionUser(request);
        if (user == null) return ResponseEntity.status(401).build();

        cartService.addProductToCart(user.getId(), productId, quantity);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long productId, HttpServletRequest request){
        User user = getSessionUser(request);
        if (user == null) return ResponseEntity.status(401).build();

        cartService.removeProductFromCart(user.getId(), productId);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/update/{productId}")
    public ResponseEntity<Void> updateCartItem(@PathVariable Long productId, @RequestParam int quantity, HttpServletRequest request){
        User user = getSessionUser(request);
        if (user == null) return ResponseEntity.status(401).build();

        cartService.updateProductQuantity(user.getId(), productId, quantity);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(HttpServletRequest request){
        User user = getSessionUser(request);
        if (user == null) return ResponseEntity.status(401).build();

        cartService.clearCart(user.getId());
        return ResponseEntity.ok().build();
    }
}

