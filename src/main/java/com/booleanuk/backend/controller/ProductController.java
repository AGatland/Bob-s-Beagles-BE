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

    // Admin endpoints \/
    @PostMapping
    public ResponseEntity<Response<?>> create(@RequestBody Product product) {
        ProductResponse productResponse = new ProductResponse();
        try {
            productResponse.set(this.productRepository.save(product));
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse();
            error.set("Bad request");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(productResponse, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<?>> delete(@PathVariable int id) {
        Product productToDelete = this.productRepository.findById(id).orElse(null);
        if (productToDelete == null) {
            ErrorResponse error = new ErrorResponse();
            error.set("Product Not Found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        this.productRepository.delete(productToDelete);
        ProductResponse productResponse = new ProductResponse();
        productResponse.set(productToDelete);
        return ResponseEntity.ok(productResponse);
    }

}
