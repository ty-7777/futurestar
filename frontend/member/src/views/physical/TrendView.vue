<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getTrend } from '@/api/physical'
import BaseChart from '@/components/BaseChart.vue'

const router = useRouter()
const trend = ref(null)

const buildOption = (metric, name, unit) => {
  const points = metric?.points || []
  return {
    title: { text: name, textStyle: { fontSize: 14 } },
    tooltip: { trigger: 'axis' },
    grid: { left: 44, right: 16, top: 44, bottom: 28 },
    xAxis: { type: 'category', data: points.map((_, i) => `第${i + 1}次`) },
    yAxis: { type: 'value', name: unit },
    series: [
      {
        type: 'line',
        smooth: true,
        data: points,
        areaStyle: { opacity: 0.15 },
        itemStyle: { color: '#16a34a' }
      }
    ]
  }
}

const METRICS = [
  { key: 'weight', name: '体重', unit: 'kg', build: (m) => buildOption(m, '体重', 'kg') },
  { key: 'bmi', name: 'BMI', unit: '', build: (m) => buildOption(m, 'BMI', '') },
  { key: 'sprint30m', name: '30米冲刺', unit: 's', build: (m) => buildOption(m, '30米冲刺', 's') }
]

onMounted(async () => {
  try {
    trend.value = await getTrend(6)
  } catch {
    // 统一提示
  }
})
</script>

<template>
  <div class="trend">
    <van-nav-bar title="体能趋势" left-arrow @click-left="router.back()" />
    <van-empty v-if="trend && !trend.weight.points?.length" description="暂无足够数据" />
    <div v-else-if="trend" class="trend__cards">
      <div v-for="m in METRICS" :key="m.key" class="trend__card">
        <div class="trend__summary">
          <span>均值 {{ trend[m.key]?.avg ?? '-' }}</span>
          <span>最高 {{ trend[m.key]?.max ?? '-' }}</span>
          <span>最低 {{ trend[m.key]?.min ?? '-' }}</span>
        </div>
        <BaseChart :option="m.build(trend[m.key])" height="220px" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.trend__cards {
  padding: 8px 16px 24px;
}
.trend__card {
  background: #fff;
  border-radius: var(--fs-radius); box-shadow: var(--fs-shadow);
  padding: 12px;
  margin-bottom: 12px;
}
.trend__summary {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #646566;
}
</style>
