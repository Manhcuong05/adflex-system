# Tracking Progress Service

Internal Workflow Tracking System for **Ultra / AdFlex**

Hệ thống quản lý toàn bộ tiến trình xử lý hồ sơ doanh nghiệp:  
Từ tiếp nhận Lead → tư vấn → chốt gói → đăng ký kinh doanh → MST → hóa đơn điện tử → dịch vụ thuế → Add-ons.

Hỗ trợ **Core Flow (tuần tự)** + **Addon Flow (song song)** kèm **SLA Alerts**.

---

## 1. Overview

Tracking Progress Service là microservice dùng để:

- Tạo & quản lý các milestone của hồ sơ (lead)
- Sinh luồng Core / Addon theo gói khách hàng
- Kiểm soát thứ tự thực hiện step theo sequence
- Kích hoạt / chặn step dựa trên trạng thái thanh toán
- Theo dõi deadline (SLA) và gửi cảnh báo Telegram
- Cung cấp API cho dashboard nội bộ

### Core Features

- Auto-generate workflow theo gói (Gói 1 / Gói 2)
- Add-ons hoạt động độc lập, không chặn luồng chính
- Payment gatekeeper cho STEP_DKDN
- SLA scanner chạy định kỳ mỗi 5 phút
- API đầy đủ hỗ trợ CRUD + thao tác milestone

---

## 2. Tech Stack

| Công nghệ          | Vai trò            |
|--------------------|--------------------|
| Java 17            | Ngôn ngữ chính     |
| Spring Boot 3      | Backend Framework  |
| PostgreSQL         | Database           |
| Spring Data JPA    | ORM                |
| Spring Scheduler   | Quét SLA           |
| Telegram Bot API   | Cảnh báo SLA       |
| Lombok             | Giảm boilerplate   |

---

## 3. Architecture & Folder Structure

```
tracking_progress/
│
├── controller/                 # API endpoints
├── service/                    # Business logic (Progress, Payment, SLA)
├── scheduler/                  # SLA cron job
├── repository/                 # JPA Repositories
├── entity/                     # Entities: LeadProgress, MilestoneConfig, Order
├── dto/                        # Response DTOs
├── enums/                      # Status enums
└── TrackingProgressApplication.java
```

### Data Flow Summary

```
Lead created
    ↓
STEP_CONSULT auto IN_PROGRESS
    ↓
Ultra chốt gói → Sinh CORE + ADDON steps
    ↓
Thanh toán → Unlock STEP_DKDN
    ↓
Ultra update milestone (START / COMPLETE / FAIL)
    ↓
Dashboard gọi API lấy timeline
    ↓
SLA Scanner quét hạn → gửi Telegram
```

---

## 4. Environment Setup

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL

### Install dependencies
```bash
mvn clean install
```

### Environment Variables

Tạo file `application.properties` hoặc đặt env:

```properties
telegram.bot.token=xxxx
telegram.chat.id=xxxx

spring.datasource.url=jdbc:postgresql://localhost:5432/tracking
spring.datasource.username=postgres
spring.datasource.password=secret
```

---

## 5. Run the Service

### Local
```bash
mvn spring-boot:run
```

### Docker (optional)
```bash
docker build -t tracking-progress .
docker run -p 8080:8080 tracking-progress
```

---

## 6. API Documentation

### Base URL
```
Local: http://localhost:8080
```

---

### 1) Init Progress  
**POST** `/api/admin/leads/{id}/init-progress`  
→ Tạo STEP_CONSULT nếu chưa có.

---

### 2) Confirm Package  
**POST** `/api/admin/leads/{id}/confirm-package`

#### Request Body:
```json
{
  "package_code": "GOI_2",
  "addons": ["WEB", "ZALO"],
  "is_paid": true
}
```

→ Sinh CORE & ADDON steps.

---

### 3) Update Milestone  
**POST** `/api/admin/leads/{id}/progress/{milestoneCode}`

#### Request Body:
```json
{
  "action": "START | COMPLETE | FAIL",
  "proof_doc_id": "optional",
  "note": "optional"
}
```

---

### 4) Get All Milestones  
**GET** `/api/admin/leads/{id}/progress`

---

### 5) SLA – Overdue List  
**GET** `/api/admin/sla/overdue`

---

### 6) Payment Simulation  
```
POST /api/payment/pay/{orderId}
POST /api/payment/callback
```

---

## 7. SLA Monitoring (CRON)

Service chạy mỗi **5 phút**:

- Tìm step IN_PROGRESS quá hạn:  
  `started_at + sla_hours`
- Gửi cảnh báo Telegram với nội dung:

```
SLA quá hạn!
Lead: <lead_id>
Step: <milestone_code>
Deadline: <due_date>
```

---

## 8. Testing Guidelines

### Unit test
```bash
mvn test
```

### Manual Testing (Postman)
- Tạo lead  
- Init progress  
- Confirm package  
- Gọi Add-ons  
- Thanh toán & callback  
- Update milestone  
- Xem SLA overdue  
- Trigger SLA scanner  

---

## 9. Troubleshooting

| Vấn đề                       | Nguyên nhân                     | Giải pháp                          |
|------------------------------|----------------------------------|-------------------------------------|
| Không START được step        | Step trước chưa COMPLETE         | Kiểm tra sequence                   |
| ĐKDN ở WAITING_PAYMENT       | Chưa thanh toán                  | Gọi payment callback                |
| Không nhận Telegram          | Token sai / ChatID sai           | Kiểm tra environment variables      |
| Addon không tạo             | Sai tên addon                    | Bắt buộc format `"WEB"`, `"ZALO"`   |

