# TINGLINEWS 新聞網站
TINGLINEWS 是功能豐富且易於使用的新聞網站，提供使用者瀏覽新聞報導、登入註冊、收藏和贊助支持等功能，並開放使用者以 GitHub 帳號登入。網站後台管理系統則可以新增或編輯報導、更改使用者權限、刪除使用者等。

## 功能介紹
### 前台功能
1. **註冊和登入**
   - 使用者可以透過註冊表單註冊新帳號。
   - 可使用 Email 或 GitHub 帳號登入，兼顧登入的便捷與安全。
   - 若忘記密碼，可透過 Email 傳送 OTP 驗證碼重設密碼。

2. **查看新聞**
   - 使用者可以查看最新的新聞報導。
   - 可依照標籤查看相關新聞。

3. **收藏新聞**
   - 使用者登入後，可以收藏喜歡的新聞報導，便於日後查看與分享。

4. **贊助新聞**
   - 使用者可以透過單次或定期贊助支持新聞報導。
   - 贊助時需輸入 captcha 驗證碼，確保贊助來源的真實性。

### 後台功能

1. **權限管理**
   - 管理使用者權限，分類包括使用者、員工、編輯、記者、主管和管理員。
   - 僅限員工登錄後台，保障系統安全。

2. **新增或編輯報導**
   - 編輯或記者使用 TinyMCE 文本編輯器，新增或編輯新聞報導。
   - 可點擊「編輯」按鈕進行編輯。
   - 可點兩下「發布」欄位，調整報導公開狀態。

3. **使用者管理**
   - 查看和管理使用者列表。
   - 調整使用者權限（僅限主管或管理員）。
   - 刪除使用者（僅限管理員）。


## 技術詳情

### 前端

- 使用 HTML5、CSS3 和 JavaScript 技術。
- 使用 Bootstrap 框架實現響應式設計，確保網站在各種設備上的良好顯示效果。
- JavaScript 主要使用 jQuery 和 ES6 語法。
- 使用 jQuery 和 DataTables 外掛來實現動態表格功能。
- 使用 SweetAlert2 美化提示框和確認框。
- 使用 Select2 提供增強的選擇框。
- 使用 TinyMCE 作為文本編輯器，提高內容創作效率。

### 後端

- 使用 Spring 框架與 Java 語言處理請求和響應。
- 提供 RESTful API 管理使用者和新聞報導數據，方便前端集成和調用。
- 採用 CSR（Client-Side Rendering） 渲染技術，提高頁面響應速度。
- 支持跨域請求，方便前端調用後端接口，拓展系統靈活性。


### 資料庫

- 使用 MySQL 資料庫儲存使用者和新聞報導資訊。

## API 說明

### 使用者 API

- `GET /users/csrf-token`：取得 CSRF Token
- `POST /users/register`：註冊
- `POST /users/login`：登入
- `GET /users/{userId}/profile`：查看使用者個人資訊
- `PUT /users/{userId}/profile`：更新使用者個人資訊
- `POST /users/otp`：發送 OTP 驗證碼 Email
- `POST /users/otp/verify`：驗證 OTP
- `PATCH /users/password`：重設密碼

- `POST /users/favorites`：收藏
- `DELETE /users/favorites/{favoriteId}`：取消收藏
- `GET /users/captcha`：取得 captcha 驗證碼
- `POST /users/captcha/verify`：驗證 captcha 驗證碼
- `POST /users/donations`：贊助
- `DELETE /users/donations/{donationId}`：取消贊助

### 新聞 API

- `GET /news`：查看所有已公開新聞
- `GET /news/{newsId}`：查看單篇新聞
- `GET /news/list/{tagId}`：按照標籤查看新聞


### 內容及使用者管理 API

- `GET /admin/news`：查看所有新聞
- `POST /admin/news`：新增新聞（僅限編輯以上員工）
- `GET /admin/tags`：查詢所有標籤選項
- `GET /admin/journalists`：查詢所有記者
- `GET /admin/news/{newsId}`：查詢單篇報導
- `PUT /admin/news/{newsId}`：修改新聞（僅限編輯以上員工）
- `PATCH /admin/news/{newsId}/publish`：發布或取消發布報導（僅限編輯以上員工）

- `GET /admin/users`：查看所有使用者
- `DELETE /admin/users/{userId}`：刪除使用者
- `GET /admin/authorities`：查詢所有權限選項
- `PATCH /admin/users/{userId}/authority`：修改使用者權限（僅限主管和管理員）

- `GET /admin/statistic/toptags`：查詢新聞收藏數標籤排行榜
- `GET /admin/statistic/topsavednews`：查詢新聞收藏數排行榜
- `GET /admin/statistic/topjournalists`：查詢新聞收藏數記者排行榜

### OAuth2 API

- `GET /callback/github/exchange`：使用 GitHub 授權碼交換訪問令牌
