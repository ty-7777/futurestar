-- =====================================================================
-- 智星足球青训综合管理系统 - 数据库初始化脚本
-- 数据库：futurestar   版本：v1.0.0   日期：2026-08-14
-- 说明：包含 17 张业务表建表语句 + 默认管理员/测试球员 + 示例数据
-- 运行方式：mysql -u root -p < futurestar.sql
-- =====================================================================

CREATE DATABASE IF NOT EXISTS futurestar
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE futurestar;

-- ---------------------------------------------------------------------
-- 1. 用户表 user（球员 + 管理员）
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS user (
  id               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  phone            VARCHAR(20)  NOT NULL COMMENT '手机号',
  password         VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
  real_name        VARCHAR(50)  DEFAULT NULL COMMENT '真实姓名',
  gender           VARCHAR(10)  DEFAULT NULL COMMENT '性别',
  birth_date       DATE         DEFAULT NULL COMMENT '出生日期',
  height           DECIMAL(5,1) DEFAULT NULL COMMENT '身高(cm)',
  weight           DECIMAL(5,1) DEFAULT NULL COMMENT '体重(kg)',
  position         VARCHAR(20)  DEFAULT NULL COMMENT '场上位置:FORWARD前锋/MIDFIELDER中场/DEFENDER后卫/GOALKEEPER门将',
  preferred_foot   VARCHAR(20)  DEFAULT NULL COMMENT '惯用脚:LEFT左脚/RIGHT右脚/BOTH双脚',
  experience_years INT          DEFAULT 0 COMMENT '球龄(年)',
  avatar           VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
  emergency_contact VARCHAR(20) DEFAULT NULL COMMENT '紧急联系人电话',
  member_level     VARCHAR(20)  DEFAULT 'NORMAL' COMMENT '会员等级:NORMAL/SILVER/GOLD/PLATINUM/DIAMOND',
  points           INT          DEFAULT 0 COMMENT '积分',
  status           VARCHAR(20)  DEFAULT 'ENABLED' COMMENT '状态:ENABLED/DISABLED',
  role             VARCHAR(20)  NOT NULL COMMENT '角色:PLAYER/ADMIN',
  create_time      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted          TINYINT      DEFAULT 0 COMMENT '逻辑删除:0未删/1已删',
  PRIMARY KEY (id),
  UNIQUE KEY uk_phone (phone),
  KEY idx_status (status),
  KEY idx_role (role),
  KEY idx_position (position)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ---------------------------------------------------------------------
-- 2. 刷新令牌表 refresh_token
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS refresh_token (
  id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  user_id     BIGINT       NOT NULL COMMENT '用户ID',
  token       VARCHAR(500) NOT NULL COMMENT 'Refresh Token值',
  expire_time DATETIME     NOT NULL COMMENT '过期时间',
  create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_user_id (user_id),
  KEY idx_expire_time (expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='刷新令牌表';

-- ---------------------------------------------------------------------
-- 3. 短信验证码表 sms_code
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sms_code (
  id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  phone       VARCHAR(20) NOT NULL COMMENT '手机号',
  code        VARCHAR(10) NOT NULL COMMENT '验证码',
  expire_time DATETIME    NOT NULL COMMENT '过期时间',
  used        TINYINT     DEFAULT 0 COMMENT '是否已使用:0未用/1已用',
  create_time DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_phone (phone),
  KEY idx_expire_time (expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短信验证码表';

-- ---------------------------------------------------------------------
-- 4. 体能记录表 physical_record
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS physical_record (
  id                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  user_id             BIGINT       NOT NULL COMMENT '用户ID',
  height              DECIMAL(5,1) DEFAULT NULL COMMENT '身高(cm)',
  weight              DECIMAL(5,1) DEFAULT NULL COMMENT '体重(kg)',
  bmi                 DECIMAL(3,1) DEFAULT NULL COMMENT 'BMI指数',
  body_fat_rate       DECIMAL(4,1) DEFAULT NULL COMMENT '体脂率(%)',
  heart_rate          INT          DEFAULT NULL COMMENT '静息心率(次/分)',
  vital_capacity      INT          DEFAULT NULL COMMENT '肺活量(ml)',
  sprint_30m          DECIMAL(4,2) DEFAULT NULL COMMENT '30米冲刺(秒)',
  standing_long_jump  DECIMAL(5,1) DEFAULT NULL COMMENT '立定跳远(cm)',
  vertical_jump       DECIMAL(5,1) DEFAULT NULL COMMENT '原地纵跳(cm)',
  endurance_run       INT          DEFAULT NULL COMMENT '12分钟耐力跑(米)',
  memo                VARCHAR(500) DEFAULT NULL COMMENT '备注',
  recorded_at         DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
  create_time         DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time         DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted             TINYINT      DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id),
  KEY idx_user_id (user_id),
  KEY idx_user_recorded (user_id, recorded_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体能记录表';

-- ---------------------------------------------------------------------
-- 5. 问卷表 questionnaire
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS questionnaire (
  id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '问卷ID',
  title       VARCHAR(200) NOT NULL COMMENT '问卷标题',
  description VARCHAR(1000) DEFAULT NULL COMMENT '问卷描述',
  status      VARCHAR(20)  DEFAULT 'DRAFT' COMMENT '状态:DRAFT草稿/PUBLISHED已发布',
  create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted     TINYINT      DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问卷表';

-- ---------------------------------------------------------------------
-- 6. 题目表 question
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS question (
  id               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '题目ID',
  questionnaire_id BIGINT       NOT NULL COMMENT '问卷ID',
  content          VARCHAR(500) NOT NULL COMMENT '题目内容',
  type             VARCHAR(20)  NOT NULL COMMENT '类型:SINGLE单选/MULTIPLE多选/TEXT文本',
  options          JSON         DEFAULT NULL COMMENT '选项JSON数组',
  sort_order       INT          DEFAULT 0 COMMENT '排序号',
  create_time      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted          TINYINT      DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id),
  KEY idx_questionnaire_id (questionnaire_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';

-- ---------------------------------------------------------------------
-- 7. 评测结果表 assessment_result
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS assessment_result (
  id               BIGINT   NOT NULL AUTO_INCREMENT COMMENT '评测结果ID',
  user_id          BIGINT   NOT NULL COMMENT '用户ID',
  questionnaire_id BIGINT   NOT NULL COMMENT '问卷ID',
  answers          JSON     DEFAULT NULL COMMENT '答案快照JSON',
  ai_score         INT      DEFAULT NULL COMMENT 'AI评分(百分制)',
  ai_suggestion    TEXT     DEFAULT NULL COMMENT 'AI建议',
  create_time      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted          TINYINT  DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id),
  KEY idx_user_id (user_id),
  KEY idx_questionnaire_id (questionnaire_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评测结果表';

-- ---------------------------------------------------------------------
-- 8. 课程套餐表 course_package
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS course_package (
  id             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '套餐ID',
  name           VARCHAR(200) NOT NULL COMMENT '课程名称',
  cover_url      VARCHAR(500) DEFAULT NULL COMMENT '封面图URL',
  description    TEXT         DEFAULT NULL COMMENT '课程描述',
  price          INT          DEFAULT 0 COMMENT '价格(积分抵扣)',
  coach_name     VARCHAR(50)  DEFAULT NULL COMMENT '授课教练',
  suitable_level VARCHAR(200) DEFAULT NULL COMMENT '适合水平',
  items          JSON         DEFAULT NULL COMMENT '包含训练项目JSON',
  status         VARCHAR(20)  DEFAULT 'ENABLED' COMMENT '状态:ENABLED/DISABLED',
  create_time    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted        TINYINT      DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程套餐表';

-- ---------------------------------------------------------------------
-- 9. 课程时段表 course_slot
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS course_slot (
  id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '时间段ID',
  package_id    BIGINT       NOT NULL COMMENT '课程套餐ID',
  course_date   DATE         NOT NULL COMMENT '课程日期',
  time_range    VARCHAR(50)  NOT NULL COMMENT '时间段,如 18:00-19:30',
  max_count     INT          DEFAULT 20 COMMENT '最大预约人数',
  current_count INT          DEFAULT 0 COMMENT '当前已预约人数',
  status        VARCHAR(20)  DEFAULT 'AVAILABLE' COMMENT '状态:AVAILABLE可约/FULL已满/CLOSED已关闭',
  create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted       TINYINT      DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id),
  KEY idx_package_date (package_id, course_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程时段表';

-- ---------------------------------------------------------------------
-- 10. 课程预约表 course_appointment
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS course_appointment (
  id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  user_id     BIGINT       NOT NULL COMMENT '用户ID',
  slot_id     BIGINT       NOT NULL COMMENT '时间段ID',
  package_id  BIGINT       NOT NULL COMMENT '课程套餐ID',
  status      VARCHAR(20)  DEFAULT 'PENDING' COMMENT '状态:PENDING待确认/CONFIRMED已确认/CANCELED已取消/COMPLETED已完成',
  report_url  VARCHAR(500) DEFAULT NULL COMMENT '训练/表现报告URL',
  create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted     TINYINT      DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id),
  KEY idx_user_id (user_id),
  KEY idx_slot_id (slot_id),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程预约表';

-- ---------------------------------------------------------------------
-- 11. 赛事活动表 match_event
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS match_event (
  id                    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  title                 VARCHAR(200) NOT NULL COMMENT '活动标题',
  type                  VARCHAR(20)  DEFAULT 'MATCH' COMMENT '类型:MATCH比赛/CAMP训练营/SELECTION选拔',
  cover_url             VARCHAR(500) DEFAULT NULL COMMENT '封面图URL',
  content               TEXT         DEFAULT NULL COMMENT '活动内容',
  registration_start    DATETIME     DEFAULT NULL COMMENT '报名开始时间',
  registration_end      DATETIME     DEFAULT NULL COMMENT '报名结束时间',
  activity_start        DATETIME     DEFAULT NULL COMMENT '活动开始时间',
  activity_end          DATETIME     DEFAULT NULL COMMENT '活动结束时间',
  max_participants      INT          DEFAULT NULL COMMENT '人数上限',
  current_participants  INT          DEFAULT 0 COMMENT '当前报名人数',
  status                VARCHAR(20)  DEFAULT 'DRAFT' COMMENT '状态:DRAFT草稿/REGISTRATING报名中/IN_PROGRESS进行中/ENDED已结束',
  create_time           DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time           DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted               TINYINT      DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛事活动表';

-- ---------------------------------------------------------------------
-- 12. 赛事报名表 event_registration
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS event_registration (
  id               BIGINT      NOT NULL AUTO_INCREMENT COMMENT '报名ID',
  user_id          BIGINT      NOT NULL COMMENT '用户ID',
  event_id         BIGINT      NOT NULL COMMENT '活动ID',
  check_in_status  VARCHAR(20) DEFAULT 'NOT_CHECKED_IN' COMMENT '签到状态:NOT_CHECKED_IN未签到/CHECKED_IN已签到',
  create_time      DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
  update_time      DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted          TINYINT     DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_event (user_id, event_id),
  KEY idx_event_id (event_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛事报名表';

-- ---------------------------------------------------------------------
-- 13. 训练指导表 training_guidance
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS training_guidance (
  id          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '指导ID',
  user_id     BIGINT   NOT NULL COMMENT '用户ID',
  type        VARCHAR(20) NOT NULL COMMENT '类型:TRAINING训练/DIET饮食/RECOVERY恢复/DATA_SUMMARY数据小结',
  content     TEXT     NOT NULL COMMENT '指导内容',
  is_read     TINYINT  DEFAULT 0 COMMENT '是否已读:0未读/1已读',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '生成时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted     TINYINT  DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id),
  KEY idx_user_id (user_id),
  KEY idx_user_type (user_id, type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='训练指导表';

-- ---------------------------------------------------------------------
-- 14. AI 会话表 ai_conversation_session
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS ai_conversation_session (
  id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  user_id     BIGINT       NOT NULL COMMENT '用户ID',
  session_name VARCHAR(100) DEFAULT NULL COMMENT '会话名称',
  create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted     TINYINT      DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id),
  KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI会话表';

-- ---------------------------------------------------------------------
-- 15. AI 对话消息表 ai_conversation_message
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS ai_conversation_message (
  id          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  session_id  BIGINT   NOT NULL COMMENT '会话ID',
  user_id     BIGINT   NOT NULL COMMENT '用户ID',
  role        VARCHAR(20) NOT NULL COMMENT '角色:user用户/assistant助手',
  message     TEXT     NOT NULL COMMENT '消息内容',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted     TINYINT  DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id),
  KEY idx_session_id (session_id),
  KEY idx_user_session (user_id, session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI对话消息表';

-- ---------------------------------------------------------------------
-- 16. 消息表 message
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS message (
  id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  user_id     BIGINT       NOT NULL COMMENT '用户ID',
  title       VARCHAR(200) NOT NULL COMMENT '消息标题',
  content     TEXT         DEFAULT NULL COMMENT '消息内容',
  type        VARCHAR(20)  DEFAULT NULL COMMENT '类型:COURSE课程/EVENT赛事/PHYSICAL体能/SYSTEM系统',
  is_read     TINYINT      DEFAULT 0 COMMENT '是否已读:0未读/1已读',
  create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted     TINYINT      DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id),
  KEY idx_user_id (user_id),
  KEY idx_is_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';

-- ---------------------------------------------------------------------
-- 17. 系统配置表 sys_config
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_config (
  id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  config_key  VARCHAR(100) NOT NULL COMMENT '配置键',
  config_value TEXT        DEFAULT NULL COMMENT '配置值',
  description VARCHAR(500) DEFAULT NULL COMMENT '配置描述',
  create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted     TINYINT      DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- =====================================================================
-- 以下为示例 / 初始化数据
-- =====================================================================

-- 系统配置初始化
INSERT INTO sys_config (config_key, config_value, description) VALUES
('ai_chat_system_prompt', '你是一位专业的足球青训顾问，请结合青少年球员的体能、身高、体型等数据，用易懂的语言提供训练与饮食建议。', 'AI对话系统提示词'),
('ai_assessment_prompt', '你是一位专业足球青训教练，根据问卷答案对学员进行百分制评分并给出针对性建议。', 'AI评测提示词'),
('ai_guidance_training_prompt', '你是足球青训教练，根据学员体能数据和异常指标给出具体训练建议。', 'AI训练指导提示词'),
('ai_guidance_diet_prompt', '你是青少年运动营养师，根据学员体能数据和异常指标给出饮食建议。', 'AI饮食指导提示词'),
('register_bonus_points', '100', '注册赠送积分'),
('checkin_bonus_points', '50', '活动签到赠送积分'),
('physical_assessment_min_score', '60', '体能评测及格分数线'),
('access_token_expire_hours', '2', 'Access Token有效期(小时)'),
('refresh_token_expire_days', '7', 'Refresh Token有效期(天)');

-- 默认管理员（密码：Admin@123456，BCrypt 加密）
INSERT INTO user (phone, password, real_name, member_level, points, status, role) VALUES
('13800000000', '$2b$10$5xxJYAxX3bB35VkjlRAuauILyrcKEUXJINVQXrWPYl6vhfZlIiy46', '系统管理员', 'PLATINUM', 99999, 'ENABLED', 'ADMIN');

-- 测试球员（密码：Test@123456，BCrypt 加密）
INSERT INTO user (phone, password, real_name, gender, birth_date, height, weight, position, preferred_foot, experience_years, member_level, points, status, role) VALUES
('13800138000', '$2b$10$La.Q.aZ.SUB5Ej3neFdzGOUYLva/QuO7sALyOPaBCxyYkro9Cpzjm', '测试球员', '男', '2012-05-20', 168.0, 55.0, 'FORWARD', 'RIGHT', 3, 'NORMAL', 1000, 'ENABLED', 'PLAYER');

-- 示例技术/体能评测问卷
INSERT INTO questionnaire (title, description, status) VALUES
('青少年足球体能评测问卷', '通过简单的问题了解您的基本体能和训练状况', 'PUBLISHED');

-- 问卷题目示例
INSERT INTO question (questionnaire_id, content, type, options, sort_order) VALUES
(1, '您的年龄是？', 'SINGLE', '["6-9岁", "10-12岁", "13-15岁", "16岁以上"]', 1),
(1, '您每周参加几次足球训练？', 'SINGLE', '["几乎不训练", "1-2 次", "3-4 次", "5 次以上"]', 2),
(1, '您目前的位置偏好是什么？', 'SINGLE', '["前锋", "中场", "后卫", "门将"]', 3),
(1, '请简要描述您目前的体能状况', 'TEXT', NULL, 4);

-- 示例课程套餐
INSERT INTO course_package (name, description, price, coach_name, suitable_level, items, status) VALUES
('基础技术训练', '适合初学者的基础传接球、带球与射门技术训练', 500, '王教练', '初级',
 '["传接球训练", "带球过人", "射门练习", "基础体能"]', 'ENABLED'),
('体能强化训练', '针对速度、耐力、力量与敏捷性的专项体能训练', 1000, '李教练', '中级',
 '["冲刺训练", "耐力跑", "核心力量", "敏捷梯"]', 'ENABLED'),
('战术配合训练', '针对团队配合与战术意识的高级训练课程', 800, '张教练', '高级',
 '["阵型演练", "进攻配合", "防守组织", "定位球"]', 'ENABLED');

-- 示例课程时段（基于今天动态生成 3 天内的课时，方便演示预约）
INSERT INTO course_slot (package_id, course_date, time_range, max_count, current_count, status) VALUES
(1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '18:00-19:30', 20, 5, 'AVAILABLE'),
(1, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '18:00-19:30', 20, 0, 'AVAILABLE'),
(2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '19:30-21:00', 20, 8, 'AVAILABLE'),
(3, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '19:30-21:00', 20, 20, 'FULL');

-- 示例赛事活动
INSERT INTO match_event (title, type, content, registration_start, registration_end, activity_start, activity_end, max_participants, current_participants, status) VALUES
('U12 秋季联赛', 'MATCH', '面向 12 岁以下球员的秋季五人制联赛', DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 7 DAY), DATE_ADD(CURDATE(), INTERVAL 10 DAY), DATE_ADD(CURDATE(), INTERVAL 10 DAY), 60, 12, 'REGISTRATING'),
('暑期足球训练营', 'CAMP', '为期两周的暑期封闭式训练营，含技战术与体能训练', DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 14 DAY), DATE_ADD(CURDATE(), INTERVAL 20 DAY), DATE_ADD(CURDATE(), INTERVAL 34 DAY), 30, 3, 'REGISTRATING');

-- 示例测试球员体能记录（近 6 个月，用于趋势分析演示）
INSERT INTO physical_record (user_id, height, weight, bmi, body_fat_rate, heart_rate, vital_capacity, sprint_30m, standing_long_jump, vertical_jump, endurance_run, recorded_at) VALUES
(2, 165.0, 52.0, 19.1, 15.2, 72, 3200, 4.85, 210.0, 42.0, 2200, DATE_SUB(CURDATE(), INTERVAL 5 MONTH)),
(2, 166.0, 53.0, 19.2, 14.8, 70, 3400, 4.72, 218.0, 45.0, 2350, DATE_SUB(CURDATE(), INTERVAL 4 MONTH)),
(2, 166.5, 54.0, 19.5, 14.5, 68, 3550, 4.60, 224.0, 47.0, 2450, DATE_SUB(CURDATE(), INTERVAL 3 MONTH)),
(2, 167.0, 54.5, 19.5, 14.3, 67, 3700, 4.55, 230.0, 49.0, 2550, DATE_SUB(CURDATE(), INTERVAL 2 MONTH)),
(2, 168.0, 55.0, 19.5, 14.0, 66, 3800, 4.48, 236.0, 51.0, 2650, DATE_SUB(CURDATE(), INTERVAL 1 MONTH));
