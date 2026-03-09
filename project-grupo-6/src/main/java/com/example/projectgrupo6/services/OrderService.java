package com.example.projectgrupo6.services;

import java.util.List;
import java.util.Optional;

import com.example.projectgrupo6.domain.CartItem;
import com.example.projectgrupo6.domain.Comment;
import com.example.projectgrupo6.domain.User;
import org.springframework.stereotype.Service;

import com.example.projectgrupo6.domain.Order;
import com.example.projectgrupo6.domain.OrderItem;
import com.example.projectgrupo6.repositories.OrderRepository;

import jakarta.transaction.Transactional;


@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Guardar un pedido
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    // Obtener todos los pedidos (útil para admin.html)
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    // Buscar un pedido por ID
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    // Borrar un pedido
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
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
        
        // Recorremos la cesta y creamos la "foto fija" (OrderItem) por cada cosa
        for (CartItem cartItem : cartItems) {
            
            OrderItem orderItem = new OrderItem(
                order, 
                cartItem.getProduct(), 
                cartItem.getQuantity(), 
                cartItem.getProduct().getPrice() // ¡Congelamos el precio!
            );
            
            order.addOrderItem(orderItem); 
        }
        
        // Al guardar el Order, gracias al CascadeType.ALL, se guardan todos los OrderItems solos
        return orderRepository.save(order);
    }
}