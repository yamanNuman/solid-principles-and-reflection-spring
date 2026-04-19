# Payment API

SOLID prensiplerine uygun, Spring Boot ile geliştirilmiş ödeme sistemi.

---

## İçindekiler

- [Proje Hakkında](#proje-hakkında)
- [Teknolojiler](#teknolojiler)
- [Proje Yapısı](#proje-yapısı)
- [SOLID Prensipleri](#solid-prensipleri)
- [Kurulum](#kurulum)
- [API Kullanımı](#api-kullanımı)
- [Yeni Ödeme Yöntemi Ekleme](#yeni-ödeme-yöntemi-ekleme)

---

## Proje Hakkında

Bu proje, genişlemeye açık ve değişime kapalı bir ödeme sistemi tasarımını göstermek amacıyla geliştirilmiştir.

Sistemde halihazırda **Kredi Kartı** ve **PayPal** ödeme yöntemleri bulunmaktadır. Yeni bir ödeme yöntemi eklemek için mevcut kodda hiçbir değişiklik yapılmasına gerek yoktur.

Reflection kullanılarak Spring context'teki tüm `PaymentProcessor` implementasyonları runtime'da otomatik olarak sisteme kaydedilir.

---

## Teknolojiler

- Java 21
- Spring Boot 3.5.13
- Spring Web
- Spring Validation
- Lombok
- Springdoc OpenAPI (Swagger UI) 2.8.6

---

## Proje Yapısı

<pre>
src/main/java/com/example/payment/
│
├── PaymentApplication.java
│
├── config/
│   └── OpenApiConfig.java
│
├── controller/
│   └── PaymentController.java
│
├── service/
│   └── PaymentService.java
│
├── processor/
│   ├── PaymentProcessor.java
│   ├── registry/
│   │   └── PaymentProcessorRegistry.java
│   └── impl/
│       ├── CreditCardProcessor.java
│       └── PayPalProcessor.java
│
├── model/
│   ├── request/
│   │   └── PaymentRequest.java
│   ├── response/
│   │   ├── PaymentResponse.java
│   └── dto/
│       └── PaymentDTO.java
│
└── exception/
    ├── UnsupportedPaymentMethodException.java
    ├── InvalidPaymentRequestException.java
    └── GlobalExceptionHandler.java
</pre>

---

## SOLID Prensipleri

### Single Responsibility Principle (SRP)

Her sınıfın tek bir sorumluluğu vardır.

- `PaymentController` → HTTP isteğini alır, service'e iletir
- `PaymentService` → Request'i DTO'ya çevirir, iş akışını yönetir
- `PaymentProcessorRegistry` → Processor'ları kaydeder ve bulur
- `CreditCardProcessor` / `PayPalProcessor` → Yalnızca kendi ödeme mantığını işler
- `GlobalExceptionHandler` → Tüm hataları merkezi olarak yönetir

### Open/Closed Principle (OCP)

Sistem yeni ödeme yöntemlerine **açık**, mevcut koda değişime **kapalıdır**.

Yeni bir ödeme yöntemi eklemek için `PaymentProcessor` interface'ini implement eden
yeni bir `@Component` sınıf yazmak yeterlidir. Başka hiçbir dosyaya dokunulmaz.

### Liskov Substitution Principle (LSP)

Tüm `PaymentProcessor` implementasyonları birbirinin yerine geçebilir.
Registry hangi processor'ı döndürdüğünden bağımsız olarak `process()` metodunu çağırır.

### Interface Segregation Principle (ISP)

`PaymentProcessor` interface'i yalnızca `getPaymentMethod()` ve `process()` metodlarını içerir.
Implementasyonlar ihtiyaç duymadıkları hiçbir metodu implement etmek zorunda kalmaz.

### Dependency Inversion Principle (DIP)

Tüm sınıflar somut implementasyonlara değil, soyutlamalara bağımlıdır.

- `PaymentService` → `PaymentProcessorRegistry`'e bağımlı, somut processor'ları bilmez
- `PaymentController` → `PaymentService`'e bağımlı, registry'i bilmez
- Tüm bağımlılıklar constructor injection ile sağlanır

---

## Reflection Kullanımı

`PaymentProcessorRegistry`, Spring tarafından inject edilen `List<PaymentProcessor>` üzerinde
Java Reflection API kullanarak her processor'ın hangi interface'leri implement ettiğini
runtime'da kontrol eder ve otomatik olarak map'e kaydeder.

Bu sayede sisteme yeni bir processor eklendiğinde registry'de herhangi bir değişiklik gerekmez.

```java
for (Class<?> iface : processor.getClass().getInterfaces()) {
    if (iface.equals(PaymentProcessor.class)) {
        processors.put(
            processor.getPaymentMethod().toUpperCase(),
            processor
        );
    }
}
```

---

## Katman Mimarisi

```
PaymentRequest   →  HTTP'den gelen ham veri (validation burada)
      ↓ map
PaymentDTO       →  Service ve Processor katmanları arasında taşınan nesne
      ↓ işlenir
PaymentResponse  →  Başarılı HTTP cevabı
ErrorResponse    →  Hatalı HTTP cevabı
```

---

## Kurulum

**Gereksinimler**

- Java 17+
- Maven 3.8+

**Adımlar**

```bash
# Repoyu klonla
git clone https://github.com/kullanici-adi/payment-api.git
cd payment-api

# Derle ve çalıştır
mvn spring-boot:run
```

Uygulama `http://localhost:8080` adresinde ayağa kalkar.

---

## API Kullanımı

### Swagger UI

Tüm endpoint'leri görüntülemek ve test etmek için:

```
http://localhost:8080/swagger-ui.html
```

---

### Desteklenen ödeme yöntemlerini listele

```
GET /api/v1/payments/methods
```

**Response (200)**

```json
["CREDIT_CARD", "PAYPAL"]
```

---

### Ödeme işlemi gerçekleştir

```
POST /api/v1/payments/process
```

**Request Body**

```json
{
  "paymentMethod": "CREDIT_CARD",
  "amount": 150.00,
  "currency": "TRY",
  "description": "Test ödemesi"
}
```

**Response (200)**

```json
{
  "success": true,
  "transactionId": "CC-123e4567-e89b-12d3-a456-426614174000",
  "paymentMethod": "CREDIT_CARD",
  "amount": 150.0,
  "currency": "TRY",
  "message": "Ödeme başarıyla tamamlandı",
  "processedAt": "2026-04-20T01:13:41"
}
```

**Response (400) — Desteklenmeyen yöntem**

```json
{
  "success": false,
  "message": "Desteklenmeyen ödeme yöntemi: BITCOIN",
  "processedAt": "2026-04-20T01:13:41"
}
```

**Response (400) — Validation hatası**

```json
{
  "success": false,
  "message": "Ödeme yöntemi boş olamaz, Tutar sıfırdan büyük olmalı",
  "processedAt": "2026-04-20T01:13:41"
}
```

---

## Yeni Ödeme Yöntemi Ekleme

Sisteme yeni bir ödeme yöntemi eklemek için tek yapman gereken
`processor/impl/` altına yeni bir sınıf oluşturmak:

```java
@Component
public class ApplePayProcessor implements PaymentProcessor {

    @Override
    public String getPaymentMethod() {
        return "APPLE_PAY";
    }

    @Override
    public PaymentResponse process(PaymentDTO dto) {
        String txId = "AP-" + UUID.randomUUID();
        return PaymentResponse.success(txId, getPaymentMethod(),
                                       dto.getAmount(), dto.getCurrency());
    }
}
```

Uygulamayı yeniden başlattığında `APPLE_PAY` otomatik olarak sisteme kaydolur.
Başka hiçbir dosyaya dokunulmaz.
