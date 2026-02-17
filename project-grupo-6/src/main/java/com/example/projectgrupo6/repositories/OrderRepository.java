package com.example.projectgrupo6.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.projectgrupo6.domain.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Aquí podrías añadir métodos personalizados en el futuro, 
    // como buscar pedidos por usuario o por estado.
}