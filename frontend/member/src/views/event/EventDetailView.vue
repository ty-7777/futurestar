<script setup>
import { onMounted, ref } from 'vue'
import { showToast } from 'vant'
import { useRoute, useRouter } from 'vue-router'
import { getEventDetail, getCheckinStatus, registerEvent, checkin } from '@/api/event'
import { useUserStore } from '@/stores/user'
import { formatDate } from '@/utils/format'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const eventId = route.params.id

const detail = ref(null)
const registered = ref(false)
const checkedIn = ref(false)

const TYPE_LABEL = { MATCH: '比赛', CAMP: '训练营', SELECTION: '选拔' }
const STATUS_LABEL = { DRAFT: '草稿', REGISTRATING: '报名中', IN_PROGRESS: '进行中', ENDED: '已结束' }

const load = async () => {
  try {
    detail.value = await getEventDetail(eventId)
    // 探测报名状态：成功则已报名，异常（未报名）静默忽略
    try {
      const status = await getCheckinStatus(eventId)
      registered.value = true
      checkedIn.value = status === 'CHECKED_IN'
    } catch {
      registered.value = false
      checkedIn.value = false
    }
  } catch {
    // 详情加载失败，统一提示
  }
}

onMounted(load)

const onRegister = async () => {
  await registerEvent(eventId)
  showToast('报名成功')
  load()
}

const onCheckin = async () => {
  await checkin(eventId)
  showToast('签到成功，+50 积分')
  userStore.user.points += 50
  checkedIn.value = true
}
</script>

<template>
  <div class="ed">
    <van-nav-bar title="活动详情" left-arrow @click-left="router.back()" />

    <div v-if="detail" class="ed__card">
      <div class="ed__head">
        <span class="ed__title">{{ detail.title }}</span>
        <van-tag type="primary" plain>{{ TYPE_LABEL[detail.type] || detail.type }}</van-tag>
      </div>
      <div class="ed__meta">
        <div>报名时间：{{ formatDate(detail.registrationStart) }} ~ {{ formatDate(detail.registrationEnd) }}</div>
        <div>活动时间：{{ formatDate(detail.activityStart) }} ~ {{ formatDate(detail.activityEnd) }}</div>
        <div>已报名 {{ detail.currentParticipants }} / {{ detail.maxParticipants }} 人</div>
      </div>
      <div class="ed__status">
        <van-tag :type="detail.status === 'REGISTRATING' ? 'success' : detail.status === 'IN_PROGRESS' ? 'warning' : 'default'">
          {{ STATUS_LABEL[detail.status] || detail.status }}
        </van-tag>
        <van-tag v-if="registered" type="primary" plain>已报名</van-tag>
        <van-tag v-if="checkedIn" type="success" plain>已签到</van-tag>
      </div>
      <div class="ed__content">{{ detail.content }}</div>
    </div>

    <div class="ed__actions">
      <van-button
        v-if="detail?.status === 'REGISTRATING' && !registered"
        type="primary"
        block
        round
        @click="onRegister"
      >
        立即报名
      </van-button>
      <van-button
        v-if="detail?.status === 'IN_PROGRESS' && registered && !checkedIn"
        type="success"
        block
        round
        @click="onCheckin"
      >
        签到
      </van-button>
      <van-button v-if="registered && checkedIn" block round disabled>已签到</van-button>
    </div>
  </div>
</template>

<style scoped>
.ed__card {
  margin: 12px 16px;
  padding: 14px;
  background: #fff;
  border-radius: var(--fs-radius); box-shadow: var(--fs-shadow);
}
.ed__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}
.ed__title {
  font-size: 16px;
  font-weight: 600;
}
.ed__meta {
  margin-top: 10px;
  font-size: 13px;
  color: #646566;
  line-height: 1.8;
}
.ed__status {
  margin-top: 10px;
  display: flex;
  gap: 8px;
}
.ed__content {
  margin-top: 12px;
  font-size: 14px;
  color: #323233;
  line-height: 1.7;
  white-space: pre-wrap;
}
.ed__actions {
  padding: 16px;
}
</style>
