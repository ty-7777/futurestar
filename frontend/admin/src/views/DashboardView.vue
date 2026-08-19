<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getDashboard } from '@/api/dashboard'
import { getAdminAppointments } from '@/api/course'
import { getPlayers } from '@/api/player'
import { formatDate } from '@/utils/format'

const router = useRouter()
const stats = ref(null)

const cards = [
  { key: 'playerTotal', label: '球员总数', icon: 'User', color: '#2f7cff', bg: '#eaf2ff' },
  { key: 'todayNewPlayers', label: '今日新增球员', icon: 'UserFilled', color: '#16b8cf', bg: '#e5f9fb' },
  { key: 'todayCourseAppointments', label: '今日课程预约', icon: 'Notebook', color: '#2eb35c', bg: '#e8f8ee' },
  { key: 'todayEventRegistrations', label: '今日赛事报名', icon: 'Trophy', color: '#f08a24', bg: '#fff3e5' },
  { key: 'pendingAppointments', label: '待确认预约', icon: 'AlarmClock', color: '#ee0a24', bg: '#fdecee' }
]

// 快捷入口
const shortcuts = [
  { title: '球员管理', desc: '账号/积分/等级', icon: 'UserFilled', path: '/players' },
  { title: '课程管理', desc: '套餐/时段/报告', icon: 'Notebook', path: '/course' },
  { title: '评测管理', desc: '问卷/题目', icon: 'EditPen', path: '/assessment' },
  { title: '赛事管理', desc: '活动/报名', icon: 'Trophy', path: '/event' },
  { title: '消息管理', desc: '推送/通知', icon: 'ChatDotRound', path: '/message' },
  { title: '系统配置', desc: 'AI提示词/积分规则', icon: 'Setting', path: '/config' }
]

// 待办（待确认预约）
const todos = ref([])
const APPOINT_STATUS = { PENDING: '待确认', CONFIRMED: '已确认', COMPLETED: '已完成', CANCELED: '已取消' }

// 最近球员
const recentPlayers = ref([])

onMounted(async () => {
  try {
    stats.value = await getDashboard()
  } catch {
    // 统一提示
  }
  try {
    const data = await getAdminAppointments({ pageNum: 1, pageSize: 5, status: 'PENDING' })
    todos.value = data.list
  } catch {
    // 忽略
  }
  try {
    const data = await getPlayers({ pageNum: 1, pageSize: 5 })
    recentPlayers.value = data.list
  } catch {
    // 忽略
  }
})
</script>

<template>
  <div class="dashboard">
    <div class="page-head">
      <h2 class="page-title">仪表盘</h2>
      <p class="page-sub">系统数据概览与快捷操作</p>
    </div>

    <!-- 指标卡 -->
    <div class="dash-grid">
      <div v-for="c in cards" :key="c.key" class="fs-card dash-card">
        <div class="dash-card__icon" :style="{ background: c.bg, color: c.color }">
          <el-icon :size="22"><component :is="c.icon" /></el-icon>
        </div>
        <div>
          <div class="dash-card__num">{{ stats ? stats[c.key] : '-' }}</div>
          <div class="dash-card__label">{{ c.label }}</div>
        </div>
      </div>
    </div>

    <!-- 快捷入口 + 待办事项 -->
    <div class="dash-cols">
      <div class="fs-card block">
        <div class="block__head">快捷入口</div>
        <div class="shortcut-grid">
          <div v-for="s in shortcuts" :key="s.path" class="shortcut" @click="router.push(s.path)">
            <div class="shortcut__icon"><el-icon :size="20"><component :is="s.icon" /></el-icon></div>
            <div>
              <div class="shortcut__title">{{ s.title }}</div>
              <div class="shortcut__desc">{{ s.desc }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="fs-card block">
        <div class="block__head">待办事项 · 待确认预约</div>
        <div v-if="todos.length" class="todo-list">
          <div v-for="t in todos" :key="t.id" class="todo-item">
            <div class="todo-item__dot" />
            <div class="todo-item__body">
              <div class="todo-item__title">{{ t.packageName }}</div>
              <div class="todo-item__meta">{{ t.courseDate }} {{ t.timeRange }}</div>
            </div>
            <el-tag size="small">{{ APPOINT_STATUS[t.status] || t.status }}</el-tag>
          </div>
        </div>
        <el-empty v-else description="暂无待办" :image-size="60" />
      </div>
    </div>

    <!-- 最近球员 -->
    <div class="fs-card block">
      <div class="block__head">最近球员</div>
      <el-table :data="recentPlayers" size="small" stripe>
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="points" label="积分" width="90" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ENABLED' ? 'success' : 'danger'" size="small">
              {{ row.status === 'ENABLED' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="注册时间">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<style scoped>
.dash-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}
.dash-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 20px;
}
.dash-card__icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.dash-card__num {
  font-size: 24px;
  font-weight: 700;
  color: #1d2129;
  line-height: 1.2;
}
.dash-card__label {
  font-size: 13px;
  color: #98a1b0;
  margin-top: 2px;
}

.dash-cols {
  display: grid;
  grid-template-columns: 1.4fr 1fr;
  gap: 16px;
  margin-bottom: 16px;
}
.block {
  padding: 16px 18px;
}
.block__head {
  font-size: 15px;
  font-weight: 700;
  color: #1d2129;
  margin-bottom: 14px;
  padding-left: 10px;
  border-left: 3px solid var(--el-color-primary);
}

.shortcut-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}
.shortcut {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px;
  border: 1px solid #f0f2f6;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.18s ease;
}
.shortcut:hover {
  border-color: var(--el-color-primary-light-5);
  box-shadow: 0 2px 10px rgba(47, 124, 255, 0.1);
  transform: translateY(-2px);
}
.shortcut__icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.shortcut__title {
  font-size: 14px;
  font-weight: 600;
  color: #1d2129;
}
.shortcut__desc {
  font-size: 12px;
  color: #98a1b0;
  margin-top: 2px;
}

.todo-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.todo-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  background: #f8fafc;
  border-radius: 8px;
}
.todo-item__dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--el-color-danger);
  flex-shrink: 0;
}
.todo-item__body {
  flex: 1;
}
.todo-item__title {
  font-size: 14px;
  font-weight: 600;
  color: #1d2129;
}
.todo-item__meta {
  font-size: 12px;
  color: #98a1b0;
  margin-top: 2px;
}

@media (max-width: 1100px) {
  .dash-cols {
    grid-template-columns: 1fr;
  }
  .shortcut-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
