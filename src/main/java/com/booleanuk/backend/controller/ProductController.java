package com.booleanuk.backend.controller;

import com.booleanuk.backend.model.Product;
import com.booleanuk.backend.payload.response.ErrorResponse;
import com.booleanuk.backend.payload.response.ProductListResponse;
import com.booleanuk.backend.payload.response.ProductResponse;
import com.booleanuk.backend.payload.response.Response;
import com.booleanuk.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("products")
public class ProductController {
    @Autowired
    ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<Response<?>> getAll() {
        ProductListResponse productListResponse = new ProductListResponse();
        productListResponse.set(this.productRepository.findAll());
        return ResponseEntity.ok(productListResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<?>> getSpecific(@PathVariable int id) {
        Product product = this.productRepository.findById(id).orElse(null);
        if (product == null) {
            ErrorResponse error = new ErrorResponse();
            error.set("Product Not Found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        ProductResponse productResponse = new ProductResponse();
        productResponse.set(product);
        return ResponseEntity.ok(productResponse);
    }
}
