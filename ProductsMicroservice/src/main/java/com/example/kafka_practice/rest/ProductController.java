package com.example.kafka_practice.rest;

import com.example.kafka_practice.exception.ErrorMessage;
import com.example.kafka_practice.model.CreatedProductRestModel;
import com.example.kafka_practice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    @PostMapping("/createProduct")
    public ResponseEntity<Object>createProduct(@RequestBody CreatedProductRestModel product)  {
        String productId;
        try {
            productId = productService.createProduct(product);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorMessage(new Date(),e.getMessage(),"/products"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(productId);
    }
}
