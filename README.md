# 🚀 Event-Driven Microservices with Advanced Apache Kafka

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-Event_Streaming-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Microservices](https://img.shields.io/badge/Architecture-Microservices-FF4081?style=for-the-badge)

This project is a production-grade, event-driven microservices architecture demonstrating advanced **Apache Kafka** patterns.

It moves beyond basic pub/sub mechanics to implement real-world enterprise resilience patterns, including **Idempotent Consumers**, **Dead Letter Queues (DLT)**, **Poison Pill Prevention**, and smart **Exception Translation**.

---

## 🏗 System Architecture & Workflow

The architecture consists of three main modules communicating asynchronously:

1. **`core`**: A shared library containing common Data Transfer Objects (DTOs) and Events (e.g., `ProductCreatedEvent`).
2. **`ProductsMicroservice` (Producer)**: Handles incoming REST API requests, creates a unique product ID, and securely publishes events to the Kafka broker.
3. **`EmailNotificationMicroservice` (Consumer)**: Listens to the Kafka topics, manages complex error handling, makes remote HTTP calls, and ensures strict data consistency (no duplicate processing).

### 🔄 Event Flow
> **🌍 Client POST Request** 👉 **📦 ProductsMicroservice** 👉 **📨 Kafka Topic (`product-created-events-topic`)** 👉 **🛡️ DB Check (Idempotency)** 👉 **📬 EmailNotificationMicroservice**

---

## 🚀 Key Technical Features (Advanced Kafka Patterns)

This project implements robust, enterprise-level solutions to handle distributed system chaos.

### 1. 🛡️ Idempotent Consumer (Duplicate Message Prevention)
Due to Kafka's "at-least-once" delivery guarantee, preventing duplicate data processing is critical. This project implements the **Check-and-Set** pattern:
* The Producer attaches a unique `messageId` to the Kafka message headers.
* Before processing, the Consumer checks a local PostgreSQL/H2 database via JPA (`IProcessedEventRepository`).
* If the `messageId` exists, the message is safely discarded. If not, it is processed and the ID is saved within a `@Transactional` boundary.

### 2. ⚡ Advanced Exception Translation & Retry Logic
Network calls fail. This project categorizes errors to prevent unnecessary system strain:
* **Transient Errors (e.g., `ResourceAccessException`):** Translated to `RetryableException`. Trigger a `FixedBackOff` strategy (retries 3 times with a 5-second interval).
* **Fatal Errors (e.g., `HttpServerErrorException`):** Translated to `NotRetryableException`. The retry mechanism is bypassed entirely to save resources.

### 3. 🗑️ Dead Letter Topic (DLT) Routing
Messages that exhaust their retry attempts or throw a `NotRetryableException` do not block the partition. They are automatically routed to a `.DLT` topic using Spring Kafka's `DeadLetterPublishingRecoverer` for later manual inspection.

### 4. 💊 Poison Pill Prevention
If a malformed message (invalid JSON/bytes) enters the topic, it can cause an infinite crash-loop. This system utilizes the `ErrorHandlingDeserializer` as a wrapper around the `JacksonJsonDeserializer` to catch serialization exceptions mid-air and route them safely without crashing the consumer.

### 5. 🏗️ Reliable Producer Configuration
The `ProductsMicroservice` is configured to never lose a message:
* `acks = all`
* `enable.idempotence = true` (Prevents duplicate sends from the producer side)
* Configured `max.in.flight.requests.per.connection` for strict ordering.

---

## 🛠️ Tech Stack

| Category | Technology |
| :--- | :--- |
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.x |
| **Message Broker**| Apache Kafka & Spring Kafka |
| **Database** | PostgreSQL / H2 In-Memory |
| **ORM** | Spring Data JPA / Hibernate |
| **Communication** | REST API (`RestTemplate`) |
| **Architecture** | Event-Driven Microservices |

---

## ⚙️ How to Run Locally

1. **Start Zookeeper & Kafka Broker:**
   Ensure you have a local Kafka environment running (default: `localhost:9092`). You can use a docker-compose file for this.

2. **Clone the repository:**
   ```bash
   git clone [https://github.com/efeerturk7/](https://github.com/efeerturk7/)[https://github.com/efeerturk7/kafka-microservices-workspace].git
   cd [https://github.com/efeerturk7/kafka-microservices-workspace]
Start the Microservices:
Run both ProductsMicroservice and EmailNotificationMicroservice via your IDE or Maven/Gradle wrapper.

Trigger an Event:
Use the following cURL command (or Postman) to create a product and watch the logs as the message travels through Kafka:

Bash
curl -X POST http://localhost:8080/products/createProduct \
-H "Content-Type: application/json" \
-d '{"title": "MacBook Pro", "price": 2500.0, "quantity": 5}'
👨‍💻 Author
Bahadır Efe ERTÜRK - Backend Developer