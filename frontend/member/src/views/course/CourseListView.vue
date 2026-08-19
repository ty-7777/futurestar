<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { getPackages } from '@/api/course'

const router = useRouter()

const list = ref([])
const loading = ref(false)
const finished = ref(false)
const pageNum = ref(1)
const pageSize = 10

const parseItems = (items) => {
  if (!items) return []
  try {
    return JSON.parse(items)
  } catch {
    return []
  }
}

const onLoad = async () => {
  try {
    const data = await getPackages({ pageNum: pageNum.value, pageSize })
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
  <div class="course">
    <van-nav-bar
      title="课程预约"
      right-text="我的预约"
      @click-right="router.push('/course/appointments')"
    />
    <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" @load="onLoad">
      <div v-for="p in list" :key="p.id" class="cp" @click="router.push(`/course/package/${p.id}`)">
        <div class="cp__head">
          <span class="cp__name">{{ p.name }}</span>
          <span class="cp__price">{{ p.price }} 积分</span>
        </div>
        <div class="cp__meta">
          <span v-if="p.coachName">教练：{{ p.coachName }}</span>
          <span v-if="p.suitableLevel">{{ p.suitableLevel }}</span>
        </div>
        <div v-if="p.description" class="cp__desc">{{ p.description }}</div>
        <div v-if="parseItems(p.items).length" class="cp__items">
          <van-tag v-for="(it, i) in parseItems(p.items)" :key="i" plain type="primary">
            {{ it }}
          </van-tag>
        </div>
      </div>
      <van-empty v-if="finished && !list.length" description="暂无课程套餐" />
    </van-list>
  </div>
</template>

<style scoped>
.cp {
  margin: 12px 16px;
  padding: 14px;
  background: #fff;
  border-radius: var(--fs-radius); box-shadow: var(--fs-shadow);
}
.cp__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.cp__name {
  font-size: 16px;
  font-weight: 600;
  color: #323233;
}
.cp__price {
  color: #ee0a24;
  font-weight: 600;
}
.cp__meta {
  margin-top: 6px;
  font-size: 13px;
  color: #969799;
  display: flex;
  gap: 12px;
}
.cp__desc {
  margin-top: 6px;
  font-size: 13px;
  color: #646566;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.cp__items {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
</style>
