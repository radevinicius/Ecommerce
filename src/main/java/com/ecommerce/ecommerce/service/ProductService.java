package com.ecommerce.ecommerce.service;

import com.ecommerce.ecommerce.dto.ProductRequestDTO;
import com.ecommerce.ecommerce.dto.ProductResponseDTO;
import com.ecommerce.ecommerce.entity.Product;
import com.ecommerce.ecommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
@Transactional
    public ProductResponseDTO create(ProductRequestDTO dto){

        Product product = new Product();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setStockQuantity(dto.stockQuantity());

        productRepository.save(product);

        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getActive()
        );
    }
}
