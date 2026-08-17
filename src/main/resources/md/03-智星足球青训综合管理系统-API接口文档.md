# 智星足球青训综合管理系统-API 接口文档

---

## 文档信息

| 项目名称 | 智星足球青训综合管理系统 |
|---------|----------------------|
| 文档版本 | v1.0.0 |
| 编写日期 | 2026-08-14 |
| 配套详细设计文档版本 | v1.0.0 |

---

## 1. 接口规范

- 协议：HTTP/HTTPS（生产必须 HTTPS）
- 数据格式：JSON（UTF-8）
- 认证方式：`Authorization: Bearer <AccessToken>`（JWT）
- 基础路径：`/api`

### 1.1 统一响应格式

成功响应：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

错误响应：

```json
{
  "code": 409,
  "message": "该时段名额已满",
  "data": null,
  "traceId": "abc123def456"
}
```

### 1.2 统一错误码

| 错误码 | HTTP 状态 | 含义 |
|--------|-----------|------|
| 200 | 200 | 成功 |
| 400 | 400 | 请求参数错误（参数缺失、格式错误、参数越界） |
| 401 | 401 | 未登录或 Token 无效/过期 |
| 403 | 403 | 无权限访问（角色不足、资源非本人） |
| 404 | 404 | 资源不存在 |
| 409 | 409 | 业务冲突（名额已满、积分不足、重复操作） |
| 429 | 429 | 请求过于频繁（限流命中） |
| 500 | 500 | 系统内部错误 |

### 1.3 分页约定

- 请求参数：`pageNum`（从 1 开始）、`pageSize`（默认 10，最大 100）
- 响应 `data` 结构：

```json
{
  "total": 100,
  "list": [],
  "pageNum": 1,
  "pageSize": 10,
  "pages": 10
}
```

---

## 2. 会员端接口

### 2.1 认证授权 `/api/auth`

#### 2.1.1 发送验证码
- **POST** `/api/auth/send-code`
- 无需认证
- 请求体：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | string | 是 | 手机号 |

- 响应：`data: null`
- 限流：同手机号 3 次/分钟，10 次/天

#### 2.1.2 注册
- **POST** `/api/auth/register`
- 请求体：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | string | 是 | 手机号 |
| code | string | 是 | 短信验证码 |
| password | string | 是 | 密码（≥8 位，含字母和数字） |

- 成功响应：`data: null`（注册成功仅创建账号，不返回 token；如需凭证，前端应再调用 2.1.3 登录接口），注册自动赠送 100 积分

#### 2.1.3 登录
- **POST** `/api/auth/login`
- 请求体：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | string | 是 | 手机号 |
| password | string | 是 | 密码 |

- 成功响应：`data: { accessToken, refreshToken, user }`

#### 2.1.4 刷新 Token
- **POST** `/api/auth/refresh`
- 请求体：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| refreshToken | string | 是 | 刷新令牌 |

- 成功响应：`data: { accessToken, refreshToken }`

#### 2.1.5 登出
- **POST** `/api/auth/logout`
- 需认证（Bearer AccessToken）
- 响应：`data: null`（Access Token 加入黑名单，Refresh Token 从数据库删除）

#### 2.1.6 密码找回
- **POST** `/api/auth/reset-password`
- 请求体：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | string | 是 | 手机号 |
| code | string | 是 | 短信验证码 |
| password | string | 是 | 新密码 |

- 成功后强制该用户所有 Token 下线

---

### 2.2 体能记录 `/api/member/physical`

#### 2.2.1 录入体能记录
- **POST** `/api/member/physical`
- 请求体：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| height | number | 否 | 身高(cm) |
| weight | number | 否 | 体重(kg) |
| bodyFatRate | number | 否 | 体脂率(%) |
| heartRate | integer | 否 | 静息心率 |
| vitalCapacity | integer | 否 | 肺活量(ml) |
| sprint30m | number | 否 | 30米冲刺(秒) |
| standingLongJump | number | 否 | 立定跳远(cm) |
| verticalJump | number | 否 | 原地纵跳(cm) |
| enduranceRun | integer | 否 | 12分钟耐力跑(米) |
| recordedAt | datetime | 否 | 记录时间，不传默认当前时间（用于补录历史数据） |
| memo | string | 否 | 备注 |

- 系统自动计算 BMI，超出阈值自动生成提醒并推送

#### 2.2.2 查询历史记录
- **GET** `/api/member/physical?pageNum=1&pageSize=10`
- 需认证
- 响应 `data`：分页，`list` 为体能记录列表（倒序）

#### 2.2.3 趋势分析
- **GET** `/api/member/physical/trend?months=6`
- 需认证
- 响应 `data`：

```json
{
  "weight": { "avg": 54.5, "max": 55.0, "min": 52.0, "points": [] },
  "bmi": { "avg": 19.4, "max": 19.5, "min": 19.1, "points": [] },
  "sprint30m": { "avg": 4.64, "max": 4.85, "min": 4.48, "points": [] }
}
```

---

### 2.3 技术/体能评测 `/api/member/assessment`

#### 2.3.1 问卷列表
- **GET** `/api/member/assessment/questionnaires`
- 需认证
- 响应 `data`：已发布问卷列表

#### 2.3.2 获取问卷题目
- **GET** `/api/member/assessment/questionnaires/{id}/questions`
- 需认证
- 响应 `data`：题目列表（含类型、选项）

#### 2.3.3 提交评测
- **POST** `/api/member/assessment`
- 请求体：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| questionnaireId | integer | 是 | 问卷 ID |
| answers | json | 是 | 答案快照（见详细设计文档 6.3.7） |

- 系统调用 AI 评分并生成建议，赠送 20 积分
- 响应 `data`：`{ id, aiScore, aiSuggestion }`

#### 2.3.4 评测历史
- **GET** `/api/member/assessment/history?pageNum=1&pageSize=10`
- 需认证

#### 2.3.5 评测详情
- **GET** `/api/member/assessment/{id}`
- 需认证
- 响应 `data`：评测结果 + AI 评分 + AI 建议 + 答案

---

### 2.4 AI 对话 `/api/member/chat`

#### 2.4.1 创建会话
- **POST** `/api/member/chat/session`
- 请求体：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| sessionName | string | 否 | 会话名称（默认自动生成） |

- 响应 `data`：`{ id, sessionName }`

#### 2.4.2 会话列表
- **GET** `/api/member/chat/session/list`
- 需认证

#### 2.4.3 发送消息（流式）
- **POST** `/api/member/chat/session/{id}/stream`
- 请求体：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| content | string | 是 | 用户消息 |

- 响应：`text/event-stream`（SSE 流式），逐字推送，最后推送 `[DONE]`
- AI 自动携带最近 10 轮上下文

#### 2.4.4 对话历史
- **GET** `/api/member/chat/session/{id}/messages`
- 需认证

#### 2.4.5 删除会话
- **DELETE** `/api/member/chat/session/{id}`
- 需认证（逻辑删除）

---

### 2.5 课程预约 `/api/member/course`

#### 2.5.1 课程套餐列表
- **GET** `/api/member/course/packages?pageNum=1&pageSize=10`
- 需认证
- 响应 `data`：课程套餐列表（含课程名、教练、价格、适合水平、项目）

#### 2.5.2 套餐详情
- **GET** `/api/member/course/packages/{id}`
- 需认证

#### 2.5.3 可预约时段
- **GET** `/api/member/course/packages/{id}/slots?date=2026-08-15`
- 需认证
- 响应 `data`：该日可预约时段列表（含剩余名额）

#### 2.5.4 提交预约
- **POST** `/api/member/course/appointment`
- 请求体：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| slotId | integer | 是 | 时段 ID |

- 扣减积分、名额原子操作，并发安全
- 业务冲突：积分不足 / 名额已满 → 409

#### 2.5.5 我的预约
- **GET** `/api/member/course/appointment/list?pageNum=1&pageSize=10&status=`
- 需认证
- 响应 `data`：预约记录（含课程、时段、状态、报告地址）

#### 2.5.6 取消预约
- **POST** `/api/member/course/appointment/{id}/cancel`
- 需认证
- 退还积分、释放名额，发送取消短信

#### 2.5.7 查看报告
- **GET** `/api/member/course/appointment/{id}/report`
- 需认证（仅本人或管理员）
- 返回带 5 分钟签名签发的 PDF 下载地址

---

### 2.6 赛事活动 `/api/member/event`

#### 2.6.1 活动列表
- **GET** `/api/member/event?pageNum=1&pageSize=10&type=`
- 需认证

#### 2.6.2 活动详情
- **GET** `/api/member/event/{id}`
- 需认证

#### 2.6.3 报名
- **POST** `/api/member/event/{id}/register`
- 需认证
- 并发名额控制，人数已满 → 409，重复报名 → 409

#### 2.6.4 我的活动
- **GET** `/api/member/event/my?pageNum=1&pageSize=10`
- 需认证

#### 2.6.5 签到状态
- **GET** `/api/member/event/{id}/checkin-status`
- 需认证

#### 2.6.6 签到
- **POST** `/api/member/event/{id}/checkin`
- 需认证，活动进行中才可签到
- 签到赠送 50 积分

---

### 2.7 个人中心 `/api/member/profile`

#### 2.7.1 获取个人信息
- **GET** `/api/member/profile`
- 需认证
- 响应 `data`：`{ id, realName, gender, birthDate, height, weight, position, preferredFoot, experienceYears, avatar, emergencyContact, memberLevel, points }`

#### 2.7.2 更新个人信息
- **PUT** `/api/member/profile`
- 请求体：个人信息字段（含场上位置、惯用脚、球龄等）

#### 2.7.3 修改密码
- **PUT** `/api/member/profile/password`
- 请求体：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| oldPassword | string | 是 | 原密码 |
| newPassword | string | 是 | 新密码 |

---

### 2.8 消息通知 `/api/member/message`

#### 2.8.1 消息列表
- **GET** `/api/member/message?pageNum=1&pageSize=10&type=`
- 需认证

#### 2.8.2 消息详情
- **GET** `/api/member/message/{id}`
- 需认证

#### 2.8.3 标记已读
- **PUT** `/api/member/message/{id}/read`
- 需认证

#### 2.8.4 未读统计
- **GET** `/api/member/message/unread-count`
- 需认证
- 响应 `data`：`{ count: 5 }`

---

## 3. 管理端接口

### 3.1 仪表盘 `/api/admin/dashboard`

#### 3.1.1 数据概览
- **GET** `/api/admin/dashboard`
- 需 ADMIN
- 响应 `data`：

```json
{
  "playerTotal": 1000,
  "todayNewPlayers": 12,
  "todayCourseAppointments": 30,
  "todayEventRegistrations": 8,
  "pendingAppointments": 15
}
```

---

### 3.2 球员管理 `/api/admin/players`

#### 3.2.1 球员列表
- **GET** `/api/admin/players?pageNum=1&pageSize=10&keyword=&position=&status=`
- 需 ADMIN
- `keyword`：姓名/手机号模糊搜索；`position`：场上位置筛选

#### 3.2.2 球员详情
- **GET** `/api/admin/players/{id}`
- 需 ADMIN
- 响应 `data`：基本信息 + 积分 + 体能记录 + 预约记录

#### 3.2.3 启用/禁用
- **PUT** `/api/admin/players/{id}/status`
- 请求体：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | string | 是 | ENABLED / DISABLED |

#### 3.2.4 调整等级
- **PUT** `/api/admin/players/{id}/level`
- 请求体：`{ memberLevel: "SILVER" }`

#### 3.2.5 调整积分
- **PUT** `/api/admin/players/{id}/points`
- 请求体：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| delta | integer | 是 | 积分变更量（正加负减） |
| reason | string | 否 | 变更原因 |

#### 3.2.6 重置密码
- **PUT** `/api/admin/players/{id}/reset-password`
- 需 ADMIN

---

### 3.3 课程管理 `/api/admin/course`

#### 3.3.1 课程套餐 CRUD
- **GET** `/api/admin/course/packages?pageNum=1&pageSize=10`
- **POST** `/api/admin/course/packages`（新增）
- **PUT** `/api/admin/course/packages/{id}`（修改）
- **DELETE** `/api/admin/course/packages/{id}`（删除，逻辑删除）

新增/修改请求体：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | string | 是 | 课程名称 |
| coverUrl | string | 否 | 封面图 |
| description | string | 否 | 描述 |
| price | integer | 是 | 价格（积分） |
| coachName | string | 否 | 授课教练 |
| suitableLevel | string | 否 | 适合水平 |
| items | json | 否 | 训练项目数组 |
| status | string | 否 | ENABLED/DISABLED |

#### 3.3.2 批量生成时段
- **POST** `/api/admin/course/packages/{id}/slots/batch`
- 请求体：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| startDate | date | 是 | 开始日期 |
| endDate | date | 是 | 结束日期 |
| timeRange | string | 是 | 时间段，如 `18:00-19:30` |
| maxCount | integer | 是 | 每时段最大人数 |

#### 3.3.3 时段管理
- **GET** `/api/admin/course/slots?packageId=&date=`
- **PUT** `/api/admin/course/slots/{id}`（修改最大人数/状态）

#### 3.3.4 预约管理
- **GET** `/api/admin/course/appointments?pageNum=1&pageSize=10&packageId=&date=&status=`
- 需 ADMIN

#### 3.3.5 上传报告
- **POST** `/api/admin/course/appointments/{id}/report`（multipart/form-data，字段名 `file`）
- 仅允许 PDF，≤20MB
- 存储到 `data/upload/report/yyyyMM/{uuid}.pdf`

---

### 3.4 评测管理 `/api/admin/assessment`

#### 3.4.1 问卷 CRUD
- **GET** `/api/admin/assessment/questionnaires?pageNum=1&pageSize=10`
- **POST** `/api/admin/assessment/questionnaires`
- **PUT** `/api/admin/assessment/questionnaires/{id}`
- **DELETE** `/api/admin/assessment/questionnaires/{id}`
- 发布/下架：`PUT /api/admin/assessment/questionnaires/{id}/status`

#### 3.4.2 题目 CRUD
- **GET** `/api/admin/assessment/questionnaires/{id}/questions`
- **POST** `/api/admin/assessment/questionnaires/{id}/questions`
- **PUT** `/api/admin/assessment/questions/{id}`
- **DELETE** `/api/admin/assessment/questions/{id}`

题目请求体：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| content | string | 是 | 题目内容 |
| type | string | 是 | SINGLE/MULTIPLE/TEXT |
| options | json | 否 | 选项数组（SINGLE/MULTIPLE 必填） |
| sortOrder | integer | 否 | 排序号 |

---

### 3.5 赛事管理 `/api/admin/event`

#### 3.5.1 活动 CRUD
- **GET** `/api/admin/event?pageNum=1&pageSize=10&status=`
- **POST** `/api/admin/event`
- **PUT** `/api/admin/event/{id}`
- **DELETE** `/api/admin/event/{id}`

活动请求体：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| title | string | 是 | 活动标题 |
| type | string | 否 | MATCH/CAMP/SELECTION |
| content | string | 否 | 内容 |
| registrationStart/End | datetime | 否 | 报名起止 |
| activityStart/End | datetime | 否 | 活动起止 |
| maxParticipants | integer | 否 | 人数上限 |

#### 3.5.2 报名列表
- **GET** `/api/admin/event/{id}/registrations?pageNum=1&pageSize=10`
- 需 ADMIN

---

### 3.6 消息管理 `/api/admin/message`

#### 3.6.1 消息列表
- **GET** `/api/admin/message?pageNum=1&pageSize=10`
- 需 ADMIN

#### 3.6.2 推送消息（单个）
- **POST** `/api/admin/message/send`
- 请求体：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | integer | 是 | 接收用户 |
| title | string | 是 | 标题 |
| content | string | 否 | 内容 |
| sendSms | boolean | 否 | 是否同时发短信 |

#### 3.6.3 批量推送
- **POST** `/api/admin/message/batch-send`
- 请求体：`{ userIds: [], title, content, sendSms }`

---

### 3.7 系统配置 `/api/admin/config`

#### 3.7.1 配置列表
- **GET** `/api/admin/config`
- 需 ADMIN

#### 3.7.2 获取配置
- **GET** `/api/admin/config/{key}`
- 需 ADMIN

#### 3.7.3 更新配置
- **PUT** `/api/admin/config/{key}`
- 请求体：`{ configValue: "..." }`
- 可用于修改 AI 提示词、积分规则参数等

---

## 4. 权限说明

| 接口前缀 | 允许角色 |
|---------|---------|
| `/api/auth/**`、`/api/sms/**` | 公开（部分需认证） |
| `/api/member/**` | PLAYER、ADMIN |
| `/api/admin/**` | ADMIN |

---

## 5. 附录：版本历史

| 版本 | 日期 | 修订内容 | 修订人 |
|------|------|---------|--------|
| v1.0.0 | 2026-08-14 | 初始版本 | 培训班学员 |

---
