package com.example.projectgrupo6.controllers.rest;

import java.net.URI;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import com.example.projectgrupo6.domain.Cart;
import com.example.projectgrupo6.domain.CartItem;
import com.example.projectgrupo6.dto.CartDTO;
import com.example.projectgrupo6.dto.basicDtos.CartItemBasicDTO;
import com.example.projectgrupo6.dto.mappers.CartItemMapper;
import com.example.projectgrupo6.dto.mappers.CartMapper;
import com.example.projectgrupo6.dto.mappers.ProductMapper;
import com.example.projectgrupo6.services.CartService;
import com.example.projectgrupo6.services.ProductService;
import com.example.projectgrupo6.services.UserService;
import com.example.projectgrupo6.services.ValidationService;

import jakarta.servlet.http.HttpServletRequest;


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
    private ValidationService validationService;

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private CartItemMapper cartItemMapper;
    @Autowired
    private ProductMapper productMapper;

    //GET
    //Get all
    @GetMapping("/")
    public Page<CartDTO> getCarts(Pageable pageable,HttpServletRequest request) {
        return cartService.getCartPage(pageable).map(cartMapper::toDTO);
    }

    //Get by user
    @GetMapping("/users/{id}")
    public ResponseEntity<CartDTO> getCart(@PathVariable long id, HttpServletRequest request) {
        if (!userService.isAuthorized(id, request)) {
            throw new IllegalArgumentException("Access Denied: You don't have permissions on this cart");
        }
        Cart cart = cartService.getCartByUserId(id);
        if(cart == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(cartMapper.toDTO(cart));
    }

    //POST
    //Add product to cart
    @PostMapping("/users/{id}/items")
    public ResponseEntity<CartItemBasicDTO> addToCart(@PathVariable long id, @RequestBody CartItemBasicDTO cartItemBasicDTO, HttpServletRequest request) {
        if(!userService.isAuthorized(id, request)){
            throw new IllegalArgumentException("Access Denied: You don't have permissions on this cart");
        }
        CartItem item = cartItemMapper.toDomainFromBasic(cartItemBasicDTO);
        validationService.validateCart(item);
        cartService.addProductToCart(id, item.getProduct().getId(), item.getQuantity());

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(item.getProduct().getId()).toUri();;
        return ResponseEntity.created(location).body(cartItemMapper.toBasicDTO(item));
        
    }

    //PUT
    //Change product in cart
    @PutMapping("/users/{id}/items/{itemId}")
    public CartItemBasicDTO updateCartItem(@PathVariable long id, @RequestBody CartItemBasicDTO cartItemBasicDTO, HttpServletRequest request) {
        if(!userService.isAuthorized(id, request)){
            throw new IllegalArgumentException("Access Denied: You don't have permissions on this cart");
        }
        CartItem item = cartItemMapper.toDomainFromBasic(cartItemBasicDTO);
        if(!validationService.isValidQuantity(item.getQuantity())){
            item.setQuantity(1);
        }
        cartService.updateProductQuantity(id, item.getProduct().getId(), item.getQuantity());
        return cartItemMapper.toBasicDTO(item);
    }

    //DELETE
    //Delete Cart
    @DeleteMapping("/users/{id}")
    public ResponseEntity<CartDTO> clearCart(@PathVariable long id, HttpServletRequest request) {
        if (!userService.isAuthorized(id, request)) {
            throw new IllegalArgumentException("Access Denied: You don't have permissions on this cart");
        }

        if(userService.getById(id).isPresent()) {
            CartDTO cart = cartMapper.toDTO(cartService.getCartByUserId(id));
            cartService.clearCart(id);
            return ResponseEntity.ok(cart);
        } else {
            throw new NoSuchElementException();
        }
    }

    //Delete Item From Cart
    @DeleteMapping("/users/{id}/items/{itemId}")
    public ResponseEntity<CartItemBasicDTO> removeFromCart(@PathVariable long id, @PathVariable Long itemId, HttpServletRequest request) {
        if (!userService.isAuthorized(id, request)) {
            throw new IllegalArgumentException("Access Denied: You don't have permissions on this cart");
        }
        if(userService.getById(id).isPresent()) {
            CartItemBasicDTO item = cartItemMapper.toBasicDTO(cartService.getCartItem(id, itemId));
            cartService.removeItemFromCart(id, itemId);
            return ResponseEntity.ok(item);
        } else {
            throw new NoSuchElementException();
        }
    }
}