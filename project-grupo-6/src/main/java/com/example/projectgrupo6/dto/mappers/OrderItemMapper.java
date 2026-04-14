package com.example.projectgrupo6.dto.mappers;

import com.example.projectgrupo6.domain.OrderItem;
import com.example.projectgrupo6.dto.OrderItemDTO;
import com.example.projectgrupo6.dto.basicDtos.OrderItemBasicDTO;
import org.mapstruct.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItemDTO toDTO(OrderItem orderItem);
    OrderItemBasicDTO toBasicDTO (OrderItem orderItem);

    List<OrderItemDTO> toDTOs (Collection<OrderItem> orderItems);
    List<OrderItemBasicDTO> toBasicDTOs (Collection<OrderItem> orderItems);

    OrderItem toDomain (OrderItemDTO orderItemDTO);
    OrderItem toDomainFromBasic (OrderItemBasicDTO orderItemBasicDTO);
}
