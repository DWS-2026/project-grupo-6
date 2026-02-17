package com.example.projectgrupo6.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.projectgrupo6.domain.Order;
import com.example.projectgrupo6.repositories.OrderRepository;

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
}