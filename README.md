# NaiLong-platform

招新平台后端服务。提供用户认证、题目答题、排行榜、公告等能力，可配合前端一起使用。

本仓库为后端 API。开发环境可通过 Knife4j 在线调试接口。

## 功能

- **用户与认证**：邮箱验证码注册 / 登录、JWT 双 Token、密码加密、登录设备管理
- **题目系统**：支持选择题、Flag 题、附件题；分数随解题人数衰减；防重复提交
- **排行榜**：基于 Redis ZSet，支持总榜与方向榜，实时更新
- **公告管理**：公告发布与查询
- **安全防护**：JWT 会话校验、登录锁定、接口限流、IP 黑名单、角色权限控制
- **API 文档**：开发环境集成 Knife4j（生产默认关闭）

## 技术栈

| 类别 | 技术 |
|------|------|
| 框架 | Spring Boot 3、Spring Security |
| 持久化 | MySQL 8+、MyBatis-Plus |
| 缓存 | Redis、Caffeine、Redisson |
| 文档 | Knife4j (OpenAPI 3) |
| 构建 | Maven、Java 17+ |
| 部署 | Docker Compose / 本地运行 |

## 项目结构

```
NaiLong-platform/
├── src/main/java/com/nailong/
│   ├── controller/     # 接口层
│   ├── service/        # 业务逻辑
│   ├── mapper/         # 数据访问
│   ├── model/          # 实体 / DTO / VO
│   ├── security/       # JWT、过滤器、权限
│   ├── config/         # 配置类
│   └── common/         # 工具、注解、异常处理
├── src/main/resources/ # 配置文件
├── sql/                # 数据库脚本
├── docker/             # Docker Compose
└── docs/               # 接口与架构说明
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- Docker / Docker Compose（推荐），或本机已安装 MySQL、Redis

### 方式一：Docker Compose（推荐）

1. 准备环境变量

```bash
cp .env.example .env
# 编辑 .env，填写 MySQL / Redis / JWT 等密码
```

2. 启动

```bash
cd docker
docker compose up -d --build
```

将拉起 MySQL、Redis 与应用容器。首次启动时 MySQL 会执行 `sql/schema.sql` 初始化表结构；示例数据可手动导入：

```bash
docker compose exec -T mysql mysql -u nailong -p"$MYSQL_PASSWORD" nailong < ../sql/init_data.sql
```

3. 访问

| 入口 | 地址 |
|------|------|
| 服务 | http://localhost:8080 |
| API 文档（非 prod） | http://localhost:8080/api/doc.html |

常用命令：

```bash
docker compose ps
docker compose logs -f app
docker compose down
```

### 方式二：本地运行

1. 准备配置

```bash
cp src/main/resources/application-local.yml.example src/main/resources/application-local.yml
# 编辑 application-local.yml，填写本机数据库与 Redis 连接信息
```

2. 初始化数据库

```bash
mysql -u nailong -p -h 127.0.0.1 --default-character-set=utf8mb4 nailong < sql/schema.sql
mysql -u nailong -p -h 127.0.0.1 --default-character-set=utf8mb4 nailong < sql/init_data.sql
```

3. 启动应用

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev,local
```

或使用脚本：

```bash
# Windows
start.bat

# macOS / Linux
chmod +x start.sh
./start.sh
```

4. 验证

```bash
# 登录（初始管理员见 sql/init_data.sql）
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin123"}'
```

登录成功后，将返回的 `accessToken` 填入文档页 Authorize，或请求头：

```text
Authorization: Bearer <accessToken>
```

## 配置说明

| 配置 | 说明 |
|------|------|
| `spring.datasource.*` | MySQL 连接 |
| `spring.data.redis.*` | Redis 连接 |
| `redisson.*` | Redisson 地址与密码 |
| `nailong.jwt.*` | JWT 密钥与过期时间（秒） |
| `nailong.cors.*` | 跨域来源与凭证策略 |
| `nailong.security.api-docs-enabled` | 是否开放接口文档 |

本地敏感配置写在 `application-local.yml`（已加入 `.gitignore`）；Docker 部署写在 `.env`（已加入 `.gitignore`）。生产环境请通过环境变量注入 `JWT_SECRET`、`DB_PASSWORD`、`REDIS_PASSWORD`、`MAIL_PASSWORD` 等。

## 文档

- [API 接口说明](docs/API.md)
- [系统架构](docs/架构设计.md)
- 开发环境在线文档：`/api/doc.html`

## License

MIT
