package com.example.projectgrupo6.services;

import com.example.projectgrupo6.domain.Cart;
import com.example.projectgrupo6.domain.CartItem;
import com.example.projectgrupo6.domain.Product;
import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.repositories.CartRepository;
import com.example.projectgrupo6.repositories.ProductRepository;
import com.example.projectgrupo6.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    
    @Transactional
    public Cart getOrCreateCartForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + userId));
        Optional<Cart> existingCart = cartRepository.findByUserId(userId);
        if (existingCart.isPresent()) {
            return existingCart.get();
        } else {
            Cart newCart = new Cart(user);
            return cartRepository.save(newCart);
        }
    }

    
    @Transactional
    public Cart addProductToCart(Long userId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }
        Cart cart = getOrCreateCartForUser(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productId));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem(product);
            newItem.setQuantity(quantity);
            newItem.setCart(cart);
            cart.getItems().add(newItem);
        }
        return cart;
    }

  @Transactional
    public Cart removeProductFromCart(Long userId, Long productId) {
        Cart cart = getOrCreateCartForUser(userId);
        cart.getItems().removeIf(item -> {
            if (item.getProduct().getId().equals(productId)) {
                item.setCart(null);
                return true;
            }
            return false;
        });
        return cart;
    }

    
    @Transactional
    public Cart updateProductQuantity(Long userId, Long productId, int newQuantity) {
        Cart cart = getOrCreateCartForUser(userId);
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            if (newQuantity <= 0) {
                cart.getItems().remove(item);
                item.setCart(null);
            } else {
                item.setQuantity(newQuantity);
            }
        } else if (newQuantity > 0) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productId));
            CartItem newItem = new CartItem(product);
            newItem.setQuantity(newQuantity);
            newItem.setCart(cart);
            cart.getItems().add(newItem);
        }
        return cart;
    }

    
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCartForUser(userId);
        cart.getItems().clear(); 
    }

    
    public double getCartTotal(Long userId) {
        Cart cart = getOrCreateCartForUser(userId);
        return cart.getItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }

   
    public int getCartTotalItems(Long userId) {
        Cart cart = getOrCreateCartForUser(userId);
        return cart.getItems().stream().mapToInt(CartItem::getQuantity).sum();
    }

    
    public List<CartItem> getCartItems(Long userId) {
        Cart cart = getOrCreateCartForUser(userId);
        return cart.getItems();
    }
}