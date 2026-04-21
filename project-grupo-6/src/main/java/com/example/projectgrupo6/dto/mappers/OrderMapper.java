package com.example.projectgrupo6.dto.mappers;

import com.example.projectgrupo6.domain.Order;
import com.example.projectgrupo6.dto.OrderDTO;
import com.example.projectgrupo6.dto.basicDtos.OrderBasicDTO;
import org.mapstruct.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface OrderMapper {
    OrderDTO toDTO(Order order);
    OrderBasicDTO toBasicDTO(Order order);

    List<OrderDTO> toDTOs (Collection<Order> orders);
    List<OrderBasicDTO> toBasicDTOs (Collection<Order> orders);

    Order toDomain (OrderDTO orderDTO);
    Order toDomainFromBasic(OrderBasicDTO orderBasicDTO);
}
