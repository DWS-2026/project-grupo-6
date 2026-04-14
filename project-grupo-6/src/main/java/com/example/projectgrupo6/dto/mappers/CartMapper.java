package com.example.projectgrupo6.dto.mappers; 

import com.example.projectgrupo6.dto.CartDTO;
import com.example.projectgrupo6.domain.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
import java.util.Collection;

@Mapper(componentModel = "spring")
public interface CartMapper {

    CartDTO toDTO(Cart cart);

    List<CartDTO> toDTOs(Collection<Cart> carts);

    @Mapping(target = "items", ignore = true)
    Cart toDomain(CartDTO cartDTO);
}