package com.example.projectgrupo6.dto.mappers;

import com.example.projectgrupo6.domain.Product;
import com.example.projectgrupo6.dto.ProductDTO;
import com.example.projectgrupo6.dto.basicDtos.ProductBasicDTO;
import org.mapstruct.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDTO toDTO(Product product);
    ProductBasicDTO toBasicDTO (Product product);

    List<ProductDTO> toDTOs (Collection<Product> products);
    List<ProductBasicDTO> toBasicDTOs (Collection<Product> products);

    Product toDomain (ProductDTO productDTO);
    Product toDomainFromBasic (ProductBasicDTO productBasicDTO);
}
