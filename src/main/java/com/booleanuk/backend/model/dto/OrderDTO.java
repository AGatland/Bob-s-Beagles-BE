package com.booleanuk.backend.model.dto;

import com.booleanuk.backend.model.enums.EOrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private int id;
    private EOrderStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime dateOrdered;
    private int userId;
    private List<BasketItemDTO> products;
}
