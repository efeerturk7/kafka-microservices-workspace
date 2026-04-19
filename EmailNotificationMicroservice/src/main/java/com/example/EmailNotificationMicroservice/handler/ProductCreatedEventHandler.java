package com.example.EmailNotificationMicroservice.handler;


import com.example.EmailNotificationMicroservice.error.NotRetryableException;
import com.example.EmailNotificationMicroservice.error.RetryableException;
import com.example.EmailNotificationMicroservice.io.IProcessedEventRepository;
import com.example.EmailNotificationMicroservice.io.ProcessedEventEntity;
import com.example.core.ProductCreatedEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
@KafkaListener(topics = {"product-created-events-topic"})
@RequiredArgsConstructor
public class ProductCreatedEventHandler {
    private final RestTemplate restTemplate;
    private final IProcessedEventRepository processedEventRepository;
    @Transactional
    @KafkaHandler
    public void handle(@Payload ProductCreatedEvent productCreatedEvent, @Header("messageId") String messageId,@Header(KafkaHeaders.RECEIVED_KEY) String messageKey) {
        log.info("Received a new event: {} ", productCreatedEvent.getTitle() + "with productID : {}", productCreatedEvent.getProductId());
        //check if this message was already processed before
        ProcessedEventEntity existingRecord =processedEventRepository.findByMessageId(messageId);
        if (existingRecord != null) {
            log.info("found a duplicate message id : {}",existingRecord.getMessageId());
            return;
        }
        String requestUrl = "http://localhost:8080";
        try {
            ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.GET, null, String.class);
            if (response.getStatusCode().value() == HttpStatus.OK.value()) {
                log.info("Recevied response from a remote service : {} ", response.getBody());
            }

        } catch (ResourceAccessException exception) {
            log.error("Resource Access Exception {} ", exception);
            throw new RetryableException(exception);
        } catch (HttpServerErrorException e) {
            log.error("Http Server Error {} ", e.getResponseBodyAsString());
            throw new NotRetryableException(e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Unknown Exception {} ", e.getMessage());
            throw new NotRetryableException(e.getMessage());
        }
        try {
            processedEventRepository.save(new ProcessedEventEntity(messageId,productCreatedEvent.getProductId()));
        }catch (DataIntegrityViolationException exception){
            throw new DataIntegrityViolationException(exception.getMessage());
        }


    }
}
