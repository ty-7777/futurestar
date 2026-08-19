<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getEvents } from '@/api/event'
import { formatDate } from '@/utils/format'

const router = useRouter()

const TABS = [
  { name: 'MATCH', title: '比赛' },
  { name: 'CAMP', title: '训练营' },
  { name: 'SELECTION', title: '选拔' }
]

const active = ref('MATCH')
const list = ref([])
const loading = ref(false)
const finished = ref(false)
const pageNum = ref(1)
const pageSize = 10

const TYPE_LABEL = { MATCH: '比赛', CAMP: '训练营', SELECTION: '选拔' }
const STATUS_LABEL = { DRAFT: '草稿', REGISTRATING: '报名中', IN_PROGRESS: '进行中', ENDED: '已结束' }

watch(active, () => {
  list.value = []
  pageNum.value = 1
  finished.value = false
  loading.value = false
})

const onLoad = async () => {
  try {
    const data = await getEvents({ pageNum: pageNum.value, pageSize, type: active.value })
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
  <div class="events">
    <van-nav-bar
      title="赛事活动"
      left-arrow
      right-text="我的活动"
      @click-left="router.back()"
      @click-right="router.push('/event/my')"
    />
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
      <div v-for="e in list" :key="e.id" class="ev" @click="router.push(`/event/${e.id}`)">
        <div class="ev__head">
          <span class="ev__title">{{ e.title }}</span>
          <van-tag type="primary" plain>{{ TYPE_LABEL[e.type] || e.type }}</van-tag>
        </div>
        <div class="ev__time">
          {{ formatDate(e.activityStart) }} ~ {{ formatDate(e.activityEnd) }}
        </div>
        <div class="ev__foot">
          <span class="ev__count">{{ e.currentParticipants }}/{{ e.maxParticipants }} 人</span>
          <van-tag :type="e.status === 'REGISTRATING' ? 'success' : 'default'">
            {{ STATUS_LABEL[e.status] || e.status }}
          </van-tag>
        </div>
      </div>
      <van-empty v-if="finished && !list.length" description="暂无活动" />
    </van-list>
  </div>
</template>

<style scoped>
.ev {
  margin: 12px 16px;
  padding: 14px;
  background: #fff;
  border-radius: var(--fs-radius); box-shadow: var(--fs-shadow);
}
.ev__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}
.ev__title {
  font-size: 15px;
  font-weight: 600;
  color: #323233;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.ev__time {
  margin-top: 8px;
  font-size: 13px;
  color: #646566;
}
.ev__foot {
  margin-top: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.ev__count {
  font-size: 12px;
  color: #969799;
}
</style>
