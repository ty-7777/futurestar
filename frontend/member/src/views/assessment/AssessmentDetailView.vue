<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAssessmentDetail } from '@/api/assessment'
import { formatDate } from '@/utils/format'

const route = useRoute()
const router = useRouter()
const detail = ref(null)

onMounted(async () => {
  try {
    detail.value = await getAssessmentDetail(route.params.id)
  } catch {
    // 统一提示
  }
})
</script>

<template>
  <div class="result-detail">
    <van-nav-bar title="评测详情" left-arrow @click-left="router.back()" />
    <div v-if="detail" class="rd">
      <div class="rd__score">
        AI 评分：<span>{{ detail.aiScore ?? '-' }}</span> 分
      </div>
      <div class="rd__time">{{ formatDate(detail.createTime) }}</div>
      <div class="rd__label">AI 建议</div>
      <div class="rd__suggestion">{{ detail.aiSuggestion }}</div>
    </div>
  </div>
</template>

<style scoped>
.rd {
  margin: 16px;
  padding: 16px;
  background: #fff;
  border-radius: var(--fs-radius); box-shadow: var(--fs-shadow);
}
.rd__score {
  font-size: 18px;
  font-weight: 600;
}
.rd__score span {
  color: #15803d;
  font-size: 24px;
}
.rd__time {
  margin-top: 6px;
  font-size: 12px;
  color: #969799;
}
.rd__label {
  margin-top: 16px;
  font-size: 14px;
  font-weight: 600;
}
.rd__suggestion {
  margin-top: 8px;
  font-size: 14px;
  line-height: 1.7;
  color: #323233;
  white-space: pre-wrap;
}
</style>
