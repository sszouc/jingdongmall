# 🛒 京东商城后端系统 (Jingdong Mall Backend)

一个基于 Spring Boot 构建的京东商城后端系统，采用现代化微服务架构设计，提供完整的电商功能。

## 🌟 项目特点

- **现代化架构**：采用 Spring Boot + MyBatis Plus + MySQL 技术栈
- **RESTful API**：遵循 REST 设计规范，前后端分离
- **安全可靠**：JWT 认证授权 + Spring Security
- **代码规范**：统一响应格式、全局异常处理、参数校验
- **易于扩展**：模块化设计，便于功能扩展和维护

## 🏗️ 技术栈

| 技术 | 版本     | 说明 |
|------|--------|------|
| Spring Boot | 3.x    | 核心框架 |
| Java | 25     | 开发语言 |
| MySQL | 8.0    | 数据库 |
| MyBatis Plus | 3.5.x  | ORM 框架 |
| JWT | 0.12.x | 认证授权 |
| Maven | 3.8+   | 项目管理 |
| Swagger/OpenAPI | 3.0    | API 文档 |

## 📁 项目结构

```
jingdong-mall-backend/
├── src/main/java/com/jingdong/mall/
│   ├── JingdongMallApplication.java    # 主启动类
│   ├── common/                         # 通用模块
│   │   ├── config/                     # 配置类
│   │   ├── exception/                  # 异常处理
│   │   ├── response/                   # 统一响应
│   │   └── utils/                      # 工具类
│   ├── controller/                     # 控制层
│   │   ├── api/                        # 用户端API
│   │   └── admin/                      # 管理端API
│   ├── service/                        # 业务层
│   │   └── impl/                       # 业务实现
│   ├── mapper/                         # 数据访问层
│   └── model/                          # 数据模型
│       ├── entity/                     # 实体类
│       └── dto/                        # 数据传输对象
│           ├── request/                # 请求DTO
│           └── response/               # 响应DTO
└── src/main/resources/
    ├── application.yml                 # 主配置
    ├── application-dev.yml             # 开发环境
    ├── application-prod.yml            # 生产环境
    └── mapper/                         # MyBatis XML
```

## 🚀 快速开始

### 环境要求

- JDK 17+
- MySQL 8.0+
- Maven 3.8+
- Redis 7.0+ (可选)

### 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE jd DEFAULT CHARACTER SET utf8mb4;
```

2. 执行数据库SQL建表语句（位于 `src/main/resources/db/schema.sql`）


### 启动应用

```bash
# 1. 克隆项目
git clone https://github.com/sszouc/jingdongmall.git

# 2. 运行应用
```


## 📚 API 文档

### Swagger UI
访问：https://s.apifox.cn/64b70f40-002d-482d-9eca-661da045186e

## 🔐 认证授权

系统采用 JWT (JSON Web Token) 进行身份认证：

### 1. 用户注册
```http
POST /api/auth/register
Content-Type: application/json

{
  "phone": "13800138000",
  "email": "user@example.com",
  "password": "password123",
  "code": "123456",
  "agreeProtocol": true
}
```

### 2. 用户登录
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "password123"
}
```

### 3. 使用 Token
在请求头中添加：
```
Authorization: Bearer <your_jwt_token>
```

## 📦 已经实现的API

### 用户认证
- 用户注销
- 用户注册
- 用户登录
- 找回密码

### 个人中心
- 退出登录
- 获取用户信息
- 更新用户信息
- 修改密码

### 地址管理
- 获取地址列表


## 🔧 开发指南

### 添加新模块

1. **创建实体类** (`model/entity/`)
2. **创建Mapper接口** (`mapper/`)
3. **创建Service接口和实现** (`service/` 和 `service/impl/`)
4. **创建Controller** (`controller/api/` 或 `controller/admin/`)
5. **添加DTO** (`model/dto/request/` 和 `model/dto/response/`)

### 编码规范

- 使用 Lombok 减少样板代码
- 接口方法必须有清晰的 JavaDoc 注释
- 统一使用 Result 包装响应
- 变量遵循驼峰命名

### 提交代码

```bash
# 1. 提交更改
git add .
git commit -m "feat: 添加用户注册功能"

# 2. 推送到远程
git push origin master
```
