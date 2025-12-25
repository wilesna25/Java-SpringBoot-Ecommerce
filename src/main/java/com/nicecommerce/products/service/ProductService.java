package com.nicecommerce.products.service;

import com.nicecommerce.core.exception.ResourceNotFoundException;
import com.nicecommerce.products.dto.CreateProductRequest;
import com.nicecommerce.products.dto.ProductDTO;
import com.nicecommerce.products.dto.ProductListResponse;
import com.nicecommerce.products.dto.UpdateProductRequest;
import com.nicecommerce.products.entity.Category;
import com.nicecommerce.products.entity.Product;
import com.nicecommerce.products.mapper.ProductMapper;
import com.nicecommerce.products.repository.CategoryRepository;
import com.nicecommerce.products.repository.ProductRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Product Service
 * 
 * Business logic for product operations.
 * Implements caching, observability, and best practices.
 * 
 * @author NiceCommerce Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final MeterRegistry meterRegistry;
    
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    
    /**
     * Find product by ID with caching
     * 
     * @param id Product ID
     * @return ProductDTO
     */
    @Cacheable(value = "products", key = "#id")
    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            log.info("Fetching product - productId: {}, operation: findById", id);
            
            Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found - productId: {}", id);
                    return new ResourceNotFoundException("Product not found with id: " + id);
                });
            
            log.info("Product fetched successfully - productId: {}, productName: {}", 
                id, product.getName());
            
            return productMapper.toDTO(product);
            
        } catch (Exception e) {
            log.error("Error fetching product - productId: {}, error: {}", 
                id, e.getMessage(), e);
            throw e;
        } finally {
            sample.stop(Timer.builder("product.fetch.duration")
                .tag("operation", "findById")
                .register(meterRegistry));
        }
    }
    
    /**
     * Find all products with pagination and caching
     * 
     * @param pageable Pagination parameters
     * @return ProductListResponse
     */
    @Cacheable(value = "products", key = "'list:' + #pageable.pageNumber + ':' + #pageable.pageSize")
    @Transactional(readOnly = true)
    public ProductListResponse findAll(Pageable pageable) {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            log.debug("Fetching products - page: {}, size: {}", 
                pageable.getPageNumber(), pageable.getPageSize());
            
            Page<Product> productPage = productRepository.findByIsActiveTrue(pageable);
            
            List<ProductDTO> productDTOs = productMapper.toDTOList(productPage.getContent());
            
            ProductListResponse response = ProductListResponse.builder()
                .products(productDTOs)
                .currentPage(productPage.getNumber())
                .totalPages(productPage.getTotalPages())
                .totalItems(productPage.getTotalElements())
                .pageSize(productPage.getSize())
                .build();
            
            log.debug("Products fetched successfully - count: {}, totalItems: {}", 
                productDTOs.size(), productPage.getTotalElements());
            
            return response;
            
        } finally {
            sample.stop(Timer.builder("product.list.duration")
                .tag("operation", "findAll")
                .register(meterRegistry));
        }
    }
    
    /**
     * Search products
     * 
     * @param searchTerm Search term
     * @param pageable Pagination parameters
     * @return ProductListResponse
     */
    @Transactional(readOnly = true)
    public ProductListResponse searchProducts(String searchTerm, Pageable pageable) {
        log.info("Searching products - searchTerm: {}, page: {}", 
            searchTerm, pageable.getPageNumber());
        
        Page<Product> productPage = productRepository.searchProducts(searchTerm, pageable);
        
        List<ProductDTO> productDTOs = productMapper.toDTOList(productPage.getContent());
        
        return ProductListResponse.builder()
            .products(productDTOs)
            .currentPage(productPage.getNumber())
            .totalPages(productPage.getTotalPages())
            .totalItems(productPage.getTotalElements())
            .pageSize(productPage.getSize())
            .build();
    }
    
    /**
     * Create a new product
     * 
     * @param request CreateProductRequest
     * @return ProductDTO
     */
    @CacheEvict(value = "products", allEntries = true)
    @Transactional
    public ProductDTO create(CreateProductRequest request) {
        log.info("Creating product - productName: {}", request.getName());
        
        // Find category
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Category not found with id: " + request.getCategoryId()));
        
        // Map request to entity
        Product product = productMapper.toEntity(request);
        
        // Generate slug
        product.setSlug(generateSlug(request.getName()));
        
        // Set category
        product.setCategory(category);
        
        // Save product
        product = productRepository.save(product);
        
        log.info("Product created successfully - productId: {}, productName: {}", 
            product.getId(), product.getName());
        
        return productMapper.toDTO(product);
    }
    
    /**
     * Update an existing product
     * 
     * @param id Product ID
     * @param request UpdateProductRequest
     * @return ProductDTO
     */
    @CacheEvict(value = "products", key = "#id")
    @Transactional
    public ProductDTO update(Long id, UpdateProductRequest request) {
        log.info("Updating product - productId: {}", id);
        
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        // Update category if provided
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Category not found with id: " + request.getCategoryId()));
            product.setCategory(category);
        }
        
        // Update other fields using MapStruct
        productMapper.updateEntityFromDTO(request, product);
        
        // Regenerate slug if name changed
        if (request.getName() != null && !request.getName().equals(product.getName())) {
            product.setSlug(generateSlug(request.getName()));
        }
        
        product = productRepository.save(product);
        
        log.info("Product updated successfully - productId: {}", product.getId());
        
        return productMapper.toDTO(product);
    }
    
    /**
     * Delete a product (soft delete)
     * 
     * @param id Product ID
     */
    @CacheEvict(value = "products", key = "#id")
    @Transactional
    public void delete(Long id) {
        log.info("Deleting product - productId: {}", id);
        
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        product.setIsActive(false);
        productRepository.save(product);
        
        log.info("Product deleted successfully - productId: {}", id);
    }
    
    /**
     * Generate URL-friendly slug from product name
     * 
     * @param name Product name
     * @return Slug
     */
    private String generateSlug(String name) {
        String nowhitespace = WHITESPACE.matcher(name).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }
}

