package com.example.kafka_practice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatedProductRestModel {
    private String title;
    private BigDecimal price;
    private Integer quantity;

}
