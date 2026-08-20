<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPackages } from '@/api/course'
import { getEvents } from '@/api/event'

const route = useRoute()
const router = useRouter()

const COURSE = 'COURSE'
const EVENT = 'EVENT'
const EVENT_TYPE = { MATCH: '比赛', CAMP: '训练营', SELECTION: '选拔' }
const EVENT_STATUS = { DRAFT: '未开始', REGISTRATING: '报名中', IN_PROGRESS: '进行中', ENDED: '已结束' }
const STATUS_COLOR = { DRAFT: '#9ca3af', REGISTRATING: '#16a34a', IN_PROGRESS: '#0b5d2e', ENDED: '#9ca3af' }

const activeTab = ref(route.query.type === EVENT ? EVENT : COURSE)
const keyword = ref(route.query.keyword || '')
const results = ref([])
const pageNum = ref(1)
const total = ref(0)
const loading = ref(false)
const finished = ref(false)

const load = async () => {
  try {
    const params = { pageNum: pageNum.value, pageSize: 10, keyword: keyword.value.trim() || undefined }
    const res = activeTab.value === COURSE ? await getPackages(params) : await getEvents(params)
    results.value.push(...(res.list || []))
    total.value = res.total ?? 0
    finished.value = results.value.length >= total.value
    pageNum.value += 1
  } catch {
    // 统一提示
  } finally {
    loading.value = false
  }
}

const switchTab = (t) => {
  if (activeTab.value === t) return
  activeTab.value = t
  results.value = []
  pageNum.value = 1
  finished.value = false
  loading.value = false
}

// 重新搜索：清空结果让 van-list 重新加载
const onSearch = () => {
  results.value = []
  pageNum.value = 1
  finished.value = false
  loading.value = false
}

const goDetail = (item) => {
  if (activeTab.value === COURSE) router.push(`/course/package/${item.id}`)
  else router.push(`/event/${item.id}`)
}
</script>

<template>
  <div class="search-page">
    <van-nav-bar title="搜索" left-arrow @click-left="router.back()" />

    <!-- 搜索栏：可修改关键词重新搜索 -->
    <div class="search-bar">
      <div class="search-bar__input">
        <van-icon name="search" size="16" />
        <input v-model="keyword" type="search" placeholder="搜索课程名称 / 教练，或赛事标题" @keyup.enter="onSearch" />
        <span class="search-bar__btn" @click="onSearch">搜索</span>
      </div>
      <div class="search-bar__tabs">
        <span
          v-for="t in [{ key: COURSE, label: '课程' }, { key: EVENT, label: '赛事' }]"
          :key="t.key"
          class="search-bar__tab"
          :class="{ active: activeTab === t.key }"
          @click="switchTab(t.key)"
        >{{ t.label }}</span>
      </div>
    </div>

    <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" @load="load">
      <template v-if="activeTab === COURSE">
        <div v-for="p in results" :key="p.id" class="s-item" @click="goDetail(p)">
          <div class="s-item__main">
            <div class="s-item__title">{{ p.name }}</div>
            <div class="s-item__meta">教练 {{ p.coachName }} · 适合 {{ p.suitableLevel }}</div>
          </div>
          <div class="s-item__price">{{ p.price }}<span>积分</span></div>
        </div>
      </template>
      <template v-else>
        <div v-for="e in results" :key="e.id" class="s-item" @click="goDetail(e)">
          <div class="s-item__main">
            <div class="s-item__title">{{ e.title }}</div>
            <div class="s-item__meta">
              {{ EVENT_TYPE[e.type] || e.type }}
              <span class="s-item__status" :style="{ color: STATUS_COLOR[e.status] }">{{ EVENT_STATUS[e.status] || e.status }}</span>
            </div>
          </div>
          <div class="s-item__count">{{ e.currentParticipants }}/{{ e.maxParticipants }}</div>
        </div>
      </template>
      <van-empty v-if="!loading && !results.length" description="暂无相关内容，换个关键词试试" />
    </van-list>
  </div>
</template>

<style scoped>
.search-page {
  min-height: 100vh;
  padding-bottom: 40px;
}

/* 搜索栏 */
.search-bar {
  margin: 12px 16px;
  background: #fff;
  border-radius: var(--fs-radius-lg);
  box-shadow: var(--fs-shadow);
  padding: 10px 14px 6px;
}
.search-bar__input {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #f2f8f4;
  border-radius: 999px;
  padding: 6px 6px 6px 12px;
  color: var(--fs-text-3);
}
.search-bar__input input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-size: 13px;
  color: var(--fs-text-1);
}
.search-bar__input input::placeholder {
  color: var(--fs-text-3);
}
.search-bar__btn {
  flex-shrink: 0;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
  background: var(--fs-gradient);
  border-radius: 999px;
  padding: 6px 14px;
  cursor: pointer;
  transition: opacity 0.18s ease;
}
.search-bar__btn:active {
  opacity: 0.85;
}
.search-bar__tabs {
  display: flex;
  gap: 24px;
  padding: 10px 2px 6px;
}
.search-bar__tab {
  position: relative;
  font-size: 14px;
  font-weight: 600;
  color: var(--fs-text-2);
  cursor: pointer;
  transition: color 0.18s ease;
}
.search-bar__tab.active {
  color: var(--fs-primary);
}
.search-bar__tab.active::after {
  content: '';
  position: absolute;
  left: 50%;
  bottom: -8px;
  transform: translateX(-50%);
  width: 20px;
  height: 3px;
  border-radius: 2px;
  background: var(--fs-gradient);
}

/* 搜索结果列表 */
.s-item {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #fff;
  border-radius: var(--fs-radius);
  box-shadow: var(--fs-shadow);
  padding: 14px;
  margin: 0 16px 10px;
  transition: transform 0.15s ease;
}
.s-item:active {
  transform: scale(0.98);
}
.s-item__main {
  flex: 1;
  min-width: 0;
}
.s-item__title {
  font-size: 15px;
  font-weight: 700;
  color: var(--fs-text-1);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.s-item__meta {
  margin-top: 4px;
  font-size: 12px;
  color: var(--fs-text-3);
}
.s-item__status {
  margin-left: 6px;
  font-weight: 600;
}
.s-item__price {
  font-size: 16px;
  font-weight: 800;
  color: var(--fs-primary-dark);
  flex-shrink: 0;
}
.s-item__price span {
  font-size: 10px;
  font-weight: 400;
  margin-left: 2px;
}
.s-item__count {
  font-size: 14px;
  font-weight: 700;
  color: var(--fs-text-2);
  flex-shrink: 0;
}
</style>
