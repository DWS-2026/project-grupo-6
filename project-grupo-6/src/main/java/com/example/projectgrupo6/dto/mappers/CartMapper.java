package com.example.projectgrupo6.dto.mappers;

import com.example.projectgrupo6.domain.Cart;
import com.example.projectgrupo6.dto.CartDTO;
import org.mapstruct.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {
    CartDTO toDTO(Cart cart);

    List<CartDTO> toDTOs (Collection<Cart> carts);

    Cart toDomain (CartDTO cartDTO);
}
