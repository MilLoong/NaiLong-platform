# 📡 NaiLong Platform - API接口文档

## 基础信息

- **Base URL**: `http://localhost:8080/api`
- **认证方式**: Bearer Token (JWT)
- **Content-Type**: `application/json`

## 通用响应格式

### 成功响应
```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... },
  "timestamp": 1699000000000
}
```

### 失败响应
```json
{
  "code": 500,
  "message": "错误信息",
  "data": null,
  "timestamp": 1699000000000
}
```

## 常用状态码

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权，需要登录 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 429 | 请求过于频繁 |
| 500 | 服务器内部错误 |

---

## 1. 认证模块

### 1.1 用户注册

**接口地址**: `POST /auth/register`

**请求头**: 无需认证

**请求体**:
```json
{
  "username": "nailong2024",
  "password": "Password123",
  "email": "user@example.com",
  "code": "123456",
  "nickname": "凌睿新人",
  "studentId": "2024001",
  "realName": "张三",
  "grade": "2024",
  "major": "计算机科学与技术",
  "direction": "backend"
}
```

**字段说明**:
- `username`: 用户名，4-20个字符，只能包含字母数字下划线
- `password`: 密码，8-20个字符，建议包含大小写字母和数字
- `email`: 邮箱地址
- `code`: 邮箱验证码
- `direction`: 方向，可选值：frontend/backend/android/design/operations

**响应示例**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": null,
  "timestamp": 1699000000000
}
```

---

### 1.2 用户登录

**接口地址**: `POST /auth/login`

**请求体**:
```json
{
  "username": "nailong2024",
  "password": "Password123"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 7200,
    "userInfo": {
      "id": 1,
      "username": "nailong2024",
      "email": "user@example.com",
      "nickname": "凌睿新人",
      "direction": "backend",
      "totalScore": 0,
      "solvedCount": 0,
      "role": "USER",
      "createTime": "2024-01-01 12:00:00"
    }
  },
  "timestamp": 1699000000000
}
```

---

### 1.3 发送邮箱验证码

**接口地址**: `POST /auth/send-code`

**请求体**:
```json
{
  "email": "user@example.com",
  "type": "register"
}
```

**字段说明**:
- `type`: 验证码类型，可选值：register（注册）/ reset（重置密码）

**响应示例**:
```json
{
  "code": 200,
  "message": "验证码已发送，请查收邮件",
  "data": null,
  "timestamp": 1699000000000
}
```

**限流规则**: 60秒内只能发送一次

---

### 1.4 重置密码

**接口地址**: `POST /auth/reset-password`

**请求体**:
```json
{
  "email": "user@example.com",
  "code": "123456",
  "newPassword": "NewPassword123"
}
```

---

### 1.5 刷新Token

**接口地址**: `POST /auth/refresh?refreshToken=xxx`

**响应示例**:
```json
{
  "code": 200,
  "message": "Token刷新成功",
  "data": {
    "accessToken": "new_access_token",
    "refreshToken": "new_refresh_token",
    "expiresIn": 7200
  }
}
```

---

### 1.6 用户登出

**接口地址**: `POST /auth/logout`

**请求头**:
```
Authorization: Bearer {access_token}
```

---

## 2. 题目模块

### 2.1 获取题目列表

**接口地址**: `GET /problems`

**请求头**: Bearer Token (可选，登录后可显示是否已解决)

**查询参数**:
- `type`: 题目类型 (CHOICE/FLAG/FILE/CODE)
- `direction`: 方向 (frontend/backend/android/design/operations)
- `difficulty`: 难度 (EASY/MEDIUM/HARD)
- `keyword`: 关键词搜索
- `pageNum`: 页码，默认1
- `pageSize`: 每页数量，默认20

**示例**: `GET /problems?direction=backend&difficulty=MEDIUM&pageNum=1&pageSize=10`

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "后端基础：HTTP状态码",
        "type": "FLAG",
        "direction": "backend",
        "difficulty": "MEDIUM",
        "currentScore": 95,
        "solvedCount": 5,
        "tags": "[\"HTTP\", \"状态码\"]",
        "solved": false
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

---

### 2.2 获取题目详情

**接口地址**: `GET /problems/{id}`

**请求头**: Bearer Token (可选)

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "title": "后端基础：HTTP状态码",
    "description": "## 题目描述\n\n找出隐藏的Flag...",
    "type": "FLAG",
    "direction": "backend",
    "difficulty": "MEDIUM",
    "currentScore": 95,
    "solvedCount": 5,
    "submitCount": 10,
    "attachmentUrl": null,
    "hint": "把所有HTTP状态码按顺序连起来",
    "tags": "[\"HTTP\", \"状态码\"]",
    "solved": false,
    "createTime": "2024-01-01 12:00:00"
  }
}
```

---

### 2.3 提交答案 🔐

**接口地址**: `POST /problems/submit`

**请求头**: 必需Bearer Token

**请求体**:
```json
{
  "problemId": 1,
  "answer": "flag{200201401500}",
  "language": "Java"
}
```

**字段说明**:
- `language`: 编程语言（仅编程题需要）

**成功响应**:
```json
{
  "code": 200,
  "message": "提交成功",
  "data": null
}
```

**注意事项**:
- 限流：5秒内不能重复提交相同答案
- 已AC的题目不能再次提交
- 答案正确会自动更新用户积分和排行榜

---

### 2.4 创建题目 🔐 (管理员)

**接口地址**: `POST /problems`

**请求头**: 必需Bearer Token (ADMIN角色)

**请求体**:
```json
{
  "title": "算法题：两数之和",
  "description": "## 题目描述...",
  "type": "CODE",
  "direction": "backend",
  "difficulty": "HARD",
  "baseScore": 200,
  "answer": null,
  "attachmentUrl": null,
  "hint": "使用哈希表",
  "tags": "[\"算法\", \"哈希表\"]",
  "sortOrder": 0
}
```

---

### 2.5 更新题目 🔐 (管理员)

**接口地址**: `PUT /problems`

**请求体**:
```json
{
  "id": 1,
  "title": "新标题",
  "difficulty": "HARD",
  "status": 1
}
```

---

### 2.6 删除题目 🔐 (管理员)

**接口地址**: `DELETE /problems/{id}`

---

## 3. 排行榜模块

### 3.1 获取总排行榜

**接口地址**: `GET /rank/all?limit=50`

**查询参数**:
- `limit`: 返回数量，默认50

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "userId": 1,
      "username": "top_user",
      "nickname": "大神",
      "avatar": "https://...",
      "grade": "2024",
      "direction": "backend",
      "totalScore": 850,
      "solvedCount": 10,
      "rank": 1
    }
  ]
}
```

---

### 3.2 获取方向排行榜

**接口地址**: `GET /rank/direction/{direction}?limit=50`

**路径参数**:
- `direction`: frontend/backend/android/design/operations

---

### 3.3 获取我的排名 🔐

**接口地址**: `GET /rank/my`

**请求头**: 必需Bearer Token

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "userId": 1,
    "allRank": 5
  }
}
```

---

### 3.4 获取我的方向排名 🔐

**接口地址**: `GET /rank/my/direction/{direction}`

---

### 3.5 同步排行榜 🔐 (管理员)

**接口地址**: `POST /rank/sync`

**说明**: 手动触发排行榜数据从MySQL同步到Redis

---

## 4. 用户模块

### 4.1 获取当前用户信息 🔐

**接口地址**: `GET /user/me`

**请求头**: 必需Bearer Token

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "nailong2024",
    "email": "user@example.com",
    "nickname": "凌睿新人",
    "avatar": null,
    "studentId": "2024001",
    "realName": "张三",
    "grade": "2024",
    "major": "计算机科学与技术",
    "direction": "backend",
    "role": "USER",
    "totalScore": 150,
    "solvedCount": 3,
    "lastLoginTime": "2024-01-01 12:00:00",
    "createTime": "2024-01-01 10:00:00"
  }
}
```

---

### 4.2 更新用户信息 🔐

**接口地址**: `PUT /user/profile`

**请求体**:
```json
{
  "nickname": "新昵称",
  "avatar": "https://...",
  "phone": "13800138000",
  "grade": "2024",
  "major": "软件工程",
  "direction": "frontend"
}
```

---

## 5. 公告模块

### 5.1 获取公告列表

**接口地址**: `GET /notices?pageNum=1&pageSize=10`

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "欢迎参加凌睿工作室2024招新！",
        "content": "## 欢迎...",
        "type": "IMPORTANT",
        "top": true,
        "viewCount": 100,
        "publishTime": "2024-01-01 12:00:00"
      }
    ],
    "total": 1
  }
}
```

---

## 认证说明

### Token使用方式

在需要认证的接口中，请求头添加：
```
Authorization: Bearer {access_token}
```

### Token过期处理

1. `accessToken` 过期时间：2小时
2. `refreshToken` 过期时间：7天
3. 当收到401响应时，使用 `refreshToken` 刷新
4. 如果 `refreshToken` 也过期，需要重新登录

---

## 错误码详解

| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 1001 | 用户不存在 | 检查用户名/邮箱 |
| 1002 | 用户已存在 | 更换用户名 |
| 1003 | 密码错误 | 检查密码 |
| 1006 | 验证码错误或已过期 | 重新获取验证码 |
| 1007 | Token已过期 | 刷新Token |
| 2001 | 题目不存在 | 检查题目ID |
| 2002 | 题目已解决 | 不能重复提交 |
| 2003 | 答案错误 | 检查答案格式 |
| 9001 | 访问频率超限 | 稍后再试 |
| 9003 | 请勿重复提交 | 等待几秒后重试 |

---

## 限流说明

| 接口 | 限流规则 |
|------|----------|
| 注册 | 5次/秒 |
| 登录 | 10次/秒 |
| 发送验证码 | 1次/60秒 |
| 提交答案 | 5次/秒，相同答案5秒防重 |
| 其他接口 | 10次/秒 |

---

## 测试账号

**管理员账号**:
- 用户名: `admin`
- 密码: `Admin123`

**普通用户**:
- 注册后即可使用

---

## 在线文档

启动项目后访问：http://localhost:8080/api/doc.html

可在线测试所有接口！