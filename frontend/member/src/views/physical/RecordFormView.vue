<script setup>
import { ref } from 'vue'
import { showToast } from 'vant'
import { useRouter } from 'vue-router'
import { addRecord } from '@/api/physical'

const router = useRouter()

const form = ref({
  height: '',
  weight: '',
  bodyFatRate: '',
  heartRate: '',
  vitalCapacity: '',
  sprint30m: '',
  standingLongJump: '',
  verticalJump: '',
  enduranceRun: '',
  memo: ''
})

const NUMERIC_FIELDS = [
  'height',
  'weight',
  'bodyFatRate',
  'heartRate',
  'vitalCapacity',
  'sprint30m',
  'standingLongJump',
  'verticalJump',
  'enduranceRun'
]

const saving = ref(false)

const onSubmit = async () => {
  const payload = {}
  for (const [k, v] of Object.entries(form.value)) {
    if (v === '' || v === null || v === undefined) continue
    payload[k] = NUMERIC_FIELDS.includes(k) ? Number(v) : v
  }
  if (!Object.keys(payload).length) return showToast('请至少填写一项指标')
  saving.value = true
  try {
    await addRecord(payload)
    showToast('录入成功')
    router.back()
  } catch {
    // 错误提示已由请求层统一处理
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="record-form">
    <van-nav-bar title="录入体能记录" left-arrow @click-left="router.back()" />
    <van-form @submit="onSubmit">
      <van-cell-group inset title="身体数据">
        <van-field v-model="form.height" label="身高" type="number" placeholder="cm" />
        <van-field v-model="form.weight" label="体重" type="number" placeholder="kg" />
        <van-field v-model="form.bodyFatRate" label="体脂率" type="number" placeholder="%" />
        <van-field v-model="form.heartRate" label="静息心率" type="number" placeholder="次/分" />
        <van-field v-model="form.vitalCapacity" label="肺活量" type="number" placeholder="ml" />
      </van-cell-group>
      <van-cell-group inset title="运动表现">
        <van-field v-model="form.sprint30m" label="30米冲刺" type="number" placeholder="秒" />
        <van-field v-model="form.standingLongJump" label="立定跳远" type="number" placeholder="cm" />
        <van-field v-model="form.verticalJump" label="原地纵跳" type="number" placeholder="cm" />
        <van-field v-model="form.enduranceRun" label="12分钟耐力跑" type="number" placeholder="米" />
      </van-cell-group>
      <van-cell-group inset title="备注">
        <van-field v-model="form.memo" type="textarea" rows="2" maxlength="500" placeholder="选填" />
      </van-cell-group>
      <div class="record-form__btn">
        <van-button type="primary" block round native-type="submit" :loading="saving">
          提交
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<style scoped>
.record-form__btn {
  padding: 24px 16px;
}
</style>
