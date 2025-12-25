package com.nicecommerce.products.mapper;

import com.nicecommerce.products.dto.CreateProductRequest;
import com.nicecommerce.products.dto.ProductDTO;
import com.nicecommerce.products.dto.UpdateProductRequest;
import com.nicecommerce.products.entity.Category;
import com.nicecommerce.products.entity.Product;
import org.mapstruct.*;

import java.util.List;

/**
 * Product Mapper
 * 
 * MapStruct mapper for converting between Product entities and DTOs.
 * MapStruct generates implementation at compile time for type-safe, performant mapping.
 * 
 * @author NiceCommerce Team
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProductMapper {
    
    /**
     * Map Product entity to ProductDTO
     * 
     * @param product Product entity
     * @return ProductDTO
     */
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(target = "category", ignore = true)  // Don't expose full category object
    ProductDTO toDTO(Product product);
    
    /**
     * Map list of Product entities to list of ProductDTOs
     * 
     * @param products List of Product entities
     * @return List of ProductDTOs
     */
    List<ProductDTO> toDTOList(List<Product> products);
    
    /**
     * Map CreateProductRequest to Product entity
     * 
     * Note: Category mapping requires a service call, so we ignore it here
     * and handle it in the service layer.
     * 
     * @param request CreateProductRequest
     * @return Product entity (without category set)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)  // Generated in service
    @Mapping(target = "category", ignore = true)  // Set in service
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(CreateProductRequest request);
    
    /**
     * Update Product entity from UpdateProductRequest
     * 
     * Only non-null fields from request will be updated.
     * 
     * @param request UpdateProductRequest
     * @param product Product entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(UpdateProductRequest request, @MappingTarget Product product);
}

