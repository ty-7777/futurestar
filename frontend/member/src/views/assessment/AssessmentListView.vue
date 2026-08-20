<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getQuestionnaires } from '@/api/assessment'

const router = useRouter()
const list = ref([])

onMounted(async () => {
  try {
    list.value = await getQuestionnaires()
  } catch {
    // 统一提示
  }
})
</script>

<template>
  <div class="assessment">
    <van-nav-bar
      title="技术/体能评测"
      left-arrow
      right-text="历史"
      @click-left="router.back()"
      @click-right="router.push('/assessment/history')"
    />
    <div v-for="q in list" :key="q.id" class="as" @click="router.push(`/assessment/${q.id}`)">
      <div class="as__title">{{ q.title }}</div>
      <div v-if="q.description" class="as__desc">{{ q.description }}</div>
      <div class="as__go">开始评测 <van-icon name="arrow" /></div>
    </div>
    <van-empty v-if="!list.length" description="暂无可评测的问卷" />
  </div>
</template>

<style scoped>
.as {
  margin: 12px 16px;
  padding: 14px;
  background: #fff;
  border-radius: var(--fs-radius); box-shadow: var(--fs-shadow);
}
.as__title {
  font-size: 15px;
  font-weight: 600;
}
.as__desc {
  margin-top: 6px;
  font-size: 13px;
  color: #646566;
}
.as__go {
  margin-top: 10px;
  font-size: 13px;
  color: #16a34a;
}
</style>
