package com.example.projectgrupo6.controllers.rest;

import com.example.projectgrupo6.domain.CartItem;
import com.example.projectgrupo6.domain.Product;
import com.example.projectgrupo6.dto.basicDtos.CartItemBasicDTO;
import com.example.projectgrupo6.dto.basicDtos.ProductBasicDTO;
import com.example.projectgrupo6.dto.mappers.CartItemMapper;
import com.example.projectgrupo6.dto.mappers.ProductMapper;
import com.example.projectgrupo6.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import com.example.projectgrupo6.services.CartService;
import com.example.projectgrupo6.services.UserService;
import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.dto.CartDTO;
import com.example.projectgrupo6.dto.mappers.CartMapper;
import com.example.projectgrupo6.domain.Cart;
import jakarta.servlet.http.HttpServletRequest;

import java.net.URI;
import java.security.Principal;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.ws.rs.Path;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;


@RestController
@RequestMapping("/api/v1/carts")
public class CartRestController {
    @Autowired
    private CartService cartService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private CartItemMapper cartItemMapper;
    @Autowired
    private ProductMapper productMapper;

    //GET
    //Get all
    @GetMapping("/")
    public Page<CartDTO> getCarts(Pageable pageable){
        return cartService.getCartPage(pageable).map(cartMapper::toDTO);
    }

    //Get by user
    @GetMapping("/user/{id}")
    public ResponseEntity<CartDTO> getCart(@PathVariable long id){
        Cart cart = cartService.getCartByUserId(id);
        if(cart == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(cartMapper.toDTO(cart));
    }

    //POST
    //Add product to cart
    /// ///////////////////////// FIX
    // Use cartItem and get quantity instead of RequestParam
    @PostMapping("/user/{id}/product/{productId}")
    public ResponseEntity<ProductBasicDTO> addToCart(@PathVariable long id, @PathVariable long productId, @RequestParam(defaultValue = "1") int quantity, @RequestBody ProductBasicDTO productBasicDTO) {
        cartService.addProductToCart(id, productId, quantity);

        Product prod = productService.getById(productId).orElseThrow();
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(prod.getId()).toUri();;
        return ResponseEntity.created(location).body(productMapper.toBasicDTO(prod));
    }

    //PUT
    //Change product in cart
    @PutMapping("/user/{id}/product/{productId}")
    public CartItemBasicDTO updateCartItem(@PathVariable long id, @PathVariable Long productId, @RequestParam int quantity) {
        if(productService.getById(productId).isPresent()) {
            cartService.updateProductQuantity(id, productId, quantity);
            CartItem cartItem = cartService.getCartItem(id, productId);
            return cartItemMapper.toBasicDTO(cartItem);
        } else {
            throw new NoSuchElementException();
        }
    }
    /// ////////////////////////////////

    //DELETE
    //Delete Cart
    @DeleteMapping("/user/{id}")
    public ResponseEntity<CartDTO> clearCart(@PathVariable long id) {
        if(userService.getById(id).isPresent()) {
            CartDTO cart = cartMapper.toDTO(cartService.getCartByUserId(id));
            cartService.clearCart(id);
            return ResponseEntity.ok(cart);
        } else {
            throw new NoSuchElementException();
        }
    }

    //Delete Product From Cart
    @DeleteMapping("/user/{id}/product/{productId}")
    public ResponseEntity<ProductBasicDTO> removeFromCart(@PathVariable long id, @PathVariable Long productId) {
        if(userService.getById(id).isPresent()) {
            ProductBasicDTO prod = productMapper.toBasicDTO(productService.getById(productId).orElseThrow());
            cartService.removeProductFromCart(id, productId);
            return ResponseEntity.ok(prod);
        } else {
            throw new NoSuchElementException();
        }
    }
}