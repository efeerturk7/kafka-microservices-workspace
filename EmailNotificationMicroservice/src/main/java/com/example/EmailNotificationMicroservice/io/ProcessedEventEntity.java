package com.example.EmailNotificationMicroservice.io;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Entity
@Table(name = "processed-event")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class ProcessedEventEntity implements Serializable {
    private static final long serialVersionUID = 3374468422352L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,unique = true)
    private String messageId;

    @Column(nullable = false)
    private String productId;

}
