package com.example.EmailNotificationMicroservice.io;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProcessedEventRepository extends JpaRepository<ProcessedEventEntity,Long> {
    ProcessedEventEntity findByMessageId(String messageId);

}

