# 未然學舍WeiranSociety(持續開發中)
WeiranSociety是一個前後端分離的線上課程平台，提供完整的學習、購買、教學管理流程包含:
學生註冊/登入/購物車/訂單
綠界ECPay金流串接(測試環境)
課程播放、進度紀錄
評價系統
老師課程上架與內容管理
管理者後台(用戶、課程、分類管理)
此專案採用Monorepo架構，整合前端、後端、資料庫

#技術架構
Backend-Spring Boot(Java 17)
Spring Boot3
Spring Security(JWt驗證與授權)
Spring Data JPA
Microsoft SQL Server(MSSQL)
ECPay 綠界金流 Stage API（測試環境）
Gmail SMTP Mail Sender(信件寄送)
Maven Wrapper(mvnw)

#前端
Frontend-Vue 3(Composition API)
Vite
Pinia
Bootstrap 5
Axios
Vue Router

#資料庫
Database-MSSQL
提供完整 Schema 與假資料
已設計：
Users（主使用者資料表）
StudentProfile / TeacherProfile（角色資料）
Course / CourseSection / CourseMedia（課程與內容）
Cart / CartDetail（購物車）
Order / OrderDetail（訂單）
Coupon / CouponType（優惠券系統）
Payment（金流交易）
已包含：
完整建表
外鍵
索引
關聯
具備實務情境的資料設計

#已完成功能(Completed Features)
使用者登入/註冊(JWT+加密)
學生/老師/管理員三身分(支援多重身分功能)
課程管理CRUD(新增/編輯/分類/影片上傳)
課程播放與進度紀錄
購物車
訂單建立流程
綠界ECPay測試金流
課程評價
後台管理系統(課程/使用者/類別)
MSSQL Schema完整建置

#開發中(Under Development/Refactoring)

架構與程式碼優化:
專案整體架構重構／優化
API 命名統一化與前後端資料格式一致化
前端 UI 風格統一化
老師與學生專屬介面再設計

功能開發中:
學習進度統計視覺化
更完整的課程評論系統
客服系統（預計整合 AI 並與管理員後台串接）
課程推薦系統（預計使用 AI 或使用者行為分析）

部署與擴充:
雲端部署規劃（前端 / 後端 / 資料庫）
檔案改存雲端（Azure Blob / AWS S3）
主流第三方登入（Line / IG / Gmail / Facebook）
金流擴充（Line Pay、Apple Pay）
