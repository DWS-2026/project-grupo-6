package com.example.projectgrupo6.domain;
import java.util.*;
import jakarta.persistence.*;

@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    private User user;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    public void addProduct(Product product){
        Optional<CartItem> existingItem = items.stream()
            .filter(item -> item.getProduct().getId().equals(product.getId()))
            .findFirst();
        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + 1);
        } else {
            CartItem newItem = new CartItem(product);
            newItem.setCart(this);
            newItem.setProduct(product);
            newItem.setQuantity(1);
            items.add(newItem);
        }
    }
    public void removeProduct(Product product){
        items.removeIf(item -> {
            if(item.getProduct().getId().equals(product.getId())){
                item.setCart(null);
                return true;
            }
            return false;
            });
        }
        public void clearCart(){
            items.clear();
        }
        public double getTotal(){
            return items.stream().mapToDouble(CartItem::getTotalPrice).sum();
        }
        public int getTotalItems(){
            return items.stream().mapToInt(CartItem::getQuantity).sum();
        }
        public Long getId() {
            return id;
        }
        public List<CartItem> getItems() {
            return items;
        }
        public void setUser(User user) {
            this.user = user;
        }   
        public User getUser() {
            return user;
        }
}    