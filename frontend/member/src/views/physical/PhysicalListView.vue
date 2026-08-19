<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { getRecords } from '@/api/physical'
import { formatDate } from '@/utils/format'

const router = useRouter()

const list = ref([])
const loading = ref(false)
const finished = ref(false)
const pageNum = ref(1)
const pageSize = 10

const METRICS = [
  { key: 'height', label: '身高', unit: 'cm' },
  { key: 'weight', label: '体重', unit: 'kg' },
  { key: 'bmi', label: 'BMI', unit: '' },
  { key: 'bodyFatRate', label: '体脂率', unit: '%' },
  { key: 'heartRate', label: '心率', unit: '次/分' },
  { key: 'vitalCapacity', label: '肺活量', unit: 'ml' },
  { key: 'sprint30m', label: '30米', unit: 's' },
  { key: 'standingLongJump', label: '立定跳远', unit: 'cm' },
  { key: 'verticalJump', label: '纵跳', unit: 'cm' },
  { key: 'enduranceRun', label: '耐力跑', unit: 'm' }
]

const onLoad = async () => {
  try {
    const data = await getRecords({ pageNum: pageNum.value, pageSize })
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
  <div class="physical">
    <van-nav-bar
      title="体能记录"
      left-arrow
      @click-left="router.back()"
      right-text="趋势"
      @click-right="router.push('/physical/trend')"
    />
    <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" @load="onLoad">
      <div v-for="r in list" :key="r.id" class="p-card">
        <div class="p-card__head">{{ formatDate(r.recordedAt) }}</div>
        <div class="p-card__grid">
          <div v-for="m in METRICS" v-show="r[m.key] != null" :key="m.key" class="p-card__item">
            <div class="p-card__label">{{ m.label }}</div>
            <div class="p-card__value">
              {{ r[m.key] }}<span class="p-card__unit">{{ m.unit }}</span>
            </div>
          </div>
        </div>
        <div v-if="r.memo" class="p-card__memo">{{ r.memo }}</div>
      </div>
      <van-empty v-if="finished && !list.length" description="还没有体能记录" />
    </van-list>
    <div class="p-add">
      <van-button type="primary" block round @click="router.push('/physical/record')">
        录入体能记录
      </van-button>
    </div>
  </div>
</template>

<style scoped>
.p-card {
  margin: 12px 16px;
  padding: 12px;
  background: #fff;
  border-radius: var(--fs-radius); box-shadow: var(--fs-shadow);
}
.p-card__head {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 8px;
}
.p-card__grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}
.p-card__item {
  background: #f7f8fa;
  border-radius: 6px;
  padding: 6px;
  text-align: center;
}
.p-card__label {
  font-size: 12px;
  color: #969799;
}
.p-card__value {
  font-size: 14px;
  font-weight: 600;
  color: #323233;
}
.p-card__unit {
  font-size: 11px;
  font-weight: 400;
  color: #969799;
  margin-left: 2px;
}
.p-card__memo {
  margin-top: 8px;
  font-size: 13px;
  color: #646566;
}
.p-add {
  padding: 16px;
}
</style>
