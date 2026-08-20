<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getMessages } from '@/api/message'
import { formatDate } from '@/utils/format'

const router = useRouter()

const TABS = [
  { name: 'COURSE', title: '课程' },
  { name: 'EVENT', title: '赛事' },
  { name: 'PHYSICAL', title: '体能' },
  { name: 'SYSTEM', title: '系统' }
]

const active = ref('COURSE')
const list = ref([])
const loading = ref(false)
const finished = ref(false)
const pageNum = ref(1)
const pageSize = 10

watch(active, () => {
  list.value = []
  pageNum.value = 1
  finished.value = false
  loading.value = false
})

const onLoad = async () => {
  try {
    const data = await getMessages({ pageNum: pageNum.value, pageSize, type: active.value })
    list.value.push(...data.list)
    pageNum.value++
    if (list.value.length >= data.total) finished.value = true
  } catch {
    finished.value = true
  } finally {
    loading.value = false
  }
}

const openDetail = (id) => router.push(`/message/${id}`)
</script>

<template>
  <div class="message">
    <van-nav-bar title="消息通知" />
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
      <div v-for="m in list" :key="m.id" class="m-item" @click="openDetail(m.id)">
        <div class="m-item__dot" v-if="!m.isRead" />
        <div class="m-item__body">
          <div class="m-item__title">{{ m.title }}</div>
          <div class="m-item__content">{{ m.content }}</div>
          <div class="m-item__time">{{ formatDate(m.createTime) }}</div>
        </div>
      </div>
      <van-empty v-if="finished && !list.length" description="暂无消息" />
    </van-list>
  </div>
</template>

<style scoped>
.m-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin: 10px 16px;
  padding: 12px;
  background: #fff;
  border-radius: var(--fs-radius); box-shadow: var(--fs-shadow);
  position: relative;
}
.m-item__dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #dc2626;
  margin-top: 6px;
  flex-shrink: 0;
}
.m-item__body {
  flex: 1;
}
.m-item__title {
  font-size: 15px;
  font-weight: 600;
  color: #323233;
}
.m-item__content {
  margin-top: 4px;
  font-size: 13px;
  color: #646566;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.m-item__time {
  margin-top: 6px;
  font-size: 12px;
  color: #969799;
}
</style>
