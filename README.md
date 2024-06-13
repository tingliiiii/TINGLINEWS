# TINGLINEWS 新聞網站
TINGLINEWS 是一個功能豐富的新聞網站，提供了新聞報導、會員管理、收藏和贊助等功能。網站包括前台和後台，提供使用者註冊、登入、查看新聞、收藏新聞、贊助新聞等操作。後台可以新增或編輯報導、更改使用者權限、刪除使用者等，並開放使用者以 GitHub 帳號登入。

## 功能介紹
### 前台功能
1. **註冊和登入**：
   - 使用者可以透過註冊表單註冊新帳號。
   - 支持使用電子郵件和 GitHub 帳號登入。

2. **查看新聞**：
   - 使用者可以查看最新的新聞報導。
   - 可依照類別篩選新聞。

3. **收藏新聞**：
   - 使用者登入後，可以收藏喜歡的新聞報導，便於日後查看。

4. **贊助新聞**：
   - 使用者登入後，可以透過單次或定期贊助支持新聞報導。
   - 贊助時需輸入驗證碼，保證操作安全。

### 後台功能

1.**權限管理**：
   - 管理使用者權限，包括使用者、員工、編輯、記者、主管和管理員。
   - 只有員工才可成功登入後台。

2. **新增或編輯報導**：
   - 編輯或記者可以新增或編輯新聞報導。
   - 可點擊「編輯」按鈕進行編輯。
   - 可點兩下「發布」欄位，調整報導公開狀態。

3. **使用者管理**：
   - 查看和管理用戶列表。
   - 更改用戶的權限級別（僅限主管或管理員）。
   - 刪除用戶（僅限管理員）。


## 技術詳情

### 前端

- 使用 HTML5、CSS3 和 JavaScript。
- JavaScript 主要使用 jQuery 和 ES6 語法。
- 使用 jQuery 和 DataTables 外掛來實現動態表格功能。
- 使用 SweetAlert2 來顯示提示框和確認框。

### 後端

- 使用 Spring 框架與 Java 語言處理請求和響應。
- 提供 RESTful API 來管理用戶和新聞報導數據。
- 渲染方式為 CSR（Client-Side Rendering）。
- 支持跨域請求，方便前端調用後端接口。


### 數據庫

- 使用 MySQL 儲存使用者和新聞報導資訊。

## API 說明

### 資訊安全 API

- `GET /user/login`: 獲取 CSRF Token
- `GET /callback/github`：使用 GitHub 登入
- `GET /callback/github/exchange`：使用 GitHub 授權碼交換訪問令牌
- `POST /user/sendEmail`：發送 OTP 驗證碼郵件
- `POST /user/verifyOTP`：驗證 OTP
- `PATCH /user/resetPassword`：重設密碼
- `GET /user/captcha`：獲取 captcha 驗證碼
- `POST /user/captcha`：驗證 captcha 驗證碼

### 使用者 API

- `POST /user/register`：使用者註冊
- `POST /user/login`：使用者登入
- `GET /user/profile/{userId}`：查看用戶資料
- `PUT /user/update/{userId}`：更新用戶資料

### 新聞 API

- `GET /news/list/`：查看所有已公開新聞
- `GET /news/list/{tagId}`：按照標籤查看新聞
- `GET /news/{newsId}`：查看單篇新聞

### 使用者管理 API

- `POST /emp/login`: 後台員工登入
- `GET /emp/user`：查看所有使用者
- `DELETE /emp/user/{userId}`：刪除使用者（僅限管理員）
- `GET /emp/authority`：獲取所有權限選項
- `PATCH /emp/authority/{userId}`：更改使用者權限（僅限主管和管理員）

### 內容管理 API

- `GET /emp/news`：查詢所有新聞
- `POST /emp/post`：新增新聞（僅限編輯以上員工）
- `GET /emp/tags`：查詢所有標籤
- `GET /emp/journalists`：查詢所有記者
- `GET /emp/news/{newsId}`：查看單篇新聞
- `PUT /emp/news/{newsId}`：修改新聞（僅限編輯以上員工）
- `PATCH /emp/publish/{newsId}`：發布或取消發布文章（僅限編輯以上員工）

### 贊助 API

- `POST /user/donate`：使用者贊助
- `DELETE /user/donate/{donateId}`：取消贊助

### 收藏 API

- `POST /user/saved`：收藏新聞
- `DELETE /user/saved/{savedId}`：取消收藏
