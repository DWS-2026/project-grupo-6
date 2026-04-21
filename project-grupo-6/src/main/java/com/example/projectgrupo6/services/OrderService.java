package com.example.projectgrupo6.services;

import java.util.List;
import java.util.Optional;

import com.example.projectgrupo6.domain.CartItem;
import com.example.projectgrupo6.domain.Comment;
import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.repositories.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.projectgrupo6.domain.Order;
import com.example.projectgrupo6.domain.OrderItem;
import com.example.projectgrupo6.repositories.OrderRepository;

import jakarta.transaction.Transactional;


@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Save an order
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    // Get all orders (useful to admin.html)
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Page<Order> findAllPaged (Pageable pageable){
        return orderRepository.findAll(pageable);
    }

    // Search by id
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public Optional<OrderItem> findItemById (Long ordId, Long itemId){
        return orderItemRepository.findByIdAndOrderId(itemId, ordId);
    }

    // Delete an order
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    //Delete an item
    public Order removeItemFromOrder(Long orderId, Long itemId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order no encontrada"));

        order.getItems().removeIf(item -> {
            boolean match = item.getId().equals(itemId);
            if (match) {
                item.setOrder(null);
            }
            return match;
        });
        return order;
    }

    public OrderItem updateOrderItem(Long orderId, Long itemId, OrderItem item){
        Order order = orderRepository.findById(orderId)
                .orElseThrow();

        order.updateItem(itemId, item.getQuantity());
        orderRepository.save(order);
        return order.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow();
    }

    public List<Order> findAllByUser (User user){
        return orderRepository.findAllByUser(user);
    }

    public void deleteList (List<Order> orders){
        orderRepository.deleteAll(orders);
    }

    @Transactional
    public Order createOrderFromCart(User user, List<CartItem> cartItems, double totalAmount) {
        
        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(totalAmount);
        order.setStatus("COMPLETED"); 
        
        // Travel through the cart and create the "pinned image" for every thing
        for (CartItem cartItem : cartItems) {
            
            OrderItem orderItem = new OrderItem(
                order, 
                cartItem.getProduct(), 
                cartItem.getQuantity(), 
                cartItem.getProduct().getPrice() // Freeze the price
            );
            
            order.addOrderItem(orderItem); 
        }
        
        // When saving Order, thanks to CascadeType.ALL, saves all OrderItems by themselves
        return orderRepository.save(order);
    }
}