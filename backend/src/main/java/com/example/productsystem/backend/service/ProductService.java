package com.example.productsystem.backend.service;

import com.example.productsystem.backend.entity.Product;
import com.example.productsystem.backend.repository.ProductRepository;
import com.example.productsystem.backend.resource.ProductResource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProductService {

    @Inject
    private ProductRepository repo;

    @Transactional
    public Product create(Product p) {
        Product product = repo.create(p);
        return product;
    }

    public Product find(Long id) {
        return repo.find(id);
    }

    @Transactional
    public void update(Product p) {
        repo.update(p);

    }

    @Transactional
    public void delete(Long id) {
        Product p = repo.find(id);
        if (p != null) repo.delete(p);

    }

    public List<Product> list(int page, int size, String sortField, boolean asc) {
        return repo.list(page, size, sortField, asc);
    }

    public List<Product> filter(String name, Float min, Float max, int page, int size) {
        return repo.filter(name, min, max, page, size);
    }

    @Transactional
    public boolean deleteByPartNumber(String partNumber) {
        Optional<Product> p = repo.findByPartNumber(partNumber);
        if (p.isPresent()) {
            repo.delete(p.get());
            return true;
        }
        return false;
    }

    public List<Product> ratingGreaterThan(int rating) {
        return repo.ratingGreaterThan(rating);
    }

    public List<String> uniquePartNumbers() {
        return repo.uniquePartNumbers();
    }

    public List<Product> priceRange(Float min, Float max) {
        return repo.priceRange(min, max);
    }

    @Transactional
    public int increasePricePercent(int percent) {
        return repo.increasePricePercent(percent);
    }
}
