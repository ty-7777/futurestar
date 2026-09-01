# 智星足球青训综合管理系统 (FutureStar)

一个面向青少年足球青训的综合性管理系统，覆盖**会员端**（球员）与**管理端**（管理员），提供体能管理、评测、AI 对话、课程预约、赛事报名、消息通知等能力。

## 技术栈

| 层面 | 技术 |
|------|------|
| 后端框架 | Spring Boot 4.1、Java 21 |
| 安全认证 | Spring Security + JWT（双 Token 认证） |
| ORM | MyBatis + PageHelper 分页 |
| 数据库 | MySQL 8.4 |
| 缓存 | Redis 8.4（Token 黑名单、验证码限流） |
| AI | Spring AI（DeepSeek / OpenAI 兼容，SSE 流式对话） |
| AOP | Spring AOP + AspectJ（切面日志、接口限流） |
| 定时任务 | Spring @Scheduled |
| 短信 | 阿里云短信 SDK |
| 文件存储 | 阿里云 OSS（头像签名直传） |
| 构建 | Maven、Lombok |
| 前端 | Vue 3 + Vite，会员端 Vant 4（5173），管理端 Element Plus（5174），ECharts |

## 项目结构

```
com.situ.futurestar
├── core/                 # 核心/通用层
│   ├── common/           # 统一响应 Result、错误码 ErrorCode
│   ├── config/           # 安全、MyBatis、Redis、跨域配置
│   ├── entity/           # 实体类（对应数据库表）
│   ├── mapper/           # MyBatis 数据访问层
│   ├── service/          # 两端共用的公共业务
│   ├── filter/           # JWT 认证过滤器
│   ├── exception/        # 业务异常 + 全局异常处理
│   ├── util/             # JWT、SecurityUtil 等工具
│   ├── dto/              # 请求 DTO
│   └── vo/               # 响应 VO
├── api/                  # 会员端 API（/api/member、/api/auth）
│   ├── controller/
│   └── service/
└── admin/                # 管理端 API（/api/admin）
    ├── controller/
    └── service/
```

## 功能模块

- **认证授权**：验证码（Redis ZSet+Lua 限流）、注册、登录、Token 刷新（**轮换**）、登出、密码找回（JWT 双 Token）；接口级 `@RateLimit` 注解限流（按 IP+方法维度，Redis 滑动窗口）
- **体能记录**：录入（超标自动 AI 生成训练/饮食指导并推送提醒）、历史分页查询、趋势分析（体重/BMI/30米冲刺）
- **技术/体能评测**：问卷、题目、评测结果 + AI 评分建议（评分落库）
- **AI 对话**：会话管理、SSE 流式对话（DeepSeek，断线自动保存片段）；长对话自动**滚动压缩**为摘要，保持长期记忆
- **AI 智能客服**：与 AI 对话分离的客服入口，Function Calling 让 AI 直接查询课程套餐/赛事活动
- **课程预约**：套餐、时段（并发防超卖）、预约、取消、报告；防重复预约（`active_key` 哨兵+唯一索引兜底）；**管理员审核**（确认/拒绝，拒绝自动退还积分并释放名额）
- **首页搜索**：首页搜索框查询课程/赛事，结果展示在独立搜索页（支持返回、Tab 切换、触底分页）
- **赛事活动**：活动发布、报名（防重复）、签到（防重复加分）
- **消息通知**：系统/课程/体能提醒推送
- **个人中心**：资料、积分、会员等级；**头像上传**（阿里云 OSS V4 签名直传）
- **操作日志**：`@Log` 注解切面日志，登录/体能录入/评测提交/课程预约等关键操作自动记录入参、耗时与成败（密码脱敏）
- **管理端**（要求 ADMIN 角色）：Dashboard 统计、球员管理、课程管理、评测管理、赛事管理、消息管理、系统配置
- **定时任务**：赛事状态流转、预约状态同步、过期时段关闭、次日课程提醒、验证码/历史数据清理等（7 个）

## 认证设计（JWT 双 Token）

- **Access Token**：有效期 30 分钟，无状态，每次请求携带于 `Authorization: Bearer`
- **Refresh Token**：有效期 7 天，存数据库，用于刷新 Access Token；刷新时**轮换**（旧 Token 作废删除，生成新 Token 落库返回）
- 若检测到已作废的旧 Refresh Token 再次被使用（重放），判定为泄露，**撤销该用户全部会话**（删库）
- 登出时 Access Token 加入 **Redis 黑名单**（TTL 与剩余有效期一致），Refresh Token 从数据库删除
- 找回密码时删除该用户所有 Refresh Token 强制重新登录

## 快速开始

### 1. 环境要求

- JDK 21
- Maven 3.9+
- MySQL 8.4、Redis 8.4

### 2. 初始化数据库

```bash
mysql -u root -p < src/main/resources/sql/futurestar.sql
```

脚本会创建 `futurestar` 数据库、17 张业务表，并写入默认管理员、测试球员及示例数据。

### 3. 配置

修改 `src/main/resources/application.yml`：

- `spring.datasource.url/username/password`：数据库连接
- `spring.data.redis.host/port`：Redis 连接
- `jwt.secret`：JWT 签名密钥（支持 `JWT_SECRET` 环境变量覆盖，生产环境务必修改）
- **`DEEPSEEK_API_KEY` 环境变量**：DeepSeek API Key，AI 对话 / 评测评分 / 训练指导功能依赖。本地可存于 `.env` 文件（已加入 `.gitignore`，**禁止提交**），启动前需让应用读到该变量
- **`OSS_ACCESS_KEY_ID` / `OSS_ACCESS_KEY_SECRET` 环境变量**：阿里云 OSS 密钥（头像上传签名直传），无默认值，缺失会导致启动失败；`oss.endpoint` / `oss.bucket` 按自己地域与存储桶修改
- 阿里云短信参数：申请验证码模板后填写

### 4. 启动

```bash
# 需要 AI 功能时先设置环境变量（Windows PowerShell：$env:DEEPSEEK_API_KEY="sk-..."）
export DEEPSEEK_API_KEY=sk-...
mvn spring-boot:run
```

默认端口 `8080`。接口前缀：`/api/auth`、`/api/member`、`/api/admin`，详细见 `src/main/resources/md/` 下的接口文档。

### 5. 启动前端（可选）

仓库内 `frontend/` 下为两个独立前端项目：

```bash
pnpm -C frontend/member dev   # 会员端（Vant4），http://localhost:5173
pnpm -C frontend/admin dev    # 管理端（Element Plus），http://localhost:5174
```

### 默认账号

| 角色 | 手机号 | 密码 |
|------|--------|------|
| 管理员 | 13800000000 | Admin@123456 |
| 测试球员 | 13800138000 | Test@123456 |

## 文档

- 需求规格说明书：`src/main/resources/md/01-*.md`
- 详细设计文档：`src/main/resources/md/02-*.md`
- API 接口文档：`src/main/resources/md/03-*.md`
