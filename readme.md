```
# 💬 RealTimeChat

Một ứng dụng chat client-server sử dụng Java thuần với giao diện Swing. Hỗ trợ nhắn tin thời gian thực, hiển thị danh sách người dùng online, và giao diện bong bóng tin nhắn như Telegram / Messenger.

---

## 🏗️ Kiến trúc tổng thể

```
Client (Swing UI) <--> TCP Socket <--> Server (Java)
```

- Giao thức: tự thiết kế, sử dụng `CMD (byte)` + `size (int)` + `payload (byte[])`
- Sử dụng `VirtualThread` (JDK 19+) cho cả đọc / ghi socket → non-blocking và siêu nhẹ
- Message được wrap bằng `MessageWriter` và đọc lại bằng `MessageReader`
- Sử dụng singleton pattern cho hầu hết các component chính (UI, Logic, Network)

---

## 🖥️ Giao diện người dùng

- Giao diện chính: `MainFrame` với 2 panel:
  - `HomePanel`: đăng nhập
  - `ChatPanel`: hiển thị danh sách online, khung chat, input
- Bong bóng chat: `MessageBubblePanel`
  - align phải nếu là mình gửi
  - align trái nếu là người khác gửi

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

Mặc định kết nối tới `localhost:15555`.

---

## 🧠 Phân tích luồng hoạt động chi tiết

> Phần này giúp bạn nắm được toàn bộ cơ chế vận hành từ UI → Logic → Network một cách tuần tự, dễ hiểu.

---

### 🚦 1. Khởi động ứng dụng

```
Main.java (Client) → mở giao diện MainFrame → hiện panel đăng nhập (HomePanel)
```

- Swing sử dụng `invokeLater()` để đảm bảo UI chạy trên EDT
- `MainFrame` có 2 panel chính: `HomePanel` và `ChatPanel`

---

### 🔐 2. Đăng nhập

**Client**
```java
ServerRequestManager.connect() → tạo Session TCP
→ gửi CMD.LOGIN (name + RSApub)
```

**Server**
- Nhận CMD.LOGIN
  - Nếu username đã tồn tại → gửi `isOK = false`
  - Nếu chưa có → chấp nhận → gửi danh sách user đang online

**Client**
- Nhận phản hồi → gọi `ControllerMessage.onMessage(CMD.LOGIN)`
- Nếu thành công:
  - Ẩn dialog
  - Hiện `ChatPanel`
  - Hiển thị danh sách user online

---

### 🟢 3. Cập nhật trạng thái online

**Server**
- Mỗi khi user connect/disconnect
  → Gửi `CMD.UPDATE_MEM_ONLINE (bool isOnline, String name)` cho toàn bộ user

**Client**
- `ControllerMessage.onMessage(CMD.UPDATE_MEM_ONLINE)`:
  - Cập nhật danh sách online trên `ChatPanel`

---

### 💬 4. Gửi tin nhắn

**Client**
```java
User chọn người nhận → set curMemChat
Nhập text → nhấn nút Gửi → gọi ServerRequestManager.sendChatMessage()
→ gửi CMD.SEND_CHAT_MESSAGE (receiverName + message)
```

- Tin nhắn được render thành `MessageBubblePanel`
- Lưu vào `DataChat.saveMessage()` để cache lại

---

### 📩 5. Nhận tin nhắn

**Server**
- Nhận CMD.SEND_CHAT_MESSAGE → forward tới người nhận bằng:
  ```java
  CMD.RECEIVE_CHAT_MESSAGE (sender + text)
  ```

**Client**
- `ControllerMessage.onMessage(CMD.RECEIVE_CHAT_MESSAGE)`:
  - Nếu đang chat với `sender` → hiển thị ngay
  - Nếu không → TODO: hiển thị thông báo mới

---

### ⚙️ 6. Cấu trúc packet

Giao thức TCP gửi dạng:
```
[ CMD (byte) ][ SIZE (int) ][ PAYLOAD (byte[]) ]
```

- Đóng gói bằng `MessageWriter`
- Đọc bằng `MessageReader`
- Mỗi `Session` quản lý riêng việc gửi/nhận

---

### 📦 7. Cơ chế xử lý song song

- `Session.startReadMessage()` → đọc socket bằng VirtualThread
- `Session.startSendMessage()` → gửi socket bằng VirtualThread
- Gửi sử dụng `LinkedBlockingQueue`
- Đảm bảo không block giao diện và đa luồng xử lý nhiều người

---

### 📚 8. Lưu lịch sử tin nhắn (cache)

- Mỗi tin nhắn gửi/nhận được lưu vào:
  ```java
  Map<String, List<MessageBubblePanel>> DataChat
  ```
- Khi chọn user → nạp lại toàn bộ tin nhắn từ cache
- Cực nhẹ và nhanh vì nằm trong RAM

---

### ☠️ 9. Mất kết nối

- Nếu socket lỗi (client thoát, mạng chập chờn, vv.)
  → `Session.dispose()` được gọi
  → Đóng toàn bộ stream + UI hiện popup yêu cầu tắt app

---

### 🧠 Tóm tắt class chính

| Class | Vai trò |
|-------|--------|
| `MainFrame` | JFrame chính, quản lý UI |
| `ChatPanel` | Giao diện khung chat |
| `HomePanel` | Giao diện đăng nhập |
| `MessageBubblePanel` | Bong bóng tin nhắn |
| `Session` | Quản lý kết nối TCP |
| `MessageReader/Writer` | Đọc/ghi packet |
| `ControllerMessage` | Xử lý tin nhắn đến |
| `ServerRequestManager` | Gửi lệnh tới server |
| `DataChat` | Lưu tin nhắn trong RAM |
| `CMD` | Định nghĩa các loại lệnh |

---

### 👶 Gợi ý

- Nếu muốn debug:
  - Đặt breakpoint trong `ControllerMessage.onMessage()`
  - Quan sát `CMD` để biết tin gì đang đến
- Nếu muốn thêm tính năng:
  - Định nghĩa `CMD_NEW_FEATURE` trong `CMD.java`
  - Cập nhật gửi (Client) và xử lý (Server)
- Mọi luồng đều đã chạy bằng VirtualThread → không cần lo block UI
- Cứ follow theo flow: `UI → Logic → Session → TCP → Server`

---

🔥 **Ghi nhớ**: Mọi thứ trong hệ thống này đều xoay quanh:
```
CMD → Message → Session → TCP
```

Nếu bạn hiểu được vòng lặp này, bạn làm được bất kỳ feature nào.

---

## 🚀 Điểm nổi bật

- ✅ Không dùng framework nặng → chạy nhanh, dễ debug
- ✅ Dùng VirtualThread → scale lên hàng ngàn kết nối vẫn mượt
- ✅ UI đẹp, dễ dùng, bóng bẩy như chat app thật
- ✅ Sử dụng Map + ReadWriteLock để quản lý session an toàn đa luồng

---

## 📌 TODO / Gợi ý nâng cấp

- [ ] Mã hóa nội dung tin nhắn (AES hoặc hybrid RSA-AES)
- [ ] Gửi file (đã có nút, chưa xử lý)
- [ ] Hiển thị trạng thái “đang gõ”
- [ ] Push thông báo nếu user khác gửi khi không mở tab

---

## 👨‍💻 Tác giả

- 🚀 Dự án bởi KhanhDz (https://www.facebook.com/khanhdepzai.pro/)
- ✨ Contributor tương lai là bạn đó!

---
```
