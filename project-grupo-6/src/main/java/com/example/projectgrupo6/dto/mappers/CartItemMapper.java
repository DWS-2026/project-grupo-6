package com.example.projectgrupo6.dto.mappers;

import com.example.projectgrupo6.domain.CartItem;
import com.example.projectgrupo6.dto.CartItemDTO;
import com.example.projectgrupo6.dto.basicDtos.CartItemBasicDTO;
import org.mapstruct.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface CartItemMapper {
    CartItemDTO toDTO(CartItem cartItem);
    CartItemBasicDTO toBasicDTO (CartItem cartItem);

    List<CartItemDTO> toDTOs (Collection<CartItem> cartItems);
    List<CartItemBasicDTO> toBasicDTOs (Collection<CartItem> cartItems);

    CartItem toDomain (CartItemDTO cartItemDTO);
    CartItem toDomainFromBasic (CartItemBasicDTO cartItemBasicDTO);

    List<CartItem> toDomainFromBasics(List<CartItemBasicDTO> itemBasicDTOS);

}
