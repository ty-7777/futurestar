-- 滑动窗口限流 ZSet实现
-- KEYS[1] 限流key，例如：rate:captcha:phone:138xxxx
-- ARGV[1] 窗口大小 毫秒 windowMs
-- ARGV[2] 最大允许请求数 maxCount
-- ARGV[3] 当前时间戳(毫秒) nowTime

local key = KEYS[1]
local windowMs = tonumber(ARGV[1])
local maxCount = tonumber(ARGV[2])
local nowTime = tonumber(ARGV[3])

-- 移除窗口外旧数据
redis.call('ZREMRANGEBYSCORE', key, 0, nowTime - windowMs)
-- 统计窗口内数量
local current = redis.call('ZCARD', key)

if current >= maxCount then
    -- 被限流
    return 0
end

-- 存入当前时间戳作为score和member
redis.call('ZADD', key, nowTime, nowTime)
-- 设置key过期，窗口秒数+10s冗余，避免残留脏数据
redis.call('EXPIRE', key, math.ceil(windowMs / 1000) + 10)
return 1
