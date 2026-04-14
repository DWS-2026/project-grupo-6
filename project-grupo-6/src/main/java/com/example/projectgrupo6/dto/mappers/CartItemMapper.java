package com.example.projectgrupo6.dto.mappers;

import com.example.projectgrupo6.dto.CartItemDTO;
import com.example.projectgrupo6.domain.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
import java.util.Collection;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mapping(target = "cart", ignore = true)
    CartItemDTO toDTO(CartItem cartItem);

    List<CartItemDTO> toDTOs(Collection<CartItem> cartItems);

    @Mapping(target = "cart", ignore = true)
    CartItem toDomain(CartItemDTO cartItemDTO);
}