package com.booleanuk.backend.controller;

import com.booleanuk.backend.model.Order;
import com.booleanuk.backend.model.Product;
import com.booleanuk.backend.model.User;
import com.booleanuk.backend.model.dto.BasketItemDTO;
import com.booleanuk.backend.model.dto.OrderDTO;
import com.booleanuk.backend.model.dto.ProductsInOrderDTO;
import com.booleanuk.backend.model.dto.StatusDTO;
import com.booleanuk.backend.payload.response.ErrorResponse;
import com.booleanuk.backend.payload.response.OrderListResponse;
import com.booleanuk.backend.payload.response.OrderResponse;
import com.booleanuk.backend.payload.response.Response;
import com.booleanuk.backend.repository.OrderRepository;
import com.booleanuk.backend.repository.ProductRepository;
import com.booleanuk.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("users/{userid}/orders")
public class OrderController {
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    AuthService authService;

    // Get order history of user
    @GetMapping
    public ResponseEntity<Response<?>> getAll(@PathVariable int userid, @RequestHeader (name="Authorization") String token) {
        if (!authService.hasAccessToResource(token, userid)) {
            ErrorResponse error = new ErrorResponse();
            error.set("These are not your orders");
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }
        List<Order> orders = this.orderRepository.findAll().stream().filter(order -> order.getUser().getId() == userid).toList();
        List<OrderDTO> orderDTOList = orders.stream().map(this::covertOrderToDTO).toList();
        OrderListResponse response = new OrderListResponse();
        response.set(orderDTOList);
        return ResponseEntity.ok(response);
    }

    // Create new order
    @PostMapping
    public ResponseEntity<Response<?>> create(@PathVariable int userid, @RequestBody ProductsInOrderDTO productsInOrderDTO, @RequestHeader (name="Authorization") String token) {
        if (!authService.hasAccessToResource(token, userid)) {
            ErrorResponse error = new ErrorResponse();
            error.set("These are not your orders");
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }
        User user = this.userRepository.findById(userid).orElse(null);
        // 404 Not found
        if (user == null) {
            ErrorResponse error = new ErrorResponse();
            error.set("User not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        Map<Product, Integer> productsInOrder = new HashMap<>();
        for (BasketItemDTO basketItemDTO: productsInOrderDTO.getProducts()) {
            Product product = this.productRepository.findById(basketItemDTO.getSku()).orElse(null);
            // 404 Not found
            if (product == null) {
                ErrorResponse error = new ErrorResponse();
                error.set("Product not found");
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            }
            productsInOrder.put(product, basketItemDTO.getQuantity());
        }
        OrderResponse response = new OrderResponse();
        try {
            Order order = this.orderRepository.save(new Order(user, productsInOrder));
            user.setBasket(new HashMap<>());
            this.userRepository.save(user);

            response.set(this.covertOrderToDTO(order));
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse();
            error.set("Bad request");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get a specific order
    @GetMapping("/{orderId}")
    public ResponseEntity<Response<?>> getSpecific(@PathVariable int orderId, @RequestHeader (name="Authorization") String token) {
        Order order = this.orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            ErrorResponse error = new ErrorResponse();
            error.set("Order not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        if (!authService.hasAccessToResource(token, order.getUser().getId())) {
            ErrorResponse error = new ErrorResponse();
            error.set("This is not your order");
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }
        OrderResponse response = new OrderResponse();
        response.set(this.covertOrderToDTO(order));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<Response<?>> update(@PathVariable int orderId, @RequestBody StatusDTO status, @RequestHeader (name="Authorization") String token) {
        if (authService.isAdminUser(token)) {
            ErrorResponse error = new ErrorResponse();
            error.set("You do not have access to edit orders");
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }
        Order orderToUpdate = this.orderRepository.findById(orderId).orElse(null);
        if (orderToUpdate == null) {
            ErrorResponse error = new ErrorResponse();
            error.set("Order not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        orderToUpdate.setStatus(status.getStatus());

        OrderResponse response = new OrderResponse();
        try {
            this.orderRepository.save(orderToUpdate);
            response.set(this.covertOrderToDTO(orderToUpdate));
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse();
            error.set("Bad request");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private OrderDTO covertOrderToDTO(Order order) {
        List<BasketItemDTO> products = new ArrayList<>();
        Map<Product, Integer> productsInOrder = order.getProductsInOrder();
        for (Product product: productsInOrder.keySet()) {
            products.add(new BasketItemDTO(product.getSku(), productsInOrder.get(product)));
        }
        return new OrderDTO(order.getId(), order.getStatus(), order.getDateOrdered(),
                order.getUser().getId(), products);
    }

}
