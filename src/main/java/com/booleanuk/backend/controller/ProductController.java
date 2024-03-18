package com.booleanuk.backend.controller;

import com.booleanuk.backend.payload.response.ProductListResponse;
import com.booleanuk.backend.payload.response.Response;
import com.booleanuk.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

}
