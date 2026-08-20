<script setup>
import { onMounted, ref } from 'vue'
import { showConfirmDialog, showToast } from 'vant'
import { useRoute, useRouter } from 'vue-router'
import { getPackageDetail, getSlots, createAppointment } from '@/api/course'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const packageId = route.params.id

const detail = ref(null)
const slots = ref([])
const loadingSlots = ref(false)
const selectedDate = ref('')

const WEEK = ['日', '一', '二', '三', '四', '五', '六']
const pad = (n) => String(n).padStart(2, '0')
const days = ref([])

const STATUS = { AVAILABLE: '可预约', FULL: '已满', CLOSED: '已关闭' }

const parseItems = (items) => {
  if (!items) return []
  try {
    return JSON.parse(items)
  } catch {
    return []
  }
}

const buildDays = () => {
  const now = new Date()
  const list = []
  for (let i = 0; i < 14; i++) {
    const d = new Date(now)
    d.setDate(now.getDate() + i)
    const date = `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
    const label = i === 0 ? '今天' : i === 1 ? '明天' : `周${WEEK[d.getDay()]}`
    list.push({ date, label, day: `${pad(d.getMonth() + 1)}-${pad(d.getDate())}` })
  }
  return list
}

const loadSlots = async (date) => {
  loadingSlots.value = true
  try {
    slots.value = await getSlots(packageId, date)
  } catch {
    // 错误提示已由请求层统一处理
  } finally {
    loadingSlots.value = false
  }
}

const selectDate = (date) => {
  selectedDate.value = date
  loadSlots(date)
}

onMounted(async () => {
  days.value = buildDays()
  selectedDate.value = days.value[0].date
  try {
    detail.value = await getPackageDetail(packageId)
  } catch {
    // 统一提示
  }
  loadSlots(selectedDate.value)
})

const onBook = async (slot) => {
  try {
    await showConfirmDialog({
      title: '确认预约',
      message: `使用 ${detail.value.price} 积分预约 ${slot.timeRange} 时段？`
    })
  } catch {
    return
  }
  await createAppointment(slot.id)
  showToast('预约成功')
  userStore.user.points -= detail.value.price
  loadSlots(selectedDate.value)
}
</script>

<template>
  <div class="pd">
    <van-nav-bar title="套餐详情" left-arrow @click-left="router.back()" />

    <div v-if="detail" class="pd__card">
      <div class="pd__head">
        <span class="pd__name">{{ detail.name }}</span>
        <span class="pd__price">{{ detail.price }} 积分</span>
      </div>
      <div class="pd__meta">
        <span v-if="detail.coachName">教练：{{ detail.coachName }}</span>
        <span v-if="detail.suitableLevel">{{ detail.suitableLevel }}</span>
      </div>
      <div v-if="detail.description" class="pd__desc">{{ detail.description }}</div>
      <div v-if="parseItems(detail.items).length" class="pd__items">
        <van-tag v-for="(it, i) in parseItems(detail.items)" :key="i" plain type="primary">
          {{ it }}
        </van-tag>
      </div>
    </div>

    <div class="pd__dates">
      <div
        v-for="d in days"
        :key="d.date"
        class="pd__date"
        :class="{ 'pd__date--active': d.date === selectedDate }"
        @click="selectDate(d.date)"
      >
        <div class="pd__date-label">{{ d.label }}</div>
        <div class="pd__date-day">{{ d.day }}</div>
      </div>
    </div>

    <div class="pd__slots">
      <van-loading v-if="loadingSlots" class="pd__loading" />
      <template v-else>
        <div v-for="s in slots" :key="s.id" class="pd__slot">
          <div class="pd__slot-info">
            <div class="pd__slot-time">{{ s.timeRange }}</div>
            <div class="pd__slot-remain">剩余 {{ s.remaining }} 人</div>
          </div>
          <van-button
            size="small"
            :type="s.status === 'AVAILABLE' ? 'primary' : 'default'"
            :disabled="s.status !== 'AVAILABLE'"
            @click="onBook(s)"
          >
            {{ STATUS[s.status] || s.status }}
          </van-button>
        </div>
        <van-empty v-if="!slots.length" description="该日期暂无时段" />
      </template>
    </div>
  </div>
</template>

<style scoped>
.pd__card {
  margin: 12px 16px;
  padding: 14px;
  background: #fff;
  border-radius: var(--fs-radius); box-shadow: var(--fs-shadow);
}
.pd__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.pd__name {
  font-size: 16px;
  font-weight: 600;
}
.pd__price {
  color: #15803d;
  font-weight: 600;
}
.pd__meta {
  margin-top: 6px;
  font-size: 13px;
  color: #969799;
  display: flex;
  gap: 12px;
}
.pd__desc {
  margin-top: 6px;
  font-size: 13px;
  color: #646566;
}
.pd__items {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.pd__dates {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding: 8px 16px;
  -webkit-overflow-scrolling: touch;
}
.pd__date {
  flex-shrink: 0;
  width: 56px;
  text-align: center;
  padding: 8px 0;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #eee;
}
.pd__date--active {
  border-color: #16a34a;
  color: #16a34a;
  background: #eafaf0;
}
.pd__date-label {
  font-size: 12px;
}
.pd__date-day {
  font-size: 13px;
  font-weight: 600;
  margin-top: 2px;
}
.pd__slots {
  padding: 4px 16px 24px;
}
.pd__loading {
  margin-top: 30vh;
}
.pd__slot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-radius: var(--fs-radius); box-shadow: var(--fs-shadow);
  padding: 12px 14px;
  margin-bottom: 10px;
}
.pd__slot-time {
  font-size: 15px;
  font-weight: 600;
}
.pd__slot-remain {
  margin-top: 4px;
  font-size: 12px;
  color: #969799;
}
</style>
