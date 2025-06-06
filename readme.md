# 💬 RealTimeChat - Mã hóa siêu cấp đỉnh cao

Một ứng dụng chat thời gian thực client-server, viết bằng Java thuần + Swing UI, tích hợp mã hóa RSA + AES để đảm bảo **bảo mật đầu cuối (End-to-End)**. Giao diện xịn sò, bong bóng tin nhắn như Messenger.

---

## 🏗️ Kiến trúc tổng thể

```
Client (Swing UI) <--> TCP Socket <--> Server (Java)
```

- Giao thức custom: `CMD (byte)` + `size (int)` + `payload (byte[])`
- Dùng **VirtualThread** (JDK 19+) cho mọi tác vụ IO → scale ngàn kết nối vẫn mượt
- Tin nhắn được wrap bằng `MessageWriter` và đọc lại bằng `MessageReader`
- Hệ thống mã hóa:
  - 🔐 Dùng RSA để trao đổi key AES
  - 🔒 Dùng AES để mã hóa nội dung chat
- Toàn bộ hệ thống được tách rõ các tầng: UI - Logic - Network - Security

---

## 🖥️ Giao diện người dùng

- UI chính: `MainFrame` dùng CardLayout
  - `HomePanel`: Đăng nhập
  - `ChatPanel`: Giao diện chat full tính năng
- Bong bóng chat: `MessageBubblePanel`
  - Align trái/phải theo người gửi
  - Scroll tự động xuống dòng mới

---

## ⚙️ Cách chạy

### 1. Yêu cầu

- Java 19+
- Gradle (hoặc dùng `./gradlew`)

### 2. Build & Run

**Chạy Server**
```bash
cd Server
../gradlew run
```

**Chạy Client**
```bash
cd Client
../gradlew run
```

> Mặc định kết nối tới `localhost:15555`.

---

## 🔒 Mã hóa End-to-End

### Tóm tắt luồng bảo mật:

1. Mỗi client khởi tạo:
   - 1 cặp khóa RSA
   - 1 khóa AES tạm
2. Khi đăng nhập:
   - Gửi `Tên` + `PublicKeyRSA` lên server
3. Khi gửi tin nhắn:
   - Mã hóa nội dung bằng AES
   - Mã hóa AES key bằng PublicKeyRSA của người nhận
4. Người nhận:
   - Dùng PrivateKeyRSA để giải mã key AES
   - Giải mã nội dung bằng AES key

> ✅ Không server nào đọc được nội dung thật sự. Đây là **"privacy by design"**.

---

## 🧩 Các thành phần chính

| Tầng | Class | Vai trò |
|------|-------|--------|
| UI | `MainFrame`, `HomePanel`, `ChatPanel` | Giao diện chính |
| Logic | `ServerRequestManager` | Gửi lệnh tới server |
| Logic | `ControllerMessage` | Nhận và xử lý lệnh từ server |
| Network | `Session` | Đọc/ghi socket TCP |
| Network | `MessageWriter`, `MessageReader` | Giao thức |
| Security | `AESUtil`, `RSAUtil` | Mã hóa dữ liệu |
| Data | `DataChat`, `MemChat` | Quản lý bộ nhớ và danh sách online |

---

## 🔁 Trình tự hoạt động

### 1. Khởi động

```java
Main.java → MainFrame.Instance.setVisible(true)
```

---

### 2. Đăng nhập

```java
ServerRequestManager.connect(name)
→ tạo RSA keypair + AES key
→ tạo Session (socket)
→ gửi CMD.LOGIN gồm tên và publicKey RSA
```

---

### 3. Nhận danh sách online

Server gửi danh sách `name + pubKeyRSA` → lưu vào `DataChat`

---

### 4. Gửi tin nhắn

```java
ServerRequestManager.sendChatMessage(curMemChat, text)
→ AES.encrypt(text)
→ RSA.encrypt(aesKey)
→ Gửi CMD.SEND_CHAT_MESSAGE(receiver, encryptedText, encryptedAESKey)
```

---

### 5. Nhận tin nhắn

```java
ControllerMessage → CMD.RECEIVE_CHAT_MESSAGE
→ RSA.decrypt(aesKey)
→ AES.decrypt(text)
→ hiển thị bong bóng
→ lưu vào DataChat
```

---

## 🔄 UML Sequence Diagram (Text)

```plaintext
ClientUser → ClientApp : Nhập tên
ClientApp → Server : CMD.LOGIN (tên, publicKeyRSA)
Server → ClientApp : CMD.LOGIN (OK + danh sách online)

Client → Server : CMD.SEND_CHAT_MESSAGE (receiver, AES(text), RSA(aesKey))
Server → Receiver : CMD.RECEIVE_CHAT_MESSAGE (sender, AES(text), RSA(aesKey))
Receiver → ClientUI : Giải mã và hiển thị
```

---

## 📚 Tài liệu cho Dev mới

| Cần hiểu | Đọc file nào |
|----------|--------------|
| UI hoạt động ra sao? | `MainFrame.java`, `ChatPanel.java` |
| Logic gửi message | `ServerRequestManager.java` |
| Nhận message | `ControllerMessage.java` |
| Mã hóa RSA + AES | `RSAUtil.java`, `AESUtil.java` |
| TCP xử lý thế nào | `Session.java`, `MessageWriter/Reader.java` |

---

## ✅ Highlight

- ✅ **Không dùng framework** → nhanh, nhẹ, dễ debug
- ✅ **Mã hóa End-to-End** → bảo mật cực cao
- ✅ **VirtualThread** toàn tập → IO không block, đa luồng cực nhẹ
- ✅ **Giao diện đẹp, dễ dùng**, align trái/phải chuẩn như Telegram

---

## 🚀 Gợi ý nâng cấp

- [ ] Gửi file đính kèm
- [ ] Thêm trạng thái "đang gõ..."
- [ ] Lưu tin nhắn vào DB / file
- [ ] Push notification khi app minimize

---

## 👨‍💻 Tác giả

- ✍️ Code by [KhanhDz](https://www.facebook.com/khanhdepzai.pro/)
- ❤️ Mọi đóng góp đều được hoan nghênh

---

🔥 **Ghi nhớ**:

> Cốt lõi hệ thống:
>
> ```
> CMD → MessageWriter → Session → TCP
> ↘                      ↗
>    AES/RSA → an toàn cực mạnh
> ```

Nếu bạn hiểu được flow này, bạn làm được bất kỳ feature nào.

---

