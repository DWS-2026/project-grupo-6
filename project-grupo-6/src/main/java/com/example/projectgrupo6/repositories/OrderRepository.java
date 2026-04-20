package com.example.projectgrupo6.repositories;

import com.example.projectgrupo6.domain.OrderItem;
import com.example.projectgrupo6.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.projectgrupo6.domain.Order;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUser (User user);
    Optional<OrderItem> findByIdAndOrderId(Long itemId, Long orderId);
}