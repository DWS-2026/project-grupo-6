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
    @PostMapping("/user/{id}/cart")
    public ResponseEntity<CartItemBasicDTO> addToCart(@PathVariable long id, @RequestBody CartItemBasicDTO cartItemBasicDTO) {
        CartItem item = cartItemMapper.toDomainFromBasic(cartItemBasicDTO);
        cartService.addProductToCart(id, item.getProduct().getId(), item.getQuantity());
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(item.getProduct().getId()).toUri();;
        return ResponseEntity.created(location).body(cartItemMapper.toBasicDTO(item));
    }

    //PUT
    //Change product in cart
    @PutMapping("/user/{id}/cart")
    public CartItemBasicDTO updateCartItem(@PathVariable long id, @RequestBody CartItemBasicDTO cartItemBasicDTO) {
        CartItem item = cartItemMapper.toDomainFromBasic(cartItemBasicDTO);
        cartService.updateProductQuantity(id, item.getProduct().getId(), item.getQuantity());
        return cartItemMapper.toBasicDTO(item);
    }

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
    @DeleteMapping("/user/{id}/item/{itemId}")
    public ResponseEntity<CartItemBasicDTO> removeFromCart(@PathVariable long id, @PathVariable Long itemId) {
        if(userService.getById(id).isPresent()) {
            CartItemBasicDTO item = cartItemMapper.toBasicDTO(cartService.getCartItem(id, itemId));
            cartService.removeItemFromCart(id, itemId);
            return ResponseEntity.ok(item);
        } else {
            throw new NoSuchElementException();
        }
    }
}