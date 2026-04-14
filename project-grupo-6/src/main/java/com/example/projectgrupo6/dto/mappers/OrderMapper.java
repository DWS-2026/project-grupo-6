package com.example.projectgrupo6.dto.mappers;

import com.example.projectgrupo6.domain.Order;
import com.example.projectgrupo6.dto.OrderDTO;
import org.mapstruct.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDTO toDTO(Order order);

    List<OrderDTO> toDTOs (Collection<Order> orders);

    Order toDomain (OrderDTO orderDTO);
}
