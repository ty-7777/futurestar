<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { getHistory } from '@/api/assessment'
import { formatDate } from '@/utils/format'

const router = useRouter()

const list = ref([])
const loading = ref(false)
const finished = ref(false)
const pageNum = ref(1)
const pageSize = 10

const onLoad = async () => {
  try {
    const data = await getHistory({ pageNum: pageNum.value, pageSize })
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
  <div class="history">
    <van-nav-bar title="评测历史" left-arrow @click-left="router.back()" />
    <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" @load="onLoad">
      <div v-for="h in list" :key="h.id" class="ht" @click="router.push(`/assessment/result/${h.id}`)">
        <div class="ht__score">{{ h.aiScore ?? '-' }} 分</div>
        <div class="ht__time">{{ formatDate(h.createTime) }}</div>
        <div class="ht__suggestion">{{ h.aiSuggestion }}</div>
      </div>
      <van-empty v-if="finished && !list.length" description="还没有评测记录" />
    </van-list>
  </div>
</template>

<style scoped>
.ht {
  margin: 12px 16px;
  padding: 14px;
  background: #fff;
  border-radius: var(--fs-radius); box-shadow: var(--fs-shadow);
}
.ht__score {
  font-size: 18px;
  font-weight: 600;
  color: #15803d;
}
.ht__time {
  margin-top: 4px;
  font-size: 12px;
  color: #969799;
}
.ht__suggestion {
  margin-top: 8px;
  font-size: 13px;
  color: #646566;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
