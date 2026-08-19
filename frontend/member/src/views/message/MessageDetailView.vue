<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getMessageDetail, markRead } from '@/api/message'
import { useMessageStore } from '@/stores/message'
import { formatDate } from '@/utils/format'

const route = useRoute()
const router = useRouter()
const messageStore = useMessageStore()

const detail = ref(null)

const TYPE_LABEL = { COURSE: '课程', EVENT: '赛事', PHYSICAL: '体能', SYSTEM: '系统' }

onMounted(async () => {
  try {
    detail.value = await getMessageDetail(route.params.id)
    // 未读则标记已读并刷新角标
    if (!detail.value.isRead) {
      await markRead(route.params.id)
      detail.value.isRead = 1
      messageStore.fetchUnread()
    }
  } catch {
    // 统一提示
  }
})
</script>

<template>
  <div class="message-detail">
    <van-nav-bar title="消息详情" left-arrow @click-left="router.back()" />
    <div v-if="detail" class="md">
      <h3 class="md__title">{{ detail.title }}</h3>
      <div class="md__meta">
        <van-tag type="primary" plain>{{ TYPE_LABEL[detail.type] || detail.type }}</van-tag>
        <span>{{ formatDate(detail.createTime) }}</span>
      </div>
      <div class="md__content">{{ detail.content }}</div>
    </div>
  </div>
</template>

<style scoped>
.md {
  margin: 16px;
  padding: 16px;
  background: #fff;
  border-radius: var(--fs-radius); box-shadow: var(--fs-shadow);
}
.md__title {
  margin: 0 0 10px;
  font-size: 17px;
}
.md__meta {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 12px;
  color: #969799;
  margin-bottom: 14px;
}
.md__content {
  font-size: 15px;
  line-height: 1.7;
  color: #323233;
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
