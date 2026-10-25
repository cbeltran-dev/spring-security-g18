package com.example.security.service;

import java.util.List;

import com.example.security.entity.Product;
import com.example.security.exception.ProductNotFoundException;
import com.example.security.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    // Inyección por constructor manual (a propósito, sin @RequiredArgsConstructor)
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> listar() {
        return productRepository.findAll();
    }

    public Product buscarPorId(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public Product crear(Product product) {
        return productRepository.save(product);
    }

    public void eliminar(Long id) {
        buscarPorId(id); // lanza ProductNotFoundException si no existe
        productRepository.deleteById(id);
    }
}
