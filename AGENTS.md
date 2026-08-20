# FutureStar 项目交接文档（给新 AI 的完整上下文）

> **用途**：让一个没有任何记忆的 AI 快速理解本项目 + 之前做过什么 + 协作方式。
>
> **怎么用**：
> - **IDE 插件**（Cursor / Windsurf / Copilot / Cline / Aider 等）→ 本项目根目录存为 `AGENTS.md`，多数插件会自动读取，无需手动粘贴。
> - **网页版 / 对话框 AI** → 直接把本文件全文粘贴进第一轮对话。
> - **回到 Claude Code** → 把本文件另存/改名为 `CLAUDE.md` 即可每次自动加载。

---

## 一句话

**智星足球青训综合管理系统（FutureStar）**：面向青少年足球青训的综合管理系统，双端齐全——**会员端**（球员用，Vue3+Vant4）与**管理端**（管理员用，Vue3+Element Plus），后端 Spring Boot 4.1 + Java 21。功能覆盖体能管理、技术/体能评测（AI 评分）、AI 对话（SSE 流式）、课程预约、赛事报名、消息通知、积分等级、管理后台 Dashboard。

## 技术栈

| 层面 | 技术 |
|------|------|
| 后端 | Spring Boot 4.1、Java 21、Maven、Lombok |
| 安全 | Spring Security + JWT（双 Token） |
| ORM | MyBatis + PageHelper 分页（XML 在 `src/main/resources/mapper/`） |
| 数据库 | MySQL 8.4（虚拟机 192.168.110.144，root/root） |
| 缓存 | Redis 8.4（Token 黑名单、验证码限流） |
| AI | Spring AI + DeepSeek（SSE 流式对话、评测评分、训练指导） |
| 定时任务 | Spring `@Scheduled`（8 个，见 `core/config/ScheduledTasks.java`） |
| 前端 | Vue 3 + Vite，会员端 Vant4（5173），管理端 Element Plus（5174），ECharts |
| 短信 | 阿里云 SDK（**未真正接入**，见「已知问题」） |

## 目录结构（改代码前先对号入座）

后端根包 `com.situ.futurestar`（`src/main/java/`）：

- `api/controller|service` — **会员端**接口，前缀 `/api/auth`、`/api/member`
  - `AuthController` 认证、`PhysicalController` 体能、`AssessmentController` 评测、`ChatController` AI 对话(SSE)、`CourseController` 课程、`EventController` 赛事、`MessageController` 消息、`ProfileController` 个人中心
- `admin/controller|service` — **管理端**接口，前缀 `/api/admin`，要求 ADMIN 角色
  - `DashboardController` 仪表盘、`PlayerController` 球员、`AdminCourseController`、`AdminAssessmentController`、`AdminEventController`、`AdminMessageController`、`AdminConfigController`
- `core/` — 共用层
  - `entity/` 实体（对应表）、`mapper/` + 接口、`dto/`、`vo/`
  - `config/`：`SecurityConfig` 安全、`ScheduledTasks` 定时任务、`ChatClientConfig` AI、`WebMvcConfig` 跨域
  - `filter/JwtAuthenticationFilter`、`exception/GlobalExceptionHandler`、`util/JwtUtil`+`SecurityUtil`
- 资源：`src/main/resources/mapper/`（XML）、`sql/futurestar.sql`（建库脚本，17 张表）、`md/`（需求/设计/API 文档）、`application.yml`、`limit.lua`（限流脚本）

前端（同仓库 `frontend/` 两个独立项目，均为 JS 非 TS）：

- `frontend/member/src/` — 会员端：`api/`（auth/physical/assessment/chat/course/event/message/profile.js）、`views/`（home/auth/chat/course/event/physical/assessment/profile/message 各模块）、`utils/request.js`（双 Token 刷新单飞队列）、`utils/sse.js`（SSE 解析）、`composables/useSendCode.js`、`styles/index.css`（运动风设计令牌）
- `frontend/admin/src/` — 管理端：`views/`（Dashboard/players/course/assessment/event/message/config/login/layout）、`components/BaseChart.vue`（ECharts）、商务高级风设计系统

## 已完成的功能

- **认证授权**：验证码（Redis ZSet + Lua 限流）、注册、登录、Token 刷新、登出（黑名单）、密码找回（JWT 双 Token）
- **体能记录**：录入（指标超标自动 AI 生成训练/饮食指导并推送提醒）、历史分页、趋势分析（体重/BMI/30 米冲刺）
- **技术/体能评测**：问卷、题目、评测结果 + AI 评分建议（评分落库）
- **AI 对话**：会话管理、SSE 流式对话（DeepSeek，断线自动保存片段）
- **课程预约**：套餐、时段（并发防超卖）、预约、取消、报告
- **赛事活动**：发布、报名（防重复）、签到（防重复加分）
- **消息通知**：系统/课程/体能提醒推送
- **个人中心**：资料、积分、会员等级
- **管理端 7 模块**：Dashboard 统计、球员管理、课程管理、评测管理、赛事管理、消息管理、系统配置
- **8 个定时任务**：赛事状态流转、预约状态同步、过期时段关闭、次日课程提醒、验证码/历史数据清理等

## 关键实现与约定（新 AI 改代码前必读）

1. **JWT 双 Token**：Access Token 2 小时（无状态，`Authorization: Bearer`），Refresh Token 7 天（存库）。登出时 Access Token 进 Redis 黑名单、Refresh 删库。找回密码删除该用户全部 Refresh Token。
2. **限流**：验证码接口用 Redis ZSet + Lua（`limit.lua`）限流。
3. **当前登录用户**：用 `SecurityUtil.getCurrentUserId()`（static 封装 SecurityContextHolder），不要用 `@AuthenticationPrincipal` 传参。
4. **统一返回**：`Result<T>` + `ErrorCode`；业务异常抛 `BizException`，全局处理器兜底（密码错误返回 401「手机号或密码错误」，不要让它变 500）。
5. **AI**：`DEEPSEEK_API_KEY` 环境变量（yml 里 `${DEEPSEEK_API_KEY}` 无默认值，**不设置启动会失败**）。
6. **分页**：PageHelper；分页 VO 用 `PageResult`。
7. **前端双 Token 刷新**：`frontend/member/src/utils/request.js` 有单飞刷新队列（401 时只发一次 refresh，其他请求排队）。
8. **SSE**：会员端 `frontend/member/src/utils/sse.js`（fetch + ReadableStream 解析 `data:` 行），对接 `ChatController` 流式对话。
9. **前端设计系统**：会员端 `src/styles/index.css` 设计令牌（品牌蓝 #2f7cff + 渐变、卡片阴影、TabBar 浮动圆角），管理端独立商务风。新页面先看现有样式再写。
10. **路由守卫**：会员端未登录跳登录；管理端要求 ADMIN 角色。

## 如何启动

⚠️ **本机默认 JAVA_HOME 是 JDK 17，必须切到 JDK 21 否则编译不过。**

```bash
export JAVA_HOME="/d/jdk21/jdk-21.0.12+8"
export PATH="$JAVA_HOME/bin:$PATH"
export DEEPSEEK_API_KEY=你的key        # 密钥存在项目根目录的" .env"（注意前面带空格）
export MAVEN_OPTS="-Dhttp.proxyHost=127.0.0.1 -Dhttp.proxyPort=7897 -Dhttps.proxyHost=127.0.0.1 -Dhttps.proxyPort=7897"
cd "D:/javacode/futurestar"
mvn spring-boot:run                     # 后端，localhost:8080，启动约 3-4 秒
```

前端（依赖安装走 7897 代理）：

```bash
pnpm -C frontend/member dev   # 会员端 5173
pnpm -C frontend/admin dev    # 管理端 5174
```

- 后台运行后端：`nohup mvn -q spring-boot:run > /d/jdk21/app.log 2>&1 &`，日志在 `/d/jdk21/app.log`
- 停止：`taskkill //F //PID <PID>`（`netstat -ano | grep :8080` 找 PID）

## 数据库与网络

- **所有对外网络访问走宿主机代理** `http://127.0.0.1:7897`（Maven 下载、GitHub push、pnpm 装依赖都要）。
- **MySQL + Redis 在虚拟机 `192.168.110.144`**（root/root，连接配置见 `application.yml`）。本地有一个 MySQL MCP 工具可直连 futurestar 库做查询/插入测试。
- MySQL 时区已修好为 `system_time_zone=CST`，`NOW()` 与东八区一致（曾因 UTC 错 8 小时导致验证码过期判断出错）。

## 测试账号与数据

| 账号 | 密码 | 说明 |
|------|------|------|
| 13800000000 | Admin@123456 | 管理员（管理端） |
| 13700005678 | NewPass123 | 球员 **Ronaldo Messi**（id=5，前端测试主力） |
| 13800138000 | Test@123456 | 测试球员（id=2，体能/评测测试） |
| 13800138123 | Test123456 | AI 测试用户（id=4） |

已补测试数据：Ronaldo Messi 9 条体能记录（趋势图数据）；问卷 2 份（足球技术自评 id=2 / 青少年体能基础 id=3）；活动 3 个；课程时段 id=9（套餐 1 今日）。

## 已知问题 / 待办

- **短信未实现**：`sendCode` 只落库 + `log.info` 打日志（联调从日志读 6 位验证码），未真正发短信（需阿里云 AccessKey + 模板）。
- 清理过期积分未做（需先建 points_log 流水表）；`PromptService` 可加 Redis 缓存（可选）。
- 训练指导去重按 type+日期，未精确到指标（需加 `trigger_indicator` 列）。
- 多处多步写库未加 `@Transactional`（register / resetPassword / 管理端批量写入）。
- refresh 无 Token 轮换（返回同一个，可用）；评测可反复刷积分（用户决定不改）。
- 前端 Phase4 待办：全流程浏览器走查、移动端真机适配、边界提示打磨。

## ⚠️ 易踩坑

1. 密钥文件名是 `" .env"`（**前面带空格**），`.gitignore` 已用 `[ ].env` 保护，**勿提交**。
2. **Windows curl 发中文 payload 会 400**（编码问题），浏览器不受影响；要测中文接口用浏览器或前端。
3. 堆积多个旧 vite dev 服务会导致"前端没变化"，需杀残留 node 进程再重启。
4. 新窗口/新终端**都要重新 export JDK21 + DEEPSEEK_API_KEY + MAVEN_OPTS**，环境变量不跨会话保留。

## 协作方式（重要，请遵守）

用户正在**学习 Spring Boot + Spring Security**，倾向自己动手写代码，AI 负责讲解原理、给思路骨架、检查纠错，而不是代写全部。

- 多讲**原理（为什么）**，给思路和骨架，让用户自己填；写完 review。
- 代码要**精简**：能不写的就不写，不建多余类。
- **不要提醒删除无用 import**（用户明确说过不用提醒，不影响运行）。
- 只提醒真正影响运行/安全的 bug；纯风格/死代码类问题少提。
- 回答问题用**中文**，简洁直接。
