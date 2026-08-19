<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { showConfirmDialog, showToast } from 'vant'
import { getAppointments, cancelAppointment } from '@/api/course'
import { formatDate } from '@/utils/format'

const router = useRouter()

const TABS = [
  { name: 'PENDING', title: '待确认' },
  { name: 'CONFIRMED', title: '已确认' },
  { name: 'COMPLETED', title: '已完成' },
  { name: 'CANCELED', title: '已取消' }
]

const active = ref('PENDING')
const list = ref([])
const loading = ref(false)
const finished = ref(false)
const pageNum = ref(1)
const pageSize = 10

const STATUS_LABEL = { PENDING: '待确认', CONFIRMED: '已确认', COMPLETED: '已完成', CANCELED: '已取消' }

watch(active, () => {
  list.value = []
  pageNum.value = 1
  finished.value = false
  loading.value = false
})

const onLoad = async () => {
  try {
    const data = await getAppointments({ pageNum: pageNum.value, pageSize, status: active.value })
    list.value.push(...data.list)
    pageNum.value++
    if (list.value.length >= data.total) finished.value = true
  } catch {
    finished.value = true
  } finally {
    loading.value = false
  }
}

const onCancel = async (a) => {
  try {
    await showConfirmDialog({ title: '提示', message: '确定取消该预约吗？积分将退还' })
  } catch {
    return
  }
  await cancelAppointment(a.id)
  showToast('已取消')
  list.value = list.value.filter((x) => x.id !== a.id)
}

const viewReport = (a) => {
  if (a.reportUrl) window.open(a.reportUrl, '_blank')
}
</script>

<template>
  <div class="my-appointments">
    <van-nav-bar title="我的预约" left-arrow @click-left="router.back()" />
    <van-tabs v-model:active="active">
      <van-tab v-for="t in TABS" :key="t.name" :name="t.name" :title="t.title" />
    </van-tabs>
    <van-list
      :key="active"
      v-model:loading="loading"
      :finished="finished"
      finished-text="没有更多了"
      @load="onLoad"
    >
      <div v-for="a in list" :key="a.id" class="ma">
        <div class="ma__head">
          <span class="ma__name">{{ a.packageName }}</span>
          <span class="ma__status">{{ STATUS_LABEL[a.status] || a.status }}</span>
        </div>
        <div class="ma__meta">
          <span v-if="a.coachName">教练：{{ a.coachName }}</span>
          <span>{{ a.courseDate }} {{ a.timeRange }}</span>
        </div>
        <div class="ma__foot">
          <span class="ma__time">{{ formatDate(a.createTime) }}</span>
          <div class="ma__actions">
            <van-button
              v-if="a.status === 'PENDING' || a.status === 'CONFIRMED'"
              size="small"
              plain
              type="danger"
              @click="onCancel(a)"
            >
              取消预约
            </van-button>
            <van-button v-if="a.status === 'COMPLETED' && a.reportUrl" size="small" type="primary" @click="viewReport(a)">
              查看报告
            </van-button>
          </div>
        </div>
      </div>
      <van-empty v-if="finished && !list.length" description="暂无预约" />
    </van-list>
  </div>
</template>

<style scoped>
.ma {
  margin: 12px 16px;
  padding: 14px;
  background: #fff;
  border-radius: var(--fs-radius); box-shadow: var(--fs-shadow);
}
.ma__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.ma__name {
  font-size: 15px;
  font-weight: 600;
  color: #323233;
}
.ma__status {
  font-size: 12px;
  color: #2f7cff;
}
.ma__meta {
  margin-top: 6px;
  font-size: 13px;
  color: #969799;
  display: flex;
  gap: 12px;
}
.ma__foot {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.ma__time {
  font-size: 12px;
  color: #969799;
}
.ma__actions {
  display: flex;
  gap: 8px;
}
</style>
