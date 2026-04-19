package com.example.kafka_practice.service;

import com.example.core.ProductCreatedEvent;
import com.example.kafka_practice.model.CreatedProductRestModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;
    public String createProduct(CreatedProductRestModel product)throws  Exception{
        String productId= UUID.randomUUID().toString();

        //TODO: Persist Product Details into database table before publishing an Event
        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(productId,product.getTitle(),product.getPrice(),product.getQuantity());
        ProducerRecord<String,ProductCreatedEvent>record=new ProducerRecord<>("product-created-events-topic",productId,productCreatedEvent);
        record.headers().add("messageId",UUID.randomUUID().toString().getBytes());
        /*CompletableFuture<SendResult<String,ProductCreatedEvent>>future=kafkaTemplate.send("newTopic-product-created",productId,productCreatedEvent);
        future.whenComplete((result,exception)->{
            if(exception!=null){
                log.info("--------- Succesfully sent message --> {}",result.getRecordMetadata());
            }else {
                log.error("---------- Failed to send message --> {}",exception.getMessage());
            }
        });

        // future.join();//Send Message Synchronously
        Send Message Asynchronously*/
        log.info("before publishing a ProductCreatedEvent");
        SendResult<String,ProductCreatedEvent>result=kafkaTemplate.send(record).get();
        log.info("partition: {}",result.getRecordMetadata().partition());
        log.info("offset: {}",result.getRecordMetadata().offset());
        log.info("topic: {}",result.getRecordMetadata().topic());
        log.info("----- Returning product id");
        return productId;


    }
}
