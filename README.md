# 邀请注册系统

一个练手用的小网站：注册、登录、邀请码拉新、邀请奖励积分。

## 需求

* 首页有「注册」和「登录」两个按钮
* 注册要填：用户名、密码、邀请码（**选填**）
* 用户名重复时提示换一个用户名
* 注册后可用用户名 + 密码登录
* 注册成功直接进个人主页，显示：
  * 自己的随机邀请码（**注册时生成一次**，之后不再变）
  * 当前积分（每成功邀请 1 人 +10）
  * 被自己邀请来的用户列表：用户名 + 注册时间

## 技术栈

* **前端**：React 19 + Vite + TypeScript（单页应用，不引第三方路由，用状态切换 4 个视图）
* **后端**：Java 17 + Spring Boot 4 + Spring Data JPA + H2 文件库
* 密码用 BCrypt 哈希存储，不存明文
* 登录态是内存里的 token（`Authorization: Bearer xxx`），后端重启需重新登录；**用户数据存在 H2 文件里不会丢**

## 怎么跑起来

需要两个终端。

**1. 启动后端**（占用 8080 端口）

Linux / macOS：

```bash
cd backend
./mvnw spring-boot:run
```

Windows（PowerShell 或 CMD）：

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

**2. 启动前端**（占用 5173 端口）

Linux / macOS / Windows 都一样：

```bash
cd frontend
npm install     # 第一次才需要
npm run dev
```

然后打开 http://localhost:5173 。

> 数据库文件在 `backend/data/invite.mv.db`。想清空所有用户重新来过，把 `backend/data/` 删掉再启动后端即可。
> 想直接看表里的数据，打开 http://localhost:8080/h2-console ，JDBC URL 填 `jdbc:h2:file:./data/invite`，用户名 `sa`，密码留空。

## 接口一览

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/auth/register` | 注册，body 为 `{username, password, inviteCode?}`，成功返回 token |
| POST | `/api/auth/login` | 登录，body 为 `{username, password}`，成功返回 token |
| POST | `/api/auth/logout` | 退出登录，作废当前 token |
| GET | `/api/me` | 个人主页数据：邀请码、积分、被邀请人列表（需带 token） |

出错时统一返回 `{"message": "中文提示"}`，前端直接把这句话显示给用户。

常见的几种提示：

* 用户名重复 → `409` 用户名「xxx」已被注册，请换一个用户名
* 邀请码填错 → `400` 邀请码无效，请核对后重填，或留空直接注册
* 密码错误 → `401` 用户名或密码错误

## 代码结构

```
backend/src/main/java/com/example/invite_system/
├── domain/User.java              用户表：用户名、密码哈希、邀请码、邀请人、积分、注册时间
├── repository/UserRepository.java
├── service/
│   ├── UserService.java          注册/登录/查主页的核心逻辑，邀请码生成与发奖也在这
│   ├── TokenStore.java           token -> 用户 id 的内存映射
│   └── ApiException.java
├── web/
│   ├── AuthController.java       注册 / 登录 / 退出
│   ├── MeController.java         个人主页数据
│   ├── ApiExceptionHandler.java  统一错误格式
│   └── BearerToken.java
└── config/WebConfig.java         CORS + BCrypt

frontend/src/
├── api.ts                        后端调用 + token 存取（localStorage）
├── format.ts                     时间格式化
├── App.tsx                       视图切换：首页 / 注册 / 登录 / 个人主页
└── pages/                        四个页面组件
```
