<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { myEvents } from '@/api/event'
import { formatDate } from '@/utils/format'

const router = useRouter()

const list = ref([])
const loading = ref(false)
const finished = ref(false)
const pageNum = ref(1)
const pageSize = 10

const TYPE_LABEL = { MATCH: '比赛', CAMP: '训练营', SELECTION: '选拔' }
const STATUS_LABEL = { DRAFT: '草稿', REGISTRATING: '报名中', IN_PROGRESS: '进行中', ENDED: '已结束' }

const onLoad = async () => {
  try {
    const data = await myEvents({ pageNum: pageNum.value, pageSize })
    list.value.push(...data.list)
    pageNum.value++
    if (list.value.length >= data.total) finished.value = true
  } catch {
    finished.value = true
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="my-events">
    <van-nav-bar title="我的活动" left-arrow @click-left="router.back()" />
    <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" @load="onLoad">
      <div v-for="e in list" :key="e.id" class="me" @click="router.push(`/event/${e.id}`)">
        <div class="me__head">
          <span class="me__title">{{ e.title }}</span>
          <van-tag type="primary" plain>{{ TYPE_LABEL[e.type] || e.type }}</van-tag>
        </div>
        <div class="me__time">{{ formatDate(e.activityStart) }} ~ {{ formatDate(e.activityEnd) }}</div>
        <div class="me__status">
          <van-tag :type="e.status === 'IN_PROGRESS' ? 'warning' : 'default'">
            {{ STATUS_LABEL[e.status] || e.status }}
          </van-tag>
        </div>
      </div>
      <van-empty v-if="finished && !list.length" description="还没有报名活动" />
    </van-list>
  </div>
</template>

<style scoped>
.me {
  margin: 12px 16px;
  padding: 14px;
  background: #fff;
  border-radius: var(--fs-radius); box-shadow: var(--fs-shadow);
}
.me__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}
.me__title {
  font-size: 15px;
  font-weight: 600;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.me__time {
  margin-top: 8px;
  font-size: 13px;
  color: #646566;
}
.me__status {
  margin-top: 8px;
}
</style>
