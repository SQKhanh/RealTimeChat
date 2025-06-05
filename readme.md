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

## 👨‍🏫 Hướng dẫn Onboarding cho Dev Mới

> Mục tiêu: giúp dev mới hiểu nhanh và chắc toàn bộ hệ thống dựa trên code thật, không lý thuyết suông.

---

### 🧩 Thành phần chính

| Tầng | Tên class | Vai trò |
|------|-----------|--------|
| UI | `MainFrame`, `HomePanel`, `ChatPanel` | Giao diện đăng nhập, giao diện chat |
| Logic | `ServerRequestManager` | Cầu nối UI ↔ TCP session |
| Network | `Session` | Đóng/mở socket, đọc/ghi dữ liệu TCP |
| Network | `MessageWriter` / `MessageReader` | Gửi / nhận packet theo giao thức |
| Handler | `ControllerMessage` | Xử lý tin nhắn đến từ server |
| Cache | `DataChat` | Lưu tin nhắn vào RAM để hiển thị lại |

---

## 🔄 Trình tự hoạt động chính

### 1. Người dùng khởi động app
```java
Main.java → MainFrame.Instance.setVisible(true)
→ hiện HomePanel (card layout)
```

---

### 2. Người dùng đăng nhập
```text
Giao diện HomePanel → gọi ServerRequestManager.connect(name, RSApub, RSApri)
→ tạo Session (TCP), khởi động thread đọc & gửi
→ gọi login() → tạo CMD.LOGIN → gửi name + RSApub lên server
```

---

### 3. Server trả về kết quả login

**Server side**
- Trong `ServerRespondManager.java`, xử lý `CMD.LOGIN`
  - Kiểm tra tên user trùng → `isOK = false`
  - Nếu hợp lệ:
    - Thêm vào `SessionManager`
    - Gửi `isOK = true + danh sách online hiện tại`

**Client side**
- `ControllerMessage.onMessage(CMD.LOGIN)`:
  - Nếu `isOK == true` → hiện `ChatPanel`, gọi `updateMemOnline(mems)`
  - Nếu `false` → hiện dialog báo lỗi

---

### 4. Cập nhật trạng thái online

**Server**
- Khi có user connect/disconnect
- Gửi `CMD.UPDATE_MEM_ONLINE (boolean isOnline, String name)` tới các client khác

**Client**
- `ControllerMessage.onMessage(CMD.UPDATE_MEM_ONLINE)` → gọi `ChatPanel.updateMemOnline(name, isOnline)` → update danh sách JList

---

### 5. Gửi tin nhắn

**Client:**
```java
ChatPanel → ServerRequestManager.sendChatMessage(receiver, message)
→ tạo MessageWriter(CMD.SEND_CHAT_MESSAGE)
→ ghi UTF: receiver, message → put vào session
```

**Server:**
- `ServerRespondManager` nhận CMD.SEND_CHAT_MESSAGE
- Gửi `CMD.RECEIVE_CHAT_MESSAGE(sender, message)` tới người nhận

**Client nhận:**
- `ControllerMessage.onMessage(CMD.RECEIVE_CHAT_MESSAGE)` → gọi `ChatPanel.sendMessageToPanel(sender, message)`

---

### 6. Lưu tin nhắn

**Client**
- Dù gửi hay nhận, UI luôn gọi:
```java
DataChat.saveMessage(who, MessageBubblePanel)
```
→ lưu trong `Map<String, List<MessageBubblePanel>>`

---

## 📜 Sequence Diagram (text UML)

```plaintext
Client User      Client App         Server
    |                 |                 |
    |  UI nhập tên    |                 |
    |---------------->|                 |
    |                 | connect()       |
    |                 |---------------->|
    |                 |                 | accept socket
    |                 | login()         |
    |                 |---------------->|
    |                 |   CMD.LOGIN     |
    |                 |   name + RSApub |
    |                 |                 |
    |                 |                 | check trùng name
    |                 |                 | send isOK + online list
    |                 |<----------------|
    | update UI       |                 |
    |---------------->|                 |
    |                 |                 |
    | chọn người chat |                 |
    |---------------->|                 |
    | gõ & gửi        |                 |
    |---------------->| sendChatMessage |
    |                 | CMD.SEND_CHAT_MESSAGE
    |                 |---------------->|
    |                 |                 | relay:
    |                 |                 | CMD.RECEIVE_CHAT_MESSAGE
    |                 |<----------------|
    | show bubble     |                 |
    | save to DataChat|                 |
```

---

## ✅ Checklist Dev mới cần hiểu

| Việc cần hiểu | File cần đọc |
|---------------|--------------|
| Giao diện UI hoạt động thế nào? | `MainFrame.java`, `ChatPanel.java` |
| Làm sao gửi CMD lên server? | `ServerRequestManager.java` |
| Làm sao xử lý lệnh server trả về? | `ControllerMessage.java` |
| TCP message đóng gói thế nào? | `MessageWriter.java` / `MessageReader.java` |
| Socket được đọc/ghi ở đâu? | `Session.java` |
| Làm sao lưu lại các message cũ? | `DataChat.java` |

---

## 💬 Lưu ý cho người mới

- Mỗi lần gửi message là 1 thread ảo gửi qua TCP (nhẹ, không block)
- Không được thao tác UI trong `Session` → phải thông qua Swing thread
- Mọi UI xử lý đều đi từ `ControllerMessage` hoặc `ChatPanel`
- Giao diện JList dùng `DefaultListModel` → muốn update thì `addElement()` / `removeElement()`

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
