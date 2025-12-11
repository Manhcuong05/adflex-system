# 📘 Lead Webhook Service (Nội bộ)

## 1. Tổng Quan Dịch Vụ (Service Overview)

**Lead Webhook Service** là một module backend chịu trách nhiệm:

- Nhận dữ liệu lead từ Google Form thông qua Webhook
- Chuẩn hóa số điện thoại
- Kiểm tra trùng lặp theo số điện thoại
- Lưu dữ liệu vào PostgreSQL
- Gửi thông báo nội bộ qua Telegram khi có lead mới hoặc lead trùng

### 🎯 Mục đích nghiệp vụ

- Tự động hóa quy trình tiếp nhận lead
- Hạn chế lead trùng
- Giúp đội vận hành xử lý nhanh và chính xác hơn

### 🔑 Tính năng chính

- Xác thực API Key qua Header
- Chuẩn hóa số điện thoại (+84 → 0)
- Check duplicate theo `phone`
- Lưu danh sách tên doanh nghiệp dạng JSONB
- Gửi Telegram khi có lead mới hoặc lead trùng

---

## 2. Công Nghệ & Phụ Thuộc (Tech Stack & Dependencies)

### 🧩 Công nghệ chính

- Java: **17**
- Spring Boot: **3.4.12**
- Maven: **3.9.x**
- PostgreSQL: **17**
- Hibernate JPA
- Lombok
- Bean Validation
- Hypersistence Utils (map JSONB)

### 🌐 Dịch vụ bên ngoài

- PostgreSQL Database
- Telegram Bot API (nội bộ)

---

## 3. Kiến Trúc & Thiết Kế (Architecture & Design)

### 📁 Cấu trúc thư mục chính

src/main/java/com/adflex/leadwebhook
│
├── controller → Nhận request API
├── service → Xử lý nghiệp vụ
├── repository → JPA Repository
├── entity → Entity JPA
├── dto
│ ├── request → DTO đầu vào
│ └── response → DTO trả về
└── integration
└── telegram → Gửi thông báo Telegram

yaml
Copy code

### Luồng xử lý dữ liệu

1. Google Form gọi webhook → `/api/webhooks/google-form`
2. Controller xác thực API Key
3. Service chuẩn hóa số điện thoại
4. Kiểm tra trùng số điện thoại trong DB
5. Nếu trùng → cập nhật `isDuplicate = true` + gửi Telegram
6. Nếu mới → lưu DB + gửi Telegram
7. Trả kết quả cho client

---

## 4. Cấu Hình Môi Trường (Environment Setup)

###  File `.env`

```env
DB_URL=jdbc:postgresql://localhost:5432/lead_webhook
DB_USERNAME=postgres
DB_PASSWORD=your_password

APP_API_KEY=your_api_key

TELEGRAM_BOT_TOKEN=xxxx
TELEGRAM_CHAT_ID=xxxx
🔧 File application.properties
properties
Copy code
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update

server.port=8081
app.api-key=${APP_API_KEY}
5. Triển Khai (Deployment)
Build project:

bash
Copy code
mvn clean package
Chạy file jar:

bash
Copy code
java -jar target/lead-webhook-0.0.1-SNAPSHOT.jar
Port mặc định: 8081

6. Tài Liệu API (API Documentation)
Endpoint chính
bash
Copy code
POST /api/webhooks/google-form
Header bắt buộc
Key	Value
X-Api-Key	your_api_key

Response
✅ Lead mới (201)
json
Copy code
{
  "id": "uuid",
  "status": "NEW"
}
🔁 Lead trùng (200)
json
Copy code
{
  "id": "uuid",
  "status": "NEW"
}
⛔ Sai API Key (401)
vbnet
Copy code
Invalid API Key
7. Monitoring & Logging
Log hiển thị trên console

Gửi Telegram khi:

Có lead mới

Có lead trùng

Có thể mở rộng tích hợp:

Grafana

Prometheus

ELK Stack

8. Xử Lý Lỗi Thường Gặp (Troubleshooting)
Lỗi	Nguyên nhân	Cách xử lý
401	API Key sai	Kiểm tra header request
28P01	Sai mật khẩu PostgreSQL	Kiểm tra DB_PASSWORD
Port in use	Trùng cổng	Đổi server.port
cannot find symbol	Hàm chưa được khai báo	Kiểm tra class TelegramNotifier
