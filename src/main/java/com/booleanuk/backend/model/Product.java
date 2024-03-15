package com.booleanuk.backend.model;

import com.booleanuk.backend.model.enums.EProductCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sku;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private EProductCategory category;

    @Column(name = "img", nullable = false)
    private String img;

    @Column(name = "price", nullable = false)
    private int price;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Product(String name, String description, EProductCategory category, String img, int price) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.img = img;
        this.price = price;
    }
}
