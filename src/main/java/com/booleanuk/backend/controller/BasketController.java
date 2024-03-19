package com.booleanuk.backend.controller;

import com.booleanuk.backend.model.Product;
import com.booleanuk.backend.model.User;
import com.booleanuk.backend.model.dto.BasketItemDTO;
import com.booleanuk.backend.payload.response.*;
import com.booleanuk.backend.repository.ProductRepository;
import com.booleanuk.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("basket")
public class BasketController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    AuthService authService;

    @GetMapping("/{id}")
    public ResponseEntity<Response<?>> getAll(@PathVariable int id, @RequestHeader (name="Authorization") String token) {
        if (!authService.hasAccessToResource(token, id)) {
            ErrorResponse error = new ErrorResponse();
            error.set("This is not your basket");
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }
        User user = this.userRepository.findById(id).orElse(null);
        // 404 Not found
        if (user == null) {
            ErrorResponse error = new ErrorResponse();
            error.set("User not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        Map<Product, Integer> basketContent = user.getBasket();
        List<BasketItemDTO> productsInBasket = new ArrayList<>();
        for (Product product: basketContent.keySet()) {
            productsInBasket.add(new BasketItemDTO(product.getSku(), basketContent.get(product)));
        }
        BasketItemListResponse response = new BasketItemListResponse();
        response.set(productsInBasket);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Response<?>> create(@PathVariable int id, @RequestBody BasketItemDTO basketItemDTO, @RequestHeader (name="Authorization") String token) {
        if (!authService.hasAccessToResource(token, id)) {
            ErrorResponse error = new ErrorResponse();
            error.set("This is not your basket");
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }
        User user = this.userRepository.findById(id).orElse(null);
        // 404 Not found
        if (user == null) {
            ErrorResponse error = new ErrorResponse();
            error.set("User not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        Product product = this.productRepository.findById(basketItemDTO.getSku()).orElse(null);
        // 404 Not found
        if (product == null) {
            ErrorResponse error = new ErrorResponse();
            error.set("Product not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        BasketItemResponse response = new BasketItemResponse();
        try {
            Map<Product, Integer> basketContent = user.getBasket();
            basketContent.put(product, basketItemDTO.getQuantity());
            user.setBasket(basketContent);
            this.userRepository.save(user);
            response.set(basketItemDTO);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse();
            error.set("Bad request");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Same code as for post request because basket is a HashMap
    @PutMapping("/{id}")
    public ResponseEntity<Response<?>> update(@PathVariable int id, @RequestBody BasketItemDTO basketItemDTO, @RequestHeader (name="Authorization") String token) {
        if (!authService.hasAccessToResource(token, id)) {
            ErrorResponse error = new ErrorResponse();
            error.set("This is not your basket");
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }
        User user = this.userRepository.findById(id).orElse(null);
        // 404 Not found
        if (user == null) {
            ErrorResponse error = new ErrorResponse();
            error.set("User not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        Product product = this.productRepository.findById(basketItemDTO.getSku()).orElse(null);
        // 404 Not found
        if (product == null) {
            ErrorResponse error = new ErrorResponse();
            error.set("Product not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        BasketItemResponse response = new BasketItemResponse();
        try {
            Map<Product, Integer> basketContent = user.getBasket();
            basketContent.put(product, basketItemDTO.getQuantity());
            user.setBasket(basketContent);
            this.userRepository.save(user);
            response.set(basketItemDTO);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse();
            error.set("Bad request");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<?>> delete(@PathVariable int id, @RequestBody BasketItemDTO basketItemDTO, @RequestHeader (name="Authorization") String token) {
        if (!authService.hasAccessToResource(token, id)) {
            ErrorResponse error = new ErrorResponse();
            error.set("This is not your basket");
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }
        User user = this.userRepository.findById(id).orElse(null);
        // 404 Not found
        if (user == null) {
            ErrorResponse error = new ErrorResponse();
            error.set("User not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        Product product = this.productRepository.findById(basketItemDTO.getSku()).orElse(null);
        // 404 Not found
        if (product == null) {
            ErrorResponse error = new ErrorResponse();
            error.set("Product not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        BasketItemResponse response = new BasketItemResponse();
        try {
            Map<Product, Integer> basketContent = user.getBasket();
            //  Removes product from basket
            basketContent.remove(product);
            user.setBasket(basketContent);
            this.userRepository.save(user);
            response.set(basketItemDTO);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse();
            error.set("Bad request");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
